/**
 * @description:
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月31日 下午4:51:08
 * @version 1.0
 */
package com.csq.thesceneryalong.ui.activitys;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.models.events.EventTrackStopedAndDelete;
import com.csq.thesceneryalong.ui.activitys.base.TemplateActionBarActivity;
import com.csq.thesceneryalong.ui.fragments.AllTrackListFragment;
import com.csq.thesceneryalong.ui.views.TrackCtrlView;
import com.csq.thesceneryalong.utils.ToastUtil;
import com.umeng.update.UmengUpdateAgent;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;

public class MainActivity extends TemplateActionBarActivity 
		implements OnQueryTextListener, OnCloseListener ,ISimpleDialogListener{

	AllTrackListFragment fmTrackList;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fmTrackList = new AllTrackListFragment();
        setFragement(fmTrackList);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setIcon(R.drawable.ic_drawer);
		
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		
		//检测更新
		UmengUpdateAgent.update(this);
		
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	
    	if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	
    	if(fmTrackList.isFoldOpen()){
    		fmTrackList.changeFoldingStatus();
    	}else{
    		super.onBackPressed();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();  
        inflater.inflate(R.menu.menu_main, menu);  
        
        MenuItem searchItem = menu.findItem(R.id.options_item_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu) {  
        switch (menu.getItemId()) {
	        case android.R.id.home:
				fmTrackList.changeFoldingStatus();
	        	break;
	        	
			default:
				break;
		}
        return super.onOptionsItemSelected(menu);  
    }
    
    @Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		//开始搜索
		fmTrackList.filterSearchText(arg0);
		return false;
	}
	
	@Override
	public boolean onClose() {
		// TODO Auto-generated method stub
		//取消搜索
		fmTrackList.filterSearchText("");
		return false;
	}

	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub
		
	}
    
	public void onEventMainThread(EventTrackStopedAndDelete event){
		ToastUtil.showToastInfo(activity, 
				getResources().getString(R.string.strTrackStopedAndDelete), 
				Style.ALERT, 
				true);
	}

	@Override
	public void onPositiveButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
		if(requestCode == TrackCtrlView.requestCodeStopTrack){
			TrackCtrlView tcv = (TrackCtrlView) findViewById(R.id.vTrackCtrl);
			if(tcv != null){
				tcv.stopTrack();
			}
		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
		
	}

}
