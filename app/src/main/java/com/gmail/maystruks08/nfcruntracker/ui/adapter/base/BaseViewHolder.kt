package com.gmail.maystruks08.nfcruntracker.ui.adapter.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Don't forget to call super.unBind(item) in ViewHolder realization =)
 * */
abstract class BaseViewHolder<out V : ViewBinding, I : Item>(
        val binding: V
) : RecyclerView.ViewHolder(binding.root) {

    lateinit var item: I

    open fun onBind(item: I) {
        this.item = item
    }

    open fun onBind(item: I, payloads: List<Any>) {
        this.item = item
    }
}