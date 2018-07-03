package educn.fudan.zhb.helloworld;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class thread {
    private String msg = "app test message : ";
    static String lt = "", dllt = "", execution = "", _eventId = "", rmShown = "";

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private CookieJar myCookieJar = new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            cookieStore.put(url.host(), cookies);
            for (Cookie cookie : cookies) {
                Log.d(msg, "cookie Name==" + cookie.name());
                Log.d(msg, "cookie Path==" + cookie.path());
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    };
    private OkHttpClient mOKHttpClient = new OkHttpClient.Builder()
            .cookieJar(myCookieJar)
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .build();

    private ArrayList<String> lists;
    ArrayList<String> getLoginPara() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                lists = new ArrayList<>();
                try {
                    Request request = new Request.Builder()
                            .url("http://uis.fudan.edu.cn/authserver/login?service=http%3A%2F%2Fjwfw.fudan.edu.cn%2Feams%2Fhome.action")
                            .get()
                            .build();
                    Response response = null;
                    response = mOKHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        int length = responseStr.length();
                        Log.d(msg, "response.code()==" + response.code());
                        Log.d(msg, "response.message()==" + response.message());
                        Log.d(msg, "contentLength==" + length);
                        String regex = "name=\"lt\" value=\"LT-[A-Za-z0-9-]*\"";
                        Pattern ptn = Pattern.compile(regex);
                        Matcher mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        String res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        lt = res;
                        lists.add(lt);
                        Log.d(msg, "lt==" + lt);

                        regex = "name=\"dllt\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        dllt = res;
                        lists.add(dllt);
                        Log.d(msg, "dllt==" + dllt);

                        regex = "name=\"execution\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        execution = res;
                        lists.add(execution);
                        Log.d(msg, "execution==" + execution);

                        regex = "name=\"_eventId\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        _eventId = res;
                        lists.add(_eventId);
                        Log.d(msg, "_eventId==" + _eventId);

                        regex = "name=\"rmShown\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        rmShown = res;
                        lists.add(rmShown);
                        Log.d(msg, "rmShown==" + rmShown);
                    }
                } catch (Exception e) {
                    Log.d(msg, e.toString());
                }
                if(lists.size() > 0){
                    for(int i = 0; i < lists.size(); i++){
                        Log.d("lists_1111111: ", lists.get(i));
                    }
                } else Log.d("lists:", "empty!");
            }
        }).start();
        return lists;
    }

//    void print(){
//        if(lists != null && lists.size() > 0){
//            for(int i = 0; i < lists.size(); i++){
//                Log.d("lists_!11111: ", lists.get(i));
//            }
//        } else Log.d("lists:", "empty!");
//    }
}
