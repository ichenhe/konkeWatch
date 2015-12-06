package cc.chenhe.konke.watch;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cc.chenhe.konke.common.ConConstant;
import cc.chenhe.konke.watch.data.Unit;
import cn.openwatch.communication.BothWayCallback;
import cn.openwatch.communication.ErrorStatus;
import cn.openwatch.communication.OpenWatchBothWay;

/**
 * Created by 宸赫 on 2015/10/1.
 */
public class FmSwitch extends Fragment {

    private AtyDetail parentActivity;
    private View flLight;
    private CheckBox cbPower, cbLight;
    private ProgressBar pbPower, pbLight;

    float x, y;

    //    boolean needSwitch;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentActivity = (AtyDetail) getActivity();
        final View rootView = inflater.inflate(R.layout.fm_switch, null);
        initView(rootView);
        loadData();
        initEvent();
        return rootView;
    }

    private void loadData() {
        final BothWayCallback lightCallback = new BothWayCallback() {
            @Override
            public void onResponsed(byte[] bytes) {
                pbLight.setVisibility(View.GONE);
                if (new String(bytes).equals(ConConstant.RESULT_FAILURE)) {
                    if (isAdded())
                        Toast.makeText(parentActivity, getString(R.string.err), Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(new String(bytes));
                if (jsonObject.getInteger("result") == -1) {
                    if (isAdded())
                        Toast.makeText(parentActivity, "err:" + jsonObject.getString("des"), Toast.LENGTH_SHORT).show();
                    return;
                }
                cbLight.setTag(jsonObject.getString("data").equals("open"));
                cbLight.setChecked(jsonObject.getString("data").equals("open"));
            }

            @Override
            public void onError(ErrorStatus errorStatus) {
                pbLight.setVisibility(View.GONE);
                if (isAdded())
                    Toast.makeText(parentActivity, getString(R.string.err), Toast.LENGTH_SHORT).show();
            }
        };

        BothWayCallback kCallback = new BothWayCallback() {
            @Override
            public void onResponsed(byte[] bytes) {
                //请求夜灯状态
                if (parentActivity.getType() == Unit.DEVICE_TYPE_2) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userid", parentActivity.getUserId());
                    jsonObject.put("kid", parentActivity.getKID());
                    pbLight.setVisibility(View.VISIBLE);
                    OpenWatchBothWay.request(parentActivity, ConConstant.PATH_GET_LIGHT_STATE, jsonObject.toString(), lightCallback);
                }

                pbPower.setVisibility(View.GONE);
                if (new String(bytes).equals(ConConstant.RESULT_FAILURE)) {
                    if (isAdded())
                        Toast.makeText(parentActivity, getString(R.string.err), Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject jsonObject = JSON.parseObject(new String(bytes));
                if (jsonObject.getInteger("result") == -1) {
                    if (isAdded())
                        Toast.makeText(parentActivity, "err:" + jsonObject.getString("des"), Toast.LENGTH_SHORT).show();
                    return;
                }
                cbPower.setTag(jsonObject.getString("data").equals("open"));
                cbPower.setChecked(jsonObject.getString("data").equals("open"));
            }


            @Override
            public void onError(ErrorStatus errorStatus) {
                pbPower.setVisibility(View.GONE);
                if (isAdded())
                    Toast.makeText(parentActivity, getString(R.string.err), Toast.LENGTH_SHORT).show();
            }
        };

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", parentActivity.getUserId());
        jsonObject.put("kid", parentActivity.getKID());
        pbPower.setVisibility(View.VISIBLE);
        //请求插座状态
        OpenWatchBothWay.request(parentActivity, ConConstant.PATH_GET_K_STATE, jsonObject.toString(), kCallback);

    }

    private void initEvent() {
        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton view, final boolean isChecked) {
                if ((boolean) view.getTag() == isChecked) {
                    return;
                }
                //禁用check box防止请求堆积
                view.setEnabled(false);

                BothWayCallback callback = new BothWayCallback() {
                    @Override
                    public void onResponsed(byte[] bytes) {
                        //隐藏进度圈
                        if (view.getId() == R.id.cbPower) {
                            pbPower.setVisibility(View.GONE);
                        } else if (view.getId() == R.id.cbLight) {
                            pbLight.setVisibility(View.GONE);
                        }
                        //恢复checkBox
                        view.setEnabled(true);

                        if (new String(bytes).equals(ConConstant.RESULT_FAILURE)) {
                            view.setChecked(!isChecked);
                            if (isAdded())
                                Toast.makeText(parentActivity, getString(R.string.err), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        JSONObject jsonObject = JSON.parseObject(new String(bytes));
                        if (jsonObject.getInteger("result") == -1) {
                            if (isAdded())
                                Toast.makeText(parentActivity, "err:" + jsonObject.getString("des"), Toast.LENGTH_SHORT).show();
                            view.setChecked(!isChecked);
                        } else {
                            view.setTag(isChecked);
                        }
                    }

                    @Override
                    public void onError(ErrorStatus errorStatus) {
                        if (view.getId() == R.id.cbPower) {
                            pbPower.setVisibility(View.GONE);
                        } else if (view.getId() == R.id.cbLight) {
                            pbLight.setVisibility(View.GONE);
                        }
                        view.setEnabled(true);
                        view.setChecked(!isChecked);
                        if (isAdded())
                            Toast.makeText(parentActivity, getString(R.string.err), Toast.LENGTH_SHORT).show();
                    }
                };
                //构造请求数据
                JSONObject jsonObject = new JSONObject();
                if (view.isChecked()) {
                    jsonObject.put("key", "open");
                } else {
                    jsonObject.put("key", "close");
                }
                jsonObject.put("userid", parentActivity.getUserId());
                jsonObject.put("kid", parentActivity.getKID());
                //发送请求
                switch (view.getId()) {
                    case R.id.cbPower:
                        pbPower.setVisibility(View.VISIBLE);
                        OpenWatchBothWay.request(getActivity(), ConConstant.PATH_DO_SWITCH_K, jsonObject.toJSONString(), callback);
                        break;

                    case R.id.cbLight:
                        pbLight.setVisibility(View.VISIBLE);
                        OpenWatchBothWay.request(getActivity(), ConConstant.PATH_DO_SWITCH_LIGHT, jsonObject.toJSONString(), callback);
                        break;
                }
            }
        };
        cbPower.setOnCheckedChangeListener(checkedChangeListener);
        cbLight.setOnCheckedChangeListener(checkedChangeListener);
    }

    private void initView(final View view) {
        ScrollView sv = (ScrollView) view.findViewById(R.id.sv);
        flLight = view.findViewById(R.id.flLight);
        cbPower = (CheckBox) view.findViewById(R.id.cbPower);
        cbLight = (CheckBox) view.findViewById(R.id.cbLight);
        pbPower = (ProgressBar) view.findViewById(R.id.pbPower);
        pbLight = (ProgressBar) view.findViewById(R.id.pbLight);

        //隐藏小夜灯开关
        if (parentActivity.getType() != Unit.DEVICE_TYPE_2) {
            flLight.setVisibility(View.GONE);
        }

        sv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = event.getRawX();
                        y = event.getRawY();
                        view.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        double k = (event.getRawY() - y) / (event.getRawX() - x);
                        if (k > 1 || k < -1) {
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                }
                return false;
            }
        });
    }
}
