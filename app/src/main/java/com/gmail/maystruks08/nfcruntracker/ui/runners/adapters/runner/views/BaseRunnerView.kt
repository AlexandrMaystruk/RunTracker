package com.gmail.maystruks08.nfcruntracker.ui.runners.adapters.runner.views

interface BaseRunnerView{

    val type: Int

    fun isItemTheSame(other: BaseRunnerView?): Boolean

    fun isContentTheSame(other: BaseRunnerView?): Boolean

}




