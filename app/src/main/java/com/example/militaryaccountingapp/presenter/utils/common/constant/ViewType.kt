package com.example.militaryaccountingapp.presenter.utils.common.constant

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.militaryaccountingapp.R

enum class ViewType(private val iconResId: Int) {
    LIST(R.drawable.ic_baseline_view_list_24dp),
    GRID(R.drawable.ic_baseline_view_grid_24dp);

    open fun getIcon(context: Context): Drawable? {
        return ContextCompat.getDrawable(context, iconResId)
    }
}