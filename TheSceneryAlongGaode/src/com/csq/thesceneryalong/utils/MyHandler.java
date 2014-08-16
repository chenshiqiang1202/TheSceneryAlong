package com.csq.thesceneryalong.utils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class MyHandler extends Handler {
	
	WeakReference<Context> mContext;

	public MyHandler(Context context) {
		mContext = new WeakReference<Context>(context);
	}

	@Override
	public void handleMessage(Message msg) {
		Context theContext = mContext.get();
		if(theContext != null){
			myHandleMessage(msg);
		}else{
			removeCallbacksAndMessages(null);
		}
	}
	
	protected abstract void myHandleMessage(Message msg);
}
