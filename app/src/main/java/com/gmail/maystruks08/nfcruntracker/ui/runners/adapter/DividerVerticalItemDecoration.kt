package com.gmail.maystruks08.nfcruntracker.ui.runners.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class DividerVerticalItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        rect: Rect,
        view: View,
        parent: RecyclerView,
        s: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            rect.top = when (parent.getChildAdapterPosition(view)) {
                0 -> spacing
                else -> 0
            }
            rect.bottom = when (parent.getChildAdapterPosition(view)) {
                adapter.itemCount -> spacing
                else -> spacing
            }
        }
    }
}