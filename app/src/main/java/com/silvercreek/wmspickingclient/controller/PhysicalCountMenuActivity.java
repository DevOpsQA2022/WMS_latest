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
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.loadpickpalletWHITRL;
import com.silvercreek.wmspickingclient.model.physicalcountSlot;
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

public class PhysicalCountMenuActivity extends AppCompatActivity {

    private EditText edtSlot;
    private ListView lvMenu;
    private TextView tvSlot;
    private RadioButton radio_show, radio_hide,radioButton;
    private RadioGroup radio_grp;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private PhysicalCountMenuAdapter adapter;
    private List<physicalcountSlot> slotList;
    private int closedcount=0;
    private List<String> slotValueList;
    private List<physicalcountSlot> mSlotList;
    private Boolean isSlotAvailable;
    private File mImpOutputFile;
    private String mUsername="";
    private String mPassword;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_FETCH_PHYSICALCOUNT_LookUPData ="PhysicalCount_LookupData";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mSessionId ="";
    private boolean isPostingDataAvail = false;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String  mCompany,mDeviceId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_count_menu);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        edtSlot = (EditText) findViewById(R.id.edtSlot);
        lvMenu = (ListView) findViewById(R.id.lst_TransItems);
        tvSlot = (TextView) findViewById(R.id.tvSlot);
        radio_grp = (RadioGroup) findViewById(R.id.radioBtnGroup);
        radio_show = (RadioButton) findViewById(R.id.radioShow);
        radio_hide = (RadioButton) findViewById(R.id.radioHide);

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;
        mCompany = Globals.gCompanyDatabase;

        if(!Globals.SETCHECKEDSHOW && !Globals.SETCHECKEDHIDE){
            Globals.SETCHECKEDSHOW= true;
            Globals.FROMSHOW = true;
            Globals.FROMHIDE = false;
        }

        radio_show.setChecked(Globals.SETCHECKEDSHOW);
        radio_hide.setChecked(Globals.SETCHECKEDHIDE);

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
        mDbHelper.openReadableDatabase();
        slotList = mDbHelper.getPhycialCountList();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        closedcount = mDbHelper.getClosedCountandPartial();
        mDbHelper.closeDatabase();

        edtSlot.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(10)});

        edtSlot.requestFocus();
        if(edtSlot.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")){
            edtSlot.setShowSoftInputOnFocus(false);

        }else {
            edtSlot.setShowSoftInputOnFocus(true);

        }



        tvSlot.setText("Slots: "+ closedcount + "/" + slotList.size());

        adapter = new PhysicalCountMenuAdapter(PhysicalCountMenuActivity.this, slotList);
        lvMenu.setAdapter(adapter);


        radio_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                radio_show.setChecked(Globals.SETCHECKEDSHOW = true);
                radio_hide.setChecked(Globals.SETCHECKEDHIDE = false);
                Globals.FROMSHOW = true;
                Globals.FROMHIDE = false;
            }
        });

        radio_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radio_hide.setChecked(Globals.SETCHECKEDHIDE = true);
                radio_show.setChecked(Globals.SETCHECKEDSHOW = false);
                Globals.FROMHIDE = true;
                Globals.FROMSHOW = false;
            }
        });

        lvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int selectedID = radio_grp.getCheckedRadioButtonId();
                radioButton = findViewById(selectedID);
             /*   Toast.makeText(PhysicalCountMenuActivity.this, radioButton.getText(), Toast.LENGTH_SHORT).show();
                String RadioSts = String.valueOf(radioButton.getText());*/

                physicalcountSlot mSlotlist = (physicalcountSlot) adapter.getItem(position);
                Globals.gPCSlot = mSlotlist.getslot().toString();
               //Globals.listCurrentPosition =0;
                mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
            }
        });

        edtSlot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            String mSlot = edtSlot.getText().toString().trim();

                            mDbHelper.openReadableDatabase();
                            isSlotAvailable = mDbHelper.isPCSlotList(mSlot);
                            mDbHelper.closeDatabase();

                            if (!isSlotAvailable) {
                                mToastMessage.showToast(PhysicalCountMenuActivity.this,
                                        "Invalid Slot");
                                edtSlot.setText("");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtSlot.requestFocus();
                                    }
                                }, 150);
                            } else {
                                Globals.gPCSlot = mSlot;
                                //Globals.listCurrentPosition =0;
                                mSupporter.simpleNavigateTo(PhysicalCountDetailActivity.class);
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
                        Globals.FROMHIDE = false;
                        Globals.FROMSHOW = true;
                        Globals.SETCHECKEDSHOW =false;
                        Globals.SETCHECKEDHIDE =false;
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


    class LogoutRequest extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUsername, pSessionId, pCompId,pDeviceId ;


        String result = "";



        public LogoutRequest(String mDeviceId, String mUsername, String mSessionId, String mCompId ) {
            this.pSessionId = mSessionId;
            this.pDeviceId = mDeviceId;
            this.pUsername = mUsername;
            this.pCompId = mCompId;


            dialog = new ProgressDialog(PhysicalCountMenuActivity.this);
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
                mToastMessage.showToast(PhysicalCountMenuActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PhysicalCountMenuActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PhysicalCountMenuActivity.this,
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