package dev.ronnie.github.imagepicker

sealed class ImageResult <out T>{
    data class Success<out T>(val value: T) : ImageResult<T>()
    data class Failure(val errorString: String): ImageResult<Nothing>()
}
