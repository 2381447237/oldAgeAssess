package com.youli.oldageassess.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by liutao on 2017/9/21.
 */

public class MyOkHttpUtils {
    //mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    public static final String BaseUrl="http://web.youli.pw:81";

    public static OkHttpClient okHttpClient=null;

    static String cookies;

    //懒汉
    private static synchronized OkHttpClient getInstance(){

          if(okHttpClient==null){

              okHttpClient=new OkHttpClient();

          }
        cookies=SharedPreferencesUtils.getString("cookie");
          return  okHttpClient;
    }


    /**
     * OKHttp 同步 Get
     *
     * @param url 请求网址
     * @return 获取到数据返回Response，若未获取到数据返回null
     */

    public static Response okHttpGet(String url){
        getInstance();

        Request request=new Request.Builder().addHeader("cookie",cookies).url(url).build();



        Response response=null;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

    }

    /**
     * OKHttp 异步 Post
     *
     * @param url 请求网址
     * @return 获取到数据返回Response，若未获取到数据返回null
     */
    public static Response okHttpPost(String url, HashMap<String,String> paramsMap){

        getInstance();

        FormBody.Builder builder = new FormBody.Builder();

        for(String key:paramsMap.keySet()){
            builder.add(key,paramsMap.get(key));
        }

        RequestBody requestBody=builder.build();

        Request request=new Request.Builder().addHeader("cookie",cookies).url(url).post(requestBody).build();

        Response response=null;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

    };

    //失业无业的调查提交
    public static Response okHttpPost(String url, String ID, String DQYX, String MQZK, String DATE){
        getInstance();
        RequestBody requestBody=new FormBody.Builder()
                .add("MDID",ID).add("DCBZ",DQYX).add("MQZK_NEW",MQZK)
                .add("DQYX_NEW",DATE)
                .build();
        Request request=new Request.Builder().url(url)
                .post(requestBody).addHeader("cookie",cookies).build();
        Response response;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return  null;
        }
        return  response;
    }

    public static Response okHttpPost(String url, String userName){

        getInstance();

        RequestBody requestBody=new FormBody.Builder().add("sfz", userName)
                .build();

        Request request=new Request.Builder().addHeader("cookie",cookies).url(url).post(requestBody).build();

        Response response=null;

        try {
            response=okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;

    };

    //修改密码http://web.youli.pw:89/Json/Set_Pwd.aspx?pwd=123&new_pwd=321
    public static Response okHttpPostFormBody(String url, HashMap<String,String> data){

        try {
            //处理参数
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : data.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(data.get(key), "utf-8")));
                pos++;
            }

            getInstance();
            String cookies=SharedPreferencesUtils.getString("cookies");

            //生成参数
            String params = tempParams.toString();
            Log.e("--1--","params:"+params);

            //创建一个请求实体对象 RequestBody
            RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, params);

            Request request=new Request.Builder().url(url).post(body).addHeader("cookie",cookies).build();
            Log.e("--2--","request:"+request);
            Response response;

            response=okHttpClient.newCall(request).execute();
            Log.e("--3--","response:"+response);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}
