package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Globals.chargelistHash;
import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.RepackList;
import com.silvercreek.wmspickingclient.model.SalesOrderCategoryList;
import com.silvercreek.wmspickingclient.model.SalesOrderPickList;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.xml.ExportPickTask;
import com.silvercreek.wmspickingclient.xml.ExportSalesOrderCharges;

import org.apache.commons.io.FileUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SalesOrderCharges extends AppCompatActivity {

    private GridView transList;
    private EditText edtSoNo;
    private TextView tvCrg;
    private ImageView imgPickSO,imgPickSO1,imgPickSO2;
    private Button btn_done,btn_cancel;
    private SalesOrderChargesAdapter adapter;
    private List<SalesOrderCategoryList> mSalesOrderCategoryList;
    private List<SalesOrderCategoryList> exportSalesOrderCategoryList;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private Boolean isSoNoAvaile=false;
    private ToastMessage mToastMessage;
    private File mImpOutputFile;
    private int mTimeout;
    private String mLoctid = "";
    private String mPallet = "";
    private String Getmsg = "";
    private String Sono = "";
    private String mSessionId ="", mCompany ="", mUsername ="", mDeviceId = "";
    private SharedPreferences sharedpreferences;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    ListView pickListRepack = null;
    private Button cancel;
    private SalesOrderListAdapter salesOredrListadapter;
    List orderChangesList;
    public static final String METHOD_GET_SO_CHARGES_LIST ="SoCharges_Category";
    public static final String METHOD_EXPORT_SOCHARGES_DATA ="SoCharges_Save";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_order_charges);

        transList = (GridView) findViewById(R.id.lst_TransItems);
        edtSoNo = (EditText) findViewById(R.id.edt_soNo);
        imgPickSO = (ImageView) findViewById(R.id.btn_pickSO);
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;

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



        edtSoNo.requestFocus();
        if (edtSoNo.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }



        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtSoNo.setShowSoftInputOnFocus(false);

        } else {
            edtSoNo.setShowSoftInputOnFocus(true);

        }


       // new GetSoChargesCategoryList(mUsername).execute();

        mDbHelper.openReadableDatabase();
        mSalesOrderCategoryList = mDbHelper.getSalesOrderChargesList();
        mDbHelper.closeDatabase();

       /* for(int i=0;i<orderChangesList.size();i++){
            orderChangesList.get(i);
        }*/


        /*if (mSalesOrderCategoryList.size()<=0){
            NoChargesAlert();
        }*/



        adapter = new SalesOrderChargesAdapter(SalesOrderCharges.this, mSalesOrderCategoryList,SOFT_KEYBOARD);
        transList.setAdapter(adapter);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //for(int i=0;i<mSalesOrderCategoryList.size();i++){
                for(int i = 0; i <= transList.getLastVisiblePosition() - transList.getFirstVisiblePosition(); i++){
                    View  chargeView = transList.getChildAt(i);
                    EditText editText =chargeView.findViewById(R.id.edt_qty);
                    editText.setText("");
                }

                edtSoNo.setText("");
                edtSoNo.setEnabled(true);
                edtSoNo.requestFocus();

            }
        });



        edtSoNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            String Sono = edtSoNo.getText().toString();

                            mDbHelper.openReadableDatabase();
                            isSoNoAvaile = mDbHelper.isSoNoAvail(Sono);
                            mDbHelper.closeDatabase();

                            if (isSoNoAvaile) {
                                edtSoNo.setText(Sono);
                                edtSoNo.setEnabled(false);
                                edtSoNo.requestFocus();



                               /* new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvCrg.requestFocus();
                                    }
                                }, 150);*/
                            } else {
                                mToastMessage.showToast(SalesOrderCharges.this, "Please enter valid SO #");
                                edtSoNo.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtSoNo.requestFocus();
                                    }
                                }, 150);
                            }
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean lis_Edt_IsNotMty = false;

                 Sono = edtSoNo.getText().toString();

                mDbHelper.openReadableDatabase();
                isSoNoAvaile = mDbHelper.isSoNoAvail(Sono);
                mDbHelper.closeDatabase();

                if (Sono.equals("")) {
                    mToastMessage.showToast(SalesOrderCharges.this, "Please enter valid SO #");
                    edtSoNo.setText("");
                    edtSoNo.requestFocus();
                }else {

                    for (int i = 0; i <= transList.getLastVisiblePosition() - transList.getFirstVisiblePosition(); i++) {
                        View chargeView = transList.getChildAt(i);
                        EditText editText = chargeView.findViewById(R.id.edt_qty);
                        String lstEdt = editText.getText().toString();
                        if (!lstEdt.equals("")) {
                            lis_Edt_IsNotMty = true;
                        }
                    }


                    if (!edtSoNo.isEnabled() || isSoNoAvaile) {

                        if (lis_Edt_IsNotMty) {
                            chargelistHash = new Hashtable<>();
                            //for (int i = 0; i < mSalesOrderCategoryList.size(); i++) {
                            for (int i = 0; i <= transList.getLastVisiblePosition() - transList.getFirstVisiblePosition(); i++) {
                                View chargeView = transList.getChildAt(i);
                                EditText editText = chargeView.findViewById(R.id.edt_qty);
                                TextView tvCharge = chargeView.findViewById(R.id.tv_charges);
                                chargelistHash.put(tvCharge.getText().toString(), editText.getText().toString());
                            }
                            String SoNo = edtSoNo.getText().toString();
                            ExportData(SoNo);
                        } else {
                            mToastMessage.showToast(SalesOrderCharges.this, "Charges field is empty");
                            edtSoNo.requestFocus();
                        }
                    } else {
                        mToastMessage.showToast(SalesOrderCharges.this, "Please enter valid SO #");
                        edtSoNo.setText("");
                        edtSoNo.requestFocus();
                    }

                }


            }
        });

        imgPickSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                List<SalesOrderPickList> list = mDbHelper.getSalesOrderListData();
                mDbHelper.closeDatabase();

                LayoutInflater li = LayoutInflater.from(SalesOrderCharges.this);
                View promptsView = li.inflate(R.layout.salesorder_pick_list,null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        SalesOrderCharges.this);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                pickListRepack = (ListView) promptsView.findViewById(R.id.lst_SOPickList);
                cancel = (Button) promptsView.findViewById(R.id.Cancel_btn);

                salesOredrListadapter = new SalesOrderListAdapter(SalesOrderCharges.this, list);
                pickListRepack.setAdapter(salesOredrListadapter);


                pickListRepack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                        SalesOrderPickList salesOrderPickList = new SalesOrderPickList();
                        salesOrderPickList = (SalesOrderPickList) pickListRepack.getItemAtPosition(i);
                        String repackNo = salesOrderPickList.getSono();




                        edtSoNo.setText(repackNo);
                        edtSoNo.setEnabled(false);

                        alertDialog.dismiss();

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        edtSoNo.requestFocus();
                    }
                });

                alertDialog.show();

            }
        });

    }

    class GetSoChargesCategoryList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetSoChargesCategoryList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(SalesOrderCharges.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting So ChargesCategory..");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_SO_CHARGES_LIST);
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

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_SO_CHARGES_LIST;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "SoCharges_Category" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().toLowerCase().contains("false")) {
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
                if (mSupporter.isNetworkAvailable(SalesOrderCharges.this)) {

                   new LoadSoChargesGategoryList().execute();

                } else {
                    mToastMessage.showToast(SalesOrderCharges.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(SalesOrderCharges.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(SalesOrderCharges.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(SalesOrderCharges.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }

    private String GetErrorMessage(){

        String GetErrMsg ="";
        try
        {
            //creating a constructor of file class and parsing an XML file
            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "SoCharges_Category1" + ".xml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(mImpOutputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Acknowledgement");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++)
            {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element eElement = (Element) node;
                    GetErrMsg = eElement.getElementsByTagName("ErrorMessage").item(0).getTextContent();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String errorCode = "Err501";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage());
            String result = "Invalid File";
            return result;
        }
        return GetErrMsg;
    }

    private class LoadSoChargesGategoryList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadSoChargesGategoryList() {
            dialog = new ProgressDialog(SalesOrderCharges.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(SalesOrderCharges.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteSalesOrderChargeCategoryList();
                mDbHelper.closeDatabase();

                if (compSize != 0) {
                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "SoCharges_Category" + ".xml");
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

                //mSupporter.simpleNavigateTo(MoveTaskActivity.class);
                //mSupporter.simpleNavigateTo(SalesOrderCharges.class);

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(SalesOrderCharges.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(SalesOrderCharges.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(SalesOrderCharges.this, "File not available");
            } else {
                mToastMessage.showToast(SalesOrderCharges.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
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



    public void ExportData(String Sono) {

        if (mSalesOrderCategoryList.size() != 0) {
            String exportXml = getRecordXmlExportPO(mSalesOrderCategoryList,Sono);
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

            if (mSalesOrderCategoryList.size() != 0) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else {
                ExportError();
            }
        } else {
            mToastMessage.showToast(SalesOrderCharges.this,
                    "Unable to update Server");
        }
    }
    public String getRecordXmlExportPO(List<SalesOrderCategoryList> dList,String SoNo) {
        String exportPODataXml = "";

        try {
            ExportSalesOrderCharges exportData = new ExportSalesOrderCharges();

            StringBuffer sb = new StringBuffer();

            exportData.writeXml( sb, SalesOrderCharges.this, mDbHelper,SoNo);

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

    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(SalesOrderCharges.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "ExportSalesOrderCharges" + ".xml");
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
                mToastMessage.showToast(SalesOrderCharges.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(SalesOrderCharges.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(SalesOrderCharges.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(SalesOrderCharges.this);
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

    class ExportTranData extends AsyncTask<String, String, String> {
        String result = "";
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId;

        public ExportTranData(String Session, String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(SalesOrderCharges.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_SOCHARGES_DATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/ExportSalesOrderCharges.xml");
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
                String soap_action = NAMESPACE + METHOD_EXPORT_SOCHARGES_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "SalesOrderChanges" + ".xml");
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
                }else if (resultString.toString().contains("<ErrorMessage>No charge information to updat</ErrorMessage>")) {
                    result = "No charge information to update.";
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


                mToastMessage.showToast(SalesOrderCharges.this,"Updated successfully.");

                for(int i = 0; i <= transList.getLastVisiblePosition() - transList.getFirstVisiblePosition(); i++){
                //for(int i=0;i<mSalesOrderCategoryList.size();i++){
                    View  chargeView = transList.getChildAt(i);
                    EditText editText =chargeView.findViewById(R.id.edt_qty);
                    editText.setText("");
                }

                edtSoNo.setText("");
                edtSoNo.setEnabled(true);
                edtSoNo.requestFocus();

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(SalesOrderCharges.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(SalesOrderCharges.this,
                        "PO Updation failed");

            } else if (result.equalsIgnoreCase("time out error")) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(SalesOrderCharges.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(SalesOrderCharges.this,
                        result.toString());
            }
            dialog.cancel();
        }
    }

    @Override
    public void onBackPressed() {

        mSupporter.simpleNavigateTo(MainmenuActivity.class);
    }


    public void NoChargesAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Alert");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("No charges available.");
        alertUser.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSupporter.simpleNavigateTo(MainmenuActivity.class);
                    }
                });

        alertUser.show();
    }


}