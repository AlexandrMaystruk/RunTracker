package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.views.stepview.Bean
import java.util.*

data class CheckpointView(
    val id: Int,
    val bean: Bean,
    var date: Date? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        Bean(parcel),
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
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