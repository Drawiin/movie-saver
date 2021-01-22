package com.jessica.yourfavoritemovies.ui.home.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jessica.yourfavoritemovies.utils.Constants
import com.jessica.yourfavoritemovies.utils.Constants.FAVORITES_PATH
import com.jessica.yourfavoritemovies.MovieRepository
import com.jessica.yourfavoritemovies.utils.MovieUtil
import com.jessica.yourfavoritemovies.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepository()
    var stateList: MutableLiveData<List<Result>> = MutableLiveData()
    var error: MutableLiveData<String> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()
    var stateFavorite: MutableLiveData<Result> = MutableLiveData()

    init {
        getListMovies(Constants.LANGUAGE_PT_BR)
    }


    fun saveFavorite(result: Result) {
        val database = Firebase.database
        val reference =
            database.getReference(MovieUtil.getUserId(getApplication()).toString() + FAVORITES_PATH)

        reference.orderByKey().addListenerForSingleValueEvent(object : ValueEventListener {

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
                        saveMovieFavorite(reference, result)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun getListMovies(language: String) {
        viewModelScope.launch {
            loading.value = true
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
    }

    private fun saveMovieFavorite(reference: DatabaseReference, result: Result) {
        reference.push().key?.let { key ->
            reference.child(key).setValue(result)
            reference.child(key).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    errorMessage(error.message)
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    stateFavorite.value = snapshot.getValue(Result::class.java)
                }

            })
        }
    }

    private fun errorMessage(message: String) {
        error.value = message
    }
}


