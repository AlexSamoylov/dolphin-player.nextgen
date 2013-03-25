package com.broov.player;

import com.broov.commons.Globals;

public class MediaPlayerSettings {

	public static int queueSizeTable[] = { 50, 100, 150, 200, 256, 380, 500, 620, 750, 870, 
		1024, 1500, 2048, 3000, 4000, 5000, 8000, 10000, 12000, 14000, 15000, 20000 }; //0-21

	public static final int AV_SYNC_TYPE_AUDIO=0;
	public static final int AV_SYNC_TYPE_VIDEO=1;
	public static final int AV_SYNC_TYPE_EXTERNAL=2;

	public static final int AV_FFMPEG_SWS_BICUBIC =0;
	public static final int AV_FFMPEG_SWS_BILINEAR=1;
	public static final int AV_FFMPEG_SWS_FAST_BILINEAR=2;

	public static int    skipFrames                = 0;
	public static int    skipBidirFrames           = 0;
	public static int    rgb565                    = 0;
	public static int    yuvRgbAsm                 = 0;  

	public	static int    queueSizeMin              = (50 * 1024);
	public static int    queueSizeMax              = (3000 * 1024);
	public static int    queueSizeTotal            = (25000 * 1024);
	public static int    queueSizeAudio            = (512 * 1024);

	public	static int    streamqueueSizeMin              = (50 * 1024);
	public static int    streamqueueSizeMax              = (3000 * 1024);
	public static int    streamqueueSizeTotal            = (25000 * 1024);
	public static int    streamqueueSizeAudio            = (512 * 1024);

	public static int    fastMode = 0;
	public static int    debugMode= 1;
	public static int    debugVideoMode = 1; //If set to 1, NativeVideoPlayer.java is called for viewing video files
	public static int    syncType = AV_SYNC_TYPE_AUDIO;
	public static int    seekDuration = 0;
	public static int    ffmpegFlags = AV_FFMPEG_SWS_FAST_BILINEAR;
	public static int	minAudioBufSize		   	  = 1024;//1 kb;
	public static int 	minVideoBufSize 		  = 10240; // 10 kb;

	public static void UpdateValuesFromSettings()
	{	
		if (Globals.dbadvancedskip) { skipFrames = 1; } else { skipFrames = 0; }
		if (Globals.dbadvancedbidirectional) { skipBidirFrames = 1; } else { skipBidirFrames = 0; }
		if (Globals.dbadvanceddebug) { debugMode = 1; } else { debugMode = 0; }
		if (Globals.dbadvancedffmpeg) { fastMode = 1; }  else { fastMode = 0; }
		if (Globals.dbadvancedavsyncmode >=0 && Globals.dbadvancedavsyncmode <= 2) { syncType = Globals.dbadvancedavsyncmode;}
		if (Globals.dbadvancedyuv ==0) { yuvRgbAsm=1;} else { yuvRgbAsm = 0; }
		if (Globals.dbadvancedpixelformat ==0) { rgb565 = 1; } else { rgb565=0; }
		if (Globals.dbadvancedswsscaler >=0 && Globals.dbadvancedswsscaler <=2) { ffmpegFlags = Globals.dbadvancedswsscaler; }

		if (Globals.dbadvancedminvideoq >=0 && Globals.dbadvancedminvideoq <=12) { queueSizeMin = queueSizeTable[Globals.dbadvancedminvideoq]*1024; }
		if (queueSizeMin < (50 *1024)) { queueSizeMin = (50*1024); }

		if (Globals.dbadvancedmaxvideoq >=0 && Globals.dbadvancedmaxvideoq <=21) { queueSizeMax = queueSizeTable[Globals.dbadvancedmaxvideoq]*1024; }
		if (queueSizeMax < (50 *1024)) { queueSizeMax = (1000*1024); }

		if (Globals.dbadvancedmaxaudioq >=0 && Globals.dbadvancedmaxaudioq <=15) { queueSizeAudio = queueSizeTable[Globals.dbadvancedmaxaudioq]*1024; }
		if (queueSizeAudio < (50 *1024)) { queueSizeAudio = (256*1024); }

		queueSizeTotal = (queueSizeMin + queueSizeMax + queueSizeAudio);

		if (Globals.dbadvancedstreamminvideoq >=0 && Globals.dbadvancedstreamminvideoq <=12){streamqueueSizeMin = queueSizeTable[Globals.dbadvancedstreamminvideoq]*1024;}
		if (streamqueueSizeMin < (50 *1024)) { streamqueueSizeMin = (50*1024); }

		if (Globals.dbadvancedstreammaxvideoq >=0 && Globals.dbadvancedstreammaxvideoq <=21){streamqueueSizeMax = queueSizeTable[Globals.dbadvancedstreammaxvideoq]*1024;}
		if (streamqueueSizeMax < (50 *1024)) { streamqueueSizeMax = (1000*1024); }

		if (Globals.dbadvancedstreammaxaudioq >=0 && Globals.dbadvancedstreammaxaudioq <=15) { streamqueueSizeAudio = queueSizeTable[Globals.dbadvancedstreammaxaudioq]*1024; }
		if (streamqueueSizeAudio < (50 *1024)) { streamqueueSizeAudio = (256*1024); }

		streamqueueSizeTotal = (streamqueueSizeMin + streamqueueSizeMax + streamqueueSizeAudio);	

		MediaPlayer.nativeSettingsUpdated=false;
	}

}
