package com.example.militaryaccountingapp.presenter.shared.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDirections
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.militaryaccountingapp.presenter.fragment.categories.CategoriesFragment
import com.example.militaryaccountingapp.presenter.fragment.items.ItemsFragment
import com.example.militaryaccountingapp.presenter.model.CategoryUi
import com.example.militaryaccountingapp.presenter.model.ItemUi

class TabsViewPagerAdapter(
    private val parentId:String,
    private val getItemDetailsDirections: (ItemUi) -> NavDirections,
//    private val getItemAddDirections: () -> NavDirections,
    private val getCategoryDetailsDirections: (CategoryUi) -> NavDirections,
//    private val getCategoryAddDirections: () -> NavDirections,
    private val tabSize: Int,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = tabSize

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CategoriesFragment(parentId, getCategoryDetailsDirections)//, getCategoryAddDirections)
            else -> ItemsFragment(parentId, getItemDetailsDirections)//, getItemAddDirections)
        }
    }

}
