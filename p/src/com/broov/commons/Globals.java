package com.broov.commons;

import java.io.File;

import com.broov.player.MediaPlayer;

import android.graphics.Color;
import android.os.Environment;

public class Globals {

	/*Audio Service Constants*/
	public static String EXITPLAYBACK	   = "com.broov.player.EXIT";
	public static String PLAYPLAYBACK	   = "com.broov.player.PLAY";
    public static String PAUSEPLAYBACK     = "com.broov.player.PAUSE";
    public static String PLAYNEXT		   = "com.broov.player.NEXT";
    public static String PLAYPREV		   = "com.broov.player.PREV";
    public static String PLAYSTOP		   = "com.broov.player.STOP";
    public static String STARTSERVICE	   = "com.broov.player.STARTSERVICE";
    public static String STOPSERVICE	   = "com.broov.player.STOPSERVICE";
    public static String STARTPLAYBACK	   = "com.broov.player.START";
  
    public static MediaPlayer mMediaPlayer;
    
    public static boolean mAudioServiceStarted = false;
    public static boolean mServiceBinded = false;
    public static boolean filenameChanged = true;
	public static String fileName; 
	

	//Used in AudioThread.
	//Use the minimum BufferSize of AudioTrack. from version 2.6, this value is 0. previously it was 2.
	public static int AudioBufferConfig   = 0;

	//public static String ApplicationName = "Dolphin Player Universal";
	public static String ApplicationName 	 = "Dolphin Player";
	public static String VersionName     	 = "2.8 Build 19September2012";
	public static String defaultDir      	 = "/sdcard";
	public static String defaultSubtitleFont = "/sdcard/broov.ttf";

	public static void setFileName(String fName) {
		fileName = fName;
	}

	public static String getFileName() {
		if (fileName == null) {
			return "";
		}
		return fileName;
	}

	public static final String supportedVideoFileFormats[] = 
		{   
		"mp4","wmv","avi","mkv","dv",
		"rm","mpg","mpeg","flv","divx",
		"swf","dat","h264","h263","h261",
		"3gp","3gpp","asf","mov","m4v", "ogv",
		"vob", "vstream", "ts", "webm",
		//to verify below file formats - reference vlc
		"vro", "tts", "tod", "rmvb", "rec", "ps", "ogx",
		"ogm", "nuv", "nsv", "mxf", "mts", "mpv2", "mpeg1", "mpeg2", "mpeg4",
		"mpe", "mp4v", "mp2v", "mp2", "m2ts",
		"m2t", "m2v", "m1v", "amv", "3gp2"
		};

	public static final String supportedAudioFileFormats[] = 
		{   
		"mp3","wma","ogg","mp2","flac",
		"aac","ac3","amr","pcm","wav",
		"au","aiff","3g2","m4a", "astream",
		//to verify below file formats - reference vlc
		"a52", "adt", "adts", "aif", "aifc",
		"aob", "ape", "awb", "dts", "cda", "it", "m4p",
		"mid", "mka", "mlp", "mod", "mp1", "mp2", "mpc",
		"oga", "oma", "rmi", "s3m", "spx", "tta",
		"voc", "vqf", "w64", "wv", "xa", "xm"
		};		

	public static final String supportedFontFileType[] = 
		{   "ttf"
		};

	public static final String supportedImageFileFormats[] = 
		{
		"gif","bmp","png","jpg"
		};

	public static final String supportedAudioStreamFileFormats[] = 
		{
		"astream"
		};

	public static final String supportedVideoStreamFileFormats[] = 
		{
		"vstream"
		};

	public static String supportedFileFormats[] =concat(supportedAudioFileFormats, supportedVideoFileFormats, supportedFontFileType);

