package jp.co.spajam.honsenapp;

import android.os.Handler;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Map;

/**
 * Created by fuji on 2015/07/05.
 */
public class YellWebSocketClient extends WebSocketClient{
    private static final String TAG = YellWebSocketClient.class.getSimpleName();
    public static final String SOCKET_SERVER_URL = "ws://36.55.240.249:8989"; //"ws://128.199.112.27:9292"
    private CallBackListener mListener;
    private Handler mHandler;
    private boolean mIsOpen = false;

    public YellWebSocketClient(URI serverURI, Handler handler, CallBackListener listener) {
        super(serverURI);

        mHandler = handler;
        mListener = listener;
    }

    public YellWebSocketClient(URI serverUri, Draft draft, Handler handler, CallBackListener listener) {
        super(serverUri, draft);

        mHandler = handler;
        mListener = listener;
    }

    public YellWebSocketClient(URI serverUri, Draft draft, Map<String, String> headers, int connecttimeout, Handler handler, CallBackListener listener) {
        super(serverUri, draft, headers, connecttimeout);

        mHandler = handler;
        mListener = listener;
    }

    @Override
    public void onOpen(final ServerHandshake handshakedata) {
        mIsOpen = true;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onOpen(handshakedata);
            }
        });
    }

    @Override
    public void onMessage(final String message) {
        Log.d(TAG, message);

        // レスポンスからYellオブジェクトを生成する
        final Yell yell = YellResponseParser.parse(message);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onMessage(yell);
            }
        });
    }

    @Override
    public void onClose(final int code,final String reason,final boolean remote) {
        mIsOpen =false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onClose(code, reason, remote);
            }
        });
    }

    @Override
    public void onError(final Exception ex) {
        mIsOpen =false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onError(ex);
            }
        });
    }

    /**
     * WebSocketサーバーにリクエストを投げる
     * @param name ニックネーム
     * @param lat 緯度
     * @param lon 経度
     * @param vol 音量
     * @param type 種類
     */
    public void reqeustYell(String name, float lat, float lon, int vol, int type, int tamaSize) {

        try {
            send(createRequestString(name, lat, lon, vol, type, tamaSize));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException.  Request cancel.");
        }
    }

    /**
     * 値を元にリクエスト用のJSON文字列を作成する
     * @param name ニックネーム
     * @param lat 緯度
     * @param lon 経度
     * @param vol 音量
     * @param type 種類
     * @return JSON文字列
     * @throws JSONException
     */
    private String createRequestString(String name, float lat, float lon, int vol, int type, int tamaSize) throws JSONException{
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("lat", lat);
        json.put("lon", lon);
        json.put("vol", vol);
        json.put("type", type);
        json.put("tama_size", tamaSize);

        return json.toString();
    }

    /**
     * WebSocketサーバーに接続しているかを取得する
     * @return boolean
     */
    public boolean isOpen() {
        return mIsOpen;
    }


    public void setCallBackListener(CallBackListener listener) {
        mListener = listener;
    }

    public interface CallBackListener {
        public void onOpen(ServerHandshake handshakedata);
        public void onMessage(Yell yell);
        public void onClose(int code, String reason, boolean remote);
        public void onError(Exception ex);
    }
}
