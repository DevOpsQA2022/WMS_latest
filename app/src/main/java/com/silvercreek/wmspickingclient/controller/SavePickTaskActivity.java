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
import android.os.Handler;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskWHMLOT;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.DecimalDigitsInputFilter;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
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

public class SavePickTaskActivity extends AppCompatActivity {

    private EditText edtSLot, edtQty;
    private Button btnSave, btnCancel;
    private TextView tvItem, tvDesc;

    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private List<picktaskdetail> picktaskdetail;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";

    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String ItemNo, strDesc;
    private String strTQty, strorgTQty;
    private String strTrkQty, strorgTrkQty;
    private String strCatchwt, strSlot, strLot, strwLotno;
    private String strTranlineNo;
    private Integer pickDuration;
    private double dAvailQty;
    private String uom = "", doctype="", docno="", doclineno="", docstat="",weight="",stkumid="",
            orgdoclineno="", volume="", decnum="",orgTranlineno="",Lbshp="", umfact = "",Tshipped="",Trkshiped="", LineSplit="";
    private Boolean isSameItem = false;
    private Boolean isProceed = true;
    private String mSlot = "";

    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mUsername,mDeviceId ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_pick_task);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        tvItem = (TextView) findViewById(R.id.tvItem);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        edtSLot = (EditText) findViewById(R.id.edtSlot);
        edtQty = (EditText) findViewById(R.id.edtQty);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

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

        mDbHelper.openReadableDatabase();
        picktaskdetail = mDbHelper.selectPickTaskDetail(Globals.gPickTaskItem);
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;

        if (picktaskdetail.size()>0) {      //SCS CIRK 2022/07/25 CT69649C:
            ItemNo = picktaskdetail.get(0).getItem();
            strDesc = picktaskdetail.get(0).getDescrip();
            strTQty = picktaskdetail.get(0).getTQty();
            strorgTQty = picktaskdetail.get(0).getorgTQty();
            strTrkQty = picktaskdetail.get(0).getTrkQty();
            strorgTrkQty = picktaskdetail.get(0).getorgTrkQty();
            strCatchwt = picktaskdetail.get(0).getCatchwt();
            strSlot = picktaskdetail.get(0).getSlot();
            strwLotno = picktaskdetail.get(0).getWLotNo();
            strLot = picktaskdetail.get(0).getLotNo();
            strTranlineNo = picktaskdetail.get(0).getTranlineno();
            orgTranlineno = picktaskdetail.get(0).getorgTranlineno();
            uom = picktaskdetail.get(0).getUom();
            doctype = picktaskdetail.get(0).getDoctype();
            docno = picktaskdetail.get(0).getDocno();
            doclineno = picktaskdetail.get(0).getDoclineno();
            orgdoclineno = picktaskdetail.get(0).getorgDoclineno();
            docstat = picktaskdetail.get(0).getDocstat();
            weight = picktaskdetail.get(0).getWeight();
            volume = picktaskdetail.get(0).getVolume();
            decnum = picktaskdetail.get(0).getdecnum();
            stkumid = picktaskdetail.get(0).getStkumid();
            umfact = picktaskdetail.get(0).getUmfact();
            Tshipped = picktaskdetail.get(0).getTshipped();
            Trkshiped = picktaskdetail.get(0).getTrkshipped();
            Lbshp = picktaskdetail.get(0).getLbshp();
            LineSplit = picktaskdetail.get(0).getLinesplit();
            tvItem.setText(ItemNo);
            tvDesc.setText(strDesc);
            edtQty.setText("");
            edtSLot.setText("");
        } else{
            mToastMessage.showToast(SavePickTaskActivity.this, "No Data Found");
            LogfileCreator.mAppendLog("No data available in picktaskdetail(SavePickTaskActivity)");
        }

        edtSLot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            mSlot = edtSLot.getText().toString().trim();

                            mDbHelper.openReadableDatabase();
                           final Boolean isValidSlot = mDbHelper.isValidSlot(mSlot);
                            mDbHelper.closeDatabase();
                            if (!isValidSlot) {
                                mToastMessage.showToast(SavePickTaskActivity.this,
                                        "Invalid Slot " + mSlot);
                                edtSLot.setText("");
                                edtSLot.requestFocus();
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (edtSLot != null) {
                                        mSlot = edtSLot.getText().toString().trim();
                                        if (!isValidSlot) {
                                            edtSLot.requestFocus();
                                        }
                                    }
                                }
                            }, 150); // Remove this Delay Handler IF requestFocus(); works just fine without delay
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
                 if(edtQty.getText().toString().equals("") && (edtSLot.getText().toString().equals(""))){
                     mSupporter.simpleNavigateTo(PickTaskActivity.class);
                 } else{
                     cancelAlert();
                 }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Qty = "", Slot = "";
                Double dQty = 0.0;
                isProceed = true;
                Slot = edtSLot.getText().toString();
                Qty = edtQty.getText().toString();
                if (!Qty.equalsIgnoreCase("")) {
                    dQty = Double.parseDouble(Qty);
                } else {
                    dQty = Double.parseDouble("0");
                }
                mDbHelper.openReadableDatabase();
                Boolean isValidSlot = mDbHelper.isValidSlot(Slot);
                mDbHelper.closeDatabase();

                mDbHelper.openReadableDatabase();
                String getTqty = mDbHelper.getTqty(Slot, Globals.gPickTaskWlotno);
                Double getqty = 0.0;
                mDbHelper.closeDatabase();

                if (!getTqty.equals("")) {
                    getqty = Double.valueOf(getTqty);
                }
                if (!isValidSlot) {
                    mToastMessage.showToast(SavePickTaskActivity.this,
                            "Invalid Slot");
                } else {
                    if (getqty < dQty) {
                        isProceed = false;
                        qtyAlert(Slot, Qty, getqty);
                    } else{
                        Saveprocess();
                    }
                }
            }
        });
    }

    private void Saveprocess() {

        String Qty = "", Slot = "", flag = "", item = "", desc = "";
        Double dQty = 0.0;
        isProceed = true;

        item = tvItem.getText().toString();
        desc = tvDesc.getText().toString();
        Slot = edtSLot.getText().toString();
        Qty = edtQty.getText().toString();
        Globals.gDecnum = decnum;

        if (!Qty.equalsIgnoreCase("")) {
            dQty = Double.parseDouble(Qty);
        } else {
            dQty = Double.parseDouble("0");
        }

        if (Integer.valueOf(strCatchwt) == 0) {
            if (!strTQty.equalsIgnoreCase("")) {
                dAvailQty = Double.parseDouble(strTQty);
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

        mDbHelper.openReadableDatabase();
        Boolean isValidSlot = mDbHelper.isValidSlot(Slot);
        mDbHelper.closeDatabase();

        if (dQty <= 0) {
            mToastMessage.showToast(SavePickTaskActivity.this, "Please Enter a Valid Qty");
        } else if (dQty > dAvailQty) {
            mToastMessage.showToast(SavePickTaskActivity.this, "Qty entered is more than Available Qty");
        } else if (!isValidSlot) {
            mToastMessage.showToast(SavePickTaskActivity.this,
                    "Invalid Slot");
        } else {
            flag = "Y";
            picktaskdetail tpicktaskdetail = new picktaskdetail();
            tpicktaskdetail.setItem(item);
            tpicktaskdetail.setDescrip(desc);
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
            tpicktaskdetail.setWeight(weight);
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
            Globals.gLotno = mDbHelper.SelectLotNo(Globals.gPickTaskWlotno);
            mDbHelper.closeDatabase();
            Globals.gEndPickDuration = System.currentTimeMillis();
            pickDuration = (int) ((Globals.gEndPickDuration - Globals.gStartPickDuration)/1000);
            tpicktaskdetail.setpickDuration(pickDuration);

            if (dQty != dAvailQty) {
                Globals.gLineSplit = "1";
                SplitDetailLine(tpicktaskdetail, (dAvailQty - dQty));
            }
            tpicktaskdetail.setLotNo(Globals.gLotno);
            tpicktaskdetail.setWLotNo(Globals.gPickTaskWlotno);
            tpicktaskdetail.setSlot(Slot);

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
                mDbHelper.updatePickTaskDetail(tpicktaskdetail, item, orgTranlineno, orgdoclineno, (dQty),"");
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
                mDbHelper.updatePickTaskDetail(tpicktaskdetail, item, strTranlineNo, doclineno, dQty,"");
                mDbHelper.closeDatabase();
            }
            mToastMessage.showToast(SavePickTaskActivity.this, "Values updated successfully");
            mSupporter.simpleNavigateTo(PickTaskActivity.class);
        }
    }

    @Override
    public void onBackPressed() {
        if(edtQty.getText().toString().equals("") && (edtSLot.getText().toString().equals(""))){
            mSupporter.simpleNavigateTo(PickTaskActivity.class);
        } else{
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
                        mSupporter.simpleNavigateTo(PickTaskActivity.class);
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

    public void qtyAlert(String Slot, String Qty, Double getqty) {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        String setMsg = "\t Insufficient quantity at Slot " + "\n\n" +
                "\t Slot              : " + Slot + "\n" +
                "\t Quantity at Slot  : " + String.valueOf(getqty) + "\n" +
                "\t Quantity Entered  : " + Qty + "\n";
        alertUser.setMessage(setMsg);

        alertUser.setPositiveButton("Proceed",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Saveprocess();
                    }
                });

        alertUser.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isProceed = false;
                        dialog.cancel();
                    }
                });

        alertUser.show();
    }

    public void SplitDetailLine(picktaskdetail tpicktaskdetail, double Qty) {

        mDbHelper.openReadableDatabase();
        int TranlineCount =  mDbHelper.mTranlineCount();
        mDbHelper.closeDatabase();
        mDbHelper.openReadableDatabase();
        int DoclineCount =  mDbHelper.mDoclineCount();
        mDbHelper.closeDatabase();
        mDbHelper.openReadableDatabase();
        int rowNo =  mDbHelper.mRowNoCount();
        mDbHelper.closeDatabase();

        mDbHelper.openWritableDatabase();
        String sQty= String.valueOf(Qty);
        String cQty = mDbHelper.DecimalFractionConversion(sQty, decnum);
        Globals.gTqty = Double.valueOf(cQty);

        Globals.gTranlineno = TranlineCount + 1;
        Globals.gDoclineno = DoclineCount + 1;
        Globals.gPTDetailRowCount = rowNo + 1;
        mDbHelper.SplitNewLine(tpicktaskdetail, Globals.gTranlineno, Globals.gDoclineno, Globals.gPTDetailRowCount, Globals.gTqty);
        mDbHelper.closeDatabase();
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


            dialog = new ProgressDialog(SavePickTaskActivity.this);
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
                mToastMessage.showToast(SavePickTaskActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(SavePickTaskActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(SavePickTaskActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
   //     new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }
}
