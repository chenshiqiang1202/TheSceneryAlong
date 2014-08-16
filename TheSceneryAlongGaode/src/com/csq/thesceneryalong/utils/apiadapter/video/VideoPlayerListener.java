/**
 * @description: 
 * @author chenshiqiang E-mail:csqwyyx@163.com
 * @date 2014年5月24日 上午1:32:59   
 * @version 1.0   
 */
package com.csq.thesceneryalong.utils.apiadapter.video;
public interface VideoPlayerListener {

	public void onVideoPrepared();
    
    public void onVideoPaused();
    
    public void onVideoPlayed();
    
    public void onVideoStoped();

    public void onVideoEnd();
    
    public void onProgressChanged(int progress);
    
    public void onSizeChanged(int w, int h);
}
