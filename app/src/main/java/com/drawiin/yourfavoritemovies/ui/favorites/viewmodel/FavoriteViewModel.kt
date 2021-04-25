package com.drawiin.yourfavoritemovies.ui.favorites.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.utils.Constants
import com.drawiin.yourfavoritemovies.utils.Constants.ID_PATH
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    var stateRemoveFavorite: MutableLiveData<Movie> = MutableLiveData()
    var stateList: MutableLiveData<List<Movie>> = MutableLiveData()
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val favoritesPath by lazy {
        MovieUtil.getUserId(getApplication()).toString() + Constants.FAVORITES_PATH
    }

    private val databaseRef by lazy {
        Firebase.database.getReference(favoritesPath)
    }

    init {
        loadFavorites()
    }

    private fun loadFavorites() = databaseRef.run {
        loading.value = true
        orderByKey()
        addValueEventListener(loadValueListener())
    }

    fun removeFavorite(movie: Movie) = databaseRef.run {
        orderByChild(ID_PATH)
        addListenerForSingleValueEvent(removeValueListener(movie))
    }

    private fun loadValueListener() = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = stateList.run {
            val retrieved = snapshot.children.mapNotNull { result ->
                result.getValue(Movie::class.java)
            }.toList()
            if (!retrieved.isNullOrEmpty()) value = retrieved
            loading.value = false
        }
    }

    private fun removeValueListener(movie: Movie) = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = snapshot.children.forEach { value ->
            value.getValue(Movie::class.java)?.id?.let { id ->
                if (id == movie.id)
                    when (id) {
                        movie.id -> {
                            value.ref.removeValue()
                            stateRemoveFavorite.value = movie
                        }
                    }
            }
        }
    }


}