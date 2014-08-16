/**
 * @description:像素转换工具
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 上午12:09:47
 * @version 1.0
 */
package com.csq.thesceneryalong.utils;

import com.csq.thesceneryalong.app.App;

public class PxUtil {
	
	public static int dip2px(float dipValue) {
		final float scale = 
				App.app.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(float pxValue) {
		final float scale = 
				App.app.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
