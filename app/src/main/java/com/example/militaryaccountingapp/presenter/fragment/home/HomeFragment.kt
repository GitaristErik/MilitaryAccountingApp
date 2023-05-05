package com.example.militaryaccountingapp.presenter.fragment.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.home.HomeViewModel.ViewData
import com.example.militaryaccountingapp.presenter.fragment.home.adapter.HomeViewPagerAdapter
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, ViewData, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun initializeView()  {
        setupActionBar()
        setupViewPager()
    }

    private fun setupActionBar() {
        binding.appBarLayout.statusBarForeground = MaterialShapeDrawable().apply {
            setTint(resources.getColor(R.color.md_surface, null))
        }
    }

    private fun setupViewPager() {
        binding.apply {
            val tabTitles = resources.getStringArray(R.array.home_tab_title)
            viewPager.apply {
                offscreenPageLimit = tabTitles.size
                adapter = HomeViewPagerAdapter(
                    tabTitles.size,
                    childFragmentManager,
                    lifecycle,
                )
                overScrollMode = View.OVER_SCROLL_NEVER
                getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            }
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}