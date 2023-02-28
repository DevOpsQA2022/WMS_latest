package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.ResetItemDetails;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportPickTask;
import com.silvercreek.wmspickingclient.xml.ExportResetPalletDeatails;

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

public class ResetPalletAllocationActivity extends AppCompatActivity {
    private Button btn_done,btn_cancel;

    private Button alt_btnCncl,alt_btnOver;
    private Button btn_resetSotranAloc,btn_resetRepackAloc,btn_resetsiteAloc,btn_updPalletQty;
    private RelativeLayout badge_task11;
    private EditText edtPallet;
    private EditText edtlin_qty;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private List<ResetItemDetails> exportTranList;
    private ToastMessage mToastMessage;
    private File mImpOutputFile;
    private int mTimeout;
    private String mLoctid = "";
    private String mPallet = "";
    private String Getmsg = "";
    private String mResetPalletTag = "";
    private String mSessionId ="", mCompany ="", mUsername ="", mDeviceId = "";
    private SharedPreferences sharedpreferences;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_DATAUTILS_RESETPALLETALLOC_SCANPALLET = "DataUtils_ResetPalletAlloc_ScanPallet";
    public static final String METHOD_EXPORT_DATA = "DataUtils_ResetPalletAlloc_Save";
    public static final String METHOD_EXPORT_UPDATE_PALLET_DATA = "DataUtils_UpdatePalletQty_Save";
    private List<ResetItemDetails> mResetItemDetails;
    private TextView tvItem,tvPalletQty,tvLoact,tvSotranAlloc,tvRepackAlloc,tvTepmAlloc;
    private TextView txtLin_Oqty,txtLin_Pallets;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pallet_allocation);
        btn_done = findViewById(R.id.btn_done);
        btn_resetSotranAloc = findViewById(R.id.btn_resetSotranAloc);
        btn_resetRepackAloc = findViewById(R.id.btn_resetRepackAloc);
        btn_updPalletQty = findViewById(R.id.btn_updPltQty);
       // btn_resetsiteAloc = findViewById(R.id.btn_resetsiteAloc);
        btn_cancel = findViewById(R.id.btn_cancel);
        edtPallet = findViewById(R.id.edtPallet);
        tvItem = findViewById(R.id.tv_RP_Item);
        tvLoact = findViewById(R.id.tvLoact);
        tvPalletQty = findViewById(R.id.tvPalletQty);
        tvSotranAlloc = findViewById(R.id.tvSotranAlloc);
        tvRepackAlloc = findViewById(R.id.tvRepackAlloc);
      //  tvSiteAlloc = findViewById(R.id.tvSiteAlloc);
        tvTepmAlloc = findViewById(R.id.tvTepmAlloc);



        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;

        mDbHelper.openReadableDatabase();
        mResetPalletTag = mDbHelper.mGetResetPalletTag();
        mDbHelper.closeDatabase();

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
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;



        edtPallet.requestFocus();
        if (edtPallet.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtPallet.setShowSoftInputOnFocus(false);
        } else {
            edtPallet.setShowSoftInputOnFocus(true);
        }


        if (mResetPalletTag.equals("Y")){
            btn_updPalletQty.setVisibility(View.VISIBLE);
        }else {
            btn_updPalletQty.setVisibility(View.GONE);
        }



        edtPallet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            mDbHelper.getWritableDatabase();
                            mDbHelper.DeletePickTaskScanPallet();
                            mDbHelper.closeDatabase();

                             mPallet = edtPallet.getText().toString().trim();

                            if (mPallet.equalsIgnoreCase("")) {
                                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                                        "Please Enter the Pallet.");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtPallet.requestFocus();
                                    }
                                }, 150);

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


        btn_updPalletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                mDbHelper.closeDatabase();


                if (mResetItemDetails.size() <= 0) {
                    mToastMessage.showToast(ResetPalletAllocationActivity.this,
                            "No scanned pallet.");
                }else {



                    final Dialog dialogTap = new Dialog(ResetPalletAllocationActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                    dialogTap.setCancelable(false);
                    dialogTap.setContentView(R.layout.edtalert_palupdate);
                    /*dialogTap.getWindow().setBackgroundDrawableResource(android.R.color.transparent);*/

                    alt_btnCncl = dialogTap.findViewById(R.id.edtCancel_btn);
                    alt_btnOver = (Button) dialogTap.findViewById(R.id.edtOver_btn);


                    edtlin_qty = (EditText) dialogTap.findViewById(R.id.edtlin_qty);

                    txtLin_Pallets = (TextView) dialogTap.findViewById(R.id.edtlin_pallet);
                    txtLin_Oqty = (TextView) dialogTap.findViewById(R.id.edtlin_oldQty);

                    edtlin_qty.setHighlightColor(ContextCompat.getColor(ResetPalletAllocationActivity.this, R.color.colorCursor));

                    if (SOFT_KEYBOARD.equals("CHECKED")) {
                        edtlin_qty.setShowSoftInputOnFocus(false);
                    } else {
                        edtlin_qty.setShowSoftInputOnFocus(true);
                    }
                    mPallet = edtPallet.getText().toString().trim();

                    mDbHelper.openReadableDatabase();
                    mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                    mDbHelper.closeDatabase();

                    String item = mResetItemDetails.get(0).getItem();
                    String palltQty = String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getQty())));
                    String loctId = mResetItemDetails.get(0).getLoctid();
                    String sotronAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPsoaloc())));
                    String repackAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPrpaloc())));
                    String siteAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPtfrout())));
                    String tempAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPtmaloc())));

                    txtLin_Pallets.setText(mPallet);
                    txtLin_Oqty.setText(palltQty);
                    edtlin_qty.setText("");
                    edtlin_qty.requestFocus();

                    dialogTap.show();

                    alt_btnOver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String mQty = edtlin_qty.getText().toString();
                            if(mQty != null){
                                if (!mQty.equals("")){

                                    String edtQtPal = edtlin_qty.getText().toString();
                                    ExportData("UPDATE_PALLET",edtQtPal);
                                    dialogTap.dismiss();

                                }else {
                                    mToastMessage.showToast(ResetPalletAllocationActivity.this,"Please enter the qty.");
                                }
                            }
                        }
                    });



                    alt_btnCncl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            edtPallet.setText("");
                            tvItem.setText("");
                            tvPalletQty.setText("");
                            tvLoact.setText("");
                            tvSotranAlloc.setText("");
                            tvRepackAlloc.setText("");
                            tvTepmAlloc.setText("");
                            edtPallet.setEnabled(true);
                            edtPallet.requestFocus();

                            mDbHelper.openWritableDatabase();
                            mDbHelper.deleteResetPalletAlloc();
                            mDbHelper.closeDatabase();

                            dialogTap.dismiss();
                        }
                    });
                }
            }
        });



