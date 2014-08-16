/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月20日 下午9:16:30
 * @version 1.0
 */
package com.csq.thesceneryalong.utils.thumb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Log;

import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.utils.BitmapUtil;

public class ThumbnailUtil {

	/** 
     * 根据指定的图像路径和大小来获取缩略图 
     * 此方法有两点好处： 
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
     *        用这个工具生成的图像不会被拉伸。 
     * @param imagePath 图像的路径 
     * @param width 指定输出图像的宽度 
     * @param height 指定输出图像的高度 
     * @return 生成的缩略图 
     */  
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {  
    	Bitmap bitmap = BitmapUtil.decodeBitmapFromFile(App.app, 
    			imagePath, 
    			width, 
    			width * height);
        
        return bitmap;  
    }  
  
    /** 
     * 获取视频的缩略图 
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。 
     * @param videoPath 视频的路径 
     * @param width 指定输出视频缩略图的宽度 
     * @param height 指定输出视频缩略图的高度度 
     * @param kind 参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96 
     * @return 指定大小的视频缩略图 
     */  
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        // 获取视频的缩略图  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        System.out.println("w"+bitmap.getWidth());  
        System.out.println("h"+bitmap.getHeight());  
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    	
    	//return createVideoThumbnail(videoPath);
    } 
    
    
    /**
     * @description: 获取视频的第一帧
     * @author: chenshiqiang E-mail:csqwyyx@163.com
     * @param filePath
     * @return
     */
    public static Bitmap getVideoFirstFrame(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
            Log.e("", "createVideoThumbnail", e);
        } catch (InvocationTargetException e) {
            Log.e("", "createVideoThumbnail", e);
        } catch (ClassNotFoundException e) {
            Log.e("", "createVideoThumbnail", e);
        } catch (NoSuchMethodException e) {
            Log.e("", "createVideoThumbnail", e);
        } catch (IllegalAccessException e) {
            Log.e("", "createVideoThumbnail", e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }
    
}
