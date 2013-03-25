package com.broov.player;


import com.broov.commons.Globals;
import com.broov.filemanager.FileManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;

public class MediaPlayer implements ServiceConnection{

	private  Context mPlayerContext;
	/*Private Members*/
	private String mMediaUrl;
	//private SurfaceView mSurfaceView;
	private SurfaceView mSurfaceView;
	private final static String TAG = "MediaPlayer";
	private AudioDriver mAudioDriver = null; //used by lower layer to play audio.
	private int mOsVersion = -1;
	private static MediaPlayer mMediaPlayer = null;
	private static boolean isAudioPlayer = false;
	private static Context mApplicationContext = null;
	private boolean paused = false;
	public Boolean usergeneratedexitApp 	 = false;
	public Boolean playnextfileFromDirectory = true;
	String         nextFile; 
	private int    loopselected              = 0;
	/*Listeners */
	private OnCompletionListener mCompletionListener 		= null;
	private OnBufferingListener mBufferringListener 		= null; 
	private OnPreparedListener mPreparedListener 			= null;
	private OnSizeChangedListener mSizeChangeListener 		= null;
	private OnDurationChangedListener mDurationListener  	= null;	
	private static OnServiceConnectedListener mConnectedListener 	= null;
	/*Movie Properties*/
	int mMovieWidth = 0;
	int mMovieHeight = 0;
	/*Public Members*/
	public boolean fileInfoUpdated			 		= false;
	public static boolean nativeSettingsUpdated    	= false;
	public static float mCurrentScaleFactor   		= 1;
	public boolean mFitToScreen 					= true;
	public boolean mResize							= true;
	public static boolean mPlayerExited				= false;
	/*Device Properties*/
	int mScreenWidth = 0;
	int mScreenHeight = 0;

