/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月25日 下午10:29:41   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.utils.StringUtils;
import com.csq.thesceneryalong.utils.TimeUtil;
import com.csq.thesceneryalong.utils.dbmodel.TrackUtil;

public class TrackDetailDrawerView extends LinearLayout {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	protected TextView tvName;
	protected TextView tvPath;
	protected TextView tvDescription;
	protected TextView tvTime;
	protected TextView tvDis;
	protected TextView tvSceneryNum;
	protected TextView tvPointNum;
	
	protected String strNone;
	protected String strM;
	protected String strKm;
	
	protected Track track;

	// ----------------------- Constructors ----------------------
	
	public TrackDetailDrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public TrackDetailDrawerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	private void initView(Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.view_track_detail_drawer, 
				this, 
				true);
		
		strNone = getResources().getString(R.string.strNone);
		strM = getResources().getString(R.string.strM);
		strKm = getResources().getString(R.string.strKm);
		
		tvName = (TextView) v.findViewById(R.id.tvName);
		tvPath = (TextView) v.findViewById(R.id.tvPath);
		tvDescription = (TextView) v.findViewById(R.id.tvDescription);
		tvTime = (TextView) v.findViewById(R.id.tvTime);
		tvDis = (TextView) v.findViewById(R.id.tvDis);
		tvSceneryNum = (TextView) v.findViewById(R.id.tvSceneryNum);
		tvPointNum = (TextView) v.findViewById(R.id.tvPointNum);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		if(track != null){
			updateView();
		}
	}

	// --------------------- Methods public ----------------------
	
	public void updateTrack(Track track){
		this.track = track;
		if(tvName != null){
			updateView();
		}
	}
	
	private void updateView(){
		tvName.setText(track.getName());
		tvPath.setText(TrackUtil.getTrackPath(track));
		tvDescription.setText(StringUtils.avoidNull(track.getDescription(), strNone));
		tvTime.setText(TimeUtil.getFormatedTimeHMS(track.getMovingTime()));
		double dis = track.getMovingDistance();
		tvDis.setText(StringUtils.getFormatDistance((int)dis, 
				2, strM, strKm));
		tvSceneryNum.setText("" + track.getSceneryNum());
		tvPointNum.setText("" + track.getPointsNum());
		requestLayout();
	}

	// --------------------- Methods private ---------------------

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
