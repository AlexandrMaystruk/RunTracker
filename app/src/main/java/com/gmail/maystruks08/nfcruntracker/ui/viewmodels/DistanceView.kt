package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

data class DistanceView(val id: Int, val name: String) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
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