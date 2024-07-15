package com.example.search.domain.use_cases

import com.example.search.domain.model.RecipeDetails
import com.example.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class GetRecipeDetailUseCaseTest {

    private val searchRepository: SearchRepository = mock()


    @Test
    fun testSuccess() = runTest {

        `when`(searchRepository.getRecipeDetails("id"))
            .thenReturn(Result.success(getRecipeDetails()))

        val repo = GetRecipeDetailUseCase(searchRepository)
        val response = repo.invoke("id")

        assertEquals(getRecipeDetails(), response.last().data)

    }

    @Test
    fun testFailure() = runTest {
        `when`(searchRepository.getRecipeDetails("id"))
            .thenReturn(Result.failure(RuntimeException("error")))

        val repo = GetRecipeDetailUseCase(searchRepository)
        val response = repo.invoke("id")

        assertEquals("error", response.last().message)
    }

    @Test
    fun testException() = runTest {
        `when`(searchRepository.getRecipeDetails("id"))
            .thenThrow(RuntimeException("error"))

        val repo = GetRecipeDetailUseCase(searchRepository)
        val response = repo.invoke("id")

        assertEquals("error", response.last().message)
    }

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