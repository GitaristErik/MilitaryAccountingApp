package com.example.militaryaccountingapp.presenter.fragment.edit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.esafirm.imagepicker.features.ImagePickerLauncher
import com.esafirm.imagepicker.features.registerImagePicker
import com.esafirm.imagepicker.model.Image
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddCategoryBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCategoryFragment :
    BaseViewModelFragment<FragmentAddCategoryBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddCategoryBinding
        get() = FragmentAddCategoryBinding::inflate

    private lateinit var imagePickerLauncher: ImagePickerLauncher

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
                    AddFragmentHelper.renderImagesCarousel(
                        binding.carousel,
                        binding.carouselEmpty,
                        binding.buttonRemoveCurrentImage,
                        it
                    )
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

    private fun setupImages() {
        binding.carousel.apply {
            registerLifecycle(viewLifecycleOwner)
            onScrollListener = AddFragmentHelper.carouselScrollListener
        }

        binding.buttonAddImage.setOnClickListener {
            startPhotoPicker()
        }

        binding.buttonRemoveCurrentImage.setOnClickListener {
            viewModel.removeImages(
                setOf(
                    AddFragmentHelper.currentImageUrl ?: return@setOnClickListener
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
        isDataInit = false
    }

    private val codesAdapter by lazy {
        BarCodeAdapter { barcode ->
            AddFragmentHelper.navigateToCodeDetails(this, barcode)
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