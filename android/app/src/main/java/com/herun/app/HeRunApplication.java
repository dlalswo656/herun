package com.herun.app;

import android.app.Application;
import com.herun.app.api.ApiClient;

public class HeRunApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(this);
        // 카카오맵 SDK 초기화 (SDK 추가 후 활성화)
        // KakaoMapSdk.init(this, "2b20b14a54847bb2ab1bdd0f55a36a07");
    }
}
