package com.drawiin.yourfavoritemovies.domain.interactor

import com.drawiin.yourfavoritemovies.domain.boundaries.UsersRepository
import javax.inject.Inject

class SaveCurrentProfileUid @Inject constructor(
    private val repository: UsersRepository
) {
    fun run(uid: String) = repository.saveCurrentProfileUid(uid)
}