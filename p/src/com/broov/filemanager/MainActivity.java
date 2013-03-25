package com.broov.filemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import com.broov.commons.Feedback;
import com.broov.commons.Globals;
import com.broov.commons.Settings;

import com.broov.playerM.*;
import com.broov.playerN.*;
import com.broov.playerx86.*;
import com.broov.player.*;

public class MainActivity extends TabActivity {

	private static final int MENU_HELP = 0x071; // option submenu id
	private static final int MENU_ABOUTUS = 0x072; // option submenu id
	private static final int MENU_FEEDBACK = 0x073; // option submenu id

	TabHost mTabHost;
	StringBuffer audio_buffer, video_buffer;
	private Intent is = new Intent();
	static Context mContext;
	public static ArrayList<String> audio_array_list, video_array_list;
	String external_path;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.dolphin_main);
		mContext = this;
		mTabHost = getTabHost();
		audio_buffer = new StringBuffer();
		video_buffer = new StringBuffer();

		audio_array_list = new ArrayList<String>();
		video_array_list = new ArrayList<String>();

		TabSpec explorerspec = mTabHost.newTabSpec("");
		explorerspec.setIndicator(mContext.getString(R.string.explorer),
				getResources().getDrawable(R.drawable.ic_tab_archive));
		Intent explorerIntent = new Intent(this, FileExplorer.class);
		explorerspec.setContent(explorerIntent);

		TabSpec audiospec = mTabHost.newTabSpec("");
		audiospec.setIndicator(mContext.getString(R.string.audio),
				getResources().getDrawable(R.drawable.ic_tab_audio));
		Intent audioIntent = new Intent(this, AudioFileAdapter.class);
		audiospec.setContent(audioIntent);

		TabSpec videospec = mTabHost.newTabSpec("");
		videospec.setIndicator(mContext.getString(R.string.video),
				getResources().getDrawable(R.drawable.ic_tab_video));
		Intent videoIntent = new Intent(this, VideoFileAdapter.class);
		videospec.setContent(videoIntent);

		TabSpec settingspec = mTabHost.newTabSpec("");
		settingspec.setIndicator(mContext.getString(R.string.setting),
				getResources().getDrawable(R.drawable.ic_tab_settings));
		Intent settingIntent = new Intent(this, Settings.class);

		savePreferenceIntent(settingIntent);
		settingspec.setContent(settingIntent);

		TabSpec exitspec = mTabHost.newTabSpec("");
		exitspec.setIndicator("Exit", getResources().getDrawable(R.drawable.ic_tab_exit));		

		Intent exitIntent = new Intent(this, Settings.class);
		exitspec.setContent(exitIntent);

		mTabHost.addTab(explorerspec);
		mTabHost.addTab(audiospec);
		mTabHost.addTab(videospec);
		mTabHost.addTab(settingspec);
		mTabHost.addTab(exitspec);

		for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
		{
			mTabHost.getTabWidget().getChildAt(i)
			.setBackgroundResource(R.drawable.s_tab_background);
		} 

		File root = Environment.getExternalStorageDirectory();
		external_path = root.toString();
		external_path = external_path.toLowerCase();
		if(external_path.indexOf("mnt")!=-1)
		{
			external_path = "/mnt";
		}
		else
		{
			external_path = "/sdcard"; 
		}

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {
				int selectedTab_item = mTabHost.getCurrentTab();

				if (selectedTab_item == 4) {
					SaveSettings();
					MediaPlayer.unBindService(getApplicationContext());
					Intent intent = new Intent ();
					intent.setAction(Globals.STOPSERVICE);
					stopService(intent);
					finish();
				}
			}
		});
		new backgroundLoadListView().execute();
	}

	public void listFiles(File directory,
			FilenameFilter audio_filter, FilenameFilter video_filter,
			int recurse) {

		if (directory == null) { return; }
		if (recurse<=0) { return; }		
		try {
			File[] entries = directory.listFiles();

			if (entries == null) return;

			if (entries.length != 0) {
				for (File entry : entries) {

					if (entry == null) {
						continue;
					}

					if (audio_filter == null
							|| audio_filter.accept(directory, entry.getName()
									.toLowerCase())) {
						//System.out.println(entry);
						audio_array_list.add(entry.toString());
					}

					if (video_filter == null
							|| video_filter.accept(directory, entry.getName()
									.toLowerCase())) {
						// System.out.println(entry);
						video_array_list.add(entry.toString());
					}

					if (entry.isDirectory()) {
						// System.out.println(entry);
						String entire_path = entry.toString();

						entire_path = entire_path.toLowerCase();

						if (entire_path.startsWith(external_path)) {
							listFiles(entry, audio_filter, video_filter, (recurse-1));
						}

					}
				}
			}
		}catch(Exception e) {

		}
	}

	public static ArrayList<String> getAudioList() {
		return audio_array_list;
	}

	public static ArrayList<String> getVideoList() {
		return video_array_list;
	}

	public class backgroundLoadListView extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			AudioVideoList();

			return null;
		}

	}

	public void AudioVideoList() {

		String files_path = "/";
		File default_path = new File(files_path);
		// System.out.println("normal sdcard :" + extStore.toString());
		FilenameFilter audio_filter = new FilenameFilter() {
			@Override
			public boolean accept(File default_path, String filename) {
				// TODO Auto-generated method stub

				return (FileManager.isAudioFile(filename));
			}
		};
		FilenameFilter video_filter = new FilenameFilter() {
			@Override
			public boolean accept(File default_path, String filename) {
				// TODO Auto-generated method stub

				return (FileManager.isVideoFile(filename));
			}
		};

		listFiles(default_path, audio_filter, video_filter, 12);

		String[] audio_string_array = audio_array_list
				.toArray(new String[audio_array_list.size()]);

		for (int i = 0; i < audio_string_array.length; i++) {
			String buffer_text = audio_string_array[i].toString();
			audio_buffer.append(buffer_text + "\n");
		}
		String audio_data = new String(audio_buffer);
		audioWriteData(audio_data);

		String[] video_string_array = video_array_list
				.toArray(new String[video_array_list.size()]);

		for (int i = 0; i < video_string_array.length; i++) {
			String buffer_text = video_string_array[i].toString();
			video_buffer.append(buffer_text + "\n");
		}
		String video_data = new String(video_buffer);
		videoWriteData(video_data);
	}

	public void audioWriteData(String data) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try {
			fOut = openFileOutput("audiofilesettings.dat", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.close();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	public void videoWriteData(String data) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try {
			fOut = openFileOutput("videofilesettings.dat", Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.close();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}

	}

	public void SaveSettings() {
		boolean check_hidden = is.getBooleanExtra("HIDDEN", Globals.dbHide);
		boolean subtitle = is.getBooleanExtra("SUBTITLE", Globals.dbSubtitle);
		int color = is.getIntExtra("COLOR", Globals.dbColor);
		int sort = is.getIntExtra("SORT", Globals.dbSort);
		int audio_loop = is.getIntExtra("AUDIOLOOP", Globals.dbAudioLoop);
		int video_loop = is.getIntExtra("VIDEOLOOP", Globals.dbVideoLoop);
		int subtitlesize = is.getIntExtra("SUBTITLESIZE",
				Globals.dbSubtitleSize);
		int subtitleencoding = is.getIntExtra("SUBTITLEENCODING",
				Globals.dbSubtitleEncoding);
		String defaulthome = is.getStringExtra("HOME");
		String subtitlefont = is.getStringExtra("SUBTITLEFONT");
		boolean skipframes = is.getBooleanExtra("SKIPFRAME",
				Globals.dbSkipframes);
		// advanced
		boolean advskipframes = is.getBooleanExtra("ADVSKIPFRAMES",
				Globals.dbadvancedskip);
		boolean advbidirectional = is.getBooleanExtra("BIDIRECTIONAL",
				Globals.dbadvancedbidirectional);
		boolean advffmpeg = is.getBooleanExtra("ADVFFMPEG",
				Globals.dbadvancedffmpeg);
		// dropdown
		int advyuv2rgb = is.getIntExtra("ADVYUV2RGB", Globals.dbadvancedyuv);
		int advminvideoq = is.getIntExtra("ADVMINVIDEOQ",
				Globals.dbadvancedminvideoq);

		int advmaxvideoq = is.getIntExtra("ADVMAXVIDEOQ",
				Globals.dbadvancedmaxvideoq);
		int advmaxaudioq = is.getIntExtra("ADVMAXAUDIOQ",
				Globals.dbadvancedmaxaudioq);
		int advstreamminvideoq = is.getIntExtra("ADVSTREAMMINVIDEOQ",
				Globals.dbadvancedstreamminvideoq);
		int advstreammaxvideoq = is.getIntExtra("ADVSTREAMMAXVIDEOQ",
				Globals.dbadvancedstreammaxvideoq);
		int advstreammaxaudioq = is.getIntExtra("ADVSTREAMMAXAUDIOQ",
				Globals.dbadvancedstreammaxaudioq);
		int advpixelformat = is.getIntExtra("ADVPIXELFORMAT",
				Globals.dbadvancedpixelformat);
		int advavsyncmode = is.getIntExtra("ADVAVSYNCMODE",
				Globals.dbadvancedavsyncmode);
		boolean advdebug = is.getBooleanExtra("ADVDEBUG",
				Globals.dbadvanceddebug);
		int advswsscaler = is.getIntExtra("ADVSWSSCALER",
				Globals.dbadvancedswsscaler);

		FileExplorer.writeSettings(check_hidden, subtitle, color, sort,
				audio_loop, video_loop, subtitlesize, subtitleencoding,
				defaulthome, subtitlefont, skipframes, advskipframes,
				advbidirectional, advffmpeg, advyuv2rgb, advminvideoq,
				advmaxvideoq, advmaxaudioq, advstreamminvideoq,
				advstreammaxvideoq, advstreammaxaudioq, advpixelformat,
				advavsyncmode, advdebug, advswsscaler);

	}

	public void savePreferenceIntent(Intent settingIntent) {
		SharedPreferences settings = getSharedPreferences(Globals.PREFS_NAME,
				MODE_PRIVATE);

		Globals.dbHide = settings.getBoolean(Globals.PREFS_HIDDEN,
				Globals.dbHide);
		Globals.dbSubtitle = settings.getBoolean(Globals.PREFS_SUBTITLE,
				Globals.dbSubtitle);
		Globals.dbColor = settings.getInt(Globals.PREFS_COLOR, Globals.dbColor);
		Globals.dbSort = settings.getInt(Globals.PREFS_SORT, Globals.dbSort);
		Globals.dbAudioLoop = settings.getInt(Globals.PREFS_AUDIOLOOP,
				Globals.dbAudioLoop);
		Globals.dbVideoLoop = settings.getInt(Globals.PREFS_VIDEOLOOP,
				Globals.dbVideoLoop);
		Globals.dbSubtitleSize = settings.getInt(Globals.PREFS_SUBTITLESIZE,
				Globals.dbSubtitleSize);
		Globals.dbLastOpenDir = settings.getString(Globals.PREFS_LASTOPENDIR,
				Globals.dbLastOpenDir);
		Globals.dbSubtitleEncoding = settings.getInt(
				Globals.PREFS_SUBTITLEENCODING, Globals.dbSubtitleEncoding);
		Globals.dbDefaultHome = settings.getString(Globals.PREFS_DEFAULTHOME,
				Globals.dbDefaultHome);
		Globals.dbSubtitleFont = settings.getString(Globals.PREFS_SUBTITLEFONT,
				Globals.dbSubtitleFont);
		Globals.dbSkipframes = settings.getBoolean(Globals.PREFS_SKIPFRAME,
				Globals.dbSkipframes);
		// System.out.println("On Main Start skipframes:"+Globals.dbSkipframes);
		Globals.dbadvancedskip = settings.getBoolean(
				Globals.PREFS_ADVSKIPFRAMES, Globals.dbadvancedskip);
		Globals.dbadvancedbidirectional = settings.getBoolean(
				Globals.PREFS_BIDIRECTIONAL, Globals.dbadvancedbidirectional);
		Globals.dbadvancedffmpeg = settings.getBoolean(Globals.PREFS_ADVFFMPEG,
				Globals.dbadvancedffmpeg);
		Globals.dbadvancedyuv = settings.getInt(Globals.PREFS_ADVYUV2RGB,
				Globals.dbadvancedyuv);

		Globals.dbadvancedminvideoq = settings.getInt(
				Globals.PREFS_ADVMINVIDEOQ, Globals.dbadvancedminvideoq);
		Globals.dbadvancedmaxvideoq = settings.getInt(
				Globals.PREFS_ADVMAXVIDEOQ, Globals.dbadvancedmaxvideoq);
		Globals.dbadvancedmaxaudioq = settings.getInt(
				Globals.PREFS_ADVMAXAUDIOQ, Globals.dbadvancedmaxaudioq);
		Globals.dbadvancedstreamminvideoq = settings.getInt(
				Globals.PREFS_ADVSTREAMMINVIDEOQ,
				Globals.dbadvancedstreamminvideoq);
		Globals.dbadvancedstreammaxvideoq = settings.getInt(
				Globals.PREFS_ADVSTREAMMAXVIDEOQ,
				Globals.dbadvancedstreammaxvideoq);
		Globals.dbadvancedstreammaxaudioq = settings.getInt(
				Globals.PREFS_ADVSTREAMMAXAUDIOQ,
				Globals.dbadvancedstreammaxaudioq);
		Globals.dbadvanceddebug = settings.getBoolean(Globals.PREFS_ADVDEBUG,
				Globals.dbadvanceddebug);
		Globals.dbadvancedpixelformat = settings.getInt(
				Globals.PREFS_ADVPIXELFORMAT, Globals.dbadvancedpixelformat);
		Globals.dbadvancedavsyncmode = settings.getInt(
				Globals.PREFS_ADVAVSYNCMODE, Globals.dbadvancedavsyncmode);
		Globals.dbadvancedswsscaler = settings.getInt(
				Globals.PREFS_ADVSWSSCALER, Globals.dbadvancedswsscaler);

		settingIntent.putExtra("HIDDEN", Globals.dbHide);
		settingIntent.putExtra("SUBTITLE", Globals.dbSubtitle);
		settingIntent.putExtra("COLOR", Globals.dbColor);
		settingIntent.putExtra("AUDIOLOOP", Globals.dbAudioLoop);
		settingIntent.putExtra("VIDEOLOOP", Globals.dbVideoLoop);
		settingIntent.putExtra("SORT", Globals.dbSort);
		settingIntent.putExtra("SUBTITLESIZE", Globals.dbSubtitleSize);
		settingIntent.putExtra("SUBTITLEENCODING", Globals.dbSubtitleEncoding);
		settingIntent.putExtra("HOME", Globals.dbDefaultHome);
		settingIntent.putExtra("SUBTITLEFONT", Globals.dbSubtitleFont);
		settingIntent.putExtra("SKIPFRAME", Globals.dbSkipframes);

		// advanced
		settingIntent.putExtra("ADVSKIPFRAMES", Globals.dbadvancedskip);
		settingIntent
		.putExtra("BIDIRECTIONAL", Globals.dbadvancedbidirectional);
		settingIntent.putExtra("ADVFFMPEG", Globals.dbadvancedffmpeg);
		settingIntent.putExtra("ADVYUV2RGB", Globals.dbadvancedyuv);
		settingIntent.putExtra("ADVMINVIDEOQ", Globals.dbadvancedminvideoq);
		settingIntent.putExtra("ADVMAXVIDEOQ", Globals.dbadvancedmaxvideoq);
		settingIntent.putExtra("ADVMAXAUDIOQ", Globals.dbadvancedmaxaudioq);
		settingIntent.putExtra("ADVSTREAMMINVIDEOQ",
				Globals.dbadvancedstreamminvideoq);
		settingIntent.putExtra("ADVSTREAMMAXVIDEOQ",
				Globals.dbadvancedstreammaxvideoq);
		settingIntent.putExtra("ADVSTREAMMAXAUDIOQ",
				Globals.dbadvancedstreammaxaudioq);
		settingIntent.putExtra("ADVPIXELFORMAT", Globals.dbadvancedpixelformat);
		settingIntent.putExtra("ADVDEBUG", Globals.dbadvanceddebug);
		settingIntent.putExtra("ADVAVSYNCMODE", Globals.dbadvancedavsyncmode);
		settingIntent.putExtra("ADVSWSSCALER", Globals.dbadvancedswsscaler);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_HELP, 0, R.string.help).setIcon(R.drawable.help);
		menu.add(0, MENU_ABOUTUS, 0, R.string.about).setIcon(R.drawable.about);
		menu.add(0, MENU_FEEDBACK, 0, R.string.feedback).setIcon(R.drawable.feedback);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_HELP:
			showDialog(MENU_HELP);
			return true;

		case MENU_ABOUTUS:
			showDialog(MENU_ABOUTUS);
			return true;

		case MENU_FEEDBACK:
			Intent Feedback = new Intent(this, Feedback.class);
			startActivity(Feedback);
			return true;

		}
		return false;
	}

	/*
	 * ================Menus, options menu and context menu end
	 * here=================
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog = new Dialog(MainActivity.this);

		switch (id) {

		case MENU_ABOUTUS: {
			AlertDialog.Builder builder;
			AlertDialog alertDialog;

			System.out.println("About click");
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.custom_dialog,
					(ViewGroup) findViewById(R.id.layout_root));

			builder = new AlertDialog.Builder(mContext);
			builder.setView(layout);
			alertDialog = builder.create();
			alertDialog.setTitle(R.string.about);
			alertDialog.setIcon(R.drawable.icon);
			WebView contentText = (WebView) layout
					.findViewById(R.id.webviewcustom);
			contentText.setBackgroundColor(Color.parseColor("#EFEFEF"));

			contentText.loadData(Globals.aboutUsContent, "text/html", "utf-8");

			alertDialog.setButton(mContext.getString(R.string.ok),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return alertDialog;
		}

		// break;
		case MENU_HELP: {
			AlertDialog.Builder builder;
			AlertDialog alertDialog;

			LayoutInflater inflaterhelp = (LayoutInflater) mContext
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layouthelp = inflaterhelp.inflate(R.layout.custom_dialog,
					(ViewGroup) findViewById(R.id.layout_root));

			WebView contentTextHelp = (WebView) layouthelp
					.findViewById(R.id.webviewcustom);
			contentTextHelp.loadData(Globals.helpContent, "text/html", "utf-8");
			contentTextHelp.setBackgroundColor(Color.parseColor("#EFEFEF"));

			builder = new AlertDialog.Builder(mContext);
			builder.setView(layouthelp);
			alertDialog = builder.create();
			alertDialog.setTitle(R.string.help);
			alertDialog.setIcon(R.drawable.icon);

			alertDialog.setButton(mContext.getString(R.string.ok),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			return alertDialog;
		}

		} // End of Switch case
		return dialog;
	} // End of OnCreateDialog() method



}