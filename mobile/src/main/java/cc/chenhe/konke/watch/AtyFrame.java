package cc.chenhe.konke.watch;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class AtyFrame extends FragmentActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private View llOne, llTwo;
    private TextView tvOne, tvTwo;
    private ImageView ivOne, ivTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_frame);

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager
                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // actionBar.setSelectedNavigationItem(position);
                    }
                });

        // checkUpdate();
        initEvent();
    }

    /**
     * 返回键被按下
     */
    private long lastClickBackTime = 0;

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() > 0) {
            mViewPager.setCurrentItem(0);
            return;
        }
        if (lastClickBackTime <= 0) {
            Toast.makeText(this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
            lastClickBackTime = System.currentTimeMillis();
        } else {
            long nowClickBackTime = System.currentTimeMillis();
            if (nowClickBackTime - lastClickBackTime < 1000) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "再按一次后退键退出应用", Toast.LENGTH_SHORT).show();
                lastClickBackTime = System.currentTimeMillis();
            }
        }
    }

    private void initEvent() {
        llOne = findViewById(R.id.llOne);
        llTwo = findViewById(R.id.llTwo);
//        llThree = findViewById(R.id.llThree);
//        llFour = findViewById(R.id.llFOUR);
        ivOne = (ImageView) findViewById(R.id.imageView1);
        ivTwo = (ImageView) findViewById(R.id.imageView2);
//        ivThree = (ImageView) findViewById(R.id.imageView3);
//        ivFour = (ImageView) findViewById(R.id.imageView4);
        tvOne = (TextView) findViewById(R.id.textView1);
        tvTwo = (TextView) findViewById(R.id.textView2);
//        tvThree = (TextView) findViewById(R.id.textView3);
//        tvFour = (TextView) findViewById(R.id.textView4);

        android.view.View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.llOne:
                        mViewPager.setCurrentItem(0);
                        break;

                    case R.id.llTwo:
                        mViewPager.setCurrentItem(1);
                        break;

//                    case R.id.llThree:
//                        mViewPager.setCurrentItem(2);
//                        break;
//
//                    case R.id.llFOUR:
//                        mViewPager.setCurrentItem(3);
//                        break;
                }
            }
        };
        llOne.setOnClickListener(clickListener);
        llTwo.setOnClickListener(clickListener);
//        llThree.setOnClickListener(clickListener);
//        llFour.setOnClickListener(clickListener);

        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                switch (arg0) {
                    case 0:
                        resetTabs();
                        ivOne.setImageResource(R.drawable.tab_user_selected);
                        tvOne.setTextColor(getResources().getColor(
                                R.color.tab_selected));
                        break;

                    case 1:
                        resetTabs();
                        ivTwo.setImageResource(R.drawable.tab_func_selected);
                        tvTwo.setTextColor(getResources().getColor(
                                R.color.tab_selected));
                        break;

                    case 2:
                        resetTabs();
//                        ivThree.setImageResource(R.drawable.tab_ex_selected);
//                        tvThree.setTextColor(getResources().getColor(
//                                R.color.tab_selected));
                        break;

                    case 3:
                        resetTabs();
//                        ivFour.setImageResource(R.drawable.tab_voice_selected);
//                        tvFour.setTextColor(getResources().getColor(
//                                R.color.tab_selected));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void resetTabs() {
        ivOne.setImageResource(R.drawable.tab_user_normal);
        tvOne.setTextColor(getResources().getColor(R.color.tab_normal));
        ivTwo.setImageResource(R.drawable.tab_func_normal);
        tvTwo.setTextColor(getResources().getColor(R.color.tab_normal));
//        ivThree.setImageResource(R.drawable.tab_ex_normal);
//        tvThree.setTextColor(getResources().getColor(R.color.tab_normal));
//        ivFour.setImageResource(R.drawable.tab_voice_normal);
//        tvFour.setTextColor(getResources().getColor(R.color.tab_normal));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            switch (position) {
                case 0:
                    return new FmMain();

                case 1:
                    return new FmFunc();

                case 2:
                    return new FmTest();

                case 3:
                    return new FmTest();
            }
            // return PlaceholderFragment.newInstance(position + 1);
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return "1";
                case 1:
                    return "2";
                case 2:
                    return "3";
                case 4:
                    return "4";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater
                    .inflate(R.layout.fm_main, container, false);
            return rootView;
        }
    }

//	private void checkUpdate() {
//		RequestParams params = new RequestParams();
//		params.put("v",
//				String.valueOf(Unit.getVersion(getApplicationContext())));
//		Unit.httpClient.post(Unit.URL + "update.php", params,
//				new TextHttpResponseHandler() {
//
//					@SuppressLint("NewApi")
//					@Override
//					public void onSuccess(int arg0, Header[] arg1,
//							final String r) {
//						// TODO Auto-generated method stub
//						if (!r.equals("ok")) {
//							new AlertDialog.Builder(context)
//									.setTitle("提示")
//									.setMessage("发现新版,是否更新?")
//									.setPositiveButton(
//											"更新",
//											new DialogInterface.OnClickListener() {
//
//												@Override
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//
//													Intent intent = new Intent();
//													intent.setAction("android.intent.action.VIEW");
//													Uri content_url = Uri
//															.parse(r);
//													intent.setData(content_url);
//													if (!Unit
//															.isIntentAvailable(
//																	context,
//																	intent)) {
//														if (Unit.getAndroidSDKVersion() < 11) {
//															android.text.ClipboardManager clip = (android.text.ClipboardManager) context
//																	.getSystemService(Context.CLIPBOARD_SERVICE);
//															clip.setText(r);
//														} else {
//															ClipboardManager clip = (ClipboardManager) context
//																	.getSystemService(Context.CLIPBOARD_SERVICE);
//															clip.setPrimaryClip(ClipData
//																	.newPlainText(
//																			null,
//																			r));
//														}
//														new AlertDialog.Builder(
//																context)
//																.setTitle("提示")
//																.setMessage(
//																		"未检测到浏览器\n更新地址已复制到剪贴板，请手动粘贴到浏览器下载")
//																.setPositiveButton(
//																		"知道了",
//																		new OnClickListener() {
//
//																			@Override
//																			public void onClick(
//																					DialogInterface dialog,
//																					int which) {
//																				// TODO
//																				// Auto-generated
//																				// method
//																				// stub
//																				finish();
//																			}
//																		})
//																.show();
//													} else {
//														startActivity(intent);
//														finish();
//													}
//												}
//											})
//									.setNegativeButton("退出",
//											new OnClickListener() {
//
//												@Override
//												public void onClick(
//														DialogInterface dialog,
//														int which) {
//													// TODO Auto-generated
//													// method stub
//													finish();
//												}
//											}).setCancelable(false).show();
//						}
//					}
//
//					@Override
//					public void onFailure(int arg0, Header[] arg1, String arg2,
//							Throwable arg3) {
//						// TODO Auto-generated method stub
//
//					}
//				});
//	}

}
