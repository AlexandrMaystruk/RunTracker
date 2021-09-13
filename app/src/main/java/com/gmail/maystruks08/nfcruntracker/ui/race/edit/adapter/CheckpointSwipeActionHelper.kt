package com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.color
import com.gmail.maystruks08.nfcruntracker.core.ext.drawable
import com.gmail.maystruks08.nfcruntracker.core.ext.toPx

abstract class CheckpointSwipeActionHelper(context: Context) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon = context.drawable(R.drawable.ic_trash)
    private val deleteIconBackground: Drawable

    private val editIcon = context.drawable(R.drawable.ic_settings)
    private val editIconBackground: Drawable

    private var prevDX = -1f

    init {
        deleteIconBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(3f)
            setColor(context.color(R.color.colorRed))
        }
        editIconBackground = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = context.resources.displayMetrics.toPx(3f)
            setColor(context.color(R.color.colorPrimary))
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
        if (viewHolder !is EditCheckpointViewHolder) return 0
        return super.getSwipeDirs(
            recyclerView,
            viewHolder
        )
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        when {
            dX < 0 -> {
                (viewHolder as? EditCheckpointViewHolder)?.let { editCheckpointViewHolder ->
                    val itemView = viewHolder.itemView
                    val itemHeight = itemView.bottom - itemView.top
                    if (editCheckpointViewHolder.isEditMode) {
                        deleteIcon?.let {
                            val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                            val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                            val iconBottom = iconTop + it.intrinsicHeight
                            deleteIconBackground.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            val iconRight = itemView.right - iconMargin
                            val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            deleteIconBackground.draw(c)
                            it.draw(c)
                            prevDX = dX
                        }
                    } else {
                        editIcon?.let {
                            val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                            val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                            val iconBottom = iconTop + it.intrinsicHeight
                            editIconBackground.setBounds(
                                itemView.right + dX.toInt(),
                                itemView.top,
                                itemView.right,
                                itemView.bottom
                            )
                            val iconRight = itemView.right - iconMargin
                            val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                            it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                            editIconBackground.draw(c)
                            it.draw(c)
                            prevDX = dX
                        }
                    }
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}