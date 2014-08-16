/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月11日 下午4:54:28   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.views.scenery;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.constant.FinalConstants;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.models.models.SceneryType;
import com.csq.thesceneryalong.utils.BitmapUtil;
import com.csq.thesceneryalong.utils.dbmodel.SceneryUtil;
import com.csq.thesceneryalong.utils.thumb.ThumbnailLoader;

public class ScenenryImageItemView extends RelativeLayout implements ScenenryItemView{

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected ImageViewTouch ivMedia;
	private ViewGroup mDisallowInterceptParent;
	
	private Scenery scenery;
	
	private Bitmap bitmap;

	// ----------------------- Constructors ----------------------
	
	public ScenenryImageItemView(Context context, ViewGroup mDisallowInterceptParent) {
		super(context);
		// TODO Auto-generated constructor stub
		this.mDisallowInterceptParent = mDisallowInterceptParent;
		initView(context);
	}
	
	private void initView(Context context){
		
		View v = LayoutInflater.from(context).inflate(R.layout.itemview_scenery_image, 
				this, 
				true);
		
		ivMedia = (ImageViewTouch) v.findViewById(R.id.ivMedia);
		ivMedia.setDisallowInterceptParent(mDisallowInterceptParent);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void setData(Scenery scenery) {
		// TODO Auto-generated method stub
		this.scenery = scenery;
		setThumb();
	}

	@Override
	public void releaseResources() {
		// TODO Auto-generated method stub
		ivMedia.setImageBitmap(null);
		ivMedia.setDisallowInterceptParent(null);
		mDisallowInterceptParent = null;
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		bitmap = BitmapUtil.decodeBitmapFromFile(
        		getContext(),
        		SceneryUtil.getSceneryFilePath(scenery), 
        		FinalConstants.pic_minSideLength_4m, 
        		FinalConstants.pic_maxNumOfPixels_4m);
        if(bitmap != null){
        	//final int size = -1; // use the original image size
        	ivMedia.setImageBitmap(bitmap, 
        			new Matrix(), 
        			ImageViewTouchBase.ZOOM_INVALID, 
        			ImageViewTouchBase.ZOOM_INVALID );
        }
	}

	@Override
	public void onHide() {
		// TODO Auto-generated method stub
		ivMedia.setImageBitmap(null);
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
		
		setThumb();
	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------
	
	private void setThumb(){
		if(scenery != null){
			Bitmap thumb = ThumbnailLoader.getInstance().getThumb(
					SceneryUtil.getSceneryFilePath(scenery), 
					SceneryType.image);
			//int scale = getWidth() / thumb.getWidth();
			ivMedia.setImageBitmap(thumb, 
							new Matrix(), 
							1, 
							1);
		}
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
