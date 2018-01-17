package com.youli.oldageassess.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.youli.oldageassess.R;

/**
 * Created by liutao on 2018/1/12.
 */

public class TestActivity extends Activity{

    private LinearLayout ll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);


        ll=findViewById(R.id.test_ll);
       FlexboxLayout fl=new FlexboxLayout(this);
       fl.setFlexWrap(FlexboxLayout.FLEX_WRAP_WRAP);
        TextView tv1=new TextView(this);
        tv1.setText("1.水陆草木之花");
        fl.addView(tv1);

        TextView tv2=new TextView(this);
        tv2.setText("2.可爱者甚蕃3.晋陶渊明独爱");
        fl.addView(tv2);

        TextView tv3=new TextView(this);
        tv3.setText("4.自李唐来");
        fl.addView(tv3);

        EditText et=new EditText(this);
        et.setText("我是填空");
        fl.addView(et);

        TextView tv4=new TextView(this);
        tv4.setText("6.予独爱莲之出淤泥而不染");
        fl.addView(tv4);

        ll.addView(fl);

    }
}
