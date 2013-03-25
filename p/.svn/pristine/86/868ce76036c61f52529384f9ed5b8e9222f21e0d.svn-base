package com.broov.player;

import android.media.AudioTrack;
import android.media.AudioManager;
import android.media.AudioFormat;

import java.lang.Thread;

class AudioDriver {

	private AudioTrack audioTrack; //Audio Track
	private byte[] audioBuffer; //Audio Buffer
	
	public byte[] getBuffer() {	return audioBuffer; }

	public AudioDriver()
 	{
		System.out.println("Inside AudioDriver");
		audioTrack       = null;
		audioBuffer 	 = null;
		nativeAudioInitJavaCallbacks();
	}

	public int writeAudio()
	{
		if (audioTrack ==null) return 0;
		if (audioBuffer==null) return 0;

		return audioTrack.write(audioBuffer, 0, audioBuffer.length);		
	}

	public int nwriteAudio(int offset, int len)
	{
		if (audioTrack ==null) return 0;
		if (audioBuffer==null) return 0;		
		if (len >= audioBuffer.length) len = audioBuffer.length;
		if (offset >= audioBuffer.length) offset = 0;

		return audioTrack.write(audioBuffer, offset, len);				
	}
	

	public int openAudio(int rate, int channels, int encoding, int bufSize)
	{
		//System.out.println("Inside openAudio::java::"+rate+"::channels::"+channels+"::encoding::"+encoding+"::bufSize::"+bufSize);

		if (audioTrack != null) return 0;

		channels = (channels == 1) ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;

		encoding = (encoding == 1) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;

		//System.out.println("Channels:"+channels+" Encoding:"+encoding);
		int minBufSize = AudioTrack.getMinBufferSize(rate, channels, encoding);
		if (minBufSize > bufSize) {
			bufSize = minBufSize;
		}

		//System.out.println("AudioTrack BufferSize:"+bufSize);

		audioBuffer = new byte[bufSize];

		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, rate, channels, encoding, bufSize, AudioTrack.MODE_STREAM);
		audioTrack.play();

		return audioBuffer.length;
	}

	public int closeAudio()
	{
		System.out.println("Inside closeAudio");
		if (audioTrack == null) return 0;
					
		audioTrack.stop();
		audioTrack.release();
		audioTrack = null;
		
		audioBuffer = null;
		return 1;
	}
	
	public int pauseAudio()
	{
		System.out.println("Inside pauseAudio");
		if (audioTrack == null) return 0;
		
		audioTrack.pause();
		return 1;		
	}

	public int resumeAudio()
	{
		System.out.println("Inside resumeAudio");
		if (audioTrack == null) return 0;
		
		audioTrack.play();
		return 1;			
	}

	public int setHighPriority()
	{
		//System.out.println("Inside setHighPriority()");	
		// Make audio thread priority higher so audio thread won't get underrun
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		return 1;
	}
	
	private native int nativeAudioInitJavaCallbacks();	

//	public int getMinimumBufferSize(int rate, int channels, int encoding)
//	{
//		System.out.println("Inside getMinimumBufferSize");
//		channels = (channels == 1) ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
//
//		encoding = (encoding == 1) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
//
//		return AudioTrack.getMinBufferSize(rate,  channels, encoding);
//	}

}
