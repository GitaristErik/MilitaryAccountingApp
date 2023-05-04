package com.example.militaryaccountingapp.presenter.utils.image

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.Serializable

/**
 * Created by rizky.kharisma on 12/01/23.
 * @ngengs
 */
class ImageOptions {
    /**
     * Placeholder Image when loading
     */
    @DrawableRes
    var imageOnLoading: Int = 0

    /**
     * Placeholder Image when loading
     */
    var imageOnLoadingDrawable: Drawable? = null

    /**
     * Image when error
     */
    @DrawableRes
    var imageOnFail: Int = 0

    /**
     * Image when error
     */
    var imageOnFailDrawable: Drawable? = null

    /**
     * Thumbnail of image
     */
    var thumbnail: RequestBuilder<Drawable>? = null

    /**
     * Image decode format
     */
    var decodeFormat: DecodeFormat = DecodeFormat.PREFER_RGB_565

    /**
     * On image loading finished listener
     */
    var onImageLoaded: ((image: Drawable?) -> Unit)? = null

    /**
     * On image load failed listener
     */
    var onLoadFailed: (() -> Unit)? = null

    /**
     * Transform image after image loaded
     */
    var transformation: Transformation<Bitmap>? = null

    /**
     * Image will be loaded only when available in cache, otherwise will trigger [onLoadFailed]
     */
    var onlyRetrieveFromCache: Boolean = false

    /**
     * Enable or disable cross fade image transition
     */
    var crossFade: Boolean = false

    /**
     * Whether the image should be cached in memory
     * Tips: if you are loading a huge bitmap, better set this to true to prevent OOM
     */
    var skipMemoryCache: Boolean = false

    /**
     * @return width x height size
     */
    var resize: Size? = null

    var centerCrop: Boolean = false
    var centerInside: Boolean = false

    @SuppressLint("CheckResult")
    fun toRequestOptions(): RequestOptions {
        return RequestOptions().also { options ->
            if (centerCrop && !centerInside) options.centerCrop()
            if (centerInside && !centerCrop) options.centerInside()
            transformation?.let { options.transform(it) }

            resize?.let { options.override(it.width, it.height) }

            options.format(decodeFormat)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .skipMemoryCache(skipMemoryCache)
                .error(imageOnFail)

            if (imageOnLoadingDrawable != null) {
                options.placeholder(imageOnLoadingDrawable)
            } else {
                options.placeholder(imageOnLoading)
            }

            if (imageOnFailDrawable != null) {
                options.error(imageOnFailDrawable)
            } else {
                options.error(imageOnFail)
            }

            if (onlyRetrieveFromCache) {
                options.onlyRetrieveFromCache(true)
            }
        }
    }

    data class Size(var width: Int, var height: Int) : Serializable
}