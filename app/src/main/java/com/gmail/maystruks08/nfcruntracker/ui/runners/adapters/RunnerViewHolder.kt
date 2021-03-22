package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.databinding.ItemRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView

class RunnerViewHolder(itemView: View, private val interaction: RunnerListAdapter.Interaction?) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemRunnerBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bindHolder(runner: RunnerView) {
        if (!runner.placeholder) {
            with(binding) {
                tvRunnerNumber.text = "#${runner.number}"
                tvRunnerFullName.text = if (runner.fullName.length > 26) runner.fullName.take(25) + ".." else runner.fullName
                runnerProgress.visibility = View.VISIBLE
                runnerProgress.setStepBean(runner.progress.map { it.bean })
            }
            when {
                runner.isOffTrack -> smallCard(R.color.colorCardOffTrack)
                runner.result != null -> bigCard(runner.result)
                else -> smallCard(R.color.colorCardInProgress)
            }
        } else {
            binding.runnerProgress.visibility = View.GONE
            binding.runnerProgress.setStepBean(listOf())
        }
        itemView.setOnClickListener {
            interaction?.onItemSelected(runner)
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