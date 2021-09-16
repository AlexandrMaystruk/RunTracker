package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.databinding.ItemRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView

class RunnerViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemRunnerBinding, RunnerView> {

    override fun isRelativeItem(item: Item) = item is RunnerView

    override fun getLayoutId() = R.layout.item_runner

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemRunnerBinding, RunnerView> {
        val binding = ItemRunnerBinding.inflate(layoutInflater, parent, false)
        return RunnerViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<RunnerView>() {
        override fun areItemsTheSame(oldItem: RunnerView, newItem: RunnerView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RunnerView, newItem: RunnerView) =
            oldItem == newItem
    }

    interface Interaction {
        fun onItemSelected(item: RunnerView)
    }

}


class RunnerViewHolder(
    binding: ItemRunnerBinding,
    private val interaction: RunnerViewHolderManager.Interaction
) : BaseViewHolder<ItemRunnerBinding, RunnerView>(binding) {

    var isSwipeEnable = true

    @SuppressLint("SetTextI18n")
    override fun onBind(item: RunnerView) = with(binding) {
        super.onBind(item)
        isSwipeEnable = !item.isOffTrack
        with(binding) {
            tvRunnerNumber.text = "#${item.id}"
            tvRunnerName.text = if (item.shortName.length > 26) item.shortName.take(25) + ".." else item.shortName
            runnerProgress.visibility = View.VISIBLE
            runnerProgress.setStepBean(item.progress.map { it.bean })
        }
        when {
            item.isOffTrack -> smallCard(R.color.colorCardOffTrack)
            item.result != null -> bigCard(item.result)
            else -> smallCard(R.color.colorCardInProgress)
        }
        itemView.setOnClickListener {
            interaction.onItemSelected(item)
        }

    }

    private fun smallCard(color: Int) {
        with(binding) {
            val layoutParams = rootItem.layoutParams.apply {
                height = itemView.resources.getDimensionPixelSize(R.dimen.runnerItemSmallHeight)
            }
            itemConstraintLayout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    color
                )
            )
            rootItem.layoutParams = layoutParams
            tvRunnerResult.text = null
            tvRunnerResult.gone()
        }
    }

    private fun bigCard(result: String) {
        with(binding) {
            val resultStr = rootItem.resources.getString(R.string.total_time, result)
            val layoutParams = rootItem.layoutParams.apply {
                height = rootItem.resources.getDimensionPixelSize(R.dimen.runnerItemBigHeight)
            }
            rootItem.layoutParams = layoutParams
            itemConstraintLayout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.colorCardFinisher
                )
            )
            tvRunnerResult.text = resultStr
            tvRunnerResult.show()
        }
    }
}