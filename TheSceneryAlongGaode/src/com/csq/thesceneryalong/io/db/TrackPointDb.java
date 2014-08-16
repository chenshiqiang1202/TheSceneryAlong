/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午9:11:46   
 * @version 1.0   
 */
package com.csq.thesceneryalong.io.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.location.Location;

import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.db.TrackPoint;
import com.csq.thesceneryalong.db.TrackPointDao;
import com.csq.thesceneryalong.db.TrackPointDao.Properties;
import com.csq.thesceneryalong.logic.manager.MyLocationManager;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.events.EventNewTrackPoint;
import com.csq.thesceneryalong.models.models.PathConfig;
import com.csq.thesceneryalong.models.models.TrackPointStatus;
import com.csq.thesceneryalong.utils.dbmodel.TrackPointUtil;
import com.csq.thesceneryalong.utils.location.LocationUtil;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class TrackPointDb {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private static volatile TrackPointDb instance;
	public static TrackPointDb getInstance(){
		if(instance == null){
			synchronized (TrackPointDb.class) {
				instance = new TrackPointDb();
			}
		}
		return instance;
	}
	
	private TrackPointDao dao;
	
	/**
	 * 当前记录的轨迹的所有点
	 */
	private List<TrackPoint> curPoints = null;
	
	/**
	 * 上次数据库记录的点
	 */
	private TrackPoint lastRecordedPoint = null;
	/**
	 * 上次数据库记录的正常点
	 */
	//private TrackPoint lastRecordedNormalPoint = null;
	
	// ----------------------- Constructors ----------------------
	
	private TrackPointDb(){
		dao = DbManager.getInstance().getTrackPointDao();
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------
	
	/**
	 * @description: 轨迹开始或者恢复，开始缓存当前轨迹点信息
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param curTrack
	 */
	public synchronized void trackRecordStartOrResume(Track curTrack){
		curPoints = curTrack.getTrackPoints();
		curTrack.resetTrackPoints();
		lastRecordedPoint = null;
		//lastRecordedNormalPoint = null;
		
		if(curPoints != null){
			if(!curPoints.isEmpty()){
				//恢复上一个点
				lastRecordedPoint = curPoints.get(curPoints.size()-1);
				//恢复上一个正常点
				/*for(int i = curPoints.size()-1 ; i >= 0 ; i--){
					TrackPoint tp = curPoints.get(i);
					if(tp.getPointStatus() == TrackPointStatus.normal.getValue()){
						lastRecordedNormalPoint = tp;
						break;
					}
				}*/
			}
		}else{
			curPoints = new ArrayList<TrackPoint>();
		}
	}
	
	/**
	 * @description: 轨迹停止，需要清空轨迹点缓存
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void trackStoped(){
		curPoints = null;
		lastRecordedPoint = null;
		//lastRecordedNormalPoint = null;
	}
	
	/**
	 * @description: 添加当前坐标点到当前轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public long addTrackPointToCurTrack(TrackPointStatus status){
		Track curTrack = TrackManager.getInstance().getCurTrack();
		if(curTrack != null){
			if(status == TrackPointStatus.normal){
				Location loc = MyLocationManager.getInstance().getCurrentLocation();
				if(loc != null){
					return addToCurTrack(TrackPointUtil.newNormalPoint(loc, curTrack.getId()));
				}
				
			}else if(status == TrackPointStatus.paused){
				if(lastRecordedPoint != null 
						&& lastRecordedPoint.getPointStatus() != TrackPointStatus.paused.getValue()){
					//有上个点，且上个点不是暂停点，才添加暂停点
					return addToCurTrack(TrackPointUtil.newPausedPoint(curTrack.getId()));
				}
				
			}else if(status == TrackPointStatus.resumed){
				if(lastRecordedPoint != null 
						&& lastRecordedPoint.getPointStatus() != TrackPointStatus.resumed.getValue()){
					return addToCurTrack(TrackPointUtil.newResumedPoint(curTrack.getId()));
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * @description: 添加轨迹点到当前轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param trackPoint
	 * @return
	 */
	private synchronized long addToCurTrack(TrackPoint trackPoint){
		long id = 0;
		Track curTrack = TrackManager.getInstance().getCurTrack();
		//没有记录的轨迹，返回
		if(curTrack == null){
			return id;
		}
		
		if(trackPoint.getTime() < 0){
			trackPoint.setTime(System.currentTimeMillis());
		}
		
		double distance = 0;
		if(lastRecordedPoint != null 
				&& lastRecordedPoint.getPointStatus() == TrackPointStatus.normal.getValue()
				&& trackPoint.getPointStatus() == TrackPointStatus.normal.getValue()){
			//2个正常点，且距离小于5米，或速度超过500km/h，返回
			distance = LocationUtil.getDistance(lastRecordedPoint, trackPoint);
			long time = trackPoint.getTime() - lastRecordedPoint.getTime();
			float speedMS = (float) (distance * 1000f / time);
			if(distance < 5 || speedMS > 140){
				return id;
			}
		}
		
		//插入数据库
		trackPoint.setTrackId(curTrack.getId());
		id =  dao.insert(trackPoint);
		//缓存到内存
		if(curPoints != null){
			curPoints.add(trackPoint);
		}
		
		
		if(trackPoint.getPointStatus() == TrackPointStatus.normal.getValue()){
			//正常点
			if(lastRecordedPoint != null 
					&& lastRecordedPoint.getPointStatus() == TrackPointStatus.normal.getValue()){
				//只有显示的轨迹之间的距离时间才累加
				//即上一个点和这一个点都要是正常的
				
				//移动的总距离和时间
				curTrack.setMovingDistance(curTrack.getMovingDistance() + distance);
				curTrack.setMovingTime(curTrack.getMovingTime() 
						+ trackPoint.getTime() - lastRecordedPoint.getTime());
				
			}else{
				//记录到的第一个点 或者上个点不是正常点
				
				//第一个正常点的时间
				if(curTrack.getMovingDistance() < 0.1){
					curTrack.setFirstPointTime(trackPoint.getTime());
				}
			}
			
			//lastRecordedNormalPoint = trackPoint;
			//最近点的时间
			curTrack.setLastPointTime(trackPoint.getTime());
			//总点数
			curTrack.setPointsNum(curTrack.getPointsNum() + 1);
			//更新轨迹
			TrackDb.getInstance().update(curTrack, true);
			
			//通知有新轨迹点
			EventBus.getDefault().post(new EventNewTrackPoint(trackPoint));
			
		}else{
			//非正常点
			//否则只记录点，不累加距离和时间
		}
		
		lastRecordedPoint = trackPoint;
		return id;
	}
	
	
	
	
	
	/**
	 * @description: trackPoint必须设置trackId
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param trackPoint
	 * @return
	 */
	public long add(TrackPoint trackPoint){
		return dao.insert(trackPoint);
	}
	
	/**
	 * @description: ps必须设置trackId
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param ps
	 */
	public void addSome(Collection<TrackPoint> ps){
		dao.insertInTx(ps);
	}
	
	public void delete(long trackPointId){
		dao.deleteByKey(trackPointId);
	}
	
	public void deleteSome(Collection<Long> trackPointIds){
		dao.deleteByKeyInTx(trackPointIds);
	}
	
	public void deleteByTrackId(long trackId){
		try {
			QueryBuilder<TrackPoint> b = dao.queryBuilder();
			b.where(Properties.TrackId.eq(trackId));
			dao.deleteInTx(b.list());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void update(TrackPoint p){
		dao.update(p);
	}
	
	public void updateSome(Collection<TrackPoint> ps){
		dao.updateInTx(ps);
	}
	
	public List<TrackPoint> query(long trackId){
		return dao._queryTrack_TrackPoints(trackId);
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------
	
	public List<TrackPoint> getCurTrackPoints() {
		return curPoints;
	}
	
	public List<PathConfig> getCurTrackPointsLatLng(int color, int width) {
		return TrackPointUtil.getTrackPointsLatLng(curPoints, color, width);
	}
	
	/**
	 * @description: 获取上次记录的点，可能是暂停、恢复状态的点
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public TrackPoint getLastRecordedPoint() {
		return lastRecordedPoint;
	}
	// --------------- Inner and Anonymous Classes ---------------
	
}
