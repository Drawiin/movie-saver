package com.drawiin.yourfavoritemovies.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseServiceModule {
    @Singleton
    @Provides
    fun providesAuthService(): FirebaseAuth{
        return Firebase.auth
    }

    @Singleton
    @Provides
    fun providesDatabaseService(): DatabaseReference {
        return Firebase.database.reference
    }
}