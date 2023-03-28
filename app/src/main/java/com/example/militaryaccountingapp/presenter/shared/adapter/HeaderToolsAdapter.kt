package com.example.militaryaccountingapp.presenter.shared.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.FragmentHomeHeaderSortBinding
import com.example.militaryaccountingapp.presenter.shared.adapter.HeaderToolsAdapter.ViewHolder
import com.example.militaryaccountingapp.presenter.utils.common.constant.SortConstant
import com.example.militaryaccountingapp.presenter.utils.ui.ext.visibleIf

class HeaderToolsAdapter(
    private var sortOption: SortConstant = SortConstant.NEWEST,
    private var onClickViewType: () -> Unit,
    private var onNewSortOptionSelected: ((SortConstant) -> Unit)? = null,
) : RecyclerView.Adapter<ViewHolder>() {

    init {
        setHasStableIds(true)
    }

    @DrawableRes
    private var iconViewType: Int = R.drawable.ic_baseline_view_grid_24dp

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
            iconViewType,
            onClickViewType,
            sortOption,
            onNewSortOptionSelected
        )
    }

    fun changeViewTypeIcon(@DrawableRes icon: Int) {
        if (iconViewType == icon) return
        iconViewType = icon
        notifyItemChanged(0)
    }

    class ViewHolder(
        private val binding: FragmentHomeHeaderSortBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            iconViewType: Int,
            onClickViewType: () -> Unit,
            sortOption: SortConstant,
            onNewSortOptionSelected: ((SortConstant) -> Unit)?
        ) {
            with(binding) {
                viewTypeButton.setOnClickListener { onClickViewType() }
                viewTypeButton.icon = ContextCompat.getDrawable(root.context, iconViewType)
                sortOptionText.text = sortOption.getDisplayName(root.context)
                sortOptionText.setOnClickListener {
                    showPopup(root, root.context, onNewSortOptionSelected ?: {})
                }
            }
        }

        private fun showPopup(
            view: View,
            context: Context,
            onNewSortOptionSelected: (SortConstant) -> Unit
        ) = PopupMenu(context, view).apply {
            setOnMenuItemClickListener { item ->
                item?.itemId?.let {
                    onNewSortOptionSelected(
                        SortConstant.fromResId(it) ?: SortConstant.NEWEST
                    )
                }
                true
            }
            inflate(R.menu.sort_options_menu)
            show()
        }

    }
}