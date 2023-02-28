package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.R;

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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class PickTaskMenuActivity extends AppBaseActivity {

    private ListView transList;
    private EditText edtScanTest;
    private Supporter mSupporter;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mUsername,mDeviceId ="";
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private PickTaskListAdapter adapter;
    private List<picktasklist> pickTaskList;
    private File mImpOutputFile;

    private String mPassword;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_FETCH_PICKTASK_LookUPData ="PickTask_LookupData";
    private SharedPreferences sharedpreferences;
    private int mTimeout;

    private boolean isPostingDataAvail = false;
    private List<String> detailTaskList;
    public static final String METHOD_PUT_PICKTASK_UPDATE = "PickTask_StatusUpdate";

    private String mLoctid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_task_menu);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

       transList = (ListView) findViewById(R.id.lst_TransItems);
        edtScanTest = (EditText) findViewById(R.id.edtScanTest);
        //edtScanTest.setVisibility(View.INVISIBLE);
        //edtScanTest.setFocusable(true);

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mDbHelper.openReadableDatabase();


        mDeviceId = Globals.gDeviceId;
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

        mDbHelper.openReadableDatabase();
        pickTaskList = mDbHelper.getPickTaskList();
        detailTaskList = mDbHelper.getDetailTaskList();
        mDbHelper.closeDatabase();


        if (SOFT_KEYBOARD.equals("CHECKED")){

            edtScanTest.setShowSoftInputOnFocus(false);
        }else {

            edtScanTest.setShowSoftInputOnFocus(false);
        }

        adapter = new PickTaskListAdapter(PickTaskMenuActivity.this, pickTaskList);
        transList.setAdapter(adapter);

        picktasklist tpicktasklist = new picktasklist();


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

               /* if(id!=0) {*/

                picktasklist mPicktasklist = (picktasklist) adapter.getItem(position);
                Globals.gTaskNo = mPicktasklist.getTaskNo().toString();
                Globals.gRoute = mPicktasklist.getRoute().toString();
                Globals.gStop = mPicktasklist.getStop().toString();
                Globals.gStatus = mPicktasklist.getStatus().toString();
                Globals.gSetActivePallet = false;
                Globals.gPickTaskPallet = "";
                mDbHelper.openReadableDatabase();
                isPostingDataAvail = mDbHelper.isDataAvailableForPost(Globals.gTaskNo);
                mDbHelper.closeDatabase();
                new UpdatePickStatus(mUsername,"ACTIVE").execute();



                /*if (isPostingDataAvail&&Globals.holdTaskNum.equalsIgnoreCase(Globals.gTaskNo)){*/

            }/*else {
                    mToastMessage.showToast(PickTaskMenuActivity.this,
                            "Please select Picktask before scanning");
                }}*/

        });
    }

    @Override
    public void onBackPressed() {

        mSupporter.simpleNavigateTo(MainmenuActivity.class);

       /* if (pickTaskList.size()>0){
            cancelAlert();
        }else {
            mSupporter.simpleNavigateTo(MainmenuActivity.class);
        }*/

    }

    public void overWriteDB() {
        final AlertDialog.Builder alertExport = new AlertDialog.Builder(
                PickTaskMenuActivity.this);
        alertExport.setTitle("Confirmation");
        alertExport.setIcon(R.drawable.warning);
        alertExport.setCancelable(false);
        alertExport.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (mSupporter.isNetworkAvailable(PickTaskMenuActivity.this)) {
                            new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gTaskNo, mUsername).execute();
                        } else {
                            mToastMessage.showToast(PickTaskMenuActivity.this,
                                    "Unable to connect with Server. Please Check your internet connection");
                        }
                    }
                });
        alertExport.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
            }
        });

        alertExport
                .setMessage("Pick Task is available. Do you want to overwrite?");
        alertExport.show();
    }

    public void cancelAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel the update?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            mSupporter.simpleNavigateTo(MainmenuActivity.class);


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

    class LoadAllData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pSession, pCompid, pTaskno, pUser;

        public LoadAllData(String mSession, String mCompid, String mTaskNo, String mUser) {
            this.pSession = mSession;
            this.pCompid = mCompid;
            this.pTaskno = mTaskNo;
            this.pUser = mUser;
            dialog = new ProgressDialog(PickTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Load Pick Task Lookup Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_PICKTASK_LookUPData);
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
                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_FETCH_PICKTASK_LookUPData;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "PickTaskAllData" + ".xml");
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

                //If kill or clash the application (For Data Base Delete)
                mDbHelper.getWritableDatabase();
                mDbHelper.deleteExportLot();
                mDbHelper.deletePicktaskDetailHold();
                mDbHelper.deletePicktaskrevetOldQty();
                mDbHelper.insertExportLot();
                mDbHelper.closeDatabase();


                new LoadPickTaskLookData().execute();

            } else if (result.equals("LoginFailed")) {
                new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gTaskNo, mUsername).execute();
                //mToastMessage.showToast(PickTaskMenuActivity.this, "Login failed invalid username or password");
            } else if(result.equalsIgnoreCase("time out error")){
                new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gTaskNo, mUsername).execute();
            }else {
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Unable to fetch data from Server. Please try again.");
            }

            dialog.cancel();
        }

    }

    private class LoadPickTaskLookData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadPickTaskLookData() {
            dialog = new ProgressDialog(PickTaskMenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickTaskMenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteAllLookupData();
                Globals.gPTDetailRowCount = 1;
                mDbHelper.closeDatabase();
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "PickTaskAllData" + ".xml");
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
                mDbHelper.getWritableDatabase();
                mDbHelper.updateDetailTaskNum();
                mDbHelper.updateDetailTempAllocTaskNum();
                mDbHelper.updatePTHeaderTaskNum();
                mDbHelper.updateWHMQTYTaskNum();
                mDbHelper.updateWHMLOTTaskNum();
                mDbHelper.closeDatabase();
                mSupporter.simpleNavigateTo(PickTaskActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickTaskMenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickTaskMenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickTaskMenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickTaskMenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
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

    class UpdatePickStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode,taskStatusUpdate;

        public UpdatePickStatus(String user,String status) {
            this.uCode = user;
            this.taskStatusUpdate = status;
            dialog = new ProgressDialog(PickTaskMenuActivity.this);
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
                info.setValue(Globals.gTaskNo);
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
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "StatusUpdate" + ".xml");
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

                if (mSupporter.isNetworkAvailable(PickTaskMenuActivity.this)) {
                    new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gTaskNo, mUsername).execute();
                } else {
                    mToastMessage.showToast(PickTaskMenuActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }

                /*if (detailTaskList.contains(Globals.gTaskNo)||isPostingDataAvail){
                    mSupporter.simpleNavigateTo(PickTaskActivity.class);
                    //overWriteDB();
                } else {
                    if (mSupporter.isNetworkAvailable(PickTaskMenuActivity.this)) {
                        new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gTaskNo, mUsername).execute();
                    } else {
                        mToastMessage.showToast(PickTaskMenuActivity.this,
                                "Unable to connect with Server. Please Check your internet connection");
                    }
                }*/
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Failed ");
            }  else if(result.equalsIgnoreCase("time out error")){
                new UpdatePickStatus(mUsername,"ACTIVE").execute();
            } else {
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Unable to Hold");
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



    class LogoutRequest extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUsername, pSessionId, pCompId,pDeviceId ;


        String result = "";



        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId ) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


            dialog = new ProgressDialog(PickTaskMenuActivity.this);
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
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
       // new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }

/*    @Override
    public boolean onKeyDown(int Keycode, KeyEvent keyEvent){

        switch (Keycode){
            case KeyEvent.KEYCODE_ENTER: {
                mToastMessage.showToast(PickTaskMenuActivity.this,
                        "Please select Receivetask before scanning");
            }
            case KeyEvent.KEYCODE_BACK:{
                mSupporter.simpleNavigateTo(MainmenuActivity.class);
            }


        }
        return false;

    }*/


}
