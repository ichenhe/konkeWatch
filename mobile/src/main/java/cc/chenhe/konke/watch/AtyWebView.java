package cc.chenhe.konke.watch;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cc.chenhe.konke.watch.ui.MyBar;

/**
 * Created by 宸赫 on 2015/10/23.
 */
public class AtyWebView extends Activity {
    private MyBar bar;
    private WebView webView;

    public static final String EXT_URL = "url";
    public static final String EXT_TITLE = "title";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_webview);
        bar = (MyBar) findViewById(R.id.bar);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        bar.setTvMiddleText(getIntent().getStringExtra(EXT_TITLE));
        initEvent();
        webView.loadUrl(getIntent().getStringExtra(EXT_URL));
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
    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            bar.setShowLoading(false);
        }
    }

}
