package com.gmail.maystruks08.nfcruntracker.ui.view_models

import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item

data class RaceView(
    val id: String,
    val name: String,
    val firstDistanceId: String?
) : Item