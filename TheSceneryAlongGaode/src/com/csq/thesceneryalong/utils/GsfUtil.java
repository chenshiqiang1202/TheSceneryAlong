/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年6月1日 下午5:16:37   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import java.util.List;

import android.content.pm.PackageInfo;

import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.config.Configer;
import com.csq.thesceneryalong.models.models.MapType;

public class GsfUtil {

	public static String pkgGoogleServiceFramwork = "com.google.android.gsf";
	
	
	/**
	 * @description: 判断谷歌服务框架是否已安装
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static boolean isGsfInstalled(){
		if(Configer.mapType != MapType.GoogleMap){
			//不是google地图，返回true
			return true;
		}
		
		List<PackageInfo> ps = App.app.getPackageManager().getInstalledPackages(0);
		if(ps != null && !ps.isEmpty()){
			for(PackageInfo pi : ps){
				if(pi.packageName.contains(pkgGoogleServiceFramwork)){
					return true;
				}
			}
		}
		return false;
	}
}
