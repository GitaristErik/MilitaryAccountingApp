package com.example.militaryaccountingapp.presenter.fragment.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentCategoriesBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.categories.CategoriesViewModel.ViewData
import com.example.militaryaccountingapp.presenter.fragment.home.HomeFragmentDirections
import com.example.militaryaccountingapp.presenter.shared.ScrollableTopScreen
import com.example.militaryaccountingapp.presenter.shared.adapter.CategoryAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.HeaderToolsAdapter
import com.example.militaryaccountingapp.presenter.shared.delegation.ChangeableListViewTypeScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.ChangeableListViewTypeScreenImpl

class CategoriesFragment :
    BaseViewModelFragment<FragmentCategoriesBinding, ViewData, CategoriesViewModel>(),
    ChangeableListViewTypeScreen by ChangeableListViewTypeScreenImpl(),
    ScrollableTopScreen {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoriesBinding
        get() = FragmentCategoriesBinding::inflate

    override val viewModel: CategoriesViewModel by viewModels()

    private val headerAdapter by lazy {
        HeaderToolsAdapter(
            sortType = viewModel.sortType,
            orderBy = viewModel.orderBy,
            onChangeViewType = {
                viewModel.changeViewType()
            },
            onChangeOrderBy = {
                viewModel.changeOrderBy()
            },
            onNewSortOptionSelected = {
                scrollToTop()
                viewModel.changeSortType(it)
            }
        ).apply {
            updatingSpaceTopBasedOnView(binding.root) {
                resources.getDimensionPixelSize(R.dimen.height_tabs)
            }
        }
    }

    private val categoriesAdapter: CategoryAdapter by lazy {
        CategoryAdapter(viewModel.viewType) { data, view ->
            val extras = FragmentNavigatorExtras(view to view.transitionName)
            val navDirections = HomeFragmentDirections
                .actionHomeFragmentToCategoryFragment(data.id)
            findNavController().navigate(navDirections, extras)
        }
    }

    private val concatAdapter by lazy {
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()

        ConcatAdapter(
            concatAdapterConfig,
            headerAdapter,
            categoriesAdapter
        )
    }


    override fun initializeView() {
        initializeViewTypeScreen(
            context = requireContext(),
            recyclerView = binding.rvCategories,
            adapter = concatAdapter,
            viewType = viewModel.viewType,
            topFullSpanItemCount = { headerAdapter.itemCount },
            singleSpanItemCount = { categoriesAdapter.itemCount },
        )
        categoriesAdapter.stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    override fun render(data: ViewData) {
        super.render(data)
        log.d("render: $data")
        headerAdapter.setViewType(data.viewType)
        headerAdapter.setOrderBy(data.orderBy)
        headerAdapter.setSortType(data.sortType)
        onHandleLayoutType(data.viewType, binding.rvCategories, concatAdapter) {
            categoriesAdapter.updateViewType(data.viewType)
        }
        categoriesAdapter.submitList(data.categories)
    }

    override fun scrollToTop() {
        binding.rvCategories.scrollToPosition(0)
    }
}