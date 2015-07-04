package jp.co.spajam.honsenapp;

import android.animation.ObjectAnimator;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

import butterknife.Bind;
import butterknife.ButterKnife;


public class YellActivity extends ActionBarActivity implements YellWebSocketClient.CallBackListener{

	public static final String TAG = YellApplication.class.getSimpleName();
	private String mNickname;
	private int mRootWidth;
	private int mRootHeight;
	private int mMapHeight;

	private YellWebSocketClient mWebSocketClient;

	@Bind(R.id.map)
	ImageView mMap;

	@Bind(R.id.yell_sample)
	ImageView mYellSample;

	@Bind(R.id.root)
	RelativeLayout mRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yell);
		ButterKnife.bind(this);

		mNickname = YellApplication.loadNickname();
		Log.d(TAG, "Nickname:" + mNickname);

        // WebSocketサーバーに接続
		mWebSocketClient = new YellWebSocketClient(URI.create(YellWebSocketClient.SOCKET_SERVER_URL), new Handler(), this);
		mWebSocketClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 * onResumeの後でよばれます。
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// 表示と同時にウィジェットの高さや幅を取得したいときは大抵ここで取る。
		if (hasFocus) {
			mRootWidth = mRoot.getWidth();
			mRootHeight = mRoot.getHeight();
			mMapHeight = mMap.getHeight();
		}
		start();
		super.onWindowFocusChanged(hasFocus);
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


	// ここからいろいろ処理を始める
	private void start() {
		moveTop(mYellSample, 3000);
	}

	/**
	 * X方向にターゲットを3秒かけて200移動する
	 * See http://techblog.yahoo.co.jp/programming/androidiphone/
	 *
	 * @param target
	 */
	private void animateTranslationY( ImageView target ) {
		int left = target.getLeft();
		int top = target.getTop();
		Log.d(TAG,"top"+top);

		float absoluteStartY = 0f;
		float absoluteEndY = -200f;
		absoluteEndY = (mRootHeight - target.getHeight()) * 0.5f * -1.0f;
		Log.d(TAG,"absoluteEndY" + absoluteEndY);

		// translationXプロパティを0fから200fに変化させます
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( target, "translationY", absoluteStartY, absoluteEndY );

		// 3秒かけて実行させます
		objectAnimator.setDuration( 3000 );

		// アニメーションを開始します
		objectAnimator.start();
	}

	/**
	 * 上方向にターゲットをduration秒かけて親ViewGroupの上部まで移動する
	 * See http://techblog.yahoo.co.jp/programming/androidiphone/
	 *
	 * @param target
	 */
	private void moveTop( ImageView target ,int duration ) {
		int left = target.getLeft();
		int top = target.getTop();

		float absoluteStartY = 0f; // 現在位置から
		float absoluteEndY = (top) * -1.0f; // 親RootViewの上まで

		// translationXプロパティを0fから200fに変化させます
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( target, "translationY", absoluteStartY, absoluteEndY );

		// 3秒かけて実行させます
		objectAnimator.setDuration( duration );

		// アニメーションを開始します
		objectAnimator.start();
    }

	//WebSocketClientからのコールバック
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		Log.d(TAG, "onOpen");
	}

	@Override
	public void onMessage(Yell yell) {
		Log.d(TAG, "onMessage :" + yell);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		Log.d(TAG, "onClose");
	}

	@Override
	public void onError(Exception ex) {
		Log.d(TAG, "onError :" + ex.getMessage());
	}
}
