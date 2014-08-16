/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月23日 下午9:24:02   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.amap.api.maps.SupportMapFragment;
import com.csq.thesceneryalong.R;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * BaseCompolent不能使用EActivity
 */
abstract public class TemplateActionBarActivity extends BaseActionBarActivity {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected SmoothProgressBar hProgressBar;

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_template_actionbar);
		
		hProgressBar = getViewById(R.id.hProgressBar);
	}

	// --------------------- Methods public ----------------------
	
	public void setFragement(Fragment fragment){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if(fragment != null){
			fragmentTransaction.add(R.id.fmContainer, fragment);
			fragmentTransaction.commit();
		}
	}
	
	public void setFragement(SupportMapFragment fragment){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if(fragment != null){
			fragmentTransaction.add(R.id.fmContainer, fragment);
			fragmentTransaction.commit();
		}
	}
	
	public void changeFragement(Fragment fragment){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if(fragment != null){
			fragmentTransaction.add(R.id.fmContainer, fragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}
	
	public void changeFragement(SupportMapFragment fragment){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if(fragment != null){
			fragmentTransaction.add(R.id.fmContainer, fragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}
	
	public void backFragement(){
		if(fragmentManager.getBackStackEntryCount() > 0){
			fragmentManager.popBackStackImmediate();
		}
	}
	
	public void showHProgressBar() {
		if(hProgressBar != null){
			hProgressBar.setVisibility(View.VISIBLE);
			hProgressBar.progressiveStart();
		}
	}
	
	public void hideHProgressBar() {
		if(hProgressBar != null){
			hProgressBar.setVisibility(View.INVISIBLE);
			hProgressBar.progressiveStop();
		}
	}
	

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
