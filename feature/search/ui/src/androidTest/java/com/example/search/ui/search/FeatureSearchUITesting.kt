package com.example.search.ui.search

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.common.navigation.NavigationRoutes
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipesFromLocalDBUseCase
import com.example.search.domain.use_cases.GetRecipeDetailUseCase
import com.example.search.domain.use_cases.InsertRecipeUseCase
import com.example.search.ui.repository.FakeFailureRepoImpl
import com.example.search.ui.repository.FakeSuccessRepoImpl
import com.example.search.ui.screens.favorite.FavoriteScreen
import com.example.search.ui.screens.favorite.FavoriteViewModel
import com.example.search.ui.screens.recipe_detail.RecipeDetail
import com.example.search.ui.screens.recipe_detail.RecipeDetailScreen
import com.example.search.ui.screens.recipe_detail.RecipeDetailViewModel
import com.example.search.ui.screens.recipe_list.RecipeList
import com.example.search.ui.screens.recipe_list.RecipeListScreen
import com.example.search.ui.screens.recipe_list.RecipeListScreenTestTag
import com.example.search.ui.screens.recipe_list.RecipeListViewModel
import com.example.search.ui.utils.getRecipeResponse
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FeatureSearchUITesting {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var getAllRecipeUseCase: GetAllRecipeUseCase
    private lateinit var getRecipeDetailUseCase: GetRecipeDetailUseCase
    private lateinit var insertRecipeUseCase: InsertRecipeUseCase
    private lateinit var deleteRecipeUseCase: DeleteRecipeUseCase
    private lateinit var getAllRecipesFromLocalDBUseCase: GetAllRecipesFromLocalDBUseCase


    private lateinit var fakeSuccessRepoImpl: FakeSuccessRepoImpl
    private lateinit var fakeFailureRepoImpl: FakeFailureRepoImpl


    @Before
    fun setUp() {
        fakeSuccessRepoImpl = FakeSuccessRepoImpl()
        fakeFailureRepoImpl = FakeFailureRepoImpl()
    }


    @After
    fun tearDown() {
        fakeSuccessRepoImpl.reset()
        fakeFailureRepoImpl.reset()
    }


    private fun initSuccessUseCase() {
        getAllRecipeUseCase = GetAllRecipeUseCase(fakeSuccessRepoImpl)
        getRecipeDetailUseCase = GetRecipeDetailUseCase(fakeSuccessRepoImpl)
        insertRecipeUseCase = InsertRecipeUseCase(fakeSuccessRepoImpl)
        deleteRecipeUseCase = DeleteRecipeUseCase(fakeSuccessRepoImpl)
        getAllRecipesFromLocalDBUseCase = GetAllRecipesFromLocalDBUseCase(fakeSuccessRepoImpl)

    }

    private fun initFailureUseCase() {
        getAllRecipeUseCase = GetAllRecipeUseCase(fakeFailureRepoImpl)
        getRecipeDetailUseCase = GetRecipeDetailUseCase(fakeFailureRepoImpl)
        insertRecipeUseCase = InsertRecipeUseCase(fakeFailureRepoImpl)
        deleteRecipeUseCase = DeleteRecipeUseCase(fakeFailureRepoImpl)
        getAllRecipesFromLocalDBUseCase = GetAllRecipesFromLocalDBUseCase(fakeFailureRepoImpl)

    }


    private fun testingEnvironment() {
        val recipeListViewModel = RecipeListViewModel(getAllRecipeUseCase)
        val recipeDetailViewModel =
            RecipeDetailViewModel(getRecipeDetailUseCase, insertRecipeUseCase, deleteRecipeUseCase)
        val favoriteViewModel =
            FavoriteViewModel(getAllRecipesFromLocalDBUseCase, deleteRecipeUseCase)

        composeRule.setContent {
            val navHostController = rememberNavController()
            NavHost(
                navController = navHostController,
                startDestination = NavigationRoutes.RecipeList.route
            ) {
                composable(route = NavigationRoutes.RecipeList.route) {
                    RecipeListScreen(
                        viewModel = recipeListViewModel,
                        navHostController = navHostController
                    ) { mealId ->
                        recipeListViewModel.onEvent(RecipeList.Event.GoToRecipeDetails(mealId))
                    }
                }

                composable(route = NavigationRoutes.RecipeDetails.route) {
                    val mealId = it.arguments?.getString("id")
                    LaunchedEffect(key1 = mealId) {
                        mealId?.let {
                            recipeDetailViewModel.onEvent(RecipeDetail.Event.FetchRecipeDetails(it))
                        }
                    }
                    RecipeDetailScreen(
                        viewModel = recipeDetailViewModel,
                        navHostController = navHostController,
                        onNavigationClick = {
                            recipeDetailViewModel.onEvent(RecipeDetail.Event.GoToRecipeListScreen)
                        },
                        onDeleteClick = {
                            recipeDetailViewModel.onEvent(RecipeDetail.Event.DeleteRecipe(it))
                        },
                        onFavoriteClick = {
                            recipeDetailViewModel.onEvent(RecipeDetail.Event.InsertRecipe(it))
                        })
                }

                composable(NavigationRoutes.FavoriteScreen.route) {

                    FavoriteScreen(
                        navHostController = navHostController,
                        viewModel = favoriteViewModel
                    ) { mealId ->
                        favoriteViewModel.onEvent(FavoriteScreen.Event.GoToDetails(mealId))

                    }
                }
            }
        }
    }


    @Test
    fun testRecipeListSuccess() {
        initSuccessUseCase()
        testingEnvironment()

        with(composeRule) {

            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")

            onNodeWithTag(RecipeListScreenTestTag.LAZY_COLUMN).assertIsDisplayed()
            onNodeWithTag(RecipeListScreenTestTag.LAZY_COLUMN).onChildAt(0)
                .assert(hasTestTag(getRecipeResponse().first().strMeal.plus(0)))

        }
    }

    @Test
    fun testRecipeListFailure() {
        initSuccessUseCase()
        testingEnvironment()
        with(composeRule) {
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performClick()
            onNodeWithTag(RecipeListScreenTestTag.SEARCH).performTextInput("chicken")



        }
    }
}