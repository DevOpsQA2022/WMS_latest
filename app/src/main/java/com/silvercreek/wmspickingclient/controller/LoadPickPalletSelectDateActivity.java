package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
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
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class LoadPickPalletSelectDateActivity extends AppBaseActivity {

    private ListView lstSelectDate;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private String mSelectedWorkDate;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    public static String NAMESPACE = "";
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_FETCH_LOADPICKPALLET_LookUPData ="LoadPickedPallets_LookupData";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mSessionId;
    private String mUsername,mDeviceId,mCompany;
    private File mImpOutputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pick_pallet_select_date);

        lstSelectDate = (ListView)findViewById(R.id.lvSelectDate);
        arrayList = new ArrayList<String>();
        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();


        mCompany = Globals.gCompanyDatabase;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;
        mUsername = Globals.gUsercode;
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
        Globals.gServerpath = URL_SERVER_PATH;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String currentDate = simpleDateFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String NextDate = simpleDateFormat.format(calendar.getTime());
        arrayList.add(currentDate);
        arrayList.add(NextDate);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){

                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                // Set the text size 25 dip for ListView each item
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15);
                // Return the view
                return view;
            }
        };
        lstSelectDate.setAdapter(adapter);

        lstSelectDate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                mSelectedWorkDate = adapter.getItem(position);
                Globals.gLPPWorkDate = mSelectedWorkDate;
                if (mSupporter.isNetworkAvailable(LoadPickPalletSelectDateActivity.this)) {
                    new GetLoadPickPalletData(mSessionId, mSelectedWorkDate, Globals.gCompanyId, mUsername).execute();
                } else {
                    mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
            }
        });
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


            dialog = new ProgressDialog(LoadPickPalletSelectDateActivity.this);
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
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }


    @Override
    protected void onDestroy() {
       // new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
    }




    class GetLoadPickPalletData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pSession, pCompid, pDate, pUser;

        public GetLoadPickPalletData(String mSession, String mDate, String mCompid, String mUser) {
            this.pSession = mSession;
            this.pDate = mDate;
            this.pCompid = mCompid;
            this.pUser = mUser;
            dialog = new ProgressDialog(LoadPickPalletSelectDateActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Load Picked Pallets Lookup Data");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_FETCH_LOADPICKPALLET_LookUPData);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(pSession);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pDate");
                info.setValue(pDate);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompid);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUser);
                info.setType(String.class);
                request.addProperty(info);
                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_FETCH_LOADPICKPALLET_LookUPData;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "LoadPickPalletAllData" + ".xml");
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
                    result = "LoginFailed";
                    if (resultString.toString().toLowerCase().contains("no data found")){
                        result = "No data found";
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

                new LoadPickPalletLookUpData().execute();

            } else if (result.equals("No data found")) {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                        "No data found.");
            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                        "Login failed invalid username or password");
            } else {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this,
                        "Invalid username or password");
            }

            dialog.cancel();
        }
    }

    private class LoadPickPalletLookUpData extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadPickPalletLookUpData() {
            dialog = new ProgressDialog(LoadPickPalletSelectDateActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(LoadPickPalletSelectDateActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteLoadPickPalletLookupData();
                Globals.gLPPSummaryRowCount = 1;
                Globals.gLPPDetailRowCount = 1;
                mDbHelper.closeDatabase();
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        String serCompName = compList.get(c);

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(mUsername, "01", "LoadPickPalletAllData" + ".xml");
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
                mSupporter.simpleNavigateTo(LoadPickPalletGetPalletActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this, "File not available");
            } else {
                mToastMessage.showToast(LoadPickPalletSelectDateActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
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
}
