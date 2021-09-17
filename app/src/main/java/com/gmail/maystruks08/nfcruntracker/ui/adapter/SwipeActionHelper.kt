package com.gmail.maystruks08.nfcruntracker.ui.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.core.ext.toPx

abstract class SwipeActionHelper(
    context: Context,
    private val rightIconDrawableId: Int?,
    private val rightIconBgId: Int?,
    private val leftIconDrawableId: Int?,
    private val leftIconBgId: Int?,
    private val swipeDirs:Int
) : ItemTouchHelper.SimpleCallback(0, swipeDirs) {

    private val rightIcon: Drawable? = rightIconDrawableId?.let { ContextCompat.getDrawable(context, it) }
    private var rightIconBg: Drawable? = null

    private val leftIcon: Drawable? = leftIconDrawableId?.let { ContextCompat.getDrawable(context, it) }
    private var leftIconBg: Drawable? = null

    private var prevDX = -1f

    init {
        rightIconBgId?.let {
            rightIconBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = context.resources.displayMetrics.toPx(8f)
                setColor(ContextCompat.getColor(context, rightIconBgId))
            }
        }

        leftIconBgId?.let {
            leftIconBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = context.resources.displayMetrics.toPx(8f)
                setColor(ContextCompat.getColor(context, leftIconBgId))
            }
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
            //Swipe left (Off track)
            dX < 0 -> {
                rightIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    rightIconBg?.setBounds(
                        itemView.right + dX.toInt(),
                        itemView.top,
                        itemView.right,
                        itemView.bottom
                    )
                    val iconRight = itemView.right - iconMargin
                    val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    rightIconBg?.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
            //Swipe right (Add current checkpoint)
            dX > 0 -> {
                leftIcon?.let {
                    val iconTop = itemView.top + (itemHeight - it.intrinsicHeight) / 2
                    val iconMargin = (itemHeight - it.intrinsicHeight) / 3
                    val iconBottom = iconTop + it.intrinsicHeight
                    leftIconBg?.setBounds(
                        itemView.left,
                        itemView.top,
                        itemView.left + dX.toInt(),
                        itemView.bottom
                    )
                    val iconLeft = itemView.left + iconMargin
                    val iconRight = itemView.left + it.intrinsicWidth + iconMargin
                    it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    leftIconBg?.draw(c)
                    it.draw(c)
                    prevDX = dX
                }
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}

