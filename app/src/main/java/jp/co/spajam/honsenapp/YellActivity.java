package jp.co.spajam.honsenapp;

import android.animation.ObjectAnimator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;


public class YellActivity extends ActionBarActivity {

	public static final String TAG = YellApplication.class.getSimpleName();
	private String mNickname;
	private int mRootWidth;
	private int mRootHeight;


	@Bind(R.id.map)
	ImageView mMap;

	@Bind(R.id.root)
	RelativeLayout mRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yell);
		ButterKnife.bind(this);

		mRootWidth = mRoot.getWidth();
		mRootHeight = mRoot.getHeight();

		mNickname = YellApplication.loadNickname();
		Log.d(TAG, "Nickname:" + mNickname);

		animateTranslationY(mMap);
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
	 * See http://techblog.yahoo.co.jp/programming/androidiphone/
	 *
	 * @param target
	 */
	private void animateTranslationY( ImageView target ) {

		float absoluteStartY = 0f;
		float absoluteEndY = -200f;
		// translationXプロパティを0fから200fに変化させます
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( target, "translationY", absoluteStartY, absoluteEndY );

		// 3秒かけて実行させます
		objectAnimator.setDuration( 3000 );

		// アニメーションを開始します
		objectAnimator.start();
	}
}
