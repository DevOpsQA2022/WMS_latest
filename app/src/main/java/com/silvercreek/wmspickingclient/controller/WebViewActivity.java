package com.silvercreek.wmspickingclient.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.util.Supporter;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class WebViewActivity extends AppBaseActivity {
    private WebView webview;
    private WMSDbHelper dbHelper;
    private Supporter supporter;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_connection_layout);

        dbHelper = new WMSDbHelper(this);
        supporter = new Supporter(this,dbHelper);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        String PROTOCOL = sharedpreferences.getString("Protocol", "");
        String SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        String SERVER_PATH = sharedpreferences.getString("Serverpath", "");

        webview = (WebView) findViewById(R.id.wView_TC);
        webview.getSettings().setJavaScriptEnabled(true);
        String url = PROTOCOL + SERVER_PATH  +"/"+
        APPLICATION_NAME +"/"+ SERVICE_NAME;
        webview.loadUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
