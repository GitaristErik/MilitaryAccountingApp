package com.example.militaryaccountingapp.presenter.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.example.militaryaccountingapp.presenter.BaseViewModel
import kotlinx.coroutines.launch

abstract class BaseViewModelFragment<VB : ViewBinding, VD, VM : BaseViewModel<VD>> :
    BaseFragment<VB>() {

    protected abstract val viewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated: base with ViewModel")
        observeData()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { render(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toast
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { message ->
                    message?.let {
                        renderToast(it)
                        viewModel.onToastShown()
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.spinner
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { renderSpinner(it) }
        }
    }

    protected open suspend fun observeCustomData() = Unit

    protected open fun render(data: VD) = Unit


    protected open fun renderSpinner(show: Boolean) {
        provideProgressBar?.invoke()?.visibility =
            if (show) View.VISIBLE else View.GONE
        log.i("show progress bar: $show")
    }

    protected open val provideProgressBar: (() -> View)? = null

    protected open fun renderToast(message: Any) {
        when (message) {
            is Int -> showToast(message)
            is String -> showToast(message)
            else -> throw IllegalArgumentException("Unknown message type")
        }
    }

    @VisibleForTesting
    fun setInitialData(initialData: VD) {
        viewModel.setInitialData(initialData)
    }
}