package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.item_runner.view.*

class RunnerViewHolder(itemView: View, private val interaction: RunnerListAdapter.Interaction?) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindHolder(runner: RunnerView) {
            if (!runner.placeholder) {
                itemView.tvRunnerNumber.text = "#${runner.number}"
                itemView.tvRunnerFullName.text = if (runner.fullName.length > 26) runner.fullName.take(25) + ".." else runner.fullName
                itemView.runnerProgress.visibility = View.VISIBLE
                itemView.runnerProgress.setStepBean(runner.progress.map { it.bean })
                when {
                    runner.isOffTrack -> smallCard(R.color.colorCardOffTrack)
                    runner.result != null -> bigCard(runner.result)
                    else -> smallCard(R.color.colorCardInProgress)
                }
            } else {
                itemView.runnerProgress.visibility = View.GONE
            }
            itemView.setOnClickListener {
                interaction?.onItemSelected(runner)
            }
        }

        private fun smallCard(color: Int) {
            val layoutParams = itemView.root_item.layoutParams.apply {
                height = itemView.resources.getDimensionPixelSize(R.dimen.runnerItemSmallHeight)
            }
            itemView.item_constraint_layout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    color
                )
            )
            itemView.root_item.layoutParams = layoutParams
            itemView.tvRunnerResult.text = null
            itemView.tvRunnerResult.gone()
        }

        private fun bigCard(result: String) {
            val resultStr = itemView.resources.getString(R.string.total_time, result)
            val layoutParams = itemView.root_item.layoutParams.apply {
                height = itemView.resources.getDimensionPixelSize(R.dimen.runnerItemBigHeight)
            }
            itemView.root_item.layoutParams = layoutParams
            itemView.item_constraint_layout.setBackgroundColor(
                ContextCompat.getColor(
                    itemView.context,
                    R.color.colorCardFinisher
                )
            )
            itemView.tvRunnerResult.text = resultStr
            itemView.tvRunnerResult.show()
        }
 }