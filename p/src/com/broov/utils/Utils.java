package com.broov.utils;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.broov.player.AudioPlayer;
import com.broov.playerM.R;

//import java.lang.reflect.Method;
//import android.view.View;

public class Utils {
	/** 
	 * Returns a formated time HoursH MinutesM SecondsS
	 * 
	 * @param millis
	 * @return
	 */
	public static String formatTime(long seconds) {
		String output = "";
		//long seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;

		String secondsD = String.valueOf(seconds);
		String minutesD = String.valueOf(minutes);
		String hoursD = String.valueOf(hours); 

		if (seconds < 10)
			secondsD = "0" + seconds;
		if (minutes < 10)
			minutesD = "0" + minutes;
		if (hours < 10){
			hoursD = "0" + hours;
		}

		if( days > 0 ){
			output = days +"d ";
		} 
		if(hours > 0) {
			output += hoursD + ":";
		}
		//output += hoursD + ":" + minutesD + ":" + secondsD;
		output += minutesD + ":" + secondsD;

		return output;
	}
	
	
	
//	
//	public static boolean audiofileFormatCheck(String fileName) {
//		fileName = fileName.toLowerCase();
//		return (fileName.endsWith(".mp3") || fileName.endsWith(".wma")
//				|| fileName.endsWith(".ogg") || fileName.endsWith(".mp2")
//				|| fileName.endsWith(".flac") || fileName.endsWith(".aac")
//				|| fileName.endsWith(".ac3") || fileName.endsWith(".amr")
//				|| fileName.endsWith(".pcm") || fileName.endsWith(".wav")
//				|| fileName.endsWith(".au") || fileName.endsWith(".aiff")
//				|| fileName.endsWith(".3g2") || fileName.endsWith(".m4a")
//				|| fileName.endsWith(".astream") || fileName.endsWith(".a52")
//				|| fileName.endsWith(".adt") || fileName.endsWith(".adts")
//				|| fileName.endsWith(".aif") || fileName.endsWith(".aifc")
//				|| fileName.endsWith(".aob") || fileName.endsWith(".ape")
//				|| fileName.endsWith(".awb") || fileName.endsWith(".dts")
//				|| fileName.endsWith(".cda") || fileName.endsWith(".it")
//				|| fileName.endsWith(".m4p") || fileName.endsWith(".mid")
//				|| fileName.endsWith(".mka") || fileName.endsWith(".mlp")
//				|| fileName.endsWith(".mod") || fileName.endsWith(".mp1")
//				|| fileName.endsWith(".mp2") || fileName.endsWith(".mpc")
//				|| fileName.endsWith(".oga") || fileName.endsWith(".oma")
//				|| fileName.endsWith(".rmi") || fileName.endsWith(".s3m")
//				|| fileName.endsWith(".spx") || fileName.endsWith(".tta")
//				|| fileName.endsWith(".voc") || fileName.endsWith(".vqf")
//				|| fileName.endsWith(".w64") || fileName.endsWith(".wv") 
//				|| fileName.endsWith(".xm"));
//
//	}
//
//	public static boolean videofileFormatCheck(String fileName) {
//		fileName = fileName.toLowerCase();
//		return (fileName.endsWith(".mp4") || fileName.endsWith(".wmv")
//				|| fileName.endsWith(".avi") || fileName.endsWith(".mkv")
//				|| fileName.endsWith(".dv") || fileName.endsWith(".rm")
//				|| fileName.endsWith(".mpg") || fileName.endsWith(".mpeg")
//				|| fileName.endsWith(".flv") || fileName.endsWith(".divx")
//				|| fileName.endsWith(".swf") || fileName.endsWith(".dat")
//				|| fileName.endsWith(".h264") || fileName.endsWith(".h263")
//				|| fileName.endsWith(".h261") || fileName.endsWith(".3gp")
//				|| fileName.endsWith(".3gpp") || fileName.endsWith(".asf")
//				|| fileName.endsWith(".mov") || fileName.endsWith(".m4v")
//				|| fileName.endsWith(".ogv") || fileName.endsWith(".vob")
//				|| fileName.endsWith(".vstream") || fileName.endsWith(".ts")
//				|| fileName.endsWith(".webm") || fileName.endsWith(".vro")
//				|| fileName.endsWith(".tts") || fileName.endsWith(".tod")
//				|| fileName.endsWith(".rmvb") || fileName.endsWith(".rec")
//				|| fileName.endsWith(".ps") || fileName.endsWith(".ogx")
//				|| fileName.endsWith(".ogm") || fileName.endsWith(".nuv")
//				|| fileName.endsWith(".nsv") || fileName.endsWith(".mxf")
//				|| fileName.endsWith(".mts") || fileName.endsWith(".mpv2")
//				|| fileName.endsWith(".mpeg1") || fileName.endsWith(".mpeg2")
//				|| fileName.endsWith(".mpeg4") || fileName.endsWith(".mpe")
//				|| fileName.endsWith(".mp4v") || fileName.endsWith(".mp2v")
//				|| fileName.endsWith(".m2ts") || fileName.endsWith(".m2t")
//				|| fileName.endsWith(".m2v") || fileName.endsWith(".m1v")
//				|| fileName.endsWith(".amv") || fileName.endsWith(".3gp2"));
//
//	}


