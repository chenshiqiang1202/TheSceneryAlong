/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午11:43:00   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.dbmodel;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import android.location.Location;

import com.amap.api.maps.model.LatLng;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.models.models.PathConfig;
import com.csq.thesceneryalong.models.models.TrackPointStatus;
import com.csq.thesceneryalong.utils.Dom4jUtil;
import com.csq.thesceneryalong.utils.StringUtils;

public class TrackPointUtil {
	
	public static final String NODE_TRACKPOINT = "TrackPoint";
	public static final String NODE_TRACKPOINT_TIME = "time";
	public static final String NODE_TRACKPOINT_LONGITUDE = "longitude";
	public static final String NODE_TRACKPOINT_LATITUDE = "latitude";
	public static final String NODE_TRACKPOINT_ALTITUDE = "altitude";
	public static final String NODE_TRACKPOINT_ACCURACY = "accuracy";
	public static final String NODE_TRACKPOINT_SPEED = "speed";
	public static final String NODE_TRACKPOINT_BEARING = "bearing";
	public static final String NODE_TRACKPOINT_PROVIDER = "provider";
	public static final String NODE_TRACKPOINT_POINTSTATUS = "pointStatus";
	
	
	public static TrackPoint newNormalPoint(Location curLoc, long trackId){
		TrackPoint p = new TrackPoint();
		p.setAccuracy(curLoc.getAccuracy());
		p.setAltitude(curLoc.getAltitude());
		p.setBearing(curLoc.getBearing());
		p.setLatitude(curLoc.getLatitude());
		p.setLongitude(curLoc.getLongitude());
		p.setPointStatus(TrackPointStatus.normal.getValue()); //轨迹点状态，0正常记录，1暂停，2恢复
		p.setProvider(curLoc.getProvider());
		p.setSpeed(curLoc.getSpeed());
		p.setTime(System.currentTimeMillis());
		p.setTrackId(trackId);
		return p;
	}

	public static TrackPoint newPausedPoint(long trackId){
		Location curLoc = MyLocationManager.getInstance().getCurrentLocation();
		TrackPoint p = new TrackPoint();
		if(curLoc != null){
			p.setAccuracy(curLoc.getAccuracy());
			p.setAltitude(curLoc.getAltitude());
			p.setBearing(curLoc.getBearing());
			p.setLatitude(curLoc.getLatitude());
			p.setLongitude(curLoc.getLongitude());
			p.setProvider(curLoc.getProvider());
			p.setSpeed(curLoc.getSpeed());
			
		} else {
			p.setAccuracy(0f);
			p.setAltitude(0d);
			p.setBearing(0f);
			p.setLatitude(0d);
			p.setLongitude(0d);
			p.setProvider("");
			p.setSpeed(0f);
		}
		p.setPointStatus(TrackPointStatus.paused.getValue()); //轨迹点状态，0正常记录，1暂停，2恢复
		p.setTime(System.currentTimeMillis());
		p.setTrackId(trackId);
		return p;
	}
	
	public static TrackPoint newResumedPoint(long trackId){
		Location curLoc = MyLocationManager.getInstance().getCurrentLocation();
		TrackPoint p = new TrackPoint();
		if(curLoc != null){
			p.setAccuracy(curLoc.getAccuracy());
			p.setAltitude(curLoc.getAltitude());
			p.setBearing(curLoc.getBearing());
			p.setLatitude(curLoc.getLatitude());
			p.setLongitude(curLoc.getLongitude());
			p.setProvider(curLoc.getProvider());
			p.setSpeed(curLoc.getSpeed());
			
		} else {
			p.setAccuracy(0f);
			p.setAltitude(0d);
			p.setBearing(0f);
			p.setLatitude(0d);
			p.setLongitude(0d);
			p.setProvider("");
			p.setSpeed(0f);
		}
		p.setPointStatus(TrackPointStatus.resumed.getValue()); //轨迹点状态，0正常记录，1暂停，2恢复
		p.setTime(System.currentTimeMillis());
		p.setTrackId(trackId);
		return p;
	}
	
	
	/**
	 * @description: 获得记录中的线路
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param tps
	 * @param color
	 * @param width
	 * @return
	 */
	public static List<PathConfig> getTrackPointsLatLng(List<TrackPoint> tps, int color, int width) {
		List<PathConfig> all = new ArrayList<PathConfig>();
		if(tps != null && !tps.isEmpty()){
			PathConfig config = null;
			for(TrackPoint tp : tps){
				if(tp.getPointStatus() == TrackPointStatus.normal.getValue()){
					//需要记录的点
					if(config == null){
						config = new PathConfig(new ArrayList<LatLng>(), width, color);
					}
					config.points.add(new LatLng(tp.getLatitude(), tp.getLongitude()));
				}else if(tp.getPointStatus() == TrackPointStatus.paused.getValue()){
					//需要暂停的点
					if(config != null && config.points.size() > 1){
						//点数必须大于1，不然报错
						all.add(config);
					}
					config = null;
				}
			}
			
			//没有中断过，也要添加到all
			if(config != null && config.points.size() > 1){
				//点数必须大于1，不然报错
				all.add(config);
			}
			config = null;
		}
		return all;
	}
	
