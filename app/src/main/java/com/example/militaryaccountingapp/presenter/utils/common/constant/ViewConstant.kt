package com.example.militaryaccountingapp.presenter.utils.common.constant

import androidx.annotation.IntDef

object ViewConstant {
    const val VIEW_TYPE_LIST = 2000
    const val VIEW_TYPE_GRID = 2001

    @IntDef(VIEW_TYPE_LIST, VIEW_TYPE_GRID)
    @Retention(AnnotationRetention.SOURCE)
    annotation class PhotoListViewType
}