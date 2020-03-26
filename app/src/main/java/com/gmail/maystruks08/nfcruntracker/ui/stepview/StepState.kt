package com.gmail.maystruks08.nfcruntracker.ui.stepview

 enum class StepState {

    UNDONE, DONE, CURRENT;

    companion object {

        fun fromOrdinal(ordinal: Int) = values().find { it.ordinal == ordinal } ?: UNDONE
    }
}