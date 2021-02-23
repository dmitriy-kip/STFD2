package com.example.stfd;

import java.util.List;

public interface NavigationFragments {
    void goToPhotoSender(String docNum, String notice, List<String> uris, String[] modules);
    void goToHistory();
    void goToSettings();
    void goToFirstScreen();
    void goToPassport();
    void onFragmentInteraction(String title, int index);
    void onDialogPositiveClick();
    void youAreExist(List<Integer> modules, String authId);

}
