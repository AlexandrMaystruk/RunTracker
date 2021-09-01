package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.domain.toDateTimeShortFormat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.hide
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.databinding.ItemCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointPosition
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.views.StepState

class RunnerCheckpointsViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemCheckpointBinding, CheckpointView> {

    override fun isRelativeItem(item: Item) = item is CheckpointView

    override fun getLayoutId() = R.layout.item_checkpoint

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemCheckpointBinding, CheckpointView> {
        val binding = ItemCheckpointBinding.inflate(layoutInflater, parent, false)
        return RunnerCheckpointViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<CheckpointView>() {
        override fun areItemsTheSame(oldItem: CheckpointView, newItem: CheckpointView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CheckpointView, newItem: CheckpointView) =
            oldItem == newItem
    }

    interface Interaction {
        fun onLongCLickAtCheckpointDate(item: CheckpointView)
    }

}


class RunnerCheckpointViewHolder(
    binding: ItemCheckpointBinding,
    private val interaction: RunnerCheckpointsViewHolderManager.Interaction
) : BaseViewHolder<ItemCheckpointBinding, CheckpointView>(binding) {

    override fun onBind(item: CheckpointView) = with(binding) {
        super.onBind(item)
        with(binding) {
            val stateDrawable = when (item.bean.state) {
                StepState.UNDONE -> R.drawable.ic_unchecked
                StepState.DONE -> R.drawable.ic_check_circle
                StepState.CURRENT -> R.drawable.ic_checked
                StepState.DONE_WARNING -> R.drawable.ic_check_warning
            }
            if (item.bean.state == StepState.CURRENT) ivRunner.show() else ivRunner.hide()
            when (item.position) {
                CheckpointPosition.Start -> {
                    topView.gone()
                    bottomView.show()
                    tvCheckpointName.text = null
                    tvCheckpointName.background =
                        ContextCompat.getDrawable(root.context, R.drawable.ic_start)
                }
                CheckpointPosition.End -> {
                    tvCheckpointName.text = null
                    tvCheckpointName.background =
                        ContextCompat.getDrawable(root.context, R.drawable.ic_finish)
                    topView.show()
                    bottomView.gone()
                }
                else -> {
                    tvCheckpointName.text = item.bean.title
                    tvCheckpointName.background = null
                    topView.show()
                    bottomView.show()
                }
            }

            ivCheckpointState.background = ContextCompat.getDrawable(root.context, stateDrawable)
            tvCheckpointDate.paintFlags = item.titlePaintFlag

            if (item.date != null) {
                tvCheckpointDate.setOnLongClickListener {
                    interaction.onLongCLickAtCheckpointDate(item)
                    true
                }
                tvCheckpointDate.show()
                tvCheckpointDate.text = item.date?.toDateTimeShortFormat()
            } else tvCheckpointDate.gone()
        }
    }
}