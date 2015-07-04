package jp.co.spajam.honsenapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by kenjendo on 2015/07/04.
 */
public class YellApplication extends Application {

	public static final String TAG = YellApplication.class.getSimpleName();
	public static final String PREFERENCES_FILE_NAME = "YELL";
	public static final String PREF_NICKNAME_KEY = "NICKNAME";
	public static final String DEFAULT_NICKNAME = "名無しさん";

	private static SharedPreferences pref;
	private static Resources res;

	@Override
	public void onCreate() {
		/** Called when the Application-class is first created. */
		Log.d(TAG, "--- onCreate() in ---");
		pref = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
		res = getApplicationContext().getResources();
	}

	@Override
	public void onTerminate() {
		/** This Method Called when this Application finished. */
		Log.d(TAG, "--- onTerminate() in ---");
	}



	/**
	 * ニックネームをプリファレンスに保存する
	 * @param nickname
	 */
	public static void saveNickname(String nickname) {
		if (pref == null) {
			Log.e(TAG,"pref is null");
		}
		if (nickname.equals("")) {
			nickname = DEFAULT_NICKNAME;
		}
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(PREF_NICKNAME_KEY, nickname);
		editor.commit();
	}

	public static String loadNickname() {
		if (pref == null) {
			Log.e(TAG,"pref is null");
		}
		String nickname = pref.getString(PREF_NICKNAME_KEY, DEFAULT_NICKNAME);
		return nickname;
	}

	public static int dp2int(int dp) {
		if (res == null) {
			Log.e(TAG,"res is null");
		}
		DisplayMetrics metrics = res.getDisplayMetrics();
		int padding = (int) (metrics.density * dp);
		return padding;
	}


}