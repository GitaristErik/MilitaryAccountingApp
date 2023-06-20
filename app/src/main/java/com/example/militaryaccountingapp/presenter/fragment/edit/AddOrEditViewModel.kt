package com.example.militaryaccountingapp.presenter.fragment.edit

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.domain.entity.data.Barcode
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.extension.await
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.repository.CategoryRepository
import com.example.militaryaccountingapp.domain.repository.ItemRepository
import com.example.militaryaccountingapp.domain.usecase.auth.CurrentUserUseCase
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.CroppingSavableViewModel
import com.example.militaryaccountingapp.presenter.utils.ui.AuthValidator
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val itemRepository: ItemRepository,
    private val currentUserUseCase: CurrentUserUseCase,
) : BaseViewModel<ViewData>(ViewData()), CroppingSavableViewModel {

    data class ViewData(
        val codes: Set<Barcode> = emptySet(),
        val title: Results<String> = Results.Loading(""),
        val description: String = "",
        val count: Int = 0,
        val saveState: Results<Any?> = Results.Canceled(null),
        val loadedData: Results<Data?> = Results.Loading(null),
    )

    var elementId: String? = null

    /* set(value) {
         field = elementId?.let {
             if (it.trim().isEmpty()) null else it
         }
     }*/
    var elementType: Data.Type = Data.Type.CATEGORY
    var parentId: String? = null
        get() {
            if (field == null) {
                field = if (elementId == null) {
                    currentUser?.rootCategoryId
                } else {
                    (data.value.loadedData as Results.Success).data!!.parentId
                }
            }
            return field
        }

    private var currentUser: User? = null

    fun save() {
        if (data.value.title !is Results.Success) return
        when (elementType) {
            Data.Type.CATEGORY -> saveCategory()
            Data.Type.ITEM -> saveItem()
        }
    }

    private fun saveCategory() {
        safeRunJobWithLoading(Dispatchers.IO) {
            val result = if (elementId == null) {
                resultWrapper(
                    categoryRepository.createCategory(
                        Category(
                            name = (data.value.title as Results.Success<String>).data,
                            description = data.value.description.trim(),
                            parentId = parentId,
                            userId = currentUser!!.id,
                            barCodes = data.value.codes.toList()
                        )
                    )
                ) { category ->
                    categoryRepository.updateCategory(
                        id = category.id,
                        userId = currentUser!!.id,
                        query = mapOf(
                            "imagesUrls" to saveImages(category.id, Data.Type.CATEGORY)
                        )
                    )
                }

            } else {
                categoryRepository.updateCategory(
                    id = elementId!!,
                    userId = currentUser!!.id,
                    query = mapOf(
                        "name" to (data.value.title as Results.Success<String>).data,
                        "description" to data.value.description.trim(),
                        "parentId" to parentId,
                        "barCodes" to data.value.codes.toList(),
                        "imagesUrls" to saveImages(elementId!!, Data.Type.CATEGORY)
                    )
                )
            }
            _data.update { it.copy(saveState = result) }
        }
    }

    private fun saveItem() {
        safeRunJobWithLoading(Dispatchers.IO) {
            val result = if (elementId == null) {
                resultWrapper(
                    itemRepository.createItem(
                        Item(
                            name = (data.value.title as Results.Success<String>).data,
                            description = data.value.description,
                            count = data.value.count,
                            parentId = parentId,
                            userId = currentUser!!.id,
                            barCodes = data.value.codes.toList(),
                        )
                    )
                ) { item ->
                    itemRepository.updateItem(
                        id = item.id,
                        query = mapOf(
                            "imagesUrls" to saveImages(item.id, Data.Type.ITEM)
                        )
                    )
                }
            } else {
                itemRepository.updateItem(
                    elementId!!,
                    mapOf(
                        "name" to (data.value.title as Results.Success<String>).data,
                        "description" to data.value.description,
                        "count" to data.value.count,
                        "parentId" to parentId,
                        "barCodes" to data.value.codes.toList(),
                        "imagesUrls" to saveImages(elementId!!, Data.Type.ITEM)
                    )
                )
            }
            _data.update { it.copy(saveState = result) }
        }
    }

    private suspend fun saveImages(id: String, type: Data.Type): MutableList<String> {
        val images: MutableList<String> = mutableListOf()
        log.d("saveImages id=$id type=$type images=${_dataImages.value}")
        _dataImages.value.forEach { imageUri ->
            val uri = Uri.parse(imageUri)
            resultWrapper(
                FirebaseStorage.getInstance()
                    .reference
                    .child((if (type == Data.Type.CATEGORY) "categories" else "items") + "/$id/${uri.lastPathSegment + Date().time}")
                    .putFile(uri)
                    .await()
            ) { task ->
                // if success, update user avatar url
                resultWrapper(task.storage.downloadUrl.await()) {
                    images.add(it.toString())
                    Results.Success(true)
                }
            }
        }
        return images
    }


    private val _dataImages: MutableStateFlow<Set<String>> = MutableStateFlow(emptySet())
    val dataImages: StateFlow<Set<String>> = _dataImages.asStateFlow()


    init {
        log.d("init")
    }

    companion object {
        private const val DELAY_TEXT = 1000L
        private const val DELAY_COUNTER = 1000L
    }


    fun addCode(code: String) {
        log.d("addCode $code")
        _data.update {
            it.copy(
                codes = it.codes.plus(
                    Barcode(
                        code = code,
                        timestamp = Date().time
                    )
                )
            )
        }
    }

    fun deleteCode(code: Barcode) = _data.update { viewData ->
        viewData.copy(codes = viewData.codes.minus(code))
    }


    // Runner
    private var titleJob: Job? = null
    private val titleDispatcher = Dispatchers.IO
    fun setTitle(title: String) {
        stopRunningJob(titleJob)
        titleJob = viewModelScope.launch(titleDispatcher) {
            delay(DELAY_TEXT)
            log.d("setTitle $title")
            _data.update {
                it.copy(title = AuthValidator.TitleValidator.validate(title))
            }
        }
    }


    // Runner
    private var descriptionJob: Job? = null
    private val descriptionDispatcher = Dispatchers.IO
    fun setDescription(description: String) {
        stopRunningJob(descriptionJob)
        descriptionJob = viewModelScope.launch(descriptionDispatcher) {
            delay(DELAY_TEXT)
            log.d("setDescription $description")
            _data.update { it.copy(description = description) }
        }
    }


    // Runner
    private var counterJob: Job? = null
    private val counterDispatcher = Dispatchers.IO
    fun setCount(count: Int) {
        stopRunningJob(counterJob)
        counterJob = viewModelScope.launch(counterDispatcher) {
            delay(DELAY_COUNTER)
            log.d("setCount $count")
            _data.update { it.copy(count = count) }
        }
    }

    fun addImages(images: List<Uri>) {
        log.d("addImages $images")
        log.d("current viewModel $this")
        _dataImages.update {
            it.plus(images.map { i -> i.toString() })
        }
    }

    fun removeImages(images: Set<String>) {
        _dataImages.update {
//            it.minus(images.map { i -> i.toString() }.toSet())
            it.minus(images)
        }
    }

    override fun saveCropUri(uri: Uri) {
        log.d("saveCropUri $uri")
        addImages(listOf(uri))
    }


    private var saveRenderJob: Job? = null
    fun onSaveRendered() {
        stopRunningJob(saveRenderJob)
        saveRenderJob = viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            _data.update {
                it.copy(
                    saveState = Results.Loading(null),
//                    codes = emptySet(),
//                    title = Results.Loading(""),
//                    description = "",
//                    count = 0,
                )
            }
//            _dataImages.update { emptySet() }
        }
    }

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUser = currentUserUseCase()
        }
        if (elementId == null) return
        viewModelScope.launch(Dispatchers.IO) {
            when (elementType) {
                Data.Type.CATEGORY -> fetchCategory()
                Data.Type.ITEM -> fetchItem()
            }
        }
    }

    private suspend fun fetchItem() {
        val result = itemRepository.getItem(elementId!!)
        if (result is Results.Success) {
            val item = result.data
            _data.update {
                it.copy(
                    codes = item.barCodes.toSet(),
                    title = Results.Success(item.name),
                    description = item.description,
                    count = item.count,
                    loadedData = result
                )
            }
            _dataImages.update { item.imagesUrls.toSet() }
        } else {
            _data.update {
                it.copy(loadedData = result)
            }
        }
    }

    private suspend fun fetchCategory() {
        val result = categoryRepository.getCategory(elementId!!)
        if (result is Results.Success) {
            val category = result.data
            _data.update {
                it.copy(
                    codes = category.barCodes.toSet(),
                    title = Results.Success(category.name),
                    description = category.description,
                    loadedData = result
                )
            }
            // fetch images
            _dataImages.update { category.imagesUrls.toSet() }
        } else {
            _data.update {
                it.copy(loadedData = result)
            }
        }
    }
}