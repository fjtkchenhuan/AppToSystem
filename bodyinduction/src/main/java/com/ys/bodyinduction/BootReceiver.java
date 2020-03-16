package com.ys.bodyinduction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            GpioUtils.upgradeRootPermissionForExport();
            GpioUtils.exportGpio(69);
            GpioUtils.upgradeRootPermissionForGpio(69);

            Intent service = new Intent(context,GuardService.class);
            context.startService(service);
        }
    }
}
