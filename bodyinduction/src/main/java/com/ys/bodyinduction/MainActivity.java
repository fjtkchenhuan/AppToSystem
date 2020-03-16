package com.ys.bodyinduction;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        GpioUtils.upgradeRootPermissionForExport();
//        GpioUtils.exportGpio(69);
//        GpioUtils.upgradeRootPermissionForGpio(69);

//        Intent service = new Intent(this,GuardService.class);
//        startService(service);
    }

}
