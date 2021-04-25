package com.drawiin.yourfavoritemovies.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.data.services.movies.MoviesPagingSource
import com.drawiin.yourfavoritemovies.domain.boundaries.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DefaultMoviesRepository @Inject constructor(
    private val pagingSource: MoviesPagingSource
): MoviesRepository {
    override fun getMoviesPlayingNow(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { pagingSource }
        ).flow
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 1
    }
}