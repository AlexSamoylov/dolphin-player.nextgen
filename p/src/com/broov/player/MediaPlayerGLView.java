package com.broov.player;



import android.content.Context;
import android.opengl.GLSurfaceView;


public class MediaPlayerGLView extends GLSurfaceView{
	
	
	private GLRenderer renderer;
	public MediaPlayerGLView(Context context,MediaPlayer player) {
		super(context);
		renderer = new GLRenderer(player);
		setRenderer(renderer);
		// TODO Auto-generated constructor stub
	}
	
	public Context getGLContext(){
		return getContext();
	}
	

}