	public static final String PREFS_NAME = "BroovPrefsFileTypeThree";	//user preference file name
	public static final String PREFS_HIDDEN = "hidden";
	public static final String PREFS_COLOR = "color";
	public static final String PREFS_SUBTITLE = "subtitle";
	public static final String PREFS_THUMBNAIL = "thumbnail";
	public static final String PREFS_AUDIOLOOP = "audioloop";
	public static final String PREFS_VIDEOLOOP = "videoloop";
	public static final String PREFS_SUBTITLESIZE = "subtitlesize";
	public static final String PREFS_SUBTITLEENCODING = "subtitleencoding";
	public static final String PREFS_SKIPFRAME = "skipframe";

	public static final String PREFS_LANGUAGE = "language";
	public static final String PREFS_LASTOPENDIR = "lastopeneddir";
	public static final String PREFS_DEFAULTHOME = "defaulthomedir";
	public static final String PREFS_SUBTITLEFONT = "subtitlefont";
	public static final String PREFS_SORT = "sort";

	//advanced
	public static final String PREFS_ADVSKIPFRAMES = "advskipframes";
	public static final String PREFS_BIDIRECTIONAL = "advbidirectional";
	public static final String PREFS_ADVFFMPEG = "advffmpeg";
	public static final String PREFS_ADVYUV2RGB = "advyuv2rgb";
	public static final String PREFS_ADVMINVIDEOQ = "advminvideoq";
	public static final String PREFS_ADVMAXVIDEOQ="advmaxvideoq";
	public static final String PREFS_ADVMAXAUDIOQ="advmaxaudioq";
	public static final String PREFS_ADVSTREAMMINVIDEOQ="advstreamminvideoq";
	public static final String PREFS_ADVSTREAMMAXVIDEOQ="advstreammaxvideoq";
	public static final String PREFS_ADVSTREAMMAXAUDIOQ="advstreammaxaudioq";
	public static final String PREFS_ADVAVSYNCMODE="advavsyncmode";
	public static final String PREFS_ADVDEBUG="advdebug";
	public static final String PREFS_ADVPIXELFORMAT="advpixelformat";
	public static final String PREFS_ADVSWSSCALER="advswsscaler";


	public static final int PLAY_ONCE =0;
	public static final int PLAY_ALL =1;
	public static final int REPEAT_ONE =2;
	public static final int REPEAT_ALL =3;

	//drop down
	public static final int ARM_ASM=0;
	public static final int FFmpeg_sws_scaler=1;
	public static final int pixelformat_RGB565=0;
	public static final int pixelformat_RGB8888=1;

	public static final int avsync_audioclock=0;
	public static final int avsync_videoclock=1;
	public static final int avsync_external=2;

	public static final int advswsscaler_swsbicubic=0;
	public static final int advswsscaler_swsbilinear=1;
	public static final int advswsscaler_swsfastbilinear=2;

	public static final int min_videokb50=0;
	public static final int min_videokb100=1;
	public static final int min_videokb150=2;
	public static final int min_videokb200=3;
	public static final int min_videokb256=4;
	public static final int min_videokb380=5;
	public static final int min_videokb500=6;
	public static final int min_videokb620=7;
	public static final int min_videokb750=8;
	public static final int min_videokb870=9;
	public static final int min_videomb1=10;
	public static final int min_videomb1_5=11;
	public static final int min_videomb2=12;

	public static final int max_videokb50=0;
	public static final int max_videokb100=1;
	public static final int max_videokb150=2;
	public static final int max_videokb200=3;
	public static final int max_videokb256=4;
	public static final int max_videokb380=5;
	public static final int max_videokb500=6;
	public static final int max_videokb620=7;
	public static final int max_videokb750=8;
	public static final int max_videokb870=9;
	public static final int max_videomb1=10;
	public static final int max_videomb1_5=11;
	public static final int max_videomb2=12;
	public static final int max_videomb3=13;
	public static final int max_videomb4=14;
	public static final int max_videomb5=15;
	public static final int max_videomb8=16;
	public static final int max_videomb10=17;
	public static final int max_videomb12=18;
	public static final int max_videomb14=19;
	public static final int max_videomb15=20;
	public static final int max_videomb20=21;

