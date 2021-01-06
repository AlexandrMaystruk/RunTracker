package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.views.Bean
import java.util.*

data class CheckpointView(
    val id: Long,
    val bean: Bean,
    var date: Date? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        Bean(parcel),
        Date(parcel.readLong())
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(bean, 5)
        date?.let { parcel.writeLong(it.time) }
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