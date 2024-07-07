package com.example.search.ui.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.common.navigation.FeatureApi
import com.example.common.navigation.NavigationRoutes
import com.example.common.navigation.NavigationSubGraphRoute
import com.example.search.domain.model.RecipeDetails
import com.example.search.ui.screens.favorite.FavoriteScreen
import com.example.search.ui.screens.favorite.FavoriteViewModel
import com.example.search.ui.screens.recipe_detail.RecipeDetail
import com.example.search.ui.screens.recipe_detail.RecipeDetailScreen
import com.example.search.ui.screens.recipe_detail.RecipeDetailViewModel
import com.example.search.ui.screens.recipe_list.RecipeList
import com.example.search.ui.screens.recipe_list.RecipeListScreen
import com.example.search.ui.screens.recipe_list.RecipeListViewModel


interface SearchFeatureApi : FeatureApi

class SearchFeatureApiImpl : SearchFeatureApi {
    override fun registerGraph(
        navGraphBuilder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        navGraphBuilder.navigation(
            route = NavigationSubGraphRoute.Search.route,
            startDestination = NavigationRoutes.RecipeList.route
        ) {
            composable(route = NavigationRoutes.RecipeList.route) {
                val viewModel: RecipeListViewModel = hiltViewModel()
                RecipeListScreen(
                    viewModel = viewModel,
                    navHostController = navHostController
                ) { mealId ->
                    viewModel.onEvent(RecipeList.Event.GoToRecipeDetails(mealId))
                }
            }

            composable(route = NavigationRoutes.RecipeDetails.route) {
                val viewModel: RecipeDetailViewModel = hiltViewModel()
                val mealId = it.arguments?.getString("id")
                LaunchedEffect(key1 = mealId) {
                    mealId?.let {
                        viewModel.onEvent(RecipeDetail.Event.FetchRecipeDetails(it))
                    }
                }
                RecipeDetailScreen(
                    viewModel = viewModel,
                    navHostController = navHostController,
                    onNavigationClick = {
                        viewModel.onEvent(RecipeDetail.Event.GoToRecipeListScreen)
                    },
                    onDeleteClick = {
                        viewModel.onEvent(RecipeDetail.Event.DeleteRecipe(it))
                    },
                    onFavoriteClick = {
                        viewModel.onEvent(RecipeDetail.Event.InsertRecipe(it))
                    })
            }

            composable(NavigationRoutes.FavoriteScreen.route) {
                val viewModel: FavoriteViewModel = hiltViewModel()

                FavoriteScreen(
                    navHostController = navHostController,
                    viewModel = viewModel
                ) { mealId ->
                    viewModel.onEvent(FavoriteScreen.Event.GoToDetails(mealId))

                }
            }
        }
    }
}