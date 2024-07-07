package com.example.dishapp.di

import android.content.Context
import com.example.dishapp.local.AppDataBase
import com.example.dishapp.navigation.NavigationSubGraph
import com.example.media_player.navigation.MediaPlayerFeatureApi
import com.example.search.data.local.RecipeDao
import com.example.search.ui.navigation.SearchFeatureApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesNavigationSubGraph(
        searchFeatureApi: SearchFeatureApi,
        mediaPlayerFeatureApi: MediaPlayerFeatureApi
    ): NavigationSubGraph =
        NavigationSubGraph(searchFeatureApi, mediaPlayerFeatureApi)


    @Provides
    @Singleton
    fun providesAppDataBase(@ApplicationContext context: Context) = AppDataBase.getInstance(context)

    @Provides
    fun providesRecipeDao(appDataBase: AppDataBase): RecipeDao =
        appDataBase.getRecipeDao()

}