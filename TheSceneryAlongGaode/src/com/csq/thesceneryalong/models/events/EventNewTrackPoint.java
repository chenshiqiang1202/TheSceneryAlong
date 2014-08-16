/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月2日 下午6:33:47   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.events;

import com.csq.thesceneryalong.db.TrackPoint;

public class EventNewTrackPoint {

	public TrackPoint newPoint;

	public EventNewTrackPoint(TrackPoint newPoint) {
		super();
		this.newPoint = newPoint;
	}
	
}
