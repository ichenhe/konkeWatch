package cc.chenhe.konke.watch;

import android.app.Application;

import cn.openwatch.communication.OpenWatchCommunication;

/**
 * Created by 宸赫 on 2015/9/26.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OpenWatchCommunication.init(getApplicationContext());
    }
}
