package edu.fudan.tengf.campusapp.http;

import android.util.Log;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.fudan.tengf.campusapp.constant.Constant;

public class DataService {
    private static final String TAG = "DataService";
    public HashMap<String, String> getLoginParam(final String data)     // 从登录网页中获取登录信息参数
    {
        HashMap<String, String> ret = new HashMap<String, String>();
        String regex = "name=\"[A-Za-z0-9_]*\" value=\"[A-Za-z0-9_-]*\"";
        String param1, param2, res;
        Pattern ptn = Pattern.compile(regex);
        Matcher mch = ptn.matcher(data);
        while (mch.find()) {
            res = mch.group();
            param1 = res.substring(res.indexOf("name=\"")+6,res.lastIndexOf("\" value="));
            param2 = res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
            ret.put(param1,param2);
            Log.d(TAG,param1+" "+param2);
        }
        return ret;
    }

    public String getActionUrl(String actionUrl, HashMap<String, String> paramsMap) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String param = tempParams.toString();
            param = String.format("%s?%s",actionUrl,param);
            return param;
        }catch (Exception e) {
            Log.e(TAG, e.toString());
            return Constant.ERROR;
        }
    }
}
