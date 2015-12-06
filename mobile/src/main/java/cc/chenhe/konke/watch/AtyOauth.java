package cc.chenhe.konke.watch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import cc.chenhe.konke.common.data.DataGetAccessToken;
import cc.chenhe.konke.common.data.DataGetUserInfo;
import cc.chenhe.konke.watch.data.Unit;
import cc.chenhe.konke.watch.ui.MyBar;

/**
 * Created by 宸赫 on 2015/8/26.
 */
public class AtyOauth extends Activity {
    public static final String RESULT_EXT_RESULT = "EXT_RESULT";
    public static final String RESULT_EXT_ACCESS_TOKEN = "EXT_ACCESS_TOKEN";
    public static final String RESULT_EXT_REFRESH_TOKEN = "EXT_REFRESH_TOKEN";
    public static final String RESULT_EXT_USER_NAME = "EXT_USER_NAME";
    public static final String RESULT_EXT_USER_ID = "EXT_USER_ID";
    public static final String RESULT_EXT_EXPIRES_IN = "EXT_EXPIRES_IN";

    private MyBar bar;
    private WebView webView;
    private Context context;

    private final String TAG = "AtyOauth";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initView();
        initEvent();
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl("http://kk.bigk2.com:8080/KOAuthDemeter/authorize?client_id=" + Unit.KONKE_ID +
                "&response_type=code&redirect_uri=" + Unit.CALLBACK_URL);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            // TODO Auto-generated method stub
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
//            Log.i(TAG,"nowUrl:"+url);
            if (url.indexOf(Unit.CALLBACK_URL) == 0) {
                webView.stopLoading();
                bar.setShowLoading(true);
                String authCode;
                authCode = url.substring(url.indexOf("=") + 1);
                Log.i(TAG, "url:" + url);
                Log.i(TAG, "auth_code:" + authCode);
                getToken(authCode);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            Log.i(TAG, "网页加载完成");
            if (url.indexOf(Unit.CALLBACK_URL) != 0) {
                bar.setShowLoading(false);
            }
        }
    }

    Intent resultIntent = new Intent();

    /**
     * 第二步：获取accessoken
     *
     * @param authCode
     */
    private void getToken(String authCode) {
        Log.i(TAG, "正在获取token");

        RequestParams params = new RequestParams();
        params.add("grant_type", "authorization_code");
        params.add("client_id", Unit.KONKE_ID);
        params.add("client_secret", Unit.KONKE_SECRET);
        params.add("redirect_uri", Unit.CALLBACK_URL);
        params.add("code", authCode);
        Unit.httpClient.post("http://kk.bigk2.com:8080/KOAuthDemeter/accessToken", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "post获取accessToken出错");
                resultIntent.putExtra(RESULT_EXT_RESULT, "998");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                return;
            }

            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, String s) {
                DataGetAccessToken tokenResult = JSON.parseObject(s, DataGetAccessToken.class);
                if (!tokenResult.result.equals("0")) {
                    Log.i(TAG, "post获取accessToken出错:" + tokenResult.result + "des:" + tokenResult.des);
                    resultIntent.putExtra(RESULT_EXT_RESULT, tokenResult.result);
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                    return;
                }
                Log.i(TAG, "原始token数据:" + s);
                Log.i(TAG, "token:" + tokenResult.access_token + "  refresh:" + tokenResult.refresh_token);
                getUserInfo(tokenResult);
            }
        });
    }



    /**
     * 第三步：获取用户信息
     *
     * @param tokenResult
     */
    private void getUserInfo(final DataGetAccessToken tokenResult) {
        Header[] headers = new Header[1];
        headers[0] = new BasicHeader("Authorization", "Bearer " + tokenResult.access_token);
        RequestParams params = new RequestParams();
        Unit.setAccessToken(context, tokenResult.access_token);
        Unit.postKonke(this, "http://kk.bigk2.com:8080/KOAuthDemeter/UserInfo", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "post获取userInfo出错");
                resultIntent.putExtra(RESULT_EXT_RESULT, "997");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                return;
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                DataGetUserInfo userResult = JSON.parseObject(s, DataGetUserInfo.class);
                if (!userResult.result.equals("0")) {
                    Log.i(TAG, "post获取userInfo出错:" + userResult.result + "des:" + userResult.des);
                    resultIntent.putExtra(RESULT_EXT_RESULT, userResult.des);
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                    return;
                }
                getUserId(userResult.username, tokenResult);
            }
        });
    }

    private void getUserId(final String username, final DataGetAccessToken tokenResult){
        JSONObject object = new JSONObject();
        object.put("username", username);
        Unit.postKonke(context, Unit.COMMON_URL + "User/queryUserId", object.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                Log.i(TAG, "获取userid出错");
                resultIntent.putExtra(RESULT_EXT_RESULT, "998");
                setResult(RESULT_CANCELED, resultIntent);
                finish();
                return;
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                JSONObject object = JSON.parseObject(s);
                if (object.getInteger("result") != 0) {
                    Log.i(TAG, "获取userid出错"+object.getString("des"));
                    resultIntent.putExtra(RESULT_EXT_RESULT, "998");
                    setResult(RESULT_CANCELED, resultIntent);
                    finish();
                    return;
                }
                resultIntent.putExtra(RESULT_EXT_USER_NAME, object.getString("username"));
                resultIntent.putExtra(RESULT_EXT_USER_ID, object.getString("userid"));
                resultIntent.putExtra(RESULT_EXT_ACCESS_TOKEN, tokenResult.access_token);
                resultIntent.putExtra(RESULT_EXT_REFRESH_TOKEN, tokenResult.refresh_token);
                resultIntent.putExtra(RESULT_EXT_EXPIRES_IN, tokenResult.expires_in);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void initEvent() {
        WebViewClient client = new MyWebViewClient();
        webView.setWebViewClient(client);
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tvLeft:
                        setResult(RESULT_CANCELED);
                        finish();
                        break;
                }

            }
        };
        bar.setOnClickListener(clickListener);
    }

    public void initView() {
        setContentView(R.layout.aty_oauth);
        bar = (MyBar) findViewById(R.id.bar);
        webView = (WebView) findViewById(R.id.webView);
    }
}
