package com.example.militaryaccountingapp.presenter.fragment.edit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ImagePickerSavePath
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddCategoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.Barcode
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

@AndroidEntryPoint
class AddCategoryFragment :
    BaseViewModelFragment<FragmentAddCategoryBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddCategoryBinding
        get() = FragmentAddCategoryBinding::inflate

    private lateinit var imagePickerLauncher: ImagePickerLauncher

    private var currentImageUrl: String? = null

    override fun initializeView() {
        setupImages()
        setupCodeScanner()
        setupCodes()
        setupTitle()
        setupDescription()
    }

    override fun observeData() {
        super.observeData()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataImages
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    renderImagesCarousel(it)
                }
        }
    }

    private var isDataInit = false
    override fun render(data: ViewData) {
        log.d("render")
        codesAdapter.submitList(data.codes.toList())

        if (isDataInit) return
        else isDataInit = true

        binding.editTitle.setText(data.title)
        binding.editDescription.setText(data.description)
    }

    private fun renderImagesCarousel(imageUrls: Set<String>) {
        binding.carousel.setData(
            imageUrls.map { CarouselItem(imageUrl = it) }
        )
    }

    private fun setupImages() {
        binding.carousel.apply {
            registerLifecycle(viewLifecycleOwner)
            onScrollListener = object : CarouselOnScrollListener {
                override fun onScrollStateChanged(
                    recyclerView: RecyclerView,
                    newState: Int,
                    position: Int,
                    carouselItem: CarouselItem?
                ) {
                    currentImageUrl = carouselItem?.imageUrl
                }
            }
        }

        binding.buttonAddImage.setOnClickListener {
            startPhotoPicker()
        }

        binding.buttonRemoveCurrentImage.setOnClickListener {
            viewModel.removeImages(setOf(currentImageUrl ?: return@setOnClickListener))
        }
    }

    private fun startPhotoPicker() {
        imagePickerLauncher.launch(
            createPickerConfig()
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imagePickerLauncher = registerImagePicker {
            if (it.size == 1) {
                navigateToCropFragment(it.first().uri)
            } else {
                viewModel.addImages(it.map(Image::uri))
            }
        }
    }

    private fun createPickerConfig() = ImagePickerConfig {
        mode = ImagePickerMode.MULTIPLE
        language = "en"
        theme = R.style.Theme_MilitaryAccountingApp
        // set folder mode (false by default)
        isFolderMode = false
        isIncludeVideo = false
        isOnlyVideo = false
//      arrowColor = requireActivity().getColor(R.id.color)
        // folder selection title
        folderTitle = "Folder"
        imageTitle = "Tap to select"
        doneButtonText = "Done"
        // max images can be selected (99 by default)
        isShowCamera = true
        savePath = ImagePickerSavePath(
            Environment.getExternalStorageDirectory().path,
            isRelative = false
        ) // can be a full path
    }

    private fun navigateToCropFragment(uri: Uri) {
        findNavController().navigate(
            R.id.action_editProfileFragment_to_cropImageFragment,
            Bundle().apply { putParcelable("uri_image", uri) }
        )
    }

    // ====== Barcode and other ======

    override fun onStop() {
        super.onStop()
        isDataInit = false
    }

    private val codesAdapter by lazy {
        BarCodeAdapter { barcode ->
            showCodeDetails(barcode)
        }
    }

    private fun showCodeDetails(barcode: Barcode) {
        log.d("showCodeDetails : $barcode")
        findNavController().navigate(
            R.id.action_addFragment_to_modalBottomSheetCodeDetails,
            Bundle().apply {
                putSerializable("code", barcode)
            }
        )
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