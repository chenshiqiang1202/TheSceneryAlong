/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年6月1日 上午1:07:03   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class ShareUtil {

	/**
	 * @description: 分享文字
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void shareText(Context context, String title, String text) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		intent.setType("text/plain");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 确定它可以被处理
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
		// 如果安全的话，启动一个activity
		if (!activities.isEmpty()) {
			Intent chooser = Intent.createChooser(intent, title);
			context.startActivity(chooser);
		}
	}
	
}
