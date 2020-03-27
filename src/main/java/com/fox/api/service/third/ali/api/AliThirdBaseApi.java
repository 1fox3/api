package com.fox.api.service.third.ali.api;

import com.fox.api.service.third.ali.entity.AliCount;
import com.fox.api.service.third.ali.util.HttpUtils;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AliThirdBaseApi {

    @Autowired
    AliCount aliCount;

    protected JSONObject handle(String host, String path, String method, Map<String, String> headers, Map<String, String> params) {
        JSONObject result = null;
        headers.put("Authorization", "APPCODE " + aliCount.getAppCode());
        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, params);
            String responseJsonStr = EntityUtils.toString(response.getEntity());
            result = JSONObject.fromObject(responseJsonStr);
            result = result.getJSONObject("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
