package com.drawiin.yourfavoritemovies.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.drawiin.yourfavoritemovies.model.ApiMovie
import com.drawiin.yourfavoritemovies.services.MoviesPagingSource
import kotlinx.coroutines.flow.Flow

class PagedMovieRepository {
    fun getMovies(language: String): Flow<PagingData<ApiMovie>> {
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { MoviesPagingSource(language) }
        ).flow
    }


    companion object {
        private const val NETWORK_PAGE_SIZE = 1
    }
}