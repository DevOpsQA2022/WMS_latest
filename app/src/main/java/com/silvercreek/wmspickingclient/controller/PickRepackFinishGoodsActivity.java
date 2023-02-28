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
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.silvercreek.wmspickingclient.xml.ExportRepackDataFinish;

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

public class PickRepackFinishGoodsActivity extends AppBaseActivity {

    private ListView transList;
    private EditText edtPallet, edtQty;
    private TextView tvRepacknum,DTeNumber,tVlotNo;
    /*private TextView tvLotRfId;*/

    private List<picktaskdetail> exportTranList;
    private List<RepackFG> exportTranListFinal;

    String filename = "PickTaskPalletLabel.pdf";
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
    public String lockStatus = "";
    public String CasePerPallet = "";
    private TextView alertItem,alertDec,alertUm;
    private EditText edtUsdQty;
    private Button alertSave,alertCancel;

    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    private List<RepackIngredients> repackFGList;
    private List<RepackIngredients> finishedMaterial;
    private List<RepackIngredients> LotNo1;
    private List<RepackFG> repackFG,repackFGBackUp;
    private List<RepackFG> NewrepackFG;
    private List<RepackFG> LotNo;
    private List<RepackIngredients> saveList;
    private List<RepackIngredients> ingredientsList;
    private List wLotNoList;
    public Double IngredientsList = 0.0;

    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_EXPORT_DATA = "PickTask_SaveMain";
    public static final String METHOD_EXPORT_DATA_FINAL ="Repack_FinSave";
    public static final String METHOD_GET_PEPACK_DATA = "Repack_LookupData";
    public static final String METHOD_GET_PEPACK_FINISHED_DATA = "WaitingForDATA";
    public static final String METHOD_REPACK_CANCEL = "Repack_unlock";
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private RepackIngredientsAdapter adapter;

   // private RepackFInishedGoodsAdapter adapterFinish;

