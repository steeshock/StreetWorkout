package com.steeshock.streetworkout.presentation.viewStates

/**
 * Describes UI state of screens Places/Favorite Places
 */
data class PlacesViewState(

    val isLoading: Boolean = false,

    val emptyState: EmptyViewState = EmptyViewState.NOT_EMPTY,
)