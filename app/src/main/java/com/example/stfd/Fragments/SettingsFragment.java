package com.example.stfd.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.stfd.R;
import com.example.stfd.Utils;

public class SettingsFragment extends Fragment {
    private FragmentManager fm;
    private HistoryFragment.OnSelectedButtonListenerHistory listener;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch saveHistoryOnRequest;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        listener = (HistoryFragment.OnSelectedButtonListenerHistory) getActivity();
        if (listener != null) {
            listener.onFragmentInteraction(getString(R.string.settings), 2);
        }

        final Switch saveHistoryAlways = rootView.findViewById(R.id.hs_always);
        saveHistoryOnRequest = rootView.findViewById(R.id.hs_on_request);
        final Switch saveHistoryWhenFailure = rootView.findViewById(R.id.hs_when_failure);
        final Switch saveHistoryNever = rootView.findViewById(R.id.hs_never);

        SharedPreferences mSettings = getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        switch (mSettings.getInt("save", Utils.SAVE_HISTORY_ON_REQUEST)){
            case Utils.SAVE_HISTORY_ALWAYS:
                saveHistoryAlways.setChecked(true);
                break;
            case Utils.SAVE_HISTORY_NEVER:
                saveHistoryNever.setChecked(true);
                break;
            case Utils.SAVE_HISTORY_ON_REQUEST:
                saveHistoryOnRequest.setChecked(true);
                break;
            case Utils.SAVE_HISTORY_WHEN_FAILURE:
                saveHistoryWhenFailure.setChecked(true);
                break;
            default:
        }

        editor = mSettings.edit();

        saveHistoryAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    saveHistoryOnRequest.setChecked(false);
                    saveHistoryNever.setChecked(false);
                    saveHistoryWhenFailure.setChecked(false);
                    editor.putInt("save", Utils.SAVE_HISTORY_ALWAYS);
                    editor.apply();
                } //else defaultSettings();
            }
        });

        saveHistoryOnRequest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveHistoryNever.setChecked(false);
                    saveHistoryWhenFailure.setChecked(false);
                    saveHistoryAlways.setChecked(false);
                    editor.putInt("save", Utils.SAVE_HISTORY_ON_REQUEST);
                    editor.apply();
                }
            }
        });

        saveHistoryWhenFailure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveHistoryOnRequest.setChecked(false);
                    saveHistoryNever.setChecked(false);
                    saveHistoryAlways.setChecked(false);
                    editor.putInt("save", Utils.SAVE_HISTORY_WHEN_FAILURE);
                    editor.apply();
                } //else defaultSettings();
            }
        });

        saveHistoryNever.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveHistoryOnRequest.setChecked(false);
                    saveHistoryWhenFailure.setChecked(false);
                    saveHistoryAlways.setChecked(false);
                    editor.putInt("save", Utils.SAVE_HISTORY_NEVER);
                    editor.apply();
                } //else defaultSettings();
            }
        });

        fm = getActivity().getSupportFragmentManager();
        return rootView;
    }

    private void defaultSettings(){
        saveHistoryOnRequest.setChecked(true);
        editor.putInt("save", Utils.SAVE_HISTORY_ON_REQUEST);
        editor.apply();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.history_menu, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                fm.popBackStack();
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
            listener = (HistoryFragment.OnSelectedButtonListenerHistory) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
