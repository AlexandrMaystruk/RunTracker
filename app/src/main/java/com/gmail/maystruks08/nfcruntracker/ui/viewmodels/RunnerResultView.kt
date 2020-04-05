package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class RunnerResultView(
    val runnerId: String,
    val runnerFullName: String,
    val runnerNumber: String,
    val runnerResultTime: String,
    val position: Int
) : Parcelable