package com.example.mvvm_news_app.util


// Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
// classes to wrap and manage the responses

// When using sealed classes are like a superEnum in which it can only be one type at the time
// And it's used with a "when" to make exhaustive search
// if the response receives something like a value or code data class or class can be used
// data Class is used it the class is only needed for hold data
// if the class has nothing to receive use object like the example below
sealed class Resource<T>{
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val data: T? = null, val code: Int? = null) : Resource<T>()
    data class NetworkError<T>(val cause: String? = null): Resource<T>()
    class Loading<T> : Resource<T>()
}