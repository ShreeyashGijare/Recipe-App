package com.example.search.ui.screens.recipe_detail

import com.example.common.util.NetworkResult
import com.example.common.util.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetRecipeDetailUseCase
import com.example.search.domain.use_cases.InsertRecipeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class RecipeDetailViewModelTest {


    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()


    @Before
    fun before() {

    }

    @After
    fun after() {

    }

    private val recipeDetailUseCase: GetRecipeDetailUseCase = mock()
    private val insertRecipeUseCase: InsertRecipeUseCase = mock()
    private val deleteRecipeUseCase: DeleteRecipeUseCase = mock()

    @Test
    fun testSuccess() = runTest {

        `when`(recipeDetailUseCase.invoke("id"))
            .thenReturn(
                flowOf(
                    NetworkResult.Success(data = getRecipeDetails())
                )
            )
        val viewModel =
            RecipeDetailViewModel(recipeDetailUseCase, insertRecipeUseCase, deleteRecipeUseCase)

        viewModel.onEvent(RecipeDetail.Event.FetchRecipeDetails("id"))
        assertEquals(getRecipeDetails(), viewModel.uiState.value.data)
    }


    @Test
    fun testFailure() = runTest {

        `when`(recipeDetailUseCase.invoke("id"))
            .thenReturn(
                flowOf(
                    NetworkResult.Error(message = "error")
                )
            )
        val viewModel =
            RecipeDetailViewModel(recipeDetailUseCase, insertRecipeUseCase, deleteRecipeUseCase)

        viewModel.onEvent(RecipeDetail.Event.FetchRecipeDetails("id"))
        assertEquals(UiText.RemoteString("error"), viewModel.uiState.value.error)
    }


    @Test
    fun testNavigateRecipeListScreen() = runTest {
        val viewModel =
            RecipeDetailViewModel(recipeDetailUseCase, insertRecipeUseCase, deleteRecipeUseCase)

        viewModel.onEvent(RecipeDetail.Event.GoToRecipeListScreen)

        val list = mutableListOf<RecipeDetail.Navigation>()

        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }

        assert(list.first() is RecipeDetail.Navigation.GoToRecipeListScreen)

    }

    @Test
    fun testNavigateToMediaPlayer() = runTest {
        val viewModel = RecipeDetailViewModel(
            recipeDetailUseCase,
            insertRecipeUseCase,
            deleteRecipeUseCase
        )

        viewModel.onEvent(RecipeDetail.Event.GoToMediaPlayer("url"))
        val list = mutableListOf<RecipeDetail.Navigation>()
        backgroundScope.launch(UnconfinedTestDispatcher()) {
            viewModel.navigation.collectLatest {
                list.add(it)
            }
        }
        assert(list.first() is RecipeDetail.Navigation.GoToMediaPlayer)
    }

    @Test
    fun testInsert() = runTest {
        val recipeDb = mutableListOf<Recipe>()
        val recipe= getRecipeDetails().toRecipe()
        `when`(insertRecipeUseCase.invoke(recipe))
            .then {
                recipeDb.add(recipe)
                flowOf(Unit)
            }

        val viewModel = RecipeDetailViewModel(
            recipeDetailUseCase,
            insertRecipeUseCase,
            deleteRecipeUseCase
        )
        viewModel.onEvent(RecipeDetail.Event.InsertRecipe(getRecipeDetails()))
        assert(recipeDb.contains(recipe))
    }

    @Test
    fun testDelete() = runTest {
        val recipeDb = mutableListOf<Recipe>()
        val recipe= getRecipeDetails().toRecipe()
        `when`(insertRecipeUseCase.invoke(recipe))
            .then {
                recipeDb.add(recipe)
                flowOf(Unit)
            }

        `when`(deleteRecipeUseCase.invoke(recipe)).
        then {
            recipeDb.remove(recipe)
            flowOf(Unit)
        }

        val viewModel = RecipeDetailViewModel(
            recipeDetailUseCase,
            insertRecipeUseCase,
            deleteRecipeUseCase
        )
        viewModel.onEvent(RecipeDetail.Event.InsertRecipe(getRecipeDetails()))
        viewModel.onEvent(RecipeDetail.Event.DeleteRecipe(getRecipeDetails()))
        assert(recipeDb.isEmpty())
    }

}

class MainDispatcherRule(private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {

    override fun starting(description: Description?) {

        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
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
