#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>

JavaVM *jniVM = NULL;

static jobject JavaAudioDriver = NULL;
static jmethodID JavaOpenAudio = NULL;
static jmethodID JavaCloseAudio = NULL;
static jmethodID JavaPauseAudio = NULL;
static jmethodID JavaResumeAudio = NULL;

static JNIEnv   *jniEnvPlaying = NULL;
static jmethodID JavaWriteAudio = NULL;
static jmethodID JavaNWriteAudio = NULL;

static jbyteArray audioBufferJNI = NULL;

int audioBufferSize = 0;
unsigned char *audioBuffer = NULL;
//unsigned char* get_audio_buffer() { return audioBuffer; }

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
	__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Inside JNI_OnLoad androidaudio");
	jniVM = vm;
	return JNI_VERSION_1_2;
};

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved)
{	
	__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Inside JNI_OnUnload androidaudio");
	jniVM = vm;
};

JNIEXPORT jint JNICALL Java_com_broov_player_AudioDriver_nativeAudioInitJavaCallbacks(JNIEnv * jniEnv, jobject thiz)
{
	__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Inside nativeAudioInitJavaCallbacks");
	jclass JavaAudioDriverClass = NULL;
	JavaAudioDriver = (*jniEnv)->NewGlobalRef(jniEnv, thiz);
	JavaAudioDriverClass = (*jniEnv)->GetObjectClass(jniEnv, JavaAudioDriver);
	JavaOpenAudio = (*jniEnv)->GetMethodID(jniEnv, JavaAudioDriverClass, "openAudio", "(IIII)I");
	JavaCloseAudio = (*jniEnv)->GetMethodID(jniEnv, JavaAudioDriverClass, "closeAudio", "()I");
	JavaPauseAudio = (*jniEnv)->GetMethodID(jniEnv, JavaAudioDriverClass, "pauseAudio", "()I");
	JavaResumeAudio = (*jniEnv)->GetMethodID(jniEnv, JavaAudioDriverClass, "resumeAudio", "()I");
    
	return 0;
}


static void CloseAudio()
{
	JNIEnv * jniEnv = NULL;
	(*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
	if( !jniEnv ) { return ; }

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Inside CloseAudio");

	(*jniEnv)->DeleteGlobalRef(jniEnv, audioBufferJNI);
	audioBufferJNI = NULL;
	audioBuffer = NULL;

	(*jniEnv)->CallIntMethod(jniEnv, JavaAudioDriver, JavaCloseAudio);
	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "End of CloseAudio");
}

static int OpenAudio(int freq, int channels, int encoding, int nsamples)
{
	JNIEnv * jniEnv = NULL;

	(*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
	if( !jniEnv ) { return -1; }

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Inside OpenAudio: %d %d %d %d", freq, channels, encoding, nsamples);

	// The returned audioBufferSize may be huge, up to 100 Kb for 44100 because user may have selected large audio buffer to get rid of choppy sound
	audioBufferSize = (*jniEnv)->CallIntMethod( jniEnv, JavaAudioDriver, JavaOpenAudio, 
			(jint)freq, (jint)channels, (jint) encoding, (jint)(nsamples));

	if (audioBufferSize == 0) {   
		CloseAudio(); 
		return -1;
	}

	return audioBufferSize;
}

void InitAudio()
{
	jclass JavaAudioDriverClass = NULL;
	jmethodID JavaSetHighPriority = NULL;
	jmethodID JavaGetBuffer = NULL;
	jboolean isCopy = JNI_TRUE;

	(*jniVM)->AttachCurrentThread(jniVM, &jniEnvPlaying, NULL);

	JavaAudioDriverClass = (*jniEnvPlaying)->GetObjectClass(jniEnvPlaying, JavaAudioDriver);
	JavaWriteAudio = (*jniEnvPlaying)->GetMethodID(jniEnvPlaying, JavaAudioDriverClass, "writeAudio", "()I");

	JavaNWriteAudio = (*jniEnvPlaying)->GetMethodID(jniEnvPlaying, JavaAudioDriverClass, "nwriteAudio", "(II)I");

	JavaSetHighPriority = (*jniEnvPlaying)->GetMethodID(jniEnvPlaying, JavaAudioDriverClass, "setHighPriority", "()I");
	(*jniEnvPlaying)->CallIntMethod(jniEnvPlaying, JavaAudioDriver, JavaSetHighPriority);

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "After setHighPriority to Thread");

	JavaGetBuffer = (*jniEnvPlaying)->GetMethodID(jniEnvPlaying, JavaAudioDriverClass, "getBuffer", "()[B");
	audioBufferJNI = (*jniEnvPlaying)->CallObjectMethod(jniEnvPlaying, JavaAudioDriver, JavaGetBuffer);
	audioBufferJNI = (*jniEnvPlaying)->NewGlobalRef(jniEnvPlaying, audioBufferJNI);

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Before AudioGetBuffer");

	audioBuffer = (unsigned char *) (*jniEnvPlaying)->GetByteArrayElements(jniEnvPlaying, audioBufferJNI, &isCopy);
	if( !audioBuffer ) { return; }

	if( isCopy == JNI_TRUE ) { /*JNI returns a copy of byte array - no audio will be played */ }
	__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "End of InitAudio");

};

