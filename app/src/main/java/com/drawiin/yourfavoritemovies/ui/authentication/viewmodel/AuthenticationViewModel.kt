package com.drawiin.yourfavoritemovies.ui.authentication.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.domain.models.User
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.drawiin.yourfavoritemovies.utils.SingleLiveEvent
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
    val passwordLoading: MutableLiveData<Boolean> = MutableLiveData()
    val googleLoading: MutableLiveData<Boolean> = MutableLiveData()
    val facebookLoading: MutableLiveData<Boolean> = MutableLiveData()

    val error: MutableLiveData<String> = MutableLiveData()
    val stateRegister: MutableLiveData<Boolean> = MutableLiveData()
    val stateLogin: MutableLiveData<SingleLiveEvent<Boolean>> = MutableLiveData()

    private val auth: FirebaseAuth = Firebase.auth
    private val database = Firebase.database.reference

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

    fun showGoogleLoading() {
        googleLoading.value = true
    }

    fun showFacebookLoading() {
        facebookLoading.value = true
    }

    fun hideAllLoadings() {
        googleLoading.value = false
        facebookLoading.value = false
        passwordLoading.value = false
    }

    fun registerUser(email: String, password: String) = auth.run {
        passwordLoading.value = true
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onEmailAuthComplete(task) }
    }

    fun logInUser(email: String, password: String) = auth.run {
        passwordLoading.value = true
        signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onEmailAuthComplete(task) }
    }

    fun firebaseAuthWithGoogle(
        account: GoogleSignInAccount?,
        activity: Activity
    ) = account?.idToken?.let { token ->
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                when {
                    task.isSuccessful -> {
                        onGoogleAuthComplete(auth.currentUser?.uid)
                    }
                    else -> {
                        errorMessage()
                    }
                }
            }
    }

    fun firebaseAuthWithFacebook(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    Log.d(TAG, "signInWithCredential:success")
                    auth.currentUser?.uid?.let {
                        onUserAuthenticate(it)
                    }
                }
                else -> {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    errorMessage()
                    hideAllLoadings()
                }
            }
        }


    }

    private fun onGoogleAuthComplete(uid: String?) {
        uid?.let {
            onUserAuthenticate(it)
        }
    }

    private fun onEmailAuthComplete(task: Task<AuthResult>) {
        when {
            task.isSuccessful -> auth.currentUser?.uid?.let {
                onUserAuthenticate(it)
            }
            else -> {
                errorMessage()
            }
        }
    }

    private fun errorMessage() {
        error.value = "Something was wrong!!!"
    }

    private fun onUserAuthenticate(uid: String) {
        val databaseRef = database.child("users").child(uid)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user == null) {
                    val key  = databaseRef.push().key
                    databaseRef.setValue(
                        User(
                            name = auth.currentUser?.displayName ?: "Padrão",
                            uid = uid,
                            profiles = listOf(
                                Profile(
                                    id = key.toString(),
                                    name = auth.currentUser?.displayName ?: "Padrão",
                                    watchList = emptyList(),
                                    watchedMovies = emptyList()
                                )
                            ),
                            birth = "11/01/2001",
                            email = auth.currentUser?.email ?: ""
                        )
                    )
                }
                MovieUtil.saveUserId(getApplication(), uid)
                hideAllLoadings()
                onAuthSuccess()
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage()
                hideAllLoadings()
            }

        })
    }

    fun loadCurrentUser() {
        Log.d("AUTH_VIEWMODEL", "onAuth sucess")
        if (auth.currentUser != null){
            MovieUtil.saveUserId(getApplication(), auth.currentUser?.uid)
            stateLogin.value = SingleLiveEvent(true)
        }
    }

    private fun onAuthSuccess() {
        stateLogin.value =  SingleLiveEvent(true)
        stateRegister.value = true
    }

    companion object {
        const val TAG = "FACEBOOK_TAG"
    }
}