package com.gmail.maystruks08.nfcruntracker.ui.runners.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.core.ext.gone
import com.gmail.maystruks08.nfcruntracker.core.ext.show
import com.gmail.maystruks08.nfcruntracker.databinding.ItemSelectCheckpointBinding
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView
import com.gmail.maystruks08.nfcruntracker.ui.views.StepState
import kotlin.properties.Delegates

class CheckpointAdapter(
    private val interaction: Interaction
) : RecyclerView.Adapter<CheckpointAdapter.ViewHolder>() {

    var checkpoints: MutableList<CheckpointView> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_select_checkpoint, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindHolder(checkpoints[position], position, interaction)
    }

    override fun getItemCount(): Int = checkpoints.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemSelectCheckpointBinding.bind(itemView)

        fun bindHolder(item: CheckpointView, position: Int, interaction: Interaction) {
            with(binding) {
                val stateDrawable = when (item.bean.state) {
                    StepState.CURRENT -> R.drawable.ic_checked
                    else -> R.drawable.ic_unchecked
                }
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

                itemView.setOnClickListener {
                    interaction.onClickAtCheckpoint(item)
                }
            }
        }
    }

    interface Interaction {

        fun onClickAtCheckpoint(checkpointView: CheckpointView)

    }
}