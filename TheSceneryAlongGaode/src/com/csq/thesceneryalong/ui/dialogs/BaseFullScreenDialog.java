package com.csq.thesceneryalong.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.csq.thesceneryalong.R;


public class BaseFullScreenDialog extends Dialog {

	public BaseFullScreenDialog(Context context) {
		this(context, R.style.Dialog_Fullscreen);
	}
	
	public BaseFullScreenDialog(Context context, int theme) {
		super(context, R.style.Dialog_Fullscreen);
	}

	protected BaseFullScreenDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		window.setBackgroundDrawable(new ColorDrawable(0));
		this.setCanceledOnTouchOutside(false);
	}
	
	@SuppressWarnings("unchecked")
    protected <T extends View> T getViewById(int id)
	{
		View view = findViewById(id);
		return (T)view;
	}
	
}