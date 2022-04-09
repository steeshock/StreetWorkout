package com.steeshock.streetworkout.data.api

interface APIResponse<Type> {

    fun onSuccess(result: Type?)

    fun onError(t: Throwable)
}
