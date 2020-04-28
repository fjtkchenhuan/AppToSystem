package com.ys.checketh;

import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.net.*;
import java.net.*;
import java.nio.charset.Charset;
import java.io.*;
import java.util.regex.*;

public class CheckService extends Service {
    private int TYPE_NORMAL = 0;
	private int TYPE_WIFI = 1;
	private int TYPE_ETH = 2;	
	private int POST_DELAY_INIT = 60;
    String ethIP = "null";
    int ethPingCount = 0;
	int wlanPingNormalCount = 0;
	int postdelay = 60;
    String pingIPWebsiteEth;
	String pingIPWebsiteWifi;
    int ethPingCheckTimer;
    private Handler handler;
	String cameraPreview;
	int camPreviewCount = 0;
	BufferedReader bufferedReader;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("harris","CheckService onCreate()");
		check_daemon_init();
        handler = new Handler();
        handler.postDelayed(CheckEth,20000);
    }

    private Runnable CheckEth = new Runnable() {
        @Override
        public void run() {
			check_eth_network_normal();
			check_wlan_network_normal();
			system_sync();
            handler.postDelayed(CheckEth, postdelay * 1000);//60s
        }
        
    };
	
	private void check_daemon_init() {
		//Get the destination address of Ping
		pingIPWebsiteEth = SystemProperties.get("persist.eth.website","www.baidu.com");//14.215.177.38
		pingIPWebsiteWifi = SystemProperties.get("persist.wifi.website","www.baidu.com");//14.215.177.38
		//Set continuous Ping several times
		ethPingCheckTimer = Integer.parseInt(SystemProperties.get("persist.eth.checktimer","300"));
		ethPingCheckTimer = ethPingCheckTimer/postdelay;
		postdelay = POST_DELAY_INIT;
	}

	public boolean isIP(String addr){
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
	
	private String run_system_command(int type,String command) {
        try {
			final Process process = Runtime.getRuntime().exec(new String[]{"/system/xbin/su","-c", command});
			if(TYPE_NORMAL != type){
				new Thread(new Runnable() {
					public void run() {
						String inputMsg;
						String inputLines = "";
						BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));		
						try {
							// 读取命令的执行结果
							while ((inputMsg = inputStream.readLine()) != null) {
								inputLines += inputMsg + "\n";
								if(TYPE_WIFI == type || TYPE_ETH == type){
									if(isIP(inputMsg)){
										if(TYPE_WIFI == type){
											SystemProperties.set("persist.wlan0.gateway",inputMsg);		
										}
										else if(TYPE_ETH == type){
											SystemProperties.set("persist.eth0.gateway",inputMsg);
										}
									}
								}
							}
							//Log.d("harris","inputLines = " + inputLines);
						} catch (Exception e) {
							Log.d("harris", e.getMessage(), e);
						} finally {
							try {
								if (inputStream != null) {
									inputStream.close();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}).start();				
			}
			int exitVal = process.waitFor();
			Log.d("harris", "exitVal " + exitVal);
        } catch (Exception e) {
            Log.d("harris", e.getMessage(), e);
        } finally {

        }
		
		return "";
    }
	
	private void check_eth_network_normal() {
        if(true == SystemProperties.getBoolean("persist.sys.eth_fix", false)){
          	if(true == isEthNetworkAvailable()){
				ethIP = getEthernetIpAddress();
				Log.d("harris","getEthernetIpAddress is "+ethIP);
				//ping
				if(ping_timeout(pingIPWebsiteEth)){
					ethPingCount = 0;
					Log.d("harris","Current Ethernet ping ok");
					postdelay = POST_DELAY_INIT;
				}
				else{
					ethPingCount++;
					if(ethPingCount >= 10){//1min
						ethPingCount = 0;
						Log.d("harris","======Current Ethernet reset=======");
						run_system_command(TYPE_NORMAL,"busybox ifconfig eth0 down");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						run_system_command(TYPE_NORMAL,"busybox ifconfig eth0 up");		
						postdelay = POST_DELAY_INIT;
					}
					else{
						postdelay = 10;
					}
					Log.d("harris","Current Ethernet ping not ok");
				}
			}
			else{
				ethPingCount = 0;
				postdelay = POST_DELAY_INIT;
				Log.d("harris","Current Ethernet is not connected");
			}		
		}
	}
	
	private void check_wlan_network_normal() {
		//wifi ping
        if(true == SystemProperties.getBoolean("persist.sys.wifi_fix", false)){	
          	if(true == isWifiNetworkAvailable()){
				//ping
				if(ping_timeout(pingIPWebsiteWifi)){
					wlanPingNormalCount = 0;
					Log.d("harris","Current wifi ping ok");
				}
				else{
					wlanPingNormalCount++;
					if(wlanPingNormalCount >= 5){//5min
						wlanPingNormalCount = 0;
						Log.d("harris","======Current wifi reset=======");
						run_system_command(TYPE_NORMAL,"busybox ifconfig wlan0 down");
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						run_system_command(TYPE_NORMAL,"busybox ifconfig wlan0 up");
					}
					Log.d("harris","Current wifi ping not ok");
				}
			}
			else{
				wlanPingNormalCount = 0;
				Log.d("harris","current wifi is not connected");
			}		
		}
	}	
	
	private void system_sync() {
		try {
			Process process = Runtime.getRuntime().exec("sync");
			process.waitFor();
		} catch (Exception e) {
			Log.e("exect", e.getMessage(), e);
		} finally {
			 
		}
	}	
	
	public static boolean ping_timeout(String web) {
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
			Log.d("harris", "ping_timeout result = " + result);
		}
		return false;
	}

	private void check_usb_camera_preview_work() {
		if("true".equals(SystemProperties.get("persist.cam.premonitor", "false"))){
			cameraPreview = SystemProperties.get("persist.cam.prestatus","false");
			if("true".equals(cameraPreview)){
				Log.d("harris","===== check_usb_camera_preview_work =====");
				String value = run_system_command(TYPE_NORMAL,"logcat -d | tail -n 10");
				if(value.length() > 0){
					if(value.contains("debugShowFPS")){
						camPreviewCount = 0;
						Log.d("harris","check_usb_camera_preview_work find");
					}
					else{
						Log.d("harris","check_usb_camera_preview_work no find");
						camPreviewCount++;		
						if(camPreviewCount >= 12){
							//60s
							Log.d("harris","#### usb camera should reset ####");
							camPreviewCount = 0;
							int index0 = Integer.parseInt(SystemProperties.get("persist.cam0.usb.index", "0")); //Yface J33
							String usb0Path = "/sys/class/gpio/gpio" + index0 +"/value";
							
							int index1 = Integer.parseInt(SystemProperties.get("persist.cam1.usb.index", "0")); //Yface J34
							String usb1Path = "/sys/class/gpio/gpio" + index1 +"/value";
							
							if(index0 > 0)
								run_system_command(TYPE_NORMAL,"echo 0 > "+ usb0Path);
							
							if(index1 > 0)
								run_system_command(TYPE_NORMAL,"echo 0 > "+ usb1Path);
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							if(index0 > 0)
								run_system_command(TYPE_NORMAL,"echo 1 > "+ usb0Path);
							
							if(index1 > 0)
								run_system_command(TYPE_NORMAL,"echo 1 > "+ usb1Path);
						}
					}
				}
			}
			else{
				camPreviewCount = 0;
			}	
		}
	}
	
	private boolean isWifiNetworkAvailable() {
		ConnectivityManager connectMgr = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo wifiInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
		if (wifiInfo != null && wifiInfo.isConnected()) { 
			return true;
		} else { 
			return false; 
		} 
	}	
	
	private boolean isEthNetworkAvailable() {
		ConnectivityManager connectMgr = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET); 
		if (ethNetInfo != null && ethNetInfo.isConnected()) { 
			return true; 
		} else { 
			return false; 
		} 
	}

	public String getEthernetIpAddress() {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		LinkProperties linkProperties =mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
		for (LinkAddress linkAddress: linkProperties.getAllLinkAddresses()) {
		    InetAddress address = linkAddress.getAddress();
		    if (address instanceof Inet4Address) {
		        return address.getHostAddress();
		    }
		}
		// IPv6 address will not be shown like WifiInfo internally does.
		return "null";
    	}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
