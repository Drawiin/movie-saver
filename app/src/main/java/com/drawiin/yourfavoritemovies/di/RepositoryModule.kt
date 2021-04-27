package com.drawiin.yourfavoritemovies.di

import com.drawiin.yourfavoritemovies.data.repository.DefaultMoviesRepository
import com.drawiin.yourfavoritemovies.data.network.MoviesPagingSource
import com.drawiin.yourfavoritemovies.data.network.MoviesService
import com.drawiin.yourfavoritemovies.domain.boundaries.MoviesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun providesMovieRepository(
        pagingSource: MoviesPagingSource,
        @Named("language") language: String,
        moviesService: MoviesService
    ): MoviesRepository {
        return DefaultMoviesRepository(pagingSource, language, moviesService)
    }
}