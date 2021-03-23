package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.view_holders.BaseViewHolder
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views.BaseRunnerView


class RunnerListAdapter(
    var interaction: Interaction? = null
) : ListAdapter<BaseRunnerView, BaseViewHolder<BaseRunnerView>>(BaseRunnerDiffCallback) {

    private val typeFactory = Factory()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BaseRunnerView> {
        val viewResId = typeFactory.viewResourceId(viewType)
        val view = LayoutInflater.from(parent.context).inflate(viewResId, parent, false)
        return typeFactory.holder(viewType, view, interaction)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<BaseRunnerView>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int) = getItem(position).type

    companion object {
        const val RUNNER = 0
        const val RESULT = 1
    }
}