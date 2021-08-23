package dev.ronnie.github.imagepicker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.lang.NullPointerException

class ImagePicker{
    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraLauncher: ActivityResultLauncher<Uri>? = null
    private var storagePermission: ActivityResultLauncher<String>?  = null
    private var cameraPermission: ActivityResultLauncher<String>? = null
    private var activity: AppCompatActivity? = null
    private var fragment: Fragment? = null
    private var context: Context

    private lateinit var takenImageUri: Uri
    private var callback: ((imageResult: ImageResult<Uri>)-> Unit)? = null

    constructor(activity: AppCompatActivity) {
        this.activity = activity
        context = this.activity!!.applicationContext
        registerActivityForResults()
    }

    constructor(fragment: Fragment) {
        this.fragment = fragment
        context = this.fragment!!.requireContext()
        registerActivityForResults()
    }

    private fun  registerActivityForResults(){
        //Camera permission
       cameraPermission = (activity
            ?: fragment)?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            when {
                granted -> {
                    launchCamera()
                }
                else -> callback?.invoke( ImageResult.Failure("Camera Permission denied"))

            }
        }

        //Storage permission
        storagePermission = (activity ?: fragment)?.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            when {
                granted -> {
                    launchGallery()
                }

                else ->  callback?.invoke( ImageResult.Failure("Storage Permission denied"))

            }
        }
        //Launch camera
       cameraLauncher =
            (activity ?: fragment)?.registerForActivityResult(
                ActivityResultContracts.TakePicture()
            ) { result ->
                if (result) {
                    callback?.invoke(ImageResult.Success(takenImageUri))
                } else {
                    callback?.invoke(ImageResult.Failure( "Camera Launch Failed"))
                }
            }

        //launch gallery
        galleryLauncher = (activity ?: fragment)?.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val uri = result.data?.data
                if (uri != null) {
                    callback?.invoke(ImageResult.Success(uri))
                } else {
                    callback?.invoke(ImageResult.Failure("Gallery Launch Failed"))
                }
            } else {
                callback?.invoke(ImageResult.Failure("Gallery Launch Failed"))
            }
        }
    }

    private fun launchCamera() {
        try {
            val takenImageFile = File(context.externalCacheDir, "takenImage${(1..1000).random()}.jpg")
            takenImageUri = FileProvider.getUriForFile(
                context, context.packageName.plus(".ronnie_image_provider"), takenImageFile
            )
            cameraLauncher!!.launch(takenImageUri)
        } catch (exception: Exception) {
            callback?.invoke(ImageResult.Failure("Camera Launch Failed"))
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        Intent.createChooser(intent, "Select Image")
        galleryLauncher?.launch(intent)
    }

    fun pickFromStorage(callback: ((imageResult: ImageResult<Uri>) -> Unit)) {
        this.callback = callback
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

    fun takeFromCamera(callback: ((imageResult: ImageResult<Uri>) -> Unit)) {
        this.callback = callback
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

    private fun showWhyPermissionNeeded(permission: String, name: String) {
        MaterialAlertDialogBuilder(activity ?: fragment!!.requireContext())
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