package com.example.stfd;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private List<Bitmap> smallImages;
    private Button b;
    private RelativeLayout r;
    int count = 0;
    //private ImageView i;

    MyAdapter(Context context, List<Bitmap> list, Button button, RelativeLayout relativeLayout){
        this.smallImages = list;
        this.inflater = LayoutInflater.from(context);
        this.b = button;
        this.r = relativeLayout;
        //this.i = imageView;
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
        Bitmap bitmap = smallImages.get(position);
        holder.imageBitmapView.setImageBitmap(bitmap);
        holder.imageBitmapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                r.setVisibility(View.VISIBLE);
                ImageView i = (ImageView) r.getChildAt(0);
                i.setImageBitmap(smallImages.get(position));
            }
        });

        holder.crossView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                smallImages.remove(position);
                notifyDataSetChanged();

                if (smallImages.size() == 0){
                    b.setVisibility(View.INVISIBLE);
                }

                Log.e("size", "" + smallImages.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return smallImages.size();
    }

}
