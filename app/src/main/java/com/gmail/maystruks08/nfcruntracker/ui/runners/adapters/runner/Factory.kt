package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner

import android.view.View
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders.ResultViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders.RunnerViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.BaseRunnerView

class Factory {

    fun viewResourceId(type: Int): Int {
        return when (type) {
            0 -> R.layout.item_runner
            1 -> R.layout.item_result
            else -> throw RuntimeException("Illegal view type")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun holder(
        type: Int,
        view: View,
        itemClickListener: Interaction?
    ): BaseViewHolder<BaseRunnerView> {
        return when (type) {
            0 -> RunnerViewHolder(view, itemClickListener) as BaseViewHolder<BaseRunnerView>
            1 -> ResultViewHolder(view) as BaseViewHolder<BaseRunnerView>
            else -> throw RuntimeException("Illegal view type")
        }
    }
}