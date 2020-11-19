package com.gmail.maystruks08.nfcruntracker.core.di.host

import androidx.lifecycle.ViewModel
import com.gmail.maystruks08.data.remote.FirestoreApi
import com.gmail.maystruks08.data.remote.FirestoreApiImpl
import com.gmail.maystruks08.data.repository.RunnerDataChangeListener
import com.gmail.maystruks08.data.repository.RunnersRepositoryImpl
import com.gmail.maystruks08.domain.repository.RunnersRepository
import com.gmail.maystruks08.nfcruntracker.HostViewModel
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelKey
import com.gmail.maystruks08.nfcruntracker.core.di.viewmodel.ViewModelModule
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
abstract class HostModule {

    @IntoMap
    @Binds
    @HostScope
    @ViewModelKey(HostViewModel::class)
    abstract fun bindRunnersViewModel(viewModel: HostViewModel): ViewModel

    @Binds
    @HostScope
    abstract fun bindRunnerDataChangeListener(viewModel: RunnersRepositoryImpl): RunnerDataChangeListener

    @Binds
    @HostScope
    abstract fun bindRunnersRepository(impl: RunnersRepositoryImpl): RunnersRepository

    companion object{
        @JvmStatic
        @Provides
        @HostScope
        fun firebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @JvmStatic
        @Provides
        @HostScope
        fun firestoreApi(firebaseFirestore: FirebaseFirestore): FirestoreApi = FirestoreApiImpl(firebaseFirestore)
    }

}