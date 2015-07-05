package jp.co.spajam.honsenapp;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
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
	private TamaHelper mTamaHelper;
	private int TAMA_SIZE = 3; // volからtamaをどれくらい大きくするかの定数(大きいほどはやくおおきくなる)

	private YellWebSocketClient mWebSocketClient;
	
	@Bind(R.id.tama)
	ImageView mTama;

	@Bind(R.id.map)
	ImageView mMap;

//	// サンプルなので不要
//	@Bind(R.id.yell_sample)
//	ImageView mYellSample;

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
		mWebSocketClient = null;
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
			mTamaHelper = new TamaHelper(mTama,mRoot);
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
		// WebSocketサーバーに接続
		mWebSocketClient = new YellWebSocketClient(URI.create(YellWebSocketClient.SOCKET_SERVER_URL), new Handler(), this);
		mWebSocketClient.connect();

//		moveTop(mYellSample, 3000); // サンプルなので不要
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
		objectAnimator.setDuration(duration);

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

	// 地図タップで埼玉からエールを送るサンプル
	@OnClick(R.id.map)
	public void test(ImageView imageView) {
		Log.d(TAG, "test");
		Yell yell = new Yell("sample",2,3,1);
		showYell(yell);
//		mTamaHelper.tamaBig(5);
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
	public void showDebugVolume(int[] aIntArr) {

	}

	@Override
	public void showDebugHealtz(int[] aIntArr) {

	}

	@Override
	public void sendData(String name, float lat, float lon, int volumeLevel, int voiceType) {

		if(mWebSocketClient == null){
			return;
		}

		if(mWebSocketClient.isOpen()){
			Log.d(TAG, "mWebSocketClient is open. request");
			mWebSocketClient.reqeustYell(name, lat, lon, volumeLevel, voiceType);
		}
		else {
			Log.d(TAG, "mWebSocketClient is not open.");
		}
	}

	// yellを打ち上げる
	private void showYell(Yell yell) {
		int area = yell.getArea();
		final String name = yell.getName();
		final int type = yell.getType();
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
				mTamaHelper.tamaBig(vol * TAMA_SIZE); // アニメーション後tamaを大きくする
				yellImage.setVisibility(View.INVISIBLE); // とりあえ図表示のみ削除
				mTamaHelper.showNameInTama(YellActivity.this, name, type);
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
