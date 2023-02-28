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
import android.widget.Button;
import android.widget.EditText;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.MoveManuallyTransaction;
import com.silvercreek.wmspickingclient.model.company;
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
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class MMToSlotActivity extends AppBaseActivity{
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_GET_SLOTNO = "MoveManually_LookupDataTo";
    public static final String METHOD_POST_SLOTNO = "MoveManually_SaveTo";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private Button btnSave,btnCancel;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private String mSessionId;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String  mCompany,mUsername,mDeviceId;
    private EditText edtToSlot,edtQty;
    private String strToSlot,strEnteredQty;
    private ArrayList<company> companyArrayList;
    private double availQty=0;
    private double enteredQty=0;
    private double remainingQty=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mm_to_slot);

        btnSave = findViewById(R.id.btnMMSave);
        btnCancel = findViewById(R.id.btnMMCancel);
        edtToSlot = findViewById(R.id.edtToSLot);
        edtQty = findViewById(R.id.edtMMQty);
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
        NAMESPACE = NAMESPACE +"/";
        Globals.gNamespace=NAMESPACE;
        Globals.gProtocol=URL_PROTOCOL;
        Globals.gServicename=URL_SERVICE_NAME;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");

        edtQty.setEnabled(false);
        btnSave.setEnabled(false);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                String strAvailQty = mDbHelper.mgetMMTranQty(Globals.mmTLotRefid,Globals.mmTSlot,Globals.mmTUOM);
                mDbHelper.closeDatabase();
                availQty = Double.parseDouble(strAvailQty);
                strEnteredQty = edtQty.getText().toString().trim();

                if(strEnteredQty.equalsIgnoreCase(null)||strEnteredQty.equalsIgnoreCase("")){
                    mToastMessage.showToast(MMToSlotActivity.this,
                            "Please enter the Qty");
                }else {
                    enteredQty = Integer.parseInt(strEnteredQty);
                    if(enteredQty>availQty||enteredQty<=0){
                        mToastMessage.showToast(MMToSlotActivity.this,
                                "Please enter the valid Qty");
                    }else{

                        remainingQty = availQty-enteredQty;
                        new PostLotData().execute();

                    }
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelAlert();
            }
        });

        edtToSlot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            strToSlot = edtToSlot.getText().toString();
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

    class GetSlotNo extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetSlotNo() {

            dialog = new ProgressDialog(MMToSlotActivity.this);
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
                info.setName("pSessionId"); // .Net Funcation argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pCompany"); // .Net Funcation argument key
                info.setValue(companyArrayList.get(0).getCompanyID());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(MMToSlotActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MMToSlotActivity)");
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
                info.setValue(Globals.mmTlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLotrefid"); // .Net Funcation argument key
                info.setValue(Globals.mmTlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Funcation argument key
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
                edtQty.setEnabled(true);
                btnSave.setEnabled(true);
                edtQty.requestFocus();

            } else if (result.equals("Invaild Slot No")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Invaild Slot No");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Data server connection failed.");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "No data found.");
            } else {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Unable to Update");
            }

            dialog.cancel();
        }

    }

    class PostLotData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public PostLotData() {

            dialog = new ProgressDialog(MMToSlotActivity.this);
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
                info.setName("pSessionId"); // .Net Funcation argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                if(companyArrayList.size()>0){      //SCS CIRK 2022/07/25 CT69649C:
                info = new PropertyInfo();
                info.setName("pCompany"); // .Net Funcation argument key
                info.setValue(companyArrayList.get(0).getCompanyID());
                info.setType(String.class);
                request.addProperty(info);
                }else{
                    mToastMessage.showToast(MMToSlotActivity.this, "No Data Available");
                    LogfileCreator.mAppendLog("No data available in companyArrayList(MMToSlotActivity)");
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
                info.setValue(Globals.mmTLotRefid = fixedLengthString(Globals.mmTLotRefid));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pWLotno"); // .Net Funcation argument key
                info.setValue(Globals.mmTlot = fixedLengthString(Globals.mmTlot));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pSlot"); // .Net Funcation argument key
                info.setValue(strToSlot);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUmeasur"); // .Net Funcation argument key
                info.setValue(Globals.mmTUOM);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pTqty"); // .Net Funcation argument key
                info.setValue(String.valueOf(enteredQty));
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pItem"); // .Net Funcation argument key
                info.setValue(Globals.mmTItem);
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
                mDbHelper.closeDatabase();

                mSupporter.simpleNavigateTo(MPMList.class);

            } else if (result.equals("Quantity is too short")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Qty not available");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Data server connection failed.");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "No data found.");
            } else {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Unable to Update");
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
                        mSupporter.simpleNavigateTo(MPMList.class);
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


            dialog = new ProgressDialog(MMToSlotActivity.this);
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
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(MMToSlotActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(MMToSlotActivity.this,
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
