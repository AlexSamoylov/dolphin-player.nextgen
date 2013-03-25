#include <unistd.h>
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include "broov_player.h"
#include "broov_drawer.h"

#define FROYO
/** use of Surface view Private native c++ in android **/
//#ifdef FROYO
//#include "surfaceflinger/Surface.h"
//#else
//#include "ui/Surface.h"
//#endif
#include <android/native_window_jni.h>

#ifdef __cplusplus
#define C_LINKAGE "C"
#else
#define C_LINKAGE
#endif

#define TAG "native_main"

ANativeWindow *nativeWindow = NULL;
extern int g_screen_width;
extern int g_screen_height;
JNIMediaPlayerDrawer *drawer = NULL;

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerSettings(JNIEnv* env, jobject obj, jstring jfileName, jint subtitleShow, jint subtitleFontSize, jint subtitleEncodingType, 
	   jint skipFrames, jint rgb565, jint yuvRgbAsm, jint skipBidirFrames,
           jint queueSizeMin, jint queueSizeMax, jint totalQueueSize, jint audioQueueSize, 
           jint fastMode, jint debugMode, jint ffmpegFlags,
           jint nqueueSizeMin, jint nqueueSizeMax, jint ntotalQueueSize, jint naudioQueueSize)
{
        jboolean isCopy;
        char     lclFileName[FILE_NAME_SIZE];

        int my_subtitle_show;
        int my_subtitle_font_size;
        int my_subtitle_encoding_type;
        int my_skip_frames;
        int my_rgb_565;
        int my_yuv_rgb_asm;
        int my_skip_bidir_frames;
        int my_queue_size_min;
        int my_queue_size_max;
        int my_total_queue_size;
        int my_audio_queue_size;
        int my_fast_mode;
        int my_debug_mode;
        int my_ffmpeg_flags;
        int my_nqueue_size_min;
        int my_nqueue_size_max;
        int my_ntotal_queue_size;
        int my_naudio_queue_size;

#ifdef BROOV_C
        const char *fileString = (*env)->GetStringUTFChars(env, jfileName, &isCopy);
#else
        const char *fileString = env->GetStringUTFChars(jfileName, &isCopy);
#endif

        strncpy(lclFileName, fileString, FILE_NAME_SIZE);
#ifdef BROOV_C
        (*env)->ReleaseStringUTFChars(env, jfileName, fileString);
#else
        env->ReleaseStringUTFChars(jfileName, fileString);

#endif
        my_subtitle_show = subtitleShow;
        my_subtitle_font_size = subtitleFontSize;
        my_subtitle_encoding_type = subtitleEncodingType;
        my_skip_frames = skipFrames;
        my_rgb_565 = rgb565;
        my_yuv_rgb_asm = yuvRgbAsm;
        my_skip_bidir_frames = skipBidirFrames;
        my_queue_size_min = queueSizeMin;
        my_queue_size_max = queueSizeMax;
        my_total_queue_size= totalQueueSize;
        my_audio_queue_size= audioQueueSize;
        my_fast_mode = fastMode;
        my_debug_mode = debugMode;
        my_ffmpeg_flags = ffmpegFlags;
        my_nqueue_size_min = nqueueSizeMin;
        my_nqueue_size_max = nqueueSizeMax;
        my_ntotal_queue_size= ntotalQueueSize;
        my_naudio_queue_size= naudioQueueSize;

        __android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "Player settings: subtitle_show:%d subtitle_font_size:%d", my_subtitle_show, my_subtitle_font_size);
        return player_settings(lclFileName, my_subtitle_show, my_subtitle_font_size, my_subtitle_encoding_type, 
			       my_skip_frames, my_rgb_565, my_yuv_rgb_asm, my_skip_bidir_frames, 
			       my_queue_size_min, my_queue_size_max, my_total_queue_size, my_audio_queue_size, 
			       my_fast_mode, my_debug_mode, my_ffmpeg_flags, 
			       my_nqueue_size_min, my_nqueue_size_max, my_ntotal_queue_size, my_naudio_queue_size);

};


extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerInit(JNIEnv* env, jobject obj)
{
        return player_init();

};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerExit(JNIEnv* env, jobject obj)
{
        __android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "Starting Player Exit");
        return player_exit();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerMain(JNIEnv* env, jobject obj, 
           jstring jfileName, 
           jint loopAfterPlay, 
           jint audioFileType,
           jint syncType,
           jint seekDuration,
           jint isStream,
           jint minAudioBufSize,
           jint minVideoBufSize)
{
        jboolean isCopy;

	int argc = 1;

        int my_loop_after_play;
        int my_audio_file_type;
        int my_sync_type;
        int my_seek_duration;
        int my_is_stream;
        int my_min_audio_buf;
        int my_min_video_buf;
    
#ifdef BROOV_C
        const char *fileString = (*env)->GetStringUTFChars(env, jfileName, &isCopy);
#else
        const char *fileString     = env->GetStringUTFChars(jfileName, &isCopy);
#endif
        char lclFileName[FILE_NAME_SIZE];

        strncpy(lclFileName, fileString, FILE_NAME_SIZE);

#ifdef BROOV_C
        (*env)->ReleaseStringUTFChars(env, jfileName, fileString);
#else
        env->ReleaseStringUTFChars(jfileName, fileString);
#endif

	char *argv[] = { lclFileName };
         
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "Starting Player main()");

        my_loop_after_play = loopAfterPlay;
        my_audio_file_type = audioFileType;
        my_sync_type= syncType;
        my_seek_duration= seekDuration;
        my_is_stream = isStream;
        my_min_audio_buf = minAudioBufSize;
        my_min_video_buf = minVideoBufSize;

        return player_main(argc, argv, my_loop_after_play, my_audio_file_type, my_sync_type, my_seek_duration, my_is_stream,my_min_audio_buf,my_min_video_buf);
}

extern C_LINKAGE 
void Java_com_broov_player_MediaPlayer_nativeSetScreenSize(JNIEnv* env, jobject obj, jint width, jint height)
{
	int screen_width 	= width;
	int screen_height 	= height;
	set_screen_size(screen_width,screen_height);
}
extern C_LINKAGE 
jint Java_com_broov_player_MediaPlayer_nativeSurfaceViewInit(JNIEnv *env,jobject obj,jobject javaSurface){
	if(nativeWindow != NULL){
		ANativeWindow_release(nativeWindow);
	}

	nativeWindow = ANativeWindow_fromSurface(env, javaSurface);
	g_screen_width = ANativeWindow_getWidth(nativeWindow);
	g_screen_height = ANativeWindow_getHeight(nativeWindow);

	return 0;

}
extern C_LINKAGE
jint Java_com_broov_player_MediaPlayer_nativeSurfaceViewDestroy(JNIEnv *env,jobject obj){
	if(nativeWindow == NULL){
		return -1;
	}	
	ANativeWindow_release(nativeWindow);
	nativeWindow = NULL;
	return 0;
}


