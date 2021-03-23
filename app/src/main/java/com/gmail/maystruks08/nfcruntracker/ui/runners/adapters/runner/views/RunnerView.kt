package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.RunnerListAdapter
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.CheckpointView

data class RunnerView(
    val cardId: String?,
    val number: Long,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val actualDistanceId: String,
    val progress: List<CheckpointView>,
    val isOffTrack: Boolean,
    val placeholder: Boolean = false
) :BaseRunnerView,  Parcelable {

    override val type: Int = RunnerListAdapter.RUNNER

    override fun isItemTheSame(other: BaseRunnerView?): Boolean {
        return type == other?.type && number == (other as? RunnerView)?.number
    }

    override fun isContentTheSame(other: BaseRunnerView?): Boolean {
        return this == (other as? RunnerView)
    }
    constructor(source: Parcel) : this(
        source.readString().orEmpty(),
        source.readLong(),
        source.readString().orEmpty(),
        source.readString().orEmpty(),
        source.readString(),
        source.readString().orEmpty(),
        source.readString().orEmpty(),
        listOf<CheckpointView>().apply { source.readList(this, CheckpointView::class.java.classLoader) },
        1 == source.readInt(),
        1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(cardId)
        writeLong(number)
        writeString(fullName)
        writeString(city)
        writeString(result)
        writeString(dateOfBirthday)
        writeString(actualDistanceId)
        writeList(progress)
        writeInt((if (isOffTrack) 1 else 0))
        writeInt((if (placeholder) 1 else 0))
    }

    companion object{

        @JvmField
        val CREATOR: Parcelable.Creator<RunnerView> = object : Parcelable.Creator<RunnerView> {
            override fun createFromParcel(source: Parcel): RunnerView = RunnerView(source)
            override fun newArray(size: Int): Array<RunnerView?> = arrayOfNulls(size)
        }
    }
}