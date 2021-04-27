package com.drawiin.yourfavoritemovies.ui.whatched.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WatchedViewModel @Inject constructor(
    application: Application,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
) : AndroidViewModel(application) {

    var stateList: MutableLiveData<List<Movie>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val currentProfileId by lazy {
        MovieUtil.getProfileId(getApplication())
    }

    private val databaseRef by lazy {
        database
            .child("users")
            .child(auth.currentUser?.uid.toString())
            .child("profiles")
    }

    init {
        loading.value = true
        loadWatchMovies()
    }

    private fun loadWatchMovies() {
        getCurrentProfileInfo { profile ->
            stateList.value = profile.watchedMovies
            loading.value = false
        }
    }


    private fun getCurrentProfileInfo(onProfileReceived: (Profile) -> Unit) {
        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot
                .children
                .map { it.getValue(Profile::class.java) }
                .findLast { it?.id == currentProfileId }
                ?.let { onProfileReceived(it) }

        }
    }

}