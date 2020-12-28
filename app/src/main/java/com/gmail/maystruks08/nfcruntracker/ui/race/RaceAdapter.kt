package com.gmail.maystruks08.nfcruntracker.ui.race

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemRaceBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RaceView
import kotlin.properties.Delegates

class RaceAdapter(private val interaction: Interaction) : RecyclerView.Adapter<RaceAdapter.ViewHolder>() {

    var raceList: MutableList<RaceView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_race, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(raceList[position], interaction)
    }

    override fun getItemCount(): Int = raceList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemRaceBinding.bind(itemView)


        fun bindHolder(item: RaceView, interaction: Interaction) {
            with(binding) {
                tvName.text = item.name
                root.setOnClickListener {
                    interaction.onClickAtRace(item)
                }
            }
        }
    }

    interface Interaction {

        fun onClickAtRace(raceView: RaceView)

    }
}