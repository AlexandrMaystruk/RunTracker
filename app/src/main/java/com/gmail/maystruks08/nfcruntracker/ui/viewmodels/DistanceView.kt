package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.views.ChartItem

data class DistanceView(
    val id: String,
    val name: String,
    val chartItems: Array<out ChartItem>,
    var isSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.createTypedArray(ChartItem).orEmpty(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeTypedArray(chartItems, flags)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

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

    companion object CREATOR : Parcelable.Creator<DistanceView> {
        override fun createFromParcel(parcel: Parcel): DistanceView {
            return DistanceView(parcel)
        }

        override fun newArray(size: Int): Array<DistanceView?> {
            return arrayOfNulls(size)
        }
    }

}