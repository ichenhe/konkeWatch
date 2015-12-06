package cc.chenhe.konke.watch;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 宸赫 on 2015/9/30.
 */
public class AtyDetail extends Activity {

    public static final String EXT_USER_ID = "userId";
    public static final String EXT_K_ID = "kId";
    public static final String EXT_TYPE = "type";

    GridViewPager pager;
    DotsPageIndicator pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_detail);
        pager = (GridViewPager) findViewById(R.id.pager);
        pageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setPager(pager);

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FmSwitch());
        pager.setAdapter(new MyAdapter(getFragmentManager(), fragments));
    }

    public String getUserId(){
       return getIntent().getStringExtra(EXT_USER_ID);
    }

    public String getKID(){
        return getIntent().getStringExtra(EXT_K_ID);
    }

    public int getType(){
        return getIntent().getIntExtra(EXT_TYPE, 0);
    }

    private class MyAdapter extends FragmentGridPagerAdapter{

        List<Fragment> fragments;

        public MyAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getFragment(int row, int column) {
            return fragments.get(column);
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return fragments.size();
        }
    }
}
