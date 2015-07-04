package jp.co.spajam.honsenapp;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by masaharu on 2015/07/05.
 */
public class SLocationManager implements LocationListener {

    private android.location.LocationManager mLocationManager;

    private static SLocationManager _mLocationManager;

    public static SLocationManager getInstance(){
        if(_mLocationManager == null){
            _mLocationManager = new SLocationManager();
        }
        return _mLocationManager;
    }

    public void startGetCurrentLocation(Context context){

        Log.d("SLocationManager", "緯度経度　取得開始");

        mLocationManager = (android.location.LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        // 位置情報機能非搭載端末の場合
        if (mLocationManager == null) {
            // 何も行いません
            return;
        }

        final Criteria criteria = new Criteria();
        // 以下は必要により
        criteria.setBearingRequired(false);		// 方位不要
        criteria.setSpeedRequired(false);		// 速度不要
        criteria.setAltitudeRequired(false);	// 高度不要
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 低精度
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低消費電力

        final String provider = mLocationManager.getBestProvider(criteria, true);

        // 位置情報の更新を受け取り開始
        mLocationManager.requestLocationUpdates(provider, // プロバイダ
                1000,    // 通知のための最小時間間隔
                1,        // 通知のための最小距離間隔
                this);	// 位置情報リスナー

    }

    @Override
    public void onLocationChanged(Location location) {
        float lat= (float) location.getLatitude();
        float lon= (float) location.getLongitude();
        YellApplication.saveLatLon(lat, lon);
        Log.d("SLocationManager", "緯度経度　取得完了：" + lat + ":" + lon);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
