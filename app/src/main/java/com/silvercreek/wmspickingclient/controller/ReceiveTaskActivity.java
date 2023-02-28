package com.silvercreek.wmspickingclient.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMLOT;
import com.silvercreek.wmspickingclient.model.picktaskWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktaskheader;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.model.receivetaskWHRPLT;
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetaskexportdetail;
import com.silvercreek.wmspickingclient.model.receivetaskheader;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.model.receivetaskloadtype;
import com.silvercreek.wmspickingclient.model.receivetaskprintdetail;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportReceiveTaskData;
import com.silvercreek.wmspickingclient.xml.ExportReceiveTaskLoadData;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class ReceiveTaskActivity extends AppCompatActivity {


    private List<receivetaskloadtype> TranList;
    private List<receivetaskexportdetail> exportTranList;
    private ListView transList;
    private TextView tvTaskDesc,tvPalletcnt,tvCasecnt,tvVendDesc;
    private Button btnExport,btnCancel;
    private EditText edtLot_recTsk;
    private Boolean resultScan = false;
    private TableRow TablePalletScan;
    String toasttext = "";
    private String isPrinttag = "0";
    private String pitem = "";
    private Document document;
    private PdfPCell cell;
    private PdfPTable table;
    private PdfContentByte cb;
    private PdfWriter docWriter;
    private File pdfFile = null;
    private String mPath;
    String filename = "ReceiveTaskPalletLabel.pdf";

    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    String updatedQty;
    private ToastMessage mToastMessage;
    public static final String METHOD_EXPORT_RECEIVETASK_DATA ="ReceiveTask_SaveMain";
    public static final String METHOD_PUT_PICKTASK_UPDATE = "ReceiveTask_StatusUpdate";
    public static final String METHOD_FETCH_RECEIVETASK_LookUPData ="ReceiveTask_LookupData";
   // public static final String METHOD_PUT_PICKTASK_UPDATE = "ReceiveTask_StatusUpdate";
    private List<receivetaskdetail> receivetaskdetail;
    private List<receivetaskWHRPLT> receivetaskWHRPLTS;
    private List<receivetaskdetail> treceivetaskdetail;
    private List<receivetaskheader> receivetaskheader;
    private List<receivetaskprintdetail> PrintLabelList;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    private SharedPreferences sharedpreferences;

    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mUsername,mDeviceId ="";
    private int mTimeout;

    private ReceiveTaskAdapter adapter;
    private String StrFlag="Y";
    private Boolean isPartial=false;
    private String Catchwt="";
    private Button btnHold;
    private String pwlotno = "", plotrefid  , precdate, pexpdate, precuser, ptaskno,
            pQty, pitmdesc,pPalno;
    private String mLoctid = "";
    String recQty = "";
    private String taskStatus="";
    private String doctype="";
    private File mImpOutputFile;

    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_task);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        tvTaskDesc = (TextView) findViewById(R.id.tvTaskDesc);
        tvVendDesc = (TextView) findViewById(R.id.tvVendDesc);
        tvPalletcnt = (TextView) findViewById(R.id.tvPalletCnt);
        tvCasecnt = (TextView) findViewById(R.id.tvCaseCnt);
        transList = (ListView) findViewById(R.id.lst_TransItems);
        btnExport = (Button) findViewById(R.id.btnExport);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnHold = (Button) findViewById(R.id.btnHold);
        edtLot_recTsk = (EditText) findViewById(R.id.edtLot_reCTsk);

        TablePalletScan = (TableRow) findViewById(R.id.tablerow4);
        tvVendDesc.setSelected(true);
        btnExport.setEnabled(false);
        btnHold.setEnabled(false);
        btnCancel.setEnabled(false);
        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        SOFT_KEYBOARD = sharedpreferences.getString("SoftKey", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
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

        if (SOFT_KEYBOARD.equals("CHECKED")){
            edtLot_recTsk.setShowSoftInputOnFocus(false);
        }else {
            edtLot_recTsk.setShowSoftInputOnFocus(true);
        }

        mDbHelper.openReadableDatabase();
        receivetaskdetail = mDbHelper.getReceiveTaskDetail();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        receivetaskheader = mDbHelper.getReceiveTaskHeader(Globals.gRTDocNo);
        mDbHelper.closeDatabase();

        /*mDbHelper.openWritableDatabase();
        mDbHelper.UpdateReceiveTaskStatus(Globals.gRTTaskNo,"ONHOLD");
        mDbHelper.closeDatabase();*/

        if (Globals.gRTSTATUS.equals("ASSIGNED") && Globals.FROMMENULIST){
       // if (Globals.gRTSTATUS.equals("ASSIGNED")){
            if(mSupporter.isNetworkAvailable(ReceiveTaskActivity.this)){
                printAlert();

                Globals.FROMMENULIST=false;
            } else {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Unable to connect with Server. Please Check your internet connection");
                Globals.FROMMENULIST=false;
            }
        }




         if(receivetaskdetail.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
             doctype = receivetaskdetail.get(0).getdoctype();
         }else{
             mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
             LogfileCreator.mAppendLog("No data available in receivetaskdetail(ReceiveTaskActivity)");
         }
             if (doctype.equals("MANUAL")) {
                 TablePalletScan.setVisibility(View.GONE);

             } else {
                 TablePalletScan.setVisibility(View.VISIBLE);
             }

        edtLot_recTsk.requestFocus();
        if(edtLot_recTsk.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if(receivetaskheader.size()>0) {        //SCS CIRK 2022/07/25 CT69649C:
            String TaskDesc = receivetaskheader.get(0).getdescrip();
            String VendDesc = receivetaskheader.get(0).getvenddescrip();
            TaskDesc = "Task: " + Globals.gRTTaskNo + "  " + TaskDesc;
            VendDesc = "Vendor " + VendDesc;
            tvTaskDesc.setText(TaskDesc);
            tvVendDesc.setText(VendDesc);
        }else{
            mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
            LogfileCreator.mAppendLog("No data available in receivetaskheader(ReceiveTaskActivity)");
        }

        if(receivetaskdetail.size()>0) {        //SCS CIRK 2022/07/25 CT69649C:
            Catchwt = receivetaskdetail.get(0).getcatchwt();
        }else{
            mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
            LogfileCreator.mAppendLog("No data available in receivetaskdetail(ReceiveTaskActivity)");
        }

        if(receivetaskheader.size()>0) {        //SCS CIRK 2022/07/25 CT69649C:
            Globals.gRTWMSDate = receivetaskheader.get(0).getwmsdate();
        }else {
            mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
            LogfileCreator.mAppendLog("No data available in receivetaskheader(ReceiveTaskActivity)");
        }

        mDbHelper.openReadableDatabase();
        String TotPalcnt = mDbHelper.mTotPalcnt();
        String TotCasecount = mDbHelper.mTotRTCasecnt(Catchwt);
        String Palcnt = mDbHelper.mPalcnt(StrFlag);
        String CaseCount = mDbHelper.mCasecnt(StrFlag, Catchwt);
        mDbHelper.closeDatabase();

        if (Palcnt == null){
            Palcnt = "0";
        }if (TotPalcnt == null){
            TotPalcnt = "0";
        }if (CaseCount == null){
            CaseCount = "0";
        }if (TotCasecount == null){
            TotCasecount = "0";
        }

        TotPalcnt =  "Pallet Cnt: " + Palcnt + "/" + TotPalcnt;
        TotCasecount =  "Case Cnt: " + String.valueOf(Math.round(Double.valueOf(CaseCount))) + "/" + String.valueOf(Math.round(Double.valueOf(TotCasecount)));
        tvPalletcnt.setText(TotPalcnt);
        tvCasecnt.setText(TotCasecount);

        if (Double.valueOf(CaseCount) > 0.0){
            btnExport.setEnabled(true);
            btnHold.setEnabled(true);
            btnCancel.setEnabled(true);
        }

        adapter = new ReceiveTaskAdapter(ReceiveTaskActivity.this, receivetaskdetail);
        transList.setAdapter(adapter);




        transList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               /* if (resultScan){
                    edtLot_recTsk.requestFocus();
                    resultScan = false;
                }else {*/
                receivetaskdetail mReceivetaskdetail = (receivetaskdetail) adapter.getItem(position);
                Globals.gRTItem = mReceivetaskdetail.getitem().toString();
                Globals.gRTItmDesc = mReceivetaskdetail.getitmdesc().toString();
                Globals.gRTQtyrec = mReceivetaskdetail.gettqtyrec().toString();
                Globals.gRTTranline = mReceivetaskdetail.gettranlineno().toString();
                Globals.gRTTrancancel = mReceivetaskdetail.gettranlineno().toString();
                Globals.gRTPalline = 1;
                Globals.holdTask=Globals.gRTTaskNo;
                Globals.holdTranNum=Globals.gRTTranline;
                mSupporter.simpleNavigateTo(ReceiveTaskSaveActivity.class);
            }
        });


        edtLot_recTsk.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int ii, KeyEvent keyEvent) {
                if ((ii == EditorInfo.IME_ACTION_UNSPECIFIED) && (keyEvent != null) &&
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        return true;
                    }
                    ii = EditorInfo.IME_ACTION_DONE;
                }


                String palNo = edtLot_recTsk.getText().toString();


                mDbHelper.openReadableDatabase();
                Boolean result = mDbHelper.isPalNoAvailable(palNo,Globals.gRTTaskNo );
                mDbHelper.closeDatabase();

                if (palNo.equalsIgnoreCase("")) {
                    mToastMessage.showToast(ReceiveTaskActivity.this,
                            "Please Enter the Pallet.");
                    resultScan = true;
                    edtLot_recTsk.requestFocus();
                    return true;

                             /*   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*/

                }else if (!result){
                    mToastMessage.showToast(ReceiveTaskActivity.this,
                            "Pallet tag not found in PO");
                    resultScan = true;
                    edtLot_recTsk.setText("");
                    edtLot_recTsk.requestFocus();
                    return true;



                    //    resultScan = true;

                }else {

                    mDbHelper.openReadableDatabase();
                    receivetaskdetail = mDbHelper.getReceiveTaskDetail();
                    mDbHelper.closeDatabase();

                    for (int i=0;i<receivetaskdetail.size();i++){

                        if(receivetaskdetail.get(i).getPalno().equals(palNo)){

                            receivetaskdetail mReceivetaskdetail = (receivetaskdetail) adapter.getItem(i);
                            Globals.gRTItem = mReceivetaskdetail.getitem().toString();
                            Globals.gRTItmDesc = mReceivetaskdetail.getitmdesc().toString();
                            Globals.gRTQtyrec = mReceivetaskdetail.gettqtyrec().toString();
                            Globals.gRTTranline = mReceivetaskdetail.gettranlineno().toString();
                            Globals.gRTTrancancel = mReceivetaskdetail.gettranlineno().toString();
                            Globals.gRTPalline = 1;
                            Globals.holdTask=Globals.gRTTaskNo;
                            Globals.holdTranNum=Globals.gRTTranline;
                            mSupporter.simpleNavigateTo(ReceiveTaskSaveActivity.class);
                            break;
                        }

                    }
                    //  resultScan = true;

                }





                return false;
            }
        });



