package com.example.militaryaccountingapp.presenter.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.home.HomeViewModel.ViewData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, ViewData, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun initializeView() {
        binding.apply {
            val tabTitles = resources.getStringArray(R.array.home_tab_title)
            viewPager.adapter = HomeViewPagerAdapter(
                tabTitles.size,
                childFragmentManager,
                lifecycle,
            )

            viewPager.offscreenPageLimit = tabTitles.size
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}