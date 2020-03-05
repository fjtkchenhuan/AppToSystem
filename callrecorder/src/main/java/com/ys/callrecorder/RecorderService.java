package com.ys.callrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 来电时的录音，电话接听后才开始录音
 */

public class RecorderService extends Service {

    private MediaRecorder recorder; //录音的一个实例

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("sky","RecorderService  onCreate()");
        TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        tm.listen(new MyListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }


    class  MyListener extends PhoneStateListener{
        //在电话状态改变的时候调用
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d("sky","state = " + state);
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态
                    if (recorder!=null){
                        recorder.stop();//停止录音
                        recorder.release();//释放资源
                        recorder=null;
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃状态  需要在响铃状态的时候初始化录音服务
                    if (recorder==null){
                        recorder=new MediaRecorder();
                        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        createRecorderFile();//创建保存录音的文件夹

                        recorder.setOutputFile("sdcard/recorder" + "/" + getCurrentTime() + ".3gp");
                        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        try {
                            recorder.prepare();//准备录音
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态（接听）
                    Log.d("sky","recorder==null" + (recorder==null));
                    if (recorder!=null){
                        recorder.start(); //接听的时候开始录音
                    }
                    break;
            }
        }

        //创建保存录音的目录
        private void createRecorderFile() {
            String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();

            String filePath=absolutePath+"/recorder";
            File file=new File(filePath);
            if (!file.exists()){
                file.mkdir();
            }
        }
        //获取当前时间，以其为名来保存录音
        private String getCurrentTime(){
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
            Date date=new Date();
            String str=format.format(date);
            return str;

        }
    }
}
 