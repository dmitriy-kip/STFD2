package com.example.stfd;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.stfd.Adapters.HistoryAdapter;
import com.example.stfd.DataBase.AppDataBase;
import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.DataBase.SingletonAppDB;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private OnSelectedButtonListenerHistory listener;
    private HistoryAdapter historyAdapter;
    private final List<HistoryEntity> historyItemList = new ArrayList<>();
    HistoryDAO historyDAO;

    public interface OnSelectedButtonListenerHistory{
        void onFragmentInteraction(String title, int index);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        listener = (OnSelectedButtonListenerHistory) getActivity();
        if (listener != null) {
            listener.onFragmentInteraction(getString(R.string.history), 2);
        }

        FragmentManager fm = getActivity().getSupportFragmentManager();

        AppDataBase db = SingletonAppDB.getInstance().getDatabase();
        historyDAO = db.historyEntity();
        historyItemList.addAll(historyDAO.getAll());

        final RecyclerView recyclerViewHistory = rootView.findViewById(R.id.recycle_history);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(getContext(), historyItemList, historyDAO, fm);
        recyclerViewHistory.setAdapter(historyAdapter);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.history_menu, menu);
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
            listener = (OnSelectedButtonListenerHistory) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}