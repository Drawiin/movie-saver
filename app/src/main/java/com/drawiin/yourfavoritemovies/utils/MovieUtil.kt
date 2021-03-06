package com.drawiin.yourfavoritemovies.utils

import android.content.Context
import android.util.Patterns
import androidx.core.content.edit
import com.drawiin.yourfavoritemovies.utils.Constants.APP_KEY
import com.drawiin.yourfavoritemovies.utils.Constants.EMPTY_STRING
import com.drawiin.yourfavoritemovies.utils.Constants.UUID_KEY

object MovieUtil {
    fun saveUserId(context: Context, uiid: String?) {
        val preferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
        preferences.edit {
            putString(UUID_KEY, uiid)
        }
    }

    fun getUserId(context: Context): String? {
        val preferences = context.getSharedPreferences(APP_KEY, Context.MODE_PRIVATE)
        return preferences.getString(UUID_KEY, EMPTY_STRING)
    }

    fun validateNameEmailPassword(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                false
            }
            password.length < 6 -> {
                false
            }
            else -> true
        }
    }

    fun validateEmailPassword(email: String, password: String): Boolean {
        return when {
            email.isEmpty() || password.isEmpty() -> {
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                false
            }
            password.length < 6 -> {
                false
            }
            else -> true
        }
    }
}