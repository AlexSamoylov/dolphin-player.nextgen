package com.broov.player;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.broov.commons.Globals;
import com.broov.filemanager.FileManager;
import com.broov.playerM.*;
import com.broov.utils.SystemUiHider;
import com.broov.utils.Utils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.os.PowerManager;
import android.provider.ContactsContract.CommonDataKinds.Event;

public class VideoPlayer extends Activity  implements OnBufferingListener, OnDurationChangedListener, OnPreparedListener, OnCompletionListener, OnSizeChangedListener,Runnable{

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		seekBarUpdater.stopIt();
		mMediaPlayer.exitApp();
	}

	@Override
	protected void onStop() 
	{
		if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}

		super.onStop();
	}


	PhoneStateListener phoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				//Incoming call: Pause music
				System.out.println("Video call state ringing");
				//Pause the video, only if video is playing 
				if ((mMediaPlayer != null) && (!paused)) { 
					System.out.println("Triggered");
					mMediaPlayer.play();
				}

				//seekBarUpdater = new Updater();
				//mHandler.postDelayed(seekBarUpdater, 500);
			} else if(state == TelephonyManager.CALL_STATE_IDLE) {
				//Not in call: Play music
				System.out.println("Video call state idle");
				//do not resume, if already paused by User  
				if ((mMediaPlayer != null) && (!paused)) {
					System.out.println("Triggered");
					mMediaPlayer.pause();
				}
				//seekBarUpdater.stopIt();
			} else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
				//A call is dialing, active or on hold
				System.out.println("Video call state offhook");
				
				if ((mMediaPlayer != null) && (!paused)) {
					System.out.println("Triggered");
					mMediaPlayer.play();
				}
				//seekBarUpdater = new Updater();
				//mHandler.postDelayed(seekBarUpdater, 500);
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	};


	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

 		System.out.println("VideoPlayer onCreate");
 		//reset the previous state
		paused = false;
		mMediaPlayer = MediaPlayer.getInstance(this);
		mScaleListener = new ScaleListener();
		mScaleDetector = new ScaleGestureDetector(getBaseContext(), mScaleListener);
		
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(
			    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
			    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		*/
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//		 Window win = getWindow();
//	     WindowManager.LayoutParams winParams = win.getAttributes();
//	     winParams.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
//	     win.setAttributes(winParams);

	    setContentView(R.layout.video_player);
	    playerView = (SurfaceView) findViewById(R.id.view_play_screen);
	    //playerView.getLayoutParams().height= 300;
	    //playerView.getLayoutParams().width = 400;
	    //playerView = (GLSurfaceView) findViewById(R.id.view_play_screen);
	    //GLRenderer renderer = new GLRenderer(mMediaPlayer);
	    //playerView.setEGLContextClientVersion(2); // This is the important line
		//playerView.setRenderer(renderer);
	    //playerView.setOnClickListener(mGoneListener);
	    //playerView.setFocusable(true);
	    //playerView.requestFocus();
		//playerView.setScaleX(0.5f);
		//playerView.setScaleY(0.5f);
	    DisplayMetrics dm = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		SurfaceHolder holder = playerView.getHolder();
		MediaPlayerSurfaceHolder sHolder = new MediaPlayerSurfaceHolder();
		
		if(MediaPlayerSettings.rgb565 == 1){
			holder.setFormat(PixelFormat.RGB_565);
		}else{
			holder.setFormat(PixelFormat.RGBA_8888);
		}
		sHolder.setMediaPlayer(mMediaPlayer);
		
		holder.addCallback(sHolder);
		
		mGestureListener = new GestureListener(mMediaPlayer);
		mGestureDetector = new GestureDetector(getBaseContext(),mGestureListener);
		mDialog			 = new ProgressDialog(this);
		mDialog.setCancelable(true);
		mDialog.setMax(100);
		mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mDialog.setMessage(getString(R.string.stringbuffering));
		mDialog.setOnCancelListener(new android.content.DialogInterface.OnCancelListener(){

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				mMediaPlayer.stop();
			}
			
		});
		//Utils.hideSystemUi(getWindow().getDecorView());
		//Utils.hideSystemUi(this.findViewById(R.id.glsurfaceview).getRootView());
		
		i = getIntent();

		if (i!= null) {
			Uri uri = i.getData();
			if (uri!= null) {
				openfileFromBrowser = uri.getEncodedPath();	

				//Change from 1.6
				String decodedOpenFileFromBrowser = null;
				try {
					decodedOpenFileFromBrowser = URLDecoder.decode(openfileFromBrowser,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				if (decodedOpenFileFromBrowser != null)
				{
					openfileFromBrowser = decodedOpenFileFromBrowser; 
				}
			}	
		}
		System.out.println("openfileFromBrowser:"+openfileFromBrowser);

		if(FileManager.isVideoFile(openfileFromBrowser)){
			Globals.setFileName(openfileFromBrowser);	
			System.out.println("================openfileFromBrowser:"+openfileFromBrowser+"=============");			

		}	
		else {
			Bundle extras = i.getExtras();
			if (extras != null) {
				String tmpFileName = extras.getString("videofilename");

				if (FileManager.isVideoFile(tmpFileName)) {
					Globals.setFileName(tmpFileName);
					System.out.println("================extras.getString videofilename:"+tmpFileName+"============");
				}
			}
		}

		System.out.println("=======================Playing filename:" + Globals.fileName);

		mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		System.out.println("TelephoneManager : "+mgr);
		if(mgr != null) {
			System.out.println("telephonemanager start");
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
			
		}

		// Find the views whose visibility will change
		mSeekBar = (SeekBar) findViewById(R.id.progressbar);

		currentTime = (TextView) findViewById(R.id.currenttime);     
		totalTime = (TextView) findViewById(R.id.totaltime);        
		controlPanel = (TableLayout) findViewById(R.id.controlPanel);
		controlPanel.getBackground().setAlpha(85);

		imgPlay = findViewById(R.id.img_vp_play);
		imgForward = findViewById(R.id.img_vp_forward);
		imgBackward = findViewById(R.id.img_vp_backward);
		imgAspectRatio = findViewById(R.id.fs_shadow);

		//trScrolledTime = findViewById(R.id.trscrolledtime);
		//scrolledtime = (TextView) findViewById(R.id.scrolledtime);

		//trScrolledTime.setVisibility(View.INVISIBLE);
		//trScrolledTime.setVisibility(View.GONE);
		mHideContainer = findViewById(R.id.hidecontainer);
		//mHideContainer.setOnClickListener(mVisibleListener);
		
		mControlPanelContainer = findViewById(R.id.controlPanel);
		mControlPanelContainer.setOnClickListener(mControlPanelListener);

		imgAspectRatio.setOnTouchListener(imgAspectRatioTouchListener);
		imgPlay.setOnTouchListener(imgPlayTouchListener);
		imgForward.setOnTouchListener(imgForwardTouchListener);
		imgBackward.setOnTouchListener(imgBackwardTouchListener);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		if(!mDialog.isShowing()){
			mDialog.show();
		}
		//System.out.println("Start - InitSDL()");
		setListeners();
		/** FullScreen **/
		mSystemUiHider = SystemUiHider.getInstance(this, playerView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
				.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
					// Cached values.
					int mControlsHeight;
					int mShortAnimTime;

					@Override
					@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
					public void onVisibilityChange(boolean visible) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
							// If the ViewPropertyAnimator API is available
							// (Honeycomb MR2 and later), use it to animate the
							// in-layout UI controls at the bottom of the
							// screen.
							if (mControlsHeight == 0) {
								mControlsHeight = mHideContainer.getHeight();
							}
							if (mShortAnimTime == 0) {
								mShortAnimTime = getResources().getInteger(
										android.R.integer.config_shortAnimTime);
							}
							mHideContainer
									.animate()
									.translationY(visible ? 0 : mControlsHeight)
									.setDuration(mShortAnimTime);
						} else {
							// If the ViewPropertyAnimator APIs aren't
							// available, simply show or hide the in-layout UI
							// controls.
							try{
								
							if(visible)
								restartUpdater();
							else
								seekBarUpdater.stopIt();
							
							mHideContainer.setVisibility(visible ? View.VISIBLE
									: View.GONE);
							}catch(Exception e){
								
							}
						}

						if (visible && AUTO_HIDE) {
							// Schedule a hide().
							delayedHide(AUTO_HIDE_DELAY_MILLIS);
						}
					}
				});
		if(MediaPlayer.isPlaying()){
			
			try {
				mMediaPlayer.exitApp();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Thread videoThread = new Thread(this);
		videoThread.start();
		
		
	}
	
	//-------------------------------------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * 
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		initSDL();
	}
	
	//-------------------------------------------------------------------------------------
	public void setListeners(){
		mMediaPlayer.setHolder(playerView);
		//player.setUrl(fileUrl);
		//mMediaPlayer.setScreenOnWhilePlaying(true);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setOnBufferingListener(this);
		mMediaPlayer.setOnDurationChangedListener(this);
		mMediaPlayer.setOnSizeChangedListener(this);
	}
	
	//-------------------------------------------------------------------------------------
	public void initSDL()
	{
		//Wake lock code
		try {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			//wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, Globals.ApplicationName);
			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, Globals.ApplicationName);
			
			wakeLock.acquire();
		} catch (Exception e) {
			System.out.println("Inside wake lock exception"+e.toString());
		}
		System.out.println("Acquired wakeup lock");
		//GLSurfaceView_SDL surfaceView = (GLSurfaceView_SDL) findViewById(R.id.glsurfaceview);
		//mMediaPlayer.setSurface(surfaceView);
		System.out.println("got the surface view:");
		//surfaceView.setOnClickListener(mGoneListener);
		//surfaceView.setFocusable(true);
		//surfaceView.requestFocus();
		mMediaPlayer.initSDL();
		
		mHandler.postDelayed(seekBarUpdater, 100);
		mMediaPlayer.playFile(VideoPlayer.this);
		//hideTimer = new UITimer(mHandler, mAutoHide, 5000,true);
		//hideTimer.start();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				totalDuration = mMediaPlayer.getTotalDuration();
				totalTime.setText(Utils.formatTime(totalDuration));
			}
		});
		
		
	}
	

	public void restartUpdater() {
		seekBarUpdater.stopIt();
		seekBarUpdater = new Updater();
		mHandler.postDelayed(seekBarUpdater, 100);
	}

	private class Updater implements Runnable {
		private boolean stop;

		public void stopIt() {
			System.out.println("Stopped updater");
			stop = true;
		}

		@Override
		public void run() {
			//Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			if(currentTime != null && mMediaPlayer != null) {
				long playedDuration = mMediaPlayer.getElapsedDuration();				
				currentTime.setText(Utils.formatTime(playedDuration));
				totalDuration = mMediaPlayer.getTotalDuration();
				if(totalDuration != 0) {
					int progress = (int)((1000 * playedDuration) / totalDuration);
					mSeekBar.setProgress(progress);							
					totalTime.setText(Utils.formatTime(totalDuration));
				}						
				if (mMediaPlayer.getFileInfoUpdated()) {
					//if (Globals.fileName != null) {
					//	videoInfo.setText(FileManager.getFileName(Globals.fileName));
					//}
					mMediaPlayer.setFileInfoUpdated(false);
				}
			}

			if(!stop) {
				if (Globals.fileName != null) {
					//Restart the updater if file is still playing
					mHandler.postDelayed(seekBarUpdater, 500);
				}
			}
		}
	}



	OnClickListener mGoneListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			System.out.println("Inside mGone Click");
			if ((mHideContainer.getVisibility() == View.INVISIBLE) ||
					(mHideContainer.getVisibility() == View.GONE))
			{
				mHideContainer.setVisibility(View.VISIBLE);
				restartUpdater();
			}	else 
			{
				mHideContainer.setVisibility(View.INVISIBLE);
				seekBarUpdater.stopIt();
			}
		}
	};



	OnClickListener mVisibleListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			if ((mHideContainer.getVisibility() == View.GONE) ||
					(mHideContainer.getVisibility() == View.INVISIBLE)) 
			{
				mHideContainer.setVisibility(View.VISIBLE);
				
			} else 
			{
				mHideContainer.setVisibility(View.INVISIBLE);
				seekBarUpdater.stopIt();
			}
		}
	};
	
	OnClickListener mControlPanelListener = new OnClickListener() 
	{
		public void onClick(View v) 
		{
			//Do not hide the control panel par, when clicked
			//System.out.println("CONTROL PANEL  LISTENER ONCLICK ");
		}
	};

	OnTouchListener imgAspectRatioTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//Do nothing for now
			}
			else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (current_aspect_ratio_type == 3) {
					img.setImageResource(R.drawable.fs_shadow_4_3);
					mMediaPlayer.setAspectRatio(0);
					current_aspect_ratio_type = 1;
				} else if (current_aspect_ratio_type == 1) {
					img.setImageResource(R.drawable.fs_shadow);
					mMediaPlayer.setAspectRatio(3);
					current_aspect_ratio_type = 2;
				} else if (current_aspect_ratio_type == 2) {
					img.setImageResource(R.drawable.fs_shadow_16_9);
					mMediaPlayer.setAspectRatio(2);
					current_aspect_ratio_type = 3;

				}
			}						
			//resetAutoHider();
			return true;
		}
	};

	OnTouchListener imgPlayTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;				

			if (event.getAction() == MotionEvent.ACTION_DOWN ) {	
				System.out.println("Down paused:" + paused);
				if(paused) {
					img.setImageResource(R.drawable.vp_play);
				}
				else {
					img.setImageResource(R.drawable.vp_pause);
				}
			}
			else if (event.getAction() == MotionEvent.ACTION_UP ) {
				System.out.println("Up paused:" + paused);		  
				System.out.println("Total:" + mMediaPlayer.getTotalDuration() + "---Current:" + mMediaPlayer.getElapsedDuration());
				if(paused) {
					mMediaPlayer.pause();
					seekBarUpdater = new Updater();
					mHandler.postDelayed(seekBarUpdater, 500);
					img.setImageResource(R.drawable.vp_pause_shadow);
				}
				else {
					mMediaPlayer.play();
					seekBarUpdater.stopIt();						
					img.setImageResource(R.drawable.vp_play_shadow);
				}		        	
				paused = !paused;
			}				
			//resetAutoHider();
			return true;
		}
	};

	OnTouchListener imgForwardTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {		            		            
				img.setImageResource(R.drawable.vp_forward_glow);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP ) {		        	
				img.setImageResource(R.drawable.vp_forward);										
				mMediaPlayer.forward();
			}							
			//resetAutoHider();
			return true;
		}
	};

	OnTouchListener imgBackwardTouchListener = new OnTouchListener() {			
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView img = (ImageView) v;
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {		            		            
				img.setImageResource(R.drawable.vp_backward_glow_80x60);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP ) {		        	
				img.setImageResource(R.drawable.vp_backward);										
				mMediaPlayer.rewind();
			}						
			//resetAutoHider();
			return true;
		}
	};

	OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			//System.out.println("Should be visible:" + trScrolledTime.getVisibility());
			//trScrolledTime.setVisibility(View.GONE);
			//trScrolledTime.setVisibility(View.INVISIBLE);
			//System.out.println("Should be gone:" + trScrolledTime.getVisibility());

			int progress = seekBar.getProgress();
			//System.out.println("Seeked to new progress" + (float) (progress / 10F ));
			//System.out.println("Progress new:"+progress);
			mMediaPlayer.seek(progress);
			if (!paused) {
				restartUpdater();
			} 
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			//	// TODO Auto-generated method stub
			//	trScrolledTime.setVisibility(View.VISIBLE);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			//System.out.println("Progress Changed = " + progress);
			//System.out.println("Progres change percent=" + (float) (progress / 10F ));				
			if(fromUser) {
				long currentSecsMoved = (long)((totalDuration * ((float) (progress / 10F ))) / 100);
				String timeMoved = Utils.formatTime(currentSecsMoved);
				//scrolledtime.setText(timeMoved);
				currentTime.setText(timeMoved);
				//resetAutoHider();
			}
		}
	};
	
	//-------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		/*mHideContainer.setVisibility(View.VISIBLE);
		restartUpdater();
		hideTimer.reset();*/
	
		// TODO Auto-generated method stub
		mScaleDetector.onTouchEvent(event);
	
		if (!mScaleDetector.isInProgress()){
			mMediaPlayer.mResize=true;
			mGestureDetector.onTouchEvent(event);
			
		}
		return true;
	}
	
	
	//-------------------------------------------------------------------------------------
	@SuppressLint("NewApi")
	public class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			float targetScale =MediaPlayer.mCurrentScaleFactor * detector.getScaleFactor();
			MediaPlayer.mCurrentScaleFactor = Math.min(MAX_ZOOM, Math.max(targetScale, MIN_ZOOM));
			return true;
		}
	}
	
	//-------------------------------------------------------------------------------------
	@Override
	public int onCompletion() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onCompletion in VideoPlayer");
		
		return 0;
	}
	
	//-------------------------------------------------------------------------------------
	@Override
	public int onPrepared() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPrepared in VideoPlayer");
		return 0;
	}
	
	//-------------------------------------------------------------------------------------
	@Override
	public int onDurationChangedListener(int duration) {
		// TODO Auto-generated method stub
		Log.d(TAG, "On duation changed Listener");
		return 0;
	}

	//-------------------------------------------------------------------------------------
	@Override
	public int onBuffering(int percentage) {
		// TODO Auto-generated method stub
		Log.d(TAG, "On bufferring "+percentage);
		this.percentage = percentage;
		runOnUiThread(mProgress);
		return 0;
	}
	//-------------------------------------------------------------------------------------
	@Override
	public int onSizeChangedListener(int width, int height) {
		// TODO Auto-generated method stub
		this.mMovieHeight = height;
		this.mMovieWidth = width;
		runOnUiThread(mSizer);
		return 0;
	}
	//-------------------------------------------------------------------------------------
	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}
	
	//-------------------------------------------------------------------------------------
	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			
			if (AUTO_HIDE&&motionEvent.getAction() == MotionEvent.ACTION_UP) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};
	
	//-------------------------------------------------------------------------------------
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};
	
	//-------------------------------------------------------------------------------------
	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	//-------------------------------------------------------------------------------------
	
	View mHideContainer;
	View mControlPanelContainer;

	View imgPlay; 
	View imgBackward; View imgForward;
	View imgAspectRatio;
	SeekBar mSeekBar;
	TextView currentTime; TextView totalTime; 

	long totalDuration;

	//DemoRenderer demoRenderer;
	TableLayout controlPanel;

	//private AudioDriver 		  mAudioDriver = null;
	private PowerManager.WakeLock wakeLock     = null;
	private Handler mHandler = new Handler();

	private Updater seekBarUpdater = new Updater();
	private static int current_aspect_ratio_type=1; //Default Aspect Ratio of the file
	private static boolean paused;
	String openfileFromBrowser = "";
	Intent i = getIntent();
	
	TelephonyManager mgr;
	MediaPlayer mMediaPlayer = null;
	SurfaceView playerView;
	//GLSurfaceView playerView;

	GestureListener mGestureListener;
	ScaleListener mScaleListener;
	ScaleGestureDetector mScaleDetector;
	GestureDetector mGestureDetector				= null;
	private static final float MAX_ZOOM = 2.0f; // 200 %
	private static final float MIN_ZOOM = 0.5f; // 50 %
	private final String TAG = "VideoPlayer";
	private ProgressDialog mDialog;
	private int mMovieWidth = 0;
	private int mMovieHeight = 0;
	private int percentage = 0;
	private Sizer mSizer =  new Sizer();
	
	private Runnable mProgress = new Runnable() {
			public void run() {
				if(mDialog != null){
					if(mDialog.isShowing() && percentage >=100){
						mDialog.dismiss();
					}
				}
			}
		};
	private class Sizer implements Runnable{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.d(TAG,"movie Width "+mMovieWidth+"Height"+mMovieHeight);
			playerView.getHolder().setFixedSize(mMovieWidth, mMovieHeight);
		}
		
	}
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 5000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	//private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	
}
