package com.example.testcreditcard.component;

import androidx.fragment.app.Fragment;

public class TabComponent {
    public String TabText;
    public Fragment _fragment;
    public TabComponent(String tabText, Fragment fragment) {
        if (tabText == null) {
            tabText = "";
        }
        TabText = tabText;
        _fragment = fragment;
    }
}