/*
        btn_updPalletQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                mDbHelper.closeDatabase();


                if (mResetItemDetails.size() <= 0) {
                    mToastMessage.showToast(ResetPalletAllocationActivity.this,
                            "No scanned pallet.");
                }else {


                    LayoutInflater li = LayoutInflater.from(ResetPalletAllocationActivity.this);
                    View promptsView = li.inflate(R.layout.edtalert_palupdate, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            ResetPalletAllocationActivity.this);
                    alertDialogBuilder.setView(promptsView);
                    final AlertDialog edtlinealert = alertDialogBuilder.create();
                    edtlinealert.setCancelable(false);




                    alt_btnCncl = (Button) promptsView.findViewById(R.id.edtCancel_btn);
                    alt_btnOver = (Button) promptsView.findViewById(R.id.edtOver_btn);


                    edtlin_qty = (EditText) promptsView.findViewById(R.id.edtlin_qty);

                    txtLin_Pallets = (TextView) promptsView.findViewById(R.id.edtlin_pallet);
                    txtLin_Oqty = (TextView) promptsView.findViewById(R.id.edtlin_oldQty);

                    if (SOFT_KEYBOARD.equals("CHECKED")) {
                        edtlin_qty.setShowSoftInputOnFocus(false);
                    } else {
                        edtlin_qty.setShowSoftInputOnFocus(true);
                    }
                    mPallet = edtPallet.getText().toString().trim();

                    mDbHelper.openReadableDatabase();
                    mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                    mDbHelper.closeDatabase();

                    String item = mResetItemDetails.get(0).getItem();
                    String palltQty = String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getQty())));
                    String loctId = mResetItemDetails.get(0).getLoctid();
                    String sotronAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPsoaloc())));
                    String repackAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPrpaloc())));
                    String siteAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPtfrout())));
                    String tempAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPtmaloc())));

                    txtLin_Pallets.setText(mPallet);
                    txtLin_Oqty.setText(palltQty);
                    edtlin_qty.setText(palltQty);
                    edtlin_qty.selectAll();
                    edtlin_qty.requestFocus();

                  //  edtlinealert.show();
                    edtlinealert.show();

                    alt_btnOver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String mQty = edtlin_qty.getText().toString();
                            if(mQty != null){
                                if (!mQty.equals("")){

                                    String edtQtPal = edtlin_qty.getText().toString();
                                    ExportData("UPDATE_PALLET",edtQtPal);
                                    edtlinealert.dismiss();

                                }else {
                                    mToastMessage.showToast(ResetPalletAllocationActivity.this,"Please enter the qty.");
                              }
                            }
                        }
                    });



                    alt_btnCncl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            edtPallet.setText("");
                            tvItem.setText("");
                            tvPalletQty.setText("");
                            tvLoact.setText("");
                            tvSotranAlloc.setText("");
                            tvRepackAlloc.setText("");
                            tvTepmAlloc.setText("");
                            edtPallet.setEnabled(true);
                            edtPallet.requestFocus();

                            mDbHelper.openWritableDatabase();
                            mDbHelper.deleteResetPalletAlloc();
                            mDbHelper.closeDatabase();

                            edtlinealert.dismiss();
                        }
                    });
                }
            }
        });
*/

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtPallet.setText("");
                tvItem.setText("");
                tvPalletQty.setText("");
                tvLoact.setText("");
                tvSotranAlloc.setText("");
                tvRepackAlloc.setText("");
                //tvSiteAlloc.setText("");
                tvTepmAlloc.setText("");
                edtPallet.requestFocus();
                edtPallet.setEnabled(true);

             mDbHelper.openWritableDatabase();
             mDbHelper.deleteResetPalletAlloc();
             mDbHelper.closeDatabase();

             mSupporter.simpleNavigateTo(MainmenuActivity.class);
            // mSupporter.simpleNavigateTo(DataUtilitiesActivity.class);
            }
        });

        btn_resetSotranAloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mSotrsn = tvSotranAlloc.getText().toString();
                String mTempAlloc = tvTepmAlloc.getText().toString();


                mDbHelper.openReadableDatabase();
                mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                mDbHelper.closeDatabase();



                if (mResetItemDetails.size() <= 0) {
                    mToastMessage.showToast(ResetPalletAllocationActivity.this,
                            "No scanned pallet.");
                }else if (mSotrsn.equals("0") && mTempAlloc.equals("0")){
                    PalletAllcoNotFound("SO.");
                }else {
                    //int mOpenSoCount = Integer.parseInt(mResetItemDetails.get(0).getOpensocount().toString());
                    OpenSoAlert();
                    //ExportData("SOTRAN","");
                }
            }
        });

        btn_resetRepackAloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mRepack = tvRepackAlloc.getText().toString();
                String mTempAlloc = tvTepmAlloc.getText().toString();

                mDbHelper.openReadableDatabase();
                mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                mDbHelper.closeDatabase();

                if (mResetItemDetails.size() <= 0) {
                    mToastMessage.showToast(ResetPalletAllocationActivity.this,
                            "No scanned pallet.");
                }else if (mRepack.equals("0") && mTempAlloc.equals("0")){
                    PalletAllcoNotFound("Repack.");
                }else {
//                    int mOpenRpackCount = 0;
                    //int mOpenRpackCount = Integer.parseInt(mResetItemDetails.get(0).getOpenrepackcount().toString());
                    OpenRepackAlert();
                    //ExportData("REPACK","");
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             edtPallet.setText("");
             tvItem.setText("");
             tvPalletQty.setText("");
             tvLoact.setText("");
             tvSotranAlloc.setText("");
             tvRepackAlloc.setText("");
            // tvSiteAlloc.setText("");
             tvTepmAlloc.setText("");
             edtPallet.setEnabled(true);
             edtPallet.requestFocus();

             mDbHelper.openWritableDatabase();
             mDbHelper.deleteResetPalletAlloc();
             mDbHelper.closeDatabase();

            }
        });
    }

    @Override
    public void onBackPressed() {
        mSupporter.simpleNavigateTo(DataUtilitiesActivity.class);
    }

    class GetPickTaskScanPallet extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetPickTaskScanPallet(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(ResetPalletAllocationActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_DATAUTILS_RESETPALLETALLOC_SCANPALLET);
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
                info.setValue(mPallet);
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
                String soap_action = NAMESPACE + METHOD_DATAUTILS_RESETPALLETALLOC_SCANPALLET;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "ResetPalletAlloc_ScanPallet" + ".xml");
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
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Invalid Pallet");
                edtPallet.setText("");
                edtPallet.requestFocus();

            } else if (result.equals("Assinged another user")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        Getmsg);
                edtPallet.setText("");
                edtPallet.requestFocus();
            } else if (result.equals("  Completed.")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Repack Completed. Unable to Proceed");
                edtPallet.setText("");
                edtPallet.requestFocus();

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
                mToastMessage.showToast(ResetPalletAllocationActivity.this, result);
                edtPallet.setText("");
                edtPallet.requestFocus();
            }
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

    private class LoadPickTask_ScanPallet extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;


        public LoadPickTask_ScanPallet() {
            dialog = new ProgressDialog(ResetPalletAllocationActivity.this);

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

                DataLoader fileLoader = new DataLoader(ResetPalletAllocationActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteResetPalletAlloc();
                mDbHelper.closeDatabase();

                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "ResetPalletAlloc_ScanPallet" + ".xml");
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

                mDbHelper.openReadableDatabase();
                mResetItemDetails = mDbHelper.getResetPalletAllocDetails(mPallet);
                mDbHelper.closeDatabase();

                if (mResetItemDetails.size() <=0){
                    mToastMessage.showToast(ResetPalletAllocationActivity.this, "No Data available");
                    edtPallet.setEnabled(true);
                    edtPallet.setText("");
                    edtPallet.requestFocus();
                }else {
                    String item = mResetItemDetails.get(0).getItem();
                    String palltQty = String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getQty())));
                    String loctId = mResetItemDetails.get(0).getLoctid();
                    String sotronAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPsoaloc())));
                    String repackAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPrpaloc())));
                    String siteAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPtfrout())));
                    String tempAlloc =String.valueOf(Math.round(Double.parseDouble(mResetItemDetails.get(0).getPtmaloc())));

                    tvItem.setText(item);
                    tvPalletQty.setText(palltQty);
                    tvLoact.setText(loctId);
                    tvSotranAlloc.setText(sotronAlloc);
                    tvRepackAlloc.setText(repackAlloc);
                    //tvSiteAlloc.setText(siteAlloc);
                    tvTepmAlloc.setText(tempAlloc);
                    btn_resetSotranAloc.requestFocus();

                }


            } else if (result.equals("nosd")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(ResetPalletAllocationActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(ResetPalletAllocationActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(ResetPalletAllocationActivity.this, "File not available");
            } else {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(ResetPalletAllocationActivity.this, "Error");
            }
        }
    }

    public void ExportData(String SOorRepack,String edtPalQty) {
        exportTranList = new ArrayList<ResetItemDetails>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getResetPalletAllocDetails(mPallet);
        mDbHelper.closeDatabase();

        if (exportTranList.size() != 0) {
            String exportXml = getRecordXmlExportPO(exportTranList,SOorRepack,edtPalQty);
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
                if (SOorRepack.equals("UPDATE_PALLET")){
                    new ExportTranDataForUpdatePal(mSessionId, Globals.gUsercode, Globals.gCompanyId,SOorRepack).execute();
                }else {
                    new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId,SOorRepack).execute();
                }

            } else {
                ExportError();
            }
        } else {
            mToastMessage.showToast(ResetPalletAllocationActivity.this,
                    "Unable to update Server");
        }
    }
    public String getRecordXmlExportPO(List<ResetItemDetails> dList,String SOorRepack,String edtPalQty) {
        String exportPODataXml = "";
        try {
            ExportResetPalletDeatails exportData = new ExportResetPalletDeatails();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "ResetItem" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, ResetPalletAllocationActivity.this, mDbHelper,SOorRepack,edtPalQty);
            }
            sb.append("</" + "ResetItem" + ">");
            exportPODataXml = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            String errorCode = "Err-CLS-2";
            String errMsg = " resetPalletAllocation export failed";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage()
                    + "\n" + errMsg);
            String result = "error";
            return result;
        }
        return exportPODataXml;
    }


    private void endDBTransaction() {
        mDbHelper.mSetTransactionSuccess(); // setting the transaction

        Log.i("Transaction success", "Transaction success.");
        mDbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        mDbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }
    private void startDBTransaction(String action) {
        // transaction is started here..
        mDbHelper.getWritableDatabase();
        Log.i("Writable DB Open", "Writable Database Opened.");
        mDbHelper.mBeginTransaction();
        Log.i("Transaction started", "Transaction successfully started for "
                + action);
    }

    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(ResetPalletAllocationActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "ResetPalletAlloction" + ".xml");
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
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(ResetPalletAllocationActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    class ExportTranData extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId,pSOorRepack;

        public ExportTranData(String Session, String Uname, String Compid,String SOorRepack) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            this.pSOorRepack = SOorRepack;
            dialog = new ProgressDialog(ResetPalletAllocationActivity.this);
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
                        + "/FinalExoprt/ResetPalletAlloction.xml");
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
                info.setName("pProcess");
                info.setValue(pSOorRepack);
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
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "ResetPalletAlloction" + ".xml");
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
                mToastMessage.showToast(ResetPalletAllocationActivity.this,"Reverted pallet allocations.");
                    mSupporter.simpleNavigateTo(ResetPalletAllocationActivity.class);
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId,pSOorRepack).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }
    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(ResetPalletAllocationActivity.this);
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


    private void PalletAllcoNotFound(String RepackOrSo) {
        LayoutInflater li = LayoutInflater.from(ResetPalletAllocationActivity.this);
        View promptsView = li.inflate(R.layout.palletallocationalrt, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ResetPalletAllocationActivity.this);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog moreQtyAlrt = alertDialogBuilder.create();
        moreQtyAlrt.setCancelable(false);

        Window window = moreQtyAlrt.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 100;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView tvOk = (TextView) promptsView.findViewById(R.id.txt_ok);
        TextView tvAlertMsg= (TextView) promptsView.findViewById(R.id.txt_alertMsg);

        tvAlertMsg.setText("No allocation exists in the "+RepackOrSo);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edtPallet.setText("");
                tvItem.setText("");
                tvPalletQty.setText("");
                tvLoact.setText("");
                tvSotranAlloc.setText("");
                tvRepackAlloc.setText("");
                // tvSiteAlloc.setText("");
                tvTepmAlloc.setText("");
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteResetPalletAlloc();
                mDbHelper.closeDatabase();

                moreQtyAlrt.dismiss();
            }
        });
        moreQtyAlrt.show();
    }


    private void OpenRepackAlert() {
        LayoutInflater li = LayoutInflater.from(ResetPalletAllocationActivity.this);
        View promptsView = li.inflate(R.layout.opensoalert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ResetPalletAllocationActivity.this);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog moreQtyAlrt = alertDialogBuilder.create();
        moreQtyAlrt.setCancelable(false);

        Window window = moreQtyAlrt.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 100;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView tvOk = (TextView) promptsView.findViewById(R.id.txt_ok);
        TextView tvCancel= (TextView) promptsView.findViewById(R.id.txt_cancel);
        TextView tvAlertMsg= (TextView) promptsView.findViewById(R.id.txt_alertMsg);
        /*if (mOpenRpackCount<=0){
            tvAlertMsg.setText("OK to zero allocations?");
        }else {
            tvAlertMsg.setText("OK to reset allocations from repacks");
        }*/

        tvAlertMsg.setText("OK to Recalculate allocations from repacks");

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportData("REPACK","");
                moreQtyAlrt.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPallet.setText("");
                tvItem.setText("");
                tvPalletQty.setText("");
                tvLoact.setText("");
                tvSotranAlloc.setText("");
                tvRepackAlloc.setText("");
                // tvSiteAlloc.setText("");
                tvTepmAlloc.setText("");
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteResetPalletAlloc();
                mDbHelper.closeDatabase();

                moreQtyAlrt.dismiss();
            }
        });
        moreQtyAlrt.show();
    }



    private void OpenSoAlert() {
        LayoutInflater li = LayoutInflater.from(ResetPalletAllocationActivity.this);
        View promptsView = li.inflate(R.layout.opensoalert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ResetPalletAllocationActivity.this);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog moreQtyAlrt = alertDialogBuilder.create();
        moreQtyAlrt.setCancelable(false);

        Window window = moreQtyAlrt.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.y = 100;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView tvOk = (TextView) promptsView.findViewById(R.id.txt_ok);
        TextView tvCancel= (TextView) promptsView.findViewById(R.id.txt_cancel);
        TextView tvAlertMsg= (TextView) promptsView.findViewById(R.id.txt_alertMsg);
        tvAlertMsg.setText("OK to Recalculate allocations from open sales orders");
        /*if (mOpenSoCount<=0){
            tvAlertMsg.setText("OK to zero allocations?");
        }else {
            tvAlertMsg.setText("OK to reset allocations from open sales orders");
        }*/

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExportData("SOTRAN","");
                moreQtyAlrt.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPallet.setText("");
                tvItem.setText("");
                tvPalletQty.setText("");
                tvLoact.setText("");
                tvSotranAlloc.setText("");
                tvRepackAlloc.setText("");
                // tvSiteAlloc.setText("");
                tvTepmAlloc.setText("");
                edtPallet.setEnabled(true);
                edtPallet.requestFocus();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteResetPalletAlloc();
                mDbHelper.closeDatabase();

                moreQtyAlrt.dismiss();
            }
        });
        moreQtyAlrt.show();
    }


    class ExportTranDataForUpdatePal extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId,pSOorRepack;

        public ExportTranDataForUpdatePal(String Session, String Uname, String Compid,String SOorRepack) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            this.pSOorRepack = SOorRepack;
            dialog = new ProgressDialog(ResetPalletAllocationActivity.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_UPDATE_PALLET_DATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/ResetPalletAlloction.xml");
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
                String soap_action = NAMESPACE + METHOD_EXPORT_UPDATE_PALLET_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "UpdatePallet" + ".xml");
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


                mToastMessage.showToast(ResetPalletAllocationActivity.this,"Updated pallet Qty.");

                mDbHelper.getWritableDatabase();
                mDbHelper.DeletePickTaskScanPallet();
                mDbHelper.DeletePickTaskScanPallet();
                mDbHelper.closeDatabase();

                mPallet = edtPallet.getText().toString().trim();

                if (mPallet.equalsIgnoreCase("")) {
                    mToastMessage.showToast(ResetPalletAllocationActivity.this,
                            "Please Enter the Pallet.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            edtPallet.requestFocus();
                        }
                    }, 150);

                } else {
                    new GetPickTaskScanPallet(mUsername).execute();
                }
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")) {
              //  new ExportTranDataForUpdatePal(mSessionId, Globals.gUsercode, Globals.gCompanyId,pSOorRepack).execute();
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Time out Unable to update Server");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(ResetPalletAllocationActivity.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }




}