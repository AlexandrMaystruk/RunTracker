package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.toPx

abstract class SwipeActionHelper(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_trash)
    private val deleteBg: Drawable

    private val inCafeIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_settings)
    private val inCafeBg: Drawable

    private var prevDX = -1f

    init {
        deleteBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(3f)
            setColor(ContextCompat.getColor(context, R.color.colorRed))
        }
        inCafeBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(3f)
            setColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }

    override fun onMove(
        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        when {
            //Swipe left (Delete)
            dX < 0 -> {
                deleteIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    deleteBg.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteBg.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
            //Swipe right (In cafe)
            dX > 0 -> {
                inCafeIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    inCafeBg.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.right + dX.toInt(),
                        itemView.bottom
                    )
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + it.intrinsicWidth + iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    inCafeBg.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}