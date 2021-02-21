package com.jessica.yourfavoritemovies.ui.authentication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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

    fun registerUser(email: String, password: String) = auth.run {
        loading.value = true
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onComplete(task) }
    }

    fun logInUser(email: String, password: String) = auth.run {
        loading.value = true
        signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onComplete(task) }
    }

    private fun onComplete(task: Task<AuthResult>) {
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

    private fun errorMessage() {
        error.value = "Something was wrong!!!"
    }

    fun loadLoggedUser() {
        auth.currentUser?.let { user ->
            MovieUtil.saveUserId(getApplication(), user.uid)
            stateLogin.value = true
        }
    }

}