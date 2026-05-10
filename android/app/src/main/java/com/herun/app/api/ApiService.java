package com.herun.app.api;

import com.herun.app.model.AuthModel;
import com.herun.app.model.RunModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/signup")
    Call<AuthModel.TokenResponse> signup(@Body AuthModel.SignupRequest request);

    @POST("auth/login")
    Call<AuthModel.TokenResponse> login(@Body AuthModel.LoginRequest request);

    @POST("runs")
    Call<RunModel.Response> saveRun(@Body RunModel.SaveRequest request);

    @GET("runs/my")
    Call<List<RunModel.Response>> getMyRuns();

    @GET("runs/my/stats")
    Call<RunModel.UserStats> getMyStats();
}
