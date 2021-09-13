package com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemEditCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.CheckpointPosition
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
        fun onCheckpointChanged(position: Int, item: EditCheckpointView)
    }
}


class EditCheckpointViewHolder(
    binding: ItemEditCheckpointBinding,
    private val interaction: EditCheckpointViewHolderManager.Interaction
) : BaseViewHolder<ItemEditCheckpointBinding, EditCheckpointView>(binding) {

    private var newTitle: String? = null

    var isEditMode: Boolean = false

    override fun onBind(item: EditCheckpointView) = with(binding) {
        super.onBind(item)
        if (item.isEditMode) bindInEditMode(item)
        else bindInViewMode(item)
        when (item.positionState) {
            CheckpointPosition.Center -> {
                topView.visibility = View.VISIBLE
                bottomView.visibility = View.VISIBLE
            }
            CheckpointPosition.End -> {
                topView.visibility = View.VISIBLE
                bottomView.visibility = View.INVISIBLE
            }
            CheckpointPosition.Start -> {
                topView.visibility = View.INVISIBLE
                bottomView.visibility = View.VISIBLE
            }
        }
    }

    private fun ItemEditCheckpointBinding.bindInViewMode(item: EditCheckpointView) {
        isEditMode = false
        tvCheckpointName.visibility = View.VISIBLE
        tvCheckpointName.text = item.title
        etCheckpointName.visibility = View.GONE
        ivSaveCheckpointChanges.visibility = View.GONE
    }

    private fun ItemEditCheckpointBinding.bindInEditMode(item: EditCheckpointView) {
        isEditMode = true
        tvCheckpointName.visibility = View.GONE
        etCheckpointName.visibility = View.VISIBLE
        ivSaveCheckpointChanges.visibility = View.VISIBLE
        etCheckpointName.setText(item.title)
        etCheckpointName.addTextChangedListener { newTitle = it.toString() }
        ivSaveCheckpointChanges.setOnClickListener {
            interaction.onCheckpointChanged(
                adapterPosition,
                item.copy(
                    title = newTitle ?: item.title,
                    isEditMode = false
                )
            )
        }
    }
}