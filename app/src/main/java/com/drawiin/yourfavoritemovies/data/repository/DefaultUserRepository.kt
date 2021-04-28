package com.drawiin.yourfavoritemovies.data.repository

import com.drawiin.yourfavoritemovies.data.cache.CacheService
import com.drawiin.yourfavoritemovies.domain.boundaries.UsersRepository
import javax.inject.Inject

class DefaultUserRepository @Inject constructor(
    private val cacheService: CacheService
): UsersRepository {
    override fun getCurrentProfileUid(): String? {
        return cacheService.getCurrentProfileUid()
    }

    override fun saveCurrentProfileUid(uid: String) {
        cacheService.saveCurrentProfileUid(uid)
    }
}