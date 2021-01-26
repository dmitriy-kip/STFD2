package com.example.stfd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.stfd.Adapters.HistoryAdapter;
import com.example.stfd.Adapters.MyAdapter;
import com.example.stfd.DataBase.AppDataBase;
import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.DataBase.SingletonAppDB;
import com.example.stfd.MyPhotoEasy.OnPictureReady;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
//import com.thorny.photoeasy.OnPictureReady;
import com.example.stfd.MyPhotoEasy.PhotoEasy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class PhotoSenderActivity extends AppCompatActivity implements PhotoSenderFragment.OnSelectedButtonListener, HistoryFragment.OnSelectedButtonListenerHistory{

    private PhotoEasy photoEasy;
    private MyAdapter myAdapter;
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private ImageView sendPhoto;
    private final ArrayList<File> listImages = new ArrayList<>();
    private final Context context = this;
    private FragmentManager fm;
    private PhotoSenderFragment photoSenderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        FragmentManager fm = getSupportFragmentManager();
        photoSenderFragment = new PhotoSenderFragment();;
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, photoSenderFragment, "photoSenderFragment");
        ft.commit();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!Objects.equals(getSupportActionBar().getTitle(), getString(R.string.app_name))) {
                try {
                    fm.popBackStack();
                } catch (Exception e) {

                }
            }
            photoSenderFragment.onKeyDown();
            //invalidateOptionsMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onFragmentInteraction(String title, int index) {
        getSupportActionBar().setTitle(title);
        switch (index){
            case 1:
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                break;
            case 2:
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }

    }


}
