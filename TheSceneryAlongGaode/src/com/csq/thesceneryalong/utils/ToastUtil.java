/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月14日 上午12:09:47
 * @version 1.0
 */
package com.csq.thesceneryalong.utils;

import android.app.Activity;
import android.os.Handler;

import com.csq.thesceneryalong.app.App;

import de.keyboardsurfer.android.widget.crouton.Configuration;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ToastUtil {
	
	private static final Configuration CONFIGURATION_LONG = new Configuration.Builder()
		    .setDuration(Configuration.DURATION_LONG)
		    .build();
	
	private static final Configuration CONFIGURATION_SHORT = new Configuration.Builder()
		    .setDuration(Configuration.DURATION_SHORT)
		    .build();
	
	
	private static Handler handler = new Handler(App.app.getMainLooper());
	
	public static void showToastInfo(final Activity activity, 
			final String text,
			final Style style,
			final boolean isLong) 
	{
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Crouton.makeText(activity, text, style)
					.setConfiguration(isLong ? CONFIGURATION_LONG : CONFIGURATION_SHORT)
					.show();
			}
		});
	}
	
	public static void showToastInfo(final Activity activity, 
			final int resId, 
			final Style style,
			final boolean isLong) 
	{
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Crouton.makeText(activity, resId, style)
						.setConfiguration(isLong ? CONFIGURATION_LONG : CONFIGURATION_SHORT)
						.show();
			}
		});
	}
	
}
