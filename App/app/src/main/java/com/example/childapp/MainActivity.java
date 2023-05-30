package com.example.childapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.example.childapp.model.AppHelper;
import com.example.childapp.model.VolleyMultipartRequest;
import com.example.childapp.model.VolleySingleton;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int CAM_CODE = 1234, STORAGE_CODE = 1000;
    ImageButton inputImg;
    ImageView outputImg;
    Button submit;
    private ProgressBar prg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prg = findViewById(R.id.progressbar);
        inputImg = findViewById(R.id.dadImg);
        outputImg = findViewById(R.id.momImg);
        submit = findViewById(R.id.submit);
        inputImg.setOnClickListener(this::onImageClick);
        submit.setOnClickListener(this::postData);
    }

    public void postData(View v) {
        if (inputImg.getDrawable() != null) {
            prg.setVisibility(View.VISIBLE);
            String url = "http://10.0.2.2:8000/api/";
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    response -> Toast.makeText(getBaseContext(), "Upload successfully!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(getBaseContext(), "Upload failed!" + error, Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("Img", new DataPart("Img.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), inputImg.getDrawable()), "image/jpeg"));
                    return params;
                }
            };
            VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        }
    }

    public void onImageClick(View v) {
        launcher.launch(ImagePicker.Companion.with(MainActivity.this)
                .maxResultSize(1080, 1080, true)
                .galleryMimeTypes(new String[]{"image/png","image/jpg", "image/jpeg"})
                .provider(ImageProvider.GALLERY)
                .createIntent());
    }

    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    inputImg.setImageURI(uri);
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    ImagePicker.Companion.getError(result.getData());
                }
            });
}