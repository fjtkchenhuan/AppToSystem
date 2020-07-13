package com.example.librarytest;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public class Setproperties {
    private String filePath;
    private FileUtils fileUtils;

    public Setproperties(final Context context){
        setValueToProp("persist.sys.autonet","0");
        setValueToProp("persist.sys.net_reset","0");
        setValueToProp("persist.sys.phone_fix","false");
        filePath = Environment.getExternalStorageDirectory() + File.separator;
        Log.d("ddd", "filePath = " + filePath);
        if (Build.MODEL.contains("3288")) {
            fileUtils = new FileUtils(context, "libreference-ril-ec20-3288.so", filePath);
        }else if (Build.MODEL.contains("3368")){
            fileUtils = new FileUtils(context, "libreference-ril-ec20-3368.so", filePath);
        }
        fileUtils.copy();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_REBOOT);
                intent.setAction("android.intent.action.reboot");
                context.sendBroadcast(intent);
            }
        }).start();
        String[] commands;
        commands = new String[4];
        if (Build.MODEL.contains("3288")) {
            commands[0] = "mount -o rw,remount /system";
            commands[1] = "cp  /sdcard/libreference-ril-ec20-3288.so" + " /system/lib/libreference-ril-ec20.so";
            commands[2] = "chmod 644 /system/lib/libreference-ril-ec20.so";
            commands[3] = "rm -rf  /sdcard/libreference-ril-ec20-3288.so";
        }else if (Build.MODEL.contains("3368")){
            commands[0] = "mount -o rw,remount /system";
            commands[1] = "cp   /sdcard/libreference-ril-ec20-3368.so" + " /system/lib64/libreference-ril-ec20.so";
            commands[2] = "chmod 644 /system/lib64/libreference-ril-ec20.so";
            commands[3] = "rm -rf  /sdcard/libreference-ril-ec20-3368.so";
        }

        for (int i = 0; i < commands.length; i++) {
            Log.d("ddd", "execCmd3(commands[i]) = " + commands[i]);
            execFor7(commands[i]);
        }
    }



    public static void setValueToProp(String key, String val) {
        Class<?> classType;
        try {
            classType = Class.forName("android.os.SystemProperties");
            Method method = classType.getDeclaredMethod("set", new Class[]{String.class, String.class});
            method.invoke(classType, new Object[]{key, val});
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static boolean execFor7(String command) {
        Log.d("execFor7", "command = " + command);
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String s = command + "\n";
            dataOutputStream.write(s.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("execFor7", "execFor7 msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("execFor7", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
        return result;
    }
}
