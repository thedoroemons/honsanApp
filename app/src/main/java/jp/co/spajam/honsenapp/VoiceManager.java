package jp.co.spajam.honsenapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by masaharu on 2015/07/04.
 */
public class VoiceManager {

    private static final int MAX_I = 100;
    private final String TAG = "VoiceManager";

    Timer mTimer   = new Timer(true);         //onClickメソッドでインスタンス生成

    private boolean _mIsRecording = false;
    private AudioRecord _mAudioRec = null;
    private int _mBufSize;
    private int FFT_SIZE;

    private static VoiceManager _mVoiceManager;

    private List<Float> _mVolumeList = new ArrayList<>();
    private List<Float> _mHealtzList = new ArrayList<>();


    interface SendDataIF {
        void showDebugHealtz(int[] aFloat);
        void sendData(String name, float lat, float lon, int volumeLevel, int voiceType);
    }
    private static SendDataIF mDebugIF;


    public static VoiceManager getInstance(SendDataIF debugIF){
        if(_mVoiceManager == null){
            _mVoiceManager = new VoiceManager();
            mDebugIF = debugIF;
        }
        return _mVoiceManager;
    }


    private void _init(){

        _mBufSize = AudioRecord.getMinBufferSize(
                SoundConst.SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        FFT_SIZE = getMin2Power(_mBufSize);

        if (FFT_SIZE > _mBufSize) {
            _mBufSize = FFT_SIZE;
        }
        _mAudioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SoundConst.SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                _mBufSize);
    }

    public int[] getAndRemoveVolumeList(){

        int[] tmp = new int[_mVolumeList.size()];

        for(int n = 0; n<_mVolumeList.size(); n++){
            tmp[n] = _mVolumeList.get(n).intValue();
        }

        _mVolumeList.clear();
        return tmp;

    }

    public int[] getAndRemoveHealtzList(){

        int[] tmp = new int[_mHealtzList.size()];

        for(int n = 0; n<_mHealtzList.size(); n++){
            tmp[n] = _mHealtzList.get(n).intValue();
        }

        _mHealtzList.clear();
        return tmp;

    }


    public void startRecording() {

        if (_mIsRecording) {
            return;
        }

        if (_mAudioRec == null){
            _init();
        }

        // 録音開始
        Log.d(TAG, "スタートじゃ！！");
        _mAudioRec.startRecording();

        _mIsRecording = true;
        // 録音スレッド
        new Thread(new Runnable() {
            @Override

            public void run() {
                float volumeBase = -1.0f;
                FFTUtilFloat fft = new FFTUtilFloat(_mBufSize);
                short buf[] = new short[fft.fftSize];
                // TODO Auto-generated method stub
                while (_mIsRecording) {
                    // 録音データ読み込み
                    int readSize = _mAudioRec.read(buf, 0, buf.length);
                    if (readSize < 0 || readSize > fft.fftSize) continue;
                    long sqsum = 0;
                    for (int i=0; i < readSize; ++i) {
                        sqsum += buf[i] * buf[i];
                    }
                    if (volumeBase < 0) {
                        volumeBase = (float)(1.0 / Math.sqrt((double)sqsum/(double)readSize));
                    }
                    float p = (float)(Math.sqrt((double)sqsum/(double)readSize)) * volumeBase;
                    if (p < 1.0f) { p = 1.0f; }
                    final float volume = (float) (20.0 * (float)Math.log10(p));
                    Arrays.fill(buf, readSize, fft.fftSize, (short) 0);
                    final float healtz = fft.getHealtz(buf, SoundConst.SAMPLING_RATE);
                    Log.v("AudioRecord", "read " + buf.length + " bytes");
                    Log.v("AudioRecord", healtz + " Hz");
                    Log.v("AudioRecord", volume + " volume");
                    _mHealtzList.add(healtz);
                    _mVolumeList.add(volume);
                }
                // 録音停止
                Log.v("AudioRecord", "stop");
                _mAudioRec.stop();
            }
        }).start();

        PostTimerTask timerTask = new PostTimerTask();
        mTimer.schedule(timerTask, SoundConst.SENDING_INTERVAL, SoundConst.SENDING_INTERVAL);

    }

    public void stopRecoding(){
        _mIsRecording = false;
    }

    private int getMin2Power(int length) {
        int i = 1;
        while(i < MAX_I) {
            int power = (int)Math.pow(2.0, (double)i);
            if (length > power) {
                return length;
            }
            ++i;
        }
        return -1;
    }

    class PostTimerTask extends TimerTask {

        @Override
        public void run() {
            // 周波数
            int[] healtz = getAndRemoveHealtzList();
            // 音声タイプ
            int voiceType = getVoiceType(healtz);

            // 音量
            int[] volume = getAndRemoveVolumeList();
            // 音量レベル
            int volumeLevel = getVolumeLevel(volume);

            // 緯度経度
            float[] latlonStr = YellApplication.loadLatLon();

            // ユーザ名
            String name = YellApplication.loadNickname();

            // voiceType と volume を渡す。あとlat,lonとニックネーム
            Log.d(TAG,"voiceType;" + voiceType + "volumeLevel:" + volumeLevel);
            Log.d(TAG,"lat;" + latlonStr[0] + "lon:" + latlonStr[1]);
            Log.d(TAG,"name;" + name);

            mDebugIF.sendData(name, latlonStr[0], latlonStr[1], volumeLevel, voiceType);


        }
    }

    // 音声タイプを取得する
    // @SoundConst.VoiceType
    private int getVoiceType(int[] healtz){

        int healtzSum0 = 0;
        int healtzSum1 = 0;

        for (int n = 0; n < healtz.length/2; n++){
            if(healtz[n] >= 0){
                healtzSum0 += healtz[n];
            }
        }
        for (int n = healtz.length/2; n < healtz.length; n++){
            if(healtz[n] >= 0){
                healtzSum1 += healtz[n];
            }
        }
        mDebugIF.showDebugHealtz(healtz);
//        str2 += "Hz0:" + healtzSum0/(healtz.length/2) ;
//        str2 += "Hz1:" + healtzSum1/(healtz.length/2) ;
//        str2 += "Hz2:" + (healtzSum0+healtzSum1)/healtz.length ;
        return SoundConst.VOICE_TYPE_NORMAL;
    }

    // 音声レベルを取得する。
    private int getVolumeLevel(int[] volume){

        int volumeSum = 0;

        for (int n = 0; n < volume.length; n++){
            if(volume[n] >= 0){
                volumeSum += volume[n];
            }
        }

        int volumeAve = volumeSum/volume.length;
        Log.d(TAG,"volumeAve:" + volumeAve );

        for(int n = 0; n < SoundConst.VOLUME_VALUE.length; n++){
            if(volumeAve < SoundConst.VOLUME_VALUE[n]){
                return n;
            }
        }

        return SoundConst.VOLUME_VALUE.length;
    }


}
