package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.Supporter;

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

public class RepackMainMenuScreen extends AppCompatActivity {
 private Button btn_RawMaterials,btn_FinishedMaterials;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static String NAMESPACE = "";
    private String mSessionId = "";
    private String mDeviceId = "";
    private String mCompany = "";
    private String mLoctid = "";
    private WMSDbHelper mDbHelper;
    private String mUsername = "";
    private SharedPreferences sharedpreferences;
    private String selectedItem = "";
    private ToastMessage mToastMessage;
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    private int mTimeout;
    public static String URL_SERVER_PATH = "";
    public static String hold = "";

    public static final String METHOD_EXPORT_DATA = "PickTask_SaveMain";

    private Supporter mSupporter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repack_main_menu_screen);
        btn_RawMaterials = findViewById(R.id.btn_Raw);
        btn_FinishedMaterials = findViewById(R.id.btn_Finished);


        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
        NAMESPACE = NAMESPACE + "/";
        Globals.gNamespace = NAMESPACE;
        Globals.gProtocol = URL_PROTOCOL;
        Globals.gServicename = URL_SERVICE_NAME;
        Globals.gAppName = APPLICATION_NAME;
        Globals.gTimeout = sharedpreferences.getString("Timeout", "");
        Globals.isNewWlotno = false;
        Globals.FromRaw= false;
        Globals.FromFinish = false;

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDeviceId = Globals.gDeviceId;
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;

        Globals.RecallLstDTE_code = getIntent().getStringExtra("dteCode");
        if(Globals.RecallLstDTE_code != null){
            Globals.RecallLstDTE_codeInc = Globals.RecallLstDTE_code;
        }

        if(Globals.RecallLstDTE_code == null) {
            Globals.RecallLstDTE_code = Globals.RecallLstDTE_codeInc;
        }
        hold = getIntent().getStringExtra("fromhold");

        Globals.RecallLstDTE_codeFin = getIntent().getStringExtra("dteCodefin");

        if(Globals.RecallLstDTE_codeFin != null){
            Globals.RecallLstDTE_codeFinish = Globals.RecallLstDTE_codeFin;
        }

        if(Globals.RecallLstDTE_codeFin == null) {
            Globals.RecallLstDTE_codeFin = Globals.RecallLstDTE_codeFinish;
        }
        hold = getIntent().getStringExtra("fromholdfin");



        btn_RawMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Globals.FromRaw= true;

                Intent theIntent = new Intent( RepackMainMenuScreen.this, PickRepackIngredientsActivity.class);
                theIntent.putExtra("repacknum", "");
                if(hold != null){
                if(hold.equals("holdTrue")){
                    theIntent.putExtra("fromhold", "holdTrue");
                }else {
                    theIntent.putExtra("fromhold", "");
                }
                }else {
                    theIntent.putExtra("fromhold", "");
                }
                theIntent.putExtra("dteCode", Globals.RecallLstDTE_code);
                startActivity(theIntent);
            }
        });

        btn_FinishedMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Globals.FromFinish = true;
                Intent theIntent = new Intent( RepackMainMenuScreen.this, PickRepackFinishGoodsActivity.class);
                theIntent.putExtra("repacknumfin", "");
                if(hold != null){
                    if(hold.equals("holdTruefin")){
                        theIntent.putExtra("fromholdfin", "holdTruefin");
                    }else {
                        theIntent.putExtra("fromholdfin", "");
                    }
                }else {
                    theIntent.putExtra("fromholdfin", "");
                }
                theIntent.putExtra("dteCodefin", Globals.RecallLstDTE_codeFin);
                startActivity(theIntent);
            }
        });
    }

    public void onBackPressed() {

          //  BackpressAlert();
        //new LogoutRequest(mDeviceId, mUsername, mSessionId, Globals.gCompanyId).execute();

        Intent theIntent = new Intent(RepackMainMenuScreen.this, MainmenuActivity.class);
        startActivity(theIntent);

    }

    private void BackpressAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(RepackMainMenuScreen.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new LogoutRequest(mDeviceId, mUsername, mSessionId, Globals.gCompanyId).execute();


                    }
                });

        alertUser.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertUser.show();
    }


    class LogoutRequest extends AsyncTask<String, String, String> {
        private String pUsername, pSessionId, pCompId,pDeviceId ;


        String result = "";



        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId ) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


        }

        @Override
        protected void onPreExecute() {
            Log.d("123123","start");

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.d("123123","start");

            String result = "";

            try {

                SoapObject request = new SoapObject(NAMESPACE, LOGOUTREQUEST);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            /*    File xmlData = Supporter.getImportFolderPath(mUsername
                        + "/Result/RepackPickList.xml");
                String pXmldata = FileUtils.readFileToString(xmlData);*/
                PropertyInfo info = new PropertyInfo();

                info.setName("pDeviceId");
                info.setValue(pDeviceId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUsername);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(pSessionId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserType");
                info.setValue("WMSUSR");
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompId);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + LOGOUTREQUEST;
                Log.d("123123","start");

                ht.call(soap_action, envelope);
                Log.d("123123","start");

                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "Result", "LogoutRequest" + ".xml");
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
                }  else {
                    result ="success";

                }
                buf.close();

            } catch (SocketTimeoutException e) {
                Log.e("tag", "error", e);

                result = "time out error";
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("tag", "error", e);

                result = "input error";
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("tag", "error", e);

                result = "error";
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("tag", "error", e);
                result = "error";
            }

            return result;
        }

        @SuppressLint("ResourceType")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            Log.d("123123","post");

            if (result.equals("success")) {

                Intent theIntent = new Intent(RepackMainMenuScreen.this, LoginScreenActivity.class);

                startActivity(theIntent);


            } else if (result.equals("server failed")) {

            }else if (result.equalsIgnoreCase("time out error")){

            } else if (result.equalsIgnoreCase("error")) {

            } else {

                mToastMessage.showToast(RepackMainMenuScreen.this,
                        "Unable to update Server.");
            }

        }
    }


}