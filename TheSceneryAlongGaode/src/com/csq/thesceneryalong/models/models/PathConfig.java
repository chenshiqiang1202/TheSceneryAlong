/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月8日 下午7:51:32   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.models;

import java.util.List;

import com.amap.api.maps.model.LatLng;

public class PathConfig {
	public List<LatLng> points;
	public int lineWidth;
	public int lineColor;
	
	public PathConfig(List<LatLng> points, int lineWidth, int lineColor) {
		super();
		this.points = points;
		this.lineWidth = lineWidth;
		this.lineColor = lineColor;
	}
}
