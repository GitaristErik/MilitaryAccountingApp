package com.example.militaryaccountingapp.presenter.fragment.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(
    private val tabSize: Int,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = tabSize

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeCategoriesFragment()
            else -> HomeItemsFragment()
        }
    }

}
