package com.jtutang.swjtu_xszc;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jtu唐 on 2015/5/25.
 */
public class SwjtuXszc {
    private String username;
    private String password;
    private String cookie;
    private String loginUrl = "http://ocw.swjtu.edu.cn/servlet/UserLoginDataAction";
    private String checkUrl = "http://ocw.swjtu.edu.cn/websys/studentstudytime.jsp";
    private String listUrl = "http://ocw.swjtu.edu.cn/page/viewVideo.jsp?c_id=196";
    private String Referer = "http://ocw.swjtu.edu.cn/websys/videoview.jsp?resource_id=";
    private String sid;
    private String sourceID;
    public SwjtuXszc(String username,String password){
        if(username.isEmpty() || password.isEmpty())return;
        this.username = username;
        this.password = password;
    }
    public boolean isLogin(){
        String restr = null;
        try{
            URL url = new URL(this.checkUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Cookie", this.cookie);
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            String line = "";
            while((line = br.readLine()) != null) {
                restr += line;
            }
            connection.disconnect();
            is.close();
        }catch (Exception e){
        }
        Log.e("DASHABI",restr);
        if(restr.indexOf("学时") >= 0)return true;
        return false;
    }

    public void chooseVideo(ArrayList<String> al){
        int len = al.size() - 1;
        int rand = (int)Math.round(Math.random()*len);
        this.sourceID = al.get(rand).split("=")[1];
    }

    public boolean login(){
        String restr = "";
        try{
            String key = null;
            String cookieVal = null;
            URL url = new URL(this.loginUrl);
            String data = "UserName=" + this.username + "&Password=" + this.password + "&UserType=stu";
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            OutputStream os = connection.getOutputStream();
            os.write(data.getBytes());
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++ ) {
                if (key.equalsIgnoreCase("set-cookie")) {
                    cookieVal = connection.getHeaderField(i);
                    cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                    this.cookie = cookieVal + ";";
                }
            }
            String line = "";
            while((line = br.readLine()) != null){
                restr += line;
            }
            connection.disconnect();
            is.close();
        }catch (IOException e){
        }
        Log.e("DASHABI",restr);
        if(restr.indexOf("成功") >= 0)return true;
        return false;
    }

    public ArrayList<String> grap_list(){
        String restr = null;
        ArrayList<String> relist = new ArrayList<String>();
        try{
            URL url = new URL(this.listUrl);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            InputStreamReader is = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(is);
            String line = "";
            while((line = br.readLine()) != null) {
                restr += line;
            }
            connection.disconnect();
            is.close();
        }catch (Exception e){
        }
        Pattern p = Pattern.compile("<a href=\"(.*?)\" target=_blank>");
        Matcher m = p.matcher(restr);
        while(m.find()){
            relist.add(m.group(1));
        }
        Log.e("DASHABI",restr);
        return relist;
    }

    public String cheat(String status){
        String restr = null;
        String utf8Restr = null;
        try{
            String Url = "http://ocw.swjtu.edu.cn/servlet/UserStudyRecordAction?resource_id=" + this.sourceID;
            Url += "&SetType=" + status;
            if("ADD" == status) Url += "&ranstring=&sid=";
            else Url += "&ranstring=&sid=" + this.sid;
            Url += "&tt=" + System.currentTimeMillis();
            Log.e("DASHABI",Url);
            String referer = this.Referer + this.sourceID;
            URL url = new URL(Url);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Cookie", this.cookie);
            connection.setRequestProperty("User-Agent","User-Agent:Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.111 Safari/537.36");
            connection.setRequestProperty("Referer",referer);
            InputStreamReader is = new InputStreamReader(connection.getInputStream(),"GBK");
            BufferedReader br = new BufferedReader(is);
            String line = "";
            while((line = br.readLine()) != null) {
                restr += line;
            }
            connection.disconnect();
            is.close();
            utf8Restr = new String(restr.getBytes("UTF-8"),"ISO-8859-1");
            utf8Restr = new String(utf8Restr.getBytes("ISO-8859-1"),"UTF-8");
        }catch (Exception e){
        }
        Log.e("DASHABI",utf8Restr);
        Pattern p = Pattern.compile("<select_message>(.*?)<\\/select_message>");
        Matcher m = p.matcher(utf8Restr);
        if("ADD" == status && m.find()){
            this.sid = m.group(1);
            return m.group(1);
        }
        if(utf8Restr.indexOf("结束") >= 0)return "success";
        return "fail";
    }
}
