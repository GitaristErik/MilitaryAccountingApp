package com.example.militaryaccountingapp.presenter.fragment.details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentCategoryBinding
import com.example.militaryaccountingapp.domain.entity.data.Category
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.details.DetailsCategoryViewModel.ViewData
import com.example.militaryaccountingapp.presenter.fragment.edit.ModalBottomSheetShare
import com.example.militaryaccountingapp.presenter.model.LastChangedUi
import com.example.militaryaccountingapp.presenter.model.UserSearchUi
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.TabsViewPagerAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersSearchAdapter
import com.example.militaryaccountingapp.presenter.shared.chart.count.ChartPieBinder
import com.example.militaryaccountingapp.presenter.shared.chart.count.ListenerValueSelected
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChart
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import com.example.militaryaccountingapp.presenter.shared.delegation.FabScreen
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import com.example.militaryaccountingapp.presenter.utils.image.CarouselHelper
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsCategoryFragment :
    BaseViewModelFragment<FragmentCategoryBinding, ViewData, DetailsCategoryViewModel>() {

    override val viewModel: DetailsCategoryViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCategoryBinding
        get() = FragmentCategoryBinding::inflate

    override fun initializeView() {
        sendArgumentsToViewModel()
        setupImages()
        setupCodes()
        setupActionBar()
        setupHistorySpinner()
        observeCustomData2()
        setupViewPager()
        setupFab()
        setupMoreButtons()
        setupUsers()
        setupShareFragment()
    }

    private fun setupMoreButtons() {
        binding.lastChangedBtnAll.setOnClickListener {
            findNavController().navigate(DetailsCategoryFragmentDirections.actionCategoryFragmentToNavigationHistory())
        }
        binding.countBtnAll.setOnClickListener {
            findNavController().navigate(DetailsCategoryFragmentDirections.actionCategoryFragmentToNavigationStatistics())
        }
    }


    private val usersAdapter by lazy {
        UsersSearchAdapter { user ->
            viewModel.loadUserPermission(userId = user.id) { permission, id ->
                log.d("permission is laoded!! id: $id  | $permission")
                if (permission is Results.Success) {
                    val nav =
                        DetailsCategoryFragmentDirections.actionCategoryFragmentToModalBottomSheetShare(
                            userId = user.id,
                            permission = permission.data,
                            categoryId = id
                        )
                    findNavController().navigate(nav)
                }
            }
        }
    }


    private fun setupUsers() {
        binding.rvShare.adapter = usersAdapter
        binding.rvShare.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            )
        )
    }

    private fun setupShareFragment() {
        setFragmentResultListener(ModalBottomSheetShare.REQUEST_KEY) { key, bundle ->
            val perm = bundle.getSerializable(ModalBottomSheetShare.PERMISSION) as UserPermission?
            val userId = bundle.getString(ModalBottomSheetShare.USER_ID)!!
            viewModel.setRules(
                userId = userId,
                canRead = perm != null,
                canEdit = perm?.canWrite ?: false,
                canShareRead = perm?.canShare ?: false,
                canShareEdit = perm?.canShareForWrite ?: false,
            )
        }
    }

    private fun setupFab() {
        (requireActivity() as? FabScreen)?.run {
            showFab()
            setOnClickFabListener {
                (viewModel.dataCategory.value as Results.Success).data.let {
                    findNavController().navigate(
                        DetailsCategoryFragmentDirections.actionCategoryFragmentToAddFragment(
                            elementType = Data.Type.CATEGORY,
                            parentId = it.id
                        )
                    )
                }
            }
        }
    }

    private fun setupViewPager() {
        binding.tabs.root.visibility = View.GONE
    }

    private fun renderViewPager(parentId: String) {
        binding.tabs.root.visibility = View.VISIBLE
        binding.tabs.apply {
            val tabTitles = resources.getStringArray(R.array.home_tab_title)
            viewPager.apply {
                offscreenPageLimit = tabTitles.size
                adapter = TabsViewPagerAdapter(
                    parentId = parentId,
                    tabSize = tabTitles.size,
                    fragmentManager = childFragmentManager,
                    lifecycle = lifecycle,
                    getItemDetailsDirections = { data ->
                        DetailsCategoryFragmentDirections.actionCategoryFragmentToItemFragment(
                            id = data.id,
                            description = data.description,
                            name = data.name,
                            count = data.count,
                        )
                    },
                    getCategoryDetailsDirections = { data ->
                        DetailsCategoryFragmentDirections.actionCategoryFragmentSelf(
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

    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
            binding.toolbar.apply {
                setNavigationOnClickListener {
                    onBackPressedDispatcher.onBackPressed()
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.edit -> {
                            // Handle edit
                            handleEdit()
                            true
                        }

                        R.id.delete -> {
                            handleDelete()
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    private fun handleEdit() {
        val item = viewModel.dataCategory.value
        if (item !is Results.Success) return
        findNavController().navigate(
            DetailsCategoryFragmentDirections.actionCategoryFragmentToAddFragment(
                elementId = item.data.id,
                elementType = Data.Type.CATEGORY,
                parentId = item.data.parentId
            )
        )
    }

    private fun handleDelete() {
        startDeleteDialog {
            viewModel.deleteItem()
        }
    }

    private fun startDeleteDialog(onDelete: (() -> Unit)? = null) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.add_code_alert_title))
            .setMessage(getString(R.string.add_category_alert_message))
            .setPositiveButton(getString(R.string.add_code_alert_positive)) { _, _ ->
                onDelete?.invoke()
            }
            .setNegativeButton(getString(R.string.add_code_alert_negative)) { _, _ -> }
            .show()
    }


    private fun sendArgumentsToViewModel() {
        requireArguments().run {
            viewModel.sendArguments(
                id = getString("id")!!,
                name = getString("name")!!,
                parentIds = getStringArray("parentIds"),
                imageUrlIds = getStringArray("imageUrlIds"),
                description = getString("description")!!,
            )
        }
    }

    private fun observeCustomData2() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataImages
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    CarouselHelper.renderImagesCarousel(
                        binding.carousel,
                        binding.carouselEmpty,
                        it
                    )
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataCategory
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    renderCategory(it)
                }
        }
    }

    override fun render(data: ViewData) {
        log.d("render in details category fragment: codes: ${data.codes} users: ${data.users} lastChanged: ${data.lastChanged} isDeleted: ${data.isDeleted}")
        codesAdapter.submitList(data.codes.toList())
        renderLastChanged(data.lastChanged)
        renderDeleted(data.isDeleted)
        renderUsers(data.users)
        renderCountChart(data.chartData ?: return)
    }

    private val countChartUsers by lazy {
        ChartPieBinder(requireContext(), binding.countChart, ListenerValueSelected).apply {
            bind("All Items")
        }
    }

    private fun renderCountChart(
        entries: List<PieEntry>
    ) {
        countChartUsers.setData(entries)
    }

    private fun renderUsers(users: Results<List<UserSearchUi>>) {
        when (users) {
            is Results.Success -> {
                usersAdapter.submitList(users.data)
            }

            is Results.Failure -> {
                users.throwable.localizedMessage?.let { showToast(it) }
            }

            else -> {}
        }
    }

    private fun renderDeleted(deleted: Results<Unit>) {
        when (deleted) {
            is Results.Success -> {
                viewModel.handleShowedDeleted()
                findNavController().popBackStack()
            }

            is Results.Failure -> {
                deleted.throwable.localizedMessage?.let { showToast(it) }
            }

            else -> {}
        }
    }

    private fun renderLastChanged(lastChanged: Results<LastChangedUi>) = with(binding) {
        if (lastChanged is Results.Success) {
            lastChangedLayout.root.visibility = View.VISIBLE
            lastChangedLabel.visibility = View.VISIBLE
            lastChangedBtnAll.visibility = View.VISIBLE
            lastChangedLayout.date.text = lastChanged.data.timestamp.asFormattedDateString()
            lastChangedLayout.user.text = lastChanged.data.name
            lastChangedLayout.rank.text = lastChanged.data.rank
            if (lastChanged.data.avatarUrl?.isNotEmpty() == true) {
                Glide.with(root)
                    .load(lastChanged.data.avatarUrl)
                    .into(lastChangedLayout.icon)
            }
        } else {
            lastChangedLayout.root.visibility = View.GONE
            lastChangedLabel.visibility = View.GONE
            lastChangedBtnAll.visibility = View.GONE
        }
    }

    private fun renderCategory(category: Results<Category>) {
        when (category) {
            is Results.Success -> {
                renderCategory(category.data)
                renderSpinner(false)
                val parentId = category.data.parentId
                // parentId is null when category is not loaded
                if (parentId != null) {
                    renderViewPager(category.data.id)
                }
            }

            is Results.Loading -> {
                renderSpinner(true)
                if (category.oldData != null) {
                    renderCategory(category.oldData)
                }
            }

            is Results.Failure -> {
                renderSpinner(false)
                showToast(category.throwable.message ?: "Error while loading category")
            }

            else -> {
                renderSpinner(false)
            }
        }
    }

    private fun renderCategory(category: Category) = with(binding) {
        toolbar.title = category.name
        description.text = category.description
        location.text = category.allParentNames.joinToString(separator = " -> ")
    }


    private fun setupImages() {
        binding.carousel.apply {
            registerLifecycle(viewLifecycleOwner)
            onScrollListener = CarouselHelper.carouselScrollListener
        }
    }


    private val codesAdapter by lazy {
        BarCodeAdapter { barcode ->
            findNavController().navigate(
                DetailsCategoryFragmentDirections.actionCategoryFragmentToModalBottomSheetCodeDetails(
                    code = barcode,
                    isRenderDelete = false
                )
            )
        }
    }

    private fun setupCodes() {
        binding.rvCodes.adapter = codesAdapter
    }

    private fun renderHistoryChart(data: ChartData? = null) {
        if (chartData == data) return
        else chartData = data

        if (data == null) {
            setHistoryChartNoDataText()
        } else {
            historyChart.displayData(data)
        }
    }


    private val historyChart: HistoryChart by lazy {
        HistoryChart(requireContext(), binding.historyChart).apply {
            setupChart()
        }
    }

    private fun setHistoryChartNoDataText() = binding.historyChart.apply {
        getPaint(Chart.PAINT_INFO).color = Color.WHITE
        setNoDataText(getString(R.string.statistics_history_loading))
        invalidate()
    }

    private fun setupHistorySpinner() {}
    private fun setupHistorySpinner2() {
        binding.historySpinner.apply {
            this.adapter = ArrayAdapter.createFromResource(
                this@DetailsCategoryFragment.requireContext(),
                R.array.history_spinner, android.R.layout.simple_spinner_item
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
//            setSelection(3)
            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        when (position) {
                            0 -> viewModel.changeHistoryChartType(DayData::class)
                            1 -> viewModel.changeHistoryChartType(WeekData::class)
                            2 -> viewModel.changeHistoryChartType(MonthData::class)
                            else -> viewModel.changeHistoryChartType(null)
                        }
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    companion object {
        private var chartData: ChartData? = null
    }
}