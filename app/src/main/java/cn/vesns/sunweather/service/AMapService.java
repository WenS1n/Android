package cn.vesns.sunweather.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import cn.vesns.sunweather.util.CommonUtil;

public class AMapService extends Service {

    private final String TAG = "sky";
    public AMapLocationClient mapLocationClient = null;

    public AMapLocationListener mLocationListener = aMapLocation -> {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                CommonUtil.CITY_INITIAL = aMapLocation.getCity();
                CommonUtil.NOW_LON = aMapLocation.getLongitude();
                CommonUtil.NOW_LAT = aMapLocation.getLatitude();
                System.out.println(aMapLocation.toString() + "66666666");
                Log.i(TAG,"now location: " +  CommonUtil.NOW_LON + "---" + CommonUtil.NOW_LAT + "---" + CommonUtil.CITY_INITIAL);
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e(TAG, errText);
            }
        }
    };

    public AMapService() {

    }

    @Override
    public void onCreate() {

        super.onCreate();

        //初始化定位
        mapLocationClient = new AMapLocationClient(getApplicationContext());
        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(10000);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        mapLocationClient.setLocationListener(mLocationListener);
        //给定位客户端对象设置定位参数
        mapLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mapLocationClient.startLocation();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mapLocationClient.stopLocation();//停止定位
        mapLocationClient.onDestroy();//销毁定位客户端。
    }
}
