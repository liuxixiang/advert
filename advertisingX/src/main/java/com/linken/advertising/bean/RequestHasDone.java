package com.linken.advertising.bean;

import com.linken.advertising.SDKContants;
import com.linken.advertising.def.OperateType;
import com.linken.advertising.net.RequestBase;

import org.json.JSONException;
import org.json.JSONObject;


public class RequestHasDone extends RequestBase {

    private @OperateType.OperateTypeDef
    String operateType;
    private String id;

    public RequestHasDone(String id, @OperateType.OperateTypeDef String operateType) {
        super();
        this.id = id;
        this.operateType = operateType;
    }

    @Override
    protected String getPath() {
        return SDKContants.URL_HASDONE;
    }

    @Override
    public String getURI() {
        String baseUrl = super.getURI();
        StringBuilder builder = new StringBuilder(baseUrl);
        builder.append("?id=" + id);
        builder.append("&operateType=" + operateType);

        return builder.toString();
    }
}
