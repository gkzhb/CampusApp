package educn.fudan.zhb.helloworld;

import android.os.Bundle;
//import android.os.StrictMode;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainActivity extends Activity {
    String msg="Android : ";
    String lt,dllt,execution,_eventId,rmShown;

   @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       Log.d(msg,"The onCreate() event");
       init();
    }


    private Callback getCallBack = new Callback() {
       @Override
       public void onFailure(Call call, IOException e) {
           Log.d(msg, "Failed");
       }

       @Override
       public void onResponse(Call call, final Response response) throws IOException {
           final ResponseBody resBody=response.body();
           final String responseStr = resBody.string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView promptText = (TextView) findViewById(R.id.promptText);
                        promptText.setText(responseStr);
                    }

                });
           Log.d(msg,"In the onResponse of getCallBack");
           String regex="name=\"lt\" value=\"LT-[A-Za-z0-9-]*\"";
           Pattern ptn=Pattern.compile(regex);
           Matcher mch=ptn.matcher(responseStr);
           String res=mch.group();
           res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
           lt=res;

           regex="name=\"dllt\" value=\"[A-Za-z0-9-]*\"";
           ptn=Pattern.compile(regex);
           mch=ptn.matcher(responseStr);
           res=mch.group();
           res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
           dllt=res;

           regex="name=\"execution\" value=\"[A-Za-z0-9-]*\"";
           ptn=Pattern.compile(regex);
           mch=ptn.matcher(responseStr);
           res=mch.group();
           res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
           execution=res;

           regex="name=\"_eventId\" value=\"[A-Za-z0-9-]*\"";
           ptn=Pattern.compile(regex);
           mch=ptn.matcher(responseStr);
           res=mch.group();
           res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
           _eventId=res;

           regex="name=\"rmShown\" value=\"[A-Za-z0-9-]*\"";
           ptn=Pattern.compile(regex);
           mch=ptn.matcher(responseStr);
           res=mch.group();
           res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
           rmShown=res;

           Log.d(msg,"response.code()=="+response.code());
           Log.d(msg,"response.message()=="+response.message());
           Log.d(msg,"lt=="+lt);
           Log.d(msg,"dllt=="+dllt);
           Log.d(msg,"execution=="+execution);
           Log.d(msg,"_eventId=="+_eventId);
           Log.d(msg,"rmShown=="+rmShown);

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
            Log.d(msg,"response.code()=="+response.code());
            Log.d(msg,"response.message()=="+response.message());
            Log.d(msg,"res=="+responseStr);

        }
    };

    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private CookieJar myCookieJar=new CookieJar() {
      @Override
      public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
      {
          cookieStore.put(url.host(), cookies);
          for (Cookie cookie:cookies) {
              Log.d(msg,"cookie Name=="+cookie.name());
              Log.d(msg,"cookie Path=="+cookie.path());
          }
      }
      @Override
      public List<Cookie> loadForRequest(HttpUrl url)
      {
          List<Cookie> cookies = cookieStore.get(url.host());
          return cookies != null ? cookies : new ArrayList<Cookie>();
      }
    };
    private OkHttpClient mOKHttpClient= new OkHttpClient.Builder()
            .cookieJar(myCookieJar)
            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
            .readTimeout(10000L, TimeUnit.MILLISECONDS)
            .build();
    private Request mRequest;
    private void init(){

        Button loginBtn=(Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText userName = (EditText) findViewById(R.id.userId);
                EditText password = (EditText) findViewById(R.id.pass);
                Log.d(msg, userName.getText().toString().trim());
                Log.d(msg, password.getText().toString().trim());
                getCourses(userName.getText().toString().trim(),password.getText().toString().trim());

            }
        });
    }

    private int getLoginPara()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                            .cookieJar(myCookieJar)
                            .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                            .readTimeout(10000L, TimeUnit.MILLISECONDS)
                            .build();*/
                    Request request = new Request.Builder()
                            .url(getResources().getString(R.string.loginUrl))
                            .get()
                            .build();
                    Response response = null;
                    response = mOKHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String responseStr=response.body().string();
                        int length=responseStr.length();
                        Log.d(msg, "response.code()==" + response.code());
                        Log.d(msg, "response.message()==" + response.message());
                        Log.d(msg,"contentLength=="+length);
                        String regex="name=\"lt\" value=\"LT-[A-Za-z0-9-]*\"";
                        Pattern ptn=Pattern.compile(regex);
                        Matcher mch=ptn.matcher(responseStr);
                        Log.d(msg,"match?="+mch.find());
                        String res=mch.group();
                        res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
                        lt=res;
                        Log.d(msg,"lt=="+lt);

                        regex="name=\"dllt\" value=\"[A-Za-z0-9-]*\"";
                        ptn=Pattern.compile(regex);
                        mch=ptn.matcher(responseStr);
                        Log.d(msg,"match?="+mch.find());
                        res=mch.group();
                        res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
                        dllt=res;
                        Log.d(msg,"dllt=="+dllt);

                        regex="name=\"execution\" value=\"[A-Za-z0-9-]*\"";
                        ptn=Pattern.compile(regex);
                        mch=ptn.matcher(responseStr);
                        Log.d(msg,"match?="+mch.find());
                        res=mch.group();
                        res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
                        execution=res;
                        Log.d(msg,"execution=="+execution);

                        regex="name=\"_eventId\" value=\"[A-Za-z0-9-]*\"";
                        ptn=Pattern.compile(regex);
                        mch=ptn.matcher(responseStr);
                        Log.d(msg,"match?="+mch.find());
                        res=mch.group();
                        res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
                        _eventId=res;
                        Log.d(msg,"_eventId=="+_eventId);

                        regex="name=\"rmShown\" value=\"[A-Za-z0-9-]*\"";
                        ptn=Pattern.compile(regex);
                        mch=ptn.matcher(responseStr);
                        Log.d(msg,"match?="+mch.find());
                        res=mch.group();
                        res=res.substring(res.indexOf("value=\"")+7,res.lastIndexOf("\""));
                        rmShown=res;
                        Log.d(msg,"rmShown=="+rmShown);
                    }
                } catch (Exception e) {
                    Log.d(msg,e.toString());
                }
            }
        }).start();
        return 0;
    }

    private void getCourses(String stuId,String password) {
        TextView promptText = (TextView) findViewById(R.id.promptText);
        if (stuId.equals(""))
        {
            promptText.setText(R.string.stuIdError);
            return;
        }
        if (password.equals(""))
        {
            promptText.setText(R.string.passwordError);
            return;
        }
        promptText.setText(stuId);

        /*OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(myCookieJar)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();*/
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.loginUrl))
                .get()
                .build();
        //okHttpClient.newCall(request).enqueue(getCallBack);

        getLoginPara();

        Log.d(msg,"Judge login para");
        if (null!=lt&&null!=dllt&&null!=execution&&null!=_eventId&&null!=rmShown) {
            Log.d(msg,"Request Post Login");
            RequestBody requestBodyLogin = new FormBody.Builder()
                .add("username",stuId)
                .add("password",password)
                .add("lt",lt)
                .add("dllt",dllt)
                .add("execution",execution)
                .add("_eventId",_eventId)
                .add("rmShown",rmShown)
                .build();
            String tmpUrlOri=getResources().getString(R.string.loginUrl);
            String tmpUrl=tmpUrlOri.substring(0,tmpUrlOri.lastIndexOf("?"))+cookieStore.get("JSESSIONID").toString()+tmpUrlOri.lastIndexOf("?");
            Log.d(msg,"PostUrl=="+tmpUrl);
            request = new Request.Builder()
                .url(tmpUrl)
                .post(requestBodyLogin)
                .build();
            mOKHttpClient.newCall(request).enqueue(postCallBack);
        }

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

   @Override
    protected void onStart() {
       super.onStart();
       Log.d(msg,"The onStart() event");
   }

   @Override
    protected void onResume() {
       super.onResume();
       Log.d(msg,"The onResume() event");
   }

   @Override
    protected void onPause() {
       super.onPause();
       Log.d(msg,"The onPause() event");
   }

   @Override
    protected void onStop() {
       super.onStop();
       Log.d(msg,"The onStop() event");
   }

   @Override
    public void onDestroy() {
       super.onDestroy();
       Log.d(msg,"The onDestroy() event");
   }
}

