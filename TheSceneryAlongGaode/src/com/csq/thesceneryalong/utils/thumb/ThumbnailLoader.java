/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月20日 下午9:16:30   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.thumb;

import java.io.File;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.constant.FinalConstants;
import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.models.models.SceneryType;
import com.csq.thesceneryalong.utils.disklrucache.DiskLruCache;

public class ThumbnailLoader {

	// ------------------------ Constants ------------------------
	
	private static final int thumbSize = 160;

	// ------------------------- Fields --------------------------
	
	private volatile static ThumbnailLoader instance;
	
	public static ThumbnailLoader getInstance() {
		if(instance == null){
			synchronized (ThumbnailLoader.class) {
				instance = new ThumbnailLoader();
			}
		}
		return instance;
	}
	
	private ThumbnailLoader(){
		memoryCache = new LruCache<String, Bitmap>(2 * FinalConstants.byte_1m){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
				return value.getWidth() * value.getHeight() * 4;
			}
			
			@Override
			protected void entryRemoved(boolean evicted, String key,
					Bitmap oldValue, Bitmap newValue) {
				// TODO Auto-generated method stub
				super.entryRemoved(evicted, key, oldValue, newValue);
			}
		};
		
		diskCache = DiskLruCache.openCache(App.app, 
				new File(PathConstants.getThumbpath()), 
				16 * FinalConstants.byte_1m);
	}
	
	private LruCache<String, Bitmap> memoryCache = null;
	
	private DiskLruCache diskCache = null;

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------

	// --------------------- Methods public ----------------------
	
	/**
	 * @description: 获得缩略图
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param filePath
	 * @param type
	 * @return
	 */
	public Bitmap getThumb(String filePath, SceneryType type){
		Bitmap ret = null;
		if(TextUtils.isEmpty(filePath) || !new File(filePath).exists()){
			return null;
		}
		//1.内存
		ret = memoryCache.get(filePath);
		if(ret == null){
			//2.sd卡
			ret = diskCache.get(filePath);
			//3.从新加载
			if(ret == null){
				if(type == SceneryType.image){
					ret = ThumbnailUtil.getImageThumbnail(filePath, 
							thumbSize, 
							thumbSize);
				}else if(type == SceneryType.video){
					ret = ThumbnailUtil.getVideoThumbnail(filePath, 
							thumbSize, 
							thumbSize, 
							MediaStore.Images.Thumbnails.MICRO_KIND);
				}
				
				if(ret != null){
					memoryCache.put(filePath, ret);
					diskCache.put(filePath, ret);
				}
			}
		}
		return ret;
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
