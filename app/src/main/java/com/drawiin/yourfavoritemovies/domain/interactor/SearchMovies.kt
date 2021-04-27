package com.drawiin.yourfavoritemovies.domain.interactor

import com.drawiin.yourfavoritemovies.domain.boundaries.MoviesRepository
import javax.inject.Inject

class SearchMovies @Inject constructor(
    private val repository: MoviesRepository
) {
    suspend fun run(query: String) = repository.searchMovies(query)
}