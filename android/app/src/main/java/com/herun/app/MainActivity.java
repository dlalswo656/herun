package com.herun.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.herun.app.api.ApiClient;

public class MainActivity extends AppCompatActivity {

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

        tvUsername.setText(ApiClient.getUsername());

        btnStartRun.setOnClickListener(v ->
                startActivity(new Intent(this, RunningActivity.class)));

        btnHistory.setOnClickListener(v ->
                startActivity(new Intent(this, HistoryActivity.class)));
    }
}
