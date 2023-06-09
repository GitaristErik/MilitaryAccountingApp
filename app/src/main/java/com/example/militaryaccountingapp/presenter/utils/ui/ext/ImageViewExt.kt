package com.example.militaryaccountingapp.presenter.utils.ui.ext

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.presenter.utils.image.ImageOptions
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.github.alexzhirkevich.customqrgenerator.vector.createQrVectorOptions
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorBallShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorColor
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorFrameShape
import com.github.alexzhirkevich.customqrgenerator.vector.style.QrVectorPixelShape

@SuppressLint("CheckResult")
fun ImageView?.load(
    url: String,
    builder: ImageOptions.() -> Unit = {},
) {
    if (this == null) return
    val validContext = getValidContext(this) ?: return
    val base = ImageOptions()
    base.builder()
    if (url.isBlank()) {
        Glide.with(validContext).load(base.imageOnFail).apply(base.toRequestOptions()).into(this)
    } else {
        val requestBuilder = Glide.with(validContext).load(url)

        if (base.onImageLoaded != null || base.onLoadFailed != null) {
            requestBuilder.listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    base.onLoadFailed?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    base.onImageLoaded?.invoke(resource)
                    return false
                }
            })
        }

        requestBuilder.apply(base.toRequestOptions())
        if (base.thumbnail != null) requestBuilder.thumbnail(base.thumbnail)
        if (base.crossFade) requestBuilder.transition(DrawableTransitionOptions.withCrossFade())

        requestBuilder.into(this)
    }
}

fun ImageView?.clearLoad() {
    if (this == null) return
    val validContext = getValidContext(this) ?: return

    Glide.with(validContext).clear(this)
}

private fun getValidContext(imageView: ImageView?): Context? {
    if (imageView == null) return null

    val context = when (val rawContext = imageView.context) {
        is Activity -> rawContext
        is ContextWrapper -> rawContext.baseContext
        else -> rawContext
    }

    if (context is Activity) {
        if (context.isFinishing || context.isDestroyed) return null
    }
    return context
}

fun ImageView.initAsQrMini(
    code: String,
) {
    val qrData = QrData.Text(code)
    val options = createQrVectorOptions {
        colors {
            dark = QrVectorColor.Solid(
                ContextCompat.getColor(this@initAsQrMini.context, R.color.md_onSecondaryContainer)
            )
            ball = QrVectorColor.Solid(
                ContextCompat.getColor(this@initAsQrMini.context, R.color.md_onPrimaryContainer)
            )
            frame = QrVectorColor.Solid(
                ContextCompat.getColor(this@initAsQrMini.context, R.color.md_onPrimaryContainer)
            )
        }
        shapes {
            darkPixel = QrVectorPixelShape
                .Circle(.75f)
            ball = QrVectorBallShape
                .Circle(.5f)
            frame = QrVectorFrameShape
                .RoundCorners(.5f, .5f)
        }
    }
    val drawable: Drawable = QrCodeDrawable(qrData, options)
    this.setImageDrawable(drawable)
}

fun ImageView.initAsQrFull(
    code: String,
) {
    val qrData = QrData.Text(code)
    val options = createQrVectorOptions {
        padding = .125f
        background {
            drawable = ContextCompat.getDrawable(
                this@initAsQrFull.context,
                R.drawable.background_qr_code
            )
        }
        colors {
            dark = QrVectorColor.Solid(
                ContextCompat.getColor(this@initAsQrFull.context, R.color.md_secondaryContainer)
            )
            ball = QrVectorColor.Solid(
                ContextCompat.getColor(this@initAsQrFull.context, R.color.md_primaryContainer)
            )
            frame = QrVectorColor.Solid(
                ContextCompat.getColor(this@initAsQrFull.context, R.color.md_primaryContainer)
            )
        }
        shapes {
            darkPixel = QrVectorPixelShape
                .Circle(0.85f)
            ball = QrVectorBallShape
                .Circle(.5f)
            frame = QrVectorFrameShape
                .RoundCorners(.5f, .75f)
        }
    }
    val drawable: Drawable = QrCodeDrawable(qrData, options)
    this.setImageDrawable(drawable)
}
