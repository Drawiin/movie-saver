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

    init {
        loadFavorites()
    }

    private fun loadFavorites() = Firebase.database.getReference(favoritesPath).apply {
        loading.value = true
        orderByKey()
        addValueEventListener(getLoadValueListener())
    }

    private fun getLoadValueListener() = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(snapshot: DataSnapshot) = stateList.run {
            value = snapshot.children.mapNotNull { result ->
                result.getValue(Result::class.java)
            }.toList()
            loading.value = false
        }
    }

    fun removeFavorite(result: Result) = Firebase.database.getReference(favoritesPath).apply {
        orderByChild(ID_PATH)
        addListenerForSingleValueEvent(getRemoveValueListener(result))
    }

    private fun getRemoveValueListener(result: Result) = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {}

        override fun onDataChange(
            snapshot: DataSnapshot
        ) = snapshot.children.forEach { valueSnapshot ->
            valueSnapshot.getValue(Result::class.java)?.id?.let { id ->
                when (id) {
                    result.id -> {
                        valueSnapshot.ref.removeValue()
                        stateRemoveFavorite.value = result
                    }
                }
            }
        }
    }


}