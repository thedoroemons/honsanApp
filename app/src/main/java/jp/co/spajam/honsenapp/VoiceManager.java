package jp.co.spajam.honsenapp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by masaharu on 2015/07/04.
 */
public class VoiceManager {

    private static final int MAX_I = 100;
    private static final int SAMPLING_RATE = 44100;
    private final String TAG = getClass().getSimpleName();

    private boolean _mIsRecording = false;
    private AudioRecord _mAudioRec = null;
    private int _mBufSize;
    private int FFT_SIZE;

    private static VoiceManager _mVoiceManager;

    private List<Float> _mVolumeList = new ArrayList<>();
    private List<Float> _mHealtzList = new ArrayList<>();

    public static VoiceManager getInstance(){
        if(_mVoiceManager == null){
            _mVoiceManager = new VoiceManager();
        }
        return _mVoiceManager;
    }


    private void _init(){

        _mBufSize = AudioRecord.getMinBufferSize(
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        FFT_SIZE = getMin2Power(_mBufSize);

        if (FFT_SIZE > _mBufSize) {
            _mBufSize = FFT_SIZE;
        }
        _mAudioRec = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLING_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                _mBufSize);
    }

    public float[] getAndRemoveVolumeList(){

        float[] tmp = new float[_mVolumeList.size()];

        for(int n = 0; n<_mVolumeList.size(); n++){
            tmp[n] = _mVolumeList.get(n);
        }

        _mVolumeList.clear();
        return tmp;

    }

    public float[] getAndRemoveHealtzList(){

        float[] tmp = new float[_mHealtzList.size()];

        for(int n = 0; n<_mHealtzList.size(); n++){
            tmp[n] = _mHealtzList.get(n);
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
                    final float healtz = fft.getHealtz(buf, SAMPLING_RATE);
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


}
