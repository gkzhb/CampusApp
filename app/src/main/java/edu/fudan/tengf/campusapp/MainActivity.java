package edu.fudan.tengf.campusapp;

//import android.support.v7.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import edu.fudan.tengf.campusapp.constant.Constant;
import edu.fudan.tengf.campusapp.http.DataService;
import edu.fudan.tengf.campusapp.http.RequestManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity {
    String msg ="Android : ";
    String lt,dllt,execution,_eventId,rmShown,stuId="",password="";
    RequestManager mReqMngr= new RequestManager();
    DataService mData = new DataService();

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message mssg) {
            super.handleMessage(mssg);
            switch (mssg.what) {
                case 0x123: {   // 完成获取登录页面
                    Log.d(msg,"Successfully get login page");
                    Log.d(msg, (String) mssg.obj);
                    HashMap<String, String> param = mData.getLoginParam((String) mssg.obj);
                    param.put("username",stuId);
                    param.put("password",password);
                    HashMap<String, String> tmpParam = new HashMap<>();
                    tmpParam.put("service",Constant.jwBaseUrl+Constant.jwHome);
                    mReqMngr.requestAsyn(Constant.uisBaseUrl,mData.getActionUrl(Constant.uisLogin,tmpParam),RequestManager.TYPE_POST_FORM,param,mHandler,0x125);
                    break;
                }
                case 0x124: {   // 网络请求错误
                    Log.d(msg, "Http request failed!");
                    break;
                }
                case 0x125: {
                    Log.d(msg, "Successfully post");
                    Log.d(msg, (String) mssg.obj);
                    HashMap<String, String> param = new HashMap<>();
                    param.put("ignoreHead","1");
                    param.put("setting.kind", "std");
                    param.put("startWeek", "1");
                    param.put("semester.id", "264");
                    param.put("ids", "352998");
                    mReqMngr.requestAsyn(Constant.jwBaseUrl,Constant.jwCourseTable,RequestManager.TYPE_POST_FORM,param,mHandler,0x126);
                    break;
                }
                case 0x126: {
                    Log.d(msg,"Successfully get Course Table!");
                    HashMap<String, String> param = new HashMap<>();
                    param.put("service",Constant.jwBaseUrl+Constant.jwLogin);
                    mReqMngr.requestAsyn(Constant.uisBaseUrl,Constant.uisLogout,RequestManager.TYPE_GET,param,mHandler,0x127);
                    break;
                }
                case 0x127: {
                    Log.d(msg,"Successfully Logout!");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText userName = (EditText) findViewById(R.id.userId);
                EditText pword = (EditText) findViewById(R.id.pass);
                stuId=userName.getText().toString().trim();
                password=pword.getText().toString().trim();
                Log.d(msg, stuId);
                Log.d(msg, password);
                HashMap<String, String> param = new HashMap<String, String>();
                param.put("service",Constant.jwBaseUrl+Constant.jwHome);
                mReqMngr.requestAsyn(Constant.uisBaseUrl, Constant.uisLogin, RequestManager.TYPE_GET, param, mHandler, 0x123);
            }
        });
    }
}
