/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月4日 下午10:13:07   
 * @version 1.0   
 */
package com.csq.thesceneryalong.io.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.db.SceneryDao;
import com.csq.thesceneryalong.db.SceneryDao.Properties;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.events.EventNewScenery;
import com.csq.thesceneryalong.models.events.EventSceneryNumChanged;
import com.csq.thesceneryalong.models.events.EventSceneryUpdated;
import com.csq.thesceneryalong.utils.dbmodel.SceneryUtil;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.event.EventBus;

public class SceneryDb {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private static volatile SceneryDb instance;
	public static SceneryDb getInstance(){
		if(instance == null){
			synchronized (SceneryDb.class) {
				instance = new SceneryDb();
			}
		}
		return instance;
	}
	
	private SceneryDao dao;
	
	/**
	 * 当前记录的轨迹的所有点
	 */
	private List<Scenery> curScenerys = null;

	// ----------------------- Constructors ----------------------
	
	private SceneryDb(){
		dao = DbManager.getInstance().getSceneryDao();
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------
	
	/**
	 * @description: 轨迹开始或者恢复，开始缓存当前轨迹点信息
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param curTrack
	 */
	public synchronized void trackRecordStartOrResume(Track curTrack){
		curScenerys = curTrack.getScenerys();
		curTrack.resetScenerys();
		
		if(curScenerys == null){
			curScenerys = new ArrayList<Scenery>();
		}
	}
	
	/**
	 * @description: 轨迹停止，需要清空轨迹点缓存
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public synchronized void trackStoped(){
		curScenerys = null;
	}
	
	/**
	 * @description: 添加轨迹点到当前轨迹
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public synchronized long addToCurTrack(Scenery scenery){
		long id = 0;
		Track curTrack = TrackManager.getInstance().getCurTrack();
		
		//没有记录的轨迹，返回
		if(curTrack == null){
			return id;
		}
		
		//插入数据库
		scenery.setTrackId(curTrack.getId());
		id =  dao.insert(scenery);
		
		//缓存到内存
		if(curScenerys != null){
			curScenerys.add(scenery);
		}
		
		//总风景数
		curTrack.setSceneryNum(curTrack.getSceneryNum() + 1);
		//更新轨迹
		TrackDb.getInstance().update(curTrack, true);
		
		//通知有新轨迹点
		EventBus.getDefault().post(new EventNewScenery(scenery));
		
		return id;
	}
	
	/**
	 * @description: ss必须设置trackId
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param ss
	 */
	public void addSome(Collection<Scenery> ss){
		dao.insertInTx(ss);
	}
	
	/**
	 * @description: 删除一个风景，并删除文件
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param scenery
	 */
	public void delete(Scenery scenery){
		//删除数据库
		dao.delete(scenery);
		
		//删除文件
		File f = new File(SceneryUtil.getSceneryFilePath(scenery));
		if(f.exists()){
			f.delete();
		}
		
		//轨迹风景数减一
		Track track = TrackDb.getInstance().queryById(scenery.getTrackId());
		if(track != null){
			track.setSceneryNum(track.getSceneryNum() - 1);
			TrackDb.getInstance().update(track, true);
		}
		
		EventBus.getDefault().post(new EventSceneryNumChanged());
	}
	
	public void deleteByTrackId(long trackId){
		try {
			QueryBuilder<Scenery> b = dao.queryBuilder();
			b.where(Properties.TrackId.eq(trackId));
			dao.deleteInTx(b.list());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void update(Scenery p){
		dao.update(p);
		
		EventBus.getDefault().post(new EventSceneryUpdated(p));
	}
	
	public void updateSome(Collection<Scenery> ps){
		dao.updateInTx(ps);
		
		EventBus.getDefault().post(new EventSceneryUpdated(null));
	}
	
	public List<Scenery> queryScenerys(long trackId){
		QueryBuilder<Scenery> b = dao.queryBuilder();
		b.where(Properties.TrackId.eq(trackId));
        return b.list();
	}
	
	public Scenery queryById(long sceneryId){
		return dao.queryBuilder().where(Properties.Id.eq(sceneryId)).list().get(0);
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	public List<Scenery> getCurScenerys() {
		return curScenerys;
	}
	// --------------- Inner and Anonymous Classes ---------------
}
