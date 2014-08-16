/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月29日 下午9:04:18   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.location;

import android.location.Location;

import com.amap.api.maps.model.LatLng;
import com.csq.thesceneryalong.config.Configer;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.models.models.MapType;
import com.csq.thesceneryalong.utils.coords.Converter;
import com.csq.thesceneryalong.utils.coords.GpsCorrect;

public class LocationUtil {
	
	public static Converter converter = new Converter();
	
	public static LatLng getLatLon(Location loc){
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}

	private static final double EARTH_RADIUS = 6378137.0;
	/**
	 * 不通过系统Location.distanceBetween计算2点距离
	 * 系统Location.distanceBetween方法在有些手机上同一数据，不同时刻计算的距离不同，奇怪！！！
	 * @return 两点间的距离，单位米
	 */
	private static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
	       double radLat1 = (lat_a * Math.PI / 180.0);
	       double radLat2 = (lat_b * Math.PI / 180.0);
	       double a = radLat1 - radLat2;
	       double b = (lng_a - lng_b) * Math.PI / 180.0;
	       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
	              + Math.cos(radLat1) * Math.cos(radLat2)
	              * Math.pow(Math.sin(b / 2), 2)));
	       s = s * EARTH_RADIUS;
	       s = Math.round(s * 10000) / 10000;
	       return s;
	}
	
	/**
	 * @description: 获得2点之间的距离
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param start
	 * @param end
	 * @return
	 */
	public static double getDistance(Location start, Location end){
		if(start == null || end == null){
			return 0;
		}
		
		return gps2m(start.getLatitude(),
				start.getLongitude(),
				end.getLatitude(),
				end.getLongitude());
	}
	
	/**
	 * @description: 获得2点之间的距离
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param start
	 * @param end
	 * @return
	 */
	public static double getDistance(TrackPoint start, TrackPoint end){
		return gps2m(start.getLatitude(),
				start.getLongitude(),
				end.getLatitude(),
				end.getLongitude());
	}
	
	/**
	 * @description: google地图纠偏，改变原始对象
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param wgs
	 * @return
	 */
	/*public static Location wgsToGcj(Location wgs){
		if(!outOfChina(wgs.getLatitude(), wgs.getLongitude())){
			//在中国境内才转换，不然返回原始gps坐标
			CoordsPoint gcj = converter.getEncryCoordsPoint(wgs.getLongitude(), wgs.getLatitude());
			wgs.setLatitude(gcj.getY());
			wgs.setLongitude(gcj.getX());
		}
		return wgs;
	}*/
	public static Location wgsToGcj(Location wgs, boolean newObject){
		if(Configer.mapType == MapType.GoogleMap){
			//google地图，验证需要将wgs转换为gcj
			double[] gcj = new double[2];
			GpsCorrect.transform(wgs.getLatitude(), wgs.getLongitude(), gcj);
			
			Location ret = null;
			if(newObject){
				ret = new Location(wgs);
			}else{
				ret = wgs;
			}
			
			ret.setLatitude(gcj[0]);
			ret.setLongitude(gcj[1]);
			
			return ret;
		}else if(Configer.mapType == MapType.AMap){
			//高德地图，返回的坐标应该直接是gcj的，不用转换
			return wgs;
		}
		
		return wgs;
	}
	
	public static boolean outOfChina(double lat, double lon) {
		if (lon < 72.004 || lon > 137.8347)
			return true;
		if (lat < 0.8293 || lat > 55.8271)
			return true;
		return false;
	}
	
}
