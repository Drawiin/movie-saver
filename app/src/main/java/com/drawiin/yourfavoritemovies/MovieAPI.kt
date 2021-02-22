package com.drawiin.yourfavoritemovies

import com.drawiin.yourfavoritemovies.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieAPI {

    companion object {
        const val SOURCE = "movie/now_playing"
    }

    @GET(SOURCE)
    suspend fun getApodDay(
        @Query("api_key") api: String,
        @Query("language") language: String
    ): Movie
}