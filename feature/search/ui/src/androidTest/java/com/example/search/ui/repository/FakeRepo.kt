package com.example.search.ui.repository

import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import com.example.search.ui.utils.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSuccessRepoImpl : SearchRepository {

    private val dbFlow = MutableStateFlow(emptyList<Recipe>())
    private val list = mutableListOf<Recipe>()


    fun reset() {
        list.clear()
    }

    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return Result.success(getRecipeResponse())
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
        return getRecipeDetailsList().find { it.idMeal == id }?.let { recipeDetails ->
            Result.success(recipeDetails)
        }?: run { Result.success(getRecipeDetails()) }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        list.add(recipe)
        dbFlow.value = list
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        list.remove(recipe)
        dbFlow.value = list
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dbFlow
    }

}

class FakeFailureRepoImpl : SearchRepository {

    private val dbFlow = MutableStateFlow(emptyList<Recipe>())
    private val list = mutableListOf<Recipe>()

    fun reset() {
        list.clear()
    }

    companion object {
        val errorMessage = "error Message"
    }

    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return Result.failure(RuntimeException(errorMessage))
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {
        return Result.failure(RuntimeException(errorMessage))
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        list.add(recipe)
        dbFlow.value = list
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        list.remove(recipe)
        dbFlow.value = list
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return dbFlow
    }


}