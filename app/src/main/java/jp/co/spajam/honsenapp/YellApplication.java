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
	public static final String DEFAULT_NICKNAME = "頑張れ！";

	public static final String PREF_LAT_KEY = "LAT";
	public static final String PREF_LON_KEY = "LON";
	public static final float DEFAULT_LAT = 35.000f;
	public static final float DEFAULT_LON = 135.000f;

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

	/**
	 * 緯度経度をプリファレンスに保存する
	 * @param lat,lon
	 */
	public static void saveLatLon(float lat, float lon) {
		if (pref == null) {
			Log.e(TAG,"pref is null");
		}
		if ("".equals(lat)) {
			lat = DEFAULT_LAT;
		}
		if ("".equals(lon)) {
			lon = DEFAULT_LON;
		}
		SharedPreferences.Editor editor = pref.edit();
		editor.putFloat(PREF_LAT_KEY, lat);
		editor.putFloat(PREF_LON_KEY, lon);
		editor.commit();
	}

	/**
	 *
	 * @return
	 */
	public static float[] loadLatLon() {
		if (pref == null) {
			Log.e(TAG, "pref is null");
		}
		float[] latlon = {
				pref.getFloat(PREF_LAT_KEY, DEFAULT_LAT),
				pref.getFloat(PREF_LON_KEY, DEFAULT_LON)};
		return latlon;

	}

	public static int dp2int(int dp) {
		if (res == null) {
			Log.e(TAG,"res is null");
		}
		DisplayMetrics metrics = res.getDisplayMetrics();
		int padding = (int) (metrics.density * dp);
		return padding;
	}

	public static int getColor(int id) {
		if (res == null) {
			Log.e(TAG,"res is null");
		}
		return res.getColor(id);
	}

}