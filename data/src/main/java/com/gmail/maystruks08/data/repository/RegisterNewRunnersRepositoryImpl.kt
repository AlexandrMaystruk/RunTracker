package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toCheckpointsResult
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.Checkpoint
import com.gmail.maystruks08.domain.entities.Runner
import com.gmail.maystruks08.domain.entities.RunnerType
import com.gmail.maystruks08.domain.exception.RunnerWithIdAlreadyExistException
import com.gmail.maystruks08.domain.repository.RegisterNewRunnersRepository
import javax.inject.Inject

class RegisterNewRunnersRepositoryImpl @Inject constructor(
    private val firestoreApi: FirestoreApi,
    private val runnerDao: RunnerDao,
    private val settingsCache: SettingsCache,
    private val runnersCache: RunnersCache,
    private val networkUtil: NetworkUtil
) : RegisterNewRunnersRepository {

    override suspend  fun saveNewRunners(runners: List<Runner>) {
        try {
            val runnersData = runners.map { it.toRunnerTable() to it.checkpoints.toCheckpointsResult(runnerId = it.id)  }
            runnerDao.insertRunners(runnersData)
            runners.forEach {
                runnersCache.getRunnerList(it.type).add(it)
                if(networkUtil.isOnline()) firestoreApi.updateRunner(it) else runnerDao.markAsNeedToSync(runnerId = it.id, needToSync = true)
            }
        } catch (e: SQLiteConstraintException) {
            throw RunnerWithIdAlreadyExistException()
        } catch (e: Exception) {
            runners.forEach { runnerDao.markAsNeedToSync(runnerId = it.id, needToSync = true) }
            throw e
        }
    }

    override suspend fun getCheckpoints(type: RunnerType): List<Checkpoint> = settingsCache.getCheckpointList(type)

}