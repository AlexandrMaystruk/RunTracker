package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views

import android.os.Parcel
import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.RunnerListAdapter

class RunnerResultView(
    val runnerNumber: String,
    val runnerFullName: String,
    val runnerResultTime: String,
    val position: Int
) : BaseRunnerView, Parcelable {

    override val type: Int = RunnerListAdapter.RESULT

    override fun isItemTheSame(other: BaseRunnerView?): Boolean {
        return type == other?.type && runnerNumber == (other as? RunnerResultView)?.runnerNumber
    }

    override fun isContentTheSame(other: BaseRunnerView?): Boolean {
        return this == (other as? RunnerResultView)
    }

    constructor(source: Parcel) : this(
        runnerNumber = source.readString().orEmpty(),
        runnerFullName = source.readString().orEmpty(),
        runnerResultTime = source.readString().orEmpty(),
        position = source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(runnerNumber)
        writeString(runnerFullName)
        writeString(runnerResultTime)
        writeInt(position)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RunnerResultView

        if (runnerNumber != other.runnerNumber) return false
        if (runnerFullName != other.runnerFullName) return false
        if (runnerResultTime != other.runnerResultTime) return false
        if (position != other.position) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = runnerNumber.hashCode()
        result = 31 * result + runnerFullName.hashCode()
        result = 31 * result + runnerResultTime.hashCode()
        result = 31 * result + position
        result = 31 * result + type
        return result
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