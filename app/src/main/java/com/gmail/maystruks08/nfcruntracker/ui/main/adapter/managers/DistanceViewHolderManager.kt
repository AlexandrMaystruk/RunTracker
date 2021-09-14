package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.color
import com.gmail.maystruks08.nfcruntracker.databinding.ItemDistanceBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.DistanceView

class DistanceViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemDistanceBinding, DistanceView> {

    override fun isRelativeItem(item: Item) = item is DistanceView

    override fun getLayoutId() = R.layout.item_distance

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemDistanceBinding, DistanceView> {
        val binding = ItemDistanceBinding.inflate(layoutInflater, parent, false)
        return DistanceViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<DistanceView>() {
        override fun areItemsTheSame(oldItem: DistanceView, newItem: DistanceView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DistanceView, newItem: DistanceView) =
            oldItem.name == newItem.name &&
                    oldItem.isSelected == newItem.isSelected &&
                    oldItem.chartItems.contentEquals(newItem.chartItems)
    }

    interface Interaction {
        fun onItemSelected(distance: DistanceView)
    }

}


class DistanceViewHolder(
    binding: ItemDistanceBinding,
    private val interaction: DistanceViewHolderManager.Interaction
) : BaseViewHolder<ItemDistanceBinding, DistanceView>(binding) {

    override fun onBind(item: DistanceView) = with(binding) {
        super.onBind(item)
        tvDistanceName.text = item.name
            if (item.isSelected) {
            distanceCard.strokeLineColor = Color.WHITE
            distanceCard.strokeLineWidth = 5f
            distanceCard.backgroundColor = root.context.color(R.color.colorAccent)
        } else {
            distanceCard.strokeLineColor = Color.BLACK
            distanceCard.strokeLineWidth = 1f
            distanceCard.backgroundColor = root.context.color(R.color.colorAccentDark)
        }
        chartView.setChartItems(item.chartItems)
        itemView.setOnClickListener {
            interaction.onItemSelected(item)
        }
    }
}