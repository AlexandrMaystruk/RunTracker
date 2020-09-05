package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RunnerView(
    val id: String,
    val number: String,
    val fullName: String,
    val city: String,
    val result: String?,
    val dateOfBirthday: String,
    val type: Int,
    val progress: List<CheckpointView>,
    val isOffTrack: Boolean
) : Parcelable