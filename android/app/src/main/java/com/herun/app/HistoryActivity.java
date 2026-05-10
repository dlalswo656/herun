package com.herun.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.herun.app.api.ApiClient;
import com.herun.app.model.RunModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvRuns;
    private TextView tvEmpty, tvTotalRuns, tvTotalDist, tvTotalCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvRuns      = findViewById(R.id.rvRuns);
        tvEmpty     = findViewById(R.id.tvEmpty);
        tvTotalRuns = findViewById(R.id.tvTotalRuns);
        tvTotalDist = findViewById(R.id.tvTotalDist);
        tvTotalCal  = findViewById(R.id.tvTotalCal);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvRuns.setLayoutManager(new LinearLayoutManager(this));

        loadStats();
        loadRuns();
    }

    private void loadStats() {
        ApiClient.getService().getMyStats().enqueue(new Callback<RunModel.UserStats>() {
            @Override
            public void onResponse(Call<RunModel.UserStats> call, Response<RunModel.UserStats> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RunModel.UserStats stats = response.body();
                    tvTotalRuns.setText(String.valueOf(stats.totalRuns));
                    tvTotalDist.setText(String.format("%.1f", stats.totalDistanceKm));
                    tvTotalCal.setText(String.format("%.0f", stats.totalCalories));
                }
            }
            @Override
            public void onFailure(Call<RunModel.UserStats> call, Throwable t) {}
        });
    }

    private void loadRuns() {
        ApiClient.getService().getMyRuns().enqueue(new Callback<List<RunModel.Response>>() {
            @Override
            public void onResponse(Call<List<RunModel.Response>> call,
                                   Response<List<RunModel.Response>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<RunModel.Response> runs = response.body();
                    if (runs.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvRuns.setVisibility(View.GONE);
                    } else {
                        rvRuns.setAdapter(new RunAdapter(runs));
                    }
                }
            }
            @Override
            public void onFailure(Call<List<RunModel.Response>> call, Throwable t) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvRuns.setVisibility(View.GONE);
            }
        });
    }

    // 러닝 기록 어댑터
    static class RunAdapter extends RecyclerView.Adapter<RunAdapter.ViewHolder> {
        private final List<RunModel.Response> runs;

        RunAdapter(List<RunModel.Response> runs) { this.runs = runs; }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_run, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int position) {
            RunModel.Response run = runs.get(position);
            h.tvDist.setText(String.format("%.2f", run.distanceKm));

            int total = run.durationSeconds;
            h.tvTime.setText(String.format("%02d:%02d:%02d",
                    total / 3600, (total % 3600) / 60, total % 60));

            if (run.paceMinPerKm != null && run.paceMinPerKm > 0) {
                int pm = (int) run.paceMinPerKm.doubleValue();
                int ps = (int) ((run.paceMinPerKm - pm) * 60);
                h.tvPace.setText(String.format("%d'%02d\"", pm, ps));
            } else {
                h.tvPace.setText("-");
            }

            String date = run.runDate != null ? run.runDate.substring(0, 10) : "";
            h.tvDate.setText(date);
        }

        @Override
        public int getItemCount() { return runs.size(); }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDist, tvTime, tvPace, tvDate;
            ViewHolder(View v) {
                super(v);
                tvDist = v.findViewById(R.id.tvRunDist);
                tvTime = v.findViewById(R.id.tvRunTime);
                tvPace = v.findViewById(R.id.tvRunPace);
                tvDate = v.findViewById(R.id.tvRunDate);
            }
        }
    }
}
