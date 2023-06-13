package com.example.militaryaccountingapp.presenter.fragment.edit

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ImagePickerSavePath
import com.example.militaryaccountingapp.R

object AddFragmentHelper {
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
}