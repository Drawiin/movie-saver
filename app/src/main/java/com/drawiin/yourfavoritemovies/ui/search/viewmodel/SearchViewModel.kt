package com.drawiin.yourfavoritemovies.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileInfo
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileUid
import com.drawiin.yourfavoritemovies.domain.interactor.SearchMovies
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
class SearchViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: DatabaseReference,
    private val searchMovies: SearchMovies,
    private val getCurrentProfileUid: GetCurrentProfileUid,
    private val getCurrentProfileInfo: GetCurrentProfileInfo
) : ViewModel() {
    var stateList: MutableLiveData<SingleLiveEvent<Flow<PagingData<Movie>>>> = MutableLiveData()
    var message: MutableLiveData<SingleLiveEvent<String>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateFavorite: MutableLiveData<Movie> = MutableLiveData()

    private val currentProfileId by lazy {
        getCurrentProfileUid.run()
    }

    private val databaseRef = auth.currentUser?.uid?.let { uid ->
        database.child("users").child(uid).child("profiles")
    }

    fun searchMovies(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading.postValue(true)
            delay(1000L)
            try {
                stateList.postValue(SingleLiveEvent(searchMovies.run(query)))
            } catch (ex: Exception) {
                showMessage("Opa temos um problema tente novamente mais tarde")
            } finally {
                loading.postValue(false)
            }
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
        databaseRef?.get()?.addOnSuccessListener { snapshot ->
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

    private fun showMessage(message: String) {
        this.message.value = SingleLiveEvent(message)
    }
}


