/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年4月30日 下午4:37:19   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.views;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import at.markushi.ui.CircleButton;

import com.csq.thesceneryalong.R;
import com.csq.thesceneryalong.app.App;
import com.csq.thesceneryalong.db.Track;
import com.csq.thesceneryalong.logic.manager.TrackManager;
import com.csq.thesceneryalong.models.events.EventCurTrackStatusChanged;
import com.csq.thesceneryalong.models.events.EventNewTrackPoint;
import com.csq.thesceneryalong.models.events.EventRecordTimeChanged;
import com.csq.thesceneryalong.models.models.RecordStatus;
import com.csq.thesceneryalong.ui.activitys.TrackDetailMapActivity;
import com.csq.thesceneryalong.ui.activitys.base.BaseActionBarActivity;
import com.csq.thesceneryalong.utils.GsfUtil;
import com.csq.thesceneryalong.utils.StringUtils;
import com.csq.thesceneryalong.utils.TimeUtil;
import com.csq.thesceneryalong.utils.ToastUtil;

import de.greenrobot.event.EventBus;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.inmite.android.lib.dialogs.SimpleDialogFragment;

public class TrackCtrlView extends RelativeLayout implements OnClickListener{


	// ------------------------ Constants ------------------------
	public final int color_start = App.getResources().getColor(R.color.green10);
	public final int color_pause = App.getResources().getColor(R.color.yellow10);
	
	public final int color_stop_nor = App.getResources().getColor(R.color.red10);
	public final int color_stop_unable = App.getResources().getColor(android.R.color.darker_gray);

	public static final int requestCodeStopTrack = 44;
	
	// ------------------------- Fields --------------------------
	
	CircleButton btnStart;
	CircleButton btnStop;
	TextView tvMid1;
	TextView tvMid2;
	
	protected String strTrackCtrlDefaultLeft;
	protected String strTrackCtrlDefaultRight;
	protected String strTrackCtrlTotalTime;
	protected String strTrackCtrlTotalDis;
	protected String strKm;
	protected String strM;
	protected String strTrackStopConfirmTitle;
	protected String strTrackStopConfirmMsg;
	protected int yellow8;
	
	
	// ----------------------- Constructors ----------------------
	
