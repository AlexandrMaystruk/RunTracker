package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.databinding.ItemDistanceBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView

class DistanceViewHolder(
    itemView: View,
    private val interaction: DistanceListAdapter.Interaction?
) : RecyclerView.ViewHolder(itemView) {

    private val binding = ItemDistanceBinding.bind(itemView)

    @SuppressLint("SetTextI18n")
    fun bindHolder(distanceView: DistanceView) {
        with(binding) {
            tvDistanceName.text = distanceView.name
            if (distanceView.isSelected) {
                distanceCard.strokeLineWidth = 3f
            } else {
                distanceCard.strokeLineWidth = 1f
            }
            itemView.setOnClickListener {
                interaction?.onItemSelected(distanceView)
            }
        }
    }
}