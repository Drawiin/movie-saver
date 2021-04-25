package com.drawiin.yourfavoritemovies.domain.interactor

import com.drawiin.yourfavoritemovies.data.repository.DefaultMoviesRepository
import com.drawiin.yourfavoritemovies.domain.boundaries.MoviesRepository
import javax.inject.Inject

class GetMovies @Inject constructor(
    private val moviesRepository: MoviesRepository
) {
    fun run() = moviesRepository.getMoviesPlayingNow()
}