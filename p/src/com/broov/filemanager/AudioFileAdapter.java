package com.broov.filemanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.broov.commons.Globals;

import com.broov.playerM.*;
import com.broov.playerN.*;
import com.broov.playerx86.*;
import com.broov.player.*;
import com.broov.utils.Utils;

public class AudioFileAdapter extends ListActivity {
	ArrayList<String> path_list_array, folder_list_array;
	AudioAdapter audio_file_view;
	private Timer timer;
	boolean file_true = true;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		path_list_array = new ArrayList<String>();
		folder_list_array = new ArrayList<String>();
		timer = new Timer();

		File audiofile = getBaseContext().getFileStreamPath("audiofilesettings.dat");

		if (!audiofile.exists()) {
			firstTimeLoading();
		} else {
			readAudioFilesSettings();
		}

		audio_file_view = new AudioAdapter();
		setListAdapter(audio_file_view);
	}

	public void firstTimeLoading() {
		final TimerTask doAsynchronousTask;
		final Handler handler = new Handler();

		doAsynchronousTask = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.post(new Runnable() {
					public void run() {
						File audiofile = getBaseContext().getFileStreamPath("audiofilesettings.dat");

						if (!audiofile.exists()) {
							fileSplitFirstTime(MainActivity.audio_array_list);

							audio_file_view = new AudioAdapter();
							setListAdapter(audio_file_view);
							audio_file_view.notifyDataSetChanged();
						} else {
							timer.cancel();
						}
					} //run()
				});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 5000);// execute in every 5000 ms
	}

	private static class AudioFileViewHolder {
		public TextView file_name;
		public TextView file_item;
	}

	private class AudioAdapter extends BaseAdapter {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			AudioFileViewHolder holder = null;
			holder = new AudioFileViewHolder();

			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.list_files,
						parent, false);
				holder.file_name = (TextView) convertView
						.findViewById(R.id.content_file_path);
				holder.file_item = (TextView) convertView
						.findViewById(R.id.content_path);

				convertView.setTag(holder);

			} else {
				holder = (AudioFileViewHolder) convertView.getTag();
			}
			try {

				String view_file_path = path_list_array.get(position);
				String[] split_items = view_file_path.split("<itemtag>");
				String getitem_foldername = split_items[0];
				String getitem_count = split_items[1];

				holder.file_name.setText("  " + getitem_foldername);
				holder.file_name.setTextColor(Globals.dbColor);

				int integer_item = Integer.parseInt(getitem_count);
				if (integer_item == 1 || integer_item == 0) {
					holder.file_item.setText(getitem_count + "  item");
					holder.file_item.setTextColor(Globals.dbColor);
				} else {
					holder.file_item.setText(getitem_count + "  items");
					holder.file_item.setTextColor(Globals.dbColor);
				}

				convertView.setBackgroundColor(Color.parseColor("#efefef"));

				convertView.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						
						switch (event.getAction()) {

						case MotionEvent.ACTION_MOVE:
							// movement
							v.setBackgroundColor(Color.parseColor("#efefef"));
							break;
						case MotionEvent.ACTION_CANCEL:
							// movement
							v.setBackgroundColor(Color.parseColor("#efefef"));
							break;
						case MotionEvent.ACTION_OUTSIDE:
							// movement
							v.setBackgroundColor(Color.parseColor("#efefef"));
							break;
						case MotionEvent.ACTION_DOWN:
							// touch down
							v.setBackgroundColor(Color.parseColor("#FFCC99"));
							break;
						case MotionEvent.ACTION_UP:
							// touch up
							v.setBackgroundColor(Color.parseColor("#efefef"));
							// view.setBackgroundColor(Color.GRAY);
							String item = path_list_array.get(position);
							String[] split_string_items = item
									.split("<itemtag>");
							String set_item = split_string_items[2];
							Intent intent = new Intent(AudioFileAdapter.this,
									VideoAudioListView.class);
							set_item = set_item + "broovpath" + "audiofile";
							intent.putExtra("filelistpath", set_item);
							startActivity(intent);
							break;
						default:
							// default
						}

						return true;

					}
				});
			} catch (Exception e) {

			}

			return convertView;
		}

	}

	public void readAudioFilesSettings() {
		File file = getBaseContext().getFileStreamPath("audiofilesettings.dat");

		if (file.exists()) {

			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String file_path;
				String repeat_foldername = "";
				
				while ((file_path = br.readLine()) != null) {
				
					String folder_name = null;

					if (file_path.indexOf("/") != -1) {
						String[] file_split = file_path.split("/");
						folder_name = file_split[file_split.length - 2];
						if (file_split.length > 2) {
							String second_path = file_split[file_split.length - 3];
							second_path = second_path + "/" + folder_name;
							folder_name = second_path;
						}

					} else {
						folder_name = file_path;
					}

					if (!repeat_foldername.equalsIgnoreCase(folder_name)) {
						System.out.println(folder_name);
						String remove_path = null;
						String get_item_path = null;

						String[] split_items = file_path.split("/");
						remove_path = "/" + split_items[split_items.length - 1];
						get_item_path = file_path.replace(remove_path, "");

						File dir = new File(get_item_path);
						File[] files = dir.listFiles();

						int numberOfItems = 0;

						for (int j = 0; j < files.length; j++) {
							if (FileManager.isAudioFile(files[j].toString())) {
								numberOfItems++;
							}
						}
						System.out.println(repeat_foldername);
						String number_to_string = Integer
								.toString(numberOfItems);
						if (numberOfItems != 0)
							path_list_array.add(folder_name + "<itemtag>"
									+ number_to_string + "<itemtag>"
									+ get_item_path);

					}
					repeat_foldername = folder_name;

				}

				removeDuplicates(path_list_array);
				Collections.sort(path_list_array);
			} catch (Exception e) {
				System.out.println("AudioFileAdapter :" + e.toString());

			}

		}
	}

	public static void removeDuplicates(ArrayList<String> list) {
		HashSet<String> set = new HashSet<String>(list);
		list.clear();
		list.addAll(set);
	}

	public void fileSplitFirstTime(ArrayList<String> reference_arraylist) {

		String[] update_array = reference_arraylist
				.toArray(new String[reference_arraylist.size()]);

		if (update_array.length != 0) {
			String repeat_foldername = "first file check";
			for (int i = 0; i < update_array.length - 1; i++) {
				String file_path = update_array[i];

				String folder_name = null;

				if (file_path.indexOf("/") != -1) {
					String[] file_split = file_path.split("/");
					folder_name = file_split[file_split.length - 2];
					if (file_split.length > 2) {
						String second_path = file_split[file_split.length - 3];
						second_path = second_path + "/" + folder_name;
						folder_name = second_path;
					}

				} else {
					folder_name = file_path;
				}

				if (!repeat_foldername.equals(folder_name)) {

					String remove_path = null;
					String get_item_path = null;

					String[] split_items = file_path.split("/");
					remove_path = "/" + split_items[split_items.length - 1];
					get_item_path = file_path.replace(remove_path, "");

					File dir = new File(get_item_path);
					File[] files = dir.listFiles();

					int numberOfItems = 0;

					for (int j = 0; j < files.length; j++) {
						if (FileManager.isAudioFile(files[j].toString())) {
							numberOfItems++;
						}
					}

					String number_to_string = Integer.toString(numberOfItems);
					if (numberOfItems != 0)
						path_list_array.add(folder_name + "<itemtag>"
								+ number_to_string + "<itemtag>"
								+ get_item_path);
				}
				repeat_foldername = folder_name;
			}

			removeDuplicates(path_list_array);
			Collections.sort(path_list_array);
		}
	}

}