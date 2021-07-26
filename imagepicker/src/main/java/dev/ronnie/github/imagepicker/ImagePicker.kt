package dev.ronnie.github.imagepicker

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.lang.NullPointerException

class ImagePicker : SendImageUri {
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null

    constructor(activity: AppCompatActivity)  {
        this.activity = activity
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
    }

    init {
        if (activity == null && fragment == null) {
            throw NullPointerException("Make sure that either Activity or Fragment isn't empty when initializing ImagePicker")
        }
    }

    private lateinit var takenImageUri: Uri
    private lateinit var imageResult: ImageResult
    private val context = activity?.applicationContext ?: fragment?.requireContext()!!


    override fun pickFromStorage(imageResult: ImageResult) {
        this.imageResult = imageResult

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                (activity ?: fragment!!.requireActivity()),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showWhyPermissionNeeded(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage")
        } else {
            storagePermission?.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

    }

    override fun takeFromCamera(imageResult: ImageResult) {
        this.imageResult = imageResult
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                (activity ?: fragment!!.requireActivity()),
                Manifest.permission.CAMERA
            )
        ) {
            showWhyPermissionNeeded(Manifest.permission.CAMERA, "Camera")
        } else {
            cameraPermission?.launch(Manifest.permission.CAMERA)
        }

    }

    //Camera permision
    private
    val cameraPermission = (activity
        ?: fragment)?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        when {
            granted -> {
                launchCamera()
            }
            else -> imageResult.onFailure("Camera Permission denied")

        }
    }

    //Storage permission
    private val storagePermission = (activity ?: fragment)?.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        when {
            granted -> {
                launchGallery()
            }

            else -> imageResult.onFailure("Storage Permission denied")

        }
    }

    //Launch camera
    private var cameraLauncher: ActivityResultLauncher<Uri?>? =
        (activity ?: fragment)?.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { result ->
            if (result) {
                imageResult.onSuccess(takenImageUri)
            } else {
                imageResult.onFailure("Camera Launch Failed")
            }
        }

    //launch gallery
    private val galleryLauncher = (activity ?: fragment)?.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
            val uri = result.data?.data
            if (uri != null) {
                imageResult.onSuccess(uri)
            } else {
                imageResult.onFailure("Gallery Launch Failed")
            }
        } else {
            imageResult.onFailure("Gallery Launch Failed")
        }
    }

    private fun launchCamera() {
        try {
            val takenImageFile = File(context.externalCacheDir, "takenImage.jpg")
            takenImageUri = FileProvider.getUriForFile(
                context, context.packageName.plus(".ronnie_image_provider"), takenImageFile
            )
            cameraLauncher!!.launch(takenImageUri)
        } catch (exception: Exception) {
            imageResult.onFailure("Camera Launch Failed")

        }

    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        Intent.createChooser(intent, "Select Image")
        galleryLauncher?.launch(intent)
    }

    private fun showWhyPermissionNeeded(permission: String, name: String) {
        AlertDialog.Builder(context)
            .setMessage("Permission needed. $name permission is required")
            .setPositiveButton(
                "Okay"
            ) { _, _ ->
                if (permission == Manifest.permission.CAMERA) {
                    cameraPermission?.launch(permission)
                } else {
                    storagePermission?.launch(permission)
                }

            }.create().show()

    }

}