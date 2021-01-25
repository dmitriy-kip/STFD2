package com.example.stfd;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.stfd.Adapters.HistoryAdapter;
import com.example.stfd.Adapters.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class PhotoSenderFragment extends Fragment {
    private OnSelectedButtonListener listener;
    private MyAdapter myAdapter;
    private HistoryAdapter historyAdapter;
    private final List<Bitmap> bitmapList = new ArrayList<>();
    private ImageView sendPhoto;

    public interface OnSelectedButtonListener extends HistoryFragment.OnSelectedButtonListenerHistory{
        void onSendPhoto();
        void onCameraGoButton();
        void onGalleryOpen();
        void goToHistory();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_photo_sender, container, false);
        listener = (OnSelectedButtonListener) getActivity();
        if (listener != null) {
            listener.onFragmentInteraction(getString(R.string.app_name), 1);
        }


        Bundle args = getArguments();
        if (args != null){
            EditText docNumEdit = rootView.findViewById(R.id.edit_num_doc);
            docNumEdit.setText(args.getString("numDoc"));

            EditText noticeEdit = rootView.findViewById(R.id.edit_notice);
            noticeEdit.setText(args.getString("notice"));
        }



        ImageView sendPhoto = rootView.findViewById(R.id.sendToServer);
        final RelativeLayout previewPhoto = rootView.findViewById(R.id.preview_photo);

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
                listener.onSendPhoto();
            }
        });

        ImageView cameraGoButton = rootView.findViewById(R.id.make_photo);
        cameraGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCameraGoButton();
            }
        });

        ImageView galleryOpen = rootView.findViewById(R.id.gallery);
        galleryOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGalleryOpen();
            }
        });

        return rootView;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settings:
                /*Toast.makeText(this, "Работает", Toast.LENGTH_LONG).show();*/
                return true;
            case R.id.history:
                listener.goToHistory();
                return true;
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
            listener = (PhotoSenderFragment.OnSelectedButtonListener) context;
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

    @Override
    public void onStart() {
        super.onStart();
        listener.onFragmentInteraction(getString(R.string.app_name), 1);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener.onFragmentInteraction(getString(R.string.app_name), 1);
    }
}
