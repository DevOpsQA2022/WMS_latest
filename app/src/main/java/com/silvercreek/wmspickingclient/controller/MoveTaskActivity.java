package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.MoveTaskHeader;
import com.silvercreek.wmspickingclient.model.Movetasklist;
import com.silvercreek.wmspickingclient.model.movetaskdetail;
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

public class MoveTaskActivity extends AppCompatActivity {

    public static final String METHOD_FETCH_MOVETASK_DETAIL = "MoveTask_LookupData";
    public static final String METHOD_PUT_MOVETASK_UPDATE = "MoveTask_StatusUpdate";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    private EditText edtScanTest;
    private ListView moveListView;
    private MoveTaskMenuAdapter moveadapter;
    private MoveTaskHeader moveTaskHeader = null;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private File mImpOutputFile;
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mSessionId, mCompany, mUsername, mDeviceId = "";
    private String mLoctid = "";
    private List<movetaskdetail> moveTaskDetail;
    private List<Movetasklist> moveTasklist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_task);

        mSupporter = new Supporter(this, mDbHelper);
        moveListView = findViewById(R.id.moveHeaderList);
        edtScanTest = findViewById(R.id.edtScanTest);

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

        edtScanTest.requestFocus();
        if (edtScanTest.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtScanTest.setShowSoftInputOnFocus(false);
        } else {
            edtScanTest.setShowSoftInputOnFocus(true);
        }
        mDbHelper.openReadableDatabase();
        moveTasklist = mDbHelper.getMoveTaskList();
        mDbHelper.closeDatabase();

        moveadapter = new MoveTaskMenuAdapter(MoveTaskActivity.this, moveTasklist);
        moveListView.setAdapter(moveadapter);

        moveListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Movetasklist movetasklist = (Movetasklist) moveadapter.getItem(i);
                Globals.gMTTaskNo = movetasklist.getTaskNo().toString();
                Globals.gMTStatus = movetasklist.getStatus().toString();
                Globals.gMTTaskType = movetasklist.getTaskType().toString();
                Globals.gMTRowPrty = movetasklist.getRowPrty();
                Globals.FROMMENULIST = true;

                mDbHelper.openReadableDatabase();
                moveTaskDetail = mDbHelper.getMoveTaskDetail(Globals.gMTTaskNo);
                mDbHelper.closeDatabase();
                if (mSupporter.isNetworkAvailable(MoveTaskActivity.this)) {
                    new LoadAllDataMoveTask(mSessionId, Globals.gCompanyId, Globals.gMTTaskNo, mUsername, Globals.gLoctid).execute();
                } else {
                    mToastMessage.showToast(MoveTaskActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
            }
        });
    }

    private void endDBTransaction() {
        mDbHelper.mSetTransactionSuccess(); // setting the transaction
        Log.i("Transaction success", "Transaction success.");
        mDbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        mDbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }

    private void startDBTransaction(String action) {
        // transaction is started here..
        mDbHelper.getWritableDatabase();
        Log.i("Writable DB Open", "Writable Database Opened.");
        mDbHelper.mBeginTransaction();
        Log.i("Transaction started", "Transaction successfully started for "
                + action);
    }

    @Override
    public void onBackPressed() {
        mSupporter.simpleNavigateTo(MainmenuActivity.class);
    }

    class LoadAllDataMoveTask extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pSession, pCompid, pTaskno, pUser, pLoctid;

        public LoadAllDataMoveTask(String mSession, String mCompid, String mTaskNo, String mUser, String mLoctid) {
            this.pSession = mSession;
            this.pCompid = mCompid;
            this.pTaskno = mTaskNo;
            this.pUser = mUser;
            this.pLoctid = mLoctid;
            dialog = new ProgressDialog(MoveTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Load Move Task Lookup Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_MOVETASK_DETAIL);
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

                /*info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(pLoctid);
                info.setType(String.class);
                request.addProperty(info);*/

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_FETCH_MOVETASK_DETAIL;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "MoveTaskAllData" + ".xml");
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
                new LoadMoveTaskLookData().execute();
            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(MoveTaskActivity.this,
                        "Login failed invalid username or password");
            } else if (result.equals("time out error")) {
                new LoadAllDataMoveTask(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();
            } else {
                mToastMessage.showToast(MoveTaskActivity.this,
                        "Unable to get Details");
            }
            dialog.cancel();
        }
    }

    private class LoadMoveTaskLookData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadMoveTaskLookData() {
            dialog = new ProgressDialog(MoveTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MoveTaskActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteLoadMoveTaskLookupData();
                mDbHelper.closeDatabase();

                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "MoveTaskAllData" + ".xml");
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
                new UpdateMoveTaskStatus(mUsername, "ACTIVE").execute();
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MoveTaskActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MoveTaskActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MoveTaskActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MoveTaskActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }

    class UpdateMoveTaskStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode, taskStatusUpdate;

        public UpdateMoveTaskStatus(String user, String status) {
            this.uCode = user;
            this.taskStatusUpdate = status;
            dialog = new ProgressDialog(MoveTaskActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_PUT_MOVETASK_UPDATE);
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
                info.setValue(Globals.gMTTaskNo);
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
                String soap_action = NAMESPACE + METHOD_PUT_MOVETASK_UPDATE;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "MoveTaskStatusUpdate" + ".xml");
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

                mDbHelper.openWritableDatabase();
                mDbHelper.UpdateMoveTaskStatus(Globals.gMTTaskNo, taskStatusUpdate);
                mDbHelper.closeDatabase();
                mSupporter.simpleNavigateTo(MoveTaskSaveActivity.class);

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MoveTaskActivity.this,
                        "Failed to Hold");
            } else if (result.equalsIgnoreCase("time out error")/*||result.equalsIgnoreCase("input error")*/) {
                //mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);
                new UpdateMoveTaskStatus(mUsername, "ACTIVE").execute();
            } else {
                mToastMessage.showToast(MoveTaskActivity.this, result);
            }
            dialog.cancel();
        }
    }
}