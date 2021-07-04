package dev.ronnie.github.imagepicker

import android.net.Uri

interface ImageResult {
    fun onSuccess(uri: Uri)
    fun onFailure(
        reason: String,
    )
}
