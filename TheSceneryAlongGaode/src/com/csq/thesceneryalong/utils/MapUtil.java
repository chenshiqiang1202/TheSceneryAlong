/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 上午12:09:47
 * @version 1.0
 */
package com.csq.thesceneryalong.utils;

import java.util.List;

import android.location.Location;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.models.models.TrackPointStatus;
import com.csq.thesceneryalong.utils.location.LocationUtil;

public class MapUtil {
	
	/**
	 * @description: 地图移动到另一点
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param map
	 * @param loc
	 */
	public static void moveTo(AMap map, LatLng loc, boolean animate){
		float zoom = map.getCameraPosition().zoom;
		moveTo(map, loc, zoom, animate);
	}
	
	
	/**
	 * 
	 * @description: 地图移动到另一点
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param map
	 * @param loc
	 * @param zoom 2.0-21.0
	 * @param animate
	 */
	public static void moveTo(AMap map, LatLng loc, float zoom, boolean animate){
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(loc, zoom);
		if(animate){
			map.animateCamera(cu);
		}else{
			map.moveCamera(cu);
		}
	}
	
	/**
	 * @description: 移动到当前位置
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param map
	 * @return
	 */
	public static boolean moveToCurLocation(AMap map){
		Location loc = MyLocationManager.getInstance().getLatestKnowLocation();
		if(loc != null){
			moveTo(map, LocationUtil.getLatLon(loc), true);
			return true;
		}
		return false;
	}

	public static void centerPoints(AMap map, List<LatLng> points){
		if(points == null || points.size() < 2){
			return;
		}
		
		LatLng first = points.get(0);
		double minLat = first.latitude;
		double maxLat = first.latitude;
		double minLon = first.longitude;
		double maxLon = first.longitude;
		for(int i = 1, num = points.size() ; i < num ; i++){
			LatLng t = points.get(i);
			if(t.latitude > maxLat){
				maxLat = t.latitude;
			}
			if(t.latitude < minLat){
				minLat = t.latitude;
			}
			if(t.longitude > maxLon){
				maxLon = t.longitude;
			}
			if(t.longitude < minLon){
				minLon = t.longitude;
			}
		}
				
		LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
	}
	
	public static void centerTrackPoints(AMap map, List<TrackPoint> points){
		if(points == null || points.size() < 2){
			return;
		}
		
		TrackPoint first = points.get(0);
		double minLat = first.getLatitude();
		double maxLat = first.getLatitude();
		double minLon = first.getLongitude();
		double maxLon = first.getLongitude();
		for(int i = 1, num = points.size() ; i < num ; i++){
			TrackPoint t = points.get(i);
			if(t.getPointStatus() == TrackPointStatus.normal.getValue()){
				if(t.getLatitude() > maxLat){
					maxLat = t.getLatitude();
				}
				if(t.getLatitude() < minLat){
					minLat = t.getLatitude();
				}
				if(t.getLongitude() > maxLon){
					maxLon = t.getLongitude();
				}
				if(t.getLongitude() < minLon){
					minLon = t.getLongitude();
				}
			}
		}
				
		LatLngBounds bounds = new LatLngBounds(new LatLng(minLat, minLon), new LatLng(maxLat, maxLon));
		map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 5));
	}
}
