package cn.vesns.sunweather.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;

import cn.vesns.sunweather.R;
import cn.vesns.sunweather.service.AMapService;

/**
 * 初始化检查权限是否获得，并开启service获取定位等操作
 */
public class MainActivity extends AppCompatActivity {

    final int REQUEST_PERMISSION_LOCATION = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // 没得权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PERMISSION_LOCATION);
        } else {
            startService(new Intent(this, AMapService.class));
            startActivity();
//            stopService(new Intent(this,AMapService.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_LOCATION:
                startService(new Intent(this, AMapService.class));
//                stopService(new Intent(this,AMapService.class));
                startActivity();
                break;
            default:
                startActivity();
//                stopService(new Intent(this,AMapService.class));
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startActivity() {
        startActivity(new Intent(this, WeatherActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this,AMapService.class));
    }
}