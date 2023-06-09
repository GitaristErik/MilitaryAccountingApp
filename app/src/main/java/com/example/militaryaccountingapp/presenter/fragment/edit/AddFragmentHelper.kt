package com.example.militaryaccountingapp.presenter.fragment.edit

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ImagePickerSavePath
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.presenter.model.Barcode
import com.google.android.material.button.MaterialButton
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

object AddFragmentHelper {

    var currentImageUrl: String? = null

    fun renderImagesCarousel(
        carousel: ImageCarousel,
        carouselEmpty: View,
        buttonRemoveCurrentImage: MaterialButton,
        imageUrls: Set<String>
    ) {
        if (imageUrls.isEmpty()) {
            carousel.visibility = View.GONE
            carouselEmpty.visibility = View.VISIBLE
            buttonRemoveCurrentImage.isEnabled = false
        } else {
            carouselEmpty.visibility = View.GONE
            carousel.visibility = View.VISIBLE

            carousel.setData(
                imageUrls.map { CarouselItem(imageUrl = it) }
            )
            buttonRemoveCurrentImage.isEnabled = true
        }
    }

    val carouselScrollListener = object : CarouselOnScrollListener {
        override fun onScrollStateChanged(
            recyclerView: RecyclerView,
            newState: Int,
            position: Int,
            carouselItem: CarouselItem?
        ) {
            currentImageUrl = carouselItem?.imageUrl
        }
    }

    fun createPickerConfig() = ImagePickerConfig {
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

    fun navigateToCropFragment(fragment: Fragment, uri: Uri) {
        fragment.findNavController().navigate(
            R.id.action_addFragment_to_cropImageFragment,
            Bundle().apply { putParcelable("uri_image", uri) }
        )
    }


    fun navigateToCodeDetails(fragment: Fragment, barcode: Barcode) {
        fragment.findNavController().navigate(
            R.id.action_addFragment_to_modalBottomSheetCodeDetails,
            Bundle().apply {
                putSerializable("code", barcode)
            }
        )
    }
}