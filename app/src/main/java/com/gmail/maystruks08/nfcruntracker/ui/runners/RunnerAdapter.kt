package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import kotlinx.android.synthetic.main.item_runner.view.*
import kotlin.properties.Delegates

class RunnerAdapter(private val clickListener: (RunnerView) -> Unit) :
    RecyclerView.Adapter<RunnerAdapter.ViewHolder>() {

    var runnerList: MutableList<RunnerView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    fun insertItem(item: RunnerView, position: Int? = null) {
        if (position != null) {
            runnerList.add(position, item)
            notifyItemInserted(position)
        } else {
            runnerList.add(item)
            notifyItemInserted(runnerList.indexOf(item))
        }
    }

    fun updateItem(item: RunnerView) {
        val index = runnerList.indexOfFirst { item.id == it.id }
        if (index == -1) return
        runnerList.removeAt(index)
        runnerList.add(index, item)
        notifyItemChanged(index)
    }

    fun removeItem(position: Int) {
        runnerList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_runner, parent, false)
        return ViewHolder(
            view
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(runnerList[position], clickListener)
    }

    override fun getItemCount(): Int = runnerList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindHolder(runner: RunnerView, clickListener: (RunnerView) -> Unit) {
            itemView.tvRunnerNumber.text = "#${runner.number}"
            itemView.tvRunnerName.text = runner.name
            itemView.tvRunnerSurname.text = runner.surname
            itemView.runnerProgress.setStepViewTexts(runner.checkpoints.map { it.stepBean })
            if(runner.checkpoints.isNotEmpty()){
                itemView.tvCurrentCheckpoint.text = runner.getCurrentPosition()?.stepBean?.name
            }
            itemView.setOnClickListener { clickListener(runner) }
        }
    }
}