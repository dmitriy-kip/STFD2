package com.example.stfd.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stfd.Adapters.MyAdapter;
import com.example.stfd.DataBase.AppDataBase;
import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.DataBase.SingletonAppDB;
import com.example.stfd.MyPhotoEasy.OnPictureReady;
import com.example.stfd.MyPhotoEasy.PhotoEasy;
import com.example.stfd.NavigationFragments;
import com.example.stfd.R;
import com.example.stfd.Utils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.impl.io.ContentLengthInputStream;

public class PhotoSenderFragment extends Fragment {

    private NavigationFragments listener;
    private PhotoEasy photoEasy;
    private MyAdapter myAdapter;
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private ImageView sendPhoto;
    private final ArrayList<File> listImages = new ArrayList<>();
    private List<String> photosUri = new ArrayList<>();
    private HistoryDAO historyDAO;
    private final Fragment f = this;
    private RelativeLayout previewPhoto;
    private EditText editNumDoc;
    private EditText editNotice;
    private String numDoc;
    private String notice;
    private int saveHistory = Utils.SAVE_HISTORY_ON_REQUEST;
    private SharedPreferences mSettings;
    private boolean fromHistory = false;
    private boolean status;
    private int id = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo_sender, container, false);

        listener = (NavigationFragments) getActivity();
        if (listener != null) {
            listener.onFragmentInteraction(getString(R.string.app_name), 1);
        }

        mSettings = getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);

        Spinner spinner = rootView.findViewById(R.id.spinner_test);
        Utils.makeSpinner("ЦООГ", getActivity(), listener, spinner);

        Bundle args = getArguments();
        if (args != null){
            if (args.getString("numDoc") != null) {
                EditText docNumEdit = rootView.findViewById(R.id.edit_num_doc);
                docNumEdit.setText(args.getString("numDoc"));
            }

            if (args.getString("notice") != null) {
                EditText noticeEdit = rootView.findViewById(R.id.edit_notice);
                noticeEdit.setText(args.getString("notice"));
            }

            fromHistory = args.getBoolean("history");
            id = args.getInt("id");

            if (args.getStringArray("photosUri") != null && args.getStringArray("photosUri").length > 0) {
            String[] photosArray = args.getStringArray("photosUri");
                photosUri.addAll(Arrays.asList(photosArray));

                for (String photo : photosUri) {
                    Bitmap bitmap = null;
                    Uri imageUri = Uri.parse(photo);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Некоторые фотографии были удалены или перемещены", Toast.LENGTH_LONG).show();
                    }
                    bitmapList.add(bitmap);
                }
            }

            args.clear();
        }

        sendPhoto = rootView.findViewById(R.id.sendToServer);
        previewPhoto = rootView.findViewById(R.id.preview_photo);

        AppDataBase db = SingletonAppDB.getInstance().getDatabase();
        historyDAO = db.historyEntity();

        final RecyclerView recyclerView = rootView.findViewById(R.id.recycle_list);
        myAdapter = new MyAdapter(getContext(), bitmapList, sendPhoto, previewPhoto, photosUri);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        photoEasy = PhotoEasy.builder()
                .setActivity(getActivity())
                .setStorageType(PhotoEasy.StorageType.media)
                .build();

        editNumDoc = rootView.findViewById(R.id.edit_num_doc);
        editNotice = rootView.findViewById(R.id.edit_notice);

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
                if (!Utils.isOnline(getActivity())){
                    Toast.makeText(getActivity(), "Нет подключения к интернету", Toast.LENGTH_LONG).show();
                    return;
                }

                final RelativeLayout progressCircle = rootView.findViewById(R.id.progress_circular1);
                progressCircle.setVisibility(View.VISIBLE);

                //получем настройки сохранения истории
                if (fromHistory) {
                    saveHistory = Utils.SAVE_HISTORY_NEVER;
                } else {
                    if (mSettings.getInt("save", Utils.SAVE_HISTORY_ON_REQUEST) != 0)
                        saveHistory = mSettings.getInt("save", Utils.SAVE_HISTORY_ON_REQUEST);
                }

                numDoc = editNumDoc.getText().toString();
                notice = editNotice.getText().toString();

                Utils.fillImageToList(bitmapList, listImages, getContext());

                File[] files = new File[listImages.size()];
                listImages.toArray(files);

                AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
                client.setConnectTimeout(30 * 1000);
                client.setResponseTimeout(3600 * 1000);
                RequestParams params = new RequestParams();
                try {
                    params.put("file_upload[]", files);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                String authId = mSettings.getString("authId", "0");
                params.put("authId", authId);
                params.put("income_num", numDoc);
                params.put("file_desc", notice);
                client.post("http://prog-matik.ru:8086/api/upload_file",params,new TextHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        progressCircle.setVisibility(View.INVISIBLE);

                        status = true;
                        switch (saveHistory){
                            case Utils.SAVE_HISTORY_ALWAYS:
                                saveData();
                                Toast.makeText(getActivity(), "Информация успешно отправлена", Toast.LENGTH_LONG).show();
                                break;
                            case Utils.SAVE_HISTORY_ON_REQUEST:
                                listener.executeDialog(Utils.RESPONSE_IS_OK, 1);
                                break;
                            case Utils.SAVE_HISTORY_WHEN_FAILURE:
                            case Utils.SAVE_HISTORY_NEVER:
                                Toast.makeText(getActivity(), "Информация успешно отправлена", Toast.LENGTH_LONG).show();
                                break;
                            default:

                        }
                        clearAllFields();
                        //photosUri.clear();

                        if (fromHistory){
                            if (id != -1){
                                historyDAO.chanceStatus(true, id);
                            }
                        }
                        Log.e("ответ","все ок " + responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        progressCircle.setVisibility(View.INVISIBLE);

                        status = false;
                        switch (saveHistory) {
                            case Utils.SAVE_HISTORY_ALWAYS:
                            case Utils.SAVE_HISTORY_WHEN_FAILURE:
                                listener.executeOnFailureDialog();
                                saveData();
                                break;
                            case Utils.SAVE_HISTORY_ON_REQUEST:
                                listener.executeDialog(Utils.RESPONSE_IS_FAILURE, 1);
                                break;
                            case Utils.SAVE_HISTORY_NEVER:
                                listener.executeOnFailureDialog();
                                break;
                            default:
                        }
                        listImages.clear();
                        //photosUri.clear();
                        Log.e("ответ", "не ок " + responseString);
                    }

                });
            }
        });

        ImageView cameraGoButton = rootView.findViewById(R.id.make_photo);
        cameraGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEasy.startActivityForResult(f);
            }
        });

        ImageView galleryOpen = rootView.findViewById(R.id.gallery);
        galleryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                /*intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);*/
                /*takeFlags = intent.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);*/


                startActivityForResult(intent, Utils.OPEN_GALLERY);
            }
        });

        return rootView;
    }

    public void onKeyDown(){
        previewPhoto.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        photoEasy.onActivityResult(requestCode, resultCode, new OnPictureReady() {
            @Override
            public void onFinish(Bitmap thumbnail, Uri uri) {
                addPhoto(thumbnail, uri);

            }
        });

        if (resultCode == Activity.RESULT_OK) {
            if(requestCode == Utils.OPEN_GALLERY) {
                Uri imageUri = data.getData();
                if (imageUri == null) {
                    Toast.makeText(getActivity(), "Невозможно отправить выбранный фаил", Toast.LENGTH_LONG).show();
                    return;
                }
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getActivity().getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
                Bitmap bitmap = null;
                try {

                    bitmap = MediaStore.Images.Media.getBitmap( getActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap == null) {
                    Toast.makeText(getActivity(), "Не удалось получить фаил", Toast.LENGTH_LONG).show();
                    return;
                }
                addPhoto(bitmap, imageUri);
            }

        }

    }

    private void addPhoto(Bitmap bitmap, Uri uri){
        bitmapList.add(bitmap);
        photosUri.add(uri.toString());
        myAdapter.notifyItemInserted(bitmapList.size()-1);
        sendPhoto.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu1, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.settings:
                listener.goToSettings();
                return true;
            case R.id.history:
                listener.goToHistory(1);
                return true;
            case R.id.clear:
                clearAllFields();
                return true;
            case R.id.exit:
                mSettings.edit().remove("phone").apply();
                listener.goToFirstScreen();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NavigationFragments) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        listener.onFragmentInteraction(getString(R.string.app_name), 1);
        super.onResume();
    }

    private void clearAllFields(){
        editNumDoc.getText().clear();
        editNotice.getText().clear();
        myAdapter.clear();
        bitmapList.clear();
        listImages.clear();
        photosUri.clear();
        fromHistory = false;
        sendPhoto.setVisibility(View.INVISIBLE);
    }


    public void saveData() {
        historyDAO.insertAll(new HistoryEntity(numDoc, notice, Utils.currentDate(), photosUri, status));
    }

}
