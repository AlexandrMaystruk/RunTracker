package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemTeamBinding
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.ViewHolderManager
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.TeamRunnerSwipeActionHelper
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView
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
        fun onItemSelected(team: TeamView)
        fun onRunnerSwipedLeft(position: Int, swipedRunner: RunnerView)
        fun onRunnerSwipedRight(position: Int, swipedRunner: RunnerView)
    }
}


class TeamViewHolder(
    binding: ItemTeamBinding,
    private val interaction: TeamViewHolderManager.Interaction
) : BaseViewHolder<ItemTeamBinding, TeamView>(binding) {

    private val teamAdapter = SimpleTeamAdapter { interaction.onItemSelected(item) }

    override fun onBind(item: TeamView) = with(binding) {
        super.onBind(item)
        tvTeamName.text = item.teamName
        tvTeamResult.text = item.teamResult.orEmpty()
        rvTeam.adapter = teamAdapter
        setUpItemTouchHelper()
        teamAdapter.team = item.runners
        root.setOnClickListener {
            interaction.onItemSelected(item)
        }
    }

    private fun setUpItemTouchHelper() {
        val orderSwipeActionHelper = object : TeamRunnerSwipeActionHelper(binding.root.context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val swipedRunner = teamAdapter.team[position] as? RunnerView
                swipedRunner ?: return
                if (direction == ItemTouchHelper.LEFT) {
                    interaction.onRunnerSwipedLeft(adapterPosition, swipedRunner)
                }
                if (direction == ItemTouchHelper.RIGHT) {
                    interaction.onRunnerSwipedRight(adapterPosition, swipedRunner)
                }
            }
        }
        ItemTouchHelper(orderSwipeActionHelper).attachToRecyclerView(binding.rvTeam)
    }
}