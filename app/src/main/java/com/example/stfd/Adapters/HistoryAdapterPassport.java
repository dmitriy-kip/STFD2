package com.example.stfd.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stfd.DataBase.BasicEntity;
import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryDAOPassport;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.DataBase.HistoryEntityPassport;
import com.example.stfd.NavigationFragments;
import com.example.stfd.R;

import java.util.List;

public class HistoryAdapterPassport extends RecyclerView.Adapter<HistoryAdapterPassport.ViewHolder> {

    private List<BasicEntity> historyItemList;
    HistoryDAOPassport historyDAO;
    FragmentManager fm;
    Context context;
    //private LayoutInflater inflater;

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView numDoc;
        TextView time;
        LinearLayout historyContainer;
        ImageView deleteButton;
        TextView sendingStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.numDoc = itemView.findViewById(R.id.history_num_doc);
            this.time = itemView.findViewById(R.id.history_time);
            this.historyContainer = itemView.findViewById(R.id.history_container);
            this.deleteButton = itemView.findViewById(R.id.history_delete_item);
            this.sendingStatus = itemView.findViewById(R.id.status);
        }
    }

    public HistoryAdapterPassport(Context context, List<BasicEntity> historyItemList, HistoryDAOPassport historyDAO, FragmentManager fm) {
        this.historyItemList = historyItemList;
        this.historyDAO = historyDAO;
        this.fm = fm;
        this.context = context;
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
        final HistoryEntityPassport historyItem = (HistoryEntityPassport) historyItemList.get(position);
        holder.numDoc.setText(historyItem.getDocNum());
        holder.time.setText(historyItem.getTime());

        if (historyItem.status){
            holder.sendingStatus.setText(R.string.sent);
            holder.sendingStatus.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.sendingStatus.setText(R.string.not_sent);
            holder.sendingStatus.setTextColor(Color.parseColor("#F44336"));
        }

        holder.historyContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationFragments buttonItem = (NavigationFragments) context;
                buttonItem.goToPassport(historyItem);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyDAO.deletePassport(historyItem);
                historyItemList.remove(historyItem);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

}
