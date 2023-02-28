package com.silvercreek.wmspickingclient.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMLOT;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktaskheader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportPickTask;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class PickTaskStagingSlot extends AppCompatActivity {

    private EditText edtStagingslot;
    private Button btnExport;
    private TextView tvPallet,tvRoute,tvTrailer,tvTrailerhead;

    private List<picktaskdetail> exportTranList;

    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;

    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_EXPORT_DATA ="PickTask_SaveMain";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mSessionId;
    private String mTrailer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_task_staging_slot);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        tvPallet = (TextView) findViewById(R.id.tvPallet);
        tvRoute = (TextView) findViewById(R.id.tvRoute);
        tvTrailer = (TextView) findViewById(R.id.tvTrailer);
        tvTrailerhead = (TextView) findViewById(R.id.tvTrailerhead);
        edtStagingslot = (EditText) findViewById(R.id.edtStagingSlot);
        btnExport = (Button) findViewById(R.id.btnExport);

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();

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
        Globals.gServerpath = URL_SERVER_PATH;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");

        tvPallet.setText(Globals.gPickTaskPallet);
        tvRoute.setText(Globals.gRoute);
        mDbHelper.openReadableDatabase();
        Boolean isTrailerAvailable = mDbHelper.isTrailerAvailable();
        mDbHelper.closeDatabase();
        if (isTrailerAvailable){
            mDbHelper.openReadableDatabase();
            mTrailer = mDbHelper.GetTrailer();
            mDbHelper.closeDatabase();
        }
        tvTrailerhead.setVisibility(View.VISIBLE);
        tvTrailer.setVisibility(View.VISIBLE);
        tvTrailer.setText(mTrailer);

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stgSlot = edtStagingslot.getText().toString();
                mDbHelper.openReadableDatabase();
                Boolean isValidStgSlot = mDbHelper.isValidStgSlot(stgSlot);
                Boolean isValidTrailer = mDbHelper.isValidTrailer(stgSlot);

                mDbHelper.closeDatabase();
                if (stgSlot.equals("")){
                    mToastMessage.showToast(PickTaskStagingSlot.this,
                            "Invalid Entry");
                } else if(!isValidStgSlot && !isValidTrailer){
                    mToastMessage.showToast(PickTaskStagingSlot.this,
                            "Invalid Entry");
                } else {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String currentDate = simpleDateFormat.format(calendar.getTime());
                    Globals.gStgslot = stgSlot;
                    Globals.gPkTime = currentDate;
                    if (mSupporter.isNetworkAvailable(PickTaskStagingSlot.this)) {
                        ExportData();
                    } else {
                        mToastMessage.showToast(PickTaskStagingSlot.this,
                                "Unable to connect with Server. Please Check your internet connection");
                    }
                }
            }
        });
    }

    public void ExportData() {
        exportTranList = new ArrayList<picktaskdetail>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getPickTaskDetail();
        mDbHelper.closeDatabase();

        if (exportTranList.size() != 0) {
            String exportXml = getRecordXmlExportPO(exportTranList);
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
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else {
                ExportError();
            }
        }
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PickTaskStagingSlot.this);
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

    // Method that returns the XML to be exported
    public String getRecordXmlExportPO(List<picktaskdetail> dList) {
        String exportPODataXml = "";
        try {
            ExportPickTask exportData = new ExportPickTask();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "PickTaskData" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickTaskStagingSlot.this, mDbHelper);
            }
            sb.append("</" + "PickTaskData" + ">");
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

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PickTaskStagingSlot.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "PickTask" + ".xml");
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
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickTaskStagingSlot.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    class ExportTranData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public ExportTranData(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickTaskStagingSlot.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_DATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/PickTask.xml");
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
                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_EXPORT_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "PickTask" + ".xml");
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
                } else if(resultString.toString().contains("<Result>true</Result>")){
                    result ="success";
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
                mDbHelper.openWritableDatabase();
                mDbHelper.deletePickTaskDetail();
                mDbHelper.closeDatabase();
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "Data exported to Server successfully");
                mSupporter.simpleNavigateTo(MainmenuActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")){
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickTaskStagingSlot.this,
                        result.toString());
            }

            dialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        cancelAlert();
    }

    public void cancelAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
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
}
