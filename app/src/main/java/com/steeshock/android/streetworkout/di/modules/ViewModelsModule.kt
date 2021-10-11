package com.steeshock.android.streetworkout.di.modules

import com.steeshock.android.streetworkout.data.repository.interfaces.ICategoriesRepository
import com.steeshock.android.streetworkout.data.repository.interfaces.IPlacesRepository
import com.steeshock.android.streetworkout.viewmodels.*
import dagger.Module
import dagger.Provides

@Module
class ViewModelsModule {

    @Provides
    fun providePlacesViewModel(
        placesRepository: IPlacesRepository,
        categoriesRepository: ICategoriesRepository,
    ): PlacesViewModel {
        return PlacesViewModel(
            placesRepository,
            categoriesRepository,
        )
    }

    @Provides
    fun provideFavoritePlacesViewModel(
        placesRepository: IPlacesRepository,
    ): FavoritePlacesViewModel {
        return FavoritePlacesViewModel(
            placesRepository,
        )
    }

    @Provides
    fun provideProfileViewModel(
        placesRepository: IPlacesRepository,
    ): ProfileViewModel {
        return ProfileViewModel(
            placesRepository,
        )
    }

    @Provides
    fun provideAddPlaceViewModel(
        placesRepository: IPlacesRepository,
        categoriesRepository: ICategoriesRepository,
    ): AddPlaceViewModel {
        return AddPlaceViewModel(
            placesRepository,
            categoriesRepository
        )
    }

    @Provides
    fun provideMapViewModel(
        placesRepository: IPlacesRepository,
    ): MapViewModel {
        return MapViewModel(
            placesRepository,
        )
    }
}