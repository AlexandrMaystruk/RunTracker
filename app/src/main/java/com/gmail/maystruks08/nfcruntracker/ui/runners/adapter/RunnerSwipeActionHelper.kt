package com.gmail.maystruks08.nfcruntracker.ui.runners.adapter

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.adapter.RunnerViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.SwipeActionHelper

abstract class RunnerSwipeActionHelper(
    context: Context,
) : SwipeActionHelper(
    context,
    R.drawable.ic_remove_circle,
    R.color.colorRed,
    R.drawable.ic_check,
    R.color.colorGreen,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

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
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if ((viewHolder as? RunnerViewHolder)?.isSwipeEnable != true) return
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

}