/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月16日 下午11:07:34   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.io.db.SceneryDb;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.utils.edittextfilter.EditTextLengthFilter;

public class EditSceneryActivity extends BaseActionBarActivity {
	
	// ------------------------ Constants ------------------------
	
	public static final String EXTRA_SCENERY_ID = "extra_scenery_id";

	// ------------------------- Fields --------------------------
	protected EditText etDescrition;
	
	protected long extra_scenery_id;
	protected String strEditScenery;
	
	protected Scenery scenery;

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_scenery);
		
		etDescrition = getViewById(R.id.etDescrition);
		extra_scenery_id = getIntent().getLongExtra(EXTRA_SCENERY_ID, 0);
		strEditScenery = getResources().getString(R.string.strEditScenery);
		
		//默认显示标题
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		//标题左边返回箭头
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(strEditScenery);
		
		viewsInited();
	}
	
	protected void viewsInited(){
		etDescrition.setFilters(new EditTextLengthFilter[]{new EditTextLengthFilter(100)});
		
		scenery = SceneryDb.getInstance().queryById(extra_scenery_id);
		
		if(scenery == null){
			finish();
			return;
		}
		
		etDescrition.setText(scenery.getDescription());
	}

	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItemCompat.setShowAsAction(menu.add(Menu.NONE, Menu.FIRST, 0, R.string.save)
				.setIcon(R.drawable.abc_ic_cab_done_holo_dark), 
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
        	break;
        	
		case Menu.FIRST:
			scenery.setDescription(etDescrition.getText().toString());
			SceneryDb.getInstance().update(scenery);
			finish();
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// --------------------- Methods public ----------------------
	
	public static final void launch(Context context, long sceneryId){
		Intent i = new Intent(context, EditSceneryActivity.class);
		i.putExtra(EXTRA_SCENERY_ID, sceneryId);
		context.startActivity(i);
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
