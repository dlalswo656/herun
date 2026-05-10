package com.herun.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.gson.Gson;
import com.herun.app.api.ApiClient;
import com.herun.app.model.RunModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RunningActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 100;

    private TextView tvTime, tvDistance, tvPace, tvCalories;
    private Button btnStop;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double totalDistanceKm = 0;
    private int elapsedSeconds = 0;
    private Location lastLocation = null;
    private Double startLat = null, startLng = null;
    private final List<Map<String, Double>> routePoints = new ArrayList<>();

    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);

        tvTime     = findViewById(R.id.tvTime);
        tvDistance = findViewById(R.id.tvDistance);
        tvPace     = findViewById(R.id.tvPace);
        tvCalories = findViewById(R.id.tvCalories);
        btnStop    = findViewById(R.id.btnStopRun);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startTimer();
        startLocationTracking();

        btnStop.setOnClickListener(v -> showStopDialog());
    }

    private void startLocationTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 3000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location location = result.getLastLocation();
                if (location == null) return;

                if (startLat == null) {
                    startLat = location.getLatitude();
                    startLng = location.getLongitude();
                }

                if (lastLocation != null) {
                    float[] results = new float[1];
                    Location.distanceBetween(
                            lastLocation.getLatitude(), lastLocation.getLongitude(),
                            location.getLatitude(), location.getLongitude(), results);
                    totalDistanceKm += results[0] / 1000.0;
                }

                lastLocation = location;

                Map<String, Double> point = new HashMap<>();
                point.put("lat", location.getLatitude());
                point.put("lng", location.getLongitude());
                routePoints.add(point);

                updateStats();
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void updateStats() {
        tvDistance.setText(String.format("%.2f", totalDistanceKm));

        if (totalDistanceKm > 0.05 && elapsedSeconds > 0) {
            double paceMin = (elapsedSeconds / 60.0) / totalDistanceKm;
            int paceM = (int) paceMin;
            int paceS = (int) ((paceMin - paceM) * 60);
            tvPace.setText(String.format("%d'%02d\"", paceM, paceS));
        }

        double calories = 8.0 * 70 * (elapsedSeconds / 3600.0);
        tvCalories.setText(String.format("%.0f", calories));
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedSeconds++;
                int h = elapsedSeconds / 3600;
                int m = (elapsedSeconds % 3600) / 60;
                int s = elapsedSeconds % 60;
                tvTime.setText(String.format("%02d:%02d:%02d", h, m, s));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void showStopDialog() {
        new AlertDialog.Builder(this)
                .setTitle("러닝 종료")
                .setMessage(String.format("거리: %.2f km\n시간: %s\n\n기록을 저장하시겠어요?",
                        totalDistanceKm, tvTime.getText()))
                .setPositiveButton("저장", (d, w) -> saveAndFinish())
                .setNegativeButton("취소", null)
                .show();
    }

    private void saveAndFinish() {
        stopTracking();

        if (totalDistanceKm < 0.01) {
            Toast.makeText(this, "러닝 거리가 너무 짧아요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        double paceMin = totalDistanceKm > 0 ? (elapsedSeconds / 60.0) / totalDistanceKm : 0;
        double calories = 8.0 * 70 * (elapsedSeconds / 3600.0);
        String routeJson = new Gson().toJson(routePoints);

        RunModel.SaveRequest request = new RunModel.SaveRequest(
                totalDistanceKm, elapsedSeconds, paceMin,
                calories, routeJson, startLat, startLng);

        ApiClient.getService().saveRun(request).enqueue(new Callback<RunModel.Response>() {
            @Override
            public void onResponse(Call<RunModel.Response> call, Response<RunModel.Response> response) {
                Toast.makeText(RunningActivity.this,
                        response.isSuccessful() ? "기록이 저장됐어요! 🎉" : "저장 실패",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Call<RunModel.Response> call, Throwable t) {
                Toast.makeText(RunningActivity.this, "저장 실패: 서버 오류", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void stopTracking() {
        timerHandler.removeCallbacks(timerRunnable);
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationTracking();
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
