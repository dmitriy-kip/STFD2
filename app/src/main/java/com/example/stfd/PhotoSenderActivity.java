package com.example.stfd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.Objects;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

public class PhotoSenderActivity extends AppCompatActivity implements PhotoSenderFragment.OnSelectedButtonListener, HistoryFragment.OnSelectedButtonListenerHistory{

    private PhotoEasy photoEasy;
    private MyAdapter myAdapter;
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private ImageView sendPhoto;
    private final ArrayList<File> listImages = new ArrayList<>();
    private final Context context = this;
    private FragmentManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        final RelativeLayout previewPhoto = findViewById(R.id.preview_photo);
        sendPhoto = findViewById(R.id.sendToServer);

        final RecyclerView recyclerView = findViewById(R.id.recycle_list);
        myAdapter = new MyAdapter(this, bitmapList, sendPhoto, previewPhoto);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        photoEasy = PhotoEasy.builder()
                .setActivity(this)
                .setStorageType(PhotoEasy.StorageType.media)
                .build();


    }

    @Override
    public void onSendPhoto() {
        if (!Utils.isOnline(context)){
            Toast.makeText(PhotoSenderActivity.this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
            return;
        }

        final RelativeLayout progressCircle = findViewById(R.id.progress_circular1);
        final ImageView sendPhoto = findViewById(R.id.sendToServer);

        progressCircle.setVisibility(View.VISIBLE);
        final EditText editNumDoc = findViewById(R.id.edit_num_doc);
        final EditText editNotice = findViewById(R.id.edit_notice);
        String numDoc = editNumDoc.getText().toString();
        String notice = editNotice.getText().toString();

        for (int i = 0; i< bitmapList.size(); i++){
            Utils.fillImageToList(bitmapList.get(i), listImages, context);
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
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                editNumDoc.getText().clear();
                editNotice.getText().clear();
                myAdapter.clear();
                listImages.clear();
                Toast.makeText(PhotoSenderActivity.this, "Информация успешено отправлена", Toast.LENGTH_LONG).show();
                sendPhoto.setVisibility(View.INVISIBLE);
                progressCircle.setVisibility(View.INVISIBLE);

                Log.e("ответ","все ок " + responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(PhotoSenderActivity.this, "Не удалось отправить", Toast.LENGTH_LONG).show();
                progressCircle.setVisibility(View.INVISIBLE);

                Log.e("ответ", "не ок " + responseString);
            }

        });

    }

    @Override
    public void onCameraGoButton() {
        photoEasy.startActivityForResult(PhotoSenderActivity.this);
    }

    @Override
    public void onGalleryOpen() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    @Override
    public void goToHistory() {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        HistoryFragment historyFragment = new HistoryFragment();
        ft.replace(R.id.container, historyFragment, "historyFragment");
        ft.addToBackStack(null);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            RelativeLayout previewPhoto = findViewById(R.id.preview_photo);
            if (previewPhoto.getVisibility() == View.VISIBLE)
                previewPhoto.setVisibility(View.INVISIBLE);
            fm.popBackStack();
            invalidateOptionsMenu();
            return true;
        }
        return true;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }*/

    /*@SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                Toast.makeText(this, "Работает", Toast.LENGTH_LONG).show();
                return true;
            case R.id.history:
                fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                HistoryFragment historyFragment = new HistoryFragment();
                ft.replace(R.id.container, historyFragment, "historyFragment");
                ft.addToBackStack(null);
                ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                ft.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    private void addPhoto(Bitmap bitmap){
        bitmapList.add(bitmap);
        myAdapter.notifyItemInserted(bitmapList.size()-1);
        sendPhoto.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragmentInteraction(String title) {
        getSupportActionBar().setTitle(title);
    }
}
