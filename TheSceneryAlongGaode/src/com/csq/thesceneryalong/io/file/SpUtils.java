/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月1日 下午9:11:46
 * @version 1.0
 */
package com.csq.thesceneryalong.io.file;

import android.content.Context;
import android.content.SharedPreferences;

import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.utils.DateUtils;

import java.util.Date;

public class SpUtils {
	
	private static final String PREFS_NAME = "com.csq.thesceneryalong.prefs";
	
    /**
     * 通用int的保存读取
     * @param key
     * @param value
     */
	public static void saveInt(String key, int value){
		SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if(editor!=null){
			editor.putInt(key, value);
			editor.commit();
		}
	}
	public static int getInt(String key, int defValue){
		SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(key, defValue);
	}

    public static void saveLong(String key, long value){
        SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if(editor!=null){
            editor.putLong(key, value);
            editor.commit();
        }
    }
    public static long getLong(String key, long defValue){
        SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(key, defValue);
    }
	
	/**
     * 通用boolean的保存读取
     * @param key
     * @param value
     */
	public static void saveBoolean(String key, boolean value){
		SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if(editor!=null){
			editor.putBoolean(key, value);
			editor.commit();
		}
	}
	public static boolean getBoolean(String key, boolean defValue){
		SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getBoolean(key, defValue);
	}
	
	/**
     * 通用String的保存读取
     * @param key
     * @param value
     */
	public static void saveString(String key, String value){
		SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		if(editor!=null){
			editor.putString(key, value);
			editor.commit();
		}
	}
	public static String getString(String key, String defValue){
		SharedPreferences prefs = App.app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(key, defValue);
	}
	
	
	/**
	 * "开始记录的提示"
	 */
	public static final String KEY_HELP_START_RECORD = "KEY_HELP_START_RECORD";
	/**
	 * @description: 是否开始记录的提示显示过
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static boolean isStartRecordToastShowed(){
		boolean isShowed = getBoolean(KEY_HELP_START_RECORD, false);
		if(!isShowed){
			saveBoolean(KEY_HELP_START_RECORD, true);
		}
		return isShowed;
	}
	
	/**
	 * "暂停/停止记录"的提示
	 */
	public static final String KEY_HELP_PAUSE_AND_STOP_RECORD = "KEY_HELP_PAUSE_AND_STOP_RECORD";
	/**
	 * @description: 是否暂停/停止记录的提示显示过
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @return
	 */
	public static boolean isPauseAndStopRecordToastShowed(){
		boolean isShowed = getBoolean(KEY_HELP_PAUSE_AND_STOP_RECORD, false);
		if(!isShowed){
			saveBoolean(KEY_HELP_PAUSE_AND_STOP_RECORD, true);
		}
		return isShowed;
	}


    /**
     * 程序第一次运行时间
     */
    public static final String KEY_FIRST_RUN_TIME = "KEY_FIRST_RUN_TIME";

}
