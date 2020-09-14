package com.gmail.maystruks08.nfcruntracker.ui.runners.paged

import androidx.paging.ItemKeyedDataSource
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toRunner
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType

class RunnerDataSource(private val runnerDao: RunnerDao, private val settingsCache: SettingsCache) : ItemKeyedDataSource<String, Runner>() {

    var selectedRunnerType = RunnerType.NORMAL

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<Runner>
    ) {
        val items = runnerDao.getRunnersWithResults(
            type = selectedRunnerType.ordinal,
            requestedLoadSize = params.requestedLoadSize
        )
        callback.onResult(items.map { it.toRunner(settingsCache.getCheckpointList(selectedRunnerType)) })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Runner>) {
        //TODO implement
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Runner>) {
        val items = runnerDao.getRunnersWithResultsAfter(
            type = selectedRunnerType.ordinal,
            key = params.key,
            requestedLoadSize = params.requestedLoadSize
        )
        callback.onResult(items.map { it.toRunner(settingsCache.getCheckpointList(selectedRunnerType)) })
    }

    override fun getKey(item: Runner): String {
        return item.fullName
    }

}