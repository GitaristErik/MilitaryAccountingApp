package com.example.militaryaccountingapp.presenter.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentUsersListBinding
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.domain.helper.Results.Companion.anyData
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.profile.UsersListViewModel.ViewData
import com.example.militaryaccountingapp.presenter.shared.adapter.LoadingItemAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.SimpleEndlessRecyclerScrollListener
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersSearchAdapter
import com.example.militaryaccountingapp.presenter.shared.delegation.ChangeableListViewTypeScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.ErrorHandlerScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.LoadingHandlerScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.SearchableScreen
import com.example.militaryaccountingapp.presenter.shared.delegation.SearchableScreenContract
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.ChangeableListViewTypeScreenImpl
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.ErrorHandlerScreenImpl
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.LoadingHandlerScreenImpl
import com.example.militaryaccountingapp.presenter.shared.delegation.implementation.SearchableScreenImpl
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType
import com.example.militaryaccountingapp.presenter.utils.ui.ext.visible
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UsersListFragment :
    BaseViewModelFragment<FragmentUsersListBinding, ViewData, UsersListViewModel>(),
    ChangeableListViewTypeScreen by ChangeableListViewTypeScreenImpl(),
    SearchableScreen by SearchableScreenImpl(),
    LoadingHandlerScreen by LoadingHandlerScreenImpl(),
    ErrorHandlerScreen by ErrorHandlerScreenImpl() {

    override val viewModel: UsersListViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentUsersListBinding
        get() = FragmentUsersListBinding::inflate

    private lateinit var onBackPressed: OnBackPressedCallback
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressed = requireActivity().onBackPressedDispatcher.addCallback(this, false) {
            (requireActivity() as? SearchableScreenContract)?.hideSearch()
        }
    }


    private val bottomLoadingAdapter by lazy {
        LoadingItemAdapter(getString(R.string.loading_more_title))
    }

    private val usersAdapter by lazy {
        UsersSearchAdapter {
            findNavController().navigate(
                UsersListFragmentDirections.actionUsersListFragmentToDetailsUserFragment(
                    userId = it.id,
                    fullName = it.fullName,
                    rank = it.rank,
//                    name = it.name
                )
            )
        }
    }

    private val concatAdapter by lazy {
        val concatAdapterConfig = ConcatAdapter.Config.Builder()
            .setStableIdMode(ConcatAdapter.Config.StableIdMode.ISOLATED_STABLE_IDS)
            .build()

        ConcatAdapter(
            concatAdapterConfig,
            usersAdapter,
            bottomLoadingAdapter,
        )
    }

    private fun initSearch() {
        (requireActivity() as? SearchableScreenContract)?.let { screenContract ->
            val searchView = screenContract.getSearchView()
            val searchBar = screenContract.getSearchBar()
            val searchViewList = screenContract.searchViewList()
            searchBar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            prepareSearch(
                context = requireContext(),
                searchBar = searchBar,
                searchView = searchView,
                searchViewList = searchViewList,
                updateSearchBarTextOnSearch = true,
                onTypeHandler = { },
                onCloseHandler = {
                    onBackPressed.isEnabled = false
//                    viewModel.resetSearchSuggestion()
                },
                onOpenHandler = {
                    onBackPressed.isEnabled = true
                },
                onSearchHandler = {
                    viewModel.setText(it)
                },
            )
        }
    }

    override fun initializeView() {
        initSearch()
        initializeViewTypeScreen(
            context = requireContext(),
            recyclerView = binding.rvUsersList,
            adapter = concatAdapter,
            viewType = ViewType.LIST,
            topFullSpanItemCount = { 0 },
            singleSpanItemCount = { usersAdapter.itemCount },
        )
        binding.rvUsersList.addOnScrollListener(
            SimpleEndlessRecyclerScrollListener(currentSpanSize()) {},
        )
        binding.rvUsersList.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            )
        )
    }

    override fun render(data: ViewData) {
        log.d("render")
        binding.rvUsersList.visible()

        renderLoading(data)
        renderSuccess(data)
        renderError(data)
    }

    private fun renderLoading(data: ViewData) {
        onHandleLoading(
            mainViewContent = binding.rvUsersList,
            layoutLoading = binding.layoutLoading,
            data = data.mainData,
            page = 0,
            hasCache = false,
            onDisplayingCache = null,
            onLoadingNextPage = {
                usersAdapter.submitList(data.mainData.anyData().orEmpty())
                bottomLoadingAdapter.startLoading()
            },
            onNoLoading = {
                bottomLoadingAdapter.stopLoading()
            },
        )
    }

    private fun renderSuccess(data: ViewData) {
        val mainData = data.mainData
        if (mainData is Results.Success) {
            log.d("renderSuccess")
            binding.rvUsersList.visible()
            usersAdapter.submitList(mainData.data)
        }
    }

    private fun renderError(data: ViewData) {
        onHandleError(
            context = requireContext(),
            mainViewContent = binding.rvUsersList,
            layoutError = binding.layoutError,
            data = data.mainData,
            page = 0,
            onRetry = { viewModel.reload() },
            onErrorNextPage = {
                val errorData = data.mainData as Results.Failure
                usersAdapter.submitList(errorData.anyData().orEmpty())
                val errorMessage = errorData.throwable.message ?: "Something went wrong"
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            },
        )
    }

}