package com.example.militaryaccountingapp.presenter.utils.image

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.listener.CarouselOnScrollListener
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

object CarouselHelper {

    var currentImageUrl: String? = null

    fun renderImagesCarousel(
        carousel: ImageCarousel,
        carouselEmpty: View,
        imageUrls: Set<String>,
        buttonRemoveCurrentImage: MaterialButton? = null,
    ) {
        if (imageUrls.isEmpty()) {
            carousel.visibility = View.GONE
            carouselEmpty.visibility = View.VISIBLE
            buttonRemoveCurrentImage?.isEnabled = false
        } else {
            carouselEmpty.visibility = View.GONE
            carousel.visibility = View.VISIBLE

            carousel.setData(
                imageUrls.map { CarouselItem(imageUrl = it) }
            )
            buttonRemoveCurrentImage?.isEnabled = true
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
}