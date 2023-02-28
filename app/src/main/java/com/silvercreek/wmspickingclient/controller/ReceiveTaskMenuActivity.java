package com.silvercreek.wmspickingclient.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetasklist;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class ReceiveTaskMenuActivity extends AppCompatActivity {

    private ListView transList;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private ReceiveTaskMenuAdapter adapter;
    private List<receivetasklist> receiveTaskList;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mUsername,mDeviceId ="";
    private EditText edtScanTest;

    private File mImpOutputFile;

    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_FETCH_RECEIVETASK_LookUPData ="ReceiveTask_LookupData";
    public static final String METHOD_FETCH_REFRESH_SLOT ="ReceiveTask_RefreshSlotList";
    public static final String METHOD_PUT_PICKTASK_UPDATE = "ReceiveTask_StatusUpdate";
    public static String SOFT_KEYBOARD = "";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mLoctid = "";
    private String taskStatus="";
    private String StrFlag="Y";
    private List<receivetaskdetail> treceivetaskdetail;
    private RelativeLayout rr_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_task_menu);

        transList = (ListView) findViewById(R.id.lst_TransItems);
        edtScanTest = (EditText) findViewById(R.id.edtScanTestRec);
      //  rr_layout = (RelativeLayout) findViewById(R.id.rr_layout);

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
        NAMESPACE = NAMESPACE +"/";
        Globals.gNamespace=NAMESPACE;
        Globals.gProtocol=URL_PROTOCOL;
        Globals.gServicename=URL_SERVICE_NAME;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;
        Globals.gRTTrancancel="";

        if (SOFT_KEYBOARD.equals("CHECKED")){

            edtScanTest.setShowSoftInputOnFocus(false);
        }else {

            edtScanTest.setShowSoftInputOnFocus(false);
        }

        mDbHelper.openReadableDatabase();
        receiveTaskList = mDbHelper.getReceiveTaskList();
        mDbHelper.closeDatabase();

        adapter = new ReceiveTaskMenuAdapter(ReceiveTaskMenuActivity.this, receiveTaskList);
        transList.setAdapter(adapter);

     /* rr_layout.setOnKeyListener(new View.OnKeyListener() {
          @Override
          public boolean onKey(View view, int i, KeyEvent keyEvent) {

              switch (i){
                  case KeyEvent.KEYCODE_ENTER: {
                      mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                              "Please select Receivetask before scanning");
                  }
                  case KeyEvent.KEYCODE_BACK:{
                      mSupporter.simpleNavigateTo(MainmenuActivity.class);
                  }


              }

              return false;
          }
      });*/


        edtScanTest.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (i == KeyEvent.KEYCODE_ENTER)) {
                    switch (i) {
                        case KeyEvent.KEYCODE_ENTER:

                            String edtScanTst = edtScanTest.getText().toString().trim();
                            if (!edtScanTst.equals("")){

                                ScanAlert();
                                edtScanTest.requestFocus();
                                //mToastMessage.showToast(PickTaskMenuActivity.this,"Please Select Task Before Scan");

                            }


                        default:
                            break;
                    }
                }
                return false;
            }
        });


        transList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                    receivetasklist mReceivetasklist = (receivetasklist) adapter.getItem(position);
                    Globals.gRTTaskNo = mReceivetasklist.getTaskNo().toString();
                    Globals.gRTDocNo = mReceivetasklist.getDocno().toString();
                    Globals.gRTDocType = mReceivetasklist.getDoctype().toString();
                    Globals.gRTSTATUS = mReceivetasklist.getStatus();

                    mDbHelper.openReadableDatabase();
                    treceivetaskdetail = mDbHelper.getReceiveTaskDetail(Globals.gRTTaskNo);
                    mDbHelper.closeDatabase();

                    Globals.FROMMENULIST= true;
                    // new UpdateReceiveStatus(mUsername,"ACTIVE").execute();

                    if (mSupporter.isNetworkAvailable(ReceiveTaskMenuActivity.this)) {
                        if (treceivetaskdetail.size() > 0) {

                            new RefreshSlot(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();

                        } else {
                            new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();
                        }
                    } else {
                        mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                                "Unable to connect with Server. Please Check your internet connection");
                    }
            }
        });
    }


    class LoadAllData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pSession, pCompid, pTaskno, pUser, pLoctid;

        public LoadAllData(String mSession, String mCompid, String mTaskNo, String mUser, String mLoctid) {
            this.pSession = mSession;
            this.pCompid = mCompid;
            this.pTaskno = mTaskNo;
            this.pUser = mUser;
            this.pLoctid = mLoctid;
            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Load Receive Task Lookup Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_RECEIVETASK_LookUPData);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(pSession);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompid);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pTaskNo");
                info.setValue(pTaskno);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUser);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(pLoctid);
                info.setType(String.class);
                request.addProperty(info);
                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_FETCH_RECEIVETASK_LookUPData;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "ReceiveTaskAllData" + ".xml");
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

                new LoadReceiveTaskLookData().execute();

            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Login failed invalid username or password");
            } else if (result.equals("time out error")) {
                new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Unable to get Details");
            }

            dialog.cancel();
        }

    }

    class RefreshSlot extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pSession, pCompid, pTaskno, pUser, pLoctid;

        public RefreshSlot(String mSession, String mCompid, String mTaskNo, String mUser, String mLoctid) {
            this.pSession = mSession;
            this.pCompid = mCompid;
            this.pTaskno = mTaskNo;
            this.pUser = mUser;
            this.pLoctid = mLoctid;
            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Load Receive RefreshSlot Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_REFRESH_SLOT);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();

                info.setName("pSessionId");
                info.setValue(pSession);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pTaskNo");
                info.setValue(pTaskno);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUser);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(pLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_FETCH_REFRESH_SLOT;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "ReceiveTaskAllDataSlot" + ".xml");
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

              //  new LoadReceiveTaskLookData().execute();
                new LoadReceiveTaskLookDataREFRESH().execute();

            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Login failed invalid username or password");
            } else if (result.equals("time out error")) {
                new RefreshSlot(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Unable to get Details");
            }

            dialog.cancel();
        }

    }

    class RefreshSlotForSever extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pSession, pCompid, pTaskno, pUser, pLoctid;

        public RefreshSlotForSever(String mSession, String mCompid, String mTaskNo, String mUser, String mLoctid) {
            this.pSession = mSession;
            this.pCompid = mCompid;
            this.pTaskno = mTaskNo;
            this.pUser = mUser;
            this.pLoctid = mLoctid;
            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Load Receive RefreshSlot Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_REFRESH_SLOT);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();

                info.setName("pSessionId");
                info.setValue(pSession);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pTaskNo");
                info.setValue(pTaskno);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUser);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(pLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_FETCH_REFRESH_SLOT;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "ReceiveTaskAllDataSlot" + ".xml");
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

             //   new LoadReceiveTaskLookData().execute();
                new LoadReceiveTaskLookDataREFRESH().execute();

            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Login failed invalid username or password");
            } else if (result.equals("time out error")) {
                new RefreshSlotForSever(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Unable to get Details");
            }

            dialog.cancel();
        }

    }


    public void ScanAlert() {
        AlertDialog.Builder ScanAlertDilog = new AlertDialog.Builder(this);
        ScanAlertDilog.setTitle("Alert");
        ScanAlertDilog.setIcon(R.drawable.warning);
        ScanAlertDilog.setCancelable(false);
        ScanAlertDilog.setMessage("Please select task before scan");
        ScanAlertDilog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

        ScanAlertDilog.show();
    }

    private class LoadReceiveTaskLookData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadReceiveTaskLookData() {
            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(ReceiveTaskMenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
               /* mDbHelper.openWritableDatabase();
                mDbHelper.deleteReceiveTaskLookupData();
                Globals.gRTDetailRowCount = 1;
                mDbHelper.closeDatabase();*/

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteReceiveTaskDetail();
                mDbHelper.closeDatabase();
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "ReceiveTaskAllData" + ".xml");
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
                mDbHelper.openReadableDatabase();
                int loadidValue = mDbHelper.GetLoadidValue();
                mDbHelper.closeDatabase();
                if (loadidValue > 0) {
                    ShowLoadType();
                } else {
                    //mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
                    new RefreshSlotForSever(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();

                    //  new UpdateReceiveStatus(mUsername,"ACTIVE").execute();


                }
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }

    private class LoadReceiveTaskLookDataREFRESH extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadReceiveTaskLookDataREFRESH() {
            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(ReceiveTaskMenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
               /* mDbHelper.openWritableDatabase();
                mDbHelper.deleteReceiveTaskLookupData();
                Globals.gRTDetailRowCount = 1;
                mDbHelper.closeDatabase();*/
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "ReceiveTaskAllDataSlot" + ".xml");
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
                mDbHelper.openReadableDatabase();
                int loadidValue = mDbHelper.GetLoadidValue();
                mDbHelper.closeDatabase();
                if (loadidValue > 0) {
                    ShowLoadType();
                } else {
                    //mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
                    //  new RefreshSlot(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();

                    new UpdateReceiveStatus(mUsername,"ACTIVE").execute();


                }
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this, "Error");
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

    private String GetLoadidValue(){
        String GetErrMsg ="";
        try
        {
            //creating a constructor of file class and parsing an XML file
            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "PickTaskList" + ".xml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(mImpOutputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Acknowledgement");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++)
            {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) node;
                    GetErrMsg = eElement.getElementsByTagName("ErrorMessage").item(0).getTextContent();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String errorCode = "Err501";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage());
            String result = "Invalid File";
            return result;
        }
        return GetErrMsg;
    }

    public void ShowLoadType() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Enter Load Information");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mSupporter.simpleNavigateTo(ReceiveTaskSelectLoadActivity.class);
                    }
                });

        alertUser.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
                    }
                });

        alertUser.show();
    }
    class UpdateReceiveStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode,taskStatusUpdate;

        public UpdateReceiveStatus(String user,String status) {
            this.uCode = user;
            this.taskStatusUpdate = status;
            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_PUT_PICKTASK_UPDATE);
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

                info = new PropertyInfo();
                info.setName("pTaskno");
                info.setValue(Globals.gRTTaskNo);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pStatus");
                info.setValue(taskStatusUpdate);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_PUT_PICKTASK_UPDATE;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "ReceiveStatusUpdate" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().contains("false")) {
                    result = "Failed";
                    if (resultString.toString().toLowerCase().contains("already assigned to another user")) {
                        result = "Assinged another user";
                    }
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

                mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);

            } else if (result.equals("Failed")) {
                new UpdateReceiveStatus(mUsername,"ACTIVE").execute();
                /*mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Invalid Pallet Number");*/
            } else if(result.equalsIgnoreCase("time out error")){
                //mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
                new UpdateReceiveStatus(mUsername,"ACTIVE").execute();
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Unable to Hold");
            }
            dialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
       mSupporter.simpleNavigateTo(MainmenuActivity.class);
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


            dialog = new ProgressDialog(ReceiveTaskMenuActivity.this);
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

//                mToastMessage.showToast(SelectCompanyActivity.this,
//                        "success");


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
    //    new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
        Log.d("receiveTask","destroy");
    }

   /* @Override
    public boolean onKeyDown(int Keycode, KeyEvent keyEvent){

        switch (Keycode){
            case KeyEvent.KEYCODE_ENTER: {
                mToastMessage.showToast(ReceiveTaskMenuActivity.this,
                        "Please select Receivetask before scanning");
            }
            case KeyEvent.KEYCODE_BACK:{
                mSupporter.simpleNavigateTo(MainmenuActivity.class);
            }


        }
       return false;

    }*/


}