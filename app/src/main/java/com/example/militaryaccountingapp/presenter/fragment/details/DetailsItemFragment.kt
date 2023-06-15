package com.example.militaryaccountingapp.presenter.fragment.details

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentItemBinding
import com.example.militaryaccountingapp.domain.entity.data.Data
import com.example.militaryaccountingapp.domain.entity.data.Item
import com.example.militaryaccountingapp.domain.entity.user.UserPermission
import com.example.militaryaccountingapp.domain.helper.Results
import com.example.militaryaccountingapp.presenter.fragment.BaseViewModelFragment
import com.example.militaryaccountingapp.presenter.fragment.details.DetailsItemViewModel.ViewData
import com.example.militaryaccountingapp.presenter.fragment.edit.ModalBottomSheetShare
import com.example.militaryaccountingapp.presenter.model.LastChangedUi
import com.example.militaryaccountingapp.presenter.model.UserSearchUi
import com.example.militaryaccountingapp.presenter.shared.adapter.BarCodeAdapter
import com.example.militaryaccountingapp.presenter.shared.adapter.UsersSearchAdapter
import com.example.militaryaccountingapp.presenter.shared.chart.history.ChartData
import com.example.militaryaccountingapp.presenter.shared.chart.history.DayData
import com.example.militaryaccountingapp.presenter.shared.chart.history.HistoryChart
import com.example.militaryaccountingapp.presenter.shared.chart.history.MonthData
import com.example.militaryaccountingapp.presenter.shared.chart.history.WeekData
import com.example.militaryaccountingapp.presenter.utils.common.ext.asFormattedDateString
import com.example.militaryaccountingapp.presenter.utils.image.CarouselHelper
import com.github.mikephil.charting.charts.Chart
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsItemFragment :
    BaseViewModelFragment<FragmentItemBinding, ViewData, DetailsItemViewModel>() {

    override val viewModel: DetailsItemViewModel by viewModels()
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentItemBinding
        get() = FragmentItemBinding::inflate

    override fun initializeView() {
        sendArgumentsToViewModel()
        setupImages()
        setupCodes()
        setupActionBar()
        setupHistorySpinner()
        setupUsers()
        setupMoreButtons()
        setupShareFragment()
    }

    private fun setupMoreButtons() {
        binding.lastChangedBtnAll.setOnClickListener {
            findNavController().navigate(DetailsCategoryFragmentDirections.actionCategoryFragmentToNavigationHistory())
        }
    }


    private val usersAdapter by lazy {
        UsersSearchAdapter { user ->
            viewModel.loadUserPermission(userId = user.id) { permission, id ->
                log.d("permission is laoded!! id: $id  | $permission")
                if (permission is Results.Success) {
                    val nav =
                        DetailsItemFragmentDirections.actionItemFragmentToModalBottomSheetShare(
                            userId = user.id,
                            permission = permission.data,
                            itemId = id
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


    private fun setupActionBar() {
        with(requireActivity() as AppCompatActivity) {
//            setSupportActionBar(binding.toolbar)
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
        val item = viewModel.data.value.item
        if (item !is Results.Success) return
        findNavController().navigate(
            DetailsItemFragmentDirections.actionItemFragmentToAddFragment(
                elementId = item.data.id,
                elementType = Data.Type.ITEM,
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
            .setMessage(getString(R.string.add_item_alert_message))
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
                count = getInt("count")
            )
        }
    }

    override suspend fun observeCustomData() {
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

    override fun render(data: ViewData) {
        log.d("render in details ITEMs fragment: codes: ${data.codes} users: ${data.users} lastChanged: ${data.lastChanged} isDeleted: ${data.isDeleted}")
        codesAdapter.submitList(data.codes.toList())
        renderItem(data.item)
        renderLastChanged(data.lastChanged)
        renderDeleted(data.isDeleted)
        renderUsers(data.users)
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

    private fun renderDeleted(deleted: Results<Void?>) {
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
            lastChangedLabel.visibility = View.GONE
            lastChangedBtnAll.visibility = View.GONE
            lastChangedLayout.root.visibility = View.GONE
        }
    }

    private fun renderItem(item: Results<Item>) {
        when (item) {
            is Results.Success -> {
                renderItem(item.data)
                renderSpinner(false)
            }

            is Results.Loading -> {
                renderSpinner(true)
                if (item.oldData != null) {
                    renderItem(item.oldData)
                }
            }

            is Results.Failure -> {
                renderSpinner(false)
                Toast.makeText(
                    context,
                    item.throwable.message ?: "Error while loading item",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {
                renderSpinner(false)
            }
        }
    }

    private fun renderItem(item: Item) = with(binding) {
        count.text = item.count.toString()
        toolbar.title = item.name
        description.text = item.description
        location.text = item.allParentNames.joinToString(separator = " -> ")
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
                DetailsItemFragmentDirections.actionItemFragmentToModalBottomSheetCodeDetails(
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

    private fun setupHistorySpinner() {
        binding.historySpinner.apply {
            this.adapter = ArrayAdapter.createFromResource(
                this@DetailsItemFragment.requireContext(),
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