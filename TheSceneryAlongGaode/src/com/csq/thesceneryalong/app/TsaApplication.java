/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月22日 下午9:00:37   
 * @version 1.0   
 */
package com.csq.thesceneryalong.app;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import com.csq.thesceneryalong.config.Configer;
import com.csq.thesceneryalong.receiver.NetworkReceiver;
import com.csq.thesceneryalong.services.TrackRecordService;
import com.csq.thesceneryalong.utils.DeviceUtil;
import com.csq.thesceneryalong.utils.DeviceUtil.ScreenInfo;
import com.csq.thesceneryalong.utils.MyHandler;
import com.csq.thesceneryalong.utils.location.GpsUtil;

import java.util.List;

public class TsaApplication extends Application {
	
	/**
     * 服务状态改变，开启或关闭
     */
    private final int MSG_SERVICE_STATUS_UPDATE = 1;
    
    private MyHandler handler;
    
    private TrackRecordService mTrackRecordService = null;
    /**
	 * 绑定服务
	 */
	private ServiceConnection conn = new ServiceConnection() {	  
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
        	mTrackRecordService = ((TrackRecordService.TrackRecordBinder)service).getService();
        }  
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        }  
    }; 
    
    private ScreenInfo screenInfo;
    
    private NetworkReceiver networkReceiver;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		App.app = this;
		
		GpsUtil.checkGPSDevice();
		
		screenInfo = DeviceUtil.getScreenInfo();
		
		TrackRecordService.bindService(this, conn);
		
		setPolicy();
		
		networkReceiver = new NetworkReceiver();
		networkReceiver.regist(this);

		handler = new MyHandler(this){
	    	@Override
	    	public void myHandleMessage(Message msg) {
	    		// TODO Auto-generated method stub
	    		switch (msg.what) {
				case MSG_SERVICE_STATUS_UPDATE:
					
					//msg.arg1 == 0开启服务
					boolean isStart = msg.arg1 == 0 ? true : false;	
					if(isStart){
						startForegroundIn();
					}else{
						stopForegroundIn();
					}
					break;

				default:
					break;
				}
	    	}

	    };
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD) 
	private void setPolicy() {
	    if (Configer.isDebugMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectAll()
	                 .penaltyLog()
	                 .build());
	     }
    }
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		App.app = null;
		
		networkReceiver.unRegist(this);
		
		this.unbindService(conn);
	}
	
	
	private synchronized void startForegroundIn(){
		if(mTrackRecordService != null){
			mTrackRecordService.startForeground();
	    }
	}
	
	public synchronized void startForeground(){
	    Message msg = handler.obtainMessage();
	    msg.what = MSG_SERVICE_STATUS_UPDATE;
	    msg.arg1 = 0;
	    
	    handler.removeMessages(MSG_SERVICE_STATUS_UPDATE);
	    handler.sendMessage(msg);
	}
	
	private synchronized void stopForegroundIn(){
		if(mTrackRecordService != null)
	    {
			mTrackRecordService.stopForeground();
        }
	}
	
	public synchronized void stopForeground()
    {
		Message msg = handler.obtainMessage();
	    msg.what = MSG_SERVICE_STATUS_UPDATE;
	    msg.arg1 = 1;
	    
	    handler.removeMessages(MSG_SERVICE_STATUS_UPDATE);
	    handler.sendMessage(msg);
    }
	
	public ScreenInfo getScreenInfo() {
		return screenInfo;
	}
	
	public int getScreenHeight() {
		return screenInfo.height;
	}
	
	public int getScreenWidth() {
		return screenInfo.width;
	}
	
	/*public int getScreenRotate(){
		int orientation = display.getRotation();
		if(orientation == Surface.ROTATION_0){
			return 0;
		}else if(orientation == Surface.ROTATION_90){
			return 90;
		}else if(orientation == Surface.ROTATION_180){
			return 180;
		}else if(orientation == Surface.ROTATION_270){
			return 270;
		}
		return 0;
	}*/
	
	/**
	 * 检查应用是否在前台显示
	 */
	public boolean isToolsForeground() {
		// 获得当前运行的任务
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        try {
        	List<RunningTaskInfo> rti = activityManager.getRunningTasks(1);
        	String currentPkg = rti.get(0).topActivity.getPackageName();
        	return currentPkg.equals(getApplicationInfo().packageName);
        } catch (Exception e) {
        	// 如果正在被卸载时，会出现异常
        }
        return false;
	}
	
}
