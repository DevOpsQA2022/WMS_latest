package com.silvercreek.wmspickingclient.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.silvercreek.wmspickingclient.model.SlotList;
import com.silvercreek.wmspickingclient.model.receivetaskWHMSLT;
import com.silvercreek.wmspickingclient.model.receivetaskWHRPLT;
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetaskprintdetail;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class ReceiveTaskSaveActivity extends AppCompatActivity {

    private EditText edtQty,edtGtin;
    private AutoCompleteTextView edtSLot;
    private Button btnSave, btnCancel;
    private ImageView imgBtn_lookup;
    private TextView tvItemDesc, tvPalletcnt, tvCasecnt, tvPrint , tvPalno,ActlQty,tvSlot;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mUsername,mDeviceId ="";
    private ListView slotList = null;



    private String strFlag="Y";
    String filename = "ReceiveTaskPalletLabel.pdf";
    private File pdfFile = null;
    String toasttext = "";
    private String mPath;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private Document document;
    private PdfContentByte cb;
    private PdfPTable table;
    private PdfWriter docWriter;
    private PdfPCell cell;

    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    private List<receivetaskdetail> receivetaskdetail;
    private List<com.silvercreek.wmspickingclient.model.SlotList> ReceiveSlotList;
    private List<receivetaskWHRPLT> receivetaskwhrplt;
    private ArrayList<receivetaskWHMSLT> SlotList;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    private SharedPreferences sharedpreferences;
    private String StrFlag="Y";
    private Boolean isPrinted=false;
    private int mTimeout;
    private double dAvailQty, dBalQty;
    String recQty = "";
    private List<String> mSlotList;
    private ArrayAdapter<String> adapter;
    private SlotListAdapter slotListAdapter;
    private String taskno="", tranlineno="", doctype="", docno="", doclineno="", item="", loctid="",
            wlotno="", umeasur="", wmsstat="", tqtyrec="", trkqtyrec="", revlev="", tqtyinc="", itmdesc="",
            pckdesc="", countryid="", itemShow="", collection="", welement="", widgetID="", catchwt="",
            lotrefid="", Linesplit="", Flag="", rowNo="";
    private String Palcnt, TotPalcnt, CaseCount, TotCasecount;
    private String isPrinttag = "0";
    private double tqtyInc= 0.0;
    private List<receivetaskprintdetail> PrintLabelList;
    private String pwlotno, plotrefid, pitem, precdate, pexpdate, precuser, ptaskno,
                     pQty, pitmdesc,pPalno,pSlot="";
    private String strGtin;
     private  SpannableStringBuilder ssBuilder;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_task_save);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();



        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();

        mCompany = Globals.gCompanyDatabase;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;

        tvItemDesc = (TextView) findViewById(R.id.tvItemDesc);
        tvPalletcnt = (TextView) findViewById(R.id.tvPallet);
        tvCasecnt = (TextView) findViewById(R.id.tvCase);
        tvPrint = (TextView) findViewById(R.id.tvPrinter);
        edtSLot = (AutoCompleteTextView) findViewById(R.id.edtSlot);
        edtQty = (EditText) findViewById(R.id.edtQty);
        edtGtin = (EditText)findViewById(R.id.edtGtin);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        imgBtn_lookup = (ImageView) findViewById(R.id.imgBtn_lookup);

        tvPalno = findViewById(R.id.tvPalno);
        tvSlot = findViewById(R.id.tvSlot);
        ActlQty = findViewById(R.id.ActlQty);

        String text = "Print Pallet";
         ssBuilder = new SpannableStringBuilder(text);


        // Initialize a new ClickableSpan to display red background
        ClickableSpan redClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // Do something
                /*if(isPrinttag.equals("0")) {*/
                    recQty = edtQty.getText().toString().trim();
                    if(!recQty.equals("")){

                        if(mSupporter.isNetworkAvailable(ReceiveTaskSaveActivity.this)){
                            printAlert();
                            edtSLot.requestFocus();
                        } else {
                            mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                                    "Unable to connect with Server. Please Check your internet connection");
                        }
                    } else{
                        mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                                "Enter valid qty");
                        edtQty.requestFocus();
                    }
             /*   }*/
            }
        };

        // Apply the clickable text to the span
        ssBuilder.setSpan(
                redClickableSpan, // Span to add
                text.indexOf("Print Pallet"), // Start of the span (inclusive)
                text.indexOf("Print Pallet") + String.valueOf("Print Pallet").length(), // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
        );
        // Display the spannable text to TextView
        tvPrint.setText(ssBuilder);

        tvPrint.setTextColor(ContextCompat.getColor(this, R.color.colorBlueNew));

        // Specify the TextView movement method
        tvPrint.setMovementMethod(LinkMovementMethod.getInstance());

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

        if (SOFT_KEYBOARD.equals("CHECKED")){
            edtQty.setShowSoftInputOnFocus(false);
            edtGtin.setShowSoftInputOnFocus(false);
            edtSLot.setShowSoftInputOnFocus(false);
        }else {
            edtQty.setShowSoftInputOnFocus(true);
            edtGtin.setShowSoftInputOnFocus(true);
            edtSLot.setShowSoftInputOnFocus(true);
        }

        mDbHelper.openReadableDatabase();
        receivetaskdetail = mDbHelper.selectReceiveTaskDetail(Globals.gRTItem );
        mDbHelper.closeDatabase();

        if (receivetaskdetail.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
            taskno=receivetaskdetail.get(0).gettaskno();
            tranlineno=receivetaskdetail.get(0).gettranlineno();
            doctype=receivetaskdetail.get(0).getdoctype();
            docno=receivetaskdetail.get(0).getdocno();
            doclineno=receivetaskdetail.get(0).getdoclineno();
            item=receivetaskdetail.get(0).getitem();
            loctid=receivetaskdetail.get(0).getloctid();
            wlotno=receivetaskdetail.get(0).getwlotno();
            umeasur=receivetaskdetail.get(0).getumeasur();
            wmsstat=receivetaskdetail.get(0).getwmsstat();
            tqtyrec=receivetaskdetail.get(0).gettqtyrec();
            trkqtyrec=receivetaskdetail.get(0).gettrkqtyrec();
            revlev=receivetaskdetail.get(0).getrevlev();
            tqtyinc=receivetaskdetail.get(0).gettqtyinc();
            itmdesc=receivetaskdetail.get(0).getitmdesc();
            pckdesc=receivetaskdetail.get(0).getpckdesc();
            countryid=receivetaskdetail.get(0).getcountryid();
            itemShow=receivetaskdetail.get(0).getitemShow();
            collection=receivetaskdetail.get(0).getcollection();
            welement=receivetaskdetail.get(0).getwelement();
            widgetID=receivetaskdetail.get(0).getwidgetID();
            catchwt=receivetaskdetail.get(0).getcatchwt();
            lotrefid=receivetaskdetail.get(0).getlotrefid();
            Linesplit=receivetaskdetail.get(0).getLinesplit();
            Flag=receivetaskdetail.get(0).getFlag();
            rowNo=receivetaskdetail.get(0).getrowNo();
            pPalno=receivetaskdetail.get(0).getPalno();
            pSlot=receivetaskdetail.get(0).getcollection();

            ReloadPalletDetail();

        } else{
            mToastMessage.showToast(ReceiveTaskSaveActivity.this, "No Data Found");
            LogfileCreator.mAppendLog("No data available in receivetaskdetail(ReceiveTaskSaveActivity)");
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        edtSLot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String mSlot = edtSLot.getText().toString().trim();

                            /*mDbHelper.openReadableDatabase();
                            Boolean isValidSlot = mDbHelper.isValidPTSlot(mSlot);
                            mDbHelper.closeDatabase();*/

                            mDbHelper.openReadableDatabase();
                            Boolean isValidSlot = mDbHelper.isValidItemSlot(Globals.gRTItem,mSlot);
                            mDbHelper.closeDatabase();

                            edtGtin.requestFocus();
                            if (!isValidSlot && !mSlot.equals("")) {
                                mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                                        "Invalid Slot");
                                edtSLot.postDelayed(new Runnable() {
                                    @Override
                                    public void run(){
                                        edtSLot.requestFocus();
                                    }
                                }, 10);

                            } else {
                                Globals.gRTSlot = mSlot;

                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        imgBtn_lookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                ReceiveSlotList = mDbHelper.selectReceiveSlotList(Globals.gRTItem ,Globals.gRTTaskNo);
                mDbHelper.closeDatabase();

                LayoutInflater li = LayoutInflater.from(ReceiveTaskSaveActivity.this);
                View promptsView = li.inflate(R.layout.receive_slot_list,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ReceiveTaskSaveActivity.this);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                slotList = (ListView) promptsView.findViewById(R.id.SlotList);
                slotListAdapter = new SlotListAdapter(ReceiveTaskSaveActivity.this, ReceiveSlotList);
                slotList.setAdapter(slotListAdapter);
                slotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        SlotList mSlotList = (SlotList) slotListAdapter.getItem(i);
                        Globals.LookUp_Slot = mSlotList.getSlot().toString();
                        alertDialog.dismiss();
                        edtSLot.setText(Globals.LookUp_Slot);
                        edtGtin.requestFocus();
                        edtGtin.requestFocus();


                    }
                });

                alertDialog.show();

            }
        });

      /*  mSlotList = getSlotList();

        adapter = new ArrayAdapter<String>(ReceiveTaskSaveActivity.this,android.R.layout.simple_list_item_1,mSlotList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                tv.getLayoutParams().height = 100;
                // Return the view
                return view;
            }
        };
        edtSLot.setAdapter(adapter);
        edtSLot.setThreshold(1);*/