void DeinitAudio() 
{
	(*jniVM)->DetachCurrentThread(jniVM);
};

static void PlayAudio()
{
	jboolean isCopy = JNI_TRUE;
	//int wroteBuf;

	(*jniEnvPlaying)->ReleaseByteArrayElements(jniEnvPlaying, audioBufferJNI, (jbyte *)audioBuffer, 0);
	audioBuffer = NULL;

	(*jniEnvPlaying)->CallIntMethod(jniEnvPlaying, JavaAudioDriver, JavaWriteAudio);

	audioBuffer = (unsigned char *) (*jniEnvPlaying)->GetByteArrayElements(jniEnvPlaying, audioBufferJNI, &isCopy);

	if( !audioBuffer ) { /* JNI::GetByteArrayElements() failed! we will crash now */ }
	if( isCopy == JNI_TRUE ) { /* JNI returns a copy of byte array - that's slow */	}

        //return wroteBuf;
}

static int PlayAudioOfLen(int offset, int len)
{
	jboolean isCopy = JNI_TRUE;
	int wroteBuf;

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Inside PlayAudioOfLen");

	(*jniEnvPlaying)->ReleaseByteArrayElements(jniEnvPlaying, audioBufferJNI, (jbyte *)audioBuffer, 0);
	audioBuffer = NULL;

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Calling NWriteAudio");

	wroteBuf = (*jniEnvPlaying)->CallIntMethod(jniEnvPlaying, JavaAudioDriver, JavaNWriteAudio, (jint) offset, (jint) len);

	audioBuffer = (unsigned char *) (*jniEnvPlaying)->GetByteArrayElements(jniEnvPlaying, audioBufferJNI, &isCopy);

	if( !audioBuffer ) { /* JNI::GetByteArrayElements() failed! we will crash now */ }
	if( isCopy == JNI_TRUE ) { /* JNI returns a copy of byte array - that's slow */	}

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "End of PlayAudioOfLen");

	return wroteBuf;
}

int BroovNDK_PauseAudio(void)
{
	if (jniVM) {
		JNIEnv * jniEnv = NULL;
		(*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
		return (*jniEnv)->CallIntMethod( jniEnv, JavaAudioDriver, JavaPauseAudio);
	}

	return 0;
};

int BroovNDK_ResumeAudio(void)
{	
	if (jniVM) {
		JNIEnv * jniEnv = NULL;
		(*jniVM)->AttachCurrentThread(jniVM, &jniEnv, NULL);
		return (*jniEnv)->CallIntMethod( jniEnv, JavaAudioDriver, JavaResumeAudio);
	}

	return 0;
};

int audio_open(int rate, int channels, int bits_per_sample, int bufSize)
{
    int actual_size;

	if (channels == 0)
		channels = 2;

	if (rate == 0)
		rate = 44100;

	if (bits_per_sample == 0)
	{
		bits_per_sample = 16;
	}

	actual_size = OpenAudio(rate, channels, (bits_per_sample==16?1:0), (bufSize));

	if (actual_size <= 0)
	{
		return actual_size;
	}

	//InitAudio();

	return actual_size;
}

void audio_write()
{
        //int actual;
	//memcpy(audioBuffer, buf, len);

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Writing %d bytes", n);
	//actual = PlayAudioOfLen(0, len);
	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Wrote %d bytes", actual);

	//Logic for writing when AudioTrack.write writes less than passed bytes
	//need to move the audio buffer and pass the rest accordingly
	//for (i = 0; i < n; i += actual) do {
	//   actual =PlayAudioOfLen(len); 
	//} while (actual < 0);

	//return len;

        signed short sample;
        signed short *sbuf= (signed short*) (audioBuffer);
        unsigned char *cbuf= (unsigned char*) (audioBuffer);
        //signed short *dbuf= (signed short*) (audioBuffer);
        int l=audioBufferSize/2;
        int i;

	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Writing %d bytes", audioBufferSize);
        for (i=0; i<l; i++, sbuf++, cbuf+=2)
        {
           sample = *sbuf;
           cbuf[0] = (sample & 0xff);
           cbuf[1] = ((sample >>8) & 0xff);
        }

	PlayAudio();
	//actual = PlayAudio();
	//__android_log_print(ANDROID_LOG_DEBUG, "BroovPlayer", "Wrote %d bytes of %d %d", actual, audioBufferSize, l);
}

void audio_close()
{
	CloseAudio();
}


