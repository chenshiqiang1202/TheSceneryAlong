/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月22日 下午8:58:21   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.csq.thesceneryalong.app.App;

public class DeviceUtil {
	
	/**
	 * @description: 获得轨迹的唯一标识,时间+DeviceId/UUID
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static String getTrackUniqueMark(){
		String u = getDeviceId();
		if(TextUtils.isEmpty(u)){
			u = getUUID();
		}
		return DateUtils.getFormatedDateYMDHMSFile(System.currentTimeMillis())
				+ "_" + u;
	}
	
	/**
	 * @description: 获取Android设备的唯一识别码
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public static String getUUID(){
		final TelephonyManager tm = (TelephonyManager) 
				App.app.getSystemService(Context.TELEPHONY_SERVICE);
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(App.app.getContentResolver(), 
	    		android.provider.Settings.Secure.ANDROID_ID);
	    UUID deviceUuid = new UUID(androidId.hashCode(), 
	    		((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid.toString();
	}
	
	/**
	 * @description: Android系统为开发者提供的用于标识手机设备的串号，也是各种方法中普适性较高的，
	 * 		可以说几乎所有的设备都可以返回这个串号，并且唯一性良好
	 * 		它会根据不同的手机设备返回IMEI，MEID或者ESN码
	 * 获取DEVICE_ID需要READ_PHONE_STATE权限
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static String getDeviceId(){
		TelephonyManager tm = (TelephonyManager)
				App.app.getSystemService(Context.TELEPHONY_SERVICE); 
		return tm.getDeviceId();
	}
	
	
	public static String getMacAddress() {  
	    String result = "";  
	    WifiManager wifiManager = (WifiManager) App.app.getSystemService(Context.WIFI_SERVICE);  
	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();  
	    result = wifiInfo.getMacAddress();  
	    return result;  
	}
	
	public static class ScreenInfo{
		public final int width;
		public final int height;
		public final float density;
		public final int dpi;
		
		public ScreenInfo(int width, int height, float density, int dpi) {
			super();
			this.width = width;
			this.height = height;
			this.density = density;
			this.dpi = dpi;
		}
	}
	
	public static ScreenInfo getScreenInfo() {  
		DisplayMetrics dm = App.getResources().getDisplayMetrics();  
	    int width = dm.widthPixels; // 宽  
	    int height = dm.heightPixels; // 高  
	    float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）  
	    int densityDpi = dm.densityDpi;
	    return new ScreenInfo(width, height, density, densityDpi);
	}
	
	public static String getAvailMemory() { 
	    ActivityManager am = (ActivityManager) App.app.getSystemService(Context.ACTIVITY_SERVICE);  
	    MemoryInfo mi = new MemoryInfo();  
	    am.getMemoryInfo(mi);  
	    return Formatter.formatFileSize(App.app, mi.availMem);// 将获取的内存大小规格化  
	}  
	 
	public static String getTotalMemory() {  
	    String str1 = "/proc/meminfo";// 系统内存信息文件  
	    String str2;  
	    String[] arrayOfString;  
	    long initial_memory = 0;  
	    try {  
	        FileReader localFileReader = new FileReader(str1);  
	        BufferedReader localBufferedReader = new BufferedReader(  
	                localFileReader, 8192);  
	        str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小  
	 
	        arrayOfString = str2.split("\\s+");  
	        for (String num : arrayOfString) {  
	            Log.i(str2, num + "\t");  
	        }  
	 
	        initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte  
	        localBufferedReader.close();  
	 
	    } catch (IOException e) {  
	    }  
	    return Formatter.formatFileSize(App.app, initial_memory);// Byte转换为KB或者MB，内存大小规格化  
	}
}