	public static final int max_audiokb50=0;
	public static final int max_audiokb100=1;
	public static final int max_audiokb150=2;
	public static final int max_audiokb200=3;
	public static final int max_audiokb256=4;
	public static final int max_audiokb380=5;
	public static final int max_audiokb500=6;
	public static final int max_audiokb620=7;
	public static final int max_audiokb750=8;
	public static final int max_audiokb870=9;
	public static final int max_audiomb1=10;
	public static final int max_audiomb1_5=11;
	public static final int max_audiomb2=12;
	public static final int max_audiomb3=13;
	public static final int max_audiomb4=14;
	public static final int max_audiomb5=15;

	public static final int streammin_videokb50=0;
	public static final int streammin_videokb100=1;
	public static final int streammin_videokb150=2;
	public static final int streammin_videokb200=3;
	public static final int streammin_videokb256=4;
	public static final int streammin_videokb380=5;
	public static final int streammin_videokb500=6;
	public static final int streammin_videokb620=7;
	public static final int streammin_videokb750=8;
	public static final int streammin_videokb870=9;
	public static final int streammin_videomb1=10;
	public static final int streammin_videomb1_5=11;
	public static final int streammin_videomb2=12;

	public static final int streammax_videokb50=0;
	public static final int streammax_videokb100=1;
	public static final int streammax_videokb150=2;
	public static final int streammax_videokb200=3;
	public static final int streammax_videokb256=4;
	public static final int streammax_videokb380=5;
	public static final int streammax_videokb500=6;
	public static final int streammax_videokb620=7;
	public static final int streammax_videokb750=8;
	public static final int streammax_videokb870=9;
	public static final int streammax_videomb1=10;
	public static final int streammax_videomb1_5=11;
	public static final int streammax_videomb2=12;
	public static final int streammax_videomb3=13;
	public static final int streammax_videomb4=14;
	public static final int streammax_videomb5=15;
	public static final int streammax_videomb8=16;
	public static final int streammax_videomb10=17;
	public static final int streammax_videomb12=18;
	public static final int streammax_videomb14=19;
	public static final int streammax_videomb15=20;
	public static final int streammax_videomb20=21;

	public static final int streammax_audiokb50=0;
	public static final int streammax_audiokb100=1;
	public static final int streammax_audiokb150=2;
	public static final int streammax_audiokb200=3;
	public static final int streammax_audiokb256=4;
	public static final int streammax_audiokb380=5;
	public static final int streammax_audiokb500=6;
	public static final int streammax_audiokb620=7;
	public static final int streammax_audiokb750=8;
	public static final int streammax_audiokb870=9;
	public static final int streammax_audiomb1=10;
	public static final int streammax_audiomb1_5=11;
	public static final int streammax_audiomb2=12;
	public static final int streammax_audiomb3=13;
	public static final int streammax_audiomb4=14;
	public static final int streammax_audiomb5=15;

	public static final int SWIPE_MIN_DISTANCE = 120;
	public static final int SWIPE_MAX_OFF_PATH = 250;
	public static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public static final int SUBTITLE_FONT_SMALL = 9;
	public static final int SUBTITLE_FONT_MEDIUM = 11;
	public static final int SUBTITLE_FONT_LARGE = 13;

	/**
	 * This method is used to concat 2 string arrays to one
	 * @param A
	 * First string Array
	 * @param B
	 * Second string Array
	 * @return
	 * Concatenated string Array of First and Second
	 */
	public static String[] concat(String[] A, String[] B, String[] C) {
		String[] temp= new String[A.length+B.length+C.length];
		System.arraycopy(A, 0, temp, 0, A.length);
		System.arraycopy(B, 0, temp, A.length, B.length);
		System.arraycopy(C, 0, temp, A.length+B.length, C.length);
		return temp;
	}

