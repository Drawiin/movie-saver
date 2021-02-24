package com.drawiin.yourfavoritemovies.ui.authentication.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationViewModel(application: Application) : AndroidViewModel(application) {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val error: MutableLiveData<String> = MutableLiveData()
    val stateRegister: MutableLiveData<Boolean> = MutableLiveData()
    val stateLogin: MutableLiveData<Boolean> = MutableLiveData()

    private val auth: FirebaseAuth = Firebase.auth

    val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).apply {
            requestEmail()
            requestIdToken(
                getApplication<Application>().getString(R.string.default_web_client_id)
            )
        }.build()
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

    fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount?,
        activity: Activity
    ) = account?.idToken?.let { token ->
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                when {
                    task.isSuccessful -> onGoogleLoginSuccess(auth.currentUser?.uid)
                    else -> errorMessage()
                }
            }
    }


    private fun onGoogleLoginSuccess(uid: String?) {
        MovieUtil.saveUserId(getApplication(), uid)
        stateLogin.value = true
        stateRegister.value = true
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