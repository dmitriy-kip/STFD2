package com.example.stfd;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.thorny.photoeasy.OnPictureReady;
import com.thorny.photoeasy.PhotoEasy;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Header;

public class PhotoSenderActivity extends AppCompatActivity {
    ImageView imageView;
    PhotoEasy photoEasy;
    List<Bitmap> smallImages = new ArrayList<>();
    MyAdapter myAdapter;
    Context context = this;
    ArrayList<File> listImages = new ArrayList<>();
    Button sendPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                RequestParams params = new RequestParams();
                try {
                    params.put("file_upload", listImages.get(0));
                    params.put("param1", "Test");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                client.post("https://172.16.0.227:600/api/upload_file",params,new TextHttpResponseHandler(){
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("ответ", "не ок " + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.e("ответ","все ок " + responseString);
                    }

                   /* @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                        // Handle resulting parsed JSON response here
                        Log.e("ответ","все ок " + response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Log.e("ответ", "не ок " + errorResponse.toString());
                    }*/
                });
            }
        });

        Button cameraGo = findViewById(R.id.make_photo);
        cameraGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEasy.startActivityForResult(PhotoSenderActivity.this);
            }
        });


        String url = "https://172.16.0.227:600/api/upload_file";
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("rsz", "8");
        client.get(url, params, new TextHttpResponseHandler() {
            /*@Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Root JSON in response is an dictionary i.e { "data : [ ... ] }
                // Handle resulting parsed JSON response here
                Log.e("ответ","все ок");
            }*/

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.e("ответ","не ок");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("ответ","все ок");
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

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listImages.add(f);
                //Log.e("проверка листа", listImages.size() + "" + f.toString());
                sendPhoto.setVisibility(View.VISIBLE);
            }
        });

    }


}
