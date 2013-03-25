#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include "broov_audio.h"
static jobject *JavaMediaPlayer 	= NULL;
static jmethodID JavaOnCompletion 	= NULL;
static jmethodID JavaOnPrepared 	= NULL;
static jmethodID JavaOnBuffering	= NULL;
static jmethodID JavaSetWidthHeight 	= NULL;
extern JavaVM *jniVM;

JNIEXPORT jint JNICALL Java_com_broov_player_MediaPlayer_nativeMediaPlayerInitJavaCallbacks(JNIEnv * jniEnv, jobject thiz)
{
        __android_log_print(ANDROID_LOG_DEBUG, "MediaPPlayer callback ", "Inside nativeMediaPlayerJavaCallbacks");

        jclass JavaMediaPlayerClass = NULL;
        JavaMediaPlayer = (*jniEnv)->NewGlobalRef(jniEnv, thiz);
        JavaMediaPlayerClass 	= (*jniEnv)->GetObjectClass(jniEnv, JavaMediaPlayer);
        JavaOnCompletion	= (*jniEnv)->GetMethodID(jniEnv, JavaMediaPlayerClass, "onCompletion", "()I");
        JavaOnPrepared		= (*jniEnv)->GetMethodID(jniEnv, JavaMediaPlayerClass, "onPrepared", "()I");
        JavaOnBuffering 	= (*jniEnv)->GetMethodID(jniEnv, JavaMediaPlayerClass, "onBuffering", "(I)I");
        JavaSetWidthHeight	= (*jniEnv)->GetMethodID(jniEnv, JavaMediaPlayerClass, "setWidthHeight", "(II)I");
        return 0;
}

void broov_on_completion_listener()
{
	JNIEnv * jniEnv = NULL;
        (*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
        if( !jniEnv ) { return ; }
	__android_log_print(ANDROID_LOG_DEBUG, "BroovListeners", "Inside on completion of play ");

        if (JavaOnCompletion == NULL) return ;
	(*jniEnv)->CallIntMethod(jniEnv, JavaMediaPlayer, JavaOnCompletion);

}

void broov_on_prepared_listener()
{
	JNIEnv * jniEnv = NULL;
        (*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
        if( !jniEnv ) { return ; }
	__android_log_print(ANDROID_LOG_DEBUG, "BroovListeners", "Inside on broov_on_prepared_listener  ");

        if (JavaOnPrepared == NULL) return;
	if(JavaMediaPlayer == NULL) return;
	(*jniEnv)->CallIntMethod(jniEnv, JavaMediaPlayer, JavaOnPrepared);

}
void set_width_height(int width,int height)
{
	JNIEnv *jniEnv = NULL;
	(*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
	if( !jniEnv ) { return; }
     	
	if(JavaSetWidthHeight == NULL) return;
	(*jniEnv)->CallIntMethod(jniEnv, JavaMediaPlayer, JavaSetWidthHeight,(jint)width,(jint)height);
}
void broov_on_buffering_listener(int percentage)
{
	__android_log_print(ANDROID_LOG_DEBUG, "BroovListeners", "Inside on broov_on_buffering_listener ");
	JNIEnv * jniEnv = NULL;
        (*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
        if( !jniEnv ) { return ; }
	__android_log_print(ANDROID_LOG_DEBUG, "BroovListeners", "Inside on broov_on_buffering_listener ");

        if (JavaOnBuffering == NULL) return;
	(*jniEnv)->CallIntMethod(jniEnv, JavaMediaPlayer, JavaOnBuffering,(jint)percentage);

}
void de_init_listeners()
{
	(*jniVM)->DetachCurrentThread(jniVM);

	#ifndef BROOV_PLAYER_NO_DEBUG_LOG
	__android_log_print(ANDROID_LOG_INFO,"BroovListeners","de init listeners");
	#endif
}
void delete_global_references()
{
	
	JNIEnv *jniEnv = NULL;
	(*jniVM)->AttachCurrentThread(jniVM,&jniEnv,NULL);
	if(!jniEnv){return;}
	(*jniEnv)->DeleteGlobalRef(jniEnv,JavaMediaPlayer);
	//#ifndef BROOV_PLAYER_NO_DEBUG_LOG
	__android_log_print(ANDROID_LOG_DEBUG,"BroovListeners","free global references");
	//#endif
	JavaMediaPlayer = NULL;
}
