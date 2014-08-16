/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月27日 下午7:49:34   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.events;

import com.amap.api.maps.model.LatLng;

public class EventReverseGeocodingResult {

	public LatLng location;
	public String address;
	public Object model;
	
	public EventReverseGeocodingResult(LatLng location, String address, Object model) {
		super();
		this.location = location;
		this.address = address;
		this.model = model;
	}
}
