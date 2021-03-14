package com.example.stfd;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.stfd.Adapters.HistoryAdapter;
import com.example.stfd.Adapters.MyAdapter;
import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.Fragments.FirstScreenFragment;
import com.example.stfd.Fragments.HistoryFragment;
//import com.thorny.photoeasy.OnPictureReady;
import com.example.stfd.Fragments.HistorySaveDialog;
import com.example.stfd.Fragments.PassportFragment;
import com.example.stfd.Fragments.PhotoSenderFragment;
import com.example.stfd.Fragments.SettingsFragment;
import com.example.stfd.MyPhotoEasy.PhotoEasy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import cz.msebera.android.httpclient.client.cache.Resource;

public class PhotoSenderActivity extends AppCompatActivity implements NavigationFragments {

    private FragmentManager fm;
    private PhotoSenderFragment photoSenderFragment;
    private SharedPreferences mSettings;
    private SharedPreferences.Editor editor;
    private PassportFragment passportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_sender);

        mSettings = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        String phoneNumber = "+7" + mSettings.getString("phone", "");

        fm = getSupportFragmentManager();
        if (!phoneNumber.equals("+7")) {
            goToPhotoSender(null, null, null);
        } else {
            goToFirstScreen();
        }

    }

    @Override
    public void goToPhotoSender(String docNum, String notice, List<String> uris) {
        FragmentTransaction ft = fm.beginTransaction();
        photoSenderFragment = new PhotoSenderFragment();

        Bundle args = new Bundle();
        args.putString("numDoc", docNum);
        args.putString("notice", notice);
        args.putBoolean("history", true);
        String[] ar = new String[0];
        if (uris != null)
            ar = (String[]) uris.toArray();
        args.putStringArray("photosUri", ar);
        photoSenderFragment.setArguments(args);

        ft.replace(R.id.container, photoSenderFragment, "historyFragment");
        //ft.addToBackStack(null);
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.commit();
    }

    @Override
    public void goToHistory(int indexModule) {
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        HistoryFragment historyFragment = new HistoryFragment();

        Bundle args = new Bundle();
        args.putInt("module", indexModule);
        historyFragment.setArguments(args);

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
    public void goToFirstScreen() {
        fm = getSupportFragmentManager();
        FirstScreenFragment firstScreenFragment = new FirstScreenFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, firstScreenFragment, "firstScreenFragment");
        ft.commit();
    }

    @Override
    public void goToPassport(String docNum, String notice, List<String> uris) {
        fm = getSupportFragmentManager();
        passportFragment = new PassportFragment();

        Bundle args = new Bundle();
        args.putString("numDoc", docNum);
        args.putString("notice", notice);
        args.putBoolean("history", true);
        String[] ar = new String[0];
        if (uris != null)
            ar = (String[]) uris.toArray();
        args.putStringArray("photosUri", ar);
        passportFragment.setArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.container, passportFragment, "passportFragment");
        ft.commit();
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void youAreExist(Set<String> modules, String authId) {
        editor = mSettings.edit();
        editor.putString("authId", authId);
        EditText pn = findViewById(R.id.phone_number);
        String phoneNumber = pn.getText().toString();

        for (String module : modules) {
            switch (module) {
                case "ЦООГ":
                    editor.putString("phone", phoneNumber);
                    editor.apply();
                    
                    goToPhotoSender(null,null,null);
                    return;
                case "Паспортный стол":
                    editor.putString("phone", phoneNumber);
                    editor.apply();

                    goToPassport(null, null, null);
                    return;
                default:
            }
        }
        Log.e("норм  ", modules.toString() + "\n" + authId);
    }

    @Override
    public void executeDialog(int response, int indexModule) {
        HistorySaveDialog historySaveDialog = new HistorySaveDialog();

        Bundle args1 = new Bundle();
        args1.putInt("response", response);
        args1.putInt("module", indexModule);
        historySaveDialog.setArguments(args1);

        historySaveDialog.show(getSupportFragmentManager(), "dialog");
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

    @SuppressLint("ResourceType")
    @Override
    public void onFragmentInteraction(String title, int index) {
        getSupportActionBar().setTitle(title);
        switch (index){
            case 1:
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.colorPrimary))));
                break;
            case 2:
                getSupportActionBar().show();
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case 3:
                getSupportActionBar().show();
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(getString(R.color.colorPassport))));

                break;
            case 4:
                getSupportActionBar().hide();
            default:
        }

    }

    @Override
    public void onDialogPositiveClick(int indexModule) {
        switch (indexModule){
            case 1:
                photoSenderFragment.saveData();
                break;
            case 2:
                passportFragment.saveData();
                break;
            default:
        }
    }
}
