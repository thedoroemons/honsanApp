package jp.co.spajam.honsenapp;

import android.util.Log;
import android.widget.ImageView;

/**
 * Created by kenjendo on 2015/07/05.
 * 地域の座標をintで取得する
 */
public class MapLocation {
	private static final String TAG = MapLocation.class.getSimpleName();
	private ImageView mMap;

	// サイズ取得したいのでonWindowFocusChanged()以降呼ぶ
	public MapLocation(ImageView map) {
		mMap = map;
		int width = mMap.getLayoutParams().width;
		int height = mMap.getLayoutParams().height;
		int top = mMap.getTop();
		int left = mMap.getLeft();
		Log.d(TAG,"w"+width+"h"+height);
	}
}
