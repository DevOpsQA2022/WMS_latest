package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.SalesOrderPickList;
import com.silvercreek.wmspickingclient.model.menulist;
import com.silvercreek.wmspickingclient.model.notificationcount;
import com.silvercreek.wmspickingclient.model.physicalcountDetail;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.List;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class MainmenuActivity extends AppBaseActivity {

    private Button btntask1, btntask2, btntask3, btntask4, btntask5, btntask6, btntask7, btntask8, btntask9;
    private TextView badgeTask1, badgeTask2, badgeTask3, badgeTask4, badgeTask5, badgeTask6, badgeTask7,badgeTask8;
    private RelativeLayout badgeTask11, badgeTask22, badgeTask33, badgeTask44, badgeTask55, badgeTask66, badgeTask77,badgeTask88;
    private RelativeLayout rTask1, rTask2, rTask3, rTask4, rTask5, rTask6, rTask7,rTask8;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private File mImpOutputFile;
    private notificationcount notification;
    private menulist menulist;
    private List<physicalcountDetail> slotDetailList;
    private String mUsername;
    public static final String LOGOUTREQUEST = "LogoutRequest";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static final String METHOD_GET_TASK_NOTIFICATION ="GetTaskNotification";
    public static final String METHOD_GET_PICKTASK_LIST ="Picktask_GetTaskList ";
    public static final String METHOD_GET_RECEIVETASK_LIST ="ReceiveTask_GetTaskList ";
    public static final String METHOD_GET_PHYSICALCOUNT_LIST ="PhysicalCount_LookupData ";
    public static final String METHOD_GET_MOVETASK_LIST ="MoveTask_GetTaskList ";
    public static final String METHOD_GET_SO_CHARGES_LIST ="SoCharges_List ";
    public static final String METHOD_GET_SO_CHARGES_gategory_LIST ="SoCharges_Category ";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private String mSessionId;
    private String mCompany,mDeviceId;
    private String mLoctid;
    public static String Picktask = "", loadpickedpallets = "", movetask = "", movemanually = "", physicalcount = "", receivetask = "", brkeruom = "",salesOrderCharg = "";
    private String Getmsg;
    private FloatingActionButton sync;
    private Handler handler;
    private int mInterval = 600000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        handler = new Handler();
        startRepeatingTask();

        /*handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new RefreshPage(mUsername).execute();
            }
        },600000);*/
        //1000 * 60 * 4


        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        btntask1 = (Button) findViewById(R.id.btn_task1);
        btntask2 = (Button) findViewById(R.id.btn_task2);
        btntask3 = (Button) findViewById(R.id.btn_task3);
        btntask4 = (Button) findViewById(R.id.btn_task4);
        btntask5 = (Button) findViewById(R.id.btn_task5);
        btntask6 = (Button) findViewById(R.id.btn_task6);
        btntask7 = (Button) findViewById(R.id.btn_task7);
        btntask8 = (Button) findViewById(R.id.btn_task8);
        btntask9 = (Button) findViewById(R.id.btn_task9);
        sync = (FloatingActionButton) findViewById(R.id.btn_Sync);

        badgeTask1=(TextView) findViewById(R.id.badge_notification_task1);
        badgeTask2=(TextView) findViewById(R.id.badge_notification_task2);
        badgeTask3=(TextView) findViewById(R.id.badge_notification_task3);
        badgeTask4=(TextView) findViewById(R.id.badge_notification_task4);
        badgeTask5=(TextView) findViewById(R.id.badge_notification_task5);
        badgeTask6=(TextView) findViewById(R.id.badge_notification_task6);
        badgeTask7=(TextView) findViewById(R.id.badge_notification_task7);
        badgeTask8=(TextView) findViewById(R.id.badge_notification_task8);

        badgeTask11=(RelativeLayout) findViewById(R.id.badge_task1);
        badgeTask22=(RelativeLayout) findViewById(R.id.badge_task2);
        badgeTask33=(RelativeLayout) findViewById(R.id.badge_task3);
        badgeTask44=(RelativeLayout) findViewById(R.id.badge_task4);
        badgeTask55=(RelativeLayout) findViewById(R.id.badge_task5);
        badgeTask66=(RelativeLayout) findViewById(R.id.badge_task6);
        badgeTask77=(RelativeLayout) findViewById(R.id.badge_task7);
        badgeTask88=(RelativeLayout) findViewById(R.id.badge_task8);


        rTask1=(RelativeLayout) findViewById(R.id.relative_task1);
        rTask2=(RelativeLayout) findViewById(R.id.relative_task2);
        rTask3=(RelativeLayout) findViewById(R.id.relative_task3);
        rTask4=(RelativeLayout) findViewById(R.id.relative_task4);
        rTask5=(RelativeLayout) findViewById(R.id.relative_task5);
        rTask6=(RelativeLayout) findViewById(R.id.relative_task6);
        rTask7=(RelativeLayout) findViewById(R.id.relative_task7);
        rTask8=(RelativeLayout) findViewById(R.id.relative_task8);

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
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
        if (Globals.gIsFromWarehouseSelection){
            updateTaskNotification();
            Globals.gIsFromWarehouseSelection = false;
        } else {
            if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {
                new RefreshPage(mUsername).execute();
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Unable to connect with Server. Please Check your internet connection");
            }
        }

        btntask1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask1.getText().toString());
            }
        });
        btntask2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask2.getText().toString());
            }
        });
        btntask3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask3.getText().toString());
            }
        });
        btntask4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask4.getText().toString());
            }
        });
        btntask5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask5.getText().toString());
            }
        });
        btntask6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask6.getText().toString());
            }
        });
        btntask7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressTask(btntask7.getText().toString());
            }
        });
        //01/02/23_CIRK_For Fresha to APN
        btntask8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent theIntent = new Intent( MainmenuActivity.this, RepackMainMenuScreen.class);
                //  theIntent.putExtra("repacknum", "");
                startActivity(theIntent);

                /*Intent theIntent = new Intent( MainmenuActivity.this, PickRepackActivity.class);
                theIntent.putExtra("repacknum", "");
                startActivity(theIntent);
                //ProgressTask(btntask8.getText().toString());*/
            }
        });
        btntask9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent theIntent = new Intent( MainmenuActivity.this, DataUtilitiesActivity.class);
                startActivity(theIntent);
            }
        });


        //Disabled
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(mStatusChecker);
                new RefreshPage(mUsername).execute();
            }
        });
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {

            try{
                //new RefreshPage(mUsername).execute();
            }finally {
                handler.postDelayed(mStatusChecker,mInterval);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new RefreshPage(mUsername).execute();
                    }
                },600000);
            }

        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(mStatusChecker);
    }

    private void ProgressTask(String getTaskname)
    {
        Intent i;
        switch (getTaskname) {
            case "Pick Task":
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {
                    handler.removeCallbacksAndMessages(null);
                    handler.removeCallbacks(mStatusChecker);
                    new GetPickTaskList(mUsername).execute();
                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
                break;
            case "Load Picked Pallets":
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(mStatusChecker);
                 i = new Intent(MainmenuActivity.this, LoadPickPalletSelectDateActivity.class);
                startActivity(i);
                break;
            case "Move Task":
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(mStatusChecker);
                new GetMoveTaskList(mUsername).execute();
                //new GetReceiveTaskList(mUsername).execute();
                //i = new Intent(MainmenuActivity.this, WorkingProgressActivity.class);
                /*i = new Intent(MainmenuActivity.this, MoveTaskActivity.class);
                startActivity(i);*/
                break;
            case "Move Manually":
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(mStatusChecker);

                mDbHelper.openWritableDatabase();
                mDbHelper.deletemoveManually();
                mDbHelper.closeDatabase();

                i = new Intent(MainmenuActivity.this, MoveManuallyActivity.class);
                startActivity(i);
                break;
            case "Physical Count":
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {
                    handler.removeCallbacksAndMessages(null);
                    handler.removeCallbacks(mStatusChecker);
                    new GetPhysicalCountList(mUsername).execute();
                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
                break;
            case "Receive Task":
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {
                    handler.removeCallbacksAndMessages(null);
                    handler.removeCallbacks(mStatusChecker);
                    new GetReceiveTaskList(mUsername).execute();
                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
                break;
           // case "Breaker UOM Utility":
            case "Sales Order Charges":
                handler.removeCallbacksAndMessages(null);
                handler.removeCallbacks(mStatusChecker);
                new GetSalesOrderList(mUsername).execute();
              /*  i = new Intent(MainmenuActivity.this, SalesOrderCharges.class);
                startActivity(i);*/
                break;
        }
    }

    private void updateAvailableTask(String taskName, String badgeValue, int taskOrder)
    {
        switch (taskOrder) {
            case 1:
                btntask1.setText(taskName);
                if  (!badgeValue.equals("")) {
                badgeTask1.setText(badgeValue);
                badgeTask1.setVisibility(View.VISIBLE);
            }
                break;
            case 2:
                btntask2.setText(taskName);
                if  (!badgeValue.equals("")) {
                    badgeTask2.setText(badgeValue);
                    badgeTask2.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                btntask3.setText(taskName);
                if  (!badgeValue.equals("")) {
                    badgeTask3.setText(badgeValue);
                    badgeTask3.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                btntask4.setText(taskName);
                if  (!badgeValue.equals("")) {
                    badgeTask4.setText(badgeValue);
                    badgeTask4.setVisibility(View.VISIBLE);
                }
                break;
            case 5:
                btntask5.setText(taskName);
                if  (!badgeValue.equals("")) {
                    badgeTask5.setText(badgeValue);
                    badgeTask5.setVisibility(View.VISIBLE);
                }
                break;
            case 6:
                btntask6.setText(taskName);
                if  (!badgeValue.equals("")) {
                    badgeTask6.setText(badgeValue);
                    badgeTask6.setVisibility(View.VISIBLE);
                }
                break;
            case 7:
                btntask7.setText(taskName);
                //badgeTask7.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void updateTaskNotification()
    {

        mDbHelper.openReadableDatabase();
        notification = mDbHelper.mGetTaskNotificationData();
        mDbHelper.closeDatabase();

        mDbHelper.openReadableDatabase();
        menulist = mDbHelper.mGetMenuListData();
        mDbHelper.closeDatabase();

        int i = 0;
        int tskorder = 0;
        String[] menuList = new String[7];

        if (menulist.getPickTask()==1) {
            tskorder++;
            Picktask = getResources().getString( R.string.txt_picktask);
            menuList[i] =  String.valueOf(notification.getPickTask());
            updateAvailableTask(Picktask, menuList[i], tskorder);
            i++;
        }
        if (menulist.getLoadPickPallets()==1) {
            tskorder++;
            loadpickedpallets = getResources().getString( R.string.txt_loadpallets);
            menuList[i] =  String.valueOf(notification.getLoadPickPallets());
            updateAvailableTask(loadpickedpallets, menuList[i], tskorder);
            i++;
        }
        if (menulist.getMoveTask()==1) {
            tskorder++;
            movetask = getResources().getString( R.string.txt_movetask);
            menuList[i] =  String.valueOf(notification.getMoveTask());
            updateAvailableTask(movetask, menuList[i], tskorder);
            i++;
        }
        if (menulist.getMoveManually()==1) {
            tskorder++;
            movemanually = getResources().getString( R.string.txt_movemanual);
            menuList[i] =  "";
            updateAvailableTask(movemanually, menuList[i], tskorder);
            i++;
        }
        if (menulist.getPhysicalCount()==1) {
            tskorder++;
            physicalcount = getResources().getString( R.string.txt_physicalcount);
            menuList[i] =  String.valueOf(notification.getPhysicalCount());
            updateAvailableTask(physicalcount, menuList[i], tskorder);
            i++;
        }
        if (menulist.getReceiveTask()==1) {
            tskorder++;
            receivetask = getResources().getString( R.string.txt_receivetask);
            menuList[i] =  String.valueOf(notification.getReceiveTask());
            updateAvailableTask(receivetask, menuList[i], tskorder);
            i++;
        }
        if (menulist.getPickTask()==1 && menulist.getReceiveTask()==1) {
            tskorder++;
            salesOrderCharg = getResources().getString( R.string.sales_order_charges);
            menuList[i] =  "";
            updateAvailableTask(salesOrderCharg, menuList[i], tskorder);
            i++;
        }
        /*if (menulist.getBreakerUomUtility()==1) {
            tskorder++;
            brkeruom = getResources().getString( R.string.txt_brkeruom);
            menuList[i] =  "";
            updateAvailableTask(brkeruom, menuList[i], tskorder);
            i++;
        }*/

        tskorder++;
        unselectedtask(tskorder);

    }
    private void unselectedtask(int unseleted){
        for (int i=unseleted; i<=7; i++) {
            switch (i) {
                case 1:
                    rTask1.setVisibility(View.GONE);
                    badgeTask1.setVisibility(View.GONE);
                    badgeTask11.setVisibility(View.GONE);
                    break;
                case 2:
                    rTask2.setVisibility(View.GONE);
                    badgeTask2.setVisibility(View.GONE);
                    badgeTask22.setVisibility(View.GONE);
                    break;
                case 3:
                    rTask3.setVisibility(View.GONE);
                    badgeTask3.setVisibility(View.GONE);
                    badgeTask33.setVisibility(View.GONE);
                    break;
                case 4:
                    rTask4.setVisibility(View.GONE);
                    badgeTask4.setVisibility(View.GONE);
                    badgeTask44.setVisibility(View.GONE);
                    break;
                case 5:
                    rTask5.setVisibility(View.GONE);
                    badgeTask5.setVisibility(View.GONE);
                    badgeTask55.setVisibility(View.GONE);
                    break;
                case 6:
                    rTask6.setVisibility(View.GONE);
                    badgeTask6.setVisibility(View.GONE);
                    badgeTask66.setVisibility(View.GONE);
                    break;
                case 7:
                    rTask7.setVisibility(View.GONE);
                    badgeTask7.setVisibility(View.GONE);
                    badgeTask77.setVisibility(View.GONE);
                    break;

            }
        }
    }

    private void logoutAlert() {
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Do you want to logout?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.removeCallbacksAndMessages(null);
                        handler.removeCallbacks(mStatusChecker);
                        new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
                        mSupporter.simpleNavigateTo(LoginScreenActivity.class);
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

    /*// back button click event
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }*/

    public void onBackPressed() {
        logoutAlert();
    }

    protected void onDestroy() {
      //  new LogoutRequest(mDeviceId, mUsername, mSessionId, mCompany).execute();
        super.onDestroy();
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


            dialog = new ProgressDialog(MainmenuActivity.this);
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
                mToastMessage.showToast(MainmenuActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Unable to update Server. Please Save again");
            }

            dialog.cancel();
        }
    }



    //Refresh the page
    class RefreshPage extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public RefreshPage(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_TASK_NOTIFICATION);
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
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_TASK_NOTIFICATION;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "TaskCount" + ".xml");
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new MainmenuActivity.LoadTaskNotification().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Login failed invalid username or password");
            } else if (result.equals("time out error")) {
                new RefreshPage(mUsername).execute();
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Unable to connect with Server. Please check your internet connection");
            }

            dialog.cancel();
        }
    }

    private class LoadTaskNotification extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadTaskNotification() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteNotificationcount();
                mDbHelper.deleteMenulist();
                mDbHelper.closeDatabase();
                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "TaskCount" + ".xml");
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
                updateTaskNotification();
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
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

    //PickTask list load the page
    class GetMoveTaskList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetMoveTaskList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_MOVETASK_LIST);
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
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_MOVETASK_LIST;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "MoveTaskList" + ".xml");
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new LoadMoveTaskList().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }

            } else if(result.equalsIgnoreCase("time out error")){
                new GetPickTaskList(mUsername).execute();
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(MainmenuActivity.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }
    class GetPickTaskList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetPickTaskList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PICKTASK_LIST);
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
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_PICKTASK_LIST;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "PickTaskList" + ".xml");
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new MainmenuActivity.LoadPickTaskList().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }

            } else if(result.equalsIgnoreCase("time out error")){
                new GetPickTaskList(mUsername).execute();
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(MainmenuActivity.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }

    private class LoadPickTaskList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadPickTaskList() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteTaskList();
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

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "PickTaskList" + ".xml");
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

                mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }

    private class LoadMoveTaskList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadMoveTaskList() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deleteMoveTaskList();
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

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "MoveTaskList" + ".xml");
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

                mSupporter.simpleNavigateTo(MoveTaskActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }


    private String GetErrorMessage(){

        String GetErrMsg ="";
        try
        {
            //creating a constructor of file class and parsing an XML file
            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "PickTaskList" + ".xml");
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

    //ReceiveTask list load the page
    class GetReceiveTaskList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetReceiveTaskList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting Receive Task Data..");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_RECEIVETASK_LIST);
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
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_RECEIVETASK_LIST;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "ReceiveTaskList" + ".xml");
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new LoadReceiveTaskList().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(MainmenuActivity.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }


    class GetSalesOrderList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetSalesOrderList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Getting Sales Order Data..");
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
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "SoCharges_List" + ".xml");
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new LoadSoChargesList().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(MainmenuActivity.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }

    private class LoadSoChargesList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadSoChargesList() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteSalesOrderList();
                mDbHelper.closeDatabase();

                if (compSize != 0) {
                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "SoCharges_List" + ".xml");
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


                new GetSoChargesCategoryList(mUsername).execute();

               // mSupporter.simpleNavigateTo(SalesOrderCharges.class);

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }


    private class LoadReceiveTaskList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadReceiveTaskList() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteReceiveTaskList();
                mDbHelper.closeDatabase();

                if (compSize != 0) {
                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "ReceiveTaskList" + ".xml");
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
                mSupporter.simpleNavigateTo(ReceiveTaskMenuActivity.class);

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }

    //PhyscialCount list load the page
    class GetPhysicalCountList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetPhysicalCountList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading...");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PHYSICALCOUNT_LIST);
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
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_PHYSICALCOUNT_LIST;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "PhysicalCountList" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                String resultValue = resultString.toString().substring(0, 100).toLowerCase();
                if (resultValue.contains("false")) {
                    result = "Failed";
                    if (resultValue.contains("already assigned to another user")) {
                        result = "Assigned another user";
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new LoadPhysicalCountList().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(MainmenuActivity.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }

    private class LoadPhysicalCountList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadPhysicalCountList() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();
                mDbHelper.openWritableDatabase();
                mDbHelper.deletePhysicalCountList();
                Globals.gPCDetailRowCount = 1;
                Globals.gPCSlotRowCount = 1;
                mDbHelper.closeDatabase();

                if (compSize != 0) {
                    startDBTransaction("db data loading"); // to start db transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "PhysicalCountList" + ".xml");
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

                mDbHelper.openWritableDatabase();
                slotDetailList = mDbHelper.getPhycialCountDetailUpdatedFlag();
                mDbHelper.closeDatabase();

                for (int i = 0;i < slotDetailList.size();i++){
                    mDbHelper.openWritableDatabase();
                    mDbHelper.updatedPhysicalCountUpdatedNo(String.valueOf(i + 1),slotDetailList.get(i).getitem(),slotDetailList.get(i).getlotrefid());
                    mDbHelper.closeDatabase();
                }


              // mSupporter.simpleNavigateTo(WorkingProgressActivity.class);
              mSupporter.simpleNavigateTo(PhysicalCountMenuActivity.class);
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }


    class GetSoChargesCategoryList extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetSoChargesCategoryList(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(MainmenuActivity.this);
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
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_SO_CHARGES_gategory_LIST);
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
                String soap_action = NAMESPACE + METHOD_GET_SO_CHARGES_gategory_LIST;
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
                if (mSupporter.isNetworkAvailable(MainmenuActivity.this)) {

                    new LoadSoChargesGategoryList().execute();

                } else {
                    mToastMessage.showToast(MainmenuActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }
            } else if (result.equals("Failed")) {
                mToastMessage.showToast(MainmenuActivity.this,
                        "No Data Found.");
            } else if (result.equals("Assinged another user")) {
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(MainmenuActivity.this,
                        Getmsg);
            } else {
                mToastMessage.showToast(MainmenuActivity.this,
                        "Invalid Process.");
            }
            dialog.cancel();
        }
    }

    private class LoadSoChargesGategoryList extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public LoadSoChargesGategoryList() {
            dialog = new ProgressDialog(MainmenuActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(MainmenuActivity.this, mDbHelper, Globals.gUsercode);

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

                mSupporter.simpleNavigateTo(SalesOrderCharges.class);

            } else if (result.equals("nosd")) {
                mToastMessage.showToast(MainmenuActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(MainmenuActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(MainmenuActivity.this, "File not available");
            } else {
                mToastMessage.showToast(MainmenuActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }



}
