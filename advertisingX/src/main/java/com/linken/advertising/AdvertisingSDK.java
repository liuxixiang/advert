package com.linken.advertising;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.linken.advertising.utils.ContextUtils;
import com.linken.advertising.utils.LogUtils;

public class AdvertisingSDK {

    private static volatile AdvertisingSDK sInstance = null;

    private Context mContext;
    private String mAppKey;
    private String mAppId;
    private boolean debug;
    private String mAuthorization;
    private String mDeviceId;
    private IAdvertisingListener mAdvertisingListener;
    private IADCollectListener mAdCollectListener;

    private AdvertisingSDK(Builder builder) {
        this.mContext = builder.mContext;
        this.mAppKey = builder.mAppKey;
        this.mAppId = builder.mAppId;
        this.debug = builder.debug;
        this.mAuthorization = builder.mAuthorization;
        this.mDeviceId = builder.mDeviceId;

        ContextUtils.init(this.mContext);
        LogUtils.setDebug(this.debug);
    }

    public void setAdvertisingListener(IAdvertisingListener advertisingListener) {
        this.mAdvertisingListener = advertisingListener;
    }

    public IAdvertisingListener getAdvertisingListener() {
        return this.mAdvertisingListener;
    }

    public void setCollectListener(IADCollectListener adCollectListener) {
        this.mAdCollectListener = adCollectListener;
    }

    public IADCollectListener getAdCollectListener() {
        return mAdCollectListener;
    }


    public static AdvertisingSDK getInstance() {
        if (sInstance == null) {
            String msg = "SDK is not yet initialized，Please start by AdvertisingSDK.Builder initializing";
            throw new RuntimeException(msg);
        }

        return sInstance;
    }


    public String getAppId() {
        return mAppId;
    }

    public String getAppKey() {
        return mAppKey;
    }

    public String getAuthorization() {
        return mAuthorization;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public static final class Builder {

        private Context mContext;
        private String mAppKey;
        private String mAppId;
        private String mAuthorization;
        private String mDeviceId;
        private boolean debug;


        public Builder() {

        }

        public Builder(AdvertisingSDK feedsSDK) {
            this.mContext = feedsSDK.mContext.getApplicationContext();
            this.mAppKey = feedsSDK.mAppKey;
            this.mAppId = feedsSDK.mAppId;
            this.debug = feedsSDK.debug;
            this.mAuthorization = feedsSDK.mAuthorization;
            this.mDeviceId = feedsSDK.mDeviceId;
        }

        public Builder setContext(Context context) {
            this.mContext = context;
            return this;
        }

        public Builder setAppKey(String appKey) {
            this.mAppKey = appKey;
            return this;
        }

        public Builder setAppId(String id) {
            this.mAppId = id;
            return this;
        }


        public Builder setDebugEnabled(boolean debugEnabled) {
            this.debug = debugEnabled;
            return this;
        }

        public AdvertisingSDK build() {
            if (this.mContext == null) {
                if (this.debug) {
                    Log.e("AdvertisingSDK", "Context cannot be null!");
                }
                return null;
            } else if (this.mAppKey != null && !this.mAppKey.isEmpty()) {
                if (this.mAppId != null && !this.mAppKey.isEmpty()) {
                    if (AdvertisingSDK.sInstance == null) {
                        Class var1 = AdvertisingSDK.class;
                        synchronized (AdvertisingSDK.class) {
                            if (AdvertisingSDK.sInstance == null) {
                                AdvertisingSDK.sInstance = new AdvertisingSDK(this);
                            }
                        }
                    }

                    return AdvertisingSDK.sInstance;
                } else {
                    if (this.debug) {
                        Log.e("AdvertisingSDK", "AppSecret cannot be null or empty!");
                    }
                    return null;
                }
            } else {
                if (this.debug) {
                    Log.e("AdvertisingSDK", "AppKey cannot be null or empty!");
                }
                return null;
            }
        }

        public Builder setAuthorization(String authorization) {
            this.mAuthorization = authorization;
            return this;
        }

        public Builder setDeviceId(String deviceId) {
            this.mDeviceId = deviceId;
            return this;
        }
    }

    public interface IAdvertisingListener {
        void onAdvertisingSucceed(boolean succeed, String id, View AdvertisingLayout, Throwable throwable);

        void onAdvertisingLimit(String msg);

        void onCountDown(int second);

        void onPageLoadFinished();

    }

    public interface IADCollectListener {
        void showCollect(String id, ImageView collectView);

        void onCollectClick(String id, ImageView collectView);

    }
}
