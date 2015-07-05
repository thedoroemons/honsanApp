package jp.co.spajam.honsenapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class YellActivity extends ActionBarActivity implements YellWebSocketClient.CallBackListener, VoiceManager.SendDataIF{

	public static final String TAG = YellActivity.class.getSimpleName();
	private String mNickname;
	private int mRootWidth;
	private int mRootHeight;
	private int mMapHeight;
	private MapLocation mMapLocation;
	private int TAMA_SIZE = 3; // volからtamaをどれくらい大きくするかの定数(大きいほどはやくおおきくなる)

	private YellWebSocketClient mWebSocketClient;
	
	@Bind(R.id.tama)
	ImageView mTama;

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

		// 音を取り始める。
		VoiceManager voiceManager = VoiceManager.getInstance(this);
		voiceManager.startRecording();

        // WebSocketサーバーに接続
		mWebSocketClient = new YellWebSocketClient(URI.create(YellWebSocketClient.SOCKET_SERVER_URL), new Handler(), this);
		mWebSocketClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VoiceManager voiceManager = VoiceManager.getInstance(this);
		voiceManager.stopRecoding();
		mWebSocketClient.close();
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
			mMapLocation = new MapLocation(mMap);
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

	// targetのleft,topがとれないときはこちらを使る（暫定バグ対応)
	private void moveTop( ImageView target ,int duration,Animator.AnimatorListener listener, int top, int left ) {

		float absoluteStartY = 0f; // 現在位置から
		float absoluteEndY = (top) * -1.0f; // 親RootViewの上まで

		// translationXプロパティを0fから200fに変化させます
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat( target, "translationY", absoluteStartY, absoluteEndY );

		// 3秒かけて実行させます
		objectAnimator.setDuration( duration );

		objectAnimator.addListener(listener);

		// アニメーションを開始します
		objectAnimator.start();
	}

	// たまを大きくする
	private void tamaBig(int vol) {
		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)mTama.getLayoutParams();
		int currentWidth = mTama.getLayoutParams().width;
		int currentHeight = mTama.getLayoutParams().height;
		int afterWidth = currentWidth + vol * 2;
		int afterHeight = currentHeight + vol;
		int afterMarginTop = (int)(afterHeight * -0.5);
		mTama.getLayoutParams().width = afterWidth;
		mTama.getLayoutParams().height = afterHeight;
		mlp.topMargin = afterMarginTop;
		mTama.setLayoutParams(mlp);
		mTama.requestLayout();
	}

	@OnClick(R.id.map)
	public void test(ImageView imageView) {
		Log.d(TAG, "test");
		tamaBig(5);
    }

	//WebSocketClientからのコールバック
	@Override
	public void onOpen(ServerHandshake handshakedata) {
		Log.d(TAG, "onOpen");
	}

	@Override
	public void onMessage(Yell yell) {
		Log.d(TAG, "onMessage :" + yell);
		showYell(yell);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		Log.d(TAG, "onClose");
	}

	@Override
	public void onError(Exception ex) {
		Log.d(TAG, "onError :" + ex.getMessage());
	}


	@Override
	public void showDebugHealtz(int[] aFloat) {

	}

	@Override
	public void sendData(String name, float lat, float lon, int volumeLevel, int voiceType) {
		if(mWebSocketClient.isOpen()){
			Log.d(TAG, "mWebSocketClient is open. request");
			mWebSocketClient.reqeustYell(name, lat, lon, volumeLevel, voiceType, 10);
		}
		else {
			Log.d(TAG, "mWebSocketClient is not open.");
		}
	}

	// yellを打ち上げる
	private void showYell(Yell yell) {
		int area = yell.getArea();
		String name = yell.getName();
		int type = yell.getType();
		final int vol = yell.getVol();

		// yellを指定された色に
		int imgResId = R.drawable.yell; //default
		if (type==1) {
			imgResId = R.drawable.yell;
		} else if (type==2) {
			imgResId = R.drawable.yell2;
		} else if (type==3) {
			imgResId = R.drawable.yell3;
		} else if (type==4) {
			imgResId = R.drawable.yell4;
		} else if (type==5) {
			imgResId = R.drawable.yell5;
		}

		// 指定地域の上にyellを動的に表示
		int width = YellApplication.dp2int(15);
		int height = YellApplication.dp2int(30);
		final ImageView yellImage = new ImageView(this);
		yellImage.setImageResource(imgResId);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(width, height);
		Pair<Integer,Integer> topLeft = mMapLocation.getLocation(area);
		int top = topLeft.first - height/2; //画像サイズ文位置調整
		int left = topLeft.second - width/2;
		rlp.topMargin = top;
		rlp.leftMargin = left;
		mRoot.addView(yellImage, rlp);

		Animator.AnimatorListener listener = new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				tamaBig(vol*TAMA_SIZE); // アニメーション後tamaを大きくする
				yellImage.setVisibility(View.INVISIBLE); // とりあえ図表示のみ削除
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		};

		// 打ち上げるアニメーション
		moveTop(yellImage, 3000,listener, top, left);
	}



}
