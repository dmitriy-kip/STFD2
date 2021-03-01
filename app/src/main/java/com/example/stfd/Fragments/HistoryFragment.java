package com.example.stfd.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stfd.Adapters.HistoryAdapter;
import com.example.stfd.DataBase.AppDataBase;
import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.DataBase.SingletonAppDB;
import com.example.stfd.NavigationFragments;
import com.example.stfd.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private NavigationFragments listener;
    private final List<HistoryEntity> historyItemList = new ArrayList<>();
    private FragmentManager fm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        listener = (NavigationFragments) getActivity();
        if (listener != null) {
            listener.onFragmentInteraction(getString(R.string.history), 2);
        }

        fm = getActivity().getSupportFragmentManager();

        int indexModule = 0;
        Bundle args = getArguments();
        if (args != null){
            indexModule = args.getInt("module");
        }

        HistoryDAO historyDAO;
        switch (indexModule){
            case 1:
                AppDataBase db = SingletonAppDB.getInstance().getDatabase();
                historyDAO = db.historyEntity();
                break;
            case 2:
                AppDataBase dbPassport = SingletonAppDB.getInstance().getDatabasePassport();
                historyDAO = dbPassport.historyEntity();
                break;
            default:
                historyDAO = null;
        }

        if (historyDAO != null)
            historyItemList.addAll(historyDAO.getAll());

        final RecyclerView recyclerViewHistory = rootView.findViewById(R.id.recycle_history);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        HistoryAdapter historyAdapter = new HistoryAdapter(getContext(), historyItemList, historyDAO, fm);
        recyclerViewHistory.setAdapter(historyAdapter);

        return rootView;
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
            listener = (NavigationFragments) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
