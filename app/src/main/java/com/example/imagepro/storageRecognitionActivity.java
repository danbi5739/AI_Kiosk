package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;

public class storageRecognitionActivity extends AppCompatActivity {

    private Button select_image;
    private ImageView recognizeImage_button;
    private ImageView image_view;
    private TextView text_view;
    int Selected_Picture = 200;

    private TextRecognizer textRecognizer;

    private String show_image_or_text = "image";
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_recognition);

        image_view = findViewById(R.id.image_view);

        select_image = findViewById(R.id.select_image);
        select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_chooser();
            }
        });

        textRecognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());

        text_view = findViewById(R.id.text_view);
        text_view.setVisibility(View.GONE);

        recognizeImage_button = findViewById(R.id.recognizeImage_button);
        recognizeImage_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    recognizeImage_button.setColorFilter(Color.DKGRAY);
                    return true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    recognizeImage_button.setColorFilter(Color.WHITE);
                    if (show_image_or_text == "text") {
                        text_view.setVisibility(View.GONE);
                        image_view.setVisibility(View.VISIBLE);
                        show_image_or_text = "image";
                    }
                    else {
                        text_view.setVisibility(View.VISIBLE);
                        image_view.setVisibility(View.GONE);
                        show_image_or_text = "text";
                    }
                    return true;
                }
                return false;
            }
        });
    }

    void image_chooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(i, "Selected Picture"), Selected_Picture);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Selected_Picture) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    Log.d("storage_Activity", "Output Uri: " + selectedImageUri);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    InputImage image = InputImage.fromBitmap(bitmap, 0);

                    Task<Text> result = textRecognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            text_view.setText(text.getText());
                            Log.d("Storage_activity", "Out: " + text.getText());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

                    image_view.setImageBitmap(bitmap);
                }
            }
        }
    }
}