package com.jtutang.swjtu_xszc;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    Button   B_login;
    Button   B_test;
    EditText ET_username;
    EditText ET_password;
    SwjtuXszc sx;
    ArrayList<String> videoList = new ArrayList<String>();
    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:{
                    Toast.makeText(getApplicationContext(),msg.obj.toString(),Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        B_login = (Button)findViewById(R.id.login);
        B_test = (Button)findViewById(R.id.test);
        ET_username = (EditText)findViewById(R.id.username);
        ET_password = (EditText)findViewById(R.id.password);
        B_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = ET_username.getText().toString();
                final String password = ET_password.getText().toString();
                if (username.trim().isEmpty() || password.trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "请输入正确的账号/密码" + password, Toast.LENGTH_SHORT).show();
                    return;
                }
                (new Thread() {
                    public void run() {
                        //String url = "UserName=20122185&Password=tangchen421&UserType=stu";
                        //HttpTools.httpPost(handler,"http://ocw.swjtu.edu.cn/servlet/UserLoginDataAction",url,1);
                        sx = new SwjtuXszc(username, password);
                        if(sx.login()){
                            videoList = sx.grap_list();
                            sx.chooseVideo(videoList);
                            Message msg = new Message();
                            msg.obj = sx.cheat("ADD");
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }

                    }
                }).start();
            }
        });

        B_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(){
                    public void run(){
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = sx.cheat("STOP");
                        handler.sendMessage(msg);
                    }
                }).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
