package cn.vesns.sunweather.adapter;

import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.vesns.sunweather.R;
import cn.vesns.sunweather.activity.SearchActivity;
import cn.vesns.sunweather.activity.WeatherActivity;
import cn.vesns.sunweather.dateinterface.DataUtil;
import cn.vesns.sunweather.entity.City;
import cn.vesns.sunweather.entity.CityList;
import cn.vesns.sunweather.util.CommonUtil;
import cn.vesns.sunweather.util.SpUtils;

public class SearchAdapter extends RecyclerView.Adapter {
    private List<City> data;
    private SearchActivity activity;
    private String searchText;
    private Lang lang;
    private CityList cityBeanList = new CityList();
    private boolean isSearching;

    public SearchAdapter(List<City> data, SearchActivity activity, String searchText, boolean isSearching) {
        this.data = data;
        this.activity = activity;
        this.searchText = searchText;
        this.isSearching = isSearching;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (isSearching) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searching, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        View itemView = viewHolder.itemView;
        String name = data.get(position).getCityName();
        int x = name.indexOf("-");
        String parentCity = name.substring(0, x);
        String location = name.substring(x + 1);

        String CityName = data.get(position).getGetCountry() + " - " + data.get(position).getGetAdm1() + " - " + data.get(position).getGetAdm2() + " - " + parentCity;
        if (TextUtils.isEmpty(data.get(position).getGetAdm1())) {
            CityName = location + "-" + parentCity + "-" + data.get(position).getGetCountry();
        }
        if (!TextUtils.isEmpty(CityName)) {
            viewHolder.tvCity.setText(CityName);
            if (CityName.contains(searchText)) {
                int index = CityName.indexOf(searchText);
                //创建一个 SpannableString对象
                SpannableString sp = new SpannableString(CityName);
                //设置高亮样式一
                sp.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.light_text_color)), index, index + searchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.tvCity.setText(sp);
            }
        }
        itemView.setOnClickListener((v -> {
            String cid = data.get(position).getId();
            System.out.println("cityname-----"+data.get(position).getCityName());
            System.out.println("cityId-----"+data.get(position).getId());
            saveData("cityBean", cid);
            saveBean("cityBean", cid, position);
            CommonUtil.CITY_INITIAL = data.get(position).getCityName().substring(0,2);
            String substring = data.get(position).getCityName();
            String[] split = substring.split("-");
            CommonUtil.CITY_INITIAL = split[0];
            CommonUtil.CITY_ID = data.get(position).getId();
            activity.onBackPressed();
            Intent intent = new Intent(activity,WeatherActivity.class);
            activity.startActivity(intent);
        }));
    }

    private void saveBean(final String key, String cid, int x) {
        List<City> citys = new ArrayList<>();
        cityBeanList = SpUtils.getBean(activity, key, CityList.class);
        if (cityBeanList != null && cityBeanList.getCityBeans() != null) {
            citys = cityBeanList.getCityBeans();
        }
        for (int i = 0; i < citys.size(); i++) {
            if ( cid.equals(citys.get(i).getId())) {
                citys.remove(i);
            }
        }
        if (citys.size() == 10) {
            citys.remove(9);
        }
        citys.add(0, data.get(x));
        CityList cityList = new CityList();
        cityList.setCityBeans(citys);
        SpUtils.saveBean(activity, key, cityList);

    }

    private void saveData(final String key, final String cid) {
        QWeather.getGeoCityLookup(activity, cid, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {
                Log.i("getGeoCityLookup", "onError: " + throwable);
                activity.onBackPressed();
            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<City> cities = new ArrayList<>();
                if ("200".equals(geoBean.getCode())) {
                    List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                    GeoBean.LocationBean locationBeanData = locationBean.get(0);
                    String name = locationBeanData.getName() + "-" + locationBeanData.getAdm2() + "-" + locationBeanData.getAdm1();
                    String adm2 = locationBeanData.getAdm2();
                    String adm1 = locationBeanData.getAdm1();
                    String country = locationBeanData.getCountry();
                    if (TextUtils.isEmpty(name)) {
                        name = adm2;
                    }
                    if (TextUtils.isEmpty(adm2)) {
                        name = adm1;
                    }
                    City city = new City();
                    city.setCityName(name);
                    city.setGetAdm2(adm2);
                    city.setGetAdm1(adm1);
                    city.setGetCountry(country);
                    Log.i("ASAA",city.toString());

                    cityBeanList = SpUtils.getBean(activity, key, CityList.class);
                    if (cityBeanList != null && cityBeanList.getCityBeans() != null) {
                        cities = cityBeanList.getCityBeans();
                    }
                    for (int i = 0; i < cities.size(); i++) {
                        if (cities.get(i).getId().equals(cid)) {
                            cities.remove(i);
                        }
                    }
                    if (cities.size() == 10) {
                        cities.remove(9);
                    }
                    cities.add(0, city);
                    CityList cityBeans = new CityList();
                    cityBeans.setCityBeans(cities);
                    SpUtils.saveBean(activity, key, cityBeans);
                    DataUtil.setCid(cid);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCity;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tv_item_history_city);
        }
    }
}
