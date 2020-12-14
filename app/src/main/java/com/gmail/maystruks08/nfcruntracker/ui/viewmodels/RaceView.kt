package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

data class RaceView(val id: Long, val name: String, val firstDistanceId: Long?) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString().orEmpty(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        firstDistanceId?.let { parcel.writeLong(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RaceView> {
        override fun createFromParcel(parcel: Parcel): RaceView {
            return RaceView(parcel)
        }

        override fun newArray(size: Int): Array<RaceView?> {
            return arrayOfNulls(size)
        }
    }
}