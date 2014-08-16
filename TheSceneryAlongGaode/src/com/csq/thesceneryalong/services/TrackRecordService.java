/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月27日 下午5:51:47   
 * @version 1.0   
 */
package com.csq.thesceneryalong.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.io.db.TrackPointDb;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.logic.manager.MyLocationManager.LocationCallback;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.models.TrackPointStatus;
import com.csq.thesceneryalong.ui.activitys.WelcomeActivity;
import com.csq.thesceneryalong.utils.WakeLockUtil;

public class TrackRecordService extends Service {

	

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private WakeLockUtil wakeLockUtil;
	
	private LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void locationChanged(Location newLoc) {
			// TODO Auto-generated method stub
			//记录轨迹点
			if(TrackManager.getInstance().isTrackRecording()){
				TrackPointDb.getInstance().addTrackPointToCurTrack(TrackPointStatus.normal);
			}
		}
	};

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		wakeLockUtil = new WakeLockUtil(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	// --------------------- Methods public ----------------------
	
	public static void bindService(Context context, ServiceConnection conn)
	{
		Intent intent = new Intent(context, TrackRecordService.class);
		// 如果先调用startService,则在多个服务绑定对象调用unbindService后服务仍不会被销毁
		// mContext.getApplicationContext().startService(intent);
		context.getApplicationContext().bindService(intent, conn,
				Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * 设置服务在前台运行，提高优先级
	 * 
	 * @author chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void startForeground()
	{
		Notification notification = new Notification(R.drawable.ic_launcher,
				App.getResources().getString(R.string.start_recording), 
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, WelcomeActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(this, 
				App.getResources().getString(R.string.app_name), 
				App.getResources().getString(R.string.recording),
				pendingIntent);
		startForeground(1, notification);
		
		wakeLockUtil.acquireWakeLock();
		
		MyLocationManager.getInstance().registLocationCallback(locationCallback);
		//开启定位
		MyLocationManager.getInstance().startListenLocation();
	}

	/**
	 * 取消在前台运行
	 * 
	 * @author chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void stopForeground()
	{
		stopForeground(true);
		
		wakeLockUtil.releaseWakeLock();
		
		MyLocationManager.getInstance().unRegistLocationCallback(locationCallback);
		//暂停定位
		MyLocationManager.getInstance().stopListenLocation();
	}

	// --------------------- Methods private ---------------------
	
	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
	
	// 服务绑定器
	public TrackRecordBinder mBinder = new TrackRecordBinder();

	public class TrackRecordBinder extends Binder
	{
		public TrackRecordService getService()
		{
			return TrackRecordService.this;
		}
	}
}
