package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.item_runner.view.*
import kotlin.properties.Delegates

class RunnerAdapter(private val clickListener: (RunnerView) -> Unit) :
    RecyclerView.Adapter<RunnerAdapter.ViewHolder>() {

    var runnerList: MutableList<RunnerView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    fun insertItemOrUpdateIfExist(item: RunnerView) {
        val index = runnerList.indexOfFirst { item.id == it.id }
        if (index == -1) {
            runnerList.add(item)
            notifyItemInserted(runnerList.lastIndex)
        } else {
            runnerList.removeAt(index)
            runnerList.add(index, item)
            notifyItemChanged(index)
        }
    }

    fun updateItem(item: RunnerView) {
        val index = runnerList.indexOfFirst { item.id == it.id }
        if (index == -1) return
        runnerList.removeAt(index)
        runnerList.add(index, item)
        notifyItemChanged(index)
    }

    fun removeItem(item: RunnerView) {
        val index = runnerList.indexOfFirst { item.id == it.id }
        if (index == -1) return
        runnerList.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_runner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(runnerList[position])
    }

    override fun getItemCount(): Int = runnerList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener { if (isAdapterPositionCorrect()) clickListener(runnerList[adapterPosition]) }
        }

        @SuppressLint("SetTextI18n")
        fun bindHolder(runner: RunnerView) {
            itemView.tvRunnerNumber.text = "#${runner.number}"
            itemView.tvRunnerFullName.text = runner.fullName
            itemView.runnerProgress.setStepBean(runner.progress.map { it.bean })
            when {
                runner.isOffTrack -> smallCard(R.color.colorCardOffTrack)
                runner.result != null -> bigCard(runner.result)
                else -> smallCard(R.color.colorCardInProgress)
            }
        }

        private fun smallCard(color: Int) {
            val layoutParams = itemView.root_item.layoutParams.apply {
                height = itemView.resources.getDimensionPixelSize(R.dimen.runnerItemSmallHeight)
            }
            itemView.item_constraint_layout.setBackgroundColor(ContextCompat.getColor(itemView.context, color))
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
            itemView.item_constraint_layout.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorCardFinisher))
            itemView.tvRunnerResult.text = resultStr
            itemView.tvRunnerResult.show()
        }

        private fun isAdapterPositionCorrect(): Boolean = adapterPosition in 0..runnerList.lastIndex
    }
}