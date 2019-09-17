package com.linken.advertising.net;


import android.text.TextUtils;

import com.linken.advertising.AdvertisingSDK;
import com.linken.advertising.utils.EncryptUtil;
import com.linken.advertising.utils.SystemUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncHttpClient extends SyncHttpClient {
    private final ExecutorService threadPool;
    private String deviceId = (!TextUtils.isEmpty(AdvertisingSDK.getInstance().getDeviceId()) && !"null".equals(AdvertisingSDK.getInstance().getDeviceId().toLowerCase()))
            ? AdvertisingSDK.getInstance().getDeviceId() : SystemUtil.generateFakeImei();

    public AsyncHttpClient() {
        super();
        threadPool = Executors.newCachedThreadPool();
        addHeaders();
    }

    /**
     * 公用的头文件
     */
    private void addHeaders() {
        addHeader("osType", "andorid");
        addHeader("appId", AdvertisingSDK.getInstance().getAppId());
        addHeader("deviceId", deviceId);
        addHeader("thirdUid", EncryptUtil.getMD5_32(deviceId + AdvertisingSDK.getInstance().getAppKey()));
    }

    @Override
    public void get(final String url, final ResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(url, Method.GET, null, handler);
            }
        });
    }

    public void post(RequestBase requestBase, final ResponseHandler handler) {
        addHeaders(requestBase.getHeaders());
        post(requestBase.getURI(), requestBase.getBody(), handler);
    }

    public void get(final RequestBase requestBase, final ResponseHandler handler) {
        addHeaders(requestBase.getHeaders());
        get(requestBase.getURI(), handler);
    }

    @Override
    public void post(final String url, final String map, final ResponseHandler handler) {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                request(url, Method.POST, map, handler);
            }
        });
    }
}
