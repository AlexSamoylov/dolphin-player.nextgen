package com.broov.player;

import com.broov.commons.Globals;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class AudioService extends Service{

	private final IBinder mBinder = new MyBinder();
	public static boolean  mIsBound ;
	private final String TAG = "AudioService";
	private MediaPlayer mMediaPlayer = null;
	  
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	
	//---------------------------------------------------------------------------------------
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		IntentFilter filter = new IntentFilter();
        filter.addAction(Globals.EXITPLAYBACK);
        filter.addAction(Globals.PLAYPLAYBACK);
        filter.addAction(Globals.PAUSEPLAYBACK);
        filter.addAction(Globals.PLAYNEXT);
        filter.addAction(Globals.PLAYPREV);
        filter.addAction(Globals.STARTPLAYBACK);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(serviceReceiver, filter);
        
		super.onCreate();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Globals.mServiceBinded = false;
		mMediaPlayer.exitApp();
		return super.onUnbind(intent);
	}
	//---------------------------------------------------------------------------------------
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	//---------------------------------------------------------------------------------------
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		unregisterReceiver(serviceReceiver); 
		
		super.onDestroy();
	}
	
	//---------------------------------------------------------------------------------------
	public class MyBinder extends Binder {
	    MediaPlayer getService(Context _context) {
	    	
	    	Globals.LoadNativeLibraries();
	    	mMediaPlayer= MediaPlayer.getInstance(_context);
	    	
	      return mMediaPlayer;
	    }
	    
	   
	  }
	
	//---------------------------------------------------------------------------------------
	private BroadcastReceiver serviceReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context _context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				Log.d(TAG,intent.getAction());
				Globals.LoadNativeLibraries();
				if(intent.getAction().equalsIgnoreCase(Globals.EXITPLAYBACK)){
					mMediaPlayer.exitApp();
				}
				else if(intent.getAction().equalsIgnoreCase(Globals.PAUSEPLAYBACK)){
					Log.d(TAG, "Pause action detected");
					mMediaPlayer.pause();
				}else if(intent.getAction().equalsIgnoreCase(Globals.PLAYNEXT)){
					Log.d(TAG, "Play next action detected");
					mMediaPlayer.next();
				}
				else if(intent.getAction().equalsIgnoreCase(Globals.PLAYPLAYBACK)){
					Log.d(TAG, "Play action detected");
					mMediaPlayer.play();
				}
				else if (intent.getAction().equalsIgnoreCase(Globals.STARTPLAYBACK)){
					Log.d(TAG, "Start action detected");
					mMediaPlayer.initAudio();
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};

}
