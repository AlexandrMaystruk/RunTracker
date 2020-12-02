package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.databinding.ItemDistanceBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView
import kotlin.properties.Delegates

class DistanceAdapter(private val interaction: Interaction) : RecyclerView.Adapter<DistanceAdapter.ViewHolder>() {

    private var selectedPosition = 0
    private var selectedBindingView: ItemDistanceBinding? = null

    var items: MutableList<DistanceView> by Delegates.observable(ArrayList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    fun insertItemOrUpdateIfExist(item: DistanceView) {
        val index = items.indexOfFirst { item.id == it.id }
        if (index == -1) {
            items.add(item)
            notifyItemInserted(items.lastIndex)
        } else {
            items.removeAt(index)
            items.add(index, item)
            notifyItemChanged(index)
        }
    }

    fun updateItem(item: DistanceView) {
        val index = items.indexOfFirst { item.id == it.id }
        if (index == -1) return
        items.removeAt(index)
        items.add(index, item)
        notifyItemChanged(index)
    }

    fun removeItem(item: DistanceView) {
        val index = items.indexOfFirst { item.id == it.id }
        if (index == -1) return
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_distance, parent, false)
        return ViewHolder(view, interaction)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View,private val  interaction: Interaction) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemDistanceBinding.bind(itemView)

        @SuppressLint("SetTextI18n")
        fun bindHolder(runner: DistanceView) {
            with(binding){
                tvDistanceName.text = runner.name
                if(selectedPosition == adapterPosition){
                    selectedBindingView = this
                    tvDistanceName.setBackgroundResource(R.drawable.bg_corner_border_selected)
                } else {
                    tvDistanceName.setBackgroundResource(R.drawable.bg_corner_border)
                }
                itemView.setOnClickListener {
                    deselectView()
                    selectView()
                    interaction.onItemSelected(runner)
                }
            }
        }

        private fun selectView(){
            selectedPosition = adapterPosition
            selectedBindingView = binding
            binding.tvDistanceName.setBackgroundResource(R.drawable.bg_corner_border_selected)
        }

        private fun deselectView(){
            selectedBindingView?.tvDistanceName?.setBackgroundResource(R.drawable.bg_corner_border)
        }
    }


    interface Interaction {

        fun onItemSelected(item: DistanceView)

    }
}