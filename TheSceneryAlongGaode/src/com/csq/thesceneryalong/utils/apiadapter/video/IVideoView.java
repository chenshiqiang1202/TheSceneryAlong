/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月21日 下午10:49:36   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.apiadapter.video;
public interface IVideoView {
	
	public void _setVideoPath(String path);
	
	public void _start();
	
	public void _pause();
	
	public void _stop();
	
	public boolean _isPlaying();
	
	public void _setVideoListener(VideoPlayerListener listener);
	
	public void _release();
	
}
