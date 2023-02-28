package com.silvercreek.wmspickingclient.controller;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.activity.result.ActivityResult;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.util.ToastMessage;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import org.json.JSONException;
import org.json.JSONObject;

public class ConfigurationActivity extends AppCompatActivity {

    private Button btnCheck, btnSave, btnClose, btnEdit;
    private EditText edtNamespace, edtTimeout, edtProtocol, edtServerpath, edtPortNumber, edtAppName, edtServiceName;
    private ToastMessage mToastMessage;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private SharedPreferences sharedpreferences;
    private Boolean isEnabled;
    private String path;
    private CheckBox Chk_softKey;
    JSONObject jsonObject;
    String namespace, timeout, protocol, serverpath, portnumber, appname, servicename,ischeckedSoftKey;
    private File sRootPath;
    private Button Btn_CopyFrom;
    String cedtNamespace ="";
    String cTimeOut = "";
    String cProtocol = "";
    String cServerPath ="";
    String cPortNumber = "";
    String cApplication = "";
    String cServiceName = "";
    public static String SOFT_KEYBOARD = "";
    private File sLogFile;
    private File vpo_common_path;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        btnCheck=findViewById(R.id.btnCheck);
        btnSave=findViewById(R.id.btnSave);
        btnClose=findViewById(R.id.btnClose);
        btnEdit=findViewById(R.id.btnEdit);
        Chk_softKey=findViewById(R.id.Chk_softKey);

        edtNamespace=findViewById(R.id.edtNamespace);
        edtTimeout=findViewById(R.id.edtTimeout);
        edtProtocol=findViewById(R.id.edtProtocol);
        edtServerpath=findViewById(R.id.edtServerPath);
        edtPortNumber=findViewById(R.id.edtPortNumber);
        edtAppName=findViewById(R.id.edtAppName);
        edtServiceName=findViewById(R.id.edtServiceName);
        Btn_CopyFrom=findViewById(R.id.btn_cpyFrom);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SOFT_KEYBOARD = sharedpreferences.getString("SoftKey", "");
        LoadPreferences();

        isEnabled = false;
        ChangeEditable(isEnabled);


        if (SOFT_KEYBOARD.equals("CHECKED")){

            edtNamespace.setShowSoftInputOnFocus(false);
            edtTimeout.setShowSoftInputOnFocus(false);
            edtProtocol.setShowSoftInputOnFocus(false);
            edtServerpath.setShowSoftInputOnFocus(false);
            edtPortNumber.setShowSoftInputOnFocus(false);
            edtAppName.setShowSoftInputOnFocus(false);
            edtServiceName.setShowSoftInputOnFocus(false);
        }else {
            edtNamespace.setShowSoftInputOnFocus(true);
            edtTimeout.setShowSoftInputOnFocus(true);
            edtProtocol.setShowSoftInputOnFocus(true);
            edtServerpath.setShowSoftInputOnFocus(true);
            edtPortNumber.setShowSoftInputOnFocus(true);
            edtAppName.setShowSoftInputOnFocus(true);
            edtServiceName.setShowSoftInputOnFocus(true);

        }



        //  KJson();


