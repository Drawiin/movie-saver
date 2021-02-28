package com.drawiin.yourfavoritemovies.services

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bumptech.glide.load.HttpException
import com.drawiin.yourfavoritemovies.model.ApiMovie
import com.drawiin.yourfavoritemovies.utils.Constants.API_KEY
import java.io.IOException

class MoviesPagingSource(
    private val language: String
) : PagingSource<Int, ApiMovie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ApiMovie> {
        Log.d("MOVIES_REPOSITORY", "New page")
        val position = params.key ?: FIRST_PAGE
        return try {
            val response = MovieService.getApi().getMovies(API_KEY, language, position)
            val movies = response.apiMovies
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

    override fun getRefreshKey(state: PagingState<Int, ApiMovie>): Int? = FIRST_PAGE

    companion object {
        private const val FIRST_PAGE = 1
    }
}