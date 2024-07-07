package com.example.dishapp.navigation

import com.example.media_player.navigation.MediaPlayerFeatureApi
import com.example.search.ui.navigation.SearchFeatureApi

data class NavigationSubGraph(
    val searchFeatureApi: SearchFeatureApi,
    val mediaPlayerApi: MediaPlayerFeatureApi
)