package com.youli.oldageassess.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferencesUtils {
    public static void putString(String key, String content) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("userInfo.txt", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key,content);
        editor.commit();
    }
    public static String getString(String key) {
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("userInfo.txt", Context.MODE_PRIVATE);
       return sp.getString(key,"");
    }

//    public static void putDouble(String key,String content) {
//        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("userInfo.txt", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putDouble(key,content);
//        editor.commit();
//    }
//    public static String getDouble(String key) {
//        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("userInfo.txt", Context.MODE_PRIVATE);
//        return sp.getString(key,"");
//    }

}
