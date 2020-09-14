package com.gmail.maystruks08.nfcruntracker.ui.runners.paged

import androidx.paging.DataSource
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.RunnerView
import com.gmail.maystruks08.nfcruntracker.ui.viewmodels.toRunnerView

class RunnersDataSourceFactory (private val dataSource: RunnerDataSource) : DataSource.Factory<String, RunnerView>() {

    fun refreshData(){
        dataSource.invalidate()
    }

    override fun create(): DataSource<String, RunnerView> {
        return dataSource.map { it.toRunnerView() }
    }
}