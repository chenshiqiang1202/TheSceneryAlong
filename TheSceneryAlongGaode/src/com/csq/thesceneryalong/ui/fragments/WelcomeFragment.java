/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月23日 下午9:44:10   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.io.db.DbManager;
import com.csq.thesceneryalong.ui.fragments.base.BaseFragment;
import com.csq.thesceneryalong.utils.SdcardUtils;

public class WelcomeFragment extends BaseFragment {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected TextView tvError;

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_welcome, container, false);
		tvError = (TextView) view.findViewById(R.id.tvError);
	    viewInited();
	    return view;
	}
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub
		
	}

	// --------------------- Methods public ----------------------
	
	// --------------------- Methods private ---------------------
	protected void viewInited(){
		if(!SdcardUtils.isSdcardExist()){
			tvError.setText(getResources().getString(R.string.strErrorNoSdcard));

		} else if(!DbManager.getInstance().dbInited){
            tvError.setText(getResources().getString(R.string.strErrorDbInitedFailed));
        }
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
