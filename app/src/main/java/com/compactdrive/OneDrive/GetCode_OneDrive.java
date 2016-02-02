package com.compactdrive.OneDrive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.support.v7.widget.Toolbar;

import com.compactdrive.R;

public class GetCode_OneDrive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_code_onedrive);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_get_code_onedrive);
        OneDriveAuthentication.readTokens();
        Toolbar toolBar = (Toolbar)findViewById(R.id.toolbar);
        toolBar.setTitle("Compact Drive");
        if (OneDriveAuthentication.refToken == null) {
            WebView w = (WebView) findViewById(R.id.getCodeWebView);
            w.getSettings().setJavaScriptEnabled(true);
            w.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            w.getSettings().setSavePassword(false);
            w.loadUrl("https://login.live.com/oauth20_authorize.srf?client_id=000000004817DF25&scope=wl.skydrive+wl.offline_access&response_type=code&redirect_uri=http://localhost");
            w.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (url.split("=")[0].equals("http://localhost/?code")) {
                        OneDriveAuthentication.code = url.split("=")[1];
                        finish();
                    }
                }
            });
        }else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
