package com.youli.oldageassess.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 2017/9/21.
 */

public class ActivityController extends Activity {

    public  static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public  static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity : activities){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }

}