/*
        edtSLot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                    long arg3) {

                String mSlot = edtSLot.getText().toString().trim();
                edtSLot.setText(mSlot);

                if (!mSlot.equals("")) {
                    //btnSave.setFocusableInTouchMode(true);
                    //btnSave.requestFocus();
                }
            }
        });*/

 /*       edtSLot.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ReceiveTaskSaveActivity.this.adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        edtQty.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN ) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            String Qty = "";
                            Double dQty = 0.0;

                            Qty = edtQty.getText().toString().trim();

                            if (!Qty.equalsIgnoreCase("")) {
                                dQty = Double.parseDouble(Qty);
                            } else {
                                dQty = Double.parseDouble("0");
                            }
                            if (dQty <= 0) {

                                mToastMessage.showToast(ReceiveTaskSaveActivity.this, "Please enter a valid qty");
                                edtQty.requestFocus();

                                edtQty.postDelayed(new Runnable() {
                                    @Override
                                    public void run(){
                                        edtQty.requestFocus();
                                    }
                                }, 10);


                            }
                            return true;
                        default:
                            break;
                    }
                }

               return false;
            }
        });






        edtGtin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            strGtin = edtGtin.getText().toString();

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((edtQty.getText().toString().equals("")) && (edtSLot.getText().toString().equals(""))){
                    mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
                } else {
                    cancelAlert();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Qty = "", Slot = "";
                Double dQty = 0.0;

                Slot = edtSLot.getText().toString();
                Qty = edtQty.getText().toString();
                strGtin = edtGtin.getText().toString();

                if (!Qty.equalsIgnoreCase("")) {
                    dQty = Double.parseDouble(Qty);
                } else {
                    dQty = Double.parseDouble("0");
                }

                if (Integer.valueOf(catchwt) == 0){
                    Globals.gCatchwt = "T";
                } else {
                    Globals.gCatchwt = "TRK";
                }


                /*mDbHelper.openReadableDatabase();
                Boolean isValidSlot = mDbHelper.isValidPTSlot(Slot);
                mDbHelper.closeDatabase();*/

                mDbHelper.openReadableDatabase();
                Boolean isValidSlot = mDbHelper.isValidItemSlot(Globals.gRTItem,Slot);
                mDbHelper.closeDatabase();



                if (dQty <= 0) {
                    mToastMessage.showToast(ReceiveTaskSaveActivity.this, "Please enter a valid qty");
                    edtQty.requestFocus();
                //} else if (!isValidSlot) {
                } else if (!isValidSlot && !Slot.equals("")) {
                    mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                            "Invalid Slot");
                } else {
                    mDbHelper.openReadableDatabase();
                    //String oldQty = mDbHelper.getOldQty(Globals.gRTTaskNo, Globals.gRTTrancancel);
                    dAvailQty = mDbHelper.getRTDetailQty(Globals.gRTTaskNo, Globals.gRTTranline);
                    mDbHelper.closeDatabase();

                    mDbHelper.openReadableDatabase();
                    dBalQty = mDbHelper.getBalanceQty(Globals.gRTTaskNo, Globals.gRTTranline);
                    mDbHelper.closeDatabase();

                    mDbHelper.openReadableDatabase();
                    strFlag = mDbHelper.getRTPalletFlag(Globals.gRTTaskNo, Globals.gRTTranline, Globals.gRTPalline);
                    mDbHelper.closeDatabase();

                    receivetaskWHRPLT treceivetaskWHRPLT = new receivetaskWHRPLT();

                    treceivetaskWHRPLT.settaskno(Globals.gRTTaskNo);
                    treceivetaskWHRPLT.settasklineno(Globals.gRTTranline);
                    treceivetaskWHRPLT.setpltstat("");
                    treceivetaskWHRPLT.setprtplttag("0");
                    treceivetaskWHRPLT.setSlot(Slot);
                    treceivetaskWHRPLT.setgTin(strGtin);

                    mDbHelper.openWritableDatabase();
                    mDbHelper.updateReceiveTaskWHRPLT(treceivetaskWHRPLT, Globals.gRTPalline, dQty, Globals.gRTTaskNo, Globals.gRTTranline, isPrinttag);
                    Globals.gRTPalline = Globals.gRTPalline + 1;
                    //Globals.gRTCount = Globals.gRTCount+1;
                    mDbHelper.updateReceiveTaskDetail(Globals.gRTTaskNo, Globals.gRTTranline, dQty,Slot);
                    mDbHelper.closeDatabase();




                    mToastMessage.showToast(ReceiveTaskSaveActivity.this, "Values updated successfully");
                    isPrinted=false;
                    mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);

                   /* if ((dBalQty > 0) && (dBalQty - dQty > 0) && (!strFlag.equals("Y"))) {
                        mDbHelper.openWritableDatabase();
                        mDbHelper.splitReceiveTaskWHRPLT(treceivetaskWHRPLT, Globals.gRTPalline + 1, dBalQty - dQty, "N",isPrinttag);
                        mDbHelper.closeDatabase();

                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateReceiveTaskWHRPLT(treceivetaskWHRPLT, Globals.gRTPalline, dQty, Globals.gRTTaskNo, Globals.gRTTranline, isPrinttag);
                        mDbHelper.closeDatabase();
                    } else if ((dBalQty > 0) && (!strFlag.equals("Y"))) {

                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateReceiveTaskWHRPLT(treceivetaskWHRPLT, Globals.gRTPalline, dQty, Globals.gRTTaskNo, Globals.gRTTranline, isPrinttag);
                        mDbHelper.closeDatabase();
                    } else {
                        if (!strFlag.equals("Y")) {
                            mDbHelper.openWritableDatabase();
                            mDbHelper.splitReceiveTaskWHRPLT(treceivetaskWHRPLT, Globals.gRTPalline, dQty, "Y", isPrinttag);
                            mDbHelper.closeDatabase();
                        }
                    }*/
                   /* Globals.gRTPalline = Globals.gRTPalline + 1;
                    if (!strFlag.equals("Y")) {
                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateReceiveTaskDetail(Globals.gRTTaskNo, Globals.gRTTranline, (dQty + dAvailQty));
                        mDbHelper.closeDatabase();
                    }*/
                   /* mToastMessage.showToast(ReceiveTaskSaveActivity.this, "Values updated successfully");
                    //ReloadPalletDetail();
                    isPrinted=false;
                    mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);*/
                }
            }
        });
    }

    private List<String> getSlotList() {
        List<String> mslotList = new ArrayList<String>();

        mDbHelper.openReadableDatabase();
        SlotList = mDbHelper.getSlotList();
        mDbHelper.closeDatabase();
        for (int i = 0; i < SlotList.size(); i++) {
            mslotList.add(SlotList.get(i).getSlot());
        }
        return mslotList;
    }

    private void ReloadPalletDetail() {
        Palcnt= ""; TotPalcnt=""; CaseCount=""; TotCasecount="";
        mDbHelper.openReadableDatabase();
        Palcnt = mDbHelper.mPalcnt(StrFlag);
        TotPalcnt = mDbHelper.mTotPalcnt();
        CaseCount = mDbHelper.mRTCasecnt(StrFlag, catchwt);
        TotCasecount = mDbHelper.mTotCasecnt(catchwt);
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

        String showPalcnt =  "Pallet Cnt: " + Palcnt + "/" + TotPalcnt;
        String showCasecount =  "Case Cnt: " + String.valueOf(Math.round(Double.valueOf(CaseCount))) + "/" + String.valueOf(Math.round(Double.valueOf(TotCasecount)));

        String itemDesc= item + " : " + itmdesc;
        tvItemDesc.setText(itemDesc);
        tvPalletcnt.setText(showPalcnt);
        tvCasecnt.setText(showCasecount);
        tvPalno.setText(pPalno);
        tvSlot.setText(pSlot);

        if(tqtyinc.equals("")){
            tqtyinc="0";
            // getQty="0.00000";
        }
        tqtyInc = Double.valueOf(tqtyinc);

        ActlQty.setText(String.valueOf(Math.round(tqtyInc)));
      //  edtQty.setText(tqtyinc);
        edtQty.setText(String.valueOf(Math.round(Double.valueOf(tqtyinc))));

        mDbHelper.openReadableDatabase();
        receivetaskwhrplt = mDbHelper.selectReceiveTaskWHRPLT();
        mDbHelper.closeDatabase();

        if (receivetaskwhrplt.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
            if (Integer.valueOf(catchwt) == 0) {
                edtQty.setText(String.valueOf(Math.round(Double.valueOf(receivetaskwhrplt.get(0).gettqty()))));
              //  edtQty.setText(receivetaskwhrplt.get(0).gettqty());
            } else {
             //   edtQty.setText(receivetaskwhrplt.get(0).gettrkqty());
                edtQty.setText(String.valueOf(Math.round(Double.valueOf(receivetaskwhrplt.get(0).gettrkqty()))));
            }
           // edtQty.setSelectAllOnFocus(true);
            edtSLot.setText(receivetaskwhrplt.get(0).getSlot());
            isPrinttag = receivetaskwhrplt.get(0).getprtplttag();
            edtGtin.setText(receivetaskwhrplt.get(0).getgTin());

            int palcount = Integer.valueOf(Palcnt);
            mDbHelper.openReadableDatabase();
            int TotPalcount = mDbHelper.mTotPalcount();
            mDbHelper.closeDatabase();
           // palcount = TotPalcount + Integer.valueOf(Globals.gRTPalline) - palcount;
            Palcnt = String.valueOf(palcount);
            TotPalcnt =  "Pallet Cnt: " + Palcnt + "/" + TotPalcnt;
            tvPalletcnt.setText(TotPalcnt);
            edtQty.setSelectAllOnFocus(true);
        } else {
            //edtQty.setText("");
            edtSLot.setText("");
            edtGtin.setText("");
            isPrinttag = "0";
            edtQty.requestFocus();
            edtQty.setSelectAllOnFocus(true);
        }
    }

    private void PDFCreate(){

        mDbHelper.openReadableDatabase();
        PrintLabelList = mDbHelper.getReceiveTaskPrint();
        mDbHelper.closeDatabase();
        new PrinterConnectOperation().execute();
    }

    public boolean DataToPrint(){
        boolean resultSent=false;
        try {
            createfile();
            document = new Document();
            document.setMargins(13, 3, 1, 1);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            cb = docWriter.getDirectContent();

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
                receivetaskprintdetail printLabel = PrintLabelList.get(0);
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
                writeDetail1(reportDetail1);
                document.add(reportDetail1);

                Paragraph reportDetail2 = new Paragraph();
                reportDetail2.setFont(FONT_BODY); //
                writeDetail2(reportDetail2);
                document.add(reportDetail2);

                Paragraph reportDetail3 = new Paragraph();
                reportDetail3.setFont(FONT_TABLE_CONTANT); //
                writeDetail3(reportDetail3);
                document.add(reportDetail3);
                document.close();
                resultSent = true;
            }else{
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,"No data available");
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

    public void writeDetail1(Paragraph reportBody){
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
            receivetaskprintdetail printLabel = PrintLabelList.get(0);
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
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail2(Paragraph reportBody){
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
            receivetaskprintdetail printLabel = PrintLabelList.get(0);
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
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail3(Paragraph reportBody){
        try {

            if(PrintLabelList.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
            receivetaskprintdetail printLabel = PrintLabelList.get(0);
            pitmdesc = printLabel.getitmdesc();
            if (pitmdesc == null){
                pitmdesc = "";
            }

            Paragraph childParagraph = new Paragraph(pitmdesc, FONT_TABLE_CONTANT);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            reportBody.add(childParagraph);
            }else{
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(ReceiveTaskSaveActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(ReceiveTaskSaveActivity.this);
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
             /*   mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                        toasttext);
                try {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                String storage = mPath;
                                File file = new File(storage);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file),"application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent = Intent.createChooser(target, "Open File");
                                startActivity(intent);

                            } catch (Exception e) {
                                mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                                        "No Application match to Open Print File");
                            }
                        }
                    }, 1000);

                } catch (Exception e) {
                    mToastMessage.showToast(ReceiveTaskSaveActivity.this, "Not Software Match to Open Print File");
                    mSupporter.simpleNavigateTo(MainmenuActivity.class);
                }*/

            } else {
             //   toasttext = "Print PDF creation Failed";
                toasttext = "Print Failed";
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,
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

    @Override
    public void onBackPressed() {

        if ((edtQty.getText().toString().equals("")) && (edtSLot.getText().toString().equals(""))){
            mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
        } else {
            cancelAlert();
        }
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
                        mSupporter.simpleNavigateTo(ReceiveTaskActivity.class);
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

    public void printAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to print?");
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
                        tvPrint.setHighlightColor(Color.TRANSPARENT);
                        isPrinttag="0";
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


            dialog = new ProgressDialog(ReceiveTaskSaveActivity.this);
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
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(ReceiveTaskSaveActivity.this,
                        "Unable to update server. Please save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
     //   new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }



    private void printPDF(){
        PrintManager printManager = (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(ReceiveTaskSaveActivity.this, mPath);
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());
        }catch (Exception ex){
            Log.e("RK",""+ex.getMessage());
            Toast.makeText(ReceiveTaskSaveActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();

        }
    }




}