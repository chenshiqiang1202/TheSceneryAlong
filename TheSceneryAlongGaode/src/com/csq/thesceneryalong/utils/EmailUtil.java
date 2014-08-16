/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月25日 上午1:34:32   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import de.keyboardsurfer.android.widget.crouton.Style;

import java.util.List;

public class EmailUtil {

	/**
	 * @description: 发送邮件
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param context
	 * @param toEmail 收件人邮箱
	 * @param title 邮件主题
	 * @param msg 邮件基本内容
	 */
	public static final void send(Context context, String toEmail, String title, String msg){
		Intent data = new Intent(Intent.ACTION_SENDTO);
		data.setData(Uri.parse("mailto:" + toEmail));  
		data.putExtra(Intent.EXTRA_SUBJECT, title);  
		data.putExtra(Intent.EXTRA_TEXT, msg);
        // 确定它可以被处理
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(data, 0);
        // 如果安全的话，启动一个activity
        if (!activities.isEmpty()) {
            context.startActivity(data);
        }else{
            ToastUtil.showToastInfo((Activity)context,
                    "未找到邮件应用，您可以通过其他方式将信息发送到邮件:" + toEmail,
                    Style.ALERT,
                    false);
        }
	}
	
}
