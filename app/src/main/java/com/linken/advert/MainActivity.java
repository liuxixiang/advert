package com.linken.advert;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
        findViewById(R.id.close_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent m = new Intent(MainActivity.this, SecActivity.class);
                startActivity(m);
            }
        });
        /**
         * 初始化SDK
         */
        new AdvertisingSDK.Builder()
                .setAppId("bitwires")
                .setAppKey("a6e0a011bb62cdcaeab1af13b2626404")
                .setContext(getApplicationContext())
                .setDebugEnabled(BuildConfig.DEBUG)
                .build();
        AdvertisingSDK.getInstance().setCollectListener(new AdvertisingSDK.IADCollectListener() {
            @Override
            public void showCollect(String id, ImageView collectView) {

            }

            @Override
            public void onCollectClick(String id, ImageView collectView) {

            }

        });
        AdvertisingSDK.getInstance().setAdvertisingListener(new AdvertisingSDK.IAdvertisingListener() {

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
