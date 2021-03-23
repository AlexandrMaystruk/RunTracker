package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.distance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView

class DistanceListAdapter(var interaction: Interaction? = null) :
    ListAdapter<DistanceView, DistanceViewHolder>(DistanceDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_distance, parent, false)
        return DistanceViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: DistanceViewHolder, position: Int) {
        val distanceAtPosition = getItem(position)
        holder.bindHolder(distanceAtPosition)
    }

    interface Interaction {

        fun onItemSelected(distance: DistanceView)

    }
}