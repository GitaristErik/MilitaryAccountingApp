package com.example.militaryaccountingapp.presenter.fragment.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFragment : BaseViewModelFragment<FragmentAddBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by navGraphViewModels(R.id.mobile_navigation)

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddBinding
        get() = FragmentAddBinding::inflate


    override fun initializeView() {
        setupActionBar()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.tabsContainer.apply {
            val tabTitles = resources.getStringArray(R.array.add_tab_title)
            viewPager.apply {
                offscreenPageLimit = tabTitles.size
                adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
                    override fun getItemCount(): Int = tabTitles.size

                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> AddCategoryFragment()
                            else -> AddItemFragment()
                        }
                    }
                }

                overScrollMode = View.OVER_SCROLL_NEVER
                getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            }
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { back() }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.done -> {
                        viewModel.save()
                        back()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun back() {
        with(requireActivity() as AppCompatActivity) {
            requireActivity().onBackPressed()
        }
    }


    override fun render(data: ViewData) {
        log.d("render")
    }
}