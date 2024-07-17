package com.drawiin.yourfavoritemovies.ui

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieApplication @Inject constructor(): Application() {
    override fun onCreate() {
        Firebase.remoteConfig.fetchAndActivate()
        super.onCreate()
    }
}