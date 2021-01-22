package com.jessica.yourfavoritemovies

import com.jessica.yourfavoritemovies.utils.Constants.API_KEY

class MovieRepository {
    suspend fun getMovies(language: String) = MovieService.getApi().getApodDay(API_KEY, language)
}