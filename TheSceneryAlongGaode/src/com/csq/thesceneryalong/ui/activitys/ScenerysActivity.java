/**
 * @description: 风景查看界面
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月10日 下午11:58:15   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.*;
import android.widget.TextView;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.db.Scenery;
import com.csq.thesceneryalong.io.db.SceneryDb;
import com.csq.thesceneryalong.models.events.EventSceneryUpdated;
import com.csq.thesceneryalong.models.models.SceneryType;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.ui.views.SceneryDetailDrawerView;
import com.csq.thesceneryalong.ui.views.scenery.ScenenryImageItemView;
import com.csq.thesceneryalong.ui.views.scenery.ScenenryItemView;
import com.csq.thesceneryalong.ui.views.scenery.ScenenryVideoItemView;
import com.csq.thesceneryalong.ui.widgets.MultiDirectionSlidingDrawer;
import com.csq.thesceneryalong.ui.widgets.MultiDirectionSlidingDrawer.OnDrawerCloseListener;
import com.csq.thesceneryalong.ui.widgets.MultiDirectionSlidingDrawer.OnDrawerOpenListener;
import com.csq.thesceneryalong.utils.DebugLog;
import com.csq.thesceneryalong.utils.tasks.CsqBackgroundTask;
import de.greenrobot.event.EventBus;
import eu.inmite.android.lib.dialogs.ISimpleDialogListener;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ScenerysActivity extends BaseActionBarActivity implements ISimpleDialogListener{

	// ------------------------ Constants ------------------------
	
	public static final String EXTRA_TRACK_ID = "extra_track_id";
	public static final String EXTRA_TRACK_NAME = "extra_track_name";
	public static final String EXTRA_SCENERY_INDEX = "extra_scenery_index";
	
	public static final int SimpleDialogDeleteSceneryRequestCode = 1;

	// ------------------------- Fields --------------------------
	
	protected long extra_track_id;
	protected String extra_track_name;
	protected int extra_scenery_index;
	
	protected ViewPager vpMedie;
	protected TextView tvIndex;
	protected MultiDirectionSlidingDrawer lyDrawer;
	protected SceneryDetailDrawerView vDetailDrawer;
	
	private List<Scenery> scenerys;
	private SceneryPagerAdapter adapter;
	
	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			// TODO Auto-generated method stub
			DebugLog.d("OnPageChangeListener onPageSelected = " + position);
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// TODO Auto-generated method stub
			DebugLog.d("OnPageChangeListener onPageScrolled = " + position 
					+ " : " + positionOffset 
					+ " : " + positionOffsetPixels);
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
			// TODO Auto-generated method stub
			if(state == ViewPager.SCROLL_STATE_DRAGGING){
				DebugLog.d("OnPageChangeListener SCROLL_STATE_DRAGGING");
				
			}else if(state == ViewPager.SCROLL_STATE_SETTLING){
				DebugLog.d("OnPageChangeListener SCROLL_STATE_SETTLING");
				
			}else if(state == ViewPager.SCROLL_STATE_IDLE){
				DebugLog.d("OnPageChangeListener SCROLL_STATE_IDLE");
				
				pageChanged();
			}
		}
	};

	// ----------------------- Constructors ----------------------
	
	public static void launch(Context context, long trackId, String trackName, int sceneryIndex){
		Intent i = new Intent(context, ScenerysActivity.class);
		i.putExtra(EXTRA_TRACK_ID, trackId);
		i.putExtra(EXTRA_TRACK_NAME, trackName);
		i.putExtra(EXTRA_SCENERY_INDEX, sceneryIndex);
		context.startActivity(i);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_scenerys);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		
		vpMedie = getViewById(R.id.vpMedie);
		tvIndex = getViewById(R.id.tvIndex);
		lyDrawer = getViewById(R.id.lyDrawer);
		vDetailDrawer = getViewById(R.id.vDetailDrawer);
		Intent i = getIntent();
		extra_track_id = i.getLongExtra(EXTRA_TRACK_ID, 0);
		extra_track_name = i.getStringExtra(EXTRA_TRACK_NAME);
		extra_scenery_index = i.getIntExtra(EXTRA_SCENERY_INDEX, 0);
		
		setupView();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
	}
	
	protected void setupView(){
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		
		getSupportActionBar().setTitle(extra_track_name);
		
		/*vpMedie.setTransitionEffect(JazzyViewPager.TransitionEffect.Tablet);*/
		adapter = new SceneryPagerAdapter();
		vpMedie.setAdapter(adapter);
		/*vpMedie.setPageMargin(30);*/
		vpMedie.setOffscreenPageLimit(3);
		vpMedie.setOnPageChangeListener(mOnPageChangeListener);
		
		refreshScenenrys();
		
		lyDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			@Override
			public void onDrawerOpened() {
				// TODO Auto-generated method stub
				lyDrawer.getHandle().setBackgroundResource(R.drawable.ic_map_drawer_down);
			}
		});
		lyDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				lyDrawer.getHandle().setBackgroundResource(R.drawable.ic_map_drawer_up);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();  
        inflater.inflate(R.menu.menu_activity_scenerys, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
        case android.R.id.home:
			finish();
        	break;
        	
        case R.id.options_item_edit:
        	if(scenerys != null && lastPageIndex < scenerys.size()){
				Scenery senery = scenerys.get(lastPageIndex);
				EditSceneryActivity.launch(activity, senery.getId());
			}
        	break;
        	
        case R.id.options_item_delete:
        	SimpleDialogFragment.createBuilder(activity, getSupportFragmentManager())
				.setTitle(R.string.deleteScenery)
				.setMessage(R.string.deleteSceneryWarn)
				.setPositiveButtonText(R.string.yes)
				.setNegativeButtonText(R.string.no)
				.setRequestCode(SimpleDialogDeleteSceneryRequestCode)
				.show();
        	break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onPositiveButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
		if(requestCode == SimpleDialogDeleteSceneryRequestCode){
			if(scenerys != null && lastPageIndex < scenerys.size()){
				Scenery remove = null;
				synchronized (scenerys) {
					remove = scenerys.remove(lastPageIndex);
					adapter.notifyDataSetChanged();
				}
				SceneryDb.getInstance().delete(remove);
				
				if(scenerys.isEmpty()){
					finish();
				}else{
					//序号减1
					lastPageIndex--;
					if(lastPageIndex < 0){
						lastPageIndex = 0;
					}
					
					vpMedie.setCurrentItem(lastPageIndex);
					pageChanged();
				}
			}
			
		}
	}

	@Override
	public void onNegativeButtonClicked(int requestCode) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(lyDrawer.isOpened()){
			lyDrawer.close();
			return;
		}
		super.onBackPressed();
	}
	
	/**
	 * @description: 刷新风景数据
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void refreshScenenrys(){
		//异步加载风景
		new CsqBackgroundTask<List<Scenery>>(this) {
			@Override
			protected List<Scenery> onRun() {
				// TODO Auto-generated method stub
				return SceneryDb.getInstance().queryScenerys(extra_track_id);
			}

			@Override
			protected void onResult(List<Scenery> result) {
				// TODO Auto-generated method stub
				scenerysLoaded(result);
			}
		}.start();
	}
	/**
	 * @description: 风景列表加载回调
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param ss
	 */
	public void scenerysLoaded(List<Scenery> ss){
		synchronized (this) {
			this.scenerys = ss;
			adapter.notifyDataSetChanged();
		}
		
		int curPageIndex = extra_scenery_index;
		if(curPageIndex < 0){
			curPageIndex = 0;
		}
		if(curPageIndex > scenerys.size()){
			curPageIndex = scenerys.size() - 1;
		}
		
		vDetailDrawer.updateTrack(extra_track_id);
		
		final int to = curPageIndex;
		vpMedie.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				vpMedie.setCurrentItem(to);
				pageChanged();
			}
		});
		
	}

	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	// --------------------- Methods public ----------------------

	// --------------------- Methods private ---------------------
	
	private int lastPageIndex = 0;
	protected void pageChanged(){
		if(scenerys == null){
			return;
		}
		
		//先暂停上一个展示的页面
		View last = vpMedie.findViewWithTag("" + lastPageIndex);
		if(last != null){
			((ScenenryItemView)last).onHide();
		}
		int newPage = vpMedie.getCurrentItem();
		//再播放这一个展示的页面
		View thisView = vpMedie.findViewWithTag("" + newPage);
		if(thisView != null){
			((ScenenryItemView)thisView).onShow();
		}
		
		tvIndex.setText((newPage+1) + " / " + scenerys.size());
		
		vDetailDrawer.updateScenerys(scenerys, newPage);
		
		lastPageIndex = newPage;
		
	}
	
	
	public void onEventMainThread(EventSceneryUpdated event){
		scenerys.set(lastPageIndex, event.scenery);
		vDetailDrawer.updateScenery(event.scenery);
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
	
	private List<SoftReference<ScenenryImageItemView>> imageCache 
		= new LinkedList<SoftReference<ScenenryImageItemView>>();
	
	private List<SoftReference<ScenenryVideoItemView>> videoCache 
		= new LinkedList<SoftReference<ScenenryVideoItemView>>();
	
	private class SceneryPagerAdapter extends PagerAdapter{
		public SceneryPagerAdapter(){
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(scenerys != null){
				return scenerys.size();
			}
			return 0;
		}
		
		public Scenery getItem(int position){
			return scenerys.get(position);
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			// TODO Auto-generated method stub
            return view == obj;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			ScenenryItemView view = null;
			Scenery data = getItem(position);
			if(data.getType().equals(SceneryType.image.name())){
				//先从缓存中找
				if(!imageCache.isEmpty()){
					Iterator<SoftReference<ScenenryImageItemView>> it = imageCache.iterator();
					while(it.hasNext()){
						view = it.next().get();
						it.remove();
						if(view != null){
							break;
						}
					}
				}
				
				//缓存没有，再加载
				if(view == null){
					view = new ScenenryImageItemView(activity, vpMedie);
				}
				
			}else{
				//先从缓存中找
				if(!videoCache.isEmpty()){
					Iterator<SoftReference<ScenenryVideoItemView>> it = videoCache.iterator();
					while(it.hasNext()){
						view = it.next().get();
						it.remove();
						if(view != null){
							break;
						}
					}
				}
				
				//缓存没有，再加载
				if(view == null){
					view = new ScenenryVideoItemView(activity);
				}
			}
			
			((View)view).setTag("" + position);
			
			view.setData(getItem(position));
			
			container.addView((View)view, 
					ViewGroup.LayoutParams.MATCH_PARENT, 
					ViewGroup.LayoutParams.MATCH_PARENT);
			//vpMedie.setObjectForPosition(view, position);
			return (View)view;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			View view = container.findViewWithTag("" + position);
			//View view = vpMedie.findViewFromObject(position);
			if(view != null){
				container.removeView(view);
				
				if(view instanceof ScenenryImageItemView){
					ScenenryImageItemView v = (ScenenryImageItemView)view;
					v.releaseResources();
					imageCache.add(new SoftReference<ScenenryImageItemView>(v));
					
				}else if(view instanceof ScenenryVideoItemView){
					ScenenryVideoItemView v = (ScenenryVideoItemView)view;
					v.releaseResources();
					videoCache.add(new SoftReference<ScenenryVideoItemView>(v));
				}
			}
		}
	}

}
