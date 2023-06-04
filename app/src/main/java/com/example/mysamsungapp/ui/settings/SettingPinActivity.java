package com.example.mysamsungapp.ui.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.mysamsungapp.CustomPinActivity;
import com.example.mysamsungapp.MainActivity;
import com.example.mysamsungapp.R;
import com.github.omadahealth.lollipin.lib.managers.AppLock;
import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class SettingPinActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_pin);

        Toolbar toolbar = findViewById(R.id.toolBar);
        CompoundButton switchPin = findViewById(R.id.pinSwitch);
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout8);
        Button buttonChange = findViewById(R.id.button_change_pin);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Код-пароль");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences isPinEnabled = getSharedPreferences("isPinEnabled", Context.MODE_PRIVATE);
        if (isPinEnabled.contains("isPinEnabled")) {
            if (isPinEnabled.getBoolean("isPinEnabled", true)) {
                switchPin.setChecked(true);
                constraintLayout.setVisibility(View.VISIBLE);
            } else {
                switchPin.setChecked(false);
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        }

        SharedPreferences.Editor editor = isPinEnabled.edit();
        LockManager<CustomPinActivity> lockManager = LockManager.getInstance();

        ActivityResultLauncher<Intent> addPinLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        editor.putBoolean("isPinEnabled", true);
                        editor.apply();
                        lockManager.enableAppLock(getApplicationContext(), CustomPinActivity.class);
                        constraintLayout.setVisibility(View.VISIBLE);
                        Snackbar.make(constraintLayout, "Код-пароль успешно установлен!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        switchPin.setChecked(false);
                    }
                });

        ActivityResultLauncher<Intent> deletePinLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        editor.putBoolean("isPinEnabled", false);
                        editor.apply();
                        lockManager.disableAppLock();
                        constraintLayout.setVisibility(View.INVISIBLE);
                        Snackbar.make(constraintLayout, "Код-пароль успешно удален!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        switchPin.setChecked(true);
                    }
                });

        ActivityResultLauncher<Intent> changePinLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Snackbar.make(constraintLayout, "Код-пароль успешно изменен!", Snackbar.LENGTH_SHORT).show();
                    }
                });

        switchPin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CustomPinActivity.class);
            if (switchPin.isChecked()){
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                addPinLauncher.launch(intent);
            } else {
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK);
                deletePinLauncher.launch(intent);
            }
        });

        buttonChange.setOnClickListener(v -> {
            //Изменение пинкода
            Intent intent = new Intent(getApplicationContext(), CustomPinActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            changePinLauncher.launch(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
