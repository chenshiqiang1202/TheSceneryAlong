/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月30日 下午4:50:14   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.models;
public enum RecordStatus {
	
	finished(0),
	recording(1),
	paused(2);
	
	private int value;
	
	private RecordStatus(int value){
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static RecordStatus create(int status){
		if(status == RecordStatus.recording.getValue()){
			//开始记录
			return RecordStatus.recording;
		}else if(status == RecordStatus.paused.getValue()){
			//暂停记录
			return RecordStatus.paused;
		}else{
			//停止记录
			return RecordStatus.finished;
		}
	}
}