	public static LatLng getTrackPointLatLng(TrackPoint tp) {
		if(tp != null){
			return new LatLng(tp.getLatitude(), tp.getLongitude());
		}
		return null;
	}
	
	
	/**
	 * @description: 创建xml文件，并添加到轨迹trackPoints节点下面
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param trackPoints
	 */
	public static void createXml(Element trackPoints, TrackPoint point){
		Element trackPointElement = trackPoints.addElement(NODE_TRACKPOINT);
		
        Element time = trackPointElement.addElement(NODE_TRACKPOINT_TIME);
        time.setText(StringUtils.avoidNull(point.getTime(), ""));
        
        Element longitude = trackPointElement.addElement(NODE_TRACKPOINT_LONGITUDE);
        longitude.setText(StringUtils.avoidNull(point.getLongitude(), ""));
        
        Element latitude = trackPointElement.addElement(NODE_TRACKPOINT_LATITUDE);
        latitude.setText(StringUtils.avoidNull(point.getLatitude(), ""));
        
        Element altitude = trackPointElement.addElement(NODE_TRACKPOINT_ALTITUDE);
        altitude.setText(StringUtils.avoidNull(point.getAltitude(), ""));
        
        Element accuracy = trackPointElement.addElement(NODE_TRACKPOINT_ACCURACY);
        accuracy.setText(StringUtils.avoidNull(point.getAccuracy(), ""));
        
        Element speed = trackPointElement.addElement(NODE_TRACKPOINT_SPEED);
        speed.setText(StringUtils.avoidNull(point.getSpeed(), ""));
        
        Element bearing = trackPointElement.addElement(NODE_TRACKPOINT_BEARING);
        bearing.setText(StringUtils.avoidNull(point.getBearing(), ""));
        
        Element provider = trackPointElement.addElement(NODE_TRACKPOINT_PROVIDER);
        provider.setText(StringUtils.avoidNull(point.getProvider(), ""));
        
        Element pointStatus = trackPointElement.addElement(NODE_TRACKPOINT_POINTSTATUS);
        pointStatus.setText(StringUtils.avoidNull(point.getPointStatus(), ""));
	}
	
	public static List<TrackPoint> parseXml(Element trackPointsElement){
		List<TrackPoint> tps = new ArrayList<TrackPoint>();
		if(trackPointsElement != null){
			List<Element> tpsEs = trackPointsElement.elements(NODE_TRACKPOINT);
			if(tpsEs != null && !tpsEs.isEmpty()){
				TrackPoint tp = null;
				for(Element item : tpsEs){
					tp = new TrackPoint();
					
					tp.setTime(Dom4jUtil.parseLong(item, 
							NODE_TRACKPOINT_TIME, 
							0));
					
					tp.setLongitude(Dom4jUtil.parseDouble(item, 
							NODE_TRACKPOINT_LONGITUDE, 
							0));
					
					tp.setLatitude(Dom4jUtil.parseDouble(item, 
							NODE_TRACKPOINT_LATITUDE, 
							0));
					
					tp.setAltitude(Dom4jUtil.parseDouble(item, 
							NODE_TRACKPOINT_ALTITUDE, 
							0));
					
					tp.setAccuracy(Dom4jUtil.parseFloat(item, 
							NODE_TRACKPOINT_ACCURACY, 
							0));
					
					tp.setSpeed(Dom4jUtil.parseFloat(item, 
							NODE_TRACKPOINT_SPEED, 
							0));
					
					tp.setBearing(Dom4jUtil.parseFloat(item, 
							NODE_TRACKPOINT_BEARING, 
							0));
					
					tp.setProvider(Dom4jUtil.parseString(item, 
							NODE_TRACKPOINT_PROVIDER, 
							""));
					
					tp.setPointStatus(Dom4jUtil.parseInterger(item, 
							NODE_TRACKPOINT_POINTSTATUS, 
							0));
					
					tps.add(tp);
				}
			}
		}
		return tps;
	}
	
}
