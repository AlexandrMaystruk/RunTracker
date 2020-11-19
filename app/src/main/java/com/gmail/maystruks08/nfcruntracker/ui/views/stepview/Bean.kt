package com.gmail.maystruks08.nfcruntracker.ui.views.stepview

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bean(val title: String, var state: StepState) : Parcelable