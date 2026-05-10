package com.herun.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.herun.app.api.ApiClient;
import com.herun.app.model.AuthModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvError, tvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이미 로그인된 경우 메인으로
        if (ApiClient.isLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvError = findViewById(R.id.tvError);
        tvSignup = findViewById(R.id.tvSignup);

        btnLogin.setOnClickListener(v -> login());
        tvSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("이메일과 비밀번호를 입력해주세요.");
            return;
        }

        btnLogin.setEnabled(false);
        tvError.setVisibility(View.GONE);

        ApiClient.getService().login(new AuthModel.LoginRequest(email, password))
                .enqueue(new Callback<AuthModel.TokenResponse>() {
                    @Override
                    public void onResponse(Call<AuthModel.TokenResponse> call,
                                           Response<AuthModel.TokenResponse> response) {
                        btnLogin.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            AuthModel.TokenResponse data = response.body();
                            ApiClient.saveToken(data.token, data.username, data.userId);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            showError("이메일 또는 비밀번호가 올바르지 않습니다.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthModel.TokenResponse> call, Throwable t) {
                        btnLogin.setEnabled(true);
                        showError("서버 연결에 실패했습니다.");
                    }
                });
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
    }
}
