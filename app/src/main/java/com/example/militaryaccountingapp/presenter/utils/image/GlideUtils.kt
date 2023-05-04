package com.example.militaryaccountingapp.presenter.utils.image

import android.content.Context
import com.bumptech.glide.Glide

object GlideUtils {
    fun thumbnailBuilder(context: Context, url: String?) = Glide
        .with(context)
        .load(url)
        .sizeMultiplier(0.05F)
}