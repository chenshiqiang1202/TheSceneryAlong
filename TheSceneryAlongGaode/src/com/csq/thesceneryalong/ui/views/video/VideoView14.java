//
// VideoView
//
//
//

package com.csq.thesceneryalong.ui.views.video;

import java.io.IOException;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import com.csq.thesceneryalong.utils.DebugLog;
import com.csq.thesceneryalong.utils.apiadapter.video.VideoPlayerListener;


/**
 * api >= 14, 播放视频使用VideoView14（TextureView）
 * @author csq
 *
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class VideoView14 extends TextureView implements TextureView.SurfaceTextureListener {

    private MediaPlayer mMediaPlayer;

//    private float mVideoHeight;
//    private float mVideoWidth;

    private boolean mIsDataSourceSet;
    private boolean mIsViewAvailable;
    private boolean mIsVideoPrepared;
    private boolean mIsPlayCalled;

    private State mState;

    public enum State {
        UNINITIALIZED, PLAY, STOP, PAUSE, END
    }

    public VideoView14(Context context) {
        super(context);
        initView();
    }

    public VideoView14(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoView14(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }
    
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer.setSurface(surface);
        
        mIsViewAvailable = true;
        if (mIsDataSourceSet && mIsPlayCalled && mIsVideoPrepared) {
        	DebugLog.d("View is available and play() was called.");
            play();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    private int lastProgress = 0;
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    	int dur = mMediaPlayer.getDuration();
    	if(dur > 0 && mState != State.END){
    		int progress = mMediaPlayer.getCurrentPosition() * 100 / dur;
    		if(progress != lastProgress){
    			if(mListener != null){
    				mListener.onProgressChanged(progress);
    				lastProgress = progress;
    			}
    		}
    	}
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	// TODO Auto-generated method stub
    	if(mMediaPlayer == null){
    		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    		return;
    	}
    	
    	float mVideoWidth = mMediaPlayer.getVideoWidth();
    	float mVideoHeight = mMediaPlayer.getVideoHeight();
    	if(mVideoWidth > 0 && mVideoHeight > 0){
    		int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
    		int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
    		float scale = 1.0f;
    		if(mVideoWidth > 0 && mVideoHeight > 0 
            		&& viewWidth > 0 && viewHeight > 0){
            	if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
                	//2边都超出,scale<1
                	scale = Math.min(viewWidth / mVideoWidth, viewHeight / mVideoHeight);
                	
                }else if(mVideoWidth < viewWidth && mVideoHeight < viewHeight){
                	//2边都小于,scale>1
                	scale = Math.min(viewWidth / mVideoWidth, viewHeight / mVideoHeight);
                	
                }else if(mVideoWidth >= viewWidth){
                	//宽度超出,scale<1
                	scale = viewWidth / mVideoWidth;
                	
                }else{
                	//高度超出,scale<1
                	scale = viewHeight / mVideoHeight;
                }
            }
    		int mx = (int) (mVideoWidth * scale);
        	int my = (int) (mVideoHeight * scale);
    		setMeasuredDimension(mx, my);
    		
    	}else{
    		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	}
    	
    	//默认占满父空间
    	DebugLog.d("VideoView14  getMeasuredWidth = " + getMeasuredWidth());
    	DebugLog.d("VideoView14  getMeasuredHeight = " + getMeasuredHeight());
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	// TODO Auto-generated method stub
    	super.onSizeChanged(w, h, oldw, oldh);
    	if(w != oldw || h != oldh){
    		if(mListener != null){
        		mListener.onSizeChanged(getMeasuredWidth(), getMeasuredHeight());
        	}
    	}
    }
    
    private void initView() {
        initPlayer();
        setSurfaceTextureListener(this);
        
        setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mIsViewAvailable && mIsDataSourceSet && mIsVideoPrepared){
					if(isPlaying()){
						pause();
					}else{
						play();
					}
				}
			}
		});
    }

    private void initPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }
        mIsVideoPrepared = false;
        mIsPlayCalled = false;
        mState = State.UNINITIALIZED;
    }
    
    private void prepare() {
        try {
            mMediaPlayer.setOnVideoSizeChangedListener(
                    new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            //mVideoWidth = width;
                            //mVideoHeight = height;
                            
                            requestLayout();
                        }
                    }
            );
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mState = State.END;
                    DebugLog.d("Video has ended.");

                    if (mListener != null) {
                        mListener.onVideoEnd();
                    }
                }
            });

            // don't forget to call MediaPlayer.prepareAsync() method when you use constructor for
            // creating MediaPlayer
            mMediaPlayer.prepareAsync();

            // Play video when the media source is ready for playback.
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mIsVideoPrepared = true;
                    
                    if (mIsPlayCalled){
                    	if(mIsViewAvailable){
                    		DebugLog.d("Player is prepared and play() was called.");
                            play();
                    	}
                    }else{
                    	//没有开始播放，也跳到第一帧，显示画面
                    	if(mIsViewAvailable){
                    		seekTo(1);
                    	}
                    }

                    if (mListener != null) {
                        mListener.onVideoPrepared();
                    }
                }
            });
            
        } catch (IllegalArgumentException e) {
        	DebugLog.d( e.getMessage());
        } catch (SecurityException e) {
        	DebugLog.d(e.getMessage());
        } catch (IllegalStateException e) {
        	DebugLog.d(e.toString());
        }
    }
    
    /*private void updateTextureViewSize() {
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        //scale相对于View来说，scale = 计算得到的视频大小/viewSize
        float scale = 1.0f;
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        
        if(mVideoWidth > 0 && mVideoHeight > 0 
        		&& viewWidth > 0 && viewHeight > 0){
        	if (mVideoWidth > viewWidth && mVideoHeight > viewHeight) {
            	//2边都超出,scale<1
            	scale = Math.min(viewWidth / mVideoWidth, viewHeight / mVideoHeight);
            	
            }else if(mVideoWidth < viewWidth && mVideoHeight < viewHeight){
            	//2边都小于,scale>1
            	scale = Math.min(viewWidth / mVideoWidth, viewHeight / mVideoHeight);
            	
            }else if(mVideoWidth >= viewWidth){
            	//宽度超出,scale<1
            	scale = viewWidth / mVideoWidth;
            	
            }else{
            	//高度超出,scale<1
            	scale = viewHeight / mVideoHeight;
            }
            scaleX = mVideoWidth * scale / viewWidth;
        	scaleY = mVideoHeight * scale / viewHeight;
        }

        // Calculate pivot points, in our case crop from center
        int pivotPointX;
        int pivotPointY;

        pivotPointX = (int) (viewWidth / 2);
        pivotPointY = (int) (viewHeight / 2);

        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY, pivotPointX, pivotPointY);

        setTransform(matrix);
    }*/
    
    
    
    

    /**
     * @see android.media.MediaPlayer#setDataSource(String)
     */
    public void setDataSource(String path) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(path);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
        	DebugLog.d(e.getMessage());
        }
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(android.content.Context, android.net.Uri)
     */
    public void setDataSource(Context context, Uri uri) {
        initPlayer();

        try {
            mMediaPlayer.setDataSource(context, uri);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
        	DebugLog.d(e.getMessage());
        }
    }

    /**
     * @see android.media.MediaPlayer#setDataSource(java.io.FileDescriptor)
     */
    public void setDataSource(AssetFileDescriptor afd) {
        initPlayer();

        try {
            long startOffset = afd.getStartOffset();
            long length = afd.getLength();
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), startOffset, length);
            mIsDataSourceSet = true;
            prepare();
        } catch (IOException e) {
        	DebugLog.d(e.getMessage());
        }
    }

    

    /**
     * Play or resume video. Video will be played as soon as view is available and media player is
     * prepared.
     *
     * If video is stopped or ended and play() method was called, video will start over.
     */
    public void play() {
        if (!mIsDataSourceSet) {
        	DebugLog.d("play() was called but data source was not set.");
            return;
        }

        mIsPlayCalled = true;

        if (!mIsVideoPrepared) {
        	DebugLog.d("play() was called but video is not prepared yet, waiting.");
            return;
        }

        if (!mIsViewAvailable) {
        	DebugLog.d("play() was called but view is not available yet, waiting.");
            return;
        }

        if (mState == State.PLAY) {
        	DebugLog.d("play() was called but video is already playing.");
            return;
        }

        if (mState == State.PAUSE) {
        	DebugLog.d("play() was called but video is paused, resuming.");
            mState = State.PLAY;
            mMediaPlayer.start();
            if(mListener != null){
            	mListener.onVideoPlayed();
            }
            return;
        }

        if (mState == State.END || mState == State.STOP) {
        	DebugLog.d("play() was called but video already ended, starting over.");
            mState = State.PLAY;
            mMediaPlayer.seekTo(0);
            mMediaPlayer.start();
            if(mListener != null){
            	mListener.onVideoPlayed();
            }
            return;
        }

        mState = State.PLAY;
        mMediaPlayer.start();
        if(mListener != null){
        	mListener.onVideoPlayed();
        }
    }
    
    public boolean isPlaying(){
    	if(mMediaPlayer != null){
    		try {
    			return mMediaPlayer.isPlaying();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
    	}
    	return false;
    }

    /**
     * Pause video. If video is already paused, stopped or ended nothing will happen.
     */
    public void pause() {
        if (mState == State.PAUSE) {
        	DebugLog.d("pause() was called but video already paused.");
            return;
        }

        if (mState == State.STOP) {
        	DebugLog.d("pause() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
        	DebugLog.d("pause() was called but video already ended.");
            return;
        }

        mState = State.PAUSE;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
        if(mListener != null){
        	mListener.onVideoPaused();
        }
    }

    /**
     * Stop video (pause and seek to beginning). If video is already stopped or ended nothing will
     * happen.
     */
    public void stop() {
        if (mState == State.STOP) {
        	DebugLog.d("stop() was called but video already stopped.");
            return;
        }

        if (mState == State.END) {
        	DebugLog.d("stop() was called but video already ended.");
            return;
        }

        mState = State.STOP;
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
        }
        if(mListener != null){
        	mListener.onVideoStoped();
        }
    }
    
    public void release() {
    	if (mMediaPlayer != null) {
    		mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mIsVideoPrepared = false;
        mIsPlayCalled = false;
        mState = State.UNINITIALIZED;
    }

    /**
     * @see android.media.MediaPlayer#setLooping(boolean)
     */
    public void setLooping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    /**
     * @see android.media.MediaPlayer#seekTo(int)
     */
    public void seekTo(int milliseconds) {
        mMediaPlayer.seekTo(milliseconds);
    }

    /**
     * @see android.media.MediaPlayer#getDuration()
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    private VideoPlayerListener mListener;

    /**
     * Listener trigger 'onVideoPrepared' and `onVideoEnd` events
     */
    public void setVideoListener(VideoPlayerListener listener) {
        mListener = listener;
    }

}
