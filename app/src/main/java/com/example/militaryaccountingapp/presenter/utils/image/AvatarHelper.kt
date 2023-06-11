package com.example.militaryaccountingapp.presenter.utils.image

import android.app.Activity
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.ImagePickerMode
import com.esafirm.imagepicker.features.ImagePickerSavePath
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.presenter.utils.common.ext.getUriViewIntent
import com.google.android.material.imageview.ShapeableImageView

object AvatarHelper {

    const val URI_AVATAR = "uri_avatar"
    const val URI_USER = "uri_user"

    private const val SAVE_PATH = "Camera"

    fun createPickerConfig() = ImagePickerConfig {
        // default is multi image mode
        mode = ImagePickerMode.SINGLE
        // Set image picker language
        language = "en"
        theme = R.style.Theme_MilitaryAccountingApp

        // set whether pick action or camera action should return immediate result or not.
        // Only works in single mode for image picker
//        returnMode = if (returnAfterCapture) ReturnMode.ALL else ReturnMode.NONE

        // set folder mode (false by default)
        isFolderMode = false
        // include video (false by default)
        isIncludeVideo = false
        isOnlyVideo = false
//      set toolbar arrow up color
//      arrowColor = requireActivity().getColor(R.id.color)
        // folder selection title
        folderTitle = "Folder"
        imageTitle = "Tap to select"
        doneButtonText = "Done"
        // max images can be selected (99 by default)
        limit = 1
        // show camera or not (true by default)
        isShowCamera = true
        // captured image directory name ("Camera" folder by default)
        savePath = ImagePickerSavePath(SAVE_PATH)
        savePath = ImagePickerSavePath(
            Environment.getExternalStorageDirectory().path,
            isRelative = false
        ) // can be a full path
    }


    fun setupAvatarWithIntent(activity: Activity, view: ShapeableImageView, uri: Uri?) {
        Glide.with(view).let {
            if (uri == null) {
                it.load(R.drawable.ic_avatar_default)
            } else {
                it.load(uri)
            }
        }.into(view)

        if (uri != null) {
            view.setOnClickListener {
                showAvatar(activity, uri)
            }
        }
    }

    fun showAvatar(activity: Activity, uri: Uri) {
        activity.startActivity(
            uri.getUriViewIntent(activity)
        )
    }
}