	public static String aboutUsContent = 
			"<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-16le\">"+
					"<body><font color='blue'>" +
					"<P ALIGN=CENTER><b>" +Globals.ApplicationName+ " " +Globals.VersionName+"</b>" +
					"</p>" +
					"<P ALIGN=CENTER> " +
					"<b>Credits</b>"+
					"</p>"+
					"<P ALIGN=JUSTIFY>"+
					"Our thanks to opensource community members of FFmpeg, SDL, freetype, SDL_image, SDL_ttf, libpng, libjpeg, Theorarm, universalchardet, iconv, andprof, Pelya for SDL porting to Android, KMP"+
					"</p>" +
					"<P ALIGN=JUSTIFY>"+
					"This is an open source media player for Android. Source can be availed at http://code.google.com/p/dolphin-player. Contributions are welcome from open source community members." +
					"</p>"+
					"<P ALIGN=JUSTIFY>"+
					"Users of Dolphin Player Premium Edition, can avail one licensed copy of BullsHit Converter 3.0 Ultimate Edition. HD Videos can be converted for optimal viewing in mobiles using this software" +
					"<br>"+
					"(Supports conversion for most of the Audio and Video files)"+
					"</p>" +
					"<P ALIGN=CENTER>"+
					"<b>Upcoming Features</b>" +
					"<br> Speech Recognition to control play, pause, forward, rewind of audio files " +
					"</p>" +
					"<P ALIGN=CENTER>"+
					"<b>Lead Developers</b>" +
					"<br>  Ragupathy Balan" +
					"</p>" +
					"<P ALIGN=CENTER>"+
					"<b>Developers</b>" +
					"<br>  Nareshprasad <br> Ganesan Narayanan <br> Arrvind Kanagaraj" +

		"</p>" +
		"</p>" +
		"<P ALIGN=CENTER>"+
		"<b>UI/UX Design</b>" +
		"<br>  Prabhu Beeman" +
		"<br>  Ragupathy Balan" +
		"<br>  Denis Oliveira" +
		"</p>" +
		"<P ALIGN=CENTER>"+
		"<b>Creator</b>" +
		"<br>   Aatral Arasu" +
		"</P>"+
		"<P ALIGN=CENTER>"+
		"<b>Reviewers</b>" +
		"<br>  Balaji Sivasubramanian" +
		"<br><br><b>Translations</b><br>  Russian - wdiabloster <br>  French - Elankathir<br>  German - Elankathir<br>  Italian - Elankathir<br>  Japanese - Ramasamy<br>  Chinese - Ramasamy<br>  All other languages - Arrvind Kanagaraj<br>" +
		"<br><br> Copyright &copy; Broov Information Services Private Limited <br> <a style=\"color:grey\" href=\"http://broov.in\">http://broov.in/</a>"+
		"</P></body></html>";

	public static String helpContent =
			"<font color='blue'>" +
					"<P ALIGN=CENTER>  "+ Globals.ApplicationName+" "+Globals.VersionName+" is an audio and video player for Android 2.1 and above." +
					"</p>"+
					"<P ALIGN=CENTER>" +
					"<b>Play Media</b>" +
					"</p>"+
					"<P ALIGN=JUSTIFY>" +
					"To play an audio or video file, just click on the file from the file list." +
					"</p>"+

		"<b> Subtitles</b>" +
		"<P ALIGN=JUSTIFY>" +
		"Subtitle files with the following extensions are supported(srt, sub, smi, rt, txt, ssa, aqt, jss, js, ass, utf, utf8, utf-8). " +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"Unicode languages are supported in subtitles. Select the ttf file present in your SDcard in file browser. " +
		"</p>"+

