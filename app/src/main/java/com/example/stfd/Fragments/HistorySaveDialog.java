package com.example.stfd.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.stfd.NavigationFragments;
import com.example.stfd.R;
import com.example.stfd.Utils;

import java.util.Objects;

public class HistorySaveDialog extends DialogFragment {

    private NavigationFragments listener;
    private int response;
    private int indexModule;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle arg = getArguments();
        if (arg != null){
            this.response = arg.getInt("response");
            this.indexModule = arg.getInt("module");
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        String message = "";
        switch (response){
            case Utils.RESPONSE_IS_OK:
                message = getString(R.string.onSuccess);
                break;
            case Utils.RESPONSE_IS_FAILURE:
                message = getString(R.string.onFailure);
                break;
            default:
        }
        dialog.setTitle(message)
                .setMessage(R.string.dialog_history_save)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDialogPositiveClick(indexModule);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return dialog.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (NavigationFragments) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(Objects.requireNonNull(getActivity()).toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
