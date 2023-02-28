package com.silvercreek.wmspickingclient.util;

import android.content.Context;

import com.silvercreek.wmspickingclient.model.BreakerUOMList;
import com.silvercreek.wmspickingclient.model.BreakerUomUtility;
import com.silvercreek.wmspickingclient.model.MoveManually;
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

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
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
import com.silvercreek.wmspickingclient.model.receivetaskheader;
import com.silvercreek.wmspickingclient.model.receivetaskitemclass;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.model.receivetaskloadtype;
import com.silvercreek.wmspickingclient.model.receivetaskprintdetail;
import com.silvercreek.wmspickingclient.model.sessiondetail;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * @author tivm Class to help data loading to database
 */
public class DataLoader {

    private Context mContext;
    private String mCurrentTable;
    private WMSDbHelper mDbHelpher;
    private String mSalespersonFolder;
    private List<RepackIngredients> ingredientsList;

    public DataLoader(Context context, WMSDbHelper dbHelpher,
                      String salespersonFolder) {
        this.mContext = context;
        this.mDbHelpher = dbHelpher;
        this.mSalespersonFolder = salespersonFolder;
    }

    public String[] parseDocument(InputStream inputStream) {

        String[] resultArray = new String[2];
        String result = "";
        String errorMsg = "";

        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            XmlHandeler h = new XmlHandeler();
            xmlReader.setContentHandler(h);
            InputSource inputSource = new InputSource();
            inputSource.setEncoding("UTF-8");
            inputSource.setByteStream(inputStream);
            xmlReader.parse(inputSource);

            result = "success";
        } catch (SAXException se) {
            se.printStackTrace();
            result = "parsing error";
            errorMsg = se.getMessage();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            result = "parsing error";
            errorMsg = pce.getMessage();
        } catch (IOException ie) {
            ie.printStackTrace();
            result = "parsing error";
            errorMsg = ie.getMessage();
        }

        resultArray[0] = result;
        resultArray[1] = errorMsg;

