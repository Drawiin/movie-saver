package com.drawiin.yourfavoritemovies.domain.boundaries

import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface UsersRepository {
    fun getCurrentProfileUid(): String?
    fun saveCurrentProfileUid(uid: String)
}