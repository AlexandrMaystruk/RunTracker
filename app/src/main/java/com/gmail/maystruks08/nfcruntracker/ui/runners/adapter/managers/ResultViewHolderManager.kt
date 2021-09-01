package com.gmail.maystruks08.nfcruntracker.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemResultBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapter.views.RunnerResultView

class ResultViewHolderManager : ViewHolderManager<ItemResultBinding, RunnerResultView> {

    override fun isRelativeItem(item: Item) = item is RunnerResultView

    override fun getLayoutId() = R.layout.item_result

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemResultBinding, RunnerResultView> {
        val binding = ItemResultBinding.inflate(layoutInflater, parent, false)
        return ResultViewHolder(binding)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<RunnerResultView>() {
        override fun areItemsTheSame(oldItem: RunnerResultView, newItem: RunnerResultView) =
            oldItem.runnerNumber == newItem.runnerNumber

        override fun areContentsTheSame(oldItem: RunnerResultView, newItem: RunnerResultView) =
            oldItem == newItem
    }

}


class ResultViewHolder(
    binding: ItemResultBinding
) : BaseViewHolder<ItemResultBinding, RunnerResultView>(binding) {


    override fun onBind(item: RunnerResultView) = with(binding) {
        super.onBind(item)
        val runnerNumberText = "#${item.runnerNumber}"
        with(binding) {
            tvRunnerPosition.text = item.position.toString()
            tvRunnerFullName.text = item.runnerFullName
            tvRunnerNumber.text = runnerNumberText
            tvRunnerResultTime.text = item.runnerResultTime
        }
    }
}