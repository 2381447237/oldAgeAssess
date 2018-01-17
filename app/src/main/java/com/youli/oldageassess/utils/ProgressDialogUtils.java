package com.youli.oldageassess.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by sfhan on 2017/11/7.
 */

public class ProgressDialogUtils {


    private static ProgressDialog progressDialog;

    public static void showMyProgressDialog(Context context){

        progressDialog=new ProgressDialog(context);
        progressDialog.setTitle("正在加载中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismissMyProgressDialog(Context context){

        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog=null;
        }

    }

}
