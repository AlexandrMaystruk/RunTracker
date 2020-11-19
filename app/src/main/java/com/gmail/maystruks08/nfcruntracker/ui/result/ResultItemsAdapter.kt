package com.gmail.maystruks08.nfcruntracker.ui.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerResultView
import kotlinx.android.synthetic.main.item_result.view.*
import kotlin.properties.Delegates

class ResultItemsAdapter : RecyclerView.Adapter<ResultItemsAdapter.ViewHolder>() {

    var resultList: MutableList<RunnerResultView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(resultList[position])
    }

    override fun getItemCount(): Int = resultList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindHolder(item: RunnerResultView) {
            val runnerNumberText = "#${item.runnerNumber}"
            itemView.tvRunnerPosition.text = item.position.toString()
            itemView.tvRunnerFullName.text = item.runnerFullName
            itemView.tvRunnerNumber.text = runnerNumberText
            itemView.tvRunnerResultTime.text = item.runnerResultTime
        }
    }
}