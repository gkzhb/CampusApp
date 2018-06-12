package educn.fudan.zhb.helloworld;

//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;
import android.app.Activity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/

public class MainActivity extends Activity {
    String msg="Android : ";
   @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);
       Log.d(msg,"The onCreate() event");
       init();
   }

   private Callback callback = new Callback() {
       @Override
       public void onFailure(Call call, IOException e) {
           //ToastUtil.showToast(GetActivity.this, "Get 失败");
           Log.d(msg, "Failed");
       }

       @Override
       public void onResponse(Call call, final Response response) throws IOException {
           final String responseStr = response.body().string();
                /*runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_result.setText(responseStr);
                    }

                });*/

           Log.d(msg,responseStr);
           //TextView promptText = (TextView) findViewById(R.id.promptText);
           //promptText.setText(responseStr);
       }
   };

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
                //getBaidu();
            }
        });
    }

    private void getBaidu() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        /*RequestBody requestBodyPost = new FormBody.Builder()
                .add("username",stuId)
                .add("password",password)
                .build();*/
        Request request = new Request.Builder()
                .get()
                .url("https://www.baidu.com")
                .build();
        Call call=okHttpClient.newCall(request);
        call.enqueue(callback);

        /*try
        {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Log.d(msg,"response.code()=="+response.code());
                Log.d(msg,"response.message()=="+response.message());
                Log.d(msg,"res=="+response.body().string());
            }
            else {
                Log.d(msg,"Failed to get response"+response.code());
            }
        } catch (Exception e) {
            Log.d(msg,"Error");
            e.printStackTrace();
        }*/
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

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        RequestBody requestBodyLogin = new FormBody.Builder()
                .add("username",stuId)
                .add("password",password)
                .add("lt","LT-1545413-4iEnbcYqDmPiBEdKrkNN7KcVDPjq3c1528512938051-RSg5-cas")
                .add("dllt","userNamePasswordLogin")
                .add("execution","e2s1")
                .add("_eventId","submit")
                .add("rmShown","1")
                .build();
        RequestBody requestBodyClass = new FormBody.Builder()
                .add("ignoreHead","1")
                .add("setting.kind","std")
                .add("startWeek","1")
                .add("semester.id","264")
                .add("ids","352998")
                .build();
        Request request = new Request.Builder()
                .url(getResources().getString(R.string.loginUrl))
                .post(requestBodyLogin)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
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

