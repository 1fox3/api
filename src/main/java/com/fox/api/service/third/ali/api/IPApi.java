package com.fox.api.service.third.ali.api;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IPApi extends AliThirdBaseApi{

    public Object ipToAddress(String ip) {
        String host = "https://hcapi20.market.alicloudapi.com";
        String path = "/ip";
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("ip", ip);
        JSONObject result = this.handle(host, path, method, headers, querys);
        return "";
    }
}
