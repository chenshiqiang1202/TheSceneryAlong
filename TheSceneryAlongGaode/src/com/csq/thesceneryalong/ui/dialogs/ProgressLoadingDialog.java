/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月18日 下午4:12:39   
 * @version 1.0   
 */
package com.csq.thesceneryalong.ui.dialogs;

import android.content.Context;
import android.widget.TextView;

import com.csq.thesceneryalong.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

public class ProgressLoadingDialog extends BaseFullScreenDialog {

	// ------------------------ Constants ------------------------

	// ------------------------- Fields --------------------------
	
	private HoloCircularProgressBar progressBar;
	protected boolean mAnimationHasEnded = false;
	private ObjectAnimator mProgressBarAnimator;
	
	private TextView tvProgress, tvMsg;

	// ----------------------- Constructors ----------------------
	
	public ProgressLoadingDialog(Context context, String msg) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(msg);
	}
	
	private void initView(String msg){
		setContentView(R.layout.dialog_progress_loading);
		
		progressBar = (HoloCircularProgressBar) findViewById(R.id.holoCircularProgressBar);
		tvProgress = (TextView) findViewById(R.id.tvProgress);
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		
		tvProgress.setText("");
		tvMsg.setText(msg);
	}

	// -------- Methods for/from SuperClass/Interfaces -----------
	
	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		animate(progressBar, new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
				animation.end();
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				if (!mAnimationHasEnded) {
					animate(progressBar, this);
				} else {
					mAnimationHasEnded = false;
				}
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
	}
	
	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		
		mAnimationHasEnded = true;
		mProgressBarAnimator.cancel();
	}

	// --------------------- Methods public ----------------------
	
	public void updataMsg(String msg){
		tvMsg.setText(msg);
	}
	
	public void updataProgress(int progress){
		tvProgress.setText(progress + " %");
	}

	// --------------------- Methods private ---------------------
	
	/**
	 * Animate.
	 * 
	 * @param progressBar
	 *            the progress bar
	 * @param listener
	 *            the listener
	 */
	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener) {
		final float progress = (float) (Math.random() * 2);
		int duration = 3000;
		animate(progressBar, listener, progress, duration);
	}

	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener,
			final float progress, final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
		mProgressBarAnimator.setDuration(duration);

		mProgressBarAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBar.setMarkerProgress(progress);
		mProgressBarAnimator.start();
	}

	// --------------------- Getter & Setter ---------------------

	// --------------- Inner and Anonymous Classes ---------------
}
