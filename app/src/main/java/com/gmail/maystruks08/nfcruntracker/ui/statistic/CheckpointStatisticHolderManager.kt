package com.gmail.maystruks08.nfcruntracker.ui.statistic

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemCheckpointStatisticBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointStatisticView

class CheckpointStatisticHolderManager : ViewHolderManager<ItemCheckpointStatisticBinding, CheckpointStatisticView> {

    override fun isRelativeItem(item: Item) = item is CheckpointStatisticView

    override fun getLayoutId() = R.layout.item_checkpoint_statistic

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemCheckpointStatisticBinding, CheckpointStatisticView> {
        val binding = ItemCheckpointStatisticBinding.inflate(layoutInflater, parent, false)
        return CheckpointStatisticViewHolder(binding)
    }

    override fun getDiffUtil() = object : DiffUtil.ItemCallback<CheckpointStatisticView>() {
        override fun areItemsTheSame(oldItem: CheckpointStatisticView, newItem: CheckpointStatisticView) = oldItem.title == newItem.title
        override fun areContentsTheSame(oldItem: CheckpointStatisticView, newItem: CheckpointStatisticView) = oldItem == newItem
    }
}


class CheckpointStatisticViewHolder(
    binding: ItemCheckpointStatisticBinding
) : BaseViewHolder<ItemCheckpointStatisticBinding, CheckpointStatisticView>(binding) {

    override fun onBind(item: CheckpointStatisticView) = with(binding) {
        super.onBind(item)
        tvCheckpointName.text = item.title
        tvInProgressLabel.text = item.runnerCountInProgress
        tvFinishersLabel.text = item.runnerCountWhoVisitCheckpoint
    }
}