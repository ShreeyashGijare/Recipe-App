package com.example.search.data.repository

import com.example.search.data.local.RecipeDao
import com.example.search.data.mappers.toDomain
import com.example.search.data.remote.SearchAPIService
import com.example.search.domain.model.Recipe
import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class SearchRepoImpl(
    private val searchAPIService: SearchAPIService,
    private val recipeDao: RecipeDao
) : SearchRepository {
    override suspend fun getRecipes(s: String): Result<List<Recipe>> {
        return try {
            val response = searchAPIService.getRecipes(s)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    Result.success(it.toDomain())
                } ?: run { Result.failure(Exception("Error Occurred")) }
            } else {
                Result.failure(Exception("Error Occurred"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error Occurred"))
        }
    }

    override suspend fun getRecipeDetails(id: String): Result<RecipeDetails> {

        return try {
            val response = searchAPIService.getRecipeDetails(id)
            if (response.isSuccessful) {
                response.body()?.meals?.let {
                    if (it.isNotEmpty()) {
                        Result.success(it.first().toDomain())
                    } else {
                        Result.failure(Exception("Error Occurred"))
                    }
                } ?: run {
                    Result.failure(Exception("Error Occurred"))
                }
            } else {
                Result.failure(Exception("Error Occurred"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error Occurred"))
        }
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insertRecipe(recipe)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
    }
}