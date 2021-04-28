package com.drawiin.yourfavoritemovies.domain.interactor

import com.drawiin.yourfavoritemovies.data.repository.DefaultUserRepository
import com.drawiin.yourfavoritemovies.domain.models.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class GetCurrentProfileInfo @Inject constructor(
    private val userRepository: DefaultUserRepository,
    private val database: DatabaseReference,
    private val auth: FirebaseAuth,
) {

    private val databaseRef by lazy {
        database
            .child("users")
            .child(auth.currentUser?.uid.toString())
            .child("profiles")
    }

    fun run(onProfileReceived: (Profile) -> Unit) {
        val currentProfileUid = userRepository.getCurrentProfileUid()
        databaseRef.get().addOnSuccessListener { snapshot ->
            snapshot
                .children
                .map { it.getValue(Profile::class.java) }
                .findLast { it?.id == currentProfileUid }
                ?.let { onProfileReceived(it) }

        }
    }
}