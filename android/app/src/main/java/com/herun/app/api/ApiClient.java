package com.herun.app.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // 에뮬레이터에서 localhost 접근 시 10.0.2.2 사용
    private static final String BASE_URL = "http://192.168.75.171:8080/api/";
    private static Retrofit retrofit = null;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static ApiService getService() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        String token = getToken();
                        if (token != null && !token.isEmpty()) {
                            Request request = original.newBuilder()
                                    .header("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(request);
                        }
                        return chain.proceed(original);
                    })
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }

    private static String getToken() {
        if (appContext == null) return null;
        SharedPreferences prefs = appContext.getSharedPreferences("herun_prefs", Context.MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    public static void saveToken(String token, String username, Long userId) {
        SharedPreferences prefs = appContext.getSharedPreferences("herun_prefs", Context.MODE_PRIVATE);
        prefs.edit()
                .putString("token", token)
                .putString("username", username)
                .putLong("userId", userId)
                .apply();
    }

    public static void clearToken() {
        SharedPreferences prefs = appContext.getSharedPreferences("herun_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    public static boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    public static String getUsername() {
        if (appContext == null) return null;
        SharedPreferences prefs = appContext.getSharedPreferences("herun_prefs", Context.MODE_PRIVATE);
        return prefs.getString("username", null);
    }
}
