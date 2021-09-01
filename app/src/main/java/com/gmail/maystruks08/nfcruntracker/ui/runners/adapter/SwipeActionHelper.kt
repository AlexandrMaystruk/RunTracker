package com.gmail.maystruks08.nfcruntracker.ui.runners.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.toPx
import com.gmail.maystruks08.nfcruntracker.ui.adapter.RunnerViewHolder

abstract class SwipeActionHelper(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val offTrackIcon: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.ic_remove_circle)
    private val offTrackBg: Drawable

    private val markAtCurrentIcon: Drawable? =
        ContextCompat.getDrawable(context, R.drawable.ic_check)
    private val markAtCurrentBg: Drawable

    private var prevDX = -1f

    init {
        offTrackBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(8f)
            setColor(ContextCompat.getColor(context, R.color.colorRed))
        }
        markAtCurrentBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(8f)
            setColor(ContextCompat.getColor(context, R.color.colorGreen))
        }
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun getSwipeDirs(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if ((viewHolder as? RunnerViewHolder)?.isSwipeEnable == true) return super.getSwipeDirs(
            recyclerView,
            viewHolder
        )
        return 0
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        if ((viewHolder as? RunnerViewHolder)?.isSwipeEnable != true) return

        when {
            //Swipe left (Off track)
            dX < 0 -> {
                offTrackIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    offTrackBg.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    offTrackBg.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
            //Swipe right (Add current checkpoint)
            dX > 0 -> {
                markAtCurrentIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    markAtCurrentBg.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.right + dX.toInt(),
                        itemView.bottom
                    )
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + it.intrinsicWidth + iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    markAtCurrentBg.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}