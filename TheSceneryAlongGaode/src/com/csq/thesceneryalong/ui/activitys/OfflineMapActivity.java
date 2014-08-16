/**
 * @description:离线地图下载管理界面
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月31日 下午4:51:08
 * @version 1.0
 */
package com.csq.thesceneryalong.ui.activitys;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapManager.OfflineMapDownloadListener;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.constant.PathConstants;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OfflineMapActivity extends BaseActionBarActivity {
	
	private ViewPager viewpager;
	private PagerTabStrip pagertab;
	
	private ListView lvAll, lvDowning;
	private Spinner spProvince;
	
	private List<View> views = new ArrayList<View>(2);
	private List<String> titles = new ArrayList<String>(2);
	
	// 离线地图下载控制器
	private OfflineMapManager mMapManager = null;
	// 保存一级目录的省直辖市
	private List<String> mProvinceList = new ArrayList<String>();
	//第二级城市列表
	private HashMap<String, List<OfflineMapCity>> mCitys = new HashMap<String, List<OfflineMapCity>>();
	//下载管理的城市
	private List<OfflineMapCity> mDownList = new ArrayList<OfflineMapCity>();
	
	private DownAdapter mAllAdapter, mManagerAdapter;
	
	private String curDowningCity = null;
	
	private OfflineMapDownloadListener mOfflineMapListener = new OfflineMapDownloadListener() {
		@Override
		public void onDownload(int status, int progress, String downName) {
			statusChanged(status, progress, downName);
			
			switch (status) {
			case OfflineMapStatus.SUCCESS:
				break;
			case OfflineMapStatus.LOADING:
				refreshDownList();
				break;
			case OfflineMapStatus.UNZIP:
				break;
			case OfflineMapStatus.WAITING:
				refreshDownList();
				break;
			case OfflineMapStatus.PAUSE:
				break;
			case OfflineMapStatus.STOP:
				refreshDownList();
				break;
			case OfflineMapStatus.ERROR:
				refreshDownList();
				break;
			default:
				break;
			}
			
			mAllAdapter.notifyDataSetChanged();
			mManagerAdapter.notifyDataSetChanged();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// 设置应用单独的地图存储目录，在下载离线地图或初始化地图时设置
		MapsInitializer.sdcardDir = PathConstants.getOfflineMapPath();
				
		setContentView(R.layout.activity_offline_map);
		
		//默认显示标题
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		//标题左边返回箭头
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setTitle(R.string.offlineMap);
		
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		pagertab = (PagerTabStrip) findViewById(R.id.pagertab);
		
		pagertab.setTabIndicatorColor(getResources().getColor(R.color.blue14)); 
		pagertab.setDrawFullUnderline(false);
		pagertab.setBackgroundColor(getResources().getColor(R.color.green6));
		pagertab.setTextSpacing(50);
		
		LayoutInflater inf = LayoutInflater.from(activity);
		View v1 = inf.inflate(R.layout.view_offline_all, null, false);
		lvAll = (ListView) v1.findViewById(R.id.lvAll);
		spProvince = (Spinner) v1.findViewById(R.id.spProvince);
		views.add(v1);
		
		View v2 = inf.inflate(R.layout.view_offline_down, null, false);
		lvDowning = (ListView) v2.findViewById(R.id.lvDowning);
		views.add(v2);

		titles.add(getResources().getString(R.string.offlineAllCity));
		titles.add(getResources().getString(R.string.offlineDownManager));
		
		viewpager.setOffscreenPageLimit(2);
		viewpager.setAdapter(new MyPagerAdapter());
		
		mAllAdapter = new DownAdapter();
		lvAll.setAdapter(mAllAdapter);
		
		mManagerAdapter = new DownAdapter();
		lvDowning.setAdapter(mManagerAdapter);
		
		initMapManager();
	}

	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
        	break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mMapManager.stop();
	}
	
	private void initMapManager(){
		mMapManager = new OfflineMapManager(this, mOfflineMapListener);
		
		List<OfflineMapProvince> pros = mMapManager.getOfflineMapProvinceList();
		
		List<OfflineMapCity> vGgaiyao = new ArrayList<OfflineMapCity>();
		String kGaiyao = "全国概要图、直辖市、港澳";
		mProvinceList.add(kGaiyao);
		mCitys.put(kGaiyao, vGgaiyao);
		
		for(OfflineMapProvince pro : pros){
			ArrayList<OfflineMapCity> cs = pro.getCityList();
			if(cs != null && !cs.isEmpty()){
				if(cs.size() == 1){
					vGgaiyao.add(cs.get(0));
					
				}else{
					mProvinceList.add(pro.getProvinceName());
					mCitys.put(pro.getProvinceName(), cs);
				}
			}
		}
		
		ArrayAdapter<String> adapter 
			= new ArrayAdapter<String>(this, 
					android.R.layout.simple_spinner_item, 
					mProvinceList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvince.setAdapter(adapter);
        spProvince.setOnItemSelectedListener(
                new OnItemSelectedListener() {
                    public void onItemSelected(
                            AdapterView<?> parent, View view, int position, long id) {
                        mAllAdapter.updateDatas(mCitys.get(mProvinceList.get(position)));
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                        
                    }
                });
        spProvince.setSelection(0);
        
        refreshDownList();
        mMapManager.restart();
	}
	
	private void refreshDownList(){
		synchronized (mDownList) {
			mDownList.clear();
			
			ArrayList<OfflineMapCity> mDowning = mMapManager.getDownloadingCityList();
			if(mDowning != null){
				mDownList.addAll(mDowning);
			}
			
			List<OfflineMapCity> mDone = mMapManager.getDownloadOfflineMapCityList();
			if(mDone != null){
				mDownList.addAll(mDone);
			}
			
			mManagerAdapter.updateDatas(mDownList);
		}
	}
	
	private void statusChanged(int status, int progress, String downName) {
		mAllAdapter.updateStatus(status, progress, downName);
		mManagerAdapter.updateStatus(status, progress, downName);
		mAllAdapter.notifyDataSetChanged();
		mManagerAdapter.notifyDataSetChanged();
	}
	
	
	class MyPagerAdapter extends PagerAdapter{
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			View v = views.get(position);
			container.addView(v);
			return v;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// TODO Auto-generated method stub
			container.removeView(views.get(position));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return views.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			return titles.get(position);
		}
		
	}
	
	class DownAdapter extends BaseAdapter{
		
		private List<OfflineMapCity> datas = new ArrayList<OfflineMapCity>();
		private HashMap<String, OfflineMapCity> names = new HashMap<String, OfflineMapCity>();
		
		public DownAdapter() {
			// TODO Auto-generated constructor stub
		}
		
		public synchronized void updateDatas(List<OfflineMapCity> datas){
			this.datas.clear();
			this.names.clear();
			if(datas != null){
				this.datas.addAll(datas);
				for(OfflineMapCity c : datas){
					names.put(c.getCity(), c);
				}
			}
			notifyDataSetChanged();
		}
		
		public void updateStatus(int status, int progress, String downName){
			OfflineMapCity item = names.get(downName);
			if(item != null){
				item.setCompleteCode(progress);
				item.setState(status);
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if(convertView == null){
				convertView = LayoutInflater.from(activity).inflate(R.layout.itemview_offline, 
						null, 
						false);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.refresh((OfflineMapCity)getItem(position));
			return convertView;
		}
		
	}
	
	class ViewHolder implements OnClickListener{
		private TextView btnDown, tvName, tvSize;
		private OfflineMapCity city;
		
		public ViewHolder(View convertView){
			btnDown = (TextView) convertView.findViewById(R.id.btnDown);
			tvName = (TextView) convertView.findViewById(R.id.tvName);
			tvSize = (TextView) convertView.findViewById(R.id.tvSize);
			btnDown.setOnClickListener(this);
		}
		
		public void refresh(OfflineMapCity city){
			this.city = city;
			btnDown.setTag(city.getCity());
			
			tvName.setText(city.getCity());
			tvSize.setText(StringUtils.getSizeStr(city.getSize()));

			int status = city.getState();
			if (status == OfflineMapStatus.SUCCESS) {
				btnDown.setText("安装完成");
                btnDown.setBackgroundColor(getResources().getColor(R.color.yellow5));

			} else if (status == OfflineMapStatus.UNZIP) {
				btnDown.setText("正在解压");
				btnDown.setBackgroundResource(R.drawable.btn_red5_blue);
				
			}else if (status == OfflineMapStatus.PAUSE
					|| status == OfflineMapStatus.STOP
					|| status == OfflineMapStatus.ERROR) {
				//开始下载
				btnDown.setText("下载");
				btnDown.setBackgroundResource(R.drawable.btn_green5_blue);
				
			}else if (status == OfflineMapStatus.LOADING) {
				if(city.getCity().equals(curDowningCity)){
					//是当前正在下载的城市
					btnDown.setText(city.getcompleteCode() + "%");
					btnDown.setBackgroundResource(R.drawable.btn_red5_blue);
					
				}else{
					//不是当前正在下载的城市
					btnDown.setText("下载");
					btnDown.setBackgroundResource(R.drawable.btn_green5_blue);
				}
			}else if (status == OfflineMapStatus.WAITING) {
				btnDown.setText("等待下载");
				btnDown.setBackgroundResource(R.drawable.btn_red5_blue);
			}
			
		}
		
		private void startDownload(){
			boolean isStart = false;
			try {
				isStart = mMapManager.downloadByCityName(city.getCity());
			} catch (AMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(isStart){
				curDowningCity = city.getCity();
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int status = city.getState();
			if (status == OfflineMapStatus.PAUSE
					|| status == OfflineMapStatus.STOP
					|| status == OfflineMapStatus.ERROR) {
				//开始下载
				startDownload();
				
			}else if (status == OfflineMapStatus.LOADING) {
				if(city.getCity().equals(curDowningCity)){
					//是当前正在下载的城市，暂停
					mMapManager.pause();
					statusChanged(OfflineMapStatus.PAUSE, city.getcompleteCode(), city.getCity());
					
				}else{
					//不是当前正在下载的城市，开始下载
					startDownload();
				}
			}
			
		}
	}

}
