package com.example.stfd.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.HistoryItem;
import com.example.stfd.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryEntity> historyItemList;
    //private LayoutInflater inflater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView numDoc;
        TextView time;
        LinearLayout historyContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.numDoc = itemView.findViewById(R.id.history_num_doc);
            this.time = itemView.findViewById(R.id.history_time);
            this.historyContainer = itemView.findViewById(R.id.history_container);
        }
    }

    public HistoryAdapter(Context context,List<HistoryEntity> historyItemList) {
        this.historyItemList = historyItemList;
        //this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEntity historyItem = historyItemList.get(position);
        holder.numDoc.setText(historyItem.getDocNum());
        holder.time.setText(historyItem.getTime());
        holder.historyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

}
