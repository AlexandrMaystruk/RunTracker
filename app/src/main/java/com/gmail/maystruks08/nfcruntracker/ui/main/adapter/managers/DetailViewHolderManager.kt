package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.drawable
import com.gmail.maystruks08.nfcruntracker.core.ext.string
import com.gmail.maystruks08.nfcruntracker.databinding.ItemDetailBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.AppAdapter
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerDetailView
import com.gmail.maystruks08.nfcruntracker.ui.runner.RunnerCheckpointsViewHolderManager

class DetailViewHolderManager(
    private val interaction: RunnerCheckpointsViewHolderManager.Interaction
) : ViewHolderManager<ItemDetailBinding, RunnerDetailView> {

    override fun isRelativeItem(item: Item) = item is RunnerDetailView

    override fun getLayoutId() = R.layout.item_detail

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemDetailBinding, RunnerDetailView> {
        val binding = ItemDetailBinding.inflate(layoutInflater, parent, false)
        return DetailViewHolder(interaction, binding)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<RunnerDetailView>() {
        override fun areItemsTheSame(oldItem: RunnerDetailView, newItem: RunnerDetailView) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RunnerDetailView, newItem: RunnerDetailView) =
            oldItem == newItem
    }
}

class DetailViewHolder(
    interaction: RunnerCheckpointsViewHolderManager.Interaction,
    binding: ItemDetailBinding
) : BaseViewHolder<ItemDetailBinding, RunnerDetailView>(binding) {

    private val checkpointsAdapter = AppAdapter(listOf(RunnerCheckpointsViewHolderManager(interaction)))

    override fun onBind(item: RunnerDetailView) = with(binding) {
        super.onBind(item)
        val numberStr = "#" + item.id
        tvRunnerNumber.text = numberStr
        tvRunnerFullName.text = item.fullName
        tvDateOfBirthday.setTextOrHide(item.dateOfBirthday)
        tvRunnerCity.setTextOrHide(item.city)
        tvRunnerCardId.setTextOrHide(item.cardId)

        rvRunnerCheckpoints.adapter = checkpointsAdapter
        checkpointsAdapter.submitList(item.progress)
        when {
            item.isOffTrack() -> {
                val buttonText = root.context.string(R.string.off_track)
                tvDetailStatus.text = buttonText
                tvDetailStatus.visibility = View.VISIBLE
                tvDetailStatus.background = root.context.drawable(R.drawable.bg_main_btn_red)
            }
            item.result != null -> {
                val totalResultStr = root.context.getString(R.string.total_time, item.result)
                tvDetailStatus.text = totalResultStr
                tvDetailStatus.visibility = View.VISIBLE
                tvDetailStatus.background = root.context.drawable(R.drawable.bg_main_btn_green)
            }
            else -> {
                tvDetailStatus.visibility = View.GONE
            }
        }
    }

    private fun TextView.setTextOrHide(itemText: String?) {
        if (!item.cardId.isNullOrEmpty()) {
            visibility = View.VISIBLE
            text = itemText
            return
        }
        visibility = View.GONE
    }
}

