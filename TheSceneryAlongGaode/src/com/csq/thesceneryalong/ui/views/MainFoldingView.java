/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 下午3:10:01   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.views;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.config.Configer;
import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.logic.manager.TrackImportManager;
import com.csq.thesceneryalong.models.events.EventTrackImport;
import com.csq.thesceneryalong.ui.activitys.AboutActivity;
import com.csq.thesceneryalong.ui.activitys.HelpActivity;
import com.csq.thesceneryalong.ui.activitys.OfflineMapActivity;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.utils.AppUtil;
import com.csq.thesceneryalong.utils.DeviceUtil.ScreenInfo;
import com.csq.thesceneryalong.utils.EmailUtil;
import com.csq.thesceneryalong.utils.ShareUtil;
import com.csq.thesceneryalong.utils.ToastUtil;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.List;

public class MainFoldingView extends LinearLayout implements View.OnClickListener{

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private String strNoImportTsaFile;
	
	protected View tvImportTracks;
	
	protected Activity activity;

	// ----------------------- Constructors ----------------------
	
	public MainFoldingView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public MainFoldingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	private void initView(Context context){
		activity = (Activity) context;
		setOrientation(VERTICAL);
		LayoutInflater.from(activity).inflate(R.layout.main_folding_view, 
				this, 
				true);
		
		tvImportTracks = findViewById(R.id.tvImportTracks);
		strNoImportTsaFile = getResources().getString(R.string.strNoImportTsaFile)
				.replace("{a}", PathConstants.getImportpath());

		findViewById(R.id.tvImportTracks).setOnClickListener(this);
		findViewById(R.id.tvOfflineMap).setOnClickListener(this);
		findViewById(R.id.tvFeedback).setOnClickListener(this);
		findViewById(R.id.tvShare).setOnClickListener(this);
		findViewById(R.id.tvHelp).setOnClickListener(this);
		findViewById(R.id.tvAbout).setOnClickListener(this);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------
	
	public void onEventMainThread(EventTrackImport event){
		StringBuilder sb = new StringBuilder();
		Style style = Style.INFO;
		sb.append( (event.curIndex + 1) + " / " + event.totalSize + " ");
		if(event.track == null){
			sb.append(getResources().getString(R.string.strImportTsaFile));
			style = Style.ALERT;
		}else{
			sb.append(getResources().getString(R.string.strImportTsaSuccess).replace("{a}", event.track.getName()));
		}
		ToastUtil.showToastInfo(activity, 
				sb.toString(), 
				style, 
				false);
		
		//导入完成，可以再导入
		if(event.curIndex + 1 == event.totalSize){
			if(tvImportTracks != null){
				tvImportTracks.setEnabled(true);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tvImportTracks:
			//导入
	    	List<String> tsaFiles = TrackUtil.getImportTasFiles();
	    	if(tsaFiles.isEmpty()){
	    		//没有要导入的文件，提醒
	    		ToastUtil.showToastInfo(activity, 
	    				strNoImportTsaFile, 
	    				Style.ALERT, 
	    				true);
	    	}else{
	    		//开始导入
	    		//先取消轨迹导入的点击事件
	    		tvImportTracks.setEnabled(false);
	    		//再启动轨迹导入线程
	    		ToastUtil.showToastInfo(activity, 
	    				getResources().getString(R.string.strImportTsaFileStart).replace("{a}", "" + tsaFiles.size()), 
	    				Style.INFO, 
	    				false);
	    		TrackImportManager.getInstance().addImportList(tsaFiles);
	    	}
			break;
			
		case R.id.tvOfflineMap:
			BaseActionBarActivity.startActivity(activity, 
					OfflineMapActivity.class, 
					false);
			break;
			
		case R.id.tvFeedback:
			String title = getResources().getString(R.string.feedbackTitle).replace("{a}", AppUtil.getVerName());
			ScreenInfo sInfo = App.app.getScreenInfo();
			String msg = getResources().getString(R.string.feedbackMsg) + "\n\n\n\n" 
					+ "Device info : \n"
					+ "DeviceName : " + Build.DEVICE + "-" + Build.MODEL + "\n"
					+ "SdkLevel : " + Build.VERSION.RELEASE + "\n"
					+ "AppVersion : " + AppUtil.getVerName() + "\n"
					+ "ScreenInfo : w = " + sInfo.width + ", h = " + sInfo.height + ", dpi = " + sInfo.dpi;
			EmailUtil.send(getContext(), Configer.MyEmail, title, msg);
			break;
			
		case R.id.tvShare:
			ShareUtil.shareText(activity, 
					getResources().getString(R.string.strShareChooseTitle), 
					getResources().getString(R.string.strShareContent));
			break;
			
		case R.id.tvHelp:
			BaseActionBarActivity.startActivity(activity, HelpActivity.class, false);
			break;
			
		case R.id.tvAbout:
			BaseActionBarActivity.startActivity(activity, AboutActivity.class, false);
			break;

		default:
			break;
		}
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
