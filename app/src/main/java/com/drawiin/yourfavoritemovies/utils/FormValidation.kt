package com.drawiin.yourfavoritemovies.utils

import java.util.regex.Pattern

object FormValidation {
    val EMAIL = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun validateNameEmailPassword(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                false
            }

            !EMAIL.matcher(email).matches() -> {
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

            !EMAIL.matcher(email).matches() -> {
                false
            }

            password.length < 6 -> {
                false
            }

            else -> true
        }
    }
}