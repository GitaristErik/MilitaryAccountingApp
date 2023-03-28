package com.example.militaryaccountingapp.presenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import timber.log.Timber

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    protected open val screenName: String get() = javaClass.simpleName
    protected val log: Timber.Tree
        get() {
            val methodName = Thread.currentThread().stackTrace[3].methodName
            return Timber.tag("${screenName}::${methodName}")
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        log.d("base")
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        log.d("base")
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    override fun onDestroyView() {
        log.d("base")
        _binding = null
        super.onDestroyView()
    }

    protected abstract fun initializeView()
}