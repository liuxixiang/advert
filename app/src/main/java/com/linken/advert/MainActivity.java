package com.linken.advert;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.linken.advertising.AdvertisingFragment;
import com.linken.advertising.AdvertisingSDK;
import com.linken.advertising.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {

    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTime = findViewById(R.id.time);
        /**
         * 初始化SDK
         */
        new AdvertisingSDK.Builder()
                .setAppId("beekuaibao")
                .setAppKey("72649e5e146a8a058c0362708da862f1")
                .setContext(getApplicationContext())
                .setDebugEnabled(BuildConfig.DEBUG)
                .build();
        AdvertisingSDK.getInstance().setIAdvertisingListener(new AdvertisingSDK.IAdvertisingListener() {

            @Override
            public void onAdvertisingSucceed(boolean b, String s, View view, Throwable throwable) {

            }

            @Override
            public void onAdvertisingLimit(String msg) {
                ToastUtils.showShort(getApplicationContext(), msg + "");
            }

            @Override
            public void onCountDown(int second) {
                if (second > 0) {
                    mTime.setVisibility(View.VISIBLE);
                    mTime.setText(second + "s");
                } else {
                    mTime.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageLoadFinished() {

            }

        });

        AdvertisingFragment fragment = AdvertisingFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitNowAllowingStateLoss();
    }

}
