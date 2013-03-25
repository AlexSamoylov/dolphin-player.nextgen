package com.broov.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.broov.commons.Globals;
import com.broov.player.AudioPlayer;
import com.broov.player.VideoPlayer;

import com.broov.playerM.*;
import com.broov.playerN.*;
import com.broov.playerx86.*;
import com.broov.player.*;

public class VideoAudioListView extends ListActivity {
	private final int KB = 1024;
	private final int MB = KB * KB;
	private final int GB = MB * KB;
	private String display_size;
	ArrayList<String> path_list_array,folder_list_array;
	HashMap<String, String> uniq_map;
	VideoAudioAdapter video_audio_fileview;
	String get_foldername,get_filetype;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		path_list_array = new ArrayList<String>();
		folder_list_array = new ArrayList<String>();
		uniq_map = new HashMap<String, String>();
		Intent intent= getIntent();
		String name = intent.getStringExtra("filelistpath");
		String[] split_file_type = name.split("broovpath");
		get_foldername = split_file_type[0];
		get_filetype = split_file_type[1];
		readFileList(get_foldername);
		
		video_audio_fileview = new VideoAudioAdapter();
		setListAdapter(video_audio_fileview);			
	}

	private static class AudioFileViewHolder {
		public TextView file_name;
		public TextView file_size;
		public ImageView image_type;
		
	}

	private class VideoAudioAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return path_list_array.size();
		}

		@Override
		public String getItem(int position) {
			return path_list_array.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			AudioFileViewHolder holder = null;
			holder = new AudioFileViewHolder();

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.video_audio_list,
						parent, false);
				holder.file_name = (TextView) convertView
						.findViewById(R.id.video_audio_filename);
				holder.file_size = (TextView) convertView
						.findViewById(R.id.video_audio_filesize);
				holder.image_type =(ImageView) convertView.findViewById(R.id.videoaudio_imageView1);
				convertView.setTag(holder);				

			} else {
				holder = (AudioFileViewHolder) convertView.getTag();
			}
			try {
                if (get_filetype.equalsIgnoreCase("audiofile"))
                {
                	holder.image_type.setBackgroundResource(R.drawable.music);
                }
                else
                {
                	holder.image_type.setBackgroundResource(R.drawable.movies);
                }
				
				String view_file_path = path_list_array.get(position);
				File file = new File(view_file_path);
				String display_filename;
				String[] file_split = view_file_path.split("/");
				display_filename = file_split[file_split.length - 1];
						
				if (file.isFile()) {
					double size = file.length();
					if (size > GB)
						display_size = String.format("%.2f Gb ", (double)size / GB);
					else if (size < GB && size > MB)
						display_size = String.format("%.2f Mb ", (double)size / MB);
					else if (size < MB && size > KB)
						display_size = String.format("%.2f Kb ", (double)size/ KB);
					else
						display_size = String.format("%.2f bytes ", (double)size);					
				} 
				
				holder.file_name.setText("  "+display_filename);
				holder.file_name.setTextColor(Globals.dbColor);
				
				holder.file_size.setText("  "+display_size);
				holder.file_size.setTextColor(Globals.dbColor);
				
				convertView.setBackgroundColor(Color.parseColor("#EFEFEF"));
				
				convertView.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						
						switch (event.getAction()) {
																	
						case MotionEvent.ACTION_DOWN:
							// touch down
							v.setBackgroundColor(Color.parseColor("#FFCC99"));
							break;
						case MotionEvent.ACTION_UP:
							// touch up
							v.setBackgroundColor(Color.parseColor("#efefef"));
							// view.setBackgroundColor(Color.GRAY);
							String item = path_list_array.get(position);
							//item = item.toLowerCase();
							File file = new File(item);

							if (file.isDirectory()) {
								
							} else if (FileManager.isSubtitleFontFile(file.getPath())) {
								String fontFilename = file.getPath();
								FileExplorer.saveAndSetSubtitleFontFile(fontFilename);

							} else if (FileManager.supportedFile(file.getPath())) {

								if (file.exists()) {
									if (FileManager.isAudioFile(file.getPath())) {
										String filename = file.getPath();
										Intent intent = new Intent(VideoAudioListView.this,
												AudioPlayer.class);
										intent.putExtra("audiofilename", filename);
										startActivity(intent);

									} else {
										String filename = file.getPath();
										Intent intent;

										intent = new Intent(VideoAudioListView.this, VideoPlayer.class);
										intent.putExtra("videofilename", filename);
										startActivity(intent);
									}
								}
							}							
							break;
						
						default:
							// default
							v.setBackgroundColor(Color.parseColor("#efefef"));
						}
						return true;				
					}
				});				
				
			} catch (Exception e) {

			}

			return convertView;
		}

	}

	private void readFileList(String get_foldername) {
		// TODO Auto-generated method stub
				
		File dir = new File(get_foldername);
        File[] files = dir.listFiles();
				
           for(int i=0;i<files.length;i++)
           {
        	  String file_path = files[i].toString();
        	  if (get_filetype.equalsIgnoreCase("audiofile") && FileManager.isAudioFile(file_path))
        	  {        	   
        	            path_list_array.add(file_path);        	   
        	  }
        	  
        	  if (get_filetype.equalsIgnoreCase("videofile") && FileManager.isVideoFile(file_path))
			  {	   
	            path_list_array.add(file_path);	  
			  }
           }
	}
		
} // end of class
