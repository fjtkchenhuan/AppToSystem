package com.ys.bodyinduction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            GpioUtils.upgradeRootPermissionForExport();
            GpioUtils.exportGpio(225);
            GpioUtils.upgradeRootPermissionForGpio(225);

            Intent service = new Intent(context,GuardService.class);
            context.startService(service);
            Log.d("sky","start GuardService");
        }
    }
}
