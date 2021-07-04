# Ronnie-Image-Picker

Makes your work easier by asking for permissions at runtime and take images either from the camera or pick from the gallery. Returns uri for the images taken or picked.

Min Api Level: 16

Build System : [Gradle](https://gradle.org/)

Latest release [![](https://jitpack.io/v/ronnieotieno/Ronnie-Image-Picker.svg)](https://jitpack.io/#ronnieotieno/Ronnie-Image-Picker)

## Getting started

You don't need to declare the permissions in the manifests.

Add this in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add this to dependencies:

```
dependencies {
	        implementation 'com.github.ronnieotieno:Ronnie-Image-Picker:0.2.0' //Add latest version
	}
```
Example in code:

```
 //activity
  val imagePicker = ImagePicker(this)
  //fragment
   val imagePicker = ImagePicker(requreActivity())

        openCameraBtn.setOnClickListener {

            imagePicker.takeFromCamera(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(this@MainActivity, reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    imageView.setImageURI(uri)
                }
            })
        }
        openGalleryBtn.setOnClickListener {

            imagePicker.pickFromStorage(object : ImageResult {
                override fun onFailure(reason: String) {
                    Toast.makeText(this@MainActivity, reason, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(uri: Uri) {
                    imageView.setImageURI(uri)
                }
            })
        }
```
