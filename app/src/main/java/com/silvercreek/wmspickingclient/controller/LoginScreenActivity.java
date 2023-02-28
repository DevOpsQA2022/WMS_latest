package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.configsettings;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class LoginScreenActivity extends AppBaseActivity {

    private Button btnLogin;
    private Button btnRestLog;
    private EditText edtUsername;
    private EditText edtPassword;
    private WMSDbHelper mDbHelper;
    private Supporter mSupporter;
    private ToastMessage mToastMessage;
    private String sUsername;
    private String sPassword;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private String mDeviceId;
    private configsettings mConfigSettings;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_GET_COMPANY = "GetCompany";
    public static final String METHOD_VALIDATE_USER ="LoginRequest";
    public static final String METHOD_RESET_LOGIN_USER ="LoginReset";
    private SharedPreferences sharedpreferences;
    private File mImpOutputFile;
    private int mTimeout;
    String uName, mSessionId, uPass;
    private Context activity;

    String cedtNamespace ="";
    String cTimeOut = "";
    String cProtocol = "";
    String cServerPath ="";
    String cPortNumber = "";
    String cApplication = "";
    String cServiceName = "";
    private File sLogFile;
    private File sRootPath;
    private File vpo_common_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

         final String android_id = Settings.Secure.getString(LoginScreenActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);


        if (!checkPermission()) {
            requestPermission();
        }

        if (mDeviceId == null) {
            mDeviceId = Build.SERIAL;
            if (mDeviceId == null) {
                mDeviceId = "1234";
            }
        }
        mDeviceId=android_id;

        btnLogin=(Button) findViewById(R.id.btn_login);
        btnRestLog=(Button) findViewById(R.id.btn_restLog);
        edtUsername=findViewById(R.id.edtUsername);
        edtPassword=findViewById(R.id.edtPassword);
        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SOFT_KEYBOARD = sharedpreferences.getString("SoftKey", "");
        Globals.gDeviceId=mDeviceId;

        if (SOFT_KEYBOARD.equals("CHECKED")){

                edtUsername.setShowSoftInputOnFocus(false);
                edtPassword.setShowSoftInputOnFocus(false);
    }else {
                edtUsername.setShowSoftInputOnFocus(true);
                edtPassword.setShowSoftInputOnFocus(true);
        }



        sRootPath = Environment.getExternalStorageDirectory();
        vpo_common_path = new File(sRootPath.getAbsoluteFile() + "/" + "Android/WMSconfig");
        sLogFile = new File(vpo_common_path, "WMSPConfig.txt");

        if(vpo_common_path.exists() && sLogFile.exists())
        {
            StringBuilder stringBuilder = null;
            try {
                FileReader fileReader = new FileReader(sLogFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                stringBuilder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


            String responce = stringBuilder.toString();


            try {
                JSONObject jsonObject = new JSONObject(responce);

                cedtNamespace = jsonObject.get("Namespace").toString();
                cTimeOut = jsonObject.get("TimeOut").toString();
                cProtocol = jsonObject.get("Protocol").toString();
                cServerPath = jsonObject.get("ServerPath").toString();
                cPortNumber = jsonObject.get("PortNumber").toString();
                cApplication = jsonObject.get("ApplicationName").toString();
                cServiceName = jsonObject.get("ServiceName").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }



            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("Namespace", cedtNamespace);
            editor.putString("Timeout", cTimeOut);
            editor.putString("Protocol", cProtocol);
            editor.putString("Serverpath", cServerPath);
            editor.putString("Portnumber", cPortNumber);
            editor.putString("AppName", cApplication);
            editor.putString("Servicename", cServiceName);
            editor.commit();


        }


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




        btnRestLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sUsername = edtUsername.getText().toString().trim();
                sPassword = edtPassword.getText().toString().trim();

                mDbHelper.openReadableDatabase();
                mConfigSettings = mDbHelper.getApplicationConfiguration();
                mDbHelper.closeDatabase();

                if (sUsername.equals("")) {
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Please Enter Username ");
                    btnLogin.setEnabled(true);
                } else if (sPassword.equals("")) {
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Please Enter password");
                    btnLogin.setEnabled(true);
                } else if(TextUtils.isEmpty(URL_SERVER_PATH)){
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Please Enter Server Address in the Settings Screen");
                }else if (!Globals.UserNameForReset.equals(sUsername)){
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "User : "+sUsername+" does not have the access to Reset");
                    btnRestLog.setVisibility(View.GONE);
                }else {
                    uName = sUsername;
                    uPass = sPassword;
                    Globals.gServerpath = URL_SERVER_PATH;
                    Globals.gUsercode = uName;
                    if (mSupporter.isNetworkAvailable(LoginScreenActivity.this)) {
                        new ResetUserCredentials(sUsername, sPassword).execute();
                    }  else {
                        mToastMessage.showToast(LoginScreenActivity.this,
                                "Unable to connect with Server. Please Check your internet connection");
                    }

                }
            }
        });



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  mDeviceId=android_id;


                sUsername = edtUsername.getText().toString().trim();
                sPassword = edtPassword.getText().toString().trim();

                mDbHelper.openReadableDatabase();
                mConfigSettings = mDbHelper.getApplicationConfiguration();
                mDbHelper.closeDatabase();

                if (sUsername.equals("")) {
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Please Enter Username ");
                    btnLogin.setEnabled(true);
                } else if (sPassword.equals("")) {
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Please Enter password");
                    btnLogin.setEnabled(true);
                } else if(TextUtils.isEmpty(URL_SERVER_PATH)){
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Please Enter Server Address in the Settings Screen");
                } else {
                    uName = sUsername;
                    uPass = sPassword;
                    Globals.gServerpath = URL_SERVER_PATH;
                    Globals.gUsercode = uName;
                    if (mSupporter.isNetworkAvailable(LoginScreenActivity.this)) {
                        new ValidateUserCredentials(sUsername, sPassword).execute();
                    } else if (sUsername.equalsIgnoreCase(mConfigSettings.getUsername()) && sPassword.equalsIgnoreCase(mConfigSettings.getPassword())) {
                        mDbHelper.openReadableDatabase();
                        mSessionId = mDbHelper.mGetSessionId();
                        Globals.gSessionId=mSessionId;
                        mDbHelper.closeDatabase();
                        mSupporter.simpleNavigateTo(SelectCompanyActivity.class);
                    } else {
                        mToastMessage.showToast(LoginScreenActivity.this,
                                "Unable to connect with Server. Please Check your internet connection");
                    }

                }
            }
        });

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_NETWORK_STATE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_NETWORK_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                mSupporter.simpleNavigateTo(ConfigurationActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean externalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean accessNetwork = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (externalStorage && accessNetwork)
                        mToastMessage.showToast(LoginScreenActivity.this, "Permission Granted.");
                    else {
                        mToastMessage.showToast(LoginScreenActivity.this, "Permission Denied");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{ACCESS_NETWORK_STATE, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(LoginScreenActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    /*
    Validate User Credentials at runtime with Web service by service name "LoginRequest".
    */
    class ValidateUserCredentials extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode, uPass;

        public ValidateUserCredentials(String user, String pass) {
            this.uCode = user;
            this.uPass = pass;
            dialog = new ProgressDialog(LoginScreenActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Validating User Credentials");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_VALIDATE_USER);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pDeviceId");
                info.setValue(mDeviceId);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pPassword");
                info.setValue(uPass);
                info.setType(String.class);
                request.addProperty(info);
                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_VALIDATE_USER;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "UserCredentials" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());


                if (resultString.toString().contains("has already login with another device. Cannot be login.")) {

                    if (resultString.toString().contains("<WMSLoginResetPriv>Y</WMSLoginResetPriv>")){
                        result = "already login with another device(WMSLoginResetPriv>Y)";
                    }else if(resultString.toString().contains("<WMSLoginResetPriv>N</WMSLoginResetPriv>")){
                        result = "already login with another device(<WMSLoginResetPriv>N)";
                    }
                    //result = "has already login with another device. Cannot be login.";
                }

                else  if (resultString.toString().toLowerCase().contains("false")) {
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
                configsettings setting1 = new configsettings();
                setting1.setAppName(mSupporter.MobileAppName);
                setting1.setAppDesc(mSupporter.MobileAppDesc);
                setting1.setDeviceId(mDeviceId);
                setting1.setUsername(uName);
                setting1.setPassword(uPass);
                setting1.setAdminPassword(uPass);
                setting1.setSessionId(mSessionId);

                mDbHelper.openWritableDatabase();
                mDbHelper.deleteConfigSettings();
                mDbHelper.closeDatabase();
                mDbHelper.openWritableDatabase();
                mDbHelper.addConfigSettingsData(setting1);
                mDbHelper.closeDatabase();

                if (mSupporter.isNetworkAvailable(LoginScreenActivity.this)) {
                    new DataLoadToSessionID().execute();

                } else {
                    mToastMessage.showToast(LoginScreenActivity.this,
                            "Unable to connect with Server. Please check your internet connection");
                }

            } else if (result.equals("already login with another device(WMSLoginResetPriv>Y)")){
                mToastMessage.showToast(LoginScreenActivity.this,"User: "+ uCode+ " has already login with another device. Cannot be login.");
                btnRestLog.setVisibility(View.VISIBLE);
                Globals.UserNameForReset = uCode;
           // } else if (result.equals("has already login with another device. Cannot be login.")){
            } else if (result.equals("already login with another device(<WMSLoginResetPriv>N)")){
                mToastMessage.showToast(LoginScreenActivity.this,"User: "+ uCode+ " has already login with another device. Cannot be login.");
                btnRestLog.setVisibility(View.GONE);

            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Login failed invalid username or password");
                btnRestLog.setVisibility(View.GONE);
            }else if (result.equals("time out error")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "The connection to the server time out");
            }  else {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Invalid username or password");
                btnRestLog.setVisibility(View.GONE);
            }

            dialog.cancel();
        }

    }


    class ResetUserCredentials extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode, uPass;

        public ResetUserCredentials(String user, String pass) {
            this.uCode = user;
            this.uPass = pass;
            dialog = new ProgressDialog(LoginScreenActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Validating User Credentials");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_RESET_LOGIN_USER);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                PropertyInfo info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_RESET_LOGIN_USER;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "ResetUserCredentials" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());


                if (resultString.toString().contains("<Result>true</Result>") && resultString.toString().contains("Successfully Reset.")) {
                    result = "success";
                }
                else  if (resultString.toString().toLowerCase().contains("false")) {
                    result = "LoginFailed";
                } else {
                    result = "LoginFailed";
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
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Successfully Reset.");
                btnRestLog.setVisibility(View.GONE);
            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Unable to Reset.");

            }else if (result.equals("time out error")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "The connection to the server time out");
            }  else {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Unable to Reset.");
            }

            dialog.cancel();
        }

    }


    public static void hideKeyboard4(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void hideKeyboard1() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {

            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


        }

    }

    private void showKeyboard1() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    private class DataLoadToSessionID extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public DataLoadToSessionID() {
            dialog = new ProgressDialog(LoginScreenActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(LoginScreenActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                if (compSize != 0) {

                    startDBTransaction("db data loading"); // to start db
                    // transaction

                    for (int c = 0; c < compList.size(); c++) {

                        for (int i = 0; i < totImpFile; i++) {

                            String fileName = "Acknowledgement";

                            if ((c > 0) && (fileName.equals("Acknowledgement"))) {
                                continue; // to continue for other files
                            }

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "UserCredentials" + ".xml");
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

                        if (!result.equals("success")) { // to break from executing other companies
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
                new GetCompany(uName).execute();
            } else if (result.equals("nosd")) {
                mToastMessage.showToast(LoginScreenActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                mToastMessage.showToast(LoginScreenActivity.this, "Error during parsing the data");
            } else if (result.equals("File not available")) {
                mToastMessage.showToast(LoginScreenActivity.this, "File not available");
            } else {
                mToastMessage.showToast(LoginScreenActivity.this, "Error");
            }
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }
    }

    class GetCompany extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetCompany(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(LoginScreenActivity.this);
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
                Globals.gSessionId=mSessionId;
                mDbHelper.closeDatabase();
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_COMPANY);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId"); // .Net Funcation argument key
                info.setValue(mSessionId);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName"); // .Net Funcation argument key
                info.setValue(uCode);
                info.setType(String.class);
                request.addProperty(info);
                envelope.dotNet = true; // to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME,mTimeout);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_COMPANY;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "company" + ".xml");
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
                new DataLoadToCompanyTable().execute();

            } else if(result.equalsIgnoreCase("time out error")){
                new GetCompany(uName).execute();
            } else if (result.equals("LoginFailed")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Log In Failed Invalid Username or Password");
            } else if (result.equals("server failed")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Data server connection failed.");
            } else if (result.equals("No data")) {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "No data found.");
            } else {
                mToastMessage.showToast(LoginScreenActivity.this,
                        "Invalid Username or Password");
            }

            dialog.cancel();
        }

    }

