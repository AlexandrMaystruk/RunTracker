package com.gmail.maystruks08.nfcruntracker.ui.view_models

import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item
import com.gmail.maystruks08.nfcruntracker.ui.views.Bean
import java.util.*

data class EditCheckpointView(
    val id: String,
    val title: String,
    val position: CheckpointPosition
) : Item


data class CheckpointView/**/(
    val id: String,
    val title: String,
    val position: CheckpointPosition,
    val titlePaintFlag: Int,
    val bean: Bean,
    var date: Date? = null
) : Item


sealed class CheckpointPosition {
    object Start : CheckpointPosition()
    object Center : CheckpointPosition()
    object End : CheckpointPosition()
}