package com.example.search.ui.screens.recipe_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.util.NetworkResult
import com.example.common.util.UiText
import com.example.search.domain.model.Recipe
import kotlinx.coroutines.channels.Channel
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetRecipeDetailUseCase
import com.example.search.domain.use_cases.InsertRecipeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val recipeDetailUseCase: GetRecipeDetailUseCase,
    private val insertRecipeUseCase: InsertRecipeUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow(RecipeDetail.UiState())
    val uiState: StateFlow<RecipeDetail.UiState> get() = _uiState.asStateFlow()

    private val _navigation = Channel<RecipeDetail.Navigation>()
    val navigation: Flow<RecipeDetail.Navigation> = _navigation.receiveAsFlow()

    fun onEvent(event: RecipeDetail.Event) {

        when (event) {
            is RecipeDetail.Event.FetchRecipeDetails -> {
                recipeDetails(event.id)

            }

            RecipeDetail.Event.GoToRecipeListScreen -> {

                viewModelScope.launch {
                    _navigation.send(RecipeDetail.Navigation.GoToRecipeListScreen)
                }
            }

            is RecipeDetail.Event.DeleteRecipe -> {

                deleteRecipeUseCase.invoke(event.recipeDetails.toRecipe()).launchIn(viewModelScope)

            }

            is RecipeDetail.Event.InsertRecipe -> {

                insertRecipeUseCase.invoke(event.recipeDetails.toRecipe()).launchIn(viewModelScope)

            }

            is RecipeDetail.Event.GoToMediaPlayer -> {
                viewModelScope.launch {
                    _navigation.send(RecipeDetail.Navigation.GoToMediaPlayer(event.youtubeUrl))
                }
            }
        }

    }


    private fun recipeDetails(id: String) =
        recipeDetailUseCase.invoke(id)
            .onEach { result ->

                when (result) {
                    is NetworkResult.Error -> {
                        _uiState.update {
                            RecipeDetail.UiState(error = UiText.RemoteString(result.message.toString()))
                        }
                    }

                    is NetworkResult.Loading -> {
                        _uiState.update {
                            RecipeDetail.UiState(isLoading = true)
                        }
                    }

                    is NetworkResult.Success -> {
                        _uiState.update {
                            RecipeDetail.UiState(data = result.data)
                        }
                    }
                }
            }
            .launchIn(viewModelScope)


    fun RecipeDetails.toRecipe(): Recipe {
        return com.example.search.domain.model.Recipe(
            idMeal,
            strArea,
            strMeal,
            strMealThumb,
            strCategory,
            strTags,
            strYoutube,
            strInstructions
        )
    }


}

object RecipeDetail {
    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: RecipeDetails? = null
    )

    sealed interface Navigation {

        data object GoToRecipeListScreen : Navigation
        data class GoToMediaPlayer(val youtubeUrl: String) : Navigation

    }

    sealed interface Event {

        data class FetchRecipeDetails(val id: String) : Event

        data object GoToRecipeListScreen : Event

        data class InsertRecipe(val recipeDetails: RecipeDetails) : Event
        data class DeleteRecipe(val recipeDetails: RecipeDetails) : Event

        data class GoToMediaPlayer(val youtubeUrl: String) : Event

    }
}