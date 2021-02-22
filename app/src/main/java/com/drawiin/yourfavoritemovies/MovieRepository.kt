package com.drawiin.yourfavoritemovies

import com.drawiin.yourfavoritemovies.utils.Constants.API_KEY

class MovieRepository {
    suspend fun getMovies(language: String) = MovieService.getApi().getApodDay(API_KEY, language)
}