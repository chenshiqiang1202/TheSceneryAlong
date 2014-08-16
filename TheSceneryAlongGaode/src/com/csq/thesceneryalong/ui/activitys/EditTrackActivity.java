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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.io.db.TrackDb;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.utils.ToastUtil;
import com.csq.thesceneryalong.utils.edittextfilter.EditTextLengthFilter;

import de.keyboardsurfer.android.widget.crouton.Style;

public class EditTrackActivity extends BaseActionBarActivity {
	
	// ------------------------ Constants ------------------------
	
	public static final String EXTRA_TRACK_ID = "extra_track_id";

	// ------------------------- Fields --------------------------
	protected EditText etName;
	protected EditText etDescrition;
	
	protected long extra_track_id;
	protected String strEditTrack;
	
	protected Track track;

	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_track);
		
		etName = getViewById(R.id.etName);
		etDescrition = getViewById(R.id.etDescrition);
		extra_track_id = getIntent().getLongExtra(EXTRA_TRACK_ID, 0);
		strEditTrack = getResources().getString(R.string.strEditTrack);
		
		//默认显示标题
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		//标题左边返回箭头
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(strEditTrack);
		
		viewsInited();
	}
	
	protected void viewsInited(){
		etName.setFilters(new EditTextLengthFilter[]{new EditTextLengthFilter(20)});
		etDescrition.setFilters(new EditTextLengthFilter[]{new EditTextLengthFilter(100)});
		
		track = TrackDb.getInstance().queryById(extra_track_id);
		
		if(track == null){
			finish();
			return;
		}
		
		etName.setText(track.getName());
		etDescrition.setText(track.getDescription());
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
			String name = etName.getText().toString();
			if(!TextUtils.isEmpty(name)){
				track.setName(name);
				track.setDescription(etDescrition.getText().toString());
				TrackDb.getInstance().update(track, true);
				finish();
			}else{
				ToastUtil.showToastInfo(activity, 
						R.string.name_is_empty, 
						Style.ALERT, 
						false);
			}
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	// --------------------- Methods public ----------------------
	
	public static final void launch(Context context, long trackId){
		Intent i = new Intent(context, EditTrackActivity.class);
		i.putExtra(EXTRA_TRACK_ID, trackId);
		context.startActivity(i);
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
