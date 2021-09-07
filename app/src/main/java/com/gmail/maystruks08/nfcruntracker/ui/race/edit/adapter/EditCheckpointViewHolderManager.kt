package com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemEditCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.EditCheckpointView

class EditCheckpointViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemEditCheckpointBinding, EditCheckpointView> {

    override fun isRelativeItem(item: Item) = item is EditCheckpointView

    override fun getLayoutId() = R.layout.item_edit_checkpoint

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemEditCheckpointBinding, EditCheckpointView> {
        val binding = ItemEditCheckpointBinding.inflate(layoutInflater, parent, false)
        return EditCheckpointViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<EditCheckpointView>() {
        override fun areItemsTheSame(oldItem: EditCheckpointView, newItem: EditCheckpointView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EditCheckpointView, newItem: EditCheckpointView) =
            oldItem == newItem
    }

    interface Interaction {

    }

}


class EditCheckpointViewHolder(
    binding: ItemEditCheckpointBinding,
    private val interaction: EditCheckpointViewHolderManager.Interaction
) : BaseViewHolder<ItemEditCheckpointBinding, EditCheckpointView>(binding) {

    override fun onBind(item: EditCheckpointView) = with(binding) {
        super.onBind(item)
        tvCheckpointName.text = item.title
    }
}