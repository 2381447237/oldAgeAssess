package com.youli.oldageassess.utils;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

import java.util.regex.Pattern;

/**
 * Created by liutao on 2018/1/12.
 */

public class TextViewUtils {

    public static void titleTvSetStyle(String content, SpannableString ss, String type){

        int length=type.length();

        ss.setSpan(new ForegroundColorSpan(0xFFFF0000), content.indexOf(type), content.indexOf(type) + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new AbsoluteSizeSpan(40), content.indexOf(type), content.indexOf(type) + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), content.indexOf(type), content.indexOf(type) + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    };

    //给每个字符串后面加两个空格
    public  static String appendSpace(String para){

        String regex = "(.{1})";
        para = para.replaceAll(regex,"$1\t\t");

        return  para;

    }

    public static boolean firstIsNumber(String response){

        if(response.length()>0) {

            if (isInteger(response.substring(0, 1))) {
                return true;
            }
        }
        return false;
    }



    //判断字符串是否是数字

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


}
