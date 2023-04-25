package com.example.militaryaccountingapp.presenter.shared.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeHeaderSortBinding
import com.example.militaryaccountingapp.presenter.shared.adapter.HeaderToolsAdapter.ViewHolder
import com.example.militaryaccountingapp.presenter.utils.common.constant.OrderBy
import com.example.militaryaccountingapp.presenter.utils.common.constant.SortType
import com.example.militaryaccountingapp.presenter.utils.common.constant.ViewType

class HeaderToolsAdapter(
    private var sortType: SortType = SortType.NAME,
    private var orderBy: OrderBy = OrderBy.DESCENDING,
    private val onChangeViewType: () -> Unit,
    private val onChangeOrderBy: () -> Unit,
    private val onNewSortOptionSelected: ((SortType) -> Unit)? = null,
) : RecyclerView.Adapter<ViewHolder>() {

    private var viewType: ViewType = ViewType.GRID

    private var spaceTop: Int = 0

    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = 1

    override fun getItemId(position: Int) = 1L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentHomeHeaderSortBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            spaceTop,
            viewType,
            orderBy,
            sortType,
            onChangeViewType,
            onChangeOrderBy
        ) {
            sortType = it
            onNewSortOptionSelected?.invoke(it)
            notifyItemChanged(0)
        }
    }

    fun setViewType(viewType: ViewType) {
        if (this.viewType == viewType) return
        this.viewType = viewType
        notifyItemChanged(0)
    }

    fun setOrderBy(orderBy: OrderBy) {
        if (this.orderBy == orderBy) return
        this.orderBy = orderBy
        notifyItemChanged(0)
    }

    fun setSortType(sortType: SortType) {
        if (this.sortType == sortType) return
        this.sortType = sortType
        notifyItemChanged(0)
    }

    fun setSpaceTop(size: Int) {
        if (spaceTop == size) return
        spaceTop = size
        notifyItemChanged(0)
    }

    fun updatingSpaceTopBasedOnView(rootView: View, calculatedSpace: () -> Int?) {
        var calculatedSpaceValue = calculatedSpace() ?: 0
        if (calculatedSpaceValue == 0) {
            rootView.post {
                calculatedSpaceValue = calculatedSpace() ?: 0
                setSpaceTop(calculatedSpaceValue)
            }
        } else {
            setSpaceTop(calculatedSpaceValue)
        }
    }

    class ViewHolder(
        private val binding: FragmentHomeHeaderSortBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val originalSpaceTop = binding.root.marginTop

        fun bind(
            spaceTop: Int = 0,
            viewType: ViewType,
            orderBy: OrderBy,
            sortType: SortType,
            onChangeViewType: () -> Unit,
            onChangeOrderBy: () -> Unit,
            onNewSortOptionSelected: (SortType) -> Unit
        ) {
            with(binding) {
                if (spaceTop > 0) addSpaceTop(spaceTop)

                btnViewType.setOnClickListener { onChangeViewType() }
                btnViewType.icon = viewType.getIcon(root.context)

                btnOrderBy.setOnClickListener { onChangeOrderBy() }
                btnOrderBy.icon = orderBy.getIcon(root.context)
                btnOrderBy.rotationX = if (orderBy == OrderBy.ASCENDING) 180f else 0f

                sortOptionText.text = sortType.getDisplayName(root.context)
                sortOptionText.setOnClickListener {
                    showPopup(root, root.context, onNewSortOptionSelected)
                }
            }
        }

        private fun addSpaceTop(spaceTop: Int) {
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = originalSpaceTop + spaceTop
            }
        }

        private fun showPopup(
            view: View,
            context: Context,
            onNewSortOptionSelected: (SortType) -> Unit
        ) = PopupMenu(context, view).run {
            setOnMenuItemClickListener { item ->
                item?.itemId?.let {
                    val sortType = SortType.fromResId(it) ?: SortType.NAME
                    with(binding) { sortOptionText.text = sortType.getDisplayName(root.context) }
                    onNewSortOptionSelected(sortType)
                }
                true
            }
            inflate(R.menu.sort_options_menu)
            show()
        }
    }
}
