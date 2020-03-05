package com.ys.callrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;


public class MainActivity extends AppCompatActivity {

    static final String[] PERMISSION_LIST = new String[]{
            Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE, Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        Intent service = new Intent(this,RecorderService.class);
        startService(service);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasUnCkeck = false;
            for (int i = 0; i < PERMISSION_LIST.length; i++) {
                if (checkSelfPermission(PERMISSION_LIST[i]) != PackageManager.PERMISSION_GRANTED) {
                    hasUnCkeck = true;
                }
            }
            if (hasUnCkeck) {
                requestPermissions(PERMISSION_LIST, 300);
            }
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            } else {
                //TODO do something you need
            }
        }
    }

}
