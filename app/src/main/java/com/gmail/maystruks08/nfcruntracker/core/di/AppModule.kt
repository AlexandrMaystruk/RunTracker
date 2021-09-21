package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import androidx.room.Room
import com.gmail.maystruks08.data.LogHelperImpl
import com.gmail.maystruks08.data.local.AppDatabase
import com.gmail.maystruks08.data.local.dao.*
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.data.remote.ApiImpl
import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.domain.DATA_TIME_FORMAT
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.domain.entities.checkpoint.Checkpoint
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointImpl
import com.gmail.maystruks08.domain.entities.checkpoint.CheckpointResultIml
import com.gmail.maystruks08.domain.entities.runner.IRunner
import com.gmail.maystruks08.domain.entities.runner.Runner
import com.gmail.maystruks08.domain.entities.runner.Team
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.utils.NetworkUtilImpl
import com.gmail.maystruks08.nfcruntracker.workers.SyncRunnersDataSchedulerImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNetworkUtil(impl: NetworkUtilImpl): NetworkUtil

    @Binds
    @Singleton
    abstract fun log(logHelper: LogHelperImpl): LogHelper

    @Binds
    @Singleton
    abstract fun provideSyncRunnersDataScheduler(syncRunnersDataSchedulerImpl: SyncRunnersDataSchedulerImpl): SyncRunnersDataScheduler

}


@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        gso: GoogleSignInOptions
    ): GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun appDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "run_tracker_db").fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun  provideRaceDao(appDatabase: AppDatabase): RaceDAO = appDatabase.raceDao()

    @Provides
    @Singleton
    fun  provideDistanceDao(appDatabase: AppDatabase): DistanceDAO = appDatabase.distanceDao()

    @Provides
    @Singleton
    fun  provideRunnerDao(appDatabase: AppDatabase): RunnerDao = appDatabase.runnerDao()

    @Provides
    @Singleton
    fun provideCheckpointDao(appDatabase: AppDatabase): CheckpointDAO = appDatabase.checkpointDao()

    @Provides
    @Singleton
    fun provideUserSettingsDao(appDatabase: AppDatabase): UserSettingsDAO =
        appDatabase.userSettingsDAO()

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(IRunner::class.java)
                .registerSubtype(Runner::class.java)
                .registerSubtype(Team::class.java)
        )

        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory.of(Checkpoint::class.java)
                .registerSubtype(CheckpointImpl::class.java)
                .registerSubtype(CheckpointResultIml::class.java)
        )
        .setDateFormat(DATA_TIME_FORMAT)
        .create()

}

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun firebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


    @Provides
    @Singleton
    fun api(firebaseFirestore: FirebaseFirestore): Api = ApiImpl(firebaseFirestore)
}
