package jp.co.spajam.honsenapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;

import jp.co.spajam.honsenapp.YellWebSocketClient.CallBackListener;


public class SasubeActivity extends ActionBarActivity implements VoiceManager.SendDataIF, CallBackListener {

    Handler mHandler = new Handler();           //UI Threadへのpost用ハンドラ
    // private YellWebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sasube);
        VoiceManager voiceManager = VoiceManager.getInstance(this);
        voiceManager.startRecording();
        YellLocationManager locationManager = YellLocationManager.getInstance();
        locationManager.startGetCurrentLocation(this);
        // WebSocketサーバーに接続
        // mWebSocketClient = new YellWebSocketClient(URI.create(YellWebSocketClient.SOCKET_SERVER_URL), new Handler(), this);
        // mWebSocketClient.connect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sasube, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager voiceManager = VoiceManager.getInstance(this);
        voiceManager.stopRecoding();
        // mWebSocketClient.close();
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

    @Override
    public void showDebugVolume(int[] aIntArr) {
        String str = "(" + aIntArr.length + ")";
        for (int n = 0; n < aIntArr.length; n++){
            if(aIntArr[n] >= 0){
                str += aIntArr[n] + "\n";
            }
        }

        final String finalStr = str;
        mHandler.post(new Runnable() {
            public void run() {
                ((TextView) findViewById(R.id.textView2)).setText(finalStr);
            }
        });
    }

    @Override
    public void showDebugHealtz(int[] aIntArr) {

        String str = "(" + aIntArr.length + ")";

        for (int n = 0; n < aIntArr.length; n++){
            // 意味のある数値以上
            if(aIntArr[n] > 10){
                str += aIntArr[n] + "\n";
            }
        }
        final String finalStr = str;
        mHandler.post(new Runnable() {
            public void run() {
                ((TextView) findViewById(R.id.textView3)).setText(finalStr);
            }
        });

    }

    @Override
    public void sendData(String name, float lat, float lon, int volumeLevel, int voiceType) {
//        if(mWebSocketClient.isOpen()){
//            mWebSocketClient.reqeustYell(name, lat, lon, volumeLevel, voiceType, 10);
//        }
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {

    }

    @Override
    public void onMessage(Yell yell) {

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }
}
