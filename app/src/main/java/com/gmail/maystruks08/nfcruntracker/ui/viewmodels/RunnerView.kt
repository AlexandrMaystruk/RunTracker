package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.stepview.STEP_CURRENT

class RunnerView(
    val id: String,
    val number: String,
    val fullName: String,
    val city: String,
    val dateOfBirthday: String,
    var checkpoints: List<CheckpointView>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createTypedArrayList(CheckpointView) as List<CheckpointView>
    )

    fun getCurrentPosition(): CheckpointView? = checkpoints.find { it.stepBean.state == STEP_CURRENT }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(number)
        parcel.writeString(fullName)
        parcel.writeString(city)
        parcel.writeString(dateOfBirthday)
        parcel.writeTypedList(checkpoints)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RunnerView> {
        override fun createFromParcel(parcel: Parcel): RunnerView {
            return RunnerView(parcel)
        }

        override fun newArray(size: Int): Array<RunnerView?> {
            return arrayOfNulls(size)
        }
    }

}