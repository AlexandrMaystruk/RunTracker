package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemTeamBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.TeamView

class TeamViewHolderManager(
    private val interaction: Interaction
) : ViewHolderManager<ItemTeamBinding, TeamView> {

    override fun isRelativeItem(item: Item) = item is TeamView

    override fun getLayoutId() = R.layout.item_team

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemTeamBinding, TeamView> {
        val binding = ItemTeamBinding.inflate(layoutInflater, parent, false)
        return TeamViewHolder(binding, interaction)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<TeamView>() {
        override fun areItemsTheSame(oldItem: TeamView, newItem: TeamView) =
            oldItem.teamName == newItem.teamName

        override fun areContentsTheSame(oldItem: TeamView, newItem: TeamView) =
            oldItem == newItem
    }

    interface Interaction {
        fun onItemSelected(distance: TeamView)
    }

}


class TeamViewHolder(
    binding: ItemTeamBinding,
    private val interaction: TeamViewHolderManager.Interaction
) : BaseViewHolder<ItemTeamBinding, TeamView>(binding) {

    override fun onBind(item: TeamView) = with(binding) {
        super.onBind(item)
        tvTeamName.text = item.teamName
        tvTeamResult.text = item.teamResult.orEmpty()

        val firstRunner = item.runners.first()
        tvRunnerNumber.text = firstRunner.number
        tvRunnerName.text = firstRunner.shortName
        tvRunnerResult.text = firstRunner.result.orEmpty()

        val secondRunner = item.runners.last()
        tvRunnerNumber2.text = secondRunner.number
        tvRunnerName2.text = secondRunner.shortName
        tvRunnerResult2.text = secondRunner.result.orEmpty()

        itemView.setOnClickListener {
            interaction.onItemSelected(item)
        }
    }
}