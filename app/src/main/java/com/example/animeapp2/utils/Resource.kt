package com.example.animeapp2.utils

sealed class Resource<T>(
    val message : String? = null,
    val data : T? = null
) {
    class Success<T>(data: T): Resource<T>(data = data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(message, data)
    class Loading<T> : Resource<T>()
}