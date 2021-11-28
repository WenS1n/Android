package cn.vesns.sunweather.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherHourlyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.vesns.sunweather.R;

import cn.vesns.sunweather.entity.City;
import cn.vesns.sunweather.entity.CityList;
import cn.vesns.sunweather.util.CommonUtil;
import cn.vesns.sunweather.util.DisplayUtil;
import cn.vesns.sunweather.util.HttpUtil;
import cn.vesns.sunweather.util.WeekUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class OneFragment extends Fragment {
    private static final String TAG = "WeatherActivity.class";
    private static final String PARAM = "LOCATION";

    public SwipeRefreshLayout swipeRefresh;
    private TextView titleCity;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView cloudageText;
    private TextView humidityText;
    private LinearLayout forecastLayout;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView backBingImg;
    private ProgressBar progressBar;
    private TextView progressBar_text;
    private TextView gradle_air_text;
    private TextView traffic_text;
    private TextView fish_text;
    private TextView travel_text;
    private View rootView;
    private String location;
    CityList cityList = new CityList();

    private LinearLayout linearLayout;

    private int[] image = {R.mipmap.cloudy, R.mipmap.fine, R.mipmap.rain, R.mipmap.thunder, R.mipmap.dawu, R.mipmap.haze, R.mipmap.fog};

    public static OneFragment newInstance(String cityId) {
        OneFragment fragment = new OneFragment();
        Bundle args = new Bundle();
        args.putString(PARAM, cityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView =  inflater.inflate(R.layout.fragment_one, container, false);
        }
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getString(PARAM);
            initView(view);
        }

    }

    public void initView(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        titleCity = view.findViewById(R.id.title_city);
        degreeText = view.findViewById(R.id.degree_text);
        weatherInfoText = view.findViewById(R.id.weather_info_text);
        cloudageText = view.findViewById(R.id.cloudage_text);
        humidityText = view.findViewById(R.id.humidity_text);
        forecastLayout = view.findViewById(R.id.forecast_layout);
        comfortText = view.findViewById(R.id.comfort_text);
        carWashText = view.findViewById(R.id.car_wash_text);
        sportText = view.findViewById(R.id.sport_text);
        backBingImg = view.findViewById(R.id.bing_pic_img);
        swipeRefresh = view.findViewById(R.id.swipe_refresh2);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar_text = view.findViewById(R.id.progressBar_text);
        gradle_air_text = view.findViewById(R.id.gradle_air);
        linearLayout = view.findViewById(R.id.linear);
        travel_text = view.findViewById(R.id.travel_text);
        fish_text = view.findViewById(R.id.fish_text);
        traffic_text = view.findViewById(R.id.traffic_text);

        TextView tvFrom = view.findViewById(R.id.tv_from);
        tvFrom.setOnClickListener(view1 -> startUri());

        swipeRefresh.setOnRefreshListener(() -> requestWeather(location));
    }

    public void requestWeather(final String weatherId) {

        /**
         * 实况天气数据
         * @param location 所查询的地区，可通过该地区名称、ID、IP和经纬度进行查询经纬度格式：经度,纬度
         *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
         * @param lang     (选填)多语言，可以不使用该参数，默认为简体中文
         * @param unit     (选填)单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问结果回调
         */
        QWeather.getGeoCityLookup(getActivity(), CommonUtil.CITY_INITIAL, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable e) {
                    swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                Map<String, Object> map = new HashMap<>();
                if (Code.OK == geoBean.getCode()) {

                    List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                    List<City> cityBeans = new ArrayList<>();
                    City cityBean = new City();

                    for (GeoBean.LocationBean bean : locationBean) {
                        cityBean.setCityName(bean.getName());
                        cityBean.setId(bean.getId());
                    }
                    if (cityList != null && cityList.getCityBeans() != null && cityList.getCityBeans().size() > 0) {
                        cityBeans = cityList.getCityBeans();
                        cityBeans.add(0, cityBean);
                    } else {
                        cityBeans.add(cityBean);
                    }
                    showWeather(geoBean,cityBeans,false);
//                    getDate(cityBeans,false);
                } else {
                    Code code = geoBean.getCode();
//                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

        QWeather.getWeatherNow(getActivity(), weatherId, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onSuccess(WeatherNowBean weatherBean) {
//                Log.i(TAG, "getWeather onSuccess: " + "*" + titleCity.getText() + " *" + new Gson().toJson(weatherBean));
                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherBean.getCode()) {
                    WeatherNowBean.NowBaseBean now = weatherBean.getNow();
                    degreeText.setText(" " + now.getTemp() + "°");
                    weatherInfoText.setText(now.getText());

                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherBean.getCode();
//                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

        QWeather.getWeather7D(getActivity(), CommonUtil.CITY_ID, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                    Toast.makeText(getContext(), "获取未来七天天气信息失败", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                List<WeatherDailyBean.DailyBean> daily = weatherDailyBean.getDaily();
                showWeather7d(daily);
            }
        });

        QWeather.getAirNow(getActivity(), CommonUtil.CITY_ID, Lang.ZH_HANS, new QWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {

                    Toast.makeText(getContext(), "获取空气信息失败", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                if (airNowBean.getCode() == Code.OK) {
                    showAQIWeather(airNowBean);
                }

            }

        });

        QWeather.getWeather24Hourly(getActivity(), CommonUtil.CITY_ID, new QWeather.OnResultWeatherHourlyListener() {
            @Override
            public void onError(Throwable throwable) {

                    Toast.makeText(getActivity(), "获取今日天气信息失败", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);

            }


            @Override
            public void onSuccess(WeatherHourlyBean weatherHourlyBean) {
                if (Code.OK == weatherHourlyBean.getCode()) {
                    List<WeatherHourlyBean.HourlyBean> hourly = weatherHourlyBean.getHourly();

                    getDayWeather(hourly);

//                    Log.i(TAG, "getWeather24Hourly onSuccess: " + new Gson().toJson(hourly));
                } else {
                    Code code = weatherHourlyBean.getCode();
//                    Log.i(TAG, "failed code: " + code);
                }
            }
        });

        List<IndicesType> list = new ArrayList<>();
        list.add(IndicesType.ALL);

        QWeather.getIndices1D(getActivity(), CommonUtil.CITY_ID, Lang.ZH_HANS, list, new QWeather.OnResultIndicesListener() {
            @Override
            public void onError(Throwable throwable) {

                    Log.i(TAG, "getIndices1D onError: " + throwable);
                    Toast.makeText(getContext(), "获取生活指数失败", Toast.LENGTH_SHORT).show();
                    swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onSuccess(IndicesBean indicesBean) {
                if (Code.OK == indicesBean.getCode()) {
                    List<IndicesBean.DailyBean> dailyList = indicesBean.getDailyList();
                    showIndexOfLive(dailyList);
                }

            }
        });
        swipeRefresh.setRefreshing(false);
        loadBingPic();
    }

    private void showWeather(GeoBean geoBean,List<City> cityList,boolean first) {
        List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
        GeoBean.LocationBean locationBean1 = locationBean.get(0);
        titleCity.setText(CommonUtil.CITY_INITIAL);
        CommonUtil.NOW_CITY_NAME = locationBean1.getName();
        CommonUtil.CITY_ID = locationBean1.getId();
//        fragments = new ArrayList<>();
//        llRound.removeAllViews();
//        for (City city : cityList) {
//            String id = city.getId();
//            cityIds.add(id);
//            OneFragment oneFragment = OneFragment.newInstance(id);
//            fragments.add(oneFragment);
//        }
//
//        View view;
//        for (int i = 0; i < fragments.size(); i++) {
//            view = new View(WeatherActivity.this);
//            view.setBackgroundResource(R.drawable.background);
//            view.setEnabled(false);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dip2px(this, 4), DisplayUtil.dip2px(this, 4));
//            //设置间隔
//            if (fragments.get(i) != fragments.get(0)) {
//                layoutParams.leftMargin = 10;
//            }
//            llRound.addView(view, layoutParams);
//        }
//        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
//        llRound.getChildAt(0).setEnabled(true);
//        mNum = 0;
//        if (fragments.size() == 1) {
//            llRound.setVisibility(View.GONE);
//        } else {
//            llRound.setVisibility(View.VISIBLE);
//        }
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                llRound.getChildAt(mNum).setEnabled(false);
//                llRound.getChildAt(position).setEnabled(true);
//                mNum = position;
//                titleCity.setText(locaitons.get(position));
//            }
//            @Override
//            public void onPageScrollStateChanged(int state) { }
//        });
//        if (!first && fragments.size() > 1) {
//            viewPager.setCurrentItem(1);
//        } else {
//            viewPager.setCurrentItem(0);
//        }
    }

    private void showWeather7d(List<WeatherDailyBean.DailyBean> daily) {
        forecastLayout.removeAllViews();
        for (WeatherDailyBean.DailyBean dailyBean : daily) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dataText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
//          显示周
            dataText.setText(WeekUtil.dateToWeek(dailyBean.getFxDate()));
            // 显示日期
//            dataText.setText(dailyBean.getFxDate());
            infoText.setText(dailyBean.getTextDay());
            maxText.setText(dailyBean.getTempMax());
            minText.setText(dailyBean.getTempMin());
            forecastLayout.addView(view);
        }

        String tempMax = daily.get(0).getTempMax();
        String tempMin = daily.get(0).getTempMin();
        cloudageText.setText("最高: " + tempMax + "° ");
        humidityText.setText("  最低: " + tempMin + "°");

    }

    private void showAQIWeather(AirNowBean airNowBean) {
        AirNowBean.NowBean now = airNowBean.getNow();
        String aqi = now.getAqi();
        int aqi_int = Integer.parseInt(aqi);
        progressBar.setMax(500);
        progressBar.setProgress(aqi_int);
        progressBar_text.setText("AQI " + aqi + " ");
        if (aqi_int > 300) {
            gradle_air_text.setText("严重污染");
        } else if (aqi_int > 200 && aqi_int <= 300) {
            gradle_air_text.setText("重度污染");
        } else if (aqi_int > 150 && aqi_int <= 200) {
            gradle_air_text.setText("中度污染");
        } else if (aqi_int > 100 && aqi_int <= 150) {
            gradle_air_text.setText("轻度污染");
        } else if (aqi_int > 50 && aqi_int <= 100) {
            gradle_air_text.setText("良");
        } else {
            gradle_air_text.setText("优");
        }
    }

    /**
     * 如果加载不出来，应该是电脑和调试机连接了不同的wifi或网络
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                getActivity().runOnUiThread(() -> Glide.with(getActivity()).load(bingPic).into(backBingImg));
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void getDayWeather(List<WeatherHourlyBean.HourlyBean> hourly) {
        linearLayout.removeAllViews();
        for (WeatherHourlyBean.HourlyBean hourlyBean : hourly) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.day_weather_item, linearLayout, false);
            TextView day_weather_text = view.findViewById(R.id.day_weather_text);
            ImageView img_dayweather = view.findViewById(R.id.img_dayweather);
            TextView text_weather_temperature = view.findViewById(R.id.text_weather_temperature);
            String fxTime = hourlyBean.getFxTime();
            String substring = fxTime.substring(11, 16);
            day_weather_text.setText(substring);
            if (hourlyBean.getText().contains("云")) {
                img_dayweather.setImageResource(image[0]);
            } else if (hourlyBean.getText().contains("晴")) {
                    img_dayweather.setImageResource(image[1]);
            } else if (hourlyBean.getText().contains("雨")) {
                img_dayweather.setImageResource(image[2]);
            } else if (hourlyBean.getText().contains("雷")) {
                img_dayweather.setImageResource(image[3]);
            } else if (hourlyBean.getText().contains("霾")) {
                img_dayweather.setImageResource(image[4]);
            } else if (hourlyBean.getText().contains("雾")) {
                img_dayweather.setImageResource(image[5]);
            } else {
                img_dayweather.setImageResource(image[6]);
            }
            text_weather_temperature.setText(hourlyBean.getTemp() + "°");
            linearLayout.addView(view);
        }
    }

    private void showIndexOfLive(List<IndicesBean.DailyBean> dailyList) {

        traffic_text.setText(dailyList.get(1).getName() + ": " +dailyList.get(1).getText());
        travel_text.setText(dailyList.get(5).getName() + ": " + dailyList.get(5).getText());
        comfortText.setText(dailyList.get(4).getName() + ": " + dailyList.get(4).getText());
        carWashText.setText(dailyList.get(15).getName() + ": " + dailyList.get(15).getText());
        sportText.setText(dailyList.get(6).getName() + ": " + dailyList.get(6).getText());
        fish_text.setText(dailyList.get(3).getName() + ": " + dailyList.get(3).getText());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    private void startUri() {
        Uri uri = Uri.parse("http://www.vesns.cn");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}