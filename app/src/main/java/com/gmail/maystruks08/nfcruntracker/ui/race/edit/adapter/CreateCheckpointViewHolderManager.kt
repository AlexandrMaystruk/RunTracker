package com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemCreateCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CreateNewCheckpointView

class CreateNewCheckpointViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemCreateCheckpointBinding, CreateNewCheckpointView> {

    override fun isRelativeItem(item: Item) = item is CreateNewCheckpointView

    override fun getLayoutId() = R.layout.item_create_checkpoint

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemCreateCheckpointBinding, CreateNewCheckpointView> {
        val binding = ItemCreateCheckpointBinding.inflate(layoutInflater, parent, false)
        return CreateCheckpointViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<CreateNewCheckpointView>() {
        override fun areItemsTheSame(
            oldItem: CreateNewCheckpointView,
            newItem: CreateNewCheckpointView
        ) = true

        override fun areContentsTheSame(
            oldItem: CreateNewCheckpointView,
            newItem: CreateNewCheckpointView
        ) = true
    }

    interface Interaction {
        fun onCreateNewCheckpointClicked(position: Int)
    }
}


class CreateCheckpointViewHolder(
    binding: ItemCreateCheckpointBinding,
    private val interaction: CreateNewCheckpointViewHolderManager.Interaction
) : BaseViewHolder<ItemCreateCheckpointBinding, CreateNewCheckpointView>(binding) {

    override fun onBind(item: CreateNewCheckpointView) = with(binding) {
        super.onBind(item)
        root.setOnClickListener { interaction.onCreateNewCheckpointClicked(position = adapterPosition) }
    }
}