package com.example.militaryaccountingapp.presenter.fragment.edit

import androidx.lifecycle.viewModelScope
import com.example.militaryaccountingapp.presenter.BaseViewModel
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.Barcode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AddOrEditViewModel @Inject constructor(
) : BaseViewModel<ViewData>(ViewData()) {

    data class ViewData(
        val codes: Set<Barcode> = emptySet(),
        val title: String = "",
        val description: String = "",
        val count: Int = 0
    )

    init {
        Timber.d("init")
    }

    companion object {
        private const val DELAY_TEXT = 500L
        private const val DELAY_COUNTER = 600L
    }

    fun addCode(code: String) {
        log.d("addCode $code")

        val barcode = Barcode(
            id = _data.value.codes.size,
            code = code,
            timestamp = Date().time
        )

        _data.update {
            it.copy(codes = it.codes.plus(barcode))
        }
    }

    fun deleteCode(id: Int) {
        log.d("deleteCode $id")
        deleteCode(getCode(id) ?: return)
    }

    fun deleteCode(code: Barcode) = _data.update { viewData ->
        viewData.copy(codes = viewData.codes.minus(code))
    }

    fun getCode(id: Int) = _data.value.codes.find { it.id == id }


    // Runner
    private var titleJob: Job? = null
    private val titleDispatcher = Dispatchers.IO
    fun setTitle(title: String) {
        stopRunningJob(titleJob)
        titleJob = viewModelScope.launch(titleDispatcher) {
            delay(DELAY_TEXT)
            log.d("setTitle $title")
            _data.update { it.copy(title = title) }
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

    fun save() {
        // TODO push barcodes to database
    }
    fun pushCodes() {}


}