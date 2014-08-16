/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月23日 下午9:27:00   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.fragments.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csq.thesceneryalong.ui.activitys.base.TemplateActionBarActivity;

abstract public class BaseFragment extends Fragment {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		releaseResources();
	}

	// --------------------- Methods public ----------------------
	
	public void changeFragment(Fragment newFragment) {
		if(getActivity() instanceof TemplateActionBarActivity){
			TemplateActionBarActivity bfActivity = (TemplateActionBarActivity) getActivity();
			bfActivity.changeFragement(newFragment);
		}
	}
	
	public void backToLastFragement() {
		if(getActivity() instanceof TemplateActionBarActivity){
			TemplateActionBarActivity bfActivity = (TemplateActionBarActivity) getActivity();
			bfActivity.backFragement();
		}
	}
	
	// --------------------- Methods private ---------------------
	
	/**
	 * @description: 界面退出时释放占用内存特别多的资源
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	abstract protected void releaseResources();

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
