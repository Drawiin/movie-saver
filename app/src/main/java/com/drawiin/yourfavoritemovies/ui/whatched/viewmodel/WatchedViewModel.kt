package com.drawiin.yourfavoritemovies.ui.whatched.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.drawiin.yourfavoritemovies.domain.interactor.GetCurrentProfileInfo
import com.drawiin.yourfavoritemovies.domain.models.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WatchedViewModel @Inject constructor(
    private val getCurrentProfileInfo: GetCurrentProfileInfo
) : ViewModel() {

    var stateList: MutableLiveData<List<Movie>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()


    init {
        loading.value = true
        loadWatchMovies()
    }

    private fun loadWatchMovies() {
        getCurrentProfileInfo.run { profile ->
            stateList.value = profile.watchedMovies
            loading.value = false
        }
    }

}