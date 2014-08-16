/**
 * @description:本地且耗时较短的异步任务
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月17日 上午12:00:40
 * @version 1.0
 */
package com.csq.thesceneryalong.utils.tasks;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;


abstract public class CsqBackgroundTask<T> extends AsyncTask<Void, Integer, T> {
	
	private WeakReference<Object> weakHolder;

	/**
	 * @param holder AsyncTask的容器，也是执行回调的class，可能为Activity或Service，弱引用，防止任务长时间引用导致内存泄漏
	 */
	public CsqBackgroundTask(Object holder) {
		weakHolder = new WeakReference<Object>(holder);
	}
	
	/**
	 * 无需回调，异步短时任务
	 * @author chenshiqiang E-mail:csqwyyx@163.com
	 */
	public CsqBackgroundTask() {
	}
	
	@Override
	protected final T doInBackground(Void... voids) {
		T result = null;
		try {
			result = onRun();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	@SuppressLint("NewApi")
	private boolean canContinue() {
		if(weakHolder == null){
			return false;
		}
		
		Object h = weakHolder.get();
		
		if(h instanceof Activity){
			return h != null && ((Activity)h).isFinishing() == false;
		}
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if(h instanceof android.support.v4.app.Fragment){
				return h != null && ((android.support.v4.app.Fragment)h).isRemoving() == false;
			}
		}
		
		if(h instanceof android.support.v4.app.Fragment){
			return h != null && ((android.support.v4.app.Fragment)h).isRemoving() == false;
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(final T t) {
		if (canContinue()) {
			onResult(t);
		}
	}

	abstract protected T onRun();
	abstract protected void onResult(T result);
	
	public void start(){
		execute();
	}
	
}
