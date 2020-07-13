package com.ys.checknet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isWifiNetworkAvailable(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEthNetworkAvailable(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager)context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static String getEthernetIpAddress(Context context) {
//        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        LinkProperties linkProperties =mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
//        for (LinkAddress linkAddress: linkProperties.getAllLinkAddresses()) {
//            InetAddress address = linkAddress.getAddress();
//            if (address instanceof Inet4Address) {
//                return address.getHostAddress();
//            }
//        }
        // IPv6 address will not be shown like WifiInfo internally does.
        return "null";
    }

    public static boolean isPingSuccess(String web) {
        String result = "ok";
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 " + web);
            int status = p.waitFor();
            if (status == 0) {
                return true;
            } else {
                result = ">>fail1";
            }
        } catch (IOException e) {
            result = ">>fail2";
        } catch (InterruptedException e) {
            result = ">>fail3";
        } finally {
            Log.d("harris", "isPingSuccess result = " + result);
        }
        return false;
    }

    public static void sync() {
        try {
            Process process = Runtime.getRuntime().exec("sync");
            process.waitFor();
        } catch (Exception e) {
            Log.e("exect", e.getMessage(), e);
        } finally {

        }
    }

    public static void runSystemCommand(String command) {
        try {
            final Process process = Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c", command});
            int exitVal = process.waitFor();
            Log.d("harris", "exitVal " + exitVal);
        } catch (Exception e) {
            Log.d("harris", e.getMessage(), e);
        } finally {
        }
    }

    public static boolean isIPAvailable(String addr){
        //首先对长度进行判断
        if(addr.length() < 7 || addr.length() > 15 || "".equals(addr)){
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();
        if (ipAddress == true){
            String ips[] = addr.split("\\.");
            if(ips.length == 4){
                try{
                    for(String ip : ips){
                        if(Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255){
                            ipAddress = false;
                        }
                    }
                }catch (Exception e){
                    ipAddress = false;
                }
                ipAddress = true;
            }else{
                ipAddress = false;
            }
        }
        return ipAddress;
    }
}
