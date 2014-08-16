/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月29日 下午10:14:20   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.views;

import java.util.List;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.io.db.SceneryDb;
import com.csq.thesceneryalong.io.db.TrackPointDb;
import com.csq.thesceneryalong.models.events.EventReverseGeocodingResult;
import com.csq.thesceneryalong.models.models.PathConfig;
import com.csq.thesceneryalong.models.models.SceneryCluster;
import com.csq.thesceneryalong.ui.fragments.base.BaseMapFragment;
import com.csq.thesceneryalong.utils.MapUtil;
import com.csq.thesceneryalong.utils.StringUtils;
import com.csq.thesceneryalong.utils.dbmodel.TrackPointUtil;
import com.csq.thesceneryalong.utils.location.ReverseGeocodingUtil;
import com.csq.thesceneryalong.utils.tasks.CsqBackgroundTask;

import de.greenrobot.event.EventBus;

public class SceneryDetailDrawerView extends LinearLayout {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected View lyDescription;
	protected TextView tvDescription; 
	protected View lyAddress;
	protected TextView tvAddress; 
	
	protected BaseMapFragment fmMap;
	protected int crFinishedTrackLine;
	protected int path_width;
	
	private List<Scenery> scenerys;
	private Scenery curScenery;

	// ----------------------- Constructors ----------------------
	
	public SceneryDetailDrawerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public SceneryDetailDrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	private void initView(Context context){
		View v = LayoutInflater.from(context).inflate(R.layout.view_scenery_detail_drawer, 
				this, 
				true);
		
		lyDescription = v.findViewById(R.id.lyDescription);
		tvDescription = (TextView) v.findViewById(R.id.tvDescription);
		lyAddress = v.findViewById(R.id.lyAddress);
		tvAddress = (TextView) v.findViewById(R.id.tvAddress);
		
		fmMap = (BaseMapFragment) ((FragmentActivity)context)
					.getSupportFragmentManager().findFragmentById(R.id.fmMap);
		crFinishedTrackLine = getResources().getColor(R.color.crFinishedTrackLine);
		path_width = getResources().getDimensionPixelSize(R.dimen.path_width);
		
		fmMap.getMap().getUiSettings().setMyLocationButtonEnabled(false);
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
	/**
	 * @description: 更新数据
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void updateTrack(final long trackId){
		
		//轨迹
		new CsqBackgroundTask<List<TrackPoint>>(SceneryDetailDrawerView.this) {
			@Override
			protected List<TrackPoint> onRun() {
				// TODO Auto-generated method stub
				return TrackPointDb.getInstance().query(trackId);
			}

			@Override
			protected void onResult(List<TrackPoint> result) {
				// TODO Auto-generated method stub
				trackPointsLoaded(result);
			}
		}.execute();
		
	}

	// --------------------- Methods private ---------------------
	
	public void trackPointsLoaded(List<TrackPoint> tps){
		if(tps != null){
			List<PathConfig> pcs = TrackPointUtil
					.getTrackPointsLatLng(tps, crFinishedTrackLine, path_width);
			fmMap.updatePaths(pcs);
			
			MapUtil.centerTrackPoints(fmMap.getMap(), tps);
		}
	}
	
	public void updateScenerys(List<Scenery> scenerys, int index){
		this.scenerys = scenerys;
		updateIndex(index);
	}
	
	public void updateIndex(int index){
		if(scenerys == null || scenerys.size() < 1){
			return;
		}
		
		if(index < 0){
			index = 0;
		}
		
		if(index >= scenerys.size()){
			index = scenerys.size() - 1;
		}
		
		updateScenery(scenerys.get(index));
	}
	
	public void updateScenery(Scenery scenery){
		fmMap.clearScenerys();
		curScenery = scenery;
		fmMap.addScenery(new SceneryCluster(curScenery));
		MapUtil.moveTo(fmMap.getMap(), 
				new LatLng(curScenery.getLatitude(), curScenery.getLongitude()), 
				true);
		
		String addr = StringUtils.avoidNull(curScenery.getAddress(), "");
		if(TextUtils.isEmpty(addr)){
			lyAddress.setVisibility(View.GONE);
			
			ReverseGeocodingUtil.reverseGeocode(
					new LatLng(curScenery.getLatitude(), curScenery.getLongitude()), curScenery);
			
		}else{
			lyAddress.setVisibility(View.VISIBLE);
			tvAddress.setText(addr);
		}
		
		String description = StringUtils.avoidNull(curScenery.getDescription(), "");
		if(TextUtils.isEmpty(description)){
			lyDescription.setVisibility(View.GONE);
			
		}else{
			lyDescription.setVisibility(View.VISIBLE);
			tvDescription.setText(description);
		}
	}
	
	public void onEventMainThread(EventReverseGeocodingResult event){
		if(!TextUtils.isEmpty(event.address)){
			if(event.model instanceof Scenery){
				Scenery u = (Scenery)event.model;
				if(curScenery != null && u.getId() == curScenery.getId()){
					//获得地址，显示
					lyAddress.setVisibility(View.VISIBLE);
					tvAddress.setText(event.address);
				}
				//更新风景地址
				u.setAddress(event.address);
				SceneryDb.getInstance().update(u);
			}
		}
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
