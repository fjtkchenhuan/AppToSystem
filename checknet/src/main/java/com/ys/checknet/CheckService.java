package com.ys.checknet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Handler;
import android.util.Log;

public class CheckService extends Service {
    String ethIP = "null";
    int ethPingCount = 0;
	int wlanPingCount = 0;
    String pingIPWebsiteEth;
	String pingIPWebsiteWifi;
    int pingCheckTimer;
    private Handler handler;
    public static final String ETH_WEBSITE = "persist.sys.eth_website";
    public static final String WIFI_WEBSITE = "persist.sys.eth_website";
    public static final String CHECK_TIME = "persist.sys.checktime";
    public static final String ETH_SWITCH = "persist.sys.eth_fix";
    public static final String WIFI_SWITCH = "persist.sys.wifi_fix";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("harris","CheckService onCreate()");
		check_daemon_init();
        handler = new Handler();
        handler.postDelayed(CheckNet,20000);
    }

    private Runnable CheckNet = new Runnable() {
        @Override
        public void run() {
			check_eth_network_normal();
			check_wlan_network_normal();
			Utils.sync();
			check_daemon_init();
            handler.postDelayed(CheckNet, pingCheckTimer);//60s
        }
        
    };
	
	private void check_daemon_init() {
//		pingIPWebsiteEth = SystemProperties.get(ETH_WEBSITE,"www.baidu.com");//14.215.177.38
//		pingIPWebsiteWifi = SystemProperties.get(WIFI_WEBSITE,"www.baidu.com");//14.215.177.38
//		pingCheckTimer = Integer.parseInt(SystemProperties.get(CHECK_TIME,"300"));
//		pingCheckTimer = pingCheckTimer * 1000;
	}


	private void check_eth_network_normal() {
//        if(SystemProperties.getBoolean(ETH_SWITCH, false)){
          	if(Utils.isEthNetworkAvailable(this)){
				ethIP = Utils.getEthernetIpAddress(this);
				Log.d("harris","getEthernetIpAddress is "+ethIP);
				//ping
				if(Utils.isPingSuccess(pingIPWebsiteEth)){
					ethPingCount = 0;
					Log.d("harris","Current Ethernet ping ok");
				} else{
                    Log.d("harris","Current Ethernet ping not ok");
					ethPingCount++;
					if(ethPingCount >= 10){//1min
						ethPingCount = 0;
						Log.d("harris","======Current Ethernet reset=======");
						Utils.runSystemCommand("busybox ifconfig eth0 down");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Utils.runSystemCommand("busybox ifconfig eth0 up");
					}

				}
			} else{
				ethPingCount = 0;
				Log.d("harris","Current Ethernet is not connected");
			}		
//		}
	}
	
	private void check_wlan_network_normal() {
//        if(SystemProperties.getBoolean(WIFI_SWITCH, false)){
          	if(Utils.isWifiNetworkAvailable(this)){
				//ping
				if(Utils.isPingSuccess(pingIPWebsiteWifi)){
					wlanPingCount = 0;
					Log.d("harris","Current wifi ping ok");
				}
				else{
					wlanPingCount++;
					if(wlanPingCount >= 5){//5min
						wlanPingCount = 0;
						Log.d("harris","======Current wifi reset=======");
						Utils.runSystemCommand("busybox ifconfig wlan0 down");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Utils.runSystemCommand("busybox ifconfig wlan0 up");
					}
					Log.d("harris","Current wifi ping not ok");
				}
			} else{
				wlanPingCount = 0;
				Log.d("harris","current wifi is not connected");
			}		
//		}
	}	
	

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
