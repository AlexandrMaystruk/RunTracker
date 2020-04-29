package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            if(runner.result != null){
                val resultStr = "Время: ${runner.result}"
                itemView.tvRunnerResult.text = resultStr
                itemView.tvRunnerResult.show()
            } else {
                itemView.tvRunnerResult.text = null
                itemView.tvRunnerResult.gone()
            }
            itemView.runnerProgress.setStepBean(runner.progress.map { it.bean })
        }

        private fun isAdapterPositionCorrect(): Boolean = adapterPosition in 0..runnerList.lastIndex
    }
}