/*
*
*
*/
private class DataLoadToCompanyTable extends
            AsyncTask<String, String, String> {

    private ProgressDialog dialog;

    public DataLoadToCompanyTable() {
        dialog = new ProgressDialog(LoginScreenActivity.this);
        dialog.setCancelable(false);
    }

    @Override
    protected String doInBackground(String... params) {

        String errMsg = "";

        try {

            String result = "";

            DataLoader fileLoader = new DataLoader(LoginScreenActivity.this, mDbHelper, Globals.gUsercode);

            List<String> importFileList = mSupporter.loadImportFileList();

            int totImpFile = importFileList.size();

            File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

            List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

            int compSize = compList.size();

            mDbHelper.openWritableDatabase();
            mDbHelper.deleteCompanyData();
            mDbHelper.closeDatabase();

            if (compSize != 0) {

                startDBTransaction("db data loading"); // to start db transaction

                for (int c = 0; c < compList.size(); c++) {

                    for (int i = 0; i < totImpFile; i++) {

                        String fileName = "Document";

                        if ((c > 0) && (fileName.equals("Document"))) {
                            continue; // to continue for other files
                        }

                        mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "Company" + ".xml");
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

                    if (!result.equals("success")) { // to break from executing other companies

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
            mSupporter.simpleNavigateTo(SelectCompanyActivity.class);

        } else if (result.equals("nosd")) {
            mToastMessage.showToast(LoginScreenActivity.this, "Sd card required");
        } else if (result.equals("parsing error")) {
            mToastMessage.showToast(LoginScreenActivity.this, "Error during parsing the data");
        } else if (result.equals("File not available")) {
            mToastMessage.showToast(LoginScreenActivity.this, "File not available");
        } else {
            mToastMessage.showToast(LoginScreenActivity.this, "Error");
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
        mDbHelper.mSetTransactionSuccess(); // setting the successfull transaction
        Log.i("Transaction success", "Transaction success.");
        mDbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        mDbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
