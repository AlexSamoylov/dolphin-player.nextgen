package com.broov.player;


interface OnCompletionListener{

	int onCompletion();

}// interface OncompletinListener Ends

interface OnPreparedListener{
	int onPrepared();
}// interface OnPrepared Ends

interface OnBufferingListener{
	int onBuffering(int percentage);
} //interface OnBuferring Ends
interface OnSizeChangedListener{
	int onSizeChangedListener(int width,int height);
}
interface OnDurationChangedListener{
	int onDurationChangedListener(int duration);
}

interface ZoomListener{
	
}

interface OnServiceConnectedListener{
	void onServiceConntected(MediaPlayer _mediaPlayer);
}