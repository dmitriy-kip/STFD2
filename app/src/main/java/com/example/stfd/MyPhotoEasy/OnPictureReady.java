package com.example.stfd.MyPhotoEasy;

import android.graphics.Bitmap;
import android.net.Uri;

import java.net.URI;

public interface OnPictureReady {
    void onFinish(Bitmap thumbnail, Uri uri);
}
