package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.io.File;

public class DataUtilitiesActivity extends AppCompatActivity {
    private Button btnRestPallAlloc,btnBreakerUOM,btnClearSessions,btn_printPallet;
    private RelativeLayout badge_task11;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private File mImpOutputFile;
    private int mTimeout;
    private String mLoctid = "";
    private String mSessionId ="", mCompany ="", mUsername ="", mDeviceId = "";
    private SharedPreferences sharedpreferences;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    private String mResetPalletTag = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_utilities);

         btnRestPallAlloc = findViewById(R.id.btn_restPallAlloc);
         btnClearSessions = findViewById(R.id.btn_ClearSessions);
         btnBreakerUOM = findViewById(R.id.btn_BreakerUOM);
         btn_printPallet = findViewById(R.id.btn_printPallet);
         badge_task11 = findViewById(R.id.badge_task11);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
        SOFT_KEYBOARD = sharedpreferences.getString("SoftKey", "");
        NAMESPACE = NAMESPACE + "/";
        Globals.gNamespace = NAMESPACE;
        Globals.gProtocol = URL_PROTOCOL;
        Globals.gServicename = URL_SERVICE_NAME;
        Globals.gAppName = APPLICATION_NAME;
        Globals.gTimeout = sharedpreferences.getString("Timeout", "");
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;


        mDbHelper.openReadableDatabase();
        mResetPalletTag = mDbHelper.mGetResetPalletTag();
        mDbHelper.closeDatabase();

        if (mResetPalletTag.equals("Y")){
            btnClearSessions.setVisibility(View.VISIBLE);
            badge_task11.setVisibility(View.VISIBLE);

        }else {
            btnClearSessions.setVisibility(View.GONE);
            badge_task11.setVisibility(View.GONE);
        }

        btnRestPallAlloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSupporter.simpleNavigateTo(ResetPalletAllocationActivity.class);
            }
        });
        btnClearSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mSupporter.simpleNavigateTo(ClearSessionsActivity.class);
                mToastMessage.showToast(DataUtilitiesActivity.this, "Development under progress.");
            }
        });

        btnBreakerUOM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSupporter.simpleNavigateTo(BreakUomUtlyActivity.class);
            }
        });
        btn_printPallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSupporter.simpleNavigateTo(PrintPalletActivity.class);
            }
        });


    }

    @Override
    public void onBackPressed() {
        mSupporter.simpleNavigateTo(MainmenuActivity.class);
    }
}