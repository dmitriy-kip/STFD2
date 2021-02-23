package com.example.stfd.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerAdapter extends ArrayAdapter {
    private Context context;
    private String[] modules;
    private int textViewResourceId;


    public SpinnerAdapter(@NonNull Context context, int resource, String[] modules) {
        super(context, resource);
        this.context = context;
        this.modules = modules;
        this.textViewResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(context, textViewResourceId, null);

            TextView tv = (TextView) convertView;
            tv.setText(modules[position]);

        return convertView;
    }
}
