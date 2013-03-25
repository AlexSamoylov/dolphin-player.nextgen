package com.broov.player;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class GestureListener extends GestureDetector.SimpleOnGestureListener{
	MediaPlayer mMediaPlayer;
	private static final float MAX_ZOOM = 2.0f; // 150 %
	private static final float MIN_ZOOM = 0.5f; // 50 %
	public GestureListener(MediaPlayer player){
		mMediaPlayer = player;
	}
	
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		// TODO Auto-generated method stub
		mMediaPlayer.mFitToScreen = true;
		mMediaPlayer.setZoomParameters();
		return super.onDoubleTap(e);
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return super.onSingleTapUp(e);
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		return super.onFling(e1, e2, velocityX, velocityY);
	}
	
	
	

}


