/**
 * @description: 拍照或录影工具
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月4日 下午7:48:15   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.constant.PathConstants;

public class CaptureMediaUtil {

	/**
	 * 照片数据回调
	 */
	public static final int REQUESTCODE_PICTURE = 100;
	/**
	 * 视频数据回调
	 */
	public static final int REQUESTCODE_VIDEO = 200;
	
	
	/**
	 * 当前照片路径,文件路径CaptureMediaUtil.curPacturePath
	 */
	private static String curPicturePath = null;
	/**
	 * 拍照获取图片
	 */
	public static void takePicture(Activity context) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
			curPicturePath = getTempMediaPath(MediaType.image);
	        // Create the File where the photo should go
	    	//不设置MediaStore.EXTRA_OUTPUT仅会返回缩略图
	    	//如果指定了目标uri，返回data=null，如果没有指定uri，则data!=null
	    	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(curPicturePath)));
	    	context.startActivityForResult(takePictureIntent, REQUESTCODE_PICTURE);
	    }else{
	    	Toast.makeText(
					context,
					context.getResources().getString(
							R.string.no_camera_intent),
					Toast.LENGTH_LONG).show();
	    }
	}

	/**
	 * @description: 录制视频
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param context
	 */
	public static void recordVideo(Activity context) {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		if (intent.resolveActivity(context.getPackageManager()) != null) {
			// 视频质量
			intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
					CamcorderProfile.QUALITY_HIGH);
			// 视频文件位置，报错！！！
			/*intent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(new File(getTempMediaPath(MediaType.video))));*/
			// 视频时间限制，秒
			intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 180);
			context.startActivityForResult(intent, REQUESTCODE_VIDEO);
		}else{
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.no_camera_intent),
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	/**
	 * @description: 处理数据回调,保证按照用户设定的文件路径curImagePath来返回文件
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return
	 */
	public static MediaData handleActivityResult(Activity activity, int requestCode, int resultCode, Intent data){
		MediaData result = null;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_PICTURE:
				if(!TextUtils.isEmpty(curPicturePath)
						&& new File(curPicturePath).exists()){
					result = new MediaData(MediaType.image, curPicturePath);
				}else{
					if(data == null){
						return null;
					}
					Uri dt = data.getData();
				    if(dt != null){
				        String path = dt.getPath();
	                    if(!TextUtils.isEmpty(path) 
								&& new File(path).exists()){
	                    	result = new MediaData(MediaType.image, path);
	                    }else{
	                    	String pu = FileUtil.getPathFromUri(activity, dt);
	                    	if(!TextUtils.isEmpty(pu) 
	    							&& new File(pu).exists()){
	    						result = new MediaData(MediaType.image, pu);
	    					}
	                    }
				    }else{
				    	if(data.getExtras() != null){
				    		Bitmap bm = data.getExtras().getParcelable("data");
				    		String cp = getTempMediaPath(MediaType.image);
				    		if(bm != null && BitmapUtil.saveBitmap(bm, cp, CompressFormat.JPEG, 100)){
					        	result = new MediaData(MediaType.image, cp);
					        }
				    	}
				    }
				}
				
				break;
			case REQUESTCODE_VIDEO:
				if(data == null){
					return null;
				}
				Uri vdt = data.getData();
			    if(vdt != null){
			    	String path = vdt.getPath();
					if(!TextUtils.isEmpty(path) 
							&& new File(path).exists()){
						result = new MediaData(MediaType.video, path);
					}else{
						String pu = FileUtil.getPathFromUri(activity, vdt);
	                	if(!TextUtils.isEmpty(pu) 
								&& new File(pu).exists()){
							result = new MediaData(MediaType.video, pu);
						}
					}
			    }
				break;
			default:
				break;
			}
		}
		
		return result;
	}
	
	
	/**
	 * @description: 根据当前时间获取缓存路径
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param mediaType
	 * @return
	 */
	public static String getTempMediaPath(MediaType mediaType){
		File cachePath = new File(PathConstants.getMediacachepath());
		if(!cachePath.exists()){
			cachePath.mkdirs();
		}
		
		if(mediaType == MediaType.image){
			return cachePath.getAbsolutePath() + File.separator 
					+ DateUtils.getFormatedDateYMDHMSFile(System.currentTimeMillis())
					+ ".jpg";
		}else{
			String videoPath = cachePath.getAbsolutePath() + File.separator 
					+ DateUtils.getFormatedDateYMDHMSFile(System.currentTimeMillis())
					+ ".mp4";
			File f = new File(videoPath);
			if (!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return videoPath;
		}
	}
	
	
	
	public static enum MediaType{
		image,
		video
	}
	
	public static class MediaData{
		public MediaType mediaType;
		public String filePath;
		
		public MediaData(MediaType mediaType, String filePath) {
			super();
			this.mediaType = mediaType;
			this.filePath = filePath;
		}
	}
}
