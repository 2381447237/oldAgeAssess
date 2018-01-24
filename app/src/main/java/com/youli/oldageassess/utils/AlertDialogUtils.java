package com.youli.oldageassess.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 作者: zhengbin on 2017/9/21.
 * <p>
 * 邮箱:2381447237@qq.com
 * <p>
 * github:2381447237
 *
 * 自定义对话框工具类
 */

public class AlertDialogUtils {


    private Context context;
    private int layout;
    private View view;
    private AlertDialog dialog;
    private int style;


    public AlertDialogUtils(Context context, int layout, int style) {
        this.context = context;
        this.layout = layout;
        this.style=style;
    }

    public AlertDialogUtils(Context context, int layout) {
        this.context = context;
        this.layout = layout;

    }

    public void showAlertDialog(){//显示对话框

        AlertDialog.Builder builder=new AlertDialog.Builder(context,style);

        view= LayoutInflater.from(context).inflate(layout,null);

        builder.setView(view);

        dialog=builder.create();

        dialog.setCancelable(false);//设置点击对话框外面的地方，对话框不能消失

        dialog.show();

    }

   public View getAduView(){//得到对话框里面的控件

        return view!=null?view:null;
    }

    public void dismissAlertDialog(){//隐藏对话框
        dialog.dismiss();
    }

}
