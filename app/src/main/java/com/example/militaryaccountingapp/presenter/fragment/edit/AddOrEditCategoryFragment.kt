package com.example.militaryaccountingapp.presenter.fragment.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddOrEditCategoryBinding
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import com.example.militaryaccountingapp.presenter.utils.image.CarouselHelper
import com.example.militaryaccountingapp.presenter.utils.ui.ext.renderValidate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

@AndroidEntryPoint
class AddOrEditCategoryFragment :
    BaseViewModelFragment<FragmentAddOrEditCategoryBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by activityViewModels()
//    override val viewModel: AddOrEditViewModel by hiltNavGraphViewModels(R.id.mobile_navigation)
//    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)
//    override val viewModel: AddOrEditViewModel by viewModels()


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddOrEditCategoryBinding
        get() = FragmentAddOrEditCategoryBinding::inflate

    private lateinit var imagePickerLauncher: ImagePickerLauncher

    override fun initializeView() {
        setupImages()
        setupCodeScanner()
        setupCodes()
        setupTitle()
        setupDescription()
        observeCustomData2()
    }

    private fun observeCustomData2() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataImages
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.CREATED)
                .collect {
                    CarouselHelper.renderImagesCarousel(
                        binding.carousel,
                        binding.carouselEmpty,
                        imageUrls = it,
                        buttonRemoveCurrentImage = binding.buttonRemoveCurrentImage,
                    )
                }
        }
    }

//    private var isDataInit = false
    override fun render(data: ViewData) {
        log.d("render")
        codesAdapter.submitList(data.codes.toList())
        renderSaveState(data.saveState)
//        if (isDataInit) return else isDataInit = true

        binding.editTitle.renderValidate(data.title, binding.layoutTitle)
        binding.editDescription.setText(data.description)
    }

    private fun renderSaveState(saveState: Results<Any?>) {
        when (saveState) {
            is Results.Success -> {
                if (viewModel.elementId == null) {
                    showToast(R.string.save_create_success)
                    viewModel.onSaveRendered()
                    (saveState.data as? Category)?.let { savedData ->
                        findNavController().navigate(
                            AddOrEditFragmentDirections.actionAddFragmentToCategoryFragment(
                                id = savedData.id,
                                name = savedData.name,
                                description = savedData.description,
                                parentIds = savedData.allParentIds.toTypedArray(),
                            )
                        )
                    }
                } else {
                    showToast(R.string.save_edit_success)
                    AddOrEditFragmentDirections.actionAddFragmentToCategoryFragment(
                        id = viewModel.elementId!!,
                        name = binding.editTitle.text.toString(),
                        description = binding.editDescription.text.toString(),
                    )
                }
            }

            is Results.Failure -> {
                viewModel.onSaveRendered()
                showToast(
                    getString(
                        if (viewModel.elementId == null) R.string.save_create_fail
                        else R.string.save_edit_fail
                    ) + "\n" + saveState.throwable.message
                )
            }

            else -> {}
        }
    }


    val carouselScrollListener = object : CarouselOnScrollListener {
        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int,
            position: Int,
            carouselItem: CarouselItem?
        ) {
            CarouselHelper.currentImageUrl = carouselItem?.imageUrl
        }
    }

    private fun setupImages() {
        binding.carousel.apply {
            registerLifecycle(viewLifecycleOwner)
            onScrollListener = carouselScrollListener
        }

        binding.buttonAddImage.setOnClickListener {
            startPhotoPicker()
        }

        binding.buttonRemoveCurrentImage.setOnClickListener {
            viewModel.removeImages(
                setOf(
                    CarouselHelper.currentImageUrl ?: return@setOnClickListener
                )
            )
        }
    }

    private fun startPhotoPicker() {
        imagePickerLauncher.launch(
            AddFragmentHelper.createPickerConfig()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imagePickerLauncher = registerImagePicker {
            if (it.size == 1) {
                AddFragmentHelper.navigateToCropFragment(this, it.first().uri)
            } else {
                viewModel.addImages(it.map(Image::uri))
            }
        }
    }

    override fun onStop() {
        super.onStop()
//        isDataInit = false
    }

    private val codesAdapter by lazy {
        BarCodeAdapter { barcode ->
            findNavController().navigate(
                AddOrEditFragmentDirections.actionAddFragmentToModalBottomSheetCodeDetails(
                    isRenderDelete = true,
                    code = barcode
                )
            )
        }
    }

    private fun setupCodes() {
        binding.rvCodes.adapter = codesAdapter
    }

    private fun setupCodeScanner() {
        binding.buttonScan.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_barcodeScannerFragment)
        }
    }

    private fun setupTitle() {
        binding.editTitle.addTextChangedListener {
            viewModel.setTitle(it.toString())
        }
    }

    private fun setupDescription() {
        binding.editDescription.addTextChangedListener {
            viewModel.setDescription(it.toString())
        }
    }
}