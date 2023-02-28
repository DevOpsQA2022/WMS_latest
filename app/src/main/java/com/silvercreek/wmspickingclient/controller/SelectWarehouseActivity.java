package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.company;
import com.silvercreek.wmspickingclient.model.location;
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

import static com.silvercreek.wmspickingclient.controller.SelectCompanyActivity.LOGOUTREQUEST;
import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class SelectWarehouseActivity extends AppBaseActivity {
    private Supporter supporter;
    private Button btnLoad;
    private Spinner spinnerLoc;
    private WMSDbHelper dbHelper;
    private ToastMessage toastMessage;
    private ArrayAdapter<String> adapter;
    private List<String> mLocationList;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    private int mTimeout;
    private String mSessionId;
    private ToastMessage mToastMessage;
    private String mCompany;
    private String mLoctid;
    private String mUsername,mDeviceId,mCompanyId;
    private SharedPreferences sharedpreferences;
    private File mImpOutputFile;
    public static final String METHOD_GET_TASK_NOTIFICATION ="GetTaskNotification";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_warehouse);

        dbHelper = new WMSDbHelper(this);
        supporter = new Supporter(this, dbHelper);
        toastMessage = new ToastMessage();


        spinnerLoc = (Spinner) findViewById(R.id.spinner);
        btnLoad = (Button) findViewById(R.id.btn_Next);

        dbHelper.openReadableDatabase();
        mSessionId = dbHelper.mGetSessionId();
        dbHelper.closeDatabase();

        mCompany = Globals.gCompanyDatabase;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;
        mCompanyId = Globals.gCompanyId;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
        NAMESPACE = NAMESPACE +"/";
        Globals.gNamespace=NAMESPACE;
        Globals.gProtocol=URL_PROTOCOL;
        Globals.gServicename=URL_SERVICE_NAME;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");

        dbHelper.openReadableDatabase();
        mLocationList = dbHelper.mGetLocations();
        dbHelper.closeDatabase();

        adapter = new ArrayAdapter<String>(SelectWarehouseActivity.this, android.R.layout.simple_spinner_item, mLocationList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoc.setAdapter(adapter);
        spinnerLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                String sLocation = spinnerLoc.getSelectedItem().toString();
                Globals.gLoctid = sLocation;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoctid = Globals.gLoctid;
                if (supporter.isNetworkAvailable(SelectWarehouseActivity.this)) {
                    new RefreshPage(mUsername).execute();
                } else {
                    toastMessage.showToast(SelectWarehouseActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
            }
        });
    }
    //Refresh the page
    class RefreshPage extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public RefreshPage(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(SelectWarehouseActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Validating parameters");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_TASK_NOTIFICATION);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(mCompany);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_TASK_NOTIFICATION;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "TaskCount" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().toLowerCase().contains("false")) {
                    result = "LoginFailed";

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
                if (supporter.isNetworkAvailable(SelectWarehouseActivity.this)) {

                    new LoadTaskNotification().execute();

                } else {
                    toastMessage.showToast(SelectWarehouseActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if(result.equalsIgnoreCase("time out error")){
                new RefreshPage(mUsername).execute();
            } else if (result.equals("LoginFailed")) {
                toastMessage.showToast(SelectWarehouseActivity.this,
                        "Login failed invalid username or password");
            } else {
                toastMessage.showToast(SelectWarehouseActivity.this,
                        "Unable to fetch Location from Server");
            }

            dialog.cancel();
        }
    }

    private class LoadTaskNotification extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadTaskNotification() {
            dialog = new ProgressDialog(SelectWarehouseActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(SelectWarehouseActivity.this, dbHelper, Globals.gUsercode);

                List<String> importFileList = supporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = supporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                dbHelper.openWritableDatabase();
                dbHelper.deleteNotificationcount();
                dbHelper.deleteMenulist();
                dbHelper.closeDatabase();
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "TaskCount" + ".xml");
                            System.out.println("Name" + mImpOutputFile);
                            if (mImpOutputFile.exists()) {
                                InputStream inputStream;
                                inputStream = new FileInputStream(mImpOutputFile);
                                String[] resultArray = fileLoader.parseDocument(inputStream);
                                result = resultArray[0];
                                errMsg = resultArray[1];

                                if (!result.equals("success")) {
                                    dbHelper.mEndTransaction();
                                    break;
                                }

                            } else {
                                result = "File not available";
                                dbHelper.mEndTransaction();
                                break;
                            }
                        }

                        if (!result.equals("success")) {

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
                //updateTaskNotification();
                Globals.gIsFromWarehouseSelection = true;
                supporter.simpleNavigateTo(MainmenuActivity.class);
            } else if (result.equals("nosd")) {
                toastMessage.showToast(SelectWarehouseActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                toastMessage.showToast(SelectWarehouseActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                toastMessage.showToast(SelectWarehouseActivity.this, "File not available");
            } else {
                toastMessage.showToast(SelectWarehouseActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
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

//                mToastMessage.showToast(SelectCompanyActivity.this,
//                        "success");


            } else if (result.equals("server failed")) {
//                mToastMessage.showToast(SelectWarehouseActivity.this,
//                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
//                mToastMessage.showToast(SelectWarehouseActivity.this,
//                        "Unable to update Server");
            } else {
//                mToastMessage.showToast(SelectWarehouseActivity.this,
//                        "Unable to update Server. Please Save again");
            }

        }
    }

    private void startDBTransaction(String action) {
        // transaction is started here..
        dbHelper.getWritableDatabase();
        Log.i("Writable DB Open", "Writable Database Opened.");
        dbHelper.mBeginTransaction();
        Log.i("Transaction started", "Transaction successfully started for "
                + action);
    }

    private void endDBTransaction() {
        dbHelper.mSetTransactionSuccess(); // setting the transaction

        Log.i("Transaction success", "Transaction success.");
        dbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        dbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }

    public void onBackPressed() {
        new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
        supporter.simpleNavigateTo(LoginScreenActivity.class);
    }
  /*  @Override
    protected void onDestroy() {
        Log.d("123123","selectCompany");
        new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
        super.onDestroy();

    }*/

    private class MyThread implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("123123","Hitting this ");

                try {

                    SoapObject request = new SoapObject(NAMESPACE, LOGOUTREQUEST);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            /*    File xmlData = Supporter.getImportFolderPath(mUsername
                        + "/Result/RepackPickList.xml");
                String pXmldata = FileUtils.readFileToString(xmlData);*/
                    PropertyInfo info = new PropertyInfo();

                    info.setName("pDeviceId");
                    info.setValue(mDeviceId);
                    info.setType(String.class);
                    request.addProperty(info);

                    info = new PropertyInfo();
                    info.setName("pUserName");
                    info.setValue(mUsername);
                    info.setType(String.class);
                    request.addProperty(info);

                    info = new PropertyInfo();
                    info.setName("pSessionId");
                    info.setValue(mSessionId);
                    info.setType(String.class);
                    request.addProperty(info);

                    info = new PropertyInfo();
                    info.setName("pUserType");
                    info.setValue("WMSUSR");
                    info.setType(String.class);
                    request.addProperty(info);

                    info = new PropertyInfo();
                    info.setName("pCompany");
                    info.setValue(mCompanyId);
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
//                        result = "Unable to Export.";
                    } else if (resultString.toString().equalsIgnoreCase(
                            "Failed to post, refer log file.")) {
//                        result = "server failed";
                    } else if (resultString.toString().contains(
                            "Unexpected end of file has occurred")) {
//                        result = "Unexpected";
                    } else if (resultString.toString().contains(
                            "Data at the root level is invalid")) {
//                        result = "Invalid";
                    } else if (resultString.toString().contains(
                            "PO Updation failed.")) {
//                        result = "PO Updation failed.";
                    }  else {
//                        result ="success";

                    }
                    buf.close();

                } catch (SocketTimeoutException e) {
//                    result = "time out error";
                    e.printStackTrace();
                } catch (IOException e) {
//                    result = "input error";
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
//                    result = "error";
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("tag", "error", e);
//                    result = "error";
                }


//                new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();

            } catch (Exception e) {
                Log.d("123123","Exception");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
//        new Thread(new MyThread()).start();
//        Log.d("123123","start");
//        new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
        runOnUiThread(new Runnable() {
            public void run() {
//                Log.d("123123","start");
                new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
            }
        });
//

        super.onDestroy();
    }
}