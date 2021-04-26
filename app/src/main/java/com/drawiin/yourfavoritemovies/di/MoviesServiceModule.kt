package com.drawiin.yourfavoritemovies.di

import com.drawiin.yourfavoritemovies.BuildConfig
import com.drawiin.yourfavoritemovies.data.network.MoviesService
import com.drawiin.yourfavoritemovies.data.network.MoviesService.Companion.BASE_URL
import com.drawiin.yourfavoritemovies.utils.Constants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MoviesServiceModule {
    @Singleton
    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return logging
    }

    @Singleton
    @Provides
    fun providesHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(loggingInterceptor)
        }
        return httpClient.build()
    }


    @Singleton
    @Provides
    fun providesMoviesService(httpClient: OkHttpClient): MoviesService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(httpClient)
            .build()
            .create(MoviesService::class.java)
    }

    @Singleton
    @Provides
    @Named("language")
    fun providesLanguage(): String {
        return Constants.LANGUAGE_PT_BR
    }
}