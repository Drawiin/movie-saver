package com.drawiin.yourfavoritemovies.data.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.drawiin.yourfavoritemovies.domain.models.Movie
import com.drawiin.yourfavoritemovies.utils.Constants.API_KEY
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class MoviesPagingSource @Inject constructor(
    @Named("language")
    private val language: String,
    private val moviesService: MoviesService
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        Log.d("MOVIES_REPOSITORY", "New page")
        val position = params.key ?: FIRST_PAGE
        return try {
            val response = moviesService.getNowPlaying(API_KEY, language, position)
            val movies = response.movies
            LoadResult.Page(
                movies,
                prevKey = null,
                nextKey = if (response.totalPages == position) null else position + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? = FIRST_PAGE

    companion object {
        private const val FIRST_PAGE = 1
    }
}