package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import android.os.Parcelable
import com.gmail.maystruks08.nfcruntracker.ui.views.stepview.Bean
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class CheckpointView(
    val id: Int,
    val bean: Bean,
    var date: Date? = null
) : Parcelable