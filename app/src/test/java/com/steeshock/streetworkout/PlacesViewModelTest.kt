package com.steeshock.streetworkout

import com.steeshock.streetworkout.data.repository.dto.CategoryDto
import com.steeshock.streetworkout.data.repository.dto.PlaceDto
import com.steeshock.streetworkout.data.repository.implementation.mockApi.SimpleApiCategoriesRepository
import com.steeshock.streetworkout.presentation.viewmodels.PlacesViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class PlacesViewModelTest {

    private val simplePlacesViewModelRepository: SimpleApiPlacesRepository =
        mock(SimpleApiPlacesRepository::class.java)
    private val simpleCategoriesViewModelRepository: SimpleApiCategoriesRepository =
        mock(SimpleApiCategoriesRepository::class.java)

    private lateinit var placeDtos: MutableList<PlaceDto>
    private lateinit var categoryDtos: MutableList<CategoryDto>
    private lateinit var placesViewModel: PlacesViewModel

    @Before
    fun initializePlaces() {
//        places = mutableListOf(Place())
//        categories = mutableListOf(Category())
//        placesViewModel = PlacesViewModel(
//            placesRepository = mockPlacesViewModelRepository,
//            categoriesRepository = mockCategoriesViewModelRepository,
//        )
    }

    @Test
    fun `verify insert Places and Categories`() {
//        placesViewModel.insertPlaces(places)
//        placesViewModel.insertCategories(categories)
//        runBlocking {
//            verify(mockPlacesViewModelRepository).insertAllPlaces(places)
//            verify(mockCategoriesViewModelRepository).insertAllCategories(categories)
//        }
    }
}