package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
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
) : Parcelable{

    companion object{
        fun getPlaceholder() =  arrayListOf(
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
            RunnerView("",  0, "", "", null, "", 1, emptyList(), isOffTrack = false, placeholder = true),
        )
    }
}