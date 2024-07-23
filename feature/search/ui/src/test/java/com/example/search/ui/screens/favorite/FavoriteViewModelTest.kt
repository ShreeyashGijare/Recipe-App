package com.example.search.ui.screens.favorite

import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipesFromLocalDBUseCase
import com.example.search.ui.screens.recipe_list.MainDispatcherRule
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class FavoriteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getAllRecipesFromLocalDBUseCase: GetAllRecipesFromLocalDBUseCase = mock()
    private val deleteRecipeUseCase: DeleteRecipeUseCase = mock()

    @Before
    fun setUp() {
        `when`(getAllRecipesFromLocalDBUseCase.invoke())
            .thenReturn(flowOf(getRecipeResponse()))
    }

    @Test
    fun testAlphabeticalSort() = runTest {
        val viewModel = FavoriteViewModel(getAllRecipesFromLocalDBUseCase, deleteRecipeUseCase)
        viewModel.onEvent(FavoriteScreen.Event.AlphabeticalSort)
        assertEquals(getRecipeResponse().sortedBy { it.strMeal }, viewModel.uiState.value.data)

    }

    @Test
    fun testLessIngredientsSort() = runTest {
        val viewModel = FavoriteViewModel(getAllRecipesFromLocalDBUseCase, deleteRecipeUseCase)
        viewModel.onEvent(FavoriteScreen.Event.LessIngredientsSort)
        assertEquals(
            getRecipeResponse().sortedBy { it.strInstructions.length },
            viewModel.uiState.value.data
        )
    }

    @Test
    fun testResetSort() = runTest {
        val viewModel = FavoriteViewModel(getAllRecipesFromLocalDBUseCase, deleteRecipeUseCase)
        viewModel.onEvent(FavoriteScreen.Event.AlphabeticalSort)
        assertEquals(getRecipeResponse().sortedBy { it.strMeal }, viewModel.uiState.value.data)
        viewModel.onEvent(FavoriteScreen.Event.ResetSort)
        assertEquals(getRecipeResponse(), viewModel.uiState.value.data)
    }

    @Test
    fun testDelete() = runTest {
        val recipeLIst = getRecipeResponse().toMutableList()
        `when`(deleteRecipeUseCase.invoke(recipeLIst.first())).then {
            recipeLIst.remove(recipeLIst.first())
            flowOf(Unit)
        }
        val viewModel = FavoriteViewModel(getAllRecipesFromLocalDBUseCase, deleteRecipeUseCase)
        viewModel.onEvent(FavoriteScreen.Event.DeleteRecipe(recipeLIst.first()))
        assert(recipeLIst.size == 1)
    }

    @Test
    fun testGoToDetails() = runTest {
        val viewModel = FavoriteViewModel(getAllRecipesFromLocalDBUseCase, deleteRecipeUseCase)
        viewModel.onEvent(FavoriteScreen.Event.ShowDetails("id"))
        val list = mutableListOf<FavoriteScreen.Navigation>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }

        assert(list.first() is FavoriteScreen.Navigation.GoToRecipeDetailScreen)

    }

}


private fun getRecipeResponse(): List<Recipe> {
    return listOf(
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "12",
        ),
        Recipe(
            idMeal = "idMeal",
            strArea = "India",
            strCategory = "category",
            strYoutube = "strYoutube",
            strTags = "tag1,tag2",
            strMeal = "Chicken",
            strMealThumb = "strMealThumb",
            strInstructions = "123",
        )
    )

}

private fun getRecipeDetails(): RecipeDetails {
    return RecipeDetails(
        idMeal = "idMeal",
        strArea = "India",
        strCategory = "category",
        strYoutube = "strYoutube",
        strTags = "tag1,tag2",
        strMeal = "Chicken",
        strMealThumb = "strMealThumb",
        strInstructions = "strInstructions",
        ingredientsPair = listOf(Pair("Ingredients", "Measure"))
    )
}
