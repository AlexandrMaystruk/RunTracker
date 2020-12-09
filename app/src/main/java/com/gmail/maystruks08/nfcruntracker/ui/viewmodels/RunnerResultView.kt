package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

class RunnerResultView(
    val runnerId: String,
    val runnerFullName: String,
    val runnerNumber: String,
    val runnerResultTime: String,
    val position: Int
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString().orEmpty(),
        source.readString().orEmpty(),
        source.readString().orEmpty(),
        source.readString().orEmpty(),
        source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(runnerId)
        writeString(runnerFullName)
        writeString(runnerNumber)
        writeString(runnerResultTime)
        writeInt(position)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RunnerResultView> =
            object : Parcelable.Creator<RunnerResultView> {
                override fun createFromParcel(source: Parcel): RunnerResultView =
                    RunnerResultView(source)

                override fun newArray(size: Int): Array<RunnerResultView?> = arrayOfNulls(size)
            }
    }
}