		"<P ALIGN=CENTER>" +
		"<b>File List Configuration</b> " +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"The file list color, sorting type, listing of hidden files are configurable in settings menu." +
		" User can set their own Home directory for convenience in settings menu." +
		"</p>"+
		"<b> Loop option</b></br>  There are four options to play an audio/video file i.e " +
		"<P ALIGN=JUSTIFY>" +
		"<b> Play Once</b> - will play the selected file once." +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"<b> Play All</b> - will play all audio/video files in the directory once." +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"<b> Repeat One</b> - will repeat playing the same file."+
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"<b> Repeat All</b> - will repeat playing all the audio/video files in directory." +
		"</p>"+
		"<P ALIGN=CENTER>" +
		"<b> Subtitle Configuration</b>" +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"User can show/hide subtitles for video and also configure size for subtitle fonts. Users can select a font file" +
		" from the file list or can configure the file in settings menu."+
		"</p>"+
		"<P ALIGN=CENTER>" +
		"<b> Supported Subtitle files</b>" +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"\"utf\", \"utf8\", \"utf-8\", \"sub\", \"srt\", " +
		"\"smi\", \"rt\", \"txt\", \"ssa\", \"aqt\", \"jss\", \"js\", \"ass\""+
		"</p>"+
		"<P ALIGN=CENTER>" +
		"<b> Contact us </b>" +
		"</p>"+
		"<P ALIGN=JUSTIFY>" +
		"For customization and feedbacks. Please send email to support@broov.in or aatrala@gmail.com"+
		"</p>"+
		"</p>";

	//advanced
	//Neon values
	//public static boolean dbadvancedskip=false;
	public static int dbadvancedpixelformat=Globals.pixelformat_RGB8888;
	public static int dbadvancedavsyncmode=Globals.avsync_audioclock;

	//Normal V5, V6, V6+VFP values
	public static boolean dbadvancedskip=false;
	//public static int dbadvancedpixelformat=Globals.pixelformat_RGB565;

	public static boolean dbadvancedbidirectional=false;
	public static boolean dbadvancedffmpeg=false; //fast decoding is disabled by default
	public static int dbadvancedyuv=Globals.ARM_ASM; //sws_scaler - 1, arm asm - 0
	public static int dbadvancedminvideoq=Globals.min_videokb256;
	public static int dbadvancedmaxvideoq=Globals.max_videomb2;
	public static int dbadvancedmaxaudioq=Globals.max_audiokb380;
	public static int dbadvancedstreamminvideoq=Globals.streammin_videokb100;
	public static int dbadvancedstreammaxvideoq=Globals.streammax_videomb3;
	public static int dbadvancedstreammaxaudioq=Globals.streammax_audiokb256;

	public static int dbadvancedswsscaler=Globals.advswsscaler_swsbicubic;
	public static boolean dbadvanceddebug=false;

	public static boolean dbHide         = false; // Show/Hide hidden folders
	public static boolean dbSubtitle     = true;  // Show/Hide subtitles
	public static int     dbColor        = Color.BLUE;
	public static int     dbSort         = 2;
	public static int     dbAudioLoop    = Globals.PLAY_ALL;
	public static int     dbVideoLoop    = Globals.REPEAT_ONE;
	public static int     dbSubtitleSize = 1;
	public static int     dbSubtitleEncoding = 0;
	public static String  dbDefaultHome	 = getSdcardPath();
	public static String  dbSubtitleFont = getSdcardPath()+"/broov.ttf";
	public static String  dbLastOpenDir  = getSdcardPath(); 
	public static boolean dbSkipframes     = true;  // Skip frames

	//advanced
	public static void setadvSkipFrames(boolean type) {
		Globals.dbadvancedskip = type;
	}
	public static void setadvbidirectional(boolean type) {
		Globals.dbadvancedbidirectional = type;
	}
	public static void setadvffmpeg(boolean type) {
		Globals.dbadvancedffmpeg = type;
	}
	public static void setadvyuv2rgb(int value){
		Globals.dbadvancedyuv = value;	
	}

