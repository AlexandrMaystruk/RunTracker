package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.hide
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.core.ext.toDateTimeShortFormat
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepState
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import kotlinx.android.synthetic.main.item_checkpoint.view.*
import kotlin.properties.Delegates

class CheckpointsAdapter(private val longClickListener: (Int) -> Unit) : RecyclerView.Adapter<CheckpointsAdapter.ViewHolder>() {

    var checkpoints: MutableList<CheckpointView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkpoint, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(checkpoints[position], position, longClickListener)
    }

    override fun getItemCount(): Int = checkpoints.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindHolder(item: CheckpointView, position: Int, longClickListener: (Int) -> Unit) {
            val stateDrawable = when (item.bean.state) {
                StepState.UNDONE -> R.drawable.ic_unchecked
                StepState.DONE -> R.drawable.ic_check_circle
                StepState.CURRENT -> R.drawable.ic_checked
                StepState.DONE_WARNING -> R.drawable.ic_check_warning

            }
            if (item.bean.state ==  StepState.CURRENT) itemView.ivRunner.show() else itemView.ivRunner.hide()
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
                    itemView.tvCheckpointName.text = item.bean.title
                    itemView.tvCheckpointName.background = null
                    itemView.topView.show()
                    itemView.bottomView.show()
                }
            }

            itemView.ivCheckpointState.background = ContextCompat.getDrawable(itemView.context, stateDrawable)
            if(item.date != null){
                itemView.tvCheckpointDate.setOnLongClickListener {
                    longClickListener.invoke(item.id)
                    true
                }
                itemView.tvCheckpointDate.show()
                itemView.tvCheckpointDate.text = item.date?.toDateTimeShortFormat()
            } else itemView.tvCheckpointDate.gone()
        }
    }
}