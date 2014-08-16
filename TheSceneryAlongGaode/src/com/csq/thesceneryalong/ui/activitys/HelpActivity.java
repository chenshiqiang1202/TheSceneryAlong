/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月31日 下午4:51:08   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys;

import android.os.Bundle;
import android.view.MenuItem;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.ui.activitys.base.TemplateActionBarActivity;
import com.csq.thesceneryalong.ui.fragments.HelpFragment;

public class HelpActivity extends TemplateActionBarActivity {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------

	// ----------------------- Constructors ----------------------
	
	protected HelpFragment fmHelp;

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

        fmHelp = new HelpFragment();
        setFragement(fmHelp);
		
		//默认显示标题
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		//标题左边返回箭头
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(R.string.help);
	}
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menu) {  
        switch (menu.getItemId()) {
	        case android.R.id.home:
				finish();
	        	break;
	        	
			default:
				break;
		}
        return super.onOptionsItemSelected(menu);  
    }

	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
