package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
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

public class MoveManuallyActivity extends AppBaseActivity {

    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";

  //  public static final String METHOD_GET_SLOTNO = "MoveManually_LookupDataTo";
  //  public static final String METHOD_POST_LOTSLOTDATA = "MoveManually_SaveFrom";

  public static final String METHOD_POST_SLOTNO = "MoveManually_SaveTo";
    public static final String METHOD_GET_SLOTNO = "MoveManually_ValidSlot";
    public static final String METHOD_GET_WLOTNO = "MoveManually_LookupDataFrom";

    private SharedPreferences sharedpreferences;
    private int mTimeout;

    private List<MoveManuallyTransaction> mmTran;
    private Button btnSave,btnCancel;
    private String strEnteredQty = "";
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    boolean automaticChanged = false;
    private Supporter supporter;
    private ToastMessage mToastMessage;
    private String mSessionId;
    private String strToSlot ="";
    private String edtPallNo ="";
    private double remainingQty=0;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static String SOFT_KEYBOARD = "";
    private String  mCompany,mUsername,mDeviceId;
    private configsettings userDataList;
    private EditText edtWlotNo,edtSlotNo,edtQty,edttoSlot,edtToQty,edtMMW_WlotNo;
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
    private ListView moveList;
    private MoveManuallyAdapter mpmAdapter;
    private Button btnGridCancel;
    private String Validation_Slot = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_manually);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();
        moveManuallyTransaction = new MoveManuallyTransaction();

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
        NAMESPACE = NAMESPACE +"/";
        SOFT_KEYBOARD = sharedpreferences.getString("SoftKey", "");
        Globals.gNamespace=NAMESPACE;
        Globals.gProtocol=URL_PROTOCOL;
        Globals.gServicename=URL_SERVICE_NAME;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");

        btnSave = findViewById(R.id.btnMMSave);
        btnCancel = findViewById(R.id.btnMMCancel);
        edtWlotNo = findViewById(R.id.edtMMWLotNo);
        edtMMW_WlotNo = findViewById(R.id.edtMMW_WlotNo);
        edtSlotNo = findViewById(R.id.edtMMSlotno);
        edtQty = findViewById(R.id.edtfromMMQty);
        edttoSlot = findViewById(R.id.edtToSLot);
        //edtToQty = findViewById(R.id.edtMMQty);
        spnUOM = findViewById(R.id.spnUOM);


        edttoSlot.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(10)});



        if (SOFT_KEYBOARD.equals("CHECKED")){

            edtWlotNo.setShowSoftInputOnFocus(false);
            edtSlotNo.setShowSoftInputOnFocus(false);
            edtQty.setShowSoftInputOnFocus(false);
            edttoSlot.setShowSoftInputOnFocus(false);
          //  edtToQty.setShowSoftInputOnFocus(false);
            edtMMW_WlotNo.setShowSoftInputOnFocus(false);
        }else {
            edtWlotNo.setShowSoftInputOnFocus(true);
            edtSlotNo.setShowSoftInputOnFocus(true);
            edtQty.setShowSoftInputOnFocus(true);
            edttoSlot.setShowSoftInputOnFocus(true);
           // edtToQty.setShowSoftInputOnFocus(true);
            edtMMW_WlotNo.setShowSoftInputOnFocus(true);
        }

        edtWlotNo.requestFocus();
        edtSlotNo.setEnabled(false);
        edtQty.setEnabled(false);
        spnUOM.setEnabled(false);
        btnSave.setEnabled(false);
        edtMMW_WlotNo.setEnabled(false);
        btnCancel.setEnabled(false);
     //   edtToQty.setEnabled(false);
        edttoSlot.setEnabled(false);

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

  /*      edtSlotNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            strSlotNo = edtSlotNo.getText().toString();
                            mDbHelper.openReadableDatabase();
                            slotNolist = mDbHelper.getMMSlotNoList(strWlotNo);
                            mDbHelper.closeDatabase();

                            if(slotNolist.contains(strSlotNo)){
                                mDbHelper.openReadableDatabase();
                                mSpinnerList = mDbHelper.getMMUOMList(strWlotNo,strSlotNo);
                                mDbHelper.closeDatabase();

                                adapter = new ArrayAdapter<String>(MoveManuallyActivity.this, android.R.layout.simple_spinner_item, mSpinnerList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                spnUOM.setAdapter(adapter);
                                edtQty.setEnabled(true);
                                spnUOM.setEnabled(true);
                                spnUOM.requestFocus();
                            }else {
                                mToastMessage.showToast(MoveManuallyActivity.this,
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
*/
        spnUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                selectedUOM = spnUOM.getSelectedItem().toString();
                mDbHelper.openReadableDatabase();
                Boolean isLocked = mDbHelper.isMMLocked(strWlotNo,strSlotNo,selectedUOM);
                mDbHelper.closeDatabase();
                if(isLocked==true){
                    mToastMessage.showToast(MoveManuallyActivity.this,
                            "Item is locked. Unable to proceed");
                    edtQty.setEnabled(true);
                    btnSave.setEnabled(false);
                }else {
                //    edtQty.setEnabled(true);
                    edtQty.setEnabled(false);
                    edtMMW_WlotNo.setEnabled(false);
                   // btnSave.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        //commented 20.10.22
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strSlotNo = edtSlotNo.getText().toString();
                strToSlot = edttoSlot.getText().toString().trim();
                edtPallNo = edtWlotNo.getText().toString().trim();
                if (strToSlot.equalsIgnoreCase(null) || strToSlot.equalsIgnoreCase("")) {
                    mToastMessage.showToast(MoveManuallyActivity.this,
                            "Enter valid To Slot");

                }else if (strSlotNo.equals(strToSlot)) {
                    mToastMessage.showToast(MoveManuallyActivity.this,"From Slot and To Slot should not be the same.");
                    edttoSlot.setText("");
                    edttoSlot.requestFocus();
                }else{
                    new GetSlotNoForFinelSave().execute();
                }




              /*  strSlotNo = edtSlotNo.getText().toString();
                strToSlot = edttoSlot.getText().toString().trim();

                if (strSlotNo.equals(strToSlot)) {
                    mToastMessage.showToast(MoveManuallyActivity.this,"From Slot and To Slot should not be the same.");
                    edttoSlot.setText("");
                }else if (Globals.TOSLOT.equals(strToSlot)){

                    mDbHelper.openReadableDatabase();
                    String strAvailQty = mDbHelper.mgetMMQty(strWlotNo, strSlotNo, selectedUOM);
                    mDbHelper.closeDatabase();

                    availQty = Double.parseDouble(strAvailQty);
                    strQty = edtQty.getText().toString().trim();
                    //  strQty = edtToQty.getText().toString().trim();


                    if (strToSlot.equalsIgnoreCase(null) || strToSlot.equalsIgnoreCase("")) {
                        mToastMessage.showToast(MoveManuallyActivity.this,
                                "Please enter the to Slot");

                    } else if (strQty.equalsIgnoreCase(null) || strQty.equalsIgnoreCase("")) {
                        mToastMessage.showToast(MoveManuallyActivity.this,
                                "Please enter the Qty");
                    } else {
                        enteredQty = Integer.parseInt(strQty);
                        if (enteredQty <= 0) {
                            mToastMessage.showToast(MoveManuallyActivity.this,
                                    "Please enter the valid Qty");
                        } else if (enteredQty > availQty) {
                            mToastMessage.showToast(MoveManuallyActivity.this,
                                    "Qty entered is greater than available Qty");
                        } else {
                            moveManuallyTransaction.setMmTranWlotno(strWlotNo);
                            moveManuallyTransaction.setMmTranSlot(strSlotNo);
                            moveManuallyTransaction.setMmTranUOM(selectedUOM);
                            moveManuallyTransaction.setMmTranEqty(String.valueOf(enteredQty));

                            new PostLotData().execute();

                        }
                    }

                }else {
                    mToastMessage.showToast(MoveManuallyActivity.this,"Enter the valid To Slot");
                    edttoSlot.setText("");
                    btnSave.setEnabled(false);
                }*/
            }
        });


       /* btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                mmTran = mDbHelper.getMmTran();
                mDbHelper.closeDatabase();


                Globals.mmTLotRefid = mmTran.get(0).getMmTranLotrefid();
                Globals.mmTSlot = mmTran.get(0).getMmTranSlot();
                Globals.mmTUOM = mmTran.get(0).getMmTranUOM();

                mDbHelper.openReadableDatabase();
                String strAvailQty = mDbHelper.mgetMMTranQty(Globals.mmTLotRefid,Globals.mmTSlot,Globals.mmTUOM);
                mDbHelper.closeDatabase();
                availQty = Double.parseDouble(strAvailQty);
                strEnteredQty = edtToQty.getText().toString().trim();

                if(strEnteredQty.equalsIgnoreCase(null)||strEnteredQty.equalsIgnoreCase("")){
                    mToastMessage.showToast(MoveManuallyActivity.this,
                            "Please enter the Qty");
                }else {
                    enteredQty = Integer.parseInt(strEnteredQty);
                    if(enteredQty>availQty||enteredQty<=0){
                        mToastMessage.showToast(MoveManuallyActivity.this,
                                "Please enter the valid Qty");
                    }else{

                        remainingQty = availQty-enteredQty;
                        new PostLotDataFinal().execute();

                    }
                }

            }
        });*/




        edttoSlot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            strToSlot = edttoSlot.getText().toString();
                            new GetSlotNo().execute();
                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }
        });

    }

    class PostLotDataFinal extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public PostLotDataFinal() {

            dialog = new ProgressDialog(MoveManuallyActivity.this);
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
                companyArrayList = mDbHelper.getCompanyList();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_POST_SLOTNO);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Function argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                    info = new PropertyInfo();
                    info.setName("pCompany"); // .Net Function argument key
                    info.setValue(companyArrayList.get(0).getCompanyID());
                    info.setType(String.class);
                    request.addProperty(info);
                }else{
                    mToastMessage.showToast(MoveManuallyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MMToSlotActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Function argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Function argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Function argument key
                info.setValue(edtPallNo);
               // info.setValue(Globals.mmTLotRefid = fixedLengthString(Globals.mmTLotRefid));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Function argument key
                info.setValue(Globals.mmTlot = fixedLengthString(Globals.mmTlot));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Function argument key
                info.setValue(strToSlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUmeasur"); // .Net Function argument key
                info.setValue(Globals.mmTUOM);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pTqty"); // .Net Function argument key
                info.setValue(String.valueOf(enteredQty));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pItem"); // .Net Function argument key
                info.setValue(Globals.mmTItem);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pFromSlot"); // .Net Function argument key
                info.setValue(strSlotNo);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_POST_SLOTNO;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "postlotno" + ".xml");
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

                } else if (resultString.toString().equalsIgnoreCase("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().equalsIgnoreCase("No data found.")) {
                    result = "No data found in server";
                } else if(resultString.toString().contains("<Result>true</Result>")) {
                    result = "success";
                } else if(resultString.toString().contains("Quantity is too short")){
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

                mDbHelper.openWritableDatabase();
                if(remainingQty>0){
                    mDbHelper.UpdateMMTranQty(Globals.mmTLotRefid,Globals.mmTSlot,Globals.mmTUOM,String.valueOf(remainingQty));
                }else {
                    mDbHelper.deleteMMTran(Globals.mmTLotRefid,Globals.mmTSlot,Globals.mmTUOM);
                }
                mDbHelper.deletemoveManually();
                mDbHelper.closeDatabase();

                edtSlotNo.setText("");
                edtMMW_WlotNo.setText("");
                edtWlotNo.setText("");
                edtQty.setText("");
                spnUOM.setAdapter(null);
                //edtToQty.setText("");
                edttoSlot.setText("");
                edtWlotNo.setEnabled(true);
                edtSlotNo.setEnabled(false);
                edtQty.setEnabled(false);
                edtMMW_WlotNo.setEnabled(false);
                spnUOM.setEnabled(false);
                btnSave.setEnabled(false);
                //edtToQty.setEnabled(false);
                edttoSlot.setEnabled(false);
                edtWlotNo.requestFocus();
                alertUser();




               // mSupporter.simpleNavigateTo(MPMList.class);

            } else if (result.equals("Quantity is too short")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Qty not available");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Data server connection failed.");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "No data found.");
            } else {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Unable to Update");
            }

            dialog.cancel();
        }

    }

    class GetSlotNoForFinelSave extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetSlotNoForFinelSave() {

            dialog = new ProgressDialog(MoveManuallyActivity.this);
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
                companyArrayList = mDbHelper.getCompanyList();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_SLOTNO);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Function argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                    info = new PropertyInfo();
                    info.setName("pCompany"); // .Net Function argument key
                    info.setValue(companyArrayList.get(0).getCompanyID());
                    info.setType(String.class);
                    request.addProperty(info);
                }else{
                    mToastMessage.showToast(MoveManuallyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MMToSlotActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Function argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Function argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Function argument key
                info.setValue(Globals.mmTlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Function argument key
                info.setValue(Globals.mmTlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Function argument key
                info.setValue(strToSlot);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_SLOTNO;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "TranSlotno" + ".xml");
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
                    result = "Invaild Slot No";

                } else if (resultString.toString().equalsIgnoreCase("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().contains("<Result>True</Result>")) {
                    result = "success";
                } else {
                    result = "Invalid";
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


                strSlotNo = edtSlotNo.getText().toString();
                strToSlot = edttoSlot.getText().toString().trim();

               /* if (strSlotNo.equals(strToSlot)) {
                    mToastMessage.showToast(MoveManuallyActivity.this,"From Slot and To Slot should not be the same.");
                    edttoSlot.setText("");
                }else *//*if (Globals.TOSLOT.equals(strToSlot))*//* {
*/
                    mDbHelper.openReadableDatabase();
                    String strAvailQty = mDbHelper.mgetMMQty(strWlotNo, strSlotNo, selectedUOM);
                    mDbHelper.closeDatabase();

                    availQty = Double.parseDouble(strAvailQty);
                    strQty = edtQty.getText().toString().trim();
                    //  strQty = edtToQty.getText().toString().trim();


                    if (strToSlot.equalsIgnoreCase(null) || strToSlot.equalsIgnoreCase("")) {
                        mToastMessage.showToast(MoveManuallyActivity.this,
                                "Enter valid To Slot");

                    } /*else if (strQty.equalsIgnoreCase(null) || strQty.equalsIgnoreCase("")) {
                        mToastMessage.showToast(MoveManuallyActivity.this,
                                "Please enter the Qty");
                    }*/ else {
                        enteredQty = Integer.parseInt(strQty);
                        if (enteredQty <= 0) {
                            mToastMessage.showToast(MoveManuallyActivity.this,
                                    "Please enter the valid Qty");
                        } else if (enteredQty > availQty) {
                            mToastMessage.showToast(MoveManuallyActivity.this,
                                    "Qty entered is greater than available Qty");
                        } else {
                            moveManuallyTransaction.setMmTranWlotno(strWlotNo);
                            moveManuallyTransaction.setMmTranSlot(strSlotNo);
                            moveManuallyTransaction.setMmTranUOM(selectedUOM);
                            moveManuallyTransaction.setMmTranEqty(String.valueOf(enteredQty));

                            new PostLotData().execute();

                        }
                    }

               /* }*//*else {
                    mToastMessage.showToast(MoveManuallyActivity.this,"Enter the valid To Slot");
                    edttoSlot.setText("");
                    btnSave.setEnabled(false);
                }*/

            } else if (result.equals("Invaild Slot No")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Enter valid To Slot");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Data server connection failed.");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "No data found.");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            } else {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Unable to Update");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            }

            dialog.cancel();
        }

    }
    class GetSlotNo extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetSlotNo() {

            dialog = new ProgressDialog(MoveManuallyActivity.this);
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
                companyArrayList = mDbHelper.getCompanyList();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_SLOTNO);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Function argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                    info = new PropertyInfo();
                    info.setName("pCompany"); // .Net Function argument key
                    info.setValue(companyArrayList.get(0).getCompanyID());
                    info.setType(String.class);
                    request.addProperty(info);
                }else{
                    mToastMessage.showToast(MoveManuallyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MMToSlotActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Function argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Function argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Function argument key
                info.setValue(Globals.mmTlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Function argument key
                info.setValue(Globals.mmTlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Function argument key
                info.setValue(strToSlot);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_SLOTNO;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "TranSlotno" + ".xml");
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
                    result = "Invaild Slot No";

                } else if (resultString.toString().equalsIgnoreCase("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().contains("<Result>True</Result>")) {
                    result = "success";
                } else {
                    result = "Invalid";
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
                //new LoadWlotData().execute();
              //  edttoSlot.setEnabled(false);
               // edttoSlot.setEnabled(false);
                //edtToQty.setEnabled(true);
                btnSave.setEnabled(true);
                //edtToQty.requestFocus();
                Globals.TOSLOT = strToSlot;
                edttoSlot.clearFocus();
                btnSave.requestFocus();

            } else if (result.equals("Invaild Slot No")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Enter valid To Slot");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Data server connection failed.");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "No data found.");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            } else {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Unable to Update");
                edttoSlot.requestFocus();
                edttoSlot.setText("");
            }

            dialog.cancel();
        }

    }

    class GetWlotNo extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetWlotNo() {

            dialog = new ProgressDialog(MoveManuallyActivity.this);
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
                companyArrayList = mDbHelper.getCompanyList();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();

                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_WLOTNO);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Function argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pCompany"); // .Net Function argument key
                info.setValue(companyArrayList.get(0).getCompanyID());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(MoveManuallyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MoveManuallyActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Function argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Function argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Function argument key
                info.setValue(strWlotNo);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_WLOTNO;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "wlotno" + ".xml");
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
                    result = "Invalid Lot";

                } else if (resultString.toString().equalsIgnoreCase("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().equalsIgnoreCase("No Data Found.")) {
                    result = "No data found in server";
                } else if(resultString.toString().contains("<Result>true</Result>")) {
                    result = "success";
                } else if(resultString.toString().contains("Quantity is too short ")){
                    result = "Qty not available";
                }else {

                    //Get slot
                    String a = resultString.toString();
                    String[] aa = a.split("<tqty>");

                    String aaa = aa[1];

                    String[] aaaa = aaa.split("</tqty>");

                    String bb = aaaa[0];

                    //Get slot
                    String[] aa1 = a.split("<slot>");

                    String aaa1 = aa1[1];

                    String[] aaaa1 = aaa1.split("</slot>");

                     Validation_Slot = aaaa1[0];

                   //Get rpAlloc
                    String RP = resultString.toString();
                    String[] rpA = RP.split("<rpallocqty>");

                    String rpAl = rpA[1];

                    String[] rpAll = rpAl.split("</rpallocqty>");

                    String rpAllo = rpAll[0];


                    if (Double.valueOf(bb)<=0){
                        result = "Qty 0 on pallet";
                    }/*else if (Double.valueOf(rpAllo)!=0){
                        result = "rpallocqty 0 on pallet";
                    }*/else {
                        result = "Success";
                    }


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

            if (result.equals("Success")) {

                new LoadWlotData().execute();

            } else if (result.equals("Invalid Lot No")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Invalid Pallet No");
                edtWlotNo.requestFocus();
                edtWlotNo.setText("");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Data server connection failed.");
                edtWlotNo.requestFocus();
                edtWlotNo.setText("");
            } else if (result.equals("No data found in server")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Invalid Pallet No");
                edtWlotNo.requestFocus();
                edtWlotNo.setText("");
            }else if (result.equals("Qty 0 on pallet")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Qty 0 on Pallet: "+strWlotNo.trim()+" in Slot: "+Validation_Slot.trim()+".Cannot move");
                edtWlotNo.requestFocus();
                edtWlotNo.setText("");
            }/*else if (result.equals("rpallocqty 0 on pallet")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Pallet: "+strWlotNo.trim()+" qty has been allocated.\nCannot move.");

                edtWlotNo.requestFocus();
                edtWlotNo.setText("");
            } */else {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Invalid Pallet No");
                edtWlotNo.requestFocus();
                edtWlotNo.setText("");
            }

            dialog.cancel();
        }

    }

    private class LoadWlotData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadWlotData() {
            dialog = new ProgressDialog(MoveManuallyActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MoveManuallyActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                /*mDbHelper.openWritableDatabase();
                mDbHelper.deleteLoadPickPalletLookupData();
                mDbHelper.closeDatabase();*/
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        String serCompName = compList.get(c);

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "MoveManuallyLot";

                            if ((c > 0) && (fileName.equals("MoveManuallyLot"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "wlotno" + ".xml");
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


                mDbHelper.openReadableDatabase();
                moveManuallyArrayList = mDbHelper.getMoveManuallyDetails(strWlotNo);
                mDbHelper.closeDatabase();

                if(moveManuallyArrayList.size() == 1){
                    edtSlotNo.setText(moveManuallyArrayList.get(0).getMmSlot());
                    edtMMW_WlotNo.setText(moveManuallyArrayList.get(0).getMmWlotno());
                    edtQty.setText(String.valueOf(Math.round(moveManuallyArrayList.get(0).getMmQty())));
                    strSlotNo = moveManuallyArrayList.get(0).getMmSlot();
                    Globals.mmTlot = moveManuallyArrayList.get(0).getMmWlotno();
                    Globals.mmTLotRefid = moveManuallyArrayList.get(0).getMmLotrefid();
                    mDbHelper.openReadableDatabase();
                    mSpinnerList = mDbHelper.getMMUOMList(strWlotNo,strSlotNo);
                    mDbHelper.closeDatabase();

                    adapter = new ArrayAdapter<String>(MoveManuallyActivity.this, android.R.layout.simple_spinner_item, mSpinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnUOM.setAdapter(adapter);

                    edtSlotNo.setEnabled(false);
                    edtWlotNo.setEnabled(false);
                    edtQty.setEnabled(false);
                    edtMMW_WlotNo.setEnabled(false);
                    spnUOM.setEnabled(false);
                    //edtToQty.setEnabled(false);
                    edttoSlot.setEnabled(true);
                    edttoSlot.requestFocus();
                    btnCancel.setEnabled(true);
                    btnSave.setEnabled(true);

                }else{

                    LayoutInflater li = LayoutInflater.from(MoveManuallyActivity.this);
                    View promptsView = li.inflate(R.layout.activity_mm_listitem, null);

                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            MoveManuallyActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    final AlertDialog edtlinealert = alertDialogBuilder.create();
                    edtlinealert.show();
                    edtlinealert.setCancelable(false);
                    moveList = (ListView) promptsView.findViewById(R.id.lst_mmItems);
                    btnGridCancel = (Button) promptsView.findViewById(R.id.btn_gridCancel);

                    mpmAdapter = new MoveManuallyAdapter(MoveManuallyActivity.this,moveManuallyArrayList);
                    moveList.setAdapter(mpmAdapter);

                    moveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            MoveManually moveManually = (MoveManually) mpmAdapter.getItem(position);
                            strWlotNo = moveManually.getMmLotrefid();
                            strSlotNo = moveManually.getMmSlot();
                            Globals.mmTlot = moveManually.getMmWlotno();
                            Globals.mmTLotRefid = moveManually.getMmLotrefid();
                            double Qty =  moveManually.getMmQty();
                            double rpAllocQty =  Double.valueOf(moveManually.getMmrpAlloc());

                            if (Qty <= 0) {
                                mToastMessage.showToast(MoveManuallyActivity.this,
                                        "Qty 0 on Pallet: "+strWlotNo.trim()+" in Slot: "+strSlotNo.trim()+".Cannot move");

                            }/*else if (rpAllocQty!=0){
                                mToastMessage.showToast(MoveManuallyActivity.this,
                                        "Pallet: "+strWlotNo.trim()+" qty has been allocated.\nCannot move.");
                            }*/else {

                                edtSlotNo.setText(strSlotNo);
                                edtMMW_WlotNo.setText(Globals.mmTlot);
                                edtQty.setText(String.valueOf(Math.round(Qty)));

                                mDbHelper.openReadableDatabase();
                                mSpinnerList = mDbHelper.getMMUOMList(strWlotNo, strSlotNo);
                                mDbHelper.closeDatabase();

                                adapter = new ArrayAdapter<String>(MoveManuallyActivity.this, android.R.layout.simple_spinner_item, mSpinnerList);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnUOM.setAdapter(adapter);

                                edtSlotNo.setEnabled(false);
                                edtWlotNo.setEnabled(false);
                                edtQty.setEnabled(false);
                                edtMMW_WlotNo.setEnabled(false);
                                spnUOM.setEnabled(false);
                                //edtToQty.setEnabled(false);
                                edttoSlot.setEnabled(true);
                                edttoSlot.requestFocus();
                                btnCancel.setEnabled(true);
                                btnSave.setEnabled(true);

                                edtlinealert.dismiss();

                            }
                            }
                    });

                    btnGridCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDbHelper.openWritableDatabase();
                            mDbHelper.deletemoveManually();
                            mDbHelper.closeDatabase();
                            edtWlotNo.setText("");
                            //mSupporter.simpleNavigateTo(MoveManuallyActivity.class);
                            edtlinealert.dismiss();

                        }
                    });
                    }


            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MoveManuallyActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MoveManuallyActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MoveManuallyActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MoveManuallyActivity.this, "Error");
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
        private String wLotNum="";

        public PostLotData() {

            dialog = new ProgressDialog(MoveManuallyActivity.this);
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

            /*try {
                mDbHelper.openReadableDatabase();
                mSessionId = mDbHelper.mGetSessionId();
                //SuserDataList = mDbHelper.mGetUserData();
                wLotNum = mDbHelper.mmgetStrLotNum(strWlotNo);
                companyArrayList = mDbHelper.getCompanyList();
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_POST_LOTSLOTDATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Function argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pCompany"); // .Net Function argument key
                info.setValue(companyArrayList.get(0).getCompanyID());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(MoveManuallyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MoveManuallyActivity)");
                }

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Function argument key
                info.setValue(Globals.gUsercode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid"); // .Net Function argument key
                info.setValue(Globals.gLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Function argument key
                info.setValue(strWlotNo.trim());
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Function argument key
                info.setValue(wLotNum);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Function argument key
                info.setValue(strSlotNo);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUmeasur"); // .Net Function argument key
                info.setValue(selectedUOM);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pTqty"); // .Net Function argument key
                info.setValue(String.valueOf(enteredQty));
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_POST_LOTSLOTDATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "postlotno" + ".xml");
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

                } else if (resultString.toString().equalsIgnoreCase("Data server connection failed.")) {
                    result = "Data server connection failed";
                } else if (resultString.toString().equalsIgnoreCase("No data found.")) {
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
            }*/
            result = "success";
            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub

            if (result.equals("success")) {

                mDbHelper.openReadableDatabase();
                moveManuallyArrayList = mDbHelper.getMoveManually(strWlotNo, strSlotNo, selectedUOM);
                mDbHelper.closeDatabase();

                if (moveManuallyArrayList.size() > 0) {//SCS CIRK 2022/07/25 CT69649C:

                    moveManuallyTransaction = new MoveManuallyTransaction();

                    moveManuallyTransaction.setMmTranWlotno(moveManuallyArrayList.get(0).getMmWlotno());
                    moveManuallyTransaction.setMmTranItem(moveManuallyArrayList.get(0).getMmItem());
                    moveManuallyTransaction.setMmTranSlot(moveManuallyArrayList.get(0).getMmSlot());
                    moveManuallyTransaction.setMmTranLoctid(moveManuallyArrayList.get(0).getMmLoctid());
                    moveManuallyTransaction.setMmTranUOM(moveManuallyArrayList.get(0).getMmUOM());
                    moveManuallyTransaction.setMmTranQty(moveManuallyArrayList.get(0).getMmQty());
                    moveManuallyTransaction.setMmTranTrkqty(moveManuallyArrayList.get(0).getMmTrkqty());
                    moveManuallyTransaction.setMmTranItmDesc(moveManuallyArrayList.get(0).getMmItemDesc());
                    moveManuallyTransaction.setMmTranCatchwt(moveManuallyArrayList.get(0).getMmCatchwt());
                    moveManuallyTransaction.setMmTranEqty(String.valueOf(enteredQty));
                    moveManuallyTransaction.setMmTranLotrefid(moveManuallyArrayList.get(0).getMmLotrefid());

                    mDbHelper.openWritableDatabase();
                    Boolean isDataAvailable = mDbHelper.isDataAvailableForMoveManually(strWlotNo, strSlotNo, selectedUOM);

                    if (isDataAvailable == true) {
                        String strAvailQty = mDbHelper.getMmtQty(strWlotNo, strSlotNo, selectedUOM);
                        double qty = Double.parseDouble(strAvailQty) + enteredQty;
                        mDbHelper.UpdateMMTranQty(strWlotNo.trim(), strSlotNo, selectedUOM, String.valueOf(qty));
                    } else {
                        mDbHelper.addMoveManuallyTran(moveManuallyTransaction);
                    }
                    mDbHelper.closeDatabase();

                   // mSupporter.simpleNavigateTo(MPMList.class);
                 }else{
                    mToastMessage.showToast(MoveManuallyActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in moveManuallyArrayList(MoveManuallyActivity)");
                 }

                mDbHelper.openReadableDatabase();
                mmTran = mDbHelper.getMmTran();
                mDbHelper.closeDatabase();


                Globals.mmTLotRefid = mmTran.get(0).getMmTranLotrefid();
                Globals.mmTSlot = mmTran.get(0).getMmTranSlot();
                Globals.mmTUOM = mmTran.get(0).getMmTranUOM();
                Globals.mmTItem = mmTran.get(0).getMmTranItem();
                Globals.mmTlot = mmTran.get(0).getMmTranWlotno();

                mDbHelper.openReadableDatabase();
                String strAvailQty = mDbHelper.mgetMMTranQty(Globals.mmTLotRefid,Globals.mmTSlot,Globals.mmTUOM);
                mDbHelper.closeDatabase();
                availQty = Double.parseDouble(strAvailQty);
                strEnteredQty = edtQty.getText().toString().trim();
               // strEnteredQty = edtToQty.getText().toString().trim();

                if(strEnteredQty.equalsIgnoreCase(null)||strEnteredQty.equalsIgnoreCase("")){
                    mToastMessage.showToast(MoveManuallyActivity.this,
                            "Please enter the Qty");
                }else {
                    enteredQty = Integer.parseInt(strEnteredQty);
                    if(enteredQty<=0){
                        mToastMessage.showToast(MoveManuallyActivity.this,
                                "Please enter the valid Qty");
                    }else{

                        remainingQty = availQty-enteredQty;
                        new PostLotDataFinal().execute();

                    }
                }

            }

            dialog.cancel();
        }

    }

    @Override
    public void onBackPressed() {

        mDbHelper.openWritableDatabase();
        mDbHelper.deletemoveManually();
        mDbHelper.closeDatabase();
        mSupporter.simpleNavigateTo(MainmenuActivity.class);
       // BackPressScancelAlert();

    }


    public void BackPressScancelAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDbHelper.openWritableDatabase();
                        mDbHelper.deletemoveManually();
                        mDbHelper.closeDatabase();
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
                        mDbHelper.openWritableDatabase();
                        mDbHelper.deletemoveManually();
                        mDbHelper.closeDatabase();
                        mSupporter.simpleNavigateTo(MoveManuallyActivity.class);
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

    public void alertUser() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MoveManuallyActivity.this);
        alertDialog.setTitle("Success");
        alertDialog.setIcon(R.drawable.tick);
        alertDialog.setCancelable(false);
        String s1 = "";
        String s2 = "Moved successfully.";
        Spanned strMessage = Html.fromHtml(s1+  "<br>" + s2);
        alertDialog.setMessage(strMessage);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        // supporter.simpleNavigateTo(MainMenu.class);
                        btnCancel.setEnabled(false);
                        btnSave.setEnabled(false);
                        dialog.dismiss();

                    }
                });

        //alertDialog.setMessage("All items have been moved.");
        alertDialog.show();


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


            dialog = new ProgressDialog(MoveManuallyActivity.this);
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


            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(MoveManuallyActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
     //   new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }


}
