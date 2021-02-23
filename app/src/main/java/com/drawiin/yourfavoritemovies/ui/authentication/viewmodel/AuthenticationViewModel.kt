package com.drawiin.yourfavoritemovies.ui.authentication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val stateRegister: MutableLiveData<Boolean> = MutableLiveData()
    val stateLogin: MutableLiveData<Boolean> = MutableLiveData()

    private var auth: FirebaseAuth = Firebase.auth

    val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestEmail()
            .requestIdToken("690220572830-snka0oe8sidbka56iknr4j524j9vjaa6.apps.googleusercontent.com")
            .build()
    }

    fun registerUser(email: String, password: String) = auth.run {
        loading.value = true
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onAuthComplete(task) }
    }

    fun logInUser(email: String, password: String) = auth.run {
        loading.value = true
        signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onAuthComplete(task) }
    }

    private fun onAuthComplete(task: Task<AuthResult>) {
        loading.value = false
        when {
            task.isSuccessful -> {
                MovieUtil.saveUserId(getApplication(), auth.currentUser?.uid)
                stateLogin.value = true
                stateRegister.value = true
            }
            else -> {
                errorMessage()
                stateLogin.value = false
                stateRegister.value = true
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