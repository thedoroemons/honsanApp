package jp.co.spajam.honsenapp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fuji on 2015/07/05.
 */
public class YellResponseParser {
    private static final String TAG = YellResponseParser.class.getSimpleName();
    private static final String KEY_NAME = "name";
    private static final String KEY_AREA = "area";
    private static final String KEY_VOL  = "vol";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TAMA_SIZE= "tama_size";

    public static Yell parse(String jsonString) {
        try {
            return parse(new JSONObject(jsonString));
        } catch (JSONException e) {
            Log.e(TAG, "JSONException");
            // エラーの場合はデフォルト値のオブジェクトを返す
            return new Yell("", 2, 1,1);
        }
    }

    public static Yell parse(JSONObject jsonObject) throws JSONException{
        Yell yell = new Yell();
        if (jsonObject.has(KEY_NAME)) {
            yell.setName(jsonObject.getString(KEY_NAME));
        } else {
            Log.e(TAG, "empty name");
            yell.setName("");
        }

        if (jsonObject.has(KEY_AREA)) {
            yell.setArea(jsonObject.getInt(KEY_AREA));
        } else {
            Log.e(TAG, "empty area");
            yell.setArea(2);
        }

        if (jsonObject.has(KEY_VOL)) {
            yell.setVol(jsonObject.getInt(KEY_VOL));
        } else {
            Log.e(TAG, "empty vol");
            yell.setVol(1);
        }

        if (jsonObject.has(KEY_TYPE)) {
            yell.setType(jsonObject.getInt(KEY_TYPE));
        } else {
            Log.e(TAG, "empty type");
            yell.setType(1);
        }

        if (jsonObject.has(KEY_TAMA_SIZE)) {
            yell.setTamaSize(jsonObject.getInt(KEY_TAMA_SIZE));
        }

        return yell;
    }
}
