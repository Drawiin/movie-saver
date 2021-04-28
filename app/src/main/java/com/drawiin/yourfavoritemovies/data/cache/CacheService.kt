package com.drawiin.yourfavoritemovies.data.cache

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import javax.inject.Inject

class CacheService @Inject constructor(
    private val context: Application
) {
    fun saveCurrentProfileUid(uid: String?) {
        val preferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
        preferences.edit {
            putString(UUID_KEY, uid)
        }
    }

    fun getCurrentProfileUid(): String? {
        val preferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
        return preferences.getString(UUID_KEY, EMPTY_STRING)
    }

    companion object {
        private const val APP_KEY = "APP"
        private const val UUID_KEY = "UUID"
        private const val EMPTY_STRING = ""
    }
}