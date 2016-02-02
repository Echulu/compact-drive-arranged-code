package com.compactdrive.GoogleDrive;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.compactdrive.R;


public class GetCode_GoogleDrive extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_get_code__google_drive);
        Toolbar t = (Toolbar)findViewById(R.id.toolbar);
        t.setTitle("Compact Drive");
        if(GoogleDriveAuthentication.refToken == null) {
            WebView w = (WebView) findViewById(R.id.GoogleDriveGetCodeWebView);
            w.getSettings().setJavaScriptEnabled(true);
            w.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            w.loadUrl(GoogleDriveAuthentication.generatePermissionUrl());
            w.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    String cod = view.getTitle();
                    if (cod.split("=")[0].equals("Success code")) {
                        GoogleDriveAuthentication.CODE = cod.split("=")[1];
                        finish();
                    }
                }
            });
        }
        else{
            finish();
        }

    }
}
