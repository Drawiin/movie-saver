package com.drawiin.yourfavoritemovies.domain.interactor

import com.drawiin.yourfavoritemovies.domain.boundaries.UsersRepository
import javax.inject.Inject

class GetCurrentProfileUid @Inject constructor(
    private val repository: UsersRepository,
) {

    fun run() = repository.getCurrentProfileUid()
}