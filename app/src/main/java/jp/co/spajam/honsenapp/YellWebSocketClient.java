package jp.co.spajam.honsenapp;

import android.os.Handler;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;

import java.net.URI;
import java.util.Map;

/**
 * Created by fuji on 2015/07/05.
 */
public class YellWebSocketClient extends WebSocketClient{
    private static final String TAG = YellWebSocketClient.class.getSimpleName();
    public static final String SOCKET_SERVER_URL = "ws://36.55.240.249:8989";
    private CallBackListener mListener;
    private Handler mHandler;

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
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onClose(code, reason, remote);
            }
        });
    }

    @Override
    public void onError(final Exception ex) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mListener.onError(ex);
            }
        });
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
