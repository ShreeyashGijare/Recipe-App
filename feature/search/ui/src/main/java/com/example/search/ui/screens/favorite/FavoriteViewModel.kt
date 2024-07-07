package com.example.search.ui.screens.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.common.util.UiText
import com.example.search.domain.model.Recipe
import com.example.search.domain.use_cases.DeleteRecipeUseCase
import com.example.search.domain.use_cases.GetAllRecipesFromLocalDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getAllRecipesFromLocalDBUseCase: GetAllRecipesFromLocalDBUseCase,
    private val deleteRecipeUseCase: DeleteRecipeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteScreen.UiState())
    val uiState: StateFlow<FavoriteScreen.UiState> get() = _uiState.asStateFlow()

    private val _navigation = Channel<FavoriteScreen.Navigation>()
    val navigation: Flow<FavoriteScreen.Navigation> = _navigation.receiveAsFlow()

    private var originalList = mutableListOf<Recipe>()

    init {
        getRecipeList()
    }

    private fun getRecipeList() = viewModelScope.launch {
        getAllRecipesFromLocalDBUseCase.invoke().collectLatest { list ->
            _uiState.update {
                originalList = list.toMutableList()
                FavoriteScreen.UiState(data = list)
            }
        }
    }

    fun onEvent(event: FavoriteScreen.Event) {
        when (event) {
            FavoriteScreen.Event.AlphabeticalSort -> {
                alphabeticalSort()
            }

            FavoriteScreen.Event.LessIngredientsSort -> {
                lessIngredientSort()
            }

            FavoriteScreen.Event.ResetSort -> {
                resetSort()
            }

            is FavoriteScreen.Event.ShowDetails -> {
                viewModelScope.launch {
                    _navigation.send(FavoriteScreen.Navigation.GoToRecipeDetailScreen(event.id))
                }
            }

            is FavoriteScreen.Event.DeleteRecipe -> {
                deleteRecipe(event.recipe)
            }

            is FavoriteScreen.Event.GoToDetails -> {
                viewModelScope.launch {
                    _navigation.send(FavoriteScreen.Navigation.GoToRecipeDetailScreen(event.id))
                }
            }
        }
    }

    private fun deleteRecipe(recipe: Recipe) =
        deleteRecipeUseCase.invoke(recipe).launchIn(viewModelScope)


    fun alphabeticalSort() = _uiState.update {
        FavoriteScreen.UiState(data = originalList.sortedBy { it.strMeal })
    }

    fun lessIngredientSort() = _uiState.update {
        FavoriteScreen.UiState(data = originalList.sortedBy { it.strInstructions.length })
    }

    fun resetSort() {
        _uiState.update {
            FavoriteScreen.UiState(data = originalList)
        }
    }
}


object FavoriteScreen {

    data class UiState(
        val isLoading: Boolean = false,
        val error: UiText = UiText.Idle,
        val data: List<Recipe>? = null
    )

    sealed interface Event {
        data object AlphabeticalSort : Event
        data object LessIngredientsSort : Event
        data object ResetSort : Event
        data class ShowDetails(val id: String) : Event
        data class DeleteRecipe(val recipe: Recipe) : Event
        data class GoToDetails(val id: String) : Event
    }

    sealed interface Navigation {
        data class GoToRecipeDetailScreen(val id: String) : Navigation
    }

}