package com.herun.app;

import android.app.Application;

import com.herun.app.api.ApiClient;
import com.kakao.vectormap.KakaoMapSdk;

public class HeRunApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(this);
        KakaoMapSdk.init(this, "2b20b14a54847bb2ab1bdd0f55a36a07");
    }
}
