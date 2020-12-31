package com.jessica.yourfavoritemovies.favorites.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jessica.yourfavoritemovies.Constants
import com.jessica.yourfavoritemovies.Constants.ID_PATH
import com.jessica.yourfavoritemovies.MovieUtil
import com.jessica.yourfavoritemovies.model.Result

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {
    var stateRemoveFavorite: MutableLiveData<Result> = MutableLiveData()
    var stateList: MutableLiveData<List<Result>> = MutableLiveData()

    init {
        loadFavorites()
    }

    fun removeFavorite(result: Result) {
        val database = Firebase.database
        val reference = database.getReference(
            MovieUtil.getUserId(getApplication()).toString() + Constants.FAVORITES_PATH
        )

        reference.orderByChild(ID_PATH).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { valueSnapshot ->
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

        })
    }

    private fun loadFavorites() {
        val database = Firebase.database
        val reference = database.getReference(
            MovieUtil.getUserId(getApplication()).toString() + Constants.FAVORITES_PATH
        )

        reference.orderByKey().addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                stateList.value =
                    snapshot.children.mapNotNull { result -> result.getValue(Result::class.java) }
                        .toList()
            }

        })
    }

}