/*
        edtLot_recTsk.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:


                            String palNo = edtLot_recTsk.getText().toString();


                            mDbHelper.openReadableDatabase();
                            Boolean result = mDbHelper.isPalNoAvailable(palNo,Globals.gRTTaskNo );
                            mDbHelper.closeDatabase();

                            if (palNo.equalsIgnoreCase("")) {
                                mToastMessage.showToast(ReceiveTaskActivity.this,
                                        "Please Enter the Pallet.");
                                resultScan = true;
                                edtLot_recTsk.requestFocus();

                             */
/*   InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);*//*


                            }else if (!result){
                                mToastMessage.showToast(ReceiveTaskActivity.this,
                                        "Pallet tag not found in PO");
                                resultScan = true;
                                edtLot_recTsk.setText("");
                                edtLot_recTsk.requestFocus();



                            //    resultScan = true;

                            }else {

                                mDbHelper.openReadableDatabase();
                                receivetaskdetail = mDbHelper.getReceiveTaskDetail();
                                mDbHelper.closeDatabase();

                                for (int i=0;i<receivetaskdetail.size();i++){

                                    if(receivetaskdetail.get(i).getPalno().equals(palNo)){

                                        receivetaskdetail mReceivetaskdetail = (receivetaskdetail) adapter.getItem(i);
                                        Globals.gRTItem = mReceivetaskdetail.getitem().toString();
                                        Globals.gRTItmDesc = mReceivetaskdetail.getitmdesc().toString();
                                        Globals.gRTQtyrec = mReceivetaskdetail.gettqtyrec().toString();
                                        Globals.gRTTranline = mReceivetaskdetail.gettranlineno().toString();
                                        Globals.gRTTrancancel = mReceivetaskdetail.gettranlineno().toString();
                                        Globals.gRTPalline = 1;
                                        Globals.holdTask=Globals.gRTTaskNo;
                                        Globals.holdTranNum=Globals.gRTTranline;
                                        mSupporter.simpleNavigateTo(ReceiveTaskSaveActivity.class);
                                        break;
                                    }

                                }
                              //  resultScan = true;

                            }




                        default:
                            break;
                    }

                }

                return false;
            }
        });
*/



        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDbHelper.openReadableDatabase();
                Boolean Scaned = mDbHelper.getUnscanedCount(Globals.gRTTaskNo);
                mDbHelper.closeDatabase();

                if(isPartialEntered() || Scaned ){
                    exportPartial();
                } else {
                    Export();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlert();

            }
        });

        btnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDbHelper.openReadableDatabase();
                updatedQty = mDbHelper.getUpdatedQty(Globals.gRTTaskNo, Globals.gRTTranline);
                mDbHelper.closeDatabase();

                mDbHelper.openWritableDatabase();
                mDbHelper.updateReceiveTaskWHRPLTQTY(Globals.gRTTaskNo, Globals.gRTTranline, updatedQty);
                mDbHelper.closeDatabase();

                Globals.holdTaskNum=Globals.gRTTaskNo;
                Globals.holdTranNum=Globals.gRTTranline;
                Globals.ISHold=true;
                Globals.FROMHOLD = true;
                new UpdateReceiveStatus(mUsername,"ONHOLD").execute();

               /* if(isPartialEntered()){
                    new UpdateReceiveStatus(mUsername,"ONHOLD").execute();
                } else {
                    new UpdateReceiveStatus(mUsername,"COUNTED").execute();
                }*/

            }
        });
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void exportPartial() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Short Remaining Items/Quantities.");
        alertUser.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Export();
                    }
                });

        alertUser.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertUser.show();
    }

    public void Export()
    {
        if (mSupporter.isNetworkAvailable(ReceiveTaskActivity.this)) {
            mDbHelper.openWritableDatabase();
            mDbHelper.loadReceiveTaskExportData();
            mDbHelper.closeDatabase();
            /*new UpdateReceiveStatus(mUsername,"COUNTED").execute();*/
            ExportData();
        } else {
            mToastMessage.showToast(ReceiveTaskActivity.this,
                    "Unable to connect with Server. Please Check your internet connection");
        }
    }
    public Boolean isPartialEntered()
    {
        isPartial=false;
        mDbHelper.openReadableDatabase();
        treceivetaskdetail = mDbHelper.getCompletedRTTrans(StrFlag);
        mDbHelper.closeDatabase();
        if(treceivetaskdetail.size()!=0){
            for(int i=0;i<treceivetaskdetail.size();i++){

                Double tqtyinc = Double.valueOf(treceivetaskdetail.get(i).gettqtyinc());
                Double tqtyrec = Double.valueOf(treceivetaskdetail.get(i).gettqtyrec());
                Double trkqtyrec = Double.valueOf(treceivetaskdetail.get(i).gettrkqtyrec());
                String catchwt = treceivetaskdetail.get(i).getcatchwt();
                if (catchwt.equals("0")){
                    if(tqtyinc > tqtyrec){
                        isPartial=true;
                    }
                } else{
                    if(tqtyinc > trkqtyrec){
                        isPartial=true;
                    }
                }
            }
        }
        return isPartial;
    }
    public void ExportData() {
        exportTranList = new ArrayList<receivetaskexportdetail>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getReceiveTaskExportData();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        TranList = mDbHelper.getReceiveTaskLTExportData();
        mDbHelper.closeDatabase();

        if (exportTranList.size() != 0) {
            String exportXml = getRecordXmlExportPO(exportTranList, TranList);
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
        AlertDialog.Builder alertExit = new AlertDialog.Builder(ReceiveTaskActivity.this);
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
    public String getRecordXmlExportPO(List<receivetaskexportdetail> RTList, List<receivetaskloadtype> RTLTList) {
        String exportPODataXml = "";
        try {
            ExportReceiveTaskData RTData = new ExportReceiveTaskData();
            ExportReceiveTaskLoadData RTLTData = new ExportReceiveTaskLoadData();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "ReceiveTaskSaveData" + ">");
            sb.append("<" + "ReceiveTaskData" + ">");
            for (int i = 0; i < RTList.size(); i++) {
                RTData.writeRTXml(RTList.get(i), sb, ReceiveTaskActivity.this, mDbHelper);
            }
            sb.append("</" + "ReceiveTaskData" + ">");

            sb.append("<" + "ReceiveTaskLoadtypelist" + ">");
            for (int i = 0; i < RTLTList.size(); i++) {
                RTLTData.writeRTLTXml(RTLTList.get(i), sb, ReceiveTaskActivity.this, mDbHelper);
            }
            sb.append("</" + "ReceiveTaskLoadtypelist" + ">");
            sb.append("</" + "ReceiveTaskSaveData" + ">");

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
            mDialog = new ProgressDialog(ReceiveTaskActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "ReceiveTask" + ".xml");
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
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(ReceiveTaskActivity.this, "Error in exporting");
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
            dialog = new ProgressDialog(ReceiveTaskActivity.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_RECEIVETASK_DATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/ReceiveTask.xml");
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
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_EXPORT_RECEIVETASK_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "ReceiveTask" + ".xml");
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
                  // result ="false";

                } else if(resultString.toString().contains("<Result>false</Result>")){
                    result ="false";
                }else {
                    result = "error";
                }
                buf.close();

            } catch (SocketTimeoutException e) {
                result = "time out error";
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "ReceiveTask" + ".xml");
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
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteReceiveTaskLookupData();
                mDbHelper.closeDatabase();

                mDbHelper.getWritableDatabase();
                mDbHelper.DeleteReceiveSlot();
                mDbHelper.closeDatabase();

                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Data exported to Server successfully");
              //  mSupporter.simpleNavigateTo(MainmenuActivity.class);
                mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);
             //   new UpdateReceiveStatus(mUsername,"COUNTED").execute();

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "PO Updation failed");
            } /*else if (result.equalsIgnoreCase("time out error")){*/
            else if(result.equalsIgnoreCase("time out error")||result.equalsIgnoreCase("input error")){
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
               /* mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Time out.");*/
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ReceiveTaskActivity.this, "Unable to update Server");

                mDbHelper.openWritableDatabase();
                mDbHelper.DeletereceivetaskdetailDetail(Globals.gRTTaskNo,Globals.gRTTranline,"Y");
                mDbHelper.closeDatabase();

               // mToastMessage.showToast(ReceiveTaskActivity.this, "PO updated already");
            }else if (result.equalsIgnoreCase("false")) {
                mToastMessage.showToast(ReceiveTaskActivity.this, "Unable to update Server");

                mDbHelper.openWritableDatabase();
                mDbHelper.DeletereceivetaskdetailDetail(Globals.gRTTaskNo,Globals.gRTTranline,"Y");
                mDbHelper.closeDatabase();
            } else {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        result.toString());
            }

            dialog.cancel();
        }
    }

    class UpdateReceiveStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode,taskStatusUpdate;

        public UpdateReceiveStatus(String user,String status) {
            this.uCode = user;
            this.taskStatusUpdate = status;
            dialog = new ProgressDialog(ReceiveTaskActivity.this);
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

                if(Globals.FROMHOLD){

                    mDbHelper.openReadableDatabase();
                    receivetaskdetail = mDbHelper.selectReceiveTaskDetailForTran(Globals.gRTTaskNo);
                    receivetaskWHRPLTS = mDbHelper.selectReceiveTaskWHRPLTForTran(Globals.gRTTaskNo);
                    mDbHelper.closeDatabase();

                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateReceiveTaskStatus(Globals.gRTTaskNo,taskStatusUpdate);
                    mDbHelper.closeDatabase();

                    mDbHelper.openWritableDatabase();
                    mDbHelper.DeleteReceiveTaskTranDetailandWHRPLTS(Globals.gRTTaskNo);
                    mDbHelper.UpdateReceiveTaskTranDetail(receivetaskdetail);
                    mDbHelper.addReceiveTaskWHRPLTToTran(receivetaskWHRPLTS);
                    mDbHelper.closeDatabase();

                    mDbHelper.getWritableDatabase();
                    mDbHelper.DeleteReceiveSlot();
                    mDbHelper.closeDatabase();

                    Globals.FROMHOLD = false;
                }

                /*if(taskStatusUpdate.equalsIgnoreCase("COUNTED")){
                    ExportData();
                } else {

                }*/
                mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Failed to Hold");
            } else if(result.equalsIgnoreCase("time out error")||result.equalsIgnoreCase("input error")){
                //mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);
                new UpdateReceiveStatus(mUsername,taskStatusUpdate).execute();
            } else {
                mToastMessage.showToast(ReceiveTaskActivity.this, result);
            }
            dialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        cancelAlert();
    }

    public void printAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Print all pallet tags for this order.");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PDFCreate();
                    }
                });

        alertUser.setNegativeButton("No",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                       // isPrinttag="0";
                    }
                });

        alertUser.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_pallet_tags, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print_pallet_tages:

                if(mSupporter.isNetworkAvailable(ReceiveTaskActivity.this)){
                    PDFCreate();

                } else {
                    mToastMessage.showToast(ReceiveTaskActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
                break;
        }
        return true;
    }


    private void PDFCreate(){

        mDbHelper.openReadableDatabase();
        PrintLabelList = mDbHelper.getReceiveTaskPrintAllTag();
        mDbHelper.closeDatabase();
        new PrinterConnectOperation().execute();
    }


    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(ReceiveTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "failed";
            try {

                boolean isDataSentSuccess;
                isDataSentSuccess = DataToPrint();
                if (isDataSentSuccess) {
                    result = "success";
                    printPDF();
                }

//                for(int i = 0;i < PrintLabelList.size();i++) {
//                    recQty = PrintLabelList.get(i).gettqty();
//                    boolean isDataSentSuccess;
//                    isDataSentSuccess = DataToPrint();
//                    if (isDataSentSuccess) {
//                        result = "success";
//                        printPDF();
//                    }
//                }

                return result;

            } catch (Exception e) {

                Log.e("tag", "error", e);
                LogfileCreator.mAppendLog("In doInBackground method: "
                        + e.getMessage());
                result = "error";
                toasttext = "File Createion Failed";
            }

            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            if (result.equals("success")) {
                //  toasttext = "Print PDF creation Success ";
                isPrinttag = "1";
                boolean isDataSentSuccess = true;

            } else {
                //   toasttext = "Print PDF creation Failed";
                toasttext = "Print Failed";
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        toasttext);
                mSupporter.simpleNavigateTo(ReceiveTaskSaveActivity.class);

            }

        } // end of PostExecute method...

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating...");
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.dialog.setMessage(values[0]);
        }
    }


    private void printPDF(){
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(ReceiveTaskActivity.this, mPath);
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }catch (Exception ex){
            Log.e("RK",""+ex.getMessage());
            Toast.makeText(ReceiveTaskActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();

        }
    }

    public boolean DataToPrint(){
        boolean resultSent=false;
        try {
            createfile();
            document = new Document();
            document.setMargins(13, 3, 1, 1);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.setPageCount(PrintLabelList.size());
            document.open();
            cb = docWriter.getDirectContent();

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
                for(int i = 0;i < PrintLabelList.size();i++) {
                    recQty = PrintLabelList.get(i).gettqty();
                    recQty = recQty.split("\\.")[0];
                    receivetaskprintdetail printLabel = PrintLabelList.get(i);
                    pwlotno = printLabel.getlotrefid();
                    if (pwlotno == null) {
                        pwlotno = "";
                    }
                    Barcode128 barcode128 = new Barcode128();
                    barcode128.setCode(pwlotno);
                    barcode128.setFont(null);
                    barcode128.setX(3.0f);
                    barcode128.setCodeType(Barcode.CODE128);
                    Image code128Image = barcode128.createImageWithBarcode(cb, null, null);
                    code128Image.setAlignment(Image.ALIGN_CENTER);
                    code128Image.setWidthPercentage(80);
                    document.add(code128Image);

                    Paragraph reportDetail1 = new Paragraph();
                    reportDetail1.setFont(FONT_TABLE_CONTANT); //
                    writeDetail1(reportDetail1,printLabel);
                    document.add(reportDetail1);

                    Paragraph reportDetail2 = new Paragraph();
                    reportDetail2.setFont(FONT_BODY); //
                    writeDetail2(reportDetail2,printLabel);
                    document.add(reportDetail2);

                    Paragraph reportDetail3 = new Paragraph();
                    reportDetail3.setFont(FONT_TABLE_CONTANT); //
                    writeDetail3(reportDetail3,printLabel);
                    document.add(reportDetail3);
                    document.newPage();
                }
                document.close();
                resultSent = true;
            }else{
                mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }

        } catch (Exception e){
            resultSent = false;
        } finally {
            return resultSent;
        }
    }


    private void createfile() {
        try {

            File root_path = new File(Environment.getExternalStorageDirectory() + "/Android/WMS/PrintReport/");
            if (!root_path.exists()) {
                root_path.mkdirs();
            }
            mPath = (Environment.getExternalStorageDirectory() + "/Android/WMS/PrintReport/" + filename);
            pdfFile = new File(mPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail1(Paragraph reportBody,receivetaskprintdetail printLabel){
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
                pitem = printLabel.getitem();
                if (pitem == null){
                    pitem = "";
                }
                pitem = "Item " + pitem;

                cell = new PdfPCell(new Phrase(pitem, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                reportBody.add(table);
            }else{
                mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail2(Paragraph reportBody,receivetaskprintdetail printLabel){
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
                plotrefid = printLabel.getlotrefid();
                if (plotrefid == null){
                    plotrefid = "";
                }
                /* plotrefid = "Lot# " + plotrefid;*/
                plotrefid = "Pallet# " + plotrefid;

                precuser = printLabel.getrecuser();
                if (precuser == null){
                    precuser = "";
                }
                precuser = "Recvr " + precuser;

                precdate = printLabel.getrecdate();
                if (precdate == null){
                    precdate = "";
                }
                precdate = "Rec Date " + precdate;

                ptaskno = printLabel.gettaskno();
                if (ptaskno == null){
                    ptaskno = "";
                }
                ptaskno = "Task " + ptaskno;

                pexpdate = printLabel.getexpdate();
                if (pexpdate == null){
                    pexpdate = "";
                }
                pexpdate = "Exp Date " + pexpdate;

                if (printLabel.getcatchwt()=="0") {
                    pQty = recQty;
                    if (pQty == null) {
                        pQty = "";
                    }
                    pQty = "Qty " + pQty;
                } else{
                    pQty = recQty;
                    if (pQty == null) {
                        pQty = "";
                    }
                    pQty = "Qty " + pQty;
                }

                cell = new PdfPCell(new Phrase(plotrefid, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(precuser, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(precdate, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(ptaskno, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(pexpdate, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                Barcode128 code128 = new Barcode128();
                code128.setFont(null);
                code128.setCode(recQty);
                code128.setCodeType(Barcode128.CODE128);
                Image code128Image = code128.createImageWithBarcode(cb, null, null);
                code128Image.setWidthPercentage(15);
                cell = new PdfPCell();
                cell.addElement(new Phrase("Qty " + recQty));
                cell.addElement(code128Image);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                reportBody.add(table);

            }else{
                mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void writeDetail3(Paragraph reportBody,receivetaskprintdetail printLabel){
        try {

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
                pitmdesc = printLabel.getitmdesc();
                if (pitmdesc == null){
                    pitmdesc = "";
                }

                Paragraph childParagraph = new Paragraph(pitmdesc, FONT_TABLE_CONTANT);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                reportBody.add(childParagraph);
            }else{
                mToastMessage.showToast(ReceiveTaskActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                        taskStatus=Globals.gRTSTATUS;
                        mDbHelper.openReadableDatabase();
                        receivetaskdetail = mDbHelper.getReceiveTaskTranDetail(Globals.gRTTaskNo);
                        receivetaskWHRPLTS = mDbHelper.selectReceiveTaskTranWHRPLT(Globals.gRTTaskNo);
                        mDbHelper.closeDatabase();

                        mDbHelper.openWritableDatabase();
                        mDbHelper.UpdateReceiveTaskStatus(Globals.gRTTaskNo,taskStatus);
                        mDbHelper.closeDatabase();

                        mDbHelper.openWritableDatabase();
                        mDbHelper.DeleteReceiveTaskDetailandWHRPLT(Globals.gRTTaskNo);
                        mDbHelper.UpdateReceiveTaskDetailFromTran(receivetaskdetail);
                        mDbHelper.addReceiveTaskWHRPLTFromTran(receivetaskWHRPLTS);
                        mDbHelper.closeDatabase();

                        mDbHelper.getWritableDatabase();
                        mDbHelper.DeleteReceiveSlot();
                        mDbHelper.closeDatabase();

                        new UpdateReceiveStatus(mUsername,taskStatus).execute();


                /*        //new ReceiveTaskMenuActivity.LoadAllData(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();

                        mDbHelper.openWritableDatabase();
                        String oldQty = mDbHelper.getOldQty(Globals.gRTTaskNo, Globals.gRTTrancancel);
                        if(!Globals.gRTTrancancel.equalsIgnoreCase("")&&Globals.ISHold&&Globals.holdTask.equalsIgnoreCase(Globals.gRTTaskNo)
                                &&Globals.holdTranNum.equalsIgnoreCase(Globals.gRTTranline)){
                            mDbHelper.updateReceiveTaskQty(Globals.gRTTaskNo, Globals.gRTTrancancel,oldQty);
                            //mDbHelper.updateReceiveTaskWHRPLTQTY(Globals.gRTTaskNo,Globals.gRTTrancancel);
                            mDbHelper.cancelReceiveTaskWHRPLTQTY(Globals.gRTTaskNo,Globals.gRTTrancancel,oldQty);
                            //mDbHelper.deleteReceiveTaskCancel(Globals.gRTTrancancel);
                        }else if(!Globals.gRTTrancancel.equalsIgnoreCase("")&&Globals.ISHold==false&&Globals.holdTask.equalsIgnoreCase(Globals.gRTTaskNo)
                        &&Globals.holdTranNum.equalsIgnoreCase(Globals.gRTTranline)) {

                            mDbHelper.openReadableDatabase();
                            String firstQty = mDbHelper.getFirstQty(Globals.gRTTaskNo, Globals.gRTTranline);
                            mDbHelper.closeDatabase();
                            mDbHelper.openWritableDatabase();
                            mDbHelper.updateFirstQty(Globals.gRTTaskNo, Globals.gRTTrancancel,firstQty);
                            mDbHelper.cancelReceiveTaskWHRPLTONHOLD(Globals.gRTTaskNo, Globals.gRTTrancancel);
                            mDbHelper.closeDatabase();
                            Globals.ISHold=false;
                        }
                        //mDbHelper.updateReceiveTaskWHRPLTQTY(Globals.gRTTaskNo,Globals.gRTTrancancel);
                        mDbHelper.UpdateReceiveTaskStatus(Globals.gRTTaskNo,Globals.gRTSTATUS);
                        mDbHelper.closeDatabase();

                        new UpdateReceiveStatus(mUsername,Globals.gRTSTATUS).execute();

                        // new LoadAllData(mSessionId, Globals.gCompanyId, Globals.gRTTaskNo, mUsername, Globals.gLoctid).execute();

                      //  mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);*/


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
        private ProgressDialog dialog;
        private String pUsername, pSessionId, pCompId,pDeviceId ;


        String result = "";



        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId ) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


            dialog = new ProgressDialog(ReceiveTaskActivity.this);
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
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(ReceiveTaskActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
        //new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }


}