package com.example.militaryaccountingapp.presenter.fragment.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentItemsBinding
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.helper.Results.Companion.anyData
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.items.ItemsViewModel.ViewData
import com.example.militaryaccountingapp.presenter.model.ItemUi
import com.example.militaryaccountingapp.presenter.shared.ScrollableTopScreen
import com.example.militaryaccountingapp.presenter.shared.adapter.HeaderToolsAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.ItemsAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.LoadingItemAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.SimpleEndlessRecyclerScrollListener
import com.example.militaryaccountingapp.presenter.shared.delegation.ChangeableListViewTypeScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.ErrorHandlerScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.LoadingHandlerScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.ChangeableListViewTypeScreenImpl
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.ErrorHandlerScreenImpl
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.LoadingHandlerScreenImpl
import com.example.militaryaccountingapp.presenter.utils.ui.ext.visible
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemsFragment(
    private val parentId: String = "",
    private val getItemDetailsDirections: (ItemUi) -> NavDirections,
    private val otherIds: List<String> = emptyList(),
) : BaseViewModelFragment<FragmentItemsBinding, ViewData, ItemsViewModel>(),
    ChangeableListViewTypeScreen by ChangeableListViewTypeScreenImpl(),
    LoadingHandlerScreen by LoadingHandlerScreenImpl(),
    ErrorHandlerScreen by ErrorHandlerScreenImpl(),
    ScrollableTopScreen {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentItemsBinding
        get() = FragmentItemsBinding::inflate

    override val viewModel: ItemsViewModel by viewModels()

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
/*            updatingSpaceTopBasedOnView(binding.root) {
                resources.getDimensionPixelSize(R.dimen.height_tabs)
            }*/
        }
    }

    private val itemsAdapter: ItemsAdapter by lazy {
        ItemsAdapter(viewModel.viewType) { data, view ->
            val extras = FragmentNavigatorExtras(view to view.transitionName)
            findNavController().navigate(getItemDetailsDirections(data), extras)
        }
    }

    private val topLoadingAdapter by lazy {
        LoadingItemAdapter(getString(R.string.loading_refresh_title))
    }
    private val bottomLoadingAdapter by lazy {
        LoadingItemAdapter(getString(R.string.loading_more_title))
    }

    private val concatAdapter by lazy {
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()

        ConcatAdapter(
            concatAdapterConfig,
            headerAdapter,
            topLoadingAdapter,
            itemsAdapter,
            bottomLoadingAdapter,
        )
    }

    private val endlessScrollListener by lazy {
        SimpleEndlessRecyclerScrollListener(currentSpanSize()) {}
    }

    override fun initializeView() {
        initializeViewTypeScreen(
            context = requireContext(),
            recyclerView = binding.rvItems,
            adapter = concatAdapter,
            viewType = viewModel.viewType,
            topFullSpanItemCount = { headerAdapter.itemCount + topLoadingAdapter.itemCount },
            singleSpanItemCount = { itemsAdapter.itemCount },
        )
        itemsAdapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        endlessScrollListener.isEnabled = false
        binding.rvItems.addOnScrollListener(endlessScrollListener)
        viewModel.reload()
        viewModel.parentId = parentId
    }

    override fun render(data: ViewData) {
        super.render(data)
        log.d("render: $data")
        headerAdapter.setViewType(data.viewType)
        headerAdapter.setOrderBy(data.orderBy)
        headerAdapter.setSortType(data.sortType)
        onHandleLayoutType(data.viewType, binding.rvItems, concatAdapter) {
            itemsAdapter.updateViewType(data.viewType)
        }

        renderLoading(data)
        renderSuccess(data)
        renderError(data)
    }

    private fun renderLoading(data: ViewData) {
        onHandleLoading(
            mainViewContent = binding.rvItems,
            layoutLoading = binding.layoutLoading,
            data = data.mainData,
            page = data.page,
            hasCache = data.cache.isNotEmpty(),
            onDisplayingCache = {
                itemsAdapter.submitList(data.cache)
                topLoadingAdapter.startLoading()
            },
            onLoadingNextPage = {
                itemsAdapter.submitList(data.mainData.anyData().orEmpty())
                bottomLoadingAdapter.startLoading()
            },
            onNoLoading = {
                topLoadingAdapter.stopLoading()
                bottomLoadingAdapter.stopLoading()
            },
        )
    }

    private fun renderSuccess(data: ViewData) {
        val mainData = data.mainData
        if (mainData is Results.Success) {
            log.d("renderSuccess")
            endlessScrollListener.isEnabled = true
            binding.rvItems.visible()
            itemsAdapter.submitList(mainData.data)
        }
    }

    private fun renderError(data: ViewData) {
        onHandleError(
            context = requireContext(),
            mainViewContent = binding.rvItems,
            layoutError = binding.layoutError,
            data = data.mainData,
            page = data.page,
            onRetry = { viewModel.reload() },
            onErrorNextPage = {
                val errorData = data.mainData as Results.Failure
                itemsAdapter.submitList(errorData.anyData().orEmpty())
                val errorMessage = errorData.throwable.message ?: "Something went wrong"
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            },
        )
    }


    override fun scrollToTop() {
        binding.rvItems.scrollToPosition(0)
    }
}