        jsonObject = new JSONObject();
        try {

            //jsonObject.put("Namespace","http://silvercreek.com/WMSPickingClient");
            jsonObject.put("Namespace","");
            jsonObject.put("TimeOut", "50000" );
            jsonObject.put("Protocol", "http://");
            jsonObject.put("ServerPath", "");
            jsonObject.put("PortNumber", "80");
            jsonObject.put("ApplicationName", "");
            jsonObject.put("ServiceName", "WMSPickingClient.asmx");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //  return jsonObject;

         sRootPath = Environment.getExternalStorageDirectory();


         vpo_common_path = new File(sRootPath.getAbsoluteFile() + "/" + "Android/WMSconfig");
        if (!vpo_common_path.exists())
        {
            vpo_common_path.mkdirs();
        }
         sLogFile =  new File(vpo_common_path, "WMSPConfig.txt");
        if (!sLogFile.exists())
        {
            try {
                sLogFile.createNewFile();
                String userString = jsonObject.toString();
                String userString1 =  userString.replace("\\/","/");
                File file = new File(String.valueOf(sLogFile));
                FileWriter fileWriter = null;

                try {
                    fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    bufferedWriter.write(userString1);
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //getConfigFromJson
        if (!sLogFile.exists() || !vpo_common_path.exists()) {
            mToastMessage.showToast(ConfigurationActivity.this, "Config file not found");
        } else {

            vpo_common_path = new File(sRootPath.getAbsoluteFile() + "/" + "Android/WMSconfig");
            if (!vpo_common_path.exists()) {
                vpo_common_path.mkdirs();
            }
            sLogFile = new File(vpo_common_path, "WMSPConfig.txt");

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


            /*if (Chk_softKey.isChecked()){
                ischeckedSoftKey = "CHECKED";
            }else {
                ischeckedSoftKey = "UNCHECKED";
            }*/

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("Namespace", cedtNamespace);
            editor.putString("Timeout", cTimeOut);
            editor.putString("Protocol", cProtocol);
            editor.putString("Serverpath", cServerPath);
            editor.putString("Portnumber", cPortNumber);
            editor.putString("AppName", cApplication);
            editor.putString("Servicename", cServiceName);
          //  editor.putString("SoftKey", ischeckedSoftKey);
            editor.commit();

            edtNamespace.setText(cedtNamespace);
            edtTimeout.setText(cTimeOut);
            edtProtocol.setText(cProtocol);
            edtServerpath.setText(cServerPath);
            edtPortNumber.setText(cPortNumber);
            edtAppName.setText(cApplication);
            edtServiceName.setText(cServiceName);
        }



        Chk_softKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToastMessage.showToast(ConfigurationActivity.this,"Please save the configuration settings.");
            }
        });


        Btn_CopyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(
                        ConfigurationActivity.this,
                        Manifest.permission
                                .READ_EXTERNAL_STORAGE)
                        != PackageManager
                        .PERMISSION_GRANTED) {
                    // When permission is not granted
                    // Result permission
                    ActivityCompat.requestPermissions(
                            ConfigurationActivity.this,
                            new String[] {
                                    Manifest.permission
                                            .READ_EXTERNAL_STORAGE },
                            1);
                }
                else {
                    // When permission is granted
                    // Create method
                    selectPDF();
                }
            }
        });


        // Initialize result launcher
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts
                        .StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(
                            ActivityResult result)
                    {
                        // Initialize result data
                        Intent data = result.getData();
                        // check condition
                        if (data != null) {
                            // When data is not equal to empty
                            // Get PDf uri
                            Uri sUri = data.getData();

//                            // Get PDF path
//                            String sPath = sUri.getPath();
//                           /* if (sPath.contains("WMSPConfi")){*/
//                                String s1 = "";
//                            if (sPath.contains("raw")) {
//                                String[] separated = sPath.split(":");
//                                s1 = separated[1];
//                            } else {
//                                String[] separated = sPath.split(":");
//                                s1 = "/storage/emulated/0/" + separated[1];
//                            }
//
//                            /*if (s1.contains("WMSPConfig.txt")) {*/
                                StringBuilder stringBuilder = null;
//
//                                sLogFile = new File(s1);

                                try {
                                    InputStream is = getContentResolver().openInputStream(sUri);
                                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

//                                    FileReader fileReader = new FileReader(sLogFile);
//                                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                                    stringBuilder = new StringBuilder();
                                    String line = bufferedReader.readLine();
                                    while (line != null) {
                                        stringBuilder.append(line).append("\n");
                                        line = bufferedReader.readLine();
                                    }
                                    bufferedReader.close();

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
                                        mToastMessage.showToast(ConfigurationActivity.this,"Invalid data format");
                                    }

                                    SharedPreferences.Editor editor = sharedpreferences.edit();

                                    editor.putString("Namespace", cedtNamespace);
                                    editor.putString("Timeout", cTimeOut);
                                    editor.putString("Protocol", cProtocol);
                                    editor.putString("Serverpath", cServerPath);
                                    editor.putString("Portnumber", cPortNumber);
                                    editor.putString("AppName", cApplication);
                                    editor.putString("Servicename", cServiceName);
                                    //  editor.putString("SoftKey", ischeckedSoftKey);
                                    editor.commit();

                                    edtNamespace.setText(cedtNamespace);
                                    edtTimeout.setText(cTimeOut);
                                    edtProtocol.setText(cProtocol);
                                    edtServerpath.setText(cServerPath);
                                    edtPortNumber.setText(cPortNumber);
                                    edtAppName.setText(cApplication);
                                    edtServiceName.setText(cServiceName);

                                } catch (FileNotFoundException f) {
                                    f.printStackTrace();
                                    mToastMessage.showToast(ConfigurationActivity.this,"Invalid file location");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    mToastMessage.showToast(ConfigurationActivity.this,"Invalid data format");
                                }
                            /*}else {
                                mToastMessage.showToast(ConfigurationActivity.this,"Invalid file name");
                            }*/

                        /*}else {
                                mToastMessage.showToast(ConfigurationActivity.this,"Invalid file location");
                            }*/
                        }



                    }

                });





