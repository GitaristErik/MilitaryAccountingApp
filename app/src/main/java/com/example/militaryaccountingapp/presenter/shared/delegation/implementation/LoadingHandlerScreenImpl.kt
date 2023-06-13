package com.example.militaryaccountingapp.presenter.shared.delegation.implementation

import android.view.View
import com.example.militaryaccountingapp.databinding.LayoutLoadingFullPageBinding
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.shared.delegation.LoadingHandlerScreen
import com.example.militaryaccountingapp.presenter.utils.ui.ext.gone
import com.example.militaryaccountingapp.presenter.utils.ui.ext.visible

class LoadingHandlerScreenImpl : LoadingHandlerScreen {
    override fun onHandleLoading(
        mainViewContent: View,
        layoutLoading: LayoutLoadingFullPageBinding,
        data: Results<*>,
        page: Long,
        hasCache: Boolean,
        onDisplayingCache: (() -> Unit)?,
        onLoadingNextPage: (() -> Unit)?,
        onNoLoading: (() -> Unit)?,
    ) {
        if (data is Results.Loading) {
            if (page == 1L && !hasCache) {
                mainViewContent.gone()
                layoutLoading.root.visible()
            } else if (page == 1L) {
                layoutLoading.root.gone()
                onDisplayingCache?.invoke()
            } else {
                layoutLoading.root.gone()
                onLoadingNextPage?.invoke()
            }
        } else {
            layoutLoading.root.gone()
            onNoLoading?.invoke()
        }
    }
}