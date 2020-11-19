package com.gmail.maystruks08.nfcruntracker.core.di

import android.content.Context
import com.gmail.maystruks08.nfcruntracker.R
import com.gmail.maystruks08.nfcruntracker.ui.login.LogOutUseCase
import com.gmail.maystruks08.nfcruntracker.ui.login.LogOutUseCaseImpl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class AuthModule {

    companion object {
        @JvmStatic
        @Provides
        @Singleton
        fun provideGoogleSignInOptions(context: Context): GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        @JvmStatic
        @Provides
        @Singleton
        fun provideGoogleSignInClient(
            context: Context,
            gso: GoogleSignInOptions
        ): GoogleSignInClient =
            GoogleSignIn.getClient(context, gso)

        @JvmStatic
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    }

    @Binds
    @Singleton
    abstract fun provideLogoutUseCase(logOutUseCase: LogOutUseCaseImpl): LogOutUseCase

}