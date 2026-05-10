package com.herun.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.herun.app.api.ApiClient;
import com.herun.app.model.AuthModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etAge, etWeight, etHeight;
    private Button btnSignup;
    private TextView tvError, tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etUsername = findViewById(R.id.etUsername);
        etEmail    = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etAge      = findViewById(R.id.etAge);
        etWeight   = findViewById(R.id.etWeight);
        etHeight   = findViewById(R.id.etHeight);
        btnSignup  = findViewById(R.id.btnSignup);
        tvError    = findViewById(R.id.tvError);
        tvLogin    = findViewById(R.id.tvLogin);

        btnSignup.setOnClickListener(v -> signup());
        tvLogin.setOnClickListener(v -> finish());
    }

    private void signup() {
        String username = etUsername.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String ageStr   = etAge.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("닉네임, 이메일, 비밀번호는 필수입니다.");
            return;
        }

        Integer age = ageStr.isEmpty() ? null : Integer.parseInt(ageStr);
        Double weight = weightStr.isEmpty() ? null : Double.parseDouble(weightStr);
        Double height = heightStr.isEmpty() ? null : Double.parseDouble(heightStr);

        btnSignup.setEnabled(false);
        tvError.setVisibility(View.GONE);

        AuthModel.SignupRequest request = new AuthModel.SignupRequest(
                username, email, password, age, weight, height);

        ApiClient.getService().signup(request).enqueue(new Callback<AuthModel.TokenResponse>() {
            @Override
            public void onResponse(Call<AuthModel.TokenResponse> call,
                                   Response<AuthModel.TokenResponse> response) {
                btnSignup.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    AuthModel.TokenResponse data = response.body();
                    ApiClient.saveToken(data.token, data.username, data.userId);
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    showError("이미 사용 중인 이메일 또는 닉네임입니다.");
                }
            }

            @Override
            public void onFailure(Call<AuthModel.TokenResponse> call, Throwable t) {
                btnSignup.setEnabled(true);
                showError("서버 연결에 실패했습니다.");
            }
        });
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
