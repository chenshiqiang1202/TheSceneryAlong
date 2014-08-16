/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月30日 下午5:08:25   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.events;

import com.csq.thesceneryalong.db.Track;

public class EventCurTrackStatusChanged {

	public final Track track;

	public EventCurTrackStatusChanged(Track track) {
		super();
		this.track = track;
	}
}
