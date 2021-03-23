package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.Interaction
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.BaseRunnerView

abstract class BaseViewHolder<T : BaseRunnerView>(
    itemView: View,
    protected val interaction: Interaction?
) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(item: T)

}