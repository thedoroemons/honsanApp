package jp.co.spajam.honsenapp;

import android.support.annotation.IntDef;

/**
 * Created by masaharu on 2015/07/05.
 */
public class SoundConst {

    // 送信間隔
    public static final int SENDING_INTERVAL = 1000;

    // 音声レベル
    public static final int[] VOLUME_VALUE = {20,30,45,60,80};

    // 音声タイプ　平常
    @IntDef({VOICE_TYPE_NORMAL,VOICE_TYPE_QUESTION,VOICE_TYPE_DOWN,VOICE_TYPE_EXICTE})
    public @interface VoiceType {}
    public static final int VOICE_TYPE_NORMAL = 1;
    // 音声タイプ　疑問
    public static final int VOICE_TYPE_QUESTION = 2;
    // 音声タイプ　落胆
    public static final int VOICE_TYPE_DOWN = 3;
    // 音声タイプ　高揚
    public static final int VOICE_TYPE_EXICTE = 4;



    // 音系定数
    public static final int SAMPLING_RATE = 44100;



}
