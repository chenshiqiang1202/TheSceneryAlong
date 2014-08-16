/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月21日 下午10:57:19   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.apiadapter;

import android.os.Build;
import android.view.View;

import com.csq.thesceneryalong.utils.apiadapter.video.IVideoView;
import com.csq.thesceneryalong.utils.apiadapter.video.MyVideoView;
import com.csq.thesceneryalong.utils.apiadapter.video.MyVideoView14;

public class ApiUtil {

	public static IVideoView getVideoView(View viewContainer, int resId){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
			return (MyVideoView14)viewContainer.findViewById(resId);
		}else{
			return (MyVideoView)viewContainer.findViewById(resId);
		}
	}
	
}
