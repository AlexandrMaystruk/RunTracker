package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.managers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.color
import com.gmail.maystruks08.nfcruntracker.databinding.LayoutTeamRunnerBinding
import com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views.items.RunnerView
import kotlin.properties.Delegates

internal class SimpleTeamAdapter : RecyclerView.Adapter<SimpleTeamAdapter.ViewHolder>() {

    var team: List<RunnerView> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutTeamRunnerBinding.bind(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.layout_team_runner, parent, false)
            )
        )

    override fun getItemCount() = team.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(team[position])
    }

    class ViewHolder(private val layoutTeamRunnerBinding: LayoutTeamRunnerBinding) : RecyclerView.ViewHolder(layoutTeamRunnerBinding.root) {

        var isSwipeEnable = true

        fun bindHolder(item: RunnerView) = with(layoutTeamRunnerBinding) {
            isSwipeEnable = !item.isOffTrack
            tvNumber.text = item.id
            tvName.text = item.shortName
            tvResult.text = item.result
            progress.setStepBean(item.progress.map { it.bean })
            when {
                item.isOffTrack -> root.setBackgroundColor(root.context.color(R.color.colorCardOffTrack))
                else -> root.setBackgroundColor(root.context.color(R.color.colorWhite))
            }
        }
    }
}