/**
 * @description: 欢迎界面
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月23日 下午9:37:05   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;

import com.csq.thesceneryalong.io.db.DbManager;
import com.csq.thesceneryalong.io.db.TrackDb;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.ui.activitys.base.TemplateActionBarActivity;
import com.csq.thesceneryalong.ui.fragments.WelcomeFragment;
import com.csq.thesceneryalong.utils.SdcardUtils;
import com.csq.thesceneryalong.utils.tasks.CsqBackgroundTask;

public class WelcomeActivity extends TemplateActionBarActivity {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private static volatile int onCreateTimes = 0;
	private long onCreateTime = 0;
	
	private WelcomeFragment welcomeFragment;
	
	private Handler handler = new Handler(Looper.getMainLooper());

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		welcomeFragment = new WelcomeFragment();
        setFragement(welcomeFragment);

        //初始化数据库
        try{
            DbManager.getInstance().init();
        }catch (Exception e){
            Log.e("", e.toString());
        }

		viewInited();
		
		onCreateTime = System.currentTimeMillis();
	}
	
	protected void viewInited(){
		if(!SdcardUtils.isSdcardExist() || !DbManager.getInstance().dbInited){
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					finish();
				}
			}, 3000);
			return;
		}		
		
		if(onCreateTimes++ == 0){
			//程序第一次运行，做程序启动的初始化操作
			MyLocationManager.getInstance();  //MyLocationManager里面Handler handler = new Handler不能在BackgroundThread执行
			initApp();
			
		}else{
			//程序后台运行恢复，仅显示1秒
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					startActivity(MainActivity.class, true);
				}
			}, 1000);
		}
	}
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub
		
	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------
	void initApp(){
		new CsqBackgroundTask<Void>() {
			@Override
			protected Void onRun() {
				// TODO Auto-generated method stub
				TrackDb.getInstance().loadAllTrackToMemory();
				TrackManager.getInstance().resumeIfHaveRecordingTrack();
				
				long excuteTime = System.currentTimeMillis() - onCreateTime;
				if(excuteTime < 1500){
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							startActivity(MainActivity.class, true);
						}
					}, 1500 - excuteTime);
					
				}else{
					startActivity(MainActivity.class, true);
				}
				
				return null;
			}

			@Override
			protected void onResult(Void result) {
				// TODO Auto-generated method stub
				
			}
		}.start();
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
	
}
