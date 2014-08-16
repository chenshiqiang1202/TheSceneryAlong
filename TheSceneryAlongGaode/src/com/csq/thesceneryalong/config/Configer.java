/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月27日 下午7:24:55   
 * @version 1.0   
 */
package com.csq.thesceneryalong.config;

import com.csq.thesceneryalong.models.models.MapType;


public class Configer {
	
	//************************* 发布需要配置 ****************************
	
	/**
	 * 其他需要配置：
	 * 1、versionCode、versionName（必须）
	 * 2、UMENG_CHANNEL（必须）Channels
	 */
	
	/**
	 * 是否是调试模式,发布需要修改为false
	 */
	public static final boolean isDebugMode = true; //false



	//************************* 发布需要配置 ****************************

	/**
	 * 还有2个about31要改
	 */
	public static final String MyEmail = "csqapp@163.com";
	
	/**
	 * 选择的地图类型
	 */
	public static final MapType mapType = MapType.AMap;

}
