package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.silvercreek.wmspickingclient.model.RepackList;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PickRepackActivity extends AppBaseActivity {

    private ListView transList;
    private EditText edtRepackNum;
    private TextView repackNum;

    private List<RepackIngredients> exportTranList;

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
    private Dialog dialogRepackPickList;
    private  Context context;
    ListView pickListRepack = null;
    private Button cancel;

    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    private List<RepackFG> repackFGList;
    private List<RepackIngredients> repackFGIngerdientList;
    private List<picktaskdetail> headerpicktaskdetail;
    private List<picktaskdetail> subPickList;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_EXPORT_DATA ="Repack_Save";
    public static final String REPACKLIST ="Repack_List";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private RepackFGAdapter adapter;
    private List<String> mPalletList;
    private ArrayList<picktaskWHIPTL> mPalletMast;
    private List<String> mLotList;
    private ArrayList<picktaskWHMQTY> mLotMast;
    private String StrFlag = "Y";
    private Boolean isTaskCompleted;
    private Boolean isTaskOnHold;
    private Boolean isItemAvailable;
    private List<picktaskPrintlabel> PrintLabelList;
    private String stop, trailer, route, dock, deldate, order, task, custid, custname, picker, palno;
    private String mPalno = "";
    private String RepackNumber = "";
    //private TextView,tvDesc txtWeight;
    private Button btnIngredients, btnOnHold, btnSave, btnCancel ,btnRecallLstDTE;
    private ImageView btn_pickRepack;
    private Boolean isSameItem = false;
    private Boolean isProceed = true;
    private String uom = "", doctype = "", docno = "", doclineno = "", docstat = "", strweight = "", stkumid = "",
            orgdoclineno = "", volume = "", decnum = "", orgTranlineno = "", Lbshp = "", umfact = "", Tshipped = "", Trkshiped = "", LineSplit = "";
    private String ItemNo= "", strDesc = "";
    private String strTQty = "", strorgTQty = "";
    private String strTrkQty = "", strorgTrkQty = "";
    private String strCatchwt = "", strSlot = "", strLot = "", strwLotno = "";
    private String strTranlineNo ="";
    private Double dQty =0.0;
    private double dAvailQty;
    private Integer pickDuration;
    private double dSoQty = 0.0, dLotQty = 0.0;
    private boolean isMoreQty = true;
    public static final String METHOD_GET_PEPACK_DATA = "Repack_LookupData";
    public static final String METHOD_REPACK_CANCEL = "Repack_unlock";
    private String mSessionId = "";
    private String mCompany = "";
    private String repackNo = "";
    private String mLoctid = "";
    private String mUsername = "";
    private String mDeviceId,mCompanyId;
    private File mImpOutputFile;
    private String Getmsg = "";
    private String selectedItem="";
    private String [] itemArray={} ;
    private int SubTranlineCount=0;
    private String subTranNo="";
    private String stagingSlot="";
    private String taskStatus="";
    private String editQty ="";
    private Boolean scanResult=true;
    private ArrayList<picktaskdetail> editPickDetail;
    private RepackFGAdapter repackFGAdapter;
    private RepackListAdapter repacklistadapter;
    private String intentRepackNum="";
    private boolean isLocked=false;
    private boolean isdeviceSideLock=false;

    private  String Repacknum = "";
    private  String fromhold = "";
    public   String userName ="";
    public   String orgPano ="";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_repack);





        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        edtRepackNum = (EditText) findViewById(R.id.edtRepackNum);
        repackNum = (TextView) findViewById(R.id.repackNum);

        btnOnHold = (Button) findViewById(R.id.btn_hold);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnIngredients = (Button) findViewById(R.id.btn_ingredients);
        btnRecallLstDTE = (Button) findViewById(R.id.recallLstDTE);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btn_pickRepack = (ImageView) findViewById(R.id.btn_pickRepack);


        transList = (ListView) findViewById(R.id.lst_TransItems);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
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
        mCompanyId = Globals.gCompanyId;

        isLocked = this.getIntent().getExtras().getBoolean("lockstatus");
        isdeviceSideLock = this.getIntent().getExtras().getBoolean("isdeviceSidelock");

        if (!Globals.fromExportData){
            Repacknum = this.getIntent().getExtras().getString("rpNum");

            Globals.fromExportData=false;
        }
        fromhold = this.getIntent().getExtras().getString("fromhold");
        if (fromhold==null){
            fromhold="0";
        }

        if (fromhold.equals("holdTrue")){
            btn_pickRepack.setEnabled(false);
        }else {
            btn_pickRepack.setEnabled(true);
        }
