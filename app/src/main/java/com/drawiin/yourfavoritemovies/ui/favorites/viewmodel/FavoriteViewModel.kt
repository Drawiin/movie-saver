package com.drawiin.yourfavoritemovies.ui.favorites.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileInfo
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileUid
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
    private val getCurrentProfileUid: GetCurrentProfileUid,
    private val getCurrentProfileInfo: GetCurrentProfileInfo
) : ViewModel() {

    var stateMovedToWatchedMovies: MutableLiveData<Movie> = MutableLiveData()
    var stateList: MutableLiveData<List<Movie>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val currentProfileId by lazy {
        getCurrentProfileUid.run()
    }

    private val databaseRef by lazy {
        database
            .child("users")
            .child(auth.currentUser?.uid.toString())
            .child("profiles")
    }

    fun init() {
        loading.value = true
        loadWatchList()
    }

    fun loadWatchList() {
        getCurrentProfileInfo.run { profile ->
            stateList.value = profile.watchList
            loading.value = false
        }
    }

    fun moveToWatchedMovies(movieToBeMoved: Movie) = getCurrentProfileInfo.run { profile ->
        profile
            .watchedMovies
            .any { it.id == movieToBeMoved.id }
            .takeUnless { it }
            ?.let {
                confirmMoveToWatchedMovies(
                    movieToBeMoved,
                    profile.watchedMovies,
                    profile.watchList
                )
            }
    }

    private fun confirmMoveToWatchedMovies(
        movieToBeMoved: Movie,
        watchedMovies: List<Movie>,
        watchList: List<Movie>
    ) {
        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot.children
                .map { profileSnapshot -> profileSnapshot.getValue(Profile::class.java) }
                .indexOfLast { profile -> profile?.id == currentProfileId }
                .let { profileId -> databaseRef.child(profileId.toString()) }
                .let { ref ->
                    ref.child("watchedMovies")
                        .setValue(watchedMovies.plus(movieToBeMoved))

                    ref.child("watchList")
                        .setValue(watchList.filter { it.id != movieToBeMoved.id })
                        .addOnSuccessListener {
                            loadWatchList()
                            stateMovedToWatchedMovies.value = movieToBeMoved
                        }
                }

        }
    }
}