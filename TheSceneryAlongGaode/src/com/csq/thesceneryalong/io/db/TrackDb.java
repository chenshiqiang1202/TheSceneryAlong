/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午3:36:30   
 * @version 1.0   
 */
package com.csq.thesceneryalong.io.db;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.csq.thesceneryalong.models.models.TrackListData;
import org.apache.commons.io.FileUtils;

import android.text.TextUtils;

import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.db.TrackDao;
import com.csq.thesceneryalong.db.TrackDao.Properties;
import com.csq.thesceneryalong.models.events.EventTrackNumChanged;
import com.csq.thesceneryalong.models.events.EventTrackUpdated;
import com.csq.thesceneryalong.models.models.RecordStatus;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition.StringCondition;
import de.greenrobot.event.EventBus;

public class TrackDb {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private static volatile TrackDb instance;
	public static TrackDb getInstance(){
		if(instance == null){
			synchronized (TrackDb.class) {
				instance = new TrackDb();
			}
		}
		return instance;
	}
	
	private TrackDao dao;
	/**
	 * 缓存到内存的所有轨迹，必须Track.getId() > 0
	 */
	private TrackMemory trackMemory;

	// ----------------------- Constructors ----------------------
	
	private TrackDb(){
		dao = DbManager.getInstance().getTrackDao();
		trackMemory = new TrackMemory();
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------
	
	public void loadAllTrackToMemory(){
		trackMemory.loadAllTrackToMemory();
	}
	
	public long add(Track track){
		long id = dao.insert(track);
		
		track.setId(id);
		trackMemory.add(track);
		
		EventBus.getDefault().post(new EventTrackNumChanged());
		
		return id;
	}
	
	public void addSome(Collection<Track> tracks){
		dao.insertInTx(tracks);
		
		//只能重新加载了
		trackMemory.loadAllTrackToMemory();
		
		EventBus.getDefault().post(new EventTrackNumChanged());
	}
	
	public void delete(long trackId){
		//数据库删除
		dao.deleteByKey(trackId);
		TrackPointDb.getInstance().deleteByTrackId(trackId);
		SceneryDb.getInstance().deleteByTrackId(trackId);
		//缓存删除
		Track del = trackMemory.remove(trackId);
		//轨迹文件夹删除
		try {
			FileUtils.deleteDirectory(
					new File(PathConstants.getTrackpath() 
							+ File.separator + del.getUniqueMack()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EventBus.getDefault().post(new EventTrackNumChanged());
	}
	
	/**
	 * @description: 更新轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param track
	 * @param updateVersion 版本信息是否+1，有些情况可能不需要更新版本号，如轨迹暂停状态改变
	 */
	public void update(Track track, boolean updateVersion){
		if(updateVersion){
			track.setVersion(track.getVersion() + 1);
		}
		dao.update(track);
		
		trackMemory.add(track);
		
		EventBus.getDefault().post(new EventTrackUpdated(track));
	}
	
	/**
	 * @description: 停止所有轨迹的记录
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void stopAllTracks(){
		QueryBuilder<Track> qb = dao.queryBuilder();
		qb.where(Properties.RecordStatus.notEq(RecordStatus.finished.getValue()));
		List<Track> uf = qb.list();
		if(uf != null && !uf.isEmpty()){
			for(Track t : uf){
				t.setRecordStatus(RecordStatus.finished.getValue());
			}
			dao.updateInTx(uf);
			trackMemory.addSome(uf);
			
			EventBus.getDefault().post(new EventTrackUpdated(null));
		}
	}
	
	/**
	 * @description: 停止某些轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param trackIds
	 */
	public void stopTracks(Collection<Long> trackIds){
		QueryBuilder<Track> qb = dao.queryBuilder();
		qb.where(Properties.Id.in(trackIds));
		List<Track> uf = qb.list();
		if(uf != null && !uf.isEmpty()){
			for(Track t : uf){
				t.setRecordStatus(RecordStatus.finished.getValue());
			}
			dao.updateInTx(uf);
			trackMemory.addSome(uf);
			
			EventBus.getDefault().post(new EventTrackUpdated(null));
		}
	}
	
	/**
	 * @description: 通过trackId查询轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param trackId
	 * @return
	 */
	public Track queryById(long trackId){
		return trackMemory.getTrack(trackId);
	}
	
	/**
	 * @description: 从数据库查询所有轨迹，按照开始时间降序
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	private List<Track> queryAllDescByBeginTimeFromDb(){
		QueryBuilder<Track> qb = dao.queryBuilder();
		qb.orderDesc(Properties.BeginTime);
		return qb.list();
	}
	
	/**
	 * @description: 获取内存缓存的所有轨迹，按照开始时间降序
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public List<Track> queryAllDescByBeginTime(){
		return trackMemory.getAllTracks();
	}
	
	public TrackListData queryAllSelectionDescByBeginTime(String filter){
        TrackListData list = TrackListData.createEmptyTrackListData();
        List<Track> ll = queryAllDescByBeginTime();
		if(ll != null && !ll.isEmpty()){
			boolean isFilterEmpty = TextUtils.isEmpty(filter);

            Track track = null;
            String lastSection = null;
			for(int i = 0, num = ll.size() ; i < num ; i++){
				track = ll.get(i);
				if(!isFilterEmpty && !track.getName().contains(filter)){
                    //被过滤，下一个
					continue;
				}

                list.tracks.add(track);
				String cs = TrackUtil.getListSelection(track);
				if(!cs.equals(lastSection)){
					//另一段
					lastSection = cs;
					//保存section++
                    list.sectionIndices.add(list.tracks.size()-1);
                    list.headLetters.add(cs);
				}
			}
		}
		return list;
	}
	
	/**
	 * @description: 查询未完成的轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public Track queryUnFinished(){
		QueryBuilder<Track> qb = dao.queryBuilder();
		qb.where(Properties.RecordStatus.notEq(RecordStatus.finished.getValue()));
		qb.orderDesc(Properties.BeginTime);
		List<Track> ls = qb.list();
		if(ls != null && !ls.isEmpty()){
			if(ls.size() == 1){
				return ls.get(0);
			}else{
				Track uf = ls.remove(0);
				//停止最近以前的所有轨迹
				List<Long> cs = new ArrayList<Long>();
				for(Track t : ls){
					cs.add(t.getId());
				}
				stopTracks(cs);
				
				return uf;
			}
		}
		return null;
	}
	
	/**
	 * @description: 通过唯一标识查询轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param uniqueMack
	 * @return
	 */
	public Track queryByUniqueMack(String uniqueMack){
		QueryBuilder<Track> qb = dao.queryBuilder();
		qb.where(Properties.UniqueMack.eq(uniqueMack));
		List<Track> ls = qb.list();
		if(ls == null || ls.isEmpty()){
			return null;
		}else{
			return ls.get(0);
		}
	}
	
	/**
	 * @description: 通过查询语句查询
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param StringCondition 例如："_ID IN " +
				"(SELECT USER_ID FROM USER_MESSAGE WHERE READ_FLAG = 0)"
	 * @return
	 */
	public List<Track> queryByStringCondition(String StringCondition){
		Query<Track> query = dao.queryBuilder().where(
				new StringCondition(StringCondition)).build();
		return query.list();
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
	
	public static class TrackBeginTimeDescComparator implements Comparator<Track>{
		@Override
		public int compare(Track o1, Track o2) {
			// TODO Auto-generated method stub
			if(o1.getBeginTime() == o2.getBeginTime()){
				return 0 ;
			}else if(o1.getBeginTime() > o2.getBeginTime()){
				return -1;
			}else{
				return 1;
			}
		}
	}
	
	public class TrackMemory{
		
		private ReentrantLock lock = new ReentrantLock();
		private List<Track> tracks = new LinkedList<Track>();
		private HashMap<Long, Track> tracksMap = new HashMap<Long, Track>();
		private TrackBeginTimeDescComparator trackBeginTimeDescComparator = new TrackBeginTimeDescComparator();
		
		/**
		 * @description: 加载所有轨迹到内存
		 * @author: chenshiqiang E-mail:csqwyyx@163.com
		 */
		public void loadAllTrackToMemory(){
			List<Track> ts = queryAllDescByBeginTimeFromDb();
			setTracks(ts, true);
		}
		
		public void setTracks(List<Track> ts, boolean isBeginTimeDesc) {
			lock.lock();
			try {
				tracks.clear();
				tracksMap.clear();
				
				if(ts != null && !ts.isEmpty()){
					tracks = ts;
					if(!isBeginTimeDesc){
						Collections.sort(tracks, trackBeginTimeDescComparator);
					}
					
					for(Track t : tracks){
						tracksMap.put(t.getId(), t);
					}
				}
				
			} finally {
				lock.unlock();
			}
			
		}
		
		public void add(Track track){
			lock.lock();
			try {
				if(tracksMap.containsKey(track.getId())){
					Track r = tracksMap.remove(track.getId());
					tracks.remove(r);
				}
				
				tracks.add(track);
				Collections.sort(tracks, trackBeginTimeDescComparator);
				
				tracksMap.put(track.getId(), track);
				
			} finally {
				lock.unlock();
			}
		}
		
		public void addSome(Collection<Track> ts){
			lock.lock();
			try {
				
				for(Track t : ts){
					if(tracksMap.containsKey(t.getId())){
						Track r = tracksMap.remove(t.getId());
						tracks.remove(r);
					}
					
					tracks.add(t);
					tracksMap.put(t.getId(), t);
				}
				
				Collections.sort(tracks, trackBeginTimeDescComparator);
				
			} finally {
				lock.unlock();
			}
		}
		
		public void remove(Track track){
			remove(track.getId());
		}
		
		public Track remove(long trackId){
			lock.lock();
			try {
				if(tracksMap.containsKey(trackId)){
					Track r = tracksMap.remove(trackId);
					tracks.remove(r);
					return r;
				}
			} finally {
				lock.unlock();
			}
			return null;
		}
		
		public List<Track> getAllTracks() {
			return tracks;
		}
		
		public Track getTrack(long trackId){
			return tracksMap.get(trackId);
		}
		
	}
}