if (!Globals.orgPano.trim().equals("")){
    repackNum.setText("Repack # "+Globals.orgPano.trim());
}




       /* mDbHelper.openReadableDatabase();
        picktaskdetail = mDbHelper.getPickTaskDetail();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        picktaskheader = mDbHelper.getPickTaskHeader();
        mDbHelper.closeDatabase();

        mDbHelper.openWritableDatabase();
        mDbHelper.UpdateTaskStatus(tpicktasklist, Globals.gTaskNo);
        mDbHelper.closeDatabase();


        adapter = new PickTaskDetailAdapter(PickRepackActivity.this, picktaskdetail);
        transList.setAdapter(adapter);*/

        mDbHelper.openReadableDatabase();
        repackFGList = mDbHelper.getRepackFG();
        repackFGIngerdientList = mDbHelper.getRepackIngredients();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getRepackIngredientsForExport();
        mDbHelper.closeDatabase();


        if(exportTranList.size()>0){
            btn_pickRepack.setEnabled(false);
            btnSave.setEnabled(true);
            btnOnHold.setEnabled(true);
        }else {
           // btn_pickRepack.setEnabled(false);
            intentRepackNum = this.getIntent().getExtras().getString("dteCode");
            btnSave.setEnabled(false);
            btnOnHold.setEnabled(false);
           /* btnCancel.setEnabled(true);
            edtRepackNum.setText(intentRepackNum);*/

        }

        if(repackFGList.size()>0){
            btnCancel.setEnabled(true);
            btnOnHold.setEnabled(true);
            btnIngredients.setEnabled(true);
            btnRecallLstDTE.setEnabled(false);
           // edtRepackNum.setText(repackFGList.get(0).getREPACKFG_PANO());
           // edtRepackNum.setText(Globals.RecallLstDTE_code);
            edtRepackNum.setText(repackFGIngerdientList.get(0).getRIT_ORIGPANO());
            edtRepackNum.setEnabled(false);
            RepackNumber = repackFGList.get(0).getREPACKFG_PANO();
        }else if(repackFGIngerdientList.size()>0){
            /*intentRepackNum = this.getIntent().getExtras().getString("repacknum");

            if(intentRepackNum!=""){
                edtRepackNum.setText(intentRepackNum);
            }*/
            btnCancel.setEnabled(true);
            btnOnHold.setEnabled(true);
            btnIngredients.setEnabled(true);
            btnRecallLstDTE.setEnabled(false);
           // edtRepackNum.setText(repackFGIngerdientList.get(0).getRIT_PANO());
            //edtRepackNum.setText(Globals.RecallLstDTE_RIT_ALLOCQTY = "-10.0"code);
        //    edtRepackNum.setText(repackFGIngerdientList.get(0).getRIT_ORIGPANO());
            edtRepackNum.setText(repackFGIngerdientList.get(0).getRIT_ORIGPANO());
            edtRepackNum.setEnabled(false);
            RepackNumber = repackFGIngerdientList.get(0).getRIT_PANO();
            // }else if (isLocked){
        }else if (isLocked && isdeviceSideLock){
            btnIngredients.setEnabled(true);
            btnRecallLstDTE.setEnabled(false);
            //edtRepackNum.setText(Repacknum);
            edtRepackNum.setText(Globals.RecallLstDTE_code);
            edtRepackNum.setEnabled(false);
            btnCancel.setEnabled(true);
        }else if (Repacknum != null &&!Repacknum.isEmpty()){
            btnIngredients.setEnabled(true);
            btnRecallLstDTE.setEnabled(false);
           // edtRepackNum.setText(Repacknum);
            edtRepackNum.setText(Globals.RecallLstDTE_code);
            RepackNumber=Repacknum;
            edtRepackNum.setEnabled(false);
            btnCancel.setEnabled(true);
        }else if (exportTranList.size()==0&& intentRepackNum != null && !intentRepackNum.equals("")){
            intentRepackNum = this.getIntent().getExtras().getString("dteCode");
            btnSave.setEnabled(false);
            btnOnHold.setEnabled(false);
            btnCancel.setEnabled(true);
            edtRepackNum.setEnabled(false);
            edtRepackNum.setText(intentRepackNum);

        }else {
            btnIngredients.setEnabled(false);
            btnRecallLstDTE.setEnabled(true);
            btnCancel.setEnabled(false);
            btnOnHold.setEnabled(false);


        }

        adapter = new RepackFGAdapter(PickRepackActivity.this, repackFGList);
        transList.setAdapter(adapter);

        intentRepackNum = this.getIntent().getExtras().getString("dteCode");

        if(intentRepackNum!=""){
            edtRepackNum.setText(intentRepackNum);
           /* btnCancel.setEnabled(true);
            edtRepackNum.setEnabled(false);*/




        }


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

                            mDbHelper.openReadableDatabase();
                            Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                            mDbHelper.closeDatabase();

                             if(RepackNumber.equalsIgnoreCase("          ")){
                                mToastMessage.showToast(PickRepackActivity.this,
                                       // "Please Enter or Scan the Repack #");
                                        "Please Scan or Enter the Repack # ");
                                 new Handler().postDelayed(new Runnable() {
                                     @Override
                                     public void run() {

                                             edtRepackNum.requestFocus();

                                     }
                                 }, 150);

                                scanResult=true;

                                    btn_pickRepack.setEnabled(true);



                             } /*else if (!result){
                            mToastMessage.showToast(PickRepackActivity.this,
                                    "Invalid Repack");
                                 btn_pickRepack.setEnabled(true);
                        }*/ else {

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
                            mToastMessage.showToast(PickRepackActivity.this,
                                    // "Please Scan or Enter the Repack #");
                                    "Empty");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    edtRepackNum.requestFocus();

                                }
                            }, 150);
                            scanResult = true;

                            btn_pickRepack.setEnabled(true);


                           // edtRepackNum.requestFocus();
                        } /*else if (!result){
                            mToastMessage.showToast(PickRepackActivity.this,
                                    "Invalid Repack");
                                 btn_pickRepack.setEnabled(true);
                        }*/ else {

                            new GetRepackData(mUsername).execute();


                        }
                    }

                }
                return false;
            }
        });

        btnIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mSupporter.simpleNavigateTo(PickRepackIngredientsActivity.class);
                Intent theIntent = new Intent(PickRepackActivity.this, PickRepackIngredientsActivity.class);
                theIntent.putExtra("repacknum", RepackNumber);
                theIntent.putExtra("OrgpaNo", Globals.orgPano);
                theIntent.putExtra("dtecode", Globals.RecallLstDTE_code);
                theIntent.putExtra("lockstatus",isLocked);
                theIntent.putExtra("isdeviceSidelock",isdeviceSideLock);
                theIntent.putExtra("lockUser",userName);

                startActivity(theIntent);
            }
        });


        btnRecallLstDTE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                repackNo = Globals.RecallLstDTE_code;

                edtRepackNum.setText(repackNo);

                // RepackNumber = edtRepackNum.getText().toString();


                RepackNumber = repackNo;
                RepackNumber = fixedLengthString(RepackNumber);

                mDbHelper.openReadableDatabase();
                Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                mDbHelper.closeDatabase();

                if(RepackNumber.equalsIgnoreCase("          ")){
                    mToastMessage.showToast(PickRepackActivity.this,
                            "Please Scan or Enter the Repack #");
                            //"btnValue empty");
                    edtRepackNum.requestFocus();
                           // "Please Enter or Scan the Repack #");
                    scanResult=true;

                    btn_pickRepack.setEnabled(true);



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

        btnOnHold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Globals.holdTaskNum=Globals.gTaskNo;
                taskStatus = "ONHOLD";


                Intent theIntent = new Intent( PickRepackActivity.this, PickRepackActivity.class);
                theIntent.putExtra("repacknum", "");
                theIntent.putExtra("fromhold", "holdTrue");
                theIntent.putExtra("dteCode",Globals.RecallLstDTE_code);
                startActivity(theIntent);

                //mSupporter.simpleNavigateTo(MainmenuActivity.class);
                // new UpdatePickStatus(mUsername,taskStatus).execute();

               /* if(repackFGList.size()>0){

                    Globals.holdTaskNum=Globals.gTaskNo;
                    taskStatus = "ONHOLD";
                    mSupporter.simpleNavigateTo(MainmenuActivity.class);
                   // new UpdatePickStatus(mUsername,taskStatus).execute();
                }else {
                    mToastMessage.showToast(PickRepackActivity.this,
                            "No Data available to Hold");
                }*/

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportData();
            }
        });

        btn_pickRepack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.getWritableDatabase();
                mDbHelper.DeleteRepackList();
                mDbHelper.closeDatabase();

                new RepackPickList(mSessionId).execute();

               /* AlertDialog.Builder builder = new AlertDialog.Builder(PickRepackActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View RepackPickdialog= LayoutInflater.from(context).inflate(R.layout.repack_pick_list, viewGroup, false);
                pickListRepack = RepackPickdialog.findViewById(R.id.lay_TranslistRepackPickList);
                builder.setView(RepackPickdialog);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();*/
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(exportTranList.size()>0){
                    mToastMessage.showToast(PickRepackActivity.this,
                            "Please save the existing transaction.");

                }else{
                    deleteAlert();
                }/*else if( repackFGList.size()>0||repackFGIngerdientList.size()>0){
                    deleteAlert();
                }else {
                    mToastMessage.showToast(PickRepackActivity.this,
                            "No data to cancel");
                }*/


            }
        });

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
                        dialog.cancel();
                        btnOnHold.setEnabled(false);
                        //  isdeviceSideLock = false;
                    }
                });


        alertUser.show();
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
                        dialog.cancel();
                    }
                });


        alertUser.show();
    }



    private void deleteRepack(){
        mDbHelper.openWritableDatabase();
        mDbHelper.deleteRepackData();
        mDbHelper.closeDatabase();
    }

    private void greaterQtySaveProcess(){
        if (headerpicktaskdetail.size() > 0 && repackFGList.size() > 0) {
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
            mToastMessage.showToast(PickRepackActivity.this,
                    "Invalid Pallet");

        }
    }



    @Override
    public void onBackPressed() {
        if(exportTranList.size()>0){
            mToastMessage.showToast(PickRepackActivity.this,
                    "Please save the existing transaction.");

        }else{
            BackpressAlert();
        }
    }


    private void deleteAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackActivity.this);
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
                        btn_pickRepack.setEnabled(true);
                        repackNum.setText("");

                        if (isdeviceSideLock){
                            deleteRepack();
                            mDbHelper.openReadableDatabase();
                            repackFGList = mDbHelper.getRepackFG();
                            mDbHelper.closeDatabase();


                            repackFGAdapter = new RepackFGAdapter(PickRepackActivity.this, repackFGList);
                            transList.setAdapter(repackFGAdapter);

                            adapter.notifyDataSetChanged();
                            repackFGAdapter.notifyDataSetChanged();
                            edtRepackNum.setEnabled(true);
                            edtRepackNum.setText("");
                            btnIngredients.setEnabled(false);
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
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
    private void BackpressAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackActivity.this);
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
                        btn_pickRepack.setEnabled(true);
                        repackNum.setText("");

                        if (isdeviceSideLock){
                            deleteRepack();
                            mDbHelper.openReadableDatabase();
                            repackFGList = mDbHelper.getRepackFG();
                            mDbHelper.closeDatabase();


                            repackFGAdapter = new RepackFGAdapter(PickRepackActivity.this, repackFGList);
                            transList.setAdapter(repackFGAdapter);
                            adapter.notifyDataSetChanged();
                            repackFGAdapter.notifyDataSetChanged();
                            edtRepackNum.setEnabled(true);
                            edtRepackNum.setText("");
                            btnIngredients.setEnabled(false);
                            btnRecallLstDTE.setEnabled(true);
                            btnCancel.setEnabled(false);
                            btnOnHold.setEnabled(false);
                            edtRepackNum.requestFocus();

                        }else {
                            new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                            new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();

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

    private void qtyAlert() {
        if (dLotQty > dSoQty) {
            AlertDialog.Builder alertUser = new AlertDialog.Builder(PickRepackActivity.this);
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

                            edtRepackNum.setText("");
                            edtRepackNum.requestFocus();
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
                        new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompanyId).execute();
                        mSupporter.simpleNavigateTo(LoginScreenActivity.class);
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
                Intent theIntent = new Intent(PickRepackActivity.this, LoginScreenActivity.class);

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
                mToastMessage.showToast(PickRepackActivity.this,
                        "Unable to update Server.");
            }

        }
    }


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

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(PickRepackActivity.this);
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
                mToastMessage.showToast(PickRepackActivity.this,
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
                                mToastMessage.showToast(PickRepackActivity.this,
                                        "No Application match to Open Print File");
                            }
                        }
                    }, 1000);

                } catch (Exception e) {
                    mToastMessage.showToast(PickRepackActivity.this, "Not Software Match to Open Print File");
                    mSupporter.simpleNavigateTo(PickRepackActivity.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                mToastMessage.showToast(PickRepackActivity.this,
                        toasttext);
                mSupporter.simpleNavigateTo(PickRepackActivity.class);

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
    class GetRepackData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetRepackData(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PickRepackActivity.this);
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
                    btn_pickRepack.setEnabled(true);
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
                btn_pickRepack.setEnabled(false);
                Globals.RecallLstDTE_code = RepackNumber;

                if (!Globals.orgPano.equals("")){
                    repackNum.setText("Repack # "+Globals.orgPano.trim());
                }

                new LoadRepackData(adapter).execute();

            } else if (result.equals("Failed")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Repack # not found");
                edtRepackNum.requestFocus();
                      //  "Invalid Repack Number");

            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();

                mToastMessage.showToast(PickRepackActivity.this,
                        Getmsg);
            }else if (result.equals("Repack Completed.")){


                mToastMessage.showToast(PickRepackActivity.this,
                       // "DTE code Completed. Unable to Proceed");
                        "Repack Completed. Unable to Proceed");
                       // "Repack Completed");

            }else if(result.equalsIgnoreCase("time out error")){
                new GetRepackData(mUsername).execute();
            } else {
                mToastMessage.showToast(PickRepackActivity.this,
                       // "Unable to Hold");
                        "Unable to Proceed");
            }
            dialog.cancel();
        }
    }

    private class LoadRepackData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;


        public LoadRepackData(RepackFGAdapter repackFGAdapter) {
            dialog = new ProgressDialog(PickRepackActivity.this);
            repackFGAdapter = repackFGAdapter;
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickRepackActivity.this, mDbHelper, Globals.gUsercode);

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
                repackFGList = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                if (isdeviceSideLock){
                    StatusLockAlertDivice();

                }else if (isLocked){
                    StatusLockAlert();

                }

                repackFGAdapter = new RepackFGAdapter(PickRepackActivity.this, repackFGList);
                transList.setAdapter(repackFGAdapter);
                repackFGAdapter.notifyDataSetChanged();

                btnIngredients.setEnabled(true);
                btnRecallLstDTE.setEnabled(false);
                btnCancel.setEnabled(true);
                //btnOnHold.setEnabled(true);
                //edtRepackNum.setText(RepackNumber);
                edtRepackNum.setText(Globals.RecallLstDTE_code);
                edtRepackNum.setEnabled(false);
                //setViewsData();

                //smSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickRepackActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickRepackActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickRepackActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickRepackActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            // this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
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

    private void setViewsData() {
        mDbHelper.openReadableDatabase();
        headerpicktaskdetail = mDbHelper.selectPickTaskDetail(Globals.gPickTaskItem);
        SubTranlineCount = mDbHelper.mTranlineCount();
        int DoclineCount = mDbHelper.mDoclineCount();
        subTranNo = mDbHelper.getSubTranNo(selectedItem);
        subPickList =  mDbHelper.getPickTaskforSub(selectedItem);
        //int rowNo = mDbHelper.mRowNoCount();
// picktaskWHMQTYList = mDbHelper.getPickTaskWHMQTY(Globals.gPickTaskItem, Globals.gPickTaskWlotno);
        mDbHelper.closeDatabase();

        Globals.gTranlineno = 9999;
        Globals.gDoclineno = DoclineCount + 1;
        //Globals.gPTDetailRowCount = rowNo + 1;

        mDbHelper.getWritableDatabase();
        mDbHelper.updateTranPickDetails(Globals.gPickTaskItem,String.valueOf(Globals.gTranlineno),
                String.valueOf(Globals.gDoclineno),selectedItem,subTranNo,subPickList.get(0).getDocno(),subPickList.get(0).getDoctype());
        mDbHelper.closeDatabase();

        if (headerpicktaskdetail.size() > 0) {
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
            strTranlineNo =  String.valueOf(Globals.gTranlineno);
            orgTranlineno = headerpicktaskdetail.get(0).getorgTranlineno();
            uom = headerpicktaskdetail.get(0).getUom();
            doctype = headerpicktaskdetail.get(0).getDoctype();
            docno = headerpicktaskdetail.get(0).getDocno();
            doclineno = String.valueOf( Globals.gDoclineno);
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
            //rowNo = String.valueOf(Globals.gPTDetailRowCount);
        }
    }

    private void listViewAlert(){
        List<String> itemList = new ArrayList<String>();
        mDbHelper.openReadableDatabase();
        itemList = mDbHelper.getItemList();
        mDbHelper.closeDatabase();
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(PickRepackActivity.this);
        builderSingle.setTitle("Select SO item to Substitute");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PickRepackActivity.this, android.R.layout.select_dialog_singlechoice);
        for (int i =0;i<itemList.size();i++){
            arrayAdapter.add(itemList.get(i));
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edtRepackNum.setText("");
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                selectedItem = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(PickRepackActivity.this);

                new LoadRepackData(adapter).execute();

            }
        });
        builderSingle.show();
    }

    class ExportTranData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public ExportTranData(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickRepackActivity.this);
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
                String soap_action = NAMESPACE + METHOD_EXPORT_DATA;
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
                btn_pickRepack.setEnabled(true);
                mToastMessage.showToast(PickRepackActivity.this,
                      //  "Repack Updated");
                        "Repack Updated");
                repackNum.setText("");
                Globals.orgPano = "";
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteRepackData();
                mDbHelper.closeDatabase();

                edtRepackNum.setEnabled(true);
                edtRepackNum.setText("");
                btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Repacknum = "";
                Globals.fromExportData = true;

              //  finish();
                overridePendingTransition(0, 0);
                getIntent().putExtra("dteCode","");
                startActivity(getIntent());
                overridePendingTransition(0, 0);

