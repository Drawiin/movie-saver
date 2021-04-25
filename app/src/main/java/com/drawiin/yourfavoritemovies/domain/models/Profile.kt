package com.drawiin.yourfavoritemovies.domain.models

data class Profile(
    val id: String = "",
    val name: String = "",
    val watchList: List<Movie> = emptyList(),
    val watchedMovies: List<Movie> = emptyList()
)