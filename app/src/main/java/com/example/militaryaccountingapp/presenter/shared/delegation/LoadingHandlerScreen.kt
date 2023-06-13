package com.example.militaryaccountingapp.presenter.shared.delegation

import android.view.View
import com.example.militaryaccountingapp.databinding.LayoutLoadingFullPageBinding
import com.example.militaryaccountingapp.domain.helper.Results

interface LoadingHandlerScreen {
    fun onHandleLoading(
        mainViewContent: View,
        layoutLoading: LayoutLoadingFullPageBinding,
        data: Results<*>,
        page: Long,
        hasCache: Boolean,
        onDisplayingCache: (() -> Unit)?,
        onLoadingNextPage: (() -> Unit)?,
        onNoLoading: (() -> Unit)?,
    )
}