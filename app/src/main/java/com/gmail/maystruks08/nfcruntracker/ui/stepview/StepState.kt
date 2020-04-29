package com.gmail.maystruks08.nfcruntracker.ui.stepview

 enum class StepState {

    UNDONE, DONE, CURRENT, DONE_WARNING;

    companion object {

        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: UNDONE
    }
}