package com.gmail.maystruks08.nfcruntracker.ui.runner

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.domain.toDateTimeShortFormat
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.hide
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.databinding.ItemCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.views.StepState
import kotlin.properties.Delegates

class CheckpointsAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<CheckpointsAdapter.ViewHolder>() {

    var checkpoints: MutableList<CheckpointView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    var isOffTrack: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_checkpoint, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(checkpoints[position], position, interaction)
    }

    override fun getItemCount(): Int = checkpoints.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemCheckpointBinding.bind(itemView)


        fun bindHolder(item: CheckpointView, position: Int, interaction: Interaction) {
            with(binding) {
                val stateDrawable = when (item.bean.state) {
                    StepState.UNDONE -> R.drawable.ic_unchecked
                    StepState.DONE -> R.drawable.ic_check_circle
                    StepState.CURRENT -> R.drawable.ic_checked
                    StepState.DONE_WARNING -> R.drawable.ic_check_warning
                }
                if (item.bean.state == StepState.CURRENT) ivRunner.show() else ivRunner.hide()
                when (position) {
                    0 -> {
                        topView.gone()
                        bottomView.show()
                        tvCheckpointName.text = null
                        tvCheckpointName.background =
                            ContextCompat.getDrawable(root.context, R.drawable.ic_start)
                    }
                    checkpoints.lastIndex -> {
                        tvCheckpointName.text = null
                        tvCheckpointName.background = ContextCompat.getDrawable(root.context, R.drawable.ic_finish)
                        topView.show()
                        bottomView.gone()
                    }
                    else -> {
                        tvCheckpointName.text = item.bean.title
                        tvCheckpointName.background = null
                        topView.show()
                        bottomView.show()
                    }
                }

                ivCheckpointState.background = ContextCompat.getDrawable(root.context, stateDrawable)
                if (isOffTrack) {
                    tvCheckpointDate.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvCheckpointDate.paintFlags = Paint.LINEAR_TEXT_FLAG
                }
                if (item.date != null) {
                    tvCheckpointDate.setOnLongClickListener {
                        interaction.onLongCLickAtCheckpointDate(item)
                        true
                    }
                    tvCheckpointDate.show()
                    tvCheckpointDate.text = item.date?.toDateTimeShortFormat()
                } else tvCheckpointDate.gone()
            }
        }
    }

    interface Interaction {

        fun onLongCLickAtCheckpointDate(checkpointView: CheckpointView)

    }
}