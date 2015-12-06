package cc.chenhe.konke.watch.data;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import cc.chenhe.konke.common.data.DataGetAccessToken;
import cc.chenhe.konke.watch.R;

/**
 * Created by 宸赫 on 2015/11/22.
 */
public class RecNetwork extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            if (mobileInfo.isConnected() || wifiInfo.isConnected()) {
                checkKonkeAuth(context);
            }
        }
    }

    private void showNotify(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.icon_);
        builder.setTicker("小K授权失效");
        builder.setContentTitle("授权失效");
        builder.setContentText("请点击重新授权小K账号");
        builder.setAutoCancel(true);

        nm.notify(0, builder.build());

    }

    private void checkKonkeAuth(final Context context) {
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
        if (sp.getString(Settings.ITEM_KONKE_USER_ID, null) == null) {
            //没授权
            showNotify(context);
        }

        //检查授权有效性
        JSONObject json = new JSONObject();
        json.put("userid", sp.getString(Settings.ITEM_KONKE_USER_ID, null));
        Unit.postKonke(context, Unit.COMMON_URL + "User/verificateAccessToken", json.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                JSONObject json = JSON.parseObject(s);
                if (json.getIntValue("result") != 0) {
                    //授权无效
                    refreshToken(context);
                }
            }
        });
    }

    /**
     * 刷新授权
     */
    private void refreshToken(final Context context){
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
        if (sp.getString(Settings.ITEM_KONKE_REFRESH_TOKEN, null) == null) {
            //需要手动授权
            showNotify(context);
            return;
        }
        RequestParams params = new RequestParams();
        params.add("grant_type", "refresh_token");
        params.add("client_id", Unit.KONKE_ID);
        params.add("client_secret", Unit.KONKE_SECRET);
        params.add("redirect_uri", Unit.CALLBACK_URL);
        params.add("refresh_token", sp.getString(Settings.ITEM_KONKE_REFRESH_TOKEN, null));
        Unit.httpClient.post("http://kk.bigk2.com:8080/KOAuthDemeter/token", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, String s, Throwable throwable) {
                //post获取accessToken出错
                showNotify(context);
                return;
            }

            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, String s) {
                DataGetAccessToken tokenResult = JSON.parseObject(s, DataGetAccessToken.class);
                if (!tokenResult.result.equals("0")) {
                    //post获取accessToken出错
                    showNotify(context);
                    return;
                }
                Settings.writeAuth(context, null, null, tokenResult.access_token, tokenResult.refresh_token, tokenResult.expires_in, System.currentTimeMillis());
                context.sendBroadcast(new Intent(Unit.ACTION_AUTH_OK));
                return;
            }
        });
    }
}
