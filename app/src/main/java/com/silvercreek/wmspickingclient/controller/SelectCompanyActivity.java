package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.company;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class SelectCompanyActivity extends AppBaseActivity {

    private Supporter mSupporter;
    private Activity mContext;
    private String mSessionId;
    private String mCompanyId;
    private String mDeviceId;
    private WMSDbHelper mDbHelper;
    private Button btnNext;

    private String mUsername;
    private Spinner spinnerCompany;
    private ToastMessage mToastMessage;
    private List<company> CompanyList;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_GET_LOCATION = "GetLocations";

    public static final String LOGOUTREQUEST = "LogoutRequest";
    int mTimeout;
    private File mImpOutputFile;
    private ArrayAdapter<String> adapter;
    private List<String> mCompanyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_company);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        spinnerCompany= (Spinner)findViewById(R.id.spinnerCompany);
        btnNext = (Button)findViewById(R.id.btn_Next);



        mUsername = Globals.gUsercode;

        NAMESPACE = Globals.gNamespace;
        URL_PROTOCOL = Globals.gProtocol;
        URL_SERVICE_NAME = Globals.gServicename;
        APPLICATION_NAME = Globals.gAppName;
        URL_SERVER_PATH = Globals.gServerpath;
        mTimeout = Integer.valueOf(Globals.gTimeout);
        mSessionId = Globals.gSessionId;

        mDeviceId = Globals.gDeviceId;
        mCompanyId = Globals.gCompanyId;
        mDbHelper.openReadableDatabase();
        mCompanyList = getCompanyList();
        mDbHelper.closeDatabase();

        adapter = new ArrayAdapter<String>(SelectCompanyActivity.this, android.R.layout.simple_spinner_item, mCompanyList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompany.setAdapter(adapter);
        spinnerCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                String sCompany = spinnerCompany.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sCompany = spinnerCompany.getSelectedItem().toString();
                //strGetRoomId = edtroomid.getText().toString().trim();
                String strSelectCompanyId[] = sCompany.split(", ");
                Globals.gCompanyId = strSelectCompanyId[0].replace(",","");
                Globals.gCompanyName = strSelectCompanyId[1].replace(",","");
                mDbHelper.openReadableDatabase();
                String mCompanydb = mDbHelper.getCompanyDb(Globals.gCompanyId);
                Globals.gCompanyDatabase = mCompanydb;
                mDbHelper.closeDatabase();
                mDbHelper.openReadableDatabase();
                Boolean isValidCompany = mDbHelper.isValidCompany(Globals.gCompanyId,Globals.gCompanyName);
                mDbHelper.closeDatabase();
                if (mSupporter.isNetworkAvailable(SelectCompanyActivity.this)) {
                    new GetLocation(Globals.gUsercode, Globals.gCompanyDatabase).execute();
                } else if (isValidCompany) {
                    Intent intentInv = new Intent(mContext,SelectWarehouseActivity.class);
                    mContext.startActivity(intentInv);
                } else {
                    mToastMessage.showToast(mContext,
                            "Unable to connect with Server. Please Check your internet connection");
                }
            }
        });
    }
    private List<String> getCompanyList() {
        List<String> sCompanyList = new ArrayList<String>();

        mDbHelper.openReadableDatabase();
        CompanyList = mDbHelper.getCompanyList();
        mDbHelper.closeDatabase();
        for (int i = 0; i < CompanyList.size(); i++) {
            sCompanyList.add(CompanyList.get(i).getCompanyID() + ", " + CompanyList.get(i).getCompanyName());
        }
        return sCompanyList;
    }



    public void onBackPressed() {

        new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
        mSupporter.simpleNavigateTo(LoginScreenActivity.class);
    }

    @Override
    protected void onDestroy() {

       // new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
        Log.d("receiveTask","destroy");
        super.onDestroy();
    }



    class LogoutRequest extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUsername, pSessionId, pCompId,pDeviceId ;


        String result = "";



        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId ) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


            dialog = new ProgressDialog(SelectCompanyActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            Log.d("SelectCompay","dobackground");

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
                ht.call(soap_action, envelope);
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

        @SuppressLint("ResourceType")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (result.equals("success")) {

 /*             mToastMessage.showToast(SelectCompanyActivity.this,
                      "success");*/


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(SelectCompanyActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(SelectCompanyActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(SelectCompanyActivity.this,
                        "Unable to update Server. Please Save again");

                Log.d("SelectCompay","faill");
            }

            dialog.cancel();
        }
    }

    class GetLocation extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;
        private String uCompdb;

        public GetLocation(String user, String compdb) {
            this.uCode = user;
            this.uCompdb=compdb;
            dialog = new ProgressDialog(SelectCompanyActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Accessing data file");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_LOCATION);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Funcation argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany"); // .Net Funcation argument key
                info.setValue(uCompdb);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Funcation argument key
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);
                //request.addProperty("uCode", uCode);
                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_LOCATION;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "location" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().equalsIgnoreCase("false")) {
                    result = "Fail to login please check user and password";

                } else if (resultString.toString().equalsIgnoreCase("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().equalsIgnoreCase("No data found.")) {
                    result = "No data found in server";
                } else {
                    result = "success";
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
                new DataLoadToLocationTable().execute();

            } else if(result.equalsIgnoreCase("time out error")){
                new GetLocation(Globals.gUsercode, Globals.gCompanyDatabase).execute();
            }else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(mContext,
                        "Log In Failed Invalid Username or Password");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(mContext,
                        "Data server connection failed.");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(mContext,
                        "No data found.");
            } else {
                mToastMessage.showToast(mContext,
                        "Invalid Username or Password");
            }
            dialog.cancel();
        }
    }

    private class DataLoadToLocationTable extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public DataLoadToLocationTable() {
            dialog = new ProgressDialog(SelectCompanyActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";
            try {
                String result = "";

                DataLoader fileLoader = new DataLoader(SelectCompanyActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteLocationData();
                mDbHelper.closeDatabase();

                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Document";

                            if ((c > 0) && (fileName.equals("Document"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "location" + ".xml");
                            System.out.println("Name" + mImpOutputFile);
                            if (mImpOutputFile.exists()) {
                                InputStream inputStream;
                                inputStream = new FileInputStream(mImpOutputFile);
                                String[] resultArray = fileLoader.parseDocument(inputStream);

                                result = resultArray[0];
                                errMsg = resultArray[1];

                                if (!result.equals("success")) {
                                    mDbHelper.mEndTransaction();
                                    break;
                                }

                            } else {
                                result = "File not available";
                                mDbHelper.mEndTransaction();
                                break;
                            }
                        }
                        if (!result.equals("success")) { // to break from

                            break;
                        }
                    }
                    endDBTransaction(); // to end db transaction

                } else {
                    result = "File not available";
                }

                return result;
            } catch (Exception exe) {
                exe.printStackTrace();
                String errorCode = "Err-CLS-2";
                LogfileCreator.mAppendLog(errorCode + " : " + exe.getMessage()
                        + "\n" + errMsg);
                String result = "error";
                return result;
            }
        }

        @Override
        protected void onPostExecute(final String result) {

            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            if (result.equals("success")) {
                mSupporter.simpleNavigateTo(SelectWarehouseActivity.class);
                /*Intent intentInv = new Intent(mContext,SelectWarehouseActivity.class);
                mContext.startActivity(intentInv);*/
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(mContext, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(mContext, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(mContext, "File not available");
            } else {
                mToastMessage.showToast(mContext, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }
    private void startDBTransaction(String action) {
        // transaction is started here..
        mDbHelper.getWritableDatabase();
        Log.i("Writable DB Open", "Writable Database Opened.");
        mDbHelper.mBeginTransaction();
        Log.i("Transaction started", "Transaction successfully started for "
                + action);
    }

    private void endDBTransaction() {
        mDbHelper.mSetTransactionSuccess(); // setting the transaction
        Log.i("Transaction success", "Transaction success.");
        mDbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        mDbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }
}
