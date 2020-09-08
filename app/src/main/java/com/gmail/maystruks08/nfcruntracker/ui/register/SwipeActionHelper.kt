package com.gmail.maystruks08.nfcruntracker.ui.register

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.toPx


abstract class SwipeActionHelper(context: Context)
    : ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT) {

    private val deleteIcon: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_trash)
    private val deleteBg: Drawable

    private var prevDX = -1f

    init {
        deleteBg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(3f)
            setColor(ContextCompat.getColor(context, R.color.colorRed))
        }
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        when {
            dX < 0 -> {
                if (!(recyclerView.adapter as RegisterRunnerAdapter).canSwipe()) return
                deleteIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    deleteBg.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteBg.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}