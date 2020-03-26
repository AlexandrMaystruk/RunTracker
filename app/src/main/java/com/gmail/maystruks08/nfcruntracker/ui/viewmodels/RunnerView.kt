package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

class RunnerView(
    val id: String,
    val number: String,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val isIron: Boolean,
    val currentPosition: Int

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString()?:"",
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(number)
        parcel.writeString(fullName)
        parcel.writeString(city)
        parcel.writeString(result)
        parcel.writeString(dateOfBirthday)
        parcel.writeByte(if (isIron) 1 else 0)
        parcel.writeInt(currentPosition)
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