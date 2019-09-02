package com.linken.advertising.bean;

import com.linken.advertising.SDKContants;
import com.linken.advertising.def.OperateType;
import com.linken.advertising.net.RequestBase;

import org.json.JSONException;
import org.json.JSONObject;


public class RequestAdCommit extends RequestBase {

    private @OperateType.OperateTypeDef
    String operateType;
    private String id;

    public RequestAdCommit(String id, @OperateType.OperateTypeDef String operateType) {
        super();
        this.id = id;
        this.operateType = operateType;
    }

    @Override
    protected String getPath() {
        return SDKContants.URL_COMMIT;
    }

    @Override
    protected String getBody() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("operateType", operateType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
