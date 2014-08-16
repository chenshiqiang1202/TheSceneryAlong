/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月21日 下午10:55:38   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.apiadapter.video;

import android.content.Context;
import android.util.AttributeSet;

import com.csq.thesceneryalong.ui.views.video.VideoView14;

public class MyVideoView14 extends VideoView14 implements IVideoView {

	public MyVideoView14(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MyVideoView14(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void _setVideoPath(String path) {
		// TODO Auto-generated method stub
		setDataSource(path);
	}

	@Override
	public void _start() {
		// TODO Auto-generated method stub
		play();
	}

	@Override
	public void _pause() {
		// TODO Auto-generated method stub
		pause();
	}

	@Override
	public void _stop() {
		// TODO Auto-generated method stub
		stop();
	}

	@Override
	public boolean _isPlaying() {
		// TODO Auto-generated method stub
		return isPlaying();
	}

	@Override
	public void _setVideoListener(VideoPlayerListener listener) {
		// TODO Auto-generated method stub
		setVideoListener(listener);
	}

	@Override
	public void _release() {
		// TODO Auto-generated method stub
		release();
	}
}
