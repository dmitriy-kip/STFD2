package com.example.stfd;

import java.util.List;
import java.util.Set;

public interface NavigationFragments {
    void goToPhotoSender(String docNum, String notice, List<String> uris);
    void goToHistory();
    void goToSettings();
    void goToFirstScreen();
    void goToPassport();
    void onFragmentInteraction(String title, int index);
    void onDialogPositiveClick();
    void youAreExist(Set<String> modules, String authId);

}
