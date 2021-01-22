package com.jessica.yourfavoritemovies.ui.authentication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.jessica.yourfavoritemovies.utils.MovieUtil

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val stateRegister: MutableLiveData<Boolean> = MutableLiveData()
    val stateLogin: MutableLiveData<Boolean> = MutableLiveData()

    private var auth: FirebaseAuth = Firebase.auth

    fun registerUser(email: String, password: String) {
        loading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading.value = false
                when {
                    task.isSuccessful -> {
                        MovieUtil.saveUserId(getApplication(), auth.currentUser?.uid)
                        stateRegister.value = true
                    }
                    else -> {
                        errorMessage()
                        stateRegister.value = false
                        loading.value = false
                    }
                }
            }
    }

    fun logInUser(email: String, password: String) {
        loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loading.value = false
                when {
                    task.isSuccessful -> {
                        MovieUtil.saveUserId(getApplication(), auth.currentUser?.uid)
                        stateLogin.value = true
                    }
                    else -> {
                        errorMessage()
                        stateLogin.value = false
                    }
                }
            }
    }


    private fun errorMessage() {
        error.value = "Something was wrong!!!"
    }

}