/* for each decoded frame */
extern C_LINKAGE 
jint Java_com_broov_player_MediaPlayer_nativeGetNextDecodedFrame(JNIEnv* env,jobject obj,jobject bitmap)
{
	AndroidBitmapInfo info;
	void* pixels;
	int ret;
	jint duration = -1;
#ifndef BROOV_NO_DEBUG_LOG
//	__android_log_print(ANDROID_LOG_DEBUG,"native_main","getNextDecodedFrame is called");
#endif
	/* uint8_t == unsigned 8 bits == jboolean */
	int numBytes;
	//__android_log_print(ANDROID_LOG_DEBUG,"native_main","b4 getNext");
	uint8_t* buffer = getNextDecodedFrame(&numBytes);
	//__android_log_print(ANDROID_LOG_DEBUG,"native_main","AFTER getNext %d",numBytes);

	if(buffer == NULL || numBytes == 0){
		return -1;
	}
	
	if((ret == AndroidBitmap_getInfo(env,bitmap,&info))<0){
		return -1;
	}
	if(info.format != ANDROID_BITMAP_FORMAT_RGB_565){
		__android_log_print(ANDROID_LOG_ERROR,"native_main","bitmap is not RGB565");
		__android_log_print(ANDROID_LOG_INFO,"native_main","info.format == %d , width = %d",info.format,info.width);
		return -1;
	}
	if((ret =AndroidBitmap_lockPixels(env, bitmap, &pixels))< 0){
	}

	memcpy(pixels,buffer,numBytes);
	AndroidBitmap_unlockPixels(env,bitmap);
	duration = player_duration();
#ifndef BROOV_NO_DEBUG_LOG
	//__android_log_print(ANDROID_LOG_INFO,"native_main","num of bytes == %d",(int)numBytes);
#endif
	
//	jbyteArray nativePixels = (env)->NewByteArray(numBytes);
//	(env)->SetByteArrayRegion(nativePixels, 0, (jsize)numBytes, (jbyte*)buffer);
#ifndef BROOV_NO_DEBUG_LOG
	//__android_log_print(ANDROID_LOG_DEBUG,"native_main","end of get decoded frame");
#endif	
	return duration;
}

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerDuration(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "Get Current FileDuration");
        return player_duration();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerTotalDuration(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "Get Total Duration");
        return player_total_duration();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerPlay(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerPlay");
        return player_play();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerPause(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerPause");
        return player_pause();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerForward(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerForward");
        return player_forward();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerRewind(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerRewind");
        return player_rewind();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerPrev(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerPrev");
        return player_prev();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerNext(JNIEnv* env, jobject obj)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerNext");
        return player_next();
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerSeek(JNIEnv* env, jobject obj, jint percentage)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerSeek");
        int my_percentage = percentage;
        return player_seek(percentage);
};

extern C_LINKAGE int
Java_com_broov_player_MediaPlayer_nativePlayerSetAspectRatio(JNIEnv* env, jobject obj, jint aspectRatio)
{
        //__android_log_print(ANDROID_LOG_INFO, "BroovPlayer", "nativePlayerSetAspectRatio");
        int my_aspect_ratio= aspectRatio;
        return player_set_aspect_ratio(my_aspect_ratio);
};

//-----------------------------------------------------------------------------
extern C_LINKAGE void
Java_com_broov_player_MediaPlayer_nativeCreateSurface(JNIEnv *env, jobject thiz, jobject weakThiz)
{

    drawer = new JNIMediaPlayerDrawer(env, thiz, weakThiz);
    //set_drawer(drawer);

    __android_log_print(ANDROID_LOG_INFO, TAG, "nativeCreateSurface");
}
//-----------------------------------------------------------------------------

extern C_LINKAGE 
void Java_com_broov_player_MediaPlayer_nativeChangeSurface(JNIEnv *env, jobject thiz, int width, int height)
{

    //JNIMediaPlayerDrawer *drawer = (JNIMediaPlayerDrawer*) (get_drawer());
	__android_log_print(ANDROID_LOG_INFO,TAG,"calling setViewPortSize(%d,%d)",width,height);
    if(drawer)
        drawer->setViewportSize(width, height);


    __android_log_print(ANDROID_LOG_INFO, TAG, "nativeChangeSurface");
}
//-----------------------------------------------------------------------------

extern C_LINKAGE  void 
Java_com_broov_player_MediaPlayer_nativeSetZoomCoeff(JNIEnv *env, jobject thiz, float zoom)
{
      //JNIMediaPlayerDrawer *drawer = (JNIMediaPlayerDrawer*) (get_drawer());

      if(drawer)
        drawer->setZoomCoeff(zoom);
}
//-----------------------------------------------------------------------------
extern C_LINKAGE void 
Java_com_broov_player_MediaPlayer_nativeVideoRefresh(JNIEnv *env,jobject thiz)
{
	refreshVideo();
}

#undef C_LINKAGE

