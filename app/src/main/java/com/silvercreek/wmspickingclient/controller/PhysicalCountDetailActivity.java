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
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.physicalcountDetail;
import com.silvercreek.wmspickingclient.model.physicalcountSlot;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportPhysicalCount;

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

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class PhysicalCountDetailActivity extends AppCompatActivity {

    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static final String METHOD_EXPORT_DATA = "PhysicalCount_Save";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";

    private EditText edtWlotno;
    private ListView lvDetail;
    private Button btnExport, btnCancel, btnNext, btnPrevious;
    private TextView tvSlot, tvPCvalue, tvpage;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private List<physicalcountDetail> DetailList;
    private ToastMessage mToastMessage;
    private PhysicalCountDetailAdapter adapter;
    private List<physicalcountDetail> exportTranList;
    private List<physicalcountDetail> slotDetailList;
    private List<physicalcountDetail> slotDetailListFromHide;
    private File mImpOutputFile;
    private String mUsername;
    private String mPassword;
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mSessionId;
    private String mCompany, mDeviceId;
    private String slotCounted, slotAssigned, countForPage;
    private String mWlotno="";
    private boolean isPostingDataAvail = false;
    private boolean isContinue = false;
    private String lotRefId = "";
    private String pageCount = "";
    private int listCountFromHide = 0;
    private List<physicalcountDetail> displayedList;
    private int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_count_detail);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        edtWlotno = (EditText) findViewById(R.id.edtWlotno);
        lvDetail = (ListView) findViewById(R.id.lst_TransItems);
        btnExport = (Button) findViewById(R.id.btn_export);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnNext = (Button) findViewById(R.id.btn_Next);
        btnPrevious = (Button) findViewById(R.id.btn_previous);
        tvSlot = (TextView) findViewById(R.id.tvSlotValue);
        tvpage = (TextView) findViewById(R.id.tvpage);
        tvPCvalue = (TextView) findViewById(R.id.tvPCvalue);
        displayedList = new ArrayList<>();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;




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

        mDbHelper.openReadableDatabase();
        //COMMENTED EXISTING METHOD
        slotCounted = mDbHelper.getSlotsCounted();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        //COMMENTED EXISTING METHOD
        slotAssigned = mDbHelper.getSlotsAssigned();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        countForPage = mDbHelper.getPhysicalCoutDetailCount();
        mDbHelper.closeDatabase();

        edtWlotno.requestFocus();
        if(edtWlotno.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")){
            edtWlotno.setShowSoftInputOnFocus(false);

        }else {
            edtWlotno.setShowSoftInputOnFocus(true);

        }

        if (Integer.parseInt(countForPage) > 1) {
           // pageCount = String.valueOf(Double.parseDouble(countForPage) / 6);
            pageCount = String.valueOf(Double.parseDouble(countForPage) / 8);
        } else {
            pageCount = "1";
        }

        tvSlot.setText("Pallet Count: " + slotCounted + "/" + slotAssigned);
        tvPCvalue.setText("Slot:  " + Globals.gPCSlot);
        Globals.gPCSurpriseAdd = false;
        Globals.gPCDetailDoclineno = "";
        Globals.gPCDetailRowCount = 1;
        mDbHelper.openWritableDatabase();
        mDbHelper.resetPCDetailRowNo();
        mDbHelper.closeDatabase();

        mDbHelper.openWritableDatabase();
        slotDetailList = mDbHelper.getPhycialCountDetailList();
        mDbHelper.closeDatabase();

        mDbHelper.openWritableDatabase();
        slotDetailListFromHide = mDbHelper.getPhycialCountDetailListForHide();
        mDbHelper.closeDatabase();

        /*if (Globals.FROMHIDE){
            listCountFromHide = slotDetailListFromHide.size();
            if(listCountFromHide > 8)
                currentPos += 8 ;
            }else {
                currentPos = Globals.listCurrentPosition;
            }*/

        currentPos = Globals.listCurrentPosition;

        setDisplayListPosition();
        /*adapter = new PhysicalCountDetailAdapter(PhysicalCountDetailActivity.this, slotDetailList);
        lvDetail.setAdapter(adapter);*/

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPos += 8;
                if (currentPos > slotDetailList.size()) {
                    currentPos -= 8;
                } else {
                    setDisplayListPosition();
                }

            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPos -= 8;
                if (currentPos < 0) {
                    currentPos = 0;
                } else {
                    setDisplayListPosition();
                }
            }
        });


        edtWlotno.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String lotRefId = "";
                             mWlotno = edtWlotno.getText().toString().trim();
                            Globals.gPCUOM = "";
                            mDbHelper.openReadableDatabase();
                            if (!mWlotno.equalsIgnoreCase("")) {
                                lotRefId = mDbHelper.getPhysicalTaskLotNum(mWlotno);
                            }
                            Boolean isAvailWlotno = mDbHelper.isValidmWlotno(lotRefId);
                            mDbHelper.closeDatabase();
                            mDbHelper.openReadableDatabase();
                            Boolean isNewWlotno = mDbHelper.isNewWlotno(lotRefId);
                            mDbHelper.closeDatabase();
                            if (isAvailWlotno) {
                                Globals.gPCWlotno = lotRefId;
                                isContinue = true;
                            } else {
                                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                                       // "Warehouse pallet is '" + lotRefId + " not on the count.");
                                        "Warehouse pallet is not on the count.");
                                //edtWlotno.requestFocus();
                                isContinue = false;
                                edtWlotno.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtWlotno.requestFocus();
                                    }
                                }, 150);
                            }
                            if (isContinue && isNewWlotno) {
                                Globals.gPCSurpriseAdd = true;
                                //Globals.listCurrentPosition = currentPos;

                                String palletid=mWlotno;
                                for (int i = 0; i < slotDetailList.size(); i++) {
                                    if (slotDetailList.get(i).getlotrefid().equals(palletid)) {
                                        if (i <= 8) {
                                            Globals.listCurrentPosition = 0;

                                        } else {
                                            Globals.listCurrentPosition = (i / 8) * 8;

                                        }
                                    }
                                }
                                mSupporter.simpleNavigateTo(PhysicalCountSaveActivity.class);
                            } else if (isContinue) {
                                //Globals.listCurrentPosition = currentPos;

                                String palletid=mWlotno;
                                for (int i = 0; i < slotDetailList.size(); i++) {
                                    if (slotDetailList.get(i).getlotrefid().equals(palletid)) {
                                        if (i <= 8) {
                                            Globals.listCurrentPosition = 0;

                                        } else {
                                            Globals.listCurrentPosition = (i / 8) * 8;

                                        }
                                    }
                                }

                                mSupporter.simpleNavigateTo(PhysicalCountSaveActivity.class);
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        lvDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Globals.listCurrentPosition = currentPos;
                mDbHelper.openReadableDatabase();
                DetailList = mDbHelper.getSeletedDetailList(adapter.getItem(position).getwlotno(), Globals.gPCUOM);
                mDbHelper.closeDatabase();

                if (DetailList.size() > 0) {      //SCS CIRK 2022/07/25 CT69649C:
                    if (DetailList.get(0).getposted().equals("P")) {
                        mToastMessage.showToast(PhysicalCountDetailActivity.this, "Select detail line already posted.");
                    } else {
                        physicalcountDetail mDetailList = (physicalcountDetail) adapter.getItem(position);
                        Globals.gPCWlotno = mDetailList.getwlotno().toString();
                        Globals.gPCUOM = mDetailList.getumeasur().toString();
                        Globals.gPCDetailDoclineno = mDetailList.getdoclineno();
                        mSupporter.simpleNavigateTo(PhysicalCountSaveActivity.class);
                    }
                } else {
                    physicalcountDetail mDetailList = (physicalcountDetail) adapter.getItem(position);
                    Globals.gPCWlotno = mDetailList.getwlotno().toString();
                    Globals.gPCUOM = mDetailList.getumeasur().toString();
                    Globals.gPCDetailDoclineno = mDetailList.getdoclineno();
                    mSupporter.simpleNavigateTo(PhysicalCountSaveActivity.class);
                }
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSupporter.isNetworkAvailable(PhysicalCountDetailActivity.this)) {
                    ExportData();
                } else {
                    mToastMessage.showToast(PhysicalCountDetailActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertUser = new AlertDialog.Builder(PhysicalCountDetailActivity.this);
                alertUser.setTitle("Confirmation");
                alertUser.setIcon(R.drawable.warning);
                alertUser.setCancelable(false);
                alertUser.setMessage("Are you sure you want to cancel?");
                alertUser.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //Globals.FROMHIDE = false;
                                Globals.listCurrentPosition = 0;
                                new RevertPhysicalCountData().execute();
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
        });
    }

    public void ExportData() {
        exportTranList = new ArrayList<physicalcountDetail>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getPCDetailList();
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
              /*  mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Data Exported.");*/
            } else {
                ExportError();
            }
        }
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PhysicalCountDetailActivity.this);
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
    public String getRecordXmlExportPO(List<physicalcountDetail> dList) {
        String exportPODataXml = "";
        try {
            ExportPhysicalCount exportData = new ExportPhysicalCount();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "PhysicalCountData" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PhysicalCountDetailActivity.this, mDbHelper);
            }
            sb.append("</" + "PhysicalCountData" + ">");
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

                        //Globals.FROMHIDE = false;
                        Globals.listCurrentPosition = 0;
                        new RevertPhysicalCountData().execute();

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

    @Override
    protected void onDestroy() {
        // new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }

    private void setDisplayListPosition() // helper function which refresh ListView based on `currentPos`
    {
        displayedList.clear();
        /*String palletid="9836";
            for (int i = 0; i < slotDetailList.size(); i++) {
                if (slotDetailList.get(i).getlotrefid().equals(palletid)) {
                    if (i <= 8) {
                        Globals.listCurrentPosition = 0;

                    } else {
                        Globals.listCurrentPosition = (i / 8) * 8;

                    }
                }
            }*/




        if (currentPos == 0) {
            btnPrevious.setEnabled(false);
        } else {
            btnPrevious.setEnabled(true);
        }

        for (int i = 0; i < 8 && i + currentPos < slotDetailList.size(); i++) {
            displayedList.add(slotDetailList.get(currentPos + i));
        }

        int currentP = currentPos / 8;
        int currentPageNo = currentP + 1;

        int ttl_page = 0;

        if (pageCount.contains(".")) {

            String[] arr = pageCount.split("[.]", 0);
            String zero = arr[1];

            if (String.valueOf(zero.charAt(0)).equals("0")) {
                ttl_page = Integer.valueOf((int) Double.parseDouble(pageCount));
            } else {
                ttl_page = Integer.valueOf(arr[0]) + 1;
            }

        } else {
            ttl_page = Integer.valueOf(pageCount);
        }

        if (currentPageNo == ttl_page) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }

        tvpage.setText("Pages: " + currentPageNo + "/" + Math.round(Double.valueOf(ttl_page)));

        adapter = new PhysicalCountDetailAdapter(PhysicalCountDetailActivity.this, displayedList);
        lvDetail.setAdapter(adapter);

    }

    public class RevertPhysicalCountData extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public RevertPhysicalCountData() {
            mDialog = new ProgressDialog(PhysicalCountDetailActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            mDialog.setMessage("Loading...");
            mDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String result = "success";

            mDbHelper.openReadableDatabase();
            int count = mDbHelper.getUpdatedValueCount();
            mDbHelper.closeDatabase();

            if (count > 0) {
                mDbHelper.openWritableDatabase();
                mDbHelper.deletePhysicalCountDetail();
                List<physicalcountDetail> psList = mDbHelper.getPhysCountTranData();
                mDbHelper.UpdatePhyCountDetailFromTran(psList);
                mDbHelper.closeDatabase();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (mDialog != null) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
            if (s == "success") {
                mSupporter.simpleNavigateTo(PhysicalCountMenuActivity.class);
            }
        }
    }

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PhysicalCountDetailActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "LPPSaveData" + ".xml");
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
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PhysicalCountDetailActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }

    }

    class ExportTranData extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public ExportTranData(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PhysicalCountDetailActivity.this);
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
                        + "/FinalExoprt/LPPSaveData.xml");
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
                String soap_action = NAMESPACE + METHOD_EXPORT_DATA;
                ht.call(soap_action, envelope);

                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "LPPSaveData" + ".xml");
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

               Globals.SETCHECKEDSHOW = false;
               Globals.SETCHECKEDHIDE = false;

                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Data exported to Server successfully");

                mSupporter.simpleNavigateTo(MainmenuActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        result.toString());
            }

            dialog.cancel();
        }
    }

    class LogoutRequest extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUsername, pSessionId, pCompId, pDeviceId;


        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


            dialog = new ProgressDialog(PhysicalCountDetailActivity.this);
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
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
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

        @SuppressLint("ResourceType")
        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (result.equals("success")) {

//                mToastMessage.showToast(SelectCompanyActivity.this,
//                        "success");


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equalsIgnoreCase("time out error")) {

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PhysicalCountDetailActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


}