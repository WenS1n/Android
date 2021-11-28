package cn.vesns.sunweather;

import android.app.Application;
import android.content.Context;

import com.qweather.sdk.view.HeConfig;

import cn.vesns.sunweather.util.CommonUtil;

public class SunApplication extends Application {

    private static SunApplication instance = null;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //初始化handler
        HeConfig.init(CommonUtil.APK_USERNAME,CommonUtil.APK_KEY);
        HeConfig.switchToDevService();

    }

    /**
     * 获得实例
     *
     * @return
     */
    public static SunApplication getInstance() {
        return instance;
    }

    /**
     * 获取context对象
     */
    public static Context getContext() {
        return instance.getApplicationContext();
    }


}
