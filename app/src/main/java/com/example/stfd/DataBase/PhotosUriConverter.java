package com.example.stfd.DataBase;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PhotosUriConverter {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @TypeConverter
    public String fromPhotosUri(List<String> photos) {
        return photos.stream().collect(Collectors.joining(","));
    }

    @TypeConverter
    public List<String> toPhotosUri(String data) {
        return Arrays.asList(data.split(","));
    }
}
