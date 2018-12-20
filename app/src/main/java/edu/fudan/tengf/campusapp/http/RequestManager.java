package edu.fudan.tengf.campusapp.http;

/**
 *  From https://www.cnblogs.com/whoislcj/p/5526431.html
 *  使用：
 *      1. 创建 RequestManagter 实例对象
 *      2. 同步请求用 requestSyn()
 *          函数，返回 String 对象
 *      3. 异步请求用 requestAsyn() 函数
 */


//import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestManager {
    public static final int TYPE_GET = 0;//get请求
    public static final int TYPE_POST_JSON = 1;//post请求参数为json
    public static final int TYPE_POST_FORM = 2;//post请求参数为表单
    public static final String ERROR= "Error";

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    //private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = RequestManager.class.getSimpleName();
    //private static final String baseUrl = "http://jwfw.fudan.edu.cn/";//请求接口根地址
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private CookieJar mCookieJar=new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
        {
            cookieStore.put(url.host(), cookies);
            for (Cookie cookie:cookies) {
                Log.d(TAG,"cookie Name=="+cookie.name());
                Log.d(TAG,"cookie Path=="+cookie.path());
            }
        }
        @Override
        public List<Cookie> loadForRequest(HttpUrl url)
        {
            List<Cookie> cookies = cookieStore.get(url.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    };

    /**
     * 统一为请求添加头信息
     * @return 返回 Request.Builder 对象
     */
    private Request.Builder addHeaders() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive");
                //.addHeader("platform", "2")
                //.addHeader("phoneModel", Build.MODEL)
                //.addHeader("systemVersion", Build.VERSION.RELEASE);
        return builder;
    }

    /**
     * 初始化RequestManager
     */
    public RequestManager() {
        //初始化OkHttpClient
        mOkHttpClient = new OkHttpClient().newBuilder()
                .cookieJar(mCookieJar)
                .connectTimeout(10, TimeUnit.SECONDS)//设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)//设置写入超时时间
                .build();
    }


    /**
     *  okHttp同步请求统一入口
     * @param baseUrl   根地址
     * @param actionUrl 接口地址
     * @param requestType   请求类型
     * @param paramsMap 请求参数
     */
    public String requestSyn(String baseUrl, String actionUrl, int requestType, HashMap<String, String> paramsMap) {
        switch (requestType) {
            case TYPE_GET:
                return requestGetBySyn(baseUrl, actionUrl, paramsMap);
                //break;
            case TYPE_POST_JSON:
                return requestPostBySyn(baseUrl, actionUrl, paramsMap);
                //break;
            case TYPE_POST_FORM:
                return requestPostBySynWithForm(baseUrl, actionUrl, paramsMap);
                //break;
        }
        return ERROR;
    }

    /**
     * okHttp get同步请求
     * @param baseUrl   根地址
     * @param actionUrl  接口地址
     * @param paramsMap   请求参数
     */
    private String requestGetBySyn(String baseUrl, String actionUrl, HashMap<String, String> paramsMap) {
        StringBuilder tempParams = new StringBuilder();
        try {
            //处理参数
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //对参数进行URLEncoder
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s%s?%s", baseUrl, actionUrl, tempParams.toString());
            //创建一个请求
            Request request = addHeaders().url(requestUrl).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            final Response response = call.execute();
            return response.body().string();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return ERROR;
    }

    /**
     * okHttp post同步请求
     * @param baseUrl   根地址
     * @param actionUrl  接口地址
     * @param paramsMap   请求参数
     */
    private String requestPostBySyn(String baseUrl, String actionUrl, HashMap<String, String> paramsMap) {
        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            //补全请求地址
            String requestUrl = String.format("%s%s", baseUrl, actionUrl);
            //生成参数
            String params = tempParams.toString();
            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(body).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            //请求执行成功
            if (response.isSuccessful()) {
                //获取返回数据 可以是String，bytes ,byteStream
                return response.body().string();
                //Log.e(TAG, "response ----->" + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return ERROR;
    }

    /**
     * okHttp post同步请求表单提交
     * @param baseUrl   根地址
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     */
    private String requestPostBySynWithForm(String baseUrl, String actionUrl, HashMap<String, String> paramsMap) {
        try {
            //创建一个FormBody.Builder
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                //追加表单信息
                builder.add(key, paramsMap.get(key));
            }
            //生成表单实体对象
            RequestBody formBody = builder.build();
            //补全请求地址
            String requestUrl = String.format("%s%s", baseUrl, actionUrl);
            //创建一个请求
            final Request request = addHeaders().url(requestUrl).post(formBody).build();
            //创建一个Call
            final Call call = mOkHttpClient.newCall(request);
            //执行请求
            Response response = call.execute();
            if (response.isSuccessful()) {
                return response.body().string();
                //Log.e(TAG, "response ----->" + response.body().string());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return ERROR;
    }

    /**
     * okHttp异步请求统一入口
     * @param baseUrl   根地址
     * @param actionUrl   接口地址
     * @param requestType 请求类型
     * @param paramsMap   请求参数
     * @param handler 回调 Handler
     * @param what  回调 信息类型
     * //@param <T> 数据泛型
     **/
    public Call requestAsyn(String baseUrl, String actionUrl, int requestType, HashMap<String, String> paramsMap, final Handler handler, final int what) {
        Call call = null;
        switch (requestType) {
            case TYPE_GET:
                call = requestGetByAsyn(baseUrl, actionUrl, paramsMap, handler, what);
                break;
            case TYPE_POST_JSON:
                call = requestPostByAsyn(baseUrl, actionUrl, paramsMap, handler, what);
                break;
            case TYPE_POST_FORM:
                call = requestPostByAsynWithForm(baseUrl, actionUrl, paramsMap, handler, what);
                break;
        }
        return call;
    }

    /**
     * okHttp get异步请求
     * @param baseUrl   根地址
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param handler 回调 Handler
     * @param what  回调 信息类型
     * //@param <T> 数据泛型
     * @return
     */
    private Call requestGetByAsyn(String baseUrl, String actionUrl, HashMap<String, String> paramsMap, final Handler handler, final int what) {
        StringBuilder tempParams = new StringBuilder();
        try {
            int pos = 0;
            for (String key : paramsMap.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(paramsMap.get(key), "utf-8")));
                pos++;
            }
            String requestUrl = String.format("%s%s?%s", baseUrl, actionUrl, tempParams.toString());
            final Request request = addHeaders().url(requestUrl).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", handler, what);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.d(TAG, "response ----->" + string);
                        successCallBack(string, handler, what);
                    } else {
                        failedCallBack("服务器错误", handler, what);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * okHttp post异步请求
     * @param baseUrl   根地址
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param handler 回调 Handler
     * @param what  回调 信息类型
     * //@param <T> 数据泛型
     * @return
     */
    private Call requestPostByAsyn(String baseUrl, String actionUrl, HashMap<String, String> paramsMap, final Handler handler, final int what) {
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
            String params = tempParams.toString();
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);
            String requestUrl = String.format("%s%s", baseUrl, actionUrl);
            final Request request = addHeaders().url(requestUrl).post(body).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", handler, what);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.d(TAG, "response ----->" + string);
                        successCallBack(string, handler, what);
                    } else {
                        failedCallBack("服务器错误", handler, what);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * okHttp post异步请求表单提交
     * @param baseUrl   根地址
     * @param actionUrl 接口地址
     * @param paramsMap 请求参数
     * @param handler 回调 Handler
     * @param what  回调 信息类型
     * //@param <T> 数据泛型
     * @return
     */
    private Call requestPostByAsynWithForm(String baseUrl, String actionUrl, HashMap<String, String> paramsMap, final Handler handler, final int what) {
        try {
            FormBody.Builder builder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
            RequestBody formBody = builder.build();
            String requestUrl = String.format("%s%s", baseUrl, actionUrl);
            final Request request = addHeaders().url(requestUrl).post(formBody).build();
            final Call call = mOkHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    failedCallBack("访问失败", handler, what);
                    Log.e(TAG, e.toString());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String string = response.body().string();
                        Log.d(TAG, "response ----->" + string);
                        successCallBack(string, handler, what);
                    } else {
                        failedCallBack("服务器错误", handler, what);
                    }
                }
            });
            return call;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * 统一同意处理成功信息
     * @param result
     * @param handler 回调 Handler
     * @param what  回调 信息类型
     * //@param <T>
     */
    private void successCallBack(final String result, final Handler handler, final int what) {
        Message message= Message.obtain();
        message.what = what;
        message.obj = result;
        handler.sendMessage(message);
    }

    /**
     * 统一处理失败信息
     * @param errorMsg
     * @param handler 回调 Handler
     * @param what  回调 信息类型
     * //@param <T>
     */
    private void failedCallBack(final String errorMsg, final Handler handler, final int what) {
        Log.d(TAG, errorMsg);
        handler.sendEmptyMessage(0x124);
    }
}
