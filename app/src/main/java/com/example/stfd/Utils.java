package com.example.stfd;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static void fillImageToList(Bitmap bitmap, ArrayList<File> listImages, String photoUri) {
        File f = new File(photoUri); //тут еще подумать надо
        try {
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

    public static String currentDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
}
