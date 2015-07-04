package jp.co.spajam.honsenapp;

import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by kenjendo on 2015/07/05.
 * 地域の座標をintで取得する
 */
public class MapLocation {
	private static final String TAG = MapLocation.class.getSimpleName();
	private ImageView mMap;
	private int mWidth;
	private int mHeight;
	private int mTop;
	private int mLeft;

	// サイズ取得したいのでonWindowFocusChanged()以降呼ぶ
	public MapLocation(ImageView map) {
		mMap = map;
//		int width = mMap.getLayoutParams().width; // 値がおかしいのでgetWidthの方を使う
//		int height = mMap.getLayoutParams().height;
		mWidth = mMap.getWidth();
		mHeight = mMap.getHeight();
		mTop = mMap.getTop();
		mLeft = mMap.getLeft();
	}

	public Pair<Integer,Integer> getLocation(int area) {
		int top = 0; //デフォルト
		int left = 0;
		if (area == 0) {
			top = mTop + (int)(mHeight * 0.23);
			left = mLeft + (int)(mWidth * 0.86);
			return Pair.create(top, left);
		} else if (area == 1) {
			top = mTop + (int)(mHeight * 0.63);
			left = mLeft + (int)(mWidth * 0.63);
			return Pair.create(top, left);
		} else if (area == 2) {
			top = mTop + (int)(mHeight * 0.84);
			left = mLeft + (int)(mWidth * 0.54);
			return Pair.create(top,left);
		} else if (area == 3) {
			top = mTop + (int)(mHeight * 0.82);
			left = mLeft + (int)(mWidth * 0.41);
			return Pair.create(top, left);
		} else if (area == 4) {
			top = mTop + (int)(mHeight * 0.80);
			left = mLeft + (int)(mWidth * 0.32);
			return Pair.create(top, left);
		} else if (area == 5) {
			top = mTop + (int)(mHeight * 0.71);
			left = mLeft + (int)(mWidth * 0.23);
			return Pair.create(top, left);
		} else if (area == 6) {
			top = mTop + (int)(mHeight * 0.84);
			left = mLeft + (int)(mWidth * 0.21);
			return Pair.create(top, left);
		} else if (area == 7) {
			top = mTop + (int)(mHeight * 0.82);
			left = mLeft + (int)(mWidth * 0.07);
			return Pair.create(top, left);
		} else {
			return Pair.create(top, left); // デフォルト
		}
	}
}
