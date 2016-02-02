package com.compactdrive.GoogleDrive;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.compactdrive.Central.Central;
import com.compactdrive.OneDrive.OneDriveAuthentication;
import com.compactdrive.R;

public class GoogleDrive_SignOut extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_drive__sign_out);
        ProgressBar progressBar= (ProgressBar)findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        WebView w = (WebView) findViewById(R.id.webView);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        w.loadUrl("https://accounts.google.com/logout");
//        final ProgressDialog prog = new ProgressDialog(this);
//        prog.setMessage("Signing out of Google drive...");
//        prog.setCancelable(false);
//        prog.setInverseBackgroundForced(false);
//        prog.show();
        w.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("Login")) {
//                    prog.dismiss();
                    finish();
                    GoogleDriveAuthentication.aceToken = null;
                    GoogleDriveAuthentication.refToken = null;
                    GoogleDriveAuthentication.storeTokens();
//                    Central.openDrawer();
                }
            }
        });
    }
}
