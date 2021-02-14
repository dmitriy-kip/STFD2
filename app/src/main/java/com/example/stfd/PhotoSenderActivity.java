package com.example.stfd;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.stfd.Adapters.HistoryAdapter;
import com.example.stfd.Adapters.MyAdapter;
import com.example.stfd.Fragments.FirstScreenFragment;
import com.example.stfd.Fragments.HistoryFragment;
//import com.thorny.photoeasy.OnPictureReady;
import com.example.stfd.Fragments.HistorySaveDialog;
import com.example.stfd.Fragments.PhotoSenderFragment;
import com.example.stfd.Fragments.SettingsFragment;
import com.example.stfd.MyPhotoEasy.PhotoEasy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotoSenderActivity extends AppCompatActivity implements PhotoSenderFragment.OnSelectedButtonListener,
        HistoryFragment.OnSelectedButtonListenerHistory, HistorySaveDialog.NoticeDialogListener, HistoryAdapter.OnSelectedButtonItem,
        FirstScreenFragment.OnSelectedFirstScreenListener {

    private FragmentManager fm;
    private PhotoSenderFragment photoSenderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        SharedPreferences mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        String phoneNumber = "+7" + mSettings.getString("phone", "");

        fm = getSupportFragmentManager();
        if (!phoneNumber.equals("+7")) {
            photoSenderFragment = new PhotoSenderFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.container, photoSenderFragment, "photoSenderFragment");
            ft.commit();
        } else {
            FirstScreenFragment firstScreenFragment = new FirstScreenFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.container, firstScreenFragment, "firstScreenFragment");
            ft.commit();
        }

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
    public void goToSettings() {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        SettingsFragment settingsFragment = new SettingsFragment();
        ft.replace(R.id.container, settingsFragment, "settingsFragment");
        ft.addToBackStack(null);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }

    @Override
    public void onSelectItemHistory(String docNum, String notice, List<String> uris) {
        FragmentTransaction ft = fm.beginTransaction();
        photoSenderFragment = new PhotoSenderFragment();

        Bundle args = new Bundle();
        args.putString("numDoc", docNum);
        args.putString("notice", notice);
        args.putBoolean("history", true);
        String[] ar = (String[]) uris.toArray();
        args.putStringArray("photosUri", ar);
        photoSenderFragment.setArguments(args);

        ft.replace(R.id.container, photoSenderFragment, "historyFragment");
        //ft.addToBackStack(null);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }

    @Override
    public void goToSender(List<String> modules) {

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
            photoSenderFragment.onKeyDown(); //вызываем из фрагмента потому что иначе ловит нул эксепшн
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
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }

    }

    @Override
    public void onDialogPositiveClick() {
        photoSenderFragment.saveData();

    }
}
