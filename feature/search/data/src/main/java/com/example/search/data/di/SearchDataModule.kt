package com.example.search.data.di

import com.example.search.data.local.RecipeDao
import com.example.search.data.remote.SearchAPIService
import com.example.search.data.repository.SearchRepoImpl
import com.example.search.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

const val BASE_URL: String = "https://www.themealdb.com/"

@InstallIn(SingletonComponent::class)
@Module
object SearchDataModule {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun providesSearchAPIService(retrofit: Retrofit): SearchAPIService =
        retrofit.create(SearchAPIService::class.java)

    @Provides
    fun providesSearchRepo(searchAPIService: SearchAPIService, recipeDao: RecipeDao): SearchRepository =
        SearchRepoImpl(searchAPIService, recipeDao)


}