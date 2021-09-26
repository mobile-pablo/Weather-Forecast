package com.company.elverano.utils

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    //Klasa ponizej moze wziasc kazdy obiekt i rozszerze jednoczesnie sealed klase
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null) : Resource<T>(data, throwable)
}