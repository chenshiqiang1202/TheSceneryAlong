/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月30日 下午5:02:22   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.events;

import com.csq.thesceneryalong.db.Track;

public class EventTrackUpdated {

	/**
	 * 如果为null，可能是更新多条轨迹
	 */
	public final Track track;

	public EventTrackUpdated(Track track) {
		super();
		this.track = track;
	}
	
}
