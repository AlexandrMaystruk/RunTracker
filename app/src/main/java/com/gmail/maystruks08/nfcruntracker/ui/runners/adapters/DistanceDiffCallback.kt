package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView

object DistanceDiffCallback : DiffUtil.ItemCallback<DistanceView?>() {

    override fun areItemsTheSame(oldDistance: DistanceView, newDistance: DistanceView): Boolean {
        return oldDistance.id == newDistance.id
    }

    override fun areContentsTheSame(oldDistance: DistanceView, newDistance: DistanceView): Boolean {
        return oldDistance.name == newDistance.name && oldDistance.isSelected == newDistance.isSelected
    }
}