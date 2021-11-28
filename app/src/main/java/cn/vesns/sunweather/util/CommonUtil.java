package cn.vesns.sunweather.util;

import cn.vesns.sunweather.SunApplication;

public class CommonUtil {

//    public static final String APK_USERNAME = "HE2106011443341252";
//    public static final String APK_KEY = "5a2f222b025e44cfbbda93be98539404";
public static final String APK_USERNAME = "HE2106060137461493";

    public static final String APK_KEY = "45ac397261214947ab3cebd34b816084";

    public static String CITY_INITIAL = "广安";

    public static String CITY_ID = "101270801";
//    public static String CITY_ID = "101270801";

    //广安职业技术学院经度
    public static Double NOW_LON = 106.674126;

    //广安职业技术学院经度
    public static Double NOW_LAT = 30.488926;

    public static String NOW_CITY_ID = SpUtils.getString(SunApplication.getContext(), "lastLocation", "101270801");
    public static String NOW_CITY_NAME = SpUtils.getString(SunApplication.getContext(), "nowCityName", "广安");

}
