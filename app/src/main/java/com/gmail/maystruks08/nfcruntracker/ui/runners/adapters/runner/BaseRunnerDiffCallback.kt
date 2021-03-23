package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner

import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.BaseRunnerView

object BaseRunnerDiffCallback : DiffUtil.ItemCallback<BaseRunnerView?>() {

    override fun areItemsTheSame(oldRunner: BaseRunnerView, newRunner: BaseRunnerView): Boolean {
        return oldRunner.type == newRunner.type && oldRunner.isItemTheSame(newRunner)
    }

    override fun areContentsTheSame(oldRunner: BaseRunnerView, newRunner: BaseRunnerView): Boolean {
        return oldRunner.isContentTheSame(newRunner)
    }
}