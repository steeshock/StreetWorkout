package com.steeshock.streetworkout.presentation.delegates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.steeshock.streetworkout.presentation.viewStates.addPlace.AddPlaceViewState

/**
 * Delegate interface for handling view state
 */
interface ViewStateDelegate<T> {

    val viewState: LiveData<T>

    fun updateViewState(postValue: Boolean = false, updateBlock: T.() -> T)
}

class ViewStateDelegateImpl<T> (private val factory: () -> T): ViewStateDelegate<T> {

    override val viewState: LiveData<T>
        get() = mutableViewState

    private val mutableViewState: MutableLiveData<T> = MutableLiveData()

    override fun updateViewState(postValue: Boolean, updateBlock: T.() -> T) {
        val currentState = mutableViewState.value ?: factory.invoke()
        val newState = currentState.run { updateBlock() }

        if (postValue) {
            mutableViewState.postValue(newState)
        } else {
            mutableViewState.value = newState
        }
    }
}