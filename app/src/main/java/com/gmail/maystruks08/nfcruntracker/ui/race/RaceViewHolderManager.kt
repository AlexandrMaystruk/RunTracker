package com.gmail.maystruks08.nfcruntracker.ui.race

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemRaceBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.RaceView

class RaceViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemRaceBinding, RaceView> {

    override fun isRelativeItem(item: Item) = item is RaceView

    override fun getLayoutId() = R.layout.item_race

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemRaceBinding, RaceView> {
        val binding = ItemRaceBinding.inflate(layoutInflater, parent, false)
        return RaceViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<RaceView>() {
        override fun areItemsTheSame(oldItem: RaceView, newItem: RaceView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RaceView, newItem: RaceView) =
            oldItem == newItem
    }

    interface Interaction {
        fun onItemSelected(item: RaceView)
    }

}


class RaceViewHolder(
    binding: ItemRaceBinding,
    private val interaction: RaceViewHolderManager.Interaction
) : BaseViewHolder<ItemRaceBinding, RaceView>(binding) {

    override fun onBind(item: RaceView) = with(binding) {
        super.onBind(item)

        tvName.text = item.name
        btnSelectRace.setOnClickListener {
            interaction.onItemSelected(item)
        }
    }
}