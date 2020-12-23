package com.example.stfd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class PhotoSenderActivity extends AppCompatActivity {

    //ImageView imageView;
    private PhotoEasy photoEasy;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private MyAdapter myAdapter;
    private final Context context = this;
    private ArrayList<File> listImages = new ArrayList<>();
    private ImageView sendPhoto;
    private String numDoc;
    private String notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        final RelativeLayout progressCircle = findViewById(R.id.progress_circular1);

        //imageView = findViewById(R.id.image);
        sendPhoto = findViewById(R.id.sendToServer);
        final RelativeLayout previewPhoto = findViewById(R.id.preview_photo);

        final RecyclerView recyclerView = findViewById(R.id.recycle_list);
        myAdapter = new MyAdapter(this, bitmapList, sendPhoto, previewPhoto);
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
                previewPhoto.setVisibility(View.INVISIBLE);
            }
        });


        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressCircle.setVisibility(View.VISIBLE);
                final EditText editNumDoc = findViewById(R.id.edit_num_doc);
                final EditText editNotice = findViewById(R.id.edit_notice);
                numDoc = editNumDoc.getText().toString();
                notice = editNotice.getText().toString();

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
                params.put("income_num", numDoc);
                params.put("file_desc", notice);
                client.post("https://172.16.0.227:600/api/upload_file",params,new TextHttpResponseHandler(){
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(PhotoSenderActivity.this, "Не удалось отправить", Toast.LENGTH_LONG).show();
                        progressCircle.setVisibility(View.INVISIBLE);

                        Log.e("ответ", "не ок " + responseString);
                    }
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        editNumDoc.getText().clear();
                        editNotice.getText().clear();
                        myAdapter.clear();
                        Toast.makeText(PhotoSenderActivity.this, "Информация успешено отправлена", Toast.LENGTH_LONG).show();
                        sendPhoto.setVisibility(View.INVISIBLE);
                        progressCircle.setVisibility(View.INVISIBLE);

                        Log.e("ответ","все ок " + responseString);
                    }

                });
            }
        });

        ImageView cameraGoButton = findViewById(R.id.make_photo);
        cameraGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEasy.startActivityForResult(PhotoSenderActivity.this);
            }
        });

        ImageView galleryOpen = findViewById(R.id.gallery);
        galleryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
    }

    private void invisibleSendPhotoButton() {
        sendPhoto.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoEasy.onActivityResult(requestCode, resultCode, new OnPictureReady() {
            @Override
            public void onFinish(Bitmap thumbnail) {
                addPhoto(thumbnail);
            }
        });

        if (resultCode == RESULT_OK) {
            if(requestCode == 2) {
                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    Toast.makeText(PhotoSenderActivity.this, "Не удалось получить фаил", Toast.LENGTH_LONG).show();
                    return;
                }
                addPhoto(bitmap);
            }
        }
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

    private void addPhoto(Bitmap bitmap){
        bitmapList.add(bitmap);
        myAdapter.notifyItemInserted(bitmapList.size()-1);
        sendPhoto.setVisibility(View.VISIBLE);
    }
    
}
