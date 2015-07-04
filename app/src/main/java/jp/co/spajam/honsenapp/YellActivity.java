package jp.co.spajam.honsenapp;

import android.animation.ObjectAnimator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class YellActivity extends ActionBarActivity {

	public static final String TAG = YellApplication.class.getSimpleName();
	private String mNickname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yell);

		mNickname = YellApplication.loadNickname();
		Log.d(TAG, "Nickname:" + mNickname);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_yell, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	/**
	 * X方向にターゲットを3秒かけて200移動する
	 *
	 * @param target
	 */
	private void animateTranslationY( ImageView target ) {

		// translationXプロパティを0fから200fに変化させます
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( target, "translationY", 0f, 200f );

		// 3秒かけて実行させます
		objectAnimator.setDuration( 3000 );

		// アニメーションを開始します
		objectAnimator.start();
	}
}
