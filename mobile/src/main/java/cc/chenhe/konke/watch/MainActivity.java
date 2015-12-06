package cc.chenhe.konke.watch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    Handler h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Main", "Creat>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        startActivity(new Intent(MainActivity.this, AtyFrame.class));
        finish();
//        h = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                startActivity(new Intent(MainActivity.this,AtyFrame.class));
//                finish();
//                super.handleMessage(msg);
//            }
//        };
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1);
//                    h.sendEmptyMessage(0);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }
}
