package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView

object RunnerDiffCallback : DiffUtil.ItemCallback<RunnerView?>() {

    override fun areItemsTheSame(oldRunner: RunnerView,newRunner: RunnerView): Boolean {
        return oldRunner.number == newRunner.number
    }

    override fun areContentsTheSame(oldRunner: RunnerView, newRunner: RunnerView): Boolean {
        return oldRunner.number == newRunner.number
    }
}