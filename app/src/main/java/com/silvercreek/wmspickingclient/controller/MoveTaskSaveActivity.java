package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.movetaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportMoveTaskData;


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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MoveTaskSaveActivity extends AppCompatActivity {

    public static final String METHOD_EXPORT_MOVETASK_DATA = "MoveTask_Save";
    public static final String METHOD_PUT_MOVETASK_UPDATE = "MoveTask_StatusUpdate";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    private TextView taskID, caseCount, TaskType;
    private ListView TransList;
    private EditText edtPalNo;
    private Button btnHold, btnCancel, btnDone;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private MoveTaskDetailAdapter adapter;
    private List<movetaskdetail> moveTaskDetail;
    private SharedPreferences sharedpreferences;
    private List<movetaskdetail> exportTranList;
    private String mSessionId = "", mCompany = "", mUsername = "", mDeviceId = "";
    private String mLoctid = "";
    private int mTimeout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_task_save);

        taskID = (TextView) findViewById(R.id.taskID);
        caseCount = (TextView) findViewById(R.id.CaseCountId);
        TaskType = (TextView) findViewById(R.id.TaskType);
        TransList = (ListView) findViewById(R.id.lst_TransItems);
        edtPalNo = (EditText) findViewById(R.id.edtLot_Palno1);
        btnHold = (Button) findViewById(R.id.btnHold);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDone = (Button) findViewById(R.id.btnExport);
        btnDone.setEnabled(false);
        btnHold.setEnabled(false);

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


        edtPalNo.requestFocus();
        if (edtPalNo.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtPalNo.setShowSoftInputOnFocus(false);
        } else {
            edtPalNo.setShowSoftInputOnFocus(true);
        }
        taskID.setText(Globals.gMTTaskNo);
        TaskType.setText(Globals.gMTTaskType);

        mDbHelper.openReadableDatabase();
        moveTaskDetail = mDbHelper.getMoveTaskDetail(Globals.gMTTaskNo);
        mDbHelper.closeDatabase();


        mDbHelper.openReadableDatabase();
        String TotCasecount = mDbHelper.mTotMTCasecnt(Globals.gMTTaskNo);
        String CaseCount = mDbHelper.mMTCasecnt(Globals.gMTTaskNo);
        mDbHelper.closeDatabase();

        if (CaseCount == null){
            CaseCount = "0";
        }if (TotCasecount == null){
            TotCasecount = "0";
        }

        TotCasecount =  "Case Cnt: " + String.valueOf(Math.round(Double.valueOf(CaseCount))) + "/" + String.valueOf(Math.round(Double.valueOf(TotCasecount)));
        caseCount.setText(TotCasecount);

        mDbHelper.openReadableDatabase();
        Boolean isScaned = mDbHelper.isScaned_MoveTask(Globals.gMTTaskNo);
        mDbHelper.closeDatabase();

        if (isScaned) {
            btnDone.setEnabled(true);
            btnHold.setEnabled(true);
        } else {
            btnDone.setEnabled(false);
            btnHold.setEnabled(false);
        }

        adapter = new MoveTaskDetailAdapter(MoveTaskSaveActivity.this, moveTaskDetail);
        TransList.setAdapter(adapter);

        TransList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                movetaskdetail mMovetaskdetail = (movetaskdetail) adapter.getItem(i);
                Globals.gMTItem = mMovetaskdetail.getItem().toString();
                Globals.gMTItmDesc = mMovetaskdetail.getItmdesc().toString();
                Globals.gMTQtyrq = mMovetaskdetail.getTqtyrq().toString();
                Globals.gMTTranline = mMovetaskdetail.getTranlineno().toString();
                Globals.gMTPalNO = mMovetaskdetail.getPalno().toString();
                Globals.gMTLoctId = mMovetaskdetail.getLoctid().toString();
                Globals.gMTFromSlot = mMovetaskdetail.getFromSlot().toString();
                String flag = mMovetaskdetail.getFlag().toString();
                String toSLot = mMovetaskdetail.getToSlot().toString();
                String lockedCount = mMovetaskdetail.getLocked().toString();
                if (flag.equals("Y") && !toSLot.equals("")) {
                    mToastMessage.showToast(MoveTaskSaveActivity.this, "Already scanned");
                    edtPalNo.requestFocus();

                } else {
                    if (lockedCount.equals("COUNT")) {
                        mToastMessage.showToast(MoveTaskSaveActivity.this, "Slot locked for Counting");
                        edtPalNo.requestFocus();
                    } else if (Globals.gMTFromSlot.equals("")) {
                        mToastMessage.showToast(MoveTaskSaveActivity.this, "From-Slot cannot be blank");
                        edtPalNo.requestFocus();
                    } else {
                        mSupporter.simpleNavigateTo(MoveTaskToSaveActivity.class);
                    }
                }
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportData("BTNSAVE");
                //new UpdateMoveTaskStatus(mUsername,"COMPLETE").execute();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UpdateMoveTaskStatus(mUsername, Globals.gMTStatus).execute();
            }
        });
        btnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportData("BTNHOLD");
            }
        });
        edtPalNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int ii, KeyEvent keyEvent) {
                if ((ii == EditorInfo.IME_ACTION_UNSPECIFIED) && (keyEvent != null) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        return true;
                    }
                    ii = EditorInfo.IME_ACTION_DONE;
                }
                String palNo = edtPalNo.getText().toString();

                mDbHelper.openReadableDatabase();
                Boolean result = mDbHelper.isPalNoAvailable_MoveTask(palNo, Globals.gMTTaskNo);
                mDbHelper.closeDatabase();

                if (palNo.equalsIgnoreCase("")) {
                    mToastMessage.showToast(MoveTaskSaveActivity.this,
                            "Please Enter the Pallet.");
                    edtPalNo.requestFocus();
                    return true;
                } else if (!result) {
                    mToastMessage.showToast(MoveTaskSaveActivity.this,
                            "Pallet not found");
                    edtPalNo.setText("");
                    edtPalNo.requestFocus();
                    return true;
                } else {
                    mDbHelper.openReadableDatabase();
                    moveTaskDetail = mDbHelper.getMoveTaskDetail(Globals.gMTTaskNo);
                    mDbHelper.closeDatabase();

                    for (int i = 0; i < moveTaskDetail.size(); i++) {
                        if (moveTaskDetail.get(i).getPalno().equals(palNo)) {
                            movetaskdetail mMovetaskdetail = (movetaskdetail) adapter.getItem(i);
                            Globals.gMTItem = mMovetaskdetail.getItem().toString();
                            Globals.gMTItmDesc = mMovetaskdetail.getItmdesc().toString();
                            Globals.gMTQtyrq = mMovetaskdetail.getTqtyrq().toString();
                            Globals.gMTTranline = mMovetaskdetail.getTranlineno().toString();
                            Globals.gMTPalNO = mMovetaskdetail.getPalno().toString();
                            Globals.gMTLoctId = mMovetaskdetail.getLoctid().toString();
                            Globals.gMTFromSlot = mMovetaskdetail.getFromSlot().toString();
                            String flag = mMovetaskdetail.getFlag().toString();
                            String toSLot = mMovetaskdetail.getToSlot().toString();
                            String lockedCount = mMovetaskdetail.getLocked().toString();
                            edtPalNo.setText("");
                            if (flag.equals("Y") && !toSLot.equals("")) {
                                mToastMessage.showToast(MoveTaskSaveActivity.this, "Already scanned");
                                edtPalNo.requestFocus();
                                return true;
                            } else {
                                if (lockedCount.equals("COUNT")) {
                                    mToastMessage.showToast(MoveTaskSaveActivity.this, "Slot locked for Counting");
                                    edtPalNo.requestFocus();
                                    return true;
                                } else if (Globals.gMTFromSlot.equals("")) {
                                    mToastMessage.showToast(MoveTaskSaveActivity.this, "From-Slot cannot be blank");
                                    edtPalNo.requestFocus();
                                    return true;
                                } else {
                                    mSupporter.simpleNavigateTo(MoveTaskToSaveActivity.class);
                                }
                            }
                            break;
                        }
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        new UpdateMoveTaskStatus(mUsername, Globals.gMTStatus).execute();
    }

    public void ExportData(String FromSaveOrHold) {
        exportTranList = new ArrayList<movetaskdetail>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getExportMoveTaskDetail(Globals.gMTTaskNo);
        mDbHelper.closeDatabase();

        if (exportTranList.size() != 0) {
            String exportXml = getRecordXmlExportPO(exportTranList, FromSaveOrHold);
            uploadDataToServiceExportItm ex = (uploadDataToServiceExportItm) new uploadDataToServiceExportItm()
                    .execute(new String[]{exportXml});
            String response = null;
            try {
                response = ex.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (exportTranList.size() != 0) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId, FromSaveOrHold).execute();
            } else {
                ExportError();
            }
        }
    }

    public String getRecordXmlExportPO(List<movetaskdetail> MTList, String FromSaveOrHold) {
        String exportPODataXml = "";
        try {
            ExportMoveTaskData MTData = new ExportMoveTaskData();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "MoveTaskSaveData" + ">");
            for (int i = 0; i < MTList.size(); i++) {
                MTData.writeRTXml(MTList.get(i), sb, MoveTaskSaveActivity.this, mDbHelper, FromSaveOrHold);
            }
            sb.append("</" + "MoveTaskSaveData" + ">");

            exportPODataXml = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            String errorCode = "Err-CLS-2";
            String errMsg = "pick List detail export failed";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage()
                    + "\n" + errMsg);
            String result = "error";
            return result;
        }
        return exportPODataXml;
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(MoveTaskSaveActivity.this);
        alertExit.setTitle("No Data");
        alertExit.setIcon(R.drawable.warning);
        alertExit.setCancelable(false);
        alertExit.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertExit.setMessage("No Data to Export");
        alertExit.show();
    }

    public void alertUser() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MoveTaskSaveActivity.this);
        alertDialog.setTitle("Success");
        alertDialog.setIcon(R.drawable.tick);
        alertDialog.setCancelable(false);
        String s1 = "";
        String s2 = "Moved successfully.";
        Spanned strMessage = Html.fromHtml(s1 + "<br>" + s2);
        alertDialog.setMessage(strMessage);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {

                        mSupporter.simpleNavigateTo(MoveTaskActivity.class);

                    }
                });
        //alertDialog.setMessage("All items have been moved.");
        alertDialog.show();
    }

    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(MoveTaskSaveActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "MoveTask" + ".xml");
            if (!exportFile.exists()) {
                Supporter.createFile(exportFile);
            } else {
                exportFile.delete(); // to refresh the file
                Supporter.createFile(exportFile);
            }

            try {
                FileOutputStream fos = new FileOutputStream(exportFile);
                fos.write(records[0].getBytes());
                result = "success";
                System.out.println("Export success");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result = "nodata";
            } catch (IOException e) {
                e.printStackTrace();
                result = "input error";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mDialog != null) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

            if (result.equals("success")) {

            } else if (result.equals("connection timeout")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this,
                        "Error in exporting, no response from server");
                System.out.println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(MoveTaskSaveActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    class ExportTranData extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId, pfromSaveOrHold;

        public ExportTranData(String Session, String Uname, String Compid, String fromSaveOrHold) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            this.pfromSaveOrHold = fromSaveOrHold;
            dialog = new ProgressDialog(MoveTaskSaveActivity.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_MOVETASK_DATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/MoveTask.xml");
                String pXmldata = FileUtils.readFileToString(xmlData);

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
                info.setName("pXmlData");
                info.setValue(pXmldata);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_EXPORT_MOVETASK_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "MoveTask" + ".xml");
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
                    // result ="false";

                } else if (resultString.toString().contains("<Result>false</Result>")) {
                    result = "false";
                } else {
                    result = "error";
                }
                buf.close();

            } catch (SocketTimeoutException e) {
                result = "time out error";
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "MoveTask" + ".xml");
                mImpOutputFile.delete(); // to refresh the file
                Supporter.createFile(mImpOutputFile);
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

                if (pfromSaveOrHold.equals("BTNHOLD")) {
                    new UpdateMoveTaskStatus(mUsername, "ONHOLD").execute();
                } else {
                    new UpdateMoveTaskStatus(mUsername, "COMPLETE").execute();
                    mDbHelper.getWritableDatabase();
                    mDbHelper.deleteMoveTaskHeaderData(Globals.gMTTaskNo);
                    mDbHelper.closeDatabase();
                }
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")/*||result.equalsIgnoreCase("input error")*/) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId, pfromSaveOrHold).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this, "Unable to update Server");

            } else if (result.equalsIgnoreCase("false")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this, "Unable to update Server");

            } else {
                mToastMessage.showToast(MoveTaskSaveActivity.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }

    class UpdateMoveTaskStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode, taskStatusUpdate;

        public UpdateMoveTaskStatus(String user, String status) {
            this.uCode = user;
            this.taskStatusUpdate = status;
            dialog = new ProgressDialog(MoveTaskSaveActivity.this);
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

                mDbHelper.getWritableDatabase();
                mDbHelper.deleteLoadMoveTaskLookupData();
                mDbHelper.closeDatabase();

                if (taskStatusUpdate.equals("COMPLETE")/*||taskStatusUpdate.equals("ONHOLD")*/) {
                    /*mToastMessage.showToast(MoveTaskSaveActivity.this,
                            "Data exported to Server successfully");*/
                    alertUser();
                } else {
                    mSupporter.simpleNavigateTo(MoveTaskActivity.class);
                }


            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MoveTaskSaveActivity.this,
                        "Failed to Hold");
            } else if (result.equalsIgnoreCase("time out error")/*||result.equalsIgnoreCase("input error")*/) {
                //mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);
                new UpdateMoveTaskStatus(mUsername, taskStatusUpdate).execute();
            } else {
                mToastMessage.showToast(MoveTaskSaveActivity.this, result);
            }
            dialog.cancel();
        }
    }

}