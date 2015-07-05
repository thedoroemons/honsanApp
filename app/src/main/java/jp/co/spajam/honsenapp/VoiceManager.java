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

    Timer _mTimer = new Timer(true);         //onClickメソッドでインスタンス生成

    private boolean _mIsRecording = false;
    private AudioRecord _mAudioRec = null;
    private int _mBufSize;
    private int FFT_SIZE;

    private static VoiceManager _mVoiceManager;

    private List<Float> _mVolumeList = new ArrayList<>();
    private List<Float> _mHealtzList = new ArrayList<>();

    // しばらくそのテンションを継続させる。
//    private int modeTime = -1;
    private int mode = VoiceConst.VOICE_TYPE_NORMAL;


    interface SendDataIF {
        void showDebugVolume(int[] aIntArr);
        void showDebugHealtz(int[] aIntArr);
        void sendData(String name, float lat, float lon, int volumeLevel, int voiceType, int additionTama);
    }
    private static SendDataIF _mDebugIF;


    public static VoiceManager getInstance(SendDataIF debugIF){
        if(_mVoiceManager == null){
            _mVoiceManager = new VoiceManager();
        }
        if(_mDebugIF == null){
            _mDebugIF = debugIF;
        }
        return _mVoiceManager;
    }


    private void _init(){

        _mBufSize = AudioRecord.getMinBufferSize(
                VoiceConst.SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        FFT_SIZE = getMin2Power(_mBufSize);

        if (FFT_SIZE > _mBufSize) {
            _mBufSize = FFT_SIZE;
        }
        _mAudioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                VoiceConst.SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                _mBufSize);
    }

    public int[] getAndRemoveVolumeList(){

        synchronized (_mVolumeList){
            int[] tmp = new int[_mVolumeList.size()];

            for(int n = 0; n< tmp.length; n++){
                tmp[n] = _mVolumeList.get(n).intValue();
            }
            _mVolumeList.clear();

            return tmp;
        }

    }

    public int[] getAndRemoveHealtzList(){

        synchronized (_mHealtzList) {
            int[] tmp = new int[_mHealtzList.size()];

            for(int n = 0; n< tmp.length; n++){
                tmp[n] = _mHealtzList.get(n).intValue();
            }

            _mHealtzList.clear();
            return tmp;
        }
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
                    final float healtz = fft.getHealtz(buf, VoiceConst.SAMPLING_RATE);
//                    Log.v("AudioRecord", "read " + buf.length + " bytes");
//                    Log.v("AudioRecord", healtz + " Hz");
//                    Log.v("AudioRecord", volume + " volume");
                    _mHealtzList.add(healtz);
                    _mVolumeList.add(volume);
                }
                // 録音停止
                Log.v("AudioRecord", "stop");
                _mAudioRec.stop();
            }
        }).start();

        PostTimerTask timerTask = new PostTimerTask();
        _mTimer.schedule(timerTask, VoiceConst.SENDING_INTERVAL * 2, VoiceConst.SENDING_INTERVAL);

    }

    public void stopRecoding(){
        _mIsRecording = false;
        _mTimer.cancel();
        _mTimer = new Timer(true);
        _mDebugIF = null;
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

            // 音量
            int[] volume = getAndRemoveVolumeList();
            // 音量レベル
            final int volumeLevel = getVolumeLevel(volume);

            // 周波数
            int[] healtz = getAndRemoveHealtzList();
            // 音声タイプ
            final int voiceType = getVoiceType(volumeLevel, healtz);

            if(! VoiceConst.DEBUG_SEND_VOLUME_0 && volumeLevel == 0){
                Log.d(TAG,"volumeLevel０なので終了");
                return;
            }

            // 緯度経度
            final float[] latlonStr = YellApplication.loadLatLon();

            // ユーザ名
            final String name = YellApplication.loadNickname();

            // volume と voiceType を渡す。あとlat,lonとニックネーム
            Log.d(TAG,"SOUND00 volumeLevel:" + volumeLevel + "voiceType;" + voiceType);
            Log.d(TAG, "lat;" + latlonStr[0] + "lon:" + latlonStr[1]);
            Log.d(TAG, "name;" + name);

            _mDebugIF.sendData(name, latlonStr[0], latlonStr[1], volumeLevel, voiceType, 0);

            // vol3以上の時、1/3 STEP後に、送る。
            if(volumeLevel >=3) {
                _mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        _mDebugIF.sendData(name, latlonStr[0], latlonStr[1], volumeLevel, voiceType, 1);
                    }
                }, VoiceConst.SENDING_INTERVAL/3);
            }

            // vol4以上の時、2/3 STEP後に、送る。
            if(volumeLevel >=4) {
                _mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        _mDebugIF.sendData(name, latlonStr[0], latlonStr[1], volumeLevel, voiceType, 1);
                    }
                }, VoiceConst.SENDING_INTERVAL * 2/3);
            }



        }
    }

    // 音声タイプを取得する
    // @VoiceConst.VoiceType
    private int getVoiceType(int volumeLevel, int[] healtz){

        _mDebugIF.showDebugHealtz(healtz);

        int healtzSum = 0;
        List<Integer> healtzList = new ArrayList<>();

        for (int n = 0; n < healtz.length; n++){
            if(healtz[n] > 10){
                healtzSum += healtz[n];
                healtzList.add(healtz[n]);
            }
        }

        int size = healtzList.size();

        if(size == 0 || volumeLevel == 0){
            return VoiceConst.VOICE_TYPE_NORMAL;
        }

        int ave  = healtzSum / size;
        int var = 0;
        int firstSum = 1;
        int secondSum = 1;
        for (int n = 0; n < size; n++){
            var += (healtzList.get(n) - ave) * (healtzList.get(n) - ave);

            if(n < size/2){
                firstSum += healtzList.get(n);
            }
            else {
                secondSum += healtzList.get(n);
            }
        }
        firstSum *= 1.13;
        Log.d("SOUND00","size:" + size + ",ave:" + ave + ",var:" + var);
        Log.d("SOUND00","firstSum:" + firstSum + ",secondSum:" + secondSum);

        // 割と平穏
        // if( volumeLevel == 1 || volumeLevel == 2){

        float val = (firstSum>secondSum)?((float)firstSum/secondSum):((float)secondSum/firstSum);

        Log.d("SOUND00","val:" + val);

        // 差分が激しくない。
        if(var <= VoiceConst.HEALTZ_VAR * ave || val < VoiceConst.VALID_DIST ){
            return mode = VoiceConst.VOICE_TYPE_NORMAL;
        }
        else {
            // 前半が高い
            if(firstSum > secondSum){
                return mode = VoiceConst.VOICE_TYPE_DOWN;
            }
            // 後半が高い
            else {
                return mode = VoiceConst.VOICE_TYPE_UP;
            }
        }
        // }

        // かなり過激な状態
//        else {
//            if(var <= VoiceConst.HEALTZ_VAR || Math.abs(firstSum - secondSum) < VoiceConst.VALID_DIST ){
//                return VoiceConst.VOICE_TYPE_BIG;
//            }
//            else {
//                // 前半が高い
//                if(firstSum > secondSum){
//                    return VoiceConst.VOICE_TYPE_BIG_UP;
//                }
//                // いらだち
//                else {
//                    return VoiceConst.VOICE_TYPE_BIG_DOWN;
//                }
//            }
//        }

    }

    // 音声レベルを取得する。
    private int getVolumeLevel(int[] volume){

        if(volume.length == 0){
            return 0;
        }

        int volumeSum = 0;



        _mDebugIF.showDebugVolume(volume);

        for (int n = 0; n < volume.length; n++){
            if(volume[n] >= 0){
                volumeSum += volume[n];
                // 瞬発力補正
                if(volume[n]>=16){
                    volumeSum += 5;
                }
                // 瞬発力補正
                if(volume[n]>=30){
                    volumeSum += 3;
                }
            }
        }

        int volumeAve = volumeSum/volume.length;
        Log.d(TAG, "SOUND00 volumeAve:" + volumeAve );

        for(int n = 0; n < VoiceConst.VOLUME_VALUE.length; n++){
            if(volumeAve < VoiceConst.VOLUME_VALUE[n]){
                return n;
            }
        }

        return VoiceConst.VOLUME_VALUE.length;
    }


}
