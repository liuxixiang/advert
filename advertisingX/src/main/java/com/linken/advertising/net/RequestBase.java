package com.linken.advertising.net;


import com.linken.advertising.AdvertisingSDK;
import com.linken.advertising.SDKContants;

import java.util.HashMap;
import java.util.Map;


public abstract class RequestBase {

    protected String getHost() {
        return SDKContants.BASE_URL;
    }

    protected abstract String getPath();

    protected String getURI() {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost());
        builder.append(getPath());
        return builder.toString();
    }

    protected Map<String, String> getHeaders() {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", AdvertisingSDK.getInstance().getAuthorization() + "");
        return map;
    }

    protected String getBody() {
        return null;
    }

}
