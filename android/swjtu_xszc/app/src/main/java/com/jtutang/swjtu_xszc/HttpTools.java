package com.jtutang.swjtu_xszc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.os.Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by jtu唐 on 2015/4/26.
 */
public class HttpTools {
    public static void GET(String path){
        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            String line = "";
            String restr = "";
            while((line = br.readLine()) != null){
                restr += line;
            }
            connection.disconnect();
            is.close();
        }catch (IOException e){
        }
    }
    public static void getImageFromUrl(Handler handler,String path){
        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            InputStream is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Message msg = new Message();
            msg.what = 1;
            msg.obj = bitmap;
            handler.sendMessage(msg);
            connection.disconnect();
            is.close();
        }catch (IOException e){
        }
    }

    public static String httpGet(String path){
        String restr = "";
        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            String line = "";
            while((line = br.readLine()) != null){
                restr += line;
            }
            connection.disconnect();
            is.close();
        }catch (IOException e){
        }
        return restr;
    }

    public static void httpPost(Handler handler,String path,String data,int MSG_FLAG){
        try{
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            String line = "";
            String restr = "";
            while((line = br.readLine()) != null){
                restr += line;
            }
            Message msg = new Message();
            msg.what = MSG_FLAG;
            msg.obj = restr;
            handler.sendMessage(msg);
            connection.disconnect();
            is.close();
        }catch (IOException e){
        }
    }

    public static boolean isOnLine(Context main){
        ConnectivityManager cm= (ConnectivityManager)main.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=cm.getActiveNetworkInfo();
        if(info != null)return true;
        return false;
    }
}
