package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

data class DistanceView(val id: Long, val name: String, var isSelected: Boolean = false) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().orEmpty(),
        parcel.readInt() != 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeInt(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
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