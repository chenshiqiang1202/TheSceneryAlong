/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月19日 上午12:24:02   
 * @version 1.0   
 */
package com.csq.thesceneryalong.logic.manager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.models.events.EventTrackImport;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;

import de.greenrobot.event.EventBus;

public class TrackImportManager {

	private static volatile TrackImportManager instance;
	public static TrackImportManager getInstance(){
		if(instance == null){
			synchronized (TrackImportManager.class) {
				instance = new TrackImportManager();
			}
		}
		return instance;
	}
	
	private ExecutorService executorService;
	private TrackImportManager(){
		executorService = Executors.newSingleThreadExecutor();
	}
	
	public void addImportList(List<String> list){
		final int totalSize = list.size();
		
		for(int i = 0; i < totalSize; i++){
			final String tsaPath = list.get(i);
			final int index = i;
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Track track = TrackUtil.importATrack(tsaPath);
					EventBus.getDefault().post(new EventTrackImport(index, 
							totalSize, 
							track));
				}
			});
		}
	}
}
