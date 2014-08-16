/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月23日 下午9:19:09   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys.base;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;

import com.csq.thesceneryalong.ui.dialogs.ProgressLoadingDialog;
import com.umeng.analytics.MobclickAgent;

abstract public class BaseActionBarActivity extends ActionBarActivity {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected FragmentActivity activity;
	protected FragmentManager fragmentManager;
	
	protected ProgressLoadingDialog mLoadingDialog;

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.activity = this;
		fragmentManager = getSupportFragmentManager();
		
		try {
			ViewConfiguration mconfig = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(mconfig, false);
			}
		} catch (Exception ex) {
		}
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(0, 0);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		releaseResources();
		System.gc();
	}
	
	// --------------------- Methods public ----------------------
	
	protected <T> T getViewById(int id){
		return (T) findViewById(id);
	}

	public void startActivity(Class<?> cls,boolean finish){
		Intent intent = new Intent(this,cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		if(finish){
			finish();
		}
	}
	
	public static void startActivity(Context context,Class<?> cls,boolean finish){
		Intent intent = new Intent(context,cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(intent);
		if(finish && context instanceof Activity){
			Activity ac = (Activity)context;
			ac.finish();
		}
	}
	
	public void exitApp()
	{
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	public void showLoadingDialog(String msg){
		if(mLoadingDialog == null){
			mLoadingDialog = new ProgressLoadingDialog(activity, msg);
		}else{
			mLoadingDialog.dismiss();
		}
		mLoadingDialog.show();
	}
	
	public void updateLoadingDialogMsg(String msg){
		if(mLoadingDialog != null){
			mLoadingDialog.updataMsg(msg);
		}
	}
	
	public void updateLoadingDialogProgress(int progress){
		if(mLoadingDialog != null){
			mLoadingDialog.updataProgress(progress);
		}
	}
	
	public void hideLoadingDialog(){
		if(mLoadingDialog != null){
			mLoadingDialog.dismiss();
		}
	}
	
	// --------------------- Methods private ---------------------
	
	/**
	 * @description: 界面退出时释放占用内存特别多的资源
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	abstract protected void releaseResources();

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
