package com.example.stfd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/*import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;*/
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.thorny.photoeasy.OnPictureReady;
import com.thorny.photoeasy.PhotoEasy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class PhotoSenderActivity extends AppCompatActivity {

    //ImageView imageView;
    private PhotoEasy photoEasy;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private MyAdapter myAdapter;
    private final Context context = this;
    private ArrayList<File> listImages = new ArrayList<>();
    private Button sendPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        //imageView = findViewById(R.id.image);
        sendPhoto = findViewById(R.id.sendToServer);
        final RelativeLayout relativeLayout = findViewById(R.id.preview_photo);

        final RecyclerView recyclerView = findViewById(R.id.recycle_list);
        myAdapter = new MyAdapter(this, bitmapList, sendPhoto, relativeLayout);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        photoEasy = PhotoEasy.builder()
                .setActivity(this)
                .setStorageType(PhotoEasy.StorageType.media)
                .build();

        ImageView bigCrossView = findViewById(R.id.big_cross);
        bigCrossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeLayout.setVisibility(View.INVISIBLE);
            }
        });


        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i< bitmapList.size(); i++){
                    fillImageToList(bitmapList.get(i),  listImages);
                }
                File[] files = new File[listImages.size()];
                listImages.toArray(files);
                AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                RequestParams params = new RequestParams();
                try {
                    params.put("file_upload[]", files);
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
                });
            }
        });

        Button cameraGoButton = findViewById(R.id.make_photo);
        cameraGoButton.setOnClickListener(new View.OnClickListener() {
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
                bitmapList.add(thumbnail);
                myAdapter.notifyItemInserted(bitmapList.size()-1);
                sendPhoto.setVisibility(View.VISIBLE);
            }
        });
    }

    private byte[] resizeBitmapData (Bitmap bitmap, ByteArrayOutputStream outputStream, int maxSize) {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] bitmapdata = outputStream.toByteArray();
        int bitmapdataSize = bitmapdata.length;
        while (bitmapdataSize > maxSize){
            bitmap = Bitmap.createScaledBitmap(bitmap,(int)( bitmap.getWidth()*0.95), (int)( bitmap.getHeight()*0.95), true);
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bitmapdata = outputStream.toByteArray();
            bitmapdataSize = bitmapdata.length;
        }
        return bitmapdata;
    }

    private String createFileName(){
        UUID uuid = UUID.randomUUID();
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String fileName = uuid.toString() + "_" + date + ".jpg";
        return fileName;
    }

    private void fillImageToList (Bitmap bitmap, ArrayList<File> listImages) {
        File f = new File(context.getCacheDir(), createFileName());
        try {
            f.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();
            int bitmapdataSize = bitmapdata.length;
            if (bitmapdataSize > 2000000){
                bitmapdata = resizeBitmapData(bitmap, bos, 2000000);
            }
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        listImages.add(f);
    }
}
