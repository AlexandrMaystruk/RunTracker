package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcel
import android.os.Parcelable

data class RunnerView(
    val cardId: String,
    val number: Int,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val type: Int,
    val progress: List<CheckpointView>,
    val isOffTrack: Boolean,
    val placeholder: Boolean = false
) : Parcelable {

    constructor(source: Parcel) : this(
        source.readString().orEmpty(),
        source.readInt(),
        source.readString().orEmpty(),
        source.readString().orEmpty(),
        source.readString(),
        source.readString().orEmpty(),
        source.readInt(),
        listOf<CheckpointView>().apply { source.readList(this, CheckpointView::class.java.classLoader) },
        1 == source.readInt(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(cardId)
        writeInt(number)
        writeString(fullName)
        writeString(city)
        writeString(result)
        writeString(dateOfBirthday)
        writeInt(type)
        writeList(progress)
        writeInt((if (isOffTrack) 1 else 0))
        writeInt((if (placeholder) 1 else 0))
    }

    companion object{
        fun getPlaceholder() =  arrayListOf(
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
        )

        @JvmField
        val CREATOR: Parcelable.Creator<RunnerView> = object : Parcelable.Creator<RunnerView> {
            override fun createFromParcel(source: Parcel): RunnerView = RunnerView(source)
            override fun newArray(size: Int): Array<RunnerView?> = arrayOfNulls(size)
        }
    }
}