    private List<String> mPalletList;
    private ArrayList<picktaskWHIPTL> mPalletMast;
    private  ArrayList<String> lst_casePerPlt;
    private List<String> mLotList;
    private ArrayList<picktaskWHMQTY> mLotMast;
    private String StrFlag = "Y";
    private Boolean isTaskCompleted;
    private Boolean isTaskOnHold;
    private Boolean isItemAvailable;
    private List<picktaskPrintlabel> PrintLabelList;
    private String stop, trailer, route, dock, deldate, order, task, custid, custname, picker, palno;
    private String mPalno = "";
    private String PalletNumber = "", enteredQty,enteredQty1, allocQtyForSave;
    //private TextView,tvDesc txtWeight;
    private Button btnCancel, btnClose, btnSave,btnRecallLstDTE,btnOnHold,btnSaveFinal,btnCancelFinal;
    private Boolean isSameItem = false;
    private Boolean isProceed = true;
    private String uom = "", doctype = "", docno = "", doclineno = "", docstat = "", strweight = "", stkumid = "",
            orgdoclineno = "", volume = "", decnum = "", orgTranlineno = "", Lbshp = "", umfact = "", Tshipped = "", Trkshiped = "", LineSplit = "";
    private String ItemNo, strDesc;
    private String strTQty, strorgTQty;
    private String strTrkQty, strorgTrkQty;
    private String strCatchwt, strSlot, strLot, strwLotno;
    private String strTranlineNo;
    private Double dQty;
    private double dAvailQty;
    private Integer pickDuration;
    private double dSoQty = 0.0, dLotQty = 0.0;
    private boolean isMoreQty = true;
    public static final String METHOD_GET_RAW_DATA = "Repack_PickRawItem";
    public static final String METHOD_GET_FINSIHED_DATA = "Repack_ScanFinItem";
    public static final String METHOD_SAVE_RAW_DATA = "Repack_TempAlloc";
    public static String SOFT_KEYBOARD = "";
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
    private double entered_qty = 0;
    private double casePer_plt = 0;
    private Double totalQty = 0.0;
    private Double whQty = 0.0;
    private Double icQty = 0.0;
    private Double qtyUsed = 0.0;
    private Double totalQtY = 0.0;
    private Double toTalQtY = 0.0;
    private String tempAlloc2 = "";
    private boolean isLocked=false;
    private boolean isdeviceSideLock=false;
    private String lockUserName = "";
    private String OrgPaNo = "";
    private String DteCode = "";
    boolean QTYALERT_OVERRIDE = false;
    boolean QTYALERT_ADD = false;
    String allocQty = "";
    String allocDiffQty = "";
    String TempallocQty = "";
    String tempAllocFinal;
    private EditText edtRepackNum;
    private String repackNo = "";
    private String RepackNumber = "";
    public   String userName ="";
    private String intentRepackNum="";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_repack_finishgoods);

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
        tVlotNo = (TextView) findViewById(R.id.lotNo);
        /* tvLotRfId = (TextView) findViewById(R.id.tvLotrfId);*/
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

        edtPallet.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(15)});

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
        exportTranListFinal = mDbHelper.getRepackFG();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        lockStatus = mDbHelper.getLockStatus(repackNum.trim());
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        repackFG = mDbHelper.getRepackFG();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        finishedMaterial= mDbHelper.getRepackFinishMaterials();
        mDbHelper.closeDatabase();

        edtRepackNum.requestFocus();

        String fromhold1 = this.getIntent().getStringExtra("fromholdfin");
        if (!fromhold1.equals("") && fromhold1 != null){
            if (fromhold1.equals("holdTruefin")){
                btnOnHold.setEnabled(true);
                btnRecallLstDTE.setEnabled(false);
            }
        }


        if(isLocked){
            edtPallet.setEnabled(false);
            edtQty.setEnabled(false);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
            // StatusLockAlert();
        }else if (isdeviceSideLock){
            edtPallet.setEnabled(false);
            edtQty.setEnabled(false);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
            //StatusLockAlertDivice();
        }
        /*edtRepackNum.setText(Globals.RecallLstDTE_code);*/
        if (!Globals.orgPanofin.trim().equals("")){
            tvRepacknum.setText("Repack # " + Globals.orgPanofin.trim());
        }else{
            tvRepacknum.setText("");
        }


        DTeNumber.setText("Repack # " + DteCode.trim());

        btnSave.setEnabled(false);
        btnCancel.setEnabled(false);
        edtQty.setEnabled(false);
        edtPallet.setEnabled(false);

        mDbHelper.openReadableDatabase();
        repackFGList = mDbHelper.getRepackIngredients();
        wLotNoList = mDbHelper.getWlotNoList();
        mDbHelper.closeDatabase();


        if(exportTranListFinal.size()>0){

            btnOnHold.setEnabled(true);
            edtPallet.setEnabled(true);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
        }else {
            intentRepackNum = this.getIntent().getExtras().getString("dteCodefin");
            btnSave.setEnabled(false);
            btnOnHold.setEnabled(false);
            btnSaveFinal.setEnabled(false);
            btnCancelFinal.setEnabled(false);
            //edtPallet.setEnabled(false);
        }

        if(repackFG.size()>0){
            btnCancel.setEnabled(false);
            btnOnHold.setEnabled(true);
            btnRecallLstDTE.setEnabled(false);
            edtRepackNum.setEnabled(false);

        }else if (isLocked && isdeviceSideLock){
            btnRecallLstDTE.setEnabled(false);
            //edtRepackNum.setText(Repacknum);
            edtRepackNum.setText(Globals.RecallLstDTE_codeFin);
            edtRepackNum.setEnabled(false);
            btnCancel.setEnabled(false);
            btnOnHold.setEnabled(false);
            btnSaveFinal.setEnabled(false);
        }else if (exportTranListFinal.size()==0&& intentRepackNum != null && !intentRepackNum.equals("")){
            intentRepackNum = this.getIntent().getExtras().getString("dteCodefin");
            btnSave.setEnabled(false);
            btnOnHold.setEnabled(false);
            btnCancel.setEnabled(true);
            edtRepackNum.setText(intentRepackNum);
            edtRepackNum.setEnabled(false);
            edtPallet.requestFocus();

        }else {

            btnCancel.setEnabled(false);
            String fromhold12 = this.getIntent().getStringExtra("fromholdfin");
            if (!fromhold12.equals("") && fromhold12 != null){
                if (fromhold12.equals("holdTruefin")){
                    btnOnHold.setEnabled(true);
                    btnRecallLstDTE.setEnabled(false);
                }else {
                    btnRecallLstDTE.setEnabled(true);
                    btnOnHold.setEnabled(false);
                }
            }




        }
        repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
        transList.setAdapter(repackFGAdapter);

       /* adapterFinish = new RepackFInishedGoodsAdapter(PickRepackFinishGoodsActivity.this, finishedMaterial);
        transList.setAdapter(adapterFinish);
        adapterFinish.notifyDataSetChanged();*/



       /* adapter = new RepackIngredientsAdapter(PickRepackFinishGoodsActivity.this, repackFGList);
        transList.setAdapter(adapter);
        adapter.notifyDataSetChanged();*/ //11-10-2022

        intentRepackNum = this.getIntent().getExtras().getString("dteCodefin");
        if (intentRepackNum != null) {
            if (!intentRepackNum.equals("")) {
                edtRepackNum.setText(intentRepackNum);
                edtRepackNum.setEnabled(false);
                edtPallet.requestFocus();
            } else {
                edtRepackNum.requestFocus();
            }
        } else {
            edtRepackNum.requestFocus();
        }


        transList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isdeviceSideLock && !isLocked) {
                    RepackFG repackFG1 = new RepackFG();
                    repackFG1 = (RepackFG) transList.getItemAtPosition(i);

                    final String tran = repackFG1.getREPACKFG_TRANLINENO();
                    final String item = repackFG1.getREPACKFG_ITEM();
                    final String Qty = repackFG1.getREPACKFG_QTYMADE();
                    final String uom = repackFG1.getREPACKFG_UMEASUR();
                    final String desc = repackFG1.getREPACKFG_DESCRIP();
                    final String addUser = repackFG1.getREPACKFG_ADDUSER();
                    final String casePerPallet = repackFG1.getREPACKFG_CASE_PL();

                    LayoutInflater li = LayoutInflater.from(PickRepackFinishGoodsActivity.this);
                    View promptsView = li.inflate(R.layout.layout_editalert, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            PickRepackFinishGoodsActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    final AlertDialog edtlinealert = alertDialogBuilder.create();
                    edtlinealert.setCancelable(false);
                    alertSave = (Button) promptsView.findViewById(R.id.edtSave_btn);
                    alertCancel = (Button) promptsView.findViewById(R.id.edtCancel_btn);
                    edtUsdQty = (EditText) promptsView.findViewById(R.id.edtlin_qty);
                    alertDec = (TextView) promptsView.findViewById(R.id.edtlin_Pallets);
                    alertItem = (TextView) promptsView.findViewById(R.id.edtlin_itemDescrib);
                    alertUm = (TextView) promptsView.findViewById(R.id.edtlin_umeasur);

                    if (SOFT_KEYBOARD.equals("CHECKED")) {
                        edtUsdQty.setShowSoftInputOnFocus(false);
                    } else {
                        edtUsdQty.setShowSoftInputOnFocus(true);
                    }

                    edtlinealert.show();


                    alertItem.setText(item);
                    alertUm.setText(uom);
                    alertDec.setText(desc);
                    edtUsdQty.setText(String.valueOf(Math.round(Double.parseDouble(Qty))));
                    edtUsdQty.selectAll();
                    edtUsdQty.requestFocus();

                    alertSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            enteredQty = edtUsdQty.getText().toString();

                            if (!enteredQty.equals(null) && !enteredQty.equals("") ){
                                entered_qty = Double.parseDouble(enteredQty);
                            }



                            if(casePerPallet!=null){
                                casePer_plt = Double.parseDouble(casePerPallet);
                            }else {
                                casePer_plt = 0;
                            }


                            if (entered_qty<=casePer_plt || casePer_plt ==0 ){

                                if(item.equalsIgnoreCase("")) {
                                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                            "Please scan the item");
                                }
                                else if (enteredQty.equalsIgnoreCase("")) {
                                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                            "Please enter the qty");
                                    edtQty.requestFocus();
                                } else if (Integer.parseInt(enteredQty) <= 0) {
                                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                            "Please enter qty greater then zero");
                                    edtQty.requestFocus();
                                } else {
                                    mDbHelper.openWritableDatabase();
                                    mDbHelper.updateFinishMaterialListClickQty(item, enteredQty, tran, addUser);
                                    mDbHelper.closeDatabase();
                                }

                                mDbHelper.openReadableDatabase();
                                repackFG = mDbHelper.getRepackFG();
                                mDbHelper.closeDatabase();

                                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                                transList.setAdapter(repackFGAdapter);
                                repackFGAdapter.notifyDataSetChanged();

                                edtlinealert.dismiss();

                            }else {
                                double lines = entered_qty /casePer_plt;

                                String split[] = String.valueOf(lines).split("\\.");
                                String afterDecimal = split[1];

                                int length = 0;

                                if(!afterDecimal.equals("0")){
                                    length  = Integer.parseInt(split[0]) + 1;
                                }else{
                                    length = Integer.parseInt(split[0]);
                                }

                                lst_casePerPlt = new ArrayList<>();

                                double qty = 0;
                                for (int i = 0; i < length ;i++){
                                    qty = qty + casePer_plt;
                                    if(qty > entered_qty){
                                        double eQty = qty - casePer_plt;
                                        double aQty =  entered_qty - eQty;
                                        lst_casePerPlt.add(String.valueOf(aQty));
                                        break;
                                    }
                                    lst_casePerPlt.add(String.valueOf(casePer_plt));
                                }


                                for (int i=0; i<lst_casePerPlt.size(); i++){


                                    mDbHelper.openReadableDatabase();
                                    saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                                    mDbHelper.closeDatabase();

                                    mDbHelper.openReadableDatabase();
                                    NewrepackFG = mDbHelper.getRepackFGForAlertSave(item,tran);
                                    mDbHelper.closeDatabase();

                                    if(NewrepackFG.size() > 0){
                                        repackFGBackUp = NewrepackFG;
                                    }
                                    mDbHelper.openReadableDatabase();
                                    int tranlineNo = mDbHelper.mTranlineCountRepackFG();
                                    mDbHelper.closeDatabase();

                                    if(tranlineNo >= 0 ) {
                                        tranlineNo = tranlineNo + 1;
                                    }else{
                                        tranlineNo = 1;
                                    }


                                    if(item.equalsIgnoreCase("")) {
                                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                                "Please scan the item");
                                    }else if (lst_casePerPlt.get(i).equalsIgnoreCase("")) {
                                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                                "Please enter the qty");
                                        mDbHelper.openWritableDatabase();
                                        mDbHelper.deleteRepackDataCancel();
                                        mDbHelper.closeDatabase();
                                        edtQty.requestFocus();
                                    }else if (Double.parseDouble(lst_casePerPlt.get(i)) <= 0){
                                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                                "Please enter qty greater then zero");

                                        mDbHelper.openWritableDatabase();
                                        mDbHelper.deleteRepackDataCancel();
                                        mDbHelper.closeDatabase();

                                        edtQty.requestFocus();

                                    }else {

                                        if(i==0){
                                            mDbHelper.openWritableDatabase();
                                            mDbHelper.updateFinishMaterialListClickQty(item, lst_casePerPlt.get(i), tran, addUser);
                                            mDbHelper.closeDatabase();
                                        }else {

                                            mDbHelper.openWritableDatabase();
                                            mDbHelper.addAndUpdtRepackFG(NewrepackFG.get(0).getREPACKFG_ITEM(),lst_casePerPlt.get(i),String.valueOf(tranlineNo),Globals.RepackNum.trim(),repackFGBackUp);
                                            mDbHelper.closeDatabase();
                                        }


                                    }
                                }

                                mDbHelper.openReadableDatabase();
                                repackFG = mDbHelper.getRepackFG();
                                mDbHelper.closeDatabase();

                                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                                transList.setAdapter(repackFGAdapter);
                                repackFGAdapter.notifyDataSetChanged();

                                edtlinealert.dismiss();

                            }
                        }
                    });


                    alertCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            edtlinealert.dismiss();
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            repackFGAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

    });

        edtRepackNum.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            isdeviceSideLock=false;
            isLocked=false;

            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:

                        RepackNumber = edtRepackNum.getText().toString();
                        RepackNumber = fixedLengthString(RepackNumber);

                        Globals.RepackNum =  RepackNumber;

                        mDbHelper.openReadableDatabase();
                        Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                        mDbHelper.closeDatabase();

                        if(RepackNumber.equalsIgnoreCase("          ")){
                            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                    // "Please Enter or Scan the Repack #");
                                    "Please scan or enter the Repack # ");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    edtRepackNum.requestFocus();

                                }
                            }, 150);

                            scanResult=true;

                            //btn_pickRepack.setEnabled(true);--07-10-2022
                        } else {

                            new GetRepackData(mUsername).execute();


                        }
                    default:
                        break;
                }
            }else if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_TAB)){

                if (keyCode == KeyEvent.KEYCODE_TAB) {
                    RepackNumber = edtRepackNum.getText().toString();
                    RepackNumber = fixedLengthString(RepackNumber);

                    mDbHelper.openReadableDatabase();
                    Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                    mDbHelper.closeDatabase();

                    if (RepackNumber.equalsIgnoreCase("          ")) {
                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                // "Please Scan or Enter the Repack #");
                                "Empty");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                edtRepackNum.requestFocus();

                            }
                        }, 150);
                        scanResult = true;

                        // btn_pickRepack.setEnabled(true);  07-10-2022


                        // edtRepackNum.requestFocus();
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
            repackFG = mDbHelper.getRepackFG();
            mDbHelper.closeDatabase();

            if(repackFG.size() > 0) {
                Globals.holdTaskNum = Globals.gTaskNo;
                taskStatus = "ONHOLD";
                Intent theIntent = new Intent(PickRepackFinishGoodsActivity.this, RepackMainMenuScreen.class);
                theIntent.putExtra("repacknumfin", "");
                theIntent.putExtra("fromholdfin", "holdTruefin");
                theIntent.putExtra("dteCodefin", Globals.RecallLstDTE_codeFin);
                theIntent.putExtra("fromFinish", "fromFinish");
                startActivity(theIntent);
            }else{
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "No data available to hold");
            }
        }
    });

        btnCancelFinal.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mDbHelper.openReadableDatabase();
            exportTranListFinal = mDbHelper.getRepackFG();
            mDbHelper.closeDatabase();
            deleteAlert();

                /*if(exportTranListFinal.size()>0){

                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                            "Please save the existing transaction.");

                }else{
                    deleteAlert();
                }*/
        }
    });


        edtPallet.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if ((event.getAction() == KeyEvent.ACTION_DOWN)
                    && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:

                        PalletNumber = edtPallet.getText().toString();
                        //PalletNumber = fixedLengthString(PalletNumber);

                        if (PalletNumber.equalsIgnoreCase("")) {
                            mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Please scan or enter the item");
                            scanResult = true;

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    edtPallet.requestFocus();
                                }
                            }, 150);
                        } else{
                            new GetRepackFinishedData(mUsername).execute();

                        }

                            /*if (PalletNumber.equalsIgnoreCase("")) {
                                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Please Scan or Enter the Pallet");
                                scanResult = true;
                                edtPallet.requestFocus();
                            } else if (wLotNoList.contains(PalletNumber)) {
                                isLocalData = true;
                                new GetRepackRawData(mUsername).execute();
                            } else {
                                Supporter.SUMQTY = false;
                                new GetRepackRawData(mUsername).execute();
                            }*/

                    default:
                        break;
                }
            }
            return false;
            //return true;
        }
    });

        btnClose.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //mSupporter.simpleNavigateTo(PickRepackActivity.class);
            Intent theIntent = new Intent(PickRepackFinishGoodsActivity.this, PickRepackActivity.class);
            theIntent.putExtra("repacknumfin", repackNum);
            theIntent.putExtra("lockstatus",isLocked);
            theIntent.putExtra("isdeviceSidelock",isdeviceSideLock);
            theIntent.putExtra("rpNum",repackNum);
            theIntent.putExtra("dteCodefin",DteCode);
            theIntent.putExtra("lockUser",lockUserName);

            //lockUser
            startActivity(theIntent);

            //cancelAlert();
        }
    });


        btnCancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            edtPallet.setText("");
            edtQty.setText("");
            tVlotNo.setText("");
            edtPallet.setEnabled(true);
            edtPallet.requestFocus();
            btnSave.setEnabled(false);
            edtQty.setEnabled(false);
            btnCancel.setEnabled(false);

            btnSaveFinal.setEnabled(true);
            btnCancelFinal.setEnabled(true);
            btnOnHold.setEnabled(true);

            mDbHelper.openWritableDatabase();
            mDbHelper.deleteRepackDataCancel();
            mDbHelper.closeDatabase();



        }
    });

        btnRecallLstDTE.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {



            repackNo = Globals.RecallLstDTE_codeFin;

            edtRepackNum.setText(repackNo);

            // RepackNumber = edtRepackNum.getText().toString();


            RepackNumber = repackNo;
            RepackNumber = fixedLengthString(RepackNumber);

            Globals.RepackNum = RepackNumber;

            mDbHelper.openReadableDatabase();
            Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
            mDbHelper.closeDatabase();

            if(RepackNumber.equalsIgnoreCase("          ")){
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Please scan or enter the Repack #");
                //"btnValue empty");
                edtRepackNum.requestFocus();
                // "Please Enter or Scan the Repack #");
                scanResult=true;

                // btn_pickRepack.setEnabled(true);



            } /*else if (!result){
                    mToastMessage.showToast(PickRepackActivity.this,
                         //   "Invalid Repack");
                            "Invalid DTE Code");
                    btn_pickRepack.setEnabled(true);
                }*/ else {

                new GetRepackData(mUsername).execute();


            }
        }
    });


        btnSave.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v){

            enteredQty = edtQty.getText().toString();
            enteredQty1 = edtPallet.getText().toString();

            mDbHelper.openReadableDatabase();
            CasePerPallet = mDbHelper.getCasePerPallet(enteredQty1.trim());
            mDbHelper.closeDatabase();

            if (!enteredQty.equals(null) && !enteredQty.equals("") ){
                entered_qty = Double.parseDouble(enteredQty);
            }


            if (CasePerPallet!=null){
                casePer_plt = Double.parseDouble(CasePerPallet);
            }else {
                casePer_plt = 0;
            }


        //  caspePerPallet(entered_qty, casePer_plt, "QTYSAVE");

                if (entered_qty<=casePer_plt || casePer_plt==0 ){

                    mDbHelper.openReadableDatabase();
                    saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                    mDbHelper.closeDatabase();

                    mDbHelper.openReadableDatabase();
                    repackFG = mDbHelper.getRepackFG();
                    mDbHelper.closeDatabase();

                    mDbHelper.openReadableDatabase();
                    int tranlineNo = mDbHelper.mTranlineCountRepackFG();
                    mDbHelper.closeDatabase();

                    if(tranlineNo >= 0 ) {
                        tranlineNo = tranlineNo + 1;
                    }else{
                        tranlineNo = 1;
                    }

                    if(enteredQty1.equalsIgnoreCase("")) {
                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                "Please scan the item");
                    }else if (enteredQty.equalsIgnoreCase("")) {
                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                "Please enter the qty");
                        mDbHelper.openWritableDatabase();
                        mDbHelper.deleteRepackDataCancel();
                        mDbHelper.closeDatabase();
                        edtQty.requestFocus();
                    }else if (Integer.parseInt(enteredQty) <= 0){
                        mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                "Please enter qty greater then zero");

                        mDbHelper.openWritableDatabase();
                        mDbHelper.deleteRepackDataCancel();
                        mDbHelper.closeDatabase();

                        edtQty.requestFocus();

                    }else {

                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateFinishMaterialQty(PalletNumber.trim(),enteredQty,String.valueOf(tranlineNo),Globals.RepackNum.trim());
                        mDbHelper.closeDatabase();

                    }
                    mDbHelper.openReadableDatabase();
                    repackFG = mDbHelper.getRepackFG();
                    mDbHelper.closeDatabase();

                    repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                    transList.setAdapter(repackFGAdapter);
                    repackFGAdapter.notifyDataSetChanged();


                    btnCancel.setEnabled(false);
                    edtPallet.setEnabled(true);
                    edtPallet.requestFocus();
                    edtPallet.setText("");
                    edtQty.setText("");
                    edtQty.setEnabled(false);
                    btnSave.setEnabled(false);
                    tVlotNo.setText("");
                    btnSaveFinal.setEnabled(true);
                    btnCancelFinal.setEnabled(true);
                    btnOnHold.setEnabled(true);

                }else {

                    double lines = entered_qty /casePer_plt;

                    String split[] = String.valueOf(lines).split("\\.");
                    String afterDecimal = split[1];

                    int length = 0;

                    if(!afterDecimal.equals("0")){
                        length  = Integer.parseInt(split[0]) + 1;
                    }else{
                        length = Integer.parseInt(split[0]);
                    }

                    lst_casePerPlt = new ArrayList<>();

                    double qty = 0;
                    for (int i = 0; i < length ;i++){
                        qty = qty + casePer_plt;
                        if(qty > entered_qty){
                            double eQty = qty - casePer_plt;
                             double aQty =  entered_qty - eQty;
                            lst_casePerPlt.add(String.valueOf(aQty));
                           break;
                        }
                        lst_casePerPlt.add(String.valueOf(casePer_plt));
                    }


                    for (int i=0; i<lst_casePerPlt.size(); i++){


                        mDbHelper.openReadableDatabase();
                        saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                        mDbHelper.closeDatabase();

                        mDbHelper.openReadableDatabase();
                        repackFG = mDbHelper.getRepackFG_backUp();
                        mDbHelper.closeDatabase();

                        if(repackFG.size() > 0){
                            repackFGBackUp = repackFG;
                        }
                        mDbHelper.openReadableDatabase();
                        int tranlineNo = mDbHelper.mTranlineCountRepackFG();
                        mDbHelper.closeDatabase();

                        if(tranlineNo >= 0 ) {
                            tranlineNo = tranlineNo + 1;
                        }else{
                            tranlineNo = 1;
                        }


                        if(enteredQty1.equalsIgnoreCase("")) {
                            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                    "Please scan the item");
                        }else if (lst_casePerPlt.get(i).equalsIgnoreCase("")) {
                            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                    "Please enter the qty");
                            mDbHelper.openWritableDatabase();
                            mDbHelper.deleteRepackDataCancel();
                            mDbHelper.closeDatabase();
                            edtQty.requestFocus();
                        }else if (Double.parseDouble(lst_casePerPlt.get(i)) <= 0){
                            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                    "Please enter qty greater then zero");

                            mDbHelper.openWritableDatabase();
                            mDbHelper.deleteRepackDataCancel();
                            mDbHelper.closeDatabase();

                            edtQty.requestFocus();

                        }else {

                            if(i==0){
                                mDbHelper.openWritableDatabase();
                                mDbHelper.updateFinishMaterialQty(PalletNumber.trim(),lst_casePerPlt.get(i),String.valueOf(tranlineNo),Globals.RepackNum.trim());
                                mDbHelper.closeDatabase();
                            }else {


                                mDbHelper.openWritableDatabase();
                                mDbHelper.addAndUpdtRepackFG(PalletNumber.trim(),lst_casePerPlt.get(i),String.valueOf(tranlineNo),Globals.RepackNum.trim(),repackFGBackUp);
                                mDbHelper.closeDatabase();
                            }


                        }
                    }


                    mDbHelper.openReadableDatabase();
                    repackFG = mDbHelper.getRepackFG();
                    mDbHelper.closeDatabase();


                    repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                    transList.setAdapter(repackFGAdapter);
                    repackFGAdapter.notifyDataSetChanged();


                    btnCancel.setEnabled(false);
                    edtPallet.setEnabled(true);
                    edtPallet.requestFocus();
                    edtPallet.setText("");
                    edtQty.setText("");
                    edtQty.setEnabled(false);
                    btnSave.setEnabled(false);
                    tVlotNo.setText("");
                    btnSaveFinal.setEnabled(true);
                    btnCancelFinal.setEnabled(true);
                    btnOnHold.setEnabled(true);
                }
        }

        });


    }


