/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午3:15:21   
 * @version 1.0   
 */
package com.csq.thesceneryalong.logic.manager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.io.db.SceneryDb;
import com.csq.thesceneryalong.io.db.TrackDb;
import com.csq.thesceneryalong.io.db.TrackPointDb;
import com.csq.thesceneryalong.models.events.EventCurTrackStatusChanged;
import com.csq.thesceneryalong.models.events.EventRecordTimeChanged;
import com.csq.thesceneryalong.models.events.EventTrackStopedAndDelete;
import com.csq.thesceneryalong.models.models.RecordStatus;
import com.csq.thesceneryalong.models.models.TrackPointStatus;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;
import com.csq.thesceneryalong.utils.tasks.CsqBackgroundTask;

import de.greenrobot.event.EventBus;

public class TrackManager {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private static volatile TrackManager instance;
	public static TrackManager getInstance(){
		if(instance == null){
			synchronized (TrackManager.class) {
				instance = new TrackManager();
			}
		}
		return instance;
	}
	
	/**
	 * 当前记录的轨迹
	 */
	private volatile Track curTrack;
	
	private Timer timer = null;
	
	// ----------------------- Constructors ----------------------
	
	private TrackManager(){
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------
	
	/**
	 * @description: 如果有正在记录的轨迹，恢复记录
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void resumeIfHaveRecordingTrack(){
		curTrack = TrackDb.getInstance().queryUnFinished();
		
		if(curTrack != null){
			//开始轨迹点缓存
			TrackPointDb.getInstance().trackRecordStartOrResume(curTrack);
			//开始风景点缓存
			SceneryDb.getInstance().trackRecordStartOrResume(curTrack);
			//如果正在记录轨迹点，前台服务
			if(curTrack.getRecordStatus() == RecordStatus.recording.getValue()){
				TrackPoint lastPoint = TrackPointDb.getInstance().getLastRecordedPoint();
				if(lastPoint != null 
						&& lastPoint.getPointStatus() == TrackPointStatus.normal.getValue()
						&& System.currentTimeMillis() - lastPoint.getTime() > 30 * 60 * 1000)
				{
					//如果距离上一次正常点间隔大于30分钟，就当作另一段轨迹,添加一个
					TrackPointDb.getInstance().addTrackPointToCurTrack(TrackPointStatus.paused);
					TrackPointDb.getInstance().addTrackPointToCurTrack(TrackPointStatus.resumed);
				}
				App.app.startForeground();
				
				startCountTime();
			}
			//通知状态
			EventBus.getDefault().post(new EventCurTrackStatusChanged(curTrack));
		}
	}
	
	/**
	 * @description: 开始记录轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void startTrack(){
		TrackDb.getInstance().stopAllTracks();
		
		Track newTrack = TrackUtil.newRecordingTrack();
		curTrack = TrackDb.getInstance().queryById(TrackDb.getInstance().add(newTrack));
		if(curTrack != null){
			//开始轨迹点缓存
			TrackPointDb.getInstance().trackRecordStartOrResume(curTrack);
			//开始风景点缓存
			SceneryDb.getInstance().trackRecordStartOrResume(curTrack);
			//前台服务
			App.app.startForeground();
			//通知状态
			EventBus.getDefault().post(new EventCurTrackStatusChanged(curTrack));
			
			//只添加轨迹开始后位置变动的位置
			/*TrackPointDb.getInstance().addTrackPointToCurTrack(TrackPointStatus.normal);*/
			
			startCountTime();
		}
	}
	public void startTrackAsyc(){
		new CsqBackgroundTask<Boolean>(){
			@Override
			protected Boolean onRun() {
				// TODO Auto-generated method stub
				startTrack();
				return true;
			}

			@Override
			protected void onResult(Boolean result) {
			}
		}.start();
	}
	
	/**
	 * @description: 暂停记录轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void pauseTrack(){
		if(curTrack == null){
			return;
		}
		//更新状态
		curTrack.setRecordStatus(RecordStatus.paused.getValue());
		//更新数据库
		TrackDb.getInstance().update(curTrack, false);
		//前台服务
		App.app.stopForeground();
		//添加一个暂停轨迹点
		TrackPointDb.getInstance().addTrackPointToCurTrack(TrackPointStatus.paused);
		//通知状态
		EventBus.getDefault().post(new EventCurTrackStatusChanged(curTrack));
		
		stopCountTime();
	}
	public void pauseTrackAsyc(){
		new CsqBackgroundTask<Boolean>(){
			@Override
			protected Boolean onRun() {
				// TODO Auto-generated method stub
				pauseTrack();
				return true;
			}
			
			@Override
			protected void onResult(Boolean result) {
			}
		}.start();
	}
	
	/**
	 * @description: 恢复记录轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void resumeTrack(){
		if(curTrack == null){
			return;
		}
		//更新状态
		curTrack.setRecordStatus(RecordStatus.recording.getValue());
		//更新数据库
		TrackDb.getInstance().update(curTrack, false);
		//前台服务
		App.app.startForeground();
		//添加一个恢复轨迹点
		TrackPointDb.getInstance().addTrackPointToCurTrack(TrackPointStatus.resumed);
		//通知状态
		EventBus.getDefault().post(new EventCurTrackStatusChanged(curTrack));
		
		startCountTime();
	}
	public void resumeTrackAsyc(){
		new CsqBackgroundTask<Boolean>(){
			@Override
			protected Boolean onRun() {
				// TODO Auto-generated method stub
				resumeTrack();
				return true;
			}
			
			@Override
			protected void onResult(Boolean result) {
			}
		}.start();
	}
	
	/**
	 * @description: 停止记录轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void stopTrack(){
		if(curTrack == null){
			return;
		}
		if(curTrack.getSceneryNum() < 1 && curTrack.getMovingDistance() < 100){
			//没有记录到风景，且移动距离小于100，直接删除
			TrackDb.getInstance().delete(curTrack.getId());
			curTrack.setRecordStatus(RecordStatus.finished.getValue());
			EventBus.getDefault().post(new EventTrackStopedAndDelete());
		}else{
			//更新数据库
			TrackDb.getInstance().stopAllTracks();
		}
		//前台服务
		App.app.stopForeground();
		//停止轨迹点缓存
		TrackPointDb.getInstance().trackStoped();
		SceneryDb.getInstance().trackStoped();
		//通知状态
		EventBus.getDefault().post(new EventCurTrackStatusChanged(curTrack));
		
		curTrack = null;
		
		stopCountTime();
	}
	public void stopTrackAsyc(){
		new CsqBackgroundTask<Boolean>(){
			@Override
			protected Boolean onRun() {
				// TODO Auto-generated method stub
				stopTrack();
				return true;
			}
			
			@Override
			protected void onResult(Boolean result) {
			}
		}.start();
	}
	
	/**
	 * @description: 是否有正在记录的轨迹，轨迹可能是暂停的
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public synchronized boolean isHaveRecordingTrack(){
		return curTrack != null;
	}
	
	/**
	 * @description: 是否有正在记录的轨迹，且轨迹不能是暂停的
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public synchronized boolean isTrackRecording(){
		return curTrack != null 
				&& curTrack.getRecordStatus() == RecordStatus.recording.getValue();
	}
	
	/**
	 * @description: 是否是正在记录的轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param trackId
	 * @return
	 */
	public synchronized boolean isTrackRecording(long trackId){
		if(curTrack != null && curTrack.getId() == trackId){
			return true;
		}
		return false;
	}
	
	/**
	 * @description: 获得当前记录的轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public Track getCurTrack() {
		return curTrack;
	}
	
	/**
	 * @description: 增加模拟时间
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param addTime
	 */
	public void addSimulateTime(int addTime) {
		if(curTrack != null){
			synchronized (curTrack) {
				long sTime = 0;
				if(curTrack.getSimulateTime() != null){
					sTime = curTrack.getSimulateTime();
				}
				curTrack.setSimulateTime(sTime + addTime);
			}
		}
	}
	
	/**
	 * @description: 重新从数据库查询轨迹点
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public List<TrackPoint> refreshAndGetCurTrackPoints(){
		if(curTrack != null){
			List<TrackPoint> tps = curTrack.getTrackPoints();
			curTrack.resetTrackPoints();
			return tps;
		}
		return null;
	}

	// --------------------- Methods private ---------------------
	
	public long getSimulatorTime(){
		long time = 0;
		if(curTrack != null && curTrack.getSimulateTime() != null){
			time = curTrack.getSimulateTime();
		}
		return time;
	}
	
	private void startCountTime(){
		stopCountTime();
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				addSimulateTime(1000);
				
				long time = getSimulatorTime();
				EventBus.getDefault().post(new EventRecordTimeChanged(time));
			}
		}, 0, 1000);
	}
	
	private void stopCountTime(){
		if(timer != null){
			timer.cancel();
		}
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
