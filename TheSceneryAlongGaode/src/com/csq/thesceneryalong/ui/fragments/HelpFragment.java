/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月31日 下午4:52:42   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.ui.fragments.base.BaseFragment;

public class HelpFragment extends BaseFragment {

	

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_help, container, false);
	    // TODO Use "injected" views...
		String help1 = getResources().getString(R.string.help11);
		help1 = help1.replace("{a}", PathConstants.getExportpath());
		help1 = help1.replace("{b}", PathConstants.getImportpath());
		((TextView)view.findViewById(R.id.tvHelp11)).setText(help1);
		
	    return view;
	}
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