/*
        Btn_CopyFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!sLogFile.exists() || !vpo_common_path.exists()) {
                    mToastMessage.showToast(ConfigurationActivity.this, "Config file not found");
                } else {

                    vpo_common_path = new File(sRootPath.getAbsoluteFile() + "/" + "Android/WMSconfig");
                    if (!vpo_common_path.exists()) {
                        vpo_common_path.mkdirs();
                    }
                    sLogFile = new File(vpo_common_path, "WMSPConfig.txt");

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

                    edtNamespace.setText(cedtNamespace);
                    edtTimeout.setText(cTimeOut);
                    edtProtocol.setText(cProtocol);
                    edtServerpath.setText(cServerPath);
                    edtPortNumber.setText(cPortNumber);
                    edtAppName.setText(cApplication);
                    edtServiceName.setText(cServiceName);
                }

            }
        });
*/

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled) {
                    mSupporter.simpleNavigateTo(LoginScreenActivity.class);

                    String strResult = checkIp();
                    if (strResult.equals("Success")) {
                        String path = serverpath;
                        Intent intent = new Intent(ConfigurationActivity.this,
                                WebViewActivity.class);
                        intent.putExtra("IP Address", path);
                        startActivity(intent);
                    } else {
                        mToastMessage.showToast(ConfigurationActivity.this,
                                "Enter Valid IP Address...");
                    }
                } else {
                    mToastMessage.showToast(ConfigurationActivity.this, "Please save the configuration settings.");
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                namespace = edtNamespace.getText().toString();
                timeout = edtTimeout.getText().toString();
                protocol = edtProtocol.getText().toString();
                serverpath = edtServerpath.getText().toString();
                portnumber = edtPortNumber.getText().toString();
                appname = edtAppName.getText().toString();
                servicename = edtServiceName.getText().toString();
                if (Chk_softKey.isChecked()){
                    ischeckedSoftKey = "CHECKED";
                }else {
                    ischeckedSoftKey = "UNCHECKED";
                }

                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString("Namespace", namespace);
                editor.putString("Timeout", timeout);
                editor.putString("Protocol", protocol);
                editor.putString("Serverpath", serverpath);
                editor.putString("Portnumber", portnumber);
                editor.putString("AppName", appname);
                editor.putString("Servicename", servicename);
                editor.putString("SoftKey", ischeckedSoftKey);
                editor.commit();
                isEnabled = false;
                ChangeEditable(isEnabled);


                jsonObject = new JSONObject();
                try {
                    jsonObject.put("Namespace",namespace);
                    jsonObject.put("TimeOut", timeout );
                    jsonObject.put("Protocol", protocol);
                    jsonObject.put("ServerPath", serverpath);
                    jsonObject.put("PortNumber", portnumber);
                    jsonObject.put("ApplicationName", appname);
                    jsonObject.put("ServiceName", servicename);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                sRootPath = Environment.getExternalStorageDirectory();


                File vpo_common_path = new File(sRootPath.getAbsoluteFile() + "/" + "Android/WMSconfig");
                if (!vpo_common_path.exists())
                {
                    vpo_common_path.mkdirs();
                }
                File sLogFile =  new File(vpo_common_path, "WMSPConfig.txt");
               /* if (!protocol.equals("")&&!namespace.equals("")&&!timeout.equals("")&&!serverpath.equals("")&&!portnumber.equals("")&&!appname.equals("")&&!servicename.equals(""))
                {*/
                    try {
                        sLogFile.createNewFile();
                        String userString = jsonObject.toString();
                        String userString1 =  userString.replace("\\/","/");
                        File file = new File(String.valueOf(sLogFile));
                        FileWriter fileWriter = null;

                        try {
                            fileWriter = new FileWriter(file);
                            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                            bufferedWriter.write(userString1);
                            bufferedWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                /*}*/

                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);

            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEnabled) {
                    mSupporter.simpleNavigateTo(LoginScreenActivity.class);
                } else {
                    cancelAlert();
                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isEnabled = true;
                ChangeEditable(isEnabled);
            } 
        });
    }

    private void ChangeEditable(Boolean isEnabled) {
        edtNamespace.setEnabled(isEnabled);
        edtTimeout.setEnabled(isEnabled);
        edtProtocol.setEnabled(isEnabled);
        edtServerpath.setEnabled(isEnabled);
        edtPortNumber.setEnabled(isEnabled);
        edtAppName.setEnabled(isEnabled);
        edtServiceName.setEnabled(isEnabled);
        Chk_softKey.setEnabled(isEnabled);
       // Btn_CopyFrom.setEnabled(isEnabled);
    }


    private JSONObject KJson(){


         jsonObject = new JSONObject();
        try {
            jsonObject.put("Namespace","http://silvercreek.com/WMSPickingClient");
            jsonObject.put("TimeOut", "50000" );
            jsonObject.put("Protocol", "http://");
            jsonObject.put("ServerPath", "67.60.17.205");
            jsonObject.put("PortNumber", "80");
            jsonObject.put("ApplicationName", "APNWMSWS");
            jsonObject.put("ServiceName", "WMSPickingClient.asmx");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject;


    }


    private void selectPDF()
    {
        // Initialize intent
        Intent intent
                = new Intent(Intent.ACTION_GET_CONTENT);
        // set type
//        intent.setType("*/*");
        intent.setType("text/plain");
       // intent.setType("application/pdf");
        // Launch intent
        resultLauncher.launch(intent);
    }

    private void LoadPreferences() {
        String namespace = sharedpreferences.getString("Namespace", "");
        String timeout = sharedpreferences.getString("Timeout", "");
        String protocol = sharedpreferences.getString("Protocol", "");
        String serverpath = sharedpreferences.getString("Serverpath", "");
        String portnumebr = sharedpreferences.getString("Portnumber", "");
        String appname = sharedpreferences.getString("AppName", "");
        String servicename = sharedpreferences.getString("Servicename", "");
        String ischeckedSoftkey = sharedpreferences.getString("SoftKey", "");

        edtNamespace.setText(namespace);
        edtTimeout.setText(timeout);
        edtProtocol.setText(protocol);
        edtServerpath.setText(serverpath);
        edtPortNumber.setText(portnumebr);
        edtAppName.setText(appname);
        edtServiceName.setText(servicename);
        if (ischeckedSoftkey.equals("CHECKED")){
            Chk_softKey.setChecked(true);
        }else {
            Chk_softKey.setChecked(false);
        }
    }

    protected String checkIp() {
        String result = "";
        path = sharedpreferences.getString("Serverpath", "");
        serverpath=path;
        final String PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        Pattern pattern = Pattern.compile(PATTERN);
        Matcher matcher = pattern.matcher(path);
        boolean IPcheck = matcher.matches();

        final String PATTERN2 = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\:" + "([0-9]+)";

        Pattern pattern2 = Pattern.compile(PATTERN2);
        Matcher matcher2 = pattern2.matcher(path);
        boolean IPcheck2 = matcher2.matches();

        if (IPcheck) {
            result = "Success";
        } else if (IPcheck2) {
            result = "Success";
        } else {
            result = "Fail";
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        if (!isEnabled) {
            mSupporter.simpleNavigateTo(LoginScreenActivity.class);
        } else {
            cancelAlert();
        }
    }

    public void cancelAlert(){
        AlertDialog.Builder alertUser = new AlertDialog.Builder(this);
        alertUser.setTitle("Confirmation");
        alertUser.setIcon(R.drawable.warning);
        alertUser.setCancelable(false);
        alertUser.setMessage("Are you sure you want to cancel the update?");
        alertUser.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
}
