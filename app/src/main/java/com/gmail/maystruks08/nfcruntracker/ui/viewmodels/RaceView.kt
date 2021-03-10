package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

data class RaceView(val id: String, val name: String, val firstDistanceId: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        firstDistanceId?.let { parcel.writeString(it) }
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