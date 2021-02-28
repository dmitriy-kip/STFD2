package com.example.stfd;

import com.example.stfd.DataBase.HistoryDAO;

import java.util.List;
import java.util.Set;

public interface NavigationFragments {
    void goToPhotoSender(String docNum, String notice, List<String> uris);
    void goToHistory(int indexModule);
    void goToSettings();
    void goToFirstScreen();
    void goToPassport();
    void onFragmentInteraction(String title, int index);
    void onDialogPositiveClick();
    void youAreExist(Set<String> modules, String authId);

}
