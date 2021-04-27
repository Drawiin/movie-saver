package com.drawiin.yourfavoritemovies.domain.models


import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("results")
    val movies: List<Movie> = listOf(),
    @SerializedName("total_pages")
    val totalPages: Int = 0,
    @SerializedName("total_results")
    val totalResults: Int = 0
)