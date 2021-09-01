package com.gmail.maystruks08.nfcruntracker.ui.adapter.base

import androidx.recyclerview.widget.DiffUtil

@Suppress("UNCHECKED_CAST")
class AppDiffUtil(
    private val list: List<ViewHolderManager<*, *>>,
) : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        if (oldItem::class != newItem::class) return false
        return getItemCallback(oldItem).areItemsTheSame(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        if (oldItem::class != newItem::class) return false
        return getItemCallback(oldItem).areContentsTheSame(oldItem, newItem)
    }

    override fun getChangePayload(oldItem: Item, newItem: Item): Any? {
        if (oldItem::class != newItem::class) return false
        return getItemCallback(oldItem).getChangePayload(oldItem, newItem)
    }

    private fun getItemCallback(item: Item): DiffUtil.ItemCallback<Item> = list.find { it.isRelativeItem(item) }
            ?.getDiffUtil()
            ?.let { it as DiffUtil.ItemCallback<Item> }
            ?: throw IllegalStateException("DiffUtil not found for $item")

}