package com.compactdrive.OneDrive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.compactdrive.Central.Central;
import com.compactdrive.R;

public class OneDrive_SignOut extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onedrive_sign_out);
        Toolbar toolBar = (Toolbar)findViewById(R.id.toolbar);
        toolBar.setTitle("Compact Drive");
        WebView w = (WebView) findViewById(R.id.webView);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        w.loadUrl("https://login.live.com/logout.srf");
        final ProgressDialog prog = new ProgressDialog(this);
        prog.setMessage("Signing out of one drive...");
        prog.setCancelable(false);
        prog.setInverseBackgroundForced(false);
        prog.show();
        w.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("msn")) {
                    prog.dismiss();
                    finish();
                    Central.openDrawer();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        OneDriveAuthentication.aceToken = null;
        OneDriveAuthentication.refToken = null;
        OneDriveAuthentication.storeTokens();
    }
}