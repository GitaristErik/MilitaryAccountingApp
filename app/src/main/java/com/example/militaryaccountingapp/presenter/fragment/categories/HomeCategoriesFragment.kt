package com.example.militaryaccountingapp.presenter.fragment.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeCategoriesBinding
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.categories.CategoriesViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.ScrollableTopScreen
import com.example.militaryaccountingapp.presenter.shared.adapter.HeaderToolsAdapter

class HomeCategoriesFragment :
    BaseViewModelFragment<FragmentHomeCategoriesBinding, ViewData, CategoriesViewModel>(),
    ScrollableTopScreen {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomeCategoriesBinding
        get() = FragmentHomeCategoriesBinding::inflate

    override val viewModel: CategoriesViewModel by viewModels()

    override fun initializeView() {
        binding.rvCategories.apply {
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = concatAdapter
        }
    }

    override fun render(data: ViewData) {
        super.render(data)
        headerAdapter.setViewType(data.viewType)
        headerAdapter.setOrderBy(data.orderBy)
        headerAdapter.setSortType(data.sortType)
        categoriesAdapter.updateItems(data.categories)
    }

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

    private val categoriesAdapter: TempAdapter by lazy {
        TempAdapter(mutableListOf())
    }

    private val concatAdapter by lazy {
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
//            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()

        ConcatAdapter(
            concatAdapterConfig,
            headerAdapter,
            categoriesAdapter
        )
    }

    override fun scrollToTop() {
        binding.rvCategories.scrollToPosition(0)
    }
}