package com.herun.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.herun.app.api.ApiClient;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private KakaoMap kakaoMap;
    private Button btnStartRun;
    private ImageButton btnHistory;
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

        tvUsername  = findViewById(R.id.tvUsername);
        btnStartRun = findViewById(R.id.btnStartRun);
        btnHistory  = findViewById(R.id.btnHistory);
        mapView     = findViewById(R.id.mapView);

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
            }
        });

        btnStartRun.setOnClickListener(v ->
                startActivity(new Intent(this, RunningActivity.class)));

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
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
