package dev.ronnie.github.imagepicker

interface SendImageUri {

    fun pickFromStorage(imageResult: ImageResult)
    fun takeFromCamera(imageResult: ImageResult)
}