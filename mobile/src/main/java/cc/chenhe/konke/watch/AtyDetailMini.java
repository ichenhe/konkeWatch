package cc.chenhe.konke.watch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.devspark.appmsg.AppMsg;
import com.kyleduo.switchbutton.SwitchButton;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

import cc.chenhe.konke.common.data.DataGetKState;
import cc.chenhe.konke.watch.data.Unit;
import cc.chenhe.konke.watch.ui.MyBar;

/**
 * Created by 宸赫 on 2015/8/27.
 */
public class AtyDetailMini extends Activity {
    public static final String INTENT_KID = "kid";
    public static final String INTENT_USER_ID = "user_id";
    public static final String INTENT_DEVICE_TYPE = "device_type";

    MyBar bar;
    CheckBox cbState;
    Activity context;
    SwitchButton sbPowerProtect, sbLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initView();
        initBaseEvent();
        loadState(false);
        initEvent();
    }

    private void loadState(final boolean showTip) {
        bar.setShowLoading(true);

        JSONObject json = new JSONObject();
        json.put("userid", getIntent().getStringExtra(INTENT_USER_ID));
        json.put("kid", getIntent().getStringExtra(INTENT_KID));
        //获取开关状态
        Unit.postKonke(context, Unit.COMMON_URL + "KInfo/getKState", json.toString(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                AppMsg.cancelAll(context);
                AppMsg.makeText(context, "获取开关状态失败", AppMsg.STYLE_CONFIRM).show();
            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                DataGetKState data = JSON.parseObject(s, DataGetKState.class);
                if (data.result != 0) {
                    AppMsg.cancelAll(context);
                    AppMsg.makeText(context, "获取开关状态失败:" + data.result, AppMsg.STYLE_CONFIRM).show();
                    return;
                }
                if (data.data.equals("close")) {
                    cbState.setChecked(false);
                } else {
                    cbState.setChecked(true);
                }
                bar.setShowLoading(false);
                if (showTip) {
                    AppMsg.cancelAll(context);
                    AppMsg.makeText(context, "刷新成功", AppMsg.STYLE_INFO).show();
                }
            }
        });

        //获取小夜灯状态
        if (getIntent().getIntExtra(INTENT_DEVICE_TYPE, 0) == Unit.DEVICE_TYPE_2) {
            Unit.postKonke(context, Unit.COMMON_URL + "User/getKLightInfo", json.toString(), new TextHttpResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                    AppMsg.cancelAll(context);
                    AppMsg.makeText(context, "获取小夜灯状态失败", AppMsg.STYLE_CONFIRM).show();
                }

                @Override
                public void onSuccess(int i, Header[] headers, String s) {
                    DataGetKState data = JSON.parseObject(s, DataGetKState.class);
                    if (data.result != 0) {
                        AppMsg.cancelAll(context);
                        AppMsg.makeText(context, "获取小夜灯状态失败:" + data.result, AppMsg.STYLE_CONFIRM).show();
                        return;
                    }
                    if (data.data.equals("close")) {
                        sbLight.setTag(false);
                        sbLight.setChecked(false);
                    } else {
                        sbLight.setTag(true);
                        sbLight.setChecked(true);
                    }
                    bar.setShowLoading(false);
                    if (showTip) {
                        AppMsg.cancelAll(context);
                        AppMsg.makeText(context, "刷新成功", AppMsg.STYLE_INFO).show();
                    }
                }
            });
        }
    }

    String actionDes = "";

    private void initEvent() {
        cbState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isChecked = cbState.isChecked();
                cbState.setEnabled(false);
                bar.setShowLoading(true);
                String state;
                if (isChecked) {
                    state = "open";
                } else {
                    state = "close";
                }
                JSONObject json = new JSONObject();
                json.put("userid", getIntent().getStringExtra(INTENT_USER_ID));
                json.put("kid", getIntent().getStringExtra(INTENT_KID));
                json.put("key", state);
                Unit.postKonke(context, Unit.COMMON_URL + "KControl/doSwitchK", json.toString(), new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        cbState.setEnabled(true);
                        bar.setShowLoading(false);
                        AppMsg.cancelAll(context);
                        AppMsg.makeText(context, "执行失败", AppMsg.STYLE_CONFIRM).show();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        cbState.setEnabled(true);
                        bar.setShowLoading(false);
                        if (s.indexOf("0") < 0) {
                            AppMsg.cancelAll(context);
                            AppMsg.makeText(context, "执行失败:" + s, AppMsg.STYLE_CONFIRM).show();
                        } else {

                        }
                    }
                });
            }
        });


        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked==(boolean)buttonView.getTag()){
                    return;
                }
                buttonView.setEnabled(false);
                bar.setShowLoading(true);

                TextHttpResponseHandler responseHandler = new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
                        buttonView.setEnabled(true);
                        bar.setShowLoading(false);
                        buttonView.setChecked(!isChecked);
                        AppMsg.makeText(context, actionDes + "失败", AppMsg.STYLE_CONFIRM).show();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, String s) {
                        buttonView.setEnabled(true);
                        bar.setShowLoading(false);
                        if (s.indexOf("0") < 0) {
                            buttonView.setChecked(!isChecked);
                            AppMsg.makeText(context, actionDes + "失败", AppMsg.STYLE_CONFIRM).show();
                        } else {
                            buttonView.setTag(buttonView.isChecked());
                            AppMsg.makeText(context, actionDes + "成功", AppMsg.STYLE_INFO).show();
                        }
                    }
                };

                JSONObject json = new JSONObject();
                json.put("userid", getIntent().getStringExtra(INTENT_USER_ID));
                json.put("kid", getIntent().getStringExtra(INTENT_KID));
                switch (buttonView.getId()) {
                    case R.id.sbPowerProtect:
                        if (isChecked) {

                        } else {
                            actionDes = "充电保护关闭";
                            Unit.postKonke(context, Unit.COMMON_URL + "User/closeChargingProtection", json.toString(), responseHandler);
                        }
                        break;

                    case R.id.sbLight:
                        if (isChecked) {
                            json.put("key", "open");
                            actionDes = "小夜灯开启";
                        } else {
                            json.put("key", "close");
                            actionDes = "小夜灯关闭";
                        }
                        Unit.postKonke(context, Unit.COMMON_URL + "User/switchKLight", json.toString(), responseHandler);
                        break;
                }

            }

        };
        sbPowerProtect.setOnCheckedChangeListener(checkedChangeListener);
        sbLight.setOnCheckedChangeListener(checkedChangeListener);

    }

    private void initBaseEvent() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tvLeft:
                        finish();
                        break;
                    case R.id.tvRight:
                        loadState(true);
                        break;
                }
            }
        };
        bar.setOnClickListener(clickListener);
    }

    private void initView() {
        setContentView(R.layout.aty_detail_mini);
        cbState = (CheckBox) findViewById(R.id.cbState);
        bar = (MyBar) findViewById(R.id.bar);
        sbPowerProtect = (SwitchButton) findViewById(R.id.sbPowerProtect);
        sbLight = (SwitchButton) findViewById(R.id.sbLight);

        sbPowerProtect.setEnabled(false);
        if (getIntent().getIntExtra(INTENT_DEVICE_TYPE, 0) != Unit.DEVICE_TYPE_2) {
            sbLight.setEnabled(false);
        }
    }
}
