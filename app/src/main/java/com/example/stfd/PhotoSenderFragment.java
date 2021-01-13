package com.example.stfd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.thorny.photoeasy.PhotoEasy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class PhotoSenderFragment extends Fragment {
    private PhotoEasy photoEasy;
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private MyAdapter myAdapter;

    private final ArrayList<File> listImages = new ArrayList<>();
    private ImageView sendPhoto;
    private String numDoc;
    private String notice;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_photo_sender, container, false);
        final RelativeLayout progressCircle = rootView.findViewById(R.id.progress_circular1);

        sendPhoto = rootView.findViewById(R.id.sendToServer);
        final RelativeLayout previewPhoto = rootView.findViewById(R.id.preview_photo);

        final RecyclerView recyclerView = rootView.findViewById(R.id.recycle_list);
        myAdapter = new MyAdapter(getContext(), bitmapList, sendPhoto, previewPhoto);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        photoEasy = PhotoEasy.builder()
                .setActivity(Objects.requireNonNull(getActivity()))
                .setStorageType(PhotoEasy.StorageType.media)
                .build();

        ImageView bigCrossView = rootView.findViewById(R.id.big_cross);
        bigCrossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewPhoto.setVisibility(View.INVISIBLE);
            }
        });


        sendPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isOnline(Objects.requireNonNull(getContext()))){
                    Toast.makeText(getContext(), "Нет подключения к интернету", Toast.LENGTH_LONG).show();
                    return;
                }
                progressCircle.setVisibility(View.VISIBLE);
                final EditText editNumDoc = rootView.findViewById(R.id.edit_num_doc);
                final EditText editNotice = rootView.findViewById(R.id.edit_notice);
                numDoc = editNumDoc.getText().toString();
                notice = editNotice.getText().toString();

                for (int i = 0; i< bitmapList.size(); i++){
                    Utils.fillImageToList(bitmapList.get(i), listImages, getContext());
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
                        Toast.makeText(getContext(), "Информация успешено отправлена", Toast.LENGTH_LONG).show();
                        sendPhoto.setVisibility(View.INVISIBLE);
                        progressCircle.setVisibility(View.INVISIBLE);

                        Log.e("ответ","все ок " + responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Toast.makeText(getContext(), "Не удалось отправить", Toast.LENGTH_LONG).show();
                        progressCircle.setVisibility(View.INVISIBLE);

                        Log.e("ответ", "не ок " + responseString);
                    }

                });
            }
        });

        ImageView cameraGoButton = rootView.findViewById(R.id.make_photo);
        cameraGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEasy.startActivityForResult(getActivity());
            }
        });

        ImageView galleryOpen = rootView.findViewById(R.id.gallery);
        galleryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        return rootView;
    }


}
