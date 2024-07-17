package com.drawiin.yourfavoritemovies.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import javax.inject.Inject

interface RemoteConfig {
    fun getApiKey(): String
}

class RemoteConfigImpl @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
): RemoteConfig {
    override fun getApiKey(): String {
        return firebaseRemoteConfig.getString("API_KEY")
    }

}