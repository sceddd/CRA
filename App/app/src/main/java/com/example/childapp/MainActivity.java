package com.example.childapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    private static final int PERMISSION_CODE = 1234;
    private ImageButton temp;
    private Uri image_uri;
    private final String TAG = "111111111111111";
    ImageButton dadImg;
    ImageButton momImg;
    Button submit;
    private ProgressBar prg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prg = findViewById(R.id.progressbar);
        dadImg = findViewById(R.id.dadImg);
        momImg = findViewById(R.id.momImg);
        submit = findViewById(R.id.submit);
        dadImg.setOnClickListener(this::onImageClick);
        momImg.setOnClickListener(this::onImageClick);
        submit.setOnClickListener(this::postData);
    }
    public void postData(View v){
        if (dadImg.getDrawable() != null && momImg.getDrawable() != null){
            prg.setVisibility(View.VISIBLE);
            String url = "http://10.0.2.2:8000/api/";
            VolleyMultipartRequest multipartRequest= new VolleyMultipartRequest(Request.Method.POST, url,
                    response -> Toast.makeText(getBaseContext(), "Upload successfully!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(getBaseContext(), "Upload failed!", Toast.LENGTH_SHORT).show()){
                @Override
                protected Map<String, String> getParams(){
                    Map<String,String> params = new HashMap<>();
                    params.put("tittle", "upload Image");
                    return params;
                }
                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    params.put("dadImg", new DataPart("dadImg.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), dadImg.getDrawable()), "image/jpeg"));
                    params.put("momImg", new DataPart("momImg.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), momImg.getDrawable()), "image/jpeg"));
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
            requestPermissions(permissions, PERMISSION_CODE);
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
         if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
