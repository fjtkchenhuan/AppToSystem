package com.ys.callrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 电话拨出的录音在此处实现，由于电话拨出时无法判断什么时候接听，所以目前的实现方式是电话拨出后就开始录音
 */

public class RecordReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("sky", "action = " + action);
        MediaRecorder recorder = new MediaRecorder();
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)) {
            Log.d("sky", "Intent.ACTION_NEW_OUTGOING_CALL");
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            createRecorderFile();//创建保存录音的文件夹

            recorder.setOutputFile("sdcard/recorder" + "/" + getCurrentTime() + ".3gp");
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                Log.d("sky", "recorder.start()");
                recorder.prepare();//准备录音
                recorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(action)) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int state = manager.getCallState();
            Log.d("sky", "state =" + state);
            if (state == TelephonyManager.CALL_STATE_IDLE) {
                //空闲状态
                recorder.stop();//停止录音
                recorder.release();//释放资源
            }
        }
    }

    //创建保存录音的目录
    private void createRecorderFile() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = absolutePath + "/recorder";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    //获取当前时间，以其为名来保存录音
    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String str = format.format(date);
        return str;
    }
}
