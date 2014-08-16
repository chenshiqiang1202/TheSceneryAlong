/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月27日 下午5:26:53   
 * @version 1.0   
 */
package com.csq.thesceneryalong.logic.manager;

import java.util.ArrayList;
import java.util.List;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.utils.location.LastLocationValidate;
import com.csq.thesceneryalong.utils.location.LocationUtil;


public class MyLocationManager {

	// ------------------------ Constants ------------------------
	
	private volatile static MyLocationManager instance;

	public static MyLocationManager getInstance() {
		synchronized (MyLocationManager.class) {
			if (instance == null) {
				instance = new MyLocationManager();
			}
		}
		return instance;
	}
	
	private MyLocationManager() {
		locationManager = LocationManagerProxy
				.getInstance(App.app);
		mLocationValider = new LastLocationValidate();
	}

	// ------------------------- Fields --------------------------
	
	private LocationManagerProxy locationManager;
	
	/** 当前位置  */
    private Location mCurrentLocation;
    
    private LastLocationValidate mLocationValider = null;
	
	private AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onProviderDisabled(String provider) {
			// Do nothing
		}

		@Override
		public void onProviderEnabled(String provider) {
			// Do nothing
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// Do nothing
		}

		@Override
		public void onLocationChanged(final AMapLocation loc) {
			if(loc == null){
        		return;
        	}
        	
        	if(loc.getTime() == 0){
        		//网络定位，时间可能为0
        		loc.setTime(System.currentTimeMillis());
        	}
        	
        	//纠偏,通知保存的都是gcj的经纬度
        	LocationUtil.wgsToGcj(loc, false);
        	
        	if(mCurrentLocation == null || mLocationValider.isBetterLocation(loc, mCurrentLocation)){
        		//位置有效
        		mCurrentLocation = loc;
        		
        		handler.removeMessages(WHAT_LOCATION_CHANGED);
        		Message msg = handler.obtainMessage();
            	msg.what = WHAT_LOCATION_CHANGED;
            	msg.obj = mCurrentLocation;
            	handler.sendMessage(msg);
        	}
		}

		@Override
		public void onLocationChanged(Location arg0) {
			// TODO Auto-generated method stub
			
		}

	};
	
	private final int WHAT_LOCATION_CHANGED = 6;
	private Handler handler = new Handler(new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == WHAT_LOCATION_CHANGED){
				
				Location loc = (Location) msg.obj;
				
				//位置更新相关操作
				for(LocationCallback lc : locationCallbacks){
					lc.locationChanged(loc);
				}
			}
			return false;
		}
	});
	
	/**
	 * 定位最短时间，毫秒
	 */
	private long locateMinTimeSeconds  = 5000;
	
	/**
	 * 定位最短距离，米
	 */
	private float locateMinDistanceMeters = 8;
	
	/**
	 * 所有应用程序位置监听器
	 */
	private List<LocationCallback> locationCallbacks 
		= new ArrayList<LocationCallback>();
	

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------
	
	private volatile boolean isListenLocation = false;
	/**
	 * @description: 开始定位
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void startListenLocation(){
		if(!isListenLocation){
			if(locationManager.getProvider(LocationProviderProxy.AMapNetwork) != null){
				// API定位采用GPS定位方式，第一个参数是定位provider，第二个参数时间最短是2000毫秒
				//第三个参数距离间隔单位是米，第四个参数是定位监听者
				locationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 
						locateMinTimeSeconds, 
						locateMinDistanceMeters, 
						locationListener);
			}
			
			if(locationManager.getProvider(LocationManager.GPS_PROVIDER) != null){
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
						locateMinTimeSeconds, 
						locateMinDistanceMeters, 
						locationListener);
			}
			
			isListenLocation = true;
		}
	}
	
	/**
	 * @description: 停止定位
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void stopListenLocation(){
		//只要有需要回调位置的地方，不能停止定位
		if(isListenLocation && locationCallbacks.isEmpty()){
			locationManager.removeUpdates(locationListener);
			
			isListenLocation = false;
		}
	}
	
	/**
	 * @description: 注册一个位置回调
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param listener
	 */
	public void registLocationCallback(LocationCallback listener){
		locationCallbacks.add(listener);
	}
	/**
	 * @description: 取消注册一个位置回调
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param listener
	 */
	public void unRegistLocationCallback(LocationCallback listener){
		locationCallbacks.remove(listener);
	}

	// --------------------- Methods private ---------------------
	
	

	// --------------------- Getter & Setter ---------------------

	/**
	 * @description: 获取当前位置
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public Location getCurrentLocation() {
		return mCurrentLocation;
	}
	
	/**
	 * @description: 获取最近gcj位置，先获取mCurrentLocation，再getLastKnowLocation(先gps，再网络)
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public Location getLatestKnowLocation() {
		Location loc = getCurrentLocation();
		if(loc == null){
			loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(loc == null){
				loc = locationManager.getLastKnownLocation(LocationProviderProxy.AMapNetwork);
			}
			//不是通过getLastKnownLocation获得的，都是新的，可以改变原始对象
			if(loc != null){
				LocationUtil.wgsToGcj(loc, false);
			}
		}
		return loc;
	}
	
	// --------------- Inner and Anonymous Classes ---------------
	
	public interface LocationCallback{
		public void locationChanged(Location newLoc);
	}
}