	//---------------------------------------------------------------------------------------
	public MediaPlayer(Context _context){
		mPlayerContext = _context;
		Globals.LoadNativeLibraries();
		this.mOsVersion		= getOsVersion();
		mMediaPlayer        = this;
		DisplayMetrics dm = new DisplayMetrics();  
		((Activity)_context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;

	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub

		super.finalize();
	}
	//---------------------------------------------------------------------------------------
	public void setUrl(String url){
		mMediaUrl = url;
	}

	//---------------------------------------------------------------------------------------
	public void setSurface(GLSurfaceView_SDL _surfaceView){
		mSurfaceView = _surfaceView;
	}

	//---------------------------------------------------------------------------------------
	public void initSDL()
	{
		//Native libraries loading code
		
		System.out.println("native libraries loaded");
		//Audio thread initializer
		mAudioDriver = new AudioDriver();
		System.out.println("Audio thread initialized");
		//DemoRenderer demoRenderer = new DemoRenderer((Activity)mPlayerContext);
		//this.mDemoRenderer = demoRenderer;
		//mSurfaceView.setRenderer(demoRenderer); 
		System.out.println("Set the surface view renderer");
		//holder.addCallback(mSurfaceView);
		System.out.println("Added the holder callback");
		//holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
		System.out.println("Hold type set");
	}

	//---------------------------------------------------------------------------------------
	public void initAudio(){
		
		System.out.println("native libraries loaded");
		//Audio thread initializer
		mAudioDriver = new AudioDriver();
		System.out.println("Audio thread initialized");
		Runnable audioRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				playFile(mPlayerContext);
			}
		};
		Thread audioThread = new Thread(audioRunnable);
		audioThread.start();
	}

	//---------------------------------------------------------------------------------------
	public int onCompletion() {
		writeLog("media player Oncompletion");
		if(mCompletionListener!=null)
			mCompletionListener.onCompletion(); 
		if(mCompletionListener != null){
			((Activity) mPlayerContext).finish();
		}

		return 0;
	}

	//--------------------------------------------------------------------------------------
	public int onBuffering(int percentage) {
		writeLog("Media player OnBuffering"+percentage);
		if(mBufferringListener!=null)
			mBufferringListener.onBuffering(percentage);
		return 0;
	}

	//--------------------------------------------------------------------------------------
	public int onPrepared() {
		writeLog("media player onPrepared ");
		if(mPreparedListener!=null)
		{
			mPreparedListener.onPrepared();
		}
		return 0;
	}

	//--------------------------------------------------------------------------------------
	public void setHolder(SurfaceView view){
		if(true){
			this.mSurfaceView = view;

		}
	}

	//--------------------------------------------------------------------------------------
	public void setOnCompletionListener(OnCompletionListener _context)
	{
		this.mCompletionListener = _context;
	}

	//--------------------------------------------------------------------------------------
	public void setOnBufferingListener(OnBufferingListener _context)
	{
		this.mBufferringListener = _context;
	}

	//--------------------------------------------------------------------------------------
	public void setOnPreparedListener(OnPreparedListener _context)
	{
		this.mPreparedListener = _context;
	}

	//--------------------------------------------------------------------------------------
	public void setOnSizeChangedListener(OnSizeChangedListener _context){
		this.mSizeChangeListener = _context;
	}

	//--------------------------------------------------------------------------------------
	public void setOnDurationChangedListener(OnDurationChangedListener _context){
		this.mDurationListener = _context;
	}

	//-------------------------------------------------------------------------------------
	public void setOnConnectionListener(OnServiceConnectedListener _context){
		mConnectedListener	= _context;
	}

	//-------------------------------------------------------------------------------------
	public void setAspectRatio(int ratioType){
		nativePlayerSetAspectRatio(ratioType);
	}

	//-------------------------------------------------------------------------------------
	public void setFileInfoUpdated(boolean updated){
		fileInfoUpdated = updated;
	}

	//-------------------------------------------------------------------------------------
	public void setPlayerType(boolean player){
		isAudioPlayer = player;
	}

	//-------------------------------------------------------------------------------------
	public void setZoomParameters()
	{
		if (mMovieWidth == -1 || mMovieHeight == -1)
			return;

		float totalZoom = 1;

		float maxXZoom = (float) mScreenWidth / (float) mMovieWidth;
		float maxYZoom = (float) mScreenHeight / (float) mMovieHeight;

		if(mMovieWidth>mScreenWidth || mMovieHeight>mScreenHeight){
			// maxXZoom = (float)  movieWidth / (float)  screenWidth;
			// maxYZoom = (float)  movieHeight/ (float) screenHeight ;
			// Config.Log(TAG, "MaxXZomm"+maxXZoom);
		}

		if (mFitToScreen)
		{
			mCurrentScaleFactor = 1;
			mFitToScreen = false;
		}
		totalZoom = Math.min(maxXZoom, maxYZoom) *  mCurrentScaleFactor;
		nativeSetZoomCoeff(totalZoom);
	}
	//-------------------------------------------------------------------------------------
	public void stop()
	{

		/*if (wakeLock != null) {
			wakeLock.release();
			wakeLock = null;
		}*/
		usergeneratedexitApp = true;
		nativeDone();

	}

	//-------------------------------------------------------------------------------------
	public void playFile(Context _context){
		this.mPlayerContext = _context;
		
		Runnable vid =  new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					mPlayerExited = false;
					nextFile = Globals.fileName;
					nativeInitJavaCallbacks();
					nativeMediaPlayerInitJavaCallbacks();
					if (!nativeSettingsUpdated) {
						nativePlayerSettings(Globals.dbSubtitleFont, FileManager.getshow_subtitle(), FileManager.getSubTitleSize(), Globals.dbSubtitleEncoding,
								MediaPlayerSettings.skipFrames, MediaPlayerSettings.rgb565, MediaPlayerSettings.yuvRgbAsm, 
								MediaPlayerSettings.skipBidirFrames, MediaPlayerSettings.streamqueueSizeMin, MediaPlayerSettings.streamqueueSizeMax, 
								MediaPlayerSettings.streamqueueSizeTotal, MediaPlayerSettings.streamqueueSizeAudio,
								MediaPlayerSettings.fastMode, MediaPlayerSettings.debugMode, MediaPlayerSettings.ffmpegFlags, 
								MediaPlayerSettings.queueSizeMin, MediaPlayerSettings.queueSizeMax, MediaPlayerSettings.queueSizeTotal, 
								MediaPlayerSettings.queueSizeAudio);			
						nativeSetScreenSize(mScreenWidth, mScreenHeight);
						nativeSettingsUpdated=true;
					}

					// Make main thread priority lower so audio thread won't get underrun
					//Thread.currentThread().setPriority((Thread.currentThread().getPriority() + Thread.MIN_PRIORITY)/2);
					//Thread.currentThread().setPriority(Thread.MAX_PRIORITY-2);

					//System.out.println("Calling playerInit");

					nativePlayerInit();

					System.out.println("File::"+Globals.fileName);

					switch(FileManager.loopOptionForFile(Globals.fileName)){
					case Globals.PLAY_ONCE:
						System.out.println("PLAY_ONCE");
						playnextfileFromDirectory = false;
						loopselected = 0;
						break;
					case Globals.PLAY_ALL:
						System.out.println("PLAY_ALL");
						loopselected = 0;
						break;
					case Globals.REPEAT_ONE:
						System.out.println("REPEAT_ONE");
						loopselected=1;
						playnextfileFromDirectory =false;
						break;
					case Globals.REPEAT_ALL:
						System.out.println("REPEAT_ALL");
						loopselected=0;
						break;
					}

					int audioFileType;
					if (FileManager.isAudioFile(Globals.fileName)) { audioFileType = 1; } 
					else {audioFileType = 0; }

					if (audioFileType == 1) { MediaPlayerSettings.syncType = MediaPlayerSettings.AV_SYNC_TYPE_AUDIO; }

					//System.out.println("nativePlayerMain(NewPlayer.fileName:"+Globals.fileName+", loopselected:"+loopselected+", audioFileType: "+audioFileType+");");
					////101 - Next button  100 - Previous button  0 - Song played finished

					int retValue;

					if ((audioFileType == 1) && FileManager.isAudioStream(Globals.fileName)){
						String stream = FileManager.ReadFirstLine(Globals.fileName);
						retValue = nativePlayerMain(stream, loopselected, audioFileType, MediaPlayerSettings.syncType, MediaPlayerSettings.seekDuration, 1, MediaPlayerSettings.minAudioBufSize,MediaPlayerSettings.minVideoBufSize);

					} else if((audioFileType==0) && FileManager.isVideoStream(Globals.fileName)) {

						String stream = FileManager.ReadFirstLine(Globals.fileName);			
						retValue = nativePlayerMain(stream, loopselected, audioFileType, MediaPlayerSettings.syncType, MediaPlayerSettings.seekDuration, 1, MediaPlayerSettings.minAudioBufSize,MediaPlayerSettings.minVideoBufSize);
					} else {

						retValue = nativePlayerMain(Globals.fileName, loopselected, audioFileType, MediaPlayerSettings.syncType, MediaPlayerSettings.seekDuration, 0,MediaPlayerSettings.minAudioBufSize,MediaPlayerSettings.minVideoBufSize);
					}

					//Initializing the arraylist
					//clear the array of already played items 
					FileManager.alreadyPlayed.clear();

					System.out.println("Returned from NativePlayerMainValue:"+ retValue);

					while (!usergeneratedexitApp && playnextfileFromDirectory ){

						if (retValue == 100){
							System.out.println("Returned from NativePlayerMainValue:"+nextFile);
							nextFile =	FileManager.getPrevFileInDirectory(nextFile);
							Globals.fileName = nextFile;
							fileInfoUpdated = true;
						}else{
							System.out.println("Returned from NativePlayerMainValue:"+ nextFile);
							nextFile =	FileManager.getNextFileInDirectory(nextFile);
							Globals.fileName = nextFile;
							fileInfoUpdated = true;

						}
						if (nextFile == "") {
							System.out.println("All files are played in directory:");
							break;
						}

						//System.out.println("nextFile before:"+nextFile);
						if (FileManager.isAudioFile(nextFile)) {
							audioFileType = 1; 
						} else {
							audioFileType = 0;
						}
						if (audioFileType == 1) { MediaPlayerSettings.syncType = MediaPlayerSettings.AV_SYNC_TYPE_AUDIO; }


						System.out.println("nativePlayerMain(fileName:"+nextFile+", loopselected:"+loopselected+", audioFileType: "+audioFileType+");");

						if ((audioFileType == 1) && FileManager.isAudioStream(nextFile)){
							String stream = FileManager.ReadFirstLine(nextFile);
							retValue = nativePlayerMain(stream, loopselected, audioFileType, MediaPlayerSettings.syncType, MediaPlayerSettings.seekDuration, 1, MediaPlayerSettings.minAudioBufSize,MediaPlayerSettings.minVideoBufSize);

						} else if((audioFileType==0) && FileManager.isVideoStream(nextFile)) {
							String stream = FileManager.ReadFirstLine(nextFile);			
							retValue = nativePlayerMain(stream, loopselected, audioFileType, MediaPlayerSettings.syncType, MediaPlayerSettings.seekDuration, 1, MediaPlayerSettings.minAudioBufSize,MediaPlayerSettings.minVideoBufSize);
						} else {
							retValue = nativePlayerMain(nextFile, loopselected, audioFileType, MediaPlayerSettings.syncType, MediaPlayerSettings.seekDuration, 0, MediaPlayerSettings.minAudioBufSize,MediaPlayerSettings.minVideoBufSize);
						}

						System.out.println("Returned from NativePlayerMainValue:"+ retValue);
					}

					System.out.println("Exited after nativePlayerMain");
					nativePlayerExit();
					System.out.println("Exited after nativePlayerExit");
					mPlayerExited = true;
					((Activity)mPlayerContext).finish();

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread td =  new Thread(vid);
		td.start();


	}
	
	//-------------------------------------------------------------------------------------
	public static boolean isPlaying()
	{
		return !mPlayerExited;
	}
	
	//-------------------------------------------------------------------------------------
	public void pause()
	{
		writeLog( "Pause in Media player");
		paused= true;
		nativePlayerPause();

		//playerView.stopRefresh();
	}

	//-------------------------------------------------------------------------------------
	public void play()
	{
		paused=false;
		writeLog("Play in Media player");
		nativePlayerPlay();
		//playerView.startRefresh();
	}


	//-------------------------------------------------------------------------------------
	public void forward()
	{
		nativePlayerForward();
	}

	//-------------------------------------------------------------------------------------
	public void rewind()
	{
		nativePlayerRewind();
	}


	//-------------------------------------------------------------------------------------
	public void seek(int duration)
	{
		nativePlayerSeek(duration);
	}

	//-------------------------------------------------------------------------------------
	public void prev(){
		nativePlayerPrev();
	}

	//-------------------------------------------------------------------------------------
	public void next(){
		nativePlayerNext();
	}

	//-------------------------------------------------------------------------------------
	public long getElapsedDuration()
	{
		long duration =-1;
		duration = nativePlayerDuration();
		return duration;
	}

	//-------------------------------------------------------------------------------------
	public long getTotalDuration()
	{
		long totalDuration =-1;
		totalDuration = nativePlayerTotalDuration();
		return totalDuration;

	}
	//-------------------------------------------------------------------------------------
	public void exitApp(){
		usergeneratedexitApp = true;
		nativeDone();
	}

	//-------------------------------------------------------------------------------------
	public int setWidthHeight(int width, int height){
		Log.d(TAG, "Width = "+width+" height = "+height);
		mMovieHeight = height;
		mMovieWidth  = height;
		if(mSizeChangeListener != null){
			mSizeChangeListener.onSizeChangedListener(width, height);
		}

		return 0;
	}

	//-------------------------------------------------------------------------------------
	public static void unBindService(Context _context){
		if(mMediaPlayer != null){
			(_context).unbindService(mMediaPlayer);
		}
	}


	//-------------------------------------------------------------------------------------
	public static void bindAudioService(Context _context){
		mApplicationContext = _context;
		if(mMediaPlayer != null){
			(_context).bindService(new Intent(_context, AudioService.class),mMediaPlayer, Context.BIND_AUTO_CREATE);	
		}
	}

	//-------------------------------------------------------------------------------------
	public static MediaPlayer getInstance(Context _context){
		if(mMediaPlayer == null){
			mMediaPlayer = new MediaPlayer(_context);
		}
		return mMediaPlayer;
	}
	

	//-------------------------------------------------------------------------------------
	public boolean getFileInfoUpdated(){
		return fileInfoUpdated;
	}

	//-------------------------------------------------------------------------------------

	public static int getOsVersion(){
		return android.os.Build.VERSION.SDK_INT; 
	}

	//-------------------------------------------------------------------------------------
	public String getOsVersionName(){
		String osName = "unknown";
		int versionCode = android.os.Build.VERSION.SDK_INT;
		if( versionCode == android.os.Build.VERSION_CODES.FROYO){
			osName = "Froyo";
		}else if(versionCode == android.os.Build.VERSION_CODES.GINGERBREAD || 
				versionCode == android.os.Build.VERSION_CODES.GINGERBREAD_MR1){
			osName = "GingerBread";
		}else if(versionCode == android.os.Build.VERSION_CODES.HONEYCOMB || 
				versionCode == android.os.Build.VERSION_CODES.HONEYCOMB_MR1 || 
				versionCode == android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
			osName = "HoneyComb";
		}else if(versionCode == android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH ||
				versionCode == android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1){
			osName = "IceCreamSandwich";
		}

		return osName;
	}

	//-------------------------------------------------------------------------------------
	public static boolean isANativeWindowSupported(){
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD){
			return true;
		}else{
			return false;
		}
	}

	//-------------------------------------------------------------------------------------
	public static void writeLog(String msg){
		Log.d(TAG,msg);
	}

	//-------------------------------------------------------------------------------------
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Service Connected");
		mMediaPlayer = ((AudioService.MyBinder) service).getService(mPlayerContext);
		try {
			if(mConnectedListener != null){
				mConnectedListener.onServiceConntected(mMediaPlayer);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//-------------------------------------------------------------------------------------
	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Service Disconnected");
		mMediaPlayer=null;
	}

	//-------------------------------------------------------------------------------------
	public void quitService(){
		MediaPlayer.unBindService(mApplicationContext);
	}

	//-------------------------------------------------------------------------------------

	public int swapBuffers() // Called from native code, returns 1 on success, 0 when GL context lost (user put app to background)
	{		
		return 1;
	}

	public int exitFromNativePlayerView()
	{
		System.out.println("Inside exitFromNativePlayerView()");
		return 1;
	}

	private native void nativeInitJavaCallbacks();

	/**
	 * 
	 * @param fontFileName
	 * Any other font file provided or selected by the user to be used for subtitles
	 * 
	 * @param subtitleShow
	 * 0 - Do not show subtitle
	 * 1 - Show subtitle
	 * 
	 * @param subtitleFontSize
	 * Valid values are 9, 11, 13 as of now
	 * @return
	 */
	private native int nativePlayerInit();

	private native int nativePlayerSettings(String fileName, int subtitleShow, int subtitleFontSize, int subtitleEncodingType,
			int skipFrames, int rgb565, int yuvRgbAsm, int skipBidirFrames, int squeueSizeMin, int squeueSizeMax, int squeueSizeTotal, int squeueSizeAudio,
			int fastMode, int debugMode, int ffmpegFlags, int queueSizeMin, int queueSizeMax, int queueSizeTotal, int queueSizeAudio);

	/**
	 * 
	 * @param fileName
	 * Name of the file to be played
	 * 
	 * @param loop
	 * 0 - Dont loop
	 * 1 - Loop the same file
	 * 
	 * @param audioFileType
	 * audio File Type = 0 or 1 (0 means video file, 1 means audio file
	 * 
	 * @param file name show string
	 * string descrbing the file name that is being played now
	 * 
	 * @param file type string 
	 * string describing the file type and any other
	 * 
	 * @param file size string 
	 * string describing the size of the file 
	 * @return
	 */
	private native int nativePlayerMain(String fileName, int loop,int audioFileType, 
			int syncType, int seekDuration, int isStream, int audioBufSize, int videoBufSize);

	private native int nativePlayerExit();

	private native void nativeResize(int w, int h);
	private native void nativeDone();

	public native int nativePlayerDuration();
	public native int nativePlayerTotalDuration();

	public native int nativePlayerPlay();
	public native int nativePlayerPause();
	public native int nativePlayerForward();
	public native int nativePlayerRewind();
	public native int nativePlayerPrev();
	public native int nativePlayerNext();
	public native int nativePlayerSeek(int percent); //Number in the range of 0-1000, meaning 99.1, 99.2, 0.1,0.2..
	public native int nativePlayerSetAspectRatio(int aspectRatioType); // 0-default, 1- 4:3, 2- 16:9, 3- FullScreen

	private Activity context = null;

	private native int nativeVideoPlayerInit(String fileName, int subtitleShow, int subtitleFontSize, int subtitleEncodingType, int rgb565);

	private native int nativeVideoPlayerMain(String fileName, int loop,int audioFileType, 
			int skipFrames, int rgb565, int yuvRgbAsm, int skipBidirFrames, int queueSizeMin, int queueSizeMax, int queueSizeTotal, int queueSizeAudio,
			int fastMode, int debugMode, int syncType, int seekDuration, int ffmpegFlags);

	private native int nativeVideoPlayerExit();

	private native void nativeVideoResize(int w, int h);
	private native void nativeVideoDone();

	public native int nativeVideoPlayerDuration();
	public native int nativeVideoPlayerTotalDuration();

	public native int nativeVideoPlayerPlay();
	public native int nativeVideoPlayerPause();
	public native int nativeVideoPlayerForward();
	public native int nativeVideoPlayerRewind();
	public native int nativeVideoPlayerPrev();
	public native int nativeVideoPlayerNext();
	public native int nativeVideoPlayerSeek(int percent); //Number in the range of 0-1000, meaning 99.1, 99.2, 0.1,0.2..
	public native int nativeVideoPlayerSetAspectRatio(int aspectRatioType); // 0-default, 1- 4:3, 2- 16:9, 3- FullScreen
	public native int nativeDeInitCallBacks();
	private native int nativeMediaPlayerInitJavaCallbacks();
	public native void nativeSetScreenSize(int width, int height);
	public native void nativeSetZoomCoeff(float zoom);

	/**
	 * Change size of the native drawing surface
	 */
	public native void nativeChangeSurface(int width, int height);
	// ---------------------------------------------------------------------------------------
	/**
	 * Create native native drawing surface
	 */
	public native void nativeCreateSurface(Object mediaplayerThis);
	// ---------------------------------------------------------------------------------------
	/**
	 * Refresh screen - draw new frame if any. Called by OpenGL render thread.
	 */
	public native void nativeVideoRefresh();
	// ---------------------------------------------------------------------------------------
	/**
	 * Initialize Surface - initializes the native window object in lower layer.
	 */
	public native void nativeSurfaceViewInit(Surface surface);
	// ---------------------------------------------------------------------------------------
	/**
	 * Frees memory used to hold surface view in lower layer
	 */
	public native void nativeSurfaceViewDestroy();
}
