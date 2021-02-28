package com.drawiin.yourfavoritemovies.services

import com.drawiin.yourfavoritemovies.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieAPI {

    companion object {
        const val SOURCE = "movie/now_playing"
    }

    @GET(SOURCE)
    suspend fun getMovies(
        @Query("api_key") api: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): ApiResponse
}