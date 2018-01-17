package com.youli.oldageassess.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by liutao on 2018/1/11.
 */

public class BaseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityController.addActivity(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ActivityController.removeActivity(this);
    }
}
