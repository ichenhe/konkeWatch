package cc.chenhe.konke.watch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import cc.chenhe.konke.watch.data.Settings;
import cc.chenhe.konke.watch.data.Unit;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by 宸赫 on 2015/10/23.
 */
public class FmFunc extends Fragment {

    View llWorn, llAbout, llClean, llUpdate;
    TextView tvUserName;
    Context context;

    MyReceiver myReceiver;

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.fm_func, null);
        initView(rootView);
        initReceiver();
        initEvent();
        return rootView;
    }

    private void initReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Unit.ACTION_AUTH_OK);
        getActivity().registerReceiver(myReceiver, filter);
    }

    private void initView(View rootView) {
        llClean = rootView.findViewById(R.id.llClean);
        llWorn = rootView.findViewById(R.id.llWorn);
        llAbout = rootView.findViewById(R.id.llAbout);
        llUpdate = rootView.findViewById(R.id.llUpdate);
        tvUserName = (TextView) rootView.findViewById(R.id.tvUserName);

        SharedPreferences sp = getActivity().getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
        tvUserName.setText("已授权：" + sp.getString(Settings.ITEM_KONKE_USER_NAME, ""));
    }

    private void initEvent() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                switch (v.getId()) {
                    case R.id.llWorn:
                        i = new Intent(context, AtyWebView.class);
                        i.putExtra(AtyWebView.EXT_URL, "https://wifi.daikeapp.com");
                        i.putExtra(AtyWebView.EXT_TITLE, "常见问题");
                        startActivity(i);
                        break;

                    case R.id.llAbout:
                        i = new Intent();
                        i.setAction("android.intent.action.VIEW");
                        i.setData(Uri.parse("http://chuye.cloud7.com.cn/12522285"));
                        startActivity(i);
                        break;

                    case R.id.llClean:
                        do_clean();
                        break;

                    case R.id.llUpdate:
                        do_update();
                        break;

                }
            }
        };
        llWorn.setOnClickListener(clickListener);
        llAbout.setOnClickListener(clickListener);
        llClean.setOnClickListener(clickListener);
        llUpdate.setOnClickListener(clickListener);
    }

    private void do_update(){
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.dialog));
        pDialog.setTitleText("正在检查更新");
        pDialog.setCancelable(false);
        pDialog.show();

        RequestParams params = new RequestParams();
        params.put("v", Unit.getVersion(context));
        Unit.httpClient.post(context, Unit.SERVICE_URL + "update.php", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                pDialog.dismiss();
                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("网络异常")
                        .setContentText("检查更新失败")
                        .setConfirmText("确认")
                        .show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, final String s) {
                pDialog.dismiss();
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
                }else {
                    new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("已是最新版本")
                            .setContentText("当前版本:"+Unit.getVersion(context))
                            .setConfirmText("确认")
                            .show();
                }
            }
        });
    }

    private void do_clean(){
        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("清除授权？")
                .setContentText("清除小K授权后你需要重新登陆以控制设备")
                .setCancelText("再想想")
                .setConfirmText("清除")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        Settings.clearAuth(context);
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("已清除")
                                .setContentText("请重启APP重新登陆")
                                .setConfirmText("退出")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        getActivity().finish();
                                    }
                                })
                                .show();
                    }
                })
                .show();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Unit.ACTION_AUTH_OK:
                    SharedPreferences sp = getActivity().getSharedPreferences(Settings.SP_NAME, Context.MODE_PRIVATE);
                    tvUserName.setText("已授权："+sp.getString(Settings.ITEM_KONKE_USER_NAME, ""));
                    break;
            }
        }
    }
}
