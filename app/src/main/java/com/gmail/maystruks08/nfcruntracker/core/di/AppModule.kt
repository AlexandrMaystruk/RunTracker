package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import androidx.room.Room
import com.gmail.maystruks08.data.LogHelperImpl
import com.gmail.maystruks08.data.local.AppDatabase
import com.gmail.maystruks08.data.local.dao.CheckpointDAO
import com.gmail.maystruks08.data.local.dao.DistanceDAO
import com.gmail.maystruks08.data.local.dao.RaceDAO
import com.gmail.maystruks08.data.local.dao.RunnerDao
import com.gmail.maystruks08.data.remote.Api
import com.gmail.maystruks08.data.remote.ApiImpl
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.data.remote.FirestoreApiImpl
import com.gmail.maystruks08.data.repository.SyncRunnersDataScheduler
import com.gmail.maystruks08.domain.LogHelper
import com.gmail.maystruks08.domain.NetworkUtil
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.utils.NetworkUtilImpl
import com.gmail.maystruks08.nfcruntracker.workers.SyncRunnersDataSchedulerImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun gson(): Gson = Gson()
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun firebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun firestoreApi(firebaseFirestore: FirebaseFirestore): FirestoreApi =
        FirestoreApiImpl(firebaseFirestore)

    @Provides
    @Singleton
    fun api(firebaseFirestore: FirebaseFirestore): Api = ApiImpl(firebaseFirestore)
}
