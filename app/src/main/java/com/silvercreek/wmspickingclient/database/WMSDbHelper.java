package com.silvercreek.wmspickingclient.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.silvercreek.wmspickingclient.model.BreakerUOMList;
import com.silvercreek.wmspickingclient.model.BreakerUomUtility;
import com.silvercreek.wmspickingclient.model.MoveManually;
import com.silvercreek.wmspickingclient.model.MoveManuallyTransaction;
//import com.silvercreek.wmspickingclient.model.Movetasklist;
import com.silvercreek.wmspickingclient.model.MoveTaskSlotList;
import com.silvercreek.wmspickingclient.model.Movetasklist;
import com.silvercreek.wmspickingclient.model.PickTaskScanPallet;
import com.silvercreek.wmspickingclient.model.PrintPalletTag;
import com.silvercreek.wmspickingclient.model.RepackFG;
import com.silvercreek.wmspickingclient.model.RepackIngredients;
import com.silvercreek.wmspickingclient.model.RepackList;
import com.silvercreek.wmspickingclient.model.ResetItemDetails;
import com.silvercreek.wmspickingclient.model.SalesOrderCategoryList;
import com.silvercreek.wmspickingclient.model.SalesOrderPickList;
import com.silvercreek.wmspickingclient.model.SlotList;
import com.silvercreek.wmspickingclient.model.company;
import com.silvercreek.wmspickingclient.model.configsettings;
import com.silvercreek.wmspickingclient.model.loadpickpalletDetails;
import com.silvercreek.wmspickingclient.model.loadpickpalletRouteDetails;
import com.silvercreek.wmspickingclient.model.loadpickpalletSummary;
import com.silvercreek.wmspickingclient.model.loadpickpalletWHIPLT;
import com.silvercreek.wmspickingclient.model.loadpickpalletWHITRL;
import com.silvercreek.wmspickingclient.model.location;
import com.silvercreek.wmspickingclient.model.menulist;
import com.silvercreek.wmspickingclient.model.movetaskdetail;
import com.silvercreek.wmspickingclient.model.notificationcount;
import com.silvercreek.wmspickingclient.model.physicalcountDetail;
import com.silvercreek.wmspickingclient.model.physicalcountICITEM;
import com.silvercreek.wmspickingclient.model.physicalcountSlot;
import com.silvercreek.wmspickingclient.model.physicalcountUom;
import com.silvercreek.wmspickingclient.model.physicalcountWHMLOT;
import com.silvercreek.wmspickingclient.model.physicalcountWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHITRLS;
import com.silvercreek.wmspickingclient.model.picktaskWHMLOT;
import com.silvercreek.wmspickingclient.model.picktaskWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskWHMSLT;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktaskheader;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.model.receivetaskWHMSLT;
import com.silvercreek.wmspickingclient.model.receivetaskWHRPLT;
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetaskexportdetail;
import com.silvercreek.wmspickingclient.model.receivetaskheader;
import com.silvercreek.wmspickingclient.model.receivetaskitemclass;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.model.receivetaskloadtype;
import com.silvercreek.wmspickingclient.model.receivetaskprintdetail;
import com.silvercreek.wmspickingclient.model.sessiondetail;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class WMSDbHelper{

    public static final int DATABASE_VERSION = 11;
    private static final String DATABASE_NAME = "wmspc.db";
    private File DATABASE_PATH = null;
    private File fPath = null;
    private Cursor mCursor;
    private String mErrCode;
    private String mErrMsg;
    private String mMsg;
    private Context mContext;
    public SQLiteDatabase mSqlitedb;

    public static final String CONFIGSETTINGS_TABLE = "configsettings";
    public static final String COMPANY_TABLE = "company";
    public static final String LOCATION_TABLE = "location";
    public static final String SESSION_TABLE = "sessiondetail";
    public static final String NOTIFICATION_COUNT_TABLE = "notificationcount";
    public static final String MENULIST_TABLE = "menulist";
    public static final String PICK_TASK_LIST_TABLE = "picktasklist";
    public static final String PICK_TASK_HEADER = "picktaskheader";
    public static final String PICK_TASK_DETAIL = "picktaskdetail";
    public static final String PICK_TASK_REVEROLDDATA = "picktaskrevertolddata";
    public static final String PICK_TASK_WHIPTL = "picktaskWHIPTL";
    public static final String PICK_TASK_WHMLOT = "picktaskWHMLOT";
    public static final String PICK_TASK_WHMSLT = "picktaskWHMSLT";
    public static final String PICK_TASK_WHMQTY = "picktaskWHMQTY";
    public static final String PICK_TASK_WHITRLS = "picktaskWHITRLS";
    public static final String PICK_TASK_PRINTLABEL = "picktaskPrintlabel";
    public static final String LPP_SUMMARY = "loadpickpalletSummary";
    public static final String LPP_DETAILS = "loadpickpalletDetails";
    public static final String LPP_ROUTE_DETAILS = "loadpickpalletRouteDetails";
    public static final String LPP_WHIPLT = "loadpickpalletWHIPLT";
    public static final String LPP_WHITRL = "loadpickpalletWHITRL";
    public static final String RECEIVE_TASK_LIST = "receivetasklist";
    public static final String RECEIVE_TASK_HEADER = "receivetaskheader";
    public static final String RECEIVE_TASK_DETAIL = "receivetaskdetail";
    public static final String RECEIVE_TASK_TRAN_DETAIL = "receivetasktrandetail";
    public static final String RECEIVE_TASK_ITEM_CLASS = "receivetaskitemclass";
    public static final String RECEIVE_TASK_LOAD_TYPE = "receivetaskloadtype";
    public static final String RECEIVE_TASK_WHMSLT = "receivetaskWHMSLT";
    public static final String RECEIVE_TASK_WHRPLT = "receivetaskWHRPLT";
    public static final String RECEIVE_TASK_TRAN_WHRPLT = "receivetasktranWHRPLT";
    public static final String RECEIVE_TASK_EXPORT = "receivetaskexportdetail";
    public static final String RECEIVE_TASK_PRINT = "receivetaskprintdetail";
    public static final String PHYSCIAL_COUNT_SLOT_TABLE = "physicalcountSlot";
    public static final String PHYSCIAL_COUNT_DETAIL_TABLE = "physicalcountDetail";
    public static final String PHYSCIAL_COUNT_DETAIL_TRAN_TABLE = "physicalCountTranDetail";
    public static final String PHYSCIAL_COUNT_UOM_TABLE = "physicalcountUom";
    public static final String PHYSCIAL_COUNT_WHMLOT_TABLE = "physicalcountWHMLOT";
    public static final String PHYSCIAL_COUNT_WHMQTY_TABLE = "physicalcountWHMQTY";
    public static final String PHYSCIAL_COUNT_ICITEM_TABLE = "physicalcountICITEM";
    public static final String MOVE_MANUALLY_TABLE = "moveManually";
    public static final String MOVE_MANUALLY_TRANSACTION_TABLE="moveManuallyTransaction";
    public static final String BREAKER_UOM_UTILITY_TABLE = "breakerUOMUtlity";
    public static final String BREAKER_UOM_UTILITY_TRANSACTION_TABLE="moveManuallyTransaction";
    public static final String BREAKER_UOM_LIST_TABLE="breakerUomList";
    public static final String EXPORT_LOT_TABLE="exportLot";
    public static final String SO_CHARGES_CATEGORY = "SoChargesCategory";
    //01/02/23_CIRK_For Fresha to APN
    /*public static final String REPACKFG_TABLE="repackFg";
    public static final String REPACK_INGREDIENT_TABLE="repackIngredient";
    public static final String REPACK_PICKLIST_TABLE="RepackList";*/
    public static final String REPACKFG_TABLE="repackFg";
    public static final String REPACK_INGREDIENT_TABLE="repackIngredient";
    public static final String REPACK_FINSIHED_GOODS_TABLE="repackFinishedGoods";
    public static final String REPACK_PICKLIST_TABLE="RepackList";
    public static final String PICK_TASK_SCANPALLET="PickTask_ScanPallet";
    public static final String RECEIVE_SLOT_LIST="receiveSlotList";
    public static final String Move_TASK_LIST_TABLE="moveTaskList";
    public static final String MOVE_TASK_DETAIL="movetaskdetail";
    public static final String MOVE_TASK_SLOT_LIST="movetaskslotlist";
    public static final String RESETITEM_DETAIL="Resetitemdetail";

    public static final String SALES_ORDER_PICKLIST_TABLE="SalesOrderPicklist";
    public static final String PRINT_PALLET_DETAILS="PrintPalletTagDetails";



    // Configuration Settings table field definition
    public static final String CONFIG_SETTINGS_ID = "configSettingsID";
    public static final String CONFIG_SETTINGS_APP_NAME = "appName";
    public static final String CONFIG_SETTINGS_APP_DESC = "appDesc";
    public static final String CONFIG_SETTINGS_INSTALLATION_DATE = "installationDate";
    public static final String CONFIG_SETTINGS_EXP_DATE = "expDate";
    public static final String CONFIG_SETTINGS_NUMBER_OF_DAYS = "noOfDays";
    public static final String CONFIG_SETTINGS_DEVICE_ID = "deviceId";
    public static final String CONFIG_SETTINGS_USERNAME = "username";
    public static final String CONFIG_SETTINGS_PASSWORD = "password";
    public static final String CONFIG_SETTINGS_ADMIN_PASSWORD = "adminPassword";
    public static final String CONFIG_SETTINGS_SESSIONID = "sessionId";
    public static final String CONFIG_SETTINGS_CURRENT_COMPANY = "currentCompany";
    public static final String CONFIG_SETTINGS_SOFTKEYBOARD = "softKeyboard";

    //Export Lot table field creation
    public static final String EXPORT_LOTNO = "exportLotno";

    //So Charge Category table field creation
    public static final String CHARGES_CATEGORY = "Category";




    // Company Settings table field definition
    public static final String COMPANY_ID = "companyID";
    public static final String COMPANY_NAME = "companyName";
    public static final String COMPANY_DATABASE = "companyDatabase";
    public static final String COMPANY_LOGO_URL = "logoURL";

    // Warehouse Maintenance table field definition
    public static final String LOCATION_ID = "locationID";
    public static final String LOCATION_DESCRIPTION = "locationDescrip";
    public static final String LOCATION_TYPE = "locationType";

    // Company Settings table field definition
    public static final String RESULT = "Result";
    public static final String SESSION_ID = "SessionId";
    public static final String PALLETRESET_PRIV = "PalletResetPriv";

    //Notification count table field definition
    public static final String PICKTASK_COUNT = "PickTask";
    public static final String RECEIVETASK_COUNT = "ReceiveTask";
    public static final String PHYSICALCOUNT_COUNT = "PhysicalCount";
    public static final String MOVETASK_COUNT = "MoveTask";
    public static final String LOADPALLETS_COUNT = "LoadPickPallets";

    //Menu List table field definition
    public static final String PICKTASK_MENU = "PickTask";
    public static final String RECEIVETASK_MENU = "ReceiveTask";
    public static final String PHYSICALCOUNT_MENU = "PhysicalCount";
    public static final String MOVETASK_MENU = "MoveTask";
    public static final String LOADPALLETS_MENU = "LoadPickPallets";
    public static final String MOVEMANUALLY_MENU = "MoveManually";
    public static final String BREAKERUOMUTILITY_MENU = "BreakerUomUtility";

    // Pick Task List table field definition
    public static final String TASK_NO = "TaskNo";
    public static final String STATUS = "Status";
    public static final String ROUTE = "Route";
    public static final String STOP = "Stop";
    public static final String DATE = "Date";
    public static final String SONOs = "SoNumbers";

    // Pick Task Header List table field definition
    public static final String CASE_COUNT = "casecount";
    public static final String PICK_WEIGHT = "weight";
    public static final String PICK_ROUTE = "Route";
    public static final String PICK_STOP = "Stop";
    public static final String PICK_TRAILER = "Trailer";
    public static final String PICK_TASKNO = "taskNum";

    // Pick Task Detail List table field definition
    public static final String DETAIL_SLOT = "Slot";
    public static final String DETAIL_TQTY = "TQty";
    public static final String DETAIL_ORGTQTY = "orgTQty";
    public static final String DETAIL_TRKQTY = "TrkQty";
    public static final String DETAIL_ORGTRKQTY = "orgTrkQty";
    public static final String DETAIL_UOM = "Uom";
    public static final String DETAIL_ITEM = "Item";
    public static final String DETAIL_DESC = "Descrip";
    public static final String DETAIL_WLOTNO = "WLotNo";
    public static final String DETAIL_LOTNO = "LotNo";
    public static final String DETAIL_TRANLINENO = "Tranlineno";
    public static final String DETAIL_ORGTRANLINENO = "orgTranlineno";
    public static final String DETAIL_DOCTYPE = "Doctype";
    public static final String DETAIL_DOCNO = "Docno";
    public static final String DETAIL_DOCLINENO = "Doclineno";
    public static final String DETAIL_ORGDOCLINENO = "orgDoclineno";
    public static final String DETAIL_DOCSTAT = "Docstat";
    public static final String DETAIL_WEIGHT = "Weight";
    public static final String DETAIL_STKUMID = "Stkumid";
    public static final String DETAIL_CATCHWT = "Catchwt";
    public static final String DETAIL_VOLUME = "Volume";
    public static final String DETAIL_DECNUM = "decnum";
    public static final String DETAIL_UMFACT = "Umfact";
    public static final String DETAIL_TSHIPPED = "Tshipped";
    public static final String DETAIL_TRKSHIPPED = "Trkshipped";
    public static final String DETAIL_LBSHP = "Lbshp";
    public static final String DETAIL_PICKDURATION = "pickDuration";
    public static final String DETAIL_LINESPLIT = "Linesplit";
    public static final String DETAIL_FLAG = "Flag";
    public static final String DETAIL_ROWNO = "rowNo";
    public static final String DETAIL_SUBSTITUTED_ITEM = "subItem";
    public static final String DETAIL_SUBSTITUTED_TRANNO = "subTranNo";
    public static final String DETAIL_IS_SUBSTITUTED = "isSub";
    public static final String DETAIL_ORG_SOITEM = "orgItem";
    public static final String DETAIL_STAGINGSLOT = "stagingSlot";
    public static final String DETAIL_TASKNO = "taskNum";
    public static final String DETAIL_ISEDITED = "isedited";
    public static final String DETAIL_CHGQTY = "chgQty";
    public static final String DETAIL_OTQTYPICKED = "oTqtypicked";
    public static final String DETAIL_PICKED = "Picked";
    public static final String DETAIL_TEMPQTY = "tempQty";

    // Pick Task WHIPTL List table field definition
    public static final String WHIPTL_PALNO = "Palno";
    public static final String WHIPTL_PALSTAT = "Palstat";
    public static final String WHIPTL_TASKNO = "Taskno";

    // Pick Task WHMLOT List table field definition
    public static final String WHMLOT_ITEM = "Item";
    public static final String WHMLOT_WLOTNO = "Wlotno";
    public static final String WHMLOT_LOTREFID = "Lotrefid";
    public static final String WHMLOT_TASKNUM = "taskNum";

    // Pick Task WHMSLT List table field definition
    public static final String WHMSLT_SLOT = "Slot";
    public static final String WHMSLT_LOCTID = "Loctid";
    public static final String WHMSLT_SLOTTYPE = "Slottype";

    // Pick Task WHMQTY List table field definition
    public static final String WHMQTY_ITEM = "Item";
    public static final String WHMQTY_LOCTID = "Loctid";
    public static final String WHMQTY_WLOTNO = "Wlotno";
    public static final String WHMQTY_SLOT = "Slot";
    public static final String WHMQTY_UMEASUR = "Umeasur";
    public static final String WHMQTY_TQTY = "Tqty";
    public static final String WHMQTY_SLOTTYPE = "Slottype";
    public static final String WHMQTY_TASKNUM = "taskNum";

    // Pick Task WHITRLS List table field definition
    public static final String WHITRLS_TRAILER = "Trailer";
    public static final String WHITRLS_RTESEQ = "Rteseq";
    public static final String WHITRLS_ROUTE = "Route";
    public static final String WHITRLS_DOCK = "Dock";

    // Pick Task Print Label table field definition
    public static final String PT_STOP = "Stop";
    public static final String PT_TRAILER = "Trailer";
    public static final String PT_ROUTE = "Route";
    public static final String PT_DOCK = "Dock";
    public static final String PT_DELDATE = "Deldate";
    public static final String PT_ORDER = "Orderno";
    public static final String PT_TASKNO = "Taskno";
    public static final String PT_CUSTID = "Custid";
    public static final String PT_CUSTNAME = "Custname";
    public static final String PT_PICKER = "Picker";
    public static final String PT_PALNO = "Palno";

    //Load Pick Pallet Summary table field definition
    public static final String LPP_SUMMARY_WMSDate = "wmsDate";
    public static final String LPP_SUMMARY_TRUCK = "Truck";
    public static final String LPP_SUMMARY_DOCK = "Dock";
    public static final String LPP_SUMMARY_ROUTECNT = "Routecnt";
    public static final String LPP_SUMMARY_STOPCNT = "Stopcnt";
    public static final String LPP_SUMMARY_PALCNT = "Palcnt";
    public static final String LPP_SUMMARY_ROWNO = "rowNo";
    public static final String LPP_SUMMARY_FLAG = "Flag";

    //Load Pick Pallet DETAILS table field definition
    public static final String LPP_DETAIL_WMSDate = "wmsDate";
    public static final String LPP_DETAIL_TASKNO = "Taskno";
    public static final String LPP_DETAIL_PICKER = "Picker";
    public static final String LPP_DETAIL_ROUTE = "Route";
    public static final String LPP_DETAIL_STOP = "Stop";
    public static final String LPP_DETAIL_TOTAL = "Total";
    public static final String LPP_DETAIL_LOADED = "Loaded";
    public static final String LPP_DETAIL_READY = "Ready";
    public static final String LPP_DETAIL_ROWNO = "rowNo";
    public static final String LPP_DETAIL_FLAG = "Flag";

    //Load Pick Pallet Route Details table field definition
    public static final String LPP_ROUTE_DETAIL_ROUTE = "Route";
    public static final String LPP_ROUTE_DETAIL_TRUCK = "Truck";
    public static final String LPP_ROUTE_DETAIL_STATUS = "Status";
    public static final String LPP_ROUTE_DETAIL_STOPCNT = "Stopcnt";
    public static final String LPP_ROUTE_DETAIL_PALIN = "Palin";
    public static final String LPP_ROUTE_DETAIL_PALRDY = "Palrdy";
    public static final String LPP_ROUTE_DETAIL_PALCNT = "Palcnt";

    //Load Pick Pallet WHIPLT table field definition
    public static final String LPP_WHIPLT_WMSDATE = "wmsDate";
    public static final String LPP_WHIPLT_PALNO = "Palno";
    public static final String LPP_WHIPLT_TASKNO = "Taskno";
    public static final String LPP_WHIPLT_PALSTAT = "Palstat";
    public static final String LPP_WHIPLT_STGSLOT = "stgslot";

    //Load Pick Pallet WHITRL table field definition
    public static final String LPP_WHITRL_WMSDATE = "wmsDate";
    public static final String LPP_WHITRL_TRAILER = "Trailer";
    public static final String LPP_WHITRL_RTESEQ = "Rteseq";
    public static final String LPP_WHITRL_ROUTE = "Route";
    public static final String LPP_WHITRL_DOCK = "Dock";

    // Receive Task List table field definition
    public static final String RT_TASKNO = "TaskNo";
    public static final String RT_STATUS = "Status";
    public static final String RT_USERID = "Userid";
    public static final String RT_DOCTYPE = "Doctype";
    public static final String RT_DOCNO = "Docno";

    // Receive Task Header table field definition
    public static final String RTH_DESCRIP = "descrip";
    public static final String RTH_VENDDESCRIP = "venddescrip";
    public static final String RTH_CASECOUNTED = "casecounted";
    public static final String RTH_CASETOTAL = "casetotal";
    public static final String RTH_PLTOTAL = "plttotal";
    public static final String RTH_PLTCOUNTED = "pltcounted";
    public static final String RTH_WMSDATE = "wmsdate";

    // Receive Task Detail table field definition
    public static final String RTD_TASKNO = "taskno";
    public static final String RTD_TRANLINENO = "tranlineno";
    public static final String RTD_DOCTYPE = "doctype";
    public static final String RTD_DOCNO = "docno";
    public static final String RTD_DOCLINENO = "doclineno";
    public static final String RTD_ITEM = "item";
    public static final String RTD_LOCTID = "loctid";
    public static final String RTD_WLOTNO = "wlotno";
    public static final String RTD_UMEASUR = "umeasur";
    public static final String RTD_WMSSTAT = "wmsstat";
    public static final String RTD_TQTYREC = "tqtyrec";
    public static final String RTD_TRKQTYREC = "trkqtyrec";
    public static final String RTD_REVLEV = "revlev";
    public static final String RTD_TQTYINC = "tqtyinc";
    public static final String RTD_ITMDESC = "itmdesc";
    public static final String RTD_PCKDESC = "pckdesc";
    public static final String RTD_COUNTRYID = "countryid";
    public static final String RTD_ITEMSHOW = "itemShow";
    public static final String RTD_COLLECTION = "collection";
    public static final String RTD_WELEMENT = "welement";
    public static final String RTD_WIDGETID = "widgetID";
    public static final String RTD_CATCHWT = "catchwt";
    public static final String RTD_DECNUM = "decnum";
    public static final String RTD_LOTREFID = "lotrefid";
    public static final String RTD_LINESPLIT = "Linesplit";
    public static final String RTD_FLAG = "Flag";
    public static final String RTD_ROWNO = "rowNo";
    public static final String RTD_PALNO = "palno";

    //Move Task Detail table field definition
    public static final String MTD_TASKNO = "taskno";
    public static final String MTD_TASKTYPE = "tasktype";
    public static final String MTD_STATUS = "status";
    public static final String MTD_TRANLINENO = "tranlineno";
    public static final String MTD_CHILDID = "childID";
    public static final String MTD_ITEM = "item";
    public static final String MTD_LOCTID = "loctid";
    public static final String MTD_WLOTNO = "wlotno";
    public static final String MTD_PALNO = "palno";
    public static final String MTD_UMEASUR = "umeasur";
    public static final String MTD_TQTYRQ = "tqtyrq";
    public static final String MTD_TQTYACT = "tqtyact";
    public static final String MTD_FROMSLOT = "fromSlot";
    public static final String MTD_TOSLOT = "toSlot";
    public static final String MTD_ITEMDESC = "itmdesc";
    public static final String MTD_PCKDESC = "pckdesc";
    public static final String MTD_WHQTY= "whqty";
    public static final String MTD_ALLOCQTY= "allocqty";
    public static final String MTD_LOCKED= "locked";
    public static final String MTD_FLAG= "flag";
    public static final String MTD_EDITED= "edited";

    // Receive Task Item Class List table field definition
    public static final String RTIC_ITMCLSS = "itmclss";
    public static final String RTIC_DESCRIP = "descrip";
    public static final String RTIC_WELEMENT = "welement";
    public static final String RTIC_COLLECTION = "collection";
    public static final String RTIC_WIDGETID = "widgetID";

    // Receive Task Load Type table field definition
    public static final String RTLT_LOADTYPE = "loadtype";
    public static final String RTLT_DESCRIP = "descrip";
    public static final String RTLT_WELEMENT = "welement";
    public static final String RTLT_COLLECTION = "collection";
    public static final String RTLT_WIDGETID = "widgetID";
    public static final String RTLT_LOADID = "LoadId";
    public static final String RTLT_LOADTYPSTS = "LoadTypeStatus";
    public static final String RTLT_WMSDATE = "WmsDate";
    public static final String RTLT_METRICVAL = "Metricval";
    public static final String RTLT_FLAG = "Flag";

    // Receive Task WHMSLT table field definition
    public static final String RTWHMSLT_SLOT = "Slot";

    // Receive Task WHRPLT table field definition
    public static final String RTWHRPLT_TASKNO = "taskno";
    public static final String RTWHRPLT_TASKLINENO = "tasklineno";
    public static final String RTWHRPLT_PLTLINENO = "pltlineno";
    public static final String RTWHRPLT_TQTY = "tqty";
    public static final String RTWHRPLT_PLTSTAT = "pltstat";
    public static final String RTWHRPLT_PRTPLTTAG = "prtplttag";
    public static final String RTWHRPLT_TRKQTY = "trkqty";
    public static final String RTWHRPLT_SLOT = "Slot";
    public static final String RTWHRPLT_FLAG = "Flag";
    public static final String RTWHRPLT_GTIN = "gtin";

    // Receive Task EXPORT table field definition
    public static final String RTE_TASKNO = "taskno";
    public static final String RTE_TRANLINENO = "tranlineno";
    public static final String RTE_DOCTYPE = "Doctype";
    public static final String RTE_DOCNO = "Docno";
    public static final String RTE_DOCLINENO = "Doclineno";
    public static final String RTE_ITEM = "Item";
    public static final String RTE_UMEASUR = "umeasur";
    public static final String RTE_LOCTID = "Loctid";
    public static final String RTE_WLOTNO = "Wlotno";
    public static final String RTE_LOTREFID = "Lotrefid";
    public static final String RTE_TQTY = "Tqtyrec";
    public static final String RTE_TRKQTY = "Trkqtyrec";
    public static final String RTE_PLTLINENO = "pltlineno";
    public static final String RTE_PTQTY = "ptqty";
    public static final String RTE_PTRKQTY = "ptrkqty";
    public static final String RTE_PRTPLTTAG = "prtplttag";
    public static final String RTE_SLOT = "Slot";
    public static final String RTE_GTIN = "rteGtin";

    // Receive Task Print table field definition
    public static final String RTP_WLOTNO = "wlotno";
    public static final String RTP_LOTREFID = "lotrefid";
    public static final String RTP_ITEM = "item";
    public static final String RTP_RECDATE = "recdate";
    public static final String RTP_EXPDATE = "expdate";
    public static final String RTP_RECUSER = "recuser";
    public static final String RTP_TASKNO = "taskno";
    public static final String RTP_TASKLINENO = "tasklineno";
    public static final String RTP_PLTLINENO = "pltlineno";
    public static final String RTP_PRTPLTTAG = "prtplttag";
    public static final String RTP_TQTY = "tqty";
    public static final String RTP_TRKQTY = "trkqty";
    public static final String RTP_ITMDESC = "itmdesc";
    public static final String RTP_CATCHWT = "catchwt";

    // Physical Count Slot table field definition
    public static final String PHYSCIAL_COUNT_SLOT = "slot";
    public static final String PHYSCIAL_COUNT_WMSSTAT = "wmsstat";
    public static final String PHYSCIAL_COUNT_POSTED = "posted";
    public static final String PHYSCIAL_COUNT_GRPCNT = "grpcnt";
    public static final String PHYSCIAL_COUNT_STATUS = "status";
    public static final String PHYSCIAL_COUNT_DOCLINECOUNT = "doclinecount";
    public static final String PHYSCIAL_COUNT_ROWNO = "rowno";

    // Physical Count Details table field definition
    public static final String PC_DETAIL_SLOT = "slot";
    public static final String PC_DETAIL_COUNTID = "countid";
    public static final String PC_DETAIL_PAGE = "page";
    public static final String PC_DETAIL_DOCLINENO = "doclineno";
    public static final String PC_DETAIL_LOCTID = "loctid";
    public static final String PC_DETAIL_ITEM = "item";
    public static final String PC_DETAIL_INVTYPE = "invtype";
    public static final String PC_DETAIL_WLOTNO = "wlotno";
    public static final String PC_DETAIL_UMEASUR = "umeasur";
    public static final String PC_DETAIL_TCOUNTQTY = "tcountqty";
    public static final String PC_DETAIL_WMSSTAT = "wmsstat";
    public static final String PC_DETAIL_POSTED = "posted";
    public static final String PC_DETAIL_ITMDESC = "itmdesc";
    public static final String PC_DETAIL_PCKDESC = "pckdesc";
    public static final String PC_DETAIL_DECNUM = "decnum";
    public static final String PC_DETAIL_LOTREFID = "lotrefid";
    public static final String PC_DETAIL_TQTY = "tqty";
    public static final String PC_DETAIL_ITEMSHOW = "itemShow";
    public static final String PC_DETAIL_SURPRISADD = "surprisadd";
    public static final String PC_DETAIL_USERID = "userid";
    public static final String PC_DETAIL_COUNTED = "counted";
    public static final String PC_DETAIL_COLLECTION = "collection";
    public static final String PC_DETAIL_WELEMENT = "welement";
    public static final String PC_DETAIL_WIDGETID = "widgetID";
    public static final String PC_DETAIL_ROWNO = "RowNo";
    public static final String PC_DETAIL_FLAG = "Flag";
    public static final String PC_DETAIL_UPDATEDNO = "UpdatedNo";


    // Physical Count UOM table field definition
    public static final String PC_UOM_ITEM = "item";
    public static final String PC_UOM_UMEASUR = "umeasur";

    // Physical Count WHMSLT table field definition
    public static final String PC_WHMSLT_SLOT = "slot";

    // Physical Count WHMLOT table field definition
    public static final String PC_WHMLOT_WLOTNO = "wlotno";
    public static final String PC_WHMLOT_ITEM = "item";
    public static final String PC_WHMLOT_LOTREFID = "lotrefid";

    // Physical Count WHMQTY table field definition
    public static final String PC_WHMQTY_SLOT = "slot";
    public static final String PC_WHMQTY_ITEM = "item";
    public static final String PC_WHMQTY_WLOTNO = "wlotno";
    public static final String PC_WHMQTY_UMEASUR = "umeasur";

    // Physical Count ICITEM table field definition
    public static final String PC_ICITEM_ITEM = "item";
    public static final String PC_ICITEM_INVTYPE = "invtype";
    public static final String PC_ICITEM_STKUMID = "stkumid";
    public static final String PC_ICITEM_PUNMSID = "punmsid";
    public static final String PC_ICITEM_SUNMSID = "sunmsid";
    public static final String PC_ICITEM_BRNUM1 = "brnam1";
    public static final String PC_ICITEM_BRNUM2 = "brnam2";
    public static final String PC_ICITEM_BRNAM3 = "brnam3";
    public static final String PC_ICITEM_BRNAM4 = "brnam4";
    public static final String PC_ICITEM_BRNAM5 = "brnam5";
    public static final String PC_ICITEM_ITMDESC = "itmdesc";
    public static final String PC_ICITEM_PCKDESC = "pckdesc";
    public static final String PC_ICITEM_DECNUM = "decnum";
    public static final String PC_ICITEM_ITEMSHOW = "itemShow";

    //MOVE_MANUALLY_WLOT Table creation field
    public static final String MOVE_MANUALLY_WLOTNO = "wLotNo";
    public static final String MOVE_MANUALLY_ITEMNO = "itemNo";
    public static final String MOVE_MANUALLY_SLOTNO = "slotNo";
    public static final String MOVE_MANUALLY_LOCTID = "loctid";
    public static final String MOVE_MANUALLY_UOM = "uom";
    public static final String MOVE_MANUALLY_QTY = "tqty";
    public static final String MOVE_MANUALLY_TRKQTY = "trkqty";
    public static final String MOVE_MANUALLY_ISLOCKED = "locked";
    public static final String MOVE_MANUALLY_ITMDESC = "itmDesc";
    public static final String MOVE_MANUALLY_CATCHWT = "catchwt";
    public static final String MOVE_MANUALLY_LOTREFID = "lotrefid";
    public static final String MOVE_MANUALLY_RPALLOC = "rpAlloc";

    //MOVE_MANUALLY_TRANSACTION Table creation field
    public static final String MOVE_MANUALLY_TRANSACTION_WLOTNO = "mmtwLotNo";
    public static final String MOVE_MANUALLY_TRANSACTION_ITEMNO = "mmtitemNo";
    public static final String MOVE_MANUALLY_TRANSACTION_SLOTNO = "mmtslotNo";
    public static final String MOVE_MANUALLY_TRANSACTION_LOCTID = "mmtloctid";
    public static final String MOVE_MANUALLY_TRANSACTION_UOM = "mmtuom";
    public static final String MOVE_MANUALLY_TRANSACTION_QTY = "mmttqty";
    public static final String MOVE_MANUALLY_TRANSACTION_TRKQTY = "mmttrkqty";
    public static final String MOVE_MANUALLY_TRANSACTION_EQTY = "mmtEqty";
    public static final String MOVE_MANUALLY_TRANSACTION_ITMDESC = "mmtItmDesc";
    public static final String MOVE_MANUALLY_TRANSACTION_CATCHWT = "mmtCatchwt";
    public static final String MOVE_MANUALLY_TRANSACTION_LotrefId = "mmtLotrefid";

    //BREAKER UOM UTILITY Table creation field
    public static final String BREAKER_UOM_WLOTNO = "wLotNo";
    public static final String BREAKER_UOM_ITEMNO = "itemNo";
    public static final String BREAKER_UOM_SLOTNO = "slotNo";
    public static final String BREAKER_UOM_LOCTID = "loctid";
    public static final String BREAKER_UOM_UOM = "uom";
    public static final String BREAKER_UOM_QTY = "tqty";
    public static final String BREAKER_UOM_TRKQTY = "trkqty";
    public static final String BREAKER_UOM_ISLOCKED = "locked";
    public static final String BREAKER_UOM_ITMDESC = "itmDesc";
    public static final String BREAKER_UOM_CATCHWT = "catchwt";
    public static final String BREAKER_UOM_STKUMID = "stkumid";
    public static final String BREAKER_UOM_LOTREFID = "lotrefid";

    //BREAKER UOM UTILITY Table creation field
    public static final String BREAKER_UOM_LIST_BRNAME = "brName";
    public static final String BREAKER_UOM_LIST_BRUNIT = "brUnit";

   /* //  RepackFg Table creation field        //01/02/23_CIRK_For Fresha to APN
    public static final String REPACKFG_PANO = "pano";
    public static final String  REPACKFG_TRANLINENO = "tranlineNo";
    public static final String  REPACKFG_ITEM = "item";
    public static final String  REPACKFG_DESCRIP = "descrip";
    public static final String  REPACKFG_UMEASUR = "umeasur";
    public static final String  REPACKFG_LOCTID = "loctid";
    public static final String  REPACKFG_LOTNO = "lotNo";
    public static final String  REPACKFG_SERIAL = "serial";
    public static final String  REPACKFG_QTYMADE = "qtymade";
    public static final String  REPACKFG_COST = "cost";
    public static final String  REPACKFG_PADATE = "padate";
    public static final String  REPACKFG_PASTAT = "pastat";
    public static final String  REPACKFG_LCKSTAT = "lckstat";
    public static final String  REPACKFG_LCKUSER = "lckuser";
    public static final String  REPACKFG_LCKDATE = "lckdate";
    public static final String  REPACKFG_LCKTIME = "lcktime";
    public static final String  REPACKFG_ADDUSER = "adduser";
    public static final String  REPACKFG_ADDDATE = "adddate";
    public static final String  REPACKFG_ADDTIME = "addtime";
    public static final String  REPACKFG_PRODLBL = "prodlbl";
    public static final String  REPACKFG_PACKCHG = "packchg";
    public static final String  REPACKFG_WASHCHG = "washchg";
    public static final String  REPACKFG_COUNTRYID = "countryid";
    public static final String  REPACKFG_VENDNO = "vendNo";
    public static final String  REPACKFG_GRADE = "grade";
    public static final String  REPACKFG_PROJNO = "projNo";
    public static final String  REPACKFG_REMARKS = "remarks";
    public static final String  REPACKFG_LCSTQTY = "lcstqty";
    public static final String  REPACKFG_CASE_PL = "case_pl";
    public static final String  REPACKFG_PALNO = "palNo";
    public static final String  REPACKFG_SETID = "setid";
    public static final String  REPACKFG_WEIGHT = "weight";
    public static final String  REPACKFG_PALLET = "pallet";
    public static final String  REPACKFG_ID_COL = "id_col";
    public static final String  REPACKFG_BINNO = "binNo";
    public static final String  REPACKFG_POSTPRG = "postprg";
    public static final String  REPACKFG_EXTPALLET = "extpallet";
    public static final String  REPACKFG_EXTCUBE = "extcube";
    public static final String  REPACKFG_EXTWEIGHT = "extweight";
    public static final String  REPACKFG_BEXTLCST = "bextlcst";
    public static final String  REPACKFG_EXTLCST = "extlcst";
    public static final String  REPACKFG_BEXTFEES = "bextfees";
    public static final String  REPACKFG_EXTFEES = "extfees";
    public static final String  REPACKFG_TPALLET = "tpallet";
    public static final String  REPACKFG_TCUBE = "tcube";
    public static final String  REPACKFG_TWEIGHT = "tweight";
    public static final String  REPACKFG_WLOTNO = "wlotNo";
    public static final String  REPACKFG_ORIGTRANLN = "origtranln";
    public static final String  REPACKFG_ORIGTRANL = "origtranl";
    public static final String  REPACKFG_ORIGDOCLN = "origdocln";
    public static final String  REPACKFG_STKUMID = "stkumid";
    public static final String  REPACKFG_USELOTS = "uselots";
    public static final String  REPACKFG_UMFACT = "umfact";
    public static final String  REPACKFG_WEIGHT1 = "weight1";
    public static final String  REPACKFG_VOLUME = "volume";
    public static final String  REPACKFG_CATCHWT = "catchwt";
    public static final String  REPACKFG_LOTREFID = "lotrefid";
    public static final String  REPACKFG_LOTEXPL = "Lotexpl";
    public static final String  REPACKFG_LINESPLIT = "Linesplit";
    public static final String  REPACKFG_TRKQTYPK = "Trkqtypk";
    public static final String  REPACKFG_UPDFLAG = "updflag";
    public static final String  REPACKFG_VPLOCKED = "vplocked";*/


   /* //  RepackIngredient Table creation field                //01/02/23_CIRK_For Fresha to APN
    public static final String RIT_PANO = "pano";
    public static final String  RIT_TRANLINENO = "tranlineNo";
    public static final String  RIT_ITEM = "item";
    public static final String  RIT_DESCRIP = "descrip";
    public static final String  RIT_UMEASUR = "umeasur";
    public static final String  RIT_LOCTID = "loctid";
    public static final String  RIT_LOTNO = "lotNo";
    public static final String  RIT_SERIAL = "serial";
    public static final String  RIT_QTYUSED = "qtyused";
    public static final String  RIT_COST = "cost";
    public static final String  RIT_PADATE = "padate";
    public static final String  RIT_PASTAT = "pastat";
    public static final String  RIT_LCKSTAT = "lckstat";
    public static final String  RIT_LCKUSER = "lckuser";
    public static final String  RIT_LCKDATE = "lckdate";
    public static final String  RIT_LCKTIME = "lcktime";
    public static final String  RIT_ADDUSER = "adduser";
    public static final String  RIT_ADDDATE = "adddate";
    public static final String  RIT_ADDTIME = "addtime";
    public static final String  RIT_COUNTRYID = "countryid";
    public static final String  RIT_VENDNO = "vendNo";
    public static final String  RIT_BINNO = "binNo";
    public static final String  RIT_PALNO = "palNo";
    public static final String  RIT_REMARKS = "remarks";
    public static final String  RIT_YIELD = "yield";
    public static final String  RIT_SETID = "setid";
    public static final String  RIT_WEIGHT = "weight";
    public static final String  RIT_ID_COL = "id_col";
    public static final String  RIT_WLOTNO = "wlotNo";
    public static final String  RIT_ORIGTRANLN = "origtranln";
    public static final String  RIT_STKUMID = "stkumid";
    public static final String  RIT_UMFACT = "umfact";
    public static final String  RIT_WEIGHT1 = "weight1";
    public static final String  RIT_VOLUME = "volume";
    public static final String  RIT_CATCHWT = "catchwt";
    public static final String  RIT_LOTREFID = "lotrefid";
    public static final String  RIT_LOTEXPL = "Lotexpl";
    public static final String  RIT_LINESPLIT = "Linesplit";
    public static final String  RIT_TRKQTYPK = "Trkqtypk";
    public static final String  RIT_UPDFLAG = "updflag";
    public static final String  RIT_USELOTS = "uselots";
    public static final String  RIT_ADDFLAG = "addflag";
    public static final String  RIT_SLOT = "slot";
    public static final String  RIT_ALLOCQTY = "allocqty";
    public static final String  RIT_TEMPALLOC = "tempAlloc";
    public static final String  RIT_WHQTY = "whqty";
    public static final String  RIT_ICQTY = "icqty";
    public static final String  RIT_RPALLOCQTY = "rpallocqty";*/


    //  RepackFg Table creation field
    public static final String REPACKFG_PANO = "pano";
    public static final String  REPACKFG_TRANLINENO = "tranlineNo";
    public static final String  REPACKFG_ITEM = "item";
    public static final String  REPACKFG_DESCRIP = "descrip";
    public static final String  REPACKFG_UMEASUR = "umeasur";
    public static final String  REPACKFG_LOCTID = "loctid";
    public static final String  REPACKFG_LOTNO = "lotNo";
    public static final String  REPACKFG_SERIAL = "serial";
    public static final String  REPACKFG_QTYMADE = "qtymade";
    public static final String  REPACKFG_COST = "cost";
    public static final String  REPACKFG_PADATE = "padate";
    public static final String  REPACKFG_PASTAT = "pastat";
    public static final String  REPACKFG_LCKSTAT = "lckstat";
    public static final String  REPACKFG_LCKUSER = "lckuser";
    public static final String  REPACKFG_LCKDATE = "lckdate";
    public static final String  REPACKFG_LCKTIME = "lcktime";
    public static final String  REPACKFG_ADDUSER = "adduser";
    public static final String  REPACKFG_ADDDATE = "adddate";
    public static final String  REPACKFG_ADDTIME = "addtime";
    public static final String  REPACKFG_PRODLBL = "prodlbl";
    public static final String  REPACKFG_PACKCHG = "packchg";
    public static final String  REPACKFG_WASHCHG = "washchg";
    public static final String  REPACKFG_COUNTRYID = "countryid";
    public static final String  REPACKFG_VENDNO = "vendNo";
    public static final String  REPACKFG_GRADE = "grade";
    public static final String  REPACKFG_PROJNO = "projNo";
    public static final String  REPACKFG_REMARKS = "remarks";
    public static final String  REPACKFG_LCSTQTY = "lcstqty";
    public static final String  REPACKFG_CASE_PL = "case_pl";
    public static final String  REPACKFG_PALNO = "palNo";
    public static final String  REPACKFG_SETID = "setid";
    public static final String  REPACKFG_WEIGHT = "weight";
    public static final String  REPACKFG_PALLET = "pallet";
    public static final String  REPACKFG_ID_COL = "id_col";
    public static final String  REPACKFG_BINNO = "binNo";
    public static final String  REPACKFG_POSTPRG = "postprg";
    public static final String  REPACKFG_EXTPALLET = "extpallet";
    public static final String  REPACKFG_EXTCUBE = "extcube";
    public static final String  REPACKFG_EXTWEIGHT = "extweight";
    public static final String  REPACKFG_BEXTLCST = "bextlcst";
    public static final String  REPACKFG_EXTLCST = "extlcst";
    public static final String  REPACKFG_BEXTFEES = "bextfees";
    public static final String  REPACKFG_EXTFEES = "extfees";
    public static final String  REPACKFG_TPALLET = "tpallet";
    public static final String  REPACKFG_TCUBE = "tcube";
    public static final String  REPACKFG_TWEIGHT = "tweight";
    public static final String  REPACKFG_WLOTNO = "wlotNo";
    public static final String  REPACKFG_ORIGTRANLN = "origtranln";
    public static final String  REPACKFG_ORIGTRANL = "origtranl";
    public static final String  REPACKFG_ORIGDOCLN = "origdocln";
    public static final String  REPACKFG_STKUMID = "stkumid";
    public static final String  REPACKFG_USELOTS = "uselots";
    public static final String  REPACKFG_UMFACT = "umfact";
    public static final String  REPACKFG_WEIGHT1 = "weight1";
    public static final String  REPACKFG_VOLUME = "volume";
    public static final String  REPACKFG_CATCHWT = "catchwt";
    public static final String  REPACKFG_LOTREFID = "lotrefid";
    public static final String  REPACKFG_LOTEXPL = "Lotexpl";
    public static final String  REPACKFG_LINESPLIT = "Linesplit";
    public static final String  REPACKFG_TRKQTYPK = "Trkqtypk";
    public static final String  REPACKFG_UPDFLAG = "updflag";
    public static final String  REPACKFG_VPLOCKED = "vplocked";
    public static final String  REPACKFG_DECNUM = "decnum";
    public static final String  REPACKFG_USEDQTY = "usedQty";
    public static final String  REPACKFG_ADDFLAG = "addFlag";


    //  RepackIngredient Table creation field   //01/02/23_CIRK_For Fresha to APN
    public static final String RIT_PANO = "pano";
    public static final String  RIT_TRANLINENO = "tranlineNo";
    public static final String  RIT_ITEM = "item";
    public static final String  RIT_DESCRIP = "descrip";
    public static final String  RIT_UMEASUR = "umeasur";
    public static final String  RIT_LOCTID = "loctid";
    public static final String  RIT_LOTNO = "lotNo";
    public static final String  RIT_SERIAL = "serial";
    public static final String  RIT_QTYUSED = "qtyused";
    public static final String  RIT_COST = "cost";
    public static final String  RIT_PADATE = "padate";
    public static final String  RIT_PASTAT = "pastat";
    public static final String  RIT_LCKSTAT = "lckstat";
    public static final String  RIT_LCKUSER = "lckuser";
    public static final String  RIT_LCKDATE = "lckdate";
    public static final String  RIT_LCKTIME = "lcktime";
    public static final String  RIT_ADDUSER = "adduser";
    public static final String  RIT_ADDDATE = "adddate";
    public static final String  RIT_ADDTIME = "addtime";
    public static final String  RIT_COUNTRYID = "countryid";
    public static final String  RIT_VENDNO = "vendNo";
    public static final String  RIT_BINNO = "binNo";
    public static final String  RIT_PALNO = "palNo";
    public static final String  RIT_REMARKS = "remarks";
    public static final String  RIT_YIELD = "yield";
    public static final String  RIT_SETID = "setid";
    public static final String  RIT_WEIGHT = "weight";
    public static final String  RIT_ID_COL = "id_col";
    public static final String  RIT_WLOTNO = "wlotNo";
    public static final String  RIT_ORIGTRANLN = "origtranln";
    public static final String  RIT_STKUMID = "stkumid";
    public static final String  RIT_UMFACT = "umfact";
    public static final String  RIT_WEIGHT1 = "weight1";
    public static final String  RIT_VOLUME = "volume";
    public static final String  RIT_CATCHWT = "catchwt";
    public static final String  RIT_LOTREFID = "lotrefid";
    public static final String  RIT_LOTEXPL = "Lotexpl";
    public static final String  RIT_LINESPLIT = "Linesplit";
    public static final String  RIT_TRKQTYPK = "Trkqtypk";
    public static final String  RIT_UPDFLAG = "updflag";
    public static final String  RIT_USELOTS = "uselots";
    public static final String  RIT_ADDFLAG = "addflag";
    public static final String  RIT_SLOT = "slot";
    public static final String  RIT_ALLOCQTY = "allocqty";
    public static final String  RIT_TEMPALLOC = "tempAlloc";
    public static final String  RIT_WHQTY = "whqty";
    public static final String  RIT_ICQTY = "icqty";
    public static final String  RIT_RPALLOCQTY = "rpallocqty";
    public static final String  RIT_ORGPANO = "orgpano";

    //  RepackPickList Table creation field
    public static final String RPL_ADDTIME = "addtime";
    public static final String RPL_LOCTID = "loctid";
    public static final String RPL_PAFDATE = "padate";
    public static final String RPL_PANO = "pano";



    //Receive Slot list
    public static final String RSL_ITEM = "item";
    public static final String RSL_SLOT = "slot";
    public static final String RSL_TASKNO = "taskno";

    //MoveTAsk Slot list
    public static final String MTSL_TASKNO = "taskNo";
    public static final String MTSL_SLOT = "slot";
    public static final String MTSL_LOCTID = "loctId";
    public static final String MTSL_SLOTTYPE = "slotType";

    //  PickTaskScanPallet Table creation field
    public static final String PTSP_ITEM = "item";
    public static final String PTSP_LOCTID = "loctid";
    public static final String PTSP_WLOTNO = "wlotno";
    public static final String PTSP_LOTNO = "lotno";
    public static final String PTSP_LOTREFID = "lotrefid";
    public static final String PTSP_SLOT= "slot";
    public static final String PTSP_UMEASUR= "umeasur";
    public static final String PTSP_TQTY= "tqty";
    public static final String PTSP_RPALLOCQTY= "rpallocqty";
    public static final String PTSP_WHQTY= "whqty";
    public static final String PTSP_ICQTY= "icqty";
    public static final String PTSP_ITMDESC= "itmdesc";
    public static final String PTSP_WEIGHT= "weight";
    public static final String PTSP_COUNTRYID= "countryid";
    public static final String PTSP_SERIAL= "serial";
    public static final String PTSP_VOLUME= "volume";
    public static final String PTSP_CATCHWT= "catchwt";
    public static final String PTSP_STKUMID= "stkumid";
    public static final String PTSP_USELOTS= "uselots";
    public static final String PTSP_UMFACT= "umfact";
    public static final String PTSP_SETID= "setid";
    public static final String PTSP_VENDNO= "vendno";
    public static final String PTSP_COST= "cost";

    // Move Task List table field definition
    public static final String MT_TASK_NO = "TaskNo";
    public static final String MT_STATUS = "Status";
    public static final String MT_ROWPRTY = "RowPrty";
    public static final String MT_TASK_TYPE = "TaskType";


    // Print Pallet Tag table field definition
    public static final String PPD_ITEM = "item";
    public static final String PPD_LOTNO = "lotno";
    public static final String PPD_PALNO = "Palno";
    public static final String PPD_QTY = "qty";
    public static final String PPD_COUNTRYID = "countryid";
    public static final String PPD_OUTPALNO = "outpalno";
    public static final String PPD_PLTCREA = "pltcrea";
    public static final String PPD_SETID = "setid";
    public static final String PPD_GRADE = "grade";
    public static final String PPD_ITEMDESC = "itmdesc";
    public static final String PPD_COMMOD = "commod";
    public static final String PPD_LABEL = "label";
    public static final String PPD_PACKAGE = "package";
    public static final String PPD_SIZESORT = "sizesort";
    public static final String PPD_VARIETY = "variety";
    public static final String PPD_SIZE = "size";



    //RESETITEM_DETAIL table field definition
    public static final String RID_ITEM = "item";
    public static final String RID_LOCTID = "loctid";
    public static final String RID_WLOTNO = "wlotno";
    public static final String RID_LOTNO = "lotno";
    public static final String RID_PALNO = "Palno";
    public static final String RID_SLOT = "slot";
    public static final String RID_UMEASUR = "umeasur";
    public static final String RID_TQTY = "tqty";
    public static final String RID_RPALLOCQTY = "rpallocqty";
    public static final String RID_WHQTY = "whqty";
    public static final String RID_ICQTY = "icqty";
    public static final String RID_ITMDESC = "itmdesc";
    public static final String RID_QTY = "qty";
    public static final String RID_PSOALOC = "psoaloc";
    public static final String RID_PRPALOC = "prpaloc";
    public static final String RID_PTFROUT = "ptfrout";
    public static final String RID_PTMALOC = "ptmaloc";
    public static final String RID_OPENSOCOUNT = "opensocount";
    public static final String RID_OPENREPACKCOUNT = "openrepackcount";



    public static final String SOL_SONO = "sono";
    public static final String SOL_SODATE = "sodate";
    public static final String SOL_CUSTNO = "custno";
    public static final String SOL_ORDAMT = "ordamt";




    // Configuration Settings table create query
    public static final String CREATE_QUERY_CONFIG_SETTINGS = "create table "
            + CONFIGSETTINGS_TABLE + "("
            + CONFIG_SETTINGS_ID + " integer primary key autoincrement,"
            + CONFIG_SETTINGS_APP_NAME + " text,"
            + CONFIG_SETTINGS_APP_DESC + " text,"
            + CONFIG_SETTINGS_INSTALLATION_DATE + " text,"
            + CONFIG_SETTINGS_EXP_DATE + " text,"
            + CONFIG_SETTINGS_NUMBER_OF_DAYS + " text,"
            + CONFIG_SETTINGS_DEVICE_ID + " text,"
            + CONFIG_SETTINGS_USERNAME + " text,"
            + CONFIG_SETTINGS_PASSWORD + " text,"
            + CONFIG_SETTINGS_ADMIN_PASSWORD + " text, "
            + CONFIG_SETTINGS_SESSIONID + " text, "
            + CONFIG_SETTINGS_CURRENT_COMPANY + " text, "
            + CONFIG_SETTINGS_SOFTKEYBOARD + " text"
            + ");";

    //Export Lotid table creation

    public static final String CREATE_QUERY_EXPORT_TABLE = "create table "
            + EXPORT_LOT_TABLE + "(" + EXPORT_LOTNO + " text);";

    //Export Lotid table creation

    public static final String CREATE_SO_CHARGES_CSTEGORY_TABLE = "create table "
            + SO_CHARGES_CATEGORY + "(" + CHARGES_CATEGORY + " text);";


    // Company table create query
    public static final String CREATE_QUERY_COMPANY = "create table "
            + COMPANY_TABLE + "(" + COMPANY_ID + " text," + COMPANY_NAME
            + " text, " + COMPANY_DATABASE + " text, " + COMPANY_LOGO_URL + " text);";

    // Location table create query
    public static final String CREATE_QUERY_LOCATION = "create table "
            + LOCATION_TABLE + "(" + LOCATION_ID + " text," + LOCATION_DESCRIPTION + " text, "
            + LOCATION_TYPE + " text);";

    // Session table create query
    public static final String CREATE_QUERY_SESSION = "create table "
            + SESSION_TABLE + "(" + RESULT + " text,"  + SESSION_ID + " text, "
            + PALLETRESET_PRIV + " text);";

    // Task Notification table create query
    public static final String CREATE_QUERY_NOTIFICATION = "create table "
            + NOTIFICATION_COUNT_TABLE + "(" + PICKTASK_COUNT + " text, " + RECEIVETASK_COUNT
            + " text, " + PHYSICALCOUNT_COUNT + " text, " + MOVETASK_COUNT + " text, " + LOADPALLETS_COUNT + " text);";

    // Task Notification table create query
    public static final String CREATE_QUERY_MENULIST = "create table "
            + MENULIST_TABLE + "(" + PICKTASK_MENU + " text, " + RECEIVETASK_MENU
            + " text, " + PHYSICALCOUNT_MENU + " text, " + MOVETASK_MENU
            + " text, " + LOADPALLETS_MENU + " text, " + MOVEMANUALLY_MENU + " text, " + BREAKERUOMUTILITY_MENU + " text);";

    // Company table create query
    public static final String CREATE_QUERY_PICKTASK = "create table "
            + PICK_TASK_LIST_TABLE + "(" + TASK_NO + " text, " + ROUTE
            + " text, " + STOP + " text," + STATUS + " text," + SONOs + " text, " + DATE + " text);";

    // PickTask Header table create query
    public static final String CREATE_QUERY_PICKTASK_HEADER = "create table "
            + PICK_TASK_HEADER + "(" + CASE_COUNT + " text, " + PICK_ROUTE
            + " text, " + PICK_STOP + " text, " + PICK_WEIGHT + " text, " + PICK_TRAILER + " text, " + PICK_TASKNO + " text);";

    // PickTask Detail table create query
    public static final String CREATE_QUERY_PICKTASK_DETAIL = "create table "
            + PICK_TASK_DETAIL + "(" + DETAIL_SLOT + " text,"
            + DETAIL_TQTY + " text, "
            + DETAIL_ORGTQTY + " text, "
            + DETAIL_TRKQTY + " text, "
            + DETAIL_ORGTRKQTY + " text, "
            + DETAIL_UOM + " text, "
            + DETAIL_ITEM + " text, "
            + DETAIL_DESC + " text, "
            + DETAIL_WLOTNO + " text, "
            + DETAIL_LOTNO + " text, "
            + DETAIL_TRANLINENO + " text, "
            + DETAIL_ORGTRANLINENO + " text, "
            + DETAIL_DOCTYPE + " text, "
            + DETAIL_DOCNO + " text, "
            + DETAIL_DOCLINENO + " text, "
            + DETAIL_ORGDOCLINENO + " text, "
            + DETAIL_DOCSTAT + " text, "
            + DETAIL_WEIGHT + " text, "
            + DETAIL_VOLUME + " text, "
            + DETAIL_DECNUM + " text, "
            + DETAIL_STKUMID + " text, "
            + DETAIL_CATCHWT + " text, "
            + DETAIL_UMFACT + " text, "
            + DETAIL_TSHIPPED + " text, "
            + DETAIL_TRKSHIPPED + " text, "
            + DETAIL_LBSHP + " text, "
            + DETAIL_PICKDURATION + " integer, "
            + DETAIL_LINESPLIT + " text, "
            + DETAIL_FLAG + " text, "
            + DETAIL_ROWNO + " integer, "
            + DETAIL_SUBSTITUTED_ITEM + " text, "
            + DETAIL_SUBSTITUTED_TRANNO + " text, "
            + DETAIL_IS_SUBSTITUTED + " text, "
            + DETAIL_ORG_SOITEM + " text, "
            + DETAIL_STAGINGSLOT + " text, "
            + DETAIL_TASKNO + " text, "
            + DETAIL_ISEDITED + " text, "
            + DETAIL_CHGQTY + " text, "
            + DETAIL_OTQTYPICKED + " text, "
            + DETAIL_PICKED + " text);";


    public static final String CREATE_QUERY_PICKTASK_REVERTDATA = "create table "
            + PICK_TASK_REVEROLDDATA + "(" + DETAIL_SLOT + " text,"
            + DETAIL_TQTY + " text, "
            + DETAIL_ORGTQTY + " text, "
            + DETAIL_TRKQTY + " text, "
            + DETAIL_ORGTRKQTY + " text, "
            + DETAIL_UOM + " text, "
            + DETAIL_ITEM + " text, "
            + DETAIL_DESC + " text, "
            + DETAIL_WLOTNO + " text, "
            + DETAIL_LOTNO + " text, "
            + DETAIL_TRANLINENO + " text, "
            + DETAIL_ORGTRANLINENO + " text, "
            + DETAIL_DOCTYPE + " text, "
            + DETAIL_DOCNO + " text, "
            + DETAIL_DOCLINENO + " text, "
            + DETAIL_ORGDOCLINENO + " text, "
            + DETAIL_DOCSTAT + " text, "
            + DETAIL_WEIGHT + " text, "
            + DETAIL_VOLUME + " text, "
            + DETAIL_DECNUM + " text, "
            + DETAIL_STKUMID + " text, "
            + DETAIL_CATCHWT + " text, "
            + DETAIL_UMFACT + " text, "
            + DETAIL_TSHIPPED + " text, "
            + DETAIL_TRKSHIPPED + " text, "
            + DETAIL_LBSHP + " text, "
            + DETAIL_PICKDURATION + " integer, "
            + DETAIL_LINESPLIT + " text, "
            + DETAIL_FLAG + " text, "
            + DETAIL_ROWNO + " integer, "
            + DETAIL_SUBSTITUTED_ITEM + " text, "
            + DETAIL_SUBSTITUTED_TRANNO + " text, "
            + DETAIL_IS_SUBSTITUTED + " text, "
            + DETAIL_ORG_SOITEM + " text, "
            + DETAIL_STAGINGSLOT + " text, "
            + DETAIL_TASKNO + " text, "
            + DETAIL_ISEDITED + " text, "
            + DETAIL_CHGQTY + " text, "
            + DETAIL_OTQTYPICKED + " text, "
            + DETAIL_PICKED + " text, "
            + DETAIL_TEMPQTY + " integer);";



    // PickTask WHIPTL table create query
    public static final String CREATE_QUERY_PICKTASK_WHIPTL = "create table "
            + PICK_TASK_WHIPTL + "(" + WHIPTL_PALNO + " text," + WHIPTL_TASKNO
            + " text, " + WHIPTL_PALSTAT + " text);";

    // PickTask WHMLOT table create query
    public static final String CREATE_QUERY_PICKTASK_WHMLOT = "create table "
            + PICK_TASK_WHMLOT + "(" + WHMLOT_ITEM + " text, " + WHMLOT_WLOTNO
            + " text, " + WHMLOT_LOTREFID + " text, " + WHMLOT_TASKNUM + " text);";

    // PickTask WHMSLT table create query
    public static final String CREATE_QUERY_PICKTASK_WHMSLT = "create table "
            + PICK_TASK_WHMSLT + "(" + WHMSLT_SLOT + " text, " + WHMSLT_LOCTID
            + " text, " + WHMSLT_SLOTTYPE + " text);";

    // PickTask WHMQTY table create query
    public static final String CREATE_QUERY_PICKTASK_WHMQTY = "create table "
            + PICK_TASK_WHMQTY + "(" + WHMQTY_ITEM + " text, " + WHMQTY_LOCTID
            + " text, " + WHMQTY_WLOTNO + " text, " + WHMQTY_SLOT
            + " text, " + WHMQTY_UMEASUR + " text, " + WHMQTY_TQTY + " text, " + WHMQTY_SLOTTYPE
            + " text, " + WHMQTY_TASKNUM + " text);";

    // PickTask WHITRLS table create query
    public static final String CREATE_QUERY_PICKTASK_WHITRLS = "create table "
            + PICK_TASK_WHITRLS + "(" + WHITRLS_TRAILER + " text, " + WHITRLS_RTESEQ
            + " text, " + WHITRLS_ROUTE + " text, " + WHITRLS_DOCK + " text);";

    // PickTask WHITRLS table create query
    public static final String CREATE_QUERY_PICKTASK_PRINTLABEL = "create table "
            + PICK_TASK_PRINTLABEL + "(" + PT_STOP + " text, " + PT_TRAILER
            + " text, " + PT_ROUTE + " text, " + PT_DOCK + " text, " + PT_DELDATE
            + " text, " + PT_ORDER + " text, " + PT_TASKNO + " text, " + PT_CUSTID
            + " text, " + PT_CUSTNAME + " text, " + PT_PICKER + " text, " + PT_PALNO + " text);";

    // LOAD Pick Pallet Summary table create query
    public static final String CREATE_QUERY_LOADPICKPALLET_SUMMARY = "create table "
            + LPP_SUMMARY + "(" + LPP_SUMMARY_WMSDate + " text, " + LPP_SUMMARY_TRUCK
            + " text, " + LPP_SUMMARY_DOCK + " text, " + LPP_SUMMARY_ROUTECNT + " text, " + LPP_SUMMARY_STOPCNT
            + " text, " + LPP_SUMMARY_PALCNT + " text, " + LPP_SUMMARY_ROWNO
            + " text, " + LPP_SUMMARY_FLAG + " text);";

    // LOAD Pick Pallet Details table create query
    public static final String CREATE_QUERY_LOADPICKPALLET_DETAILS = "create table "
            + LPP_DETAILS + "(" + LPP_DETAIL_WMSDate + " text, " + LPP_DETAIL_TASKNO
            + " text, " + LPP_DETAIL_PICKER + " text, " + LPP_DETAIL_ROUTE
            + " text, " + LPP_DETAIL_STOP + " text, " + LPP_DETAIL_TOTAL
            + " text, " + LPP_DETAIL_LOADED + " text, " + LPP_DETAIL_READY
            + " text, " + LPP_DETAIL_ROWNO + " text, " + LPP_DETAIL_FLAG + " text);";

    // LOAD Pick Pallet Route Details table create query
    public static final String CREATE_QUERY_LOADPICKPALLET_ROUTEDETAILS = "create table "
            + LPP_ROUTE_DETAILS + "(" + LPP_ROUTE_DETAIL_ROUTE + " text, " + LPP_ROUTE_DETAIL_TRUCK
            + " text, " + LPP_ROUTE_DETAIL_STATUS + " text, " + LPP_ROUTE_DETAIL_STOPCNT
            + " text, " + LPP_ROUTE_DETAIL_PALIN + " text, " + LPP_ROUTE_DETAIL_PALRDY
            + " text, " + LPP_ROUTE_DETAIL_PALCNT + " text);";

    // LOAD Pick Pallet WHIPLT table create query
    public static final String CREATE_QUERY_LOADPICKPALLET_WHIPLT = "create table "
            + LPP_WHIPLT + "(" + LPP_WHIPLT_WMSDATE + " text, " + LPP_WHIPLT_PALNO
            + " text, " + LPP_WHIPLT_TASKNO + " text, " + LPP_WHIPLT_PALSTAT
            + " text, " + LPP_WHIPLT_STGSLOT + " text);";

    // LOAD Pick Pallet Summary table create query
    public static final String CREATE_QUERY_LOADPICKPALLET_WHITRL = "create table "
            + LPP_WHITRL + "(" + LPP_WHITRL_WMSDATE + " text, " + LPP_WHITRL_TRAILER
            + " text, " + LPP_WHITRL_RTESEQ + " text, " + LPP_WHITRL_ROUTE
            + " text, " + LPP_WHITRL_DOCK + " text);";

    // create query for Receive Task list
    public static final String CREATE_QUERY_RECEIVETASK_LIST = "create table "
            + RECEIVE_TASK_LIST + "(" + RT_TASKNO + " text, " + RT_STATUS + " text, " + RT_USERID
            + " text, " + RT_DOCTYPE + " text, " + RT_DOCNO + " text);";

    //create query Receive Task Header
    public static final String CREATE_QUERY_RECEIVETASK_HEADER = "create table "
            + RECEIVE_TASK_HEADER + "(" + RTH_DESCRIP + " text, " + RTH_VENDDESCRIP + " text, " + RTH_CASECOUNTED + " text, " + RTH_CASETOTAL
            + " text, " + RTH_PLTOTAL + " text, " + RTH_PLTCOUNTED + " text, " + RTH_WMSDATE + " text);";

    //create query Receive Task Detail
    public static final String CREATE_QUERY_RECEIVETASK_DETAIL = "create table "
            + RECEIVE_TASK_DETAIL + "(" + RTD_TASKNO + " text, " + RTD_TRANLINENO + " text, " + RTD_DOCTYPE
            + " text, " + RTD_DOCNO + " text, " + RTD_DOCLINENO + " text, " + RTD_ITEM
            + " text, " + RTD_LOCTID + " text, " + RTD_WLOTNO + " text, " + RTD_UMEASUR
            + " text, " + RTD_WMSSTAT + " text, " + RTD_TQTYREC + " text, " + RTD_TRKQTYREC
            + " text, " + RTD_REVLEV + " text, " + RTD_TQTYINC + " text, " + RTD_ITMDESC
            + " text, " + RTD_PCKDESC + " text, " + RTD_COUNTRYID + " text, " + RTD_ITEMSHOW
            + " text, " + RTD_COLLECTION + " text, " + RTD_WELEMENT + " text, " + RTD_WIDGETID
            + " text, " + RTD_CATCHWT + " text,  " + RTD_DECNUM + " text, " + RTD_LOTREFID + " text, " + RTD_LINESPLIT
            + " text, " + RTD_FLAG + " text, " + RTD_ROWNO + " text, " + RTD_PALNO + " text);";

    //create query Move Task Detail
    public static final String CREATE_QUERY_MOVETASK_DETAIL = "create table "
            + MOVE_TASK_DETAIL + "(" + MTD_TASKNO + " text, " + MTD_TASKTYPE + " text, " + MTD_STATUS
            + " text, " + MTD_TRANLINENO + " text, " + MTD_CHILDID + " text, " + MTD_ITEM
            + " text, " + MTD_LOCTID + " text, " + MTD_WLOTNO + " text, " + MTD_PALNO
            + " text, " + MTD_UMEASUR + " text, " + MTD_TQTYRQ + " text, " + MTD_TQTYACT
            + " text, " + MTD_FROMSLOT + " text, " + MTD_TOSLOT + " text, " + MTD_ITEMDESC
            + " text, " + MTD_PCKDESC + " text, " + MTD_WHQTY + " text," + MTD_LOCKED + " text," + MTD_FLAG + " text," + MTD_EDITED + " text, " + MTD_ALLOCQTY + " text);";

    //create query Receive Task Detail
    public static final String CREATE_QUERY_RECEIVETASK_TRAN_DETAIL = "create table "
            + RECEIVE_TASK_TRAN_DETAIL + "(" + RTD_TASKNO + " text, " + RTD_TRANLINENO + " text, " + RTD_DOCTYPE
            + " text, " + RTD_DOCNO + " text, " + RTD_DOCLINENO + " text, " + RTD_ITEM
            + " text, " + RTD_LOCTID + " text, " + RTD_WLOTNO + " text, " + RTD_UMEASUR
            + " text, " + RTD_WMSSTAT + " text, " + RTD_TQTYREC + " text, " + RTD_TRKQTYREC
            + " text, " + RTD_REVLEV + " text, " + RTD_TQTYINC + " text, " + RTD_ITMDESC
            + " text, " + RTD_PCKDESC + " text, " + RTD_COUNTRYID + " text, " + RTD_ITEMSHOW
            + " text, " + RTD_COLLECTION + " text, " + RTD_WELEMENT + " text, " + RTD_WIDGETID
            + " text, " + RTD_CATCHWT + " text,  " + RTD_DECNUM + " text, " + RTD_LOTREFID + " text, " + RTD_LINESPLIT
            + " text, " + RTD_FLAG + " text, " + RTD_ROWNO + " text, " + RTD_PALNO + " text);";
    //create query Receive Task Item Class
    public static final String CREATE_QUERY_RECEIVETASK_ITEMCLASS = "create table "
            + RECEIVE_TASK_ITEM_CLASS + "(" + RTIC_ITMCLSS + " text, " + RTIC_DESCRIP + " text, " + RTIC_WELEMENT
            + " text, " + RTIC_COLLECTION + " text, " + RTIC_WIDGETID + " text);";

    //create query Receive Task Item Class
    public static final String CREATE_QUERY_RECEIVETASK_LOADTYPE = "create table "
            + RECEIVE_TASK_LOAD_TYPE + "(" + RTLT_LOADTYPE + " text, " + RTLT_DESCRIP + " text, " + RTLT_WELEMENT
            + " text, " + RTLT_COLLECTION + " text, " + RTLT_WIDGETID + " text, " + RTLT_LOADID + " text, " + RTLT_LOADTYPSTS
            + " text, " + RTLT_WMSDATE + " text, " + RTLT_METRICVAL + " text, " + RTLT_FLAG + " text);";

    //create query Receive Task WHMSLT
    public static final String CREATE_QUERY_RECEIVETASK_WHMSLT = "create table "
            + RECEIVE_TASK_WHMSLT + "(" + RTWHMSLT_SLOT + " text);";

    //create query Receive Task WHRPLT
    public static final String CREATE_QUERY_RECEIVETASK_WHRPLT = "create table "
            + RECEIVE_TASK_WHRPLT + "(" + RTWHRPLT_TASKNO + " text, " + RTWHRPLT_TASKLINENO + " text, " + RTWHRPLT_PLTLINENO
            + " text, " + RTWHRPLT_TQTY + " text, " + RTWHRPLT_PLTSTAT + " text, " + RTWHRPLT_PRTPLTTAG
            + " text, " + RTWHRPLT_TRKQTY + " text, " + RTWHRPLT_SLOT + " text, " + RTWHRPLT_FLAG + " text, " + RTWHRPLT_GTIN + " text);";

    //create query Receive Task WHRPLT
    public static final String CREATE_QUERY_RECEIVETASK_TRAN_WHRPLT = "create table "
            + RECEIVE_TASK_TRAN_WHRPLT + "(" + RTWHRPLT_TASKNO + " text, " + RTWHRPLT_TASKLINENO + " text, " + RTWHRPLT_PLTLINENO
            + " text, " + RTWHRPLT_TQTY + " text, " + RTWHRPLT_PLTSTAT + " text, " + RTWHRPLT_PRTPLTTAG
            + " text, " + RTWHRPLT_TRKQTY + " text, " + RTWHRPLT_SLOT + " text, " + RTWHRPLT_FLAG + " text, " + RTWHRPLT_GTIN + " text);";

    //create query Receive Task WHRPLT
    public static final String CREATE_QUERY_RECEIVETASK_EXPORT = "create table "
            + RECEIVE_TASK_EXPORT + "(" + RTE_TASKNO + " text, " + RTE_TRANLINENO + " text, " + RTE_DOCTYPE
            + " text, " + RTE_DOCNO + " text, " + RTE_DOCLINENO + " text, " + RTE_ITEM
            + " text, " + RTE_UMEASUR + " text, " + RTE_LOCTID + " text, " + RTE_WLOTNO
            + " text, " + RTE_LOTREFID + " text, " + RTE_TQTY + " text, " + RTE_TRKQTY
            + " text, " + RTE_PLTLINENO + " text, " + RTE_PTQTY
            + " text, " + RTE_PTRKQTY + " text, " + RTE_PRTPLTTAG + " text, " + RTE_SLOT + " text, " + RTE_GTIN + " text);";

    //create query Receive Task WHRPLT
    public static final String CREATE_QUERY_RECEIVETASK_PRINT = "create table "
            + RECEIVE_TASK_PRINT + "(" + RTP_WLOTNO + " text, " + RTP_LOTREFID + " text, " + RTP_ITEM
            + " text, " + RTP_RECDATE + " text, " + RTP_EXPDATE + " text, " + RTP_RECUSER
            + " text, " + RTP_TASKNO + " text, " + RTP_TASKLINENO + " text, " + RTP_PLTLINENO
            + " text, " + RTP_PRTPLTTAG + " text, " + RTP_TQTY + " text, " + RTP_TRKQTY
            + " text, " + RTP_ITMDESC + " text, " + RTP_CATCHWT + " text);";

    // Physical Count Slot table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_SLOT_TABLE = "create table "
            + PHYSCIAL_COUNT_SLOT_TABLE + "(" + PHYSCIAL_COUNT_SLOT + " text," + PHYSCIAL_COUNT_WMSSTAT
            + " text," + PHYSCIAL_COUNT_POSTED + " text, " + PHYSCIAL_COUNT_GRPCNT + " text, " + PHYSCIAL_COUNT_STATUS
            + " text, " + PHYSCIAL_COUNT_DOCLINECOUNT + " text, " + PHYSCIAL_COUNT_ROWNO + " text);";

    // Physical Count Detail table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_DETAIL_TABLE = "create table "
            + PHYSCIAL_COUNT_DETAIL_TABLE + "(" + PC_DETAIL_SLOT + " text," + PC_DETAIL_COUNTID + " text," + PC_DETAIL_PAGE
            + " text," + PC_DETAIL_DOCLINENO + " text," + PC_DETAIL_LOCTID + " text," + PC_DETAIL_ITEM
            + " text," + PC_DETAIL_INVTYPE + " text," + PC_DETAIL_WLOTNO + " text," + PC_DETAIL_UMEASUR + " text," + PC_DETAIL_TCOUNTQTY
            + " text," + PC_DETAIL_WMSSTAT + " text," + PC_DETAIL_POSTED + " text," + PC_DETAIL_ITMDESC + " text," + PC_DETAIL_PCKDESC
            + " text," + PC_DETAIL_DECNUM + " text," + PC_DETAIL_LOTREFID + " text," + PC_DETAIL_TQTY
            + " text," + PC_DETAIL_ITEMSHOW + " text," + PC_DETAIL_SURPRISADD + " text," + PC_DETAIL_USERID
            + " text," + PC_DETAIL_COUNTED + " text," + PC_DETAIL_COLLECTION + " text," + PC_DETAIL_WELEMENT
            + " text," + PC_DETAIL_WIDGETID + " text," + PC_DETAIL_ROWNO + " text," + PC_DETAIL_FLAG + " text, " + PC_DETAIL_UPDATEDNO + " text);";

    // Physical Count Tran Detail table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_TRAN_DETAIL_TABLE = "create table "
            + PHYSCIAL_COUNT_DETAIL_TRAN_TABLE + "(" + PC_DETAIL_SLOT + " text," + PC_DETAIL_COUNTID + " text," + PC_DETAIL_PAGE
            + " text," + PC_DETAIL_DOCLINENO + " text," + PC_DETAIL_LOCTID + " text," + PC_DETAIL_ITEM
            + " text," + PC_DETAIL_INVTYPE + " text," + PC_DETAIL_WLOTNO + " text," + PC_DETAIL_UMEASUR + " text," + PC_DETAIL_TCOUNTQTY
            + " text," + PC_DETAIL_WMSSTAT + " text," + PC_DETAIL_POSTED + " text," + PC_DETAIL_ITMDESC + " text," + PC_DETAIL_PCKDESC
            + " text," + PC_DETAIL_DECNUM + " text," + PC_DETAIL_LOTREFID + " text," + PC_DETAIL_TQTY
            + " text," + PC_DETAIL_ITEMSHOW + " text," + PC_DETAIL_SURPRISADD + " text," + PC_DETAIL_USERID
            + " text," + PC_DETAIL_COUNTED + " text," + PC_DETAIL_COLLECTION + " text," + PC_DETAIL_WELEMENT
            + " text," + PC_DETAIL_WIDGETID + " text," + PC_DETAIL_ROWNO + " text," + PC_DETAIL_FLAG + " text, " + PC_DETAIL_UPDATEDNO + " text);";


    // Physical Count UOM table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_UOM_TABLE = "create table "
            + PHYSCIAL_COUNT_UOM_TABLE + "(" + PC_UOM_ITEM + " text," + PC_UOM_UMEASUR + " text);";

    // Physical Count WHMLOT table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_WHMLOT_TABLE = "create table "
            + PHYSCIAL_COUNT_WHMLOT_TABLE + "(" + PC_WHMLOT_WLOTNO + " text, "+ PC_WHMLOT_ITEM + " text, "+ PC_WHMLOT_LOTREFID + " text);";

    // Physical Count WHMQTY table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_WHMQTY_TABLE = "create table "
            + PHYSCIAL_COUNT_WHMQTY_TABLE + "(" + PC_WHMQTY_SLOT + " text," + PC_WHMQTY_ITEM
            + " text," + PC_WHMQTY_WLOTNO + " text," + PC_WHMQTY_UMEASUR + " text);";

    // Physical Count ICITEM table create query
    public static final String CREATE_QUERY_PHYSICALCOUNT_ICITEM_TABLE = "create table "
            + PHYSCIAL_COUNT_ICITEM_TABLE + "(" + PC_ICITEM_ITEM + " text," + PC_ICITEM_INVTYPE  + " text," + PC_ICITEM_STKUMID  + " text," + PC_ICITEM_PUNMSID
            + " text, " + PC_ICITEM_SUNMSID + " text," + PC_ICITEM_BRNUM1  + " text," + PC_ICITEM_BRNUM2
            + " text, " + PC_ICITEM_BRNAM3 + " text," + PC_ICITEM_BRNAM4  + " text," + PC_ICITEM_BRNAM5
            + " text, " + PC_ICITEM_ITMDESC + " text," + PC_ICITEM_PCKDESC
            + " text, " + PC_ICITEM_DECNUM + " text, " + PC_ICITEM_ITEMSHOW + " text);";// Physical Count ICITEM table create query



    //Move Manually table create query
    public static final String CREATE_QUERY_MOVE_MANUALLY_TABLE = "create table "
            + MOVE_MANUALLY_TABLE + "(" + MOVE_MANUALLY_WLOTNO + " text," + MOVE_MANUALLY_ITEMNO  + " text," + MOVE_MANUALLY_SLOTNO
            + " text," + MOVE_MANUALLY_LOCTID + " text, " + MOVE_MANUALLY_UOM + " text," + MOVE_MANUALLY_QTY  + " text," + MOVE_MANUALLY_TRKQTY
            + " text, " + MOVE_MANUALLY_ISLOCKED + " text, " + MOVE_MANUALLY_ITMDESC + " text," + MOVE_MANUALLY_RPALLOC + " text, " + MOVE_MANUALLY_CATCHWT
            + " text, " + MOVE_MANUALLY_LOTREFID + " text);";

    //Move Manually Transaction table create query
    public static final String CREATE_QUERY_MOVE_MANUALLY_TRANSACTION_TABLE = "create table "
            + MOVE_MANUALLY_TRANSACTION_TABLE + "(" + MOVE_MANUALLY_TRANSACTION_WLOTNO + " text," + MOVE_MANUALLY_TRANSACTION_ITEMNO
            + " text," + MOVE_MANUALLY_TRANSACTION_SLOTNO + " text," + MOVE_MANUALLY_TRANSACTION_LOCTID + " text, "
            + MOVE_MANUALLY_TRANSACTION_UOM + " text," + MOVE_MANUALLY_TRANSACTION_QTY  + " text," + MOVE_MANUALLY_TRANSACTION_TRKQTY
            + " text, " + MOVE_MANUALLY_TRANSACTION_EQTY + " text, " + MOVE_MANUALLY_TRANSACTION_ITMDESC + " text, "
            + MOVE_MANUALLY_TRANSACTION_CATCHWT + " text, " + MOVE_MANUALLY_TRANSACTION_LotrefId + " text);";

    //Breaker UOM table create query
    public static final String CREATE_QUERY_BREAKER_UOM_TABLE = "create table "
            + BREAKER_UOM_UTILITY_TABLE + "(" + BREAKER_UOM_WLOTNO + " text," + BREAKER_UOM_ITEMNO  + " text," + BREAKER_UOM_SLOTNO
            + " text," + BREAKER_UOM_LOCTID + " text, " + BREAKER_UOM_UOM + " text," + BREAKER_UOM_QTY  + " text," + BREAKER_UOM_TRKQTY
            + " text, " + BREAKER_UOM_ISLOCKED + " text, " + BREAKER_UOM_ITMDESC + " text, "
            + BREAKER_UOM_CATCHWT + " text, " + BREAKER_UOM_STKUMID + " text, " + BREAKER_UOM_LOTREFID + " text);";

    //Breaker UOM List table create query
    public static final String CREATE_QUERY_BREAKER_UOM_LIST_TABLE = "create table "
            + BREAKER_UOM_LIST_TABLE + "(" + BREAKER_UOM_LIST_BRNAME + " text," + BREAKER_UOM_LIST_BRUNIT  + " text);";

    /*//RepackFg table create query       //01/02/23_CIRK_For Fresha to APN
    public static final String CREATE_REPACKFG_TABLE = " create table "
            + REPACKFG_TABLE + "(" + REPACKFG_PANO + " text," + REPACKFG_TRANLINENO
            + " text," + REPACKFG_ITEM   + " text," + REPACKFG_DESCRIP   + " text," + REPACKFG_UMEASUR
            + " text, " + REPACKFG_LOCTID  + " text," + REPACKFG_LOTNO   + " text," + REPACKFG_SERIAL
            + " text, " + REPACKFG_QTYMADE  + " text," + REPACKFG_COST   + " text," + REPACKFG_PADATE
            + " text, " + REPACKFG_PASTAT  + " text," + REPACKFG_LCKSTAT + " text, " + REPACKFG_LCKUSER
            + " text, " + REPACKFG_LCKDATE + " text," + REPACKFG_LCKTIME + " text," + REPACKFG_ADDUSER
            + " text," + REPACKFG_ADDDATE  + " text, " + REPACKFG_ADDTIME  + " text," + REPACKFG_PRODLBL
            + " text," + REPACKFG_PACKCHG + " text, " + REPACKFG_WASHCHG  + " text, " + REPACKFG_COUNTRYID
            + " text, " + REPACKFG_VENDNO + " text," + REPACKFG_GRADE   + " text," + REPACKFG_PROJNO
            + " text," + REPACKFG_REMARKS  + " text, " + REPACKFG_LCSTQTY  + " text," + REPACKFG_CASE_PL
            + " text," + REPACKFG_PALNO + " text, " + REPACKFG_SETID  + " text, " + REPACKFG_WEIGHT
            + " text, " + REPACKFG_PALLET  + " text, " + REPACKFG_ID_COL  + " text, " + REPACKFG_BINNO
            + " text," + REPACKFG_POSTPRG   + " text," + REPACKFG_EXTPALLET + " text," + REPACKFG_EXTCUBE
            + " text, " + REPACKFG_EXTWEIGHT  + " text," + REPACKFG_BEXTLCST   + " text," + REPACKFG_EXTLCST
            + " text, " + REPACKFG_BEXTFEES  + " text, " + REPACKFG_EXTFEES  + " text, " + REPACKFG_TPALLET
            + " text, " + REPACKFG_TCUBE  + " text, " + REPACKFG_TWEIGHT  + " text," + REPACKFG_WLOTNO
            + " text," + REPACKFG_ORIGTRANLN + " text," + REPACKFG_ORIGTRANL  + " text," + REPACKFG_ORIGDOCLN
            + " text," + REPACKFG_STKUMID + " text," + REPACKFG_USELOTS  + " text," + REPACKFG_UMFACT
            + " text," + REPACKFG_WEIGHT1  + " text," + REPACKFG_VOLUME + " text," + REPACKFG_CATCHWT
            + " text," + REPACKFG_LOTREFID  + " text," + REPACKFG_LOTEXPL  + " text," + REPACKFG_LINESPLIT
            + " text," + REPACKFG_TRKQTYPK  + " text," + REPACKFG_UPDFLAG + " text," + REPACKFG_VPLOCKED + " text);";
*/
    /*//RepackIngredients table create query      //01/02/23_CIRK_For Fresha to APN
    public static final String CREATE_REPACK_INGREDIENT_TABLE = " create table "
            + REPACK_INGREDIENT_TABLE + "(" + RIT_PANO + " text," + RIT_TRANLINENO
            + " text," + RIT_ITEM   + " text," + RIT_DESCRIP   + " text," + RIT_UMEASUR
            + " text, " + RIT_LOCTID  + " text," + RIT_LOTNO   + " text," + RIT_SERIAL
            + " text, " + RIT_QTYUSED  + " text," + RIT_COST   + " text," + RIT_PADATE
            + " text, " + RIT_PASTAT  + " text," + RIT_LCKSTAT + " text, " + RIT_LCKUSER
            + " text, " + RIT_LCKDATE + " text," + RIT_LCKTIME + " text," + RIT_ADDUSER
            + " text," + RIT_ADDDATE  + " text, " + RIT_ADDTIME  + " text," + RIT_COUNTRYID
            + " text, " +RIT_VENDNO + " text," + RIT_REMARKS  + " text, " + RIT_PALNO
            + " text, " + RIT_SETID  + " text, " + RIT_WEIGHT + " text, " + RIT_ID_COL
            + " text, " + RIT_BINNO + " text," + RIT_WLOTNO + " text," + RIT_ORIGTRANLN
            + " text," + RIT_STKUMID + " text," + RIT_USELOTS  + " text," + RIT_UMFACT
            + " text," + RIT_WEIGHT1 + " text," + RIT_VOLUME + " text," + RIT_CATCHWT
            + " text," + RIT_LOTREFID + " text," + RIT_LOTEXPL  + " text," + RIT_LINESPLIT
            + " text," + RIT_TRKQTYPK + " text," + RIT_YIELD + " text," + RIT_UPDFLAG
            + " text, " + RIT_ADDFLAG + " text," + RIT_SLOT + " text," + RIT_ALLOCQTY
            + " text, " + RIT_TEMPALLOC + " text," + RIT_WHQTY + " text," + RIT_ICQTY + " text," + RIT_RPALLOCQTY + " text);";
*/



    //01/02/23_CIRK_For Fresha to APN
    /*public static final String CREATE_REPACK_PICKLIST_TABLE = "create table "
            + REPACK_PICKLIST_TABLE + "(" + RPL_ADDTIME + " addtime, " + RPL_LOCTID
            + " loctid, " + RPL_PAFDATE + " padate, " + RPL_PANO + " pano);";*/



    //RepackFg table create query
    public static final String CREATE_REPACKFG_TABLE = " create table "
            + REPACKFG_TABLE + "(" + REPACKFG_PANO + " text," + REPACKFG_TRANLINENO
            + " text," + REPACKFG_ITEM   + " text," + REPACKFG_DESCRIP   + " text," + REPACKFG_UMEASUR
            + " text, " + REPACKFG_LOCTID  + " text," + REPACKFG_LOTNO   + " text," + REPACKFG_SERIAL
            + " text, " + REPACKFG_QTYMADE  + " text," + REPACKFG_COST   + " text," + REPACKFG_PADATE
            + " text, " + REPACKFG_PASTAT  + " text," + REPACKFG_LCKSTAT + " text, " + REPACKFG_LCKUSER
            + " text, " + REPACKFG_LCKDATE + " text," + REPACKFG_LCKTIME + " text," + REPACKFG_ADDUSER
            + " text," + REPACKFG_ADDDATE  + " text, " + REPACKFG_ADDTIME  + " text," + REPACKFG_PRODLBL
            + " text," + REPACKFG_PACKCHG + " text, " + REPACKFG_WASHCHG  + " text, " + REPACKFG_COUNTRYID
            + " text, " + REPACKFG_VENDNO + " text," + REPACKFG_GRADE   + " text," + REPACKFG_PROJNO
            + " text," + REPACKFG_REMARKS  + " text, " + REPACKFG_LCSTQTY  + " text," + REPACKFG_CASE_PL
            + " text," + REPACKFG_PALNO + " text, " + REPACKFG_SETID  + " text, " + REPACKFG_WEIGHT
            + " text, " + REPACKFG_PALLET  + " text, " + REPACKFG_ID_COL  + " text, " + REPACKFG_BINNO
            + " text," + REPACKFG_POSTPRG   + " text," + REPACKFG_EXTPALLET + " text," + REPACKFG_EXTCUBE
            + " text, " + REPACKFG_EXTWEIGHT  + " text," + REPACKFG_BEXTLCST   + " text," + REPACKFG_EXTLCST
            + " text, " + REPACKFG_BEXTFEES  + " text, " + REPACKFG_EXTFEES  + " text, " + REPACKFG_TPALLET
            + " text, " + REPACKFG_TCUBE  + " text, " + REPACKFG_TWEIGHT  + " text," + REPACKFG_WLOTNO
            + " text," + REPACKFG_ORIGTRANLN + " text," + REPACKFG_ORIGTRANL  + " text," + REPACKFG_ORIGDOCLN
            + " text," + REPACKFG_STKUMID + " text," + REPACKFG_USELOTS  + " text," + REPACKFG_UMFACT
            + " text," + REPACKFG_WEIGHT1  + " text," + REPACKFG_VOLUME + " text," + REPACKFG_CATCHWT
            + " text," + REPACKFG_LOTREFID  + " text," + REPACKFG_LOTEXPL  + " text," + REPACKFG_LINESPLIT
            + " text," + REPACKFG_TRKQTYPK  + " text," + REPACKFG_UPDFLAG + " text," + REPACKFG_ADDFLAG + " text," + REPACKFG_DECNUM + " text," + REPACKFG_USEDQTY + " text," + REPACKFG_VPLOCKED + " text);";





    //RepackIngredients table create query
    public static final String CREATE_REPACK_INGREDIENT_TABLE = " create table "
            + REPACK_INGREDIENT_TABLE + "(" + RIT_PANO + " text," + RIT_TRANLINENO
            + " text," + RIT_ITEM   + " text," + RIT_DESCRIP   + " text," + RIT_UMEASUR
            + " text, " + RIT_LOCTID  + " text," + RIT_LOTNO   + " text," + RIT_SERIAL
            + " text, " + RIT_QTYUSED  + " text," + RIT_COST   + " text," + RIT_PADATE
            + " text, " + RIT_PASTAT  + " text," + RIT_LCKSTAT + " text, " + RIT_LCKUSER
            + " text, " + RIT_LCKDATE + " text," + RIT_LCKTIME + " text," + RIT_ADDUSER
            + " text," + RIT_ADDDATE  + " text, " + RIT_ADDTIME  + " text," + RIT_COUNTRYID
            + " text, " +RIT_VENDNO + " text," + RIT_REMARKS  + " text, " + RIT_PALNO
            + " text, " + RIT_SETID  + " text, " + RIT_WEIGHT + " text, " + RIT_ID_COL
            + " text, " + RIT_BINNO + " text," + RIT_WLOTNO + " text," + RIT_ORIGTRANLN
            + " text," + RIT_STKUMID + " text," + RIT_USELOTS  + " text," + RIT_UMFACT
            + " text," + RIT_WEIGHT1 + " text," + RIT_VOLUME + " text," + RIT_CATCHWT
            + " text," + RIT_LOTREFID + " text," + RIT_LOTEXPL  + " text," + RIT_LINESPLIT
            + " text," + RIT_TRKQTYPK + " text," + RIT_YIELD + " text," + RIT_UPDFLAG
            + " text, " + RIT_ADDFLAG + " text," + RIT_SLOT + " text," + RIT_ALLOCQTY
            + " text, " + RIT_TEMPALLOC + " text," + RIT_WHQTY + " text," + RIT_ICQTY + " text,"
            + RIT_RPALLOCQTY + " text, " + RIT_ORGPANO +" text);";


    //RepackIngredients table create query
    public static final String CREATE_REPACK_FINISHED_GOODS_TABLE = " create table "
            + REPACK_FINSIHED_GOODS_TABLE + "(" + RIT_ITEM + " text," + RIT_DESCRIP
            + " text, " + RIT_UMEASUR + " text," + RIT_LOCTID + " text," + RIT_LOTNO + " text,"
            + RIT_TRANLINENO + " integer primary key autoincrement, " + RIT_COUNTRYID + " text, " + RIT_QTYUSED +" text);";

    public static final String CREATE_REPACK_PICKLIST_TABLE = "create table "
            + REPACK_PICKLIST_TABLE + "(" + RPL_ADDTIME + " addtime, " + RPL_LOCTID
            + " loctid, " + RPL_PAFDATE + " padate, " + RPL_PANO + " pano);";


    public static final String CREATE_RECEIVE_SLOTLIST_TABLE = "create table "
            + RECEIVE_SLOT_LIST + "(" + RSL_ITEM + " text, " + RSL_SLOT
            + " text,"+ RSL_TASKNO +" text);";

    public static final String CREATE_MOVE_TASK_SLOTLIST_TABLE = "create table "
            + MOVE_TASK_SLOT_LIST + "(" + MTSL_TASKNO + " text," + MTSL_LOCTID + " text, " + MTSL_SLOT
            + " text,"+ MTSL_SLOTTYPE +" text);";


    public static final String CREATE_SALES_ORDER_PICKLIST_TABLE = "create table "
            + SALES_ORDER_PICKLIST_TABLE + "(" + SOL_SONO + " text," + SOL_SODATE + " text, " + SOL_CUSTNO
            + " text,"+ SOL_ORDAMT +" text);";


    // RESETITEM DETAIL table field definition
    public static final String CREATE_QUERY_RESETITEM_DETAIL_TABLE = "create table "
            + RESETITEM_DETAIL + "(" + RID_ITEM + " text," + RID_LOCTID  + " text," + RID_WLOTNO  + " text," + RID_LOTNO
            + " text, " + RID_PALNO + " text," + RID_SLOT  + " text," + RID_UMEASUR
            + " text, " + RID_TQTY + " text," + RID_RPALLOCQTY  + " text," + RID_WHQTY
            + " text, " + RID_QTY + " text," + RID_PSOALOC  + " text," + RID_PRPALOC
            + " text, " + RID_PTFROUT + " text," + RID_PTMALOC  + " text, " + RID_ICQTY  + " text," + RID_ITMDESC  + " text, " + RID_OPENREPACKCOUNT
            + " text, " + RID_OPENSOCOUNT + " text);";



    public static final String CREATE_PICKTASK_SCANPALLET = "create table "
            + PICK_TASK_SCANPALLET + "(" + PTSP_ITEM + " text," + PTSP_LOCTID   + " text," + PTSP_WLOTNO  + " text," + PTSP_LOTNO
            + " text, " + PTSP_LOTREFID + " text," + PTSP_SLOT  + " text," + PTSP_UMEASUR
            + " text, " + PTSP_TQTY + " text," + PTSP_RPALLOCQTY  + " text," + PTSP_WHQTY
            + " text, " + PTSP_ICQTY + " text," + PTSP_ITMDESC
            + " text, " + PTSP_WEIGHT + " text," + PTSP_COUNTRYID
            + " text, " + PTSP_SERIAL + " text," + PTSP_VOLUME
            + " text, " + PTSP_CATCHWT + " text," + PTSP_STKUMID
            + " text, " + PTSP_USELOTS + " text," + PTSP_UMFACT
            + " text, " + PTSP_SETID + " text," + PTSP_VENDNO+ " text,"
            + PTSP_COST + " text);";


    // Company table create query
    public static final String CREATE_QUERY_MOVETASK = "create table "
            + Move_TASK_LIST_TABLE + "(" + MT_TASK_NO + " text, " + MT_ROWPRTY
            + " text, " + MT_TASK_TYPE + " text, " + MT_STATUS + " text);";

    //create query print pallet tag
    public static final String CREATE_QUERY_PRINT_PALLET_DETAILS = "create table "
            + PRINT_PALLET_DETAILS + "(" + PPD_ITEM + " text, " + PPD_LOTNO + " text, " + PPD_PALNO
            + " text, " + PPD_COUNTRYID + " text, " + PPD_OUTPALNO + " text, " + PPD_ITEMDESC
            + " text, " + PPD_COMMOD + " text," + PPD_PLTCREA + " text," + PPD_SETID + " text," + PPD_GRADE
            + " text," + PPD_VARIETY + " text," + PPD_LABEL + " text," + PPD_SIZESORT + " text," + PPD_SIZE + " text," + PPD_PACKAGE
            + " text, " + PPD_QTY + " text);";


    public static final String DELETE_QUERY = "DROP TABLE IF EXISTS ";
    public static final String DELETE_APP_CONFIG = DELETE_QUERY + CONFIGSETTINGS_TABLE;
    public static final String DELETE_COMPANY = DELETE_QUERY + COMPANY_TABLE;
    public static final String DELETE_LOCATION = DELETE_QUERY + LOCATION_TABLE;
    public static final String DELETE_SESSION = DELETE_QUERY + SESSION_TABLE;
    public static final String DELETE_NOTIFICATION = DELETE_QUERY + NOTIFICATION_COUNT_TABLE;
    public static final String DELETE_PICKTASK = DELETE_QUERY + PICK_TASK_LIST_TABLE;
    public static final String DELETE_PICKTASK_HEADER = DELETE_QUERY + PICK_TASK_HEADER;
    public static final String DELETE_PICKTASK_DETAIL = DELETE_QUERY + PICK_TASK_DETAIL;
    public static final String DELETE_PICKTASK_WHIPTL = DELETE_QUERY + PICK_TASK_WHIPTL;
    public static final String DELETE_PICKTASK_WHMLOT = DELETE_QUERY + PICK_TASK_WHMLOT;
    public static final String DELETE_PICKTASK_WHMSLT = DELETE_QUERY + PICK_TASK_WHMSLT;
    public static final String DELETE_PICKTASK_WHMQTY = DELETE_QUERY + PICK_TASK_WHMQTY;
    public static final String DELETE_PICKTASK_WHITRLS = DELETE_QUERY + PICK_TASK_WHITRLS;

    public static final String DELETE_LPP_SUMMARY = DELETE_QUERY + LPP_SUMMARY;
    public static final String DELETE_LPP_DETAILS = DELETE_QUERY + LPP_DETAILS;
    public static final String DELETE_LPP_ROUTE_DETAILS = DELETE_QUERY + LPP_ROUTE_DETAILS;
    public static final String DELETE_LPP_WHIPLT = DELETE_QUERY + LPP_WHIPLT;
    public static final String DELETE_LPP_WHITRL = DELETE_QUERY + LPP_WHITRL;

    public WMSDbHelper(Context context) {
        //super(context, DATABASE_NAME, null  , DATABASE_VERSION);
        this.mContext = context;
        File root_path = Environment.getExternalStorageDirectory();
        DATABASE_PATH =new File(root_path.getAbsoluteFile() + "/"
                + "Android/WMS");
        if(!DATABASE_PATH.exists())
        {
            DATABASE_PATH.mkdirs();
        }
        fPath = new File(DATABASE_PATH, DATABASE_NAME);
        if(!fPath.exists()){
            mSqlitedb = SQLiteDatabase.openOrCreateDatabase(DATABASE_PATH
                    + File.separator + DATABASE_NAME,null);
            try{
                mSqlitedb.beginTransaction();
                mSqlitedb.execSQL(CREATE_QUERY_CONFIG_SETTINGS);
                mSqlitedb.execSQL(CREATE_QUERY_COMPANY);
                mSqlitedb.execSQL(CREATE_QUERY_LOCATION);
                mSqlitedb.execSQL(CREATE_QUERY_SESSION);
                mSqlitedb.execSQL(CREATE_QUERY_NOTIFICATION);
                mSqlitedb.execSQL(CREATE_QUERY_MENULIST);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_HEADER);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_DETAIL);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_REVERTDATA);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_WHIPTL);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_WHMLOT);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_WHMSLT);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_WHMQTY);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_WHITRLS);
                mSqlitedb.execSQL(CREATE_QUERY_PICKTASK_PRINTLABEL);
                mSqlitedb.execSQL(CREATE_QUERY_EXPORT_TABLE);
                mSqlitedb.execSQL(CREATE_PICKTASK_SCANPALLET);
                mSqlitedb.execSQL(CREATE_RECEIVE_SLOTLIST_TABLE);

                mSqlitedb.execSQL(CREATE_QUERY_LOADPICKPALLET_SUMMARY);
                mSqlitedb.execSQL(CREATE_QUERY_LOADPICKPALLET_DETAILS);
                mSqlitedb.execSQL(CREATE_QUERY_LOADPICKPALLET_ROUTEDETAILS);
                mSqlitedb.execSQL(CREATE_QUERY_LOADPICKPALLET_WHIPLT);
                mSqlitedb.execSQL(CREATE_QUERY_LOADPICKPALLET_WHITRL);

                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_LIST);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_HEADER);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_DETAIL);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_TRAN_DETAIL);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_ITEMCLASS);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_LOADTYPE);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_WHMSLT);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_WHRPLT);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_TRAN_WHRPLT);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_EXPORT);
                mSqlitedb.execSQL(CREATE_QUERY_RECEIVETASK_PRINT);

                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_SLOT_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_DETAIL_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_TRAN_DETAIL_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_UOM_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_WHMLOT_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_WHMQTY_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PHYSICALCOUNT_ICITEM_TABLE);

                mSqlitedb.execSQL(CREATE_QUERY_MOVE_MANUALLY_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_MOVE_MANUALLY_TRANSACTION_TABLE);

                mSqlitedb.execSQL(CREATE_QUERY_BREAKER_UOM_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_BREAKER_UOM_LIST_TABLE);

                /*mSqlitedb.execSQL(CREATE_REPACKFG_TABLE);
                mSqlitedb.execSQL(CREATE_REPACK_INGREDIENT_TABLE);
                mSqlitedb.execSQL(CREATE_REPACK_PICKLIST_TABLE);*/


                mSqlitedb.execSQL(CREATE_REPACKFG_TABLE);
                mSqlitedb.execSQL(CREATE_REPACK_INGREDIENT_TABLE);
                mSqlitedb.execSQL(CREATE_REPACK_FINISHED_GOODS_TABLE);
                mSqlitedb.execSQL(CREATE_REPACK_PICKLIST_TABLE);


                mSqlitedb.execSQL(CREATE_QUERY_MOVETASK);
                mSqlitedb.execSQL(CREATE_QUERY_MOVETASK_DETAIL);
                mSqlitedb.execSQL(CREATE_MOVE_TASK_SLOTLIST_TABLE);


                mSqlitedb.execSQL(CREATE_SALES_ORDER_PICKLIST_TABLE);


                mSqlitedb.execSQL(CREATE_QUERY_RESETITEM_DETAIL_TABLE);

                mSqlitedb.execSQL(CREATE_SO_CHARGES_CSTEGORY_TABLE);
                mSqlitedb.execSQL(CREATE_QUERY_PRINT_PALLET_DETAILS);

                mSqlitedb.setTransactionSuccessful();
            }
            catch (Exception e) {
                mErrCode = "Error 001";
                mMsg = "Table Creation failed.";
                mErrMsg = mErrCode + " : " + mMsg;
                LogfileCreator.mAppendLog(mErrMsg);
            } finally {
                mSqlitedb.endTransaction();
            }

            mSqlitedb.close();
        }
    }
    public void openReadableDatabase()
    {
        if(fPath.exists()){
            mSqlitedb = SQLiteDatabase.openDatabase(DATABASE_PATH
                            + File.separator + DATABASE_NAME,
                    null,SQLiteDatabase.OPEN_READONLY);

            LogfileCreator.mAppendLog("Db opened" +" : "+ "Readable Db opened.");
        }
    }

    public void closeDatabase()
    {
        this.mSqlitedb.close();
        LogfileCreator.mAppendLog("Db closed" +" : "+ "Database closed.");
    }

    public void openWritableDatabase()
    {
        if(fPath.exists()) {
            mSqlitedb = SQLiteDatabase.openDatabase(DATABASE_PATH
                            + File.separator + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);

            LogfileCreator.mAppendLog("Db opened" +" : "+ "Writable Db opened.");
            Log.i("Db opened", "Writable Db opened.");
        }
    }

    public SQLiteDatabase getWritableDatabase()
    {
        if(fPath.exists()) {
            mSqlitedb = SQLiteDatabase.openDatabase(DATABASE_PATH
                            + File.separator + DATABASE_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);

            Log.i("Db opened", "Writable Db opened.");
        }
        return mSqlitedb;
    }

    public void deleteConfigSettings()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + CONFIGSETTINGS_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteNotificationcount()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + NOTIFICATION_COUNT_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteMenulist()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + MENULIST_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteReceiveTaskList()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_LIST);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }


    public void deleteSalesOrderList()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + SALES_ORDER_PICKLIST_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteSalesOrderChargeCategoryList()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + SO_CHARGES_CATEGORY);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }


    public void deleteTaskList()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_LIST_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }
    public void deletePrintPalletDetails()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + PRINT_PALLET_DETAILS);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }


    public void deleteResetPalletAlloc()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + RESETITEM_DETAIL);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteMoveTaskList()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + Move_TASK_LIST_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deletePickTaskDetail()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_DETAIL);
            mSqlitedb.execSQL("DELETE FROM " + EXPORT_LOT_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteAllLookupData()
    {
        try
        {
           // mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_HEADER);
           // mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_DETAIL);
            mSqlitedb.execSQL("DELETE FROM " + EXPORT_LOT_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHIPTL);
            mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHMSLT);
           //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHMLOT);
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHMQTY);
            mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_PRINTLABEL);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteLoadPickPalletLookupData()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + LPP_SUMMARY);
            mSqlitedb.execSQL("DELETE FROM " + LPP_DETAILS);
            mSqlitedb.execSQL("DELETE FROM " + LPP_ROUTE_DETAILS);
            mSqlitedb.execSQL("DELETE FROM " + LPP_WHIPLT);
            mSqlitedb.execSQL("DELETE FROM " + LPP_WHITRL);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }
    public void deleteLoadMoveTaskLookupData()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + MOVE_TASK_DETAIL);
            mSqlitedb.execSQL("DELETE FROM " + MOVE_TASK_SLOT_LIST);

        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteMoveTaskHeaderData(String taskNo)
    {
        try
        {

            mSqlitedb.execSQL("DELETE FROM moveTaskList where TaskNo = '"+taskNo+"'");


        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteReceiveTaskLookupData()
    {
        try
        {

          /*  mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_HEADER);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_DETAIL);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_ITEM_CLASS);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_LOAD_TYPE);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_WHMSLT);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_WHRPLT);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_EXPORT);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_PRINT);*/

            String temp="PO: " + Globals.gRTDocNo;


            mSqlitedb.execSQL("DELETE FROM  receivetaskheader where descrip = '" + temp + "'");
            mSqlitedb.execSQL("DELETE FROM  receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "'");
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_ITEM_CLASS);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_LOAD_TYPE);
            mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_WHMSLT);
            mSqlitedb.execSQL("DELETE FROM  receivetaskWHRPLT where taskno = '" + Globals.gRTTaskNo + "'");
            mSqlitedb.execSQL("DELETE FROM  receivetaskexportdetail where taskno = '" + Globals.gRTTaskNo + "'");
            mSqlitedb.execSQL("DELETE FROM  receivetaskprintdetail where taskno = '" + Globals.gRTTaskNo + "'");
            mSqlitedb.execSQL("DELETE FROM  receivetasklist where TaskNo = '" + Globals.gRTTaskNo + "'");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteReceiveTaskCancel(String tranNo)
    {
        try
        {
            //mSqlitedb.execSQL("DELETE FROM  receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "'");
            mSqlitedb.execSQL("DELETE FROM  receivetaskWHRPLT where taskno = '" + Globals.gRTTaskNo + "' and tasklineno = '" + tranNo + "' and pltlineno = '" + Globals.gRTTaskNo + "'");


        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deletePhysicalCountList()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_SLOT_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_DETAIL_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_DETAIL_TRAN_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_UOM_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_WHMLOT_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_WHMQTY_TABLE);
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_ICITEM_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public configsettings getApplicationConfiguration() {
        configsettings config = null;

        String query =
                "SELECT "
                        + CONFIG_SETTINGS_ID
                        + " ," + CONFIG_SETTINGS_APP_NAME
                        + " ," + CONFIG_SETTINGS_APP_DESC
                        + " ," + CONFIG_SETTINGS_INSTALLATION_DATE
                        + " ," + CONFIG_SETTINGS_EXP_DATE
                        + " ," + CONFIG_SETTINGS_NUMBER_OF_DAYS
                        + " ," + CONFIG_SETTINGS_DEVICE_ID
                        + " ," + CONFIG_SETTINGS_USERNAME
                        + " ," + CONFIG_SETTINGS_PASSWORD
                        + " ," + CONFIG_SETTINGS_ADMIN_PASSWORD
                        + " ," + CONFIG_SETTINGS_SESSIONID
                        + " ," + CONFIG_SETTINGS_CURRENT_COMPANY
                        + " FROM " + CONFIGSETTINGS_TABLE
                        + " ORDER BY " + CONFIG_SETTINGS_INSTALLATION_DATE + " DESC"
                        + " LIMIT 1"
                        + ";";

        try
        {
            mCursor = mSqlitedb.rawQuery(query, null);

            while (mCursor.moveToNext()) {
                config = new configsettings();

                config.setID(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_ID)));
                config.setAppName(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_APP_NAME)));
                config.setAppDesc(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_APP_DESC)));
                config.setInstallationDate(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_INSTALLATION_DATE)));
                config.setExpDate(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_EXP_DATE)));
                config.setNoOfDays(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_NUMBER_OF_DAYS)));
                config.setDeviceId(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_DEVICE_ID)));
                config.setUsername(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_USERNAME)));
                config.setPassword(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_PASSWORD)));
                config.setAdminPassword(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_ADMIN_PASSWORD)));
                config.setSessionId(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_SESSIONID)));
                config.setCurrentCompany(mCursor.getString(mCursor.getColumnIndex(CONFIG_SETTINGS_CURRENT_COMPANY)));
            }
        }
        catch (Exception e)
        {
            mCursor.close();
            LogfileCreator.mAppendLog(e.getMessage());

        }
        mCursor.close();
        return config;
    }

    public void addConfigSettingsData(configsettings settings) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(CONFIG_SETTINGS_ID, settings.getID());
            cv.put(CONFIG_SETTINGS_APP_NAME, settings.getAppName());
            cv.put(CONFIG_SETTINGS_APP_DESC, settings.getAppDesc());
            cv.put(CONFIG_SETTINGS_INSTALLATION_DATE, settings.getInstallationDate());
            cv.put(CONFIG_SETTINGS_EXP_DATE, settings.getExpDate());
            cv.put(CONFIG_SETTINGS_NUMBER_OF_DAYS, settings.getNoOfDays());
            cv.put(CONFIG_SETTINGS_DEVICE_ID, settings.getDeviceId());
            cv.put(CONFIG_SETTINGS_USERNAME, settings.getUsername());
            cv.put(CONFIG_SETTINGS_PASSWORD, settings.getPassword());
            cv.put(CONFIG_SETTINGS_ADMIN_PASSWORD, settings.getAdminPassword());
            cv.put(CONFIG_SETTINGS_SESSIONID, settings.getSessionId());
            cv.put(CONFIG_SETTINGS_CURRENT_COMPANY, settings.getCurrentCompany());
            cv.put(CONFIG_SETTINGS_SOFTKEYBOARD,"");

            mSqlitedb.insert(CONFIGSETTINGS_TABLE, null, cv);
            LogfileCreator.mAppendLog("Configuration Settings: App Configuration inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Settings data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPrintPalletDetails(PrintPalletTag tPrintPalletTag) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(PPD_ITEM, tPrintPalletTag.getItem());
            cv.put(PPD_LOTNO, tPrintPalletTag.getLotId());
            cv.put(PPD_PALNO, tPrintPalletTag.getPalletNo());
            cv.put(PPD_QTY, tPrintPalletTag.getQty());
            cv.put(PPD_COUNTRYID, tPrintPalletTag.getCountryid());
            cv.put(PPD_OUTPALNO, tPrintPalletTag.getOutpalno());
            cv.put(PPD_PLTCREA, tPrintPalletTag.getPltcrea());
            cv.put(PPD_SETID, tPrintPalletTag.getSetid());
            cv.put(PPD_GRADE, tPrintPalletTag.getGrade());
            cv.put(PPD_ITEMDESC, tPrintPalletTag.getItemDesc());
            cv.put(PPD_COMMOD, tPrintPalletTag.getCommod());
            cv.put(PPD_LABEL, tPrintPalletTag.getLabel());
            cv.put(PPD_PACKAGE, tPrintPalletTag.getPackage());
            cv.put(PPD_SIZESORT, tPrintPalletTag.getSizesort());
            cv.put(PPD_VARIETY, tPrintPalletTag.getVariety());
            cv.put(PPD_SIZE, tPrintPalletTag.getSize());




            mSqlitedb.insert(PRINT_PALLET_DETAILS, null, cv);
            LogfileCreator.mAppendLog("Print pallet tag Details: Print pallet details inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Print pallet details insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void deleteLocationData()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + LOCATION_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }
    public void addLocationData(location tlocation) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + LOCATION_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(LOCATION_ID, tlocation.getLocationID());
            cv.put(LOCATION_DESCRIPTION, tlocation.getLocationDesrip());
            cv.put(LOCATION_TYPE, tlocation.getLocationType());

            mSqlitedb.insert(LOCATION_TABLE, null, cv);
            LogfileCreator.mAppendLog("Location: Location details inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Settings data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addSoChargesData(SalesOrderCategoryList tSalesOrderCategoryList) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(CHARGES_CATEGORY, tSalesOrderCategoryList.getCategory());


            mSqlitedb.insert(SO_CHARGES_CATEGORY, null, cv);
            LogfileCreator.mAppendLog("SO_CHARGES_CATEGORY inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "SO_CHARGES_CATEGORY insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void addSessionData(sessiondetail tsessiondetail) {

        try {
            mSqlitedb.execSQL("DELETE FROM " + SESSION_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(RESULT, tsessiondetail.getResult());
            cv.put(SESSION_ID, tsessiondetail.getSessionId());
            cv.put(PALLETRESET_PRIV, tsessiondetail.getPalletResetPriv());

            mSqlitedb.insert(SESSION_TABLE, null, cv);
            LogfileCreator.mAppendLog("Session: New Session inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Session data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void deleteCompanyData()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM " + COMPANY_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }
    public void addCompanyData(company tcompany) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + COMPANY_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(COMPANY_ID, tcompany.getCompanyID());
            cv.put(COMPANY_NAME, tcompany.getCompanyName());
            cv.put(COMPANY_DATABASE, tcompany.getCompanyDatabase());

            mSqlitedb.insert(COMPANY_TABLE, null, cv);
            LogfileCreator.mAppendLog("Configuration Settings: App Configuration inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Settings data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addNotificationData(notificationcount tnotificationcount) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + NOTIFICATION_COUNT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(PICKTASK_COUNT, tnotificationcount.getPickTask());
            cv.put(RECEIVETASK_COUNT, tnotificationcount.getReceiveTask());
            cv.put(PHYSICALCOUNT_COUNT, tnotificationcount.getPhysicalCount());
            cv.put(MOVETASK_COUNT, tnotificationcount.getMoveTask());
            cv.put(LOADPALLETS_COUNT, tnotificationcount.getLoadPickPallets());

            mSqlitedb.insert(NOTIFICATION_COUNT_TABLE, null, cv);
            LogfileCreator.mAppendLog("Notification Count: Task count inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task count insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addMenuList(menulist tmenulist) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + NOTIFICATION_COUNT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(PICKTASK_MENU, tmenulist.getPickTask());
            cv.put(RECEIVETASK_MENU, tmenulist.getReceiveTask());
            cv.put(PHYSICALCOUNT_MENU, tmenulist.getPhysicalCount());
            cv.put(MOVETASK_MENU, tmenulist.getMoveTask());
            cv.put(LOADPALLETS_MENU, tmenulist.getLoadPickPallets());
            cv.put(MOVEMANUALLY_MENU, tmenulist.getMoveManually());
            cv.put(BREAKERUOMUTILITY_MENU, tmenulist.getBreakerUomUtility());

            mSqlitedb.insert(MENULIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("Menu List: menu inserted successfully");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task count insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPickTaskListData(picktasklist tpicktasklist) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + NOTIFICATION_COUNT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(TASK_NO, tpicktasklist.getTaskNo());
            cv.put(ROUTE, tpicktasklist.getRoute());
            cv.put(STOP, tpicktasklist.getStop());
            cv.put(STATUS, tpicktasklist.getStatus());
            cv.put(DATE, tpicktasklist.getDate());
            cv.put(SONOs, tpicktasklist.getSonos());

            mSqlitedb.insert(PICK_TASK_LIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("Pick Task List: Task List inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void addMoveTaskListData(Movetasklist movetasklist) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + NOTIFICATION_COUNT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(MT_TASK_NO, movetasklist.getTaskNo());
            cv.put(MT_TASK_TYPE, movetasklist.getTaskType());
            cv.put(MT_ROWPRTY, movetasklist.getRowPrty());
            cv.put(MT_STATUS, movetasklist.getStatus());

            mSqlitedb.insert(Move_TASK_LIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("Pick Task List: Task List inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void addPickTaskHeaderData(picktaskheader tpicktaskheader) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_HEADER);
            ContentValues cv = new ContentValues();

            cv.put(CASE_COUNT, tpicktaskheader.getCasecount());
            cv.put(PICK_WEIGHT, tpicktaskheader.getWeight());
            cv.put(PICK_ROUTE, tpicktaskheader.getRoute());
            cv.put(PICK_STOP, tpicktaskheader.getStop());

            mSqlitedb.insert(PICK_TASK_HEADER, null, cv);
            LogfileCreator.mAppendLog("Pick Task Header: Task Header inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Header insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPickTaskDetailData(picktaskdetail tpicktaskdetail) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            cv.put(DETAIL_SLOT, tpicktaskdetail.getSlot());



            if (!tpicktaskdetail.getoTqtypicked().equals("0.000000") || !tpicktaskdetail.getoTqtypicked().equals("")){
                cv.put(DETAIL_TQTY, tpicktaskdetail.getoTqtypicked());
            }else {
                cv.put(DETAIL_TQTY, tpicktaskdetail.getTQty());
            }
            cv.put(DETAIL_ORGTQTY, tpicktaskdetail.getTQty());
            cv.put(DETAIL_TRKQTY, tpicktaskdetail.getTrkQty());
            cv.put(DETAIL_ORGTRKQTY, tpicktaskdetail.getTrkQty());
            cv.put(DETAIL_UOM, tpicktaskdetail.getUom());
            cv.put(DETAIL_ITEM, tpicktaskdetail.getItem());
            cv.put(DETAIL_DESC, tpicktaskdetail.getDescrip());
            cv.put(DETAIL_WLOTNO, tpicktaskdetail.getWLotNo());
            cv.put(DETAIL_LOTNO, tpicktaskdetail.getLotNo());
            cv.put(DETAIL_TRANLINENO, tpicktaskdetail.getTranlineno());
            cv.put(DETAIL_ORGTRANLINENO, tpicktaskdetail.getTranlineno());
            cv.put(DETAIL_DOCTYPE, tpicktaskdetail.getDoctype());
            cv.put(DETAIL_DOCNO, tpicktaskdetail.getDocno());
            cv.put(DETAIL_DOCLINENO, tpicktaskdetail.getDoclineno());
            cv.put(DETAIL_ORGDOCLINENO, tpicktaskdetail.getDoclineno());
            cv.put(DETAIL_DOCSTAT, tpicktaskdetail.getDocstat());
            cv.put(DETAIL_WEIGHT, tpicktaskdetail.getWeight());
            cv.put(DETAIL_VOLUME, tpicktaskdetail.getVolume());
            cv.put(DETAIL_DECNUM, tpicktaskdetail.getdecnum());
            cv.put(DETAIL_STKUMID, tpicktaskdetail.getStkumid());
            cv.put(DETAIL_CATCHWT, tpicktaskdetail.getCatchwt());
            cv.put(DETAIL_UMFACT, tpicktaskdetail.getUmfact());
            cv.put(DETAIL_TSHIPPED, tpicktaskdetail.getTshipped());
            cv.put(DETAIL_TRKSHIPPED, tpicktaskdetail.getTrkshipped());
            cv.put(DETAIL_LBSHP, tpicktaskdetail.getLbshp());
            cv.put(DETAIL_PICKDURATION, 0);
            cv.put(DETAIL_LINESPLIT, "0");
            if (tpicktaskdetail.getWLotNo().equals("") && tpicktaskdetail.getLotNo().equals("") || tpicktaskdetail.getChgQty().equals("Y") && tpicktaskdetail.getLotNo().equals("")){
                cv.put(DETAIL_FLAG, "N");
            }else {
                cv.put(DETAIL_FLAG, "Y");
            }

            cv.put(DETAIL_ROWNO, Globals.gPTDetailRowCount);
            Globals.gPTDetailRowCount = Globals.gPTDetailRowCount + 1;
            cv.put(DETAIL_SUBSTITUTED_ITEM,tpicktaskdetail.getSubItem());
            cv.put(DETAIL_SUBSTITUTED_TRANNO,tpicktaskdetail.getSubTranNo());
            cv.put(DETAIL_ORG_SOITEM,tpicktaskdetail.getOrgSOItem());
            cv.put(DETAIL_STAGINGSLOT,tpicktaskdetail.getStagingSlot());
            cv.put(DETAIL_CHGQTY,tpicktaskdetail.getChgQty());
            cv.put(DETAIL_OTQTYPICKED,tpicktaskdetail.getoTqtypicked());
            cv.put(DETAIL_PICKED,"N");

            mSqlitedb.insert(PICK_TASK_DETAIL, null, cv);


            LogfileCreator.mAppendLog("Pick Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void addPickTaskDetailRevertData(picktaskdetail tpicktaskdetail) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            cv.put(DETAIL_SLOT, tpicktaskdetail.getSlot());



            if (!tpicktaskdetail.getoTqtypicked().equals("0.000000") || !tpicktaskdetail.getoTqtypicked().equals("")){
                cv.put(DETAIL_TQTY, tpicktaskdetail.getoTqtypicked());
            }else {
                cv.put(DETAIL_TQTY, tpicktaskdetail.getTQty());
            }
            cv.put(DETAIL_ORGTQTY, tpicktaskdetail.getTQty());
            cv.put(DETAIL_TRKQTY, tpicktaskdetail.getTrkQty());
            cv.put(DETAIL_ORGTRKQTY, tpicktaskdetail.getTrkQty());
            cv.put(DETAIL_UOM, tpicktaskdetail.getUom());
            cv.put(DETAIL_ITEM, tpicktaskdetail.getItem());
            cv.put(DETAIL_DESC, tpicktaskdetail.getDescrip());
            cv.put(DETAIL_WLOTNO, tpicktaskdetail.getWLotNo());
            cv.put(DETAIL_LOTNO, tpicktaskdetail.getLotNo());
            cv.put(DETAIL_TRANLINENO, tpicktaskdetail.getTranlineno());
            cv.put(DETAIL_ORGTRANLINENO, tpicktaskdetail.getTranlineno());
            cv.put(DETAIL_DOCTYPE, tpicktaskdetail.getDoctype());
            cv.put(DETAIL_DOCNO, tpicktaskdetail.getDocno());
            cv.put(DETAIL_DOCLINENO, tpicktaskdetail.getDoclineno());
            cv.put(DETAIL_ORGDOCLINENO, tpicktaskdetail.getDoclineno());
            cv.put(DETAIL_DOCSTAT, tpicktaskdetail.getDocstat());
            cv.put(DETAIL_WEIGHT, tpicktaskdetail.getWeight());
            cv.put(DETAIL_VOLUME, tpicktaskdetail.getVolume());
            cv.put(DETAIL_DECNUM, tpicktaskdetail.getdecnum());
            cv.put(DETAIL_STKUMID, tpicktaskdetail.getStkumid());
            cv.put(DETAIL_CATCHWT, tpicktaskdetail.getCatchwt());
            cv.put(DETAIL_UMFACT, tpicktaskdetail.getUmfact());
            cv.put(DETAIL_TSHIPPED, tpicktaskdetail.getTshipped());
            cv.put(DETAIL_TRKSHIPPED, tpicktaskdetail.getTrkshipped());
            cv.put(DETAIL_LBSHP, tpicktaskdetail.getLbshp());
            cv.put(DETAIL_PICKDURATION, 0);
            cv.put(DETAIL_LINESPLIT, "0");
            if (tpicktaskdetail.getWLotNo().equals("") && tpicktaskdetail.getLotNo().equals("") || tpicktaskdetail.getChgQty().equals("Y") && tpicktaskdetail.getLotNo().equals("")){
                cv.put(DETAIL_FLAG, "N");
            }else {
                cv.put(DETAIL_FLAG, "Y");
            }
            /*Globals.gPTDetailRowCount = Globals.gPTDetailRowCount - 1;*/
            cv.put(DETAIL_ROWNO, Globals.gPTDetailRowCount);
            /*Globals.gPTDetailRowCount = Globals.gPTDetailRowCount + 1;*/
            cv.put(DETAIL_SUBSTITUTED_ITEM,tpicktaskdetail.getSubItem());
            cv.put(DETAIL_SUBSTITUTED_TRANNO,tpicktaskdetail.getSubTranNo());
            cv.put(DETAIL_ORG_SOITEM,tpicktaskdetail.getOrgSOItem());
            cv.put(DETAIL_STAGINGSLOT,tpicktaskdetail.getStagingSlot());
            cv.put(DETAIL_CHGQTY,tpicktaskdetail.getChgQty());
            cv.put(DETAIL_OTQTYPICKED,tpicktaskdetail.getoTqtypicked());
            cv.put(DETAIL_PICKED,"N");
            cv.put(DETAIL_TEMPQTY,"");

            mSqlitedb.insert(PICK_TASK_REVEROLDDATA, null, cv);


            LogfileCreator.mAppendLog("Pick Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void UpdatePickTaskRevertOldData(List<picktaskdetail> tpicktaskdetail) {

        try {

            ContentValues cv = new ContentValues();

            for(int i=0 ; i < tpicktaskdetail.size() ; i++) {

                cv.put(DETAIL_SLOT, tpicktaskdetail.get(i).getSlot());
                cv.put(DETAIL_TQTY,  tpicktaskdetail.get(i).getTQty());
                cv.put(DETAIL_ORGTQTY,  tpicktaskdetail.get(i).getorgTQty());
                cv.put(DETAIL_TRKQTY,  tpicktaskdetail.get(i).getTrkQty());
                cv.put(DETAIL_ORGTRKQTY,  tpicktaskdetail.get(i).getTrkQty());
                cv.put(DETAIL_UOM,  tpicktaskdetail.get(i).getUom());
                cv.put(DETAIL_ITEM,  tpicktaskdetail.get(i).getItem());
                cv.put(DETAIL_DESC,  tpicktaskdetail.get(i).getDescrip());
                cv.put(DETAIL_WLOTNO,  tpicktaskdetail.get(i).getWLotNo());
                cv.put(DETAIL_LOTNO,  tpicktaskdetail.get(i).getLotNo());
                cv.put(DETAIL_TRANLINENO,  tpicktaskdetail.get(i).getTranlineno());
                cv.put(DETAIL_ORGTRANLINENO,  tpicktaskdetail.get(i).getTranlineno());
                cv.put(DETAIL_DOCTYPE,  tpicktaskdetail.get(i).getDoctype());
                cv.put(DETAIL_DOCNO,  tpicktaskdetail.get(i).getDocno());
                cv.put(DETAIL_DOCLINENO,  tpicktaskdetail.get(i).getDoclineno());
                cv.put(DETAIL_ORGDOCLINENO,  tpicktaskdetail.get(i).getDoclineno());
                cv.put(DETAIL_DOCSTAT,  tpicktaskdetail.get(i).getDocstat());
                cv.put(DETAIL_WEIGHT,  tpicktaskdetail.get(i).getWeight());
                cv.put(DETAIL_VOLUME,  tpicktaskdetail.get(i).getVolume());
                cv.put(DETAIL_DECNUM,  tpicktaskdetail.get(i).getdecnum());
                cv.put(DETAIL_STKUMID,  tpicktaskdetail.get(i).getStkumid());
                cv.put(DETAIL_CATCHWT,  tpicktaskdetail.get(i).getCatchwt());
                cv.put(DETAIL_UMFACT,  tpicktaskdetail.get(i).getUmfact());
                cv.put(DETAIL_TSHIPPED,  tpicktaskdetail.get(i).getTshipped());
                cv.put(DETAIL_TRKSHIPPED,  tpicktaskdetail.get(i).getTrkshipped());
                cv.put(DETAIL_LBSHP,  tpicktaskdetail.get(i).getLbshp());
                cv.put(DETAIL_PICKDURATION,  tpicktaskdetail.get(i).getpickDuration());
                cv.put(DETAIL_LINESPLIT,  tpicktaskdetail.get(i).getLinesplit());
                cv.put(DETAIL_FLAG,  tpicktaskdetail.get(i).getFlag());
                cv.put(DETAIL_ROWNO,  tpicktaskdetail.get(i).getrowNo());
                cv.put(DETAIL_SUBSTITUTED_ITEM,  tpicktaskdetail.get(i).getSubItem());
                cv.put(DETAIL_SUBSTITUTED_TRANNO,  tpicktaskdetail.get(i).getSubTranNo());
                cv.put(DETAIL_ORG_SOITEM,  tpicktaskdetail.get(i).getOrgSOItem());
                cv.put(DETAIL_STAGINGSLOT,  tpicktaskdetail.get(i).getStagingSlot());
                cv.put(DETAIL_CHGQTY,  tpicktaskdetail.get(i).getChgQty());
                cv.put(DETAIL_OTQTYPICKED,  tpicktaskdetail.get(i).getoTqtypicked());
                cv.put(DETAIL_PICKED,  tpicktaskdetail.get(i).getPicked());

                mSqlitedb.insert(PICK_TASK_REVEROLDDATA, null, cv);
            }

            LogfileCreator.mAppendLog("Pick Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void addPickTaskScanPallet(PickTaskScanPallet tPickTaskScanPallet) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            cv.put(PTSP_ITEM ,tPickTaskScanPallet.getPtsP_item());
            cv.put(PTSP_LOCTID ,tPickTaskScanPallet.getPtsP_loctid());
            cv.put(PTSP_WLOTNO ,tPickTaskScanPallet.getPtsP_wlotno());
            cv.put(PTSP_LOTNO ,tPickTaskScanPallet.getPtsP_lotno());
            cv.put(PTSP_LOTREFID ,tPickTaskScanPallet.getPtsP_lotrefid());
            cv.put(PTSP_SLOT ,tPickTaskScanPallet.getPtsP_slot());
            cv.put(PTSP_UMEASUR ,tPickTaskScanPallet.getPtsP_umeasur());
            cv.put(PTSP_TQTY ,tPickTaskScanPallet.getPtsP_tqty());
            cv.put(PTSP_RPALLOCQTY ,tPickTaskScanPallet.getPtsP_rpallocqty());
            cv.put(PTSP_WHQTY ,tPickTaskScanPallet.getPtsP_whqty());
            cv.put(PTSP_ICQTY ,tPickTaskScanPallet.getPtsP_icqty());
            cv.put(PTSP_ITMDESC ,tPickTaskScanPallet.getPtsP_itmdesc());
            cv.put(PTSP_WEIGHT ,tPickTaskScanPallet.getPtsP_weight());
            cv.put(PTSP_COUNTRYID ,tPickTaskScanPallet.getPtsP_countryid());
            cv.put(PTSP_SERIAL ,tPickTaskScanPallet.getPtsP_serial());
            cv.put(PTSP_VOLUME ,tPickTaskScanPallet.getPtsP_volume());
            cv.put(PTSP_CATCHWT ,tPickTaskScanPallet.getPtsP_catchwt());
            cv.put(PTSP_STKUMID ,tPickTaskScanPallet.getPtsP_stkumid());
            cv.put(PTSP_USELOTS ,tPickTaskScanPallet.getPtsP_uselots());
            cv.put(PTSP_UMFACT ,tPickTaskScanPallet.getPtsP_umfact());
            cv.put(PTSP_SETID ,tPickTaskScanPallet.getPtsP_setid());
            cv.put(PTSP_VENDNO ,tPickTaskScanPallet.getPtsP_vendno());
            cv.put(PTSP_COST ,tPickTaskScanPallet.getPtsP_cost());


            mSqlitedb.insert(PICK_TASK_SCANPALLET, null, cv);


            LogfileCreator.mAppendLog("Pick Task Scan Pallet: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Scan pallet insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPickTaskWHIPTLData(picktaskWHIPTL tpicktaskWHIPTL) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHIPTL);
            ContentValues cv = new ContentValues();

            cv.put(WHIPTL_PALNO, tpicktaskWHIPTL.getPalno());
            cv.put(WHIPTL_TASKNO, tpicktaskWHIPTL.getTaskno());
            cv.put(WHIPTL_PALSTAT, tpicktaskWHIPTL.getPalstat());

            mSqlitedb.insert(PICK_TASK_WHIPTL, null, cv);
            LogfileCreator.mAppendLog("Pick Task WHIPTL: Task WHIPTL inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHIPTL insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void addPickTaskWHMLOTData(picktaskWHMLOT tpicktaskWHMLOT) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHMLOT);
            ContentValues cv = new ContentValues();

            cv.put(WHMLOT_ITEM, tpicktaskWHMLOT.getItem());
            cv.put(WHMLOT_WLOTNO, tpicktaskWHMLOT.getWlotno());
            cv.put(WHMLOT_LOTREFID, tpicktaskWHMLOT.getLotrefid());

            mSqlitedb.insert(PICK_TASK_WHMLOT, null, cv);
            LogfileCreator.mAppendLog("Pick Task WHMLOT: Task WHMLOT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHMLOT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void addPickTaskWHMSLTData(picktaskWHMSLT tpicktaskWHMSLT) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHMSLT);
            ContentValues cv = new ContentValues();

            cv.put(WHMSLT_SLOT, tpicktaskWHMSLT.getSlot());
            cv.put(WHMSLT_LOCTID, tpicktaskWHMSLT.getLoctid());
            cv.put(WHMSLT_SLOTTYPE, tpicktaskWHMSLT.getSlottype());

            mSqlitedb.insert(PICK_TASK_WHMSLT, null, cv);
            LogfileCreator.mAppendLog("Pick Task WHMSLT: Task WHMSLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHMSLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPickTaskWHMQTYData(picktaskWHMQTY tpicktaskWHMQTY) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHMSLT);
            ContentValues cv = new ContentValues();

            cv.put(WHMQTY_ITEM, tpicktaskWHMQTY.getItem());
            cv.put(WHMQTY_LOCTID, tpicktaskWHMQTY.getLoctid());
            cv.put(WHMQTY_WLOTNO, tpicktaskWHMQTY.getWlotno());
            cv.put(WHMQTY_SLOT, tpicktaskWHMQTY.getSlot());
            cv.put(WHMQTY_UMEASUR, tpicktaskWHMQTY.getUmeasur());
            cv.put(WHMQTY_TQTY, tpicktaskWHMQTY.getTqty());

            mSqlitedb.insert(PICK_TASK_WHMQTY, null, cv);
            LogfileCreator.mAppendLog("Pick Task WHMQTY: Task WHMQTY inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHMQTY insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPickTaskWHITRLSData(picktaskWHITRLS tpicktaskWHITRLS) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            ContentValues cv = new ContentValues();

            cv.put(WHITRLS_TRAILER, tpicktaskWHITRLS.getTrailer());
            cv.put(WHITRLS_RTESEQ, tpicktaskWHITRLS.getRteseq());
            cv.put(WHITRLS_ROUTE, tpicktaskWHITRLS.getRoute());
            cv.put(WHITRLS_DOCK, tpicktaskWHITRLS.getDock());

            mSqlitedb.insert(PICK_TASK_WHITRLS, null, cv);
            LogfileCreator.mAppendLog("Pick Task WHITRLS: Task WHITRLS inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHITRLS insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPickTaskPrintLabel(picktaskPrintlabel tpicktaskPrintlabel) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_PRINTLABEL);
            ContentValues cv = new ContentValues();

            cv.put(PT_STOP, tpicktaskPrintlabel.getStop());
            cv.put(PT_TRAILER, tpicktaskPrintlabel.getTrailer());
            cv.put(PT_ROUTE, tpicktaskPrintlabel.getRoute());
            cv.put(PT_DOCK, tpicktaskPrintlabel.getDock());
            cv.put(PT_DELDATE, tpicktaskPrintlabel.getDeldate());
            cv.put(PT_ORDER, tpicktaskPrintlabel.getOrderno());
            cv.put(PT_TASKNO, tpicktaskPrintlabel.getTaskno());
            cv.put(PT_CUSTID, tpicktaskPrintlabel.getCustid());
            cv.put(PT_CUSTNAME, tpicktaskPrintlabel.getCustname());
            cv.put(PT_PICKER, tpicktaskPrintlabel.getPicker());
            cv.put(PT_PALNO, tpicktaskPrintlabel.getPalno());

            mSqlitedb.insert(PICK_TASK_PRINTLABEL, null, cv);
            LogfileCreator.mAppendLog("PICK TASK PRINT LABEL: PRINT LABEL detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "PRINT LABEL detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addLoadPickPalletSummaryData(loadpickpalletSummary tloadpickpalletSummary) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            ContentValues cv = new ContentValues();

            cv.put(LPP_SUMMARY_WMSDate, tloadpickpalletSummary.getwmsDate());
            cv.put(LPP_SUMMARY_TRUCK, tloadpickpalletSummary.getTruck());
            cv.put(LPP_SUMMARY_DOCK, tloadpickpalletSummary.getDock());
            cv.put(LPP_SUMMARY_ROUTECNT, tloadpickpalletSummary.getRoutecnt());
            cv.put(LPP_SUMMARY_STOPCNT, tloadpickpalletSummary.getStopcnt());
            cv.put(LPP_SUMMARY_PALCNT, tloadpickpalletSummary.getPalcnt());
            cv.put(LPP_SUMMARY_ROWNO, Globals.gLPPSummaryRowCount);
            cv.put(LPP_SUMMARY_FLAG, "N");
            Globals.gLPPSummaryRowCount = Globals.gLPPSummaryRowCount + 1;

            mSqlitedb.insert(LPP_SUMMARY, null, cv);
            LogfileCreator.mAppendLog("LPP SUMMARY: LLP SUMMARY inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "LLP SUMMARY insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addLoadPickPalletDetailsData(loadpickpalletDetails tloadpickpalletDetails) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            ContentValues cv = new ContentValues();

            cv.put(LPP_DETAIL_WMSDate, tloadpickpalletDetails.getwmsDate());
            cv.put(LPP_DETAIL_TASKNO, tloadpickpalletDetails.getTaskno());
            cv.put(LPP_DETAIL_PICKER, tloadpickpalletDetails.getPicker());
            cv.put(LPP_DETAIL_ROUTE, tloadpickpalletDetails.getRoute());
            cv.put(LPP_DETAIL_STOP, tloadpickpalletDetails.getStop());
            cv.put(LPP_DETAIL_TOTAL, tloadpickpalletDetails.getTotal());
            cv.put(LPP_DETAIL_LOADED, tloadpickpalletDetails.getLoaded());
            cv.put(LPP_DETAIL_READY, tloadpickpalletDetails.getReady());
            cv.put(LPP_DETAIL_ROWNO, Globals.gLPPDetailRowCount);
            cv.put(LPP_DETAIL_FLAG, "N");
            Globals.gLPPDetailRowCount = Globals.gLPPDetailRowCount + 1;

            mSqlitedb.insert(LPP_DETAILS, null, cv);
            LogfileCreator.mAppendLog("LPP DETAILS: LLP DETAILS inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "LLP DETAILS insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addLoadPickPalletRouteDetailsData(loadpickpalletRouteDetails tloadpickpalletRouteDetails) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            ContentValues cv = new ContentValues();

            cv.put(LPP_ROUTE_DETAIL_ROUTE, tloadpickpalletRouteDetails.getRoute());
            cv.put(LPP_ROUTE_DETAIL_TRUCK, tloadpickpalletRouteDetails.getTruck());
            cv.put(LPP_ROUTE_DETAIL_STATUS, tloadpickpalletRouteDetails.getStatus());
            cv.put(LPP_ROUTE_DETAIL_STOPCNT, tloadpickpalletRouteDetails.getStopcnt());
            cv.put(LPP_ROUTE_DETAIL_PALIN, tloadpickpalletRouteDetails.getPalin());
            cv.put(LPP_ROUTE_DETAIL_PALRDY, tloadpickpalletRouteDetails.getPalrdy());
            cv.put(LPP_ROUTE_DETAIL_PALCNT, tloadpickpalletRouteDetails.getPalcnt());

            mSqlitedb.insert(LPP_ROUTE_DETAILS, null, cv);
            LogfileCreator.mAppendLog("LPP ROUTE DETAILS: LLP ROUTE DETAILS inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "LLP ROUTE DETAILS insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addLoadPickPalletWHIPLT(loadpickpalletWHIPLT tloadpickpalletWHIPLT) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            ContentValues cv = new ContentValues();

            cv.put(LPP_WHIPLT_WMSDATE, tloadpickpalletWHIPLT.getwmsDate());
            cv.put(LPP_WHIPLT_PALNO, tloadpickpalletWHIPLT.getPalno());
            cv.put(LPP_DETAIL_TASKNO, tloadpickpalletWHIPLT.getTaskno());
            cv.put(LPP_WHIPLT_PALSTAT, tloadpickpalletWHIPLT.getPalstat());
            cv.put(LPP_WHIPLT_STGSLOT, tloadpickpalletWHIPLT.getstgslot());

            mSqlitedb.insert(LPP_WHIPLT, null, cv);
            LogfileCreator.mAppendLog("LPP WHIPLT: LLP WHIPLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "LLP WHIPLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addLoadPickPalletWHIPLT(loadpickpalletWHITRL tloadpickpalletWHITRL) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_WHITRLS);
            ContentValues cv = new ContentValues();

            cv.put(LPP_WHITRL_WMSDATE, tloadpickpalletWHITRL.getwmsDate());
            cv.put(LPP_WHITRL_TRAILER, tloadpickpalletWHITRL.getTrailer());
            cv.put(LPP_WHITRL_RTESEQ, tloadpickpalletWHITRL.getRteseq());
            cv.put(LPP_WHITRL_ROUTE, tloadpickpalletWHITRL.getRoute());
            cv.put(LPP_WHITRL_DOCK, tloadpickpalletWHITRL.getDock());

            mSqlitedb.insert(LPP_WHITRL, null, cv);
            LogfileCreator.mAppendLog("LPP WHITRL: LLP WHITRL inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "LLP WHITRL insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskListData(receivetasklist treceivetasklist) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + NOTIFICATION_COUNT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(RT_TASKNO, treceivetasklist.getTaskNo());
            cv.put(RT_STATUS, treceivetasklist.getStatus());
            cv.put(RT_USERID, treceivetasklist.getUserid());
            cv.put(RT_DOCTYPE, treceivetasklist.getDoctype());
            cv.put(RT_DOCNO, treceivetasklist.getDocno());

            mSqlitedb.insert(RECEIVE_TASK_LIST, null, cv);
            LogfileCreator.mAppendLog("Receive Task List: Task List inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskHeader(receivetaskheader treceivetaskheader) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_HEADER);
            ContentValues cv = new ContentValues();

            cv.put(RTH_DESCRIP, treceivetaskheader.getdescrip());
            cv.put(RTH_VENDDESCRIP, treceivetaskheader.getvenddescrip());
            cv.put(RTH_CASECOUNTED, treceivetaskheader.getcasecounted());
            cv.put(RTH_CASETOTAL, treceivetaskheader.getcasetotal());
            cv.put(RTH_PLTOTAL, treceivetaskheader.getplttotal());
            cv.put(RTH_PLTCOUNTED, treceivetaskheader.getpltcounted());
            cv.put(RTH_WMSDATE, treceivetaskheader.getwmsdate());

            mSqlitedb.insert(RECEIVE_TASK_HEADER, null, cv);
            LogfileCreator.mAppendLog("Receive Task Header: Task Header inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Header insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addMoveTaskDetail(movetaskdetail tmovetaskdetail) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(MTD_TASKNO, tmovetaskdetail.getTaskno());
            cv.put(MTD_TASKTYPE, tmovetaskdetail.getTasktype());
            cv.put(MTD_STATUS, tmovetaskdetail.getStatus());
            cv.put(MTD_TRANLINENO, tmovetaskdetail.getTranlineno());
            cv.put(MTD_CHILDID, tmovetaskdetail.getChildID());
            cv.put(MTD_ITEM, tmovetaskdetail.getItem());
            cv.put(MTD_LOCTID, tmovetaskdetail.getLoctid());
            cv.put(MTD_WLOTNO, tmovetaskdetail.getWlotno());
            cv.put(MTD_PALNO, tmovetaskdetail.getPalno());
            cv.put(MTD_UMEASUR, tmovetaskdetail.getUmeasur());
            cv.put(MTD_TQTYRQ, tmovetaskdetail.getTqtyrq());
            cv.put(MTD_TQTYACT, tmovetaskdetail.getTqtyact());
            cv.put(MTD_FROMSLOT, tmovetaskdetail.getFromSlot());
            cv.put(MTD_TOSLOT, tmovetaskdetail.getToSlot());
            cv.put(MTD_ITEMDESC, tmovetaskdetail.getItmdesc());
            cv.put(MTD_PCKDESC, tmovetaskdetail.getPckdesc());
            cv.put(MTD_WHQTY, tmovetaskdetail.getWhqty());
            cv.put(MTD_ALLOCQTY, tmovetaskdetail.getAllocqty());
            cv.put(MTD_LOCKED, tmovetaskdetail.getLocked());
            if (!tmovetaskdetail.getToSlot().equals("")&& Double.parseDouble(tmovetaskdetail.getTqtyact())>0.0){
                cv.put(MTD_FLAG,"Y");
            }else{
                cv.put(MTD_FLAG,"N");
            }
            cv.put(MTD_EDITED,"0");


            mSqlitedb.insert(MOVE_TASK_DETAIL, null, cv);
            LogfileCreator.mAppendLog("Move Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskDetail(receivetaskdetail treceivetaskdetail) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            cv.put(RTD_TASKNO, treceivetaskdetail.gettaskno());
            cv.put(RTD_TRANLINENO, treceivetaskdetail.gettranlineno());
            cv.put(RTD_DOCTYPE, treceivetaskdetail.getdoctype());
            cv.put(RTD_DOCNO, treceivetaskdetail.getdocno());
            cv.put(RTD_DOCLINENO, treceivetaskdetail.getdoclineno());
            cv.put(RTD_ITEM, treceivetaskdetail.getitem());
            cv.put(RTD_LOCTID, treceivetaskdetail.getloctid());
            cv.put(RTD_WLOTNO, treceivetaskdetail.getwlotno());
            cv.put(RTD_UMEASUR, treceivetaskdetail.getumeasur());
            cv.put(RTD_WMSSTAT, treceivetaskdetail.getwmsstat());
            cv.put(RTD_TQTYREC, treceivetaskdetail.gettqtyrec());
            cv.put(RTD_TRKQTYREC, treceivetaskdetail.gettqtyinc());
            /*cv.put(RTD_TRKQTYREC, treceivetaskdetail.gettrkqtyrec());*/
            cv.put(RTD_REVLEV, treceivetaskdetail.getrevlev());
            cv.put(RTD_TQTYINC, treceivetaskdetail.gettqtyinc());
            cv.put(RTD_ITMDESC, treceivetaskdetail.getitmdesc());
            cv.put(RTD_PCKDESC, treceivetaskdetail.getpckdesc());
            cv.put(RTD_COUNTRYID, treceivetaskdetail.getcountryid());
            cv.put(RTD_ITEMSHOW, treceivetaskdetail.getitemShow());
            cv.put(RTD_COLLECTION, treceivetaskdetail.getcollection());
            cv.put(RTD_WELEMENT, treceivetaskdetail.getwelement());
            cv.put(RTD_WIDGETID, treceivetaskdetail.getwidgetID());
            cv.put(RTD_CATCHWT, treceivetaskdetail.getcatchwt());
            cv.put(RTD_DECNUM, treceivetaskdetail.getdecnum());
            cv.put(RTD_LOTREFID, treceivetaskdetail.getlotrefid());
            cv.put(RTD_LINESPLIT, "0");
            cv.put(RTD_FLAG, "N");
            cv.put(RTD_ROWNO, Globals.gRTDetailRowCount);
            Globals.gRTDetailRowCount = Globals.gRTDetailRowCount + 1;
            cv.put(RTD_PALNO,treceivetaskdetail.getPalno());

            mSqlitedb.insert(RECEIVE_TASK_DETAIL, null, cv);
            LogfileCreator.mAppendLog("Receive Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveSlotList(SlotList slotList) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(RSL_ITEM, slotList.getItem());
            cv.put(RSL_SLOT, slotList.getSlot());
            cv.put(RSL_TASKNO, Globals.gRTTaskNo);


            mSqlitedb.insert(RECEIVE_SLOT_LIST, null, cv);
            LogfileCreator.mAppendLog("Receive Slot list: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Slot List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addMoveTaskSlotList(MoveTaskSlotList mMoveTaskSlotList) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(MTSL_TASKNO,Globals.gMTTaskNo);
            cv.put(MTSL_SLOT, mMoveTaskSlotList.getSlot());
            cv.put(MTSL_LOCTID, mMoveTaskSlotList.getLoctid());
            cv.put(MTSL_SLOTTYPE, mMoveTaskSlotList.getSlottype());


            mSqlitedb.insert(MOVE_TASK_SLOT_LIST, null, cv);
            LogfileCreator.mAppendLog("MoveTask Slot list: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Slot List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addSalesOrderPickList(SalesOrderPickList mSalesOrderPickList) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(SOL_SONO,mSalesOrderPickList.getSono());
            cv.put(SOL_SODATE, mSalesOrderPickList.getSodate());
            cv.put(SOL_CUSTNO, mSalesOrderPickList.getCustno());
            cv.put(SOL_ORDAMT, mSalesOrderPickList.getOrdamt());


            mSqlitedb.insert(SALES_ORDER_PICKLIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("Sales Order list : Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Sales Order List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }



    public void addResetItemDetails(ResetItemDetails mResetItemDetails) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(RID_ITEM,mResetItemDetails.getItem());
            cv.put(RID_LOCTID, mResetItemDetails.getLoctid());
            cv.put(RID_WLOTNO, mResetItemDetails.getWlotno());
            cv.put(RID_LOTNO, mResetItemDetails.getLotno());
            cv.put(RID_PALNO, mResetItemDetails.getPalno());
            cv.put(RID_SLOT, mResetItemDetails.getSlot());
            cv.put(RID_UMEASUR, mResetItemDetails.getUmeasur());
            cv.put(RID_TQTY, mResetItemDetails.getTqty());
            cv.put(RID_RPALLOCQTY, mResetItemDetails.getRpallocqty());
            cv.put(RID_WHQTY, mResetItemDetails.getWhqty());
            cv.put(RID_ICQTY, mResetItemDetails.getIcqty());
            cv.put(RID_ITMDESC, mResetItemDetails.getItmdesc());
            cv.put(RID_QTY, mResetItemDetails.getQty());
            cv.put(RID_PSOALOC, mResetItemDetails.getPsoaloc());
            cv.put(RID_PRPALOC, mResetItemDetails.getPrpaloc());
            cv.put(RID_PTFROUT, mResetItemDetails.getPtfrout());
            cv.put(RID_PTMALOC, mResetItemDetails.getPtmaloc());
            //cv.put(RID_OPENSOCOUNT, mResetItemDetails.getOpensocount());
            //cv.put(RID_OPENREPACKCOUNT, mResetItemDetails.getOpenrepackcount());


            mSqlitedb.insert(RESETITEM_DETAIL, null, cv);
            LogfileCreator.mAppendLog("ResetItemDetail : ResetItemDetail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "SResetItemDetail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void UpdateReceiveTaskDetailFromTran(List<receivetaskdetail> treceivetaskdetail) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            for(int i = 0; i < treceivetaskdetail.size(); i++) {
                cv.put(RTD_TASKNO, treceivetaskdetail.get(i).gettaskno());
                cv.put(RTD_TRANLINENO, treceivetaskdetail.get(i).gettranlineno());
                cv.put(RTD_DOCTYPE, treceivetaskdetail.get(i).getdoctype());
                cv.put(RTD_DOCNO, treceivetaskdetail.get(i).getdocno());
                cv.put(RTD_DOCLINENO, treceivetaskdetail.get(i).getdoclineno());
                cv.put(RTD_ITEM, treceivetaskdetail.get(i).getitem());
                cv.put(RTD_LOCTID, treceivetaskdetail.get(i).getloctid());
                cv.put(RTD_WLOTNO, treceivetaskdetail.get(i).getwlotno());
                cv.put(RTD_UMEASUR, treceivetaskdetail.get(i).getumeasur());
                cv.put(RTD_WMSSTAT, treceivetaskdetail.get(i).getwmsstat());
                cv.put(RTD_TQTYREC, treceivetaskdetail.get(i).gettqtyrec());
                cv.put(RTD_TRKQTYREC, treceivetaskdetail.get(i).gettqtyinc());
                cv.put(RTD_REVLEV, treceivetaskdetail.get(i).getrevlev());
                cv.put(RTD_TQTYINC, treceivetaskdetail.get(i).gettqtyinc());
                cv.put(RTD_ITMDESC, treceivetaskdetail.get(i).getitmdesc());
                cv.put(RTD_PCKDESC, treceivetaskdetail.get(i).getpckdesc());
                cv.put(RTD_COUNTRYID, treceivetaskdetail.get(i).getcountryid());
                cv.put(RTD_ITEMSHOW, treceivetaskdetail.get(i).getitemShow());
                cv.put(RTD_COLLECTION, treceivetaskdetail.get(i).getcollection());
                cv.put(RTD_WELEMENT, treceivetaskdetail.get(i).getwelement());
                cv.put(RTD_WIDGETID, treceivetaskdetail.get(i).getwidgetID());
                cv.put(RTD_CATCHWT, treceivetaskdetail.get(i).getcatchwt());
                cv.put(RTD_DECNUM, treceivetaskdetail.get(i).getdecnum());
                cv.put(RTD_LOTREFID, treceivetaskdetail.get(i).getlotrefid());
                cv.put(RTD_LINESPLIT,treceivetaskdetail.get(i).getLinesplit());
                cv.put(RTD_FLAG, treceivetaskdetail.get(i).getFlag());
                cv.put(RTD_ROWNO,  treceivetaskdetail.get(i).getrowNo());
                cv.put(RTD_PALNO, treceivetaskdetail.get(i).getPalno());

                mSqlitedb.insert(RECEIVE_TASK_DETAIL, null, cv);
            }
            LogfileCreator.mAppendLog("Receive Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void UpdateReceiveTaskTranDetail(List<receivetaskdetail> treceivetaskdetail) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            for(int i = 0; i < treceivetaskdetail.size(); i++) {
                cv.put(RTD_TASKNO, treceivetaskdetail.get(i).gettaskno());
                cv.put(RTD_TRANLINENO, treceivetaskdetail.get(i).gettranlineno());
                cv.put(RTD_DOCTYPE, treceivetaskdetail.get(i).getdoctype());
                cv.put(RTD_DOCNO, treceivetaskdetail.get(i).getdocno());
                cv.put(RTD_DOCLINENO, treceivetaskdetail.get(i).getdoclineno());
                cv.put(RTD_ITEM, treceivetaskdetail.get(i).getitem());
                cv.put(RTD_LOCTID, treceivetaskdetail.get(i).getloctid());
                cv.put(RTD_WLOTNO, treceivetaskdetail.get(i).getwlotno());
                cv.put(RTD_UMEASUR, treceivetaskdetail.get(i).getumeasur());
                cv.put(RTD_WMSSTAT, treceivetaskdetail.get(i).getwmsstat());
                cv.put(RTD_TQTYREC, treceivetaskdetail.get(i).gettqtyrec());
                cv.put(RTD_TRKQTYREC, treceivetaskdetail.get(i).gettrkqtyrec());
                cv.put(RTD_REVLEV, treceivetaskdetail.get(i).getrevlev());
                cv.put(RTD_TQTYINC, treceivetaskdetail.get(i).gettqtyinc());
                cv.put(RTD_ITMDESC, treceivetaskdetail.get(i).getitmdesc());
                cv.put(RTD_PCKDESC, treceivetaskdetail.get(i).getpckdesc());
                cv.put(RTD_COUNTRYID, treceivetaskdetail.get(i).getcountryid());
                cv.put(RTD_ITEMSHOW, treceivetaskdetail.get(i).getitemShow());
                cv.put(RTD_COLLECTION, treceivetaskdetail.get(i).getcollection());
                cv.put(RTD_WELEMENT, treceivetaskdetail.get(i).getwelement());
                cv.put(RTD_WIDGETID, treceivetaskdetail.get(i).getwidgetID());
                cv.put(RTD_CATCHWT, treceivetaskdetail.get(i).getcatchwt());
                cv.put(RTD_DECNUM, treceivetaskdetail.get(i).getdecnum());
                cv.put(RTD_LOTREFID, treceivetaskdetail.get(i).getlotrefid());
                cv.put(RTD_LINESPLIT,treceivetaskdetail.get(i).getLinesplit());
                cv.put(RTD_FLAG, treceivetaskdetail.get(i).getFlag());
                cv.put(RTD_ROWNO,  treceivetaskdetail.get(i).getrowNo());
                cv.put(RTD_PALNO, treceivetaskdetail.get(i).getPalno());

                mSqlitedb.insert(RECEIVE_TASK_TRAN_DETAIL, null, cv);
            }
            LogfileCreator.mAppendLog("Receive Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskTranDetail(receivetaskdetail treceivetaskdetail) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            cv.put(RTD_TASKNO, treceivetaskdetail.gettaskno());
            cv.put(RTD_TRANLINENO, treceivetaskdetail.gettranlineno());
            cv.put(RTD_DOCTYPE, treceivetaskdetail.getdoctype());
            cv.put(RTD_DOCNO, treceivetaskdetail.getdocno());
            cv.put(RTD_DOCLINENO, treceivetaskdetail.getdoclineno());
            cv.put(RTD_ITEM, treceivetaskdetail.getitem());
            cv.put(RTD_LOCTID, treceivetaskdetail.getloctid());
            cv.put(RTD_WLOTNO, treceivetaskdetail.getwlotno());
            cv.put(RTD_UMEASUR, treceivetaskdetail.getumeasur());
            cv.put(RTD_WMSSTAT, treceivetaskdetail.getwmsstat());
            cv.put(RTD_TQTYREC, treceivetaskdetail.gettqtyrec());
            cv.put(RTD_TRKQTYREC, treceivetaskdetail.gettqtyinc());
            cv.put(RTD_REVLEV, treceivetaskdetail.getrevlev());
            cv.put(RTD_TQTYINC, treceivetaskdetail.gettqtyinc());
            cv.put(RTD_ITMDESC, treceivetaskdetail.getitmdesc());
            cv.put(RTD_PCKDESC, treceivetaskdetail.getpckdesc());
            cv.put(RTD_COUNTRYID, treceivetaskdetail.getcountryid());
            cv.put(RTD_ITEMSHOW, treceivetaskdetail.getitemShow());
            cv.put(RTD_COLLECTION, treceivetaskdetail.getcollection());
            cv.put(RTD_WELEMENT, treceivetaskdetail.getwelement());
            cv.put(RTD_WIDGETID, treceivetaskdetail.getwidgetID());
            cv.put(RTD_CATCHWT, treceivetaskdetail.getcatchwt());
            cv.put(RTD_DECNUM, treceivetaskdetail.getdecnum());
            cv.put(RTD_LOTREFID, treceivetaskdetail.getlotrefid());
            cv.put(RTD_LINESPLIT, "0");
            cv.put(RTD_FLAG, "N");
            cv.put(RTD_ROWNO, Globals.gRTDetailRowCount);
            Globals.gRTDetailRowCount = Globals.gRTDetailRowCount + 1;
            cv.put(RTD_PALNO,treceivetaskdetail.getPalno());

            mSqlitedb.insert(RECEIVE_TASK_TRAN_DETAIL, null, cv);
            LogfileCreator.mAppendLog("Receive Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    //01/02/23_CIRK_For Fresha to APN
    /*public void addRepackLock(RepackFG repackFG) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + REPACKFG_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(REPACKFG_VPLOCKED, repackFG.getREPACKFG_VPLOCKED());

            mSqlitedb.insert(REPACKFG_TABLE, null, cv);
            LogfileCreator.mAppendLog("RepackFG inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "RepackFG insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }*/


/*       //01/02/23_CIRK_For Fresha to APN
    public void addRepackFG(RepackFG repackFG) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + REPACKFG_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(REPACKFG_PANO, repackFG.getREPACKFG_PANO());
            cv.put(REPACKFG_TRANLINENO, repackFG.getREPACKFG_TRANLINENO());
            cv.put(REPACKFG_ITEM, repackFG.getREPACKFG_ITEM());
            cv.put(REPACKFG_DESCRIP, repackFG.getREPACKFG_DESCRIP());
            cv.put(REPACKFG_UMEASUR, repackFG.getREPACKFG_UMEASUR());
            cv.put(REPACKFG_LOCTID, repackFG.getREPACKFG_LOCTID());
            cv.put(REPACKFG_LOTNO, repackFG.getREPACKFG_LOTNO());
            cv.put(REPACKFG_SERIAL, repackFG.getREPACKFG_SERIAL());
            cv.put(REPACKFG_QTYMADE, repackFG.getREPACKFG_QTYMADE());
            cv.put(REPACKFG_COST, repackFG.getREPACKFG_COST());
            cv.put(REPACKFG_PADATE, repackFG.getREPACKFG_PADATE());
            cv.put(REPACKFG_PASTAT, repackFG.getREPACKFG_PASTAT());
            cv.put(REPACKFG_LCKSTAT, repackFG.getREPACKFG_LCKSTAT());
            cv.put(REPACKFG_LCKUSER, repackFG.getREPACKFG_LCKUSER());
            cv.put(REPACKFG_LCKDATE, repackFG.getREPACKFG_LCKDATE());
            cv.put(REPACKFG_LCKTIME, repackFG.getREPACKFG_LCKTIME());
            cv.put(REPACKFG_ADDUSER, repackFG.getREPACKFG_ADDUSER());
            cv.put(REPACKFG_ADDDATE, repackFG.getREPACKFG_ADDDATE());
            cv.put(REPACKFG_ADDTIME, repackFG.getREPACKFG_ADDTIME());
            cv.put(REPACKFG_PRODLBL, repackFG.getREPACKFG_PRODLBL());
            cv.put(REPACKFG_PACKCHG, repackFG.getREPACKFG_PACKCHG());
            cv.put(REPACKFG_WASHCHG, repackFG.getREPACKFG_WASHCHG());
            cv.put(REPACKFG_COUNTRYID, repackFG.getREPACKFG_COUNTRYID());
            cv.put(REPACKFG_VENDNO, repackFG.getREPACKFG_VENDNO());
            cv.put(REPACKFG_GRADE, repackFG.getREPACKFG_GRADE());
            cv.put(REPACKFG_PROJNO, repackFG.getREPACKFG_PROJNO());
            cv.put(REPACKFG_REMARKS, repackFG.getREPACKFG_REMARKS());
            cv.put(REPACKFG_LCSTQTY, repackFG.getREPACKFG_LCSTQTY());
            cv.put(REPACKFG_CASE_PL, repackFG.getREPACKFG_CASE_PL());
            cv.put(REPACKFG_PALNO, repackFG.getREPACKFG_PALNO());
            cv.put(REPACKFG_SETID, repackFG.getREPACKFG_SETID());
            cv.put(REPACKFG_WEIGHT, repackFG.getREPACKFG_WEIGHT());
            cv.put(REPACKFG_PALLET, repackFG.getREPACKFG_PALLET());
            cv.put(REPACKFG_ID_COL, repackFG.getREPACKFG_ID_COL());
            cv.put(REPACKFG_BINNO, repackFG.getREPACKFG_BINNO());
            cv.put(REPACKFG_POSTPRG, repackFG.getREPACKFG_POSTPRG());
            cv.put(REPACKFG_EXTPALLET, repackFG.getREPACKFG_EXTPALLET());
            cv.put(REPACKFG_EXTCUBE, repackFG.getREPACKFG_EXTCUBE());
            cv.put(REPACKFG_EXTWEIGHT, repackFG.getREPACKFG_EXTWEIGHT());
            cv.put(REPACKFG_BEXTLCST, repackFG.getREPACKFG_BEXTLCST());
            cv.put(REPACKFG_EXTLCST, repackFG.getREPACKFG_EXTLCST());
            cv.put(REPACKFG_BEXTFEES, repackFG.getREPACKFG_BEXTFEES());
            cv.put(REPACKFG_EXTFEES, repackFG.getREPACKFG_EXTFEES());
            cv.put(REPACKFG_TPALLET, repackFG.getREPACKFG_TPALLET());
            cv.put(REPACKFG_TCUBE, repackFG.getREPACKFG_TCUBE());
            cv.put(REPACKFG_TWEIGHT, repackFG.getREPACKFG_TWEIGHT());
            cv.put(REPACKFG_WLOTNO, repackFG.getREPACKFG_WLOTNO());
            cv.put(REPACKFG_ORIGTRANLN, repackFG.getREPACKFG_ORIGTRANLN());
            cv.put(REPACKFG_ORIGTRANL, repackFG.getREPACKFG_ORIGTRANL());
            cv.put(REPACKFG_ORIGDOCLN, repackFG.getREPACKFG_ORIGDOCLN());
            cv.put(REPACKFG_STKUMID, repackFG.getREPACKFG_STKUMID());
            cv.put(REPACKFG_USELOTS, repackFG.getREPACKFG_USELOTS());
            cv.put(REPACKFG_UMFACT, repackFG.getREPACKFG_UMFACT());
            cv.put(REPACKFG_WEIGHT1, repackFG.getREPACKFG_WEIGHT1());
            cv.put(REPACKFG_VOLUME, repackFG.getREPACKFG_VOLUME());
            cv.put(REPACKFG_CATCHWT, repackFG.getREPACKFG_CATCHWT());
            cv.put(REPACKFG_LOTREFID, repackFG.getREPACKFG_LOTREFID());
            cv.put(REPACKFG_LOTEXPL, repackFG.getREPACKFG_LOTEXPL());
            cv.put(REPACKFG_LINESPLIT, repackFG.getREPACKFG_LINESPLIT());
            cv.put(REPACKFG_TRKQTYPK, repackFG.getREPACKFG_TRKQTYPK());
            cv.put(REPACKFG_UPDFLAG, repackFG.getREPACKFG_UPDFLAG());
            cv.put(REPACKFG_VPLOCKED, repackFG.getREPACKFG_VPLOCKED());

            mSqlitedb.insert(REPACKFG_TABLE, null, cv);
            LogfileCreator.mAppendLog("RepackFG inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "RepackFG insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/

/*  //01/02/23_CIRK_For Fresha to APN
    public void updateRawItem(String Item,RepackIngredients repackIngredients) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_WLOTNO,repackIngredients.getRIT_WLOTNO());
            cv.put(WMSDbHelper.RIT_PALNO,repackIngredients.getRIT_PALNO());
            cv.put(WMSDbHelper.RIT_LOTNO,repackIngredients.getRIT_LOTNO());
            if(repackIngredients.getRIT_TRKQTYPK()==null){
                cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.getRIT_QTYUSED());
            }
            //cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.getRIT_TRKQTYPK());
            cv.put(WMSDbHelper.RIT_SLOT,repackIngredients.getRIT_SLOT());
            cv.put(WMSDbHelper.RIT_LOTREFID,repackIngredients.getRIT_LOTREFID());
            cv.put(WMSDbHelper.RIT_REMARKS,repackIngredients.getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_RPALLOCQTY,repackIngredients.getRIT_RPALLOCQTY());
            cv.put(WMSDbHelper.RIT_WHQTY,repackIngredients.getRIT_WHQTY());
            cv.put(WMSDbHelper.RIT_ICQTY,repackIngredients.getRIT_ICQTY());

           */
/* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*//*

            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.getRIT_WLOTNO() + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/

    public void UpdatedSoftKeyBoardChecked(String KeyCheckStatus) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.CONFIG_SETTINGS_SOFTKEYBOARD,KeyCheckStatus);

            mSqlitedb.update(WMSDbHelper.CONFIGSETTINGS_TABLE, cv, "username != '' OR username = ''", null);

            Log.i("KeyCheckStatus : ", "KeyCheckStatus updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "KeyCheckStatus  update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


/*    //01/02/23_CIRK_For Fresha to APN
    public void updateAllocQty(String Item,List<RepackIngredients> repackIngredients,String Allocqty,String qtyUsed,String UpdFlag,String Addflag,String TempQty) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_QTYUSED,qtyUsed);
            cv.put(WMSDbHelper.RIT_ALLOCQTY,Allocqty);
            cv.put(WMSDbHelper.RIT_UPDFLAG,UpdFlag);
            cv.put(WMSDbHelper.RIT_ADDFLAG,Addflag);
            cv.put(WMSDbHelper.RIT_TEMPALLOC,TempQty);


           */
/* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*//*

            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.get(0).getRIT_WLOTNO() + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/

/*    //01/02/23_CIRK_For Fresha to APN
    public void updateRawItemQty(String Item,List<RepackIngredients> repackIngredients,String Qty,String Allocqty,String UpdFlag,String Addflag,String TempQty) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_WLOTNO,repackIngredients.get(0).getRIT_WLOTNO());
            cv.put(WMSDbHelper.RIT_PALNO,repackIngredients.get(0).getRIT_PALNO());
            cv.put(WMSDbHelper.RIT_LOTNO,repackIngredients.get(0).getRIT_LOTNO());
            cv.put(WMSDbHelper.RIT_QTYUSED,Qty);
            if(repackIngredients.get(0).getRIT_TRKQTYPK()==null||Double.parseDouble(repackIngredients.get(0).getRIT_TRKQTYPK())<=0){
                cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.get(0).getRIT_QTYUSED());
            }
            cv.put(WMSDbHelper.RIT_UPDFLAG,UpdFlag);
            cv.put(WMSDbHelper.RIT_ADDFLAG,Addflag);
            cv.put(WMSDbHelper.RIT_ALLOCQTY,Allocqty);
            cv.put(WMSDbHelper.RIT_TEMPALLOC,TempQty);

           */
/* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*//*

            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.get(0).getRIT_WLOTNO() + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/

    public int getMaxTranNum() {

        int Count = 0;

       // mCursor = mSqlitedb.rawQuery("select max(CAST(tranlineNo AS INTEGER)) as tranlineNo from repackIngredient where tranlineNo < 0 ", null);
        mCursor = mSqlitedb.rawQuery("select max(tranlineNo) from repackIngredient where tranlineNo < 0 ", null);


        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

/*  //01/02/23_CIRK_For Fresha to APN
    public void updateFlagData(String Item,List<RepackIngredients> repackIngredients,String wLotno,String Flag,String Flag2) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_PANO,repackIngredients.get(0).getRIT_PANO());
            cv.put(WMSDbHelper.RIT_ADDFLAG,Flag);
            cv.put(WMSDbHelper.RIT_UPDFLAG,Flag2);
            cv.put(WMSDbHelper.RIT_TRANLINENO,repackIngredients.get(0).getRIT_TRANLINENO());
            cv.put(WMSDbHelper.RIT_ORIGTRANLN,repackIngredients.get(0).getRIT_TRANLINENO());
            cv.put(WMSDbHelper.RIT_QTYUSED,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_ALLOCQTY,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_TEMPALLOC,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_BINNO,"");
            cv.put(WMSDbHelper.RIT_LOTEXPL,repackIngredients.get(0).getRIT_LOTEXPL());
            cv.put(WMSDbHelper.RIT_LINESPLIT,repackIngredients.get(0).getRIT_LINESPLIT());

           */
/* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*//*

            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wLotno = '" + wLotno + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/

/*    //01/02/23_CIRK_For Fresha to APN
    public void addRepackIngredients(RepackIngredients repackIngredients) {

        try {
           // mSqlitedb.execSQL("DELETE FROM " + REPACK_INGREDIENT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(RIT_PANO, repackIngredients.getRIT_PANO());
            cv.put(RIT_TRANLINENO, repackIngredients.getRIT_TRANLINENO());
            cv.put(RIT_ITEM, repackIngredients.getRIT_ITEM());
            cv.put(RIT_DESCRIP, repackIngredients.getRIT_DESCRIP());
            cv.put(RIT_UMEASUR, repackIngredients.getRIT_UMEASUR());
            cv.put(RIT_LOCTID, repackIngredients.getRIT_LOCTID());
            cv.put(RIT_LOTNO, repackIngredients.getRIT_LOTNO());
            cv.put(RIT_SERIAL, repackIngredients.getRIT_SERIAL());
            cv.put(RIT_QTYUSED, repackIngredients.getRIT_QTYUSED());
            cv.put(RIT_COST, repackIngredients.getRIT_COST());
            cv.put(RIT_PADATE, repackIngredients.getRIT_PADATE());
            cv.put(RIT_PASTAT, repackIngredients.getRIT_PASTAT());
            cv.put(RIT_LCKSTAT, repackIngredients.getRIT_LCKSTAT());
            cv.put(RIT_LCKUSER, repackIngredients.getRIT_LCKUSER());
            cv.put(RIT_LCKDATE, repackIngredients.getRIT_LCKDATE());
            cv.put(RIT_LCKTIME, repackIngredients.getRIT_LCKTIME());
            cv.put(RIT_ADDUSER, repackIngredients.getRIT_ADDUSER());
            cv.put(RIT_ADDDATE, repackIngredients.getRIT_ADDDATE());
            cv.put(RIT_ADDTIME, repackIngredients.getRIT_ADDTIME());
            cv.put(RIT_COUNTRYID, repackIngredients.getRIT_COUNTRYID());
            cv.put(RIT_VENDNO, repackIngredients.getRIT_VENDNO());
            cv.put(RIT_BINNO, repackIngredients.getRIT_BINNO());
            cv.put(RIT_PALNO, repackIngredients.getRIT_PALNO());
            cv.put(RIT_REMARKS, repackIngredients.getRIT_REMARKS());
            cv.put(RIT_YIELD, repackIngredients.getRIT_YIELD());
            cv.put(RIT_SETID, repackIngredients.getRIT_SETID());
            cv.put(RIT_WEIGHT, repackIngredients.getRIT_WEIGHT());
            cv.put(RIT_ID_COL, repackIngredients.getRIT_ID_COL());
            cv.put(RIT_WLOTNO, repackIngredients.getRIT_WLOTNO());
            cv.put(RIT_ORIGTRANLN, repackIngredients.getRIT_ORIGTRANLN());
            cv.put(RIT_STKUMID, repackIngredients.getRIT_STKUMID());
            cv.put(RIT_USELOTS, repackIngredients.getRIT_USELOTS());
            cv.put(RIT_UMFACT, repackIngredients.getRIT_UMFACT());
            cv.put(RIT_WEIGHT1, repackIngredients.getRIT_WEIGHT1());
            cv.put(RIT_VOLUME, repackIngredients.getRIT_VOLUME());
            cv.put(RIT_CATCHWT, repackIngredients.getRIT_CATCHWT());
            cv.put(RIT_LOTREFID, repackIngredients.getRIT_LOTREFID());
            cv.put(RIT_LOTEXPL, repackIngredients.getRIT_LOTEXPL());
            cv.put(RIT_LINESPLIT, repackIngredients.getRIT_LINESPLIT());
            cv.put(RIT_TRKQTYPK, repackIngredients.getRIT_TRKQTYPK());
            cv.put(RIT_UPDFLAG, repackIngredients.getRIT_UPDFLAG());
            cv.put(RIT_ADDFLAG, repackIngredients.getRIT_ADDFLAG());
            cv.put(RIT_SLOT, repackIngredients.getRIT_SLOT());
            cv.put(RIT_ALLOCQTY, repackIngredients.getRIT_ALLOCQTY());
            cv.put(RIT_TEMPALLOC, repackIngredients.getRIT_TEMPALLOC());
            cv.put(RIT_WHQTY, repackIngredients.getRIT_WHQTY());
            cv.put(RIT_ICQTY, repackIngredients.getRIT_ICQTY());
            cv.put(RIT_RPALLOCQTY, repackIngredients.getRIT_RPALLOCQTY());

            mSqlitedb.insert(REPACK_INGREDIENT_TABLE, null, cv);
            LogfileCreator.mAppendLog("repackIngredients inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "repackIngredients insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/

    public void addReceiveTaskItemClass(receivetaskitemclass treceivetaskitemclass) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_ITEM_CLASS);
            ContentValues cv = new ContentValues();

            cv.put(RTIC_ITMCLSS, treceivetaskitemclass.getitmclss());
            cv.put(RTIC_DESCRIP, treceivetaskitemclass.getdescrip());
            cv.put(RTIC_WELEMENT, treceivetaskitemclass.getwelement());
            cv.put(RTIC_COLLECTION, treceivetaskitemclass.getcollection());
            cv.put(RTIC_WIDGETID, treceivetaskitemclass.getwidgetID());

            mSqlitedb.insert(RECEIVE_TASK_ITEM_CLASS, null, cv);
            LogfileCreator.mAppendLog("Receive Task Item Class: Task Item Class inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Item Class insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskLoadType(receivetaskloadtype treceivetaskloadtype) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_ITEM_CLASS);
            ContentValues cv = new ContentValues();

            cv.put(RTLT_LOADTYPE, treceivetaskloadtype.getloadtype());
            cv.put(RTLT_DESCRIP, treceivetaskloadtype.getdescrip().trim());
            cv.put(RTLT_WELEMENT, treceivetaskloadtype.getwelement());
            cv.put(RTLT_COLLECTION, treceivetaskloadtype.getcollection());
            cv.put(RTLT_WIDGETID, treceivetaskloadtype.getwidgetID());
            cv.put(RTLT_LOADID, treceivetaskloadtype.getLoadId());
            cv.put(RTLT_LOADTYPSTS, treceivetaskloadtype.getLoadTypeStatus());
            cv.put(RTLT_WMSDATE, treceivetaskloadtype.getWmsDate());
            cv.put(RTLT_METRICVAL, treceivetaskloadtype.getMetricval());
            cv.put(RTLT_FLAG, "N");

            mSqlitedb.insert(RECEIVE_TASK_LOAD_TYPE, null, cv);
            LogfileCreator.mAppendLog("Receive Task Load Type: Task Load Type inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Load Type insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskWHMSLT(receivetaskWHMSLT treceivetaskWHMSLT) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_ITEM_CLASS);
            ContentValues cv = new ContentValues();

            cv.put(RTWHMSLT_SLOT, treceivetaskWHMSLT.getSlot());

            mSqlitedb.insert(RECEIVE_TASK_WHMSLT, null, cv);
            LogfileCreator.mAppendLog("Receive Task WHMSLT: Task WHMSLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHMSLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskWHRPLT(receivetaskWHRPLT treceivetaskWHRPLT) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_WHRPLT);
            ContentValues cv = new ContentValues();

            cv.put(RTWHRPLT_TASKNO, treceivetaskWHRPLT.gettaskno());
            cv.put(RTWHRPLT_TASKLINENO, treceivetaskWHRPLT.gettasklineno());
            cv.put(RTWHRPLT_PLTLINENO, treceivetaskWHRPLT.getpltlineno());
            cv.put(RTWHRPLT_TQTY, treceivetaskWHRPLT.gettqty());
            cv.put(RTWHRPLT_PLTSTAT, treceivetaskWHRPLT.getpltstat());
            cv.put(RTWHRPLT_PRTPLTTAG, treceivetaskWHRPLT.getprtplttag());
            cv.put(RTWHRPLT_TRKQTY, treceivetaskWHRPLT.gettrkqty());
            cv.put(RTWHRPLT_SLOT, "");
            cv.put(RTWHRPLT_FLAG, "N");
            cv.put(RTWHRPLT_GTIN, treceivetaskWHRPLT.getgTin());

            mSqlitedb.insert(RECEIVE_TASK_WHRPLT, null, cv);
            LogfileCreator.mAppendLog("Receive Task WHRPLT: Task WHRPLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHRPLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskWHRPLTFromTran(List<receivetaskWHRPLT> treceivetaskWHRPLT) {

        try {
            ContentValues cv = new ContentValues();
            for(int i = 0; i< treceivetaskWHRPLT.size(); i++) {
                cv.put(RTWHRPLT_TASKNO, treceivetaskWHRPLT.get(i).gettaskno());
                cv.put(RTWHRPLT_TASKLINENO, treceivetaskWHRPLT.get(i).gettasklineno());
                cv.put(RTWHRPLT_PLTLINENO, treceivetaskWHRPLT.get(i).getpltlineno());
                cv.put(RTWHRPLT_TQTY, treceivetaskWHRPLT.get(i).gettqty());
                cv.put(RTWHRPLT_PLTSTAT, treceivetaskWHRPLT.get(i).getpltstat());
                cv.put(RTWHRPLT_PRTPLTTAG, treceivetaskWHRPLT.get(i).getprtplttag());
                cv.put(RTWHRPLT_TRKQTY, treceivetaskWHRPLT.get(i).gettrkqty());
                cv.put(RTWHRPLT_SLOT, treceivetaskWHRPLT.get(i).getSlot());
                cv.put(RTWHRPLT_FLAG, treceivetaskWHRPLT.get(i).getFlag());
                cv.put(RTWHRPLT_GTIN, treceivetaskWHRPLT.get(i).getgTin());

                mSqlitedb.insert(RECEIVE_TASK_WHRPLT, null, cv);
            }
            LogfileCreator.mAppendLog("Receive Task WHRPLT: Task WHRPLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHRPLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskWHRPLTToTran(List<receivetaskWHRPLT> treceivetaskWHRPLT) {

        try {
            ContentValues cv = new ContentValues();
            for(int i = 0; i< treceivetaskWHRPLT.size(); i++) {
                cv.put(RTWHRPLT_TASKNO, treceivetaskWHRPLT.get(i).gettaskno());
                cv.put(RTWHRPLT_TASKLINENO, treceivetaskWHRPLT.get(i).gettasklineno());
                cv.put(RTWHRPLT_PLTLINENO, treceivetaskWHRPLT.get(i).getpltlineno());
                cv.put(RTWHRPLT_TQTY, treceivetaskWHRPLT.get(i).gettqty());
                cv.put(RTWHRPLT_PLTSTAT, treceivetaskWHRPLT.get(i).getpltstat());
                cv.put(RTWHRPLT_PRTPLTTAG, treceivetaskWHRPLT.get(i).getprtplttag());
                cv.put(RTWHRPLT_TRKQTY, treceivetaskWHRPLT.get(i).gettrkqty());
                cv.put(RTWHRPLT_SLOT, treceivetaskWHRPLT.get(i).getSlot());
                cv.put(RTWHRPLT_FLAG, treceivetaskWHRPLT.get(i).getFlag());
                cv.put(RTWHRPLT_GTIN, treceivetaskWHRPLT.get(i).getgTin());

                mSqlitedb.insert(RECEIVE_TASK_TRAN_WHRPLT, null, cv);
            }
            LogfileCreator.mAppendLog("Receive Task WHRPLT: Task WHRPLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHRPLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskTranWHRPLT(receivetaskWHRPLT treceivetaskWHRPLT) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + RECEIVE_TASK_WHRPLT);
            ContentValues cv = new ContentValues();

            cv.put(RTWHRPLT_TASKNO, treceivetaskWHRPLT.gettaskno());
            cv.put(RTWHRPLT_TASKLINENO, treceivetaskWHRPLT.gettasklineno());
            cv.put(RTWHRPLT_PLTLINENO, treceivetaskWHRPLT.getpltlineno());
            cv.put(RTWHRPLT_TQTY, treceivetaskWHRPLT.gettqty());
            cv.put(RTWHRPLT_PLTSTAT, treceivetaskWHRPLT.getpltstat());
            cv.put(RTWHRPLT_PRTPLTTAG, treceivetaskWHRPLT.getprtplttag());
            cv.put(RTWHRPLT_TRKQTY, treceivetaskWHRPLT.gettrkqty());
            cv.put(RTWHRPLT_SLOT, "");
            cv.put(RTWHRPLT_FLAG, "N");
            cv.put(RTWHRPLT_GTIN, treceivetaskWHRPLT.getgTin());

            mSqlitedb.insert(RECEIVE_TASK_TRAN_WHRPLT, null, cv);
            LogfileCreator.mAppendLog("Receive Task WHRPLT: Task WHRPLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task WHRPLT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addReceiveTaskPrint(receivetaskprintdetail treceivetaskprintdetail) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(RTP_WLOTNO, treceivetaskprintdetail.getwlotno());
            cv.put(RTP_LOTREFID, treceivetaskprintdetail.getlotrefid());
            cv.put(RTP_ITEM, treceivetaskprintdetail.getitem());
            cv.put(RTP_RECDATE, treceivetaskprintdetail.getrecdate());
            cv.put(RTP_EXPDATE, treceivetaskprintdetail.getexpdate());
            cv.put(RTP_RECUSER, treceivetaskprintdetail.getrecuser());
            cv.put(RTP_TASKNO, treceivetaskprintdetail.gettaskno());
            cv.put(RTP_TASKLINENO, treceivetaskprintdetail.gettasklineno());
            cv.put(RTP_PLTLINENO, treceivetaskprintdetail.getpltlineno());
            cv.put(RTP_PRTPLTTAG, treceivetaskprintdetail.getprtplttag());
            cv.put(RTP_TQTY, treceivetaskprintdetail.gettqty());
            cv.put(RTP_TRKQTY, treceivetaskprintdetail.gettrkqty());
            cv.put(RTP_ITMDESC, treceivetaskprintdetail.getitmdesc());
            cv.put(RTP_CATCHWT, treceivetaskprintdetail.getcatchwt());

            mSqlitedb.insert(RECEIVE_TASK_PRINT, null, cv);
            LogfileCreator.mAppendLog("Receive Task Print: Task Print inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Print insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPhysicalCountSlot(physicalcountSlot tphysicalcountSlot) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(PHYSCIAL_COUNT_SLOT, tphysicalcountSlot.getslot());
            cv.put(PHYSCIAL_COUNT_WMSSTAT, tphysicalcountSlot.getwmsstat());
            cv.put(PHYSCIAL_COUNT_POSTED, tphysicalcountSlot.getposted());
            cv.put(PHYSCIAL_COUNT_GRPCNT, tphysicalcountSlot.getgrpcnt());
            cv.put(PHYSCIAL_COUNT_STATUS, tphysicalcountSlot.getstatus());
            cv.put(PHYSCIAL_COUNT_DOCLINECOUNT, tphysicalcountSlot.getdoclinecount());
            cv.put(PHYSCIAL_COUNT_ROWNO, Globals.gPCSlotRowCount);
            Globals.gPCSlotRowCount = Globals.gPCSlotRowCount + 1;

            mSqlitedb.insert(PHYSCIAL_COUNT_SLOT_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT SLOT: Task inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void addPhysicalCountDetail(physicalcountDetail tphysicalcountDetail) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_DETAIL_SLOT, tphysicalcountDetail.getslot());
            cv.put(PC_DETAIL_COUNTID, tphysicalcountDetail.getcountid());
            cv.put(PC_DETAIL_PAGE, tphysicalcountDetail.getpage());
            cv.put(PC_DETAIL_DOCLINENO, tphysicalcountDetail.getdoclineno());
            cv.put(PC_DETAIL_LOCTID, tphysicalcountDetail.getloctid());
            cv.put(PC_DETAIL_ITEM, tphysicalcountDetail.getitem());
            cv.put(PC_DETAIL_WLOTNO, tphysicalcountDetail.getwlotno());
            cv.put(PC_DETAIL_UMEASUR, tphysicalcountDetail.getumeasur());
            cv.put(PC_DETAIL_TCOUNTQTY, tphysicalcountDetail.gettcountqty());
            cv.put(PC_DETAIL_WMSSTAT, tphysicalcountDetail.getwmsstat());
            cv.put(PC_DETAIL_POSTED, tphysicalcountDetail.getposted());
            cv.put(PC_DETAIL_ITMDESC, tphysicalcountDetail.getitmdesc());
            cv.put(PC_DETAIL_PCKDESC, tphysicalcountDetail.getpckdesc());
            cv.put(PC_DETAIL_DECNUM, tphysicalcountDetail.getdecnum());
            cv.put(PC_DETAIL_LOTREFID, tphysicalcountDetail.getlotrefid());
            cv.put(PC_DETAIL_TQTY, tphysicalcountDetail.gettqty());
            cv.put(PC_DETAIL_ITEMSHOW, tphysicalcountDetail.getitemShow());
            cv.put(PC_DETAIL_SURPRISADD, tphysicalcountDetail.getsurprisadd());
            cv.put(PC_DETAIL_USERID, tphysicalcountDetail.getuserid());
            cv.put(PC_DETAIL_COUNTED, tphysicalcountDetail.getcounted());
            cv.put(PC_DETAIL_COLLECTION, tphysicalcountDetail.getcollection());
            cv.put(PC_DETAIL_WELEMENT, tphysicalcountDetail.getwelement());
            cv.put(PC_DETAIL_WIDGETID, tphysicalcountDetail.getwidgetID());
            //cv.put(PC_DETAIL_ROWNO, Globals.gPCDetailRowCount);

            if(Double.parseDouble(tphysicalcountDetail.gettcountqty()) == 0.0 && !tphysicalcountDetail.getposted().equals("P") && !tphysicalcountDetail.getwmsstat().equals("C")) {
                cv.put(PC_DETAIL_FLAG, "N");
            }else{
                cv.put(PC_DETAIL_FLAG, "Y");
            }

            //Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;

            mSqlitedb.insert(PHYSCIAL_COUNT_DETAIL_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT Detail: Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPhysicalCountTranDetail(physicalcountDetail tphysicalcountDetail) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_DETAIL_SLOT, tphysicalcountDetail.getslot());
            cv.put(PC_DETAIL_COUNTID, tphysicalcountDetail.getcountid());
            cv.put(PC_DETAIL_PAGE, tphysicalcountDetail.getpage());
            cv.put(PC_DETAIL_DOCLINENO, tphysicalcountDetail.getdoclineno());
            cv.put(PC_DETAIL_LOCTID, tphysicalcountDetail.getloctid());
            cv.put(PC_DETAIL_ITEM, tphysicalcountDetail.getitem());
            cv.put(PC_DETAIL_WLOTNO, tphysicalcountDetail.getwlotno());
            cv.put(PC_DETAIL_UMEASUR, tphysicalcountDetail.getumeasur());
            cv.put(PC_DETAIL_TCOUNTQTY, tphysicalcountDetail.gettcountqty());
            cv.put(PC_DETAIL_WMSSTAT, tphysicalcountDetail.getwmsstat());
            cv.put(PC_DETAIL_POSTED, tphysicalcountDetail.getposted());
            cv.put(PC_DETAIL_ITMDESC, tphysicalcountDetail.getitmdesc());
            cv.put(PC_DETAIL_PCKDESC, tphysicalcountDetail.getpckdesc());
            cv.put(PC_DETAIL_DECNUM, tphysicalcountDetail.getdecnum());
            cv.put(PC_DETAIL_LOTREFID, tphysicalcountDetail.getlotrefid());
            cv.put(PC_DETAIL_TQTY, tphysicalcountDetail.gettqty());
            cv.put(PC_DETAIL_ITEMSHOW, tphysicalcountDetail.getitemShow());
            cv.put(PC_DETAIL_SURPRISADD, tphysicalcountDetail.getsurprisadd());
            cv.put(PC_DETAIL_USERID, tphysicalcountDetail.getuserid());
            cv.put(PC_DETAIL_COUNTED, tphysicalcountDetail.getcounted());
            cv.put(PC_DETAIL_COLLECTION, tphysicalcountDetail.getcollection());
            cv.put(PC_DETAIL_WELEMENT, tphysicalcountDetail.getwelement());
            cv.put(PC_DETAIL_WIDGETID, tphysicalcountDetail.getwidgetID());
            //cv.put(PC_DETAIL_ROWNO, Globals.gPCDetailRowCount);
            if(Double.parseDouble(tphysicalcountDetail.gettcountqty()) == 0.0 && !tphysicalcountDetail.getposted().equals("P")  && !tphysicalcountDetail.getwmsstat().equals("C")) {
                cv.put(PC_DETAIL_FLAG, "N");
            }else{
                cv.put(PC_DETAIL_FLAG, "Y");
            }
            //Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;

            mSqlitedb.insert(PHYSCIAL_COUNT_DETAIL_TRAN_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT Detail: Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPhysicalCountUOM(physicalcountUom tphysicalcountUom) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_UOM_ITEM, tphysicalcountUom.getitem());
            cv.put(PC_UOM_UMEASUR, tphysicalcountUom.getumeasur());

            mSqlitedb.insert(PHYSCIAL_COUNT_UOM_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT UOM: UOM inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "UOM insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPhysicalCountWHMLOT(physicalcountWHMLOT tphysicalcountWHMLOT) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_WHMLOT_WLOTNO, tphysicalcountWHMLOT.getwlotno());
            cv.put(PC_WHMLOT_ITEM, tphysicalcountWHMLOT.getitem());
            cv.put(PC_WHMLOT_LOTREFID, tphysicalcountWHMLOT.getlotrefid());

            mSqlitedb.insert(PHYSCIAL_COUNT_WHMLOT_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT WHMLOT: WHMLOT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "WHMLOT insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPhysicalCountWHMQTY(physicalcountWHMQTY tphysicalcountWHMQTY) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_WHMQTY_SLOT, tphysicalcountWHMQTY.getslot());
            cv.put(PC_WHMQTY_ITEM, tphysicalcountWHMQTY.getitem());
            cv.put(PC_WHMQTY_WLOTNO, tphysicalcountWHMQTY.getwlotno());
            cv.put(PC_WHMQTY_UMEASUR, tphysicalcountWHMQTY.getumeasur());

            mSqlitedb.insert(PHYSCIAL_COUNT_WHMQTY_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT WHMQTY: WHMQTY inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "WHMQTY insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addPhysicalCountICITEM(physicalcountICITEM tphysicalcountICITEM) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_ICITEM_ITEM, tphysicalcountICITEM.getitem());
            cv.put(PC_ICITEM_INVTYPE, tphysicalcountICITEM.getinvtype());
            cv.put(PC_ICITEM_STKUMID, tphysicalcountICITEM.getstkumid());
            cv.put(PC_ICITEM_PUNMSID, tphysicalcountICITEM.getpunmsid());
            cv.put(PC_ICITEM_SUNMSID, tphysicalcountICITEM.getsunmsid());
            cv.put(PC_ICITEM_BRNUM1, tphysicalcountICITEM.getbrnam1());
            cv.put(PC_ICITEM_BRNUM2, tphysicalcountICITEM.getbrnam2());
            cv.put(PC_ICITEM_BRNAM3, tphysicalcountICITEM.getbrnam3());
            cv.put(PC_ICITEM_BRNAM4, tphysicalcountICITEM.getbrnam4());
            cv.put(PC_ICITEM_BRNAM5, tphysicalcountICITEM.getbrnam5());
            cv.put(PC_ICITEM_ITMDESC, tphysicalcountICITEM.getitmdesc());
            cv.put(PC_ICITEM_PCKDESC, tphysicalcountICITEM.getpckdesc());
            cv.put(PC_ICITEM_DECNUM, tphysicalcountICITEM.getdecnum());
            cv.put(PC_ICITEM_ITEMSHOW, tphysicalcountICITEM.getitemShow());

            mSqlitedb.insert(PHYSCIAL_COUNT_ICITEM_TABLE, null, cv);
            LogfileCreator.mAppendLog("PHYSCIAL COUNT ICITEM: ICITEM inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "ICITEM insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addMoveManuallylot(MoveManually moveManually) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MOVE_MANUALLY_WLOTNO, moveManually.getMmWlotno());
            cv.put(MOVE_MANUALLY_ITEMNO, moveManually.getMmItem());
            cv.put(MOVE_MANUALLY_SLOTNO, moveManually.getMmSlot());
            cv.put(MOVE_MANUALLY_LOCTID, moveManually.getMmLoctid());
            cv.put(MOVE_MANUALLY_UOM, moveManually.getMmUOM());
            cv.put(MOVE_MANUALLY_QTY, moveManually.getMmQty());
            cv.put(MOVE_MANUALLY_TRKQTY, moveManually.getMmTrkqty());
            cv.put(MOVE_MANUALLY_ISLOCKED, moveManually.getMmIslocked());
            cv.put(MOVE_MANUALLY_ITMDESC, moveManually.getMmItemDesc());
            cv.put(MOVE_MANUALLY_CATCHWT, moveManually.getMmCatchwt());
            cv.put(MOVE_MANUALLY_LOTREFID, moveManually.getMmLotrefid());
            cv.put(MOVE_MANUALLY_RPALLOC, moveManually.getMmrpAlloc());

            mSqlitedb.insert(MOVE_MANUALLY_TABLE, null, cv);
            LogfileCreator.mAppendLog("Move Manually inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Move Manually insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addBreakerUOM(BreakerUomUtility breakerUomUtility) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(BREAKER_UOM_WLOTNO, breakerUomUtility.getBuWlotno());
            cv.put(BREAKER_UOM_ITEMNO, breakerUomUtility.getBuItem());
            cv.put(BREAKER_UOM_SLOTNO, breakerUomUtility.getBuSlot());
            cv.put(BREAKER_UOM_LOCTID, breakerUomUtility.getBuLoctid());
            cv.put(BREAKER_UOM_UOM, breakerUomUtility.getBuUOM());
            cv.put(BREAKER_UOM_QTY, breakerUomUtility.getBuQty());
            cv.put(BREAKER_UOM_TRKQTY, breakerUomUtility.getBuTrkqty());
            cv.put(BREAKER_UOM_ISLOCKED, breakerUomUtility.getBuIslocked());
            cv.put(BREAKER_UOM_ITMDESC, breakerUomUtility.getBuItemDesc());
            cv.put(BREAKER_UOM_CATCHWT, breakerUomUtility.getBuCatchwt());
            cv.put(BREAKER_UOM_STKUMID, breakerUomUtility.getBuStkumid());
            cv.put(BREAKER_UOM_LOTREFID, breakerUomUtility.getBuLotRefId());

            mSqlitedb.insert(BREAKER_UOM_UTILITY_TABLE, null, cv);
            LogfileCreator.mAppendLog("Breaker UOM Utility inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Breaker UOM Utility insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addBreakerUOMList(BreakerUOMList breakerUOMList) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(BREAKER_UOM_LIST_BRNAME, breakerUOMList.getBuBrName());
            cv.put(BREAKER_UOM_LIST_BRUNIT, breakerUOMList.getBuUnit());

            mSqlitedb.insert(BREAKER_UOM_LIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("Breaker UOM List inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Breaker UOM List insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public ArrayList<MoveManually> getMoveManually(String wLotno,String slotNo,String uom) {
        ArrayList<MoveManually> mmList = new ArrayList<>();
        MoveManually moveManually = null;

        mCursor = mSqlitedb.rawQuery("select * from moveManually where lotrefid='" + wLotno.trim() + "' and UOM='" + uom + "' and slotNo='" + slotNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                moveManually = new MoveManually();
                moveManually.setMmWlotno(mCursor.getString(0));
                moveManually.setMmItem(mCursor.getString(1));
                moveManually.setMmSlot(mCursor.getString(2));
                moveManually.setMmLoctid(mCursor.getString(3));
                moveManually.setMmUOM(mCursor.getString(4));
                moveManually.setMmQty(Double.parseDouble((mCursor.getString(5))));
                moveManually.setMmTrkqty(Double.parseDouble(mCursor.getString(6)));
                moveManually.setMmIslocked(mCursor.getString(7));
                moveManually.setMmItemDesc(mCursor.getString(8));
                moveManually.setMmCatchwt(mCursor.getString(9));
                moveManually.setMmLotrefid(mCursor.getString(10));

                mmList.add(moveManually);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHITRL";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return mmList;
    }
    public ArrayList<MoveManually> getMoveManuallyDetails(String wLotno) {
        ArrayList<MoveManually> mmList = new ArrayList<>();
        MoveManually moveManually = null;

        String wlotNum = wLotno.trim();

        mCursor = mSqlitedb.rawQuery("select * from moveManually where lotrefid = '" + wlotNum + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                moveManually = new MoveManually();
                moveManually.setMmWlotno(mCursor.getString(0));
                moveManually.setMmItem(mCursor.getString(1));
                moveManually.setMmSlot(mCursor.getString(2));
                moveManually.setMmLoctid(mCursor.getString(3));
                moveManually.setMmUOM(mCursor.getString(4));
                moveManually.setMmQty(Double.parseDouble((mCursor.getString(5))));
                moveManually.setMmTrkqty(Double.parseDouble(mCursor.getString(6)));
                moveManually.setMmIslocked(mCursor.getString(7));
                moveManually.setMmItemDesc(mCursor.getString(8));
                moveManually.setMmrpAlloc(mCursor.getString(9));
                moveManually.setMmCatchwt(mCursor.getString(10));
                moveManually.setMmLotrefid(mCursor.getString(11));

                mmList.add(moveManually);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHITRL";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return mmList;
    }

    public void insertExportLot() {

        mCursor = mSqlitedb.rawQuery("insert  into exportLot (exportLotNo) SELECT wLotno from picktaskdetail where Flag='Y' ", null);
        try {
            while (mCursor.moveToNext()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select breaker UOM";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }

    }

    public ArrayList<BreakerUomUtility> getBreakerUOM(String wLotno) {
        ArrayList<BreakerUomUtility> breakerUomUtilityArrayList = new ArrayList<>();
        BreakerUomUtility breakerUomUtility = null;

        mCursor = mSqlitedb.rawQuery("select * from breakerUOMUtlity where lotrefid='" + wLotno.trim() + "'", null);
        try {
            while (mCursor.moveToNext()) {
                breakerUomUtility = new BreakerUomUtility();
                breakerUomUtility.setBuWlotno(mCursor.getString(0));
                breakerUomUtility.setBuItem(mCursor.getString(1));
                breakerUomUtility.setBuSlot(mCursor.getString(2));
                breakerUomUtility.setBuLoctid(mCursor.getString(3));
                breakerUomUtility.setBuUOM(mCursor.getString(4));
                breakerUomUtility.setBuQty(Double.parseDouble((mCursor.getString(5))));
                breakerUomUtility.setBuTrkqty(Double.parseDouble(mCursor.getString(6)));
                breakerUomUtility.setBuIslocked(mCursor.getString(7));
                breakerUomUtility.setBuItemDesc(mCursor.getString(8));
                breakerUomUtility.setBuCatchwt(mCursor.getString(9));
                breakerUomUtility.setBuStkumid(mCursor.getString(10));
                breakerUomUtility.setBuLotRefId(mCursor.getString(11));

                breakerUomUtilityArrayList.add(breakerUomUtility);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select breaker UOM";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return breakerUomUtilityArrayList;
    }

    public ArrayList<BreakerUOMList> getBreakerUOMList() {
        ArrayList<BreakerUOMList> uomListArrayList = new ArrayList<>();
        BreakerUOMList breakerUOMList = null;

        mCursor = mSqlitedb.rawQuery("select * from breakerUomList ", null);
        try {
            while (mCursor.moveToNext()) {
                breakerUOMList = new BreakerUOMList();
                breakerUOMList.setBuBrName(mCursor.getString(0));
                breakerUOMList.setBuUnit(mCursor.getString(1));


                uomListArrayList.add(breakerUOMList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select breaker UOM List";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return uomListArrayList;
    }

    public void addMoveManuallyTran(MoveManuallyTransaction moveManuallyTransaction) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MOVE_MANUALLY_TRANSACTION_WLOTNO, moveManuallyTransaction.getMmTranWlotno());
            cv.put(MOVE_MANUALLY_TRANSACTION_ITEMNO, moveManuallyTransaction.getMmTranItem());
            cv.put(MOVE_MANUALLY_TRANSACTION_SLOTNO, moveManuallyTransaction.getMmTranSlot());
            cv.put(MOVE_MANUALLY_TRANSACTION_LOCTID, moveManuallyTransaction.getMmTranLoctid());
            cv.put(MOVE_MANUALLY_TRANSACTION_UOM, moveManuallyTransaction.getMmTranUOM());
            cv.put(MOVE_MANUALLY_TRANSACTION_QTY, moveManuallyTransaction.getMmTranQty());
            cv.put(MOVE_MANUALLY_TRANSACTION_TRKQTY, moveManuallyTransaction.getMmTranTrkqty());
            cv.put(MOVE_MANUALLY_TRANSACTION_EQTY, moveManuallyTransaction.getMmTranEqty());
            cv.put(MOVE_MANUALLY_TRANSACTION_ITMDESC, moveManuallyTransaction.getMmTranItmDesc());
            cv.put(MOVE_MANUALLY_TRANSACTION_CATCHWT, moveManuallyTransaction.getMmTranCatchwt());
            cv.put(MOVE_MANUALLY_TRANSACTION_LotrefId, moveManuallyTransaction.getMmTranLotrefid());

            mSqlitedb.insert(MOVE_MANUALLY_TRANSACTION_TABLE, null, cv);
            //mSqlitedb.update(MOVE_MANUALLY_TRANSACTION_TABLE, cv,null,null);
            LogfileCreator.mAppendLog("Move Manually inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Move Manually insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public boolean isDataAvailableForMoveManually(String wLotno,String slotNo,String uom) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from moveManuallyTransaction where mmtLotrefid='" + wLotno.trim() + "' and mmtuom='" + uom + "' and mmtslotNo='" + slotNo + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public String getMmtQty(String wLotno,String slotNo,String uom) {

        String mmtQty = "";

        mCursor = mSqlitedb.rawQuery("select mmtEqty from moveManuallyTransaction where mmtLotrefid='" + wLotno.trim() + "' and mmtuom='" + uom + "' and mmtslotNo='" + slotNo + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mmtQty = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mmtQty;
    }

    public String getPickTaskLotNum(String wLotno) {

        String mmtQty = "";

       // mCursor = mSqlitedb.rawQuery("select wlotno from picktaskWHMLOT where wlotno='" + wLotno.trim() + "'", null);
        mCursor = mSqlitedb.rawQuery("select wlotno from picktaskWHMLOT where lotrefid='" + wLotno.trim() + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mmtQty = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mmtQty;
    }

    public String getPhysicalTaskLotNum(String wLotno) {

        String mmtQty = "";

        mCursor = mSqlitedb.rawQuery("select wlotno from physicalcountWHMLOT where lotrefid='" + wLotno.trim() + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mmtQty = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mmtQty;
    }

    public String getSubTranNo(String itemNum) {

        String tranNum = "";
        String flag ="N";

        mCursor = mSqlitedb.rawQuery("select Tranlineno from picktaskdetail where Item ='" + itemNum.trim() + "' and Flag ='" + flag + "'", null);

        try {
            while (mCursor.moveToNext()) {
                tranNum = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return tranNum;
    }


    public void mAddLoadType(receivetaskloadtype treceivetaskloadtype, String loadvalue, String load) {

        try {

            int count = 0;

            mCursor = mSqlitedb.rawQuery("select * from receivetaskloadtype where descrip = 'Blank'", null);
            count = mCursor.getCount();
            mCursor.close();
            if (count != 0) {
                ContentValues cv = new ContentValues();
                cv.put(WMSDbHelper.RTLT_LOADTYPSTS,
                        "1");
                cv.put(WMSDbHelper.RTLT_METRICVAL,
                        loadvalue);

                mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_LOAD_TYPE, cv, "descrip = '"
                        + load + "'", null);

                Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");
            } else {

                ContentValues cv = new ContentValues();

                cv.put(RTLT_LOADTYPE, load);
                cv.put(RTLT_DESCRIP, load);
                cv.put(RTLT_WELEMENT, treceivetaskloadtype.getwelement());
                cv.put(RTLT_COLLECTION, treceivetaskloadtype.getcollection());
                cv.put(RTLT_WIDGETID, treceivetaskloadtype.getwidgetID());
                cv.put(RTLT_LOADID, treceivetaskloadtype.getLoadId());
                cv.put(RTLT_METRICVAL, loadvalue);
                cv.put(RTLT_LOADTYPSTS, "1");

                mSqlitedb.insert(RECEIVE_TASK_LOAD_TYPE, null, cv);
                LogfileCreator.mAppendLog("Receive Task LOAD TYPE: LOAD TYPE inserted successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void mUpdateLoadType(String loadvalue, String load) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RTLT_LOADTYPSTS,
                    "1");
            cv.put(WMSDbHelper.RTLT_METRICVAL,
                    loadvalue);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_LOAD_TYPE, cv, "descrip = '"
                    + load + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public configsettings mGetUserData() {
        configsettings settings = null;

        mCursor = mSqlitedb.rawQuery("select * from configsettings ", null);
        try {
            while (mCursor.moveToNext()) {
                settings = new configsettings();
                settings.setUsername(mCursor.getString(7));
                settings.setPassword(mCursor.getString(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Username and Password selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return settings;
    }

    public boolean isDataAvailableForPost(String taskNum) {

        boolean isAvail = false;

        int count = 0;

       // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Flag = 'Y' AND taskNum = '" + taskNum + "' " , null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + taskNum + "' " , null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }


    public ArrayList<Movetasklist> getMoveTaskList() {
        ArrayList<Movetasklist> movetaskList = new ArrayList<>();
        Movetasklist tMovetasklist = null;

        mCursor = mSqlitedb.rawQuery("select * from moveTaskList", null);
        try {
            while (mCursor.moveToNext()) {
                tMovetasklist = new Movetasklist();

                tMovetasklist.setTaskNo(mCursor.getString(mCursor.getColumnIndex(MT_TASK_NO)));
                tMovetasklist.setStatus(mCursor.getString(mCursor.getColumnIndex(MT_STATUS)));
                tMovetasklist.setTaskType(mCursor.getString(mCursor.getColumnIndex(MT_TASK_TYPE)));
                tMovetasklist.setRowPrty(mCursor.getString(mCursor.getColumnIndex(MT_ROWPRTY)));


                movetaskList.add(tMovetasklist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive Task List selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return movetaskList;
    }

    public ArrayList<receivetasklist> getReceiveTaskList() {
        ArrayList<receivetasklist> receiveList = new ArrayList<>();
        receivetasklist treceivetasklist = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetasklist", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetasklist = new receivetasklist();

                treceivetasklist.setTaskNo(mCursor.getString(mCursor.getColumnIndex(RT_TASKNO)));
                treceivetasklist.setStatus(mCursor.getString(mCursor.getColumnIndex(RT_STATUS)));
                treceivetasklist.setUserid(mCursor.getString(mCursor.getColumnIndex(RT_USERID)));
                treceivetasklist.setDoctype(mCursor.getString(mCursor.getColumnIndex(RT_DOCTYPE)));
                treceivetasklist.setDocno(mCursor.getString(mCursor.getColumnIndex(RT_DOCNO)));

                receiveList.add(treceivetasklist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive Task List selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveList;
    }

    public ArrayList<picktasklist> getPickTaskList() {
        ArrayList<picktasklist> pickList = new ArrayList<>();
        picktasklist tpicktasklist = null;

        mCursor = mSqlitedb.rawQuery("select * from picktasklist ORDER by taskno DESC", null);

        try {
            while (mCursor.moveToNext()) {
                tpicktasklist = new picktasklist();

                tpicktasklist.setTaskNo(mCursor.getString(mCursor.getColumnIndex(TASK_NO)));
                tpicktasklist.setRoute(mCursor.getString(mCursor.getColumnIndex(ROUTE)));
                tpicktasklist.setStop(mCursor.getString(mCursor.getColumnIndex(STOP)));
                tpicktasklist.setStatus(mCursor.getString(mCursor.getColumnIndex(STATUS)));
                tpicktasklist.setDate(mCursor.getString(mCursor.getColumnIndex(DATE)));
                tpicktasklist.setSonos(mCursor.getString(mCursor.getColumnIndex(SONOs)));

                pickList.add(tpicktasklist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickList;
    }


    public ArrayList<SalesOrderCategoryList> getSalesOrderChargesList() {
        ArrayList<SalesOrderCategoryList> tSalesOrderCategoryList = new ArrayList<>();
        SalesOrderCategoryList mSalesOrderCategoryList = null;

        mCursor = mSqlitedb.rawQuery("select * from SoChargesCategory ORDER BY Category ASC", null);

        try {
            while (mCursor.moveToNext()) {
                mSalesOrderCategoryList = new SalesOrderCategoryList();

                mSalesOrderCategoryList.setCategory(mCursor.getString(mCursor.getColumnIndex(CHARGES_CATEGORY)));


                tSalesOrderCategoryList.add(mSalesOrderCategoryList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "SoChargesCategory selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return tSalesOrderCategoryList;
    }



    public ArrayList<String> getDetailTaskList() {
        ArrayList<String> detailTaskList = new ArrayList<>();

        mCursor = mSqlitedb.rawQuery("select DISTINCT taskNum from picktaskdetail", null);
        try {
            while (mCursor.moveToNext()) {
                detailTaskList.add(mCursor.getString(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return detailTaskList;
    }


    public ArrayList<ResetItemDetails> getResetPalletAllocDetails(String pallno) {
        ArrayList<ResetItemDetails> mResetItemDetails = new ArrayList<>();
        ResetItemDetails tResetItemDetails = null;

        mCursor = mSqlitedb.rawQuery("select * from Resetitemdetail where Palno = '"+pallno+"' ", null);

        try {
            while (mCursor.moveToNext()) {

                tResetItemDetails = new ResetItemDetails();

                tResetItemDetails.setItem(mCursor.getString(mCursor.getColumnIndex(RID_ITEM)));
                tResetItemDetails.setLoctid(mCursor.getString(mCursor.getColumnIndex(RID_LOCTID)));
                tResetItemDetails.setWlotno(mCursor.getString(mCursor.getColumnIndex(RID_WLOTNO)));
                tResetItemDetails.setLotno(mCursor.getString(mCursor.getColumnIndex(RID_LOTNO)));
                tResetItemDetails.setPalno(mCursor.getString(mCursor.getColumnIndex(RID_PALNO)));
                tResetItemDetails.setSlot(mCursor.getString(mCursor.getColumnIndex(RID_SLOT)));
                tResetItemDetails.setUmeasur(mCursor.getString(mCursor.getColumnIndex(RID_UMEASUR)));
                tResetItemDetails.setTqty(mCursor.getString(mCursor.getColumnIndex(RID_TQTY)));
                tResetItemDetails.setRpallocqty(mCursor.getString(mCursor.getColumnIndex(RID_RPALLOCQTY)));
                tResetItemDetails.setWhqty(mCursor.getString(mCursor.getColumnIndex(RID_WHQTY)));
                tResetItemDetails.setIcqty(mCursor.getString(mCursor.getColumnIndex(RID_ICQTY)));
                tResetItemDetails.setItmdesc(mCursor.getString(mCursor.getColumnIndex(RID_ITMDESC)));
                tResetItemDetails.setQty(mCursor.getString(mCursor.getColumnIndex(RID_QTY)));
                tResetItemDetails.setPsoaloc(mCursor.getString(mCursor.getColumnIndex(RID_PSOALOC)));
                tResetItemDetails.setPrpaloc(mCursor.getString(mCursor.getColumnIndex(RID_PRPALOC)));
                tResetItemDetails.setPtfrout(mCursor.getString(mCursor.getColumnIndex(RID_PTFROUT)));
                tResetItemDetails.setPtmaloc(mCursor.getString(mCursor.getColumnIndex(RID_PTMALOC)));
                //tResetItemDetails.setOpensocount(mCursor.getString(mCursor.getColumnIndex(RID_OPENSOCOUNT)));
                //tResetItemDetails.setOpenrepackcount(mCursor.getString(mCursor.getColumnIndex(RID_OPENREPACKCOUNT)));

                mResetItemDetails.add(tResetItemDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return mResetItemDetails;
    }


    public ArrayList<physicalcountSlot> getPhycialCountList() {
        ArrayList<physicalcountSlot> physicalCountList = new ArrayList<>();
        physicalcountSlot tphysicalcountlist = null;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountSlot", null);
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountlist = new physicalcountSlot();

                tphysicalcountlist.setslot(mCursor.getString(mCursor.getColumnIndex(PHYSCIAL_COUNT_SLOT)));
                tphysicalcountlist.setstatus(mCursor.getString(mCursor.getColumnIndex(PHYSCIAL_COUNT_STATUS)));

                physicalCountList.add(tphysicalcountlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalCountList;
    }

    public void resetPCDetailRowNo() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.PC_DETAIL_ROWNO, "");
            mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TABLE, cv, "", null);

            Log.i("Physical Count", "Physical Count Detail rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Physical Count Detail rowno reset  failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateDetailTaskNum() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.DETAIL_TASKNO, Globals.gTaskNo);
            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, "taskNum is NULL", null);

            Log.i("Physical Count", "Physical Count Detail rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Physical Count Detail rowno reset  failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }


    public void updateDetailTempAllocTaskNum() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.DETAIL_TASKNO, Globals.gTaskNo);
            mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv, "taskNum is NULL", null);

            Log.i("Physical Count", "Physical Count Detail rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Physical Count Detail rowno reset  failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }


    public void updatePTHeaderTaskNum() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.PICK_TASKNO, Globals.gTaskNo);
            mSqlitedb.update(WMSDbHelper.PICK_TASK_HEADER, cv, "taskNum is NULL", null);

            Log.i("taskNum", "TaskNum rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "TaskNum rowno reset successfully...";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateWHMQTYTaskNum() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.WHMQTY_TASKNUM, Globals.gTaskNo);
            mSqlitedb.update(WMSDbHelper.PICK_TASK_WHMQTY, cv, "taskNum is NULL", null);

            Log.i("taskNum", "TaskNum rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "TaskNum rowno reset successfully...";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateEditFlag() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.DETAIL_FLAG, "Y");
            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, " taskNum = '" + Globals.gTaskNo + "' and " + "Flag='H' ", null);

            Log.i("taskNum", "TaskNum rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "TaskNum rowno reset successfully...";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateWHMLOTTaskNum() {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.WHMLOT_TASKNUM, Globals.gTaskNo);
            mSqlitedb.update(WMSDbHelper.PICK_TASK_WHMLOT, cv, "taskNum is NULL", null);

            Log.i("taskNum", "TaskNum rowno reset successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "TaskNum rowno reset successfully...";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public ArrayList<physicalcountDetail> getPhycialCountDetailUpdatedFlag() {
        ArrayList<physicalcountDetail> physicalcountDetailList = new ArrayList<>();
        physicalcountDetail tphysicalcountDetaillist = null;

            mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where Flag = 'Y'" , null);
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountDetaillist = new physicalcountDetail();

                tphysicalcountDetaillist.setslot(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SLOT)));
                tphysicalcountDetaillist.setcountid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTID)));
                tphysicalcountDetaillist.setpage(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PAGE)));
                tphysicalcountDetaillist.setdoclineno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO)));
                tphysicalcountDetaillist.setloctid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOCTID)));
                tphysicalcountDetaillist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEM)));
                tphysicalcountDetaillist.setwlotno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WLOTNO)));
                tphysicalcountDetaillist.setumeasur(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_UMEASUR)));
                tphysicalcountDetaillist.settcountqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TCOUNTQTY)));
                tphysicalcountDetaillist.setwmsstat(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WMSSTAT)));
                tphysicalcountDetaillist.setposted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_POSTED)));
                tphysicalcountDetaillist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITMDESC)));
                tphysicalcountDetaillist.setpckdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PCKDESC)));
                tphysicalcountDetaillist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DECNUM)));
                tphysicalcountDetaillist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOTREFID)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEMSHOW)));
                tphysicalcountDetaillist.setsurprisadd(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SURPRISADD)));
                tphysicalcountDetaillist.setuserid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_USERID)));
                tphysicalcountDetaillist.setcounted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTED)));
                tphysicalcountDetaillist.setcollection(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COLLECTION)));
                tphysicalcountDetaillist.setwelement(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WELEMENT)));
                tphysicalcountDetaillist.setwidgetID(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WIDGETID)));
                //tphysicalcountDetaillist.setRowNo(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ROWNO)));
                tphysicalcountDetaillist.setRowNo(String.valueOf(Globals.gPCDetailRowCount));
                tphysicalcountDetaillist.setFlag(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_FLAG)));
                String doclineNum =mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO));
                physicalcountDetailList.add(tphysicalcountDetaillist);

                ContentValues cv = new ContentValues();
                cv.put(WMSDbHelper.PC_DETAIL_ROWNO, String.valueOf(Globals.gPCDetailRowCount));
                mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TABLE, cv, "doclineno = '" + doclineNum + "'", null);
                Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountDetailList;
    }

    public ArrayList<physicalcountDetail> getPhycialCountDetailList() {
        ArrayList<physicalcountDetail> physicalcountDetailList = new ArrayList<>();
        physicalcountDetail tphysicalcountDetaillist = null;
        if(!Globals.FROMHIDE) {
        mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                " and userid = '"+ Globals.gUsercode +"'", null);

        /*mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                " and userid = '"+ Globals.gUsercode +"' ORDER BY CAST(UpdatedNo AS Integer) DESC", null);*/

        }else{
            mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                    " and userid = '"+ Globals.gUsercode +"' and Flag = 'Y' ORDER BY CAST(UpdatedNo AS Integer) DESC" , null);
        }
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountDetaillist = new physicalcountDetail();

                tphysicalcountDetaillist.setslot(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SLOT)));
                tphysicalcountDetaillist.setcountid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTID)));
                tphysicalcountDetaillist.setpage(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PAGE)));
                tphysicalcountDetaillist.setdoclineno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO)));
                tphysicalcountDetaillist.setloctid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOCTID)));
                tphysicalcountDetaillist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEM)));
                tphysicalcountDetaillist.setwlotno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WLOTNO)));
                tphysicalcountDetaillist.setumeasur(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_UMEASUR)));
                tphysicalcountDetaillist.settcountqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TCOUNTQTY)));
                tphysicalcountDetaillist.setwmsstat(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WMSSTAT)));
                tphysicalcountDetaillist.setposted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_POSTED)));
                tphysicalcountDetaillist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITMDESC)));
                tphysicalcountDetaillist.setpckdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PCKDESC)));
                tphysicalcountDetaillist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DECNUM)));
                tphysicalcountDetaillist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOTREFID)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEMSHOW)));
                tphysicalcountDetaillist.setsurprisadd(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SURPRISADD)));
                tphysicalcountDetaillist.setuserid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_USERID)));
                tphysicalcountDetaillist.setcounted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTED)));
                tphysicalcountDetaillist.setcollection(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COLLECTION)));
                tphysicalcountDetaillist.setwelement(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WELEMENT)));
                tphysicalcountDetaillist.setwidgetID(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WIDGETID)));
                //tphysicalcountDetaillist.setRowNo(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ROWNO)));
                tphysicalcountDetaillist.setRowNo(String.valueOf(Globals.gPCDetailRowCount));
                tphysicalcountDetaillist.setFlag(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_FLAG)));
                String doclineNum =mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO));
                physicalcountDetailList.add(tphysicalcountDetaillist);

                ContentValues cv = new ContentValues();
                cv.put(WMSDbHelper.PC_DETAIL_ROWNO, String.valueOf(Globals.gPCDetailRowCount));
                mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TABLE, cv, "doclineno = '" + doclineNum + "'", null);
                Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountDetailList;
    }

    public ArrayList<physicalcountDetail> getPhycialCountDetailListForHide() {
        ArrayList<physicalcountDetail> physicalcountDetailList = new ArrayList<>();
        physicalcountDetail tphysicalcountDetaillist = null;

            mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                    " and userid = '"+ Globals.gUsercode +"'", null);

        try {
            while (mCursor.moveToNext()) {
                tphysicalcountDetaillist = new physicalcountDetail();

                tphysicalcountDetaillist.setslot(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SLOT)));
                tphysicalcountDetaillist.setcountid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTID)));
                tphysicalcountDetaillist.setpage(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PAGE)));
                tphysicalcountDetaillist.setdoclineno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO)));
                tphysicalcountDetaillist.setloctid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOCTID)));
                tphysicalcountDetaillist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEM)));
                tphysicalcountDetaillist.setwlotno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WLOTNO)));
                tphysicalcountDetaillist.setumeasur(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_UMEASUR)));
                tphysicalcountDetaillist.settcountqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TCOUNTQTY)));
                tphysicalcountDetaillist.setwmsstat(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WMSSTAT)));
                tphysicalcountDetaillist.setposted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_POSTED)));
                tphysicalcountDetaillist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITMDESC)));
                tphysicalcountDetaillist.setpckdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PCKDESC)));
                tphysicalcountDetaillist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DECNUM)));
                tphysicalcountDetaillist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOTREFID)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEMSHOW)));
                tphysicalcountDetaillist.setsurprisadd(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SURPRISADD)));
                tphysicalcountDetaillist.setuserid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_USERID)));
                tphysicalcountDetaillist.setcounted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTED)));
                tphysicalcountDetaillist.setcollection(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COLLECTION)));
                tphysicalcountDetaillist.setwelement(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WELEMENT)));
                tphysicalcountDetaillist.setwidgetID(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WIDGETID)));
                //tphysicalcountDetaillist.setRowNo(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ROWNO)));
                tphysicalcountDetaillist.setRowNo(String.valueOf(Globals.gPCDetailRowCount));
                tphysicalcountDetaillist.setFlag(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_FLAG)));
                String doclineNum =mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO));
                physicalcountDetailList.add(tphysicalcountDetaillist);

                ContentValues cv = new ContentValues();
                cv.put(WMSDbHelper.PC_DETAIL_ROWNO, String.valueOf(Globals.gPCDetailRowCount));
                mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TABLE, cv, "doclineno = '" + doclineNum + "'", null);
                Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountDetailList;
    }

    public ArrayList<physicalcountDetail> getSeletedDetailList(String wlotno, String umeasur) {
        ArrayList<physicalcountDetail> physicalcountDetailList = new ArrayList<>();
        physicalcountDetail tphysicalcountDetaillist = null;
        if (umeasur.equals("")){
            mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                    " and userid = '" + Globals.gUsercode + "' and wlotno = '" + wlotno + "'", null);
        } else{
            mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                    " and userid = '" + Globals.gUsercode + "' and wlotno = '" + wlotno + "' and  umeasur = '" + umeasur + "'", null);
        }

        try {
            while (mCursor.moveToNext()) {
                tphysicalcountDetaillist = new physicalcountDetail();

                tphysicalcountDetaillist.setslot(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SLOT)));
                tphysicalcountDetaillist.setcountid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTID)));
                tphysicalcountDetaillist.setpage(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PAGE)));
                tphysicalcountDetaillist.setdoclineno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO)));
                tphysicalcountDetaillist.setloctid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOCTID)));
                tphysicalcountDetaillist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEM)));
                tphysicalcountDetaillist.setwlotno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WLOTNO)));
                tphysicalcountDetaillist.setumeasur(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_UMEASUR)));
                tphysicalcountDetaillist.settcountqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TCOUNTQTY)));
                tphysicalcountDetaillist.setwmsstat(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WMSSTAT)));
                tphysicalcountDetaillist.setposted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_POSTED)));
                tphysicalcountDetaillist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITMDESC)));
                tphysicalcountDetaillist.setpckdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PCKDESC)));
                tphysicalcountDetaillist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DECNUM)));
                tphysicalcountDetaillist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOTREFID)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEMSHOW)));
                tphysicalcountDetaillist.setsurprisadd(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SURPRISADD)));
                tphysicalcountDetaillist.setuserid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_USERID)));
                tphysicalcountDetaillist.setcounted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTED)));
                tphysicalcountDetaillist.setcollection(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COLLECTION)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setwelement(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WELEMENT)));
                tphysicalcountDetaillist.setwidgetID(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WIDGETID)));
                tphysicalcountDetaillist.setFlag(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_FLAG)));
                tphysicalcountDetaillist.setRowNo(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ROWNO)));

                physicalcountDetailList.add(tphysicalcountDetaillist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountDetailList;
    }

    public ArrayList<physicalcountDetail> getSeletedWlotnoCommonList() {
        ArrayList<physicalcountDetail> physicalcountDetailList = new ArrayList<>();
        physicalcountDetail tphysicalcountDetaillist = null;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "'" +
                " and userid = '" + Globals.gUsercode + "' Limit 1", null);
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountDetaillist = new physicalcountDetail();

                tphysicalcountDetaillist.setslot(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SLOT)));
                tphysicalcountDetaillist.setcountid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTID)));
                tphysicalcountDetaillist.setpage(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PAGE)));
                tphysicalcountDetaillist.setdoclineno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO)));
                tphysicalcountDetaillist.setloctid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOCTID)));
                tphysicalcountDetaillist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEM)));
                tphysicalcountDetaillist.setwlotno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WLOTNO)));
                tphysicalcountDetaillist.setumeasur(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_UMEASUR)));
                tphysicalcountDetaillist.settcountqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TCOUNTQTY)));
                tphysicalcountDetaillist.setwmsstat(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WMSSTAT)));
                tphysicalcountDetaillist.setposted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_POSTED)));
                tphysicalcountDetaillist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITMDESC)));
                tphysicalcountDetaillist.setpckdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PCKDESC)));
                tphysicalcountDetaillist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DECNUM)));
                tphysicalcountDetaillist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOTREFID)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEMSHOW)));
                tphysicalcountDetaillist.setsurprisadd(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SURPRISADD)));
                tphysicalcountDetaillist.setuserid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_USERID)));
                tphysicalcountDetaillist.setcounted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTED)));
                tphysicalcountDetaillist.setcollection(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COLLECTION)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setwelement(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WELEMENT)));
                tphysicalcountDetaillist.setwidgetID(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WIDGETID)));
                tphysicalcountDetaillist.setFlag(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_FLAG)));
                tphysicalcountDetaillist.setRowNo(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ROWNO)));

                physicalcountDetailList.add(tphysicalcountDetaillist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountDetailList;
    }

    public ArrayList<physicalcountICITEM> getSeletedItemList(String item) {
        ArrayList<physicalcountICITEM> physicalcountICITEMList = new ArrayList<>();
        physicalcountICITEM tphysicalcountICITEMlist = null;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountICITEM where item = '" + item + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountICITEMlist = new physicalcountICITEM();

                tphysicalcountICITEMlist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_ICITEM_ITEM)));
                tphysicalcountICITEMlist.setinvtype(mCursor.getString(mCursor.getColumnIndex(PC_ICITEM_INVTYPE)));
                tphysicalcountICITEMlist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_ICITEM_ITMDESC)));
                tphysicalcountICITEMlist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_ICITEM_DECNUM)));
                tphysicalcountICITEMlist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_ICITEM_ITEMSHOW)));

                physicalcountICITEMList.add(tphysicalcountICITEMlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountICITEMList;
    }

    public ArrayList<physicalcountWHMLOT> getSelectedItem(String wlotno) {
        ArrayList<physicalcountWHMLOT> physicalcountWHMLOTList = new ArrayList<>();
        physicalcountWHMLOT tphysicalcountWHMLOTlist = null;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountWHMLOT where lotrefid = '" + wlotno + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountWHMLOTlist = new physicalcountWHMLOT();

                tphysicalcountWHMLOTlist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_WHMLOT_ITEM)));
                tphysicalcountWHMLOTlist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_WHMLOT_LOTREFID)));

                physicalcountWHMLOTList.add(tphysicalcountWHMLOTlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count WHMLOT selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountWHMLOTList;
    }

    public ArrayList<physicalcountDetail> getPCDetailList() {
        ArrayList<physicalcountDetail> physicalcountDetailList = new ArrayList<>();
        physicalcountDetail tphysicalcountDetaillist = null;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where slot = '" + Globals.gPCSlot + "' and Flag='Y'" +
                " and userid = '" + Globals.gUsercode + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tphysicalcountDetaillist = new physicalcountDetail();

                tphysicalcountDetaillist.setslot(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SLOT)));
                tphysicalcountDetaillist.setcountid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTID)));
                tphysicalcountDetaillist.setpage(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PAGE)));
                tphysicalcountDetaillist.setdoclineno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DOCLINENO)));
                tphysicalcountDetaillist.setloctid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOCTID)));
                tphysicalcountDetaillist.setitem(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEM)));
                tphysicalcountDetaillist.setwlotno(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WLOTNO)));
                tphysicalcountDetaillist.setumeasur(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_UMEASUR)));
                tphysicalcountDetaillist.settcountqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TCOUNTQTY)));
                tphysicalcountDetaillist.setwmsstat(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WMSSTAT)));
                tphysicalcountDetaillist.setposted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_POSTED)));
                tphysicalcountDetaillist.setitmdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITMDESC)));
                tphysicalcountDetaillist.setpckdesc(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_PCKDESC)));
                tphysicalcountDetaillist.setdecnum(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_DECNUM)));
                tphysicalcountDetaillist.setlotrefid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_LOTREFID)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setitemShow(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_ITEMSHOW)));
                tphysicalcountDetaillist.setinvtype(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_INVTYPE)));
                tphysicalcountDetaillist.setsurprisadd(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_SURPRISADD)));
                tphysicalcountDetaillist.setuserid(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_USERID)));
                tphysicalcountDetaillist.setcounted(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COUNTED)));
                tphysicalcountDetaillist.setcollection(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_COLLECTION)));
                tphysicalcountDetaillist.settqty(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_TQTY)));
                tphysicalcountDetaillist.setwelement(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WELEMENT)));
                tphysicalcountDetaillist.setwidgetID(mCursor.getString(mCursor.getColumnIndex(PC_DETAIL_WIDGETID)));

                physicalcountDetailList.add(tphysicalcountDetaillist);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Physical count selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return physicalcountDetailList;
    }

    public ArrayList<picktaskPrintlabel> getPickTaskPrintLabel() {
        ArrayList<picktaskPrintlabel> picktaskPrintlabel = new ArrayList<>();
        picktaskPrintlabel tpicktaskPrintlabel = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskPrintlabel", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskPrintlabel = new picktaskPrintlabel();

                tpicktaskPrintlabel.setStop(Globals.gStop);
                tpicktaskPrintlabel.setTrailer(mCursor.getString(mCursor.getColumnIndex(PT_TRAILER)));
                tpicktaskPrintlabel.setRoute(Globals.gRoute);
                tpicktaskPrintlabel.setDock(mCursor.getString(mCursor.getColumnIndex(PT_DOCK)));
                tpicktaskPrintlabel.setDeldate(mCursor.getString(mCursor.getColumnIndex(PT_DELDATE)));
                tpicktaskPrintlabel.setOrderno(mCursor.getString(mCursor.getColumnIndex(PT_ORDER)));
                tpicktaskPrintlabel.setTaskno(Globals.gTaskNo);
                tpicktaskPrintlabel.setCustid(mCursor.getString(mCursor.getColumnIndex(PT_CUSTID)));
                tpicktaskPrintlabel.setCustname(mCursor.getString(mCursor.getColumnIndex(PT_CUSTNAME)));
                tpicktaskPrintlabel.setPicker(Globals.gUsercode);
                tpicktaskPrintlabel.setPalno(mCursor.getString(mCursor.getColumnIndex(PT_PALNO)));

                picktaskPrintlabel.add(tpicktaskPrintlabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return picktaskPrintlabel;
    }



    public ArrayList<PrintPalletTag> getPrintPalletTag() {
        ArrayList<PrintPalletTag> mPrintPalletTag = new ArrayList<>();
        PrintPalletTag tPrintPalletTag = null;

        mCursor = mSqlitedb.rawQuery("select * from PrintPalletTagDetails", null);
        try {
            while (mCursor.moveToNext()) {
                tPrintPalletTag = new PrintPalletTag();

                tPrintPalletTag.setItem(mCursor.getString(mCursor.getColumnIndex(PPD_ITEM)));
                tPrintPalletTag.setLotId(mCursor.getString(mCursor.getColumnIndex(PPD_LOTNO)));
                tPrintPalletTag.setPalletNo(mCursor.getString(mCursor.getColumnIndex(PPD_PALNO)));
                tPrintPalletTag.setQty(mCursor.getString(mCursor.getColumnIndex(PPD_QTY)));
                tPrintPalletTag.setCountryid(mCursor.getString(mCursor.getColumnIndex(PPD_COUNTRYID)));
                tPrintPalletTag.setOutpalno(mCursor.getString(mCursor.getColumnIndex(PPD_OUTPALNO)));
                tPrintPalletTag.setPltcrea(mCursor.getString(mCursor.getColumnIndex(PPD_PLTCREA)));
                tPrintPalletTag.setSetid(mCursor.getString(mCursor.getColumnIndex(PPD_SETID)));
                tPrintPalletTag.setGrade(mCursor.getString(mCursor.getColumnIndex(PPD_GRADE)));
                tPrintPalletTag.setItemDesc(mCursor.getString(mCursor.getColumnIndex(PPD_ITEMDESC)));
                tPrintPalletTag.setCommod(mCursor.getString(mCursor.getColumnIndex(PPD_COMMOD)));
                tPrintPalletTag.setLabel(mCursor.getString(mCursor.getColumnIndex(PPD_LABEL)));
                tPrintPalletTag.setPackage(mCursor.getString(mCursor.getColumnIndex(PPD_PACKAGE)));
                tPrintPalletTag.setSizesort(mCursor.getString(mCursor.getColumnIndex(PPD_SIZESORT)));
                tPrintPalletTag.setVariety(mCursor.getString(mCursor.getColumnIndex(PPD_VARIETY)));
                tPrintPalletTag.setSize(mCursor.getString(mCursor.getColumnIndex(PPD_SIZE)));



                mPrintPalletTag.add(tPrintPalletTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return mPrintPalletTag;
    }


    public ArrayList<receivetaskprintdetail> getReceiveTaskPrint() {
        ArrayList<receivetaskprintdetail> receivetaskprintlabel = new ArrayList<>();
        receivetaskprintdetail treceivetaskprintdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskprintdetail where taskno = '" + Globals.gRTTaskNo + "' and " +
                "tasklineno='" + Globals.gRTTranline + "'", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskprintdetail = new receivetaskprintdetail();

                treceivetaskprintdetail.setwlotno(mCursor.getString(mCursor.getColumnIndex(RTP_WLOTNO)));
                treceivetaskprintdetail.setlotrefid(mCursor.getString(mCursor.getColumnIndex(RTP_LOTREFID)));
                treceivetaskprintdetail.setitem(mCursor.getString(mCursor.getColumnIndex(RTP_ITEM)));
                treceivetaskprintdetail.setrecdate(mCursor.getString(mCursor.getColumnIndex(RTP_RECDATE)));
                treceivetaskprintdetail.setexpdate(mCursor.getString(mCursor.getColumnIndex(RTP_EXPDATE)));
                treceivetaskprintdetail.setrecuser(mCursor.getString(mCursor.getColumnIndex(RTP_RECUSER)));
                treceivetaskprintdetail.settaskno(mCursor.getString(mCursor.getColumnIndex(RTP_TASKNO)));
                treceivetaskprintdetail.settasklineno(mCursor.getString(mCursor.getColumnIndex(RTP_TASKLINENO)));
                treceivetaskprintdetail.setpltlineno(mCursor.getString(mCursor.getColumnIndex(RTP_PLTLINENO)));
                treceivetaskprintdetail.setprtplttag(mCursor.getString(mCursor.getColumnIndex(RTP_PRTPLTTAG)));
                treceivetaskprintdetail.settqty(mCursor.getString(mCursor.getColumnIndex(RTP_TQTY)));
                treceivetaskprintdetail.settrkqty(mCursor.getString(mCursor.getColumnIndex(RTP_TRKQTY)));
                treceivetaskprintdetail.setitmdesc(mCursor.getString(mCursor.getColumnIndex(RTP_ITMDESC)));
                treceivetaskprintdetail.setcatchwt(mCursor.getString(mCursor.getColumnIndex(RTP_CATCHWT)));

                receivetaskprintlabel.add(treceivetaskprintdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receivetaskprintlabel;
    }
    public ArrayList<receivetaskprintdetail> getReceiveTaskPrintAllTag() {
        ArrayList<receivetaskprintdetail> receivetaskprintlabel = new ArrayList<>();
        receivetaskprintdetail treceivetaskprintdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskprintdetail where taskno = '" + Globals.gRTTaskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskprintdetail = new receivetaskprintdetail();

                treceivetaskprintdetail.setwlotno(mCursor.getString(mCursor.getColumnIndex(RTP_WLOTNO)));
                treceivetaskprintdetail.setlotrefid(mCursor.getString(mCursor.getColumnIndex(RTP_LOTREFID)));
                treceivetaskprintdetail.setitem(mCursor.getString(mCursor.getColumnIndex(RTP_ITEM)));
                treceivetaskprintdetail.setrecdate(mCursor.getString(mCursor.getColumnIndex(RTP_RECDATE)));
                treceivetaskprintdetail.setexpdate(mCursor.getString(mCursor.getColumnIndex(RTP_EXPDATE)));
                treceivetaskprintdetail.setrecuser(mCursor.getString(mCursor.getColumnIndex(RTP_RECUSER)));
                treceivetaskprintdetail.settaskno(mCursor.getString(mCursor.getColumnIndex(RTP_TASKNO)));
                treceivetaskprintdetail.settasklineno(mCursor.getString(mCursor.getColumnIndex(RTP_TASKLINENO)));
                treceivetaskprintdetail.setpltlineno(mCursor.getString(mCursor.getColumnIndex(RTP_PLTLINENO)));
                treceivetaskprintdetail.setprtplttag(mCursor.getString(mCursor.getColumnIndex(RTP_PRTPLTTAG)));
                treceivetaskprintdetail.settqty(mCursor.getString(mCursor.getColumnIndex(RTP_TQTY)));
                treceivetaskprintdetail.settrkqty(mCursor.getString(mCursor.getColumnIndex(RTP_TRKQTY)));
                treceivetaskprintdetail.setitmdesc(mCursor.getString(mCursor.getColumnIndex(RTP_ITMDESC)));
                treceivetaskprintdetail.setcatchwt(mCursor.getString(mCursor.getColumnIndex(RTP_CATCHWT)));

                receivetaskprintlabel.add(treceivetaskprintdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "PO number selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receivetaskprintlabel;
    }

    public ArrayList<picktaskheader> getPickTaskHeader() {
        ArrayList<picktaskheader> picktaskheader = new ArrayList<>();
        picktaskheader tpicktaskheader = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskheader where taskNum = '" + Globals.gTaskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskheader = new picktaskheader();
                tpicktaskheader.setCasecount(mCursor.getString(mCursor.getColumnIndex(CASE_COUNT)));
                tpicktaskheader.setRoute(mCursor.getString(mCursor.getColumnIndex(PICK_ROUTE)));
                tpicktaskheader.setStop(mCursor.getString(mCursor.getColumnIndex(PICK_STOP)));
                tpicktaskheader.setWeight(mCursor.getString(mCursor.getColumnIndex(PICK_WEIGHT)));

                picktaskheader.add(tpicktaskheader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Pick Task Header selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return picktaskheader;
    }

    public ArrayList<loadpickpalletDetails> getCompletedLPPDetail() {
        ArrayList<loadpickpalletDetails> loadpickpalletDetails = new ArrayList<>();
        loadpickpalletDetails tloadpickpalletDetails = null;

        mCursor = mSqlitedb.rawQuery("select rowNo from loadpickpalletDetails where Loaded = '1'", null);
        try {
            while (mCursor.moveToNext()) {
                tloadpickpalletDetails = new loadpickpalletDetails();
                tloadpickpalletDetails.setrowNo(mCursor.getString(0));

                loadpickpalletDetails.add(tloadpickpalletDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Load Pick Pallet Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return loadpickpalletDetails;
    }

    public ArrayList<loadpickpalletSummary> getCompletedLPPSummary(String strFlag) {
        ArrayList<loadpickpalletSummary> loadpickpalletSummary = new ArrayList<>();
        loadpickpalletSummary tloadpickpalletSummary = null;

        mCursor = mSqlitedb.rawQuery("select rowNo from loadpickpalletSummary where Flag = '" + strFlag + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tloadpickpalletSummary = new loadpickpalletSummary();
                tloadpickpalletSummary.setrowNo(mCursor.getString(0));

                loadpickpalletSummary.add(tloadpickpalletSummary);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Load Pick Pallet Summary insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return loadpickpalletSummary;
    }

    public ArrayList<picktaskdetail> getCompletedTrans(String strFlag,String taskNum) {
        ArrayList<picktaskdetail> tpicktaskdetail = new ArrayList<>();
        picktaskdetail picktaskdetail = null;


       mCursor = mSqlitedb.rawQuery("select rowNo, Tranlineno, Uom, Item, WLotNo, decnum,chgQty from picktaskdetail where Flag = 'Y'  and "  + " taskNUM = '" + Globals.gTaskNo + "'", null);
       // mCursor = mSqlitedb.rawQuery("select rowNo, Tranlineno, Uom, Item, WLotNo, decnum from picktaskdetail where slot >''", null);

        try {
            while (mCursor.moveToNext()) {
                picktaskdetail = new picktaskdetail();
                picktaskdetail.setrowNo(mCursor.getString(0));

                picktaskdetail.setTranlineno(mCursor.getString(1));
                picktaskdetail.setUom(mCursor.getString(2));
                picktaskdetail.setItem(mCursor.getString(3));
                picktaskdetail.setWLotNo(mCursor.getString(4));
                picktaskdetail.setdecnum(mCursor.getString(5));
                picktaskdetail.setChgQty(mCursor.getString(6));

                tpicktaskdetail.add(picktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return tpicktaskdetail;
    }

    public ArrayList<picktaskdetail> getCompletedStagingTrans(String strFlag) {
        ArrayList<picktaskdetail> tpicktaskdetail = new ArrayList<>();
        picktaskdetail picktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select rowNo, Tranlineno, Uom, Item, WLotNo, decnum from picktaskdetail where Flag = '" + strFlag + "'" + " and stagingSlot is not null ", null);


        try {
            while (mCursor.moveToNext()) {
                picktaskdetail = new picktaskdetail();
                picktaskdetail.setrowNo(mCursor.getString(0));
                picktaskdetail.setTranlineno(mCursor.getString(1));
                picktaskdetail.setUom(mCursor.getString(2));
                picktaskdetail.setItem(mCursor.getString(3));
                picktaskdetail.setWLotNo(mCursor.getString(4));
                picktaskdetail.setdecnum(mCursor.getString(5));

                tpicktaskdetail.add(picktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return tpicktaskdetail;
    }

    public ArrayList<receivetaskdetail> getCompletedRTTrans(String strFlag) {
        ArrayList<receivetaskdetail> receivetaskdetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select rowNo, tqtyinc, tqtyrec, trkqtyrec, catchwt, tranlineno, palno  from receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "' and cast (tqtyrec  as INTEGER) > 0 ", null);
        mCursor = mSqlitedb.rawQuery("select rowNo, tqtyinc, tqtyrec, trkqtyrec, catchwt, tranlineno, palno  from receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "' and cast (tqtyrec  as INTEGER) > 0 ", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskdetail = new receivetaskdetail();
                treceivetaskdetail.setrowNo(mCursor.getString(0));
                treceivetaskdetail.settqtyinc(mCursor.getString(1));
                treceivetaskdetail.settqtyrec(mCursor.getString(2));
                treceivetaskdetail.settrkqtyrec(mCursor.getString(3));
                treceivetaskdetail.setcatchwt(mCursor.getString(4));
                treceivetaskdetail.settranlineno(mCursor.getString(5));
                treceivetaskdetail.setPalno(mCursor.getString(6));

                receivetaskdetail.add(treceivetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Receive Task Detail selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return receivetaskdetail;
    }

    public ArrayList<movetaskdetail> getCompletedMTTrans(String strFlag) {
        ArrayList<movetaskdetail> movetaskdetails = new ArrayList<>();
        movetaskdetail tmovetaskdetail = null;


        mCursor = mSqlitedb.rawQuery("select tranlineno, palno, tqtyrq, tqtyact, toSlot,flag from movetaskdetail where taskNo = '" + Globals.gMTTaskNo + "' and  cast (tqtyrq  as INTEGER) > 0 ", null);
        //mCursor = mSqlitedb.rawQuery("select tranlineno, palno, tqtyrq, tqtyact, toSlot  from movetaskdetail where taskno = '" + Globals.gMTTaskNo + "' and cast (tqtyrec  as INTEGER) > 0 ", null);
        try {
            while (mCursor.moveToNext()) {
                tmovetaskdetail = new movetaskdetail();
                tmovetaskdetail.setTranlineno(mCursor.getString(0));
                tmovetaskdetail.setPalno(mCursor.getString(1));
                tmovetaskdetail.setTqtyrq(mCursor.getString(2));
                tmovetaskdetail.setTqtyact(mCursor.getString(3));
                tmovetaskdetail.setToSlot(mCursor.getString(4));
                tmovetaskdetail.setFlag(mCursor.getString(5));

                movetaskdetails.add(tmovetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Receive Task Detail selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return movetaskdetails;
    }

    public ArrayList<receivetaskdetail> getReceiveTaskDetail(String taskNum) {
        ArrayList<receivetaskdetail> receivetaskdetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select rowNo, tqtyinc, tqtyrec, trkqtyrec, catchwt  from receivetaskdetail where taskno = '" + taskNum + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskdetail = new receivetaskdetail();
                treceivetaskdetail.setrowNo(mCursor.getString(0));
                treceivetaskdetail.settqtyinc(mCursor.getString(1));
                treceivetaskdetail.settqtyrec(mCursor.getString(2));
                treceivetaskdetail.settrkqtyrec(mCursor.getString(3));
                treceivetaskdetail.setcatchwt(mCursor.getString(4));

                receivetaskdetail.add(treceivetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Receive Task Detail selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return receivetaskdetail;
    }

    public ArrayList<physicalcountSlot> getCompletedPCSLOTTrans() {
        ArrayList<physicalcountSlot> tphysicalcountSlot = new ArrayList<>();
        physicalcountSlot physicalcountSlot = null;

        mCursor = mSqlitedb.rawQuery("select rowno, slot, status from physicalcountSlot", null);
        try {
            while (mCursor.moveToNext()) {
                physicalcountSlot = new physicalcountSlot();
                physicalcountSlot.setrowno(mCursor.getString(0));
                physicalcountSlot.setslot(mCursor.getString(1));
                physicalcountSlot.setstatus(mCursor.getString(2));

                tphysicalcountSlot.add(physicalcountSlot);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return tphysicalcountSlot;
    }
    public ArrayList<physicalcountDetail> getCompletedPCTrans(String strFlag) {
        ArrayList<physicalcountDetail> tphysicalcountDetail = new ArrayList<>();
        physicalcountDetail physicalcountDetail = null;

        mCursor = mSqlitedb.rawQuery("select RowNo, doclineno, countid, item, wlotno, decnum, posted from physicalcountDetail where posted = 'P' or wmsstat = 'C' and " +
                "slot = '" + Globals.gPCSlot + "'", null);
        try {
            while (mCursor.moveToNext()) {
                physicalcountDetail = new physicalcountDetail();
                physicalcountDetail.setRowNo(mCursor.getString(0));
                physicalcountDetail.setdoclineno(mCursor.getString(1));
                physicalcountDetail.setcountid(mCursor.getString(2));
                physicalcountDetail.setitem(mCursor.getString(3));
                physicalcountDetail.setwlotno(mCursor.getString(4));
                physicalcountDetail.setdecnum(mCursor.getString(5));
                physicalcountDetail.setposted(mCursor.getString(6));

                tphysicalcountDetail.add(physicalcountDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
        return tphysicalcountDetail;
    }

    public void SplitNewLine(picktaskdetail tpicktaskdetail, int Tranlineno, int Doclineno, int rowNo, Double Qty) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + PICK_TASK_DETAIL);
            ContentValues cv = new ContentValues();

            cv.put(DETAIL_SLOT, tpicktaskdetail.getSlot());
            if (Globals.gCatchwt == "T"){
                cv.put(DETAIL_TQTY, String.valueOf(Qty));
                cv.put(DETAIL_TRKQTY, tpicktaskdetail.getTrkQty());
            }else{
                cv.put(DETAIL_TQTY, tpicktaskdetail.getTQty());
                cv.put(DETAIL_TRKQTY, String.valueOf(Qty));
            }
            //cv.put(DETAIL_ORGTQTY, tpicktaskdetail.getorgTQty());
            cv.put(DETAIL_ORGTQTY, String.valueOf(Qty));
            cv.put(DETAIL_ORGTRKQTY, tpicktaskdetail.getorgTrkQty());
            cv.put(DETAIL_UOM, tpicktaskdetail.getUom());
            cv.put(DETAIL_ITEM, tpicktaskdetail.getItem());
            cv.put(DETAIL_DESC, tpicktaskdetail.getDescrip());
            /*cv.put(DETAIL_WLOTNO, tpicktaskdetail.getWLotNo());
            cv.put(DETAIL_LOTNO, tpicktaskdetail.getLotNo());*/
            cv.put(DETAIL_TRANLINENO, String.valueOf(Tranlineno));
            cv.put(DETAIL_ORGTRANLINENO, tpicktaskdetail.getorgTranlineno());
            cv.put(DETAIL_DOCTYPE, tpicktaskdetail.getDoctype());
            cv.put(DETAIL_DOCNO, tpicktaskdetail.getDocno());
            cv.put(DETAIL_DOCLINENO, String.valueOf(Doclineno));
            cv.put(DETAIL_ORGDOCLINENO, tpicktaskdetail.getorgDoclineno());
            cv.put(DETAIL_DOCSTAT, tpicktaskdetail.getDocstat());
            cv.put(DETAIL_WEIGHT, tpicktaskdetail.getWeight());
            cv.put(DETAIL_STKUMID, tpicktaskdetail.getStkumid());
            cv.put(DETAIL_CATCHWT, tpicktaskdetail.getCatchwt());
            cv.put(DETAIL_VOLUME, tpicktaskdetail.getVolume());
            cv.put(DETAIL_DECNUM, tpicktaskdetail.getdecnum());
            cv.put(DETAIL_UMFACT, tpicktaskdetail.getUmfact());
            cv.put(DETAIL_TSHIPPED, tpicktaskdetail.getTshipped());
            cv.put(DETAIL_TRKSHIPPED, tpicktaskdetail.getTrkshipped());
            cv.put(DETAIL_LBSHP, tpicktaskdetail.getLbshp());
            cv.put(DETAIL_PICKDURATION, tpicktaskdetail.getpickDuration());
            cv.put(DETAIL_LINESPLIT, Globals.gLineSplit);
            cv.put(DETAIL_ROWNO, rowNo);
            cv.put(DETAIL_FLAG, "N");
            cv.put(DETAIL_SUBSTITUTED_ITEM,tpicktaskdetail.getSubItem());
            cv.put(DETAIL_SUBSTITUTED_TRANNO,tpicktaskdetail.getSubTranNo());
            cv.put(DETAIL_IS_SUBSTITUTED,tpicktaskdetail.getIsSubItem());
            cv.put(DETAIL_ORG_SOITEM,tpicktaskdetail.getOrgSOItem());
            cv.put(DETAIL_TASKNO,tpicktaskdetail.getDetailsTaskNum());
            cv.put(DETAIL_CHGQTY,"");
            cv.put(DETAIL_OTQTYPICKED,"0.000000");
            cv.put(DETAIL_PICKED,"N");

            //cv.put(DETAIL_STAGINGSLOT,tpicktaskdetail.getStagingSlot());

            mSqlitedb.insert(PICK_TASK_DETAIL, null, cv);
            cv.put(DETAIL_TEMPQTY,"");

            mSqlitedb.insert(PICK_TASK_REVEROLDDATA, null, cv);
            LogfileCreator.mAppendLog("Pick Task Detail: Task Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Task Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public ArrayList<loadpickpalletDetails> getloadpickpalletDetail() {
        ArrayList<loadpickpalletDetails> loadpickpalletDetails = new ArrayList<>();
        loadpickpalletDetails tloadpickpalletDetails = null;

        mCursor = mSqlitedb.rawQuery("select * from loadpickpalletDetails c WHERE " +
                "c.Route = (select a.Route from loadpickpalletDetails a " +
                "INNER JOIN loadpickpalletRouteDetails b on a.Route = b.Route " +
                "INNER JOIN loadpickpalletWHIPLT d on d.Taskno = a.Taskno " +
                "WHERE a.wmsDate = '" + Globals.gLPPWorkDate + "' and d.Palno = '" + Globals.gLPPPallet + "')", null);
        try {
            while (mCursor.moveToNext()) {
                tloadpickpalletDetails = new loadpickpalletDetails();

                tloadpickpalletDetails.setStop(mCursor.getString(mCursor.getColumnIndex(LPP_DETAIL_STOP)));
                tloadpickpalletDetails.setLoaded(mCursor.getString(mCursor.getColumnIndex(LPP_DETAIL_LOADED)));
                tloadpickpalletDetails.setReady(mCursor.getString(mCursor.getColumnIndex(LPP_DETAIL_READY)));
                tloadpickpalletDetails.setTotal(mCursor.getString(mCursor.getColumnIndex(LPP_DETAIL_TOTAL)));
                tloadpickpalletDetails.setPicker(mCursor.getString(mCursor.getColumnIndex(LPP_DETAIL_PICKER)));
                tloadpickpalletDetails.setTaskno(mCursor.getString(mCursor.getColumnIndex(LPP_DETAIL_TASKNO)));

                loadpickpalletDetails.add(tloadpickpalletDetails);
                LogfileCreator.mAppendLog("Load Pick Pallet Detail:Load Pick Pallet Detail Loaded");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Load Pick Pallet Detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return loadpickpalletDetails;
    }

    public ArrayList<loadpickpalletSummary> getloadpickpalletSummary() {
        ArrayList<loadpickpalletSummary> loadpickpalletSummary = new ArrayList<>();
        loadpickpalletSummary tloadpickpalletSummary = null;

        mCursor = mSqlitedb.rawQuery("select * from loadpickpalletSummary", null);
        try {
            while (mCursor.moveToNext()) {
                tloadpickpalletSummary = new loadpickpalletSummary();

                tloadpickpalletSummary.setwmsDate(mCursor.getString(mCursor.getColumnIndex(LPP_SUMMARY_WMSDate)));
                tloadpickpalletSummary.setTruck(mCursor.getString(mCursor.getColumnIndex(LPP_SUMMARY_TRUCK)));
                tloadpickpalletSummary.setDock(mCursor.getString(mCursor.getColumnIndex(LPP_SUMMARY_DOCK)));
                tloadpickpalletSummary.setRoutecnt(mCursor.getString(mCursor.getColumnIndex(LPP_SUMMARY_ROUTECNT)));
                tloadpickpalletSummary.setStopcnt(mCursor.getString(mCursor.getColumnIndex(LPP_SUMMARY_STOPCNT)));
                tloadpickpalletSummary.setPalcnt(mCursor.getString(mCursor.getColumnIndex(LPP_SUMMARY_PALCNT)));

                loadpickpalletSummary.add(tloadpickpalletSummary);
                LogfileCreator.mAppendLog("Load Pick Pallet Summary:Load Pick Pallet Summary Loaded");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Load Pick Pallet Summary save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return loadpickpalletSummary;
    }

    public ArrayList<loadpickpalletWHIPLT> getLPPDetails() {
        ArrayList<loadpickpalletWHIPLT> loadpickpalletWHIPLT = new ArrayList<>();
        loadpickpalletWHIPLT tloadpickpalletWHIPLT = null;

        mCursor = mSqlitedb.rawQuery("select Taskno, Palno, stgslot from loadpickpalletWHIPLT Where Palno = '"+ Globals.gLPPPallet +"'", null);
        try {
            while (mCursor.moveToNext()) {
                tloadpickpalletWHIPLT = new loadpickpalletWHIPLT();

                tloadpickpalletWHIPLT.setTaskno(mCursor.getString(mCursor.getColumnIndex(LPP_WHIPLT_TASKNO)));
                tloadpickpalletWHIPLT.setPalno(mCursor.getString(mCursor.getColumnIndex(LPP_WHIPLT_PALNO)));
                tloadpickpalletWHIPLT.setstgslot(mCursor.getString(mCursor.getColumnIndex(LPP_WHIPLT_STGSLOT)));

                loadpickpalletWHIPLT.add(tloadpickpalletWHIPLT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Load Pick Pallet WHIPLT save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return loadpickpalletWHIPLT;
    }

/*  //01/02/23_CIRK_For Fresha to APN
    public ArrayList<RepackFG> getRepackFG() {
        ArrayList<RepackFG> repackFGArrayList = new ArrayList<>();
        RepackFG repackFG = null;

        mCursor = mSqlitedb.rawQuery("select * from repackFg ", null);
        try {
            while (mCursor.moveToNext()) {
                repackFG = new RepackFG();

                repackFG.setREPACKFG_PANO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PANO)));
                repackFG.setREPACKFG_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRANLINENO)));
                repackFG.setREPACKFG_ITEM(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ITEM)));
                repackFG.setREPACKFG_DESCRIP(mCursor.getString(mCursor.getColumnIndex(REPACKFG_DESCRIP)));
                repackFG.setREPACKFG_UMEASUR(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMEASUR)));
                repackFG.setREPACKFG_LOCTID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOCTID)));
                repackFG.setREPACKFG_LOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTNO)));
                repackFG.setREPACKFG_SERIAL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SERIAL)));
                repackFG.setREPACKFG_QTYMADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_QTYMADE)));
                repackFG.setREPACKFG_COST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COST)));
                repackFG.setREPACKFG_PADATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PADATE)));
                repackFG.setREPACKFG_PASTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PASTAT)));
                repackFG.setREPACKFG_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKSTAT)));
                repackFG.setREPACKFG_LCKUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKUSER)));
                repackFG.setREPACKFG_LCKDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKDATE)));
                repackFG.setREPACKFG_LCKTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKTIME)));
                repackFG.setREPACKFG_ADDUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDUSER)));
                repackFG.setREPACKFG_ADDDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDDATE)));
                repackFG.setREPACKFG_ADDTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDTIME)));
                repackFG.setREPACKFG_PRODLBL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PRODLBL)));
                repackFG.setREPACKFG_PACKCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PACKCHG)));
                repackFG.setREPACKFG_WASHCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WASHCHG)));
                repackFG.setREPACKFG_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COUNTRYID)));
                repackFG.setREPACKFG_VENDNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VENDNO)));
                repackFG.setREPACKFG_GRADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_GRADE)));
                repackFG.setREPACKFG_PROJNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PROJNO)));
                repackFG.setREPACKFG_REMARKS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_REMARKS)));
                repackFG.setREPACKFG_LCSTQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCSTQTY)));
                repackFG.setREPACKFG_CASE_PL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CASE_PL)));
                repackFG.setREPACKFG_PALNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALNO)));
                repackFG.setREPACKFG_SETID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SETID)));
                repackFG.setREPACKFG_WEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT)));
                repackFG.setREPACKFG_PALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALLET)));
                repackFG.setREPACKFG_ID_COL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ID_COL)));
                repackFG.setREPACKFG_BINNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BINNO)));
                repackFG.setREPACKFG_POSTPRG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_POSTPRG)));
                repackFG.setREPACKFG_EXTPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTPALLET)));
                repackFG.setREPACKFG_EXTCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTCUBE)));
                repackFG.setREPACKFG_EXTWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTWEIGHT)));
                repackFG.setREPACKFG_BEXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTLCST)));
                repackFG.setREPACKFG_EXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTLCST)));
                repackFG.setREPACKFG_BEXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTFEES)));
                repackFG.setREPACKFG_EXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTFEES)));
                repackFG.setREPACKFG_TPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TPALLET)));
                repackFG.setREPACKFG_TCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TCUBE)));
                repackFG.setREPACKFG_TWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TWEIGHT)));
                repackFG.setREPACKFG_WLOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WLOTNO)));
                repackFG.setREPACKFG_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANLN)));
                repackFG.setREPACKFG_ORIGTRANL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANL)));
                repackFG.setREPACKFG_ORIGDOCLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGDOCLN)));
                repackFG.setREPACKFG_STKUMID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_STKUMID)));
                repackFG.setREPACKFG_USELOTS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USELOTS)));
                repackFG.setREPACKFG_UMFACT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMFACT)));
                repackFG.setREPACKFG_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT1)));
                repackFG.setREPACKFG_VOLUME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VOLUME)));
                repackFG.setREPACKFG_CATCHWT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CATCHWT)));
                repackFG.setREPACKFG_LOTREFID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTREFID)));
                repackFG.setREPACKFG_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTEXPL)));
                repackFG.setREPACKFG_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LINESPLIT)));
                repackFG.setREPACKFG_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRKQTYPK)));
                repackFG.setREPACKFG_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UPDFLAG)));
                repackFG.setREPACKFG_VPLOCKED(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VPLOCKED)));

                repackFGArrayList.add(repackFG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackFGArrayList;
    }
*/

/*  //01/02/23_CIRK_For Fresha to APN
    public ArrayList<RepackIngredients> getWlotRepackIngredients(String WlotNum) {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where lotrefid = '" + WlotNum + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_PANO(mCursor.getString(mCursor.getColumnIndex(RIT_PANO)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_SERIAL(mCursor.getString(mCursor.getColumnIndex(RIT_SERIAL)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));
                repackIngredients.setRIT_COST(mCursor.getString(mCursor.getColumnIndex(RIT_COST)));
                repackIngredients.setRIT_PADATE(mCursor.getString(mCursor.getColumnIndex(RIT_PADATE)));
                repackIngredients.setRIT_PASTAT(mCursor.getString(mCursor.getColumnIndex(RIT_PASTAT)));
                repackIngredients.setRIT_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(RIT_LCKSTAT)));
                repackIngredients.setRIT_LCKUSER(mCursor.getString(mCursor.getColumnIndex(RIT_LCKUSER)));
                repackIngredients.setRIT_LCKDATE(mCursor.getString(mCursor.getColumnIndex(RIT_LCKDATE)));
                repackIngredients.setRIT_LCKTIME(mCursor.getString(mCursor.getColumnIndex(RIT_LCKTIME)));
                repackIngredients.setRIT_ADDUSER(mCursor.getString(mCursor.getColumnIndex(RIT_ADDUSER)));
                repackIngredients.setRIT_ADDDATE(mCursor.getString(mCursor.getColumnIndex(RIT_ADDDATE)));
                repackIngredients.setRIT_ADDTIME(mCursor.getString(mCursor.getColumnIndex(RIT_ADDTIME)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_VENDNO(mCursor.getString(mCursor.getColumnIndex(RIT_VENDNO)));
                repackIngredients.setRIT_BINNO(mCursor.getString(mCursor.getColumnIndex(RIT_BINNO)));
                repackIngredients.setRIT_PALNO(mCursor.getString(mCursor.getColumnIndex(RIT_PALNO)));
                repackIngredients.setRIT_REMARKS(mCursor.getString(mCursor.getColumnIndex(RIT_REMARKS)));
                repackIngredients.setRIT_YIELD(mCursor.getString(mCursor.getColumnIndex(RIT_YIELD)));
                repackIngredients.setRIT_SETID(mCursor.getString(mCursor.getColumnIndex(RIT_SETID)));
                repackIngredients.setRIT_WEIGHT(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT)));
                repackIngredients.setRIT_ID_COL(mCursor.getString(mCursor.getColumnIndex(RIT_ID_COL)));
                repackIngredients.setRIT_WLOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_WLOTNO)));
                repackIngredients.setRIT_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(RIT_ORIGTRANLN)));
                repackIngredients.setRIT_STKUMID(mCursor.getString(mCursor.getColumnIndex(RIT_STKUMID)));
                repackIngredients.setRIT_USELOTS(mCursor.getString(mCursor.getColumnIndex(RIT_USELOTS)));
                repackIngredients.setRIT_UMFACT(mCursor.getString(mCursor.getColumnIndex(RIT_UMFACT)));
                repackIngredients.setRIT_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT1)));
                repackIngredients.setRIT_VOLUME(mCursor.getString(mCursor.getColumnIndex(RIT_VOLUME)));
                repackIngredients.setRIT_CATCHWT(mCursor.getString(mCursor.getColumnIndex(RIT_CATCHWT)));
                repackIngredients.setRIT_LOTREFID(mCursor.getString(mCursor.getColumnIndex(RIT_LOTREFID)));
                repackIngredients.setRIT_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(RIT_LOTEXPL)));
                repackIngredients.setRIT_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(RIT_LINESPLIT)));
                repackIngredients.setRIT_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(RIT_TRKQTYPK)));
                repackIngredients.setRIT_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_UPDFLAG)));
                repackIngredients.setRIT_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_ADDFLAG)));
                repackIngredients.setRIT_SLOT(mCursor.getString(mCursor.getColumnIndex(RIT_SLOT)));
                repackIngredients.setRIT_ALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ALLOCQTY)));
                repackIngredients.setRIT_TEMPALLOC(mCursor.getString(mCursor.getColumnIndex(RIT_TEMPALLOC)));
                repackIngredients.setRIT_WHQTY(mCursor.getString(mCursor.getColumnIndex(RIT_WHQTY)));
                repackIngredients.setRIT_ICQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ICQTY)));
                repackIngredients.setRIT_RPALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_RPALLOCQTY)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }
*/



    public ArrayList<PickTaskScanPallet> getPicktaskScanPallet(String WlotNum) {
        ArrayList<PickTaskScanPallet> pickTaskScanPalletArrayList = new ArrayList<>();
        PickTaskScanPallet pickTaskScanPallet = null;

        mCursor = mSqlitedb.rawQuery("select * from PickTask_ScanPallet where lotrefid = '" + WlotNum + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                pickTaskScanPallet = new PickTaskScanPallet();

                pickTaskScanPallet.setPtsP_rpallocqty(mCursor.getString(mCursor.getColumnIndex(PTSP_RPALLOCQTY)));
                pickTaskScanPallet.setPtsP_whqty(mCursor.getString(mCursor.getColumnIndex(PTSP_WHQTY)));
                pickTaskScanPallet.setPtsP_icqty(mCursor.getString(mCursor.getColumnIndex(PTSP_ICQTY)));
                pickTaskScanPallet.setPtsP_tqty(mCursor.getString(mCursor.getColumnIndex(PTSP_TQTY)));
                pickTaskScanPallet.setPtsP_wlotno(mCursor.getString(mCursor.getColumnIndex(PTSP_WLOTNO)));
                pickTaskScanPallet.setPtsP_lotrefid(mCursor.getString(mCursor.getColumnIndex(PTSP_LOTREFID)));
                pickTaskScanPallet.setPtsP_item(mCursor.getString(mCursor.getColumnIndex(PTSP_ITEM)));
                pickTaskScanPallet.setPtsP_loctid(mCursor.getString(mCursor.getColumnIndex(PTSP_LOCTID)));
                pickTaskScanPallet.setPtsP_lotno(mCursor.getString(mCursor.getColumnIndex(PTSP_LOTNO)));
                pickTaskScanPallet.setPtsP_slot(mCursor.getString(mCursor.getColumnIndex(PTSP_SLOT)));
                pickTaskScanPallet.setPtsP_umeasur(mCursor.getString(mCursor.getColumnIndex(PTSP_UMEASUR)));
                pickTaskScanPallet.setPtsP_itmdesc(mCursor.getString(mCursor.getColumnIndex(PTSP_ITMDESC)));
                pickTaskScanPallet.setPtsP_weight(mCursor.getString(mCursor.getColumnIndex(PTSP_WEIGHT)));
                pickTaskScanPallet.setPtsP_countryid(mCursor.getString(mCursor.getColumnIndex(PTSP_COUNTRYID)));
                pickTaskScanPallet.setPtsP_serial(mCursor.getString(mCursor.getColumnIndex(PTSP_SERIAL)));
                pickTaskScanPallet.setPtsP_volume(mCursor.getString(mCursor.getColumnIndex(PTSP_VOLUME)));
                pickTaskScanPallet.setPtsP_catchwt(mCursor.getString(mCursor.getColumnIndex(PTSP_CATCHWT)));
                pickTaskScanPallet.setPtsP_stkumid(mCursor.getString(mCursor.getColumnIndex(PTSP_STKUMID)));
                pickTaskScanPallet.setPtsP_uselots(mCursor.getString(mCursor.getColumnIndex(PTSP_USELOTS)));
                pickTaskScanPallet.setPtsP_umfact(mCursor.getString(mCursor.getColumnIndex(PTSP_UMFACT)));
                pickTaskScanPallet.setPtsP_setid(mCursor.getString(mCursor.getColumnIndex(PTSP_SETID)));
                pickTaskScanPallet.setPtsP_vendno(mCursor.getString(mCursor.getColumnIndex(PTSP_VENDNO)));
                pickTaskScanPallet.setPtsP_cost(mCursor.getString(mCursor.getColumnIndex(PTSP_COST)));


                pickTaskScanPalletArrayList.add(pickTaskScanPallet);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickTaskScanPalletArrayList;
    }

    public boolean isEmptyItemAvailable(String item) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where Item = '" + item + "' and WlotNo = '' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }



    public boolean isRawItemAvailable(String item,String WlotNo) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where Item = '" + item + "' and WlotNo = '" + WlotNo + "' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }


    public boolean isTranlineNullAvailable() {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where tranlineNo IS NULL ", null);
//        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where tranlineNo = '' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }




    public boolean getUnscanedCount(String TaskNo) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select tqtyrec from receivetaskdetail where taskno = '" + TaskNo + "' and tqtyrec = '0.000000' ", null);

        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }


/*  //01/02/23_CIRK_For Fresha to APN
    public ArrayList<RepackIngredients> getRepackIngredientsForExport() {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where allocqty >'' and tranlineNo IS NOT NULL ", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_PANO(mCursor.getString(mCursor.getColumnIndex(RIT_PANO)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_SERIAL(mCursor.getString(mCursor.getColumnIndex(RIT_SERIAL)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));
                repackIngredients.setRIT_COST(mCursor.getString(mCursor.getColumnIndex(RIT_COST)));
                repackIngredients.setRIT_PADATE(mCursor.getString(mCursor.getColumnIndex(RIT_PADATE)));
                repackIngredients.setRIT_PASTAT(mCursor.getString(mCursor.getColumnIndex(RIT_PASTAT)));
                repackIngredients.setRIT_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(RIT_LCKSTAT)));
                repackIngredients.setRIT_LCKUSER(mCursor.getString(mCursor.getColumnIndex(RIT_LCKUSER)));
                repackIngredients.setRIT_LCKDATE(mCursor.getString(mCursor.getColumnIndex(RIT_LCKDATE)));
                repackIngredients.setRIT_LCKTIME(mCursor.getString(mCursor.getColumnIndex(RIT_LCKTIME)));
                repackIngredients.setRIT_ADDUSER(mCursor.getString(mCursor.getColumnIndex(RIT_ADDUSER)));
                repackIngredients.setRIT_ADDDATE(mCursor.getString(mCursor.getColumnIndex(RIT_ADDDATE)));
                repackIngredients.setRIT_ADDTIME(mCursor.getString(mCursor.getColumnIndex(RIT_ADDTIME)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_VENDNO(mCursor.getString(mCursor.getColumnIndex(RIT_VENDNO)));
                repackIngredients.setRIT_BINNO(mCursor.getString(mCursor.getColumnIndex(RIT_BINNO)));
                repackIngredients.setRIT_PALNO(mCursor.getString(mCursor.getColumnIndex(RIT_PALNO)));
                repackIngredients.setRIT_REMARKS(mCursor.getString(mCursor.getColumnIndex(RIT_REMARKS)));
                repackIngredients.setRIT_YIELD(mCursor.getString(mCursor.getColumnIndex(RIT_YIELD)));
                repackIngredients.setRIT_SETID(mCursor.getString(mCursor.getColumnIndex(RIT_SETID)));
                repackIngredients.setRIT_WEIGHT(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT)));
                repackIngredients.setRIT_ID_COL(mCursor.getString(mCursor.getColumnIndex(RIT_ID_COL)));
                repackIngredients.setRIT_WLOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_WLOTNO)));
                repackIngredients.setRIT_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(RIT_ORIGTRANLN)));
                repackIngredients.setRIT_STKUMID(mCursor.getString(mCursor.getColumnIndex(RIT_STKUMID)));
                repackIngredients.setRIT_USELOTS(mCursor.getString(mCursor.getColumnIndex(RIT_USELOTS)));
                repackIngredients.setRIT_UMFACT(mCursor.getString(mCursor.getColumnIndex(RIT_UMFACT)));
                repackIngredients.setRIT_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT1)));
                repackIngredients.setRIT_VOLUME(mCursor.getString(mCursor.getColumnIndex(RIT_VOLUME)));
                repackIngredients.setRIT_CATCHWT(mCursor.getString(mCursor.getColumnIndex(RIT_CATCHWT)));
                repackIngredients.setRIT_LOTREFID(mCursor.getString(mCursor.getColumnIndex(RIT_LOTREFID)));
                repackIngredients.setRIT_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(RIT_LOTEXPL)));
                repackIngredients.setRIT_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(RIT_LINESPLIT)));
                repackIngredients.setRIT_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(RIT_TRKQTYPK)));
                repackIngredients.setRIT_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_UPDFLAG)));
                repackIngredients.setRIT_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_ADDFLAG)));
                repackIngredients.setRIT_SLOT(mCursor.getString(mCursor.getColumnIndex(RIT_SLOT)));
                repackIngredients.setRIT_ALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ALLOCQTY)));
                repackIngredients.setRIT_TEMPALLOC(mCursor.getString(mCursor.getColumnIndex(RIT_TEMPALLOC)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }
*/

/*  //01/02/23_CIRK_For Fresha to APN
    public ArrayList<RepackIngredients> getRepackIngredients() {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where tranlineno >''", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_PANO(mCursor.getString(mCursor.getColumnIndex(RIT_PANO)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_SERIAL(mCursor.getString(mCursor.getColumnIndex(RIT_SERIAL)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));
                repackIngredients.setRIT_COST(mCursor.getString(mCursor.getColumnIndex(RIT_COST)));
                repackIngredients.setRIT_PADATE(mCursor.getString(mCursor.getColumnIndex(RIT_PADATE)));
                repackIngredients.setRIT_PASTAT(mCursor.getString(mCursor.getColumnIndex(RIT_PASTAT)));
                repackIngredients.setRIT_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(RIT_LCKSTAT)));
                repackIngredients.setRIT_LCKUSER(mCursor.getString(mCursor.getColumnIndex(RIT_LCKUSER)));
                repackIngredients.setRIT_LCKDATE(mCursor.getString(mCursor.getColumnIndex(RIT_LCKDATE)));
                repackIngredients.setRIT_LCKTIME(mCursor.getString(mCursor.getColumnIndex(RIT_LCKTIME)));
                repackIngredients.setRIT_ADDUSER(mCursor.getString(mCursor.getColumnIndex(RIT_ADDUSER)));
                repackIngredients.setRIT_ADDDATE(mCursor.getString(mCursor.getColumnIndex(RIT_ADDDATE)));
                repackIngredients.setRIT_ADDTIME(mCursor.getString(mCursor.getColumnIndex(RIT_ADDTIME)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_VENDNO(mCursor.getString(mCursor.getColumnIndex(RIT_VENDNO)));
                repackIngredients.setRIT_BINNO(mCursor.getString(mCursor.getColumnIndex(RIT_BINNO)));
                repackIngredients.setRIT_PALNO(mCursor.getString(mCursor.getColumnIndex(RIT_PALNO)));
                repackIngredients.setRIT_REMARKS(mCursor.getString(mCursor.getColumnIndex(RIT_REMARKS)));
                repackIngredients.setRIT_YIELD(mCursor.getString(mCursor.getColumnIndex(RIT_YIELD)));
                repackIngredients.setRIT_SETID(mCursor.getString(mCursor.getColumnIndex(RIT_SETID)));
                repackIngredients.setRIT_WEIGHT(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT)));
                repackIngredients.setRIT_ID_COL(mCursor.getString(mCursor.getColumnIndex(RIT_ID_COL)));
                repackIngredients.setRIT_WLOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_WLOTNO)));
                repackIngredients.setRIT_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(RIT_ORIGTRANLN)));
                repackIngredients.setRIT_STKUMID(mCursor.getString(mCursor.getColumnIndex(RIT_STKUMID)));
                repackIngredients.setRIT_USELOTS(mCursor.getString(mCursor.getColumnIndex(RIT_USELOTS)));
                repackIngredients.setRIT_UMFACT(mCursor.getString(mCursor.getColumnIndex(RIT_UMFACT)));
                repackIngredients.setRIT_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT1)));
                repackIngredients.setRIT_VOLUME(mCursor.getString(mCursor.getColumnIndex(RIT_VOLUME)));
                repackIngredients.setRIT_CATCHWT(mCursor.getString(mCursor.getColumnIndex(RIT_CATCHWT)));
                repackIngredients.setRIT_LOTREFID(mCursor.getString(mCursor.getColumnIndex(RIT_LOTREFID)));
                repackIngredients.setRIT_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(RIT_LOTEXPL)));
                repackIngredients.setRIT_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(RIT_LINESPLIT)));
                repackIngredients.setRIT_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(RIT_TRKQTYPK)));
                repackIngredients.setRIT_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_UPDFLAG)));
                repackIngredients.setRIT_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_ADDFLAG)));
                repackIngredients.setRIT_SLOT(mCursor.getString(mCursor.getColumnIndex(RIT_SLOT)));
                repackIngredients.setRIT_ALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ALLOCQTY)));
                repackIngredients.setRIT_TEMPALLOC(mCursor.getString(mCursor.getColumnIndex(RIT_TEMPALLOC)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }
*/

    public ArrayList<picktaskdetail> getEditPickTaskDetail() {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail", null);
        // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' '  AND stagingSlot > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Flag='H' AND taskNum = '" + Globals.gTaskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setDetailsTaskNum(mCursor.getString(mCursor.getColumnIndex(DETAIL_TASKNO)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
              //  tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public ArrayList<picktaskdetail> getPickTaskMenuDetail() {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail", null);
        // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' '  AND stagingSlot > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setDetailsTaskNum(mCursor.getString(mCursor.getColumnIndex(DETAIL_TASKNO)));
                tpicktaskdetail.setChgQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_CHGQTY)));
                tpicktaskdetail.setoTqtypicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_OTQTYPICKED)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
            //    tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public ArrayList<picktaskdetail> getPickTaskDetail() {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail", null);
       // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' '  AND stagingSlot > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setDetailsTaskNum(mCursor.getString(mCursor.getColumnIndex(DETAIL_TASKNO)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
            //    tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public ArrayList<picktaskdetail> getPickTaskDetailForValidation(String wlotNo) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail", null);
        // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' '  AND stagingSlot > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno = '" +wlotNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setDetailsTaskNum(mCursor.getString(mCursor.getColumnIndex(DETAIL_TASKNO)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
                tpicktaskdetail.setoTqtypicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_OTQTYPICKED)));
                //    tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public ArrayList<picktaskdetail> getPickTaskDetailForValidationRevert(String wlotNo) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail", null);
        // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wlotno > ' '  AND stagingSlot > ' ' AND taskNum = '" + Globals.gTaskNo + "' order by rowno desc ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskrevertolddata where wlotno = '" +wlotNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setDetailsTaskNum(mCursor.getString(mCursor.getColumnIndex(DETAIL_TASKNO)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
                tpicktaskdetail.setoTqtypicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_OTQTYPICKED)));
                //    tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }


    public ArrayList<picktaskdetail> getExportPickTaskDetail(String Item,String TranNo) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Tranlineno = '" + TranNo + "' OR wlotno is Null and Item = '" + Item + "' and taskNum = '" + Globals.gTaskNo + "' ", null);
        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "' and Tranlineno = '" + TranNo + "' OR wlotno is Null and Item = '" + Item + "' and stagingSlot >' ' ", null);
        //mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "' and Tranlineno = '" + TranNo + "' OR wlotno is Null and Item = '" + Item + "' ", null);

      /*  if (Globals.FROMBTNDONE){
            mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "' ", null);
        }else {
            mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "' and ( Tranlineno = '" + TranNo + "' OR wlotno is Null ) and Item = '" + Item + "' ", null);
        }*/

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));
                tpicktaskdetail.setChgQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_CHGQTY)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }




        return pickListdetail;
    }


    public ArrayList<picktaskdetail> getExportPickTaskTempAlloc() {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

       // mCursor = mSqlitedb.rawQuery("select * from picktaskrevertolddata  ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskrevertolddata where tempQty <>'' and tempQty <>'0'", null);


        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));
                tpicktaskdetail.setChgQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_CHGQTY)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
                tpicktaskdetail.setTempQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TEMPQTY)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }




        return pickListdetail;
    }
    public ArrayList<picktaskdetail> getExportPickTaskTempAllocForHoldAndSave() {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskrevertolddata WHERE lotno NOT in(SELECT lotno FROM picktaskdetail) and tempQty <>''", null);

        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));
                tpicktaskdetail.setChgQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_CHGQTY)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));
                tpicktaskdetail.setTempQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TEMPQTY)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }




        return pickListdetail;
    }




    public ArrayList<picktaskdetail> getRevertDataDetails(String Item,String TranNo) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where taskNum = '" + Globals.gTaskNo + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setIsedited(mCursor.getString(mCursor.getColumnIndex(DETAIL_ISEDITED)));
                tpicktaskdetail.setChgQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_CHGQTY)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }




        return pickListdetail;
    }



    public ArrayList<picktaskdetail> getPickTaskforSlot(String wlotNo) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where lotrefid = '"+ wlotNo +"'", null);
                //mCursor = mSqlitedb.rawQuery("select invtype from physicalcountICITEM where item = '"+ item +"'", null);

        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }



    public ArrayList<picktaskdetail> getPickTaskforSub(String itemNumber) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Item = '"+ itemNumber +"'", null);
        //mCursor = mSqlitedb.rawQuery("select invtype from physicalcountICITEM where item = '"+ item +"'", null);

        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public ArrayList<picktaskdetail> getCompletedPickTaskDetail() {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;
        String flag="Y";

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where flag='Y' ", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();

                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setpickDuration(mCursor.getColumnIndex(DETAIL_PICKDURATION));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));


                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public void loadReceiveTaskExportData() {

        mCursor = mSqlitedb.rawQuery("select a.taskno, a.tranlineno, a.doctype, a.docno, a.doclineno, a.item, a.umeasur, " +
                "a.loctid, a.wlotno, a.lotrefid, a.tqtyrec, a.trkqtyrec, b.pltlineno, b.tqty, b.trkqty, b.prtplttag, b.Slot, b.gtin " +
                " from receivetaskdetail a, receivetaskWHRPLT b " +
                "WHERE a.taskno = b.taskno and a.tranlineno = b.tasklineno and b.Flag = 'Y' ", null);
        try {
            while (mCursor.moveToNext()) {

                ContentValues cv = new ContentValues();
                cv.put(RTE_TASKNO, mCursor.getString(0));
                cv.put(RTE_TRANLINENO, mCursor.getString(1));
                cv.put(RTE_DOCTYPE, mCursor.getString(2));
                cv.put(RTE_DOCNO, mCursor.getString(3));
                cv.put(RTE_DOCLINENO, mCursor.getString(4));
                cv.put(RTE_ITEM, mCursor.getString(5));
                cv.put(RTE_UMEASUR, mCursor.getString(6));
                cv.put(RTE_LOCTID, mCursor.getString(7));
                cv.put(RTE_WLOTNO, mCursor.getString(8));
                cv.put(RTE_LOTREFID, mCursor.getString(9));
                cv.put(RTE_TQTY, mCursor.getString(10));
                cv.put(RTE_TRKQTY, mCursor.getString(11));
                cv.put(RTE_PLTLINENO, mCursor.getString(12));
                cv.put(RTE_PTQTY, mCursor.getString(13));
                cv.put(RTE_PTRKQTY, mCursor.getString(14));
                cv.put(RTE_PRTPLTTAG, mCursor.getString(15));
                cv.put(RTE_SLOT, mCursor.getString(16));
                cv.put(RTE_GTIN, mCursor.getString(17));

                mSqlitedb.insert(RECEIVE_TASK_EXPORT, null, cv);
                LogfileCreator.mAppendLog("Receive Task Export: Receive Task Export inserted successfully");

            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive Task Export save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public ArrayList<receivetaskloadtype> getReceiveTaskLTExportData() {
        ArrayList<receivetaskloadtype> receiveListdetail = new ArrayList<>();
        receivetaskloadtype treceivetaskloadtype = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskloadtype WHERE LoadTypeStatus = '1'", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskloadtype = new receivetaskloadtype();
                treceivetaskloadtype.setloadtype(mCursor.getString(0));
                treceivetaskloadtype.setdescrip(mCursor.getString(1));
                treceivetaskloadtype.setwelement(mCursor.getString(2));
                treceivetaskloadtype.setcollection(mCursor.getString(3));
                treceivetaskloadtype.setwidgetID(mCursor.getString(4));
                treceivetaskloadtype.setLoadId(mCursor.getString(5));
                treceivetaskloadtype.setLoadTypeStatus(mCursor.getString(6));
                treceivetaskloadtype.setWmsDate(mCursor.getString(7));
                treceivetaskloadtype.setMetricval(mCursor.getString(8));

                receiveListdetail.add(treceivetaskloadtype);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive Task LoadType save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListdetail;
    }

    public ArrayList<receivetaskexportdetail> getReceiveTaskExportData() {
        ArrayList<receivetaskexportdetail> receiveListdetail = new ArrayList<>();
        receivetaskexportdetail treceivetaskexportdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskexportdetail where taskno = '" + Globals.gRTTaskNo  + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskexportdetail = new receivetaskexportdetail();
                treceivetaskexportdetail.settaskno(mCursor.getString(0));
                treceivetaskexportdetail.settranlineno(mCursor.getString(1));
                treceivetaskexportdetail.setDoctype(mCursor.getString(2));
                treceivetaskexportdetail.setDocno(mCursor.getString(3));
                treceivetaskexportdetail.setDoclineno(mCursor.getString(4));
                treceivetaskexportdetail.setItem(mCursor.getString(5));
                treceivetaskexportdetail.setumeasur(mCursor.getString(6));
                treceivetaskexportdetail.setLoctid(mCursor.getString(7));
                treceivetaskexportdetail.setWlotno(mCursor.getString(8));
                treceivetaskexportdetail.setLotrefid(mCursor.getString(9));
                treceivetaskexportdetail.setTqtyrec(mCursor.getString(10));
                treceivetaskexportdetail.setTrkqtyrec(mCursor.getString(11));
                treceivetaskexportdetail.setpltlineno(mCursor.getString(12));
                treceivetaskexportdetail.setptqty(mCursor.getString(13));
                treceivetaskexportdetail.setptrkqty(mCursor.getString(14));
                treceivetaskexportdetail.setprtplttag(mCursor.getString(15));
                treceivetaskexportdetail.setSlot(mCursor.getString(16));
                treceivetaskexportdetail.setgTin(mCursor.getString(17));

                receiveListdetail.add(treceivetaskexportdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListdetail;
    }

    public ArrayList<movetaskdetail> getExportMoveTaskDetail(String taskNo) {
        ArrayList<movetaskdetail> moveTaskListDetail = new ArrayList<>();
        movetaskdetail tmovetaskdetail = null;

        // mCursor = mSqlitedb.rawQuery("select * from movetaskdetail ", null);
        mCursor = mSqlitedb.rawQuery("select * from movetaskdetail where taskno = '" + taskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tmovetaskdetail = new movetaskdetail();

                tmovetaskdetail.setTaskno(mCursor.getString(mCursor.getColumnIndex(MTD_TASKNO)));
                tmovetaskdetail.setTasktype(mCursor.getString(mCursor.getColumnIndex(MTD_TASKTYPE)));
                tmovetaskdetail.setStatus(mCursor.getString(mCursor.getColumnIndex(MTD_STATUS)));
                tmovetaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(MTD_TRANLINENO)));
                tmovetaskdetail.setChildID(mCursor.getString(mCursor.getColumnIndex(MTD_CHILDID)));
                tmovetaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(MTD_ITEM)));
                tmovetaskdetail.setLoctid(mCursor.getString(mCursor.getColumnIndex(MTD_LOCTID)));
                tmovetaskdetail.setWlotno(mCursor.getString(mCursor.getColumnIndex(MTD_WLOTNO)));
                tmovetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(MTD_PALNO)));
                tmovetaskdetail.setUmeasur(mCursor.getString(mCursor.getColumnIndex(MTD_UMEASUR)));
                tmovetaskdetail.setTqtyrq(mCursor.getString(mCursor.getColumnIndex(MTD_TQTYRQ)));
                tmovetaskdetail.setTqtyact(mCursor.getString(mCursor.getColumnIndex(MTD_TQTYACT)));
                tmovetaskdetail.setFromSlot(mCursor.getString(mCursor.getColumnIndex(MTD_FROMSLOT)));
                tmovetaskdetail.setToSlot(mCursor.getString(mCursor.getColumnIndex(MTD_TOSLOT)));
                tmovetaskdetail.setItmdesc(mCursor.getString(mCursor.getColumnIndex(MTD_ITEMDESC)));
                tmovetaskdetail.setPckdesc(mCursor.getString(mCursor.getColumnIndex(MTD_PCKDESC)));
                tmovetaskdetail.setWhqty(mCursor.getString(mCursor.getColumnIndex(MTD_WHQTY)));
                tmovetaskdetail.setAllocqty(mCursor.getString(mCursor.getColumnIndex(MTD_ALLOCQTY)));
                tmovetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(MTD_FLAG)));
                tmovetaskdetail.setLocked(mCursor.getString(mCursor.getColumnIndex(MTD_LOCKED)));
                tmovetaskdetail.setEdited(mCursor.getString(mCursor.getColumnIndex(MTD_EDITED)));

                moveTaskListDetail.add(tmovetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return moveTaskListDetail;
    }
    public ArrayList<movetaskdetail> getMoveTaskDetailForSave(String taskNo,String palNo,String item) {
        ArrayList<movetaskdetail> moveTaskListDetail = new ArrayList<>();
        movetaskdetail tmovetaskdetail = null;

        // mCursor = mSqlitedb.rawQuery("select * from movetaskdetail ", null);
        mCursor = mSqlitedb.rawQuery("select * from movetaskdetail where taskno = '" + taskNo + "' and palno ='"+palNo+"' and item = '"+item+"'", null);
        try {
            while (mCursor.moveToNext()) {
                tmovetaskdetail = new movetaskdetail();

                tmovetaskdetail.setTaskno(mCursor.getString(mCursor.getColumnIndex(MTD_TASKNO)));
                tmovetaskdetail.setTasktype(mCursor.getString(mCursor.getColumnIndex(MTD_TASKTYPE)));
                tmovetaskdetail.setStatus(mCursor.getString(mCursor.getColumnIndex(MTD_STATUS)));
                tmovetaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(MTD_TRANLINENO)));
                tmovetaskdetail.setChildID(mCursor.getString(mCursor.getColumnIndex(MTD_CHILDID)));
                tmovetaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(MTD_ITEM)));
                tmovetaskdetail.setLoctid(mCursor.getString(mCursor.getColumnIndex(MTD_LOCTID)));
                tmovetaskdetail.setWlotno(mCursor.getString(mCursor.getColumnIndex(MTD_WLOTNO)));
                tmovetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(MTD_PALNO)));
                tmovetaskdetail.setUmeasur(mCursor.getString(mCursor.getColumnIndex(MTD_UMEASUR)));
                tmovetaskdetail.setTqtyrq(mCursor.getString(mCursor.getColumnIndex(MTD_TQTYRQ)));
                tmovetaskdetail.setTqtyact(mCursor.getString(mCursor.getColumnIndex(MTD_TQTYACT)));
                tmovetaskdetail.setFromSlot(mCursor.getString(mCursor.getColumnIndex(MTD_FROMSLOT)));
                tmovetaskdetail.setToSlot(mCursor.getString(mCursor.getColumnIndex(MTD_TOSLOT)));
                tmovetaskdetail.setItmdesc(mCursor.getString(mCursor.getColumnIndex(MTD_ITEMDESC)));
                tmovetaskdetail.setPckdesc(mCursor.getString(mCursor.getColumnIndex(MTD_PCKDESC)));
                tmovetaskdetail.setWhqty(mCursor.getString(mCursor.getColumnIndex(MTD_WHQTY)));
                tmovetaskdetail.setAllocqty(mCursor.getString(mCursor.getColumnIndex(MTD_ALLOCQTY)));
                tmovetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(MTD_FLAG)));
                tmovetaskdetail.setLocked(mCursor.getString(mCursor.getColumnIndex(MTD_LOCKED)));
                tmovetaskdetail.setEdited(mCursor.getString(mCursor.getColumnIndex(MTD_EDITED)));

                moveTaskListDetail.add(tmovetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return moveTaskListDetail;
    }

    public ArrayList<movetaskdetail> getMoveTaskDetail(String taskNo) {
        ArrayList<movetaskdetail> moveTaskListDetail = new ArrayList<>();
        movetaskdetail tmovetaskdetail = null;

       // mCursor = mSqlitedb.rawQuery("select * from movetaskdetail ", null);
        mCursor = mSqlitedb.rawQuery("select * from movetaskdetail where taskno = '" + taskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                tmovetaskdetail = new movetaskdetail();

                tmovetaskdetail.setTaskno(mCursor.getString(mCursor.getColumnIndex(MTD_TASKNO)));
                tmovetaskdetail.setTasktype(mCursor.getString(mCursor.getColumnIndex(MTD_TASKTYPE)));
                tmovetaskdetail.setStatus(mCursor.getString(mCursor.getColumnIndex(MTD_STATUS)));
                tmovetaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(MTD_TRANLINENO)));
                tmovetaskdetail.setChildID(mCursor.getString(mCursor.getColumnIndex(MTD_CHILDID)));
                tmovetaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(MTD_ITEM)));
                tmovetaskdetail.setLoctid(mCursor.getString(mCursor.getColumnIndex(MTD_LOCTID)));
                tmovetaskdetail.setWlotno(mCursor.getString(mCursor.getColumnIndex(MTD_WLOTNO)));
                tmovetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(MTD_PALNO)));
                tmovetaskdetail.setUmeasur(mCursor.getString(mCursor.getColumnIndex(MTD_UMEASUR)));
                tmovetaskdetail.setTqtyrq(mCursor.getString(mCursor.getColumnIndex(MTD_TQTYRQ)));
                tmovetaskdetail.setTqtyact(mCursor.getString(mCursor.getColumnIndex(MTD_TQTYACT)));
                tmovetaskdetail.setFromSlot(mCursor.getString(mCursor.getColumnIndex(MTD_FROMSLOT)));
                tmovetaskdetail.setToSlot(mCursor.getString(mCursor.getColumnIndex(MTD_TOSLOT)));
                tmovetaskdetail.setItmdesc(mCursor.getString(mCursor.getColumnIndex(MTD_ITEMDESC)));
                tmovetaskdetail.setPckdesc(mCursor.getString(mCursor.getColumnIndex(MTD_PCKDESC)));
                tmovetaskdetail.setWhqty(mCursor.getString(mCursor.getColumnIndex(MTD_WHQTY)));
                tmovetaskdetail.setAllocqty(mCursor.getString(mCursor.getColumnIndex(MTD_ALLOCQTY)));
                tmovetaskdetail.setLocked(mCursor.getString(mCursor.getColumnIndex(MTD_LOCKED)));
                tmovetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(MTD_FLAG)));
                tmovetaskdetail.setEdited(mCursor.getString(mCursor.getColumnIndex(MTD_EDITED)));

                moveTaskListDetail.add(tmovetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return moveTaskListDetail;
    }
    public ArrayList<receivetaskdetail> getReceiveTaskDetail() {
        ArrayList<receivetaskdetail> receiveListDetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskdetail = new receivetaskdetail();

                treceivetaskdetail.settaskno(mCursor.getString(mCursor.getColumnIndex(RTD_TASKNO)));
                treceivetaskdetail.settranlineno(mCursor.getString(mCursor.getColumnIndex(RTD_TRANLINENO)));
                treceivetaskdetail.setdoctype(mCursor.getString(mCursor.getColumnIndex(RTD_DOCTYPE)));
                treceivetaskdetail.setdocno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCNO)));
                treceivetaskdetail.setdoclineno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCLINENO)));
                treceivetaskdetail.setitem(mCursor.getString(mCursor.getColumnIndex(RTD_ITEM)));
                treceivetaskdetail.setloctid(mCursor.getString(mCursor.getColumnIndex(RTD_LOCTID)));
                treceivetaskdetail.setwlotno(mCursor.getString(mCursor.getColumnIndex(RTD_WLOTNO)));
                treceivetaskdetail.setumeasur(mCursor.getString(mCursor.getColumnIndex(RTD_UMEASUR)));
                treceivetaskdetail.setwmsstat(mCursor.getString(mCursor.getColumnIndex(RTD_WMSSTAT)));
                treceivetaskdetail.settqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYREC)));
                treceivetaskdetail.settrkqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TRKQTYREC)));
                treceivetaskdetail.setrevlev(mCursor.getString(mCursor.getColumnIndex(RTD_REVLEV)));
                treceivetaskdetail.settqtyinc(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYINC)));
                treceivetaskdetail.setitmdesc(mCursor.getString(mCursor.getColumnIndex(RTD_ITMDESC)));
                treceivetaskdetail.setpckdesc(mCursor.getString(mCursor.getColumnIndex(RTD_PCKDESC)));
                treceivetaskdetail.setcountryid(mCursor.getString(mCursor.getColumnIndex(RTD_COUNTRYID)));
                treceivetaskdetail.setitemShow(mCursor.getString(mCursor.getColumnIndex(RTD_ITEMSHOW)));
                treceivetaskdetail.setcollection(mCursor.getString(mCursor.getColumnIndex(RTD_COLLECTION)));
                treceivetaskdetail.setwelement(mCursor.getString(mCursor.getColumnIndex(RTD_WELEMENT)));
                treceivetaskdetail.setwidgetID(mCursor.getString(mCursor.getColumnIndex(RTD_WIDGETID)));
                treceivetaskdetail.setcatchwt(mCursor.getString(mCursor.getColumnIndex(RTD_CATCHWT)));
                treceivetaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(RTD_DECNUM)));
                treceivetaskdetail.setlotrefid(mCursor.getString(mCursor.getColumnIndex(RTD_LOTREFID)));
                treceivetaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(RTD_LINESPLIT)));
                treceivetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(RTD_FLAG)));
                treceivetaskdetail.setrowNo(mCursor.getString(mCursor.getColumnIndex(RTD_ROWNO)));
                treceivetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(RTD_PALNO)));

                receiveListDetail.add(treceivetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListDetail;
    }

    public ArrayList<MoveManuallyTransaction> getMmTran() {
        ArrayList<MoveManuallyTransaction> mmTranList = new ArrayList<>();
        MoveManuallyTransaction mmTran = null;

        mCursor = mSqlitedb.rawQuery("select * from moveManuallyTransaction", null);
        try {
            while (mCursor.moveToNext()) {
                mmTran = new MoveManuallyTransaction();

                mmTran.setMmTranWlotno(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_WLOTNO)));
                mmTran.setMmTranItem(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_ITEMNO)));
                mmTran.setMmTranSlot(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_SLOTNO)));
                mmTran.setMmTranLoctid(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_LOCTID)));
                mmTran.setMmTranUOM(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_UOM)));
                mmTran.setMmTranQty(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_QTY))));
                mmTran.setMmTranTrkqty(Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_TRKQTY))));
                mmTran.setMmTranEqty(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_EQTY)));
                mmTran.setMmTranItmDesc(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_ITMDESC)));
                mmTran.setMmTranCatchwt(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_CATCHWT)));
                mmTran.setMmTranLotrefid(mCursor.getString(mCursor.getColumnIndex(MOVE_MANUALLY_TRANSACTION_LotrefId)));

                mmTranList.add(mmTran);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return mmTranList;
    }


    public ArrayList<receivetaskdetail> getReceiveTaskDetailForChargesTesting() {
        ArrayList<receivetaskdetail> receiveListDetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskdetail ", null);
        try {


                for (int i =0; i<=6; i++) {

                    treceivetaskdetail = new receivetaskdetail();

                    if (i==0){
                        treceivetaskdetail.settaskno("Airbag");
                    }if (i==1){
                        treceivetaskdetail.settaskno("ColdStore");
                    }if (i==2){
                        treceivetaskdetail.settaskno("CrossDock");
                    }if (i==3){
                        treceivetaskdetail.settaskno("Frt");
                    }if (i==4){
                        treceivetaskdetail.settaskno("FRTIN");
                    }if (i==5){
                        treceivetaskdetail.settaskno("Lcing");
                    }if (i==6){
                        treceivetaskdetail.settaskno("Repack");
                    }

                    receiveListDetail.add(treceivetaskdetail);
                }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListDetail;
    }



    public ArrayList<receivetaskheader> getReceiveTaskHeader(String TaskNum) {
        ArrayList<receivetaskheader> receiveListHeader = new ArrayList<>();
        receivetaskheader treceivetaskheader = null;
        TaskNum = "PO: "+ TaskNum;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskheader where descrip LIKE '" + TaskNum +"' ", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskheader = new receivetaskheader();

                treceivetaskheader.setdescrip(mCursor.getString(mCursor.getColumnIndex(RTH_DESCRIP)));
                treceivetaskheader.setvenddescrip(mCursor.getString(mCursor.getColumnIndex(RTH_VENDDESCRIP)));
                treceivetaskheader.setcasecounted(mCursor.getString(mCursor.getColumnIndex(RTH_CASECOUNTED)));
                treceivetaskheader.setcasetotal(mCursor.getString(mCursor.getColumnIndex(RTH_CASETOTAL)));
                treceivetaskheader.setplttotal(mCursor.getString(mCursor.getColumnIndex(RTH_PLTOTAL)));
                treceivetaskheader.setpltcounted(mCursor.getString(mCursor.getColumnIndex(RTH_PLTCOUNTED)));
                treceivetaskheader.setwmsdate(mCursor.getString(mCursor.getColumnIndex(RTH_WMSDATE)));
                receiveListHeader.add(treceivetaskheader);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Receive List Header save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListHeader;
    }

    public int GetLoadidValue() {

        String loadtype = "";

        int loadid = 0;

        mCursor = mSqlitedb.rawQuery("select LoadId from receivetaskloadtype Limit 1", null);
        loadid = mCursor.getCount();
        try {
            while (mCursor.moveToNext()) {
                loadtype = mCursor.getString(0);
                if(loadtype!="null") {
                    loadid = Integer.parseInt(mCursor.getString(0));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Load Id Failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return loadid;
    }

    public ArrayList<receivetaskloadtype> selectreceivetaskload() {
        ArrayList<receivetaskloadtype> receiveLoadList = new ArrayList<>();
        receivetaskloadtype treceivetaskloadtype = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskloadtype LIMIT 1", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskloadtype = new receivetaskloadtype();
                treceivetaskloadtype.setwelement(mCursor.getString(mCursor.getColumnIndex(RTLT_WELEMENT)));
                treceivetaskloadtype.setcollection(mCursor.getString(mCursor.getColumnIndex(RTLT_COLLECTION)));
                treceivetaskloadtype.setwidgetID(mCursor.getString(mCursor.getColumnIndex(RTLT_WIDGETID)));
                treceivetaskloadtype.setLoadId(mCursor.getString(mCursor.getColumnIndex(RTLT_LOADID)));

                receiveLoadList.add(treceivetaskloadtype);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive Load List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveLoadList;
    }

    public ArrayList<receivetaskWHRPLT> selectReceiveTaskWHRPLT() {
        ArrayList<receivetaskWHRPLT> receiveListWHRPLT = new ArrayList<>();
        receivetaskWHRPLT treceivetaskWHRPLT = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskWHRPLT where taskno = '" + Globals.gRTTaskNo + "' and " +
                "tasklineno='" + Globals.gRTTranline + "' and pltlineno='" + Globals.gRTPalline + "' and Flag ='Y' ", null);

        try {
            while (mCursor.moveToNext()) {
                treceivetaskWHRPLT = new receivetaskWHRPLT();

                treceivetaskWHRPLT.settaskno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TASKNO)));
                treceivetaskWHRPLT.settasklineno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TASKLINENO)));
                treceivetaskWHRPLT.setpltlineno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PLTLINENO)));
                treceivetaskWHRPLT.settqty(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TQTY)));
                treceivetaskWHRPLT.setpltstat(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PLTSTAT)));
                treceivetaskWHRPLT.setprtplttag(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PRTPLTTAG)));
                treceivetaskWHRPLT.settrkqty(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TRKQTY)));
                treceivetaskWHRPLT.setSlot(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_SLOT)));
                treceivetaskWHRPLT.setgTin(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_GTIN)));

                receiveListWHRPLT.add(treceivetaskWHRPLT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List WHRPLT by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListWHRPLT;
    }

    public ArrayList<receivetaskWHRPLT> selectReceiveTaskWHRPLTForTran(String taskno) {
        ArrayList<receivetaskWHRPLT> receiveListWHRPLT = new ArrayList<>();
        receivetaskWHRPLT treceivetaskWHRPLT = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskWHRPLT where taskno = '" + taskno+ "'", null);

        try {
            while (mCursor.moveToNext()) {
                treceivetaskWHRPLT = new receivetaskWHRPLT();

                treceivetaskWHRPLT.settaskno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TASKNO)));
                treceivetaskWHRPLT.settasklineno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TASKLINENO)));
                treceivetaskWHRPLT.setpltlineno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PLTLINENO)));
                treceivetaskWHRPLT.settqty(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TQTY)));
                treceivetaskWHRPLT.setpltstat(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PLTSTAT)));
                treceivetaskWHRPLT.setprtplttag(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PRTPLTTAG)));
                treceivetaskWHRPLT.settrkqty(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TRKQTY)));
                treceivetaskWHRPLT.setSlot(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_SLOT)));
                treceivetaskWHRPLT.setgTin(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_GTIN)));
                treceivetaskWHRPLT.setFlag(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_FLAG)));

                receiveListWHRPLT.add(treceivetaskWHRPLT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List WHRPLT by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListWHRPLT;
    }

    public ArrayList<receivetaskWHRPLT> selectReceiveTaskTranWHRPLT(String taskno) {
        ArrayList<receivetaskWHRPLT> receiveListWHRPLT = new ArrayList<>();
        receivetaskWHRPLT treceivetaskWHRPLT = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetasktranWHRPLT where taskno = '"+ taskno +"'", null);

        try {
            while (mCursor.moveToNext()) {
                treceivetaskWHRPLT = new receivetaskWHRPLT();

                treceivetaskWHRPLT.settaskno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TASKNO)));
                treceivetaskWHRPLT.settasklineno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TASKLINENO)));
                treceivetaskWHRPLT.setpltlineno(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PLTLINENO)));
                treceivetaskWHRPLT.settqty(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TQTY)));
                treceivetaskWHRPLT.setpltstat(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PLTSTAT)));
                treceivetaskWHRPLT.setprtplttag(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_PRTPLTTAG)));
                treceivetaskWHRPLT.settrkqty(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_TRKQTY)));
                treceivetaskWHRPLT.setSlot(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_SLOT)));
                treceivetaskWHRPLT.setgTin(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_GTIN)));
                treceivetaskWHRPLT.setFlag(mCursor.getString(mCursor.getColumnIndex(RTWHRPLT_FLAG)));

                receiveListWHRPLT.add(treceivetaskWHRPLT);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List WHRPLT by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListWHRPLT;
    }


    public ArrayList<receivetaskdetail> selectReceiveTaskDetail(String mPickItem) {
        ArrayList<receivetaskdetail> receiveListdetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from receivetaskdetail where item = '" + mPickItem + "' and Flag='N' ", null);
        mCursor = mSqlitedb.rawQuery("select * from receivetaskdetail where item = '" + mPickItem + "' and tranlineno = '" + Globals.gRTTranline + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskdetail = new receivetaskdetail();
                //notification.setPickTask(mCursor.getColumnIndex(PICKTASK_COUNT));
                treceivetaskdetail.settaskno(mCursor.getString(mCursor.getColumnIndex(RTD_TASKNO)));
                treceivetaskdetail.settranlineno(mCursor.getString(mCursor.getColumnIndex(RTD_TRANLINENO)));
                treceivetaskdetail.setdoctype(mCursor.getString(mCursor.getColumnIndex(RTD_DOCTYPE)));
                treceivetaskdetail.setdocno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCNO)));
                treceivetaskdetail.setdoclineno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCLINENO)));
                treceivetaskdetail.setitem(mCursor.getString(mCursor.getColumnIndex(RTD_ITEM)));
                treceivetaskdetail.setloctid(mCursor.getString(mCursor.getColumnIndex(RTD_LOCTID)));
                treceivetaskdetail.setwlotno(mCursor.getString(mCursor.getColumnIndex(RTD_WLOTNO)));
                treceivetaskdetail.setumeasur(mCursor.getString(mCursor.getColumnIndex(RTD_UMEASUR)));
                treceivetaskdetail.setwmsstat(mCursor.getString(mCursor.getColumnIndex(RTD_WMSSTAT)));
                treceivetaskdetail.settqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYREC)));
                treceivetaskdetail.settrkqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TRKQTYREC)));
                treceivetaskdetail.setrevlev(mCursor.getString(mCursor.getColumnIndex(RTD_REVLEV)));
                treceivetaskdetail.settqtyinc(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYINC)));
                treceivetaskdetail.setitmdesc(mCursor.getString(mCursor.getColumnIndex(RTD_ITMDESC)));
                treceivetaskdetail.setpckdesc(mCursor.getString(mCursor.getColumnIndex(RTD_PCKDESC)));
                treceivetaskdetail.setcountryid(mCursor.getString(mCursor.getColumnIndex(RTD_COUNTRYID)));
                treceivetaskdetail.setitemShow(mCursor.getString(mCursor.getColumnIndex(RTD_ITEMSHOW)));
                treceivetaskdetail.setcollection(mCursor.getString(mCursor.getColumnIndex(RTD_COLLECTION)));
                treceivetaskdetail.setwelement(mCursor.getString(mCursor.getColumnIndex(RTD_WELEMENT)));
                treceivetaskdetail.setwidgetID(mCursor.getString(mCursor.getColumnIndex(RTD_WIDGETID)));
                treceivetaskdetail.setcatchwt(mCursor.getString(mCursor.getColumnIndex(RTD_CATCHWT)));
                treceivetaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(RTD_DECNUM)));
                treceivetaskdetail.setlotrefid(mCursor.getString(mCursor.getColumnIndex(RTD_LOTREFID)));
                treceivetaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(RTD_LINESPLIT)));
                treceivetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(RTD_FLAG)));
                treceivetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(RTD_PALNO)));

                receiveListdetail.add(treceivetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListdetail;
    }
    public ArrayList<SlotList> selectReceiveSlotList(String mPickItem,String taskno) {
        ArrayList<SlotList> slotLists = new ArrayList<>();
        SlotList tSlotList = null;


        mCursor = mSqlitedb.rawQuery("select slot from receiveSlotList where item = '" + mPickItem + "' and taskno = '"+ taskno +"'", null);
        try {
            while (mCursor.moveToNext()) {
                tSlotList = new SlotList();
                tSlotList.setSlot(mCursor.getString(mCursor.getColumnIndex(RSL_SLOT)));

                slotLists.add(tSlotList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return slotLists;
    }

    public boolean isMoveTaskSlotAvail(String LoctId,String taskno, String slot) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select slot from movetaskslotlist where loctId = '" + LoctId + "' and taskNo = '"+ taskno +"' and slot = '"+slot+"'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }


    public boolean isSoNoAvail(String SoNo) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select sono from SalesOrderPicklist where sono = '" + SoNo + "' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public void updateMoveTaskDetail(String palNo,String fromSlot,String qty,String toSlot,String taskNo) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.MTD_TOSLOT,toSlot);
            cv.put(WMSDbHelper.MTD_TQTYACT,qty);
            cv.put(WMSDbHelper.MTD_FLAG,"Y");
            cv.put(WMSDbHelper.MTD_EDITED,"1");



            mSqlitedb.update(WMSDbHelper.MOVE_TASK_DETAIL, cv, "taskNo = '"+ taskNo + "' and palno = '" +palNo+ "' and fromSlot = '" +fromSlot+ "'", null);

            Log.i("Move Task Details: ", "Move Task Details updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Move Task Details update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }



    public ArrayList<MoveTaskSlotList> selectMoveTaskSlotList(String LoctId,String taskno, String slot) {
        ArrayList<MoveTaskSlotList> moveTaskSlotList = new ArrayList<>();
        MoveTaskSlotList tMoveTaskSlotList = null;


        mCursor = mSqlitedb.rawQuery("select * from movetaskslotlist where loctId = '" + LoctId + "' and taskNo = '"+ taskno +"' and slot <> '"+slot+"'", null);
        try {
            while (mCursor.moveToNext()) {
                tMoveTaskSlotList = new MoveTaskSlotList();

                tMoveTaskSlotList.setSlot(mCursor.getString(mCursor.getColumnIndex(MTSL_SLOT)));
                tMoveTaskSlotList.setSlottype(mCursor.getString(mCursor.getColumnIndex(MTSL_SLOTTYPE)));

                moveTaskSlotList.add(tMoveTaskSlotList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return moveTaskSlotList;
    }

    public ArrayList<receivetaskdetail> selectReceiveTaskDetailForTran(String taskno) {
        ArrayList<receivetaskdetail> receiveListdetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        //mCursor = mSqlitedb.rawQuery("select * from receivetaskdetail where item = '" + mPickItem + "' and Flag='N' ", null);
        mCursor = mSqlitedb.rawQuery("select * from receivetaskdetail where taskno = '" + taskno + "'", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskdetail = new receivetaskdetail();
                //notification.setPickTask(mCursor.getColumnIndex(PICKTASK_COUNT));
                treceivetaskdetail.settaskno(mCursor.getString(mCursor.getColumnIndex(RTD_TASKNO)));
                treceivetaskdetail.settranlineno(mCursor.getString(mCursor.getColumnIndex(RTD_TRANLINENO)));
                treceivetaskdetail.setdoctype(mCursor.getString(mCursor.getColumnIndex(RTD_DOCTYPE)));
                treceivetaskdetail.setdocno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCNO)));
                treceivetaskdetail.setdoclineno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCLINENO)));
                treceivetaskdetail.setitem(mCursor.getString(mCursor.getColumnIndex(RTD_ITEM)));
                treceivetaskdetail.setloctid(mCursor.getString(mCursor.getColumnIndex(RTD_LOCTID)));
                treceivetaskdetail.setwlotno(mCursor.getString(mCursor.getColumnIndex(RTD_WLOTNO)));
                treceivetaskdetail.setumeasur(mCursor.getString(mCursor.getColumnIndex(RTD_UMEASUR)));
                treceivetaskdetail.setwmsstat(mCursor.getString(mCursor.getColumnIndex(RTD_WMSSTAT)));
                treceivetaskdetail.settqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYREC)));
                treceivetaskdetail.settrkqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TRKQTYREC)));
                treceivetaskdetail.setrevlev(mCursor.getString(mCursor.getColumnIndex(RTD_REVLEV)));
                treceivetaskdetail.settqtyinc(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYINC)));
                treceivetaskdetail.setitmdesc(mCursor.getString(mCursor.getColumnIndex(RTD_ITMDESC)));
                treceivetaskdetail.setpckdesc(mCursor.getString(mCursor.getColumnIndex(RTD_PCKDESC)));
                treceivetaskdetail.setcountryid(mCursor.getString(mCursor.getColumnIndex(RTD_COUNTRYID)));
                treceivetaskdetail.setitemShow(mCursor.getString(mCursor.getColumnIndex(RTD_ITEMSHOW)));
                treceivetaskdetail.setcollection(mCursor.getString(mCursor.getColumnIndex(RTD_COLLECTION)));
                treceivetaskdetail.setwelement(mCursor.getString(mCursor.getColumnIndex(RTD_WELEMENT)));
                treceivetaskdetail.setwidgetID(mCursor.getString(mCursor.getColumnIndex(RTD_WIDGETID)));
                treceivetaskdetail.setcatchwt(mCursor.getString(mCursor.getColumnIndex(RTD_CATCHWT)));
                treceivetaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(RTD_DECNUM)));
                treceivetaskdetail.setlotrefid(mCursor.getString(mCursor.getColumnIndex(RTD_LOTREFID)));
                treceivetaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(RTD_LINESPLIT)));
                treceivetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(RTD_FLAG)));
                treceivetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(RTD_PALNO)));
                treceivetaskdetail.setrowNo(mCursor.getString(mCursor.getColumnIndex(RTD_ROWNO)));

                receiveListdetail.add(treceivetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListdetail;
    }

    public ArrayList<receivetaskdetail> getReceiveTaskTranDetail(String taskno) {
        ArrayList<receivetaskdetail> receiveListdetail = new ArrayList<>();
        receivetaskdetail treceivetaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from receivetasktrandetail where taskno = '"+taskno+"'", null);
        try {
            while (mCursor.moveToNext()) {
                treceivetaskdetail = new receivetaskdetail();

                treceivetaskdetail.settaskno(mCursor.getString(mCursor.getColumnIndex(RTD_TASKNO)));
                treceivetaskdetail.settranlineno(mCursor.getString(mCursor.getColumnIndex(RTD_TRANLINENO)));
                treceivetaskdetail.setdoctype(mCursor.getString(mCursor.getColumnIndex(RTD_DOCTYPE)));
                treceivetaskdetail.setdocno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCNO)));
                treceivetaskdetail.setdoclineno(mCursor.getString(mCursor.getColumnIndex(RTD_DOCLINENO)));
                treceivetaskdetail.setitem(mCursor.getString(mCursor.getColumnIndex(RTD_ITEM)));
                treceivetaskdetail.setloctid(mCursor.getString(mCursor.getColumnIndex(RTD_LOCTID)));
                treceivetaskdetail.setwlotno(mCursor.getString(mCursor.getColumnIndex(RTD_WLOTNO)));
                treceivetaskdetail.setumeasur(mCursor.getString(mCursor.getColumnIndex(RTD_UMEASUR)));
                treceivetaskdetail.setwmsstat(mCursor.getString(mCursor.getColumnIndex(RTD_WMSSTAT)));
                treceivetaskdetail.settqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYREC)));
                treceivetaskdetail.settrkqtyrec(mCursor.getString(mCursor.getColumnIndex(RTD_TRKQTYREC)));
                treceivetaskdetail.setrevlev(mCursor.getString(mCursor.getColumnIndex(RTD_REVLEV)));
                treceivetaskdetail.settqtyinc(mCursor.getString(mCursor.getColumnIndex(RTD_TQTYINC)));
                treceivetaskdetail.setitmdesc(mCursor.getString(mCursor.getColumnIndex(RTD_ITMDESC)));
                treceivetaskdetail.setpckdesc(mCursor.getString(mCursor.getColumnIndex(RTD_PCKDESC)));
                treceivetaskdetail.setcountryid(mCursor.getString(mCursor.getColumnIndex(RTD_COUNTRYID)));
                treceivetaskdetail.setitemShow(mCursor.getString(mCursor.getColumnIndex(RTD_ITEMSHOW)));
                treceivetaskdetail.setcollection(mCursor.getString(mCursor.getColumnIndex(RTD_COLLECTION)));
                treceivetaskdetail.setwelement(mCursor.getString(mCursor.getColumnIndex(RTD_WELEMENT)));
                treceivetaskdetail.setwidgetID(mCursor.getString(mCursor.getColumnIndex(RTD_WIDGETID)));
                treceivetaskdetail.setcatchwt(mCursor.getString(mCursor.getColumnIndex(RTD_CATCHWT)));
                treceivetaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(RTD_DECNUM)));
                treceivetaskdetail.setlotrefid(mCursor.getString(mCursor.getColumnIndex(RTD_LOTREFID)));
                treceivetaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(RTD_LINESPLIT)));
                treceivetaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(RTD_FLAG)));
                treceivetaskdetail.setPalno(mCursor.getString(mCursor.getColumnIndex(RTD_PALNO)));
                treceivetaskdetail.setrowNo(mCursor.getString(mCursor.getColumnIndex(RTD_ROWNO)));

                receiveListdetail.add(treceivetaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select Receive List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return receiveListdetail;
    }

    public ArrayList<picktaskdetail> selectPickTaskDetail(String mPickItem) {
        ArrayList<picktaskdetail> pickListdetail = new ArrayList<>();
        picktaskdetail tpicktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Item = '" + mPickItem + "' and Flag='N' and taskNum = '" + Globals.gTaskNo + "'  and Docstat <> 'X' and Docstat <> 'V' ORDER by Docno ", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskdetail();
                //notification.setPickTask(mCursor.getColumnIndex(PICKTASK_COUNT));
                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_SLOT)));
                tpicktaskdetail.setTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TQTY)));
                tpicktaskdetail.setorgTQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTQTY)));
                tpicktaskdetail.setTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKQTY)));
                tpicktaskdetail.setorgTrkQty(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRKQTY)));
                tpicktaskdetail.setUom(mCursor.getString(mCursor.getColumnIndex(DETAIL_UOM)));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ITEM)));
                tpicktaskdetail.setDescrip(mCursor.getString(mCursor.getColumnIndex(DETAIL_DESC)));
                tpicktaskdetail.setWLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_WLOTNO)));
                tpicktaskdetail.setLotNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_LOTNO)));
                tpicktaskdetail.setTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRANLINENO)));
                tpicktaskdetail.setorgTranlineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGTRANLINENO)));
                tpicktaskdetail.setDoctype(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCTYPE)));
                tpicktaskdetail.setDocno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCNO)));
                tpicktaskdetail.setDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCLINENO)));
                tpicktaskdetail.setorgDoclineno(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORGDOCLINENO)));
                tpicktaskdetail.setDocstat(mCursor.getString(mCursor.getColumnIndex(DETAIL_DOCSTAT)));
                tpicktaskdetail.setWeight(mCursor.getString(mCursor.getColumnIndex(DETAIL_WEIGHT)));
                tpicktaskdetail.setVolume(mCursor.getString(mCursor.getColumnIndex(DETAIL_VOLUME)));
                tpicktaskdetail.setdecnum(mCursor.getString(mCursor.getColumnIndex(DETAIL_DECNUM)));
                tpicktaskdetail.setStkumid(mCursor.getString(mCursor.getColumnIndex(DETAIL_STKUMID)));
                tpicktaskdetail.setCatchwt(mCursor.getString(mCursor.getColumnIndex(DETAIL_CATCHWT)));
                tpicktaskdetail.setUmfact(mCursor.getString(mCursor.getColumnIndex(DETAIL_UMFACT)));
                tpicktaskdetail.setTshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TSHIPPED)));
                tpicktaskdetail.setTrkshipped(mCursor.getString(mCursor.getColumnIndex(DETAIL_TRKSHIPPED)));
                tpicktaskdetail.setLbshp(mCursor.getString(mCursor.getColumnIndex(DETAIL_LBSHP)));
                tpicktaskdetail.setLinesplit(mCursor.getString(mCursor.getColumnIndex(DETAIL_LINESPLIT)));
                tpicktaskdetail.setFlag(mCursor.getString(mCursor.getColumnIndex(DETAIL_FLAG)));
                tpicktaskdetail.setSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_ITEM)));
                tpicktaskdetail.setSubTranNo(mCursor.getString(mCursor.getColumnIndex(DETAIL_SUBSTITUTED_TRANNO)));
                tpicktaskdetail.setIsSubItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_IS_SUBSTITUTED)));
                tpicktaskdetail.setOrgSOItem(mCursor.getString(mCursor.getColumnIndex(DETAIL_ORG_SOITEM)));
                tpicktaskdetail.setStagingSlot(mCursor.getString(mCursor.getColumnIndex(DETAIL_STAGINGSLOT)));
                tpicktaskdetail.setoTqtypicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_OTQTYPICKED)));
                tpicktaskdetail.setPicked(mCursor.getString(mCursor.getColumnIndex(DETAIL_PICKED)));

                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select pick List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public ArrayList<picktaskWHMQTY> getPickTaskWHMQTY(String mPickItem,String wLotno) {
        ArrayList<picktaskWHMQTY> pickListdetail = new ArrayList<>();
        picktaskWHMQTY tpicktaskdetail = null;

        mCursor = mSqlitedb.rawQuery("select * from picktaskWHMQTY where Item = '" + mPickItem + "' and wLotno = '" + wLotno + "' and taskNum = '" + Globals.gTaskNo + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                tpicktaskdetail = new picktaskWHMQTY();
                //notification.setPickTask(mCursor.getColumnIndex(PICKTASK_COUNT));
                tpicktaskdetail.setItem(mCursor.getString(mCursor.getColumnIndex(WHMQTY_ITEM)));
                tpicktaskdetail.setLoctid(mCursor.getString(mCursor.getColumnIndex(WHMQTY_LOCTID)));
                tpicktaskdetail.setWlotno(mCursor.getString(mCursor.getColumnIndex(WHMQTY_WLOTNO)));
                tpicktaskdetail.setSlot(mCursor.getString(mCursor.getColumnIndex(WHMQTY_SLOT)));
                tpicktaskdetail.setUmeasur(mCursor.getString(mCursor.getColumnIndex(WHMQTY_UMEASUR)));
                tpicktaskdetail.setTqty(mCursor.getString(mCursor.getColumnIndex(WHMQTY_TQTY)));
                tpicktaskdetail.setSlottype(mCursor.getString(mCursor.getColumnIndex(WHMQTY_SLOTTYPE)));

                pickListdetail.add(tpicktaskdetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "Select pick List detail by Item";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pickListdetail;
    }

    public boolean isItemAvailable(String item) {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Item = '" + item + "' and Flag = 'N' and taskNum='" + Globals.gTaskNo +  "' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }



    public boolean isDataAvailable(String item, String strFlag) {

        boolean isAvail = false;

        int count = 0;


        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Item = '" + item + "' and NOT Flag = '" + strFlag + "'  and Docstat <> 'X' and Docstat <> 'V' ", null);

        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isTaskCompleted() {

        boolean isAvail = false;

        int count = 0;

       // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where (Flag = 'N' OR Flag = 'H') OR (chgQty = 'Y' and LotNo = '') and taskNum = '" + Globals.gTaskNo + "' and NOT Docstat='V'", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where (Flag = 'N' OR Flag = 'H') and taskNum = '" + Globals.gTaskNo + "' and NOT Docstat='V'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isChangedAvail() {

        boolean isAvail = false;

        int count = 0;

        //    mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Changed = 'Y' and TaskNo = '"+Globals.gTaskNo+"'", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where chgQty = 'Y' and not Docstat='X'and not Docstat='V' and taskNum = '"+Globals.gTaskNo+"'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }


    public boolean isTaskOnHold() {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Flag = 'H' and taskNum = '" + Globals.gTaskNo + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isStagingCompleted() {

        boolean isAvail = false;

        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where Flag = 'Y' and  stagingSlot is null", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public int mTranlineCount() {

        int Count = 0;

        mCursor = mSqlitedb.rawQuery("select max(CAST(Tranlineno AS INTEGER)) as Tranlineno from picktaskdetail where taskNum='" + Globals.gTaskNo + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

    public int getmDoclineCount(String docNo) {

        int Count = 0;

      //  mCursor = mSqlitedb.rawQuery("select max(CAST(Doclineno AS INTEGER)) as Doclineno from picktaskdetail where taskNum='" + Globals.gTaskNo + "' ", null);
        mCursor = mSqlitedb.rawQuery("select max(CAST(Doclineno AS INTEGER)) as Doclineno from picktaskdetail where taskNum='" + Globals.gTaskNo + "' and Docno ='"+docNo+"' ", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Doclineno Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }
    public int mDoclineCount() {

        int Count = 0;

        mCursor = mSqlitedb.rawQuery("select max(CAST(Doclineno AS INTEGER)) as Doclineno from picktaskdetail where taskNum='" + Globals.gTaskNo + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Doclineno Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

    public int orgTranCount() {

        int Count = 0;

        mCursor = mSqlitedb.rawQuery("select max(CAST(orgTranlineno AS INTEGER)) as orgTranlineno from picktaskdetail ", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Doclineno Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

    public int docLineCount() {

        int doclineCount = 0;

        mCursor = mSqlitedb.rawQuery("select doclinecount as Doclineno from physicalcountSlot ", null);

        try {
            while (mCursor.moveToNext()) {
                doclineCount = Integer.parseInt(mCursor.getString(0));

                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.PHYSCIAL_COUNT_DOCLINECOUNT, String.valueOf(doclineCount+1));
                mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_SLOT_TABLE, cv, "", null);

                Log.i("PickTask Print Label", "PickTask Print Label updated successfully...");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Doclineno Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return doclineCount;
    }

    public int mPalLineCount() {

        int Count = 0;

        mCursor = mSqlitedb.rawQuery("select max(CAST(pltlineno AS INTEGER)) as pltlineno from receivetaskWHRPLT ", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

    public String getRTPalletFlag(String taskno, String tranlineno, int pallineno) {

        String sFlag = "";
        String palno ="";
        palno = String.valueOf(pallineno);

        mCursor = mSqlitedb.rawQuery("select Flag from receivetaskWHRPLT where taskno = '" + taskno + "'" +
                "and tasklineno = '" + tranlineno + "' and pltlineno = '" + palno + "'", null);

        try {
            while (mCursor.moveToNext()) {
                sFlag = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return sFlag;
    }

    public int getClosedCountandPartial() {

        int count = 0;


        mCursor = mSqlitedb.rawQuery("select * from physicalcountSlot where wmsstat = 'C' ", null);

        try {

            count = mCursor.getCount();

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection counted failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return count;
    }

    /*public String getSlotsCounted() {

        String slotcount = "";

        mCursor = mSqlitedb.rawQuery("select COUNT(*) from physicalcountSlot where wmsstat = 'C'", null);

        try {
            while (mCursor.moveToNext()) {
                slotcount = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotcount;
    }*/

    public String getSlotsCounted() {

        String slotcount = "";

        mCursor = mSqlitedb.rawQuery("select COUNT(*) from physicalcountDetail where slot = '"+Globals.gPCSlot+"' and Flag = 'Y' and posted = 'P' and userid = '"+ Globals.gUsercode +"'", null);

        try {
            while (mCursor.moveToNext()) {
                slotcount = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotcount;
    }


    public String getLockStatus(String pano) {

        String LockStatus = "";

        mCursor = mSqlitedb.rawQuery("select vplocked from repackFg where pano = '" + pano + "'", null);

        try {
            while (mCursor.moveToNext()) {
                LockStatus = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return LockStatus;
    }


    public void addAndUpdtRepackFG(String Item,String FinQty,String tranlineno,String pano,List<RepackFG>  repackFG) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(REPACKFG_PANO,pano);
            cv.put(REPACKFG_TRANLINENO,tranlineno);
            cv.put(REPACKFG_ITEM, Item);
            cv.put(REPACKFG_DESCRIP, repackFG.get(0).getREPACKFG_DESCRIP());
            cv.put(REPACKFG_UMEASUR, repackFG.get(0).getREPACKFG_UMEASUR());
            cv.put(REPACKFG_LOCTID, repackFG.get(0).getREPACKFG_LOCTID());
            cv.put(REPACKFG_LOTNO, repackFG.get(0).getREPACKFG_LOTNO());
            cv.put(REPACKFG_SERIAL, repackFG.get(0).getREPACKFG_SERIAL());
            cv.put(REPACKFG_QTYMADE, FinQty);
            cv.put(REPACKFG_COST, repackFG.get(0).getREPACKFG_COST());
            cv.put(REPACKFG_PADATE, repackFG.get(0).getREPACKFG_PADATE());
            cv.put(REPACKFG_PASTAT, repackFG.get(0).getREPACKFG_PASTAT());
            cv.put(REPACKFG_LCKSTAT, repackFG.get(0).getREPACKFG_LCKSTAT());
            cv.put(REPACKFG_LCKUSER, repackFG.get(0).getREPACKFG_LCKUSER());
            cv.put(REPACKFG_LCKDATE, repackFG.get(0).getREPACKFG_LCKDATE());
            cv.put(REPACKFG_LCKTIME, repackFG.get(0).getREPACKFG_LCKTIME());
            cv.put(REPACKFG_ADDUSER, repackFG.get(0).getREPACKFG_ADDUSER());
            cv.put(REPACKFG_ADDDATE, repackFG.get(0).getREPACKFG_ADDDATE());
            cv.put(REPACKFG_ADDTIME, repackFG.get(0).getREPACKFG_ADDTIME());
            cv.put(REPACKFG_PRODLBL, repackFG.get(0).getREPACKFG_PRODLBL());
            cv.put(REPACKFG_PACKCHG, repackFG.get(0).getREPACKFG_PACKCHG());
            cv.put(REPACKFG_WASHCHG, repackFG.get(0).getREPACKFG_WASHCHG());
            cv.put(REPACKFG_COUNTRYID, repackFG.get(0).getREPACKFG_COUNTRYID());
            cv.put(REPACKFG_VENDNO, repackFG.get(0).getREPACKFG_VENDNO());
            cv.put(REPACKFG_GRADE, repackFG.get(0).getREPACKFG_GRADE());
            cv.put(REPACKFG_PROJNO, repackFG.get(0).getREPACKFG_PROJNO());
            cv.put(REPACKFG_REMARKS, repackFG.get(0).getREPACKFG_REMARKS());
            cv.put(REPACKFG_LCSTQTY, repackFG.get(0).getREPACKFG_LCSTQTY());
            cv.put(REPACKFG_CASE_PL, repackFG.get(0).getREPACKFG_CASE_PL());
            cv.put(REPACKFG_PALNO, repackFG.get(0).getREPACKFG_PALNO());
            cv.put(REPACKFG_SETID, repackFG.get(0).getREPACKFG_SETID());
            cv.put(REPACKFG_WEIGHT, repackFG.get(0).getREPACKFG_WEIGHT());
            cv.put(REPACKFG_PALLET, repackFG.get(0).getREPACKFG_PALLET());
            cv.put(REPACKFG_ID_COL, repackFG.get(0).getREPACKFG_ID_COL());
            cv.put(REPACKFG_BINNO, repackFG.get(0).getREPACKFG_BINNO());
            cv.put(REPACKFG_POSTPRG, repackFG.get(0).getREPACKFG_POSTPRG());
            cv.put(REPACKFG_EXTPALLET, repackFG.get(0).getREPACKFG_EXTPALLET());
            cv.put(REPACKFG_EXTCUBE, repackFG.get(0).getREPACKFG_EXTCUBE());
            cv.put(REPACKFG_EXTWEIGHT, repackFG.get(0).getREPACKFG_EXTWEIGHT());
            cv.put(REPACKFG_BEXTLCST, repackFG.get(0).getREPACKFG_BEXTLCST());
            cv.put(REPACKFG_EXTLCST, repackFG.get(0).getREPACKFG_EXTLCST());
            cv.put(REPACKFG_BEXTFEES, repackFG.get(0).getREPACKFG_BEXTFEES());
            cv.put(REPACKFG_EXTFEES, repackFG.get(0).getREPACKFG_EXTFEES());
            cv.put(REPACKFG_TPALLET, repackFG.get(0).getREPACKFG_TPALLET());
            cv.put(REPACKFG_TCUBE, repackFG.get(0).getREPACKFG_TCUBE());
            cv.put(REPACKFG_TWEIGHT, repackFG.get(0).getREPACKFG_TWEIGHT());
            cv.put(REPACKFG_WLOTNO, repackFG.get(0).getREPACKFG_WLOTNO());
            cv.put(REPACKFG_ORIGTRANLN, repackFG.get(0).getREPACKFG_ORIGTRANLN());
            cv.put(REPACKFG_ORIGTRANL, repackFG.get(0).getREPACKFG_ORIGTRANL());
            cv.put(REPACKFG_ORIGDOCLN, repackFG.get(0).getREPACKFG_ORIGDOCLN());
            cv.put(REPACKFG_STKUMID, repackFG.get(0).getREPACKFG_STKUMID());
            cv.put(REPACKFG_USELOTS, repackFG.get(0).getREPACKFG_USELOTS());
            cv.put(REPACKFG_UMFACT, repackFG.get(0).getREPACKFG_UMFACT());
            cv.put(REPACKFG_WEIGHT1, repackFG.get(0).getREPACKFG_WEIGHT1());
            cv.put(REPACKFG_VOLUME, repackFG.get(0).getREPACKFG_VOLUME());
            cv.put(REPACKFG_CATCHWT, repackFG.get(0).getREPACKFG_CATCHWT());
            cv.put(REPACKFG_LOTREFID, repackFG.get(0).getREPACKFG_LOTREFID());
            cv.put(REPACKFG_LOTEXPL, repackFG.get(0).getREPACKFG_LOTEXPL());
            cv.put(REPACKFG_LINESPLIT, repackFG.get(0).getREPACKFG_LINESPLIT());
            cv.put(REPACKFG_TRKQTYPK, repackFG.get(0).getREPACKFG_TRKQTYPK());
            cv.put(REPACKFG_UPDFLAG, repackFG.get(0).getREPACKFG_UPDFLAG());
            cv.put(REPACKFG_VPLOCKED, repackFG.get(0).getREPACKFG_VPLOCKED());
            cv.put(REPACKFG_DECNUM, repackFG.get(0).getREPACKFG_DECNUM());
            cv.put(REPACKFG_USEDQTY, "");
            cv.put(REPACKFG_ADDFLAG, "1");

            mSqlitedb.insert(REPACKFG_TABLE, null, cv);
            LogfileCreator.mAppendLog("RepackFG inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "RepackFG insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }



    public String getCasePerPallet(String Item) {

        String LockStatus = "";

        mCursor = mSqlitedb.rawQuery("select case_pl from repackFg where item = '"+ Item + "' and qtymade = ''", null);

        try {
            while (mCursor.moveToNext()) {
                LockStatus = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return LockStatus;
    }

    public String getInvtype(String item) {

        String invtype = "";


        mCursor = mSqlitedb.rawQuery("select invtype from physicalcountICITEM where item = '"+ item +"'", null);
        try {
            while (mCursor.moveToNext()) {
                invtype = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return invtype;
    }

    public String getExportLotId(String lotno) {

        String lotid = "";

        mCursor = mSqlitedb.rawQuery("select ROWID from exportLot where exportLotno = '"+ lotno +"'", null);

        try {
            while (mCursor.moveToNext()) {
                lotid = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection of Lotid is failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return lotid;
    }

    /*public String getSlotsAssigned() {

        String slotassign = "";

        mCursor = mSqlitedb.rawQuery("select COUNT(*) from physicalcountSlot where wmsstat <> 'X'", null);

        try {
            while (mCursor.moveToNext()) {
                slotassign = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotassign;
    }*/

    public String getSlotsAssigned() {

        String slotassign = "";

        mCursor = mSqlitedb.rawQuery("select COUNT(*) from physicalcountDetail where slot = '"+Globals.gPCSlot+"' and userid = '"+ Globals.gUsercode +"'", null);

        try {
            while (mCursor.moveToNext()) {
                slotassign = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotassign;
    }

    public String getPhysicalCoutDetailCount() {

        String slotassign = "";
             if (!Globals.FROMHIDE){
                 mCursor = mSqlitedb.rawQuery("select COUNT(*) from physicalcountDetail where slot = '"+ Globals.gPCSlot +"'and userid = '"+ Globals.gUsercode +"'", null);
             }else {
                 mCursor = mSqlitedb.rawQuery("select COUNT(*) from physicalcountDetail where slot = '"+ Globals.gPCSlot +"'and Flag='Y' and userid = '"+ Globals.gUsercode +"'", null);
             }

        try {
            while (mCursor.moveToNext()) {
                slotassign = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotassign;
    }

    public double getBalanceQty(String taskno, String tranlineno) {

        double dQty = 0.0;
        String Qty="";

        if (Globals.gCatchwt == "T"){
            mCursor = mSqlitedb.rawQuery("select tqty from receivetaskWHRPLT where taskno = '" + taskno + "'" +
                    "and tasklineno = '" + tranlineno + "' and Flag ='N'", null);
        } else{
            mCursor = mSqlitedb.rawQuery("select trkqty from receivetaskWHRPLT where taskno = '" + taskno + "'" +
                    "and tasklineno = '" + tranlineno + "' and Flag ='N'", null);
        }

        try {
            while (mCursor.moveToNext()) {
                Qty = mCursor.getString(0);
                if (Qty !="null") {
                    dQty = Double.parseDouble(Qty);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return dQty;
    }

    public double getRTDetailQty(String taskno, String tranlineno) {

        double dQty = 0.0;
        String Qty = "";

        if (Globals.gCatchwt == "T"){
            mCursor = mSqlitedb.rawQuery("select SUM(tqty) as tqty from receivetaskWHRPLT where taskno = '" + taskno + "'" +
                    "and tasklineno = '" + tranlineno + "' and Flag ='Y'", null);
        } else{
            mCursor = mSqlitedb.rawQuery("select SUM(trkqty) as trkqty from receivetaskWHRPLT where taskno = '" + taskno + "'" +
                    "and tasklineno = '" + tranlineno + "' and Flag ='Y'", null);
        }

        try {
            while (mCursor.moveToNext()) {
                Qty = mCursor.getString(0);
                if (Qty !="null") {
                    dQty = Double.parseDouble(mCursor.getString(0));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection tqty failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return dQty;
    }

    public int mRowNoCount() {

        int Count = 0;

        mCursor = mSqlitedb.rawQuery("select max(CAST(rowNo AS INTEGER)) as rowNo from picktaskdetail where taskNum='" + Globals.gTaskNo + "'", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Doclineno Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

    public String mTotWeight(String strFlag) {

        String mTotWight = "";

        mCursor = mSqlitedb.rawQuery("select sum(Weight) as Weight from picktaskdetail where (Flag = 'Y' OR Flag = 'H') and taskNum = '" + Globals.gTaskNo + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mTotWight = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotWight;
    }

    public void getStopList(String route) {

        String mTaskNo = "";
        mCursor = mSqlitedb.rawQuery("select Trailer, Dock from loadpickpalletWHITRL where Route = '"+ route +"'", null);

        try {
            while (mCursor.moveToNext()) {
                Globals.gLPPTrailer = mCursor.getString(0);
                Globals.gLPPDock = mCursor.getString(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection LPP Stop list failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public String getTaskNo(String pallet) {

        String mTaskNo = "";
        mCursor = mSqlitedb.rawQuery("select Taskno from loadpickpalletWHIPLT where Palno = '"+ pallet +"'", null);

        try {
            while (mCursor.moveToNext()) {
                mTaskNo = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection LPP TaskNo failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTaskNo;
    }

    public String getRoute(String pallet) {

        String mRoute = "";

        mCursor = mSqlitedb.rawQuery("SELECT a.Route from loadpickpalletDetails a " +
                " INNER JOIN loadpickpalletWHIPLT b on b.Taskno = a.Taskno " +
                " WHERE b.wmsDate = '" + Globals.gLPPWorkDate + "' and b.Palno = '" + pallet + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mRoute = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection LPP Route failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mRoute;
    }

    public String SelectPallet(String taskNo) {

        String mPallet = "";

        mCursor = mSqlitedb.rawQuery("select Palno from picktaskWHIPTL where Taskno = '" + taskNo + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mPallet = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Palno failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mPallet;
    }
    public String mTotCase(String strFlag) {

        String mTotCase = "";

        mCursor = mSqlitedb.rawQuery("select sum(Tqty) as Tqty from picktaskdetail where (Flag = 'Y' OR Flag = 'H') and taskNum = '" + Globals.gTaskNo + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                mTotCase = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCase;
    }

    public String mCasecnt(String strFlag, String Catchwt) {

        String mTotCasecnt = "";
        if (Catchwt.equals("0")) {
            mCursor = mSqlitedb.rawQuery("select sum(tqty) as tqty from receivetaskWHRPLT where Flag = '" + strFlag + "' and taskno = '" + Globals.gRTTaskNo + "'", null);
        }else{
            mCursor = mSqlitedb.rawQuery("select sum(trkqty) as trkqty from receivetaskWHRPLT where Flag = '" + strFlag + "' and taskno = '" + Globals.gRTTaskNo + "' ", null);
        }
        try {
            while (mCursor.moveToNext()) {
                mTotCasecnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCasecnt;
    }

    public String mRTCasecnt(String strFlag, String Catchwt) {

        String mTotCasecnt = "";
        if (Catchwt.equals("0")) {
            mCursor = mSqlitedb.rawQuery("select sum(tqty) as tqty from receivetaskWHRPLT where Flag = '" + strFlag + "' and  " +
                    "tasklineno='" + Globals.gRTTranline + "' and taskno = '" + Globals.gRTTaskNo + "' ", null);
        }else{
            mCursor = mSqlitedb.rawQuery("select sum(trkqty) as trkqty from receivetaskWHRPLT where Flag = '" + strFlag + "' and " +
                    "tasklineno='" + Globals.gRTTranline + "' taskno = '" + Globals.gRTTaskNo + "' ", null);
        }
        try {
            while (mCursor.moveToNext()) {
                mTotCasecnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCasecnt;
    }

    public String mTotCasecnt(String Catchwt) {

        String mTotCasecnt = "";
        mCursor = mSqlitedb.rawQuery("select tqtyinc as tqtyinc from receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "' and " +
                "tranlineno='" + Globals.gRTTranline + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                mTotCasecnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Qty selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCasecnt;
    }

    public String mTotRTCasecnt(String Catchwt) {

        String mTotCasecnt = "";
        mCursor = mSqlitedb.rawQuery("select sum(tqtyinc) as tqtyinc from receivetaskdetail where taskno = '" + Globals.gRTTaskNo + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mTotCasecnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Qty selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCasecnt;
    }


    public String mTotMTCasecnt(String TaskNo) {

        String mTotCasecnt = "";
        mCursor = mSqlitedb.rawQuery("select sum(tqtyrq) as tqtyrq from movetaskdetail where taskno = '" + TaskNo + "'", null);


        try {
            while (mCursor.moveToNext()) {
                mTotCasecnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Qty selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCasecnt;
    }


    public String mMTCasecnt(String TaskNo) {

        String mTotCasecnt = "";
        mCursor = mSqlitedb.rawQuery("select sum(tqtyrq) as tqtyrq from movetaskdetail where taskno = '" + TaskNo + "' and flag ='Y'", null);


        try {
            while (mCursor.moveToNext()) {
                mTotCasecnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Qty selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotCasecnt;
    }


    public String mTotPalcnt() {

        String mTotpalcnt = "";

       mCursor = mSqlitedb.rawQuery("select count(*) as palcount from receivetaskWHRPLT where taskno = '" + Globals.gRTTaskNo + "' ", null);
       /* mCursor = mSqlitedb.rawQuery("select count(*) as palcount from receivetaskWHRPLT where taskno = '" + Globals.gRTTaskNo + "' and " +
                "tranlineno='" + Globals.gRTTranline + "' ", null);*/

        try {
            while (mCursor.moveToNext()) {
                mTotpalcnt = mCursor.getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotpalcnt;
    }

    public int mTotPalcount() {

        int mTotpalcnt = 0;

        mCursor = mSqlitedb.rawQuery("select count(*) as palcount from receivetaskWHRPLT where taskno = '" + Globals.gRTTaskNo + "' ", null);

        try {
            while (mCursor.moveToNext()) {
                mTotpalcnt = Integer.valueOf(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotpalcnt;
    }

    public String mPalcnt(String strFlag) {

        String mTotpalcnt = "";

        mCursor = mSqlitedb.rawQuery("select count(*) as palcount from receivetaskWHRPLT where Flag = '" + strFlag + "' and taskno = '" + Globals.gRTTaskNo + "'", null);

        try {
            while (mCursor.moveToNext()) {
                mTotpalcnt = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mTotpalcnt;
    }

    public List<String> getLoadTypeList() {

        List<String> lstLoadType = new ArrayList<String>();
        String query = "select descrip from " + RECEIVE_TASK_LOAD_TYPE +" where descrip <> 'Blank'";
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                lstLoadType.add(mCursor.getString(0).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return lstLoadType;
    }

    public List<String> getWlotNoList() {

        List<String> wLotNoList = new ArrayList<String>();
        /*String query = "select wlotno from repackIngredient";*/
        String query = "select lotrefid from repackIngredient  where tranlineno >'' ";
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                wLotNoList.add(mCursor.getString(0).trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return wLotNoList;
    }


    public void RevertPickTaskDetail(picktaskdetail tpicktaskdetail, String itemNum, String tranlineNum, String doclineNum,  String iSEdited) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(WMSDbHelper.DETAIL_SLOT,
                    tpicktaskdetail.getSlot());

            if (Globals.gCatchwt == "T"){
                cv.put(WMSDbHelper.DETAIL_TQTY,
                        tpicktaskdetail.getorgTQty());
                       // String.valueOf(dQty));
                cv.put(WMSDbHelper.DETAIL_ORGTQTY,
                        tpicktaskdetail.getorgTQty());
                       // String.valueOf(dQty));
                cv.put(WMSDbHelper.DETAIL_TRKQTY,
                        tpicktaskdetail.getTrkQty());
            }else{
                cv.put(WMSDbHelper.DETAIL_TQTY,
                        tpicktaskdetail.getTQty());
                cv.put(WMSDbHelper.DETAIL_ORGTQTY,
                        tpicktaskdetail.getorgTQty());
                cv.put(WMSDbHelper.DETAIL_TRKQTY,
                        tpicktaskdetail.getTrkQty());
                       // String.valueOf(dQty));
            }
           /* cv.put(WMSDbHelper.DETAIL_ORGTQTY,
                    tpicktaskdetail.getorgTQty());*/
            cv.put(WMSDbHelper.DETAIL_ORGTRKQTY,
                    tpicktaskdetail.getorgTrkQty());
            cv.put(WMSDbHelper.DETAIL_UOM,
                    tpicktaskdetail.getUom());
            cv.put(WMSDbHelper.DETAIL_ITEM,
                    tpicktaskdetail.getItem());
            cv.put(WMSDbHelper.DETAIL_DESC,
                    tpicktaskdetail.getDescrip());
            cv.put(WMSDbHelper.DETAIL_WLOTNO,
                    tpicktaskdetail.getWLotNo());
            if(tpicktaskdetail.getLotNo()!=null&&tpicktaskdetail.getLotNo()!=""){
                cv.put(WMSDbHelper.DETAIL_LOTNO,
                        tpicktaskdetail.getLotNo());
            }
            cv.put(WMSDbHelper.DETAIL_TRANLINENO,
                    tranlineNum);
            cv.put(WMSDbHelper.DETAIL_ORGTRANLINENO,
                    tpicktaskdetail.getorgTranlineno());
            /*cv.put(WMSDbHelper.DETAIL_DOCTYPE,
                    tpicktaskdetail.getDoctype());
            cv.put(WMSDbHelper.DETAIL_DOCNO,
                    tpicktaskdetail.getDocno());*/
            cv.put(WMSDbHelper.DETAIL_DOCLINENO,
                    doclineNum);
            cv.put(WMSDbHelper.DETAIL_ORGDOCLINENO,
                    tpicktaskdetail.getorgDoclineno());
            cv.put(WMSDbHelper.DETAIL_DOCSTAT,
                    tpicktaskdetail.getDocstat());
            cv.put(WMSDbHelper.DETAIL_WEIGHT,
                    tpicktaskdetail.getWeight());
            cv.put(WMSDbHelper.DETAIL_CATCHWT,
                    tpicktaskdetail.getCatchwt());
            cv.put(WMSDbHelper.DETAIL_VOLUME,
                    tpicktaskdetail.getVolume());
            cv.put(WMSDbHelper.DETAIL_DECNUM,
                    tpicktaskdetail.getdecnum());
            cv.put(WMSDbHelper.DETAIL_STKUMID,
                    tpicktaskdetail.getStkumid());
            cv.put(WMSDbHelper.DETAIL_UMFACT,
                    tpicktaskdetail.getUmfact());
            cv.put(WMSDbHelper.DETAIL_TSHIPPED,
                    tpicktaskdetail.getTshipped());
            cv.put(WMSDbHelper.DETAIL_TRKSHIPPED,
                    tpicktaskdetail.getTrkshipped());
            cv.put(WMSDbHelper.DETAIL_LBSHP,
                    tpicktaskdetail.getLbshp());
            cv.put(WMSDbHelper.DETAIL_PICKDURATION,
                    tpicktaskdetail.getpickDuration());
            cv.put(WMSDbHelper.DETAIL_LINESPLIT,
                    Globals.gLineSplit);
            cv.put(WMSDbHelper.DETAIL_FLAG, "N");
            //cv.put(WMSDbHelper.DETAIL_FLAG, tpicktaskdetail.getFlag());
            cv.put(WMSDbHelper.DETAIL_STAGINGSLOT,
                    tpicktaskdetail.getStagingSlot() );
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_ITEM,tpicktaskdetail.getSubItem());
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_TRANNO,tpicktaskdetail.getSubTranNo());
            /*cv.put(WMSDbHelper.DETAIL_IS_SUBSTITUTED,tpicktaskdetail.getIsSubItem());*/
            cv.put(WMSDbHelper.DETAIL_TASKNO,tpicktaskdetail.getDetailsTaskNum());
            cv.put(WMSDbHelper.DETAIL_ISEDITED,"");

            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv,  " taskNum = '" + Globals.gTaskNo + "' and Item = '"
                    + itemNum + "' and Tranlineno='" + tranlineNum + "' ", null);

            Log.i("PickTask Detail", "PickTask Detail record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updatePickTaskDetail(picktaskdetail tpicktaskdetail, String itemNum, String tranlineNum, String doclineNum, double dQty, String iSEdited) {

        int count = 0;
        boolean isAvail=false;
        try {


            ContentValues cv = new ContentValues();

            cv.put(WMSDbHelper.DETAIL_SLOT,
                    tpicktaskdetail.getSlot());

            if (Globals.gCatchwt == "T"){
                cv.put(WMSDbHelper.DETAIL_TQTY,
                        String.valueOf(dQty));
                if (dQty < Double.parseDouble(tpicktaskdetail.getorgTQty())){
                    cv.put(WMSDbHelper.DETAIL_ORGTQTY, String.valueOf(dQty));
                }
               // cv.put(WMSDbHelper.DETAIL_ORGTQTY, String.valueOf(dQty));

                cv.put(WMSDbHelper.DETAIL_TRKQTY,
                        tpicktaskdetail.getTrkQty());
            }else{
                cv.put(WMSDbHelper.DETAIL_TQTY,
                        tpicktaskdetail.getTQty());
                cv.put(WMSDbHelper.DETAIL_ORGTQTY,
                        tpicktaskdetail.getTQty());
                cv.put(WMSDbHelper.DETAIL_TRKQTY,
                        String.valueOf(dQty));
            }
           /* cv.put(WMSDbHelper.DETAIL_ORGTQTY,
                    tpicktaskdetail.getorgTQty());*/
            cv.put(WMSDbHelper.DETAIL_ORGTRKQTY,
                    tpicktaskdetail.getorgTrkQty());
            cv.put(WMSDbHelper.DETAIL_UOM,
                    tpicktaskdetail.getUom());
            cv.put(WMSDbHelper.DETAIL_ITEM,
                    tpicktaskdetail.getItem());
            cv.put(WMSDbHelper.DETAIL_DESC,
                    tpicktaskdetail.getDescrip());
            cv.put(WMSDbHelper.DETAIL_WLOTNO,
                    tpicktaskdetail.getWLotNo());
            if(tpicktaskdetail.getLotNo()!=null&&tpicktaskdetail.getLotNo()!=""){
                cv.put(WMSDbHelper.DETAIL_LOTNO,
                        tpicktaskdetail.getLotNo());
            }
            cv.put(WMSDbHelper.DETAIL_TRANLINENO, tranlineNum);
            cv.put(WMSDbHelper.DETAIL_ORGTRANLINENO,
                    tpicktaskdetail.getorgTranlineno());
            /*cv.put(WMSDbHelper.DETAIL_DOCTYPE,
                    tpicktaskdetail.getDoctype());
            cv.put(WMSDbHelper.DETAIL_DOCNO,
                    tpicktaskdetail.getDocno());*/
            cv.put(WMSDbHelper.DETAIL_DOCLINENO,
                    doclineNum);
            cv.put(WMSDbHelper.DETAIL_ORGDOCLINENO,
                    tpicktaskdetail.getorgDoclineno());
            cv.put(WMSDbHelper.DETAIL_DOCSTAT,
                    tpicktaskdetail.getDocstat());
            cv.put(WMSDbHelper.DETAIL_WEIGHT,
                    tpicktaskdetail.getWeight());
            cv.put(WMSDbHelper.DETAIL_CATCHWT,
                    tpicktaskdetail.getCatchwt());
            cv.put(WMSDbHelper.DETAIL_VOLUME,
                    tpicktaskdetail.getVolume());
            cv.put(WMSDbHelper.DETAIL_DECNUM,
                    tpicktaskdetail.getdecnum());
            cv.put(WMSDbHelper.DETAIL_STKUMID,
                    tpicktaskdetail.getStkumid());
            cv.put(WMSDbHelper.DETAIL_UMFACT,
                    tpicktaskdetail.getUmfact());
            cv.put(WMSDbHelper.DETAIL_TSHIPPED,
                    tpicktaskdetail.getTshipped());
            cv.put(WMSDbHelper.DETAIL_TRKSHIPPED,
                    tpicktaskdetail.getTrkshipped());
            cv.put(WMSDbHelper.DETAIL_LBSHP,
                    tpicktaskdetail.getLbshp());
            cv.put(WMSDbHelper.DETAIL_PICKDURATION,
                    tpicktaskdetail.getpickDuration());
            cv.put(WMSDbHelper.DETAIL_LINESPLIT,tpicktaskdetail.getLinesplit());
           // cv.put(WMSDbHelper.DETAIL_LINESPLIT, Globals.gLineSplit);
            cv.put(WMSDbHelper.DETAIL_FLAG, "Y");
            //cv.put(WMSDbHelper.DETAIL_FLAG, tpicktaskdetail.getFlag());
            cv.put(WMSDbHelper.DETAIL_STAGINGSLOT,
                    tpicktaskdetail.getStagingSlot() );
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_ITEM,tpicktaskdetail.getSubItem());
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_TRANNO,tpicktaskdetail.getSubTranNo());
            /*cv.put(WMSDbHelper.DETAIL_IS_SUBSTITUTED,tpicktaskdetail.getIsSubItem());*/
            cv.put(WMSDbHelper.DETAIL_TASKNO,tpicktaskdetail.getDetailsTaskNum());
            cv.put(WMSDbHelper.DETAIL_ISEDITED,iSEdited);
            cv.put(WMSDbHelper.DETAIL_CHGQTY, "");
            cv.put(WMSDbHelper.DETAIL_PICKED, "Y");

            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv,  " taskNum = '" + Globals.gTaskNo + "' and Item = '"
                    + itemNum + "' and Tranlineno='" + tranlineNum + "' ", null);


           /* mCursor = mSqlitedb.rawQuery("select * from picktaskrevertolddata where taskNum = '" +  Globals.gTaskNo + "' " +
                    "and  Item = '" + itemNum + "' and  Tranlineno = '" + tranlineNum + "' and WLotNo<>'' and LotNo <> ''", null);*/

            mCursor = mSqlitedb.rawQuery("select * from picktaskrevertolddata where taskNum = '" +  Globals.gTaskNo + "' " +
                    "and  Item = '" + itemNum + "' and  LotNo = '" + tpicktaskdetail.getLotNo() + "'", null);

          count=mCursor.getCount();
            if (count != 0) {
                isAvail = true;
            }

            if (isAvail) {
                mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv, " taskNum = '" + Globals.gTaskNo + "' and Item = '"
                        + itemNum + "' and LotNo='" + tpicktaskdetail.getLotNo() + "' ", null);
            }else {
                cv.put(WMSDbHelper.DETAIL_TEMPQTY, "");
                mSqlitedb.insert(PICK_TASK_REVEROLDDATA, null, cv);
            }
           //

            Log.i("PickTask Detail", "PickTask Detail record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }


    public void updateTranPickDetails(String itemNum, String tranlineNum, String doclineNum,String subItem,String subTranNo,String docNo,String docType) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(WMSDbHelper.DETAIL_TRANLINENO,
                    tranlineNum);
            cv.put(WMSDbHelper.DETAIL_ORGTRANLINENO,
                    tranlineNum);
            cv.put(WMSDbHelper.DETAIL_DOCLINENO,
                    doclineNum);
            cv.put(WMSDbHelper.DETAIL_ORGDOCLINENO,
                    doclineNum);
            cv.put(WMSDbHelper.DETAIL_IS_SUBSTITUTED,"True");
            cv.put(WMSDbHelper.DETAIL_ORG_SOITEM,subItem);
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_TRANNO,subTranNo);
            cv.put(WMSDbHelper.DETAIL_DOCNO,docNo);
            cv.put(WMSDbHelper.DETAIL_DOCTYPE,docType);


            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, " Item = '" + itemNum + "'", null);

            Log.i("PickTask Detail", "PickTask Detail Tran record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateEditQty(String itemNum, String tranlineNum,String Qty,String diffQty) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.DETAIL_TQTY, Qty);
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_ITEM,diffQty);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, "Item = '"
                    + itemNum + "' and Tranlineno='" + tranlineNum + "' and tasknum= '" + Globals.gTaskNo + "'", null);

            Log.i("PickTask Detail", "PickTask Detail record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public String getDiffQty(String Item, String tranNo) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select subItem from picktaskdetail where Item = '" + Item + "' " +
                "and  Tranlineno = '" + tranNo + "' and  tasknum = '" + Globals.gTaskNo + "'", null);
        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
                /*if (flag.equals("Y")){
                    Tqty = mCursor.getString(0);
                } else {
                    Tqty = mCursor.getString(1);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public void updateStagingSlotDetails(String itemNum, String tranlineNum, String stagingSlot) {

        try {

            ContentValues cv = new ContentValues();

            cv.put(WMSDbHelper.DETAIL_STAGINGSLOT,
                    stagingSlot);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, " Item = '" + itemNum + "' and " + "Tranlineno='" + tranlineNum + "'", null);

            Log.i("PickTask Detail", "Staging slot  record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Staging slot  update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateReceiveTaskWHRPLT(receivetaskWHRPLT treceivetaskWHRPLT, int orgPltlineno, double dQty, String taskno, String tranlineno, String prtPaltag) {

        try {

            ContentValues cv = new ContentValues();
            String pltLineno  = String.valueOf(orgPltlineno);

            if (Globals.gCatchwt == "T"){
                cv.put(WMSDbHelper.RTWHRPLT_TQTY,
                        String.valueOf(dQty));
                cv.put(WMSDbHelper.RTWHRPLT_TRKQTY,
                        "0.00");
            }else{
                cv.put(WMSDbHelper.RTWHRPLT_TQTY,
                        "0.00");
                cv.put(WMSDbHelper.RTWHRPLT_TRKQTY,
                        String.valueOf(dQty));
            }
            cv.put(WMSDbHelper.RTWHRPLT_PRTPLTTAG, prtPaltag);
            cv.put(WMSDbHelper.RTWHRPLT_SLOT, treceivetaskWHRPLT.getSlot());
            cv.put(WMSDbHelper.RTWHRPLT_FLAG, "Y");
            cv.put(WMSDbHelper.RTWHRPLT_GTIN, treceivetaskWHRPLT.getgTin());

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_WHRPLT, cv, "taskno = '"
                    + taskno + "' and tasklineno = '" + tranlineno + "' and pltlineno = '" + pltLineno + "'", null);

            Log.i("PickTask Detail", "PickTask Detail record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void splitReceiveTaskWHRPLT(receivetaskWHRPLT treceivetaskWHRPLT, int orgPltlineno, double dQty, String Flag, String prtPlttag) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(RTWHRPLT_TASKNO, treceivetaskWHRPLT.gettaskno());
            cv.put(RTWHRPLT_TASKLINENO, treceivetaskWHRPLT.gettasklineno());
            cv.put(RTWHRPLT_PLTLINENO, String.valueOf(orgPltlineno));
            if (Globals.gCatchwt == "T"){
                cv.put(RTWHRPLT_TQTY, String.valueOf(dQty));
                cv.put(RTWHRPLT_TRKQTY, "0.00");
            } else {
                cv.put(RTWHRPLT_TQTY, "0.00");
                cv.put(RTWHRPLT_TRKQTY, String.valueOf(dQty));
            }
            cv.put(RTWHRPLT_PLTSTAT, treceivetaskWHRPLT.getpltstat());
            cv.put(RTWHRPLT_PRTPLTTAG, prtPlttag);
            cv.put(RTWHRPLT_SLOT, treceivetaskWHRPLT.getSlot());
            cv.put(RTWHRPLT_FLAG, Flag);
            cv.put(RTWHRPLT_GTIN, treceivetaskWHRPLT.getgTin());

            mSqlitedb.insert(RECEIVE_TASK_WHRPLT, null, cv);
            LogfileCreator.mAppendLog("Receive Task WHRPLT: Receive Task WHRPLT inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Receive Task WHRPLT data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void splitPhysicalCountDetail(physicalcountDetail tphysicalcountDetail, int lineno, double dQty) {

        try {
            ContentValues cv = new ContentValues();

            cv.put(PC_DETAIL_SLOT, tphysicalcountDetail.getslot());
            cv.put(PC_DETAIL_COUNTID, tphysicalcountDetail.getcountid());
            cv.put(PC_DETAIL_PAGE, tphysicalcountDetail.getpage());
            cv.put(PC_DETAIL_DOCLINENO, String.valueOf(lineno));
            cv.put(PC_DETAIL_LOCTID, tphysicalcountDetail.getloctid());
            cv.put(PC_DETAIL_ITEM, tphysicalcountDetail.getitem());
            cv.put(PC_DETAIL_WLOTNO, tphysicalcountDetail.getwlotno());
            cv.put(PC_DETAIL_LOTREFID, tphysicalcountDetail.getlotrefid());
            cv.put(PC_DETAIL_UMEASUR, tphysicalcountDetail.getumeasur());
            cv.put(PC_DETAIL_TCOUNTQTY, String.valueOf(dQty));
            cv.put(PC_DETAIL_TQTY, "0.00000");
            cv.put(PC_DETAIL_ITMDESC, tphysicalcountDetail.getitmdesc());
            cv.put(PC_DETAIL_PCKDESC, tphysicalcountDetail.getitmdesc());
            cv.put(PC_DETAIL_ITEMSHOW, tphysicalcountDetail.getitemShow());
            cv.put(PC_DETAIL_INVTYPE, tphysicalcountDetail.getinvtype());
            cv.put(PC_DETAIL_SURPRISADD, "1");
            cv.put(PC_DETAIL_COUNTED, String.valueOf(dQty));
            cv.put(PC_DETAIL_WMSSTAT, "C");
            cv.put(PC_DETAIL_USERID, Globals.gUsercode);
            cv.put(PC_DETAIL_ROWNO, Globals.gPCDetailRowCount);
            cv.put(PC_DETAIL_FLAG, "Y");
            Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;

            mSqlitedb.insert(PHYSCIAL_COUNT_DETAIL_TABLE, null, cv);
            LogfileCreator.mAppendLog("Physical Count Detail: Physical Count Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Physical Count ICITEM data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void UpdatePhysicalCountDetail(physicalcountDetail tphysicalcountDetail, String item, String wlotno, String umeasur, double dQty,String updatedNo) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(PC_DETAIL_WLOTNO, tphysicalcountDetail.getwlotno());
            cv.put(PC_DETAIL_LOTREFID, tphysicalcountDetail.getlotrefid());
            cv.put(PC_DETAIL_UMEASUR, tphysicalcountDetail.getumeasur());
            cv.put(PC_DETAIL_TCOUNTQTY, String.valueOf(dQty));
            cv.put(PC_DETAIL_TQTY, tphysicalcountDetail.gettqty());
            cv.put(PC_DETAIL_INVTYPE, tphysicalcountDetail.getinvtype());
            cv.put(PC_DETAIL_COUNTED, String.valueOf(dQty));
            cv.put(PC_DETAIL_WMSSTAT, "C");
            cv.put(PC_DETAIL_FLAG, "Y");
            cv.put(PC_DETAIL_UPDATEDNO, updatedNo);

            mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TABLE, cv, "item = '"
                    + item + "' and wlotno = '" + wlotno + "' and umeasur = '" + umeasur + "'", null);

            Log.i("Physical Count Detail", "Physical Count Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Physical Count Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updatedPhysicalCountUpdatedNo(String updatedNo,String item,String lotrefid) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(PC_DETAIL_UPDATEDNO, updatedNo);

            mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TABLE, cv, "item = '"
                    + item + "' and lotrefid = '" + lotrefid + "'", null);
            mSqlitedb.update(WMSDbHelper.PHYSCIAL_COUNT_DETAIL_TRAN_TABLE, cv, "item = '"
                    + item + "' and lotrefid = '" + lotrefid + "'", null);


            Log.i("Physical Count Detail", "Physical Count Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Physical Count Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateReceiveTaskDetail(String taskno, String Tranlineno, double qty,String DefSlot) {
        try {

            ContentValues cv = new ContentValues();

                cv.put(RTD_TQTYREC, String.valueOf(qty));
                cv.put(RTD_COLLECTION,DefSlot );
                //cv.put(RTD_TRKQTYREC,oldQty);
               //cv.put(RTD_TQTYINC,String.valueOf(qty));
                //cv.put(RTD_TQTYINC, qty);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_DETAIL, cv, "Taskno = '"
                    + taskno + "' and tranlineno='" + Tranlineno + "'", null);

            Log.i("Receive Task Detail", "Receive Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Receive Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateReceiveTaskDetailONHOLD(String taskno, String Tranlineno, double qty) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(RTD_TQTYREC, String.valueOf(qty));
            cv.put(RTD_TQTYINC,String.valueOf(qty));
            //cv.put(RTD_TQTYINC, qty);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_DETAIL, cv, "Taskno = '"
                    + taskno + "' and tranlineno='" + Tranlineno + "'", null);

            Log.i("Receive Task Detail", "Receive Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Receive Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void UpdateLoaded(String taskno) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.LPP_DETAIL_LOADED,
                    "1");

            mSqlitedb.update(WMSDbHelper.LPP_DETAILS, cv, "Taskno = '"
                    + taskno + "'", null);

            Log.i("Load Pick Pallet Detail", "Load Pick Pallet Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Load Pick Pallet update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void UpdateTaskStatus(picktasklist tpicktasklist, String taskno) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.STATUS,
                    "ONHOLD");

            mSqlitedb.update(WMSDbHelper.PICK_TASK_LIST_TABLE, cv, "taskNo = '"
                    + taskno + "'", null);

            Log.i("PickTask List", "PickTask List record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask List update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void UpdateMoveTaskStatus(String taskno,String Status) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.MT_STATUS,
                    Status);

            mSqlitedb.update(WMSDbHelper.Move_TASK_LIST_TABLE, cv, "taskNo = '"
                    + taskno + "'", null);

            Log.i("MoveTask List", "MoveTask List record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "MoveTask List update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }
    public void UpdateReceiveTaskStatus(String taskno,String Status) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RT_STATUS,
                    Status);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_LIST, cv, "taskNo = '"
                    + taskno + "'", null);

            Log.i("ReceiveTask List", "ReceiveTask List record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask List update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void UpdateSubItem(String itemNum,String subItem,String tranlineno) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.DETAIL_FLAG,"S");
            cv.put(WMSDbHelper.DETAIL_SUBSTITUTED_ITEM,subItem);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, "Item = '"
                    + itemNum + "' and Tranlineno='" + tranlineno + "' and taskNum = '" + Globals.gTaskNo + "'", null);

            Log.i("ReceiveTask List", "Sub Item record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask List update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }


    public notificationcount mGetTaskNotificationData() {
        notificationcount notification = null;

        mCursor = mSqlitedb.rawQuery("select * from notificationcount ", null);
        try {
            while (mCursor.moveToNext()) {
                notification = new notificationcount();
                //tpicktasklist.setStatus(mCursor.getString(mCursor.getColumnIndex(STATUS)));
                notification.setPickTask(mCursor.getInt(mCursor.getColumnIndex(PICKTASK_COUNT)));
                notification.setReceiveTask(mCursor.getInt(mCursor.getColumnIndex(RECEIVETASK_COUNT)));
                notification.setPhysicalCount(mCursor.getInt(mCursor.getColumnIndex(PHYSICALCOUNT_COUNT)));
                notification.setMoveTask(mCursor.getInt(mCursor.getColumnIndex(MOVETASK_COUNT)));
                notification.setLoadPickPallets(mCursor.getInt(mCursor.getColumnIndex(LOADPALLETS_COUNT)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Task Notification selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return notification;
    }

    public menulist mGetMenuListData() {
        menulist menulist = null;

        mCursor = mSqlitedb.rawQuery("select * from menulist ", null);
        try {
            while (mCursor.moveToNext()) {
                menulist = new menulist();

                menulist.setPickTask(mCursor.getInt(mCursor.getColumnIndex(PICKTASK_MENU)));
                menulist.setReceiveTask(mCursor.getInt(mCursor.getColumnIndex(RECEIVETASK_MENU)));
                menulist.setPhysicalCount(mCursor.getInt(mCursor.getColumnIndex(PHYSICALCOUNT_MENU)));
                menulist.setMoveTask(mCursor.getInt(mCursor.getColumnIndex(MOVETASK_MENU)));
                menulist.setLoadPickPallets(mCursor.getInt(mCursor.getColumnIndex(LOADPALLETS_MENU)));
                menulist.setMoveManually(mCursor.getInt(mCursor.getColumnIndex(MOVEMANUALLY_MENU)));
                menulist.setBreakerUomUtility(mCursor.getInt(mCursor.getColumnIndex(BREAKERUOMUTILITY_MENU)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Menu List selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return menulist;
    }

    public String mGetSessionId() {

        String mSession="";
        String query = "select * from " + SESSION_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                mSession = mCursor.getString(1);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load SessionId ";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mSession;
    }


    public String mGetResetPalletTag() {

        String mResetPallet="";
        String query = "select * from " + SESSION_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                mResetPallet = mCursor.getString(2);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load SessionId ";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mResetPallet;
    }

    public String getSoftKeyCheckStatus() {

        String mSession="";
        String query = "select softKeyboard from " + CONFIGSETTINGS_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                mSession = mCursor.getString(1);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load SessionId ";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mSession;
    }

    public String getUserName() {

        String mSession="";
        String query = "select username from " + CONFIGSETTINGS_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                mSession = mCursor.getString(1);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load SessionId ";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mSession;
    }


    public int getMaxUpdatedNo() {

        int updatedno = 0;

        mCursor = mSqlitedb.rawQuery("select max(cast(UpdatedNo AS Integer)) as UpdatedNo from physicalcountDetail" ,null);
        //mCursor = mSqlitedb.rawQuery("select max(UpdatedNo) from physicalcountDetail" ,null);
        try {
            while (mCursor.moveToNext()) {
                updatedno = mCursor.getInt(0);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load SessionId ";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return updatedno;
    }

    public String PickedQty(String catchwt, String tranlineno) {

        String mPickedQty="";
        String query = "select TQty,TrkQty from picktaskdetail where Tranlineno='" + tranlineno + "'";
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                if (catchwt == "T") {
                    mPickedQty = mCursor.getString(0);
                } else{
                    mPickedQty = mCursor.getString(1);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load SessionId ";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mPickedQty;
    }

    public ArrayList<company> getCompanyList() {

        ArrayList<company> lstCompany = new ArrayList<company>();
        company comp = null;

        String query = "select * from " + COMPANY_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                comp = new company();
                comp.setCompanyID(mCursor.getString(mCursor.getColumnIndex(COMPANY_ID)));
                comp.setCompanyName(mCursor.getString(mCursor.getColumnIndex(COMPANY_NAME)));
                lstCompany.add(comp);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return lstCompany;
    }

    public List<String> getMMUOMList(String lotNo,String slotno) {

        List<String> uomList = new ArrayList<String>();
        mCursor = mSqlitedb.rawQuery("select distinct uom from moveManually where lotrefid='" + lotNo.trim()+ "' and slotNo= '"+slotno+"'", null);
        try {
            while (mCursor.moveToNext()) {
                uomList.add(mCursor.getString(0));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 123";
            mMsg = "Failed to load UOM List";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return uomList;
    }

    public List<String> getItemList() {
        String flag = "N";

        List<String> uomList = new ArrayList<String>();
        mCursor = mSqlitedb.rawQuery("select distinct Item,Descrip from picktaskdetail where Flag='" + flag + "'", null);
        try {
            while (mCursor.moveToNext()) {
                uomList.add(mCursor.getString(0));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 123";
            mMsg = "Failed to load UOM List";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return uomList;
    }

    public List<String> getBUUOMList() {

        List<String> uomList = new ArrayList<String>();
        mCursor = mSqlitedb.rawQuery("select distinct brName from breakerUomList ", null);
        try {
            while (mCursor.moveToNext()) {
                uomList.add(mCursor.getString(0));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 123";
            mMsg = "Failed to load UOM List";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return uomList;
    }

    public String getCompanyDb(String compid) {

        String CompDb = "";
        String query = "select * from " + COMPANY_TABLE + " where companyID='" + compid + "'";
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                CompDb=mCursor.getString(mCursor.getColumnIndex(COMPANY_DATABASE));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return CompDb;
    }

    public List<String> mGetLocations() {

        List<String> lstLocation = new ArrayList<String>();
        String query = "select * from " + LOCATION_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                lstLocation.add(mCursor.getString(0));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return lstLocation;
    }

    public List<String> getMMSlotNoList(String lotNo) {

        List<String> slotList = new ArrayList<String>();
        mCursor = mSqlitedb.rawQuery("select slotNo from moveManually where lotrefid='" + lotNo.trim() + "'", null);
        try {
            while (mCursor.moveToNext()) {
                slotList.add(mCursor.getString(0));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 123";
            mMsg = "Failed to slotList";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotList;
    }

    public List<String> getBUSlotNoList(String lotNo) {

        List<String> slotList = new ArrayList<String>();
        mCursor = mSqlitedb.rawQuery("select slotNo from breakerUOMUtlity where lotrefid='" + lotNo.trim() + "'", null);
        try {
            while (mCursor.moveToNext()) {
                slotList.add(mCursor.getString(0));
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 123";
            mMsg = "Failed to slotList";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return slotList;
    }


    public List<String> mGetCase(String item, String umeasur) {
        int count = 0;
        boolean isAvail = false;
        List<String> lstCase = new ArrayList<String>();

        mCursor = mSqlitedb.rawQuery("select umeasur from physicalcountUom where item='" + item + "' and " +
                "umeasur='" + umeasur + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
        }

        String query = "select umeasur from physicalcountUom where item='" + item + "'";
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {
                lstCase.add(mCursor.getString(0));
            }

            if (isAvail){
                lstCase.remove(umeasur);
                lstCase.add(0, umeasur);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load umeasur";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return lstCase;
    }
    public boolean isMMLocked(String lotno,String slotno,String UOM) {
        boolean isAvail = false;
        int count = 0;
        String locked="";
        List<String> lockList=null;

        mCursor = mSqlitedb.rawQuery("select locked from moveManually where lotrefid='" + lotno.trim() + "' and UOM='" + UOM + "' and slotNo='" + slotno + "'", null);
        try {
            while (mCursor.moveToNext()) {
                locked = mCursor.getString(0);
            }
        }catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load locked value";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        if (locked.equalsIgnoreCase("COUNT")) {
            isAvail = true;
        }
        return isAvail;
    }

    public String mgetMMQty(String wLotNo, String slotNO,String uom) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select tqty from moveManually where lotrefid='" + wLotNo.trim() + "' and UOM='" + uom + "' and slotNo='" + slotNO + "'", null);

        try {
            while (mCursor.moveToNext()) {
                    Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String mmgetStrLotNum(String wLotNo) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select wLotNo from moveManually where lotrefid='" + wLotNo.trim() + "'", null);

        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String breakUOMStrLotNum(String wLotNo) {
        String Tqty="";

        try {
            mCursor = mSqlitedb.rawQuery("select wLotNo from breakerUOMUtlity where lotrefid='" + wLotNo.trim() + "'", null);
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String mgetBUQty(String wLotNo, String slotNO) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select tqty from breakerUomUtlity where lotrefid='" + wLotNo.trim() + "' and slotNo='" + slotNO + "'", null);

        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String mgetBuBrUnit(String uom) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select brUnit from breakerUomList where brName='" + uom + "'", null);

        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String mgetMMTranQty(String wLotNo, String slotNO,String uom) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select mmtEqty from moveManuallyTransaction where mmtLotrefid='" + wLotNo.trim() + "' and mmtuom='" + uom + "' and mmtslotNo='" + slotNO + "'", null);

        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }


    public boolean isValidCompany(String compid,String compName) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from company where companyID='" + compid + "' and companyName='" + compName + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidWlotno(String wlotno) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskWHMQTY where wlotno ='" + wlotno + "' and Slottype='PICK' and taskNum='" + Globals.gTaskNo +  "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }
    public boolean isValidWlotnoFromScanpAllet(String wlotno) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from PickTask_ScanPallet where wlotno ='" + wlotno + "' and Slottype='PICK' and taskNum='" + Globals.gTaskNo +  "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidStagingWlotno(String wlotno) {
        boolean isAvail = false;
        int count = 0;

       // mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wLotNo ='" + wlotno + "' and taskNum='" + Globals.gTaskNo +  "' and Docstat <> 'X' and Docstat <> 'V' ", null);
        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where wLotNo ='" + wlotno + "' and taskNum='" + Globals.gTaskNo +  "' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }
    public boolean isValidStagingWlotnoFromScanPallet(String wlotno) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from PickTask_ScanPallet where wLotNo ='" + wlotno + "' and taskNum='" + Globals.gTaskNo +  "' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidPTSlot(String slot) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from receivetaskWHMSLT where Slot='" + slot + "' ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidItemSlot(String mItem,String slot) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from receiveSlotList where item='" + mItem + "' and slot ='" + slot + "'  ", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidmWlotno(String wlotno) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountWHMLOT where wlotno='" + wlotno + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isAvailUOM(String wlotno, String umeasur) {
        boolean isAvail = true;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where wlotno='" + wlotno + "' " +
                    "and slot='" + Globals.gPCSlot + "' and umeasur = '" + umeasur + "' " +
                    "and userid = '" + Globals.gUsercode + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count == 0) {
            isAvail = false;
        }
        return isAvail;
    }

    public boolean isAvailUOMWHMQTY(String wlotno, String umeasur) {
        boolean isAvail = true;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountWHMQTY where wlotno='" + wlotno + "' " +
                "and slot='" + Globals.gPCSlot + "' and umeasur = '" + umeasur + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count == 0) {
            isAvail = false;
        }
        return isAvail;
    }

    public boolean isNewWlotno(String wlotno) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from physicalcountDetail where wlotno='" + wlotno + "' " +
                "and Slot='" + Globals.gPCSlot + "' and wmsstat=''", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count == 0) {
            mCursor = mSqlitedb.rawQuery("select * from physicalcountWHMQTY where wlotno='" + wlotno + "' " +
                    "and Slot='" + Globals.gPCSlot + "'", null);
            count = mCursor.getCount();
            mCursor.close();
            if (count == 0) {
                isAvail = true;
                count = 0;
            }
        }
        return isAvail;
    }

    public boolean isValidSlot(String slot) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskWHMSLT where Slot='" + slot + "' and Slottype='PICK'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isSameItem(String wlotno, String tranlineno) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskdetail where WlotNo ='" + wlotno + "' " +
                "and Linesplit='1' and Tranlineno='" + tranlineno + "' and Flag = 'Y'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isTrailerAvailable() {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskWHITRLS", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidStgSlot(String sLot) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskWHMSLT where Slot='" + sLot + "' and Slottype='DELIVER'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public boolean isValidTrailer(String sTrailer) {
        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select * from picktaskWHITRLS where Trailer='" + sTrailer + "'", null);
        count = mCursor.getCount();
        mCursor.close();
        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    // method for transactions
    public void mBeginTransaction() { this.mSqlitedb.beginTransaction();}

    public void mSetTransactionSuccess() {
        this.mSqlitedb.setTransactionSuccessful();
    }

    public void mEndTransaction() {
        this.mSqlitedb.endTransaction();
    }

    public boolean isPCSlotList(String slot) {

        boolean isAvail = false;
        int count = 0;

        mCursor = mSqlitedb.rawQuery("select slot from physicalcountSlot where slot='" + slot + "'", null);
        count = mCursor.getCount();
        mCursor.close();

        if (count != 0) {
            isAvail = true;
            count = 0;
        }
        return isAvail;
    }

    public ArrayList<loadpickpalletWHITRL> getLPPTruckList(String Route) {
        ArrayList<loadpickpalletWHITRL> truckList = new ArrayList<>();
        loadpickpalletWHITRL truckMast = null;

        mCursor = mSqlitedb.rawQuery("select Trailer from loadpickpalletWHITRL where Route = '"+ Route +"'", null);
        try {
            while (mCursor.moveToNext()) {
                truckMast = new loadpickpalletWHITRL();
                truckMast.setTrailer(mCursor.getString(0));
                truckList.add(truckMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHITRL";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return truckList;
    }



    public ArrayList<loadpickpalletWHIPLT> getLPPPalletList() {
        ArrayList<loadpickpalletWHIPLT> palletList = new ArrayList<>();
        loadpickpalletWHIPLT palletMast = null;

        mCursor = mSqlitedb.rawQuery("select Palno from loadpickpalletWHIPLT", null);
        try {
            while (mCursor.moveToNext()) {
                palletMast = new loadpickpalletWHIPLT();
                palletMast.setPalno(mCursor.getString(0));
                palletList.add(palletMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHIPLT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return palletList;
    }
    public ArrayList<picktaskWHIPTL> getPalletList() {
        ArrayList<picktaskWHIPTL> palletList = new ArrayList<>();
        picktaskWHIPTL palletMast = null;

        mCursor = mSqlitedb.rawQuery("select Palno from picktaskWHIPTL where Palstat<>'X'", null);
        try {
            while (mCursor.moveToNext()) {
                palletMast = new picktaskWHIPTL();
                palletMast.setPalno(mCursor.getString(0));

                palletList.add(palletMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHIPTL";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return palletList;
    }

    public ArrayList<receivetaskWHMSLT> getSlotList() {
        ArrayList<receivetaskWHMSLT> slotList = new ArrayList<>();
        receivetaskWHMSLT slotMast = null;

        mCursor = mSqlitedb.rawQuery("select Slot from receivetaskWHMSLT", null);
        try {
            while (mCursor.moveToNext()) {
                slotMast = new receivetaskWHMSLT();
                slotMast.setSlot(mCursor.getString(0));

                slotList.add(slotMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHIPTL";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return slotList;
    }

    public String mgetQty(String umeasur, String flag) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select tcountqty, tqty from physicalcountDetail where wlotno = '" + Globals.gPCWlotno + "' " +
                                            "and  umeasur = '" + umeasur + "' and  slot = '" + Globals.gPCSlot + "'", null);
        try {
            while (mCursor.moveToNext()) {
                if (flag.equals("Y")){
                    Tqty = mCursor.getString(0);
                } else {
                    Tqty = mCursor.getString(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String GetTrailer() {
        String Trailer="";
        String TrailerList="";

        mCursor = mSqlitedb.rawQuery("select Trailer from picktaskWHITRLS", null);
        try {
            while (mCursor.moveToNext()) {
                Trailer = mCursor.getString(0);

                if (TrailerList.equals("")){
                    TrailerList = Trailer;
                } else {
                    TrailerList = TrailerList + ", " + Trailer;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return TrailerList;
    }

    public String getTqty(String Slot, String wLotNo) {
        String tqty="";

        mCursor = mSqlitedb.rawQuery("select Tqty from picktaskWHMQTY where Slot = '" + Slot + "' and  lotrefid = '" + wLotNo + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return tqty;
    }

    public String getLotItemList(String wLotNo) {
        String Lotitem="";

        mCursor = mSqlitedb.rawQuery("select Item from picktaskWHMLOT where wLotNo = '" + wLotNo + "' and taskNum = '" + Globals.gTaskNo + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                Lotitem = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return Lotitem;
    }
    public String getSubItem(String wLotNo) {
        String Lotitem="";

        mCursor = mSqlitedb.rawQuery("select Item from picktaskdetail where lotrefid = '" + wLotNo + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                Lotitem = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return Lotitem;
    }

    public String getDocNo(String wLotNo) {
        String Lotitem="";

        mCursor = mSqlitedb.rawQuery("select Docno from picktaskdetail where lotrefid = '" + wLotNo + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                Lotitem = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return Lotitem;
    }


    public String SelectLotNo(String wLotNo) {
        String Lotno="";

        mCursor = mSqlitedb.rawQuery("select Lotrefid from picktaskWHMLOT where wLotNo = '" + wLotNo + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                Lotno = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return Lotno;
    }

    public void UpdateSlottype() {
        String mSlot="", mSlottype="", mLoctid="";

        mCursor = mSqlitedb.rawQuery("select a.Slot, a.Loctid, b.Slottype from picktaskWHMQTY a  " +
                "Inner join picktaskWHMSLT b ON a.Slot = b.Slot and a.Loctid = b.Loctid", null);
        try {
            while (mCursor.moveToNext()) {
                mSlot = mCursor.getString(0);
                mLoctid = mCursor.getString(1);
                mSlottype = mCursor.getString(2);
                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.WHMQTY_SLOTTYPE,
                        mSlottype);

                mSqlitedb.update(WMSDbHelper.PICK_TASK_WHMQTY, cv, "Slot = '"
                        + mSlot + "' and Loctid='" + mLoctid + "'", null);

                Log.i("PickTask WHMQTY", "PickTask WHMQTY Slottype record updated successfully...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Upadte Slottype field in WHMQTY";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public void UpdateFromPickTaskpallet() {
        String mPallet = "";

        mCursor = mSqlitedb.rawQuery("select Palno from picktaskWHIPTL ", null);
        try {
            while (mCursor.moveToNext()) {
                mPallet = mCursor.getString(0);

                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.PT_PALNO,
                        mPallet);

                mSqlitedb.update(WMSDbHelper.PICK_TASK_PRINTLABEL, cv, "", null);

                Log.i("PickTask Print Label", "PickTask Print Label updated successfully...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Print Label field";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public void UpdateFromPickTaskdetail() {
        String mDocno = "";

        mCursor = mSqlitedb.rawQuery("select Docno from picktaskdetail", null);
        try {
            while (mCursor.moveToNext()) {
                mDocno = mCursor.getString(0);

                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.PT_ORDER,
                        mDocno);

                mSqlitedb.update(WMSDbHelper.PICK_TASK_PRINTLABEL, cv, "", null);

                Log.i("PickTask Print Label", "PickTask Print Label updated successfully...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Print Label field";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public void UpdateFromPickTaskWHITRLS() {
        String mTrailer = "", mDock = "";

        mCursor = mSqlitedb.rawQuery("select Trailer, Dock from picktaskWHITRLS", null);
        try {
            while (mCursor.moveToNext()) {
                mTrailer = mCursor.getString(0);
                mDock = mCursor.getString(1);

                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.PT_TRAILER,
                        mTrailer);
                cv.put(WMSDbHelper.PT_DOCK,
                        mTrailer);

                mSqlitedb.update(WMSDbHelper.PICK_TASK_PRINTLABEL, cv, "", null);

                Log.i("PickTask Print Label", "PickTask Print Label updated successfully...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Print Label field";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public void UpdateFromReceiveTask(String recQty) {
        String mPallet = "";
        try {
            ContentValues cv = new ContentValues();
            if (Globals.gCatchwt.equals("T"))
                cv.put(WMSDbHelper.RTP_TQTY,
                        recQty);
            else {
                cv.put(WMSDbHelper.RTP_TRKQTY,
                        recQty);
            }

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_PRINT, cv, "", null);

            Log.i("ReceiveTask Print Label", "ReceiveTask Print Label updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Print Label field";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public ArrayList<picktaskWHMLOT> getLotList() {
        ArrayList<picktaskWHMLOT> lotList = new ArrayList<>();
        picktaskWHMLOT lotMast = null;

        mCursor = mSqlitedb.rawQuery("select lotrefid,Item from picktaskWHMLOT ", null);
        try {
            while (mCursor.moveToNext()) {
                lotMast = new picktaskWHMLOT();
                lotMast.setWlotno(mCursor.getString(0));
                lotMast.setItem(mCursor.getString(1));

                lotList.add(lotMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return lotList;
    }

    public ArrayList<picktaskWHMQTY> getWHMQTYLotList() {
        ArrayList<picktaskWHMQTY> lotList = new ArrayList<>();
        picktaskWHMQTY lotMast = null;

        mCursor = mSqlitedb.rawQuery("select wLotNo from picktaskWHMQTY ", null);
        try {
            while (mCursor.moveToNext()) {
                lotMast = new picktaskWHMQTY();
                lotMast.setWlotno(mCursor.getString(0));

                lotList.add(lotMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return lotList;
    }

    public ArrayList<picktaskWHMLOT> getLoctIdLotList(String loctid) {
        ArrayList<picktaskWHMLOT> lotList = new ArrayList<>();
        picktaskWHMLOT lotMast = null;

        mCursor = mSqlitedb.rawQuery("select lotrefid,Item from picktaskWHMLOT ", null);
        try {
            while (mCursor.moveToNext()) {
                lotMast = new picktaskWHMLOT();
                lotMast.setWlotno(mCursor.getString(0));
                lotMast.setItem(mCursor.getString(1));

                lotList.add(lotMast);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return lotList;
    }
    public void deleteSelectedPickTaskDetail(String tranlineno)
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM picktaskdetail where Tranlineno='" + tranlineno + "'");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }
    public void deleteReceiveTaskDetail()
    {
        try
        {
            mSqlitedb.execSQL("DELETE from receivetasktrandetail");
            mSqlitedb.execSQL("DELETE from receivetaskdetail");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void DeletereceivetaskdetailDetail(String taskNo,String tranlineNo,String flag)
    {
        try
        {
            //mSqlitedb.execSQL("DELETE FROM receivetaskexportdetail where taskno = '" + taskNo + "' and tranlineno = '" + tranlineNo + "'");
            mSqlitedb.execSQL("DELETE FROM receivetaskexportdetail");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteBreakerUOM()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM breakerUOMUtlity");
            mSqlitedb.execSQL("DELETE FROM breakerUomList");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }




    public void deleteRepackData()
    {
        try
        {
           /* mSqlitedb.execSQL("DELETE FROM repackFg");*/
            mSqlitedb.execSQL("DELETE FROM repackIngredient");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteExportLot()
    {
        try
        {
            mSqlitedb.execSQL("DELETE from exportLot");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deletemoveManually()
    {
        try
        {
            mSqlitedb.execSQL("DELETE from moveManually");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deletePicktaskDetail()
    {
        try
        {
            mSqlitedb.execSQL("DELETE from picktaskdetail where taskNum = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktaskheader where taskNum = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktasklist where TaskNo = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktaskWHMQTY where taskNum = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktaskWHMLOT where taskNum = '" + Globals.gTaskNo + "'");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }


    public void deletePicktaskrevetOldQty()
    {
        try
        {
            mSqlitedb.execSQL("DELETE from picktaskrevertolddata ");

        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }


    public void UpdatePickTaskStatus(String taskno,String Status) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.STATUS, Status);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_LIST_TABLE, cv, "taskNo = '"
                    + taskno + "'", null);

            Log.i("ReceiveTask List", "ReceiveTask List record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask List update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void deletePicktaskDetailHold()
    {
        try
        {
            mSqlitedb.execSQL("DELETE from picktaskdetail where taskNum = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktaskheader where taskNum = '" + Globals.gTaskNo + "'");
            //mSqlitedb.execSQL("DELETE from picktasklist where TaskNo = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktaskWHMQTY where taskNum = '" + Globals.gTaskNo + "'");
            mSqlitedb.execSQL("DELETE from picktaskWHMLOT where taskNum = '" + Globals.gTaskNo + "'");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void deleteMMTran(String lotno,String slotno,String UOM)
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM moveManuallyTransaction where mmtLotrefid='" + lotno.trim() + "' and mmtslotNo='" + slotno + "' and mmtuom='" + UOM + "'");
            Log.i("Move Manually", "Move Manually Tran record deleted successfully...");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void UpdateMMTranQty(String lotno,String slotno,String UOM,String Qty) {
        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.MOVE_MANUALLY_TRANSACTION_EQTY, Qty);

            mSqlitedb.update(WMSDbHelper.MOVE_MANUALLY_TRANSACTION_TABLE, cv, "mmtLotrefid = '"
                    + lotno.trim() + "' and mmtslotNo='" + slotno + "' and mmtuom ='" + UOM + "'", null);

            Log.i("Move Manually", "Move Manually Tran Qty record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 802";
            mMsg = "Move Manually Tran  update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void UpdateTranlineno(String tranlineno) {
        String mTranlineNo="";
        int nTlineno = 0;
        nTlineno = Integer.valueOf(tranlineno);
        mCursor = mSqlitedb.rawQuery("select Tranlineno from picktaskdetail where Tranlineno > '" + tranlineno + "'", null);
        try {
            while (mCursor.moveToNext()) {

                mTranlineNo = mCursor.getString(0);

                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.DETAIL_TRANLINENO,
                        String.valueOf(nTlineno));
                cv.put(WMSDbHelper.DETAIL_DOCLINENO,
                        String.valueOf(nTlineno));

                mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, "Tranlineno =" +
                        "'" + mTranlineNo + "'", null);
                nTlineno = nTlineno + 1;
                Log.i("PickTask Detail", "PickTask Detail TranlineNo record updated successfully...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Upadte TranlineNo field in PickTask Detail";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public void UpdateRowno() {
        String mRowno="", mTranlineNo="";;
        int nRowno = 1;

        mCursor = mSqlitedb.rawQuery("select rowNo, Tranlineno from picktaskdetail", null);
        try {
            while (mCursor.moveToNext()) {

                mRowno = mCursor.getString(0);
                mTranlineNo = mCursor.getString(1);

                ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.DETAIL_ROWNO,
                        String.valueOf(nRowno));


                mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv, "rowNo =" +
                        "'" + mRowno + "' and Tranlineno ='" + mTranlineNo + "'", null);
                nRowno = nRowno + 1;
                Log.i("PickTask Detail", "PickTask Detail Rowno record updated successfully...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to Upadte rowNo field in PickTask Detail";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mSqlitedb.close();
    }

    public String DecimalFractionConversion(String pAmount, String pDecnum){
        String amount ="" , decnum="";
        int idecnum;
        double dAmt;
        try {
            amount = pAmount;
            decnum = pDecnum;
            dAmt = Double.valueOf(amount);
            idecnum = Integer.valueOf(decnum);
            amount = String.format("%." + idecnum + "f", dAmt);
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Decimal Fraction covertion failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return amount;
    }


    public void updatePicktaskLineItems(String qty, String Tranlineno,String pallatNo,String slot,String lotNo,String isedited,String difQty) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(DETAIL_TQTY, qty);
            if (!isedited.equals("E")){
                cv.put(DETAIL_ORGTQTY, qty);
            }

            //cv.put(RTD_TRKQTYREC, qty);
            cv.put(DETAIL_FLAG,"Y");
            cv.put(DETAIL_SLOT,slot );
            cv.put(DETAIL_LOTNO,lotNo);
            cv.put(DETAIL_WLOTNO,pallatNo);
            cv.put(DETAIL_ISEDITED,isedited);
            cv.put(DETAIL_SUBSTITUTED_ITEM,difQty);
            cv.put(DETAIL_CHGQTY,"");
            cv.put(DETAIL_PICKED,"Y");

            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv,  " taskNum = '" + Globals.gTaskNo + "' and Tranlineno='" + Tranlineno + "' ", null);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo='" + lotNo + "' ", null);

           // mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv,  " taskNum = '" + Globals.gTaskNo + "' and Tranlineno='" + Tranlineno + "' ", null);

            Log.i("Pick Task Detail", "Pick Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Pick Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updatePicktaskTempAlloc(String TempAloc, String palletNo,String wlotNo,String item,String strTranlineNo) {
        try {

            mSqlitedb.execSQL("UPDATE " + WMSDbHelper.PICK_TASK_REVEROLDDATA + " SET " + DETAIL_TEMPQTY + "=" + DETAIL_TEMPQTY + TempAloc + " WHERE wlotno" + "=?" + " AND Item" + "=?" +" AND LotNo" + "=?",
                    new String[] { wlotNo,item,palletNo} );

            /*mSqlitedb.execSQL("UPDATE " + WMSDbHelper.PICK_TASK_REVEROLDDATA + " SET " + DETAIL_TEMPQTY + "=" + DETAIL_TEMPQTY + TempAloc + " WHERE wlotno" + "=?" + " AND Item" + "=?" +" AND LotNo" + "=?" +" AND Tranlineno" + "=?",
                    new String[] { wlotNo,item,palletNo,strTranlineNo } );*/


            Log.i("Pick Task TempAlloc", "Pick Task TempAlloc record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Pick Task TempAlloc update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }


    public void RevertPicktaskData(picktaskdetail mPicktaskDetail, String Tranlineno) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(DETAIL_TQTY, mPicktaskDetail.getTQty());
           cv.put(DETAIL_ORGTQTY, mPicktaskDetail.getorgTQty());
            //cv.put(RTD_TRKQTYREC, qty);
            cv.put(DETAIL_FLAG,mPicktaskDetail.getFlag());
            cv.put(DETAIL_SLOT,mPicktaskDetail.getSlot() );
            cv.put(DETAIL_LOTNO,mPicktaskDetail.getLotNo());
            cv.put(DETAIL_WLOTNO,mPicktaskDetail.getWLotNo());
            cv.put(DETAIL_ISEDITED,mPicktaskDetail.getIsedited());
            cv.put(DETAIL_SUBSTITUTED_ITEM,mPicktaskDetail.getSubItem());


            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv,  " taskNum = '" + Globals.gTaskNo + "' and Tranlineno='" + Tranlineno + "' ", null);

            mSqlitedb.execSQL("DELETE from picktaskdetail where taskNum = '"+Globals.gTaskNo+"' and ( Tranlineno = '"+Tranlineno+"' OR wlotno is Null ) and Item = '"+ mPicktaskDetail.getItem() +"' and slot <> ''");

            Log.i("Pick Task Detail", "Pick Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Pick Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }






    public void updatePicktaskLneItmsIfZero(String qty, String Tranlineno,String pallatNo,String isEdited,String difQty,String wLotNo,String slot) {
        try {

            ContentValues cv = new ContentValues();


            cv.put(DETAIL_SLOT, "");
            cv.put(DETAIL_TQTY, qty);
            if (!isEdited.equals("E")){
                cv.put(DETAIL_ORGTQTY,qty);
            }
            //cv.put(RTD_TRKQTYREC, qty);
            cv.put(DETAIL_WLOTNO,"");
            cv.put(DETAIL_SUBSTITUTED_ITEM,difQty);
            cv.put(DETAIL_LOTNO,"");
            cv.put(DETAIL_PICKDURATION,0);
            cv.put(DETAIL_FLAG,"N");
            cv.put(DETAIL_SUBSTITUTED_TRANNO,"");
            cv.put(DETAIL_ISEDITED,isEdited);
            cv.put(DETAIL_CHGQTY,"");
            if (isEdited.equals("E")){
                cv.put(DETAIL_PICKED,"Y");
            }else {
                cv.put(DETAIL_PICKED,"");
            }








            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo = '"
                    + pallatNo + "' and Tranlineno='" + Tranlineno + "' ", null);

            cv.put(DETAIL_LOTNO,pallatNo);
            cv.put(DETAIL_WLOTNO,wLotNo);
            cv.put(DETAIL_SLOT, slot);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo = '"
                    + pallatNo + "' and LotNo='" + pallatNo + "' ", null);

           /* mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo = '"
                    + pallatNo + "' and Tranlineno='" + Tranlineno + "' ", null);*/

            Log.i("Pick Task Detail", "Pick Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Pick Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }




    public void updatePicktaskLneIfEditfZero(String qty, String Tranlineno,String pallatNo,String isEdited,String difQty,String WlotNo,String slot,String lot) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(DETAIL_SLOT, slot);
            cv.put(DETAIL_TQTY, qty);
            if (!isEdited.equals("E")){
                cv.put(DETAIL_ORGTQTY,qty);
            }

            //cv.put(RTD_TRKQTYREC, qty);
            cv.put(DETAIL_WLOTNO,WlotNo);
            cv.put(DETAIL_SUBSTITUTED_ITEM,difQty);
            cv.put(DETAIL_LOTNO,lot);
            cv.put(DETAIL_PICKDURATION,0);
            cv.put(DETAIL_FLAG,"N");
            cv.put(DETAIL_SUBSTITUTED_TRANNO,"");
            cv.put(DETAIL_ISEDITED,isEdited);
            cv.put(DETAIL_CHGQTY,"");


            mSqlitedb.update(WMSDbHelper.PICK_TASK_DETAIL, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo = '"
                    + pallatNo + "' and Tranlineno='" + Tranlineno + "' ", null);

            mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo = '"
                    + pallatNo + "' and LotNo='" + lot + "' ", null);


            /*mSqlitedb.update(WMSDbHelper.PICK_TASK_REVEROLDDATA, cv,  " taskNum = '" + Globals.gTaskNo + "' and LotNo = '"
                    + pallatNo + "' and Tranlineno='" + Tranlineno + "' ", null);*/

            Log.i("Pick Task Detail", "Pick Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Pick Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }



    public void updateReceiveTaskQty(String taskno, String Tranlineno,String qty) {
        try {

            ContentValues cv = new ContentValues();

                cv.put(RTD_TQTYREC, qty);
                //cv.put(RTD_TRKQTYREC, qty);
                cv.put(RTD_TQTYINC,qty);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_DETAIL, cv, "Taskno = '"
                    + taskno + "' and " + "tranlineno='" + Tranlineno + "'", null);

            Log.i("Receive Task Detail", "Receive Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Receive Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void updateReceiveTaskWHRPLTQTY(String taskno,String tranlineno,String qty) {

        try {

            ContentValues cv = new ContentValues();

                cv.put(WMSDbHelper.RTWHRPLT_TQTY,
                        qty);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_WHRPLT, cv, "Taskno = '"
                    + taskno + "' and " + "tasklineno='" + tranlineno + "'", null);

            Log.i("PickTask Detail", "PickTask Detail record updated successfully...");
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "PickTask Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void cancelReceiveTaskWHRPLTQTY(String taskno,String tranlineno,String qty) {

        try {

            ContentValues cv = new ContentValues();
            if (Globals.gCatchwt == "T") {
                cv.put(RTWHRPLT_TQTY, String.valueOf(qty));
                //cv.put(RTD_TQTYINC,String.valueOf(qty));
            } else {
                cv.put(RTWHRPLT_TQTY, String.valueOf(qty));
                //cv.put(RTD_TQTYINC,String.valueOf(qty));
            }

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_WHRPLT, cv, "Taskno = '"
                    + taskno + "' and tasklineno='" + tranlineno + "'", null);

            Log.i("Receive Task Detail", "Receive Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Receive Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public String getOldQty(String taskNum, String tranNum) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select tqtyinc from receivetaskdetail where Taskno = '" + taskNum + "' " +
                "and  tranlineno = '" + tranNum + "'", null);
        try {
            while (mCursor.moveToNext()) {
                    Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String getUpdatedQty(String taskNum, String tranNum) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select tqtyrec from receivetaskdetail where Taskno = '" + taskNum + "' " +
                "and  tranlineno = '" + tranNum + "'", null);
        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public String getFirstQty(String taskNum, String tranNum) {
        String Tqty="";

        mCursor = mSqlitedb.rawQuery("select trkqtyrec from receivetaskdetail where Taskno = '" + taskNum + "' " +
                "and  tranlineno = '" + tranNum + "'", null);
        try {
            while (mCursor.moveToNext()) {
                Tqty = mCursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Lot Item field from WHMLOT";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Tqty;
    }

    public void updateFirstQty(String taskno, String Tranlineno,String qty) {
        try {

            ContentValues cv = new ContentValues();

            cv.put(RTD_TQTYREC, "0.000000");
            //cv.put(RTD_TRKQTYREC, qty);
            cv.put(RTD_TQTYINC,qty);

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_DETAIL, cv, "Taskno = '"
                    + taskno + "' and " + "tranlineno='" + Tranlineno + "'", null);

            Log.i("Receive Task Detail", "Receive Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Receive Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }

    public void cancelReceiveTaskWHRPLTONHOLD(String taskno,String tranlineno) {

        try {

            ContentValues cv = new ContentValues();
            if (Globals.gCatchwt == "T") {
                cv.put(RTWHRPLT_TQTY, "0.000000");
                //cv.put(RTD_TQTYINC,String.valueOf(qty));
            } else {
                cv.put(RTWHRPLT_TQTY, "0.000000");
                //cv.put(RTD_TQTYINC,String.valueOf(qty));
            }
            cv.put(WMSDbHelper.RTWHRPLT_PRTPLTTAG, 0);
            cv.put(WMSDbHelper.RTWHRPLT_SLOT, "");
            cv.put(WMSDbHelper.RTWHRPLT_FLAG, "N");
            cv.put(WMSDbHelper.RTWHRPLT_GTIN, "");

            mSqlitedb.update(WMSDbHelper.RECEIVE_TASK_WHRPLT, cv, "Taskno = '"
                    + taskno + "' and tasklineno='" + tranlineno + "'", null);

            Log.i("Receive Task Detail", "Receive Task Detail record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "Receive Task Detail update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
    }
    public void DeleteRepackList() {

        try
        {
            mSqlitedb.execSQL("DELETE from RepackList");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void DeleteReceiveSlot() {

        try
        {
            mSqlitedb.execSQL("DELETE from receiveSlotList");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void DeleteReceiveTaskDetailandWHRPLT(String taskno) {

        try
        {
            mSqlitedb.execSQL("DELETE from receivetaskdetail where taskno = '"+taskno+"'");
            mSqlitedb.execSQL("DELETE from receivetaskWHRPLT where taskno = '"+taskno+"'");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void updatewhenCancel(String lotrefid,List<RepackIngredients> repackIngredients) {

        try {

            if (repackIngredients.get(0).getRIT_PALNO().equals("")){

                ContentValues cv = new ContentValues();
                cv.put(WMSDbHelper.RIT_LOTNO,"");
                cv.put(WMSDbHelper.RIT_WLOTNO,"");
                cv.put(WMSDbHelper.RIT_LOTREFID,"");
                cv.put(WMSDbHelper.RIT_SLOT,"");
                cv.put(WMSDbHelper.RIT_WHQTY,"");
                cv.put(WMSDbHelper.RIT_ICQTY,"");
                cv.put(WMSDbHelper.RIT_RPALLOCQTY,"");


                mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "lotrefid = '"
                        + lotrefid + "'", null);
            }



            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void DeleteRevertCancel(String lotrefid) {

        try
        {
            mSqlitedb.execSQL("DELETE  from repackIngredient where tranlineNo IS NULL");


        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void DeleteReceiveTaskTranDetailandWHRPLTS(String taskno) {

        try
        {
            mSqlitedb.execSQL("DELETE from receivetasktrandetail where taskno = '"+taskno+"'");
            mSqlitedb.execSQL("DELETE from receivetasktranWHRPLT where taskno = '"+taskno+"'");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public void DeletePickTaskScanPallet() {

        try
        {
            mSqlitedb.execSQL("DELETE from PickTask_ScanPallet");
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }


/*  //01/02/23_CIRK_For Fresha to APN
    public ArrayList<RepackList> getRepackListData() {
        RepackList repackList = null;
        ArrayList<RepackList> repckPickList = new ArrayList<>();

        String query = "select * from " + REPACK_PICKLIST_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {

                repackList = new RepackList();

                repackList.setAddtime(mCursor.getString(0));
                repackList.setLoctid(mCursor.getString(1));
                repackList.setPadate(mCursor.getString(2));
                repackList.setPano(mCursor.getString(3));

                repckPickList.add(repackList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return repckPickList;
    }
*/

/*   //01/02/23_CIRK_For Fresha to APN
    public void addRepackList(RepackList repackList) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(RPL_PANO, repackList.getPano());
            cv.put(RPL_ADDTIME, repackList.getAddtime());
            cv.put(RPL_LOCTID, repackList.getLoctid());
            cv.put(RPL_PAFDATE, repackList.getPadate());


            mSqlitedb.insert(REPACK_PICKLIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("RepackPickList inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = " data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
*/


    public Boolean isRepackListAvailable(String repackNumber) {
        boolean result = false;

        mCursor = mSqlitedb.rawQuery("select pano from RepackList where pano='"+repackNumber+"'", null);
        try {
            while (mCursor.moveToNext()) {
                String pano = mCursor.getString(0);
                result = true;

            } } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Boolean isPalNoAvailable(String palno,String taskNo) {
        boolean result = false;

        mCursor = mSqlitedb.rawQuery("select palno from receivetaskdetail where palno = '" + palno + "' " +
                "and  taskno = '" + taskNo + "'", null);
        //mCursor = mSqlitedb.rawQuery("select palno from receivetaskdetail where palno = '" + palno + "'", null);
        try {
            while (mCursor.moveToNext()) {
                String pano = mCursor.getString(0);
                result = true;

            } } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Boolean isPalNoAvailable_MoveTask(String palno,String taskNo) {
        boolean result = false;

        mCursor = mSqlitedb.rawQuery("select palno from movetaskdetail where palno = '" + palno + "' " +
                "and  taskno = '" + taskNo + "'", null);
        //mCursor = mSqlitedb.rawQuery("select palno from receivetaskdetail where palno = '" + palno + "'", null);
        try {
            while (mCursor.moveToNext()) {
                String pano = mCursor.getString(0);
                result = true;

            } } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public Boolean isScaned_MoveTask(String taskNo) {

        boolean result = false;

        mCursor = mSqlitedb.rawQuery("select palno from movetaskdetail where flag='Y' and taskno='"+taskNo+"'", null);
        //mCursor = mSqlitedb.rawQuery("select palno from receivetaskdetail where palno = '" + palno + "'", null);
        try {
            while (mCursor.moveToNext()) {
                String pano = mCursor.getString(0);
                result = true;

            } } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public int getUpdatedValueCount() {
        int Count = 0;

        try{

        mCursor = mSqlitedb.rawQuery("select *  from physicalcountDetail where FLag = 'Y' and userid = '"+ Globals.gUsercode +"'", null);
        Count = mCursor.getCount();
        mCursor.close();

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return Count;
    }

    public void deletePhysicalCountDetail() {

        try
        {
            mSqlitedb.execSQL("DELETE FROM " + PHYSCIAL_COUNT_DETAIL_TABLE);
        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public ArrayList<physicalcountDetail> getPhysCountTranData() {
        ArrayList<physicalcountDetail> pcList = new ArrayList<>();
        physicalcountDetail physicalcountDetail = null;

        mCursor = mSqlitedb.rawQuery("select * from physicalCountTranDetail", null);
        try {
            while (mCursor.moveToNext()) {
                physicalcountDetail = new physicalcountDetail();

                physicalcountDetail.setslot(mCursor.getString(0));
                physicalcountDetail.setcountid(mCursor.getString(1));
                physicalcountDetail.setpage(mCursor.getString(2));
                physicalcountDetail.setdoclineno(mCursor.getString(3));
                physicalcountDetail.setloctid(mCursor.getString(4));
                physicalcountDetail.setitem(mCursor.getString(5));
                physicalcountDetail.setwlotno(mCursor.getString(7));
                physicalcountDetail.setumeasur(mCursor.getString(8));
                physicalcountDetail.settcountqty(mCursor.getString(9));
                physicalcountDetail.setwmsstat(mCursor.getString(10));
                physicalcountDetail.setposted(mCursor.getString(11));
                physicalcountDetail.setitmdesc(mCursor.getString(12));
                physicalcountDetail.setpckdesc(mCursor.getString(13));
                physicalcountDetail.setdecnum(mCursor.getString(14));
                physicalcountDetail.setlotrefid(mCursor.getString(15));
                physicalcountDetail.settqty(mCursor.getString(16));
                physicalcountDetail.setitemShow(mCursor.getString(17));
                physicalcountDetail.setsurprisadd(mCursor.getString(18));
                physicalcountDetail.setuserid(mCursor.getString(19));
                physicalcountDetail.setcounted(mCursor.getString(20));
                physicalcountDetail.setcollection(mCursor.getString(21));
                physicalcountDetail.setwelement(mCursor.getString(22));
                physicalcountDetail.setwidgetID(mCursor.getString(23));
                //cv.put(PC_DETAIL_ROWNO, Globals.gPCDetailRowCount);
                physicalcountDetail.setFlag(mCursor.getString(25));
                physicalcountDetail.setUpdateNo(mCursor.getString(26));
                //Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;

                pcList.add(physicalcountDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to select Pallet field from WHITRL";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return pcList;
    }

    public void UpdatePhyCountDetailFromTran(List<physicalcountDetail> list) {

        try {

            for(int i=0;i < list.size();i++) {

                ContentValues cv = new ContentValues();

                cv.put(PC_DETAIL_SLOT, list.get(i).getslot());
                cv.put(PC_DETAIL_COUNTID, list.get(i).getcountid());
                cv.put(PC_DETAIL_PAGE, list.get(i).getpage());
                cv.put(PC_DETAIL_DOCLINENO, list.get(i).getdoclineno());
                cv.put(PC_DETAIL_LOCTID, list.get(i).getloctid());
                cv.put(PC_DETAIL_ITEM, list.get(i).getitem());
                cv.put(PC_DETAIL_WLOTNO, list.get(i).getwlotno());
                cv.put(PC_DETAIL_UMEASUR, list.get(i).getumeasur());
                cv.put(PC_DETAIL_TCOUNTQTY, list.get(i).gettcountqty());
                cv.put(PC_DETAIL_WMSSTAT, list.get(i).getwmsstat());
                cv.put(PC_DETAIL_POSTED, list.get(i).getposted());
                cv.put(PC_DETAIL_ITMDESC, list.get(i).getitmdesc());
                cv.put(PC_DETAIL_PCKDESC, list.get(i).getpckdesc());
                cv.put(PC_DETAIL_DECNUM, list.get(i).getdecnum());
                cv.put(PC_DETAIL_LOTREFID, list.get(i).getlotrefid());
                cv.put(PC_DETAIL_TQTY, list.get(i).gettqty());
                cv.put(PC_DETAIL_ITEMSHOW, list.get(i).getitemShow());
                cv.put(PC_DETAIL_SURPRISADD, list.get(i).getsurprisadd());
                cv.put(PC_DETAIL_USERID, list.get(i).getuserid());
                cv.put(PC_DETAIL_COUNTED, list.get(i).getcounted());
                cv.put(PC_DETAIL_COLLECTION, list.get(i).getcollection());
                cv.put(PC_DETAIL_WELEMENT, list.get(i).getwelement());
                cv.put(PC_DETAIL_WIDGETID, list.get(i).getwidgetID());
                //cv.put(PC_DETAIL_ROWNO, Globals.gPCDetailRowCount);
                cv.put(PC_DETAIL_FLAG, list.get(i).getFlag());
                cv.put(PC_DETAIL_UPDATEDNO, list.get(i).getUpdateNo());
                //Globals.gPCDetailRowCount = Globals.gPCDetailRowCount + 1;

                mSqlitedb.insert(PHYSCIAL_COUNT_DETAIL_TABLE, null, cv);
            }
            LogfileCreator.mAppendLog("PHYSCIAL COUNT Detail: Detail inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "Detail insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }



    //01/02/23_CIRK_For Fresha to APN

    public ArrayList<RepackIngredients> getRepackIngredientsForExport() {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where allocqty >'' and tranlineNo IS NOT NULL ", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_PANO(mCursor.getString(mCursor.getColumnIndex(RIT_PANO)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_SERIAL(mCursor.getString(mCursor.getColumnIndex(RIT_SERIAL)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));
                repackIngredients.setRIT_COST(mCursor.getString(mCursor.getColumnIndex(RIT_COST)));
                repackIngredients.setRIT_PADATE(mCursor.getString(mCursor.getColumnIndex(RIT_PADATE)));
                repackIngredients.setRIT_PASTAT(mCursor.getString(mCursor.getColumnIndex(RIT_PASTAT)));
                repackIngredients.setRIT_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(RIT_LCKSTAT)));
                repackIngredients.setRIT_LCKUSER(mCursor.getString(mCursor.getColumnIndex(RIT_LCKUSER)));
                repackIngredients.setRIT_LCKDATE(mCursor.getString(mCursor.getColumnIndex(RIT_LCKDATE)));
                repackIngredients.setRIT_LCKTIME(mCursor.getString(mCursor.getColumnIndex(RIT_LCKTIME)));
                repackIngredients.setRIT_ADDUSER(mCursor.getString(mCursor.getColumnIndex(RIT_ADDUSER)));
                repackIngredients.setRIT_ADDDATE(mCursor.getString(mCursor.getColumnIndex(RIT_ADDDATE)));
                repackIngredients.setRIT_ADDTIME(mCursor.getString(mCursor.getColumnIndex(RIT_ADDTIME)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_VENDNO(mCursor.getString(mCursor.getColumnIndex(RIT_VENDNO)));
                repackIngredients.setRIT_BINNO(mCursor.getString(mCursor.getColumnIndex(RIT_BINNO)));
                repackIngredients.setRIT_PALNO(mCursor.getString(mCursor.getColumnIndex(RIT_PALNO)));
                repackIngredients.setRIT_REMARKS(mCursor.getString(mCursor.getColumnIndex(RIT_REMARKS)));
                repackIngredients.setRIT_YIELD(mCursor.getString(mCursor.getColumnIndex(RIT_YIELD)));
                repackIngredients.setRIT_SETID(mCursor.getString(mCursor.getColumnIndex(RIT_SETID)));
                repackIngredients.setRIT_WEIGHT(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT)));
                repackIngredients.setRIT_ID_COL(mCursor.getString(mCursor.getColumnIndex(RIT_ID_COL)));
                repackIngredients.setRIT_WLOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_WLOTNO)));
                repackIngredients.setRIT_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(RIT_ORIGTRANLN)));
                repackIngredients.setRIT_STKUMID(mCursor.getString(mCursor.getColumnIndex(RIT_STKUMID)));
                repackIngredients.setRIT_USELOTS(mCursor.getString(mCursor.getColumnIndex(RIT_USELOTS)));
                repackIngredients.setRIT_UMFACT(mCursor.getString(mCursor.getColumnIndex(RIT_UMFACT)));
                repackIngredients.setRIT_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT1)));
                repackIngredients.setRIT_VOLUME(mCursor.getString(mCursor.getColumnIndex(RIT_VOLUME)));
                repackIngredients.setRIT_CATCHWT(mCursor.getString(mCursor.getColumnIndex(RIT_CATCHWT)));
                repackIngredients.setRIT_LOTREFID(mCursor.getString(mCursor.getColumnIndex(RIT_LOTREFID)));
                repackIngredients.setRIT_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(RIT_LOTEXPL)));
                repackIngredients.setRIT_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(RIT_LINESPLIT)));
                repackIngredients.setRIT_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(RIT_TRKQTYPK)));
                repackIngredients.setRIT_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_UPDFLAG)));
                repackIngredients.setRIT_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_ADDFLAG)));
                repackIngredients.setRIT_SLOT(mCursor.getString(mCursor.getColumnIndex(RIT_SLOT)));
                repackIngredients.setRIT_ALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ALLOCQTY)));
                repackIngredients.setRIT_TEMPALLOC(mCursor.getString(mCursor.getColumnIndex(RIT_TEMPALLOC)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }


    public ArrayList<RepackFG> getRepackFGForAlertSave(String item,String tran) {
        ArrayList<RepackFG> repackFGArrayList = new ArrayList<>();
        RepackFG repackFG = null;

        mCursor = mSqlitedb.rawQuery("select * from repackFg where item ='"+item+"' and tranlineNo= '"+tran+"'", null);
        try {
            while (mCursor.moveToNext()) {
                repackFG = new RepackFG();

                repackFG.setREPACKFG_PANO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PANO)));
                repackFG.setREPACKFG_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRANLINENO)));
                repackFG.setREPACKFG_ITEM(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ITEM)));
                repackFG.setREPACKFG_DESCRIP(mCursor.getString(mCursor.getColumnIndex(REPACKFG_DESCRIP)));
                repackFG.setREPACKFG_UMEASUR(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMEASUR)));
                repackFG.setREPACKFG_LOCTID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOCTID)));
                repackFG.setREPACKFG_LOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTNO)));
                repackFG.setREPACKFG_SERIAL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SERIAL)));
                repackFG.setREPACKFG_QTYMADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_QTYMADE)));
                repackFG.setREPACKFG_COST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COST)));
                repackFG.setREPACKFG_PADATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PADATE)));
                repackFG.setREPACKFG_PASTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PASTAT)));
                repackFG.setREPACKFG_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKSTAT)));
                repackFG.setREPACKFG_LCKUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKUSER)));
                repackFG.setREPACKFG_LCKDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKDATE)));
                repackFG.setREPACKFG_LCKTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKTIME)));
                repackFG.setREPACKFG_ADDUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDUSER)));
                repackFG.setREPACKFG_ADDDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDDATE)));
                repackFG.setREPACKFG_ADDTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDTIME)));
                repackFG.setREPACKFG_PRODLBL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PRODLBL)));
                repackFG.setREPACKFG_PACKCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PACKCHG)));
                repackFG.setREPACKFG_WASHCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WASHCHG)));
                repackFG.setREPACKFG_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COUNTRYID)));
                repackFG.setREPACKFG_VENDNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VENDNO)));
                repackFG.setREPACKFG_GRADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_GRADE)));
                repackFG.setREPACKFG_PROJNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PROJNO)));
                repackFG.setREPACKFG_REMARKS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_REMARKS)));
                repackFG.setREPACKFG_LCSTQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCSTQTY)));
                repackFG.setREPACKFG_CASE_PL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CASE_PL)));
                repackFG.setREPACKFG_PALNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALNO)));
                repackFG.setREPACKFG_SETID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SETID)));
                repackFG.setREPACKFG_WEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT)));
                repackFG.setREPACKFG_PALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALLET)));
                repackFG.setREPACKFG_ID_COL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ID_COL)));
                repackFG.setREPACKFG_BINNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BINNO)));
                repackFG.setREPACKFG_POSTPRG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_POSTPRG)));
                repackFG.setREPACKFG_EXTPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTPALLET)));
                repackFG.setREPACKFG_EXTCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTCUBE)));
                repackFG.setREPACKFG_EXTWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTWEIGHT)));
                repackFG.setREPACKFG_BEXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTLCST)));
                repackFG.setREPACKFG_EXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTLCST)));
                repackFG.setREPACKFG_BEXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTFEES)));
                repackFG.setREPACKFG_EXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTFEES)));
                repackFG.setREPACKFG_TPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TPALLET)));
                repackFG.setREPACKFG_TCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TCUBE)));
                repackFG.setREPACKFG_TWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TWEIGHT)));
                repackFG.setREPACKFG_WLOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WLOTNO)));
                repackFG.setREPACKFG_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANLN)));
                repackFG.setREPACKFG_ORIGTRANL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANL)));
                repackFG.setREPACKFG_ORIGDOCLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGDOCLN)));
                repackFG.setREPACKFG_STKUMID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_STKUMID)));
                repackFG.setREPACKFG_USELOTS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USELOTS)));
                repackFG.setREPACKFG_UMFACT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMFACT)));
                repackFG.setREPACKFG_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT1)));
                repackFG.setREPACKFG_VOLUME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VOLUME)));
                repackFG.setREPACKFG_CATCHWT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CATCHWT)));
                repackFG.setREPACKFG_LOTREFID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTREFID)));
                repackFG.setREPACKFG_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTEXPL)));
                repackFG.setREPACKFG_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LINESPLIT)));
                repackFG.setREPACKFG_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRKQTYPK)));
                repackFG.setREPACKFG_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UPDFLAG)));
                repackFG.setREPACKFG_VPLOCKED(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VPLOCKED)));
                repackFG.setREPACKFG_USEDQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USEDQTY)));
                repackFG.setREPACKFG_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDFLAG)));

                repackFGArrayList.add(repackFG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackFGArrayList;
    }


    public ArrayList<RepackFG> getRepackFG_backUp() {
        ArrayList<RepackFG> repackFGArrayList = new ArrayList<>();
        RepackFG repackFG = null;

        mCursor = mSqlitedb.rawQuery("select * from repackFg where qtymade = ''", null);
        try {
            while (mCursor.moveToNext()) {
                repackFG = new RepackFG();

                repackFG.setREPACKFG_PANO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PANO)));
                repackFG.setREPACKFG_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRANLINENO)));
                repackFG.setREPACKFG_ITEM(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ITEM)));
                repackFG.setREPACKFG_DESCRIP(mCursor.getString(mCursor.getColumnIndex(REPACKFG_DESCRIP)));
                repackFG.setREPACKFG_UMEASUR(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMEASUR)));
                repackFG.setREPACKFG_LOCTID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOCTID)));
                repackFG.setREPACKFG_LOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTNO)));
                repackFG.setREPACKFG_SERIAL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SERIAL)));
                repackFG.setREPACKFG_QTYMADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_QTYMADE)));
                repackFG.setREPACKFG_COST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COST)));
                repackFG.setREPACKFG_PADATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PADATE)));
                repackFG.setREPACKFG_PASTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PASTAT)));
                repackFG.setREPACKFG_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKSTAT)));
                repackFG.setREPACKFG_LCKUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKUSER)));
                repackFG.setREPACKFG_LCKDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKDATE)));
                repackFG.setREPACKFG_LCKTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKTIME)));
                repackFG.setREPACKFG_ADDUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDUSER)));
                repackFG.setREPACKFG_ADDDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDDATE)));
                repackFG.setREPACKFG_ADDTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDTIME)));
                repackFG.setREPACKFG_PRODLBL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PRODLBL)));
                repackFG.setREPACKFG_PACKCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PACKCHG)));
                repackFG.setREPACKFG_WASHCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WASHCHG)));
                repackFG.setREPACKFG_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COUNTRYID)));
                repackFG.setREPACKFG_VENDNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VENDNO)));
                repackFG.setREPACKFG_GRADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_GRADE)));
                repackFG.setREPACKFG_PROJNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PROJNO)));
                repackFG.setREPACKFG_REMARKS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_REMARKS)));
                repackFG.setREPACKFG_LCSTQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCSTQTY)));
                repackFG.setREPACKFG_CASE_PL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CASE_PL)));
                repackFG.setREPACKFG_PALNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALNO)));
                repackFG.setREPACKFG_SETID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SETID)));
                repackFG.setREPACKFG_WEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT)));
                repackFG.setREPACKFG_PALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALLET)));
                repackFG.setREPACKFG_ID_COL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ID_COL)));
                repackFG.setREPACKFG_BINNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BINNO)));
                repackFG.setREPACKFG_POSTPRG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_POSTPRG)));
                repackFG.setREPACKFG_EXTPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTPALLET)));
                repackFG.setREPACKFG_EXTCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTCUBE)));
                repackFG.setREPACKFG_EXTWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTWEIGHT)));
                repackFG.setREPACKFG_BEXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTLCST)));
                repackFG.setREPACKFG_EXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTLCST)));
                repackFG.setREPACKFG_BEXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTFEES)));
                repackFG.setREPACKFG_EXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTFEES)));
                repackFG.setREPACKFG_TPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TPALLET)));
                repackFG.setREPACKFG_TCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TCUBE)));
                repackFG.setREPACKFG_TWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TWEIGHT)));
                repackFG.setREPACKFG_WLOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WLOTNO)));
                repackFG.setREPACKFG_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANLN)));
                repackFG.setREPACKFG_ORIGTRANL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANL)));
                repackFG.setREPACKFG_ORIGDOCLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGDOCLN)));
                repackFG.setREPACKFG_STKUMID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_STKUMID)));
                repackFG.setREPACKFG_USELOTS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USELOTS)));
                repackFG.setREPACKFG_UMFACT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMFACT)));
                repackFG.setREPACKFG_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT1)));
                repackFG.setREPACKFG_VOLUME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VOLUME)));
                repackFG.setREPACKFG_CATCHWT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CATCHWT)));
                repackFG.setREPACKFG_LOTREFID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTREFID)));
                repackFG.setREPACKFG_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTEXPL)));
                repackFG.setREPACKFG_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LINESPLIT)));
                repackFG.setREPACKFG_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRKQTYPK)));
                repackFG.setREPACKFG_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UPDFLAG)));
                repackFG.setREPACKFG_VPLOCKED(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VPLOCKED)));
                repackFG.setREPACKFG_USEDQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USEDQTY)));
                repackFG.setREPACKFG_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDFLAG)));

                repackFGArrayList.add(repackFG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackFGArrayList;
    }



    public ArrayList<RepackFG> getRepackFG() {
        ArrayList<RepackFG> repackFGArrayList = new ArrayList<>();
        RepackFG repackFG = null;

        mCursor = mSqlitedb.rawQuery("select * from repackFg where qtymade<> ''", null);
        try {
            while (mCursor.moveToNext()) {
                repackFG = new RepackFG();

                repackFG.setREPACKFG_PANO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PANO)));
                repackFG.setREPACKFG_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRANLINENO)));
                repackFG.setREPACKFG_ITEM(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ITEM)));
                repackFG.setREPACKFG_DESCRIP(mCursor.getString(mCursor.getColumnIndex(REPACKFG_DESCRIP)));
                repackFG.setREPACKFG_UMEASUR(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMEASUR)));
                repackFG.setREPACKFG_LOCTID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOCTID)));
                repackFG.setREPACKFG_LOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTNO)));
                repackFG.setREPACKFG_SERIAL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SERIAL)));
                repackFG.setREPACKFG_QTYMADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_QTYMADE)));
                repackFG.setREPACKFG_COST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COST)));
                repackFG.setREPACKFG_PADATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PADATE)));
                repackFG.setREPACKFG_PASTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PASTAT)));
                repackFG.setREPACKFG_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKSTAT)));
                repackFG.setREPACKFG_LCKUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKUSER)));
                repackFG.setREPACKFG_LCKDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKDATE)));
                repackFG.setREPACKFG_LCKTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCKTIME)));
                repackFG.setREPACKFG_ADDUSER(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDUSER)));
                repackFG.setREPACKFG_ADDDATE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDDATE)));
                repackFG.setREPACKFG_ADDTIME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDTIME)));
                repackFG.setREPACKFG_PRODLBL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PRODLBL)));
                repackFG.setREPACKFG_PACKCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PACKCHG)));
                repackFG.setREPACKFG_WASHCHG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WASHCHG)));
                repackFG.setREPACKFG_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_COUNTRYID)));
                repackFG.setREPACKFG_VENDNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VENDNO)));
                repackFG.setREPACKFG_GRADE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_GRADE)));
                repackFG.setREPACKFG_PROJNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PROJNO)));
                repackFG.setREPACKFG_REMARKS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_REMARKS)));
                repackFG.setREPACKFG_LCSTQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LCSTQTY)));
                repackFG.setREPACKFG_CASE_PL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CASE_PL)));
                repackFG.setREPACKFG_PALNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALNO)));
                repackFG.setREPACKFG_SETID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_SETID)));
                repackFG.setREPACKFG_WEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT)));
                repackFG.setREPACKFG_PALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_PALLET)));
                repackFG.setREPACKFG_ID_COL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ID_COL)));
                repackFG.setREPACKFG_BINNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BINNO)));
                repackFG.setREPACKFG_POSTPRG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_POSTPRG)));
                repackFG.setREPACKFG_EXTPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTPALLET)));
                repackFG.setREPACKFG_EXTCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTCUBE)));
                repackFG.setREPACKFG_EXTWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTWEIGHT)));
                repackFG.setREPACKFG_BEXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTLCST)));
                repackFG.setREPACKFG_EXTLCST(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTLCST)));
                repackFG.setREPACKFG_BEXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_BEXTFEES)));
                repackFG.setREPACKFG_EXTFEES(mCursor.getString(mCursor.getColumnIndex(REPACKFG_EXTFEES)));
                repackFG.setREPACKFG_TPALLET(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TPALLET)));
                repackFG.setREPACKFG_TCUBE(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TCUBE)));
                repackFG.setREPACKFG_TWEIGHT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TWEIGHT)));
                repackFG.setREPACKFG_WLOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WLOTNO)));
                repackFG.setREPACKFG_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANLN)));
                repackFG.setREPACKFG_ORIGTRANL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGTRANL)));
                repackFG.setREPACKFG_ORIGDOCLN(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ORIGDOCLN)));
                repackFG.setREPACKFG_STKUMID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_STKUMID)));
                repackFG.setREPACKFG_USELOTS(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USELOTS)));
                repackFG.setREPACKFG_UMFACT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UMFACT)));
                repackFG.setREPACKFG_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(REPACKFG_WEIGHT1)));
                repackFG.setREPACKFG_VOLUME(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VOLUME)));
                repackFG.setREPACKFG_CATCHWT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_CATCHWT)));
                repackFG.setREPACKFG_LOTREFID(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTREFID)));
                repackFG.setREPACKFG_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTEXPL)));
                repackFG.setREPACKFG_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LINESPLIT)));
                repackFG.setREPACKFG_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(REPACKFG_TRKQTYPK)));
                repackFG.setREPACKFG_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_UPDFLAG)));
                repackFG.setREPACKFG_VPLOCKED(mCursor.getString(mCursor.getColumnIndex(REPACKFG_VPLOCKED)));
                repackFG.setREPACKFG_USEDQTY(mCursor.getString(mCursor.getColumnIndex(REPACKFG_USEDQTY)));
                repackFG.setREPACKFG_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(REPACKFG_ADDFLAG)));

                repackFGArrayList.add(repackFG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackFGArrayList;
    }

    public ArrayList<RepackIngredients> getRepackIngredients() {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where tranlineno >''", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_PANO(mCursor.getString(mCursor.getColumnIndex(RIT_PANO)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_SERIAL(mCursor.getString(mCursor.getColumnIndex(RIT_SERIAL)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));
                repackIngredients.setRIT_COST(mCursor.getString(mCursor.getColumnIndex(RIT_COST)));
                repackIngredients.setRIT_PADATE(mCursor.getString(mCursor.getColumnIndex(RIT_PADATE)));
                repackIngredients.setRIT_PASTAT(mCursor.getString(mCursor.getColumnIndex(RIT_PASTAT)));
                repackIngredients.setRIT_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(RIT_LCKSTAT)));
                repackIngredients.setRIT_LCKUSER(mCursor.getString(mCursor.getColumnIndex(RIT_LCKUSER)));
                repackIngredients.setRIT_LCKDATE(mCursor.getString(mCursor.getColumnIndex(RIT_LCKDATE)));
                repackIngredients.setRIT_LCKTIME(mCursor.getString(mCursor.getColumnIndex(RIT_LCKTIME)));
                repackIngredients.setRIT_ADDUSER(mCursor.getString(mCursor.getColumnIndex(RIT_ADDUSER)));
                repackIngredients.setRIT_ADDDATE(mCursor.getString(mCursor.getColumnIndex(RIT_ADDDATE)));
                repackIngredients.setRIT_ADDTIME(mCursor.getString(mCursor.getColumnIndex(RIT_ADDTIME)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_VENDNO(mCursor.getString(mCursor.getColumnIndex(RIT_VENDNO)));
                repackIngredients.setRIT_BINNO(mCursor.getString(mCursor.getColumnIndex(RIT_BINNO)));
                repackIngredients.setRIT_PALNO(mCursor.getString(mCursor.getColumnIndex(RIT_PALNO)));
                repackIngredients.setRIT_REMARKS(mCursor.getString(mCursor.getColumnIndex(RIT_REMARKS)));
                repackIngredients.setRIT_YIELD(mCursor.getString(mCursor.getColumnIndex(RIT_YIELD)));
                repackIngredients.setRIT_SETID(mCursor.getString(mCursor.getColumnIndex(RIT_SETID)));
                repackIngredients.setRIT_WEIGHT(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT)));
                repackIngredients.setRIT_ID_COL(mCursor.getString(mCursor.getColumnIndex(RIT_ID_COL)));
                repackIngredients.setRIT_WLOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_WLOTNO)));
                repackIngredients.setRIT_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(RIT_ORIGTRANLN)));
                repackIngredients.setRIT_STKUMID(mCursor.getString(mCursor.getColumnIndex(RIT_STKUMID)));
                repackIngredients.setRIT_USELOTS(mCursor.getString(mCursor.getColumnIndex(RIT_USELOTS)));
                repackIngredients.setRIT_UMFACT(mCursor.getString(mCursor.getColumnIndex(RIT_UMFACT)));
                repackIngredients.setRIT_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT1)));
                repackIngredients.setRIT_VOLUME(mCursor.getString(mCursor.getColumnIndex(RIT_VOLUME)));
                repackIngredients.setRIT_CATCHWT(mCursor.getString(mCursor.getColumnIndex(RIT_CATCHWT)));
                repackIngredients.setRIT_LOTREFID(mCursor.getString(mCursor.getColumnIndex(RIT_LOTREFID)));
                repackIngredients.setRIT_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(RIT_LOTEXPL)));
                repackIngredients.setRIT_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(RIT_LINESPLIT)));
                repackIngredients.setRIT_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(RIT_TRKQTYPK)));
                repackIngredients.setRIT_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_UPDFLAG)));
                repackIngredients.setRIT_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_ADDFLAG)));
                repackIngredients.setRIT_SLOT(mCursor.getString(mCursor.getColumnIndex(RIT_SLOT)));
                repackIngredients.setRIT_ALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ALLOCQTY)));
                repackIngredients.setRIT_TEMPALLOC(mCursor.getString(mCursor.getColumnIndex(RIT_TEMPALLOC)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }

    public ArrayList<RepackIngredients> getWlotRepackIngredients(String WlotNum) {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackIngredient where lotrefid = '" + WlotNum + "' ", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_PANO(mCursor.getString(mCursor.getColumnIndex(RIT_PANO)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_SERIAL(mCursor.getString(mCursor.getColumnIndex(RIT_SERIAL)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));
                repackIngredients.setRIT_COST(mCursor.getString(mCursor.getColumnIndex(RIT_COST)));
                repackIngredients.setRIT_PADATE(mCursor.getString(mCursor.getColumnIndex(RIT_PADATE)));
                repackIngredients.setRIT_PASTAT(mCursor.getString(mCursor.getColumnIndex(RIT_PASTAT)));
                repackIngredients.setRIT_LCKSTAT(mCursor.getString(mCursor.getColumnIndex(RIT_LCKSTAT)));
                repackIngredients.setRIT_LCKUSER(mCursor.getString(mCursor.getColumnIndex(RIT_LCKUSER)));
                repackIngredients.setRIT_LCKDATE(mCursor.getString(mCursor.getColumnIndex(RIT_LCKDATE)));
                repackIngredients.setRIT_LCKTIME(mCursor.getString(mCursor.getColumnIndex(RIT_LCKTIME)));
                repackIngredients.setRIT_ADDUSER(mCursor.getString(mCursor.getColumnIndex(RIT_ADDUSER)));
                repackIngredients.setRIT_ADDDATE(mCursor.getString(mCursor.getColumnIndex(RIT_ADDDATE)));
                repackIngredients.setRIT_ADDTIME(mCursor.getString(mCursor.getColumnIndex(RIT_ADDTIME)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_VENDNO(mCursor.getString(mCursor.getColumnIndex(RIT_VENDNO)));
                repackIngredients.setRIT_BINNO(mCursor.getString(mCursor.getColumnIndex(RIT_BINNO)));
                repackIngredients.setRIT_PALNO(mCursor.getString(mCursor.getColumnIndex(RIT_PALNO)));
                repackIngredients.setRIT_REMARKS(mCursor.getString(mCursor.getColumnIndex(RIT_REMARKS)));
                repackIngredients.setRIT_YIELD(mCursor.getString(mCursor.getColumnIndex(RIT_YIELD)));
                repackIngredients.setRIT_SETID(mCursor.getString(mCursor.getColumnIndex(RIT_SETID)));
                repackIngredients.setRIT_WEIGHT(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT)));
                repackIngredients.setRIT_ID_COL(mCursor.getString(mCursor.getColumnIndex(RIT_ID_COL)));
                repackIngredients.setRIT_WLOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_WLOTNO)));
                repackIngredients.setRIT_ORIGTRANLN(mCursor.getString(mCursor.getColumnIndex(RIT_ORIGTRANLN)));
                repackIngredients.setRIT_STKUMID(mCursor.getString(mCursor.getColumnIndex(RIT_STKUMID)));
                repackIngredients.setRIT_USELOTS(mCursor.getString(mCursor.getColumnIndex(RIT_USELOTS)));
                repackIngredients.setRIT_UMFACT(mCursor.getString(mCursor.getColumnIndex(RIT_UMFACT)));
                repackIngredients.setRIT_WEIGHT1(mCursor.getString(mCursor.getColumnIndex(RIT_WEIGHT1)));
                repackIngredients.setRIT_VOLUME(mCursor.getString(mCursor.getColumnIndex(RIT_VOLUME)));
                repackIngredients.setRIT_CATCHWT(mCursor.getString(mCursor.getColumnIndex(RIT_CATCHWT)));
                repackIngredients.setRIT_LOTREFID(mCursor.getString(mCursor.getColumnIndex(RIT_LOTREFID)));
                repackIngredients.setRIT_LOTEXPL(mCursor.getString(mCursor.getColumnIndex(RIT_LOTEXPL)));
                repackIngredients.setRIT_LINESPLIT(mCursor.getString(mCursor.getColumnIndex(RIT_LINESPLIT)));
                repackIngredients.setRIT_TRKQTYPK(mCursor.getString(mCursor.getColumnIndex(RIT_TRKQTYPK)));
                repackIngredients.setRIT_UPDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_UPDFLAG)));
                repackIngredients.setRIT_ADDFLAG(mCursor.getString(mCursor.getColumnIndex(RIT_ADDFLAG)));
                repackIngredients.setRIT_SLOT(mCursor.getString(mCursor.getColumnIndex(RIT_SLOT)));
                repackIngredients.setRIT_ALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ALLOCQTY)));
                repackIngredients.setRIT_TEMPALLOC(mCursor.getString(mCursor.getColumnIndex(RIT_TEMPALLOC)));
                repackIngredients.setRIT_WHQTY(mCursor.getString(mCursor.getColumnIndex(RIT_WHQTY)));
                repackIngredients.setRIT_ICQTY(mCursor.getString(mCursor.getColumnIndex(RIT_ICQTY)));
                repackIngredients.setRIT_RPALLOCQTY(mCursor.getString(mCursor.getColumnIndex(RIT_RPALLOCQTY)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }

    public void updateFlagData(String Item,List<RepackIngredients> repackIngredients,String wLotno,String Flag,String Flag2) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_PANO,repackIngredients.get(0).getRIT_PANO());
            cv.put(WMSDbHelper.RIT_ADDFLAG,Flag);
            cv.put(WMSDbHelper.RIT_UPDFLAG,Flag2);
            cv.put(WMSDbHelper.RIT_TRANLINENO,repackIngredients.get(0).getRIT_TRANLINENO());
            cv.put(WMSDbHelper.RIT_ORIGTRANLN,repackIngredients.get(0).getRIT_TRANLINENO());
            cv.put(WMSDbHelper.RIT_QTYUSED,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_ALLOCQTY,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_TEMPALLOC,repackIngredients.get(0).getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_BINNO,"");
            cv.put(WMSDbHelper.RIT_LOTEXPL,repackIngredients.get(0).getRIT_LOTEXPL());
            cv.put(WMSDbHelper.RIT_LINESPLIT,repackIngredients.get(0).getRIT_LINESPLIT());

           /* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*/
            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wLotno = '" + wLotno + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void updateRawItemQty(String Item,List<RepackIngredients> repackIngredients,String Qty,String Allocqty,String UpdFlag,String Addflag,String TempQty,String tranlinNo) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_WLOTNO,repackIngredients.get(0).getRIT_WLOTNO());
            // cv.put(WMSDbHelper.RIT_PALNO,repackIngredients.get(0).getRIT_PALNO());
            cv.put(WMSDbHelper.RIT_LOTNO,repackIngredients.get(0).getRIT_LOTNO());
            cv.put(WMSDbHelper.RIT_QTYUSED,Qty);
            if(repackIngredients.get(0).getRIT_TRKQTYPK()==null||Double.parseDouble(repackIngredients.get(0).getRIT_TRKQTYPK())<=0){
                cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.get(0).getRIT_QTYUSED());
            }
            cv.put(WMSDbHelper.RIT_UPDFLAG,UpdFlag);
            cv.put(WMSDbHelper.RIT_ADDFLAG,Addflag);
            cv.put(WMSDbHelper.RIT_ALLOCQTY,Allocqty);
            cv.put(WMSDbHelper.RIT_TEMPALLOC,TempQty);

           /* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*/
            /*mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.get(0).getRIT_WLOTNO() + "'", null);*/
            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.get(0).getRIT_WLOTNO() + "' and tranlineNo = '"+tranlinNo+"'  ", null);
            //    + Item + "' and wlotNo = '" + repackIngredients.get(0).getRIT_WLOTNO() + "' and tranlineNo = '1'  ", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void updateAllocQty(String Item,List<RepackIngredients> repackIngredients,String Allocqty,String qtyUsed,String UpdFlag,String Addflag,String TempQty,String tranlinNo) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_QTYUSED,qtyUsed);
            cv.put(WMSDbHelper.RIT_ALLOCQTY,Allocqty);
            cv.put(WMSDbHelper.RIT_UPDFLAG,UpdFlag);
            cv.put(WMSDbHelper.RIT_ADDFLAG,Addflag);
            cv.put(WMSDbHelper.RIT_TEMPALLOC,TempQty);



            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.get(0).getRIT_WLOTNO() + "' and tranlineNo = '"+tranlinNo+"'  ", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public ArrayList<RepackList> getRepackListData() {
        RepackList repackList = null;
        ArrayList<RepackList> repckPickList = new ArrayList<>();

        String query = "select * from " + REPACK_PICKLIST_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {

                repackList = new RepackList();

                repackList.setAddtime(mCursor.getString(0));
                repackList.setLoctid(mCursor.getString(1));
                repackList.setPadate(mCursor.getString(2));
                repackList.setPano(mCursor.getString(3));

                repckPickList.add(repackList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return repckPickList;
    }

    public ArrayList<SalesOrderPickList> getSalesOrderListData() {
        SalesOrderPickList salesOrderPickList = null;
        ArrayList<SalesOrderPickList> mSalesOrderPickList = new ArrayList<>();

        String query = "select * from " + SALES_ORDER_PICKLIST_TABLE;
        mCursor = mSqlitedb.rawQuery(query,null);
        try {
            while (mCursor.moveToNext()) {

                salesOrderPickList = new SalesOrderPickList();

                salesOrderPickList.setSono(mCursor.getString(0));
                salesOrderPickList.setSodate(mCursor.getString(1));
                salesOrderPickList.setCustno(mCursor.getString(2));
                salesOrderPickList.setOrdamt(mCursor.getString(3));

                mSalesOrderPickList.add(salesOrderPickList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 103";
            mMsg = "Failed to load Company id and Company name";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return mSalesOrderPickList;
    }



    public ArrayList<RepackIngredients> getRepackFinishMaterials() {
        ArrayList<RepackIngredients> repackIngredientsArrayList = new ArrayList<>();
        RepackIngredients repackIngredients = null;

        mCursor = mSqlitedb.rawQuery("select * from repackFinishedGoods where  qtyused != ''", null);
        try {
            while (mCursor.moveToNext()) {
                repackIngredients = new RepackIngredients();

                repackIngredients.setRIT_ITEM(mCursor.getString(mCursor.getColumnIndex(RIT_ITEM)));
                repackIngredients.setRIT_TRANLINENO(mCursor.getString(mCursor.getColumnIndex(RIT_TRANLINENO)));
                repackIngredients.setRIT_DESCRIP(mCursor.getString(mCursor.getColumnIndex(RIT_DESCRIP)));
                repackIngredients.setRIT_UMEASUR(mCursor.getString(mCursor.getColumnIndex(RIT_UMEASUR)));
                repackIngredients.setRIT_LOCTID(mCursor.getString(mCursor.getColumnIndex(RIT_LOCTID)));
                repackIngredients.setRIT_LOTNO(mCursor.getString(mCursor.getColumnIndex(RIT_LOTNO)));
                repackIngredients.setRIT_COUNTRYID(mCursor.getString(mCursor.getColumnIndex(RIT_COUNTRYID)));
                repackIngredients.setRIT_QTYUSED(mCursor.getString(mCursor.getColumnIndex(RIT_QTYUSED)));

                repackIngredientsArrayList.add(repackIngredients);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackIngredientsArrayList;
    }

    public void deleteRepackDataFinished()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM repackFg");

        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }

    public ArrayList<RepackFG> getRepackFGLotNo( String item) {
        ArrayList<RepackFG> repackFGArrayList = new ArrayList<>();
        RepackFG repackFG = null;

        mCursor = mSqlitedb.rawQuery("select * from repackFg where item = '"+item+"'", null);
        try {
            while (mCursor.moveToNext()) {
                repackFG = new RepackFG();

                repackFG.setREPACKFG_LOTNO(mCursor.getString(mCursor.getColumnIndex(REPACKFG_LOTNO)));

                repackFGArrayList.add(repackFG);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 101";
            mMsg = "pick List detail save failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        return repackFGArrayList;
    }


    public void updateFinishMaterialListClickQty(String Item,String FinQty,String tranlineno,String adduser) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.REPACKFG_QTYMADE,FinQty);
            if(!adduser.equals("")) {
                cv.put(WMSDbHelper.REPACKFG_UPDFLAG, "1");
            }
            mSqlitedb.update(WMSDbHelper.REPACKFG_TABLE, cv, "item = '"+ Item + "' and tranlineNo = '"+tranlineno+"'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void deleteRepackDataCancel()
    {
        try
        {
            mSqlitedb.execSQL("DELETE FROM repackFg where qtymade= ''");

        }
        catch (Exception e)
        {
            LogfileCreator.mAppendLog(e.getMessage());
        }
    }
    public int mTranlineCountRepackFG() {

        int Count = 0;

        mCursor = mSqlitedb.rawQuery("select max(CAST(tranlineNo AS INTEGER)) as tranlineNo from repackFG", null);

        try {
            while (mCursor.moveToNext()) {
                Count = Integer.parseInt(mCursor.getString(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "selection Tranline Count failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
        mCursor.close();
        return Count;
    }

    public void updateFinishMaterialQty(String Item,String FinQty,String tranlineno,String pano) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.REPACKFG_QTYMADE,FinQty);
            cv.put(WMSDbHelper.REPACKFG_TRANLINENO,tranlineno);
            cv.put(WMSDbHelper.REPACKFG_PANO,pano);
            cv.put(WMSDbHelper.REPACKFG_ADDFLAG,"1");

            mSqlitedb.update(WMSDbHelper.REPACKFG_TABLE, cv, "item = '"+ Item + "' and qtymade = ''", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }
    public void addRepackFG(RepackFG repackFG) {

        try {
            //mSqlitedb.execSQL("DELETE FROM " + REPACKFG_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(REPACKFG_PANO,Globals.RepackNum.trim());
           // cv.put(REPACKFG_PANO, repackFG.getREPACKFG_PANO());
            cv.put(REPACKFG_TRANLINENO, repackFG.getREPACKFG_TRANLINENO());
            cv.put(REPACKFG_ITEM, repackFG.getREPACKFG_ITEM());
            cv.put(REPACKFG_DESCRIP, repackFG.getREPACKFG_DESCRIP());
            cv.put(REPACKFG_UMEASUR, repackFG.getREPACKFG_UMEASUR());
            cv.put(REPACKFG_LOCTID, repackFG.getREPACKFG_LOCTID());

            /*if(repackFG.getREPACKFG_LOTNO().equals("")) {
                cv.put(REPACKFG_LOTNO, Globals.RepackNum);
            }else{
                cv.put(REPACKFG_LOTNO, repackFG.getREPACKFG_LOTNO());
            }*/
         if (repackFG.getREPACKFG_LOTNO()!=null){
             cv.put(REPACKFG_LOTNO, repackFG.getREPACKFG_LOTNO());
         }else {
             cv.put(REPACKFG_LOTNO,"");
         }

            cv.put(REPACKFG_SERIAL, repackFG.getREPACKFG_SERIAL());

            if (repackFG.getREPACKFG_QTYMADE() != null){
                cv.put(REPACKFG_QTYMADE, repackFG.getREPACKFG_QTYMADE());
            }else {
                cv.put(REPACKFG_QTYMADE, "");
            }
            cv.put(REPACKFG_COST, repackFG.getREPACKFG_COST());
            cv.put(REPACKFG_PADATE, repackFG.getREPACKFG_PADATE());
            cv.put(REPACKFG_PASTAT, repackFG.getREPACKFG_PASTAT());
            cv.put(REPACKFG_LCKSTAT, repackFG.getREPACKFG_LCKSTAT());
            cv.put(REPACKFG_LCKUSER, repackFG.getREPACKFG_LCKUSER());
            cv.put(REPACKFG_LCKDATE, repackFG.getREPACKFG_LCKDATE());
            cv.put(REPACKFG_LCKTIME, repackFG.getREPACKFG_LCKTIME());
            if (repackFG.getREPACKFG_ADDUSER() != null) {
                cv.put(REPACKFG_ADDUSER, repackFG.getREPACKFG_ADDUSER());
            }else{
                cv.put(REPACKFG_ADDUSER,"");
            }
            cv.put(REPACKFG_ADDDATE, repackFG.getREPACKFG_ADDDATE());
            cv.put(REPACKFG_ADDTIME, repackFG.getREPACKFG_ADDTIME());
            cv.put(REPACKFG_PRODLBL, repackFG.getREPACKFG_PRODLBL());
            cv.put(REPACKFG_PACKCHG, repackFG.getREPACKFG_PACKCHG());
            cv.put(REPACKFG_WASHCHG, repackFG.getREPACKFG_WASHCHG());
            cv.put(REPACKFG_COUNTRYID, repackFG.getREPACKFG_COUNTRYID());
            cv.put(REPACKFG_VENDNO, repackFG.getREPACKFG_VENDNO());
            cv.put(REPACKFG_GRADE, repackFG.getREPACKFG_GRADE());
            cv.put(REPACKFG_PROJNO, repackFG.getREPACKFG_PROJNO());
            cv.put(REPACKFG_REMARKS, repackFG.getREPACKFG_REMARKS());
            cv.put(REPACKFG_LCSTQTY, repackFG.getREPACKFG_LCSTQTY());
            cv.put(REPACKFG_CASE_PL, repackFG.getREPACKFG_CASE_PL());
            cv.put(REPACKFG_PALNO, repackFG.getREPACKFG_PALNO());
            cv.put(REPACKFG_SETID, repackFG.getREPACKFG_SETID());
            cv.put(REPACKFG_WEIGHT, repackFG.getREPACKFG_WEIGHT());
            cv.put(REPACKFG_PALLET, repackFG.getREPACKFG_PALLET());
            cv.put(REPACKFG_ID_COL, repackFG.getREPACKFG_ID_COL());
            cv.put(REPACKFG_BINNO, repackFG.getREPACKFG_BINNO());
            cv.put(REPACKFG_POSTPRG, repackFG.getREPACKFG_POSTPRG());
            cv.put(REPACKFG_EXTPALLET, repackFG.getREPACKFG_EXTPALLET());
            cv.put(REPACKFG_EXTCUBE, repackFG.getREPACKFG_EXTCUBE());
            cv.put(REPACKFG_EXTWEIGHT, repackFG.getREPACKFG_EXTWEIGHT());
            cv.put(REPACKFG_BEXTLCST, repackFG.getREPACKFG_BEXTLCST());
            cv.put(REPACKFG_EXTLCST, repackFG.getREPACKFG_EXTLCST());
            cv.put(REPACKFG_BEXTFEES, repackFG.getREPACKFG_BEXTFEES());
            cv.put(REPACKFG_EXTFEES, repackFG.getREPACKFG_EXTFEES());
            cv.put(REPACKFG_TPALLET, repackFG.getREPACKFG_TPALLET());
            cv.put(REPACKFG_TCUBE, repackFG.getREPACKFG_TCUBE());
            cv.put(REPACKFG_TWEIGHT, repackFG.getREPACKFG_TWEIGHT());
            cv.put(REPACKFG_WLOTNO, repackFG.getREPACKFG_WLOTNO());
            cv.put(REPACKFG_ORIGTRANLN, repackFG.getREPACKFG_ORIGTRANLN());
            cv.put(REPACKFG_ORIGTRANL, repackFG.getREPACKFG_ORIGTRANL());
            cv.put(REPACKFG_ORIGDOCLN, repackFG.getREPACKFG_ORIGDOCLN());
            cv.put(REPACKFG_STKUMID, repackFG.getREPACKFG_STKUMID());
            cv.put(REPACKFG_USELOTS, repackFG.getREPACKFG_USELOTS());
            cv.put(REPACKFG_UMFACT, repackFG.getREPACKFG_UMFACT());
            cv.put(REPACKFG_WEIGHT1, repackFG.getREPACKFG_WEIGHT1());
            cv.put(REPACKFG_VOLUME, repackFG.getREPACKFG_VOLUME());
            cv.put(REPACKFG_CATCHWT, repackFG.getREPACKFG_CATCHWT());
            cv.put(REPACKFG_LOTREFID, repackFG.getREPACKFG_LOTREFID());
            cv.put(REPACKFG_LOTEXPL, repackFG.getREPACKFG_LOTEXPL());
            cv.put(REPACKFG_LINESPLIT, repackFG.getREPACKFG_LINESPLIT());
            cv.put(REPACKFG_TRKQTYPK, repackFG.getREPACKFG_TRKQTYPK());
            cv.put(REPACKFG_UPDFLAG, repackFG.getREPACKFG_UPDFLAG());
            cv.put(REPACKFG_VPLOCKED, repackFG.getREPACKFG_VPLOCKED());
            cv.put(REPACKFG_DECNUM, repackFG.getREPACKFG_DECNUM());
            cv.put(REPACKFG_USEDQTY, "");
            cv.put(REPACKFG_ADDFLAG, "0");

            mSqlitedb.insert(REPACKFG_TABLE, null, cv);
            LogfileCreator.mAppendLog("RepackFG inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "RepackFG insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }


    public void updateEmptyItem(String Item,RepackIngredients repackIngredients) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_WLOTNO,repackIngredients.getRIT_WLOTNO());
            //cv.put(WMSDbHelper.RIT_PALNO,repackIngredients.getRIT_PALNO());
            if (repackIngredients.getRIT_LOTNO()!=null){
                cv.put(WMSDbHelper.RIT_LOTNO,repackIngredients.getRIT_LOTNO());
            }else{
                cv.put(WMSDbHelper.RIT_LOTNO,"");
            }

            if(repackIngredients.getRIT_TRKQTYPK()==null){
                cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.getRIT_QTYUSED());
            }
            //cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.getRIT_TRKQTYPK());
            cv.put(WMSDbHelper.RIT_SLOT,repackIngredients.getRIT_SLOT());
            cv.put(WMSDbHelper.RIT_LOTREFID,repackIngredients.getRIT_LOTREFID());
            cv.put(WMSDbHelper.RIT_REMARKS,repackIngredients.getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_RPALLOCQTY,repackIngredients.getRIT_RPALLOCQTY());
            cv.put(WMSDbHelper.RIT_WHQTY,repackIngredients.getRIT_WHQTY());
            cv.put(WMSDbHelper.RIT_ICQTY,repackIngredients.getRIT_ICQTY());
            cv.put(RIT_ORGPANO, repackIngredients.getRIT_ORIGPANO());


            /*mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = ''", null);*/


            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "tranlineno = (select MIN(tranlineno) FROM repackIngredient WHERE item = '"
                    + Item + "' and wlotNo = '')", null);


            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void updateRawItem(String Item,RepackIngredients repackIngredients) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(WMSDbHelper.RIT_WLOTNO,repackIngredients.getRIT_WLOTNO());
            //cv.put(WMSDbHelper.RIT_PALNO,repackIngredients.getRIT_PALNO());
            if (repackIngredients.getRIT_LOTNO()!=null){
                cv.put(WMSDbHelper.RIT_LOTNO,repackIngredients.getRIT_LOTNO());
            }else{
                cv.put(WMSDbHelper.RIT_LOTNO,"");
            }

            if(repackIngredients.getRIT_TRKQTYPK()==null){
                cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.getRIT_QTYUSED());
            }
            //cv.put(WMSDbHelper.RIT_TRKQTYPK,repackIngredients.getRIT_TRKQTYPK());
            cv.put(WMSDbHelper.RIT_SLOT,repackIngredients.getRIT_SLOT());
            cv.put(WMSDbHelper.RIT_LOTREFID,repackIngredients.getRIT_LOTREFID());
            cv.put(WMSDbHelper.RIT_REMARKS,repackIngredients.getRIT_QTYUSED());
            cv.put(WMSDbHelper.RIT_RPALLOCQTY,repackIngredients.getRIT_RPALLOCQTY());
            cv.put(WMSDbHelper.RIT_WHQTY,repackIngredients.getRIT_WHQTY());
            cv.put(WMSDbHelper.RIT_ICQTY,repackIngredients.getRIT_ICQTY());
            cv.put(RIT_ORGPANO, repackIngredients.getRIT_ORIGPANO());

           /* mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + wLotNo + "'", null);*/
            mSqlitedb.update(WMSDbHelper.REPACK_INGREDIENT_TABLE, cv, "item = '"
                    + Item + "' and wlotNo = '" + repackIngredients.getRIT_WLOTNO() + "'", null);
                    //+ Item + "' and lotNo = '" + repackIngredients.getRIT_WLOTNO() + "'", null);

            Log.i("ReceiveTask LoadType: ", "ReceiveTask LoadType record updated successfully...");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 102";
            mMsg = "ReceiveTask LoadType update selection failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addRepackIngredients(RepackIngredients repackIngredients) {

        try {
            // mSqlitedb.execSQL("DELETE FROM " + REPACK_INGREDIENT_TABLE);
            ContentValues cv = new ContentValues();

            cv.put(RIT_PANO, Globals.RecallLstDTE_code);
          //  cv.put(RIT_PANO, repackIngredients.getRIT_PANO());
            /*if (repackIngredients.getRIT_TRANLINENO() == null){
                cv.put(RIT_TRANLINENO, "");
            }else {
                cv.put(RIT_TRANLINENO, repackIngredients.getRIT_TRANLINENO());
            }*/
            cv.put(RIT_TRANLINENO, repackIngredients.getRIT_TRANLINENO());
            cv.put(RIT_ITEM, repackIngredients.getRIT_ITEM());
            cv.put(RIT_DESCRIP, repackIngredients.getRIT_DESCRIP());
            cv.put(RIT_UMEASUR, repackIngredients.getRIT_UMEASUR());
            cv.put(RIT_LOCTID, repackIngredients.getRIT_LOCTID());
            cv.put(RIT_LOTNO, repackIngredients.getRIT_LOTNO());
            cv.put(RIT_SERIAL, repackIngredients.getRIT_SERIAL());
            cv.put(RIT_QTYUSED, repackIngredients.getRIT_QTYUSED());
            cv.put(RIT_COST, repackIngredients.getRIT_COST());
            cv.put(RIT_PADATE, repackIngredients.getRIT_PADATE());
            cv.put(RIT_PASTAT, repackIngredients.getRIT_PASTAT());
            cv.put(RIT_LCKSTAT, repackIngredients.getRIT_LCKSTAT());
            cv.put(RIT_LCKUSER, repackIngredients.getRIT_LCKUSER());
            cv.put(RIT_LCKDATE, repackIngredients.getRIT_LCKDATE());
            cv.put(RIT_LCKTIME, repackIngredients.getRIT_LCKTIME());
            cv.put(RIT_ADDUSER, repackIngredients.getRIT_ADDUSER());
            cv.put(RIT_ADDDATE, repackIngredients.getRIT_ADDDATE());
            cv.put(RIT_ADDTIME, repackIngredients.getRIT_ADDTIME());
            cv.put(RIT_COUNTRYID, repackIngredients.getRIT_COUNTRYID());
            cv.put(RIT_VENDNO, repackIngredients.getRIT_VENDNO());
            cv.put(RIT_BINNO, repackIngredients.getRIT_BINNO());
            cv.put(RIT_PALNO, repackIngredients.getRIT_PALNO());
            cv.put(RIT_REMARKS, repackIngredients.getRIT_REMARKS());
            cv.put(RIT_YIELD, repackIngredients.getRIT_YIELD());
            cv.put(RIT_SETID, repackIngredients.getRIT_SETID());
            cv.put(RIT_WEIGHT, repackIngredients.getRIT_WEIGHT());
            cv.put(RIT_ID_COL, repackIngredients.getRIT_ID_COL());
            cv.put(RIT_WLOTNO, repackIngredients.getRIT_WLOTNO());
            cv.put(RIT_ORIGTRANLN, repackIngredients.getRIT_ORIGTRANLN());
            cv.put(RIT_STKUMID, repackIngredients.getRIT_STKUMID());
            cv.put(RIT_USELOTS, repackIngredients.getRIT_USELOTS());
            cv.put(RIT_UMFACT, repackIngredients.getRIT_UMFACT());
            cv.put(RIT_WEIGHT1, repackIngredients.getRIT_WEIGHT1());
            cv.put(RIT_VOLUME, repackIngredients.getRIT_VOLUME());
            cv.put(RIT_CATCHWT, repackIngredients.getRIT_CATCHWT());
            cv.put(RIT_LOTREFID, repackIngredients.getRIT_LOTREFID());
            cv.put(RIT_LOTEXPL, repackIngredients.getRIT_LOTEXPL());
            cv.put(RIT_LINESPLIT, repackIngredients.getRIT_LINESPLIT());
            cv.put(RIT_TRKQTYPK, repackIngredients.getRIT_TRKQTYPK());
            cv.put(RIT_UPDFLAG, repackIngredients.getRIT_UPDFLAG());
            cv.put(RIT_ADDFLAG, repackIngredients.getRIT_ADDFLAG());
            cv.put(RIT_SLOT, repackIngredients.getRIT_SLOT());
            cv.put(RIT_ALLOCQTY, repackIngredients.getRIT_ALLOCQTY());
            cv.put(RIT_TEMPALLOC, repackIngredients.getRIT_TEMPALLOC());
            cv.put(RIT_WHQTY, repackIngredients.getRIT_WHQTY());
            cv.put(RIT_ICQTY, repackIngredients.getRIT_ICQTY());
            cv.put(RIT_RPALLOCQTY, repackIngredients.getRIT_RPALLOCQTY());
            cv.put(RIT_ORGPANO, repackIngredients.getRIT_ORIGPANO());

            mSqlitedb.insert(REPACK_INGREDIENT_TABLE, null, cv);
            LogfileCreator.mAppendLog("repackIngredients inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = "repackIngredients insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }

    public void addRepackList(RepackList repackList) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(RPL_PANO, repackList.getPano());
            cv.put(RPL_ADDTIME, repackList.getAddtime());
            cv.put(RPL_LOCTID, repackList.getLoctid());
            cv.put(RPL_PAFDATE, repackList.getPadate());


            mSqlitedb.insert(REPACK_PICKLIST_TABLE, null, cv);
            LogfileCreator.mAppendLog("RepackPickList inserted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            mErrCode = "Error 201";
            mMsg = " data insert failed";
            mErrMsg = mErrCode + " : " + mMsg;
            LogfileCreator.mAppendLog(mErrMsg);
            mSqlitedb.close();
        }
    }




}
