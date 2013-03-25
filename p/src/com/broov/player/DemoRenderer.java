package com.broov.player;

import javax.microedition.khronos.opengles.GL10;

import javax.microedition.khronos.egl.EGLConfig;
import android.app.Activity;

import com.broov.commons.*;
import com.broov.filemanager.FileManager;

public class DemoRenderer extends GLSurfaceView_SDL.Renderer {

	//private static int queueSizeMinTable[] = { 50, 100, 150, 200, 256, 380, 500, 620, 750, 870, 1024, 1500, 2048 }; //0-12
	//private static int queueSizeMaxTable[] = { 50, 100, 150, 200, 256, 380, 500, 620, 750, 870, 1024, 1500, 2048, 3000, 4000, 5000, 8000, 10000, 12000, 14000, 15000, 20000 }; //0-21
	//private static int audioQueueSizeMaxTable[] = { 50, 100, 150, 200, 256, 380, 500, 620, 750, 870, 1024, 1500, 2048, 3000, 4000, 5000 };//0-15
	
	



	DemoRenderer(Activity _context)
	{
		System.out.println("DemoRenderer instance created:");
		//context = _context;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		System.out.println("Surface Created");
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) 
	{
		System.out.println("OnSurfaceChanged");
		//nativeResize(w, h);
	}

	
	public void onDrawFrame(GL10 gl) 
	{
		System.out.println("Inside on DrawFrame");
		//playFile();
		
	}
	
	
	public int swapBuffers() // Called from native code, returns 1 on success, 0 when GL context lost (user put app to background)
	{		
		return super.SwapBuffers() ? 1 : 0;
	}

	public void exitApp() 
	{
		System.out.println("Calling nativeDone");

		
	};

	public int exitFromNativePlayerView()
	{
		System.out.println("Inside exitFromNativePlayerView()");
		return 1;
	}

	

}
