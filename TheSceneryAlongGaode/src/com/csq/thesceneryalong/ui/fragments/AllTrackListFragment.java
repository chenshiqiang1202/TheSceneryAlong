/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月23日 下午9:49:29   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.io.db.TrackDb;
import com.csq.thesceneryalong.io.file.SpUtils;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.events.EventCurTrackStatusChanged;
import com.csq.thesceneryalong.models.events.EventTrackNumChanged;
import com.csq.thesceneryalong.models.events.EventTrackUpdated;
import com.csq.thesceneryalong.models.models.TrackListData;
import com.csq.thesceneryalong.ui.activitys.TrackDetailMapActivity;
import com.csq.thesceneryalong.ui.fragments.base.BaseFragment;
import com.csq.thesceneryalong.ui.views.MainFoldingView;
import com.csq.thesceneryalong.utils.GsfUtil;
import com.csq.thesceneryalong.utils.StringUtils;
import com.csq.thesceneryalong.utils.TimeUtil;
import com.csq.thesceneryalong.utils.ToastUtil;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.nhaarman.supertooltips.ToolTipView.OnToolTipViewClickedListener;
import com.ptr.folding.FoldingPaneLayout;
import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Style;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import java.lang.reflect.Field;

public class AllTrackListFragment extends BaseFragment {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private AllTrackListAdapter adapter;
	
	FoldingPaneLayout vFoldingPaneLayout;
	MainFoldingView vLeftMenu;
    StickyListHeadersListView lvTracks;
	private ToolTipRelativeLayout lyTooltipframe;
    private View emptyView;
	
	String strM;
	String strKm;
	String strRecording;
	
	private ToolTipView mStartTipView;
	