/*
    private void caspePerPallet(Double entered_qty,Double casePer_plt,String saveFrom){

        if (entered_qty<=casePer_plt){

            mDbHelper.openReadableDatabase();
            saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
            mDbHelper.closeDatabase();

            mDbHelper.openReadableDatabase();
            repackFG = mDbHelper.getRepackFG();
            mDbHelper.closeDatabase();

            mDbHelper.openReadableDatabase();
            int tranlineNo = mDbHelper.mTranlineCountRepackFG();
            mDbHelper.closeDatabase();

            if(tranlineNo >= 0 ) {
                tranlineNo = tranlineNo + 1;
            }else{
                tranlineNo = 1;
            }

            if(enteredQty1.equalsIgnoreCase("")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Please scan the item");
            }else if (enteredQty.equalsIgnoreCase("")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Please enter the qty");
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteRepackDataCancel();
                mDbHelper.closeDatabase();
                edtQty.requestFocus();
            }else if (Integer.parseInt(enteredQty) <= 0){
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Please enter qty greater then zero");

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteRepackDataCancel();
                mDbHelper.closeDatabase();

                edtQty.requestFocus();

            }else {


                mDbHelper.openWritableDatabase();
                mDbHelper.updateFinishMaterialQty(PalletNumber.trim(),enteredQty,String.valueOf(tranlineNo),Globals.RepackNum.trim());
                mDbHelper.closeDatabase();

            }
            mDbHelper.openReadableDatabase();
            repackFG = mDbHelper.getRepackFG();
            mDbHelper.closeDatabase();

            repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
            transList.setAdapter(repackFGAdapter);
            repackFGAdapter.notifyDataSetChanged();


            btnCancel.setEnabled(false);
            edtPallet.setEnabled(true);
            edtPallet.requestFocus();
            edtPallet.setText("");
            edtQty.setText("");
            edtQty.setEnabled(false);
            btnSave.setEnabled(false);
            tVlotNo.setText("");
            btnSaveFinal.setEnabled(true);
            btnCancelFinal.setEnabled(true);
            btnOnHold.setEnabled(true);

        }else {

            double lines = entered_qty /casePer_plt;

            String split[] = String.valueOf(lines).split("\\.");
            String afterDecimal = split[1];

            int length = 0;

            if(!afterDecimal.equals("0")){
                length  = Integer.parseInt(split[0]) + 1;
            }else{
                length = Integer.parseInt(split[0]);
            }

            lst_casePerPlt = new ArrayList<>();

            double qty = 0;
            for (int i = 0; i < length ;i++){
                qty = qty + casePer_plt;
                if(qty > entered_qty){
                    double eQty = qty - casePer_plt;
                    double aQty =  entered_qty - eQty;
                    lst_casePerPlt.add(String.valueOf(aQty));
                    break;
                }
                lst_casePerPlt.add(String.valueOf(casePer_plt));
            }


            for (int i=0; i<lst_casePerPlt.size(); i++){


                mDbHelper.openReadableDatabase();
                saveList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                mDbHelper.closeDatabase();

                mDbHelper.openReadableDatabase();
                repackFG = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();
                if(repackFG != null){
                    repackFGBackUp = repackFG;
                }
                mDbHelper.openReadableDatabase();
                int tranlineNo = mDbHelper.mTranlineCountRepackFG();
                mDbHelper.closeDatabase();

                if(tranlineNo >= 0 ) {
                    tranlineNo = tranlineNo + 1;
                }else{
                    tranlineNo = 1;
                }


                if(enteredQty1.equalsIgnoreCase("")) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                            "Please scan the item");
                }else if (lst_casePerPlt.get(i).equalsIgnoreCase("")) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                            "Please enter the qty");
                    mDbHelper.openWritableDatabase();
                    mDbHelper.deleteRepackDataCancel();
                    mDbHelper.closeDatabase();
                    edtQty.requestFocus();
                }else if (Double.parseDouble(lst_casePerPlt.get(i)) <= 0){
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                            "Please enter qty greater then zero");

                    mDbHelper.openWritableDatabase();
                    mDbHelper.deleteRepackDataCancel();
                    mDbHelper.closeDatabase();

                    edtQty.requestFocus();

                }else {

                    if(i==0){
                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateFinishMaterialQty(PalletNumber.trim(),lst_casePerPlt.get(i),String.valueOf(tranlineNo),Globals.RepackNum.trim());
                        mDbHelper.closeDatabase();
                    }else {


                        mDbHelper.openWritableDatabase();
                        mDbHelper.addAndUpdtRepackFG(PalletNumber.trim(),lst_casePerPlt.get(i),String.valueOf(tranlineNo),Globals.RepackNum.trim(),repackFGBackUp);
                        mDbHelper.closeDatabase();
                    }


                }
            }


            mDbHelper.openReadableDatabase();
            repackFG = mDbHelper.getRepackFG();
            mDbHelper.closeDatabase();


            repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
            transList.setAdapter(repackFGAdapter);
            repackFGAdapter.notifyDataSetChanged();


            btnCancel.setEnabled(false);
            edtPallet.setEnabled(true);
            edtPallet.requestFocus();
            edtPallet.setText("");
            edtQty.setText("");
            edtQty.setEnabled(false);
            btnSave.setEnabled(false);
            tVlotNo.setText("");
            btnSaveFinal.setEnabled(true);
            btnCancelFinal.setEnabled(true);
            btnOnHold.setEnabled(true);
        }



    }
*/

    private void deleteAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackFinishGoodsActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //new GetPickTaskList(mUsername).execute();
                        //listViewAlert();
                        // btn_pickRepack.setEnabled(true);
                        //repackNum.setText("");

                        if (isdeviceSideLock){
                            deleteRepack();
                            mDbHelper.openReadableDatabase();
                            repackFG = mDbHelper.getRepackFG();
                            mDbHelper.closeDatabase();


                            repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                            transList.setAdapter(repackFGAdapter);
                            repackFGAdapter.notifyDataSetChanged();
                            edtRepackNum.setEnabled(true);
                            edtPallet.setEnabled(false);
                            edtRepackNum.setText("");
                            tvRepacknum.setText("");
                            // btnIngredients.setEnabled(false);
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
                            btnCancelFinal.setEnabled(false);
                            btnOnHold.setEnabled(false);
                            edtRepackNum.requestFocus();

                        }else {
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

    private void deleteRepack(){
        mDbHelper.openWritableDatabase();
        mDbHelper.deleteRepackDataFinished();
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

        totalQty = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
        whQty = Double.valueOf(ingredientsList.get(0).getRIT_WHQTY());
        icQty = Double.valueOf(ingredientsList.get(0).getRIT_ICQTY());
        qtyUsed = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED());

        if (qtyUsed <= 0&&!isLocalData) {
            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "No qty available on the " + PalletNumber + " pallet");
            result = false;
        } else if (totalQty <= 0&&!isLocalData) {
            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "Qty less allocation insufficient on the " + PalletNumber + "  pallet");
            result = false;
        } /*else if (whQty < totalQty) {
            mToastMessage.showToast(PickRepackIngredientsActivity.this,
                    "Insufficient quantity in WH Quantity table for the Pallet " + PalletNumber);
            result = false;
2        }*/ else if (icQty < totalQty) {
            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "Insufficient quantity in IC quantity table for the Pallet " + PalletNumber);
            result = false;
        } else if (Double.parseDouble(enteredQty) > firstQty) {

            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "Qty entered is more than available qty for the Pallet " + PalletNumber);
            edtQty.requestFocus();
            result = false;
        }
        return result;
    }


    class LogoutRequest extends AsyncTask<String, String, String> {
        private String pUsername, pSessionId, pCompId,pDeviceId ;


        String result = "";



        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId ) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


        }

        @Override
        protected void onPreExecute() {
            Log.d("123123","start");

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.d("123123","start");

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
                Log.d("123123","start");

                ht.call(soap_action, envelope);
                Log.d("123123","start");

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
                Log.e("tag", "error", e);

                result = "time out error";
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("tag", "error", e);

                result = "input error";
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.e("tag", "error", e);

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
            Log.d("123123","post");

            if (result.equals("success")) {

//                mToastMessage.showToast(SelectCompanyActivity.this,
//                        "success");
                Intent theIntent = new Intent(PickRepackFinishGoodsActivity.this, LoginScreenActivity.class);

                startActivity(theIntent);


            } else if (result.equals("server failed")) {
//                mToastMessage.showToast(SelectWarehouseActivity.this,
//                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
//                mToastMessage.showToast(SelectWarehouseActivity.this,
//                        "Unable to update Server");
            } else {
//                mToastMessage.showToast(SelectWarehouseActivity.this,
//                        "Unable to update Server. Please Save again");
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Unable to update Server.");
            }

        }
    }

    class CancelRepackFinal extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public CancelRepackFinal(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                info.setValue(Globals.orgPanofin);
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
                repackFG= mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();

                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                //  btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.orgPanofin="";




            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
            }else if (result.equalsIgnoreCase("time out error")){
                new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
                //  "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }

    class CancelRepack extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public CancelRepack(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                info.setValue(Globals.orgPanofin);
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
                repackFG= mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();
                repackFGAdapter.notifyDataSetChanged();
                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                //  btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.orgPanofin="";
                tvRepacknum.setText("");

                overridePendingTransition( 0, 0);
                getIntent().putExtra("fromholdfin", "");
                getIntent().putExtra("dteCodefin", "");
                startActivity(getIntent());
                overridePendingTransition( 0, 0);


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
            }else if (result.equalsIgnoreCase("time out error")){
                new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
                //  "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }

    class CancelRepackForAlert extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public CancelRepackForAlert(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                info.setValue(Globals.orgPanofin);
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
                repackFG= mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();
                repackFGAdapter.notifyDataSetChanged();
                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                //  btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.orgPanofin="";
                tvRepacknum.setText("");

                overridePendingTransition( 0, 0);
                getIntent().putExtra("fromholdfin", "");
                getIntent().putExtra("dteCodefin", "");
                startActivity(getIntent());
                overridePendingTransition( 0, 0);


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
            }else if (result.equalsIgnoreCase("time out error")){
                new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "The server did not respond please try again");
                //  "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }




    public void ExportFinalData() {
        exportTranListFinal = new ArrayList<RepackFG>();

        mDbHelper.openReadableDatabase();
        exportTranListFinal = mDbHelper.getRepackFG();
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
        }else {
            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "Unable to update Server");
        }
    }

    public void SetData() {

        mDbHelper.openReadableDatabase();
        ingredientsList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
        mDbHelper.closeDatabase();
        Supporter.SUMQTY = false;
        isLocalData = true;
        edtPallet.setText(PalletNumber.trim());

        totalQtY = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
        //totalQtY = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED());
       /*if(String.valueOf(totalQtY).contains("-")){
           edtQty.setText(String.valueOf(Math.round(Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()))));
       }else {
           edtQty.setText(String.valueOf(Math.round(totalQtY)));
       }*/
        edtQty.setText(String.valueOf(Math.round(Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()))));
        // edtQty.setText(String.valueOf(Math.round(totalQtY)));
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
        //    isLocalData = true;
    }

    @Override
    public void onBackPressed() {

        mDbHelper.openReadableDatabase();
        exportTranListFinal = mDbHelper.getRepackFG();
        mDbHelper.closeDatabase();


        if (edtRepackNum.isEnabled()){
            if (isdeviceSideLock){
                deleteRepack();
                mDbHelper.openReadableDatabase();
                repackFG = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();


                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();
                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                // btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnCancelFinal.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();

            }else {
                new CancelRepackFinal(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mSupporter.simpleNavigateTo(RepackMainMenuScreen.class);
                Intent theIntent = new Intent(PickRepackFinishGoodsActivity.this, RepackMainMenuScreen.class);
                theIntent.putExtra("dteCodefin","");
                startActivity(theIntent);


            }
        }else {
            BackpressAlert();
        }


        /*if(exportTranListFinal.size()>0){
            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "Please save the existing transaction.");
        }else{
            BackpressAlert();
        }*/
    }


    private void BackpressAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackFinishGoodsActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //new GetPickTaskList(mUsername).execute();
                        //listViewAlert();
                        // btn_pickRepack.setEnabled(true);
                        // repackNum.setText("");

                        if (isdeviceSideLock){
                            deleteRepack();
                            mDbHelper.openReadableDatabase();
                            repackFG = mDbHelper.getRepackFG();
                            mDbHelper.closeDatabase();


                            repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                            transList.setAdapter(repackFGAdapter);
                            repackFGAdapter.notifyDataSetChanged();
                            edtRepackNum.setEnabled(true);
                            edtPallet.setEnabled(false);
                            edtRepackNum.setText("");
                            // btnIngredients.setEnabled(false);
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
                            btnCancelFinal.setEnabled(false);
                            btnOnHold.setEnabled(false);
                            edtRepackNum.requestFocus();

                        }else {
                            new CancelRepackFinal(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                            //mSupporter.simpleNavigateTo(RepackMainMenuScreen.class);
                            Intent theIntent = new Intent(PickRepackFinishGoodsActivity.this, RepackMainMenuScreen.class);
                            theIntent.putExtra("dteCodefin","");
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
                        // mSupporter.simpleNavigateTo(PickRepackActivity.class);
                        Intent theIntent = new Intent(PickRepackFinishGoodsActivity.this, PickRepackActivity.class);
                        theIntent.putExtra("lockstatus",isLocked);
                        theIntent.putExtra("isdeviceSidelock",isdeviceSideLock);
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

    /*    public void StatusLockAlert() {
            final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
            alertUser.setTitle("Alert");
            alertUser.setIcon(R.drawable.warning);
            alertUser.setCancelable(false);
            alertUser.setMessage("This repack has been locked by VP user"+lockUserName+".you can only View");
            alertUser.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });


            alertUser.show();
        }*/
  /*  public void StatusLockAlertDivice() {
        final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("This repack has been locked by scanner user"+lockUserName+".you can only View");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //  isdeviceSideLock = false;
                    }
                });


        alertUser.show();
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_pick_task_item, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
       /*     case R.id.print_pick_task_item:

                if(mSupporter.isNetworkAvailable(PickTaskActivity.this)){
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
                break;*/
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

    public static String fixedLengthString(String string) {
        return String.format("%1$10"+"s", string);
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

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }


    private void qtyAlert() {

        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackFinishGoodsActivity.this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(true);
        alertUser.setMessage("Quantity Add or Override?");
        alertUser.setPositiveButton("Override",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                             /*QTYALERT_ADD = true;
                             wlotContainPalletNo();
                             dialog.dismiss();*/

                        QTYALERT_ADD = false;
                        wlotContainPalletNo();
                        dialog.dismiss();
                        edtPallet.requestFocus();
                        edtQty.setEnabled(false);
                        btnSave.setEnabled(false);
                        edtRepackNum.setText(Globals.RecallLstDTE_codeFin);
                        edtRepackNum.setEnabled(false);

                    }
                });

        alertUser.setNegativeButton("Add",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                          /*  QTYALERT_ADD = false;
                            wlotContainPalletNo();
                            dialog.dismiss();*/
                        // QTYALERT_OVERRIDE = true;

                        QTYALERT_ADD = true;
                        wlotContainPalletNo();
                        dialog.dismiss();
                        edtPallet.requestFocus();
                        edtQty.setEnabled(false);
                        btnSave.setEnabled(false);
                        edtRepackNum.setText(Globals.RecallLstDTE_codeFin);
                        edtRepackNum.setEnabled(false);
                    }
                });

        alertUser.show();


    }

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
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
                                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                                        "No application match to open Print File");
                            }
                        }
                    }, 1000);

                } catch (Exception e) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Not software match to Open Print File");
                    mSupporter.simpleNavigateTo(PickRepackFinishGoodsActivity.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        toasttext);
                mSupporter.simpleNavigateTo(PickRepackFinishGoodsActivity.class);

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
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                // SoapPrimitive lock = (SoapPrimitive) envelope.getResponse().getValue("VPLOCK").toString();
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

                    // String[] vpLckAry = String.valueOf(resultString).split("<ErrorMessage>");
                    //btn_pickRepack.setEnabled(true); --07-10-2022
                    result = "Failed";
                    if (resultString.toString().toLowerCase().contains("already assigned to another user")) {
                        result = "Assinged another user";

                    }else if (resultString.toString().contains("Repack Completed. Unable to proceed")){
                        result = "Repack Completed.";

                    }

                } else {
                    if(resultString.toString().contains("<vplocked>L-")){

                        String[] vpLckAry = String.valueOf(resultString).split("<vplocked>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</vplocked>");

                        String lockByusr1 = vpLckAry1[0];

                        String[] vpLckAry2 = lockByusr1.split("-");

                        String firstVlu = vpLckAry2[0];

                        userName = vpLckAry2[1];

                        isLocked = true;
                        // result = "Locked";"
                    }else if (resultString.toString().contains("<vplocked>S-")){

                        String[] vpLckAry = String.valueOf(resultString).split("<vplocked>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</vplocked>");

                        String lockByusr1 = vpLckAry1[0];

                        String[] vpLckAry2 = lockByusr1.split("-");

                        String firstVlu = vpLckAry2[0];

                        userName = vpLckAry2[1];


                        isdeviceSideLock = true;
                    }/*else if (resultString.toString().contains("origpano")){

                        String[] vpLckAry = String.valueOf(resultString).split("<origpano>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</origpano>");

                        String lockByusr1 = vpLckAry1[0];

                        //String[] vpLckAry2 = lockByusr1.split("-");

                        //String firstVlu = vpLckAry2[0];

                        Globals.orgPano = lockByusr1;



                    }*/else {
                        isLocked=false;
                        isdeviceSideLock = false;
                    }
                    if (resultString.toString().contains("origpano")){

                        String[] vpLckAry = String.valueOf(resultString).split("<origpano>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</origpano>");

                        String lockByusr1 = vpLckAry1[0];

                        //String[] vpLckAry2 = lockByusr1.split("-");

                        //String firstVlu = vpLckAry2[0];

                        Globals.orgPanofin = lockByusr1;



                    }
                    if (resultString.toString().contains("<RepackIngredients>")){
                        Globals.AVAILINGREADIENTS = true;
                    }else {
                        Globals.AVAILINGREADIENTS = false;
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
                // btn_pickRepack.setEnabled(false);  --07-10-2022
                Globals.RecallLstDTE_codeFin = RepackNumber;
/*
                if (!Globals.orgPano.equals("")){
                    repackNum.setText("Repack # "+Globals.orgPano.trim());
                }*/

                new LoadRepackDataDteCode(repackFGAdapter).execute();
                btnCancel.setEnabled(false);
                edtPallet.requestFocus();
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Repack # not found");
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();

                //  "Invalid Repack Number");

            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();

                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        Getmsg);
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();
            }else if (result.equals("Repack Completed.")){


                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        // "DTE code Completed. Unable to Proceed");
                        "Repack completed. unable to proceed");
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();
                // "Repack Completed");

            }else if(result.equalsIgnoreCase("time out error")){
                new GetRepackData(mUsername).execute();
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        // "Unable to Hold");
                        "Unable to proceed");
                edtRepackNum.setText("");
                edtRepackNum.requestFocus();
            }
            dialog.cancel();
            /*overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);*/
        }
    }

    /*class GetRepackFinishedData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetRepackFinishedData(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PEPACK_FINISHED_DATA);
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

                info = new PropertyInfo();
                info.setName("pItem");
                info.setValue(PalletNumber);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_PEPACK_FINISHED_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                // SoapPrimitive lock = (SoapPrimitive) envelope.getResponse().getValue("VPLOCK").toString();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "RepackFinishedData" + ".xml");
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

                    // String[] vpLckAry = String.valueOf(resultString).split("<ErrorMessage>");
                    //btn_pickRepack.setEnabled(true); --07-10-2022
                    result = "Failed";
                    if (resultString.toString().toLowerCase().contains("already assigned to another user")) {
                        result = "Assinged another user";

                    }else if (resultString.toString().contains("Repack Completed. Unable to proceed")){
                        result = "Repack Completed.";

                    }

                } else {
                    if(resultString.toString().contains("<vplocked>L-")){

                        String[] vpLckAry = String.valueOf(resultString).split("<vplocked>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</vplocked>");

                        String lockByusr1 = vpLckAry1[0];

                        String[] vpLckAry2 = lockByusr1.split("-");

                        String firstVlu = vpLckAry2[0];

                        userName = vpLckAry2[1];

                        isLocked = true;
                        // result = "Locked";"
                    }else if (resultString.toString().contains("<vplocked>S-")){

                        String[] vpLckAry = String.valueOf(resultString).split("<vplocked>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</vplocked>");

                        String lockByusr1 = vpLckAry1[0];

                        String[] vpLckAry2 = lockByusr1.split("-");

                        String firstVlu = vpLckAry2[0];

                        userName = vpLckAry2[1];


                        isdeviceSideLock = true;
                    }*//*else if (resultString.toString().contains("origpano")){

                        String[] vpLckAry = String.valueOf(resultString).split("<origpano>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</origpano>");

                        String lockByusr1 = vpLckAry1[0];

                        //String[] vpLckAry2 = lockByusr1.split("-");

                        //String firstVlu = vpLckAry2[0];

                        Globals.orgPano = lockByusr1;



                    }*//*else {
                        isLocked=false;
                        isdeviceSideLock = false;
                    }
                    if (resultString.toString().contains("origpano")){

                        String[] vpLckAry = String.valueOf(resultString).split("<origpano>");

                        String lockByusr = vpLckAry[1];

                        String[] vpLckAry1 = lockByusr.split("</origpano>");

                        String lockByusr1 = vpLckAry1[0];

                        //String[] vpLckAry2 = lockByusr1.split("-");

                        //String firstVlu = vpLckAry2[0];

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
                // btn_pickRepack.setEnabled(false);  --07-10-2022
                Globals.RecallLstDTE_code = RepackNumber;
*//*
                if (!Globals.orgPano.equals("")){
                    repackNum.setText("Repack # "+Globals.orgPano.trim());
                }*//*

                new LoadRepackDataDteCode(adapter1).execute();

                edtPallet.requestFocus();
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "DTE code not found");
                edtRepackNum.requestFocus();
                //  "Invalid Repack Number");

            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();

                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        Getmsg);
            }else if (result.equals("Repack Completed.")){


                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        // "DTE code Completed. Unable to Proceed");
                        "Repack Completed. Unable to Proceed");
                // "Repack Completed");

            }else if(result.equalsIgnoreCase("time out error")){
                new GetRepackData(mUsername).execute();
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        // "Unable to Hold");
                        "Unable to Proceed");
            }
            dialog.cancel();
            *//*overridePendingTransition( 0, 0);
            startActivity(getIntent());
            overridePendingTransition( 0, 0);*//*
        }
    }*/



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
        /*if(String.valueOf(TranlineCount).contains("99")){
            Globals.gTranlineno = TranlineCount+1;
        }else {
            Globals.gTranlineno = 9901;
        }*/
        //strTranlineNo = String.valueOf(Globals.gTranlineno);
        Globals.gDoclineno = DoclineCount + 1;
        Globals.gPTDetailRowCount = rowNo + 1;
        mDbHelper.SplitNewLine(tpicktaskdetail, Globals.gTranlineno, Globals.gDoclineno, Globals.gPTDetailRowCount, Globals.gTqty);
        mDbHelper.closeDatabase();
    }

    //Repack list load the page
    class GetRepackRawData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetRepackRawData(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                    // result = resultString.toString();
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
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Invalid item");
                edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.requestFocus();
            } else if (result.equals("No Data Found")) {
                if (!isLocalData) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Invalid item.");
                    edtPallet.setText("");
                    edtQty.setText("");
                    btnSave.setEnabled(false);
                    edtPallet.setEnabled(true);
                    btnCancel.setEnabled(false);
                    edtPallet.requestFocus();
                }
                isLocalData = false;

               /* edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();*/
            } else if (result.equalsIgnoreCase("time out error")) {
                new GetRepackRawData(mUsername).execute();
            } else {
                edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.requestFocus();
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        result);
            }
            dialog.cancel();
        }
    }

    class GetRepackFinishedData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetRepackFinishedData(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_FINSIHED_DATA);
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
                info.setName("pItem");
                info.setValue(PalletNumber);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pPano");
                info.setValue(Globals.RepackNum.trim());
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_FINSIHED_DATA;
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
                    // result = resultString.toString();
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
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Invalid item");
                edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.requestFocus();
            } else if (result.equals("No Data Found")) {
                if (!isLocalData) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Invalid item.");
                    edtPallet.setText("");
                    edtQty.setText("");
                    btnSave.setEnabled(false);
                    edtPallet.setEnabled(true);
                    btnCancel.setEnabled(false);
                    edtPallet.requestFocus();
                }
                isLocalData = false;

               /* edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();*/
            } else if (result.equalsIgnoreCase("time out error")) {
                new GetRepackRawData(mUsername).execute();
            } else {
                edtPallet.setText("");
                edtQty.setText("");
                btnSave.setEnabled(false);
                edtPallet.requestFocus();
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        result);
            }
            dialog.cancel();
        }
    }

    private class LoadRepackDataDteCode extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;


        public LoadRepackDataDteCode(RepackFGAdapter repackFGAdapter) {
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
            repackFGAdapter = repackFGAdapter;
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickRepackFinishGoodsActivity.this, mDbHelper, Globals.gUsercode);

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
                // PickTaskActivity.this.notifyAll();

                mDbHelper.openReadableDatabase();
                repackFG = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                if (isdeviceSideLock){
                    StatusLockAlertDivice();

                }else if (isLocked){
                    StatusLockAlert();

                }else if (!Globals.AVAILINGREADIENTS){
                    RrpackRawNotAvilAlert();
                }

                repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();

                //  btnIngredients.setEnabled(true);
                btnRecallLstDTE.setEnabled(false);
                btnCancel.setEnabled(true);
                //btnOnHold.setEnabled(true);
                //edtRepackNum.setText(RepackNumber);
                edtRepackNum.setText(Globals.RecallLstDTE_codeFin);
                edtRepackNum.setEnabled(false);


                repackNum = RepackNumber;
                lockUserName = userName;
                OrgPaNo = Globals.orgPanofin;
                DteCode = Globals.RecallLstDTE_codeFin;


                if (isdeviceSideLock || isLocked) {
                    edtPallet.setEnabled(false);
                    edtQty.setEnabled(false);
                    btnSave.setEnabled(false);
                    btnCancel.setEnabled(false);
                    btnCancelFinal.setEnabled(true);
                    btnSaveFinal.setEnabled(false);
                }else {

                    btnOnHold.setEnabled(true);
                    btnSaveFinal.setEnabled(true);
                    btnCancelFinal.setEnabled(true);
                    edtPallet.setEnabled(true);
                    btnSave.setEnabled(false);
                    btnCancel.setEnabled(false);
                    edtPallet.requestFocus();
                }
                if (!Globals.orgPanofin.trim().equals("")) {
                    tvRepacknum.setText("Repack # " + Globals.orgPanofin.trim());
                }else {
                    tvRepacknum.setText("");
                }
                mDbHelper.openReadableDatabase();
                repackFGList = mDbHelper.getRepackIngredients();
                wLotNoList = mDbHelper.getWlotNoList();
                mDbHelper.closeDatabase();



                //setViewsData();
               /* adapter = new RepackIngredientsAdapter(PickRepackFinishGoodsActivity.this, repackFGList);
                transList.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/

                //smSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Error");
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
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickRepackFinishGoodsActivity.this, mDbHelper, Globals.gUsercode);

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
                edtPallet.setEnabled(false);
                edtQty.requestFocus();

               /* mDbHelper.openReadableDatabase();
                finishedMaterial= mDbHelper.getRepackFinishMaterials(PalletNumber);
                mDbHelper.closeDatabase(); */

                mDbHelper.openReadableDatabase();
                LotNo= mDbHelper.getRepackFGLotNo(PalletNumber.trim());
                mDbHelper.closeDatabase();

                if (LotNo != null) {
                    if (LotNo.size() > 0) {
                        String Lotno = LotNo.get(0).getREPACKFG_LOTNO();


                        if (Lotno != null) {
                            if (!Lotno.equals("")) {
                                tVlotNo.setText("Lot #" + Lotno);
                            } else {
                                tVlotNo.setText("");
                            }
                        } else {
                            tVlotNo.setText("");
                        }

                    } else {
                        tVlotNo.setText("");
                    }
                } else {
                    tVlotNo.setText("");
                }








                /*mDbHelper.openReadableDatabase();
                ingredientsList = mDbHelper.getWlotRepackIngredients(PalletNumber.trim());
                mDbHelper.closeDatabase();
                if (!isLocalData) {
                    toTalQtY = Double.valueOf(ingredientsList.get(0).getRIT_QTYUSED()) - Double.valueOf(ingredientsList.get(0).getRIT_RPALLOCQTY());
                   // edtQty.setText(String.valueOf(Math.round(Double.parseDouble(ingredientsList.get(0).getRIT_QTYUSED()))));
                    edtQty.setText(String.valueOf(Math.round(toTalQtY)));
                } else {
                    SetData();
                }

                boolean validationResult = validateDate();
                if (validationResult) {

                    edtPallet.setText(PalletNumber.trim());
                    isLocalData = false;
                    edtQty.requestFocus();
                    edtQty.setSelectAllOnFocus(true);
                    edtQty.selectAll();
                    edtPallet.setEnabled(false);
                } else {
                    edtPallet.setText("");
                    edtQty.setText("");
                    btnSave.setEnabled(false);
                    edtPallet.requestFocus();
                }*/


            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Error");
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
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                //new LoadRepackData(adapter).execute();
                int tempTran = 0, tempTran2 = 0;
                String tranNum = saveList.get(0).getRIT_TRANLINENO();
               /* mToastMessage.showToast(PickRepackIngredientsActivity.this,
                        "Qty Allocated");*/


                if (tranNum == null) {
                    mDbHelper.openReadableDatabase();
                    tempTran = mDbHelper.getMaxTranNum();
                    mDbHelper.closeDatabase();
                    tempTran2 = tempTran + (-1);
                    saveList.get(0).setRIT_TRANLINENO(Integer.toString(tempTran2));
                    if(QTYALERT_ADD){
                        saveList.get(0).setRIT_QTYUSED(allocQtyForSave);
                        saveList.get(0).setRIT_TEMPALLOC(allocQtyForSave);
                    }else{
                        saveList.get(0).setRIT_QTYUSED(enteredQty);
                        saveList.get(0).setRIT_TEMPALLOC(enteredQty);
                    }
                    saveList.get(0).setRIT_PANO(Globals.orgPanofin.trim());
                    saveList.get(0).setRIT_ORIGPANO(Globals.RecallLstDTE_codeFin.trim());
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


                    if(QTYALERT_ADD){
                        String allocQty = enteredQty;
                        //tempAllocFinal = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_QTYUSED()));
                        tempAllocFinal = enteredQty;
                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateRawItemQty(saveList.get(0).getRIT_ITEM(), saveList, allocQtyForSave, allocQty, UpdFlag, AddFlag, tempAllocFinal,saveList.get(0).getRIT_TRANLINENO());
                        mDbHelper.closeDatabase();
                    }else {
                        String allocQty = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()));
                        tempAllocFinal = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_QTYUSED()));
                        mDbHelper.openWritableDatabase();
                        mDbHelper.updateRawItemQty(saveList.get(0).getRIT_ITEM(), saveList, enteredQty, allocQty, UpdFlag, AddFlag, tempAllocFinal,saveList.get(0).getRIT_TRANLINENO());
                        mDbHelper.closeDatabase();
                    }


                }

                mDbHelper.openReadableDatabase();
                repackFGList = mDbHelper.getRepackIngredients();
                mDbHelper.closeDatabase();

              /*  adapter = new RepackIngredientsAdapter(PickRepackFinishGoodsActivity.this, repackFGList);
                transList.setAdapter(adapter);
                adapter.notifyDataSetChanged();*/  //11-10-2022

                edtPallet.setText("");
                edtQty.setText("");
                /*tvLotRfId.setText("");*/

                edtPallet.setEnabled(true);
                btnSave.setEnabled(false);
                btnCancel.setEnabled(false);
                edtPallet.requestFocus();
                /*finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);*/

                //adapter.notifyDataSetChanged();






            }/**/ else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Invalid item");
            } else if (result.equalsIgnoreCase("time out error")) {
                new SaveRepackRawData(mUsername, allocQtyForSave).execute();
            } else if (result.equalsIgnoreCase("Does not match")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Does not match with IC quantity table");
            } else if (result.equalsIgnoreCase("Insufficient Qty")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Insufficient qty in IC pallet table");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        result);
            }
            dialog.cancel();
        }
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
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PickRepackFinishGoodsActivity.this);
        builderSingle.setTitle("Select SO item to Substitute");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PickRepackFinishGoodsActivity.this, android.R.layout.select_dialog_singlechoice);
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
                AlertDialog.Builder builderInner = new AlertDialog.Builder(PickRepackFinishGoodsActivity.this);

                //new LoadRepackData(adapter).execute();

            }
        });
        builderSingle.show();
    }

    class ExportTranDataFinal extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public ExportTranDataFinal(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
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
                } else if(resultString.toString().contains("<Result>true</Result>")){
                    result ="success";
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
                //btn_pickRepack.setEnabled(true);
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        //  "Repack Updated");
                        "Repack updated");
                //repackNum.setText("");
                Globals.orgPanofin = "";
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteRepackDataFinished();
                mDbHelper.closeDatabase();

                edtRepackNum.setEnabled(true);
                edtPallet.setEnabled(false);
                edtRepackNum.setText("");
                tvRepacknum.setText("");
                //btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                // Repacknum = "";
                Globals.fromExportData = true;
                Globals.RecallLstDTE_codeFinish = "";

                //  finish();
                overridePendingTransition(0, 0);
                getIntent().putExtra("dteCodefin","");
                getIntent().putExtra("fromholdfin", "");
                startActivity(getIntent());
                overridePendingTransition(0, 0);

