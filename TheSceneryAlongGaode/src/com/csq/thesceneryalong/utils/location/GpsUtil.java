/**
 * @description: Gps相关工具
 * @author chenshiqiang E-mail:csqwyyx@163.com
 */
package com.csq.thesceneryalong.utils.location;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

import com.csq.thesceneryalong.app.App;

public class GpsUtil {

	public static Intent getLocationSetingIntent() {
		Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		return intent;
	}
	
	
	public static boolean hasGPSDevice = true;
	
	public static boolean isGPSOpen(){
		LocationManager lm = (LocationManager)App.app.getSystemService(Context.LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	public static void setGPS(Activity context,int requestCode){
	    try {
	        context.startActivityForResult(getLocationSetingIntent(), requestCode);
        }
        catch (Exception e) {
            // TODO: handle exception
            Log.e("", "setGPS  "+e.toString());
        }
	}
	
	/**
	 * 判定手机是否支持gps的初始化操作
	 * @param app
	 */
	public static void checkGPSDevice()
	{
	    final LocationManager mgr = (LocationManager)App.app.getSystemService(Context.LOCATION_SERVICE);
	    if ( mgr == null ) 
	    	hasGPSDevice = false;
	    final List<String> providers = mgr.getAllProviders();
	    if ( providers == null ){
	    	hasGPSDevice = false;
	    }
	    hasGPSDevice = providers.contains(LocationManager.GPS_PROVIDER);
	}
	
}
