package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
import com.silvercreek.wmspickingclient.model.RepackFG;
import com.silvercreek.wmspickingclient.model.RepackIngredients;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportPickTask;
import com.silvercreek.wmspickingclient.xml.ExportRepackData;

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
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PickRepackIngredientsActivity extends AppBaseActivity {

    public static final String METHOD_EXPORT_DATA = "PickTask_SaveMain";
    public static final String METHOD_EXPORT_DATA_FINAL = "Repack_Save";
    public static final String METHOD_GET_PEPACK_DATA = "Repack_LookupData";
    public static final String METHOD_REPACK_CANCEL = "Repack_unlock";
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static final String METHOD_GET_RAW_DATA = "Repack_PickRawItem";
    public static final String METHOD_SAVE_RAW_DATA = "Repack_TempAlloc";
    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public Double IngredientsList = 0.0;
    public String lockStatus = "";
    public String userName = "";
    String toasttext = "";
    String filename = "PickTaskPalletLabel.pdf";
    boolean QTYALERT_OVERRIDE = false;
    boolean QTYALERT_ADD = false;
    String allocQty = "";
    String allocDiffQty = "";
    String TempallocQty = "";
    String tempAllocFinal;
    private ListView transList;
    private EditText edtPallet, edtQty;
    private TextView tvRepacknum, DTeNumber;
    private File pdfFile = null;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private Document document;
    private PdfContentByte cb;
    private PdfPTable table;
    private PdfWriter docWriter;
    private PdfPCell cell;
    private List<RepackIngredients> repackFGList;
    private List<RepackFG> repackFG;
    private List<RepackIngredients> saveList;
    private List<RepackIngredients> repackTotalList;
    private List<RepackIngredients> ingredientsList;
    private List wLotNoList;
    private List<RepackIngredients> exportTranListFinal;
    private String mPath;
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private RepackIngredientsAdapter adapter;
    private RepackFGAdapter adapter1;
    private ArrayList<picktaskWHIPTL> mPalletMast;
    private ArrayList<picktaskWHMQTY> mLotMast;
    private List<picktaskPrintlabel> PrintLabelList;
    private String stop, trailer, route, dock, deldate, order, task, custid, custname, picker, palno;
    private String mPalno = "";
    private String PalletNumber = "", enteredQty, enteredQty1, allocQtyForSave;
    private Button btnCancel, btnClose, btnSave, btnRecallLstDTE, btnOnHold, btnSaveFinal, btnCancelFinal;
    private String decnum = "";
    private String mSessionId = "";
    private String mDeviceId = "";
    private String mCompany = "";
    private String mLoctid = "";
    private String mUsername = "";
    private File mImpOutputFile;
    private String Getmsg = "";
    private String selectedItem = "";
    private String[] itemArray = {};
    private int SubTranlineCount = 0;
    private String subTranNo = "";
    private String stagingSlot = "";
    private String taskStatus = "";
    private String editQty = "";
    private Boolean scanResult = true;
    private RepackFGAdapter repackFGAdapter;
    private ArrayList<picktaskdetail> editPickDetail;
    private String repackNum = "";
    private boolean isLocalData = false;
    private boolean ISSUMQTY = false;
    private double firstQty = 0;
    private Double totalQty = 0.0;
    private Double whQty = 0.0;
    private Double icQty = 0.0;
    private Double qtyUsed = 0.0;
    private Double totalQtY = 0.0;
    private Double toTalQtY = 0.0;
    private String tempAlloc2 = "";
    private boolean isLocked = false;
    private boolean isdeviceSideLock = false;
    private String lockUserName = "";
    private String OrgPaNo = "";
    private String DteCode = "";
    private EditText edtRepackNum;
    private String repackNo = "";
    private String RepackNumber = "";
    private String intentRepackNum = "";

    public static String fixedLengthString(String string) {
        return String.format("%1$10" + "s", string);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_repack_ingredients);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        edtPallet = (EditText) findViewById(R.id.edtPallet);
        edtQty = (EditText) findViewById(R.id.edtQty);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnClose = (Button) findViewById(R.id.btn_close);
        tvRepacknum = (TextView) findViewById(R.id.tv_repacknum);
        DTeNumber = (TextView) findViewById(R.id.DTeNumber);
        edtRepackNum = (EditText) findViewById(R.id.edtRepackNum);
        btnRecallLstDTE = (Button) findViewById(R.id.recallLstDTE);
        btnOnHold = (Button) findViewById(R.id.btn_hold);
        btnSaveFinal = (Button) findViewById(R.id.btn_Fsave);
        btnCancelFinal = (Button) findViewById(R.id.btn_Fcancel);
        String customerId;
        String _currentJson;


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
        mDeviceId = Globals.gDeviceId;
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;

        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtRepackNum.setShowSoftInputOnFocus(false);
            edtPallet.setShowSoftInputOnFocus(false);
            edtQty.setShowSoftInputOnFocus(false);

        } else {
            edtRepackNum.setShowSoftInputOnFocus(true);
            edtPallet.setShowSoftInputOnFocus(true);
            edtQty.setShowSoftInputOnFocus(true);
        }

        edtRepackNum.requestFocus();
        if (edtRepackNum.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        mDbHelper.openReadableDatabase();
        exportTranListFinal = mDbHelper.getRepackIngredientsForExport();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        lockStatus = mDbHelper.getLockStatus(repackNum.trim());
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        repackFG = mDbHelper.getRepackFG();
        mDbHelper.closeDatabase();

        edtRepackNum.requestFocus();

        String fromhold1 = this.getIntent().getStringExtra("fromhold");
        if (!fromhold1.equals("") && fromhold1 != null) {
            if (fromhold1.equals("holdTrue")) {
                btnOnHold.setEnabled(true);
                edtPallet.setEnabled(true);
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
                btnCancelFinal.setEnabled(true);
                btnSaveFinal.setEnabled(true);
                btnRecallLstDTE.setEnabled(false);
                edtPallet.requestFocus();
            } else {
                btnRecallLstDTE.setEnabled(true);
            }
        }


        if (isLocked) {
            edtPallet.setEnabled(false);
            edtQty.setEnabled(false);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);

        } else if (isdeviceSideLock) {
            edtPallet.setEnabled(false);
            edtQty.setEnabled(false);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
        }

        DTeNumber.setText("Repack # " + DteCode.trim());

        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        edtPallet.setEnabled(false);
        edtQty.setEnabled(false);

        mDbHelper.openReadableDatabase();
        repackFGList = mDbHelper.getRepackIngredients();
        wLotNoList = mDbHelper.getWlotNoList();
        mDbHelper.closeDatabase();

        if (repackFGList.size() > 0) {
            if (!Globals.orgPano.trim().equals("")) {
                tvRepacknum.setText("Repack # " + Globals.orgPano.trim());
            } else {
                tvRepacknum.setText("");
            }
        } else {
            tvRepacknum.setText("");
        }


        if (exportTranListFinal.size() > 0) {
            btnOnHold.setEnabled(true);
            btnCancelFinal.setEnabled(true);
            btnSaveFinal.setEnabled(true);
        } else {
            intentRepackNum = this.getIntent().getExtras().getString("dteCode");
            btnSave.setEnabled(false);
            btnOnHold.setEnabled(false);
            btnSaveFinal.setEnabled(false);
            btnCancelFinal.setEnabled(false);

        }

        if (repackFGList.size() > 0) {
            btnCancel.setEnabled(true);
            btnOnHold.setEnabled(true);
            btnCancelFinal.setEnabled(true);
            btnSaveFinal.setEnabled(true);
            btnRecallLstDTE.setEnabled(false);
            edtRepackNum.setEnabled(false);

        } else if (isLocked && isdeviceSideLock) {
            btnRecallLstDTE.setEnabled(false);
            edtRepackNum.setText(Globals.RecallLstDTE_code);
            edtRepackNum.setEnabled(false);
            btnCancel.setEnabled(true);
            btnOnHold.setEnabled(false);
            btnSaveFinal.setEnabled(false);
        } else if (exportTranListFinal.size() == 0 && intentRepackNum != null && !intentRepackNum.equals("")) {
            intentRepackNum = this.getIntent().getExtras().getString("dteCode");
            btnSave.setEnabled(false);
            btnOnHold.setEnabled(false);
            btnCancel.setEnabled(true);
            edtRepackNum.setText(intentRepackNum);
            edtRepackNum.setEnabled(false);
            edtPallet.requestFocus();
        } else {

            btnCancel.setEnabled(false);
            String fromhold12 = this.getIntent().getStringExtra("fromhold");
            if (!fromhold12.equals("") && fromhold12 != null) {
                if (fromhold12.equals("holdTrue")) {
                    btnOnHold.setEnabled(true);
                    edtPallet.setEnabled(true);
                    btnSave.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnCancelFinal.setEnabled(true);
                    btnSaveFinal.setEnabled(true);
                    edtPallet.requestFocus();
                    btnRecallLstDTE.setEnabled(false);
                } else {
                    btnOnHold.setEnabled(false);
                    btnRecallLstDTE.setEnabled(true);
                }
            }
        }

        adapter = new RepackIngredientsAdapter(PickRepackIngredientsActivity.this, repackFGList);
        transList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        intentRepackNum = this.getIntent().getExtras().getString("dteCode");
        if (intentRepackNum != null) {
            if (!intentRepackNum.equals("")) {
                edtRepackNum.setText(intentRepackNum);
                edtRepackNum.setEnabled(false);
                edtPallet.setEnabled(true);
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
                edtPallet.requestFocus();
            } else {
                edtRepackNum.requestFocus();
            }
        } else {
            edtRepackNum.requestFocus();
        }
        edtRepackNum.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                isdeviceSideLock = false;
                isLocked = false;

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            RepackNumber = edtRepackNum.getText().toString();
                            RepackNumber = fixedLengthString(RepackNumber);

                            mDbHelper.openReadableDatabase();
                            Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                            mDbHelper.closeDatabase();

                            if (RepackNumber.equalsIgnoreCase("          ")) {
                                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                                        // "Please Enter or Scan the Repack #");
                                        "Please scan or enter the Repack # ");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        edtRepackNum.requestFocus();

                                    }
                                }, 150);

                                scanResult = true;
                            } else {
                                new GetRepackData(mUsername).execute();
                            }
                        default:
                            break;
                    }
                } else if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_TAB)) {

                    if (keyCode == KeyEvent.KEYCODE_TAB) {
                        RepackNumber = edtRepackNum.getText().toString();
                        RepackNumber = fixedLengthString(RepackNumber);

                        mDbHelper.openReadableDatabase();
                        Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                        mDbHelper.closeDatabase();

                        if (RepackNumber.equalsIgnoreCase("          ")) {
                            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                                    // "Please Scan or Enter the Repack #");
                                    "Empty");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    edtRepackNum.requestFocus();

                                }
                            }, 150);
                            scanResult = true;

                        } else {
                            new GetRepackData(mUsername).execute();
                        }
                    }
                }
                return false;
            }
        });


        btnSaveFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportFinalData();
            }
        });

        btnOnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                repackFGList = mDbHelper.getRepackIngredients();
                mDbHelper.closeDatabase();

                if (repackFGList.size() > 0) {
                    Globals.holdTaskNum = Globals.gTaskNo;
                    taskStatus = "ONHOLD";
                    Intent theIntent = new Intent(PickRepackIngredientsActivity.this, RepackMainMenuScreen.class);
                    theIntent.putExtra("repacknum", "");
                    theIntent.putExtra("fromhold", "holdTrue");
                    theIntent.putExtra("dteCode", Globals.RecallLstDTE_code);
                    theIntent.putExtra("fromRaw", "fromRaw");
                    startActivity(theIntent);
                } else {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this,
                            "No data available to hold");
                }
            }
        });

        btnCancelFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDbHelper.openReadableDatabase();
                exportTranListFinal = mDbHelper.getRepackIngredientsForExport();
                mDbHelper.closeDatabase();

                if (exportTranListFinal.size() > 0) {

                    mToastMessage.showToast(PickRepackIngredientsActivity.this,
                            "Please save the existing transaction.");

                } else {
                    deleteAlert();
                }
            }
        });


        edtPallet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            mDbHelper.openReadableDatabase();
                            repackFGList = mDbHelper.getRepackIngredients();
                            wLotNoList = mDbHelper.getWlotNoList();
                            mDbHelper.closeDatabase();

                            PalletNumber = edtPallet.getText().toString();

                            if (PalletNumber.equalsIgnoreCase("")) {
                                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                                        "Please scan or enter the pallet");
                                scanResult = true;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtPallet.requestFocus();
                                    }
                                }, 150);

                            } else if (wLotNoList.contains(PalletNumber)) {
                                isLocalData = true;
                                Globals.isLocalData=true;
                                new GetRepackRawData(mUsername).execute();

                            } else {
                                Supporter.SUMQTY = false;
                                new GetRepackRawData(mUsername).execute();
                            }
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent theIntent = new Intent(PickRepackIngredientsActivity.this, PickRepackActivity.class);
                theIntent.putExtra("repacknum", repackNum);
                theIntent.putExtra("lockstatus", isLocked);
                theIntent.putExtra("isdeviceSidelock", isdeviceSideLock);
                theIntent.putExtra("rpNum", repackNum);
                theIntent.putExtra("dteCode", DteCode);
                theIntent.putExtra("lockUser", lockUserName);
                //lockUser
                startActivity(theIntent);
                //cancelAlert();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mDbHelper.openReadableDatabase();
                Boolean isScaned = mDbHelper.isTranlineNullAvailable();
                mDbHelper.closeDatabase();


                mDbHelper.openReadableDatabase();
                repackTotalList = mDbHelper.getWlotRepackIngredients(edtPallet.getText().toString());
                mDbHelper.closeDatabase();

                if (!Globals.isLocalData){

                    mDbHelper.openWritableDatabase();
                    mDbHelper.updatewhenCancel(edtPallet.getText().toString(),repackTotalList);
                    mDbHelper.closeDatabase();

                }else if (isScaned){
                     mDbHelper.openWritableDatabase();
                    mDbHelper.DeleteRevertCancel(edtPallet.getText().toString());
                    mDbHelper.closeDatabase();
                }



                edtPallet.setText("");
                edtQty.setText("");
                edtPallet.setEnabled(true);
                btnCancel.setEnabled(false);
                edtQty.setEnabled(false);
                edtPallet.requestFocus();
                btnSave.setEnabled(false);
                btnSaveFinal.setEnabled(true);
                btnCancelFinal.setEnabled(true);
                btnOnHold.setEnabled(true);



                btnCancel.setEnabled(false);

            }
        });

        btnRecallLstDTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                repackNo = Globals.RecallLstDTE_code;
                edtRepackNum.setText(repackNo);
                RepackNumber = repackNo;
                RepackNumber = fixedLengthString(RepackNumber);

                mDbHelper.openReadableDatabase();
                Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                mDbHelper.closeDatabase();

                if (RepackNumber.equalsIgnoreCase("          ")) {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this,
                            "Please scan or enter the Repack #");
                    edtRepackNum.requestFocus();
                    scanResult = true;
                } else {
                    new GetRepackData(mUsername).execute();
                }
            }
        });


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enteredQty = edtQty.getText().toString();
                enteredQty1 = edtPallet.getText().toString();

                mDbHelper.openReadableDatabase();
                saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                mDbHelper.closeDatabase();

                if (enteredQty1.equalsIgnoreCase("")) {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this,
                            "Please scan the item");
                } else if (enteredQty.equalsIgnoreCase("")) {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this,
                            "Please enter the qty");
                    edtQty.requestFocus();

                } else if (Integer.parseInt(enteredQty) <= 0) {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this,
                            "Please enter qty greater then zero");
                    edtQty.requestFocus();
                } else if (wLotNoList.contains(PalletNumber)) {
                    firstQty = Double.parseDouble(saveList.get(0).getRIT_REMARKS());

                    totalQty = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
                    whQty = Double.valueOf(ingredientsList.get(0).getRIT_WHQTY());
                    icQty = Double.valueOf(ingredientsList.get(0).getRIT_ICQTY());
                    qtyUsed = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED());
                    qtyAlert();
                    edtPallet.requestFocus();
                    edtRepackNum.setText(Globals.RecallLstDTE_code);
                    edtRepackNum.setEnabled(false);

                } else {
                    firstQty = Double.parseDouble(saveList.get(0).getRIT_REMARKS());

                    totalQty = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
                    whQty = Double.valueOf(ingredientsList.get(0).getRIT_WHQTY());
                    icQty = Double.valueOf(ingredientsList.get(0).getRIT_ICQTY());
                    qtyUsed = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED());
                    allocQtyForSave = enteredQty;
                    new SaveRepackRawData(mUsername, allocQtyForSave).execute();
                }
            }
        });

    }

    private void deleteAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackIngredientsActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (isdeviceSideLock) {
                            deleteRepack();

                            mDbHelper.openReadableDatabase();
                            repackFGList = mDbHelper.getRepackIngredients();
                            wLotNoList = mDbHelper.getWlotNoList();

                            adapter = new RepackIngredientsAdapter(PickRepackIngredientsActivity.this, repackFGList);
                            transList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            edtRepackNum.setEnabled(true);
                            edtPallet.setEnabled(false);
                            edtRepackNum.setText("");
                            tvRepacknum.setText("");
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
                            btnCancelFinal.setEnabled(false);
                            btnOnHold.setEnabled(false);
                            edtRepackNum.requestFocus();

                        } else {
                            new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                        }

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

    private void deleteRepack() {
        mDbHelper.openWritableDatabase();
        mDbHelper.deleteRepackData();
        mDbHelper.closeDatabase();
    }

    public boolean validateDate() {

        String allocQty = "";
        boolean result = true;
        enteredQty = edtQty.getText().toString();
        mDbHelper.openReadableDatabase();
        saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
        mDbHelper.closeDatabase();
        firstQty = Double.parseDouble(saveList.get(0).getRIT_REMARKS()) + Double.parseDouble(saveList.get(0).getRIT_QTYUSED());

      //  totalQty = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
        totalQty = Double.valueOf(ingredientsList.get(0).getRIT_REMARKS()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
        whQty = Double.valueOf(ingredientsList.get(0).getRIT_WHQTY());
        icQty = Double.valueOf(ingredientsList.get(0).getRIT_ICQTY());
        qtyUsed = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED());

       // if (qtyUsed <= 0 && !isLocalData) {
        if (totalQty <= 0 && !isLocalData) {
            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                    "No qty available on the " + PalletNumber + " pallet");

            result = false;
       // } else if (totalQty <= 0 && !isLocalData) {
        } else if (totalQty <= 0 && !isLocalData) {
            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                    "Qty less allocation insufficient on the " + PalletNumber + "  pallet");
            result = false;
        } else if (Double.parseDouble(enteredQty) > firstQty) {

            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                    "Qty entered is more than available qty for the pallet " + PalletNumber);
            edtQty.requestFocus();
            result = false;
        }
        return result;
    }

    public void ExportFinalData() {
        exportTranListFinal = new ArrayList<RepackIngredients>();

        mDbHelper.openReadableDatabase();
        exportTranListFinal = mDbHelper.getRepackIngredientsForExport();
        mDbHelper.closeDatabase();

        if (exportTranListFinal.size() != 0) {

            String exportXml = getRecordXmlExportPOFinal(exportTranListFinal);
            uploadDataToServiceExportItmFinal ex = (uploadDataToServiceExportItmFinal) new uploadDataToServiceExportItmFinal()
                    .execute(new String[]{exportXml});
            String response = null;
            try {
                response = ex.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (exportTranListFinal.size() != 0) {
                new ExportTranDataFinal(mSessionId, mUsername, mCompany).execute();
            } else {
                ExportError();
            }
        } else {
            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                    "No export data available");
        }
    }

    public void SetData() {

        mDbHelper.openReadableDatabase();
        ingredientsList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
        mDbHelper.closeDatabase();

        Supporter.SUMQTY = false;
        isLocalData = true;
        Globals.isLocalData=true;
        edtPallet.setText(PalletNumber.trim());

       // totalQtY = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
        totalQtY = Double.parseDouble(ingredientsList.get(0).getRIT_REMARKS()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
        Double usdQty = Double.parseDouble(ingredientsList.get(0).getRIT_QTYUSED());
        if (usdQty == 0.0){
            edtQty.setText(String.valueOf(Math.round(Double.valueOf(totalQtY))));
        }else {
            edtQty.setText(String.valueOf(Math.round(Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()))));
        }

        //edtQty.setText(String.valueOf(Math.round(Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()))));
        edtQty.requestFocus();
        edtQty.setSelectAllOnFocus(true);
        edtQty.selectAll();
        edtPallet.setEnabled(false);

        btnSave.setEnabled(true);
        btnCancel.setEnabled(true);
        edtQty.setEnabled(true);
        btnSaveFinal.setEnabled(false);
        btnCancelFinal.setEnabled(false);
        btnOnHold.setEnabled(false);
        edtQty.setSelectAllOnFocus(true);
    }

    @Override
    public void onBackPressed() {


        mDbHelper.openReadableDatabase();
        exportTranListFinal = mDbHelper.getRepackIngredientsForExport();
        mDbHelper.closeDatabase();

        if (exportTranListFinal.size() > 0) {
            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                    "Please save the existing transaction.");

        } else {
            if (edtRepackNum.isEnabled()){

                if (isdeviceSideLock) {
                    deleteRepack();
                    mDbHelper.openReadableDatabase();
                    repackFG = mDbHelper.getRepackFG();
                    mDbHelper.closeDatabase();


                    repackFGAdapter = new RepackFGAdapter(PickRepackIngredientsActivity.this, repackFG);
                    transList.setAdapter(repackFGAdapter);
                    adapter.notifyDataSetChanged();
                    repackFGAdapter.notifyDataSetChanged();
                    edtRepackNum.setEnabled(true);
                    edtPallet.setEnabled(false);
                    edtRepackNum.setText("");
                    btnRecallLstDTE.setEnabled(true);
                    btnCancel.setEnabled(false);
                    btnCancelFinal.setEnabled(false);
                    btnOnHold.setEnabled(false);
                    edtRepackNum.requestFocus();

                } else {
                    new CancelRepackFinal(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();

                    Intent theIntent = new Intent(PickRepackIngredientsActivity.this, RepackMainMenuScreen.class);
                    theIntent.putExtra("dteCode", "");
                    startActivity(theIntent);
                }

            }else {
                BackpressAlert();
            }

        }
    }


    private void BackpressAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackIngredientsActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (isdeviceSideLock) {
                            deleteRepack();
                            mDbHelper.openReadableDatabase();
                            repackFG = mDbHelper.getRepackFG();
                            mDbHelper.closeDatabase();


                            repackFGAdapter = new RepackFGAdapter(PickRepackIngredientsActivity.this, repackFG);
                            transList.setAdapter(repackFGAdapter);
                            adapter.notifyDataSetChanged();
                            repackFGAdapter.notifyDataSetChanged();
                            edtRepackNum.setEnabled(true);
                            edtPallet.setEnabled(false);
                            edtRepackNum.setText("");
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
                            btnCancelFinal.setEnabled(false);
                            btnOnHold.setEnabled(false);
                            edtRepackNum.requestFocus();

                        } else {
                            new CancelRepackFinal(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();

                            Intent theIntent = new Intent(PickRepackIngredientsActivity.this, RepackMainMenuScreen.class);
                            theIntent.putExtra("dteCode", "");
                            startActivity(theIntent);
                        }

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
                        Intent theIntent = new Intent(PickRepackIngredientsActivity.this, PickRepackActivity.class);
                        theIntent.putExtra("lockstatus", isLocked);
                        theIntent.putExtra("isdeviceSidelock", isdeviceSideLock);
                        startActivity(theIntent);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {
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
            picktaskPrintlabel printLabel = PrintLabelList.get(0);
            custname = printLabel.getCustname();
            if (custname == null) {
                custname = "";
            }

            Paragraph childParagraph = new Paragraph(custname, FONT_TABLE_CONTANT);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            reportBody.add(childParagraph);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void qtyAlert() {

        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackIngredientsActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(true);
        alertUser.setMessage("Quantity Add or Override?");
        alertUser.setPositiveButton("Override",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        QTYALERT_ADD = false;
                        wlotContainPalletNo();
                        dialog.dismiss();
                        edtPallet.requestFocus();
                        edtQty.setEnabled(false);
                        btnSave.setEnabled(false);
                        edtRepackNum.setText(Globals.RecallLstDTE_code);
                        edtRepackNum.setEnabled(false);

                    }
                });

        alertUser.setNegativeButton("Add",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        QTYALERT_ADD = true;
                        wlotContainPalletNo();
                        dialog.dismiss();
                        edtPallet.requestFocus();
                        edtQty.setEnabled(false);
                        btnSave.setEnabled(false);
                        edtRepackNum.setText(Globals.RecallLstDTE_code);
                        edtRepackNum.setEnabled(false);
                    }
                });

        alertUser.show();


    }

    public void SplitDetailLine(picktaskdetail tpicktaskdetail, double Qty) {

        mDbHelper.openReadableDatabase();
        int TranlineCount = mDbHelper.mTranlineCount();
        mDbHelper.closeDatabase();
        mDbHelper.openReadableDatabase();
        int DoclineCount = mDbHelper.mDoclineCount();
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

    private void listViewAlert() {
        List<String> itemList = new ArrayList<String>();
        mDbHelper.openReadableDatabase();
        itemList = mDbHelper.getItemList();
        mDbHelper.closeDatabase();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PickRepackIngredientsActivity.this);
        builderSingle.setTitle("Select SO item to Substitute");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PickRepackIngredientsActivity.this, android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < itemList.size(); i++) {
            arrayAdapter.add(itemList.get(i));
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edtPallet.setText("");
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                selectedItem = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(PickRepackIngredientsActivity.this);

                //new LoadRepackData(adapter).execute();

            }
        });
        builderSingle.show();
    }

    public void StatusLockAlert() {
        final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("This repack has been locked by VP user " + userName + ". You can only View.");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnOnHold.setEnabled(false);
                        Globals.OkPressed = true;
                        dialog.cancel();
                    }
                });


        alertUser.show();
    }

    public void StatusLockAlertDivice() {
        final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("This repack has been locked by scanner user " + userName + ". You can only View.");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnOnHold.setEnabled(false);
                        Globals.OkPressed = true;
                        dialog.cancel();
                    }
                });


        alertUser.show();
    }

    public void wlotContainPalletNo() {
        String UpdFlag = "", AddFlag = "";

        Double RIT_QTYUSED = Double.parseDouble(saveList.get(0).getRIT_QTYUSED());
        Double RIT_TRKQTYPK = Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK());
        String RIT_TRANLINENO = saveList.get(0).getRIT_TRANLINENO();
        String RIT_ITEM = saveList.get(0).getRIT_ITEM();
        String RIT_TEMPALLOC = saveList.get(0).getRIT_TEMPALLOC();

        if (QTYALERT_ADD) {
            allocDiffQty = enteredQty;
            tempAlloc2 = allocDiffQty;
            allocQty = String.valueOf(Double.parseDouble(enteredQty) + RIT_QTYUSED);
        } else {
            allocQty = String.valueOf(Double.parseDouble(enteredQty) - RIT_QTYUSED);
        }

        allocQtyForSave = allocQty;
        Double temp = RIT_TRKQTYPK;
        Double temp2 = Double.parseDouble(enteredQty);
        if ((Double.parseDouble(allocQtyForSave) > firstQty)) {

            if (QTYALERT_ADD) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Insufficient qty.");
                       // "Can't add,qty entered is more than available qty");
                edtPallet.setText("");
                edtQty.setText("");
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();

                btnCancelFinal.setEnabled(true);
                btnSaveFinal.setEnabled(true);
                btnCancel.setEnabled(false);


            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Insufficient qty.");
                        //"Can't override,qty entered is more than available qty");
                edtPallet.setText("");
                edtQty.setText("");
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();

                btnCancelFinal.setEnabled(true);
                btnSaveFinal.setEnabled(true);
                btnCancel.setEnabled(false);
            }

        } else if ((RIT_TRKQTYPK <= Double.parseDouble(enteredQty)) && (RIT_TRANLINENO != null && !RIT_TRANLINENO.contains("-"))) {

            if (QTYALERT_ADD) {
                new SaveRepackRawData(mUsername, allocDiffQty).execute();
            } else {
                new SaveRepackRawData(mUsername, allocQtyForSave).execute();
            }

        } else if ((RIT_TRKQTYPK >= Double.parseDouble(enteredQty)) && (RIT_TRANLINENO != null && !RIT_TRANLINENO.contains("-"))) {

            if (saveList.get(0).getRIT_TRANLINENO().contains("-")) {
                UpdFlag = "0";
                AddFlag = "1";
            } else {
                UpdFlag = "1";
                AddFlag = "0";
            }

            String allocQty2 = String.valueOf(Double.parseDouble(enteredQty) - RIT_TRKQTYPK);
            if (QTYALERT_ADD) {
                new SaveRepackRawData(mUsername, allocDiffQty).execute();
            } else {
                String tempalloc = "-" + RIT_TEMPALLOC;
                if (RIT_TEMPALLOC != null) {
                    if (Double.parseDouble(RIT_TEMPALLOC) != 0) {
                        new SaveRepackRawData(mUsername, tempalloc).execute();
                    }
                }
                mDbHelper.openWritableDatabase();
                mDbHelper.updateAllocQty(RIT_ITEM, saveList, allocQty2, enteredQty, UpdFlag, AddFlag, tempAlloc2, RIT_TRANLINENO);
                mDbHelper.closeDatabase();
            }
            mDbHelper.openReadableDatabase();
            repackFGList = mDbHelper.getRepackIngredients();
            mDbHelper.closeDatabase();


            adapter = new RepackIngredientsAdapter(PickRepackIngredientsActivity.this, repackFGList);
            transList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            edtPallet.setText("");
            edtQty.setText("");

            edtPallet.requestFocus();
            btnCancelFinal.setEnabled(true);
            btnSaveFinal.setEnabled(true);
            edtPallet.setEnabled(true);
            btnCancel.setEnabled(false);

        } else {
            if (QTYALERT_ADD) {
                new SaveRepackRawData(mUsername, enteredQty).execute();
            } else {
                new SaveRepackRawData(mUsername, allocQtyForSave).execute();
            }
        }
        btnOnHold.setEnabled(true);
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PickRepackIngredientsActivity.this);
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

    public String getRecordXmlExportPO(List<picktaskdetail> dList) {
        String exportPODataXml = "";
        try {
            ExportPickTask exportData = new ExportPickTask();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "PickTaskData" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickRepackIngredientsActivity.this, mDbHelper);
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

    public String getRecordXmlExportPOFinal(List<RepackIngredients> dList) {
        String exportPODataXml = "";
        try {
            ExportRepackData exportData = new ExportRepackData();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "RepackIngredients" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickRepackIngredientsActivity.this, mDbHelper);
            }
            sb.append("</" + "RepackIngredients" + ">");
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

    class CancelRepackFinal extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public CancelRepackFinal(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Cancelling the data..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_REPACK_CANCEL);
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
                info.setName("pPano");
                info.setValue(Globals.orgPano);
                //info.setValue(RepackNumber);
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
                String soap_action = NAMESPACE + METHOD_REPACK_CANCEL;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "01", "RepackDataCancel" + ".xml");
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

                deleteRepack();
                mDbHelper.openReadableDatabase();
                repackFG = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                repackFGAdapter = new RepackFGAdapter(PickRepackIngredientsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                adapter.notifyDataSetChanged();
                repackFGAdapter.notifyDataSetChanged();
                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.orgPano = "";

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "The server did not respond please try again");
            } else if (result.equalsIgnoreCase("time out error")) {
                new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "The server did not respond please try again");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "The server did not respond please try again");
            }
            dialog.cancel();
        }
    }

    class CancelRepack extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public CancelRepack(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Cancelling the data..");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {

                SoapObject request = new SoapObject(NAMESPACE, METHOD_REPACK_CANCEL);
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
                info.setName("pPano");
                info.setValue(Globals.orgPano);
                //info.setValue(RepackNumber);
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
                String soap_action = NAMESPACE + METHOD_REPACK_CANCEL;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "01", "RepackDataCancel" + ".xml");
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

                deleteRepack();

                mDbHelper.openReadableDatabase();
                repackFGList = mDbHelper.getRepackIngredients();
                wLotNoList = mDbHelper.getWlotNoList();

                adapter = new RepackIngredientsAdapter(PickRepackIngredientsActivity.this, repackFGList);
                transList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.orgPano = "";
                tvRepacknum.setText("");

                overridePendingTransition(0, 0);
                getIntent().putExtra("fromhold", "");
                getIntent().putExtra("dteCode", "");
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "The server did not respond please try again");
            } else if (result.equalsIgnoreCase("time out error")) {
                new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "The server did not respond please try again");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "The server did not respond please try again");
            }
            dialog.cancel();
        }
    }

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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
                toasttext = "Print PDF creation Success ";
                boolean isDataSentSuccess = true;
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
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
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent = Intent.createChooser(target, "Open File");
                                startActivity(intent);

                            } catch (Exception e) {
                                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                                        "No application match to open Print File");
                            }
                        }
                    }, 1000);

                } catch (Exception e) {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this, "Not Software Match to Open Print File");
                    mSupporter.simpleNavigateTo(PickRepackIngredientsActivity.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        toasttext);
                mSupporter.simpleNavigateTo(PickRepackIngredientsActivity.class);

            }
        } // end of PostExecute method...

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating PDF...");
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.dialog.setMessage(values[0]);
        }
    }

    class GetRepackData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetRepackData(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PEPACK_DATA);
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
                info.setName("pPano");
                info.setValue(RepackNumber);
                info.setType(String.class);
                request.addProperty(info);


                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_PEPACK_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "RepackData" + ".xml");
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
                    if (resultString.toString().contains("<vplocked>L-")) {

                        String[] vpLckAry = String.valueOf(resultString).split("<vplocked>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</vplocked>");

                        String lockByusr1 = vpLckAry1[0];

                        String[] vpLckAry2 = lockByusr1.split("-");

                        String firstVlu = vpLckAry2[0];

                        userName = vpLckAry2[1];

                        isLocked = true;

                    } else if (resultString.toString().contains("<vplocked>S-")) {

                        String[] vpLckAry = String.valueOf(resultString).split("<vplocked>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</vplocked>");

                        String lockByusr1 = vpLckAry1[0];

                        String[] vpLckAry2 = lockByusr1.split("-");

                        String firstVlu = vpLckAry2[0];

                        userName = vpLckAry2[1];


                        isdeviceSideLock = true;
                    } else {
                        isLocked = false;
                        isdeviceSideLock = false;
                    }
                    if (resultString.toString().contains("origpano")) {

                        String[] vpLckAry = String.valueOf(resultString).split("<origpano>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</origpano>");

                        String lockByusr1 = vpLckAry1[0];

                        Globals.orgPano = lockByusr1;

                    }
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
                Globals.RecallLstDTE_code = RepackNumber;
                new LoadRepackDataDteCode(adapter1).execute();
                btnCancel.setEnabled(false);
                edtPallet.requestFocus();

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Repack # not found");
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();

            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();

                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        Getmsg);
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();
            } else if (result.equals("Repack Completed.")) {


                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Repack completed. unable to proceed");
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();

            } else if (result.equalsIgnoreCase("time out error")) {
                new GetRepackData(mUsername).execute();
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Unable to proceed");
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();
            }
            dialog.cancel();
        }
    }

    //Repack list load the page
    class GetRepackRawData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetRepackRawData(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_RAW_DATA);
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
                info.setName("pWLotno");
                info.setValue(PalletNumber);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_RAW_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "RepackDataIngr" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().contains("No Data Found")) {

                    result = "No Data Found";

                } else if (resultString.toString().contains("false")) {
                    result = "Failed";
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
                new LoadRepackData().execute();

                btnSave.setEnabled(true);
                btnCancel.setEnabled(true);
                edtQty.setEnabled(true);
                btnSaveFinal.setEnabled(false);
                btnCancelFinal.setEnabled(false);
                btnOnHold.setEnabled(false);


            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Invalid pallet number");
                edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.requestFocus();
            } else if (result.equals("No Data Found")) {
                if (!isLocalData) {
                    mToastMessage.showToast(PickRepackIngredientsActivity.this, "Invalid pallet number.");
                    edtPallet.setText("");
                    edtQty.setText("");
                    btnSave.setEnabled(false);
                    edtPallet.setEnabled(true);
                    btnCancel.setEnabled(false);
                    edtPallet.requestFocus();
                }
                isLocalData = false;
                Globals.isLocalData=false;
            } else if (result.equalsIgnoreCase("time out error")) {
                new GetRepackRawData(mUsername).execute();
            } else {
                edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.requestFocus();
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        result);
            }
            dialog.cancel();
        }
    }

    private class LoadRepackDataDteCode extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;


        public LoadRepackDataDteCode(RepackFGAdapter repackFGAdapter) {
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
            repackFGAdapter = repackFGAdapter;
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            Globals.FROM_EDTREPACK = true ;

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickRepackIngredientsActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "RepackData" + ".xml");
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
                repackFG = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                if (isdeviceSideLock) {
                    StatusLockAlertDivice();
                } else if (isLocked) {
                    StatusLockAlert();
                }

                repackFGAdapter = new RepackFGAdapter(PickRepackIngredientsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();
                btnRecallLstDTE.setEnabled(false);
                btnCancel.setEnabled(true);
                edtRepackNum.setText(Globals.RecallLstDTE_code);
                edtRepackNum.setEnabled(false);
                Globals.FROM_EDTREPACK = false;
                repackNum = RepackNumber;
                lockUserName = userName;
                OrgPaNo = Globals.orgPano;
                DteCode = Globals.RecallLstDTE_code;

                if (isdeviceSideLock || isLocked) {
                    edtPallet.setEnabled(false);
                    edtQty.setEnabled(false);
                    btnSave.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnCancelFinal.setEnabled(true);
                    btnSaveFinal.setEnabled(false);
                } else {

                    btnOnHold.setEnabled(true);
                    btnSaveFinal.setEnabled(true);
                    btnCancelFinal.setEnabled(true);
                    edtPallet.setEnabled(true);
                    btnSave.setEnabled(false);
                    btnCancel.setEnabled(false);
                    edtPallet.requestFocus();
                }
                if (!Globals.orgPano.trim().equals("")) {
                    tvRepacknum.setText("Repack # " + Globals.orgPano.trim());
                } else {
                    tvRepacknum.setText("");
                }

                mDbHelper.openReadableDatabase();
                repackFGList = mDbHelper.getRepackIngredients();
                wLotNoList = mDbHelper.getWlotNoList();
                mDbHelper.closeDatabase();

                adapter = new RepackIngredientsAdapter(PickRepackIngredientsActivity.this, repackFGList);
                transList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            // this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }

    private class LoadRepackData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadRepackData() {
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickRepackIngredientsActivity.this, mDbHelper, Globals.gUsercode);

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

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "RepackDataIngr" + ".xml");
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
                ingredientsList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                mDbHelper.closeDatabase();
                if (ingredientsList.size()>0) {
                    if (!isLocalData) {

                        Double usdQty = Double.parseDouble(ingredientsList.get(0).getRIT_QTYUSED());
                        if (usdQty==0.0){
                            toTalQtY = Double.valueOf(ingredientsList.get(0).getRIT_REMARKS()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
                            edtQty.setText(String.valueOf(Math.round(toTalQtY)));
                        }else {
                            toTalQtY = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
                            edtQty.setText(String.valueOf(Math.round(toTalQtY)));
                        }

                    } else {
                        SetData();
                    }
                    boolean validationResult = validateDate();
                    if (validationResult) {

                        edtPallet.setText(PalletNumber.trim());
                        isLocalData = false;
                        Globals.isLocalData=false;
                        edtQty.requestFocus();
                        edtQty.setSelectAllOnFocus(true);
                        edtQty.selectAll();
                        edtPallet.setEnabled(false);
                    } else {

                        mDbHelper.openReadableDatabase();
                        Boolean isScaned = mDbHelper.isTranlineNullAvailable();
                        mDbHelper.closeDatabase();

                        mDbHelper.openReadableDatabase();
                        repackTotalList = mDbHelper.getWlotRepackIngredients(edtPallet.getText().toString());
                        mDbHelper.closeDatabase();

                        if (!Globals.isLocalData){

                            mDbHelper.openWritableDatabase();
                            mDbHelper.updatewhenCancel(edtPallet.getText().toString(),repackTotalList);
                            mDbHelper.closeDatabase();

                        }else if (isScaned){
                            mDbHelper.openWritableDatabase();
                            mDbHelper.DeleteRevertCancel(edtPallet.getText().toString());
                            mDbHelper.closeDatabase();
                        }


                        edtPallet.setText("");
                        edtQty.setText("");
                        btnSave.setEnabled(false);
                        edtPallet.requestFocus();
                    }
                }else{
                    mToastMessage.showToast(PickRepackIngredientsActivity.this,"No data available.");
                }
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            //    this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.setMessage("Please wait...");
            this.dialog.show();
        }
    }

    class SaveRepackRawData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;
        private String qty;

        public SaveRepackRawData(String user, String qty) {
            this.uCode = user;
            this.qty = qty;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_SAVE_RAW_DATA);
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
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWlotno");
                info.setValue(saveList.get(0).getRIT_WLOTNO());
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotno");
                info.setValue(saveList.get(0).getRIT_LOTNO());
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pPalno");
                info.setValue(saveList.get(0).getRIT_LOTREFID());
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("Item");
                info.setValue(saveList.get(0).getRIT_ITEM());
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pAllocQty");
                info.setValue(qty);
                info.setType(DecimalFormat.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot");
                info.setValue(saveList.get(0).getRIT_SLOT());
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_SAVE_RAW_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "RepackData" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().contains("Does not match")) {
                    result = "Does not match";
                } else if (resultString.toString().contains("Insufficient Qty")) {
                    result = "Insufficient Qty";
                } else if (resultString.toString().contains("false")) {
                    result = "Failed";
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
                isLocalData = false;
                Globals.isLocalData=false;
                int tempTran = 0, tempTran2 = 0;
                String tranNum = saveList.get(0).getRIT_TRANLINENO();
                if (tranNum == null) {
                    mDbHelper.openReadableDatabase();
                    tempTran = mDbHelper.getMaxTranNum();
                    mDbHelper.closeDatabase();
                    tempTran2 = tempTran + (-1);
                    saveList.get(0).setRIT_TRANLINENO(Integer.toString(tempTran2));
                    if (QTYALERT_ADD) {
                        saveList.get(0).setRIT_QTYUSED(allocQtyForSave);
                        saveList.get(0).setRIT_TEMPALLOC(allocQtyForSave);
                    } else {
                        saveList.get(0).setRIT_QTYUSED(enteredQty);
                        saveList.get(0).setRIT_TEMPALLOC(enteredQty);
                    }
                    saveList.get(0).setRIT_PANO(Globals.RecallLstDTE_code);
                   // saveList.get(0).setRIT_PANO(Globals.orgPano.trim());
                    saveList.get(0).setRIT_ORIGPANO(Globals.RecallLstDTE_code.trim());
                    saveList.get(0).setRIT_LOTEXPL("1");
                    saveList.get(0).setRIT_LINESPLIT("0");

                    mDbHelper.openWritableDatabase();
                    mDbHelper.updateFlagData(saveList.get(0).getRIT_ITEM(), saveList, saveList.get(0).getRIT_WLOTNO(), "1", "0");
                    mDbHelper.closeDatabase();
                } else {
                    String UpdFlag = "", AddFlag = "";
                    if (saveList.get(0).getRIT_TRANLINENO() != null && saveList.get(0).getRIT_TRANLINENO().contains("-")) {
                        UpdFlag = "0";
                        AddFlag = "1";
                    } else {
                        UpdFlag = "1";
                        AddFlag = "0";
                    }
                    if (Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()) < Double.parseDouble(enteredQty)) {

                    } else {

                    }


                    if (QTYALERT_ADD) {
                        String allocQty = enteredQty;
                        tempAllocFinal = enteredQty;
                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateRawItemQty(saveList.get(0).getRIT_ITEM(), saveList, allocQtyForSave, allocQty, UpdFlag, AddFlag, tempAllocFinal, saveList.get(0).getRIT_TRANLINENO());
                        mDbHelper.closeDatabase();
                    } else {
                        String allocQty = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()));
                        tempAllocFinal = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_QTYUSED()));
                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateRawItemQty(saveList.get(0).getRIT_ITEM(), saveList, enteredQty, allocQty, UpdFlag, AddFlag, tempAllocFinal, saveList.get(0).getRIT_TRANLINENO());
                        mDbHelper.closeDatabase();
                    }


                }

                mDbHelper.openReadableDatabase();
                repackFGList = mDbHelper.getRepackIngredients();
                mDbHelper.closeDatabase();

                adapter = new RepackIngredientsAdapter(PickRepackIngredientsActivity.this, repackFGList);
                transList.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                edtPallet.setText("");
                edtQty.setText("");

                edtPallet.setEnabled(true);
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
                btnCancelFinal.setEnabled(true);
                btnSaveFinal.setEnabled(true);
                btnOnHold.setEnabled(true);
                edtPallet.requestFocus();

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Invalid pallet number");
            } else if (result.equalsIgnoreCase("time out error")) {
                new SaveRepackRawData(mUsername, allocQtyForSave).execute();
            } else if (result.equalsIgnoreCase("Does not match")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Does not match with IC quantity table");
            } else if (result.equalsIgnoreCase("Insufficient Qty")) {
               // mToastMessage.showToast(PickRepackIngredientsActivity.this, "Insufficient qty in IC pallet table");
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Insufficient qty.");
                edtPallet.setText("");
                edtQty.setText("");
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();

                btnCancelFinal.setEnabled(true);
                btnSaveFinal.setEnabled(true);
                btnCancel.setEnabled(false);
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        result);
            }
            dialog.cancel();
        }
    }

    class ExportTranDataFinal extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public ExportTranDataFinal(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_DATA_FINAL);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/RepackData.xml");
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
                String soap_action = NAMESPACE + METHOD_EXPORT_DATA_FINAL;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "RepackDataResult" + ".xml");
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
                    result = resultString.toString();
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
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Repack updated");
                Globals.orgPano = "";

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteRepackData();
                mDbHelper.closeDatabase();

                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                tvRepacknum.setText("");

                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.fromExportData = true;
                Globals.RecallLstDTE_codeInc = "";

                overridePendingTransition(0, 0);
                getIntent().putExtra("dteCode", "");
                getIntent().putExtra("fromhold", "");
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranDataFinal(mSessionId, mUsername, mCompany).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Unable to update server");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Unable to update server. Please save again");
            }
            dialog.cancel();
        }
    }

/*
    class ExportTranData extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public ExportTranData(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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
                mSupporter.simpleNavigateTo(PickRepackIngredientsActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranDataFinal(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        result.toString());
            }

            dialog.cancel();
        }
    }
*/

    // Method that returns the XML to be exported

    public class uploadDataToServiceExportItmFinal extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItmFinal() {
            mDialog = new ProgressDialog(PickRepackIngredientsActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "RepackData" + ".xml");
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
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PickRepackIngredientsActivity.this);
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
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickRepackIngredientsActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }
}



