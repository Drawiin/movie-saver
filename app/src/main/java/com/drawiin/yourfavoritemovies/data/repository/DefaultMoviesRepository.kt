package com.drawiin.yourfavoritemovies.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.data.network.MoviesPagingSource
import com.drawiin.yourfavoritemovies.data.network.MoviesSearchPagingSource
import com.drawiin.yourfavoritemovies.data.network.MoviesService
import com.drawiin.yourfavoritemovies.domain.boundaries.MoviesRepository
import com.drawiin.yourfavoritemovies.domain.models.Movie
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Named

class DefaultMoviesRepository @Inject constructor(
    private val pagingSource: MoviesPagingSource,
    @Named("language")
    private val language: String,
    private val moviesService: MoviesService
) : MoviesRepository {
    override fun getMoviesPlayingNow(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { pagingSource }
        ).flow
    }

    override fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { MoviesSearchPagingSource(query, language, moviesService) }
        ).flow
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 1
    }
}