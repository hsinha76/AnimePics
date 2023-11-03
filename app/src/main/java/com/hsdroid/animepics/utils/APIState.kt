package com.hsdroid.animepics.utils

sealed class APIState<out T> {
    object EMPTY : APIState<Nothing>()
    object LOADING : APIState<Nothing>()
    class SUCCESS<out T>(val data: T) : APIState<T>()
    class FAILURE(val throwable: Throwable) : APIState<Nothing>()
}
