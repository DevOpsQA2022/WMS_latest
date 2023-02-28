package com.silvercreek.wmspickingclient.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.physicalcountDetail;
import com.silvercreek.wmspickingclient.model.physicalcountICITEM;
import com.silvercreek.wmspickingclient.model.physicalcountSlot;
import com.silvercreek.wmspickingclient.model.physicalcountWHMLOT;
import com.silvercreek.wmspickingclient.model.receivetaskWHRPLT;
import com.silvercreek.wmspickingclient.util.DecimalDigitsInputFilter;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


public class PhysicalCountSaveActivity extends AppCompatActivity {

    private TextView tvSlot, tvItemDesc, tvLot,OrgQty;
    private EditText edtQty;
    private Spinner spinnerUOM;
    private Button btnSave, btnClose;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private ArrayAdapter<String> adapter;
    private List<physicalcountDetail> DetailList;
    private List<physicalcountDetail> CommonList;
    private List<physicalcountICITEM> ItemList;
    private List<physicalcountWHMLOT> WhmlotList;
    private List<String> mCaseList;
    private String mUsername="";
    private String mPassword;
    private boolean isValid = false;
    private Boolean isAvailDetail = false;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_FETCH_PHYSICALCOUNT_LookUPData ="PhysicalCount_LookupData";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private int updatedno = 0;
    private String mSessionId="";
    private String getQty;
    public Double getqTy = 0.0;
    public  Double tCountQty = 0.0;
    public  Double tqtY = 0.0;
    private String item, umeasur, slot, countid, page, doclineno, loctid, wlotno, tcountqty, lotrefid,counted,
                    decnum,tqty, itemShow, itemdesc, surprisadd,flag, posted;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String  mCompany,mDeviceId ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physcial_count_save);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        edtQty = (EditText) findViewById(R.id.edtQty);
        spinnerUOM = (Spinner) findViewById(R.id.spinner);
        tvSlot = (TextView) findViewById(R.id.tvSlot);
        tvItemDesc = (TextView) findViewById(R.id.tvItemDesc);
        tvLot = (TextView) findViewById(R.id.tvLot);
        OrgQty = (TextView) findViewById(R.id.OrgQty);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnClose = (Button) findViewById(R.id.btnCancel);

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;
        mCompany = Globals.gCompanyDatabase;

        mDeviceId = Globals.gDeviceId;

        edtQty.requestFocus();

        if(edtQty.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

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

        GetAllDetailData();

        mDbHelper.openReadableDatabase();
        mCaseList = mDbHelper.mGetCase(item, Globals.gPCUOM);
        mDbHelper.closeDatabase();


        edtQty.requestFocus();
        if(edtQty.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")){
            edtQty.setShowSoftInputOnFocus(false);

        }else {
            edtQty.setShowSoftInputOnFocus(true);

        }



        tvSlot.setText("Physical Counter " + slot);
        tvItemDesc.setText(itemShow);
        tvLot.setText("Pallet # : " + lotrefid);
      /*  edtQty.requestFocus();
        edtQty.setSelectAllOnFocus(true);
*/

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
            }
        });

        adapter = new ArrayAdapter<String>(PhysicalCountSaveActivity.this, android.R.layout.simple_spinner_item, mCaseList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUOM.setAdapter(adapter);


        spinnerUOM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub

                umeasur = spinnerUOM.getSelectedItem().toString();
                Globals.gPCUOM = umeasur;
                GetAllDetailData();
                mDbHelper.openReadableDatabase();
                getQty = mDbHelper.mgetQty(umeasur, flag);
                mDbHelper.closeDatabase();

                if(getQty.equals("")){
                    getQty="0";
                    // getQty="0.00000";
                }
                getqTy = Double.valueOf(getQty);

                if (flag=="Y") {

                    if(String.valueOf(Math.round(getqTy)) == "0"){
                        edtQty.setText("");
                    } else{
                        edtQty.setText(String.valueOf(Math.round(getqTy)));
                        edtQty.setFocusableInTouchMode(true);
                        edtQty.requestFocus();
                        edtQty.selectAll();
                    }
                } else{

                    if(getQty == "0") {
                        edtQty.setText("");
                    }else{
                        edtQty.setText(String.valueOf(Math.round(Double.parseDouble(getQty))));
                        edtQty.setFocusableInTouchMode(true);
                        edtQty.requestFocus();
                        edtQty.selectAll();

                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Qty = "", Umeasur = "";
                Double dQty = 0.0, dTcountqty = 0.0;

                Qty = edtQty.getText().toString();
                Umeasur = spinnerUOM.getSelectedItem().toString();

                if (!Qty.equalsIgnoreCase("")) {
                    dQty = Double.parseDouble(Qty);
                } else {
                    dQty = Double.parseDouble("0");
                }

            //    if (dQty <= 0) {
                if (Qty.equals("")) {
                    //mToastMessage.showToast(PhysicalCountSaveActivity.this, "Please enter a Valid Qty");
                    mToastMessage.showToast(PhysicalCountSaveActivity.this, "Please enter Qty");
                } else {

                    if (surprisadd == "0") {
                        mDbHelper.openReadableDatabase();
                        Boolean isAvailUOM = mDbHelper.isAvailUOM(wlotno, Umeasur);
                        Boolean isAvailUOMWHMQTY = mDbHelper.isAvailUOMWHMQTY(wlotno, Umeasur);
                        mDbHelper.closeDatabase();
                        if (!isAvailUOM && !isAvailUOMWHMQTY) {
                            surprisadd = "1";
                            isValid = true;
                        } else if(!isAvailUOM & isAvailUOMWHMQTY){
                            isValid = false;
                        } else if(isAvailUOM & !isAvailUOMWHMQTY){
                            isValid = false;
                        } else{
                            isValid = true;
                        }
                    } else {
                        mDbHelper.openReadableDatabase();
                        isAvailDetail = mDbHelper.isAvailUOM(wlotno, Umeasur);
                        mDbHelper.closeDatabase();
                        if (isAvailDetail) {
                            isValid = true;
                        }
                    }

                    mDbHelper.openReadableDatabase();
                    String invtype =  mDbHelper.getInvtype(item);
                    mDbHelper.closeDatabase();

                    physicalcountDetail tphysicalcountDetail = new physicalcountDetail();

                    tphysicalcountDetail.setslot(Globals.gPCSlot);
                    tphysicalcountDetail.setcountid(countid);
                    tphysicalcountDetail.setpage(page);
                    tphysicalcountDetail.setdoclineno(doclineno);
                    tphysicalcountDetail.setloctid(loctid);
                    tphysicalcountDetail.setitem(item);
                    tphysicalcountDetail.setinvtype(invtype);
                    tphysicalcountDetail.setwlotno(wlotno);
                    tphysicalcountDetail.setlotrefid(lotrefid);
                    tphysicalcountDetail.setumeasur(Umeasur);
                    tphysicalcountDetail.setitmdesc(itemdesc);
                    tphysicalcountDetail.setitemShow(itemShow);
                    tphysicalcountDetail.settcountqty(tcountqty);
                    tphysicalcountDetail.settqty(tqty);
                    tphysicalcountDetail.setsurprisadd(surprisadd);

                    String cQty = mDbHelper.DecimalFractionConversion(String.valueOf(dQty), decnum);
                    dQty = Double.valueOf(cQty);

                    if (surprisadd.equals("1") && !isAvailDetail){

                        mDbHelper.openReadableDatabase();
                        int DoclineCount =  mDbHelper.docLineCount();
                        mDbHelper.closeDatabase();

                        mDbHelper.openWritableDatabase();
                        mDbHelper.splitPhysicalCountDetail(tphysicalcountDetail, DoclineCount + 1, dQty);
                        mDbHelper.closeDatabase();
                        mToastMessage.showToast(PhysicalCountSaveActivity.this, "Values updated successfully");
                        mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
                    } else if(isValid){

                        mDbHelper.openReadableDatabase();
                        updatedno =  mDbHelper.getMaxUpdatedNo();
                        mDbHelper.closeDatabase();

                        if(updatedno != 0){
                            updatedno = updatedno +1;
                        }else{
                            updatedno = 1;
                        }

                        mDbHelper.openWritableDatabase();
                        mDbHelper.UpdatePhysicalCountDetail(tphysicalcountDetail, item, wlotno, Umeasur, dQty,String.valueOf(updatedno));
                        mDbHelper.closeDatabase();
                        mToastMessage.showToast(PhysicalCountSaveActivity.this, "Values updated successfully");
                        mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
                    } else {
                        mToastMessage.showToast(PhysicalCountSaveActivity.this, "Unit of measure is not assigned to you.");
                    }
                }
            }
        });
        edtQty.requestFocus();
        edtQty.setSelectAllOnFocus(true);
        edtQty.selectAll();
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
                        mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
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
    public void GetAllDetailData(){

        mDbHelper.openReadableDatabase();
        DetailList = mDbHelper.getSeletedDetailList(Globals.gPCWlotno, Globals.gPCUOM);
        mDbHelper.closeDatabase();

        if (DetailList.size()>0) {      //SCS CIRK 2022/07/25 CT69649C:
            if (DetailList.get(0).getposted().equals("P"))
            {
                mToastMessage.showToast(PhysicalCountSaveActivity.this, "Select detail line already posted.");
                mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
            } else {
                item = DetailList.get(0).getitem();
                umeasur = DetailList.get(0).getumeasur();
                slot = DetailList.get(0).getslot();
                countid = DetailList.get(0).getcountid();
                page = DetailList.get(0).getpage();
                doclineno = DetailList.get(0).getdoclineno();
                loctid = DetailList.get(0).getloctid();
                wlotno = DetailList.get(0).getwlotno();
                tcountqty = DetailList.get(0).gettcountqty();
                lotrefid = DetailList.get(0).getlotrefid();
                tqty = DetailList.get(0).gettqty();
                decnum = DetailList.get(0).getdecnum();
                itemdesc = DetailList.get(0).getitmdesc();
                itemShow = DetailList.get(0).getitemShow();
                counted = DetailList.get(0).getcounted();
                flag = DetailList.get(0).getFlag();
                surprisadd = DetailList.get(0).getsurprisadd();

                OrgQty.setText(String.valueOf(Math.round(tqtY)));

                if (Globals.gPCSurpriseAdd || surprisadd.equals("1")) {
                    surprisadd = "1";
                } else {
                    surprisadd = "0";
                }
                Globals.gPCItem = item;

                if (flag == "Y") {
                     tCountQty = Double.valueOf(tcountqty);
                     if(String.valueOf(Math.round(tCountQty)) == "0"){
                         edtQty.setText("");
                     }else {
                         edtQty.setText(String.valueOf(Math.round(tCountQty)));
                     }
                   // edtQty.setText(tcountqty);
                    edtQty.requestFocus();
                    edtQty.setSelectAllOnFocus(true);
                    edtQty.selectAll();
                }else {
                     tqtY = Double.valueOf(tqty);
                     if(String.valueOf(Math.round(tqtY)) == "0"){
                         edtQty.setText("");
                     }else {
                         edtQty.setText(String.valueOf(Math.round(tqtY)));
                     }
                  // edtQty.setText(tqty);
                    edtQty.requestFocus();
                    edtQty.setSelectAllOnFocus(true);
                    edtQty.selectAll();
                }
            }
        } else{
            mDbHelper.openReadableDatabase();
            WhmlotList = mDbHelper.getSelectedItem(Globals.gPCWlotno);
            mDbHelper.closeDatabase();
            if (WhmlotList.size() > 0){     //SCS CIRK 2022/07/25 CT69649C:

                item = WhmlotList.get(0).getitem();
                slot = Globals.gPCSlot;
                wlotno = Globals.gPCWlotno;
                lotrefid = WhmlotList.get(0).getlotrefid();

                mDbHelper.openReadableDatabase();
                CommonList = mDbHelper.getSeletedWlotnoCommonList();
                mDbHelper.closeDatabase();
                if (CommonList.size() > 0) {        //SCS CIRK 2022/07/25 CT69649C:
                    countid = CommonList.get(0).getcountid();
                    page = CommonList.get(0).getpage();
                    loctid = CommonList.get(0).getloctid();
                }

                mDbHelper.openReadableDatabase();
                ItemList = mDbHelper.getSeletedItemList(item);
                mDbHelper.closeDatabase();
                if (ItemList.size() > 0) {      //SCS CIRK 2022/07/25 CT69649C:
                    itemdesc = ItemList.get(0).getitmdesc();
                    itemShow = ItemList.get(0).getitemShow();
                    decnum = ItemList.get(0).getdecnum();
                }
                doclineno = Globals.gPCDetailDoclineno;
                tcountqty = "0.00000";
                umeasur = "";
                tqty = "0.00000";
                counted = "0";
                flag = "N";
                if (Globals.gPCSurpriseAdd) {
                    surprisadd = "1";
                } else {
                    surprisadd = "0";
                }
            } else {
                Globals.gPCItem = item;
                mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
                mToastMessage.showToast(PhysicalCountSaveActivity.this, "No Data Found.");
                LogfileCreator.mAppendLog("No data available in saveList,WhmlotList(PhysicalCountSaveActivity)");
            }
        }

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


            dialog = new ProgressDialog(PhysicalCountSaveActivity.this);
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
                mToastMessage.showToast(PhysicalCountSaveActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PhysicalCountSaveActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PhysicalCountSaveActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
    //    new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }


}