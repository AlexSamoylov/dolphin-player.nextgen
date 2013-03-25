package com.broov.player;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

public class GLRenderer implements Renderer{
	
	MediaPlayer mMediaPlayer;
	int mScreenWidth = 0;
	int mScreenHeight = 0;
	
	GLRenderer(MediaPlayer player){
		mMediaPlayer = player;
	}

	
	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		if(mMediaPlayer.mResize){
			//gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			mMediaPlayer.setZoomParameters();
			mMediaPlayer.mResize=false;
		}
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mMediaPlayer.nativeVideoRefresh();
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO Auto-generated method stub
		mScreenWidth = width;
		mScreenHeight = height;
		Log.d("GLRenderer", "width ="+width+"height = "+height);
		if (mMediaPlayer != null)
			mMediaPlayer.nativeChangeSurface(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO Auto-generated method stub
		
		if (mMediaPlayer != null)
		{
			mMediaPlayer.nativeCreateSurface(new WeakReference<MediaPlayer>(mMediaPlayer));

		}
		
		
	}
	
	

}
