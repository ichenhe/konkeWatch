package cc.chenhe.konke.watch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import cc.chenhe.konke.common.ConConstant;
import cc.chenhe.konke.common.data.DataGetAccessToken;
import cc.chenhe.konke.watch.data.Settings;
import cc.chenhe.konke.watch.data.Unit;
import cn.openwatch.communication.OpenWatchBothWay;
import cn.openwatch.communication.service.OpenWatchListenerService;

/**
 * Created by 宸赫 on 2015/9/26.
 */
public class SerWatchListener extends OpenWatchListenerService {

    private Context context;

    @Override
    public void onMessageReceived(String path, byte[] data) {
        context = this;
        super.onMessageReceived(path, data);
        String r = new String(data);
//        Toast.makeText(context,"onMessage",Toast.LENGTH_SHORT).show();
        if (path.equals(ConConstant.PATH_GET_DEVICE_LIST)) {
            do_getDeveiceList(path);
        } else if (path.equals(ConConstant.PATH_DO_SWITCH_K)) {
            do_doSwitchK(path, r);
        }else if (path.equals(ConConstant.PATH_DO_SWITCH_LIGHT)) {
            do_doSwitchLight(path, r);
        }else if (path.equals(ConConstant.PATH_GET_K_STATE)) {
            do_getKState(path, r);
        }else if (path.equals(ConConstant.PATH_GET_LIGHT_STATE)) {
            do_getLightState(path, r);
        }else if (path.equals(ConConstant.PATH_REFRESH_TOKEN)) {
            do_refreshToken(path);
        }
    }

    /**
     * 刷新授权
     * @param path
     */
    private void do_refreshToken(final String path){
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
        if (sp.getString(Settings.ITEM_KONKE_REFRESH_TOKEN, null) == null) {
            //需要手动授权
            OpenWatchBothWay.response(context,path,"-1");
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
                OpenWatchBothWay.response(context,path,"-1");
                return;
            }

            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, String s) {
                DataGetAccessToken tokenResult = JSON.parseObject(s, DataGetAccessToken.class);
                if (!tokenResult.result.equals("0")) {
                    //post获取accessToken出错
                    OpenWatchBothWay.response(context, path, "-1");
                    return;
                }
                Settings.writeAuth(context, null, null, tokenResult.access_token, tokenResult.refresh_token, tokenResult.expires_in, System.currentTimeMillis());
                context.sendBroadcast(new Intent(Unit.ACTION_AUTH_OK));
                OpenWatchBothWay.response(context, path, "0");
                return;
            }
        });
    }

    /**
     * 获取小夜灯状态
     * @param path
     * @param data
     */
    private void do_getLightState(final String path, String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Unit.postKonke(context, Unit.COMMON_URL + "User/getKLightInfo", jsonObject.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                OpenWatchBothWay.response(context, path, ConConstant.RESULT_FAILURE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                OpenWatchBothWay.response(context, path, s);
            }
        });
    }

    /**
     * 获取小K状态
     * @param path
     * @param data
     */
    private void do_getKState(final String path, String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Unit.postKonke(context, Unit.COMMON_URL + "KInfo/getKState", jsonObject.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                OpenWatchBothWay.response(context, path, ConConstant.RESULT_FAILURE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                OpenWatchBothWay.response(context, path, s);
            }
        });
    }

    /**
     * 控制夜灯开关
     * @param path
     * @param data
     */
    private void do_doSwitchLight(final String path, String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Unit.postKonke(context, Unit.COMMON_URL + "User/switchKLight", jsonObject.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                OpenWatchBothWay.response(context, path, ConConstant.RESULT_FAILURE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                OpenWatchBothWay.response(context, path, s);
            }
        });
    }

    /**
     * 控制小K开关
     * @param path
     * @param data
     */
    private void do_doSwitchK(final String path, String data) {
        JSONObject jsonObject = JSON.parseObject(data);
        Unit.postKonke(context, Unit.COMMON_URL + "KControl/doSwitchK", jsonObject.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                OpenWatchBothWay.response(context, path, ConConstant.RESULT_FAILURE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                OpenWatchBothWay.response(context, path, s);
            }
        });
    }

    /**
     * 获取设备列表
     * @param path
     */
    private void do_getDeveiceList(final String path) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", Unit.getUserid(context));
        Unit.postKonke(context, Unit.COMMON_URL + "User/getKList", jsonObject.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                OpenWatchBothWay.response(context, path, ConConstant.RESULT_FAILURE);
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                JSONObject object = JSON.parseObject(s);
                if (object.getInteger("result")==-2){
                    //授权失效

//                    return;
                }
                OpenWatchBothWay.response(context, path, s);
            }
        });
    }
}