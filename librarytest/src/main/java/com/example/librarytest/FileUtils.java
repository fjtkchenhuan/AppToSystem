package com.example.librarytest;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * adb shell am start -n com.yishengkj.testtools/MainActivity
 * Created by Administrator on 2017/12/14.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";
    String filePath;
    String fileName;
    private Context context;

    public FileUtils(Context context, String fileName, String filePath) {
        this.context = context;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public void copy() {
        InputStream inputStream;
        try {
            inputStream = context.getResources().getAssets().open(fileName);// assets文件夹下的文件
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath + "/" + fileName);// 保存到本地的文件夹下的文件
            byte[] buffer = new byte[1024];
            int count = 0;
            while ((count = inputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void CopyAssets(Context context, String oldPath, String newPath) {
        Log.e("heef", "oldPath:" + oldPath + "  newPath:" + newPath);
        try {
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            File file = new File(newPath);
            if (fileNames.length > 0) {// 如果是目录
                file.mkdirs();// 如果文件夹不存在，则创建
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                if (file.exists()) return;
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveToSDCard(String fileName, String content) {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.e("saveToSDCard", "没有权限");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {
            file.delete();
            file = new File(Environment.getExternalStorageDirectory(), fileName);
        }
        writeFile(file, content);

    }

    public static void writeFile(File file, String content) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFile(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                sb.append(readline);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取该路径下第一个文件名称
     *
     * @param extSDPath
     * @return
     */
    public static String getFirstFileNameWithPath(String extSDPath) {
        String fileName = extSDPath;
        if (extSDPath != null) {
            File file = new File(extSDPath);
            if (file.exists()) {
                fileName = getFileName(file);
            }
        }
        return fileName;
    }

    public static String getFileName(File dir) {
        File[] fileArray = dir.listFiles();
        String filname = dir.getAbsolutePath();
        if (fileArray == null) return filname;
        for (File f : fileArray) {
            if (f.isFile()) {
                filname = f.getAbsolutePath();
                break;
            } else {
                getFileName(f);
            }
        }
        return filname;
    }
    boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete())
                return true;
            else
                return false;
        }
        else
            return false;

    }
}