	public TrackCtrlView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public TrackCtrlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}
	
	private void initView(Context context) {
		View v = LayoutInflater.from(context).inflate(R.layout.view_track_ctrl, 
				this, 
				true);
		
		btnStart = (CircleButton) v.findViewById(R.id.btnStart);
		btnStop = (CircleButton) v.findViewById(R.id.btnStop);
		tvMid1 = (TextView) v.findViewById(R.id.tvMid1);
		tvMid2 = (TextView) v.findViewById(R.id.tvMid2);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		
		strTrackCtrlDefaultLeft = getResources().getString(R.string.strTrackCtrlDefaultLeft);
		strTrackCtrlDefaultRight = getResources().getString(R.string.strTrackCtrlDefaultRight);
		strTrackCtrlTotalTime = getResources().getString(R.string.strTrackCtrlTotalTime);
		strTrackCtrlTotalDis = getResources().getString(R.string.strTrackCtrlTotalDis);
		strKm = getResources().getString(R.string.strKm);
		strM = getResources().getString(R.string.strM);
		strTrackStopConfirmTitle = getResources().getString(R.string.strTrackStopConfirmTitle);
		strTrackStopConfirmMsg = getResources().getString(R.string.strTrackStopConfirmMsg);
		yellow8= getResources().getColor(R.color.yellow8);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		if(!EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().register(this);
		}
		
		Track track = TrackManager.getInstance().getCurTrack();
		if(track != null){
			updateStatus(track.getRecordStatus());
		}else{
			updateStatus(RecordStatus.finished.getValue());
		}
		
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();

		if(EventBus.getDefault().isRegistered(this)){
			EventBus.getDefault().unregister(this);
		}
	}

	// --------------------- Methods public ----------------------
	
	private boolean isStartLaunchMap = false;
	
	public void stopTrack(){
		btnStop.setEnabled(false);
		
		Track track = TrackManager.getInstance().getCurTrack();
		if(track != null){
			TrackManager.getInstance().stopTrackAsyc();
		}
	}

	// --------------------- Methods private ---------------------
	
	/**
	 * @description: 轨迹记录状态改变
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 */
	public void onEventMainThread(EventCurTrackStatusChanged event){
		updateStatus(event.track.getRecordStatus());
	}
	
	/**
	 * @description: 新轨迹点
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param event
	 */
	public void onEventMainThread(EventNewTrackPoint event){
		updateTotalDistance();
	}
	
	/**
	 * @description: 计时改变
	 * @author: chenshiqiang E-mail:csqwyyx@163.com
	 * @param event
	 */
	public void onEventMainThread(EventRecordTimeChanged event){
		updateSimulatorTime(event.time);
	}
	
	private int curStatus = -1;
	private void updateStatus(int status){
		if(curStatus != status){
			if(status == RecordStatus.recording.getValue()){
				//正在记录
				btnStart.setColor(color_pause);
				btnStart.setImageResource(R.drawable.ic_track_pause);
				
				btnStop.setColor(color_stop_nor);
				btnStop.setEnabled(true);
				
				updateSimulatorTime(TrackManager.getInstance().getSimulatorTime());
				updateTotalDistance();
				
				if(isStartLaunchMap && GsfUtil.isGsfInstalled()){
					//第一次开始，启动地图界面
					TrackDetailMapActivity.launch(getContext(), 
							TrackManager.getInstance().getCurTrack().getId());
					isStartLaunchMap = false;
				}
				
			}else if(status == RecordStatus.paused.getValue()){
				//暂停了
				btnStart.setColor(color_start);
				btnStart.setImageResource(R.drawable.ic_track_start);
				
				btnStop.setColor(color_stop_nor);
				btnStop.setEnabled(true);
				
				updateSimulatorTime(TrackManager.getInstance().getSimulatorTime());
				updateTotalDistance();
				
			}else{
				//停止了
				btnStart.setColor(color_start);
				btnStart.setImageResource(R.drawable.ic_track_start);
				
				btnStop.setColor(color_stop_unable);
				btnStop.setEnabled(false);
				
				tvMid1.setText(strTrackCtrlDefaultLeft);
				tvMid2.setText(strTrackCtrlDefaultRight);
			}
			curStatus = status;
		}
		
		btnStart.setEnabled(true);
	}
	
	private void updateSimulatorTime(long time){
		String strTime = strTrackCtrlTotalTime + TimeUtil.getFormatedTimeHMS(time);
		SpannableString span = new SpannableString(strTime);
		span.setSpan(new ForegroundColorSpan(yellow8), 
				0, 
				strTrackCtrlTotalDis.length(), 
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tvMid1.setText(span);
	}
	
	private void updateTotalDistance(){
		int dis = 0;
		Track curTrack = TrackManager.getInstance().getCurTrack();
		if(curTrack != null){
			double s = curTrack.getMovingDistance();
			dis = (int) s;
		}
		
		String strDis = strTrackCtrlTotalDis 
				+ StringUtils.getFormatDistance(dis, 2, strM, strKm);
		SpannableString span = new SpannableString(strDis);
		span.setSpan(new ForegroundColorSpan(yellow8), 
				0, 
				strTrackCtrlTotalDis.length(), 
				Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		tvMid2.setText(span);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnStart:
			if(!GsfUtil.isGsfInstalled()){
				//没有谷歌服务框架
				ToastUtil.showToastInfo((Activity)getContext(), 
						R.string.strErrorNoGsf, 
						Style.ALERT, 
						false);
				return;
			}
			
			btnStart.setEnabled(false);
			
			Track track = TrackManager.getInstance().getCurTrack();
			if(track != null){
				if(track.getRecordStatus() == RecordStatus.recording.getValue()){
					TrackManager.getInstance().pauseTrackAsyc();
				}else if(track.getRecordStatus() == RecordStatus.paused.getValue()){
					TrackManager.getInstance().resumeTrackAsyc();;
				}else{
					TrackManager.getInstance().startTrackAsyc();
				}
			}else{
				isStartLaunchMap = true;
				TrackManager.getInstance().startTrackAsyc();
			}
			break;

		case R.id.btnStop:
			Activity act = (Activity) getContext();
			if(act instanceof BaseActionBarActivity){
				SimpleDialogFragment.createBuilder(act, ((BaseActionBarActivity)act).getSupportFragmentManager())
				.setTitle(strTrackStopConfirmTitle)
				.setMessage(strTrackStopConfirmMsg)
				.setPositiveButtonText(R.string.yes)
				.setNegativeButtonText(R.string.no)
				.setRequestCode(requestCodeStopTrack)
				.show();
			}else{
				stopTrack();
			}
			break;
		default:
			break;
		}
	}
	
	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
