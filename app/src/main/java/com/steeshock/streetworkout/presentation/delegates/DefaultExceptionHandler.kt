package com.steeshock.streetworkout.presentation.delegates

import kotlinx.coroutines.CoroutineExceptionHandler

interface ExceptionHandler {
    val defaultExceptionHandler: ((Throwable) -> Unit) -> CoroutineExceptionHandler
}

class DefaultExceptionHandler : ExceptionHandler {
    override val defaultExceptionHandler: ((Throwable) -> Unit) -> CoroutineExceptionHandler
        get() = {
            CoroutineExceptionHandler { _, throwable ->
                it.invoke(throwable)
            }
        }
}