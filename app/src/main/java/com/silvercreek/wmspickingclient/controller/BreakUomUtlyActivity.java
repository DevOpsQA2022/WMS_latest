package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.BreakerUOMList;
import com.silvercreek.wmspickingclient.model.BreakerUomUtility;
import com.silvercreek.wmspickingclient.model.MoveManually;
import com.silvercreek.wmspickingclient.model.MoveManuallyTransaction;
import com.silvercreek.wmspickingclient.model.company;
import com.silvercreek.wmspickingclient.model.configsettings;
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

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class BreakUomUtlyActivity extends AppBaseActivity {

    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_GET_WLOTNO = "BreakerUOMUtility_LookupData";
    public static final String METHOD_POST_LOTSLOTDATA = "BreakerUOMUtility_Save";
    private SharedPreferences sharedpreferences;
    private int mTimeout;


    private Button btnSave,btnCancel;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;


    private configsettings userDataList;
    private EditText edtWlotNo,edtSlotNo,edtQty;
    private String strWlotNo="",strSlotNo="";
    private ArrayList<company> companyArrayList;
    private File mImpOutputFile;
    private List<String> slotNolist;
    private Spinner spnUOM;
    private List<String> mSpinnerList;
    private ArrayAdapter<String> adapter;
    private String selectedUOM="";
    private String strQty="";
    private double availQty=0;
    private double enteredQty=0;
    private MoveManuallyTransaction moveManuallyTransaction;
    private MoveManually moveManually;
    private ArrayList<MoveManually> moveManuallyArrayList;
    private TableRow tblScanLot,tblItem,tblLot,tblSlot,tblQty,tblUOM,tblItemDesc;
    private RelativeLayout btnLayout;
    private TextView lblCases,tvLotno,tvItem,tvItemDesc;
    private ArrayList<BreakerUomUtility> breakerUOMListArrayList;
    private ArrayList<BreakerUOMList> uomList;
    private String convertedQty;

    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId="", mCompany,mUsername,mDeviceId ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaker_uom_utility);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

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
        NAMESPACE = NAMESPACE +"/";
        Globals.gNamespace=NAMESPACE;
        Globals.gProtocol=URL_PROTOCOL;
        Globals.gServicename=URL_SERVICE_NAME;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");

        btnSave = findViewById(R.id.btnMMSave);
        btnCancel = findViewById(R.id.btnMMCancel);
        edtWlotNo = findViewById(R.id.edtMMWLotNo);
        edtSlotNo = findViewById(R.id.edtMMSlotno);
        edtQty = findViewById(R.id.edtMMQty);
        spnUOM = findViewById(R.id.spnUOM);
        lblCases = findViewById(R.id.tvQty);

        tblScanLot = findViewById(R.id.tablerow1);
        tblItem  = findViewById(R.id.tablerow2);
        tblItemDesc = findViewById(R.id.tablerow3);
        tblLot = findViewById(R.id.tablerow4);
        tblSlot = findViewById(R.id.tablerow5);
        tblQty = findViewById(R.id.tablerow6);
        tblUOM = findViewById(R.id.tablerow7);
        btnLayout = findViewById(R.id.rlayout);
        tvLotno = findViewById(R.id.txtViewLotNo);
        tvItem = findViewById(R.id.txtViewItem);
        lblCases = findViewById(R.id.tvQty);
        tvItemDesc = findViewById(R.id.txtViewItemDesc);

        edtSlotNo.setEnabled(false);
        edtQty.setEnabled(false);
        spnUOM.setEnabled(false);
        btnSave.setEnabled(false);


        if (SOFT_KEYBOARD.equals("CHECKED")){

            edtWlotNo.setShowSoftInputOnFocus(false);
            edtSlotNo.setShowSoftInputOnFocus(false);
            edtQty.setShowSoftInputOnFocus(false);

        }else {
            edtWlotNo.setShowSoftInputOnFocus(true);
            edtSlotNo.setShowSoftInputOnFocus(true);
            edtQty.setShowSoftInputOnFocus(true);

        }

        edtWlotNo.requestFocus();
        if(edtWlotNo.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }



        hideView();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlert();
            }
        });


        edtWlotNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            strWlotNo = edtWlotNo.getText().toString();
                            strWlotNo = fixedLengthString(strWlotNo);
                            new GetWlotNo().execute();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        edtSlotNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            strSlotNo = edtSlotNo.getText().toString();
                            mDbHelper.openReadableDatabase();
                            slotNolist = mDbHelper.getBUSlotNoList(strWlotNo);
                            mDbHelper.closeDatabase();

                            if(slotNolist.contains(strSlotNo)){
                                mDbHelper.openReadableDatabase();
                                mSpinnerList = mDbHelper.getBUUOMList();
                                mDbHelper.closeDatabase();

                                adapter = new ArrayAdapter<String>(BreakUomUtlyActivity.this, android.R.layout.simple_spinner_item, mSpinnerList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                spnUOM.setAdapter(adapter);
                                edtQty.setEnabled(true);
                                spnUOM.setEnabled(true);
                                btnSave.setEnabled(true);
                                spnUOM.requestFocus();
                            }else {
                                mToastMessage.showToast(BreakUomUtlyActivity.this,
                                        "Invalid Slot No");
                                edtQty.setEnabled(false);
                                spnUOM.setEnabled(false);
                                edtSlotNo.requestFocus();
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        spnUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                selectedUOM = spnUOM.getSelectedItem().toString();
                /*mDbHelper.openReadableDatabase();
                Boolean isLocked = mDbHelper.isMMLocked(strWlotNo,strSlotNo,selectedUOM);
                mDbHelper.closeDatabase();
                if(isLocked==true){
                    mToastMessage.showToast(BreakUomUtlyActivity.this,
                            "Item is locked. Unable to proceed");
                    edtQty.setEnabled(false);
                    btnSave.setEnabled(true);
                }else {
                    edtQty.setEnabled(true);
                    btnSave.setEnabled(true);
                }*/

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                String strAvailQty = mDbHelper.mgetBUQty(strWlotNo,strSlotNo);
                String strConvFact = mDbHelper.mgetBuBrUnit(selectedUOM);
                mDbHelper.closeDatabase();
                availQty = Double.parseDouble(strAvailQty);
                strQty = edtQty.getText().toString().trim();
                convertedQty = String.valueOf(Double.parseDouble(strConvFact)*Double.parseDouble(strQty));

                if(strQty.equalsIgnoreCase(null)||strQty.equalsIgnoreCase("")){
                    mToastMessage.showToast(BreakUomUtlyActivity.this,
                            "Please enter the Qty");
                }else {
                    enteredQty = Integer.parseInt(strQty);
                    if(enteredQty<=0){
                        mToastMessage.showToast(BreakUomUtlyActivity.this,
                                "Please enter the valid Qty ");
                    }else if(enteredQty>availQty){
                        mToastMessage.showToast(BreakUomUtlyActivity.this,
                                "Qty entered is greater than available Qty");
                    }
                    else{
                       /* moveManuallyTransaction.setMmTranWlotno(strWlotNo);
                        moveManuallyTransaction.setMmTranSlot(strSlotNo);
                        moveManuallyTransaction.setMmTranUOM(selectedUOM);
                        moveManuallyTransaction.setMmTranEqty(String.valueOf(enteredQty));*/
                        saveBreakerAlert();

                    }
                }

            }
        });

    }

    class GetWlotNo extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;
        private String lotNum;

        public GetWlotNo() {

            dialog = new ProgressDialog(BreakUomUtlyActivity.this);
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
                mDbHelper.openReadableDatabase();
                mSessionId = mDbHelper.mGetSessionId();
                //SuserDataList = mDbHelper.mGetUserData();
                lotNum = strWlotNo.trim();
                companyArrayList = mDbHelper.getCompanyList();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_WLOTNO);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Funcation argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0) {     //SCS CIRK 2022/07/25 CT69649C:
                    info = new PropertyInfo();
                    info.setName("pCompany"); // .Net Funcation argument key
                    info.setValue(companyArrayList.get(0).getCompanyID());
                    info.setType(String.class);
                    request.addProperty(info);
                }else{
                    mToastMessage.showToast(BreakUomUtlyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(BreakUomUtlyActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Funcation argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Funcation argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Funcation argument key
                info.setValue(lotNum);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_WLOTNO;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "breakerUOM" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().contains("orphaned")) {
                    result = "orphaned";

                } else if (resultString.toString().contains("Invalid Pallet")) {
                    result = "Invalid Pallet";
                } else if (resultString.toString().contains("No Data Found ")) {
                    result = "No Data Found ";
                } else if(resultString.toString().contains("BreakerUomWHMQTYList")) {
                    result = "success";
                } else {
                    result = "Invalid Pallet";
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
                new LoadWlotData().execute();

            } else if (result.equals("orphaned")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Pallet Code has orphaned item number ");
            } else if (result.equals("No Data Found ")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "No Data Found for this Pallet");
            } else if (result.equals("Invalid Pallet")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Invalid Pallet");
            } else {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        " No Data Found ");
            }

            dialog.cancel();
        }

    }

    private class LoadWlotData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadWlotData() {
            dialog = new ProgressDialog(BreakUomUtlyActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(BreakUomUtlyActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteBreakerUOM();
                mDbHelper.closeDatabase();
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        String serCompName = compList.get(c);

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "BreakerUOM";

                            if ((c > 0) && (fileName.equals("BreakerUOM"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "breakerUOM" + ".xml");
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

                        if (!result.equals("success")) { // to break from
                            // executing other companies
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
                //mSupporter.simpleNavigateTo(LoadPickPalletGetPalletActivity.class);

                mDbHelper.openReadableDatabase();
                uomList = mDbHelper.getBreakerUOMList();
                mDbHelper.closeDatabase();
                if(uomList.size()>0){
                    showView();
                    setData();
                    edtSlotNo.setEnabled(true);
                    edtSlotNo.requestFocus();
                }else {
                    tblScanLot.setVisibility(View.VISIBLE);
                    deleteBreakerRecord();
                    mSupporter.simpleNavigateTo(BreakUomUtlyActivity.class);
                    mToastMessage.showToast(BreakUomUtlyActivity.this, "No Breaker UOM available for this Pallet");
                }

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this, "File not available");
            } else {
                mToastMessage.showToast(BreakUomUtlyActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Data is loaded");
            this.dialog.show();
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
            mDbHelper.mSetTransactionSuccess(); // setting the successfull transaction.
            Log.i("Transaction success", "Transaction success.");
            mDbHelper.mEndTransaction();
            Log.i("Transaction success", "Transaction end.");
            mDbHelper.closeDatabase();
            Log.i("DB closed", "Database closed successfully.");
        }
    }

    class PostLotData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;
        private String lotNum;
        private String lotRefNum;

        public PostLotData() {

            dialog = new ProgressDialog(BreakUomUtlyActivity.this);
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
                mDbHelper.openReadableDatabase();
                mSessionId = mDbHelper.mGetSessionId();
                //SuserDataList = mDbHelper.mGetUserData();
                lotNum = mDbHelper.breakUOMStrLotNum(strWlotNo);
                companyArrayList = mDbHelper.getCompanyList();
                lotRefNum = strWlotNo.trim();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_POST_LOTSLOTDATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Funcation argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if (companyArrayList.size() > 0) {      //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pCompany"); // .Net Funcation argument key
                info.setValue(companyArrayList.get(0).getCompanyID());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(BreakUomUtlyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(BreakUomUtlyActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Funcation argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Funcation argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Funcation argument key
                info.setValue(lotNum);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Funcation argument key
                info.setValue(lotRefNum);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Funcation argument key
                info.setValue(strSlotNo);
                info.setType(String.class);
                request.addProperty(info);

                if(breakerUOMListArrayList.size()>0){       //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pFromUmeasur"); // .Net Funcation argument key
                info.setValue(breakerUOMListArrayList.get(0).getBuUOM());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(BreakUomUtlyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in breakerUOMListArrayList(BreakUomUtlyActivity)");
                }

                info = new PropertyInfo();
                info.setName("pToUmeasur"); // .Net Funcation argument key
                info.setValue(selectedUOM);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pFromTqty"); // .Net Funcation argument key
                info.setValue(String.valueOf(enteredQty));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pToTqty"); // .Net Funcation argument key
                info.setValue(String.valueOf(convertedQty));
                info.setType(String.class);
                request.addProperty(info);

                if(breakerUOMListArrayList.size()>0){       //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pItem"); // .Net Funcation argument key
                info.setValue(breakerUOMListArrayList.get(0).getBuItem());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(BreakUomUtlyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in breakerUOMListArrayList(BreakUomUtlyActivity)");
                }

           /*     info = new PropertyInfo();
                info.setName("lotrefid"); // .Net Funcation argument key
                info.setValue(breakerUOMListArrayList.get(0).getBuLotRefId());
                info.setType(String.class);
                request.addProperty(info);*/

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_POST_LOTSLOTDATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "postBreakerUOM" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().equalsIgnoreCase("false")) {
                    result = "Fail to login please check user and password";

                } else if (resultString.toString().contains("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().contains("No data found.")) {
                    result = "No data found in server";
                } else if(resultString.toString().contains("<Result>true</Result>")) {
                    result = "success";
                } else if(resultString.toString().contains("Quantity is too short ")){
                    result = "Qty not available";
                }else {
                    result = "Unable to update";
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

                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Data posted successfulluy");
                hideView();
                tblScanLot.setVisibility(View.VISIBLE);
                deleteBreakerRecord();
                mSupporter.simpleNavigateTo(BreakUomUtlyActivity.class);

            } else if (result.equals("Qty not available")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Qty not available");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Data server connection failed.");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "No data found.");
            } else {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Unable to Update");
            }

            dialog.cancel();
        }

    }

    @Override
    public void onBackPressed() {
        //cancelAlert();
        deleteBreakerRecord();
        mSupporter.simpleNavigateTo(MainmenuActivity.class);
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
                        hideView();
                        tblScanLot.setVisibility(View.VISIBLE);
                        deleteBreakerRecord();
                        mSupporter.simpleNavigateTo(BreakUomUtlyActivity.class);
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

    public void saveBreakerAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Convert "+enteredQty+" "+breakerUOMListArrayList.get(0).getBuUOM()+"(s) to " + convertedQty+" " + selectedUOM +"(s)");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(breakerUOMListArrayList.size()>0) {
                            new PostLotData().execute();
                        }else{
                            mToastMessage.showToast(BreakUomUtlyActivity.this, "No Data Available");
                            LogfileCreator.mAppendLog("No data available in breakerUOMListArrayList(BreakUomUtlyActivity)");
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

    public void showView(){
        tblItem.setVisibility(View.VISIBLE);
        tblItemDesc.setVisibility(View.VISIBLE);
        tblLot.setVisibility(View.VISIBLE);
        tblSlot.setVisibility(View.VISIBLE);
        tblQty.setVisibility(View.VISIBLE);
        tblUOM.setVisibility(View.VISIBLE);
        btnLayout.setVisibility(View.VISIBLE);
        tblScanLot.setVisibility(View.GONE);
    }

    public void hideView(){
        tblItem.setVisibility(View.GONE);
        tblItemDesc.setVisibility(View.GONE);
        tblLot.setVisibility(View.GONE);
        tblSlot.setVisibility(View.GONE);
        tblQty.setVisibility(View.GONE);
        tblUOM.setVisibility(View.GONE);
        btnLayout.setVisibility(View.GONE);
    }

    public void setData(){
        mDbHelper.openReadableDatabase();
        breakerUOMListArrayList = mDbHelper.getBreakerUOM(strWlotNo);
        uomList = mDbHelper.getBreakerUOMList();
        mDbHelper.closeDatabase();

        if(breakerUOMListArrayList.size()>0) {      //SCS CIRK 2022/07/25 CT69649C:

            tvLotno.setText(breakerUOMListArrayList.get(0).getBuWlotno());
            tvItem.setText(breakerUOMListArrayList.get(0).getBuItem());
            tvItemDesc.setText(breakerUOMListArrayList.get(0).getBuItemDesc());
            lblCases.setText(breakerUOMListArrayList.get(0).getBuStkumid()+"(s)" + " to convert :");

        }else{
            mToastMessage.showToast(BreakUomUtlyActivity.this, "No Data Available");
            LogfileCreator.mAppendLog("No data available in breakerUOMListArrayList(BreakUomUtlyActivity)");
        }


    }
    private void deleteBreakerRecord(){
        mDbHelper.openWritableDatabase();
        mDbHelper.deleteBreakerUOM();
        mDbHelper.closeDatabase();
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
        mDbHelper.mSetTransactionSuccess(); // setting the successfull transaction
        Log.i("Transaction success", "Transaction success.");
        mDbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        mDbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }

    public static String fixedLengthString(String string) {
        return String.format("%1$10"+"s", string);
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


            dialog = new ProgressDialog(BreakUomUtlyActivity.this);
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
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(BreakUomUtlyActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
      //  new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }

}
