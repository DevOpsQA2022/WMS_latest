package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.PickTaskScanPallet;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktaskheader;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportPickTask;
import com.silvercreek.wmspickingclient.xml.ExportPickTaskTempQty;

import org.apache.commons.io.FileUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PickTaskActivity extends AppBaseActivity {

    public static final String METHOD_EXPORT_DATA = "PickTask_SaveMain";
    public static final String METHOD_EXPORT_REVERTALLOC = "PickTask_RevertAlloc";
    public static final String METHOD_REVERT_DATA = "PickTask_RevertAlloc";
    public static final String METHOD_PICKTASK_TEMPALLOC = "PickTask_TempAlloc";
    public static final String METHOD_PUT_PICKTASK_UPDATE = "PickTask_StatusUpdate";
    public static final String METHOD_PICKTASK_SCANPALLET = "PickTask_ScanPallet";
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    /*private TextView tvRoute;*/
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";

    public Double cQTy = 0.0;
    public Double neWcQTy = 0.0;
    public double UpdQty = 0.0;
    public Double updtQty = 0.0;
    public Double orgQty = 0.0;
    public Double strtQty = 0.0;
    public Double difQty = 0.0;
    public Double upDqty = 0.0;
    public String isEdited = "N";
    public String updtPaltNo = "";
    public String eWlotNo = "";
    public String filename = "PickTaskPalletLabel.pdf";
    public String toasttext = "";
    double orgnal = 0.0;
    double orgQtyTQty = 0.0;
    double maxQty = 0.0;
    double artEdt_whqTy = 0.0, artEdt_rpAllocqty = 0.0, artEdt_tQty = 0.0;
    boolean BtnEdtTrue = false;
    boolean validationResult = false;
    private double firstQty = 0.0;
    private Double totalQty = 0.0;
    private Double tempRevertQty = 0.0;
    private Double whQty = 0.0;
    private Double icQty = 0.0;
    private Double rpAllocQTy = 0.0;
    private Double qtyUsed = 0.0;
    private Double totalQtY = 0.0;
    private Double toTalQtY = 0.0;
    private Double oldQty = 0.0;
    private double dSoQty = 0.0, dLotQty = 0.0;
    private Double dQty = 0.0;
    private double dAvailQty;
    private String PalletNumber = "", enteredQty, allocQtyForSave;
    private String mPath;
    private String StrFlag = "Y";
    private String uom = "", doctype = "", docno = "", doclineno = "", docstat = "", strweight = "", stkumid = "",
            orgdoclineno = "", volume = "", decnum = "", orgTranlineno = "", Lbshp = "", umfact = "", Tshipped = "", Trkshiped = "", LineSplit = "";
    private String ItemNo, strDesc;
    private String strTQty, strorgTQty;
    private String strTrkQty, strorgTrkQty;
    private String strCatchwt, strSlot, strLot, strwLotno;
    private String strTranlineNo;
    private String qtyfn = "";
    private String oPickedQty = "";
    private String edtlinpalletNo = "", edtlindescrip = "", edtLinitem = "", edtLinuom = "", edtTranLineNo = "", strEdtLineOrgQty = "", strEdtLineslot = "", oTqtyPicked = "", WlotNo = "";
    private String stop, trailer, route, dock, deldate, order, task, custid, custname, picker, palno;
    private String mPalno = "";
    private String mWlotno = "";
    private String mSessionId, mCompany, mUsername, mDeviceId = "";
    private String mLoctid = "";
    private String Getmsg = "";
    private String selectedItem = "";
    private String[] itemArray = {};
    private String subTranNo = "";
    private String stagingSlot = "";
    private String taskStatus = "";
    private String editQty = "";
    private String TotWeight = "", TotCasecount = "";
    private String weight = "";
    private String CaseCount = "";
    private String lotRefid = "";
    private int mTimeout = 0;
    private Integer pickDuration = 0;
    private int SubTranlineCount = 0;
    private Boolean isTaskCompleted;
    private Boolean isChangedAvail;
    private Boolean isTaskOnHold;
    private Boolean isItemAvailable;
    private Boolean isSameItem = false;
    private Boolean isAvailable = false;
    private Boolean isValidWlotno = false;
    private Boolean isProceed = true;
    private boolean isMoreQty = true;
    private Boolean scanResult = true;
    private ListView transList;
    private PickTaskDetailAdapter adapter;

    private EditText edtLot;
    private EditText edtlin_qty;
    private EditText edtStagingSlot;
    /*private EditText edtPallet;*/


    private TextView tvActivePallet, tvWeight, tvQty, tvCase;
    private TextView tvDesc;
    private TextView edtlin_Pallets;
    private TextView edtlin_itemDescrib, describ, edtlin_umeasur, edtlin_AvailQty;
    //private TextView,tvDesc txtWeight;

    private Button btnDone, btnOnHold, btnCancel;
    private Button BtnEdt;
    private Button cancel, edtSave;

    private File pdfFile = null;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private Document document;
    private PdfContentByte cb;
    private PdfPTable table;
    private PdfWriter docWriter;
    private PdfPCell cell;
    private SharedPreferences sharedpreferences;
    private File mImpOutputFile;

    private List<picktaskdetail> revertDataList;
    private List<String> mPalletList;
    private List<String> mLotList;
    private List<picktaskdetail> picktaskdetail;
    private List<picktaskdetail> picktaskdetailForvalidat;
    private List<picktaskdetail> picktaskEditdetail;
    private List<picktaskdetail> headerpicktaskdetail;
    private List<picktaskdetail> revertheaderpicktaskdetail;
    private List<picktaskdetail> subPickList;
    private List<picktaskWHMQTY> picktaskWHMQTYList;
    private List<picktaskheader> picktaskheader;
    private List<picktaskdetail> exportTranList;
    private List<picktaskdetail> exportTempAlloc;
    private List<picktaskPrintlabel> PrintLabelList;
    private List<PickTaskScanPallet> pickTaskScanPallet;

    private picktaskdetail tpicktaskdetail;
    private picktaskdetail referenceData;

    private ArrayList<picktaskWHIPTL> mPalletMast;
    private ArrayList<picktaskWHMQTY> mLotMast;
    private ArrayList<picktaskdetail> editPickDetail;

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_task);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        tvActivePallet = (TextView) findViewById(R.id.tvActivePallet);
        edtLot = (EditText) findViewById(R.id.edtLot);
        tvCase = (TextView) findViewById(R.id.tvCase);
        btnOnHold = (Button) findViewById(R.id.btn_Hold);
        btnCancel = (Button) findViewById(R.id.btn_CAncel);
        btnDone = (Button) findViewById(R.id.btn_save);
        BtnEdt = (Button) findViewById(R.id.BtnEdt);
        transList = (ListView) findViewById(R.id.lst_TransItems);

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
        Globals.isNewWlotno = false;
        picktasklist tpicktasklist = new picktasklist();
        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;

        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtLot.setShowSoftInputOnFocus(false);

        } else {
            edtLot.setShowSoftInputOnFocus(true);
        }

        edtLot.requestFocus();
        if (edtLot.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        mDbHelper.openReadableDatabase();
        picktaskdetail = mDbHelper.getPickTaskMenuDetail();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        picktaskheader = mDbHelper.getPickTaskHeader();
        mDbHelper.closeDatabase();

        mDbHelper.openWritableDatabase();
        mDbHelper.UpdateTaskStatus(tpicktasklist, Globals.gTaskNo);
        mDbHelper.closeDatabase();

        if (picktaskheader.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
            TotWeight = picktaskheader.get(0).getWeight();
            TotCasecount = picktaskheader.get(0).getCasecount();
        } else {
            LogfileCreator.mAppendLog("No data available in picktaskdetail(PickTaskActivity)");
        }

        mDbHelper.openReadableDatabase();
        weight = mDbHelper.mTotWeight(StrFlag);
        CaseCount = mDbHelper.mTotCase(StrFlag);
        mDbHelper.closeDatabase();

        if (Globals.gSetActivePallet) {
            tvActivePallet.setTextColor(Color.parseColor("#28F130"));
            tvActivePallet.setText("Active Pallet");
            edtLot.requestFocus();
        } else {
            mDbHelper.openWritableDatabase();
            mDbHelper.UpdateSlottype();
            mDbHelper.closeDatabase();
        }

        if (weight == null || weight.equals("")) {
            weight = "0";
        }
        if (TotWeight == null || TotWeight.equals("")) {
            TotWeight = "0";
        }
        if (CaseCount == null || CaseCount.equals("")) {
            CaseCount = "0";
        }
        if (TotCasecount == null || TotCasecount.equals("")) {
            TotCasecount = "0";
        }

        double dTWeight = Double.parseDouble(TotWeight);
        dTWeight = Math.ceil(dTWeight);
        DecimalFormat format = new DecimalFormat("0.##"); // Choose the number of decimal places to work with in case they are different than zero and zero value will be removed
        format.setRoundingMode(RoundingMode.DOWN); // choose your Rounding Mode
        TotWeight = format.format(dTWeight);

        double dWeight = Double.parseDouble(weight);
        dWeight = Math.ceil(dWeight);
        format.setRoundingMode(RoundingMode.DOWN);
        weight = format.format(dWeight);

        double dTotCasecount = Double.parseDouble(TotCasecount);
        dTotCasecount = Math.ceil(dTotCasecount);
        TotCasecount = format.format(dTotCasecount);

        TotWeight = weight + "/" + TotWeight;
        TotCasecount = CaseCount + "/" + TotCasecount;
        tvCase.setText(TotCasecount);

        adapter = new PickTaskDetailAdapter(PickTaskActivity.this, picktaskdetail);
        transList.setAdapter(adapter);

        BtnEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BtnEdtTrue = true;
                BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorLgtGreen)));
                BtnEdt.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });

        transList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (BtnEdtTrue || picktaskdetail.get(i).getChgQty().equals("Y")) {
                    picktaskdetail mPicktasklist = (picktaskdetail) adapter.getItem(i);
                    referenceData = mPicktasklist;
                    String qty = mPicktasklist.getTQty().toString();
                    cQTy = Double.valueOf(mDbHelper.DecimalFractionConversion(qty, decnum));
                    qtyfn = String.valueOf(Math.round(cQTy));
                    oPickedQty = mPicktasklist.getoTqtypicked().toString();
                    if (mPicktasklist.getLotNo() != null && !mPicktasklist.getLotNo().equals("") && !mPicktasklist.getDocstat().equals("X") && !mPicktasklist.getDocstat().equals("V")) {
                        edtlinpalletNo = mPicktasklist.getLotNo().toString();
                        edtlindescrip = mPicktasklist.getDescrip().toString();
                        edtLinitem = mPicktasklist.getItem().toString();
                        edtLinuom = mPicktasklist.getUom().toString();
                        edtTranLineNo = mPicktasklist.getTranlineno().toString();
                        strEdtLineOrgQty = mPicktasklist.getorgTQty().toString();
                        oTqtyPicked = mPicktasklist.getoTqtypicked().toString();
                        strEdtLineslot = mPicktasklist.getSlot().toString();
                        WlotNo = mPicktasklist.getWLotNo().toString();

                        new GetPickTaskEdtQty(mUsername).execute();
                    } else {
                        if (mPicktasklist.getDocstat().equals("X")) {
                            mToastMessage.showToast(PickTaskActivity.this, "Item already deleted");
                            BtnEdtTrue = false;
                            BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrgBtn)));
                            BtnEdt.setTextColor(Color.BLACK);
                        } else if (mPicktasklist.getDocstat().equals("V")) {
                            mToastMessage.showToast(PickTaskActivity.this, "Item already void");
                            BtnEdtTrue = false;
                            BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrgBtn)));
                            BtnEdt.setTextColor(Color.BLACK);
                        } else {
                            mToastMessage.showToast(PickTaskActivity.this, "Please Scan Pallet");
                            BtnEdtTrue = false;
                            BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrgBtn)));
                            BtnEdt.setTextColor(Color.BLACK);
                        }
                    }
                }
            }
        });

        mDbHelper.openReadableDatabase();
        isTaskCompleted = mDbHelper.isTaskCompleted();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        isChangedAvail = mDbHelper.isChangedAvail();
        mDbHelper.closeDatabase();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        edtLot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrgBtn)));
                            BtnEdt.setTextColor(Color.BLACK);

                            mDbHelper.getWritableDatabase();
                            mDbHelper.DeletePickTaskScanPallet();
                            mDbHelper.closeDatabase();

                            mWlotno = edtLot.getText().toString().trim();

                            if (mWlotno.equalsIgnoreCase("")) {
                                mToastMessage.showToast(PickTaskActivity.this,
                                        "Please Enter the Pallet.");
                                scanResult = true;
                                edtLot.requestFocus();

                            } else {
                                new GetPickTaskScanPallet(mUsername).execute();
                            }
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Globals.FROMBTNDONE = true;
                if (picktaskdetail.size() < 0) {

                    mToastMessage.showToast(PickTaskActivity.this,
                            "No Data available to complete the Task");
                    Globals.FROMBTNDONE = false;

                } else if (!isTaskCompleted && !isChangedAvail) {

                    // taskStatus = "COMPLETE";
                    taskStatus = "PICKED";
                    Globals.editItemNum = "";
                    new UpdatePickStatus(mUsername, taskStatus).execute();

                } else {

                    mToastMessage.showToast(PickTaskActivity.this,
                            "Items are pending to Scan Qty");
                }
            }
        });

        btnOnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (picktaskdetail.size() > 0) {

                    Globals.holdTaskNum = Globals.gTaskNo;
                    taskStatus = "ONHOLD";
                    new UpdatePickStatus(mUsername, taskStatus).execute();
                } else {
                    mToastMessage.showToast(PickTaskActivity.this,
                            "No Data available to Hold");
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlert();
            }
        });
    }

    private void Saveprocess() {

        String Qty = "", Slot = "", flag = "", item = "", desc = "";
        dQty = 0.0;
        Double diffQty = 0.0;
        isProceed = true;

        if (pickTaskScanPallet.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
            Slot = pickTaskScanPallet.get(0).getPtsP_slot();
            Qty = String.valueOf(Double.valueOf(pickTaskScanPallet.get(0).getPtsP_tqty()) - Double.valueOf(pickTaskScanPallet.get(0).getPtsP_rpallocqty()));
            Globals.gDecnum = decnum;

            if (!Qty.equalsIgnoreCase("")) {
                if (updtQty > 0) {
                    dQty = updtQty;
                } else {
                    dQty = Double.parseDouble(Qty);
                }
            } else {
                dQty = Double.parseDouble("0");
            }
            if (Integer.valueOf(strCatchwt) == 0) {
                if (!strTQty.equalsIgnoreCase("")) {
                    dAvailQty = Double.parseDouble(strorgTQty);
                } else {
                    dAvailQty = Double.parseDouble("0");
                }
                Globals.gCatchwt = "T";
            } else {
                if (!strTrkQty.equalsIgnoreCase("")) {
                    dAvailQty = Double.parseDouble(strTrkQty);
                } else {
                    dAvailQty = Double.parseDouble("0");
                }
                Globals.gCatchwt = "TRK";
            }
            if (dQty > dAvailQty) {
                diffQty = dQty - dAvailQty;
                Globals.editItemNum = ItemNo;
                Globals.editTranNum = strTranlineNo;
                Globals.editSlot = strSlot;
                Globals.editDiffQty = diffQty;
                Globals.editQty1 = dAvailQty;
                Globals.editQty2 = dQty;
                Globals.holdTaskNum = Globals.gTaskNo;
                flag = "H";
                mDbHelper.openWritableDatabase();
                mDbHelper.updateEditFlag();
                mDbHelper.closeDatabase();
            } else {
                mDbHelper.openReadableDatabase();
                editPickDetail = mDbHelper.getEditPickTaskDetail();
                mDbHelper.closeDatabase();
                if (editPickDetail.size() > 0) {
                    mDbHelper.openWritableDatabase();
                    mDbHelper.updateEditFlag();
                    mDbHelper.closeDatabase();
                }
                Globals.editItemNum = "";
                Globals.editTranNum = "";
                Globals.editSlot = "";
                Globals.editDiffQty = 0.0;
                Globals.editQty1 = 0.0;
                Globals.editQty2 = 0.0;
                flag = "Y";
            }
            mDbHelper.openReadableDatabase();
            Boolean isValidSlot = mDbHelper.isValidSlot(Slot);
            mDbHelper.closeDatabase();

            if (dQty <= 0) {
                mToastMessage.showToast(PickTaskActivity.this, "Please Enter a Valid Qty");
            } /*else if (dQty > dAvailQty) {
                 qtyAlert();
            }*/ /*else if (!isValidSlot) {
                mToastMessage.showToast(PickTaskActivity.this,
                    "Invalid Slot");
            }*/ else {
                tpicktaskdetail = new picktaskdetail();
                tpicktaskdetail.setItem(ItemNo);
                tpicktaskdetail.setDescrip(strDesc);
                tpicktaskdetail.setSlot(Slot);
                tpicktaskdetail.setTQty(strTQty);
                tpicktaskdetail.setorgTQty(strorgTQty);
                tpicktaskdetail.setTrkQty(strTrkQty);
                tpicktaskdetail.setorgTrkQty(strorgTrkQty);
                tpicktaskdetail.setUom(uom);
                tpicktaskdetail.setWLotNo(strwLotno);
                tpicktaskdetail.setorgTranlineno(orgTranlineno);
                tpicktaskdetail.setDoctype(doctype);
                tpicktaskdetail.setDocno(docno);
                tpicktaskdetail.setDoclineno(doclineno);
                tpicktaskdetail.setorgDoclineno(orgdoclineno);
                tpicktaskdetail.setDocstat(docstat);
                tpicktaskdetail.setWeight(strweight);
                tpicktaskdetail.setStkumid(stkumid);
                tpicktaskdetail.setCatchwt(strCatchwt);
                tpicktaskdetail.setVolume(volume);
                tpicktaskdetail.setdecnum(Globals.gDecnum);
                tpicktaskdetail.setUmfact(umfact);
                tpicktaskdetail.setTshipped(Tshipped);
                tpicktaskdetail.setTrkshipped(Trkshiped);
                tpicktaskdetail.setLbshp(Lbshp);
                tpicktaskdetail.setFlag(flag);
                tpicktaskdetail.setSlot("");

                mDbHelper.openReadableDatabase();
                Globals.gLotno = pickTaskScanPallet.get(0).getPtsP_lotrefid();
                mDbHelper.closeDatabase();

                Globals.gEndPickDuration = System.currentTimeMillis();
                pickDuration = (int) ((Globals.gEndPickDuration - Globals.gStartPickDuration) / 1000);

                tpicktaskdetail.setpickDuration(pickDuration);
                tpicktaskdetail.setDetailsTaskNum(Globals.gTaskNo);
                tpicktaskdetail.setLotNo(Globals.gLotno);
                tpicktaskdetail.setWLotNo(Globals.gPickTaskWlotno);
                tpicktaskdetail.setSlot(Slot);
                tpicktaskdetail.setStagingSlot(stagingSlot);
                tpicktaskdetail.setSubItem("0.0");
                tpicktaskdetail.setSubTranNo(String.valueOf(dAvailQty));
                tpicktaskdetail.setLinesplit(LineSplit);

                if (LineSplit.equals("1")) {
                    mDbHelper.openReadableDatabase();
                    isSameItem = mDbHelper.isSameItem(Globals.gPickTaskWlotno, orgTranlineno);
                    mDbHelper.closeDatabase();
                }

                if (isSameItem) {
                    mDbHelper.openReadableDatabase();
                    String PickedQty = mDbHelper.PickedQty(Globals.gCatchwt, orgTranlineno);
                    mDbHelper.closeDatabase();
                    Double dPickedQty = Double.parseDouble(PickedQty);
                    mDbHelper.openWritableDatabase();
                    String sQty = String.valueOf(dPickedQty + dQty);
                    String cQty = mDbHelper.DecimalFractionConversion(sQty, decnum);
                    dQty = Double.valueOf(cQty);
                    mDbHelper.updatePickTaskDetail(tpicktaskdetail, item, orgTranlineno, orgdoclineno, (dQty), isEdited); //updPicktask
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.deleteSelectedPickTaskDetail(strTranlineNo);
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateTranlineno(strTranlineNo);
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateRowno();
                    mDbHelper.closeDatabase();
                } else {
                    String sQty = String.valueOf(dQty);
                    String cQty = mDbHelper.DecimalFractionConversion(sQty, decnum);
                    dQty = Double.valueOf(cQty);

                    mDbHelper.openWritableDatabase();
                    mDbHelper.updatePickTaskDetail(tpicktaskdetail, ItemNo, strTranlineNo, doclineno, dQty, isEdited); //updPicktask
                    mDbHelper.closeDatabase();

                    double TempALloc = dQty;
                    new ExportTempAlloc(mSessionId, Globals.gUsercode, Globals.gCompanyId, strLot, strwLotno, ItemNo, String.valueOf(TempALloc), String.valueOf(dQty), strTranlineNo, pickTaskScanPallet.get(0).getPtsP_slot()).execute();
                }
                if (dQty < dAvailQty) {
                    Double splitQty = dAvailQty - dQty;
                    Globals.gLineSplit = "1";
                    SplitDetailLine(tpicktaskdetail, splitQty);
                }
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        } else {
            mToastMessage.showToast(PickTaskActivity.this, "No data available"); // Check NULL 16.8.22
            LogfileCreator.mAppendLog("No data available in pickTaskScanPallet(PickTaskActivity)");
        }
    }

    private void greaterQtySaveProcess() {
        if (headerpicktaskdetail.size() > 0 && pickTaskScanPallet.size() > 0) {     //SCS CIRK 2022/07/25 CT69649C:
            ItemNo = headerpicktaskdetail.get(0).getItem();
            strDesc = headerpicktaskdetail.get(0).getDescrip();
            strTQty = headerpicktaskdetail.get(0).getTQty();
            strorgTQty = headerpicktaskdetail.get(0).getorgTQty();
            strTrkQty = headerpicktaskdetail.get(0).getTrkQty();
            strorgTrkQty = headerpicktaskdetail.get(0).getorgTrkQty();
            strCatchwt = headerpicktaskdetail.get(0).getCatchwt();
            strSlot = headerpicktaskdetail.get(0).getSlot();
            strwLotno = headerpicktaskdetail.get(0).getWLotNo();
            strLot = headerpicktaskdetail.get(0).getLotNo();
            strTranlineNo = headerpicktaskdetail.get(0).getTranlineno();
            orgTranlineno = headerpicktaskdetail.get(0).getorgTranlineno();
            uom = headerpicktaskdetail.get(0).getUom();
            doctype = headerpicktaskdetail.get(0).getDoctype();
            docno = headerpicktaskdetail.get(0).getDocno();
            doclineno = headerpicktaskdetail.get(0).getDoclineno();
            orgdoclineno = headerpicktaskdetail.get(0).getorgDoclineno();
            docstat = headerpicktaskdetail.get(0).getDocstat();
            strweight = headerpicktaskdetail.get(0).getWeight();
            volume = headerpicktaskdetail.get(0).getVolume();
            decnum = headerpicktaskdetail.get(0).getdecnum();
            stkumid = headerpicktaskdetail.get(0).getStkumid();
            umfact = headerpicktaskdetail.get(0).getUmfact();
            Tshipped = headerpicktaskdetail.get(0).getTshipped();
            Trkshiped = headerpicktaskdetail.get(0).getTrkshipped();
            Lbshp = headerpicktaskdetail.get(0).getLbshp();
            LineSplit = headerpicktaskdetail.get(0).getLinesplit();
        } else {
            mToastMessage.showToast(PickTaskActivity.this,
                    "Invalid Pallet");
        }
    }

    @Override
    public void onBackPressed() {
        cancelAlert();
    }

    public boolean validateDate() {
        String allocQty = "";
        boolean result = true;
        enteredQty = String.valueOf(Math.round(neWcQTy));

        if (pickTaskScanPallet.size() > 0) {
            //SCS CIRK 2022/07/25 CT69649C:
            totalQty = Double.valueOf(pickTaskScanPallet.get(0).getPtsP_tqty()) - Double.valueOf(pickTaskScanPallet.get(0).getPtsP_rpallocqty());
            qtyUsed = Double.valueOf(pickTaskScanPallet.get(0).getPtsP_tqty());
            whQty = Double.valueOf(pickTaskScanPallet.get(0).getPtsP_whqty());
            icQty = Double.valueOf(pickTaskScanPallet.get(0).getPtsP_icqty());
            rpAllocQTy = Double.valueOf(pickTaskScanPallet.get(0).getPtsP_rpallocqty());
        } else {
            mToastMessage.showToast(PickTaskActivity.this, "No data available");
            LogfileCreator.mAppendLog("No data available in pickTaskScanPallet(PickTaskActivity)");
        }
        if (qtyUsed <= 0) {
            mToastMessage.showToast(PickTaskActivity.this,
                    "No Qty available on the " + mWlotno + " pallet");
            edtLot.setText("");
            edtLot.requestFocus();
            result = false;
        } else if (totalQty <= 0) {
            mToastMessage.showToast(PickTaskActivity.this,
                    "Qty less allocation insufficient on the " + mWlotno + "  pallet");
            edtLot.setText("");
            edtLot.requestFocus();
            result = false;
        } else if (whQty < totalQty) {
            mToastMessage.showToast(PickTaskActivity.this,
                    "Insufficient quantity in WH Quantity table for the Pallet " + mWlotno);
            edtLot.setText("");
            edtLot.requestFocus();
            result = false;
        } else if (icQty < totalQty) {
            mToastMessage.showToast(PickTaskActivity.this,
                    "Insufficient quantity in IC Quantity table for the Pallet " + mWlotno);
            edtLot.setText("");
            edtLot.requestFocus();
            result = false;
        }
        return result;
    }

    private void subAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickTaskActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Item is not on the sales order.Is this a substitution?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listViewAlert();
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

    private void edtLineAlert() {
        if (dLotQty > dSoQty) {
            AlertDialog.Builder alertUser = new AlertDialog.Builder(PickTaskActivity.this);
            alertUser.setTitle("Confirmation");
            alertUser.setIcon(R.drawable.warning);
            alertUser.setCancelable(false);
            alertUser.setMessage("Do you want to Scan more qty than Ordered?");
            alertUser.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            greaterQtySaveProcess();
                        }
                    });

            alertUser.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edtLot.setText("");
                            edtLot.requestFocus();
                            dialog.cancel();
                        }
                    });

            alertUser.show();

        }
    }

    private void qtyAlert() {
        if (dLotQty > dSoQty) {
            AlertDialog.Builder alertUser = new AlertDialog.Builder(PickTaskActivity.this);
            alertUser.setTitle("Confirmation");
            alertUser.setIcon(R.drawable.warning);
            alertUser.setCancelable(false);
            alertUser.setMessage("Confirm picked qty");
            alertUser.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            alertUser.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            edtLot.setText("");
                            edtLot.requestFocus();
                            dialog.cancel();
                        }
                    });

            alertUser.show();

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
                        Globals.FROMBTNCANCEL = true;
                        taskStatus = Globals.gStatus;
                        new UpdatePickStatus(mUsername, taskStatus).execute();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_pick_task_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.print_pick_task_item:

                if (mSupporter.isNetworkAvailable(PickTaskActivity.this)) {
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateFromPickTaskpallet();
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateFromPickTaskdetail();
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateFromPickTaskWHITRLS();
                    mDbHelper.closeDatabase();
                    PDFCreate();

                } else {
                    mToastMessage.showToast(PickTaskActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
                break;
        }
        return true;
    }

    // to get Pallet List
    public List<String> getPalletList() {
        List<String> PalletList = new ArrayList<String>();

        mDbHelper.openReadableDatabase();
        mPalletMast = mDbHelper.getPalletList();
        mDbHelper.closeDatabase();
        for (int i = 0; i < mPalletMast.size(); i++) {
            PalletList.add(mPalletMast.get(i).getPalno());
        }
        return PalletList;
    }

    // to get Lot List
    public List<String> getLotList() {
        List<String> LotList = new ArrayList<String>();

        mDbHelper.openReadableDatabase();
        //mLotMast = mDbHelper.getLotList();
        mLotMast = mDbHelper.getWHMQTYLotList();
        mDbHelper.closeDatabase();
        for (int i = 0; i < mLotMast.size(); i++) {
            LotList.add(mLotMast.get(i).getWlotno());
        }
        return LotList;
    }

    public String getLotItemList(String lotno) {
        String lotItem = "";

        mDbHelper.openReadableDatabase();
        lotItem = mDbHelper.getLotItemList(lotno);
        mDbHelper.closeDatabase();
        return lotItem;
    }

    private void PDFCreate() {

        mDbHelper.openReadableDatabase();
        PrintLabelList = mDbHelper.getPickTaskPrintLabel();
        mDbHelper.closeDatabase();
        new PrinterConnectOperation().execute();
    }

    public boolean DataToPrint() {
        boolean resultSent = false;
        try {
            createfile();
            document = new Document();
            document.setMargins(13, 3, 1, 1);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            cb = docWriter.getDirectContent();
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                picktaskPrintlabel printLabel = PrintLabelList.get(0);
                palno = printLabel.getPalno();
                Barcode128 barcode128 = new Barcode128();
                barcode128.setCode(palno);
                barcode128.setFont(null);
                barcode128.setCodeType(Barcode.CODE128);
                Image code128Image = barcode128.createImageWithBarcode(cb, null, null);
                code128Image.setAlignment(Image.ALIGN_CENTER);
                code128Image.setWidthPercentage(50);
                document.add(code128Image);
            } else {
                mToastMessage.showToast(PickTaskActivity.this, "No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskActivity)");
            }

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

        } catch (Exception e) {
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


    public void writeDetail1(Paragraph reportBody) {
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                picktaskPrintlabel printLabel = PrintLabelList.get(0);
                stop = printLabel.getStop();
                if (stop == null) {
                    stop = "";
                }
                stop = "Stop " + stop;

                trailer = printLabel.getTrailer();
                if (trailer == null) {
                    trailer = "";
                }
                trailer = "Trailer " + trailer;

                cell = new PdfPCell(new Phrase(stop, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            } else {
                mToastMessage.showToast(PickTaskActivity.this, "No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskActivity)");
            }
            cell = new PdfPCell(new Phrase(trailer, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            reportBody.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail2(Paragraph reportBody) {
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                picktaskPrintlabel printLabel = PrintLabelList.get(0);
                route = printLabel.getRoute();
                if (route == null) {
                    route = "";
                }
                route = "Route " + route;

                dock = printLabel.getDock();
                if (dock == null) {
                    dock = "";
                }
                dock = "Dock " + dock;

                deldate = printLabel.getDeldate();
                if (deldate == null) {
                    deldate = "";
                }
                deldate = "Del Date " + deldate;

                order = printLabel.getOrderno();
                if (order == null) {
                    order = "";
                }
                order = "Order # " + order;

                task = printLabel.getTaskno();
                if (task == null) {
                    task = "";
                }
                task = "Task #" + task;

                custid = printLabel.getCustid();
                if (custid == null) {
                    custid = "";
                }
                custid = "Cust #" + custid;

                picker = printLabel.getPicker();
                if (picker == null) {
                    picker = "";
                }
                picker = "Picker " + picker;

                cell = new PdfPCell(new Phrase(route, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            } else {
                mToastMessage.showToast(PickTaskActivity.this, "No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskActivity)");
            }


            cell = new PdfPCell(new Phrase(dock, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(deldate, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase("01 of 01", FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(order, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(task, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(custid, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(picker, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            reportBody.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail3(Paragraph reportBody) {
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                picktaskPrintlabel printLabel = PrintLabelList.get(0);
                custname = printLabel.getCustname();
                if (custname == null) {
                    custname = "";
                }

                Paragraph childParagraph = new Paragraph(custname, FONT_TABLE_CONTANT);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                reportBody.add(childParagraph);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SplitDetailLine(picktaskdetail tpicktaskdetail, double Qty) {

        mDbHelper.openReadableDatabase();
        int TranlineCount = mDbHelper.mTranlineCount();
        mDbHelper.closeDatabase();
        mDbHelper.openReadableDatabase();
        int DoclineCount = mDbHelper.getmDoclineCount(tpicktaskdetail.getDocno());
        mDbHelper.closeDatabase();
        mDbHelper.openReadableDatabase();
        int rowNo = mDbHelper.mRowNoCount();
        mDbHelper.closeDatabase();

        mDbHelper.openWritableDatabase();
        String sQty = String.valueOf(Qty);
        String cQty = mDbHelper.DecimalFractionConversion(sQty, decnum);
        Globals.gTqty = Double.valueOf(cQty);
        Globals.gTranlineno = TranlineCount + 1;
        Globals.gDoclineno = DoclineCount + 1;
        Globals.gPTDetailRowCount = rowNo + 1;

        mDbHelper.SplitNewLine(tpicktaskdetail, Globals.gTranlineno, Globals.gDoclineno, Globals.gPTDetailRowCount, Globals.gTqty);
        mDbHelper.closeDatabase();
    }

    private String GetErrorMessage() {

        String GetErrMsg = "";
        try {
            //creating a constructor of file class and parsing an XML file
            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "SubPickTaskList" + ".xml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(mImpOutputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Acknowledgement");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element eElement = (org.w3c.dom.Element) node;
                    GetErrMsg = eElement.getElementsByTagName("ErrorMessage").item(0).getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorCode = "Err501";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage());
            String result = "Invalid File";
            return result;
        }
        return GetErrMsg;
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

    private void setViewsData(String DocNo) {

        mDbHelper.openReadableDatabase();
        headerpicktaskdetail = mDbHelper.selectPickTaskDetail(Globals.gPickTaskItem);
        SubTranlineCount = mDbHelper.mTranlineCount();
        int DoclineCount = mDbHelper.getmDoclineCount(DocNo);
        subTranNo = mDbHelper.getSubTranNo(selectedItem);
        subPickList = mDbHelper.getPickTaskforSub(selectedItem);
        mDbHelper.closeDatabase();

        Globals.gTranlineno = 9999;
        Globals.gDoclineno = DoclineCount + 1;
        if (subPickList.size() > 0) {      //SCS CIRK 2022/07/25 CT69649C:
            mDbHelper.getWritableDatabase();
            mDbHelper.updateTranPickDetails(Globals.gPickTaskItem, String.valueOf(Globals.gTranlineno),
                    String.valueOf(Globals.gDoclineno), selectedItem, subTranNo, subPickList.get(0).getDocno(), subPickList.get(0).getDoctype());
            mDbHelper.closeDatabase();
        } else {
            mToastMessage.showToast(PickTaskActivity.this, "No data available");
            LogfileCreator.mAppendLog("No data available in subPickList(PickTaskActivity)");
        }
        if (headerpicktaskdetail.size() > 0) {      //SCS CIRK 2022/07/25 CT69649C:
            ItemNo = headerpicktaskdetail.get(0).getItem();
            strDesc = headerpicktaskdetail.get(0).getDescrip();
            strTQty = headerpicktaskdetail.get(0).getTQty();
            strorgTQty = headerpicktaskdetail.get(0).getorgTQty();
            strTrkQty = headerpicktaskdetail.get(0).getTrkQty();
            strorgTrkQty = headerpicktaskdetail.get(0).getorgTrkQty();
            strCatchwt = "0";
            strSlot = headerpicktaskdetail.get(0).getSlot();
            strwLotno = headerpicktaskdetail.get(0).getWLotNo();
            strLot = headerpicktaskdetail.get(0).getLotNo();
            strTranlineNo = String.valueOf(Globals.gTranlineno);
            orgTranlineno = headerpicktaskdetail.get(0).getorgTranlineno();
            uom = headerpicktaskdetail.get(0).getUom();
            doctype = headerpicktaskdetail.get(0).getDoctype();
            docno = headerpicktaskdetail.get(0).getDocno();
            doclineno = String.valueOf(Globals.gDoclineno);
            orgdoclineno = headerpicktaskdetail.get(0).getorgDoclineno();
            docstat = headerpicktaskdetail.get(0).getDocstat();
            strweight = headerpicktaskdetail.get(0).getWeight();
            volume = headerpicktaskdetail.get(0).getVolume();
            decnum = headerpicktaskdetail.get(0).getdecnum();
            stkumid = headerpicktaskdetail.get(0).getStkumid();
            umfact = headerpicktaskdetail.get(0).getUmfact();
            Tshipped = headerpicktaskdetail.get(0).getTshipped();
            Trkshiped = headerpicktaskdetail.get(0).getTrkshipped();
            Lbshp = headerpicktaskdetail.get(0).getLbshp();
            LineSplit = headerpicktaskdetail.get(0).getLinesplit();
            strtQty = Double.valueOf(strTQty);
            btnDone.setEnabled(true);
        } else {
            mToastMessage.showToast(PickTaskActivity.this, "No data available");
            LogfileCreator.mAppendLog("No data available in headerpicktaskdetail(PickTaskActivity)");
        }
    }

    private void listViewAlert() {
        List<String> itemList = new ArrayList<String>();
        mDbHelper.openReadableDatabase();
        itemList = mDbHelper.getItemList();
        mDbHelper.closeDatabase();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PickTaskActivity.this);
        builderSingle.setTitle("Select SO item to Substitute");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PickTaskActivity.this, android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < itemList.size(); i++) {
            arrayAdapter.add(itemList.get(i));
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edtLot.setText("");
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                selectedItem = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(PickTaskActivity.this);

                new PickTaskActivity.LoadPickTaskList(adapter).execute();

            }
        });
        builderSingle.show();
    }

    public void ExportData() {
        exportTranList = new ArrayList<picktaskdetail>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getExportPickTaskDetail(ItemNo, strTranlineNo);
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
        } else {
            mToastMessage.showToast(PickTaskActivity.this,
                    "Unable to update Server");
        }
    }


    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PickTaskActivity.this);
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
                exportData.writeXml(dList.get(i), sb, PickTaskActivity.this, mDbHelper);
            }
            sb.append("</" + "PickTaskData" + ">");

            Globals.FROMBTNDONE = false;
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


    public String getRecordXmlExportPOTempAlloc(List<picktaskdetail> dList, String fromSaveOrCancel) {
        String exportPODataXml = "";
        try {
            ExportPickTaskTempQty exportData = new ExportPickTaskTempQty();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "PickTaskTempData" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickTaskActivity.this, mDbHelper, fromSaveOrCancel);
            }
            sb.append("</" + "PickTaskTempData" + ">");

            Globals.FROMBTNDONE = false;
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


    public void ExportPicktaskTempData(String fromHoldOrSave) {

        exportTempAlloc = new ArrayList<picktaskdetail>();

        mDbHelper.openReadableDatabase();
        exportTempAlloc = mDbHelper.getExportPickTaskTempAlloc();
        mDbHelper.closeDatabase();

        if (exportTempAlloc.size() != 0) {
            String exportXml = getRecordXmlExportPOTempAlloc(exportTempAlloc, fromHoldOrSave);
            uploadDataToServiceTempAlloc ex = (uploadDataToServiceTempAlloc) new uploadDataToServiceTempAlloc()
                    .execute(new String[]{exportXml});
            String response = null;
            try {
                response = ex.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (exportTempAlloc.size() != 0) {
               // Log.e("CIRK",fromHoldOrSave);
                Globals.gForHoldTesting=fromHoldOrSave;
                new ExportTempDataPickTask(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else {
                ExportError();
            }
        } else {
            mDbHelper.getWritableDatabase();
            mDbHelper.deleteExportLot();
            mDbHelper.deletePicktaskDetailHold();
            mDbHelper.deletePicktaskrevetOldQty();
            mDbHelper.insertExportLot();
            mDbHelper.closeDatabase();
            Globals.FROMBTNCANCEL = false;
            mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
        }
    }

    private void editAlert() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(parms);
        layout.setGravity(Gravity.CLIP_VERTICAL);
        layout.setPadding(2, 2, 2, 2);

        TextView tv = new TextView(this);
        tv.setText("Item : " + Globals.editItemNum);
        tv.setPadding(80, 40, 40, 20);
        tv.setGravity(Gravity.LEFT);
        tv.setTextSize(16);

        TextView tv2 = new TextView(this);
        tv2.setText("Please Enter Qty between " + Globals.editQty1 + " to " + Globals.editQty2);
        tv2.setPadding(00, 40, 40, 00);
        tv2.setGravity(Gravity.LEFT);
        tv2.setTextSize(10);

        final EditText et = new EditText(this);
        et.setHint("Please Enter the Qty");
        et.setTextSize(11);
        TextView tv1 = new TextView(this);

        LinearLayout.LayoutParams tv1Params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv1Params.bottomMargin = 5;
        layout.addView(tv2, tv1Params);
        layout.addView(et, new LinearLayout.LayoutParams(250, 50));
        layout.setPadding(80, 0, 0, 0);

        alertDialogBuilder.setView(layout);
        alertDialogBuilder.setCustomTitle(tv);
        alertDialogBuilder.setIcon(R.drawable.warning);
        alertDialogBuilder.setCancelable(false);

        // Setting Negative "Cancel" Button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        // Setting Negative "Positive" Button
        alertDialogBuilder.setPositiveButton("OK", null);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button postiveBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        postiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editQty = et.getText().toString();
                double Temp = 0.0;
                double Temp2 = 0.0;
                double EPS = 0.00001;
                if (!editQty.equalsIgnoreCase("")) {
                    Temp = Double.parseDouble(editQty);
                }

                if (editQty.equalsIgnoreCase("")) {
                    mToastMessage.showToast(PickTaskActivity.this, "Please enter the Qty");
                } else if (Temp > Globals.editQty2) {
                    mToastMessage.showToast(PickTaskActivity.this, "Entered Qty is Greater than Ordered Qty");
                    et.setText("");
                    et.setHint("Entered Qty is Greater than Tqty");
                } else if (Temp < Globals.editQty1) {
                    mToastMessage.showToast(PickTaskActivity.this, "Entered Qty is Lesser than Ordered Qty");
                } else {

                    Temp2 = Globals.editQty2 - Temp;     //needtocheck
                    Globals.editQty2 = Temp;

                    mDbHelper.getWritableDatabase();
                    mDbHelper.updateEditQty(Globals.editItemNum, Globals.editTranNum, editQty, String.valueOf(Temp2));//updPicktask
                    mDbHelper.closeDatabase();

                    ItemNo = Globals.editItemNum;
                    strTranlineNo = Globals.editTranNum;
                    strSlot = Globals.editSlot;
                    // ExportData(); ForCancel
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        //new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }

    public void RevertData() {


        if (referenceData != null) {
            mDbHelper.openWritableDatabase();
            mDbHelper.RevertPicktaskData(referenceData, strTranlineNo);
            mDbHelper.closeDatabase();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        } else {
            picktaskdetail pickTaskdetail = new picktaskdetail();

            if (revertheaderpicktaskdetail != null) {
                if (revertheaderpicktaskdetail.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                    pickTaskdetail.setItem(revertheaderpicktaskdetail.get(0).getItem());
                    pickTaskdetail.setDescrip(revertheaderpicktaskdetail.get(0).getDescrip());
                    pickTaskdetail.setTQty(revertheaderpicktaskdetail.get(0).getTQty());
                    pickTaskdetail.setorgTQty(revertheaderpicktaskdetail.get(0).getorgTQty());
                    pickTaskdetail.setTrkQty(revertheaderpicktaskdetail.get(0).getTrkQty());
                    pickTaskdetail.setorgTrkQty(revertheaderpicktaskdetail.get(0).getorgTrkQty());
                    pickTaskdetail.setCatchwt(revertheaderpicktaskdetail.get(0).getCatchwt());
                    pickTaskdetail.setSlot(revertheaderpicktaskdetail.get(0).getSlot());
                    pickTaskdetail.setWLotNo(revertheaderpicktaskdetail.get(0).getWLotNo());
                    pickTaskdetail.setLotNo(revertheaderpicktaskdetail.get(0).getLotNo());
                    pickTaskdetail.setTranlineno(revertheaderpicktaskdetail.get(0).getTranlineno());
                    pickTaskdetail.setorgTranlineno(revertheaderpicktaskdetail.get(0).getorgTranlineno());
                    pickTaskdetail.setUom(revertheaderpicktaskdetail.get(0).getUom());
                    pickTaskdetail.setDoctype(revertheaderpicktaskdetail.get(0).getDoctype());
                    pickTaskdetail.setDocno(revertheaderpicktaskdetail.get(0).getDocno());
                    pickTaskdetail.setDoclineno(revertheaderpicktaskdetail.get(0).getDoclineno());
                    pickTaskdetail.setorgDoclineno(revertheaderpicktaskdetail.get(0).getorgDoclineno());
                    pickTaskdetail.setDocstat(revertheaderpicktaskdetail.get(0).getDocstat());
                    pickTaskdetail.setWeight(revertheaderpicktaskdetail.get(0).getWeight());
                    pickTaskdetail.setVolume(revertheaderpicktaskdetail.get(0).getVolume());
                    pickTaskdetail.setdecnum(revertheaderpicktaskdetail.get(0).getdecnum());
                    pickTaskdetail.setStkumid(revertheaderpicktaskdetail.get(0).getStkumid());
                    pickTaskdetail.setUmfact(revertheaderpicktaskdetail.get(0).getUmfact());
                    pickTaskdetail.setTshipped(revertheaderpicktaskdetail.get(0).getTshipped());
                    pickTaskdetail.setTrkshipped(revertheaderpicktaskdetail.get(0).getTrkshipped());
                    pickTaskdetail.setLbshp(revertheaderpicktaskdetail.get(0).getLbshp());
                    pickTaskdetail.setLinesplit(revertheaderpicktaskdetail.get(0).getLinesplit());
                    pickTaskdetail.setFlag(revertheaderpicktaskdetail.get(0).getFlag());

                    mDbHelper.openWritableDatabase();
                    mDbHelper.RevertPicktaskData(pickTaskdetail, strTranlineNo);
                    mDbHelper.closeDatabase();

                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                } else {

                    mToastMessage.showToast(PickTaskActivity.this, "No data available");
                    LogfileCreator.mAppendLog("No data available in revertheaderpicktaskdetail(PickTaskActivity)");
                }
            } else {
                LogfileCreator.mAppendLog("No data available in revertheaderpicktaskdetail(PickTaskActivity)");
            }
        }
    }

    private void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(PickTaskActivity.this, mPath);
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ex) {
            Log.e("RK", "" + ex.getMessage());
            Toast.makeText(PickTaskActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(PickTaskActivity.this);
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
                boolean isDataSentSuccess = true;

            } else {
                toasttext = "Print Failed";
                mToastMessage.showToast(PickTaskActivity.this,
                        toasttext);
                mSupporter.simpleNavigateTo(PickTaskActivity.class);

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

    //PickTask list load the page
    class UpdatePickStatus extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode, taskStatusUpdate;

        public UpdatePickStatus(String user, String status) {
            this.uCode = user;
            this.taskStatusUpdate = status;
            dialog = new ProgressDialog(PickTaskActivity.this);
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

                mDbHelper.openWritableDatabase();
                mDbHelper.UpdatePickTaskStatus(Globals.gTaskNo, taskStatus);
                mDbHelper.closeDatabase();

                if (taskStatus.equalsIgnoreCase("PICKED") && !Globals.FROMBTNCANCEL) {
                    //Log.e("CIRK","PICKED");
                    ExportData();
                } else if (taskStatus.equalsIgnoreCase("ONHOLD") && !Globals.FROMBTNCANCEL) {
                    //Log.e("CIRK","ONHOLD");
                    ExportData();
                } else {
                    ExportPicktaskTempData("");
                }

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Invalid Pallet Number");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(PickTaskActivity.this,
                        Getmsg);
            } else if (result.equalsIgnoreCase("time out error")) {
                new UpdatePickStatus(mUsername, taskStatus).execute();
            } else {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to Hold");
            }
            dialog.cancel();
        }
    }

    private class LoadPickTaskList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;
        private PickTaskDetailAdapter pickAdapter;

        public LoadPickTaskList(PickTaskDetailAdapter pickTaskAdapter) {
            dialog = new ProgressDialog(PickTaskActivity.this);
            pickAdapter = pickTaskAdapter;
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {
                String result = "";

                DataLoader fileLoader = new DataLoader(PickTaskActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                /*mDbHelper.openWritableDatabase();
                mDbHelper.deleteTaskList();
                mDbHelper.closeDatabase();*/
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "SubPickTaskList" + ".xml");
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
                picktaskdetail = mDbHelper.getPickTaskDetail();
                String tempItem = mDbHelper.getSubItem(mWlotno);
                String DocNo = mDbHelper.getDocNo(mWlotno);
                mDbHelper.closeDatabase();

                Globals.gPickTaskWlotno = mWlotno;
                Globals.gPickTaskItem = tempItem;
                pickAdapter = new PickTaskDetailAdapter(PickTaskActivity.this, picktaskdetail);
                transList.setAdapter(pickAdapter);
                pickAdapter.notifyDataSetChanged();
                setViewsData(DocNo);

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickTaskActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickTaskActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickTaskActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickTaskActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
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
            dialog = new ProgressDialog(PickTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating the server..");
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
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
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
                } else if (resultString.toString().contains("<Result>true</Result>")) {
                    result = "success";
                    // result = "error";
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
                if (taskStatus.equalsIgnoreCase("PICKED") && !Globals.FROMBTNCANCEL) {
                    //FromPICKED
                   // Log.e("CIRK","PICKED");
                    ExportPicktaskTempData("NOTFROMCANCEL");

                    mDbHelper.openReadableDatabase();
                    Globals.gPickTaskPallet = mDbHelper.SelectPallet(Globals.gTaskNo);
                    mDbHelper.closeDatabase();

                    mDbHelper.getWritableDatabase();
                    mDbHelper.deleteExportLot();
                    mDbHelper.deletePicktaskDetail();
                    mDbHelper.deletePicktaskrevetOldQty();
                    mDbHelper.insertExportLot();
                    mDbHelper.closeDatabase();

                    mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
                } else {
                    //FromHold


                       // Log.e("CIRK","FromHold1");


                    ExportPicktaskTempData("NOTFROMCANCEL");

                    mDbHelper.openReadableDatabase();
                    Globals.gPickTaskPallet = mDbHelper.SelectPallet(Globals.gTaskNo);
                    mDbHelper.closeDatabase();

                    mDbHelper.getWritableDatabase();
                    mDbHelper.deleteExportLot();
                    mDbHelper.deletePicktaskDetailHold();
                    mDbHelper.deletePicktaskrevetOldQty();
                    mDbHelper.insertExportLot();
                    mDbHelper.closeDatabase();

                    mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
                }

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Failed to post, refer log file.");
                RevertData();
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "PO Updation failed");
                RevertData();
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to update Server");
                RevertData();
            } else {
                mToastMessage.showToast(PickTaskActivity.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }

    class ExportTempDataPickTask extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public ExportTempDataPickTask(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating the server..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                Log.e("CIRK1","RevertAllock");
                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_REVERTALLOC);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/PickTaskTempData.xml");
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
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_EXPORT_REVERTALLOC;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                Log.e("CIRK2",String.valueOf(resultString));
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "PickTaskTempAlloc" + ".xml");
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
                    //result = "error";
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

                Log.e("CIRK3","RevertAllock1");

                mDbHelper.getWritableDatabase();
                mDbHelper.deleteExportLot();
                mDbHelper.deletePicktaskDetailHold();
                mDbHelper.deletePicktaskrevetOldQty();
                mDbHelper.insertExportLot();
                mDbHelper.closeDatabase();
                Globals.FROMBTNCANCEL = false;
                mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Failed to post, refer log file.");
                RevertData();
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "PO Updation failed");
                RevertData();
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to update Server");
                RevertData();
            } else {
                mToastMessage.showToast(PickTaskActivity.this,
                        result.toString());
            }

            dialog.cancel();
        }
    }

    class ExportTempAlloc extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;
        private String palletNo, wlotNo, Item, TempAlloc, DbTempAlloc, strTranlineNo, strSlot;

        public ExportTempAlloc(String Session, String Uname, String Compid, String palletNo, String wlotNo, String Item, String tempAlloc, String dbTempQty, String strTranlineNo, String strSlot) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            this.palletNo = palletNo;
            this.wlotNo = wlotNo;
            this.Item = Item;
            this.TempAlloc = tempAlloc;
            this.DbTempAlloc = dbTempQty;
            this.strTranlineNo = strTranlineNo;
            this.strSlot = strSlot;
            dialog = new ProgressDialog(PickTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Updating the server..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_PICKTASK_TEMPALLOC);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/PickTask.xml");
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
                info.setName("pLoctid");
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWlotno");
                info.setValue(wlotNo);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotno");
                info.setValue("");
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pPalno");
                info.setValue(palletNo);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot");
                info.setValue(strSlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("Item");
                info.setValue(Item);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pAllocQty");
                info.setValue(TempAlloc);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUname);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_PICKTASK_TEMPALLOC;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "PickTaskTempAlloc" + ".xml");
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
                    //result = "error";
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

                String tempAlc;
                if (Math.round(Double.valueOf(TempAlloc)) >= 0) {
                    tempAlc = "+" + String.valueOf(Math.round(Double.valueOf(TempAlloc)));
                } else {
                    tempAlc = String.valueOf(Math.round(Double.valueOf(TempAlloc)));
                }
                mDbHelper.openWritableDatabase();
                mDbHelper.updatePicktaskTempAlloc(tempAlc, palletNo, wlotNo, Item, strTranlineNo);
                mDbHelper.closeDatabase();

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Failed to post, refer log file.");
                RevertData();
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "PO Updation failed");
                RevertData();
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to update Server");
                RevertData();
            } else {
                mToastMessage.showToast(PickTaskActivity.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }

    class GetPickTaskScanPallet extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetPickTaskScanPallet(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickTaskActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_PICKTASK_SCANPALLET);
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
                info.setName("pPalno");
                info.setValue(mWlotno);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(mUsername);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_PICKTASK_SCANPALLET;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "PickTaskScanPallet" + ".xml");
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
                    } else if (resultString.toString().contains("Repack Completed. Unable to proceed")) {
                        result = "Repack Completed.";
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
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            if (result.equals("success")) {

                new LoadPickTask_ScanPallet().execute();

            } else if (result.equals("Failed")) {

                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this,
                        "Invalid Pallet");
                edtLot.setText("");
                edtLot.requestFocus();

            } else if (result.equals("Assinged another user")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(PickTaskActivity.this,
                        Getmsg);
                edtLot.setText("");
                edtLot.requestFocus();
            } else if (result.equals("  Completed.")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this,
                        "Repack Completed. Unable to Proceed");
                edtLot.setText("");
                edtLot.requestFocus();

            } else if (result.equalsIgnoreCase("time out error")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                new GetPickTaskScanPallet(mUsername).execute();
            } else {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to Hold");
                edtLot.setText("");
                edtLot.requestFocus();
            }
        }
    }

    class GetPickTaskEdtQty extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetPickTaskEdtQty(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickTaskActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
           /* dialog.setMessage("Please wait...");
            dialog.show();*/
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_PICKTASK_SCANPALLET);
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
                info.setName("pPalno");
                info.setValue(edtlinpalletNo);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(mUsername);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_PICKTASK_SCANPALLET;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                // SoapPrimitive lock = (SoapPrimitive) envelope.getResponse().getValue("VPLOCK").toString();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "PickTaskScanPallet" + ".xml");
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
                    } else if (resultString.toString().contains("Repack Completed. Unable to proceed")) {
                        result = "Repack Completed.";
                    }

                } else {

                    result = "success";


                    String[] Whqty = String.valueOf(resultString).split("<whqty>");

                    String wHqty = Whqty[1];

                    String[] wHqty1 = wHqty.split("</whqty>");

                    artEdt_whqTy = Double.valueOf(wHqty1[0]);


                    String[] rpallocqty = String.valueOf(resultString).split("<rpallocqty>");

                    String rpallocqty1 = rpallocqty[1];

                    String[] rpallocqty2 = rpallocqty1.split("</rpallocqty>");

                    artEdt_rpAllocqty = Double.valueOf(rpallocqty2[0]);


                    String[] tqty = String.valueOf(resultString).split("<tqty>");

                    String tqty1 = tqty[1];

                    String[] tqty2 = tqty1.split("</tqty>");

                    artEdt_tQty = Double.valueOf(tqty2[0]);

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

                //For Edit Qty
                LayoutInflater li = LayoutInflater.from(PickTaskActivity.this);
                View promptsView = li.inflate(R.layout.edtline_picktask, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        PickTaskActivity.this);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog edtlinealert = alertDialogBuilder.create();
                edtlinealert.setCancelable(false);
                edtSave = (Button) promptsView.findViewById(R.id.edtSave_btn);
                cancel = (Button) promptsView.findViewById(R.id.edtCancel_btn);
                edtlin_qty = (EditText) promptsView.findViewById(R.id.edtlin_qty);
                edtlin_Pallets = (TextView) promptsView.findViewById(R.id.edtlin_Pallets);
                edtlin_itemDescrib = (TextView) promptsView.findViewById(R.id.edtlin_itemDescrib);
                edtlin_umeasur = (TextView) promptsView.findViewById(R.id.edtlin_umeasur);

                if (SOFT_KEYBOARD.equals("CHECKED")) {
                    edtlin_qty.setShowSoftInputOnFocus(false);
                } else {
                    edtlin_qty.setShowSoftInputOnFocus(true);
                }
                edtlinealert.show();

                edtSave.setOnClickListener(new View.OnClickListener() {   //ForEditDilog
                    @Override
                    public void onClick(View view) {
                        String UdtQty = edtlin_qty.getText().toString();
                        updtPaltNo = edtlin_Pallets.getText().toString();
                        eWlotNo = WlotNo;
                        if (UdtQty.equals("")) {
                            upDqty = 0.0;
                        } else {
                            upDqty = Double.valueOf(UdtQty);
                        }
                        orgnal = Double.valueOf(oPickedQty);
                        orgQtyTQty = Double.valueOf(qtyfn) + artEdt_tQty;
                        maxQty = orgQtyTQty - artEdt_rpAllocqty;

                        if (updtQty.equals("")) {

                        } else if (upDqty <= maxQty) {

                            if (UdtQty.equals("")) {
                                mToastMessage.showToast(PickTaskActivity.this, "Please enter Qty");
                            } else {
                                updtQty = Double.valueOf(UdtQty);
                                orgQty = Double.parseDouble(strEdtLineOrgQty);
                                if (!oTqtyPicked.equals("0.000000") || !oTqtyPicked.equals("") || oTqtyPicked != null) {
                                    difQty = updtQty - Double.parseDouble(oTqtyPicked);
                                } else {
                                    difQty = 0.0;
                                }

                                if (updtQty <= 0) {
                                    isEdited = "E";

                                    mDbHelper.openWritableDatabase();
                                    mDbHelper.updatePicktaskLneIfEditfZero(String.valueOf(updtQty), edtTranLineNo, edtlinpalletNo, isEdited, String.valueOf(difQty), eWlotNo, strEdtLineslot, edtlinpalletNo);  //needtocheck
                                    mDbHelper.closeDatabase();

                                    ItemNo = edtLinitem;
                                    strTranlineNo = edtTranLineNo;
                                    strSlot = strEdtLineslot;

                                    mDbHelper.openWritableDatabase();
                                    mDbHelper.updatePicktaskLneItmsIfZero(String.valueOf(updtQty), edtTranLineNo, edtlinpalletNo, isEdited, "", WlotNo, strEdtLineslot);  //needtocheck
                                    mDbHelper.closeDatabase();

                                    double TempALloc = upDqty - Double.valueOf(qtyfn);

                                    new ExportTempAlloc(mSessionId, Globals.gUsercode, Globals.gCompanyId, edtlinpalletNo, WlotNo, edtLinitem, String.valueOf(TempALloc), String.valueOf(upDqty), edtTranLineNo, strEdtLineslot).execute();

                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);

                                } else {

                                    isEdited = "E";
                                    mDbHelper.openWritableDatabase();
                                    mDbHelper.updatePicktaskLineItems(UdtQty, edtTranLineNo, eWlotNo, strEdtLineslot, edtlinpalletNo, isEdited, String.valueOf(difQty));//updPicktask
                                    mDbHelper.closeDatabase();

                                    double TempALloc = updtQty - Double.valueOf(qtyfn);
                                    new ExportTempAlloc(mSessionId, Globals.gUsercode, Globals.gCompanyId, edtlinpalletNo, WlotNo, edtLinitem, String.valueOf(TempALloc), String.valueOf(updtQty), edtTranLineNo, strEdtLineslot).execute();
                                    updtQty = 0.0;
                                    ItemNo = edtLinitem;
                                    strTranlineNo = edtTranLineNo;
                                    strSlot = strEdtLineslot;

                                    overridePendingTransition(0, 0);
                                    startActivity(getIntent());
                                    overridePendingTransition(0, 0);
                                }
                                isEdited = "N";
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);

                                edtlinealert.dismiss();
                                BtnEdtTrue = false;

                                BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrgBtn)));
                                BtnEdt.setTextColor(Color.BLACK);
                            }
                        } else {
                            mToastMessage.showToast(PickTaskActivity.this,
                                    "you can enter qty between 0 and " + Math.round(maxQty));
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BtnEdtTrue = false;
                        edtlinealert.dismiss();
                        BtnEdt.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrgBtn)));
                        BtnEdt.setTextColor(Color.BLACK);
                    }
                });

                edtlin_itemDescrib.setText(edtLinitem);
                edtlin_umeasur.setText(edtLinuom);
                edtlin_qty.setText(qtyfn);
                edtlin_Pallets.setText(edtlinpalletNo);
                edtlin_qty.setSelectAllOnFocus(true);
                edtlin_qty.requestFocus();

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Invalid Pallet");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(PickTaskActivity.this,
                        Getmsg);
            } else if (result.equals("  Completed.")) {

                mToastMessage.showToast(PickTaskActivity.this,
                        "Repack Completed. Unable to Proceed");

            } else if (result.equalsIgnoreCase("time out error")) {
                new GetPickTaskScanPallet(mUsername).execute();
            } else {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to Hold");
            }
        }
    }

    private class LoadPickTask_ScanPallet extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;


        public LoadPickTask_ScanPallet() {
            dialog = new ProgressDialog(PickTaskActivity.this);

            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            // this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickTaskActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                /*mDbHelper.openWritableDatabase();
                mDbHelper.deleteTaskList();
                mDbHelper.closeDatabase();*/
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "PicktaskScanPallet" + ".xml");
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
                pickTaskScanPallet = mDbHelper.getPicktaskScanPallet(mWlotno.trim());
                mDbHelper.closeDatabase();

                mLotList = getLotList();

                if (!mWlotno.equalsIgnoreCase("")) {

                    mDbHelper.openReadableDatabase();
                    lotRefid = mDbHelper.getPickTaskLotNum(mWlotno);
                    mDbHelper.closeDatabase();
                }

                if (pickTaskScanPallet.size() > 0) {//SCS CIRK 2022/07/25 CT69649C:

                    mDbHelper.openReadableDatabase();
                    isAvailable = mDbHelper.isValidStagingWlotno(pickTaskScanPallet.get(0).getPtsP_wlotno());
                    mDbHelper.closeDatabase();

                    if (isAvailable) {
                        mToastMessage.showToast(PickTaskActivity.this,
                                mWlotno + " is already assigned ");
                        edtLot.setText("");
                        edtLot.requestFocus();
                        scanResult = true;
                    } else if (!isTaskCompleted) {
                        edtLot.setText("");
                        mToastMessage.showToast(PickTaskActivity.this,
                                "All the items are scanned.");
                        scanResult = true;
                    } else {
                        validationResult = validateDate();
                        if (validationResult) {

                            Globals.gPickTaskWlotno = pickTaskScanPallet.get(0).getPtsP_wlotno();
                            Globals.gPickTaskItem = getLotItemList(Globals.gPickTaskWlotno);
                            Globals.gStartPickDuration = System.currentTimeMillis();
                            isItemAvailable = true;
                            mDbHelper.openReadableDatabase();
                            if (mDbHelper.isDataAvailable(pickTaskScanPallet.get(0).getPtsP_item(), StrFlag)) {

                                mDbHelper.openReadableDatabase();
                                headerpicktaskdetail = mDbHelper.selectPickTaskDetail(pickTaskScanPallet.get(0).getPtsP_item());
                                revertheaderpicktaskdetail = headerpicktaskdetail;
                                strEdtLineOrgQty = revertheaderpicktaskdetail.get(0).getorgTQty();
                                oTqtyPicked = revertheaderpicktaskdetail.get(0).getoTqtypicked();
                                mDbHelper.closeDatabase();

                                if (headerpicktaskdetail.size() > 0 && pickTaskScanPallet.size() > 0) {
                                    dSoQty = Double.parseDouble(headerpicktaskdetail.get(0).getorgTQty());
                                    dLotQty = Double.parseDouble(pickTaskScanPallet.get(0).getPtsP_tqty()) - Double.parseDouble(pickTaskScanPallet.get(0).getPtsP_rpallocqty());

                                    if (headerpicktaskdetail.get(0).getItem().equalsIgnoreCase(pickTaskScanPallet.get(0).getPtsP_item())) {

                                        if (headerpicktaskdetail.size() > 0 && pickTaskScanPallet.size() > 0) {     //SCS CIRK 2022/07/25 CT69649C:
                                            ItemNo = headerpicktaskdetail.get(0).getItem();
                                            strDesc = headerpicktaskdetail.get(0).getDescrip();
                                            strTQty = headerpicktaskdetail.get(0).getTQty();
                                            strorgTQty = headerpicktaskdetail.get(0).getorgTQty();
                                            strTrkQty = headerpicktaskdetail.get(0).getTrkQty();
                                            strorgTrkQty = headerpicktaskdetail.get(0).getorgTrkQty();
                                            strCatchwt = headerpicktaskdetail.get(0).getCatchwt();
                                            strSlot = headerpicktaskdetail.get(0).getSlot();
                                            strwLotno = lotRefid;
                                            strLot = mWlotno;
                                            strTranlineNo = headerpicktaskdetail.get(0).getTranlineno();
                                            orgTranlineno = headerpicktaskdetail.get(0).getorgTranlineno();
                                            uom = headerpicktaskdetail.get(0).getUom();
                                            doctype = headerpicktaskdetail.get(0).getDoctype();
                                            docno = headerpicktaskdetail.get(0).getDocno();
                                            doclineno = headerpicktaskdetail.get(0).getDoclineno();
                                            orgdoclineno = headerpicktaskdetail.get(0).getorgDoclineno();
                                            docstat = headerpicktaskdetail.get(0).getDocstat();
                                            strweight = headerpicktaskdetail.get(0).getWeight();
                                            volume = headerpicktaskdetail.get(0).getVolume();
                                            decnum = headerpicktaskdetail.get(0).getdecnum();
                                            stkumid = headerpicktaskdetail.get(0).getStkumid();
                                            umfact = headerpicktaskdetail.get(0).getUmfact();
                                            Tshipped = headerpicktaskdetail.get(0).getTshipped();
                                            Trkshiped = headerpicktaskdetail.get(0).getTrkshipped();
                                            Lbshp = headerpicktaskdetail.get(0).getLbshp();
                                            LineSplit = headerpicktaskdetail.get(0).getLinesplit();
                                            scanResult = true;

                                            if (dLotQty > dSoQty) {
                                                //For more Qty
                                                LayoutInflater li = LayoutInflater.from(PickTaskActivity.this);
                                                View promptsView = li.inflate(R.layout.picktaskmore_qty_alrt, null);
                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                        PickTaskActivity.this);
                                                alertDialogBuilder.setView(promptsView);
                                                final AlertDialog moreQtyAlrt = alertDialogBuilder.create();
                                                moreQtyAlrt.setCancelable(false);
                                                edtSave = (Button) promptsView.findViewById(R.id.edtSave_btn);
                                                cancel = (Button) promptsView.findViewById(R.id.edtCancel_btn);
                                                edtlin_qty = (EditText) promptsView.findViewById(R.id.edtlin_qty);
                                                edtlin_Pallets = (TextView) promptsView.findViewById(R.id.edtlin_Pallets);
                                                edtlin_itemDescrib = (TextView) promptsView.findViewById(R.id.edtlin_itemDescrib);
                                                edtlin_umeasur = (TextView) promptsView.findViewById(R.id.edtlin_umeasur);
                                                edtlin_AvailQty = (TextView) promptsView.findViewById(R.id.edtlin_AvailQty);
                                                String updpaltNo = edtlin_Pallets.getText().toString();
                                                edtlin_Pallets.setText(mWlotno);
                                                edtlin_itemDescrib.setText(pickTaskScanPallet.get(0).getPtsP_item());
                                                edtlin_umeasur.setText(pickTaskScanPallet.get(0).getPtsP_umeasur());
                                                neWcQTy = Double.valueOf(pickTaskScanPallet.get(0).getPtsP_tqty()) - Double.valueOf(pickTaskScanPallet.get(0).getPtsP_rpallocqty());

                                                edtlin_qty.setText(String.valueOf(Math.round(dSoQty)));
                                                edtlin_AvailQty.setText(String.valueOf(Math.round(neWcQTy)));

                                                if (SOFT_KEYBOARD.equals("CHECKED")) {
                                                    edtlin_qty.setShowSoftInputOnFocus(false);

                                                } else {
                                                    edtlin_qty.setShowSoftInputOnFocus(true);

                                                }
                                                moreQtyAlrt.show();

                                                edtSave.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {    //formoreQty

                                                        String qtySave = edtlin_qty.getText().toString();
                                                        String updtPaltNo = edtlin_Pallets.getText().toString();
                                                        if (qtySave.equals("")) {
                                                            UpdQty = 0.0;
                                                        } else {
                                                            UpdQty = Double.valueOf(qtySave);
                                                        }
                                                        if (qtySave.equals("")) {
                                                            mToastMessage.showToast(PickTaskActivity.this, "Please enter Qty");
                                                        } else {
                                                            updtQty = Double.valueOf(qtySave);
                                                            orgQty = Double.parseDouble(strEdtLineOrgQty);
                                                            if (!oTqtyPicked.equals("0.000000") || !oTqtyPicked.equals("") || oTqtyPicked != null) {
                                                                difQty = updtQty - Double.parseDouble(oTqtyPicked);
                                                            } else {
                                                                difQty = 0.0;
                                                            }

                                                            if (qtySave.equals("")) {
                                                            } else if (UpdQty <= neWcQTy) {
                                                                updtQty = Double.valueOf(qtySave);
                                                                edtTranLineNo = headerpicktaskdetail.get(0).getTranlineno();
                                                                String item = pickTaskScanPallet.get(0).getPtsP_item();
                                                                String pallet = pickTaskScanPallet.get(0).getPtsP_lotrefid();

                                                                mDbHelper.openWritableDatabase();
                                                                if (updtQty <= 0) {
                                                                    isEdited = "";
                                                                    mDbHelper.updatePicktaskLneItmsIfZero(strEdtLineOrgQty, edtTranLineNo, edtlinpalletNo, isEdited, "", WlotNo, strEdtLineslot);
                                                                    double TempALloc = updtQty;
                                                                } else {
                                                                    Saveprocess();
                                                                    updtQty = 0.0;
                                                                }
                                                                mDbHelper.closeDatabase();

                                                                overridePendingTransition(0, 0);
                                                                startActivity(getIntent());
                                                                overridePendingTransition(0, 0);

                                                                moreQtyAlrt.dismiss();
                                                            } else {
                                                                mToastMessage.showToast(PickTaskActivity.this,
                                                                        "you can enter qty between 0 and " + Math.round(neWcQTy));
                                                            }
                                                        }
                                                    }
                                                });
                                                cancel.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        edtLot.setText("");
                                                        edtLot.requestFocus();
                                                        moreQtyAlrt.dismiss();
                                                    }
                                                });
                                                edtlin_qty.setSelectAllOnFocus(true);
                                                edtlin_qty.requestFocus();
                                            } else {
                                                Saveprocess();
                                            }
                                        } else {
                                            mToastMessage.showToast(PickTaskActivity.this,
                                                    "Invalid Pallet");
                                            edtLot.setText("");
                                            edtLot.requestFocus();
                                            scanResult = true;
                                        }
                                    } else {
                                        mToastMessage.showToast(PickTaskActivity.this,
                                                "Item not available");
                                        edtLot.setText("");
                                        edtLot.requestFocus();
                                        scanResult = true;
                                    }
                                } else {
                                    mToastMessage.showToast(PickTaskActivity.this,
                                            "Item not available");
                                    edtLot.setText("");
                                    edtLot.requestFocus();
                                    scanResult = true;
                                }
                            } else {
                                mToastMessage.showToast(PickTaskActivity.this,
                                        "Item already picked");
                                edtLot.setText("");
                                edtLot.requestFocus();
                                scanResult = false;
                            }
                            mDbHelper.closeDatabase();
                        }
                    }
                    Log.d("PickTaskScanPallet", "Success");
                } else {
                    mToastMessage.showToast(PickTaskActivity.this, "No data available");
                    LogfileCreator.mAppendLog("No data available in pickTaskScanPallet(PickTaskActivity)");
                }
            } else if (result.equals("nosd")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this, "File not available");
            } else {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PickTaskActivity.this, "Error");
            }
        }


    }

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PickTaskActivity.this);
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
                mToastMessage.showToast(PickTaskActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickTaskActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    public class uploadDataToServiceTempAlloc extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceTempAlloc() {
            mDialog = new ProgressDialog(PickTaskActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "PickTaskTempData" + ".xml");
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
                mToastMessage.showToast(PickTaskActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickTaskActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
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


            dialog = new ProgressDialog(PickTaskActivity.this);
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
                // mToastMessage.showToast(SelectCompanyActivity.this,"success");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equalsIgnoreCase("time out error")) {
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickTaskActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }
}



