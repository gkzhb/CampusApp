package educn.fudan.zhb.helloworld;

import android.annotation.SuppressLint;
import android.os.Bundle;
//import android.os.StrictMode;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.app.Activity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity {
    String msg = "app test message";
    String lt = "", dllt = "", execution = "", _eventId = "", rmShown = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private Callback getCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(msg, "Failed");
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            final ResponseBody resBody = response.body();
            final String responseStr = resBody.string();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView promptText = (TextView) findViewById(R.id.promptText);
                    promptText.setText(responseStr);
                }

            });
            Log.d(msg, "In the onResponse of getCallBack");
            String regex = "name=\"lt\" value=\"LT-[A-Za-z0-9-]*\"";
            Pattern ptn = Pattern.compile(regex);
            Matcher mch = ptn.matcher(responseStr);
            String res = mch.group();
            res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
            lt = res;

            regex = "name=\"dllt\" value=\"[A-Za-z0-9-]*\"";
            ptn = Pattern.compile(regex);
            mch = ptn.matcher(responseStr);
            res = mch.group();
            res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
            dllt = res;

            regex = "name=\"execution\" value=\"[A-Za-z0-9-]*\"";
            ptn = Pattern.compile(regex);
            mch = ptn.matcher(responseStr);
            res = mch.group();
            res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
            execution = res;

            regex = "name=\"_eventId\" value=\"[A-Za-z0-9-]*\"";
            ptn = Pattern.compile(regex);
            mch = ptn.matcher(responseStr);
            res = mch.group();
            res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
            _eventId = res;

            regex = "name=\"rmShown\" value=\"[A-Za-z0-9-]*\"";
            ptn = Pattern.compile(regex);
            mch = ptn.matcher(responseStr);
            res = mch.group();
            res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
            rmShown = res;

            Log.d(msg, "response.code()==" + response.code());
            Log.d(msg, "response.message()==" + response.message());
            Log.d(msg, "lt==" + lt);
            Log.d(msg, "dllt==" + dllt);
            Log.d(msg, "execution==" + execution);
            Log.d(msg, "_eventId==" + _eventId);
            Log.d(msg, "rmShown==" + rmShown);

        }
    };

    private Callback postCallBack = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(msg, "Failed");
        }

        @Override
        public void onResponse(Call call, final Response response) throws IOException {
            final String responseStr = response.body().string();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView promptText = (TextView) findViewById(R.id.promptText);
                    promptText.setText(responseStr);
                }

            });
            Log.d(msg, "response.code()=" + response.code());
            Log.d(msg, "response.message()=" + response.message());
            Log.d(msg, "res=" + responseStr);
            Log.d("text context ", responseStr);

        }
    };

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
    private Request mRequest;

    private void init() {
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userName = (EditText) findViewById(R.id.userId);
                EditText password = (EditText) findViewById(R.id.pass);
                getCourses(userName.getText().toString().trim(), password.getText().toString().trim());

            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    ArrayList<String> lists = (ArrayList<String>) msg.obj;
                    if (lists != null && lists.size() > 0) {
                        lt = lists.get(0);
                        dllt = lists.get(1);
                        execution = lists.get(2);
                        _eventId = lists.get(3);
                        rmShown = lists.get(4);
                        String stuId = lists.get(5);
                        String pass = lists.get(6);
                        login(stuId, pass, lt, dllt, execution, _eventId, rmShown);
                    } else Log.d("lists", "empty!");

            }
        }
    };

    private void getLoginPara(final String stuId, final String pass) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<String> lists = new ArrayList<>();
                    Request request = new Request.Builder()
                            .url(getResources().getString(R.string.loginUrl))
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
                        lists.add(res);

                        regex = "name=\"dllt\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        lists.add(res);

                        regex = "name=\"execution\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        lists.add(res);

                        regex = "name=\"_eventId\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        lists.add(res);

                        regex = "name=\"rmShown\" value=\"[A-Za-z0-9-]*\"";
                        ptn = Pattern.compile(regex);
                        mch = ptn.matcher(responseStr);
                        Log.d(msg, "match?=" + mch.find());
                        res = mch.group();
                        res = res.substring(res.indexOf("value=\"") + 7, res.lastIndexOf("\""));
                        lists.add(res);
                        lists.add(stuId);
                        lists.add(pass);
                        //send message
                        mHandler.sendEmptyMessage(0);
                        Message message = new Message();
                        message.obj = lists;
                        mHandler.sendMessage(message);
                    }
                } catch (Exception e) {
                    Log.d(msg, e.toString());
                }
            }
        }).start();
    }

    private void getCourses(String stuId, String password) {
        TextView promptText = (TextView) findViewById(R.id.promptText);
        if (stuId.equals("")) {
            promptText.setText(R.string.stuIdError);
        } else if (password.equals("")) {
            promptText.setText(R.string.passwordError);
        }
        promptText.setText(stuId);
        // todo and try
        getLoginPara(stuId, password);
        // todo end finished!

        /*RequestBody requestBodyCourses = new FormBody.Builder()
                .add("ignoreHead","1")
                .add("setting.kind","std")
                .add("startWeek","1")
                .add("semester.id","264")
                .add("ids","352998")
                .build();
        request = new Request.Builder()
                .url(getResources().getString(R.string.courseTableUrl))
                .post(requestBodyCourses)
                .build();
        okHttpClient.newCall(request).enqueue(postCallBack);        */

    }

    private void login(String stuId, String pass, String lt, String dllt, String execution, String _eventId, String rmShown) {

        Request request = new Request.Builder()
                .url(getResources().getString(R.string.loginUrl))
                .get()
                .build();
        if(cookieStore.size() > 0) {
            Iterator iterator = cookieStore.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue().toString();
                Log.d(key, value);
            }
        } else Log.d("cookies ", "empty!");
        if (!lt.equals("") && !dllt.equals("") && !execution.equals("") && !_eventId.equals("") && !rmShown.equals("")) {
            Log.d(msg, "Request Post Login");
            RequestBody requestBodyLogin = new FormBody.Builder()
                    .add("username", stuId)
                    .add("password", pass)
                    .add("lt", lt)
                    .add("dllt", dllt)
                    .add("execution", execution)
                    .add("_eventId", _eventId)
                    .add("rmShown", rmShown)
                    .build();
            String tmpUrlOri = getResources().getString(R.string.loginUrl);
            Log.d("stuId== ", stuId);
            Log.d("pass==", pass);
            Log.d("lt==", lt);
            Log.d("dllt== ", dllt);
            Log.d("excution==", execution);
            Log.d("eventId== ", _eventId);
            Log.d("rmShown== ", rmShown);
            String tmpUrl = tmpUrlOri.substring(0, tmpUrlOri.lastIndexOf("?")) + cookieStore.get("JSESSIONID").toString() + tmpUrlOri.lastIndexOf("?");
            Log.d(msg, "PostUrl==" + tmpUrl);
            request = new Request.Builder()
                    .url(tmpUrl)
                    .post(requestBodyLogin)
                    .build();
            mOKHttpClient.newCall(request).enqueue(postCallBack);
        }
    }
}