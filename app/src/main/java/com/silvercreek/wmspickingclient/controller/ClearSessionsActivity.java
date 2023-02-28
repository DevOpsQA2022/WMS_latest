package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.Supporter;

import org.apache.commons.io.FileUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;

public class ClearSessionsActivity extends AppCompatActivity {
    private Button btn_increase,btn_decrease,btn_CloseSessions;
    private EditText edtTxt_days;
    private ToastMessage mToastMessage;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    private String mSessionId ="", mCompany ="", mUsername ="", mDeviceId = "";
    public static final String METHOD_EXPORT_CLEAR_SESSIONS = "DataUtils_ClearSessions";
    private Supporter mSupporter;
    private SharedPreferences sharedpreferences;
    private WMSDbHelper mDbHelper;
    private int mTimeout;
    private String mLoctid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_sessions);
        btn_increase = findViewById(R.id.btn_increase);
        btn_decrease = findViewById(R.id.btn_decrease);
        btn_CloseSessions = findViewById(R.id.btn_CloseSessions);
        edtTxt_days = findViewById(R.id.edtTxt_days);




        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;

       /* mDbHelper.openReadableDatabase();
        mResetPalletTag = mDbHelper.mGetResetPalletTag();
        mDbHelper.closeDatabase();*/

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


        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtTxt_days.setShowSoftInputOnFocus(false);
        } else {
            edtTxt_days.setShowSoftInputOnFocus(true);
        }

        btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtTxt_days.getText().toString().equals("")&&Integer.parseInt(edtTxt_days.getText().toString())>=0){
                    display(Integer.parseInt(edtTxt_days.getText().toString())+ 1);
                }else {
                    mToastMessage.showToast(ClearSessionsActivity.this, "Enter number of days.");
                }

            }
        });

        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtTxt_days.getText().toString().equals("")){
                if (Integer.parseInt(edtTxt_days.getText().toString()) > 1){
                    display(Integer.parseInt(edtTxt_days.getText().toString())- 1);
                }
            }else {
                    mToastMessage.showToast(ClearSessionsActivity.this, "Enter number of days.");
                }
            }
        });

        btn_CloseSessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String NumOfDays = edtTxt_days.getText().toString();
                if (!NumOfDays.equals("")&&Integer.parseInt(NumOfDays)>=1){
                    new ExportTranDataForUpdatePal(mSessionId, Globals.gUsercode, Globals.gCompanyId,NumOfDays).execute();
                }else {
                    mToastMessage.showToast(ClearSessionsActivity.this, "Enter number of days.");
                }
            }
        });


    }

    private void display(int num) {
        edtTxt_days.setText(String.valueOf(num));
    }

    class ExportTranDataForUpdatePal extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId,pDays;

        public ExportTranDataForUpdatePal(String Session, String Uname, String Compid,String pDays) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            this.pDays = pDays;
            dialog = new ProgressDialog(ClearSessionsActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating the server..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_CLEAR_SESSIONS);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(pSessionId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUname);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pDays");
                info.setValue(pDays);
                info.setType(String.class);
                request.addProperty(info);


                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_EXPORT_CLEAR_SESSIONS;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "CloseSessions" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().equalsIgnoreCase("Export failed.")) {
                    result = "Unable to Export.";
                } else if (resultString.toString().equalsIgnoreCase(
                        "Failed to post, refer log file.")) {
                    result = "server failed";
                } else if (resultString.toString().contains(
                        "Unexpected end of file has occurred")) {
                    result = "Unexpected";
                } else if (resultString.toString().contains(
                        "Data at the root level is invalid")) {
                    result = "Invalid";
                } else if (resultString.toString().contains(
                        "PO Updation failed.")) {
                    result = "PO Updation failed.";
                } else if (resultString.toString().contains("<Result>true</Result>")) {
                    result = "success";
                    // result = "error";
                } else {
                    result = "error";
                }
                buf.close();

            } catch (SocketTimeoutException e) {
                result = "time out error";
                e.printStackTrace();
            } catch (IOException e) {
                result = "input error";
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                result = "error";
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("tag", "error", e);
                result = "error";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (result.equals("success")) {

                mToastMessage.showToast(ClearSessionsActivity.this,"Sessions close Successfully.");

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(ClearSessionsActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(ClearSessionsActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")) {
                //  new ExportTranDataForUpdatePal(mSessionId, Globals.gUsercode, Globals.gCompanyId,pSOorRepack).execute();
                mToastMessage.showToast(ClearSessionsActivity.this,
                        "Time out Unable to update Server");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ClearSessionsActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(ClearSessionsActivity.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        mSupporter.simpleNavigateTo(DataUtilitiesActivity.class);
    }
}