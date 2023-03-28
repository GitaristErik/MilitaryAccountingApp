package com.example.militaryaccountingapp.presenter.utils.common.constant

import android.content.Context
import com.example.militaryaccountingapp.R

enum class SortConstant(val resId: Int?) {

    NEWEST(R.id.newest_first) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_newest_first)
    },

    OLDEST(R.id.oldest_first) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_oldest_first)
    },

    AZ(R.id.a_z) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_a_z)
    },

    ZA(R.id.z_a) {
        override fun getDisplayName(context: Context) =
            context.getString(R.string.home_item_header_menu_z_a)
    };

    abstract fun getDisplayName(context: Context): String?

    companion object {
        fun fromResId(resId: Int): SortConstant? {
            return values().firstOrNull { it.resId == resId }
        }
    }

}