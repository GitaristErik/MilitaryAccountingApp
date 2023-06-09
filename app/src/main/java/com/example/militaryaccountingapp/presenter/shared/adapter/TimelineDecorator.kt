package com.example.militaryaccountingapp.presenter.shared.adapter

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lriccardo.timelineview.TimelineView

class TimelineDecorator(
    private val indicatorStyle: TimelineView.IndicatorStyle = TimelineView.IndicatorStyle.Filled,
    private val indicatorDrawable: Drawable? = null,
    @DrawableRes val indicatorDrawableRes: Int? = null,
    private val indicatorSize: Float = 12.toPx().toFloat(),
    private val indicatorYPosition: Float = 0.5f,
    private val checkedIndicatorSize: Float? = null,
    private val checkedIndicatorStrokeWidth: Float = 4.toPx().toFloat(),
    private val lineStyle: TimelineView.LineStyle? = null,
    private val linePadding: Float? = null,
    private val lineDashLength: Float? = null,
    private val lineDashGap: Float? = null,
    private val lineWidth: Float? = null,
    val padding: Float = 16.toPx().toFloat(),
    val position: Position = Position.Left,
    @ColorInt val indicatorColor: Int? = null,
    @ColorInt val lineColor: Int? = null
) : RecyclerView.ItemDecoration() {

    val width = ((indicatorSize * 2) + (padding * 2))

    enum class Position {
        Left,
        Right
    }

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        val size = when (indicatorStyle) {
            TimelineView.IndicatorStyle.Filled -> width
            TimelineView.IndicatorStyle.Empty -> width + checkedIndicatorStrokeWidth
            TimelineView.IndicatorStyle.Checked -> width + checkedIndicatorStrokeWidth
        }.toInt()

        when (position) {
            Position.Left ->
                rect.left = size

            Position.Right ->
                rect.right = size
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        c.save()
        c.clipRect(
            parent.paddingLeft,
            parent.paddingTop,
            parent.width,
            parent.height - parent.paddingBottom
        )

        val adapter = if (parent.adapter is ConcatAdapter) {
            (parent.adapter as ConcatAdapter).adapters.firstOrNull { it is TimeLineAdapter } as? TimeLineAdapter
        } else {
            (parent.adapter as? TimeLineAdapter)
        }
        drawTimeline(c, parent, adapter ?: return)
        c.restore()
    }

    private fun drawTimeline(c: Canvas, parent: RecyclerView, adapter: TimeLineAdapter) {
        parent.children.forEach { view ->
            val itemPosition = parent.getChildAdapterPosition(view)
            if (itemPosition == RecyclerView.NO_POSITION) return@forEach

            // if the view type is not timeline then return
            if (adapter.getItemViewType(itemPosition) != TimeLineAdapter.VIEW_TYPE) return@forEach

            val timelineView = TimelineView(context = parent.context)

            adapter.getTimelineViewType(itemPosition)?.let {
                timelineView.viewType = it
            } ?: timelineView.setType(
                itemPosition,
                adapter.itemCount ?: -1
            )

            (adapter.getIndicatorDrawable(itemPosition) ?: indicatorDrawable)?.let {
                timelineView.indicatorDrawable = it
            } ?: (adapter.getIndicatorDrawableRes(itemPosition) ?: indicatorDrawableRes)?.let {
                timelineView.indicatorDrawable = ContextCompat.getDrawable(parent.context, it)
            }

            (adapter.getIndicatorColor(itemPosition) ?: indicatorColor)?.let {
                timelineView.indicatorColor = it
            }

            (adapter.getLineColor(itemPosition) ?: lineColor)?.let {
                timelineView.lineColor = it
            }

            (adapter.getIndicatorStyle(itemPosition) ?: indicatorStyle).let {
                timelineView.indicatorStyle = it
            }

            (adapter.getLineStyle(itemPosition) ?: lineStyle)?.let {
                timelineView.lineStyle = it
            }

            (adapter.getLinePadding(itemPosition) ?: linePadding)?.let {
                timelineView.linePadding = it
            }

            timelineView.indicatorSize = indicatorSize

            timelineView.indicatorYPosition = indicatorYPosition

            checkedIndicatorSize?.let {
                timelineView.checkedIndicatorSize = it
            }

            checkedIndicatorStrokeWidth.let {
                timelineView.checkedIndicatorStrokeWidth = it
            }

            lineDashLength?.let {
                timelineView.lineDashLength = it
            }

            lineDashGap?.let {
                timelineView.lineDashGap = it
            }

            lineWidth?.let {
                timelineView.lineWidth = it
            }

            timelineView.measure(
                View.MeasureSpec.getSize(width.toInt()),
                View.MeasureSpec.getSize(view.measuredHeight)
            )

            c.save()
            when (position) {
                Position.Left -> {
                    c.translate(padding + parent.paddingLeft, view.top.toFloat())
                    timelineView.layout(0, 0, timelineView.measuredWidth, view.measuredHeight)
                }

                Position.Right -> {
                    c.translate(
                        view.measuredWidth + padding + parent.paddingLeft,
                        view.top.toFloat()
                    )
                    timelineView.layout(0, 0, timelineView.measuredWidth, view.measuredHeight)
                }
            }
            timelineView.draw(c)
            c.restore()
        }
    }
}

internal fun Number.toPx() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    this.toFloat(),
    Resources.getSystem().displayMetrics
).toInt()