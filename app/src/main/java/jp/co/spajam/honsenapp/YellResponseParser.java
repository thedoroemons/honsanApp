package jp.co.spajam.honsenapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fuji on 2015/07/05.
 */
public class YellResponseParser {
    private static final String KEY_NAME = "name";
    private static final String KEY_AREA = "area";
    private static final String KEY_VOL  = "vol";
    private static final String KEY_TYPE = "type";

    public static Yell parse(String jsonString) throws JSONException{
        return parse(new JSONObject(jsonString));
    }

    public static Yell parse(JSONObject jsonObject) throws JSONException{
        Yell yell = new Yell();
        if (jsonObject.has(KEY_NAME)) {
            yell.setName(jsonObject.getString(KEY_NAME));
        }

        if (jsonObject.has(KEY_AREA)) {
            yell.setArea(jsonObject.getInt(KEY_AREA));
        }

        if (jsonObject.has(KEY_VOL)) {
            yell.setVol(jsonObject.getInt(KEY_VOL));
        }

        if (jsonObject.has(KEY_TYPE)) {
            yell.setType(jsonObject.getInt(KEY_TYPE));
        }

        return yell;
    }
}
