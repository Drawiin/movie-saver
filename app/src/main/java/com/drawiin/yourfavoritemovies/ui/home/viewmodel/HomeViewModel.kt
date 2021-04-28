package com.drawiin.yourfavoritemovies.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileInfo
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileUid
import com.drawiin.yourfavoritemovies.domain.interactor.GetMovies
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.ui.utils.architecture.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getMovies: GetMovies,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
    private val getCurrentProfileUid: GetCurrentProfileUid,
    private val getCurrentProfileInfo: GetCurrentProfileInfo
) : AndroidViewModel(application) {
    var stateList: MutableLiveData<SingleLiveEvent<Flow<PagingData<Movie>>>> = MutableLiveData()
    var message: MutableLiveData<SingleLiveEvent<String>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    val currentProfile = MutableLiveData<Profile>()

    private val currentProfileId by lazy {
        getCurrentProfileUid.run()
    }

    private val databaseRef by lazy {
        database
            .child("users")
            .child(auth.currentUser?.uid.toString())
            .child("profiles")
    }

    init {
        getListMovies()
        getCurrentProfileInfo.run {
            currentProfile.value = it
        }
    }

    fun saveToWatchList(movie: Movie) = getCurrentProfileInfo.run { profile ->
        profile
            .watchList
            .any { it.id == movie.id }
            .takeUnless { it }
            ?.let { saveMovieOnWatchList(movie, profile.watchList) }
    }

    private fun saveMovieOnWatchList(movie: Movie, watchList: List<Movie>) {
        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot.children
                .map { it.getValue(Profile::class.java) }
                .indexOfLast { it?.id == currentProfileId }
                .let { databaseRef.child(it.toString()).child("watchList") }
                .setValue(watchList.plus(movie))
                .addOnSuccessListener {
                    showMessage("Filme ${movie.title} adicionado a lista para assistir")
                }
        }
    }


    private fun getListMovies() = viewModelScope.launch(Dispatchers.IO) {
        loading.postValue(true)
        try {
            stateList.postValue(SingleLiveEvent(getMovies.run()))
        } catch (ex: Exception) {
            showMessage("Opa temos um problema tente novamente mais tarde")
        } finally {
            delay(1000L)
            loading.postValue(false)
        }
    }


    private fun showMessage(message: String) {
        this.message.value = SingleLiveEvent(message)
    }
}



