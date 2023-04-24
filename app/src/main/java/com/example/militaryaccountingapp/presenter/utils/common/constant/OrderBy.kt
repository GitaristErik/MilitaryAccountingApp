package com.example.militaryaccountingapp.presenter.utils.common.constant

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.militaryaccountingapp.R

enum class OrderBy {
    ASCENDING {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_sort)
        }
    },
    DESCENDING {
        override fun getIcon(context: Context): Drawable? {
            return ContextCompat.getDrawable(context, R.drawable.ic_sort)
        }
    };

    abstract fun getIcon(context: Context): Drawable?
}