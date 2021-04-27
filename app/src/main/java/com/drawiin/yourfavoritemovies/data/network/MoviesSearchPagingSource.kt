package com.drawiin.yourfavoritemovies.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.utils.Constants.API_KEY
import java.io.IOException

class MoviesSearchPagingSource(
    private val query: String,
    private val language: String,
    private val moviesService: MoviesService
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val response = moviesService.searchMovies(query, API_KEY, language, page)
            val movies = response.movies
            LoadResult.Page(
                movies,
                prevKey = null,
                nextKey = if (response.totalPages == page) null else page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int = FIRST_PAGE

    companion object {
        private const val FIRST_PAGE = 1
    }
}