package com.jessica.yourfavoritemovies.ui.favorites.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jessica.yourfavoritemovies.model.Result
import com.jessica.yourfavoritemovies.utils.Constants
import com.jessica.yourfavoritemovies.utils.Constants.ID_PATH
import com.jessica.yourfavoritemovies.utils.MovieUtil

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    var stateRemoveFavorite: MutableLiveData<Result> = MutableLiveData()
    var stateList: MutableLiveData<List<Result>> = MutableLiveData()
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

    fun removeFavorite(result: Result) = databaseRef.run {
        orderByChild(ID_PATH)
        addListenerForSingleValueEvent(removeValueListener(result))
    }

    private fun loadValueListener() = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = stateList.run {
            value = snapshot.children.mapNotNull { result ->
                result.getValue(Result::class.java)
            }.toList()
            loading.value = false
        }
    }

    private fun removeValueListener(result: Result) = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = snapshot.children.forEach { value ->
            value.getValue(Result::class.java)?.id?.let { id ->
                if (id == result.id)
                when (id) {
                    result.id -> {
                        value.ref.removeValue()
                        stateRemoveFavorite.value = result
                    }
                }
            }
        }
    }


}