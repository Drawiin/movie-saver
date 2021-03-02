package com.drawiin.yourfavoritemovies.ui.favorites.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.model.ApiMovie
import com.drawiin.yourfavoritemovies.utils.Constants
import com.drawiin.yourfavoritemovies.utils.Constants.ID_PATH
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    var stateRemoveFavorite: MutableLiveData<ApiMovie> = MutableLiveData()
    var stateList: MutableLiveData<List<ApiMovie>> = MutableLiveData()
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

    fun removeFavorite(apiMovie: ApiMovie) = databaseRef.run {
        orderByChild(ID_PATH)
        addListenerForSingleValueEvent(removeValueListener(apiMovie))
    }

    private fun loadValueListener() = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = stateList.run {
            value = snapshot.children.mapNotNull { result ->
                result.getValue(ApiMovie::class.java)
            }.toList()
            loading.value = false
        }
    }

    private fun removeValueListener(apiMovie: ApiMovie) = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = snapshot.children.forEach { value ->
            value.getValue(ApiMovie::class.java)?.id?.let { id ->
                if (id == apiMovie.id)
                    when (id) {
                        apiMovie.id -> {
                            value.ref.removeValue()
                            stateRemoveFavorite.value = apiMovie
                        }
                    }
            }
        }
    }


}