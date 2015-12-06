package cc.chenhe.konke.watch.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.chenhe.konke.watch.R;


public class MyBar extends RelativeLayout {

    RelativeLayout layout;
    ImageView ivLoading;
    TextView tvLeft, tvMiddle, tvRight;
    String tvLeftText, tvMiddleText, tvRightText;
    boolean showLoading;

    public MyBar(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MyBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.bar, this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyBar);
        tvLeftText = a.getString(R.styleable.MyBar_left_text);
        if (tvLeftText == null) {
            tvLeftText = "< 返回";
        }
        tvMiddleText = a.getString(R.styleable.MyBar_middle_text);
        if (tvMiddleText == null) {
            tvMiddleText = "";
        }
        tvRightText = a.getString(R.styleable.MyBar_right_text);
        if (tvRightText == null) {
            tvRightText = "";
        }
        showLoading = a.getBoolean(R.styleable.MyBar_show_loading, false);
        a.recycle();

    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        layout = (RelativeLayout) findViewById(R.id.layout);
        ivLoading = (ImageView) findViewById(R.id.ivLoading);
        tvLeft = (TextView) findViewById(R.id.tvLeft);
        tvMiddle = (TextView) findViewById(R.id.tvMiddle);
        tvRight = (TextView) findViewById(R.id.tvRight);


        if (showLoading) {
            ivLoading.startAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.loading_small_rotate));
            ivLoading.setVisibility(View.VISIBLE);
        } else {
            ivLoading.setVisibility(View.GONE);
        }
        tvLeft.setText(tvLeftText);
        tvMiddle.setText(tvMiddleText);
        tvRight.setText(tvRightText);

    }

    public void setOnClickListener(OnClickListener listener) {
        if (listener != null) {
            tvLeft.setOnClickListener(listener);
            tvMiddle.setOnClickListener(listener);
            tvRight.setOnClickListener(listener);
        }
    }

    public void setShowLoading(boolean isShowLoading) {
        if (isShowLoading) {
            ivLoading.startAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.loading_small_rotate));
            ivLoading.setVisibility(View.VISIBLE);
        } else {
            ivLoading.clearAnimation();
            ivLoading.setVisibility(View.GONE);
        }
    }

    public void setTvLeftText(String text) {
        if (tvLeft != null) {
            tvLeft.setText(text);
        }
    }

    public void setTvMiddleText(String text) {
        if (tvMiddle != null) {
            tvMiddle.setText(text);
        }
    }

    public void setTvRightText(String text) {
        if (tvRight != null) {
            tvRight.setText(text);
        }
    }

    public boolean getShowLoading() {
        if (ivLoading.getVisibility() == View.GONE) {
            return false;
        } else {
            return true;
        }
    }

    public TextView getTvLeft() {
        return tvLeft;
    }

    public TextView getTvMiddle() {
        return tvMiddle;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public ImageView getIvLoading() {
        return ivLoading;
    }

}