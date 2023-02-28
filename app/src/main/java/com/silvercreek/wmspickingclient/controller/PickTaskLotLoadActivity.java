package com.silvercreek.wmspickingclient.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMLOT;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketTimeoutException;
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

public class PickTaskLotLoadActivity extends AppCompatActivity {

    private ListView transList;
    private EditText edtLot,edtWeight,edtStop,edtRoute,edtCase;
    private Button btnSave, btnCancel, btnExport;
    private TextView tvActivePallet;

    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private List<picktaskdetail> picktaskdetail;
    private String mUsername;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mDeviceId ="";
    private String mPassword;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private PickTaskDetailAdapter adapter;
    private List<String> mLotList;
    private ArrayList<picktaskWHMLOT> mLotMast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_task_lot_load);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        tvActivePallet = (TextView) findViewById(R.id.tvActivePallet);
        edtLot =(EditText) findViewById(R.id.edtLot);
        edtWeight = (EditText) findViewById(R.id.edtWeight);
        edtStop = (EditText) findViewById(R.id.edtStop);
        edtRoute = (EditText) findViewById(R.id.edtRoute);
        edtCase = (EditText) findViewById(R.id.edtCase);
        btnSave = (Button) findViewById(R.id.btn_save);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnExport = (Button) findViewById(R.id.btn_export);
        transList = (ListView) findViewById(R.id.lst_TransItems);

        mUsername = Globals.gUsercode;
        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;

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

        mDbHelper.openReadableDatabase();
        picktaskdetail = mDbHelper.getPickTaskDetail();
        mDbHelper.closeDatabase();

        adapter = new PickTaskDetailAdapter(PickTaskLotLoadActivity.this, picktaskdetail);
        transList.setAdapter(adapter);

        edtLot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String mWlotno = edtLot.getText().toString().trim();

                            mLotList = getLotList();
                            edtLot.getText().clear();
                            if(mLotList.size()>0) {     //SCS CIRK 2022/07/25 CT69649C:
                                if (!mLotList.get(0).contains(mWlotno)) {
                                    mToastMessage.showToast(PickTaskLotLoadActivity.this,
                                            "Invalid Lot");
                                } else {
                                    Globals.gPickTaskWlotno = mWlotno;
                                    Globals.gPickTaskItem = mLotList.get(1).toString();
                                    mSupporter.simpleNavigateTo(SavePickTaskActivity.class);
                                }
                            }else{
                                mToastMessage.showToast(PickTaskLotLoadActivity.this,"No data available");
                                LogfileCreator.mAppendLog("No data available in mLotList(PickTaskLotLoadActivity)");
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    // to get Lot List
    public List<String> getLotList() {
        List<String> LotList = new ArrayList<String>();

        mDbHelper.openReadableDatabase();
        mLotMast = mDbHelper.getLotList();
        mDbHelper.closeDatabase();
        for (int i = 0; i < mLotMast.size(); i++) {
            LotList.add(mLotMast.get(i).getWlotno());
            LotList.add(mLotMast.get(i).getItem());
        }
        return LotList;
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


            dialog = new ProgressDialog(PickTaskLotLoadActivity.this);
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
                mToastMessage.showToast(PickTaskLotLoadActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskLotLoadActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickTaskLotLoadActivity.this,
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
