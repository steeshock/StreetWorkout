package com.steeshock.streetworkout.presentation.delegates

import androidx.lifecycle.LiveData
import com.steeshock.streetworkout.presentation.viewStates.SingleLiveEvent

/**
 * Delegate interface for sending single view events
 */
interface ViewEventDelegate<T> {

    val viewEvent: LiveData<T>

    fun sendViewEvent(event: T)

    fun postViewEvent(event: T)
}

class ViewEventDelegateImpl<T> : ViewEventDelegate<T> {

    override val viewEvent: LiveData<T>
        get() = mutableViewEvent

    private val mutableViewEvent = SingleLiveEvent<T>()

    override fun sendViewEvent(event: T) {
        event?.let {
            mutableViewEvent.value = it
        }
    }

    override fun postViewEvent(event: T) {
        event?.let {
            mutableViewEvent.postValue(it)
        }
    }
}