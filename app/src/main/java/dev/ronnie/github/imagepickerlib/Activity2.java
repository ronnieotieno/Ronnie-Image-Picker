package dev.ronnie.github.imagepickerlib;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dev.ronnie.github.imagepicker.ImagePicker;
import dev.ronnie.github.imagepicker.ImageResult;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class Activity2 extends AppCompatActivity {

    ImageView imageView;
    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagePicker = new ImagePicker(this);

        Button camera = findViewById(R.id.openCamera);
        imageView = findViewById(R.id.imageView);
        Button storage = findViewById(R.id.openStorage);
        Button openSelection = findViewById(R.id.openSelection);


        camera.setOnClickListener(v -> imagePicker.takeFromCamera(imageCallBack()));

        storage.setOnClickListener(v -> imagePicker.pickFromStorage(imageCallBack()));

        openSelection.setOnClickListener(v -> imagePicker.selectSource(imageCallBack()));


    }

    private Function1<ImageResult<? extends Uri>, Unit> imageCallBack() {
        return imageResult -> {
            if (imageResult instanceof ImageResult.Success) {
                Uri uri = ((ImageResult.Success<Uri>) imageResult).getValue();
                imageView.setImageURI(uri);
            } else {
                String errorString = ((ImageResult.Failure) imageResult).getErrorString();
                Toast.makeText(Activity2.this, errorString, Toast.LENGTH_LONG).show();
            }
            return null;
        };
    }
}
