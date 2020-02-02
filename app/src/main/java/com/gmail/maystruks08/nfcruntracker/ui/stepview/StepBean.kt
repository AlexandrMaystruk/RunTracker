package com.gmail.maystruks08.nfcruntracker.ui.stepview

import android.os.Parcel
import android.os.Parcelable

const val STEP_UNDO = -1
const val STEP_CURRENT = 0
const val STEP_COMPLETED = 1

data class StepBean constructor(var name: String, var state: Int) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(state)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<StepBean> {
        override fun createFromParcel(parcel: Parcel): StepBean {
            return StepBean(parcel)
        }

        override fun newArray(size: Int): Array<StepBean?> {
            return arrayOfNulls(size)
        }
    }
}