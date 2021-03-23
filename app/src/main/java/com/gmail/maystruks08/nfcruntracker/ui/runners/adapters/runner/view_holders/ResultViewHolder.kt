package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders

import android.view.View
import com.gmail.maystruks08.nfcruntracker.databinding.ItemResultBinding
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.RunnerResultView

class ResultViewHolder(
    itemView: View,
) : BaseViewHolder<RunnerResultView>(itemView, null) {

    private val binding = ItemResultBinding.bind(itemView)

    override fun bind(item: RunnerResultView) {
        val runnerNumberText = "#${item.runnerNumber}"
        with(binding) {
            tvRunnerPosition.text = item.position.toString()
            tvRunnerFullName.text = item.runnerFullName
            tvRunnerNumber.text = runnerNumberText
            tvRunnerResultTime.text = item.runnerResultTime
        }
    }

}