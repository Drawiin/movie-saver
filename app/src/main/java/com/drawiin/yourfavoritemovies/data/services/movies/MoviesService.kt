package com.drawiin.yourfavoritemovies.data.services.movies

import com.drawiin.yourfavoritemovies.domain.models.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {

    companion object {
        const val NOW_PLAYING = "movie/now_playing"
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }

    @GET(NOW_PLAYING)
    suspend fun getNowPlaying(
        @Query("api_key") api: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): ApiResponse
}