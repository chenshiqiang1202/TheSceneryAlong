/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午3:23:43   
 * @version 1.0   
 */
package com.csq.thesceneryalong.io.db;

import java.io.File;
import java.io.IOException;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.db.DaoMaster;
import com.csq.thesceneryalong.utils.FileUtil;
import com.csq.thesceneryalong.utils.SdcardUtils;

public class MyOpenHelper extends SQLiteOpenHelper {

	// ------------------------ Constants ------------------------
	private static final String dbName = "TheSceneryAlong.db";
	private final static int dbVersion = 1;

	// ------------------------- Fields --------------------------

	// ----------------------- Constructors ----------------------
	
	public MyOpenHelper() {
		super(App.app, getDBPath(), null, dbVersion);
		// TODO Auto-generated constructor stub
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		DaoMaster.createAllTables(db, false);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		/*new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
		}).start();*/
	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------
	
	/**
	 * 有sd卡，在sd卡创建数据库，如果data下面有而sd卡没有，则先拷贝data下面数据库到sd卡
	 * 没有sd卡，在data目录下创建数据库
	 */
	private static String getDBPath()
    {
        // 2.2 之前的版本 数据库不能传路径,放在data下
		if (android.os.Build.VERSION.SDK_INT < 8) {
			return dbName;
		}
        
        String dataDbPath = App.app.getDatabasePath(dbName).getAbsolutePath();
        
        if(SdcardUtils.isSdcardExist())
        {
        	//sd卡存在
			File dir = new File(PathConstants.getDbpath());
			if (!dir.exists()) {
				dir.mkdirs();
			}
            
            String sdcardDbPath = PathConstants.getDbpath() + File.separator + dbName;
            if(!new File(sdcardDbPath).exists()
            		&& new File(dataDbPath).exists())
            {
            	//尝试复制data数据库文件到sd卡
            	try {
					FileUtil.saveFile(dataDbPath, sdcardDbPath, false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            
            return sdcardDbPath;
        }

        return dataDbPath;
    }

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
