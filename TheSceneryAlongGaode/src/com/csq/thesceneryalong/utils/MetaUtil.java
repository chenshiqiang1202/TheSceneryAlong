/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年6月4日 下午9:56:38   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.csq.thesceneryalong.app.App;

public class MetaUtil {

	public static String getValue(String key) {
		try {
			ApplicationInfo appInfo = App.app.getPackageManager().getApplicationInfo(
					App.app.getPackageName(), PackageManager.GET_META_DATA);
			return appInfo.metaData.getString(key);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public static String getChannel(){
		return getValue("UMENG_CHANNEL");
	}
}
