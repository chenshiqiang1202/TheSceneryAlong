/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月31日 下午4:52:42   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.config.Configer;
import com.csq.thesceneryalong.ui.fragments.base.BaseFragment;
import com.csq.thesceneryalong.utils.AppUtil;
import com.csq.thesceneryalong.utils.MetaUtil;

public class AboutFragment extends BaseFragment {

	

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected TextView tvVersion;

	// ----------------------- Constructors ----------------------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		tvVersion = (TextView) view.findViewById(R.id.tvVersion);
	    tvVersion.setText(getResources().getString(R.string.about41pre) + AppUtil.getVerName());
	    
	    view.findViewById(R.id.vAbout).setOnClickListener(new View.OnClickListener() {
			private long lastClickTime = 0;
			private int clickIndex = 0;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long curTime = System.currentTimeMillis();
				int cTime = (int) (curTime - lastClickTime);
				if(cTime < 500){
					if(++clickIndex == 3){
						//展示
						AlertDialog.Builder bu = new AlertDialog.Builder(getActivity());
						String msg = "isDebugMode = " + Configer.isDebugMode + "\n"
								+ "channel = " + MetaUtil.getChannel();
						bu.setMessage(msg);
						bu.create().show();
						
						clickIndex = 0;
					}
				}else{
					clickIndex = 1;
				}
				lastClickTime = curTime;
			}
		});
	    
	    return view;
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
