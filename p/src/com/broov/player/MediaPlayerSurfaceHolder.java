package com.broov.player;

import android.util.Log;
import android.view.SurfaceHolder;


public class MediaPlayerSurfaceHolder implements SurfaceHolder.Callback{

	int movieWidth = 0;
	int movieHeight = 0;
	int screenWidth = 0;
	int screenHeight = 0;
	MediaPlayer mPlayer;
	final String TAG = "MSurfaceView";
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		screenWidth = width;
		screenHeight = height;
		Log.d(TAG, "holder is surface view Changed "+holder.getSurface());
		mPlayer.nativeSurfaceViewInit(holder.getSurface());
		Log.d(TAG, "wodth == "+width+"height == "+height);
		mPlayer.nativeSetScreenSize(width, height);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "holder is "+holder.getSurface());
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Msurface view destroy"+holder.getSurface());
		mPlayer.nativeSurfaceViewDestroy();
	}
	
	public void setMediaPlayer(MediaPlayer player){
		this.mPlayer = player;
	}

	
	

}
