package com.example.childapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.example.childapp.model.AppHelper;
import com.example.childapp.model.VolleyMultipartRequest;
import com.example.childapp.model.VolleySingleton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private static final int CAM_CODE = 1234,STORAGE_CODE = 1000;

    private ImageButton temp;
    private Uri image_uri;
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
    public void postData(View v){
        if (inputImg.getDrawable() != null){
            prg.setVisibility(View.VISIBLE);
            String url = "http://10.0.2.2:8000/api/";
            VolleyMultipartRequest multipartRequest= new VolleyMultipartRequest(Request.Method.POST, url,
                    response -> Toast.makeText(getBaseContext(), "Upload successfully!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(getBaseContext(), "Upload failed!"+error, Toast.LENGTH_SHORT).show()){
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
        temp = (ImageButton) v;
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_DENIED) {
            String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissions, CAM_CODE);
        }else {
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"new image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From the camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent_cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent_cam.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        launchCameraAcForResult.launch(intent_cam);
    }
    ActivityResultLauncher<Intent> launchCameraAcForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),rs-> {
        if (rs.getResultCode() == Activity.RESULT_OK){
            Intent intent = rs.getData();
            if (intent != null){
                temp.setImageURI(image_uri);
                temp.setClickable(false);
            }
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         if (requestCode == CAM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == STORAGE_CODE) {
             if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
             }
         }
    }

}
