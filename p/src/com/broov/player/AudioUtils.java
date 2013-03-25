package com.broov.player;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class AudioUtils {

	private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
	private static final String TAG = "AudioUtils";
	public static Bitmap getArtworkFromFile(Context context, long songid, long albumid) {
	        Bitmap bm = null;
	        byte [] art = null;
	        String path = null;

	        if (albumid < 0 && songid < 0) {
	            throw new IllegalArgumentException("Must specify an album or a song id");
	        }

	        try {
	            if (albumid < 0) {
	                Uri uri = Uri.parse("content://media/external/audio/media/" + songid + "/albumart");
	                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
	                if (pfd != null) {
	                    FileDescriptor fd = pfd.getFileDescriptor();
	                    bm = BitmapFactory.decodeFileDescriptor(fd);
	                }
	            } else {
	                Uri uri = ContentUris.withAppendedId(sArtworkUri, albumid);
	                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
	                if (pfd != null) {
	                    FileDescriptor fd = pfd.getFileDescriptor();
	                    bm = BitmapFactory.decodeFileDescriptor(fd);
	                    
	                }
	            }
	        } catch (IllegalStateException ex) {
	        	Log.d(TAG, "Illegal State Exception");
	        } catch (FileNotFoundException ex) {
	        	Log.d(TAG, "File Not Found");
	        }
	        
	        return bm;
	    }
}
