package com.example.militaryaccountingapp.presenter.utils.common.constant

import android.content.Context
import com.example.militaryaccountingapp.R

enum class SortType(val resId: Int?) {

    NAME(R.id.option1) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_option1)
    },

    DATE_CREATED(R.id.option2) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_option2)
    },

    DATE_UPDATED(R.id.option3) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_option3)
    },

    DESCRIPTION(R.id.option4) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_option4)
    };

    abstract fun getDisplayName(context: Context): String?

    companion object {
        fun fromResId(resId: Int): SortType? {
            return values().firstOrNull { it.resId == resId }
        }
    }

}