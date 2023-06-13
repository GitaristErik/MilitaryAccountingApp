package com.example.militaryaccountingapp.presenter.fragment.edit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentAddOrEditBinding
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.edit.AddOrEditViewModel.ViewData
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddOrEditFragment :
    BaseViewModelFragment<FragmentAddOrEditBinding, ViewData, AddOrEditViewModel>() {

    override val viewModel: AddOrEditViewModel by activityViewModels<AddOrEditViewModel>()
//    override val viewModel: AddOrEditViewModel by hiltNavGraphViewModels<AddOrEditViewModel>(R.id.mobile_navigation)
//    override val viewModel: AddOrEditViewModel by viewModels()


    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddOrEditBinding
        get() = FragmentAddOrEditBinding::inflate

    companion object {
        const val ELEMENT_ID_KEY = "elementId"
        const val ELEMENT_TYPE_KEY = "elementType"
        const val PARENT_ID_KEY = "parentId"
    }

    override fun initializeView() {
        sendArgsToViewModel()
        setupActionBar()
        setupViewPager()
    }

    private fun sendArgsToViewModel() {
        requireArguments().run {
            viewModel.elementId = getString(ELEMENT_ID_KEY, null).also {
                if (it != null) binding.toolbar.title = getString(R.string.edit_title)
            }
            log.e("elementId: ${viewModel.elementId}")
            viewModel.elementType = getSerializable(ELEMENT_TYPE_KEY) as Data.Type
            log.e("elementType: ${viewModel.elementType}")
            viewModel.parentId = getString(PARENT_ID_KEY)
            log.e("parentId: ${viewModel.parentId}")
        }
        viewModel.fetchData()
    }

    private fun setupViewPager() {
        binding.tabsContainer.apply {
            log.e("setupViewPager elementId: ${viewModel.elementId}")
            if (viewModel.elementId != null) {
                log.e("elementId: ${viewModel.elementId}")
                binding.tabsContainer.tabLayout.visibility = View.GONE
                viewPager.apply {
                    offscreenPageLimit = 1
                    adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
                        override fun getItemCount(): Int = 1

                        override fun createFragment(position: Int): Fragment {
                            return when (viewModel.elementType) {
                                Data.Type.CATEGORY -> AddOrEditCategoryFragment()
                                Data.Type.ITEM -> AddOrEditItemFragment()
                            }
                        }
                    }
                }
            } else {
                val tabTitles = resources.getStringArray(R.array.add_tab_title)
                viewPager.apply {
                    offscreenPageLimit = tabTitles.size
                    adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
                        override fun getItemCount(): Int = tabTitles.size

                        override fun createFragment(position: Int): Fragment {
                            return when (position) {
                                0 -> AddOrEditCategoryFragment()
                                else -> AddOrEditItemFragment()
                            }
                        }
                    }
                    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            binding.toolbar.title = tabTitles[position]
                            viewModel.elementType = if (position == 0)
                                Data.Type.CATEGORY
                            else
                                Data.Type.ITEM
                        }
                    })

                    overScrollMode = View.OVER_SCROLL_NEVER
                    getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
                }
                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                    tab.text = tabTitles[position]
                }.attach()
            }
        }
    }

    private fun setupActionBar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { back() }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.done -> {
                        viewModel.save()
//                        back()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun back() {
        findNavController().navigateUp()
    }

    override fun render(data: ViewData) {
        log.d("render")
    }
}