//                Intent theIntent = new Intent(PickRepackActivity.this, PickRepackActivity.class);
//                theIntent.putExtra("dteCode","");
//                startActivity(theIntent);

                //mSupporter.simpleNavigateTo(PickRepackActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){
                new ExportTranData(mSessionId, mUsername, mCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }

    public void ExportData() {
        exportTranList = new ArrayList<RepackIngredients>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getRepackIngredientsForExport();
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
                new ExportTranData(mSessionId, mUsername, mCompanyId).execute();
            } else {
                ExportError();
            }
        }else {
            mToastMessage.showToast(PickRepackActivity.this,
                    "Unable to update Server");
        }
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PickRepackActivity.this);
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

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PickRepackActivity.this);
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
                mToastMessage.showToast(PickRepackActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickRepackActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    // Method that returns the XML to be exported
    public String getRecordXmlExportPO(List<RepackIngredients> dList) {
        String exportPODataXml = "";
        try {
            ExportRepackData exportData = new ExportRepackData();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "RepackIngredients" + ">");
            for (int i = 0; i < dList.size(); i++) {
                //exportData.writeXml(dList.get(i), sb, PickRepackActivity.this, mDbHelper);
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



    class RepackPickList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;


        String result = "";



        public RepackPickList(String Session) {
            this.pSessionId = Session;

            dialog = new ProgressDialog(PickRepackActivity.this);
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

                SoapObject request = new SoapObject(NAMESPACE, REPACKLIST);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            /*    File xmlData = Supporter.getImportFolderPath(mUsername
                        + "/Result/RepackPickList.xml");
                String pXmldata = FileUtils.readFileToString(xmlData);*/
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(pSessionId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(mCompany);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + REPACKLIST;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "Result", "RepackPickList" + ".xml");
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

                    new  LoadRepackPickList().execute();

             /*   AlertDialog.Builder builder = new AlertDialog.Builder(PickRepackActivity.this);
                ViewGroup viewGroup = findViewById(android.R.id.content);
                View RepackPickdialog= LayoutInflater.from(context).inflate(R.layout.repack_pick_list, viewGroup, false);
                pickListRepack = RepackPickdialog.findViewById(R.id.lay_TranslistRepackPickList);
                builder.setView(RepackPickdialog);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();*/


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){
                new ExportTranData(mSessionId, mUsername, mCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickRepackActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    private class LoadRepackPickList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadRepackPickList() {
            dialog = new ProgressDialog(PickRepackActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PickRepackActivity.this, mDbHelper, Globals.gUsercode);

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

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "Result", "RepackPickList" + ".xml");
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
                List<RepackList> list = mDbHelper.getRepackListData();
                mDbHelper.closeDatabase();

                LayoutInflater li = LayoutInflater.from(PickRepackActivity.this);
                View promptsView = li.inflate(R.layout.repack_pick_list,null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        PickRepackActivity.this);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                pickListRepack = (ListView) promptsView.findViewById(R.id.lst_repackPickList);
                cancel = (Button) promptsView.findViewById(R.id.Cancel_btn);

                repacklistadapter = new RepackListAdapter(PickRepackActivity.this, list);
                pickListRepack.setAdapter(repacklistadapter);


                pickListRepack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                        RepackList repackList = new RepackList();
                        repackList = (RepackList) pickListRepack
                                .getItemAtPosition(i);
                         repackNo = repackList.getPano();

                         edtRepackNum.setText(repackNo);

                       // RepackNumber = edtRepackNum.getText().toString();


                        RepackNumber = repackNo;
                        RepackNumber = fixedLengthString(RepackNumber);

                        mDbHelper.openReadableDatabase();
                        Boolean result = mDbHelper.isRepackListAvailable(RepackNumber);
                        mDbHelper.closeDatabase();

                        if(RepackNumber.equalsIgnoreCase("")){
                            mToastMessage.showToast(PickRepackActivity.this,
                                   // "Please Enter or Scan the Repack #");
                                    "Please Scan or Enter the  Repack # ");
                            edtRepackNum.requestFocus();
                            scanResult=true;

                            btn_pickRepack.setEnabled(true);



                        } /*else if (!result){
                            mToastMessage.showToast(PickRepackActivity.this,
                                    "Invalid Repack");
                            btn_pickRepack.setEnabled(true);
                        } */else {

                            new GetRepackData(mUsername).execute();


                        }




                         alertDialog.dismiss();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
                //alertDialog.getWindow().setLayout(700, 400);


            } else if (result.equals("nosd")) {
                mToastMessage.showToast(PickRepackActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(PickRepackActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(PickRepackActivity.this, "File not available");
            } else {
                mToastMessage.showToast(PickRepackActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            //    this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.setMessage("Loading..");
            this.dialog.show();
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
            dialog = new ProgressDialog(PickRepackActivity.this);
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
                repackFGList = mDbHelper.getRepackFG();
                mDbHelper.closeDatabase();

                repackFGAdapter = new RepackFGAdapter(PickRepackActivity.this, repackFGList);
                transList.setAdapter(repackFGAdapter);
                adapter.notifyDataSetChanged();
                repackFGAdapter.notifyDataSetChanged();
                edtRepackNum.setEnabled(true);
                edtRepackNum.setText("");
                btnIngredients.setEnabled(false);
                btnRecallLstDTE.setEnabled(true);
                btnCancel.setEnabled(false);
                btnOnHold.setEnabled(false);
                edtRepackNum.requestFocus();
                Globals.orgPano="";
                Globals.orgPano="";


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "The server did not respond please try again");
            }else if (result.equalsIgnoreCase("time out error")){
                new CancelRepack(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickRepackActivity.this,
                        "The server did not respond please try again");
            } else {
                mToastMessage.showToast(PickRepackActivity.this,
                        "The server did not respond please try again");
                      //  "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }

    public static String fixedLengthString(String string) {
        return String.format("%1$10"+"s", string);
    }

}



