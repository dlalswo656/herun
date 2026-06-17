package com.herun.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.herun.app.api.ApiClient;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdateFactory;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private KakaoMap kakaoMap;
    private Button btnStartRun;
    private ImageButton btnHistory;
    private ImageButton btnLogout;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabMyLocation;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!ApiClient.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        tvUsername    = findViewById(R.id.tvUsername);
        btnStartRun   = findViewById(R.id.btnStartRun);
        btnHistory    = findViewById(R.id.btnHistory);
        btnLogout     = findViewById(R.id.btnLogout);
        fabMyLocation = findViewById(R.id.fabMyLocation);
        mapView       = findViewById(R.id.mapView);

        tvUsername.setText(ApiClient.getUsername());

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {}

            @Override
            public void onMapError(Exception e) {
                Toast.makeText(MainActivity.this, "지도 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap map) {
                kakaoMap = map;
                moveToCurrentLocation();
            }
        });

        btnStartRun.setOnClickListener(v ->
                startActivity(new Intent(this, RunningActivity.class)));

        fabMyLocation.setOnClickListener(v -> moveToCurrentLocation());

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));

        btnLogout.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("로그아웃")
                        .setMessage("로그아웃 하시겠어요?")
                        .setPositiveButton("로그아웃", (d, w) -> {
                            ApiClient.clearToken();
                            startActivity(new Intent(this, LoginActivity.class));
                            finish();
                        })
                        .setNegativeButton("취소", null)
                        .show());
    }

    private void moveToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) return;

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && kakaoMap != null) {
                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(
                        LatLng.from(location.getLatitude(), location.getLongitude()), 15));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) mapView.finish();
    }
}
