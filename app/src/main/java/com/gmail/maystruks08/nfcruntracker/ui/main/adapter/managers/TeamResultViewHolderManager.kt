package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemTeamResultBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.result.TeamResultView

class TeamResultViewHolderManager : ViewHolderManager<ItemTeamResultBinding, TeamResultView> {

    override fun isRelativeItem(item: Item) = item is TeamResultView

    override fun getLayoutId() = R.layout.item_team_result

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemTeamResultBinding, TeamResultView> {
        val binding = ItemTeamResultBinding.inflate(layoutInflater, parent, false)
        return TeamResultViewHolder(binding)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<TeamResultView>() {
        override fun areItemsTheSame(oldItem: TeamResultView, newItem: TeamResultView) =
            oldItem.teamName == newItem.teamName

        override fun areContentsTheSame(oldItem: TeamResultView, newItem: TeamResultView) =
            oldItem == newItem
    }

}


class TeamResultViewHolder(
    binding: ItemTeamResultBinding
) : BaseViewHolder<ItemTeamResultBinding, TeamResultView>(binding) {


    override fun onBind(item: TeamResultView) = with(binding) {
        super.onBind(item)
        val teamPositionText = "#${item.position}"
        tvTeamPosition.text = teamPositionText
        tvTeamName.text = item.teamName

        tvRunnerNumber.text = item.firstRunnerNumber
        tvRunnerName.text = item.firstRunnerFullName
        tvRunnerResult.text = item.firstRunnerResultTime

        tvRunnerNumber2.text = item.secondRunnerNumber
        tvRunnerName2.text = item.secondRunnerFullName
        tvRunnerResult2.text = item.secondRunnerResultTime
    }
}