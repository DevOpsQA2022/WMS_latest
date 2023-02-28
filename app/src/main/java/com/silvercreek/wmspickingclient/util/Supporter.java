package com.silvercreek.wmspickingclient.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Environment;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Supporter {
    private Context context;
    private Activity curActivity;
    private Cursor cursor;
    private WMSDbHelper dbHelper;
    public static String NAMESPACE;
    public static int TIMEOUT;
    public static String PROTOCOL;
    public static String SERVER_PATH;
    public static int PORT_NUMBER;
    public static String APPLICATION_NAME;
    public static String SERVICE_NAME;
    public static Boolean SUMQTY=false;
    private String mDeviceId;
    private SharedPreferences publicData;
    private SharedPreferences commonData;
    private SharedPreferences deliveryData;
    private SharedPreferences returnData;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String MobileAppName = "WMS Picking Client" ;
    public static final String MobileAppDesc = "WMS Picking Client" ;

    public Supporter(Activity activity, WMSDbHelper pdbHelper) {
        curActivity = activity;
        dbHelper = pdbHelper;

            publicData = curActivity.getSharedPreferences("publicData",
                    Context.MODE_PRIVATE);
            commonData = curActivity.getSharedPreferences("commonData",
                    Context.MODE_PRIVATE);
            deliveryData = curActivity.getSharedPreferences("deliveryData",
                    Context.MODE_PRIVATE);
            returnData = curActivity.getSharedPreferences("returnData",
                    Context.MODE_PRIVATE);

    }

    public Supporter(Context pContext)
    {
        WMSDbHelper temporaryDatabaseHelper = new WMSDbHelper(pContext);
        this.dbHelper = temporaryDatabaseHelper;

        publicData = pContext.getSharedPreferences("publicData", Context.MODE_PRIVATE);
        commonData = pContext.getSharedPreferences("commonData", Context.MODE_PRIVATE);
        deliveryData = pContext.getSharedPreferences("deliveryData", Context.MODE_PRIVATE);
        returnData = pContext.getSharedPreferences("returnData", Context.MODE_PRIVATE);
    }

    // to get application import location folder
    public static File getImportFolderPath(String importFolderName) {

        File importPath = null;

        if (isSdPresent()) {
            File commonPath = getAppCommonPath();

            importPath = new File(commonPath, importFolderName);

            if (!importPath.exists()) {
                importPath.mkdirs();
            }
        }

        return importPath;
    }

    // to check sdcard availability
    public static boolean isSdPresent() {

        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);

    }

    public static File getAppCommonPath() {

        File file = null;

        if (isSdPresent()) {
            File root_path = Environment.getExternalStorageDirectory();

            File vpo_common_path = new File(root_path.getAbsoluteFile() + "/"
                    + "Android/WMS");

            if (!vpo_common_path.exists()) {
                vpo_common_path.mkdirs();
            }

            file = vpo_common_path;
        }

        return file;

    }

    public static File getImpOutputFilePathByCompany(String spCode,
                                                     String serCompCode, String fileName) {
        File imp_folder_path = Supporter.getImportFolderPath(spCode);

        File impOutputCompPath = new File(imp_folder_path, serCompCode);

        if (!impOutputCompPath.exists()) {
            impOutputCompPath.mkdirs();
        }

        File impOutputFile = new File(impOutputCompPath, fileName);

        return impOutputFile;
    }

    public static String GetSoftKeyStatus(String spCode) {


        return "";
    }


    public File createCommonPath() {
        File root_path = Environment.getExternalStorageDirectory();

        File msp_common_path = new File(root_path.getAbsoluteFile() + "/"
                + "Android/WMS");

        if (!msp_common_path.exists()) {
            msp_common_path.mkdirs();
        }
        return msp_common_path;
    }

    // to get application import location folder
    public File getImportFolderPath(String importFolderName, Context context) {

        File importPath;
        File commonPath = createCommonPath();

        importPath = new File(commonPath, importFolderName);

        if (!importPath.exists()) {
            importPath.mkdirs();
        }
        return importPath;
    }

    public File getImpOutputFilePath(String pUsername, String pFileName, Context context) {
        File imp_folder_path = getImportFolderPath(pUsername, context);

        if (!imp_folder_path.exists()) {
            imp_folder_path.mkdirs();
        }

        File impOutputFile = new File(imp_folder_path, pFileName);

        return impOutputFile;
    }

    // to get application common working folder
    public File getAppCommonPath(Context context) {

        File file;
        File root_path = context.getFilesDir();

        File miscs_common_path = new File(root_path.getAbsoluteFile() + "/");

        if (!miscs_common_path.exists()) {
            miscs_common_path.mkdirs();
        }

        file = miscs_common_path;

        return file;

    }

    public List<String> loadImportFileList() {
        List<String> listContent2 = new ArrayList<String>();

        listContent2.add("PO");
        //listContent2.add("CompanyList");
        //listContent2.add("AppConfig");

        return listContent2;
    }

    public List<String> getFolderNames(File myDir) {

        List<String> fileNames = new ArrayList<String>();
        for (File f : myDir.listFiles()) {
            if (f.isDirectory()) {
                String name = f.getName();
                fileNames.add(name);
            }

        }

        return fileNames;
    }

    public static void createFile(File outputFile) {
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    // method to help navigation
    public void simpleNavigateTo(Class<?> cls) {
        Intent intent = new Intent(curActivity, cls);
        curActivity.startActivity(intent);
    }

    // to clear shared Preference
    public void clearCommonPreference() {

        SharedPreferences.Editor edtComDate = commonData.edit();
        edtComDate.clear();
        edtComDate.commit();

    }

    // to set LogedIn permission
    public void setLogedIn(boolean isLogedIn) {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putBoolean("isLogedIn", isLogedIn);
        ed.commit();
    }

    // to get LogedIn permission
    public boolean isLogedIn() {
        boolean isLogedIn = false;
        isLogedIn = commonData.getBoolean("isLogedIn", false);
        return isLogedIn;
    }

    public void setNamespace(String pNamespace)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("Namespace",pNamespace);
        ed.commit();
    }

    public String getNamespace()
    {
        String namespace = "";
        namespace = commonData.getString("Namespace","");
        return namespace;
    }

    public void setTimeout(String pTimeout)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("Timeout",pTimeout);
        ed.commit();
    }

    public String getTimeout()
    {
        String timeout = "";
        timeout = commonData.getString("Timeout","");
        return timeout;
    }

    public void setProtocol(String pProtocol)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("Protocol",pProtocol);
        ed.commit();
    }

    public String getProtocol()
    {
        String protocol = "";
        protocol = commonData.getString("Protocol","");
        return protocol;
    }

    public void setServerPath(String pServerPath){
        SharedPreferences.Editor editor = commonData.edit();
        editor.putString("ServerPath", pServerPath);
        editor.commit();
    }

    public String getServerPath(){
        String serverPath = "";
        serverPath = commonData.getString("ServerPath", "");
        return serverPath;
    }

    public void setPortnumber(String pPortNumber)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("Portnumber",pPortNumber);
        ed.commit();
    }

    public String getPortnumber()
    {
        String portnumber = "";
        portnumber = commonData.getString("Portnumber","");
        return portnumber;
    }

    public void setAppName(String pAppName)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("AppName",pAppName);
        ed.commit();
    }

    public String getAppName()
    {
        String appname = "";
        appname = commonData.getString("AppName","");
        return appname;
    }

    public void setServiceName(String pServiceName)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("ServiceName",pServiceName);
        ed.commit();
    }

    public String getServiceName()
    {
        String servicename = "";
        servicename = commonData.getString("ServiceName","");
        return servicename;
    }

    public void setUsername(String pUsername)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("Username",pUsername);
        ed.commit();
    }

    public String getUsername()
    {
        String username = "";
        username = commonData.getString("Username","");
        return username;
    }

    public String getDeviceId()
    {
        String deviceid = "";
        deviceid = commonData.getString("DeviceId","");
        return deviceid;
    }

    public void setDeviceId(String pDeviceId)
    {
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("DeviceId",pDeviceId);
        ed.commit();
    }



    public String getDateFormat() {
        String dFormat = commonData.getString("dateFormat", "MM/dd/yyyy");
        return dFormat;
    }

    public String getDecimalFormat() {
        String dFormat = "";
        dFormat = commonData.getString("decimalFormat", "2");
        return dFormat;
    }

    public void setSessionId(String pSessionId)	{
        SharedPreferences.Editor ed = commonData.edit();
        ed.putString("SessionId",pSessionId);
        ed.commit();
    }

    public String getSessionId()	{
        String sessionId = "";
        sessionId = commonData.getString("SessionId","");
        return sessionId;
    }

}
