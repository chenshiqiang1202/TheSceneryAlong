/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月19日 上午12:30:48   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.events;

import com.csq.thesceneryalong.db.Track;

public class EventTrackImport {

	public int curIndex = 0;
	public int totalSize = 0;
	public Track track = null;
	
	public EventTrackImport(int curIndex, int totalSize, Track track) {
		super();
		this.curIndex = curIndex;
		this.totalSize = totalSize;
		this.track = track;
	}
	
}
