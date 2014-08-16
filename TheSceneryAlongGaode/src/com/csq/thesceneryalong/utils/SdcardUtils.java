/**
 * @description:sd卡相关工具
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 上午12:09:47
 * @version 1.0
 */
package com.csq.thesceneryalong.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class SdcardUtils {

	/**
	 * @author chenshiqiang
	 * Description: 判断SD卡是否存在
	 * @return
	 */
	public static boolean isSdcardExist() {
		return Environment.getExternalStorageState().
			equals(android.os.Environment.MEDIA_MOUNTED);
	}
	
	/**
	 * @author chenshiqiang
	 * Description: 获取SD卡路径
	 * @return
	 */
	public static String getSdcardpath() {
		String sdcardStr = "";
		if (isSdcardExist()) {
			sdcardStr = Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return sdcardStr;
	}
	
	/**
	 * @author chenshiqiang
	 * Description: 获得sdcard剩余空间
	 * @return
	 */
    public static long getSdcardAvailableSize() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			@SuppressWarnings("deprecation")
			long blockSize = sf.getBlockSize();
			@SuppressWarnings("deprecation")
			long availableCount = sf.getAvailableBlocks();
			return availableCount * blockSize;
		}else {
			return 0;
		}
	}
    
    public static boolean isCanDown(long size){
    	long ava = getSdcardAvailableSize();
    	if(ava > size){
    		return true;
    	}
    	return false;
    }
}
