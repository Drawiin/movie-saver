package com.drawiin.yourfavoritemovies.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.drawiin.yourfavoritemovies.MovieRepository
import com.drawiin.yourfavoritemovies.model.SingleLiveEvent
import com.drawiin.yourfavoritemovies.model.Result
import com.drawiin.yourfavoritemovies.utils.Constants
import com.drawiin.yourfavoritemovies.utils.Constants.FAVORITES_PATH
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovieRepository()

    var stateList: MutableLiveData<List<Result>> = MutableLiveData()
    var error: MutableLiveData<SingleLiveEvent<String>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateFavorite: MutableLiveData<Result> = MutableLiveData()

    private val path by lazy {
        MovieUtil.getUserId(getApplication()).toString() + FAVORITES_PATH
    }

    private val databaseRef by lazy {
        Firebase.database.getReference(path)
    }

    init {
        getListMovies(Constants.LANGUAGE_PT_BR)
    }


    fun saveFavorite(result: Result) = databaseRef.apply {
        orderByKey()
        addListenerForSingleValueEvent(getSaveFavoriteListener(result))
    }

    private fun getListMovies(language: String) = viewModelScope.launch {
        loading.value = true
        delay(1000L) // TODO - For Better Showing The Animation
        try {
            val movieResult = withContext(Dispatchers.IO) {
                repository.getMovies(language)
            }
            stateList.value = movieResult.results
            loading.value = false
        } catch (ex: Exception) {
            errorMessage("It looks like we had a problem. Try later!")
        } finally {
            loading.value = false
        }
    }

    private fun saveMovieFavorite(result: Result) = databaseRef.push().key?.let { key ->
        databaseRef.child(key).apply {
            setValue(result)
            addListenerForSingleValueEvent(getSaveFavoriteMovieListener())
        }

    }

    private fun getSaveFavoriteListener(result: Result) = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            var movieAdded = false
            snapshot.children.forEach { snapResult ->
                snapResult.getValue(Result::class.java)?.id?.let { id ->
                    when (id) {
                        result.id -> {
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
                    saveMovieFavorite(result)
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
            stateFavorite.value = snapshot.getValue(Result::class.java)
        }

    }

    private fun errorMessage(message: String) {
        error.value = SingleLiveEvent(message)
    }
}

