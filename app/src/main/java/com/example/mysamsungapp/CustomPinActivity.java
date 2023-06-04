package com.example.mysamsungapp;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.mysamsungapp.ui.home.HomeFragment;
import com.example.mysamsungapp.ui.settings.SettingPinActivity;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.AppLockActivity;

import java.util.Arrays;
import java.util.List;

public class CustomPinActivity extends AppLockActivity {
    @Override
    public void showForgotDialog() {

    }

    @Override
    public void onPinFailure(int attempts) {

    }

    @Override
    public void onPinSuccess(int attempts) {

    }

    @Override
    public int getContentView() {
        return R.layout.custom_pin_screen;
    }

    @Override
    public List<Integer> getBackableTypes() {
        return Arrays.asList(AppLock.ENABLE_PINLOCK, AppLock.DISABLE_PINLOCK);
    }
}
