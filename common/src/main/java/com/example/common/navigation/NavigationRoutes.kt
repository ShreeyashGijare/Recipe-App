package com.example.common.navigation

sealed class NavigationRoutes(val route: String) {

    data object RecipeList : NavigationRoutes("/recipe_list")
    data object RecipeDetails : NavigationRoutes("/recipe_details/{id}") {
        fun sendId(id: String) = "/recipe_details/${id}"
    }

    data object FavoriteScreen : NavigationRoutes("/favorite")

    data object MediaPlayer : NavigationRoutes("/player/{video_id}") {
        fun sendUrl(videoId: String) = "/player/$videoId"
    }
}


sealed class NavigationSubGraphRoute(val route: String) {
    data object Search : NavigationSubGraphRoute(route = "/search")
    data object MediaPlayer : NavigationSubGraphRoute(route = "/media_player")
}