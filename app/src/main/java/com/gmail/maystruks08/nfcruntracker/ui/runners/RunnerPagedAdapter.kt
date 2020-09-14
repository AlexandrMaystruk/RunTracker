package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.item_runner.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RunnerPagedAdapter(private val clickListener: (RunnerView) -> Unit) :
    PagedListAdapter<RunnerView, ViewHolder>(DIFF_CALLBACK) {

    val runnerList: MutableList<RunnerView> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_runner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(getItem(position), clickListener)
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    @SuppressLint("SetTextI18n")
    fun bindHolder(runner: RunnerView?, clickListener: (RunnerView) -> Unit) {
        if (runner == null) return
        itemView.tvRunnerNumber.text = "#${runner.number}"
        itemView.tvRunnerFullName.text = runner.fullName
        itemView.runnerProgress.setStepBean(runner.progress.map { it.bean })
        when {
            runner.isOffTrack -> smallCard(R.color.colorCardOffTrack)
            runner.result != null -> bigCard(runner.result)
            else -> smallCard(R.color.colorCardInProgress)
        }
        itemView.setOnClickListener { clickListener(runner) }
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

private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RunnerView>() {

    override fun areItemsTheSame(oldRunnerView: RunnerView, newRunnerView: RunnerView): Boolean =
        oldRunnerView.id == newRunnerView.id

    override fun areContentsTheSame(oldItem: RunnerView, newItem: RunnerView): Boolean =
        oldItem == newItem

}