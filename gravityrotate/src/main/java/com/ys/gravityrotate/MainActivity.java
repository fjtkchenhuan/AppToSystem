package com.ys.gravityrotate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GpioUtils.upgradeRootPermissionForExport();
        GpioUtils.exportGpio(68);
        GpioUtils.upgradeRootPermissionForGpio(68);

        GpioUtils.exportGpio(69);
        GpioUtils.upgradeRootPermissionForGpio(69);

        Intent service = new Intent(this,GuardService.class);
        startService(service);
    }
}
