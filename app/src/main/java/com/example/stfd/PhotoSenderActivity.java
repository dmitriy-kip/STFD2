package com.example.stfd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thorny.photoeasy.OnPictureReady;
import com.thorny.photoeasy.PhotoEasy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhotoSenderActivity extends AppCompatActivity {
    ImageView imageView;
    PhotoEasy photoEasy;
    List<Bitmap> smallImages = new ArrayList<>();
    MyAdapter myAdapter;
    Context context = this;
    ArrayList<String> listImages = new ArrayList<>();
    Button sendPhoto;
    String encodeImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new NukeSSLCerts();
        NukeSSLCerts.nuke();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        RecyclerView recyclerView = findViewById(R.id.recycle_list);
        myAdapter = new MyAdapter(this, smallImages);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        photoEasy = PhotoEasy.builder()
                .setActivity(this)
                .setStorageType(PhotoEasy.StorageType.media)
                .build();

        imageView = findViewById(R.id.image);

        sendPhoto = findViewById(R.id.sendToServer);
        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest request = new StringRequest(Request.Method.POST, "https://172.16.0.227:600/api/upload_file", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("otvet", "ok" + response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("otvet", "ok" + error);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("file_upload", listImages.get(0));
                        return map;
                    }
                };
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(request);
            }
        });

        Button cameraGo = findViewById(R.id.make_photo);
        cameraGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEasy.startActivityForResult(PhotoSenderActivity.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoEasy.onActivityResult(requestCode, resultCode, new OnPictureReady() {
            @Override
            public void onFinish(Bitmap thumbnail) {
                smallImages.add(thumbnail);
                myAdapter.notifyItemInserted(smallImages.size()-1);

                File f = new File(context.getCacheDir(), "temporary_file.jpg");
                try {
                    f.createNewFile();

                    Bitmap bitmap = thumbnail;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                    byte[] bitmapdata = bos.toByteArray();
                    encodeImageString = android.util.Base64.encodeToString(bitmapdata, Base64.DEFAULT);

                    /*FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();*/
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listImages.add(encodeImageString);
                //Log.e("проверка листа", listImages.size() + "" + f.toString());
                sendPhoto.setVisibility(View.VISIBLE);
            }
        });

    }


}
