package com.silvercreek.wmspickingclient.util;

import java.*;
import java.util.HashMap;
import java.util.Hashtable;

public class Globals {
    public static String gUsercode = "";
    public static String gPassword = "";
    public static String gServerpath = "";
    public static String gCompanyId = "";
    public static String gCompanyName = "";
    public static String gCompanyDatabase = "";
    public static String gSessionId = "";
    public static String gDeviceId = "";
    public static String gNamespace = "";
    public static String gProtocol = "";
    public static String gServicename = "";
    public static String gAppName = "";
    public static String gTimeout = "";
    public static String gLoctid = "";
    public static Boolean gIsFromWarehouseSelection = false;
    public static Boolean fromExportData = false;

    public static String gLotno = "";
    public static String gStgslot = "";
    public static String gTaskNo = "";
    public static String gStop = "";
    public static String gRoute = "";
    public static String gStatus = "";
    public static String gPickTaskPallet = "";
    public static String gPickTaskWlotno = "";
    public static String gPickTaskItem = "";
    public static String gPickTaskSlot = "";
    public static String gPickTaskQty = "";
    public static Boolean isNewItem = false;
    public static Boolean isNewWlotno = false;
    public static String gCatchwt = "";
    public static String gLineSplit = "";
    public static String gPkTime = "";
    public static long gStartPickDuration = 0;
    public static long gEndPickDuration = 0;
    public static Double gTqty = 0.0;
    public static int gTranlineno = 0;
    public static int gPTDetailRowCount = 1;
    public static int gDoclineno = 0;
    public static Boolean gSetActivePallet = false;
    public static String gDecnum = "";
    public static String editItemNum="",editTranNum="",editSlot="";
    public static double editDiffQty=0.0;
    public static double editQty1=0.0;
    public static double editQty2=0.0;
    public static String holdTaskNum="";
    public static String TOSLOT="";

    public static String gLPPWorkDate = "";
    public static String gLPPPallet = "";
    public static String gLPPRoute = "";
    public static String gLPPDock = "";
    public static String gLPPTruck = "";
    public static String gLPPTaskNo = "";
    public static String gLPPTrailer = "";
    public static int gLPPDetailRowCount = 1;
    public static int gLPPSummaryRowCount = 1;

    public static String gRTTaskNo = "";
    public static String gRTSlot = "";
    public static String gRTDocType = "";
    public static String gRTDocNo = "";
    public static int gRTDetailRowCount = 1;
    public static String gRTItem = "";
    public static String gRTItmDesc = "";
    public static String gRTQtyrec = "";
    public static String gRTSelectedLoad = "";
    public static int gRTTranlineno = 0;
    public static String gRTTranline = "";
    public static String gRTTrancancel = "";
    public static int gRTPalline = 0;
    public static String gRTPrintpaltag = "0";
    public static String gRTWMSDate = "";
    public static String gRTSTATUS = "";
    public static String holdTask = "";
    public static String holdTranNum = "";
    public static boolean ISHold = false;
    public static boolean FROMHOLD = false;

    public static String gPCSlot = "";
    public static int gPCDetailRowCount = 1;
    public static int gPCSlotRowCount = 1;
    public static String gPCWlotno = "";
    public static String gPCUOM = "";
    public static String gPCDetailDoclineno = "";
    public static Boolean gPCSurpriseAdd = false;
    public static String gPCItem = "";
    public static int listCurrentPosition = 0;

    public static String mmTSlot = "";
    public static String mmTlot = "";
    public static String mmTUOM = "";
    public static String mmTItem = "";
    public static String mmTLotRefid = "";
    public static String mmTWLotNo = "";



    public static Boolean isQtySaved=false;
    public static Boolean FROMSHOW=false;
    public static Boolean FROMHIDE=false;
    public static Boolean SETCHECKEDSHOW=false;
    public static Boolean SETCHECKEDHIDE=false;
    public static Boolean FROMBTNDONE=false;
    public static Boolean FROMBTNCANCEL=false;
    public static Boolean FROMMENULIST=false;
    public static Boolean DATAREVERTED=false;
    /*public static Boolean FROMSERVER =false;*/

    public static String LookUp_Slot = "";
    public static String LookUp_SlotType = "";

    //MoveTask
    public static String gMTTaskNo = "";
    public static String gMTStatus = "";
    public static String gMTTaskType = "";
    public static String gMTRowPrty = "";
    public static String gMTItem = "";
    public static String gMTItmDesc = "";
    public static String gMTQtyrq = "";
    public static String gMTTranline = "";
    public static String gMTPalNO = "";
    public static String gMTLoctId = "";
    public static String gMTFromSlot = "";


    public static String gForHoldTesting = "";



    public static Boolean OkPressed = false;
    public static Boolean AVAILINGREADIENTS = false;
    public static String RecallLstDTE_code="";
    public static String RecallLstDTE_codeInc="";
    public static String RecallLstDTE_codeFin="";
    public static String RecallLstDTE_codeFinish="";
    //public static String RecallLstDTE_code="";
    public static String orgPano ="";
    public static String orgPanofin ="";
    public static Boolean FromRaw = false;
    public static String RepackNum = "";
    public static Boolean FromFinish = false;
    public static String UserNameForReset = "";
    public static Hashtable<String,String> chargelistHash = null;


    public static Boolean FROM_EDTREPACK = false;
    public static Boolean isLocalData = false;












}


