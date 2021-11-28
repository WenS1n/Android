package cn.vesns.sunweather.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.vesns.sunweather.R;
import cn.vesns.sunweather.adapter.SearchAdapter;
import cn.vesns.sunweather.entity.City;
import cn.vesns.sunweather.util.CommonUtil;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity.class";

    private AutoCompleteTextView autoCompleteTextView;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
        initSearch();

        imageView.setOnClickListener(v -> {
            if (v.getId() == R.id.iv_search_back) {
                onBackPressed();
            }
        });
    }


    private void init() {
        imageView = findViewById(R.id.iv_search_back);
        autoCompleteTextView = findViewById(R.id.et_search);
        linearLayout = findViewById(R.id.ll_history);
        recyclerView = findViewById(R.id.recycle_search);
        LinearLayoutManager sevenManager = new LinearLayoutManager(this);
        sevenManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sevenManager);

    }

    String location = "null";

    private void initSearch() {
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = autoCompleteTextView.getText().toString();
                if (!TextUtils.isEmpty(input)) {
                    linearLayout.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    QWeather.getGeoCityLookup(SearchActivity.this, input, new QWeather.OnResultGeoListener() {
                        @Override
                        public void onError(Throwable throwable) {
                            runOnUiThread(() -> {
                                Toast.makeText(SearchActivity.this, input, Toast.LENGTH_SHORT).show();

                            });
                            Log.i(TAG, "getGeoCityLookup onError: " + throwable);
                        }

                        @Override
                        public void onSuccess(GeoBean geoBean) {
                            Log.i(TAG, "getGeoCityLookup onSuccess: " + new Gson().toJson(geoBean));
                            Map<String, Object> map = new HashMap<>();
                            if (Code.OK == geoBean.getCode()) {
                                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                                List<City> cityList = new ArrayList<>();
                                if (locationBean != null && locationBean.size() > 0) {
                                    if (cityList.size() > 0) {
                                        cityList.clear();
                                    }
                                    for (int i = 0; i < locationBean.size(); i++) {
                                        GeoBean.LocationBean locationBeanData = locationBean.get(i);
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
                                        city.setId(locationBeanData.getId());
                                        city.setCityName(name);
                                        city.setGetAdm2(adm2);
                                        city.setGetAdm1(adm1);
                                        city.setGetCountry(country);
                                        Log.i("Asdd",city.toString());
                                        cityList.add(city);
                                    }

                                    SearchAdapter searchAdapter = new SearchAdapter(cityList, SearchActivity.this, autoCompleteTextView.getText().toString(), true);
                                    recyclerView.setAdapter(searchAdapter);
                                    searchAdapter.notifyDataSetChanged();
                                }


                            } else {
                                Code code = geoBean.getCode();
                                Log.i(TAG, "failed code: " + code);
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }



}