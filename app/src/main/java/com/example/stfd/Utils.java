package com.example.stfd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class Utils {
    public static final int RESPONSE_IS_OK = 1;
    public static final int RESPONSE_IS_FAILURE = 2;
    public static final int OPEN_GALLERY = 2;
    public static final int SAVE_HISTORY_ALWAYS = 1;
    public static final int SAVE_HISTORY_ON_REQUEST = 2;
    public static final int SAVE_HISTORY_WHEN_FAILURE = 3;
    public static final int SAVE_HISTORY_NEVER = 4;

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    private static String createFileName(){
        UUID uuid = UUID.randomUUID();
        String pattern = "dd.MM.yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String fileName = uuid.toString() + "_" + date + ".jpg";
        return fileName;
    }

    private static byte[] resizeBitmapData(Bitmap bitmap, ByteArrayOutputStream outputStream, int maxSize) {
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

    public static void fillImageToList(Bitmap bitmap, ArrayList<File> listImages, Context context1) {

        try {
            File f = new File(context1.getCacheDir(), createFileName());
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
            listImages.add(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String currentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void makeSpinner(String module, Activity activity, final NavigationFragments listener, Spinner spinner){
        SharedPreferences mSettings = activity.getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        //получаем модули которые доступны пользователю и преобразуем их в обычный строковый массив
        //если модулей 1, тогда нам не нужен выпадающий список
        Set<String> set = mSettings.getStringSet("modules", null);
        String[] modules;
        if (set != null && set.size() > 1)
            modules = set.toArray(new String[set.size()]);
        else {
            spinner.setVisibility(View.INVISIBLE);
            return;
        }
        //настраиваем выпадающий список
        //SpinnerAdapter adapter = new SpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, modules);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, modules);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int index = 0;
        final String[] finalModules = modules;
        for (int i = 0; i < finalModules.length; i++) {
            if (finalModules[i].equals(module)) index = i;
        }
        final int indexOfModule = index;
        spinner.setSelection(indexOfModule);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == indexOfModule) return;

                String module = finalModules[pos];
                switch (module){
                    case "ЦООГ":
                        listener.goToPhotoSender(null,null, null);
                        break;
                    case "Паспортный стол":
                        listener.goToPassport();
                        break;
                    default:
                }
            }
        });
    }
}
