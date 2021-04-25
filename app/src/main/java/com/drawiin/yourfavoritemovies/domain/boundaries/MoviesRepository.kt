package com.drawiin.yourfavoritemovies.domain.boundaries

import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.domain.models.Movie
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {
    fun getMoviesPlayingNow(): Flow<PagingData<Movie>>
}