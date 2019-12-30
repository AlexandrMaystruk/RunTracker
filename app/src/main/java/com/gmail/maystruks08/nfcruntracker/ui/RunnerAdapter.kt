package com.gmail.maystruks08.nfcruntracker.ui

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.nfcruntracker.R
import kotlinx.android.synthetic.main.item_runner.view.*
import kotlin.properties.Delegates

class RunnerAdapter(private val clickListener: (Runner) -> Unit) :
    RecyclerView.Adapter<RunnerAdapter.ViewHolder>() {

    var runnerList: MutableList<Runner> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_runner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(runnerList[position], clickListener)
    }

    override fun getItemCount(): Int = runnerList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindHolder(runner: Runner, clickListener: (Runner) -> Unit) {
            itemView.tvRunnerName.text = runner.name
            itemView.setOnClickListener { clickListener(runner) }
        }
    }
}