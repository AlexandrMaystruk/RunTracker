package com.gmail.maystruks08.data.repository

import android.database.sqlite.SQLiteConstraintException
import com.gmail.maystruks08.data.cache.RunnersCache
import com.gmail.maystruks08.data.cache.SettingsCache
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.mappers.toCheckpointsResult
import com.gmail.maystruks08.data.mappers.toRunnerTable
import com.gmail.maystruks08.data.remote.FirestoreApi
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
    private val runnersCache: RunnersCache
) : RegisterNewRunnersRepository {

    override suspend  fun saveNewRunner(runner: Runner) {
        try {
            runnerDao.insertRunner(runner.toRunnerTable(), runner.checkpoints.toCheckpointsResult(runnerId = runner.id))
            runnersCache.getRunnerList(runner.type).add(runner)
            firestoreApi.updateRunner(runner)
        } catch (e: SQLiteConstraintException) {
            throw RunnerWithIdAlreadyExistException()
        } catch (e: Exception) {
            runnerDao.markAsNeedToSync(runnerId = runner.id, needToSync = true)
            throw e
        }
    }

    override suspend fun getCheckpoints(type: RunnerType): List<Checkpoint> = settingsCache.getCheckpointList(type)

}