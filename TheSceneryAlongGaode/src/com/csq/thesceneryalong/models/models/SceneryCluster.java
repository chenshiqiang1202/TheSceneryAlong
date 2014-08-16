/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月28日 下午11:47:57   
 * @version 1.0   
 */
package com.csq.thesceneryalong.models.models;

import android.graphics.Bitmap;

import com.amap.api.maps.model.LatLng;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.utils.BitmapUtil;
import com.csq.thesceneryalong.utils.dbmodel.SceneryUtil;
import com.csq.thesceneryalong.utils.thumb.ThumbnailLoader;
import com.gaode.maps.android.clustering.ClusterItem;

public class SceneryCluster implements ClusterItem{

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private Scenery scenery;

	// ----------------------- Constructors ----------------------
	
	public SceneryCluster(Scenery scenery) {
		super();
		this.scenery = scenery;
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public LatLng getPosition() {
		// TODO Auto-generated method stub
		return new LatLng(scenery.getLatitude(), scenery.getLongitude());
	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	public Bitmap getPicture() {
		SceneryType type = SceneryType.none;
		if(scenery.getType().equals(SceneryType.image.name())){
			type = SceneryType.image;
		}else if(scenery.getType().equals(SceneryType.video.name())){
			type = SceneryType.video;
		}
		Bitmap result = ThumbnailLoader.getInstance().getThumb(SceneryUtil.getSceneryFilePath(scenery), 
				type);
		if(type == SceneryType.video){
			result = BitmapUtil.addVideoWatermark(result);
		}
		return result;
	}
	
	public Scenery getScenery() {
		return scenery;
	}
	
	// --------------- Inner and Anonymous Classes ---------------
}
