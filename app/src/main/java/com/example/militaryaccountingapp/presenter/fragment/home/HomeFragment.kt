package com.example.militaryaccountingapp.presenter.fragment.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeBinding
import com.example.militaryaccountingapp.domain.entity.user.User
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.home.HomeViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.adapter.TabsViewPagerAdapter
import com.example.militaryaccountingapp.presenter.shared.delegation.FabScreen
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseViewModelFragment<FragmentHomeBinding, ViewData, HomeViewModel>() {

    override val viewModel: HomeViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    override fun initializeView() {
        setupFab()
    }

    private fun setupFab() {
        (requireActivity() as? FabScreen)?.setOnClickFabListener {
            findNavController().navigate(
                HomeFragmentDirections.actionNavigationHomeToAddFragment()
            )
        }
    }


    private fun setupViewPager(currentUser: User) {
        binding.tabsLayout.apply {
            val tabTitles = resources.getStringArray(R.array.home_tab_title)
            viewPager.apply {
                offscreenPageLimit = tabTitles.size
                adapter = TabsViewPagerAdapter(
                    parentId = currentUser.rootCategoryId,
                    otherIds = currentUser.sharedRootCategories,
                    tabSize = tabTitles.size,
                    fragmentManager = childFragmentManager,
                    lifecycle = lifecycle,
                    getItemDetailsDirections = { data ->
                        HomeFragmentDirections.actionHomeFragmentToItemFragment(
                            id = data.id,
                            description = data.description,
                            name = data.name,
                            count = data.count,
                        )
                    },
                    getCategoryDetailsDirections = { data ->
                        HomeFragmentDirections.actionHomeFragmentToCategoryFragment(
                            id = data.id,
                            description = data.description,
                            name = data.name,
                        )
                    }
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
        if (data.currentUser is Results.Success) {
            if (data.currentUser.data == null) {
                findNavController().navigate(
                    R.id.action_navigation_home_to_loginFragment, null,
                    NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()
                )
            } else {
                setupViewPager(data.currentUser.data)
            }
        }
    }
}