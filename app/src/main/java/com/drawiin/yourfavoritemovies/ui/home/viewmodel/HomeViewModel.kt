package com.drawiin.yourfavoritemovies.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.drawiin.yourfavoritemovies.model.ApiMovie
import com.drawiin.yourfavoritemovies.model.SingleLiveEvent
import com.drawiin.yourfavoritemovies.repository.MovieRepository
import com.drawiin.yourfavoritemovies.utils.Constants
import com.drawiin.yourfavoritemovies.utils.Constants.FAVORITES_PATH
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository =
        MovieRepository()

    var stateList: MutableLiveData<SingleLiveEvent<List<ApiMovie>>> = MutableLiveData()
    var error: MutableLiveData<SingleLiveEvent<String>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateFavorite: MutableLiveData<ApiMovie> = MutableLiveData()

    private val path by lazy {
        MovieUtil.getUserId(getApplication()).toString() + FAVORITES_PATH
    }

    private val databaseRef by lazy {
        Firebase.database.getReference(path)
    }

    init {
        getListMovies(Constants.LANGUAGE_PT_BR)
    }


    fun saveFavorite(apiMovie: ApiMovie) = databaseRef.apply {
        orderByKey()
        addListenerForSingleValueEvent(getSaveFavoriteListener(apiMovie))
    }

    private fun getListMovies(language: String) = viewModelScope.launch {
        loading.value = true
        delay(2000L)
        try {
            stateList.value = SingleLiveEvent(repository.getMovies(language))
        } catch (ex: Exception) {
            errorMessage("It looks like we had a problem. Try later!")
        } finally {
            loading.value = false
        }
    }

    private fun saveMovieFavorite(apiMovie: ApiMovie) = databaseRef.push().key?.let { key ->
        databaseRef.child(key).apply {
            setValue(apiMovie)
            addListenerForSingleValueEvent(getSaveFavoriteMovieListener())
        }

    }

    private fun getSaveFavoriteListener(apiMovie: ApiMovie) = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var movieAdded = false
            snapshot.children.forEach { snapResult ->
                snapResult.getValue(ApiMovie::class.java)?.id?.let { id ->
                    when (id) {
                        apiMovie.id -> {
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
                    saveMovieFavorite(apiMovie)
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
            stateFavorite.value = snapshot.getValue(ApiMovie::class.java)
        }

    }

    private fun errorMessage(message: String) {
        error.value = SingleLiveEvent(message)
    }
}


