package com.example.stfd;

import com.example.stfd.DataBase.HistoryDAO;
import com.example.stfd.DataBase.HistoryDAOPassport;
import com.example.stfd.DataBase.HistoryEntity;
import com.example.stfd.DataBase.HistoryEntityPassport;

import java.util.List;
import java.util.Set;

public interface NavigationFragments {
    void goToPhotoSender(HistoryEntity historyEntity);
    void goToHistory(int indexModule);
    void goToSettings();
    void goToFirstScreen();
    void goToPassport(HistoryEntityPassport historyEntityPassport);
    void onFragmentInteraction(String title, int index);
    void onDialogPositiveClick(int indexModule);
    void youAreExist(Set<String> modules, String authId);
    void executeDialog(int response, int indexModule);
    void executeOnFailureDialog();
}
