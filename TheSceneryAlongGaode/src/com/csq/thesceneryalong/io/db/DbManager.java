/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午3:54:23   
 * @version 1.0   
 */
package com.csq.thesceneryalong.io.db;

import android.database.sqlite.SQLiteDatabase;

import com.csq.thesceneryalong.config.Configer;
import com.csq.thesceneryalong.db.DaoMaster;
import com.csq.thesceneryalong.db.DaoSession;
import com.csq.thesceneryalong.db.SceneryDao;
import com.csq.thesceneryalong.db.TrackDao;
import com.csq.thesceneryalong.db.TrackPointDao;

import de.greenrobot.dao.query.QueryBuilder;


public class DbManager {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private static volatile DbManager instance;
	public static DbManager getInstance(){
		if(instance == null){
			synchronized (DbManager.class) {
				instance = new DbManager();
			}
		}
		return instance;
	}
	
	private SQLiteDatabase db;
	private DaoMaster daoMaster;
	private DaoSession daoSession;

	// ----------------------- Constructors ----------------------
	
	private DbManager(){
		
	}

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------

    public boolean dbInited = false;
	
	public synchronized void init(){
		MyOpenHelper helper = new MyOpenHelper();
		db = helper.getWritableDatabase();
		daoMaster = new DaoMaster(db);
		daoSession = daoMaster.newSession();

        if(getTrackDao() != null){
            dbInited = true;
        }

		if(Configer.isDebugMode){
			QueryBuilder.LOG_SQL = true;  
			QueryBuilder.LOG_VALUES = true;
		}
	}
	
	public TrackDao getTrackDao(){
		return daoSession.getTrackDao();
	}
	
	public SceneryDao getSceneryDao(){
		return daoSession.getSceneryDao();
	}
	
	public TrackPointDao getTrackPointDao(){
		return daoSession.getTrackPointDao();
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------
	
	public SQLiteDatabase getDb() {
		return db;
	}
	
	public DaoMaster getDaoMaster() {
		return daoMaster;
	}
	
	// --------------- Inner and Anonymous Classes ---------------
}