//                Intent theIntent = new Intent(PickRepackActivity.this, PickRepackActivity.class);
//                theIntent.putExtra("dteCode","");
//                startActivity(theIntent);

                //mSupporter.simpleNavigateTo(PickRepackActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){
                new ExportTranDataFinal(mSessionId, mUsername, mCompany).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }




    class ExportTranData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;
        String result = "";

        public ExportTranData(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
               /* mDbHelper.openWritableDatabase();
                mDbHelper.deletePickTaskDetail();
                mDbHelper.closeDatabase();*/
               /* mToastMessage.showToast(PickTaskActivity.this,
                        "Data exported to Server successfully");*/
                /*mDbHelper.openWritableDatabase();
                mDbHelper.updateFlagPickTask(ItemNo,strTranlineNo);
                mDbHelper.closeDatabase();*/
                mSupporter.simpleNavigateTo(PickRepackFinishGoodsActivity.class);
                //edtRepackNum.requestFocus();

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        result.toString());
            }

            dialog.cancel();
        }
    }
    public void StatusLockAlert() {
        final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("This repack has been locked by VP user "+userName+". You can only View.");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnOnHold.setEnabled(false);
                        Globals.OkPressed = true;
                        dialog.cancel();
                       /* overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);*/
                    }
                });


        alertUser.show();
    }

    public void RrpackRawNotAvilAlert() {
        final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Raw item must be added to packout before finished item is added.");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnOnHold.setEnabled(false);
                        Globals.OkPressed = true;

                        if (isdeviceSideLock){
                            deleteRepack();
                            mDbHelper.openReadableDatabase();
                            repackFG = mDbHelper.getRepackFG();
                            mDbHelper.closeDatabase();


                            repackFGAdapter = new RepackFGAdapter(PickRepackFinishGoodsActivity.this, repackFG);
                            transList.setAdapter(repackFGAdapter);

                            repackFGAdapter.notifyDataSetChanged();
                            edtRepackNum.setEnabled(true);
                            edtRepackNum.setText("");
                            tvRepacknum.setText("");
                            // btnIngredients.setEnabled(false);
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
                            btnOnHold.setEnabled(false);
                            edtRepackNum.requestFocus();

                        }else {

                            new CancelRepackForAlert(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();

                        }
                        Globals.AVAILINGREADIENTS = false;


                        dialog.cancel();
                       /* overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);*/
                    }
                });


        alertUser.show();
    }


    public void StatusLockAlertDivice() {
        final AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("This repack has been locked by scanner user "+userName+". You can only View.");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnOnHold.setEnabled(false);
                        Globals.OkPressed = true;
                        dialog.cancel();
                        /*overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);*/
                        //  isdeviceSideLock = false;
                    }
                });


        alertUser.show();
    }

    public void wlotContainPalletNo(){
        String UpdFlag = "", AddFlag = "";



        //if (!enteredQty.equalsIgnoreCase(saveList.get(0).getRIT_QTYUSED()) || (saveList.get(0).getRIT_TRANLINENO() != null && !saveList.get(0).getRIT_TRANLINENO().contains("-"))) {
        if (QTYALERT_ADD){
          /*      TempallocQty = String.valueOf(Double.parseDouble(enteredQty) + Double.parseDouble(saveList.get(0).getRIT_QTYUSED()));
                allocQty = String.valueOf(Double.parseDouble(saveList.get(0).getRIT_QTYUSED())+Math.abs(Double.parseDouble(TempallocQty)));*/

            //allocDiffQty = String.valueOf( Double.parseDouble(saveList.get(0).getRIT_QTYUSED())+Double.parseDouble(enteredQty));
            allocDiffQty = enteredQty;
            tempAlloc2 =   allocDiffQty;
            allocQty = String.valueOf(Double.parseDouble(enteredQty) + Double.parseDouble(saveList.get(0).getRIT_QTYUSED()));
        }else{
            // }else if (QTYALERT_OVERRIDE){

            allocQty = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_QTYUSED()));
        }


       /* } else {
            allocQty = saveList.get(0).getRIT_QTYUSED();
        }*/
        allocQtyForSave = allocQty;

        Double temp = Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK());
        Double temp2 = Double.parseDouble(enteredQty);
        if((Double.parseDouble(allocQtyForSave) > firstQty)) {

            if (QTYALERT_ADD){
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Can't add, qty entered is more than available qty");
                edtQty.requestFocus();
            }else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Can't override, qty entered is more than available qty");
                edtQty.requestFocus();
            }

        } else if ((Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()) <= Double.parseDouble(enteredQty)) && (saveList.get(0).getRIT_TRANLINENO() != null && !saveList.get(0).getRIT_TRANLINENO().contains("-"))) {

            if(QTYALERT_ADD){
                new SaveRepackRawData(mUsername, allocDiffQty).execute();
            }else {
                new SaveRepackRawData(mUsername, allocQtyForSave).execute();
            }

            // new SaveRepackRawData(mUsername, allocQtyForSave).execute();

        } else if ((Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()) >= Double.parseDouble(enteredQty)) && (saveList.get(0).getRIT_TRANLINENO() != null && !saveList.get(0).getRIT_TRANLINENO().contains("-"))) {
            // } else if ((Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()) >= Double.parseDouble(enteredQty)) && (saveList.get(0).getRIT_TRANLINENO() != null )) {

            if (saveList.get(0).getRIT_TRANLINENO().contains("-")) {
                UpdFlag = "0";
                AddFlag = "1";
            } else {
                UpdFlag = "1";
                AddFlag = "0";
            }


            String allocQty2 = String.valueOf(Double.parseDouble(enteredQty) - Double.parseDouble(saveList.get(0).getRIT_TRKQTYPK()));
            /*tempAlloc2 = "-" + saveList.get(0).getRIT_TEMPALLOC();*/
            if(QTYALERT_ADD){
                new SaveRepackRawData(mUsername, allocDiffQty).execute();
               /*  mDbHelper.openWritableDatabase();
                mDbHelper.updateAllocQty(saveList.get(0).getRIT_ITEM(), saveList, allocQty2, allocQtyForSave, UpdFlag, AddFlag, tempAlloc2);
                mDbHelper.closeDatabase();*/
            }else {
                mDbHelper.openWritableDatabase();
                mDbHelper.updateAllocQty(saveList.get(0).getRIT_ITEM(), saveList, allocQty2, enteredQty, UpdFlag, AddFlag, tempAlloc2,saveList.get(0).getRIT_TRANLINENO());
                // mDbHelper.updateAllocQty(saveList.get(0).getRIT_ITEM(), saveList, allocQty2, allocQtyForSave, UpdFlag, AddFlag, tempAlloc2);
                mDbHelper.closeDatabase();
            }

           /* mDbHelper.openWritableDatabase();
            mDbHelper.updateAllocQty(saveList.get(0).getRIT_ITEM(), saveList, allocQty2, enteredQty, UpdFlag, AddFlag, tempAlloc2);
           // mDbHelper.updateAllocQty(saveList.get(0).getRIT_ITEM(), saveList, allocQty2, allocQtyForSave, UpdFlag, AddFlag, tempAlloc2);
            mDbHelper.closeDatabase();*/
            mDbHelper.openReadableDatabase();
            repackFGList = mDbHelper.getRepackIngredients();
            mDbHelper.closeDatabase();


           /* adapter = new RepackIngredientsAdapter(PickRepackFinishGoodsActivity.this, repackFGList);
            transList.setAdapter(adapter);
            adapter.notifyDataSetChanged();*/  //11-10-2022

            edtPallet.setText("");
            edtQty.setText("");
            /* tvLotRfId.setText("");*/
            edtPallet.setEnabled(true);
            btnSave.setEnabled(false);
            btnCancel.setEnabled(false);
            edtPallet.requestFocus();
            /* finish();*/
          /*  overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
*/
        } else {
            if(QTYALERT_ADD) {
                new SaveRepackRawData(mUsername, enteredQty).execute();
            }else{
                new SaveRepackRawData(mUsername, allocQtyForSave).execute();
            }

        }
        btnOnHold.setEnabled(true);
    }

    public void ExportData() {
        exportTranList = new ArrayList<picktaskdetail>();

        mDbHelper.openReadableDatabase();
        /*exportTranList = mDbHelper.getPickTaskDetail();*/
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
            mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                    "Unable to update server");
        }
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PickRepackFinishGoodsActivity.this);
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

    public class uploadDataToServiceExportItmFinal extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItmFinal() {
            mDialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PickRepackFinishGoodsActivity.this);
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
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    // Method that returns the XML to be exported



    public String getRecordXmlExportPO(List<picktaskdetail> dList) {
        String exportPODataXml = "";
        try {
            ExportPickTask exportData = new ExportPickTask();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "PickTaskData" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickRepackFinishGoodsActivity.this, mDbHelper);
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

    public String getRecordXmlExportPOFinal(List<RepackFG> dList) {
        String exportPODataXml = "";
        try {
            ExportRepackDataFinish exportData = new ExportRepackDataFinish();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "RepackFinished" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickRepackFinishGoodsActivity.this, mDbHelper);
            }
            sb.append("</" + "RepackFinished" + ">");
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
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Please enter the qty");
                } else if (Temp > Globals.editQty2) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Entered qty is greater than ordered qty");
                    et.setText("");
                    et.setHint("Entered Qty is Greater than Tqty");
                } else if (Temp < Globals.editQty1) {
                    mToastMessage.showToast(PickRepackFinishGoodsActivity.this, "Entered qty is lesser than ordered qty");
                } else {
                    //Globals.editQty2 = Double.parseDouble(editQty);
                    Temp2 = Globals.editQty2 - Temp;
                    Globals.editQty2 = Temp;
                   /* if(Globals.editQty2!=Temp){
                        Temp2 = Globals.editQty2 - Temp;
                    }*/
                    mDbHelper.getWritableDatabase();
                    mDbHelper.updateEditQty(Globals.editItemNum, Globals.editTranNum, editQty, String.valueOf(Temp2));
                    mDbHelper.closeDatabase();

                    ItemNo = Globals.editItemNum;
                    strTranlineNo = Globals.editTranNum;
                    ExportData();
                }
            }
        });

    }

}



