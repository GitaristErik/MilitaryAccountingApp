package com.example.militaryaccountingapp.presenter.fragment.profile

import android.app.Activity
import android.net.Uri
import com.bumptech.glide.Glide
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.presenter.utils.common.ext.getUriViewIntent
import com.google.android.material.imageview.ShapeableImageView

object ProfileHelper {

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