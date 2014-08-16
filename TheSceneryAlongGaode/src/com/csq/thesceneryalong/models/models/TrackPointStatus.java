/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 */
package com.csq.thesceneryalong.models.models;
public enum TrackPointStatus {
	
	normal(0),
	paused(1),
	resumed(2);
	
	private int value;
	
	private TrackPointStatus(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static TrackPointStatus create(int status){
		if(status == TrackPointStatus.normal.getValue()){
			//正常轨迹点
			return TrackPointStatus.normal;
		}else if(status == TrackPointStatus.paused.getValue()){
			//暂停轨迹时的轨迹点
			return TrackPointStatus.paused;
		}else{
			//恢复轨迹时的轨迹点
			return TrackPointStatus.resumed;
		}
	}
}