	// ----------------------- Constructors ----------------------

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_all_track_list, container, false);

		vFoldingPaneLayout = (FoldingPaneLayout) view.findViewById(R.id.vFoldingPaneLayout);
		vLeftMenu = (MainFoldingView) view.findViewById(R.id.vLeftMenu);
		lvTracks = (StickyListHeadersListView) view.findViewById(R.id.lvTracks);
		lyTooltipframe = (ToolTipRelativeLayout) view.findViewById(R.id.lyTooltipframe);
        emptyView = view.findViewById(R.id.emptyView);
	    
	    strM = getResources().getString(R.string.strM);
		strKm = getResources().getString(R.string.strKm);
		strRecording = getResources().getString(R.string.strRecording);
	    // TODO Use "injected" views...
		setupView();
	    return view;
    }
	
    protected void setupView() {
    	if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		
		vFoldingPaneLayout.getFoldingLayout().setBackgroundColor(Color.BLACK);

        lvTracks.setEmptyView(emptyView);

		adapter = new AllTrackListAdapter(
				TrackDb.getInstance().queryAllSelectionDescByBeginTime(searchFilter));
		lvTracks.setAdapter(adapter);

        lvTracks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(GsfUtil.isGsfInstalled()){
                    int headers = lvTracks.getHeaderViewsCount();
                    Track track = (Track)adapter.getItem(i - headers);
                    TrackDetailMapActivity.launch(getActivity(), track.getId());

                }else{
                    //没有谷歌服务框架
                    ToastUtil.showToastInfo(getActivity(),
                            R.string.strErrorNoGsf,
                            Style.ALERT,
                            false);
                }
            }
        });

        intFastScrollView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!SpUtils.isStartRecordToastShowed() && !TrackManager.getInstance().isHaveRecordingTrack()){
            addStartTipView();
        }
    }

    @Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
	}
	
	@Override
	protected void releaseResources() {
		// TODO Auto-generated method stub
		
	}

	// --------------------- Methods public ----------------------
	
	public void onEventMainThread(EventTrackNumChanged event){
		adapter.updateDatas(
				TrackDb.getInstance().queryAllSelectionDescByBeginTime(searchFilter));
	}
	
	public void onEventMainThread(EventTrackUpdated event){
		adapter.notifyDataSetChanged();
	}
	
	public void onEventMainThread(EventCurTrackStatusChanged event){
		removeStartTipView();
	}
	
	public void changeFoldingStatus(){
		if(vFoldingPaneLayout.isOpen()){
			vFoldingPaneLayout.closePane();
		}else{
			vFoldingPaneLayout.openPane();
		}
	}
	
	public boolean isFoldOpen(){
		return vFoldingPaneLayout.isOpen();
	}
	
	private String searchFilter = "";
	public void filterSearchText(String filter){
		this.searchFilter = filter;
		if(adapter != null){
			adapter.updateDatas(
					TrackDb.getInstance().queryAllSelectionDescByBeginTime(searchFilter));
		}
	}

	// --------------------- Methods private ---------------------
	
	private void addStartTipView() {
		String strHelpTipStartRecord = getResources().getString(R.string.strHelpTipStartRecord);
		ToolTip toolTip = new ToolTip().withText(strHelpTipStartRecord)
				.withColor(getResources().getColor(R.color.green6))
				.withTextColor(getResources().getColor(R.color.red))
				.withAnimationType(ToolTip.AnimationType.FROM_TOP);

		View btnStart = getActivity().findViewById(R.id.btnStart);
		if(btnStart != null && lyTooltipframe != null){
			mStartTipView = lyTooltipframe.showToolTipForView(toolTip, btnStart);
			mStartTipView.setOnToolTipViewClickedListener(new OnToolTipViewClickedListener() {
				@Override
				public void onToolTipViewClicked(ToolTipView toolTipView) {
					// TODO Auto-generated method stub
					mStartTipView = null;
				}
			});
		}
	}
	
	private void removeStartTipView() {
		if(mStartTipView != null){
			mStartTipView.remove();
			mStartTipView = null;
		}
	}


    /**
     * 隐藏默认的section提示
     */
    private void intFastScrollView(){
        //背景图片
        try {
            Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);
            Object o = f.get(lvTracks.getWrappedList());
            f = f.getType().getDeclaredField("mOverlayDrawable");
            f.setAccessible(true);
            Drawable drawable = getActivity().getResources().getDrawable(R.drawable.transparent);
            f.set(o, drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //提示方形大小
        try {
            Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);
            Object o = f.get(lvTracks.getWrappedList());
            f = f.getType().getDeclaredField("mOverlaySize");
            f.setAccessible(true);
            f.set(o, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //文字大小
        try {
            Paint mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(2);
            mPaint.setColor(getResources().getColor(R.color.green1));
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            Field f = AbsListView.class.getDeclaredField("mFastScroller");
            f.setAccessible(true);
            Object o = f.get(lvTracks.getWrappedList());
            f = f.getType().getDeclaredField("mPaint");
            f.setAccessible(true);
            f.set(o, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
	
	private class AllTrackListAdapter extends BaseAdapter implements StickyListHeadersAdapter, SectionIndexer {

        private final int[] COLORS = new int[] {
                R.color.holo_blue_dark,
                R.color.holo_yellow_dark,
                R.color.holo_green_dark,
                R.color.holo_purple_dark,
                R.color.holo_red_dark};

		private TrackListData tracks;
		
		public AllTrackListAdapter(TrackListData tracks) {
			super();
			updateDatas(tracks);
		}
		
		public void updateDatas(TrackListData tracks){
			this.tracks = tracks;
			if(tracks == null){
				tracks = TrackListData.createEmptyTrackListData();
			}
			notifyDataSetChanged();
		}

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.item_selection_all_tracks, null);
            }

            //?????不加会报空指针
            convertView.setLayoutParams(new StickyListHeadersListView.LayoutParams(
                    StickyListHeadersListView.LayoutParams.MATCH_PARENT,
                    StickyListHeadersListView.LayoutParams.WRAP_CONTENT));

            convertView.setBackgroundColor(getResources().getColor(COLORS[position%COLORS.length]));

            String sl = TrackUtil.getListSelection(tracks.tracks.get(position));
            ((TextView)convertView).setText(sl);

            return convertView;
        }

        /**
         * Remember that these have to be static, postion=1 should always return
         * the same Id that is.
         */
        @Override
        public long getHeaderId(int position) {
            int sections = tracks.sectionIndices.size();
            for(int i = sections - 1; i >= 0; i--){
                int curPos = tracks.sectionIndices.get(i);
                if(position >= curPos){
                    return curPos;
                }
            }
            return position;
        }

        @Override
        public int getCount() {
            return tracks.tracks.size();
        }

        @Override
        public Object getItem(int i) {
            return tracks.tracks.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder vh = null;
            if(convertView == null){
                convertView = LayoutInflater.from(getActivity())
                        .inflate(R.layout.itemview_all_tracks, null);
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            }else{
                vh = (ViewHolder) convertView.getTag();
            }

            Track data = (Track) getItem(position);
            vh.updateData(data, position);

            return convertView;
        }

        @Override
        public Object[] getSections() {
            return tracks.headLetters.toArray();
        }

        @Override
        public int getPositionForSection(int section) {
            if (section >= tracks.sectionIndices.size()) {
                section = tracks.sectionIndices.size() - 1;
            } else if (section < 0) {
                section = 0;
            }
            return tracks.sectionIndices.get(section);
        }

        @Override
        public int getSectionForPosition(int position) {
            for (int i = 0; i < tracks.sectionIndices.size(); i++) {
                if (position < tracks.sectionIndices.get(i)) {
                    return i - 1;
                }
            }
            return tracks.sectionIndices.size() - 1;
        }
    }
	
	private class ViewHolder{
		
		private TextView tvName, tvTime, tvDis, tvSceneryNum, tvPointNum;
		private View lyLine1, lyLine2, tvRecording;
		
		public ViewHolder(View convertView){
			tvName = (TextView) convertView.findViewById(R.id.tvName);
			tvTime = (TextView) convertView.findViewById(R.id.tvTime);
			tvDis = (TextView) convertView.findViewById(R.id.tvDis);
			tvSceneryNum = (TextView) convertView.findViewById(R.id.tvSceneryNum);
			tvPointNum = (TextView) convertView.findViewById(R.id.tvPointNum);
			lyLine1 = convertView.findViewById(R.id.lyLine1);
			lyLine2 = convertView.findViewById(R.id.lyLine2);
			tvRecording = convertView.findViewById(R.id.tvRecording);
		}
		
		public void updateData(Track track, int position){
			tvName.setText(track.getName());
			
			if(position == 0 && TrackManager.getInstance().isTrackRecording(track.getId())){
				//是正在记录的轨迹
				lyLine1.setVisibility(View.GONE);
				lyLine2.setVisibility(View.GONE);
				tvRecording.setVisibility(View.VISIBLE);
				
			}else{
				
				lyLine1.setVisibility(View.VISIBLE);
				lyLine2.setVisibility(View.VISIBLE);
				tvRecording.setVisibility(View.GONE);
				
				
				//不是正在记录的轨迹
				tvTime.setText(TimeUtil.getFormatedTimeHMS(track.getMovingTime()));
				
				tvDis.setText(StringUtils.getFormatDistance(StringUtils.decimalRoundToInt(track.getMovingDistance()), 
								2, 
								strM, 
								strKm));
				
				tvSceneryNum.setText("" + track.getSceneryNum());
				
				tvPointNum.setText("" + track.getPointsNum());
			}
			
		}
	}

}
