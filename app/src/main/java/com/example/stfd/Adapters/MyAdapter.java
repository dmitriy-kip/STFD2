package com.example.stfd.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stfd.R;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Bitmap> smallImages;
    private ImageView sendButton;
    private RelativeLayout r;
    private List<String> uris;

    public MyAdapter(Context context, List<Bitmap> list, ImageView button, RelativeLayout relativeLayout, List<String> photosUri){
        this.smallImages = list;
        this.inflater = LayoutInflater.from(context);
        this.sendButton = button;
        this.r = relativeLayout;
        this.uris = photosUri;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageBitmapView;
        final ImageView crossView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageBitmapView = (ImageView) itemView.findViewById(R.id.photo);
            this.crossView = (ImageView) itemView.findViewById(R.id.cross);
        }
    }
    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, final int position) {
        if (smallImages.size() != 0){
            sendButton.setVisibility(View.VISIBLE);
        }
        final Bitmap bitmap = smallImages.get(position);

        //проверяем потому что из истории могла прийти фотка которой уже нет
        if (bitmap == null){
            holder.imageBitmapView.setImageResource(R.drawable.notfound2);
        } else {
            holder.imageBitmapView.setImageBitmap(bitmap);
        }

        holder.imageBitmapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r.setVisibility(View.VISIBLE);
                ImageView i = (ImageView) r.getChildAt(0);
                if (bitmap == null) i.setImageResource(R.drawable.notfound2);
                else i.setImageBitmap(bitmap);
            }
        });

        holder.crossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smallImages.remove(position);
                uris.remove(position);
                notifyDataSetChanged();

                if (smallImages.size() == 0){
                    sendButton.setVisibility(View.INVISIBLE);
                }

                Log.e("size", "" + smallImages.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return smallImages.size();
    }

    public void clear(){
        smallImages.clear();
        notifyDataSetChanged();
    }
}
