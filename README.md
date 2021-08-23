 # Ronnie-Image-Picker

Asks for Camera and storage permission and return uri of the images taken or picked from the gallery.

Min Api Level: 16

Build System : [Gradle](https://gradle.org/)

 [![](https://jitpack.io/v/ronnieotieno/Ronnie-Image-Picker.svg)](https://jitpack.io/#ronnieotieno/Ronnie-Image-Picker)

## Getting started

You don't need to declare the permissions in the manifests.

Add this in your root build.gradle at the end of repositories:

```groovy
allprojects {
    repositories {
        ...
       maven { url 'https://jitpack.io' }
    }
}
```
Add this to dependencies:

```groovy
dependencies {
	     implementation 'com.github.ronnieotieno:Ronnie-Image-Picker:0.8.0' //Add latest version
	}
```
Example in code, Kotlin:

```kotlin
  lateinit var imagePicker : ImagePicker
  
  //Make sure that you initialize it at Oncreate
   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

	 //activity or fragment
	 imagePicker = ImagePicker(this)

    //Camera
      imagePicker.takeFromCamera { imageResult ->
          when (imageResult) {
              is ImageResult.Success -> {
                  val uri = imageResult.value
                  imageView.setImageURI(uri)
              }
              is ImageResult.Failure -> {
                  val errorString = imageResult.errorString
                  Toast.makeText(this@MainActivity, errorString, Toast.LENGTH_LONG).show()
              }
          }

      }
     
     //Gallery
      imagePicker.pickFromStorage { imageResult ->
          when (imageResult) {
              is ImageResult.Success -> {
                  val uri = imageResult.value
                  imageView.setImageURI(uri)
              }
              is ImageResult.Failure -> {
                  val errorString = imageResult.errorString
                  Toast.makeText(this@MainActivity, errorString, Toast.LENGTH_LONG).show()
              }
          }

      }
```
Example in code,Java:

```java
  ImagePicker imagePicker;
    
    //Make sure that you initialize it at Oncreate
    @Override
     protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //activity or fragment
           imagePicker = new ImagePicker(this);
           
       
       //Gallery
         imagePicker.pickFromStorage(imageResult -> {
         if(imageResult instanceof ImageResult.Success){
             Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
             imageView.setImageURI(uri);
         }else{
             String errorString = ((ImageResult.Failure) imageResult).getErrorString();
             Toast.makeText(MainActivity2.this, errorString, Toast.LENGTH_LONG).show();
         }
         return null;
     }));
        
       //Camera
         imagePicker.takeFromCamera(imageResult -> {
         if(imageResult instanceof ImageResult.Success){
             Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
             imageView.setImageURI(uri);
         }else{
             String errorString = ((ImageResult.Failure) imageResult).getErrorString();
             Toast.makeText(MainActivity2.this, errorString, Toast.LENGTH_LONG).show();
         }
         return null;
     }));

```
