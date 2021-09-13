package com.gmail.maystruks08.nfcruntracker.ui.race.edit.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.color
import com.gmail.maystruks08.nfcruntracker.core.ext.drawable
import com.gmail.maystruks08.nfcruntracker.databinding.ItemDistanceEditBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.view_models.EditDistanceView

class EditDistanceViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemDistanceEditBinding, EditDistanceView> {

    override fun isRelativeItem(item: Item) = item is EditDistanceView

    override fun getLayoutId() = R.layout.item_distance_edit

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemDistanceEditBinding, EditDistanceView> {
        val binding = ItemDistanceEditBinding.inflate(layoutInflater, parent, false)
        return EditDistanceViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<EditDistanceView>() {
        override fun areItemsTheSame(oldItem: EditDistanceView, newItem: EditDistanceView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EditDistanceView, newItem: EditDistanceView) =
            oldItem.name == newItem.name &&
                    oldItem.isSelected == newItem.isSelected &&
                    oldItem.isEditMode == newItem.isEditMode
    }

    interface Interaction {
        fun onItemSelected(distance: EditDistanceView)
        fun onEditDistanceClicked(position: Int, distance: EditDistanceView)
        fun onDistanceEdited(position: Int, distance: EditDistanceView)
    }

}


class EditDistanceViewHolder(
    binding: ItemDistanceEditBinding,
    private val interaction: EditDistanceViewHolderManager.Interaction
) : BaseViewHolder<ItemDistanceEditBinding, EditDistanceView>(binding) {

    private var newName: String? = null

    override fun onBind(item: EditDistanceView) = with(binding) {
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

        if (item.isEditMode) {
            tvDistanceName.visibility = View.GONE
            etDistanceName.visibility = View.VISIBLE
            etDistanceName.setText(item.name)
            etDistanceName.addTextChangedListener { newName = it?.toString() }
            ivEditDistance.setImageDrawable(root.context.drawable(R.drawable.ic_check_circle))
        } else {
            newName = null
            tvDistanceName.visibility = View.VISIBLE
            etDistanceName.visibility = View.GONE
            etDistanceName.setText("")
            ivEditDistance.setImageDrawable(root.context.drawable(R.drawable.ic_mode_edit))
        }

        ivEditDistance.setOnClickListener {
            if (item.isEditMode) {
                interaction.onDistanceEdited(
                    adapterPosition,
                    item.copy(name = newName ?: item.name)
                )
                return@setOnClickListener
            }
            interaction.onEditDistanceClicked(adapterPosition, item)
        }

        itemView.setOnClickListener {
            interaction.onItemSelected(item)
        }
    }
}