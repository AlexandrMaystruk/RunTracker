package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.databinding.ItemRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.Interaction
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.RunnerView

class RunnerViewHolder(
    itemView: View,
    interaction: Interaction?
) : BaseViewHolder<RunnerView>(itemView, interaction) {

    private val binding = ItemRunnerBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    override fun bind(item: RunnerView) {
        isSwipeEnable = !item.isOffTrack
        with(binding) {
            tvRunnerNumber.text = "#${item.number}"
            tvRunnerFullName.text = if (item.fullName.length > 26) item.fullName.take(25) + ".." else item.fullName
            runnerProgress.visibility = View.VISIBLE
            runnerProgress.setStepBean(item.progress.map { it.bean })
        }
        when {
            item.isOffTrack -> smallCard(R.color.colorCardOffTrack)
            item.result != null -> bigCard(item.result)
            else -> smallCard(R.color.colorCardInProgress)
        }
        itemView.setOnClickListener {
            interaction?.onItemSelected(item)
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