package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView


class RunnerListAdapter(var interaction: Interaction? = null) :
    ListAdapter<RunnerView, RunnerViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_runner, parent, false)
        return RunnerViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: RunnerViewHolder, position: Int) {
        val runnerAtPosition = getItem(position)
        holder.bindHolder(runnerAtPosition)
    }

    interface Interaction {

        fun onItemSelected(item: RunnerView)

    }
}