        return resultArray;
    }

    class XmlHandeler extends DefaultHandler {

        private String value;
        private String tempVal;

        private configsettings tSettings;
        private company tCompany;
        private location tLocation;
        private sessiondetail tSessiondetail;
        private notificationcount tNotificationCount;
        private menulist tmenulist;
        private picktasklist tpicktasklist;
        private picktaskheader tpicktaskheader;
        private picktaskdetail tpicktaskdetail;
        private picktaskWHIPTL tpicktaskWHIPTL;
        private picktaskWHMLOT tpicktaskWHMLOT;
        private picktaskWHMSLT tpicktaskWHMSLT;
        private picktaskWHMQTY tpicktaskWHMQTY;
        private picktaskWHITRLS tpicktaskWHITRLS;
        private loadpickpalletSummary tloadpickpalletSummary;
        private loadpickpalletDetails tloadpickpalletDetails;
        private loadpickpalletRouteDetails tloadpickpalletRouteDetails;
        private loadpickpalletWHIPLT tloadpickpalletWHIPLT;
        private loadpickpalletWHITRL tloadpickpalletWHITRL;
        private picktaskPrintlabel tpicktaskPrintlabel;
        private receivetasklist treceivetasklist;
        private receivetaskheader treceivetaskheader;
        private receivetaskdetail treceivetaskdetail;
        private receivetaskitemclass treceivetaskitemclass;
        private receivetaskloadtype treceivetaskloadtype;
        private receivetaskWHMSLT treceivetaskWHMSLT;
        private receivetaskWHRPLT treceivetaskWHRPLT;
        private receivetaskprintdetail treceivetaskprintdetail;
        private physicalcountSlot tphysicalcountSlot;
        private physicalcountDetail tphysicalcountDetail;
        private physicalcountUom tphysicalcountUom;
        private physicalcountWHMLOT tphysicalcountWHMLOT;
        private physicalcountWHMQTY tphysicalcountWHMQTY;
        private physicalcountICITEM tphysicalcountICITEM;
        private MoveManually tMoveManually;
        private BreakerUomUtility tbreakerUomUtility;
        private BreakerUOMList tbreakerUOMList;
        private PickTaskScanPallet tPickTaskScanPallet;
        private SlotList tSlotList;
        private Movetasklist tmovetasklist;
        private movetaskdetail tmovetaskdetail;
        private MoveTaskSlotList tMoveTaskSlotList;
        private ResetItemDetails tResetItemDetails;
        private RepackFG tRepackFG;
        private RepackIngredients tRepackIngredients;
        private RepackList tRepackList;
        private SalesOrderPickList tSalesOrderPickList;
        private SalesOrderCategoryList tSalesOrderCategoryList;
        private PrintPalletTag tPrintPalletTag;

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {

            super.startElement(uri, localName, qName, attributes);
            String smallLocalName = localName.toLowerCase();

            value = "";
            tempVal = "";

            System.out.println("Start:" + localName);

            if (smallLocalName.equalsIgnoreCase("ConfigSettings")) {
                tSettings = new configsettings();
                mCurrentTable = "settings";
            } else if (smallLocalName.equalsIgnoreCase("Companies")) {
                tCompany = new company();
                mCurrentTable = "company";
            } else if (smallLocalName.equalsIgnoreCase("Location")) {
                tLocation = new location();
                mCurrentTable = "location";
            }  else if (smallLocalName.equalsIgnoreCase("Acknowledgement")) {
                tSessiondetail = new sessiondetail();
                mCurrentTable = "sessiondetail";
            } else if (smallLocalName.equalsIgnoreCase("NotificationCount")) {
                tNotificationCount = new notificationcount();
                mCurrentTable = "notificationcount";
            } else if (smallLocalName.equalsIgnoreCase("MenuList")) {
                tmenulist = new menulist();
                mCurrentTable = "menulist";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskList")) {
                tpicktasklist = new picktasklist();
                mCurrentTable = "picktasklist";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskHeader")) {
                tpicktaskheader = new picktaskheader();
                mCurrentTable = "picktaskheader";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskDetails")) {
                tpicktaskdetail = new picktaskdetail();
                mCurrentTable = "picktaskdetail";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskWLOTInfo")) {
                tpicktaskdetail = new picktaskdetail();
                mCurrentTable = "picktaskdetail";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskWHIPTLS")) {
                tpicktaskWHIPTL = new picktaskWHIPTL();
                mCurrentTable = "picktaskWHIPTL";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskWHMLOTS")) {
                tpicktaskWHMLOT = new picktaskWHMLOT();
                mCurrentTable = "picktaskWHMLOT";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskWHMSLTS")) {
                tpicktaskWHMSLT = new picktaskWHMSLT();
                mCurrentTable = "picktaskWHMSLT";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskWHMQTYS")) {
                tpicktaskWHMQTY = new picktaskWHMQTY();
                mCurrentTable = "picktaskWHMQTY";
            } else if (smallLocalName.equalsIgnoreCase("PickTaskWHITRLS")) {
                tpicktaskWHITRLS = new picktaskWHITRLS();
                mCurrentTable = "picktaskWHITRLS";
            } else if (smallLocalName.equalsIgnoreCase("picktaskPrintlabel")) {
                tpicktaskPrintlabel = new picktaskPrintlabel();
                mCurrentTable = "picktaskPrintlabel";
            }

            else if (smallLocalName.equalsIgnoreCase("LoadPickedPalletsSummary")) {
                tloadpickpalletSummary = new loadpickpalletSummary();
                mCurrentTable = "loadpickpalletSummary";
            } else if (smallLocalName.equalsIgnoreCase("LoadPickedPalletsDetails")) {
                tloadpickpalletDetails = new loadpickpalletDetails();
                mCurrentTable = "loadpickpalletDetails";
            } else if (smallLocalName.equalsIgnoreCase("LoadPickedPalletsRouteDetails")) {
                tloadpickpalletRouteDetails = new loadpickpalletRouteDetails();
                mCurrentTable = "loadpickpalletRouteDetails";
            } else if (smallLocalName.equalsIgnoreCase("LoadPickedPalletsWHIPLTS")) {
                tloadpickpalletWHIPLT = new loadpickpalletWHIPLT();
                mCurrentTable = "loadpickpalletWHIPLT";
            } else if (smallLocalName.equalsIgnoreCase("LoadPickedPalletsWHITRLS")) {
                tloadpickpalletWHITRL = new loadpickpalletWHITRL();
                mCurrentTable = "loadpickpalletWHITRL";
            }

            else if (smallLocalName.equalsIgnoreCase("ReceiveTaskList")) {
                treceivetasklist = new receivetasklist();
                mCurrentTable = "receivetasklist";
            }   else if (smallLocalName.equalsIgnoreCase("ReceiveTaskHeader")) {
                treceivetaskheader = new receivetaskheader();
                mCurrentTable = "receivetaskheader";
            }   else if (smallLocalName.equalsIgnoreCase("ReceiveTaskDetails")) {
                treceivetaskdetail = new receivetaskdetail();
                mCurrentTable = "receivetaskdetail";
            }   else if (smallLocalName.equalsIgnoreCase("ReceiveTaskItmclsslists")) {
                treceivetaskitemclass = new receivetaskitemclass();
                mCurrentTable = "receivetaskitemclass";
            }   else if (smallLocalName.equalsIgnoreCase("ReceiveTaskLoadtypelist")) {
                treceivetaskloadtype = new receivetaskloadtype();
                mCurrentTable = "receivetaskloadtype";
            }   else if (smallLocalName.equalsIgnoreCase("ReceiveTaskWHMSLTS")) {
                treceivetaskWHMSLT = new receivetaskWHMSLT();
                mCurrentTable = "receivetaskWHMSLT";
            }   else if (smallLocalName.equalsIgnoreCase("ReceiveTaskWHRPLTS")) {
                treceivetaskWHRPLT = new receivetaskWHRPLT();
                mCurrentTable = "receivetaskWHRPLT";
            } else if (smallLocalName.equalsIgnoreCase("ReceiveTaskPrint")) {
                treceivetaskprintdetail = new receivetaskprintdetail();
                mCurrentTable = "receivetaskprintdetail";
            }

            else if (smallLocalName.equalsIgnoreCase("PhysicalCountSlotList")) {
                tphysicalcountSlot = new physicalcountSlot();
                mCurrentTable = "physicalcountSlot";
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCountDetailList")) {
                tphysicalcountDetail = new physicalcountDetail();
                mCurrentTable = "physicalcountDetail";
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCountUOMList")) {
                tphysicalcountUom = new physicalcountUom();
                mCurrentTable = "physicalcountUom";
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCountWHMLOTList")) {
                tphysicalcountWHMLOT = new physicalcountWHMLOT();
                mCurrentTable = "physicalcountWHMLOT";
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCountWHMQTYList")) {
                tphysicalcountWHMQTY = new physicalcountWHMQTY();
                mCurrentTable = "physicalcountWHMQTY";
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCountICITEMList")) {
                tphysicalcountICITEM = new physicalcountICITEM();
                mCurrentTable = "physicalcountICITEM";
            } else if (smallLocalName.equalsIgnoreCase("documents")) {
                tMoveManually = new MoveManually();
                mCurrentTable = "moveManuallyTable";
            } else if (smallLocalName.equalsIgnoreCase("BreakerUomWHMQTY")) {
                tbreakerUomUtility = new BreakerUomUtility();
                mCurrentTable = "breakerUOMTable";
            } else if (smallLocalName.equalsIgnoreCase("ICITEMUomWHMQTY")) {
                tbreakerUOMList = new BreakerUOMList();
                mCurrentTable = "breakerUOMListTable";
            }
            else if (smallLocalName.equalsIgnoreCase("pickitem")) {
                //tbreakerUOMList = new BreakerUOMList();
                tPickTaskScanPallet = new PickTaskScanPallet();
                mCurrentTable = "PickTask_ScanPallet";

            }else if (smallLocalName.equalsIgnoreCase("ItemSlotList")) {
                //tbreakerUOMList = new BreakerUOMList();
                tSlotList = new SlotList();
                mCurrentTable = "receiveSlotList";

            }else if (smallLocalName.equalsIgnoreCase("MoveTaskList")) {
                // Move Task List
                tmovetasklist = new Movetasklist();
                mCurrentTable = "moveTaskList";
            }else if (smallLocalName.equalsIgnoreCase("MoveTaskDetails")) {
                // Move Task Details
                tmovetaskdetail = new movetaskdetail();
                mCurrentTable = "movetaskdetail";
            }  else if (smallLocalName.equalsIgnoreCase("MoveTaskWHMSLTS")) {
                // Move Task SlotList
                tMoveTaskSlotList = new MoveTaskSlotList();
                mCurrentTable = "movetaskslotlist";
            }else if (smallLocalName.equalsIgnoreCase("Resetiteminfo")) {
                // Move Task SlotList
                tResetItemDetails = new ResetItemDetails();
                mCurrentTable = "Resetitemdetail";
            }else if (smallLocalName.equalsIgnoreCase("RepackFg")) {
                //tbreakerUOMList = new BreakerUOMList();
                tRepackFG = new RepackFG();
                mCurrentTable = "RepackFg";
            } else if (smallLocalName.equalsIgnoreCase("RepackIngredient")||smallLocalName.equalsIgnoreCase("Pickiteminfo")) {
                //tbreakerUOMList = new BreakerUOMList();
                tRepackIngredients = new RepackIngredients();
                mCurrentTable = "RepackIngredient";
            }else if (smallLocalName.equalsIgnoreCase("Repack")) {
                //tbreakerUOMList = new BreakerUOMList();
                tRepackList = new RepackList();
                mCurrentTable = "RepackList";
            }else if (smallLocalName.equalsIgnoreCase("Repack")||smallLocalName.equalsIgnoreCase("Finiteminfo")) {
                //tbreakerUOMList = new BreakerUOMList();
                tRepackFG = new RepackFG();
                mCurrentTable = "RepackFg";
            }else if (smallLocalName.equalsIgnoreCase("SoCharges")) {
                //tbreakerUOMList = new BreakerUOMList();
                tSalesOrderPickList = new SalesOrderPickList();
                mCurrentTable = "SalesOrderPicklist";
            }else if (smallLocalName.equalsIgnoreCase("CategoryList")) {
                //tbreakerUOMList = new BreakerUOMList();
                tSalesOrderCategoryList = new SalesOrderCategoryList();
                mCurrentTable = "SoChargesCategory";
            }else if (smallLocalName.equalsIgnoreCase("Pallettag")) {
                tPrintPalletTag = new PrintPalletTag();
                mCurrentTable = "PrintPalletTagDetails";
            }



        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            value = new String(ch, start, length);
            tempVal = tempVal + value;
            tempVal = tempVal.trim();
        }

        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {

            System.out.println("Value:" + tempVal);
            System.out.println("End:" + localName);

            String smallLocalName = localName.toLowerCase();

            if (smallLocalName.equalsIgnoreCase("settings")
                    && mCurrentTable.equals("settings")) {

                // save Configuration Settings in db
                mDbHelpher.addConfigSettingsData(tSettings);

            } else if (smallLocalName.equalsIgnoreCase("configSettingsID;")
                    && mCurrentTable.equals("settings")) {
                tSettings.setID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("appName")
                    && mCurrentTable.equals("settings")) {
                tSettings.setAppName(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("appDesc")
                    && mCurrentTable.equals("settings")) {
                tSettings.setAppDesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("installationDate")
                    && mCurrentTable.equals("settings")) {
                tSettings.setInstallationDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("expDate")
                    && mCurrentTable.equals("settings")) {
                tSettings.setExpDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("noOfDays")
                    && mCurrentTable.equals("settings")) {
                tSettings.setNoOfDays(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("deviceId")
                    && mCurrentTable.equals("settings")) {
                tSettings.setDeviceId(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("username")
                    && mCurrentTable.equals("settings")) {
                tSettings.setUsername(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("password")
                    && mCurrentTable.equals("settings")) {
                tSettings.setPassword(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("adminPassword")
                    && mCurrentTable.equals("settings")) {
                tSettings.setAdminPassword(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("sessionId")
                    && mCurrentTable.equals("settings")) {
                tSettings.setSessionId(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("currentCompany")
                    && mCurrentTable.equals("settings")) {
                tSettings.setCurrentCompany(tempVal);
            }

            if (smallLocalName.equalsIgnoreCase("Companies")
                    && mCurrentTable.equals("company")) {

                // save Company values in db
                mDbHelpher.addCompanyData(tCompany);

            } else if (smallLocalName.equalsIgnoreCase("compid")
                    && mCurrentTable.equals("company")) {
                tCompany.setCompanyID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Company")
                    && mCurrentTable.equals("company")) {
                tCompany.setCompanyName(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("DbName")
                    && mCurrentTable.equals("company")) {
                tCompany.setCompanyDatabase(tempVal);
            }


            if (smallLocalName.equalsIgnoreCase("Location")
                    && mCurrentTable.equals("location")) {

                // save Location values in db
                mDbHelpher.addLocationData(tLocation);

            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("location")) {
                tLocation.setLocationID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("locdesc")
                    && mCurrentTable.equals("location")) {
                tLocation.setLocationDesrip(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctype")
                    && mCurrentTable.equals("location")) {
                tLocation.setLocationType(tempVal);
            }

            if (smallLocalName.equalsIgnoreCase("Acknowledgement")
                    && mCurrentTable.equals("sessiondetail")) {

                // save Location values in db
                mDbHelpher.addSessionData(tSessiondetail);

            } else if (smallLocalName.equalsIgnoreCase("Result")
                    && mCurrentTable.equals("sessiondetail")) {
                tSessiondetail.setResult(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("SessionId")
                    && mCurrentTable.equals("sessiondetail")) {
                tSessiondetail.setSessionId(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("WMSPalletResetPriv")
                    && mCurrentTable.equals("sessiondetail")) {
                tSessiondetail.setPalletResetPriv(tempVal);
            }

            // Save Task Notification Count
            if (smallLocalName.equalsIgnoreCase("NotificationCount")
                    && mCurrentTable.equals("notificationcount")) {

                // save Location values in db
                mDbHelpher.addNotificationData(tNotificationCount);

            } else if (smallLocalName.equalsIgnoreCase("PickTask")
                    && mCurrentTable.equals("notificationcount")) {
                tNotificationCount.setPickTask(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("ReceiveTask")
                    && mCurrentTable.equals("notificationcount")) {
                tNotificationCount.setReceiveTask(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCount")
                    && mCurrentTable.equals("notificationcount")) {
                tNotificationCount.setPhysicalCount(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("MoveTask")
                    && mCurrentTable.equals("notificationcount")) {
                tNotificationCount.setMoveTask(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("LoadPickPallets")
                    && mCurrentTable.equals("notificationcount")) {
                tNotificationCount.setLoadPickPallets(Integer.valueOf(tempVal));
            }

            // Save Task Menu List
            if (smallLocalName.equalsIgnoreCase("MenuList")
                    && mCurrentTable.equals("menulist")) {

                // save Location values in db
                mDbHelpher.addMenuList(tmenulist);

            } else if (smallLocalName.equalsIgnoreCase("PickTask")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setPickTask(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("ReceiveTask")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setReceiveTask(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("PhysicalCount")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setPhysicalCount(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("MoveTask")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setMoveTask(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("LoadPickPallets")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setLoadPickPallets(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("MoveManually")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setMoveManually(Integer.valueOf(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("BreakerUomUtility")
                    && mCurrentTable.equals("menulist")) {
                tmenulist.setBreakerUomUtility(Integer.valueOf(tempVal));
            }

            // Save Pick Task List
            if (smallLocalName.equalsIgnoreCase("PickTaskList")
                    && mCurrentTable.equals("picktasklist")) {
                // save Location values in db
                mDbHelpher.addPickTaskListData(tpicktasklist);

            } else if (smallLocalName.equalsIgnoreCase("TaskNo")
                    && mCurrentTable.equals("picktasklist")) {
                tpicktasklist.setTaskNo(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Status")
                    && mCurrentTable.equals("picktasklist")) {
                tpicktasklist.setStatus(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Route")
                    && mCurrentTable.equals("picktasklist")) {
                tpicktasklist.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Stop")
                    && mCurrentTable.equals("picktasklist")) {
                tpicktasklist.setStop(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Date")
                    && mCurrentTable.equals("picktasklist")) {
                tpicktasklist.setDate(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Sonos")
                    && mCurrentTable.equals("picktasklist")) {
                tpicktasklist.setSonos(tempVal);
            }





            // Save Pick Task Header
            if (smallLocalName.equalsIgnoreCase("PickTaskHeader")
                    && mCurrentTable.equals("picktaskheader")) {
                mDbHelpher.addPickTaskHeaderData(tpicktaskheader);

            } else if (smallLocalName.equalsIgnoreCase("casecount")
                    && mCurrentTable.equals("picktaskheader")) {
                tpicktaskheader.setCasecount(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("weight")
                    && mCurrentTable.equals("picktaskheader")) {
                tpicktaskheader.setWeight(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Route")
                    && mCurrentTable.equals("picktaskheader")) {
                tpicktaskheader.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Stop")
                    && mCurrentTable.equals("picktaskheader")) {
                tpicktaskheader.setStop(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Trailer")
                    && mCurrentTable.equals("picktaskheader")) {
                tpicktaskheader.setTrailer(tempVal);
            }

            // Save Pick Task Detail
            if ((smallLocalName.equalsIgnoreCase("PickTaskDetail") || smallLocalName.equalsIgnoreCase("PickTaskWLOTInfo"))
                    && mCurrentTable.equals("picktaskdetail")) {
                mDbHelpher.addPickTaskDetailData(tpicktaskdetail);
                mDbHelpher.addPickTaskDetailRevertData(tpicktaskdetail);

            } else if (smallLocalName.equalsIgnoreCase("Slot")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setSlot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("TQty")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setTQty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("TrkQty")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setTrkQty(tempVal);
            } else if ((smallLocalName.equalsIgnoreCase("Uom") || smallLocalName.equalsIgnoreCase("umeasur"))
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setUom(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Item")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setItem(tempVal);
            } else if ((smallLocalName.equalsIgnoreCase("Desc") || smallLocalName.equalsIgnoreCase("itmdesc"))
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setDescrip(tempVal);
            } else if ((smallLocalName.equalsIgnoreCase("Lotno") || smallLocalName.equalsIgnoreCase("wlotno"))
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setWLotNo(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Tranlineno")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setTranlineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Doctype")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setDoctype(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Docno")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setDocno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Doclineno")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setDoclineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Docstat")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setDocstat(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Weight")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setWeight(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Volume")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setVolume(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("decnum")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setdecnum(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Stkumid")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setStkumid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Catchwt")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setCatchwt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Umfact")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setUmfact(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Tshipped")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setTshipped(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Trkshipped")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setTrkshipped(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Lbshp")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setLbshp(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setLotNo(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Chgqty")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setChgQty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("oTqtypicked")
                    && mCurrentTable.equals("picktaskdetail")) {
                tpicktaskdetail.setoTqtypicked(tempVal);
            }

            // Save Pick Task WHIPTL
            if (smallLocalName.equalsIgnoreCase("WHIPTL")
                    && mCurrentTable.equals("picktaskWHIPTL")) {
                mDbHelpher.addPickTaskWHIPTLData(tpicktaskWHIPTL);

            } else if (smallLocalName.equalsIgnoreCase("palno")
                    && mCurrentTable.equals("picktaskWHIPTL")) {
                tpicktaskWHIPTL.setPalno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("taskno")
                    && mCurrentTable.equals("picktaskWHIPTL")) {
                tpicktaskWHIPTL.setTaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("palstat")
                    && mCurrentTable.equals("picktaskWHIPTL")) {
                tpicktaskWHIPTL.setPalstat(tempVal);
            }

            // PickTask ScanPallet
            if (smallLocalName.equalsIgnoreCase("Pickiteminformation")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                mDbHelpher.addPickTaskScanPallet(tPickTaskScanPallet);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_item(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_loctid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("wlotno") && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_wlotno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("lotno")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_lotno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_lotrefid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_slot(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_umeasur(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_tqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("rpallocqty")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_rpallocqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("whqty")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_whqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("icqty")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_icqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_itmdesc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("weight")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_weight(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("countryid")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_countryid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("serial")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_serial(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("volume")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_volume(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("catchwt")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_catchwt(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("stkumid")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_stkumid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("uselots")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_uselots(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("umfact")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setUmfact(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("setid")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_setid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("vendno")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_vendno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("cost")
                    && mCurrentTable.equals("PickTask_ScanPallet")) {
                tPickTaskScanPallet.setPtsP_cost(tempVal);
            }




            // Save Pick Task WHMLOT
            if (smallLocalName.equalsIgnoreCase("WHMLOT")
                    && mCurrentTable.equals("picktaskWHMLOT")) {
                mDbHelpher.addPickTaskWHMLOTData(tpicktaskWHMLOT);

            } else if (smallLocalName.equalsIgnoreCase("Item")
                    && mCurrentTable.equals("picktaskWHMLOT")) {
                tpicktaskWHMLOT.setItem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Wlotno")
                    && mCurrentTable.equals("picktaskWHMLOT")) {
                tpicktaskWHMLOT.setWlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Lotrefid")
                    && mCurrentTable.equals("picktaskWHMLOT")) {
                tpicktaskWHMLOT.setLotrefid(tempVal);
            }

            // Save Pick Task WHMSLT
            if (smallLocalName.equalsIgnoreCase("WHMSLT")
                    && mCurrentTable.equals("picktaskWHMSLT")) {
                mDbHelpher.addPickTaskWHMSLTData(tpicktaskWHMSLT);

            } else if (smallLocalName.equalsIgnoreCase("Slot")
                    && mCurrentTable.equals("picktaskWHMSLT")) {
                tpicktaskWHMSLT.setSlot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Loctid")
                    && mCurrentTable.equals("picktaskWHMSLT")) {
                tpicktaskWHMSLT.setLoctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Slottype")
                    && mCurrentTable.equals("picktaskWHMSLT")) {
                tpicktaskWHMSLT.setSlottype(tempVal);
            }
            // Save Pick Task WHMQTY
            if (smallLocalName.equalsIgnoreCase("WHMQTY")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                mDbHelpher.addPickTaskWHMQTYData(tpicktaskWHMQTY);

            } else if (smallLocalName.equalsIgnoreCase("Item")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                tpicktaskWHMQTY.setItem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Loctid")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                tpicktaskWHMQTY.setLoctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Wlotno")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                tpicktaskWHMQTY.setWlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Slot")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                tpicktaskWHMQTY.setSlot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Umeasur")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                tpicktaskWHMQTY.setUmeasur(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Tqty")
                    && mCurrentTable.equals("picktaskWHMQTY")) {
                tpicktaskWHMQTY.setTqty(tempVal);
            }

            // Save Pick Task WHITRLS
            if (smallLocalName.equalsIgnoreCase("WHITRL")
                    && mCurrentTable.equals("picktaskWHITRLS")) {
                mDbHelpher.addPickTaskWHITRLSData(tpicktaskWHITRLS);

            } else if (smallLocalName.equalsIgnoreCase("trailer")
                    && mCurrentTable.equals("picktaskWHITRLS")) {
                tpicktaskWHITRLS.setTrailer(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("rteseq")
                    && mCurrentTable.equals("picktaskWHITRLS")) {
                tpicktaskWHITRLS.setRteseq(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("route")
                    && mCurrentTable.equals("picktaskWHITRLS")) {
                tpicktaskWHITRLS.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("dock")
                    && mCurrentTable.equals("picktaskWHITRLS")) {
                tpicktaskWHITRLS.setDock(tempVal);
            }

            // Save pick task Print Label
            if (smallLocalName.equalsIgnoreCase("PrintLabel")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                mDbHelpher.addPickTaskPrintLabel(tpicktaskPrintlabel);

            } else if (smallLocalName.equalsIgnoreCase("Stop")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setStop(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Trailer")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setTrailer(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Route")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("dock")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setDock(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("DeliveryDate")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setDeldate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Order")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setOrderno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Taskno")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setTaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("CustomerId")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setCustid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("CustomerName")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setCustname(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Picker")
                    && mCurrentTable.equals("picktaskPrintlabel")) {
                tpicktaskPrintlabel.setPicker(tempVal);
            }


            // Save load pick pallet Summary
            if (smallLocalName.equalsIgnoreCase("Summary")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                mDbHelpher.addLoadPickPalletSummaryData(tloadpickpalletSummary);

            } else if (smallLocalName.equalsIgnoreCase("wmsDate")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                tloadpickpalletSummary.setwmsDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Truck")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                tloadpickpalletSummary.setTruck(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Dock")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                tloadpickpalletSummary.setDock(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Routecnt")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                tloadpickpalletSummary.setRoutecnt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Stopcnt")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                tloadpickpalletSummary.setStopcnt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Palcnt")
                    && mCurrentTable.equals("loadpickpalletSummary")) {
                tloadpickpalletSummary.setPalcnt(tempVal);
            }
            // Save load pick pallet Details
            if (smallLocalName.equalsIgnoreCase("Details")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                mDbHelpher.addLoadPickPalletDetailsData(tloadpickpalletDetails);

            } else if (smallLocalName.equalsIgnoreCase("wmsDate")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setwmsDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Taskno")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setTaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Picker")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setPicker(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Route")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Stop")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setStop(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Total")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setTotal(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Loaded")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setLoaded(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Ready")
                    && mCurrentTable.equals("loadpickpalletDetails")) {
                tloadpickpalletDetails.setReady(tempVal);
            }
            // Save load pick pallet Route Details
            if (smallLocalName.equalsIgnoreCase("RouteDetails")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                mDbHelpher.addLoadPickPalletRouteDetailsData(tloadpickpalletRouteDetails);

            } else if (smallLocalName.equalsIgnoreCase("Route")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Truck")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setTruck(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Status")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setStatus(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Stopcnt")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setStopcnt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Palin")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setPalin(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Palrdy")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setPalrdy(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Palcnt")
                    && mCurrentTable.equals("loadpickpalletRouteDetails")) {
                tloadpickpalletRouteDetails.setPalcnt(tempVal);
            }
            // Save load pick pallet WHIPLT
            if (smallLocalName.equalsIgnoreCase("WHIPLT")
                    && mCurrentTable.equals("loadpickpalletWHIPLT")) {
                mDbHelpher.addLoadPickPalletWHIPLT(tloadpickpalletWHIPLT);

            } else if (smallLocalName.equalsIgnoreCase("wmsDate")
                    && mCurrentTable.equals("loadpickpalletWHIPLT")) {
                tloadpickpalletWHIPLT.setwmsDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Palno")
                    && mCurrentTable.equals("loadpickpalletWHIPLT")) {
                tloadpickpalletWHIPLT.setPalno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Taskno")
                    && mCurrentTable.equals("loadpickpalletWHIPLT")) {
                tloadpickpalletWHIPLT.setTaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Palstat")
                    && mCurrentTable.equals("loadpickpalletWHIPLT")) {
                tloadpickpalletWHIPLT.setPalstat(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("stgslot")
                    && mCurrentTable.equals("loadpickpalletWHIPLT")) {
                tloadpickpalletWHIPLT.setstgslot(tempVal);
            }
            // Save load pick pallet WHITRL
            if (smallLocalName.equalsIgnoreCase("WHITRL")
                    && mCurrentTable.equals("loadpickpalletWHITRL")) {
                mDbHelpher.addLoadPickPalletWHIPLT(tloadpickpalletWHITRL);

            } else if (smallLocalName.equalsIgnoreCase("wmsDate")
                    && mCurrentTable.equals("loadpickpalletWHITRL")) {
                tloadpickpalletWHITRL.setwmsDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Trailer")
                    && mCurrentTable.equals("loadpickpalletWHITRL")) {
                tloadpickpalletWHITRL.setTrailer(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Rteseq")
                    && mCurrentTable.equals("loadpickpalletWHITRL")) {
                tloadpickpalletWHITRL.setRteseq(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Route")
                    && mCurrentTable.equals("loadpickpalletWHITRL")) {
                tloadpickpalletWHITRL.setRoute(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Dock")
                    && mCurrentTable.equals("loadpickpalletWHITRL")) {
                tloadpickpalletWHITRL.setDock(tempVal);
            }

            // Save Receive Task List
            if (smallLocalName.equalsIgnoreCase("ReceiveTaskList")
                    && mCurrentTable.equals("receivetasklist")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskListData(treceivetasklist);
               // mDbHelpher.addMoveTaskListData(treceivetasklist);

            } else if (smallLocalName.equalsIgnoreCase("TaskNo")
                    && mCurrentTable.equals("receivetasklist")) {
                treceivetasklist.setTaskNo(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Status")
                    && mCurrentTable.equals("receivetasklist")) {
                treceivetasklist.setStatus(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("UserId")
                    && mCurrentTable.equals("receivetasklist")) {
                treceivetasklist.setUserid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("DocType")
                    && mCurrentTable.equals("receivetasklist")) {
                treceivetasklist.setDoctype(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("DocNo")
                    && mCurrentTable.equals("receivetasklist")) {
                treceivetasklist.setDocno(tempVal);
            }


            // Move Task List
              if (smallLocalName.equalsIgnoreCase("MoveTaskList")
                    && mCurrentTable.equals("moveTaskList")) {
                // save Location values in db
                mDbHelpher.addMoveTaskListData(tmovetasklist);

            } else if (smallLocalName.equalsIgnoreCase("TaskNo")
                    && mCurrentTable.equals("moveTaskList")) {
                tmovetasklist.setTaskNo(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("TaskType")
                    && mCurrentTable.equals("moveTaskList")) {
                tmovetasklist.setTaskType(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Status")
                    && mCurrentTable.equals("moveTaskList")) {
                tmovetasklist.setStatus(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("RowPrty")
                    && mCurrentTable.equals("moveTaskList")) {
                tmovetasklist.setRowPrty(tempVal);
            }



            // Save Receive Task Header
            if (smallLocalName.equalsIgnoreCase("ReceiveTaskHeader")
                    && mCurrentTable.equals("receivetaskheader")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskHeader(treceivetaskheader);


            } else if (smallLocalName.equalsIgnoreCase("descrip")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setdescrip(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("venddescrip")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setvenddescrip(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("casecounted")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setcasecounted(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("casetotal")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setcasetotal(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("plttotal")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setplttotal(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pltcounted")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setpltcounted(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wmsdate")
                    && mCurrentTable.equals("receivetaskheader")) {
                treceivetaskheader.setwmsdate(tempVal);
            }

            // Save Receive Task Detail
            if (smallLocalName.equalsIgnoreCase("ReceiveTaskDetail")
                    && mCurrentTable.equals("receivetaskdetail")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskDetail(treceivetaskdetail);
                mDbHelpher.addReceiveTaskTranDetail(treceivetaskdetail);

            } else if (smallLocalName.equalsIgnoreCase("taskno")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.settaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tranlineno")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.settranlineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("doctype")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setdoctype(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("docno")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setdocno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("doclineno")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setdoclineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setloctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setwlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setumeasur(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wmsstat")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setwmsstat(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqtyrec")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.settqtyrec(tempVal);
                treceivetaskdetail.settrkqtyrec(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("trkqtyrec")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.settrkqtyrec(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("revlev")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setrevlev(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqtyinc")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.settqtyinc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setitmdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pckdesc")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setpckdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("countryid")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setcountryid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itemShow")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setitemShow(tempVal);
            } /*else if (smallLocalName.equalsIgnoreCase("collection")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setcollection(tempVal);
            }*/ else if (smallLocalName.equalsIgnoreCase("welement")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setwelement(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("widgetID")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setwidgetID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("catchwt")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setcatchwt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("decnum")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setdecnum(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setlotrefid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("palno")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setPalno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("defslot")
                    && mCurrentTable.equals("receivetaskdetail")) {
                treceivetaskdetail.setcollection(tempVal);
            }

            // Save Move Task Detail
            if (smallLocalName.equalsIgnoreCase("MoveTaskDetail")
                    && mCurrentTable.equals("movetaskdetail")) {
                // save Location values in db
                mDbHelpher.addMoveTaskDetail(tmovetaskdetail);

            } else if (smallLocalName.equalsIgnoreCase("taskno")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setTaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tasktype")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setTasktype(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("status")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setStatus(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("locked")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setLocked(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("tranlineno")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setTranlineno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("childID")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setChildID(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setItem(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setLoctid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setWlotno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("palno")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setPalno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setUmeasur(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("tqtyrq")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setTqtyrq(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("tqtyact")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setTqtyact(tempVal);
                }else if (smallLocalName.equalsIgnoreCase("fromSlot")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setFromSlot(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("toSlot")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setToSlot(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setItmdesc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("pckdesc")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setPckdesc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("whqty")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setWhqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("allocqty")
                    && mCurrentTable.equals("movetaskdetail")) {
                tmovetaskdetail.setAllocqty(tempVal);
            }


            // Receive Slot List
            if (smallLocalName.equalsIgnoreCase("SlotList")
                    && mCurrentTable.equals("receiveSlotList")) {
                // save Location values in db
                mDbHelpher.addReceiveSlotList(tSlotList);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("receiveSlotList")) {
                tSlotList.setItem(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("receiveSlotList")) {
                tSlotList.setSlot(tempVal);
            }

            // MoveTAsk Slot List
            if (smallLocalName.equalsIgnoreCase("MoveTaskWHMSLT")
                    && mCurrentTable.equals("movetaskslotlist")) {
                // save Location values in db
                mDbHelpher.addMoveTaskSlotList(tMoveTaskSlotList);

            } else if (smallLocalName.equalsIgnoreCase("Slot")
                    && mCurrentTable.equals("movetaskslotlist")) {
                tMoveTaskSlotList.setSlot(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Loctid")
                    && mCurrentTable.equals("movetaskslotlist")) {
                tMoveTaskSlotList.setLoctid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Slottype")
                    && mCurrentTable.equals("movetaskslotlist")) {
                tMoveTaskSlotList.setSlottype(tempVal);
            }

            //Sales Order Charges PickList
            if (smallLocalName.equalsIgnoreCase("SoChargesDetails")
                    && mCurrentTable.equals("SalesOrderPicklist")) {
                // save Location values in db
                mDbHelpher.addSalesOrderPickList(tSalesOrderPickList);

            } else if (smallLocalName.equalsIgnoreCase("sono")
                    && mCurrentTable.equals("SalesOrderPicklist")) {
                tSalesOrderPickList.setSono(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("sodate")
                    && mCurrentTable.equals("SalesOrderPicklist")) {
                tSalesOrderPickList.setSodate(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("custno")
                    && mCurrentTable.equals("SalesOrderPicklist")) {
                tSalesOrderPickList.setCustno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("ordamt")
                    && mCurrentTable.equals("SalesOrderPicklist")) {
                tSalesOrderPickList.setOrdamt(tempVal);
            }


            //Sales Order Charges Category List
            if (smallLocalName.equalsIgnoreCase("SoChargesDetails")
                    && mCurrentTable.equals("SoChargesCategory")) {
                // save Location values in db
                mDbHelpher.addSoChargesData(tSalesOrderCategoryList);

            } else if (smallLocalName.equalsIgnoreCase("chrgcat")
                    && mCurrentTable.equals("SoChargesCategory")) {
                tSalesOrderCategoryList.setCategory(tempVal);
            }

            //ResetItemDetails
            if (smallLocalName.equalsIgnoreCase("Resetiteminfo")
                    && mCurrentTable.equals("Resetitemdetail")) {
                // save Location values in db
                mDbHelpher.addResetItemDetails(tResetItemDetails);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setItem(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setLoctid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setWlotno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("lotno")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setLotno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Palno")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setPalno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setSlot(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setUmeasur(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setTqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("rpallocqty")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setRpallocqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("whqty")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setWhqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("icqty")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setIcqty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setItmdesc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("qty")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setQty(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("psoaloc")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setPsoaloc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("prpaloc")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setPrpaloc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("ptfrout")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setPtfrout(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("ptmaloc")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setPtmaloc(tempVal);
            }/*else if (smallLocalName.equalsIgnoreCase("opensocount")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setOpensocount(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("openrepackcount")
                    && mCurrentTable.equals("Resetitemdetail")) {
                tResetItemDetails.setOpenrepackcount(tempVal);
            }*/

            // Save Receive Task Item Class
            if (smallLocalName.equalsIgnoreCase("Itmclsslist")
                    && mCurrentTable.equals("receivetaskitemclass")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskItemClass(treceivetaskitemclass);

            } else if (smallLocalName.equalsIgnoreCase("itmclss")
                    && mCurrentTable.equals("receivetaskitemclass")) {
                treceivetaskitemclass.setitmclss(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("descrip")
                    && mCurrentTable.equals("receivetaskitemclass")) {
                treceivetaskitemclass.setdescrip(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("welement")
                    && mCurrentTable.equals("receivetaskitemclass")) {
                treceivetaskitemclass.setwelement(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("collection")
                    && mCurrentTable.equals("receivetaskitemclass")) {
                treceivetaskitemclass.setcollection(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("widgetID")
                    && mCurrentTable.equals("receivetaskitemclass")) {
                treceivetaskitemclass.setwidgetID(tempVal);
            }

            // Save Receive Task Load Type
            if (smallLocalName.equalsIgnoreCase("Loadtypelist")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskLoadType(treceivetaskloadtype);

            } else if (smallLocalName.equalsIgnoreCase("loadtype")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setloadtype(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("descrip")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setdescrip(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("welement")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setwelement(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("collection")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setcollection(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("widgetID")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setwidgetID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("LoadId")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setLoadId(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("LoadTypeStatus")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setLoadTypeStatus(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("WmsDate")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setWmsDate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("Metricval")
                    && mCurrentTable.equals("receivetaskloadtype")) {
                treceivetaskloadtype.setMetricval(tempVal);
            }

            // Save Receive Task WHMSLT
            if (smallLocalName.equalsIgnoreCase("WHMSLT")
                    && mCurrentTable.equals("receivetaskWHMSLT")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskWHMSLT(treceivetaskWHMSLT);

            } else if (smallLocalName.equalsIgnoreCase("Slot")
                    && mCurrentTable.equals("receivetaskWHMSLT")) {
                treceivetaskWHMSLT.setSlot(tempVal);
            }

            // Print Pallet Tag
            if (smallLocalName.equalsIgnoreCase("Printpallettag")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                // save Location values in db
               mDbHelpher.addPrintPalletDetails(tPrintPalletTag);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setItem(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("lotno")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setLotId(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("Palno")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setPalletNo(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("qty")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setQty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("countryid")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setCountryid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("outpalno")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setOutpalno(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("pltcrea")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setPltcrea(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("setid")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setSetid(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("grade")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setGrade(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setItemDesc(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("commod")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setCommod(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("label")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setLabel(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("package")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setPackage(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("sizesort")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setSizesort(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("variety")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setVariety(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("size")
                    && mCurrentTable.equals("PrintPalletTagDetails")) {
                tPrintPalletTag.setSize(tempVal);
            }

            // Save Receive Task WHRPLT
            if (smallLocalName.equalsIgnoreCase("WHRPLT")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskWHRPLT(treceivetaskWHRPLT);
                mDbHelpher.addReceiveTaskTranWHRPLT(treceivetaskWHRPLT);

            } else if (smallLocalName.equalsIgnoreCase("taskno")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.settaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tasklineno")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.settasklineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pltlineno")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.setpltlineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.settqty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pltstat")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.setpltstat(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("prtplttag")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.setprtplttag(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("trkqty")
                    && mCurrentTable.equals("receivetaskWHRPLT")) {
                treceivetaskWHRPLT.settrkqty(tempVal);
            }

            // Save Receive Task WHRPLT
            if (smallLocalName.equalsIgnoreCase("Print")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                // save Location values in db
                mDbHelpher.addReceiveTaskPrint(treceivetaskprintdetail);

            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setwlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setlotrefid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("recdate")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setrecdate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("expdate")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setexpdate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("recuser")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setrecuser(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("taskno")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.settaskno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tasklineno")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.settasklineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pltlineno")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setpltlineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("prtplttag")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setprtplttag(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.settqty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("trkqty")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.settrkqty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setitmdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("catchwt")
                    && mCurrentTable.equals("receivetaskprintdetail")) {
                treceivetaskprintdetail.setcatchwt(tempVal);
            }

            // Save Physical count Slot List
            if (smallLocalName.equalsIgnoreCase("PhysicalCountSlots")
                    && mCurrentTable.equals("physicalcountSlot")) {
                mDbHelpher.addPhysicalCountSlot(tphysicalcountSlot);

            } else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("physicalcountSlot")) {
                tphysicalcountSlot.setslot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wmsstat")
                    && mCurrentTable.equals("physicalcountSlot")) {
                tphysicalcountSlot.setwmsstat(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("posted")
                    && mCurrentTable.equals("physicalcountSlot")) {
                tphysicalcountSlot.setposted(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("grpcnt")
                    && mCurrentTable.equals("physicalcountSlot")) {
                tphysicalcountSlot.setgrpcnt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("status")
                    && mCurrentTable.equals("physicalcountSlot")) {
                tphysicalcountSlot.setstatus(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("doclinecount")
                    && mCurrentTable.equals("physicalcountSlot")) {
                tphysicalcountSlot.setdoclinecount(tempVal);
            }


            // Save Physical Count Detail List
            if (smallLocalName.equalsIgnoreCase("physicalcountDetails")
                    && mCurrentTable.equals("physicalcountDetail")) {
                mDbHelpher.addPhysicalCountDetail(tphysicalcountDetail);
                mDbHelpher.addPhysicalCountTranDetail(tphysicalcountDetail);

            } else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setslot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("countid")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setcountid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("page")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setpage(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("doclineno")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setdoclineno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setloctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setwlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setumeasur(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tcountqty")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.settcountqty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wmsstat")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setwmsstat(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("posted")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setposted(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setitmdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pckdesc")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setpckdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("decnum")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setdecnum(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setlotrefid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.settqty(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itemShow")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setitemShow(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("surprisadd")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setsurprisadd(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("userid")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setuserid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("counted")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setcounted(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("collection")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setcollection(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("welement")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setwelement(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("widgetID")
                    && mCurrentTable.equals("physicalcountDetail")) {
                tphysicalcountDetail.setwidgetID(tempVal);
            }

            // Save Physical Count UOM List
            if (smallLocalName.equalsIgnoreCase("PhysicalCountUOM")
                    && mCurrentTable.equals("physicalcountUom")) {
                mDbHelpher.addPhysicalCountUOM(tphysicalcountUom);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("physicalcountUom")) {
                tphysicalcountUom.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("physicalcountUom")) {
                tphysicalcountUom.setumeasur(tempVal);
            }

            // Save Physical Count WHMLOT List
            if (smallLocalName.equalsIgnoreCase("PhysicalCountWHMLOT")
                    && mCurrentTable.equals("physicalcountWHMLOT")) {
                mDbHelpher.addPhysicalCountWHMLOT(tphysicalcountWHMLOT);

            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("physicalcountWHMLOT")) {
                tphysicalcountWHMLOT.setwlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("physicalcountWHMLOT")) {
                tphysicalcountWHMLOT.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("physicalcountWHMLOT")) {
                tphysicalcountWHMLOT.setlotrefid(tempVal);
            }

            // Save Physical Count UOM List
            if (smallLocalName.equalsIgnoreCase("PhysicalCountWHMQTY")
                    && mCurrentTable.equals("physicalcountWHMQTY")) {
                mDbHelpher.addPhysicalCountWHMQTY(tphysicalcountWHMQTY);

            } else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("physicalcountWHMQTY")) {
                tphysicalcountWHMQTY.setslot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("physicalcountWHMQTY")) {
                tphysicalcountWHMQTY.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("physicalcountWHMQTY")) {
                tphysicalcountWHMQTY.setwlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("physicalcountWHMQTY")) {
                tphysicalcountWHMQTY.setumeasur(tempVal);
            }

            // Save Physical Count ICITEM List
            if (smallLocalName.equalsIgnoreCase("PhysicalCountICITEM")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                mDbHelpher.addPhysicalCountICITEM(tphysicalcountICITEM);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setitem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("invtype")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setinvtype(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("stkumid")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setstkumid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("punmsid")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setpunmsid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("sunmsid")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setsunmsid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("brnam1")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setbrnam1(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("brnam2")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setbrnam2(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("brnam3")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setbrnam3(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("brnam4")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setbrnam4(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("brnam5")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setbrnam5(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setitmdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("pckdesc")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setpckdesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("decnum")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setdecnum(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itemShow")
                    && mCurrentTable.equals("physicalcountICITEM")) {
                tphysicalcountICITEM.setitemShow(tempVal);
            }

            // Save MoveManually Lot List
            if (smallLocalName.equalsIgnoreCase("MoveManuallyWHMQTY")
                    && mCurrentTable.equals("moveManuallyTable")) {
                mDbHelpher.addMoveManuallylot(tMoveManually);

            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmWlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmLotrefid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmItem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmSlot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmLoctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmUOM(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmQty(Double.parseDouble(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("trkqty")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmTrkqty(Double.parseDouble(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("locked")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmIslocked(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmItemDesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("catchwt")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmCatchwt(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("rpallocqty")
                    && mCurrentTable.equals("moveManuallyTable")) {
                tMoveManually.setMmrpAlloc(tempVal);
            }

            // Save BreakerUOM Data
            if (smallLocalName.equalsIgnoreCase("BreakerUomWHMQTY")
                    && mCurrentTable.equals("breakerUOMTable")) {
                mDbHelpher.addBreakerUOM(tbreakerUomUtility);

            } else if (smallLocalName.equalsIgnoreCase("wlotno")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuWlotno(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuItem(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("slot")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuSlot(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuLoctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuUOM(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("tqty")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuQty(Double.parseDouble(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("trkqty")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuTrkqty(Double.parseDouble(tempVal));
            } else if (smallLocalName.equalsIgnoreCase("locked")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuIslocked(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuItemDesc(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("catchwt")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuCatchwt(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("stkumid")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuStkumid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                    && mCurrentTable.equals("breakerUOMTable")) {
                tbreakerUomUtility.setBuLotRefId(tempVal);
            }

            // Save BreakerUOM List Data
            if (smallLocalName.equalsIgnoreCase("ICITEMUomWHMQTY")
                    && mCurrentTable.equals("breakerUOMListTable")) {
                mDbHelpher.addBreakerUOMList(tbreakerUOMList);

            } else if (smallLocalName.equalsIgnoreCase("brnam")
                    && mCurrentTable.equals("breakerUOMListTable")) {
                tbreakerUOMList.setBuBrName(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("brunit")
                    && mCurrentTable.equals("breakerUOMListTable")) {
                tbreakerUOMList.setBuUnit(tempVal);
            }


            // Save Repack FG Data
            if(Globals.FromFinish) {
                if (smallLocalName.equalsIgnoreCase("RepackFg")
                        && mCurrentTable.equals("RepackFg")) {
                    // save Location values in db
                    mDbHelpher.addRepackFG(tRepackFG);

                } else if (smallLocalName.equalsIgnoreCase("pano")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PANO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("tranlineno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_TRANLINENO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("item")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ITEM(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("descrip")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_DESCRIP(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("umeasur")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_UMEASUR(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("loctid")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LOCTID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lotno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LOTNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("serial")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_SERIAL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("qtymade")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_QTYMADE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("cost")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_COST(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("padate")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PADATE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("pastat")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PASTAT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lckstat")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LCKSTAT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lckuser")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LCKUSER(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lckdate")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LCKDATE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lcktime")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LCKTIME(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("adduser")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ADDUSER(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("adddate")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ADDDATE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("addtime")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ADDTIME(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("prodlbl")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PRODLBL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("packchg")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PACKCHG(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("washchg")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_WASHCHG(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("countryid")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_COUNTRYID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("vendno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_VENDNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("grade")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_GRADE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("projno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PROJNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("remarks")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_REMARKS(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lcstqty")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LCSTQTY(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("case_pl")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_CASE_PL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("palno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PALNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("setid")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_SETID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("weight")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_WEIGHT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("pallet")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_PALLET(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("id_col")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ID_COL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("binno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_BINNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("postprg")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_POSTPRG(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("extpallet")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_EXTPALLET(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("extcube")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_EXTCUBE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("extweight")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_EXTWEIGHT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("bextlcst")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_BEXTLCST(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("extlcst")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_EXTLCST(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("bextfees")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_BEXTFEES(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("extfees")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_EXTFEES(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("tpallet")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_TPALLET(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("tcube")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_TCUBE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("tweight")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_TWEIGHT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("wlotno")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_WLOTNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("origtranln")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ORIGTRANLN(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("origtranl")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ORIGTRANL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("origdocln")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_ORIGDOCLN(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("stkumid")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_STKUMID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("uselots")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_USELOTS(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("umfact")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_UMFACT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("weight1")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_WEIGHT1(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("volume")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_VOLUME(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("catchwt")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_CATCHWT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LOTREFID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("Lotexpl")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LOTEXPL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("Linesplit")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_LINESPLIT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("Trkqtypk")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_TRKQTYPK(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("updflag")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_UPDFLAG(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("vplocked")
                        && mCurrentTable.equals("RepackFg")) {
                    tRepackFG.setREPACKFG_VPLOCKED(tempVal);
                }
            }


            // Save Repack Ingredients Data
            if(Globals.FromRaw) {
                if ((smallLocalName.equalsIgnoreCase("RepackIngredient") || smallLocalName.equalsIgnoreCase("Pickiteminfo"))
                        && mCurrentTable.equals("RepackIngredient")) {

                    boolean isItemAvailable = mDbHelpher.isRawItemAvailable(tRepackIngredients.getRIT_ITEM(), tRepackIngredients.getRIT_WLOTNO());
                    boolean isEmptyItemAvailable = mDbHelpher.isEmptyItemAvailable(tRepackIngredients.getRIT_ITEM());
                    // save Location values in db
                    if (isEmptyItemAvailable && !Globals.FROM_EDTREPACK && !Globals.isLocalData) {
                        mDbHelpher.updateEmptyItem(tRepackIngredients.getRIT_ITEM(), tRepackIngredients);
                    }else if (isItemAvailable && !tRepackIngredients.getRIT_WLOTNO().equals("")) {
                        mDbHelpher.updateRawItem(tRepackIngredients.getRIT_ITEM(), tRepackIngredients);
                    } else {
                        mDbHelpher.addRepackIngredients(tRepackIngredients);
                    }


                } else if (smallLocalName.equalsIgnoreCase("pano")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_PANO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("tranlineno")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_TRANLINENO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("item")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ITEM(tempVal);
                } else if ((smallLocalName.equalsIgnoreCase("descrip") || smallLocalName.equalsIgnoreCase("itmdesc"))
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_DESCRIP(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("umeasur")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_UMEASUR(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("loctid")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LOCTID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lotno")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LOTNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("serial")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_SERIAL(tempVal);
                } else if ((smallLocalName.equalsIgnoreCase("qtyused") || smallLocalName.equalsIgnoreCase("tqty"))
                        && mCurrentTable.equals("RepackIngredient")) {
                    if (Supporter.SUMQTY = true) {
                        tRepackIngredients.setRIT_QTYUSED(tempVal);
                        tRepackIngredients.setRIT_REMARKS(tempVal);
                        tRepackIngredients.setRIT_TRKQTYPK(tempVal);
                    } else {
                        tRepackIngredients.setRIT_QTYUSED(tempVal);
                        tRepackIngredients.setRIT_TRKQTYPK(tempVal);
                        tRepackIngredients.setRIT_REMARKS(tempVal);
                    }

                } else if (smallLocalName.equalsIgnoreCase("cost")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_COST(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("padate")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_PADATE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("pastat")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_PASTAT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lckstat")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LCKSTAT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lckuser")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LCKUSER(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lckdate")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LCKDATE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lcktime")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LCKTIME(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("adduser")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ADDUSER(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("adddate")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ADDDATE(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("addtime")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ADDTIME(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("countryid")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_COUNTRYID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("vendno")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_VENDNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("binno")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_BINNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("palno")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_PALNO(tempVal);
                } /*else if (smallLocalName.equalsIgnoreCase("remarks")
                    && mCurrentTable.equals("RepackIngredient")) {
                tRepackIngredients.setRIT_REMARKS(tempVal);
            }*/ else if (smallLocalName.equalsIgnoreCase("yield")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_YIELD(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("setid")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_SETID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("weight")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_WEIGHT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("id_col")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ID_COL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("wlotno")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_WLOTNO(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("origtranln")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ORIGTRANLN(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("stkumid")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_STKUMID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("uselots")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_USELOTS(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("umfact")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_UMFACT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("weight1")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_WEIGHT1(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("volume")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_VOLUME(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("catchwt")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_CATCHWT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("lotrefid")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LOTREFID(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("Lotexpl")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LOTEXPL(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("Linesplit")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_LINESPLIT(tempVal);
                } /*else if (smallLocalName.equalsIgnoreCase("Trkqtypk")
                    && mCurrentTable.equals("RepackIngredient")) {
                tRepackIngredients.setRIT_TRKQTYPK(tempVal);
            }*/ else if (smallLocalName.equalsIgnoreCase("updflag")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_UPDFLAG(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("slot")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_SLOT(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("whqty")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_WHQTY(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("icqty")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ICQTY(tempVal);
                }/*else if (smallLocalName.equalsIgnoreCase("allocqty")
                    && mCurrentTable.equals("RepackIngredient")) {
                tRepackIngredients.setRIT_ALLOCQTY(tempVal);
            }*/ else if (smallLocalName.equalsIgnoreCase("rpallocqty")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_RPALLOCQTY(tempVal);
                } else if (smallLocalName.equalsIgnoreCase("origpano")
                        && mCurrentTable.equals("RepackIngredient")) {
                    tRepackIngredients.setRIT_ORIGPANO(tempVal);
                }
            }

            // Save Repack Ingredients Data
            //  if ((smallLocalName.equalsIgnoreCase("RepackIngredient")||smallLocalName.equalsIgnoreCase("Pickiteminfo"))
            if ((smallLocalName.equalsIgnoreCase("Finiteminfo")) && mCurrentTable.equals("RepackFg")) {

                // mDbHelpher.addRepackFinishedGoods(tRepackIngredients);
                mDbHelpher.addRepackFG(tRepackFG);

            } else if (smallLocalName.equalsIgnoreCase("item")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_ITEM(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("lotno")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_LOTNO(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_LOCTID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umeasur")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_UMEASUR(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("itmdesc")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_DESCRIP(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("countryid")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_COUNTRYID(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("decnum")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_DECNUM(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("umfact")
                    && mCurrentTable.equals("RepackFg")) {
                tRepackFG.setREPACKFG_UMFACT(tempVal);
            }


            // Save RepackList
            if (smallLocalName.equalsIgnoreCase("repack")
                    && mCurrentTable.equals("RepackList")) {
                mDbHelpher.addRepackList(tRepackList);
            }
            else if (smallLocalName.equalsIgnoreCase("pano")
                    && mCurrentTable.equals("RepackList")) {
                tRepackList.setPano(tempVal);
            }else if (smallLocalName.equalsIgnoreCase("loctid")
                    && mCurrentTable.equals("RepackList")) {
                tRepackList.setLoctid(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("padate")
                    && mCurrentTable.equals("RepackList")) {
                tRepackList.setPadate(tempVal);
            } else if (smallLocalName.equalsIgnoreCase("addtime")
                    && mCurrentTable.equals("RepackList")) {
                tRepackList.setAddtime(tempVal);
            }




        }
    }
    public String CreateNewAppConfig(File pImportFile, String pPassword, String pDeviceId)
    {
        String result = "";
        try
        {
            WebServiceAcknowledgement response = this.ReadAcknowledgment(pImportFile);

            configsettings newConfig = new configsettings();
            newConfig.setUsername(mSalespersonFolder);
            newConfig.setPassword(pPassword);
            newConfig.setAppDesc("WMS Picking Client");
            newConfig.setInstallationDate(String.valueOf(System.currentTimeMillis()));
            newConfig.setAppName("WMS Picking Client");
            newConfig.setDeviceId(pDeviceId);

            String sessionId = response.SessionId;
            if (!response.Result)
            {
                return response.Message;
            }
            newConfig.setSessionId(sessionId);

            mDbHelpher.deleteConfigSettings();
            mDbHelpher.addConfigSettingsData(newConfig);
            result = "success";
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            LogfileCreator.mAppendLog(ex.getMessage());
            return "";
        }
        return result;
    }
    public class WebServiceAcknowledgement
    {
        public Boolean Result;
        public String SessionId;
        public String Message;

        public WebServiceAcknowledgement(){}
        public WebServiceAcknowledgement(Boolean pResult, String pSessionId, String pMessage)
        {
            this.Result = pResult;
            this.SessionId = pSessionId;
            this.Message = pMessage;
        }
    }

    public WebServiceAcknowledgement ReadAcknowledgment(File pImportFile)
    {
        WebServiceAcknowledgement acknowledgement = new WebServiceAcknowledgement();

        try
        {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();


            Document doc = builder.parse(pImportFile);
            doc.getDocumentElement().normalize();


            XPath xPath = XPathFactory.newInstance().newXPath();
            String expression = "/Acknowledgement";

            Element acknowledgementNode =  (Element) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);

            Boolean result = Boolean.parseBoolean(acknowledgementNode.getElementsByTagName("Result").item(0).getTextContent());
            String sessionId = acknowledgementNode.getElementsByTagName("SessionId").item(0).getTextContent();
            String message = acknowledgementNode.getElementsByTagName("ErrorMessage").item(0).getTextContent();

            //Set the new value of the object
            acknowledgement = new WebServiceAcknowledgement(result,sessionId,message);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            LogfileCreator.mAppendLog(ex.getMessage());
        }

        return acknowledgement;
    }

}

