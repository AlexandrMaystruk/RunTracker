package com.gmail.maystruks08.nfcruntracker.ui.view_models

import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.views.ChartItem

data class DistanceView(
    val id: String,
    val name: String,
    val chartItems: Array<ChartItem>,
    var isSelected: Boolean = false
) : Item {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DistanceView

        if (id != other.id) return false
        if (name != other.name) return false
        if (!chartItems.contentEquals(other.chartItems)) return false
        if (isSelected != other.isSelected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + chartItems.contentHashCode()
        result = 31 * result + isSelected.hashCode()
        return result
    }
}