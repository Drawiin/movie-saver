package com.drawiin.yourfavoritemovies.ui.authentication.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.R
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.domain.models.User
import com.drawiin.yourfavoritemovies.ui.utils.architecture.SingleLiveEvent
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    application: Application,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) : AndroidViewModel(application) {
    val passwordLoading: MutableLiveData<Boolean> = MutableLiveData()
    val googleLoading: MutableLiveData<Boolean> = MutableLiveData()
    val facebookLoading: MutableLiveData<Boolean> = MutableLiveData()

    val error: MutableLiveData<String> = MutableLiveData()
    val stateRegister: MutableLiveData<Boolean> = MutableLiveData()
    val stateLogin: MutableLiveData<SingleLiveEvent<Boolean>> = MutableLiveData()

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

    fun registerUser(email: String, password: String, name: String, birth: String) = auth.run {
        passwordLoading.value = true
        createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task -> onEmailAuthComplete(task, name, birth) }
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
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        errorMessage("Já existe um usuário cadastrado com esse email")
                    } else {
                        errorMessage()
                    }
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

    private fun onEmailAuthComplete(
        task: Task<AuthResult>,
        name: String? = null,
        birth: String? = null
    ) {
        when {
            task.isSuccessful -> auth.currentUser?.uid?.let {
                onUserAuthenticate(it, name, birth)
            }
            else -> {
                errorMessage()
            }
        }
    }

    private fun errorMessage(message: String = "Houve um erro ao realizar login verifique as credencias") {
        error.value = message
    }

    private fun onUserAuthenticate(uid: String, name: String? = null, birth: String? = null) {
        val databaseRef = database.child("users").child(uid)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user == null) {
                    val key = databaseRef.push().key
                    databaseRef.setValue(
                        User(
                            name = auth.currentUser?.displayName ?: name ?: "Padrão",
                            uid = uid,
                            profiles = listOf(
                                Profile(
                                    id = key.toString(),
                                    name = auth.currentUser?.displayName ?: name ?: "Padrão",
                                    watchList = emptyList(),
                                    watchedMovies = emptyList()
                                )
                            ),
                            birth = birth ?: "11/01/2001",
                            email = auth.currentUser?.email ?: ""
                        )
                    )
                }
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
        if (auth.currentUser != null) {
            stateLogin.value = SingleLiveEvent(true)
        }
    }

    private fun onAuthSuccess() {
        stateLogin.value = SingleLiveEvent(true)
        stateRegister.value = true
    }

    companion object {
        const val TAG = "FACEBOOK_TAG"
    }
}