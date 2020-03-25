package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.CheckpointState
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.hide
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateTimeShortFormat
import kotlinx.android.synthetic.main.item_checkpoint.view.*
import kotlin.properties.Delegates

class CheckpointsAdapter : RecyclerView.Adapter<CheckpointsAdapter.ViewHolder>() {

    var checkpoints: MutableList<Checkpoint> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkpoint, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(checkpoints[position], position)
    }

    override fun getItemCount(): Int = checkpoints.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindHolder(item: Checkpoint, position: Int) {
            val stateDrawable = when (item.state) {
                CheckpointState.UNDONE -> R.drawable.ic_unchecked
                CheckpointState.DONE -> R.drawable.ic_check_circle
                CheckpointState.CURRENT -> R.drawable.ic_checked
            }
            if (item.state == CheckpointState.CURRENT) itemView.ivRunner.show() else itemView.ivRunner.hide()
            when (position) {
                0 -> {
                    itemView.topView.gone()
                    itemView.bottomView.show()
                    itemView.tvCheckpointName.text = null
                    itemView.tvCheckpointName.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_start)
                }
                checkpoints.lastIndex -> {
                    itemView.tvCheckpointName.text = null
                    itemView.tvCheckpointName.background = ContextCompat.getDrawable(itemView.context, R.drawable.ic_finish)
                    itemView.topView.show()
                    itemView.bottomView.gone()
                }
                else -> {
                    itemView.tvCheckpointName.text = item.name
                    itemView.tvCheckpointName.background = null
                    itemView.topView.show()
                    itemView.bottomView.show()
                }
            }

            itemView.ivCheckpointState.background = ContextCompat.getDrawable(itemView.context, stateDrawable)
            if(item.date != null){
                itemView.tvCheckpointDate.show()
                itemView.tvCheckpointDate.text = item.date?.toDateTimeShortFormat()
            } else itemView.tvCheckpointDate.gone()
        }
    }
}