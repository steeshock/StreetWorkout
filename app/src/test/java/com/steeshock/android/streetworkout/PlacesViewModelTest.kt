package com.steeshock.android.streetworkout

import com.steeshock.android.streetworkout.data.model.Category
import com.steeshock.android.streetworkout.data.model.Place
import com.steeshock.android.streetworkout.data.repository.implementation.mockApi.MockApiCategoriesRepository
import com.steeshock.android.streetworkout.data.repository.implementation.mockApi.MockApiPlacesRepository
import com.steeshock.android.streetworkout.presentation.viewmodels.PlacesViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Before
import org.mockito.Mockito.*

class PlacesViewModelTest {

    private val mockPlacesViewModelRepository: MockApiPlacesRepository =
        mock(MockApiPlacesRepository::class.java)
    private val mockCategoriesViewModelRepository: MockApiCategoriesRepository =
        mock(MockApiCategoriesRepository::class.java)

    private lateinit var places: MutableList<Place>
    private lateinit var categories: MutableList<Category>
    private lateinit var placesViewModel: PlacesViewModel

    @Before
    fun initializePlaces() {
        places = mutableListOf(Place())
        categories = mutableListOf(Category())
        placesViewModel = PlacesViewModel(
            placesRepository = mockPlacesViewModelRepository,
            categoriesRepository = mockCategoriesViewModelRepository,
        )
    }

    @Test
    fun `verify insert Places and Categories`() {
        placesViewModel.insertPlaces(places)
        placesViewModel.insertCategories(categories)
        runBlocking {
            verify(mockPlacesViewModelRepository).insertAllPlaces(places)
            verify(mockCategoriesViewModelRepository).insertAllCategories(categories)
        }
    }
}