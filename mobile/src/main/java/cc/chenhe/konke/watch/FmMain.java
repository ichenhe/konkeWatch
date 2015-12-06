package cc.chenhe.konke.watch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.devspark.appmsg.AppMsg;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

import cc.chenhe.konke.common.data.DataGetAccessToken;
import cc.chenhe.konke.common.data.DataGetKList;
import cc.chenhe.konke.watch.data.DataMainLV;
import cc.chenhe.konke.watch.data.Settings;
import cc.chenhe.konke.watch.data.Unit;
import cc.chenhe.konke.watch.ui.CommonAdapter;
import cc.chenhe.konke.watch.ui.MyBar;
import cc.chenhe.konke.watch.ui.ViewHolder;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 宸赫 on 2015/8/26.
 */
public class FmMain extends Fragment {
    private final String TAG = "FmMain";

    private SwipeRefreshLayout refreshLayout;
    private ListView lv;
    private MyBar bar;

    private Activity context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fm_main, null);
        initView(rootView);
        initEvent();

        updateData();
        checkUpdate();//检查更新同时刷新最后启动的版本
        checkKonkeAuth();//根据结果自动执行相应操作

        return rootView;
    }

    private void initEvent() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadDevices(true);
            }
        });
    }

    /**
     * 从旧版升级的用户更新数据
     */
    private void updateData(){
        SharedPreferences sp =context.getSharedPreferences(Settings.SP_NAME,Context.MODE_PRIVATE);
        switch (sp.getInt(Settings.ITEM_VERSION,-1)){
            case 1:
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("重新授权")
                        .setContentText("小K授权API已更新，请到扩展选项卡中清除授权再使用")
                        .setConfirmText("知道了")
                        .show();
                break;
        }
    }

    private void initView(View rootView) {
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
//        refreshLayout.setColorScheme(getResources().getColor(android.R.color.holo_blue_bright),
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
        lv = (ListView) rootView.findViewById(R.id.lv);
        lv.setDivider(null);
        bar = (MyBar) rootView.findViewById(R.id.bar);
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        SharedPreferences sp = context.getSharedPreferences(Settings.SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putInt(Settings.ITEM_VERSION,Unit.getVersion(context));
        e.commit();

        RequestParams params = new RequestParams();
        params.put("v", Unit.getVersion(context));
        Unit.httpClient.post(context, Unit.SERVICE_URL + "update.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, final String s) {
                if (!s.equals("ok")) {
                    new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("新版驾到")
                            .setContentText("是否立即更新？")
                            .setCancelText("下次再说")
                            .setConfirmText("火速更新")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    Intent i = new Intent();
                                    i.setAction("android.intent.action.VIEW");
                                    i.setData(Uri.parse(s));
                                    startActivity(i);
                                    getActivity().finish();
                                }
                            })
                            .show();
                }
            }
        });
    }

    /**
     * 加载设备列表
     *
     * @param showTip
     */
    private void loadDevices(final boolean showTip) {
        bar.setShowLoading(true);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", Unit.getUserid(context));
        Unit.postKonke(getActivity(), "http://kk.bigk2.com:8080/KOAuthDemeter/User/getKList", jsonObject.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                bar.setShowLoading(false);
                refreshLayout.setRefreshing(false);
                AppMsg.cancelAll(getActivity());
                AppMsg.makeText(getActivity(), "获取设备列表失败,请检查网络", AppMsg.STYLE_CONFIRM).show();
                Log.i(TAG, s);
                for (int a = 0; a < headers.length; a++) {
                    Log.i(TAG, headers[a].getName() + ": " + headers[a].getValue());
                }
                return;
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                bar.setShowLoading(false);
                refreshLayout.setRefreshing(false);
                AppMsg.cancelAll(getActivity());
                DataGetKList list = JSON.parseObject(s, DataGetKList.class);
                if (list.result != 0) {
                    AppMsg.makeText(getActivity(), "获取设备列表失败:" + list.result, AppMsg.STYLE_CONFIRM).show();
                    return;
                }

                List<DataMainLV> adapterData = new ArrayList<DataMainLV>();
                for (int a = 0; a < list.datalist.size(); a++) {
                    adapterData.add(new DataMainLV(list.datalist.get(a).device_name, Integer.valueOf(list.datalist.get(a).device_type), list.datalist.get(a).kid, list.datalist.get(a).user_id));
                }
                lv.setAdapter(new MyAdapter(getActivity(), adapterData));
                if (showTip) {
                    AppMsg.cancelAll(getActivity());
                    AppMsg.makeText(getActivity(), "刷新设备列表成功", AppMsg.STYLE_INFO).show();
                }
            }
        });
    }

    /**
     * 检测控客授权有效性
     */
    private void checkKonkeAuth() {
        SharedPreferences sp = getActivity().getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
        if (sp.getString(Settings.ITEM_KONKE_USER_ID, null) == null) {
            //没授权
            Log.i(TAG, "未授权");
            refreshToken();
            return;
        }

        bar.setShowLoading(true);
        //检查授权有效性
        JSONObject json = new JSONObject();
        json.put("userid", sp.getString(Settings.ITEM_KONKE_USER_ID, null));
        Unit.postKonke(context, Unit.COMMON_URL + "User/verificateAccessToken", json.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                bar.setShowLoading(false);
                AppMsg.makeText(context, "检查授权失败", AppMsg.STYLE_CONFIRM).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                bar.setShowLoading(false);
                JSONObject json = JSON.parseObject(s);
                if (json.getIntValue("result") != 0) {
                    //授权无效
                    refreshToken();
                } else {
                    loadDevices(false);
                }
            }
        });
    }

    /**
     * 刷新获取token,无效则重新授权
     */
    private void refreshToken() {
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.dialog));
        pDialog.setTitleText("正在刷新小K授权");
        pDialog.setCancelable(false);
        pDialog.show();

        Log.i(TAG, "正在获取token");
        SharedPreferences sp = getActivity().getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
        if (sp.getString(Settings.ITEM_KONKE_REFRESH_TOKEN, null) == null) {
            pDialog.dismiss();
            startActivityForResult(new Intent(getActivity(), AtyOauth.class), 0);
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
                Log.i(TAG, "post获取accessToken出错");
                pDialog.dismiss();
                AppMsg.makeText(getActivity(), getString(R.string.internet_err), AppMsg.STYLE_CONFIRM).show();
                return;
            }

            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, String s) {
                DataGetAccessToken tokenResult = JSON.parseObject(s, DataGetAccessToken.class);
                if (!tokenResult.result.equals("0")) {
                    Log.i(TAG, "post获取accessToken出错:" + tokenResult.result + "des:" + tokenResult.des);
                    pDialog.dismiss();
                    AppMsg.makeText(getActivity(), "刷新授权失败:" + tokenResult.result, AppMsg.STYLE_CONFIRM).show();
                    return;
                }
                Settings.writeAuth(getActivity(), null, null, tokenResult.access_token, tokenResult.refresh_token, tokenResult.expires_in, System.currentTimeMillis());
                pDialog.dismiss();
                getActivity().sendBroadcast(new Intent(Unit.ACTION_AUTH_OK));
                AppMsg.makeText(getActivity(), "刷新授权成功", AppMsg.STYLE_INFO).show();
                return;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0://小K授权
                if (resultCode == Activity.RESULT_CANCELED) {
                    //获取授权失败
                    if (data == null) {
                        AppMsg.makeText(getActivity(), "获取授权失败,请检查网络", AppMsg.STYLE_CONFIRM).show();
                    } else {
                        AppMsg.makeText(getActivity(), "获取授权失败:" + data.getStringExtra(AtyOauth.RESULT_EXT_RESULT), AppMsg.STYLE_CONFIRM).show();
                    }
                } else {
                    //获取授权成功
                    Settings.writeAuth(getActivity(), data.getStringExtra(AtyOauth.RESULT_EXT_USER_NAME),
                            data.getStringExtra(AtyOauth.RESULT_EXT_USER_ID),
                            data.getStringExtra(AtyOauth.RESULT_EXT_ACCESS_TOKEN),
                            data.getStringExtra(AtyOauth.RESULT_EXT_REFRESH_TOKEN),
                            data.getIntExtra(AtyOauth.RESULT_EXT_EXPIRES_IN, 0), System.currentTimeMillis());
                    getActivity().sendBroadcast(new Intent(Unit.ACTION_AUTH_OK));
                    loadDevices(false);
                    AppMsg.makeText(getActivity(), "授权成功:" + data.getStringExtra(AtyOauth.RESULT_EXT_USER_NAME), AppMsg.STYLE_INFO).show();
                }
                break;
        }
    }

    private class MyAdapter extends CommonAdapter<DataMainLV> {

        public MyAdapter(Context context, List<DataMainLV> mDatas) {
            super(context, mDatas, R.layout.item_listview_main);
        }

        @Override
        public void convert(ViewHolder helper, final DataMainLV item, int position) {
            LinearLayout llParent = helper.getView(R.id.llParent);
//            if (position == 1) {
//                llParent.setBackgroundResource(R.drawable.leba_bg_top_selector);
//            } else if (position == getCount() - 1) {
//                llParent.setBackgroundResource(R.drawable.leba_bg_bottom_selector);
//            } else {
//                llParent.setBackgroundResource(R.drawable.leba_bg_mid_selector);
//            }
            switch (item.deviceType) {
                case 1:
                    helper.setImageResource(R.id.ivIco, R.drawable.ico_device_1);
                    helper.setText(R.id.tvName, item.deviceName + "[一代]");
                    break;
                case 2:
                    helper.setImageResource(R.id.ivIco, R.drawable.ico_device_2);
                    helper.setText(R.id.tvName, item.deviceName + "[二代]");
                    break;
                case 3:
                    helper.setImageResource(R.id.ivIco, R.drawable.ico_device_3);
                    helper.setText(R.id.tvName, item.deviceName + "[mini]");
                    break;
                case 4:
                    helper.setImageResource(R.id.ivIco, R.drawable.ico_device_3);
                    helper.setText(R.id.tvName, item.deviceName + "[mini PRO]");
                    break;
            }

            llParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AtyDetailMini.class);
                    i.putExtra(AtyDetailMini.INTENT_KID, item.kid);
                    i.putExtra(AtyDetailMini.INTENT_USER_ID, item.userid);
                    i.putExtra(AtyDetailMini.INTENT_DEVICE_TYPE, item.deviceType);
                    startActivity(i);
                }
            });
        }
    }

}
