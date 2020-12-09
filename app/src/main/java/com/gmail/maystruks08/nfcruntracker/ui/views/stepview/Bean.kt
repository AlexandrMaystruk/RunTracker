package com.gmail.maystruks08.nfcruntracker.ui.views.stepview

import android.os.Parcel
import android.os.Parcelable


data class Bean(val title: String, var state: StepState): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        StepState.fromOrdinal(parcel.readInt())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(state.ordinal)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Bean> {
        override fun createFromParcel(parcel: Parcel): Bean {
            return Bean(parcel)
        }

        override fun newArray(size: Int): Array<Bean?> {
            return arrayOfNulls(size)
        }
    }
}