package com.drawiin.yourfavoritemovies.repository

import com.drawiin.yourfavoritemovies.services.MovieService
import com.drawiin.yourfavoritemovies.utils.Constants.API_KEY

class MovieRepository {
    suspend fun getMovies(language: String) = MovieService.getApi().run {
        getMovies(API_KEY, language, PAGE).apiMovies
    }

    companion object {
        private const val PAGE: Int = 1
    }
}