package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepBean
import java.util.*

class CheckpointView(
    val id: Int,
    val stepBean: StepBean,
    val date: Date? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt() ,
        parcel.readParcelable(StepBean::class.java.classLoader)!!,
        Date(parcel.readLong())

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeParcelable(stepBean, flags)
        date?.time?.let { parcel.writeLong(it) }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CheckpointView> {
        override fun createFromParcel(parcel: Parcel): CheckpointView {
            return CheckpointView(parcel)
        }

        override fun newArray(size: Int): Array<CheckpointView?> {
            return arrayOfNulls(size)
        }
    }


}