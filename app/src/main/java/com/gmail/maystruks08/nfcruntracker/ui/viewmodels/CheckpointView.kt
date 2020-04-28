package com.gmail.maystruks08.nfcruntracker.ui.viewmodels

import com.gmail.maystruks08.nfcruntracker.ui.stepview.StepState
import java.util.*

class CheckpointView(
    val id: Int,
    val name: String,
    var state: StepState,
    var date: Date? = null
)