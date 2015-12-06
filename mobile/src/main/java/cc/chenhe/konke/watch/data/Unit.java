package cc.chenhe.konke.watch.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.TypedValue;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by 宸赫 on 2015/8/26.
 */
public class Unit {

    public static final String COMMON_URL = "http://kk.bigk2.com:8080/KOAuthDemeter/"; //控客api通用rul
    public static final String SERVICE_URL = "http://api.chenhe.cc/konke_wear/"; //暂时仅用于检查更新
    public static final String CALLBACK_URL = "http://api.chenhe.cc/konke/aouth.php"; //鉴权回调可任意填
    public static final String KONKE_ID = ""; //控客开放平台appid
    public static final String KONKE_SECRET = ""; ////控客开放平台appsecret

    public static final int DEVICE_TYPE_1 = 1;
    public static final int DEVICE_TYPE_2 = 2;
    public static final int DEVICE_TYPE_MINI = 3;
    public static final int DEVICE_TYPE_MINI_PRO = 4;

    public static AsyncHttpClient httpClient = new AsyncHttpClient();

    public static final String ACTION_AUTH_OK = "ACTION_AUTH_OK";


    public static void setAccessToken(Context context, String token){
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString(Settings.ITEM_KONKE_ACCESS_TOKEN,token);
        e.commit();
    }

    public static String getAccessToken(Context context){
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME,Context.MODE_PRIVATE);
        return sp.getString(Settings.ITEM_KONKE_ACCESS_TOKEN,null);
    }

    public static String getUserid(Context context){
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME,Context.MODE_PRIVATE);
        return sp.getString(Settings.ITEM_KONKE_USER_ID,null);
    }

    public static void postKonke(Context context, String url, String json, ResponseHandlerInterface callback) {
        Header[] headers = new Header[3];
//        Log.i("", "accesstoken:" + accessToken);
        headers[0] = new BasicHeader("Authorization", "Bearer " + getAccessToken(context));
        headers[1] = new BasicHeader("Content-Type", "application/json");
        headers[2] = new BasicHeader("User-Agent", "imgfornote");
        try {
            StringEntity s = new StringEntity(json);
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            Unit.httpClient.post(context, url, headers, s, "application/json", callback);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public static void postKonke(Context context, String url, RequestParams params, ResponseHandlerInterface callback) {
        Header[] headers = new Header[1];
//        Log.i("","accesstoken:"+accessToken);
        headers[0] = new BasicHeader("Authorization", "Bearer " + getAccessToken(context));
//        httpClient.post(context,url,headers,params,"application/json",callback);
        httpClient.post(context, url, headers, params, null, callback);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static int getAndroidSDKVersion() {
        int version = 0;
        version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
        return version;
    }

    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    //wheel view
    public static float pixelToDp(Context context, float val) {
        float density = context.getResources().getDisplayMetrics().density;
        return val * density;
    }

    public static int dipToPx(Context context, int dipValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dipValue, context.getResources().getDisplayMetrics());
    }
}
