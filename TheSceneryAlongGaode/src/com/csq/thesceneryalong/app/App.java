/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月22日 下午9:01:03   
 * @version 1.0   
 */
package com.csq.thesceneryalong.app;

import android.content.res.Resources;

public class App {
	
	public volatile static TsaApplication app;
	
	public static final Resources getResources(){
		return app.getResources();
	}
	
}
