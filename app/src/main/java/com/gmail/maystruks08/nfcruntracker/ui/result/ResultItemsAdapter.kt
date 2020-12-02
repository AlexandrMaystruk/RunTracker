package com.gmail.maystruks08.nfcruntracker.ui.result

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemResultBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerResultView
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

        private val binding = ItemResultBinding.bind(itemView)

        fun bindHolder(item: RunnerResultView) {
            val runnerNumberText = "#${item.runnerNumber}"
            with(binding){
                tvRunnerPosition.text = item.position.toString()
                tvRunnerFullName.text = item.runnerFullName
                tvRunnerNumber.text = runnerNumberText
                tvRunnerResultTime.text = item.runnerResultTime
            }
        }
    }
}