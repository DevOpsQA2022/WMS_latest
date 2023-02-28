package com.silvercreek.wmspickingclient.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
import com.silvercreek.wmspickingclient.model.picktaskWHIPTL;
import com.silvercreek.wmspickingclient.model.picktaskWHMLOT;
import com.silvercreek.wmspickingclient.model.picktaskWHMQTY;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktaskheader;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.DataLoader;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.LogfileCreator;
import com.silvercreek.wmspickingclient.util.Supporter;
import com.silvercreek.wmspickingclient.R;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.silvercreek.wmspickingclient.xml.ExportPickTask;

import org.apache.commons.io.FileUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Math;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

public class PickTaskStagingActivity extends AppBaseActivity {

    private ListView transList;
    private EditText edtLot;
    private EditText edtStaging;
    private TextView tvActivePallet, tvWeight, tvStop, tvRoute, tvCase;

    private List<picktaskdetail> exportTranList;

    String filename = "PickTaskPalletLabel.pdf";
    private File pdfFile = null;
    String toasttext = "";
    private String mPath;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private Document document;
    private PdfContentByte cb;
    private PdfPTable table;
    private PdfWriter docWriter;
    private PdfPCell cell;

    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);

    private List<picktaskdetail> picktaskdetail;
    private List<picktaskdetail> headerpicktaskdetail;
    private List<picktaskWHMQTY> picktaskWHMQTYList;
    private List<picktaskheader> picktaskheader;

    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    private SharedPreferences sharedpreferences;
    private int mTimeout;
    private PickTaskStagingAdapter adapter;
    private List<String> mPalletList;
    private ArrayList<picktaskWHIPTL> mPalletMast;
    private List<String> mLotList;
    private ArrayList<picktaskWHMLOT> mLotMast;
    private String StrFlag = "Y";
    private Boolean isTaskCompleted;
    private Boolean isItemAvailable;
    private List<picktaskPrintlabel> PrintLabelList;
    private String stop, trailer, route, dock, deldate, order, task, custid, custname, picker, palno;
    private String mPalno = "";
    private String mWlotno = "";
    private String mStagingSlot="";
    private TextView tvDesc, txtWeight;
    private Button btnSave, btnCancel;
    private Boolean isSameItem = false;
    private Boolean isProceed = true;
    private String uom = "", doctype = "", docno = "", doclineno = "", docstat = "", strweight = "", stkumid = "",
            orgdoclineno = "", volume = "", decnum = "", orgTranlineno = "", Lbshp = "", umfact = "", Tshipped = "", Trkshiped = "", LineSplit = "";
    private String ItemNo, strDesc;
    private String strTQty, strorgTQty;
    private String strTrkQty, strorgTrkQty;
    private String strCatchwt, strSlot, strLot, strwLotno;
    private String strTranlineNo;
    private Double dQty;
    private double dAvailQty;
    private Integer pickDuration;
    private double dSoQty = 0.0, dLotQty = 0.0;
    private boolean isMoreQty = true;
    public static final String METHOD_GET_SUB_PICKTASK_LIST = "PickTask_WLOTINFO";
    public static final String METHOD_EXPORT_DATA ="PickTask_SaveMain";
    public static final String LOGOUTREQUEST = "LogoutRequest";
    private String mSessionId, mCompany,mUsername,mDeviceId ="";

    private String mLoctid = "";

    private File mImpOutputFile;
    private String Getmsg = "";
    private String selectedItem="";
    private String [] itemArray={} ;
    private TextView txtLot;
    private int SubTranlineCount=0;
    private Boolean isValidWlotno;
    private Boolean isValidStgSlot;
    private Boolean isValidTrailer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staging_task);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        tvActivePallet = (TextView) findViewById(R.id.tvActivePallet);
        edtStaging =(EditText) findViewById(R.id.edtStaging);
        edtLot = (EditText) findViewById(R.id.edtLot);
        txtWeight = (TextView) findViewById(R.id.tvWeight);
        tvStop = (TextView) findViewById(R.id.tvStop);
        tvRoute = (TextView) findViewById(R.id.tvRoute);
        tvCase = (TextView) findViewById(R.id.tvCase);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
      //  tvWeight = (TextView) findViewById(R.id.txtViewWeight);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
        btnSave = (Button) findViewById(R.id.btn_save);
        txtLot = (TextView) findViewById(R.id.txtLotNo);

        transList = (ListView) findViewById(R.id.lst_TransItems);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
        NAMESPACE = NAMESPACE + "/";
        Globals.gNamespace = NAMESPACE;
        Globals.gProtocol = URL_PROTOCOL;
        Globals.gServicename = URL_SERVICE_NAME;
        Globals.gAppName = APPLICATION_NAME;
        Globals.gTimeout = sharedpreferences.getString("Timeout", "");
        Globals.isNewWlotno = false;
        picktasklist tpicktasklist = new picktasklist();
        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;

        mDbHelper.openReadableDatabase();
        picktaskdetail = mDbHelper.getCompletedPickTaskDetail();
        mDbHelper.closeDatabase();
        btnSave.setEnabled(false);
        edtStaging.setEnabled(false);

        if (Globals.gSetActivePallet) {
            tvActivePallet.setTextColor(Color.parseColor("#28F130"));
            tvActivePallet.setText("Active Pallet");
            //edtPallet.setText(Globals.gPickTaskPallet);
            edtLot.requestFocus();
        } else {
            mDbHelper.openWritableDatabase();
            mDbHelper.UpdateSlottype();
            mDbHelper.closeDatabase();
        }

        adapter = new PickTaskStagingAdapter(PickTaskStagingActivity.this, picktaskdetail);
        transList.setAdapter(adapter);

        mDbHelper.openReadableDatabase();
        isTaskCompleted = mDbHelper.isStagingCompleted();
        mDbHelper.closeDatabase();
        if (!isTaskCompleted) {
            mDbHelper.openReadableDatabase();
            Globals.gPickTaskPallet = mDbHelper.SelectPallet(Globals.gTaskNo);
            mDbHelper.closeDatabase();
            btnSave.setEnabled(true);
            //mSupporter.simpleNavigateTo(PickTaskStagingActivity.class);
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        edtLot.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            mWlotno = edtLot.getText().toString().trim();

                            mLotList = getLotList();
                            mDbHelper.openReadableDatabase();
                            isValidWlotno = mDbHelper.isValidStagingWlotno(mWlotno);
                            mDbHelper.closeDatabase();
                            /*if (!mLotList.contains(mWlotno))*/
                            if(!isValidWlotno){
                                mToastMessage.showToast(PickTaskStagingActivity.this,
                                        "Invalid Pallet " + mWlotno);
                                edtLot.requestFocus();
                                edtLot.setText("");
                                //subAlert();
                               // new GetPickTaskList(mUsername).execute();
                            } else {
                                edtStaging.setEnabled(true);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!isValidWlotno) {
                                        edtLot.requestFocus();
                                    }
                                }
                            }, 150); // Remove this Delay Handler IF requestFocus(); works just fine without delay

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        edtStaging.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            mStagingSlot = edtStaging.getText().toString().trim();

                            mDbHelper.openReadableDatabase();
                             isValidStgSlot = mDbHelper.isValidStgSlot(mStagingSlot);
                             isValidTrailer = mDbHelper.isValidTrailer(mStagingSlot);
                            mDbHelper.closeDatabase();
                            if(!isValidStgSlot&&!isValidTrailer){
                                mToastMessage.showToast(PickTaskStagingActivity.this,
                                        "Invalid Staging Slot " + mStagingSlot);
                                edtStaging.requestFocus();
                                edtStaging.setText("");
                            } else {
                                btnSave.setEnabled(false);
                                Saveprocess();

                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(!isValidStgSlot&&!isValidTrailer){
                                        edtStaging.requestFocus();
                                    }
                                }
                            }, 150); // Remove this Delay Handler IF requestFocus(); works just fine without delay

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Saveprocess();
                ExportData();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvStop.getText().toString().equals("") && (tvRoute.getText().toString().equals(""))) {
                    mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
                } else {
                    cancelAlert();
                }
            }
        });

    }

    private void Saveprocess() {
        String statgingSlot="";
        statgingSlot=edtStaging.getText().toString();
        mWlotno = edtLot.getText().toString();


        mDbHelper.openReadableDatabase();
        picktaskdetail = mDbHelper.getPickTaskforSlot(mWlotno);
        mDbHelper.closeDatabase();

        if(picktaskdetail.size()>0) {       //SCS CIRK 2022/07/25 CT69649C:
            mDbHelper.openWritableDatabase();
            mDbHelper.updateStagingSlotDetails(picktaskdetail.get(0).getItem(), picktaskdetail.get(0).getTranlineno(), statgingSlot);
            mDbHelper.closeDatabase();
        }else{
            mToastMessage.showToast(PickTaskStagingActivity.this,"No data available");
            LogfileCreator.mAppendLog("No data available in picktaskdetail(PickTaskStagingActivity)");
        }

        edtLot.setEnabled(true);
        edtStaging.setEnabled(false);
        mSupporter.simpleNavigateTo(PickTaskStagingActivity.class);
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
                        mSupporter.simpleNavigateTo(PickTaskMenuActivity.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.print_pick_task_item, menu);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
       /*     case R.id.print_pick_task_item:

                if(mSupporter.isNetworkAvailable(PickTaskStagingActivity.this)){
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateFromPickTaskpallet();
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateFromPickTaskdetail();
                    mDbHelper.closeDatabase();
                    mDbHelper.openWritableDatabase();
                    mDbHelper.UpdateFromPickTaskWHITRLS();
                    mDbHelper.closeDatabase();
                    PDFCreate();

                } else {
                    mToastMessage.showToast(PickTaskStagingActivity.this,
                            "Unable to connect with Server. Please Check your internet connection");
                }
                break;*/
        }
        return true;
    }

    // to get Lot List
    public List<String> getLotList() {
        List<String> LotList = new ArrayList<String>();

        mDbHelper.openReadableDatabase();
        mLotMast = mDbHelper.getLotList();
        mDbHelper.closeDatabase();
        for (int i = 0; i < mLotMast.size(); i++) {
            LotList.add(mLotMast.get(i).getWlotno());
        }
        return LotList;
    }

    public boolean DataToPrint() {
        boolean resultSent = false;
        try {
            createfile();
            document = new Document();
            document.setMargins(13, 3, 1, 1);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            cb = docWriter.getDirectContent();
            if(PrintLabelList.size()>0){        //SCS CIRK 2022/07/25 CT69649C:
            picktaskPrintlabel printLabel = PrintLabelList.get(0);
            palno = printLabel.getPalno();
            Barcode128 barcode128 = new Barcode128();
            barcode128.setCode(palno);
            barcode128.setFont(null);
            barcode128.setCodeType(Barcode.CODE128);
            Image code128Image = barcode128.createImageWithBarcode(cb, null, null);
            code128Image.setAlignment(Image.ALIGN_CENTER);
            code128Image.setWidthPercentage(50);
            document.add(code128Image);


            Paragraph reportDetail1 = new Paragraph();
            reportDetail1.setFont(FONT_TABLE_CONTANT); //
            writeDetail1(reportDetail1);
            document.add(reportDetail1);

            Paragraph reportDetail2 = new Paragraph();
            reportDetail2.setFont(FONT_BODY); //
            writeDetail2(reportDetail2);
            document.add(reportDetail2);

            Paragraph reportDetail3 = new Paragraph();
            reportDetail3.setFont(FONT_TABLE_CONTANT); //
            writeDetail3(reportDetail3);
            document.add(reportDetail3);
            document.close();
            resultSent = true;
            }else{
                mToastMessage.showToast(PickTaskStagingActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskStagingActivity)");
            }


        } catch (Exception e) {
            resultSent = false;
        } finally {
            return resultSent;
        }
    }

    private void createfile() {
        try {

            File root_path = new File(Environment.getExternalStorageDirectory() + "/Android/WMS/PrintReport/");

            if (!root_path.exists()) {
                root_path.mkdirs();
            }

            mPath = (Environment.getExternalStorageDirectory() + "/Android/WMS/PrintReport/" + filename);

            pdfFile = new File(mPath);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail1(Paragraph reportBody) {
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {
            if(PrintLabelList.size()>0){        //SCS CIRK 2022/07/25 CT69649C:
            picktaskPrintlabel printLabel = PrintLabelList.get(0);
            stop = printLabel.getStop();
            if (stop == null) {
                stop = "";
            }
            stop = "Stop " + stop;

            trailer = printLabel.getTrailer();
            if (trailer == null) {
                trailer = "";
            }
            trailer = "Trailer " + trailer;

            cell = new PdfPCell(new Phrase(stop, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);



            cell = new PdfPCell(new Phrase(trailer, FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            reportBody.add(table);

            }else{
                mToastMessage.showToast(PickTaskStagingActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskStagingActivity)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail2(Paragraph reportBody) {
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {
            if(PrintLabelList.size()>0){        //SCS CIRK 2022/07/25 CT69649C:
            picktaskPrintlabel printLabel = PrintLabelList.get(0);
            route = printLabel.getRoute();
            if (route == null) {
                route = "";
            }
            route = "Route " + route;

            dock = printLabel.getDock();
            if (dock == null) {
                dock = "";
            }
            dock = "Dock " + dock;

            deldate = printLabel.getDeldate();
            if (deldate == null) {
                deldate = "";
            }
            deldate = "Del Date " + deldate;

            order = printLabel.getOrderno();
            if (order == null) {
                order = "";
            }
            order = "Order # " + order;

            task = printLabel.getTaskno();
            if (task == null) {
                task = "";
            }
            task = "Task #" + task;

            custid = printLabel.getCustid();
            if (custid == null) {
                custid = "";
            }
            custid = "Cust #" + custid;

            picker = printLabel.getPicker();
            if (picker == null) {
                picker = "";
            }
            picker = "Picker " + picker;

            cell = new PdfPCell(new Phrase(route, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);



            cell = new PdfPCell(new Phrase(dock, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(deldate, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase("01 of 01", FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(order, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(task, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(custid, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);


            cell = new PdfPCell(new Phrase(picker, FONT_TABLE_BODY));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);
            reportBody.add(table);

            }else{
                mToastMessage.showToast(PickTaskStagingActivity.this,"No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskStagingActivity)");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeDetail3(Paragraph reportBody) {
        try {
            if(PrintLabelList.size()>0){        //SCS CIRK 2022/07/25 CT69649C:
            picktaskPrintlabel printLabel = PrintLabelList.get(0);
            custname = printLabel.getCustname();
            if (custname == null) {
                custname = "";
            }

            Paragraph childParagraph = new Paragraph(custname, FONT_TABLE_CONTANT);
            childParagraph.setAlignment(Element.ALIGN_LEFT);
            reportBody.add(childParagraph);
        }else{
            mToastMessage.showToast(PickTaskStagingActivity.this,"No data available");
            LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskStagingActivity)");
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(PickTaskStagingActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String result = "failed";
            try {
                boolean isDataSentSuccess;
                isDataSentSuccess = DataToPrint();
                if (isDataSentSuccess) {
                    result = "success";
                }
                return result;

            } catch (Exception e) {

                Log.e("tag", "error", e);
                LogfileCreator.mAppendLog("In doInBackground method: "
                        + e.getMessage());
                result = "error";
                toasttext = "File Createion Failed";
            }

            return result;
        }


        @Override
        protected void onPostExecute(final String result) {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            if (result.equals("success")) {
                toasttext = "Print PDF creation Success ";
                boolean isDataSentSuccess = true;
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        toasttext);
                try {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                String storage = mPath;
                                File file = new File(storage);
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent intent = Intent.createChooser(target, "Open File");
                                startActivity(intent);

                            } catch (Exception e) {
                                mToastMessage.showToast(PickTaskStagingActivity.this,
                                        "No Application match to Open Print File");
                            }
                        }
                    }, 1000);

                } catch (Exception e) {
                    mToastMessage.showToast(PickTaskStagingActivity.this, "Not Software Match to Open Print File");
                    mSupporter.simpleNavigateTo(PickTaskStagingActivity.class);
                }


            } else {
                toasttext = "Print PDF creation Failed";
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        toasttext);
                mSupporter.simpleNavigateTo(PickTaskStagingActivity.class);

            }
        } // end of PostExecute method...

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating PDF...");
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.dialog.setMessage(values[0]);
        }
    }

    public void ExportData() {
        exportTranList = new ArrayList<picktaskdetail>();

        mDbHelper.openReadableDatabase();
        exportTranList = mDbHelper.getPickTaskDetail();
        mDbHelper.closeDatabase();

        if (exportTranList.size() != 0) {
            String exportXml = getRecordXmlExportPO(exportTranList);
            PickTaskStagingActivity.uploadDataToServiceExportItm ex = (PickTaskStagingActivity.uploadDataToServiceExportItm) new PickTaskStagingActivity.uploadDataToServiceExportItm()
                    .execute(new String[]{exportXml});
            String response = null;
            try {
                response = ex.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (exportTranList.size() != 0) {
                new ExportTranData(mSessionId, Globals.gUsercode, Globals.gCompanyId).execute();
            } else {
                ExportError();
            }
        }
    }

    private void ExportError() {
        AlertDialog.Builder alertExit = new AlertDialog.Builder(PickTaskStagingActivity.this);
        alertExit.setTitle("No Data");
        alertExit.setIcon(R.drawable.warning);
        alertExit.setCancelable(false);
        alertExit.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertExit.setMessage("No Data to Export");
        alertExit.show();
    }

    // Method that returns the XML to be exported
    public String getRecordXmlExportPO(List<picktaskdetail> dList) {
        String exportPODataXml = "";
        try {
            ExportPickTask exportData = new ExportPickTask();

            StringBuffer sb = new StringBuffer();
            sb.append("<" + "PickTaskData" + ">");
            for (int i = 0; i < dList.size(); i++) {
                exportData.writeXml(dList.get(i), sb, PickTaskStagingActivity.this, mDbHelper);
            }
            sb.append("</" + "PickTaskData" + ">");
            exportPODataXml = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            String errorCode = "Err-CLS-2";
            String errMsg = "pick List detail export failed";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage()
                    + "\n" + errMsg);
            String result = "error";
            return result;
        }
        return exportPODataXml;
    }

    // Async task to upload the created XML to the Web Service
    public class uploadDataToServiceExportItm extends AsyncTask<String, String, String> {
        private ProgressDialog mDialog;

        public uploadDataToServiceExportItm() {
            mDialog = new ProgressDialog(PickTaskStagingActivity.this);
            mDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... records) {
            String result = "result";
            File exportFile = mSupporter.getImpOutputFilePathByCompany(Globals.gUsercode,
                    "FinalExoprt", "PickTask" + ".xml");
            if (!exportFile.exists()) {
                Supporter.createFile(exportFile);
            } else {
                exportFile.delete(); // to refresh the file
                Supporter.createFile(exportFile);
            }

            try {
                FileOutputStream fos = new FileOutputStream(exportFile);
                fos.write(records[0].getBytes());
                result = "success";
                System.out.println("Export success");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                result = "nodata";
            } catch (IOException e) {
                e.printStackTrace();
                result = "input error";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mDialog != null) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }

            if (result.equals("success")) {

            } else if (result.equals("connection timeout")) {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Data not posted connection timeout");
                System.out.println("Data not posted connection timeout");
            } else if (result.equals("nodata")) {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Error in exporting, no response from server");
                System.out
                        .println("Error in exporting, no response from server");
            } else {
                mToastMessage.showToast(PickTaskStagingActivity.this, "Error in exporting");
                System.out.println("Error in exporting");
            }
        }
    }

    class ExportTranData extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String pUname, pSessionId, pCompId ;
        String result = "";

        public ExportTranData(String Session,String Uname, String Compid) {
            this.pSessionId = Session;
            this.pUname = Uname;
            this.pCompId = Compid;
            dialog = new ProgressDialog(PickTaskStagingActivity.this);
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

                SoapObject request = new SoapObject(NAMESPACE, METHOD_EXPORT_DATA);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                File xmlData = Supporter.getImportFolderPath(pUname
                        + "/FinalExoprt/PickTask.xml");
                String pXmldata = FileUtils.readFileToString(xmlData);
                PropertyInfo info = new PropertyInfo();
                info.setName("pSessionId");
                info.setValue(pSessionId);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pCompany");
                info.setValue(pCompId);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(pUname);
                info.setType(String.class);
                request.addProperty(info);
                info = new PropertyInfo();
                info.setName("pXmlData");
                info.setValue(pXmldata);
                info.setType(String.class);
                request.addProperty(info);
                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH +"/"+ APPLICATION_NAME +"/"+ URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_EXPORT_DATA;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(pUname, "Result", "PickTask" + ".xml");
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
                } else if(resultString.toString().contains("<Result>true</Result>")){
                    result ="success";
                } else {
                    result = "error";
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
                mDbHelper.openWritableDatabase();
                mDbHelper.deletePickTaskDetail();
                mDbHelper.closeDatabase();
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Data exported to Server successfully");
                mSupporter.simpleNavigateTo(MainmenuActivity.class);

            } else if (result.equals("server failed")) {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Failed to post, refer log file.");
            } else if (result.equals("PO Updation failed.")) {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "PO Updation failed");
            } else if (result.equalsIgnoreCase("time out error")){
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        result.toString());
            }

            dialog.cancel();
        }
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


            dialog = new ProgressDialog(PickTaskStagingActivity.this);
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
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Failed to post, refer log file.");
            }else if (result.equalsIgnoreCase("time out error")){

                //mToastMessage.showToast(PickTaskActivity.this, "Time out.");
            } else if (result.equalsIgnoreCase("error")) {
                mToastMessage.showToast(PickTaskStagingActivity.this,
                        "Unable to update Server");
            } else {
                mToastMessage.showToast(PickTaskStagingActivity.this,
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

