/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月3日 下午3:55:27   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.constant.GlobelConstants;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.io.db.SceneryDb;
import com.csq.thesceneryalong.io.db.TrackDb;
import com.csq.thesceneryalong.io.db.TrackPointDb;
import com.csq.thesceneryalong.io.file.SpUtils;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.events.EventCurTrackStatusChanged;
import com.csq.thesceneryalong.models.events.EventNewScenery;
import com.csq.thesceneryalong.models.events.EventNewTrackPoint;
import com.csq.thesceneryalong.models.events.EventSceneryNumChanged;
import com.csq.thesceneryalong.models.events.EventTrackUpdated;
import com.csq.thesceneryalong.models.models.PathConfig;
import com.csq.thesceneryalong.models.models.RecordStatus;
import com.csq.thesceneryalong.models.models.SceneryCluster;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.ui.fragments.base.BaseMapFragment;
import com.csq.thesceneryalong.ui.fragments.base.BaseMapFragment.MyLocationTypeListener;
import com.csq.thesceneryalong.ui.views.TrackCtrlView;
import com.csq.thesceneryalong.ui.views.TrackDetailDrawerView;
import com.csq.thesceneryalong.ui.widgets.MultiDirectionSlidingDrawer;
import com.csq.thesceneryalong.ui.widgets.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.csq.thesceneryalong.ui.widgets.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.csq.thesceneryalong.utils.CaptureMediaUtil;
import com.csq.thesceneryalong.utils.CaptureMediaUtil.MediaData;
import com.csq.thesceneryalong.utils.MapUtil;
import com.csq.thesceneryalong.utils.ToastUtil;
import com.csq.thesceneryalong.utils.dbmodel.SceneryUtil;
import com.csq.thesceneryalong.utils.dbmodel.TrackPointUtil;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;
import com.csq.thesceneryalong.utils.location.GpsUtil;
import com.csq.thesceneryalong.utils.tasks.CsqBackgroundTask;
import com.gaode.maps.android.clustering.Cluster;
import com.gaode.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.gaode.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.nhaarman.supertooltips.ToolTipView.OnToolTipViewClickedListener;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class TrackDetailMapActivity extends BaseActionBarActivity 
									implements ISimpleDialogListener{

	// ------------------------ Constants ------------------------
	
	public static final String EXTRA_TRACK_ID = "extra_track_id";
	
	private static final int SimpleDialogDeleteTrackRequestCode = 1;
	private static final int SimpleDialogOpenGpsRequestCode = 2;

	// ------------------------- Fields --------------------------
	
	protected long extra_track_id;
	protected BaseMapFragment fmMap;
	
	protected TrackCtrlView vTrackCtrl;
	protected SmoothProgressBar hProgressBar;
	protected TrackDetailDrawerView vDetailDrawer;
	protected MultiDirectionSlidingDrawer lyDrawer;
	protected ToolTipRelativeLayout lyTooltipframe;
	private TextView tvMyLocMode;
	
	protected int crCurTrackLine;
	protected int crFinishedTrackLine;
	protected int path_width;
	protected String strTrackHaveNoPoints;
	protected String strSavingAScenery;
	protected String strSavingAScenerySuccess;
	protected String strSavingASceneryFail;
	protected String strExportATrackStart;
	protected String strExportATrackSuccess;
	protected String strExportATrackFailed;
	
	
	private Track track;
	private List<Scenery> mSenerys;
	private List<PathConfig> mPaths;
	
	private boolean isRecordingTrack = false;
	
	/**
	 * 单个风景点击
	 */
	private OnClusterItemClickListener<SceneryCluster> onClusterItemClickListener = new OnClusterItemClickListener<SceneryCluster>() {
		@Override
		public boolean onClusterItemClick(SceneryCluster item) {
			// TODO Auto-generated method stub
			if(track != null && mSenerys != null){
				for(int i = 0, num = mSenerys.size(); i < num ; i++){
					if(mSenerys.get(i).getId() == item.getScenery().getId()){
						ScenerysActivity.launch(activity, track.getId(), track.getName(), i);
						break;
					}
				}
			}
			return false;
		}
	};
	/**
	 * 多个风景重叠点击
	 */
	private OnClusterClickListener<SceneryCluster> onClusterClickListener = new OnClusterClickListener<SceneryCluster>() {
		@Override
		public boolean onClusterClick(Cluster<SceneryCluster> cluster) {
			// TODO Auto-generated method stub
			if(track != null && mSenerys != null){
				Scenery top = cluster.getItems().iterator().next().getScenery();
				for(int i = 0, num = mSenerys.size(); i < num ; i++){
					if(mSenerys.get(i).getId() == top.getId()){
						ScenerysActivity.launch(activity, track.getId(), track.getName(), i);
						break;
					}
				}
			}
			return false;
		}
	};
	
	private ToolTipView mPauseTipView, mStopTipView, mGetSceneryTipView;
	
	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_track_detail);
		
		//标题左边返回箭头
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		vTrackCtrl = getViewById(R.id.vTrackCtrl);
		hProgressBar = getViewById(R.id.hProgressBar);
		vDetailDrawer = getViewById(R.id.vDetailDrawer);
		lyDrawer = getViewById(R.id.lyDrawer);
		lyTooltipframe = getViewById(R.id.lyTooltipframe);
		tvMyLocMode = getViewById(R.id.tvMyLocMode);
		extra_track_id = getIntent().getLongExtra(EXTRA_TRACK_ID, 0);
		fmMap = (BaseMapFragment) getSupportFragmentManager().findFragmentById(R.id.fmMap);
		crCurTrackLine = getResources().getColor(R.color.crCurTrackLine);
		crFinishedTrackLine = getResources().getColor(R.color.crFinishedTrackLine);
		path_width = getResources().getDimensionPixelSize(R.dimen.path_width);
		strTrackHaveNoPoints = getResources().getString(R.string.strTrackHaveNoPoints);
		strSavingAScenery = getResources().getString(R.string.strSavingAScenery);
		strSavingAScenerySuccess = getResources().getString(R.string.strSavingAScenerySuccess);
		strSavingASceneryFail = getResources().getString(R.string.strSavingASceneryFail);
		strExportATrackStart = getResources().getString(R.string.strExportATrackStart);
		strExportATrackSuccess = getResources().getString(R.string.strExportATrackSuccess);
		strExportATrackFailed = getResources().getString(R.string.strExportATrackFailed);
		
		isRecordingTrack = TrackManager.getInstance().isTrackRecording(extra_track_id);
		if(isRecordingTrack){
			tvMyLocMode.setVisibility(View.VISIBLE);
			fmMap.setCanChangeMyLocationType(true);
			tvMyLocMode.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					fmMap.changeMyLocationType();
				}
			});
			fmMap.setMyLocationTypeListener(new MyLocationTypeListener() {
				@Override
				public void myLocationTypeChanged(int type) {
					// TODO Auto-generated method stub
					if(type == AMap.LOCATION_TYPE_MAP_FOLLOW){
						tvMyLocMode.setText(R.string.locationModeFollow);
					}else if(type == AMap.LOCATION_TYPE_MAP_ROTATE){
						tvMyLocMode.setText(R.string.locationModeRotate);
					}else{
						tvMyLocMode.setText(R.string.locationModeLocate);
					}
				}
			});
		}else{
			tvMyLocMode.setVisibility(View.INVISIBLE);
			fmMap.setCanChangeMyLocationType(false);
		}
		
		viewsInited();
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isRecordingTrack){
			if(!GpsUtil.isGPSOpen()){
				SimpleDialogFragment.createBuilder(this, getSupportFragmentManager())
					.setTitle(R.string.dialog_title_gps_not_open_title)
					.setMessage(R.string.dialog_title_gps_not_open_msg)
					.setPositiveButtonText(R.string.yes)
					.setNegativeButtonText(R.string.no)
					.setRequestCode(SimpleDialogOpenGpsRequestCode)
					.show();
			}
		}
	}
	
	protected void viewsInited(){
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		
		fmMap.setOnSceneryClusterItemClickListener(onClusterItemClickListener);
		fmMap.setOnClusterClickListener(onClusterClickListener);
		
		if(isRecordingTrack){
			vTrackCtrl.setVisibility(View.VISIBLE);
			lyDrawer.setVisibility(View.INVISIBLE);
		}else{
			vTrackCtrl.setVisibility(View.GONE);
			lyDrawer.setVisibility(View.VISIBLE);
			
			lyDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
				@Override
				public void onDrawerOpened() {
					// TODO Auto-generated method stub
					lyDrawer.getHandle().setBackgroundResource(R.drawable.ic_map_drawer_down);
				}
			});
			lyDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
				@Override
				public void onDrawerClosed() {
					// TODO Auto-generated method stub
					lyDrawer.getHandle().setBackgroundResource(R.drawable.ic_map_drawer_up);
				}
			});
		}
		
		//异步加载轨迹
		new CsqBackgroundTask<Track>(this) {
			@Override
			protected Track onRun() {
				// TODO Auto-generated method stub
				return TrackDb.getInstance().queryById(extra_track_id);
			}

			@Override
			protected void onResult(Track result) {
				// TODO Auto-generated method stub
				trackLoaded(result);
			}
		}.start();
		
		if(isRecordingTrack){
			if(!SpUtils.isPauseAndStopRecordToastShowed()){
				addPauseTipView();
			}
		}
	}
	
	/**
	 * @description: 轨迹异步回调函数
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 */
	public void trackLoaded(final Track track){
		this.track = track;
		
		if(track == null){
			finish();
			return;
		}
		
		getSupportActionBar().setTitle(track.getName());
		
		if(!isRecordingTrack){
			vDetailDrawer.updateTrack(track);
		}
		
		//异步加载路线和风景
		new CsqBackgroundTask<Void>(this) {
			@Override
			protected Void onRun() {
				// TODO Auto-generated method stub
				updatePathAndSceneryTimes++;
				
				if(isRecordingTrack){
					mPaths = TrackPointDb.getInstance()
							.getCurTrackPointsLatLng(crCurTrackLine, path_width);
					
					mSenerys = SceneryDb.getInstance().getCurScenerys();
					
					if(updatePathAndSceneryTimes == 1){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//轨迹居中
								MapUtil.centerTrackPoints(fmMap.getMap(), 
										TrackPointDb.getInstance().getCurTrackPoints());
							}
						});
					}
					
				}else{
					final List<TrackPoint> tps = track.getTrackPoints();
					track.resetTrackPoints();
					mPaths = TrackPointUtil
							.getTrackPointsLatLng(tps, crFinishedTrackLine, path_width);
					
					mSenerys = track.getScenerys();
					track.resetScenerys();
					
					if(updatePathAndSceneryTimes == 1){
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								//轨迹居中
								MapUtil.centerTrackPoints(fmMap.getMap(), 
										tps);
							}
						});
					}
					
				}
				return null;
			}

			@Override
			protected void onResult(Void result) {
				// TODO Auto-generated method stub
				pathAndSceneryLoaded(result);
			}
		}.start();
	}
	
	private int updatePathAndSceneryTimes = 0;
	/**
	 * @description: 路径和风景异步回调函数
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param result
	 */
	public void pathAndSceneryLoaded(Void result){
		if(mPaths != null){
			fmMap.updatePaths(mPaths);
		}
		
		if(mSenerys != null){
			new CsqBackgroundTask<Void>() {
				
				List<SceneryCluster> scs = new ArrayList<SceneryCluster>();
				
				protected void onPreExecute() {
					showHProgressBar();
				};
				
				@Override
				protected Void onRun() {
					// TODO Auto-generated method stub
					SceneryCluster sc = null;
					for(Scenery s : mSenerys){
						sc = new SceneryCluster(s);
						sc.getPicture();
						scs.add(sc);
					}
					return null;
				}
				
				protected void onPostExecute(Void t) {
					hideHProgressBar();
					fmMap.setScenerys(scs);
				}

				@Override
				protected void onResult(Void result) {
					// TODO Auto-generated method stub
					
				};
				
			}.start();
			
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		final MediaData media = CaptureMediaUtil.handleActivityResult(this, 
				requestCode, 
				resultCode, 
				data);
		
		if(media != null){
			new CsqBackgroundTask<Boolean>(this) {
				
				protected void onPreExecute() {
					ToastUtil.showToastInfo(activity, 
							strSavingAScenery, 
							Style.INFO, 
							false);
				};
				
				@Override
				protected Boolean onRun() {
					// TODO Auto-generated method stub
					Scenery s = SceneryUtil.newSceneryOfCurTrack(media);
					if(s != null){
						return SceneryDb.getInstance().addToCurTrack(s) > 0;
					}
					return false;
				}

				@Override
				protected void onResult(Boolean result) {
					// TODO Auto-generated method stub
					saveASceneryFinished(result);
				}
			}.start();
		}
	}
	
	/**
	 * @description: 保存一个风景
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void saveASceneryFinished(Boolean success){
		String info = null;
		if(success){
			info = strSavingAScenerySuccess;
		}else{
			info = strSavingASceneryFail;
		}
		ToastUtil.showToastInfo(activity, 
				info, 
				Style.INFO, 
				false);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
	}
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();  
		if(isRecordingTrack){
	        inflater.inflate(R.menu.menu_recording_map, menu);  
		}else{
			inflater.inflate(R.menu.menu_track_detail, menu);  
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		int id;
		switch (GlobelConstants.mapType) {
		case AMap.MAP_TYPE_NORMAL:
			id = R.id.menu_map_type_map;
			break;
		case AMap.MAP_TYPE_SATELLITE:
			id = R.id.menu_map_type_satellite;
			break;
		case AMap.MAP_TYPE_NIGHT:
			id = R.id.menu_map_type_night;
			break;
		default:
			id = R.id.menu_map_type_map;
		}
		MenuItem menuItem = menu.findItem(id);
		if (menuItem != null) {
			menuItem.setChecked(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
	        case android.R.id.home:
				finish();
	        	break;
	        	
	        case R.id.options_item_pic:
				if(MyLocationManager.getInstance().getCurrentLocation() != null){
					//已经有记录到轨迹点
					CaptureMediaUtil.takePicture(activity);
				}else{
					ToastUtil.showToastInfo(activity, 
							strTrackHaveNoPoints, 
							Style.ALERT, 
							false);
				}
	        	break;
	        	
	        case R.id.options_item_video:
	        	if(MyLocationManager.getInstance().getCurrentLocation() != null){
					//已经有记录到轨迹点
	        		CaptureMediaUtil.recordVideo(activity);
				}else{
					ToastUtil.showToastInfo(activity, 
							strTrackHaveNoPoints, 
							Style.ALERT, 
							false);
				}
	        	break;
	        	
	        case R.id.options_item_delete:
	        	SimpleDialogFragment.createBuilder(activity, getSupportFragmentManager())
					.setTitle(R.string.deleteTrack)
					.setMessage(R.string.deleteTrackWarn)
					.setPositiveButtonText(R.string.deleteTrackExport)
					.setNegativeButtonText(R.string.deleteTrackUnExport)
					.setRequestCode(SimpleDialogDeleteTrackRequestCode)
					.show();
	        	break;
	        	
	        case R.id.options_item_edit:
	        	EditTrackActivity.launch(activity, extra_track_id);
	        	break;
	        	
	        case R.id.options_item_export:
	        	new CsqBackgroundTask<String>(this) {
	        		protected void onPreExecute() {
	        			showLoadingDialog(strExportATrackStart);
	        		};
	        		
	        		protected void onPostExecute(String t) {
	        			hideLoadingDialog();
	        			super.onPostExecute(t);
	        		};
	        		
					@Override
					protected String onRun() {
						// TODO Auto-generated method stub
						return TrackUtil.zipPack(track);
					}

					@Override
					protected void onResult(String result) {
						// TODO Auto-generated method stub
						trackExported(result);
					}
				}.start();
	        	break;
	
	        /*case R.id.options_item_share:
	        	
	        	break;*/
	        	
	        case R.id.menu_map_type_map:
	        	GlobelConstants.mapType = AMap.MAP_TYPE_NORMAL;
	        	fmMap.getMap().setMapType(AMap.MAP_TYPE_NORMAL);
	        	
	        	item.setChecked(true);
	        	break;
	        	
	        case R.id.menu_map_type_satellite:
	        	GlobelConstants.mapType = AMap.MAP_TYPE_SATELLITE;
	        	fmMap.getMap().setMapType(AMap.MAP_TYPE_SATELLITE);
	        	
	        	item.setChecked(true);
	        	break;
	        	
	        case R.id.menu_map_type_night:
	        	GlobelConstants.mapType = AMap.MAP_TYPE_NIGHT;
	        	fmMap.getMap().setMapType(AMap.MAP_TYPE_NIGHT);
	        	
	        	item.setChecked(true);
	        	break;
	        	
			default:
				break;
		}
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
		if(requestCode == SimpleDialogDeleteTrackRequestCode){
			new CsqBackgroundTask<String>(this) {
        		protected void onPreExecute() {
        			showLoadingDialog(strExportATrackStart);
        		};
        		
        		protected void onPostExecute(String t) {
        			hideLoadingDialog();
        			super.onPostExecute(t);
        		};
        		
				@Override
				protected String onRun() {
					// TODO Auto-generated method stub
					return TrackUtil.zipPack(track);
				}

				@Override
				protected void onResult(String result) {
					// TODO Auto-generated method stub
					trackExportedThenDelete(result);
				}
			}.start();
		}else if(requestCode == TrackCtrlView.requestCodeStopTrack){
			vTrackCtrl.stopTrack();
		}else if(requestCode == SimpleDialogOpenGpsRequestCode){
			startActivity(GpsUtil.getLocationSetingIntent());
		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
		if(requestCode == SimpleDialogDeleteTrackRequestCode){
			TrackDb.getInstance().delete(extra_track_id);
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(lyDrawer.isOpened()){
			lyDrawer.close();
			return;
		}
		super.onBackPressed();
	}
	
	// --------------------- Methods public ----------------------
	
	public static void launch(Context context , long trackId){
		Intent i = new Intent(context, TrackDetailMapActivity.class);
		i.putExtra(EXTRA_TRACK_ID, trackId);
		context.startActivity(i);
	}
	
	public void showHProgressBar() {
		if(hProgressBar != null){
			hProgressBar.setVisibility(View.VISIBLE);
			hProgressBar.progressiveStart();
		}
	}
	
	public void hideHProgressBar() {
		if(hProgressBar != null){
			hProgressBar.setVisibility(View.INVISIBLE);
			hProgressBar.progressiveStop();
		}
	}

	// --------------------- Methods private ---------------------
	
	public void onEventMainThread(EventCurTrackStatusChanged event){
		if(isRecordingTrack){
			if(event.track.getRecordStatus() == RecordStatus.finished.getValue()){
				finish();
				
				if(isRecordingTrack 
						&& TrackDb.getInstance().queryById(extra_track_id) != null){
					//当前轨迹停止且没删除时退出，编辑
					EditTrackActivity.launch(activity, extra_track_id);
				}
			}
		}
	}
	
	public void onEventMainThread(EventNewTrackPoint event){
		if(isRecordingTrack){
			fmMap.updatePaths(TrackPointDb.getInstance()
					.getCurTrackPointsLatLng(crCurTrackLine, path_width));
		}
	}
	
	public void onEventMainThread(EventNewScenery event){
		if(isRecordingTrack){
			fmMap.addScenery(new SceneryCluster(event.scenery));
		}
	}
	
	public void onEventMainThread(EventTrackUpdated event){
		trackLoaded(event.track);
	}
	
	public void onEventMainThread(EventSceneryNumChanged event){
		//异步加载风景
		/* 因为风景改变会导致轨迹改变EventTrackUpdated，会刷新风景数
		 * new CsqBackgroundTask<Void>(this , "pathAndSceneryLoaded") {
			@Override
			protected Void onRun() {
				// TODO Auto-generated method stub
				mSenerys = track.getScenerys();
				track.resetScenerys();
				return null;
			}
		}.start();*/
	}
	
	public void trackExported(String zipPath){
		if(TextUtils.isEmpty(zipPath)){
			ToastUtil.showToastInfo(activity, 
					strExportATrackFailed, 
					Style.ALERT, 
					false);
			
		}else{
			ToastUtil.showToastInfo(activity, 
					strExportATrackSuccess, 
					Style.INFO, 
					false);
			
		}
	}
	
	public void trackExportedThenDelete(String zipPath){
		if(TextUtils.isEmpty(zipPath)){
			ToastUtil.showToastInfo(activity, 
					strExportATrackFailed, 
					Style.ALERT, 
					false);
		}else{
			ToastUtil.showToastInfo(activity, 
					strExportATrackSuccess, 
					Style.INFO, 
					false);
			TrackDb.getInstance().delete(extra_track_id);
			finish();
		}
	}

	
	private void addPauseTipView() {
		String strHelpTipPauseRecord = getResources().getString(R.string.strHelpTipPauseRecord);
		ToolTip toolTip = new ToolTip().withText(strHelpTipPauseRecord)
				.withColor(getResources().getColor(R.color.yellow10))
				.withTextColor(getResources().getColor(R.color.purple13))
				.withAnimationType(ToolTip.AnimationType.FROM_TOP);

		View btnPause = findViewById(R.id.btnStart);
		if(btnPause != null && lyTooltipframe != null){
			mPauseTipView = lyTooltipframe.showToolTipForView(toolTip, btnPause);
			mPauseTipView.setOnToolTipViewClickedListener(new OnToolTipViewClickedListener() {
				@Override
				public void onToolTipViewClicked(ToolTipView toolTipView) {
					// TODO Auto-generated method stub
					mPauseTipView = null;
				}
			});
			
			//6秒后隐藏
			btnPause.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					removeStartTipView();
				}
			}, 6000);
			
			//2秒后显示停止提示
			btnPause.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					addStopTipView();
				}
			}, 2000);
			
		}
	}
	
	private void removeStartTipView() {
		if(mPauseTipView != null){
			mPauseTipView.remove();
			mPauseTipView = null;
		}
	}
	
	private void addStopTipView() {
		String strHelpTipStopRecord = getResources().getString(R.string.strHelpTipStopRecord);
		ToolTip toolTip = new ToolTip().withText(strHelpTipStopRecord)
				.withColor(getResources().getColor(R.color.red10))
				.withTextColor(getResources().getColor(R.color.green))
				.withAnimationType(ToolTip.AnimationType.FROM_TOP);

		View btnStop = findViewById(R.id.btnStop);
		if(btnStop != null && lyTooltipframe != null){
			mStopTipView = lyTooltipframe.showToolTipForView(toolTip, btnStop);
			mStopTipView.setOnToolTipViewClickedListener(new OnToolTipViewClickedListener() {
				@Override
				public void onToolTipViewClicked(ToolTipView toolTipView) {
					// TODO Auto-generated method stub
					mStopTipView = null;
				}
			});
			
			//6秒后隐藏，并显示拍照提示
			btnStop.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					removeStopTipView();
					
					addGetSceneryTipView();
				}
			}, 6000);
			
		}
		
	}
	
	private void removeStopTipView() {
		if(mStopTipView != null){
			mStopTipView.remove();
			mStopTipView = null;
		}
	}
	
	private void addGetSceneryTipView() {
		String strHelpTipGetScenery = getResources().getString(R.string.strHelpTipGetScenery);
		ToolTip toolTip = new ToolTip().withText(strHelpTipGetScenery)
				.withColor(getResources().getColor(R.color.red10))
				.withTextColor(getResources().getColor(R.color.green))
				.withAnimationType(ToolTip.AnimationType.FROM_MASTER_VIEW);

		View vGetScenery = findViewById(R.id.vGetScenery);
		if(vGetScenery != null && lyTooltipframe != null){
			mGetSceneryTipView = lyTooltipframe.showToolTipForView(toolTip, vGetScenery);
			mGetSceneryTipView.setOnToolTipViewClickedListener(new OnToolTipViewClickedListener() {
				@Override
				public void onToolTipViewClicked(ToolTipView toolTipView) {
					// TODO Auto-generated method stub
					mGetSceneryTipView = null;
				}
			});
			
			//6秒后隐藏
			vGetScenery.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					removeGetSceneryTipView();
				}
			}, 6000);
		}
		
	}
	
	private void removeGetSceneryTipView() {
		if(mGetSceneryTipView != null){
			mGetSceneryTipView.remove();
			mGetSceneryTipView = null;
		}
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