	//advminvideoq
	public static void setadvminvideoq(int value){
		Globals.dbadvancedminvideoq = value;	
	}
	public static void setadvmaxvideoq(int value){
		Globals.dbadvancedmaxvideoq = value;	
	}
	public static void setadvmaxaudioq(int value){
		Globals.dbadvancedmaxaudioq = value;	
	}
	public static void setadvstreamminvideoq(int value){
		Globals.dbadvancedstreamminvideoq = value;	
	}
	public static void setadvstreammaxvideoq(int value){
		Globals.dbadvancedstreammaxvideoq = value;	
	}
	public static void setadvstreammaxaudioq(int value){
		Globals.dbadvancedstreammaxaudioq = value;	
	}

	public static void setadvpixelformat(int value){
		Globals.dbadvancedpixelformat = value;	
	}
	public static void setadvavsyncmode(int value){
		Globals.dbadvancedavsyncmode = value;	
	}
	public static void setadvdebug(boolean type) {
		Globals.dbadvanceddebug = type;
	}

	public static void setadvswsscaler(int value){
		Globals.dbadvancedswsscaler = value;	
	}


	/**
	 * This will determine if hidden files and folders will be visible to the
	 * user.
	 * @param choice	true if user is viewing hidden files, false otherwise
	 */

	public static void setShowHiddenFiles(boolean type) {
		Globals.dbHide = type;
	}

	public static void setSortType(int type) {
		Globals.dbSort = type;
	}

	public static void setAudioLoop(int type){
		Globals.dbAudioLoop = type;
	}

	public static void setVideoLoop(int type){
		Globals.dbVideoLoop = type;
	}

	public static void setShowSubTitle(boolean type){
		Globals.dbSubtitle = type;	
	}

	public static void setSubTitleSize(int value){
		Globals.dbSubtitleSize = value;	
	}

	public static void setSubTitleEncoding(int value){
		Globals.dbSubtitleEncoding = value;	
	}

	public static void setDefaultHome(String value){
		System.out.println("setDefault Home value:"+value);
		if(value != null) {
			File file = new File(value);

			//if (value.contains("/")){
			if ( (file.isDirectory()&& file.exists()) ){
				Globals.dbDefaultHome = value;	
			} else {
				Globals.dbDefaultHome = defaultDir; //"/sdcard";
			}
		}
	}

	public static void setSubTitleFont(String value){
		System.out.println("setDefault Subtitle font value:"+value);
		if(value != null) {
			File file = new File(value);

			//if (value.contains("/")){
			if (  file.exists() ){
				Globals.dbSubtitleFont = value;	
			} else {
				Globals.dbSubtitleFont = defaultSubtitleFont; //"/sdcard";
			}
		}
	}

	public static void setLastOpenDir(String value){
		System.out.println("setLastOpendir value:"+value);
		if (value.contains("/")){
			Globals.dbLastOpenDir = value;	
		} else {
			Globals.dbLastOpenDir = defaultDir; //"/sdcard";
		}
	}

	public static void setSkipFrames(boolean choice) {
		Globals.dbSkipframes = choice;
	}

	public static void setColor(int value) {
		Globals.dbColor = value;
	}

	public static String getSdcardPath(){
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		System.out.println("sdcardPath:"+sdcardPath);
		return sdcardPath;
	}
	static boolean isNativeLibrariesLoaded = false;
	public static void LoadNativeLibraries() {
		if (!isNativeLibrariesLoaded) {

			System.loadLibrary("ffmpeg");

			System.loadLibrary("sdl");
			System.loadLibrary("sdl_ttf");
			System.loadLibrary("sdl_image");

			System.loadLibrary("iconv");
			System.loadLibrary("universalchardet");

			System.loadLibrary("yuv2rgb");

			System.loadLibrary("application");

			isNativeLibrariesLoaded = true;
		}
	}

}




//<!--   android:theme="@android:style/Theme.Dialog" 
//android:screenOrientation="portrait"/>-->

//<activity android:name=".Player"
//    android:label="@string/app_name" android:screenOrientation="landscape">


//<activity android:name=".Player"
//    android:label="@string/app_name" android:configChanges="orientation|keyboardHidden" 
//    android:screenOrientation="landscape" >