	//Code reference: http://code.google.com/p/libgdx

	//	private static Method setSystemUiVisibilityMethod;
	//	
	//	static {
	//		initCompatibility();
	//	};
	//	
	//	public static void initCompatibility() {
	//		try {
	//			Class classView = Class.forName("android.view.View");
	//			setSystemUiVisibilityMethod = classView.getDeclaredMethod("setSystemUiVisibility", int.class);
	//		} catch(NoSuchMethodException exception) {
	//			debug("AndroidUtils", "Could not get setSystemUiVisibility method", exception);
	//		} catch (ClassNotFoundException e) {
	//			//e.printStackTrace();
	//		}
	//	}
	//	
	//	public static void hideSystemUi(View view) {
	//		int apiVersion = android.os.Build.VERSION.SDK_INT;
	//		view.getVisibility();
	//		try {
	//			if(apiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
	//				setSystemUiVisibilityMethod.invoke(view, View.SYSTEM_UI_FLAG_LOW_PROFILE);
	//
	//				//setSystemUiVisibilityMethod.invoke(view, View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	//			} else {
	//				setSystemUiVisibilityMethod.invoke(view, View.STATUS_BAR_HIDDEN);
	//			}
	//		} catch(Exception exception) {
	//			debug("AndroidUtils", "Could not invoke setSystemUiVisibility method", exception);
	//		}
	//	}
	//	
	//	public static void debug (String tag, String message, Throwable exception) {
	//			System.out.println(tag + ": " + message);
	//			//exception.printStackTrace(System.out);
	//	}

	//	OnSystemUiVisibilityChangeListener visibilityChangeListener = new OnSystemUiVisibilityChangeListener() {
	//		@Override
	//		public void  onSystemUiVisibilityChange(int state) {
	////			int SDK_INT = android.os.Build.VERSION.SDK_INT;
	////			System.out.println("SDK Version:"+SDK_INT);
	////			if(SDK_INT >= 11 && SDK_INT < 14) {
	////				getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	////			}else if(SDK_INT >= 14){
	////				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	////
	////				//getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	////				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	////
	////			}
	//
	//		}
	//	};
	//
	//	getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(visibilityChangeListener);

	//	{
	//		int SDK_INT = android.os.Build.VERSION.SDK_INT;
	//		System.out.println("SDK Version:"+SDK_INT);
	//		if (SDK_INT >=11) {
	//
	//			if(SDK_INT >= 11 && SDK_INT < 14) {
	//				getWindow().getDecorView().setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
	//			}else if(SDK_INT >= 14){
	//				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	//				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	//			}
	//		}
	//	}
	
	@SuppressLint("NewApi")
	public static void createNotification(Context _context) {
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(_context, AudioPlayer.class);
	    PendingIntent pIntent = PendingIntent.getActivity(_context, 0, intent, 0);

	 // Sets a custom content view for the notification, including an image button.
        RemoteViews layout = new RemoteViews(_context.getPackageName(), R.layout.audio_player_notify);
        layout.setImageViewBitmap(R.id.noti_next,BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_media_next));
        layout.setImageViewBitmap(R.id.noti_prev,BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_media_previous));
        layout.setImageViewBitmap(R.id.noti_play,BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_media_pause));
        
        
        
	    // Build notification
	    // Actions are just fake
	    Notification noti = new NotificationCompat.Builder(_context)
	        .setContentIntent(pIntent)
	        .setContent(layout)
	        .setAutoCancel(true)
	        
	        .build();
	    NotificationManager notificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
	    // Hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    notificationManager.notify(0, noti);

	  }

	
}
