package com.gmail.maystruks08.nfcruntracker.ui.main.adapter.views

import com.gmail.maystruks08.nfcruntracker.ui.adapter.base.Item

interface RunnerScreenItem : Item {

    val id: String
}

interface RunnerDetailScreenItem : Item {

    val id: String

    fun isOffTrack(): Boolean
    fun isRunnerHasResult(): Boolean

}