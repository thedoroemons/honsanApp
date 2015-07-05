package jp.co.spajam.honsenapp;

import android.support.annotation.IntDef;

/**
 * Created by masaharu on 2015/07/05.
 */
public class VoiceConst {

    // DEBUGモード
    // 音量０でも送る
    public static final boolean DEBUG_SEND_VOLUME_0 = false;

    // 送信間隔
    public static final int SENDING_INTERVAL = 1100;

    // 音声レベル
    // テスト用（しきい値低め）
    // public static final int[] VOLUME_VALUE = {5,8,10,15,20};
    // ちゃんとした用
    public static final int[] VOLUME_VALUE = {8,13,18,25,32};

    // 音声タイプ　分散しきい値
    public static final float HEALTZ_VAR = 4.5f;

    // 音声タイプ　前後半有意差
    public static final float VALID_DIST = 1.15f;

    // 音声タイプ　平常
    @IntDef({VOICE_TYPE_UP,VOICE_TYPE_DOWN, VOICE_TYPE_NORMAL})
    public @interface VoiceType {}
    // 音声タイプ　疑問　ななめうえ
    public static final int VOICE_TYPE_UP = 1;
    // 音声タイプ　落胆　ななめした
    public static final int VOICE_TYPE_DOWN = 2;
    // 音声タイプ　通常
    public static final int VOICE_TYPE_NORMAL = 3;
    // 音声タイプ　大きめ
//    public static final int VOICE_TYPE_BIG = 4;
//    // 音声タイプ　高揚　した
//    public static final int VOICE_TYPE_BIG_UP = 5;
//    // 音声タイプ　いらだち　うえ
//    public static final int VOICE_TYPE_BIG_DOWN = 6;




    // 音系定数
    public static final int SAMPLING_RATE = 44100;



}
