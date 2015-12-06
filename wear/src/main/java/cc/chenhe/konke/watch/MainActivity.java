package cc.chenhe.konke.watch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cc.chenhe.konke.common.ConConstant;
import cc.chenhe.konke.common.data.DataGetKList;
import cc.chenhe.konke.common.data.DataItemGetKList;
import cc.chenhe.konke.watch.data.DataLvMain;
import cc.chenhe.konke.watch.data.Unit;
import cn.openwatch.communication.BothWayCallback;
import cn.openwatch.communication.ErrorStatus;
import cn.openwatch.communication.OpenWatchBothWay;

public class MainActivity extends Activity {

    private WearableListView lv;
    private View llLoading;
    private TextView tv;
    private Context context;

    //刷新授权回调
    final BothWayCallback refreshTokenCallback = new BothWayCallback(){

        @Override
        public void onResponsed(byte[] bytes) {
            int result = Integer.valueOf(new String(bytes));
            if (result==0){
                OpenWatchBothWay.request(context, ConConstant.PATH_GET_DEVICE_LIST, "",callback);
                tv.setText("正在获取设备列表");
            }else {
                Toast.makeText(context, "授权异常\n请打开手机app重新授权" , Toast.LENGTH_LONG).show();
                llLoading.setVisibility(View.GONE);
            }
        }

        @Override
        public void onError(ErrorStatus errorStatus) {
            Toast.makeText(context, "授权异常\n请打开手机app重新授权" , Toast.LENGTH_LONG).show();
            llLoading.setVisibility(View.GONE);
        }
    };

    //获取设备列表回调
    BothWayCallback callback =  new BothWayCallback() {
        @Override
        public void onResponsed(final byte[] bytes) {
            String r = new String(bytes);
            final DataGetKList list = JSON.parseObject(r, DataGetKList.class);
            if (list.result != 0) {
                if (list.result == -2) {
                    tv.setText("正在刷新授权");
                    OpenWatchBothWay.request(context, ConConstant.PATH_REFRESH_TOKEN, "", refreshTokenCallback);
                } else {
                    Toast.makeText(context, "获取设备列表失败:" + list.result + list.des, Toast.LENGTH_LONG).show();
                    llLoading.setVisibility(View.GONE);
                }
                return;
            }

            List<DataLvMain> datas = new ArrayList<DataLvMain>();
            for (int a = 0; a < list.datalist.size(); a++) {
                DataItemGetKList item = list.datalist.get(a);
                DataLvMain dataLvMain = new DataLvMain(item.device_name, item.kid, item.user_id, Integer.valueOf(item.device_type));
                datas.add(dataLvMain);
            }
            lv.setAdapter(new MyAdapter(datas));
            llLoading.setVisibility(View.GONE);
        }

        @Override
        public void onError(ErrorStatus errorStatus) {
            Toast.makeText(context, "获取设备列表失败", Toast.LENGTH_LONG).show();
            llLoading.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_main);

//        setAmbientEnabled();
        initView(); //内部调用initEvent()

        //请求获取设备列表
        OpenWatchBothWay.request(context, ConConstant.PATH_GET_DEVICE_LIST, "",callback);

    }

    private void initView() {
        WatchViewStub stub = (WatchViewStub) findViewById(R.id.stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {
                lv = (WearableListView) findViewById(R.id.listView);
                llLoading = findViewById(R.id.llLoading);
                tv = (TextView) findViewById(R.id.tv);
                initEvent();
            }
        });

    }

    private void initEvent() {
    }

    private class MyAdapter extends WearableListView.Adapter {

        private List<DataLvMain> datas;

        public MyAdapter(List<DataLvMain> datas) {
            this.datas = datas;
        }

        @Override
        public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lv_main, null);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
            MyViewHolder viewHolder = (MyViewHolder) holder;
            final DataLvMain data = datas.get(position);

            viewHolder.tvName.setText(data.deviceName);
            switch (data.deviceType) {
                case Unit.DEVICE_TYPE_1:
                    viewHolder.ivIco.setImageResource(R.drawable.ico_device_1);
                    viewHolder.tvType.setText("一代");
                    break;
                case Unit.DEVICE_TYPE_2:
                    viewHolder.ivIco.setImageResource(R.drawable.ico_device_2);
                    viewHolder.tvType.setText("二代");
                    break;
                case Unit.DEVICE_TYPE_MINI:
                    viewHolder.ivIco.setImageResource(R.drawable.ico_device_3);
                    viewHolder.tvType.setText("mini");
                    break;
                case Unit.DEVICE_TYPE_MINI_PRO:
                    viewHolder.ivIco.setImageResource(R.drawable.ico_device_3);
                    viewHolder.tvType.setText("PRO");
                    break;
            }

            viewHolder.parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AtyDetail.class);
                    intent.putExtra(AtyDetail.EXT_K_ID, data.kId);
                    intent.putExtra(AtyDetail.EXT_TYPE, data.deviceType);
                    intent.putExtra(AtyDetail.EXT_USER_ID, data.userId);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }
    }

    private class MyViewHolder extends WearableListView.ViewHolder {

        ImageView ivIco;
        TextView tvName, tvType;
        View parent;

        public MyViewHolder(View itemView) {
            super(itemView);
            parent = itemView;
            ivIco = (ImageView) itemView.findViewById(R.id.ivIco);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
        }
    }

}
