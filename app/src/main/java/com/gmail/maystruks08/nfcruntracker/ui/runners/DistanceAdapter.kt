package com.gmail.maystruks08.nfcruntracker.ui.runners

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.DistanceView
import kotlinx.android.synthetic.main.item_distance.view.*
import kotlin.properties.Delegates

class DistanceAdapter(private val clickListener: (DistanceView) -> Unit) : RecyclerView.Adapter<DistanceAdapter.ViewHolder>() {

    var items: MutableList<DistanceView> by Delegates.observable(ArrayList()) { _, oldValue, newValue ->
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
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener { if (isAdapterPositionCorrect()) clickListener(items[adapterPosition]) }
        }

        @SuppressLint("SetTextI18n")
        fun bindHolder(runner: DistanceView) {
            itemView.tvDistanceName.text = runner.name
        }

        private fun isAdapterPositionCorrect(): Boolean = adapterPosition in 0..items.lastIndex
    }
}