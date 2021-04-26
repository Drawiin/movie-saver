package com.drawiin.yourfavoritemovies.ui.profile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.drawiin.yourfavoritemovies.utils.MovieUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val auth: FirebaseAuth,
    private val database: DatabaseReference
) : AndroidViewModel(application) {
    val profiles get() = _profiles
    private val _profiles: MutableLiveData<List<Profile>> by lazy { MutableLiveData() }

    private val databaseRef = auth.currentUser?.uid?.let { uid ->
        database.child("users").child(uid).child("profiles")
    }

    init {
        loadProfiles()
    }

    private fun loadProfiles() =
        databaseRef?.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot
                        .children
                        .mapNotNull { result ->
                            result.getValue(Profile::class.java)
                        }.toList()
                        .takeUnless { it.isNullOrEmpty() }
                        ?.let {
                            _profiles.value = it
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )


    fun createProfile(name: String) = name.takeUnless { it.isNullOrBlank() }?.let {
        databaseRef?.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot
                        .children
                        .mapNotNull { result ->
                            result.getValue(Profile::class.java)
                        }.toList()
                        .takeUnless { it.isNullOrEmpty() }
                        ?.let { profiles ->
                            if (profiles.size < 4) {
                                saveNewProfile(name, profiles)
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )
    }


    private fun saveNewProfile(
        newProfileName: String,
        profiles: List<Profile>
    ) = databaseRef?.push()?.key?.let { key ->
        val newProfile = Profile(
            id = key,
            name = newProfileName
        )
        databaseRef.setValue(profiles.plus(newProfile))
    }

    fun deleteProfile(profile: Profile) =
        databaseRef?.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot
                        .children
                        .mapNotNull { result ->
                            result.getValue(Profile::class.java)
                        }.toList()
                        .takeUnless { it.isNullOrEmpty() }
                        ?.let { profiles ->
                            removeProfileById(profile.id, profiles)
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )

    private fun removeProfileById(
        id: String,
        profiles: List<Profile>
    ) {
        if (profiles.size > 1) {
            databaseRef?.setValue(profiles.filter { it.id != id })
        }
    }

    fun saveCurrentProfileId(id: String) {
        MovieUtil.saveProfileUid(getApplication(), id)
    }

}


