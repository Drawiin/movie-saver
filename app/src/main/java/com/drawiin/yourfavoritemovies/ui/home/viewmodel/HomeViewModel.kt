package com.drawiin.yourfavoritemovies.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.domain.interactor.GetMovies
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.utils.SingleLiveEvent
import com.drawiin.yourfavoritemovies.utils.Constants.FAVORITES_PATH
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getMovies: GetMovies
) : AndroidViewModel(application) {


    var stateList: MutableLiveData<SingleLiveEvent<Flow<PagingData<Movie>>>> = MutableLiveData()
    var error: MutableLiveData<SingleLiveEvent<String>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateFavorite: MutableLiveData<Movie> = MutableLiveData()

    private val path by lazy {
        MovieUtil.getUserId(getApplication()).toString() + FAVORITES_PATH
    }

    private val databaseRef by lazy {
        Firebase.database.getReference(path)
    }

    init {
        getListMovies()
    }

    fun saveFavorite(movie: Movie) = databaseRef.apply {
        orderByKey()
        addListenerForSingleValueEvent(getSaveFavoriteListener(movie))
    }

    private fun getListMovies() = viewModelScope.launch(Dispatchers.IO) {
        loading.postValue(true)
        delay(2000L)
        try {
            stateList.postValue(SingleLiveEvent(getMovies.run()))
        } catch (ex: Exception) {
            errorMessage("Opa temos um problema tente novamente mais tarde")
        } finally {
            loading.postValue(false)
        }
    }

    private fun saveMovieFavorite(movie: Movie) = databaseRef.push().key?.let { key ->
        databaseRef.child(key).apply {
            setValue(movie)
            addListenerForSingleValueEvent(getSaveFavoriteMovieListener())
        }

    }

    private fun getSaveFavoriteListener(movie: Movie) = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var movieAdded = false
            snapshot.children.forEach { snapResult ->
                snapResult.getValue(Movie::class.java)?.id?.let { id ->
                    when (id) {
                        movie.id -> {
                            movieAdded = true
                        }
                    }
                }
            }

            when {
                movieAdded -> {
                    errorMessage("O Filme já foi adicionado")
                }
                else -> {
                    saveMovieFavorite(movie)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {}
    }

    private fun getSaveFavoriteMovieListener() = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            errorMessage(error.message)
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            stateFavorite.value = snapshot.getValue(Movie::class.java)
        }

    }

    private fun errorMessage(message: String) {
        error.value = SingleLiveEvent(message)
    }
}


