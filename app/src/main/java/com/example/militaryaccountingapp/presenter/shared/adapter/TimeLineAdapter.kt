package com.example.militaryaccountingapp.presenter.shared.adapter

import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.databinding.ItemTimelineBinding
import com.example.militaryaccountingapp.domain.entity.data.ActionType
import com.example.militaryaccountingapp.presenter.model.TimelineUi
import com.example.militaryaccountingapp.presenter.utils.ui.ext.load
import com.lriccardo.timelineview.TimelineAdapter
import com.lriccardo.timelineview.TimelineView

class TimeLineAdapter(
    private val onClickListener: (TimelineUi, View) -> Unit
) : ListAdapter<TimelineUi, TimeLineAdapter.TimeLineViewHolder>(TimelineDiffCallback),
    TimelineAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTimelineBinding.inflate(layoutInflater, parent, false)
        return TimeLineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onClickListener)
    }


    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

//    override fun getItemId(position: Int) = getItem(position).hashCode().toLong() + 1000L

    override fun getIndicatorStyle(position: Int): TimelineView.IndicatorStyle {
        return if (position < 1)
            TimelineView.IndicatorStyle.Checked
        else TimelineView.IndicatorStyle.Empty
    }

    override fun getLineStyle(position: Int): TimelineView.LineStyle {
        return if (position < 2)
            TimelineView.LineStyle.Dashed
        else TimelineView.LineStyle.Normal
//        return super.getLineStyle(position)
    }

    override fun getLinePadding(position: Int): Float? {
        if (position > 0)
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                12f,
                Resources.getSystem().displayMetrics
            )
        return super.getLinePadding(position)
    }

    class TimeLineViewHolder(private val binding: ItemTimelineBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(timeline: TimelineUi, onClickListener: (TimelineUi, View) -> Unit) = with(binding) {
            this.value.text = timeline.value
            this.title.text = timeline.title
            this.date.text = timeline.date
            setOperationChip(timeline.operation)
            this.location.text = timeline.location
            this.name.text = timeline.name

            root.setOnClickListener {
                onClickListener(timeline, it)
            }

            this.userIcon.load(timeline.userIcon ?: return@with)
//            this.userIcon.setImageDrawable(timeline.userIcon)
        }

        private fun setOperationChip(operation: ActionType) {
            val (iconId, titleId) = when (operation) {
                ActionType.INCREASE_COUNT -> R.drawable.ic_add_24dp to R.string.history_chips_add
                ActionType.DECREASE_COUNT -> R.drawable.ic_remove_24dp to R.string.history_chips_dec

                ActionType.SHARE -> R.drawable.ic_share_24dp to R.string.history_chips_share
                ActionType.UNSHARE -> R.drawable.ic_unshare_24dp to R.string.history_chips_unshare

                ActionType.CREATE -> R.drawable.ic_outline_edit_24dp to R.string.history_chips_create
                ActionType.DELETE -> R.drawable.ic_delete_24dp to R.string.history_chips_delete
                ActionType.RESTORE -> R.drawable.ic_restore_24dp to R.string.history_chips_restore
                ActionType.UPDATE -> R.drawable.ic_outline_edit_24dp to R.string.history_chips_update
            }

            binding.operationType.apply {
                text = binding.root.context.getString(titleId)
                chipIcon = ContextCompat.getDrawable(binding.root.context, iconId)
            }
        }
    }

    companion object {
        const val VIEW_TYPE = 1005
    }
}