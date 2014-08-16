/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月28日 下午10:50:47   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.fragments.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.LocationSource.OnLocationChangedListener;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.csq.thesceneryalong.constant.GlobelConstants;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.logic.manager.MyLocationManager.LocationCallback;
import com.csq.thesceneryalong.models.models.PathConfig;
import com.csq.thesceneryalong.models.models.SceneryCluster;
import com.csq.thesceneryalong.models.models.cluster_renderer.SceneryRenderer;
import com.csq.thesceneryalong.utils.MapUtil;
import com.gaode.maps.android.clustering.ClusterManager;
import com.gaode.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.gaode.maps.android.clustering.ClusterManager.OnClusterInfoWindowClickListener;
import com.gaode.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.gaode.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;

public class BaseMapFragment extends SupportMapFragment {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	private Activity activity;
	private ClusterManager<SceneryCluster> mSceneryClusterManager;
	
	private OnLocationChangedListener mLocation;
	private LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void locationChanged(Location newLoc) {
			// TODO Auto-generated method stub
			if(mLocation != null){
				mLocation.onLocationChanged(newLoc);
			}
		}
	};
	
	/**
	 * 我的位置模式监听
	 */
	private MyLocationTypeListener mLocationTypeListener;
	/**
	 * 是否能改变我的位置模式
	 */
	private boolean canChangeMyLocationType = false;
	/**
	 * 我的位置模式
	 */
	private int mLocationType = 0;
	
	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = super.onCreateView(inflater, container, savedInstanceState);
		
		mSceneryClusterManager = new ClusterManager<SceneryCluster>(activity, getMap());
		mSceneryClusterManager.setRenderer(new SceneryRenderer(activity, 
				getMap(), 
				mSceneryClusterManager));
		
		AMap map = getMap();
		map.setOnCameraChangeListener(mSceneryClusterManager);
		map.setOnMarkerClickListener(mSceneryClusterManager);
		map.setOnInfoWindowClickListener(mSceneryClusterManager);
		map.setTrafficEnabled(false);
        //默认先移动到北京
        MapUtil.moveTo(getMap(), new LatLng(39.908683,116.408386), 16, false);
        
        UiSettings setting = getMap().getUiSettings();
        //指南针
        setting.setCompassEnabled(true);
        //比例尺
        setting.setScaleControlsEnabled(true);
        //缩放按钮
        setting.setZoomControlsEnabled(true);
        //我的位置
        setting.setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        getMap().setMyLocationEnabled(true);
        //默认定位模式
        setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		getMap().setLocationSource(new LocationSource() {
			@Override
			public void deactivate() {
				// TODO Auto-generated method stub
				mLocation = null;
			}
			
			@Override
			public void activate(OnLocationChangedListener arg0) {
				// TODO Auto-generated method stub
				mLocation = arg0;
				
				Location latest = MyLocationManager.getInstance().getLatestKnowLocation();
				if(latest != null && mLocation != null){
					mLocation.onLocationChanged(latest);
					
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							MapUtil.moveToCurLocation(getMap());
						}
					});
				}
			}
		});
		getMap().setMapType(GlobelConstants.mapType);
		MyLocationManager.getInstance().registLocationCallback(locationCallback);
		MyLocationManager.getInstance().startListenLocation();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		clearScenerys();
		activity = null;
		mSceneryClusterManager = null;
		
		getMap().setMyLocationEnabled(false);
		MyLocationManager.getInstance().unRegistLocationCallback(locationCallback);
		MyLocationManager.getInstance().stopListenLocation();
		
		releaseResources();
	}
	
	
	// --------------------- Methods public ----------------------
	
	// -------------------------- 风景 ----------------------------
	/**
	 * @description: 添加一个风景
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenery
	 */
	public synchronized void addScenery(final SceneryCluster scenery){
		if(scenery == null){
			return;
		}
		
		mSceneryClusterManager.addItem(scenery);
		mSceneryClusterManager.cluster();
	}
	
	/**
	 * @description: 添加多个风景
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void addScenerys(final Collection<SceneryCluster> scenerys){
		if(scenerys == null || scenerys.isEmpty()){
			return;
		}

		mSceneryClusterManager.addItems(scenerys);
		mSceneryClusterManager.cluster();
	}
	
	/**
	 * 清空并设置显示的风景
	 * @description: 
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenerys
	 */
	public synchronized void setScenerys(Collection<SceneryCluster> scenerys){
		mSceneryClusterManager.clearItems();
		mSceneryClusterManager.cluster();
		addScenerys(scenerys);
	}
	
	/**
	 * @description: 移除一个风景
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenery
	 */
	public synchronized void removeScenery(SceneryCluster scenery){
		if(scenery == null){
			return;
		}
		mSceneryClusterManager.removeItem(scenery);
		mSceneryClusterManager.cluster();
	}
	
	/**
	 * @description: 清空风景
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void clearScenerys(){
		mSceneryClusterManager.clearItems();
		mSceneryClusterManager.cluster();
	}
	
	
	// -------------------------- 轨迹 ----------------------------
	//轨迹缓存
	private List<Polyline> polylineCaches = new ArrayList<Polyline>();
	
	/**
	 * @description: 更新轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param points
	 */
	public void updatePaths(List<PathConfig> points){
		int newNum = points.size();
		int oldNum = polylineCaches.size();
		int num = Math.max(newNum, oldNum);
		
		Polyline pl = null;
		PathConfig pc = null;
		for(int i = 0 ; i < num ; i++){
			
			if(i < oldNum && i < newNum){
				//有缓存，可以直接取并设置显示
				pc = points.get(i);
				
				pl = polylineCaches.get(i);
				pl.setPoints(pc.points);
				pl.setColor(pc.lineColor);
				pl.setWidth(pc.lineWidth);
				pl.setVisible(true);
				
			}else if(newNum > oldNum){
				pc = points.get(i);
				
				//需新建
				addAPath(pc);
				
			}else if(newNum < oldNum){
				//需隐藏
				pl = polylineCaches.get(i);
				removeAPath(pl);
			}
		}
		
	}
	
	/**
	 * @description: 添加一段轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public Polyline addAPath(PathConfig path){
		Polyline l = getMap().addPolyline(new PolylineOptions()
						.width(path.lineWidth)
						.color(path.lineColor)
						.visible(true)
						.geodesic(true)
						.addAll(path.points));
		polylineCaches.add(l);
		return l;
	}
	
	/**
	 * @description: 移除一段轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param path
	 */
	public void removeAPath(Polyline path){
		path.getPoints().clear();
		path.setVisible(false);
	}
	
	public void setMyLocationTypeListener(
			MyLocationTypeListener mLocationTypeListener) {
		this.mLocationTypeListener = mLocationTypeListener;
	}
	
	public void setCanChangeMyLocationType(boolean canChangeMyLocationType) {
		this.canChangeMyLocationType = canChangeMyLocationType;
	}
	
	public void changeMyLocationType(){
		if(canChangeMyLocationType){
			if(mLocationType == AMap.LOCATION_TYPE_LOCATE){
				setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
				
			}else if(mLocationType == AMap.LOCATION_TYPE_MAP_FOLLOW){
				setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
				
			}else{
				setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
			}
		}
	}
	
	
	// -------------------------- 定位 ----------------------------
	
	// --------------------- Methods private ---------------------
	
	/**
	 * @description: 界面退出时释放占用内存特别多的资源
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	protected void releaseResources(){
		
	}
	
	
	private void setMyLocationType(int type){
		mLocationType = type;
		getMap().setMyLocationType(mLocationType);
		
		if(mLocationTypeListener != null){
			mLocationTypeListener.myLocationTypeChanged(mLocationType);
		}
	}

	// --------------------- Getter & Setter ---------------------
	
	public void setOnSceneryClusterItemClickListener(OnClusterItemClickListener<SceneryCluster> listener) {
		mSceneryClusterManager.setOnClusterItemClickListener(listener);
	}
	
	public void setOnClusterItemInfoWindowClickListener(OnClusterItemInfoWindowClickListener<SceneryCluster> listener) {
		mSceneryClusterManager.setOnClusterItemInfoWindowClickListener(listener);
	}
	
	public void setOnClusterClickListener(OnClusterClickListener<SceneryCluster> listener) {
		mSceneryClusterManager.setOnClusterClickListener(listener);
	}
	
	public void setOnClusterInfoWindowClickListener(OnClusterInfoWindowClickListener<SceneryCluster> listener) {
		mSceneryClusterManager.setOnClusterInfoWindowClickListener(listener);
	}

	// --------------- Inner and Anonymous Classes ---------------
	
	public static interface MyLocationTypeListener{
		public void myLocationTypeChanged(int type);
	}
}
