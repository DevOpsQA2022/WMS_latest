package com.silvercreek.wmspickingclient.util;

import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogfileCreator {
    public static void mAppendLog(String sErrorMsg) {

        File sRootPath = Environment.getExternalStorageDirectory();
        File sAppPath = new File(sRootPath.getAbsoluteFile() + "/" + "Android/WMS");
        File sLogFile =  new File(sAppPath, "WMSPClog.txt");

        SimpleDateFormat sdf;
        String sCurrentTime;
        if(!sLogFile.exists()){
            try {
                sLogFile.createNewFile();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        try{
            sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            sCurrentTime = sdf.format(new Date());
            BufferedWriter buf = new BufferedWriter(new FileWriter(sLogFile,
                        true));
            buf.append(sCurrentTime + ":- " + sErrorMsg);
            System.out.println("Text Write to file:" + sErrorMsg);
            buf.newLine();
            buf.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
