package com.leo.enjoytime.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.leo.enjoytime.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ContentLoadingProgressBar progressBar;
    public static final String URL_PARAM = "url";
    public static final String TAG = WebViewActivity.class.getSimpleName();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String url = getIntent().getStringExtra(URL_PARAM);
        if (url == null){
            Log.d(TAG,"webview load url is null");
            return;
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        webView = (WebView) findViewById(R.id.web_view);
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.progressbar);

        //enable js
        webView.getSettings().setJavaScriptEnabled(true);

        //set WebViewClient and WebChromeClient
        webView.setWebViewClient(new WebClient());
        webView.setWebChromeClient(new WebChrome());

        //set settings
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(url);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (View.INVISIBLE == progressBar.getVisibility()) {
                progressBar.setVisibility(View.VISIBLE);
            }
            setTitle(getString(R.string.loading));
        }

    }

    private class WebChrome extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.hide();
            } else {
                if (View.INVISIBLE == progressBar.getVisibility()) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
                progressBar.show();
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            setTitle(title);
            super.onReceivedTitle(view, title);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
