package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.PrintPalletTag;
import com.silvercreek.wmspickingclient.model.picktaskPrintlabel;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class PrintPalletActivity extends AppCompatActivity {
    private EditText edtPallet;
    private Button btn_cancel,btn_print;
    private String mPallet = "";
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private File mImpOutputFile;
    private int mTimeout;
    private String mLoctid = "";
    private String Getmsg = "";
    private String mSessionId ="", mCompany ="", mUsername ="", mDeviceId = "";
    private SharedPreferences sharedpreferences;
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    public static String SOFT_KEYBOARD = "";
    public static final String METHOD_GET_PALLET_DETAILS = "DataUtils_PrintPalletTag";
    public String toasttext = "";
    private String mPath;
    public String filename = "PrintPalletTag.pdf";
    private File pdfFile = null;
    private Document document;
    private PdfContentByte cb;
    private PdfPTable table;
    private PdfWriter docWriter;
    private List<PrintPalletTag> PrintLabelList;
    private PdfPCell cell;
    private String commodity, method, item, lotId, variety, size, Package, grade, label, setId, palno,region,outPalletNo,productMat,pQty,date;
    //private String stop, trailer, route, dock, deldate, order, task, custid, custname, picker, palno;


    public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);
    public static Font FONT_TABLE_CONTANT_WITHOUT_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD);
    public static Font FONT_BODY_BELLOW_PRINT = new Font(Font.FontFamily.TIMES_ROMAN, 8, Font.NORMAL);

    /*public static Font FONT_TABLE_CONTANT = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.BOLD);
    public static Font FONT_TABLE_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.NORMAL);
    public static Font FONT_BODY = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
    public static Font FONT_TABLE_CONTANT_WITHOUT_BOLD = new Font(Font.FontFamily.TIMES_ROMAN, 30, Font.NORMAL);*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pallet);
        edtPallet = findViewById(R.id.PP_edtPallet);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_print = findViewById(R.id.btn_print);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();

        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();
        mUsername = Globals.gUsercode;


        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        NAMESPACE = sharedpreferences.getString("Namespace", "");
        URL_PROTOCOL = sharedpreferences.getString("Protocol", "");
        URL_SERVICE_NAME = sharedpreferences.getString("Servicename", "");
        URL_SERVER_PATH = sharedpreferences.getString("Serverpath", "");
        APPLICATION_NAME = sharedpreferences.getString("AppName", "");
        mTimeout = Integer.valueOf(sharedpreferences.getString("Timeout", "0"));
        SOFT_KEYBOARD = sharedpreferences.getString("SoftKey", "");
        NAMESPACE = NAMESPACE + "/";
        Globals.gNamespace = NAMESPACE;
        Globals.gProtocol = URL_PROTOCOL;
        Globals.gServicename = URL_SERVICE_NAME;
        Globals.gAppName = APPLICATION_NAME;
        Globals.gTimeout = sharedpreferences.getString("Timeout", "");
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;



        edtPallet.requestFocus();
        if (edtPallet.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")) {
            edtPallet.setShowSoftInputOnFocus(false);
        } else {
            edtPallet.setShowSoftInputOnFocus(true);
        }

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPallet.setText("");
                mSupporter.simpleNavigateTo(DataUtilitiesActivity.class);
            }
        });
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPallet = edtPallet.getText().toString().trim();

                if (mPallet.equalsIgnoreCase("")) {
                    mToastMessage.showToast(PrintPalletActivity.this,
                            "Please Enter the Pallet.");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            edtPallet.requestFocus();
                        }
                    }, 150);

                } else {

                    new GetPrintPalletDetails(mUsername).execute();
                }
            }
        });

        edtPallet.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:

                            /*mDbHelper.getWritableDatabase();
                            mDbHelper.DeletePickTaskScanPallet();
                            mDbHelper.closeDatabase();*/

                            mPallet = edtPallet.getText().toString().trim();

                            if (mPallet.equalsIgnoreCase("")) {
                                mToastMessage.showToast(PrintPalletActivity.this,
                                        "Please Enter the Pallet.");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        edtPallet.requestFocus();
                                    }
                                }, 150);

                            } else {

                                new GetPrintPalletDetails(mUsername).execute();
                            }
                        default:
                            break;
                    }
                }
                return false;
            }
        });

    }
    private void PDFCreate() {

        mDbHelper.openReadableDatabase();
        PrintLabelList = mDbHelper.getPrintPalletTag();
        mDbHelper.closeDatabase();
        new PrinterConnectOperation().execute();
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


    private void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(PrintPalletActivity.this, mPath);
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ex) {
            Log.e("RK", "" + ex.getMessage());
            Toast.makeText(PrintPalletActivity.this, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }


    private class PrinterConnectOperation extends
            AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        public PrinterConnectOperation() {
            dialog = new ProgressDialog(PrintPalletActivity.this);
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
                    printPDF();
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
                boolean isDataSentSuccess = true;

            } else {
                toasttext = "Print Failed";
                mToastMessage.showToast(PrintPalletActivity.this,
                        toasttext);
               // mSupporter.simpleNavigateTo(PickTaskActivity.class);

            }
        } // end of PostExecute method...

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Creating...");
            this.dialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            this.dialog.setMessage(values[0]);
        }
    }


    public boolean DataToPrint() {
        boolean resultSent = false;
        try {
            createfile();
            document = new Document();
            document.setMargins(23, 3, 2, 1);
            docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
            cb = docWriter.getDirectContent();
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);
                palno = printLabel.getPalletNo();

                Barcode128 barcode128 = new Barcode128();
                barcode128.setCode(palno);
                barcode128.setFont(null);
                barcode128.setBarHeight(37f);
                barcode128.setCodeType(Barcode.CODE128);
                Image code128Image = barcode128.createImageWithBarcode(cb, null, null);
                code128Image.setAlignment(Image.ALIGN_LEFT);
                code128Image.setWidthPercentage(50);
                code128Image.setIndentationLeft(110);
                document.add(code128Image);
            } else {
                mToastMessage.showToast(PrintPalletActivity.this, "No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskActivity)");
            }

            Paragraph reportDetail1 = new Paragraph();
            reportDetail1.setFont(FONT_TABLE_CONTANT); //
            writeDetail1(reportDetail1);
            document.add(reportDetail1);

            Paragraph reportDetail_2 = new Paragraph();
            reportDetail_2.setFont(FONT_TABLE_CONTANT); //
            writeLine11(reportDetail_2);
            document.add(reportDetail_2);

            Paragraph reportDetail_3 = new Paragraph();
            reportDetail_3.setFont(FONT_TABLE_CONTANT); //
            writeLine33(reportDetail_3);
            document.add(reportDetail_3);

            Paragraph reportDetail_4 = new Paragraph();
            reportDetail_4.setFont(FONT_TABLE_CONTANT); //
            writeLine44(reportDetail_4);
            document.add(reportDetail_4);

            Paragraph reportDetail_5 = new Paragraph();
            reportDetail_5.setFont(FONT_TABLE_CONTANT); //
            writeLine55(reportDetail_5);
            document.add(reportDetail_5);

            Paragraph reportDetail_6 = new Paragraph();
            reportDetail_6.setFont(FONT_TABLE_CONTANT); //
            writeLine66(reportDetail_6);
            document.add(reportDetail_6);

            Paragraph reportDetail_7 = new Paragraph();
            reportDetail_7.setFont(FONT_TABLE_CONTANT); //
            writeLine77(reportDetail_7);
            document.add(reportDetail_7);

            Paragraph reportDetail_8 = new Paragraph();
            reportDetail_8.setFont(FONT_TABLE_CONTANT); //
            writeLine88(reportDetail_8);
            document.add(reportDetail_8);

            Paragraph reportDetail_9 = new Paragraph();
            reportDetail_9.setFont(FONT_TABLE_CONTANT); //
            writeLine99(reportDetail_9);
            document.add(reportDetail_9);

            /*Paragraph reportDetail2 = new Paragraph();
            reportDetail2.setFont(FONT_BODY); //
            writeDetail2(reportDetail2);
            document.add(reportDetail2);*/

            /*Paragraph reportDetail3 = new Paragraph();
            reportDetail3.setFont(FONT_TABLE_CONTANT); //
            writeDetail3(reportDetail3);
            document.add(reportDetail3);*/

            document.close();
            resultSent = true;

        } catch (Exception e) {
            resultSent = false;
        } finally {
            return resultSent;
        }
    }



    /*
    public void writeDetail3(Paragraph reportBody) {
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);
                custname = printLabel.getSlot();
                if (custname == null) {
                    custname = "";
                }

                Paragraph childParagraph = new Paragraph(custname, FONT_TABLE_CONTANT);
                childParagraph.setAlignment(Element.ALIGN_LEFT);
                reportBody.add(childParagraph);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


/*
    public void writeDetail2(Paragraph reportBody) {
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setWidthPercentage(100);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);
                route = printLabel.getCommod();
                if (route == null) {
                    route = "";
                }
                route = "Route " + route;

                dock = printLabel.getLotId();
                if (dock == null) {
                    dock = "";
                }
                dock = "Dock " + dock;

                deldate = printLabel.getQty();
                if (deldate == null) {
                    deldate = "";
                }
                deldate = "Del Date " + deldate;

                order = printLabel.gettQty();
                if (order == null) {
                    order = "";
                }
                order = "Order # " + order;

                task = printLabel.getItem();
                if (task == null) {
                    task = "";
                }
                task = "Task #" + task;

                custid = printLabel.getCommod();
                if (custid == null) {
                    custid = "";
                }
                custid = "Cust #" + custid;

                picker = printLabel.getLotId();
                if (picker == null) {
                    picker = "";
                }
                picker = "Picker " + picker;

                cell = new PdfPCell(new Phrase(route, FONT_TABLE_BODY));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            } else {
                mToastMessage.showToast(PrintPalletActivity.this, "No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskActivity)");
            }


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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/


    public void writeDetail1(Paragraph reportBody) {
        float[] columnWidths = {5f, 4f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

//                trailer = printLabel.getQty();
                palno = printLabel.getPalletNo();
                if (palno == null) {
                    palno = "";
                }
                palno = "Pallet # : " + palno;

                cell = new PdfPCell(new Phrase(palno, FONT_TABLE_CONTANT));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingBottom(10f);
                table.addCell(cell);
            } else {
                mToastMessage.showToast(PrintPalletActivity.this, "No data available");
                LogfileCreator.mAppendLog("No data available in PrintLabelList(PickTaskActivity)");
            }
            cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPaddingBottom(10f);
            table.addCell(cell);
            reportBody.add(table);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLine44(Paragraph reportBody) {
        float[] columnWidths = {5f, 5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                size = printLabel.getSize();
                if (size == null) {
                    size = "";
                }
                size = "   " + size;

                Package = printLabel.getPackage();
                if (Package == null) {
                    Package = "";
                }
                Package = "   " + Package;

                grade = printLabel.getGrade();
                if (grade == null) {
                    grade = "";
                }
                grade = "   " + grade;

                label = printLabel.getLabel();
                if (label == null) {
                    label = "";
                }
                label = "   " + label;



                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("SIZE",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(size,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("PACKAGE",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(Package,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("GRADE",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(grade,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("LABEL",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(label,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);









                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeLine66(Paragraph reportBody) {
        float[] columnWidths = {5f, 5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                region = printLabel.getCountryid();
                if (region == null) {
                    region = "";
                }
                region = "   " + region;

                outPalletNo = printLabel.getOutpalno();
                if (outPalletNo == null) {
                    outPalletNo = "";
                }
                outPalletNo = "   " + outPalletNo;

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("REGION",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(region,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("OUTSIDE PALLET NUMBER",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(outPalletNo,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeLine99(Paragraph reportBody) {
        float[] columnWidths = {5f, 5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.getDefaultCell().setBorderWidth(0f);

        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                palno = printLabel.getPalletNo();
                if (palno == null) {
                    palno = "";
                }
                palno = "" + palno;

                lotId = printLabel.getLotId();
                if (lotId == null) {
                    lotId = "";
                }
                lotId = "" + lotId;

                item = printLabel.getItem();
                if (item == null) {
                    item = "";
                }
                item = "" + item;

                commodity = printLabel.getCommod();
                if (commodity == null) {
                    commodity = "";
                }
                commodity = "" + commodity;

                variety = printLabel.getVariety();
                String[] vpLckAry = String.valueOf(variety).split(" ");
                variety = vpLckAry[0];

                if (variety == null) {
                    variety = "";
                }
                variety = "" + variety;

                size = printLabel.getSize();
                if (size == null) {
                    size = "";
                }
                size = "" + size;

                pQty = printLabel.getQty();
                if (pQty == null) {
                    pQty = "";
                }
                pQty = "" + pQty;


                PdfPTable testTable = new PdfPTable(3);

                //Pallet
                PdfPCell pd = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd.addElement(new Paragraph(palno,FONT_BODY_BELLOW_PRINT));
                pd.setBorder(Rectangle.NO_BORDER);
                pd.setPaddingTop(-3f);
                pd.setPaddingBottom(3f);
                pd.setPaddingLeft(3f);
                pd.setNoWrap(true);
                testTable.addCell(pd);

                //lotNo
                PdfPCell pd1 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd1.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd1.addElement(new Paragraph(lotId,FONT_BODY_BELLOW_PRINT));
                pd1.setBorder(Rectangle.NO_BORDER);
                pd1.setPaddingTop(-3f);
                pd1.setPaddingBottom(3f);
                pd1.setPaddingLeft(3f);
                pd1.setNoWrap(true);
                testTable.addCell(pd1);

                //product
                PdfPCell pd2 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd2.setHorizontalAlignment(Element.ALIGN_LEFT);
//                pd2.addElement(new Paragraph("wwwwwwwwwwwwwwww",FONT_BODY_BELLOW_PRINT));
                pd2.addElement(new Paragraph(item,FONT_BODY_BELLOW_PRINT));
                pd2.setBorder(Rectangle.NO_BORDER);
                pd2.setPaddingTop(-3f);
                pd2.setPaddingBottom(3f);
                pd2.setPaddingLeft(3f);
                //pd2.setNoWrap(true);
                pd2.setFixedHeight(15f);
                testTable.addCell(pd2);

                table.addCell(testTable);



                PdfPTable testTable1 = new PdfPTable(3);

                //pallet
                PdfPCell pd11 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd11.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd11.addElement(new Paragraph(palno,FONT_BODY_BELLOW_PRINT));
                pd11.setBorder(Rectangle.NO_BORDER);
                pd11.setPaddingTop(-3f);
                pd11.setPaddingBottom(3f);
                pd11.setPaddingLeft(3f);
                pd11.setNoWrap(true);
                testTable1.addCell(pd11);

                //lotNo
                PdfPCell pd22 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd22.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd22.addElement(new Paragraph(lotId,FONT_BODY_BELLOW_PRINT));
                pd22.setBorder(Rectangle.NO_BORDER);
                pd22.setPaddingTop(-3f);
                pd22.setPaddingBottom(3f);
                pd22.setPaddingLeft(3f);
                pd22.setNoWrap(true);
                testTable1.addCell(pd22);

                //product
                PdfPCell pd33 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd33.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd33.addElement(new Paragraph(item,FONT_BODY_BELLOW_PRINT));
                pd33.setBorder(Rectangle.NO_BORDER);
                pd33.setPaddingTop(-3f);
                pd33.setPaddingBottom(3f);
                pd33.setPaddingLeft(3f);
                pd33.setFixedHeight(15f);
//                pd33.setNoWrap(true);
                testTable1.addCell(pd33);

                table.addCell(testTable1);



                PdfPTable testTable2 = new PdfPTable(3);

                //commodity
                PdfPCell pd111 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd111.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd111.addElement(new Paragraph(commodity,FONT_BODY_BELLOW_PRINT));
                pd111.setBorder(Rectangle.NO_BORDER);
                pd111.setPaddingTop(-3f);
                pd111.setPaddingBottom(3f);
                pd111.setPaddingLeft(3f);
                pd111.setNoWrap(true);
                testTable2.addCell(pd111);

                //variety
                PdfPCell pd222 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd222.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd222.addElement(new Paragraph(variety,FONT_BODY_BELLOW_PRINT));
                pd222.setBorder(Rectangle.NO_BORDER);
                pd222.setPaddingTop(-3f);
                pd222.setPaddingBottom(3f);
                pd222.setPaddingLeft(13f);
                pd222.setNoWrap(true);
                testTable2.addCell(pd222);

                //empty
                PdfPCell pd333 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd333.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd333.addElement(new Paragraph("",FONT_BODY_BELLOW_PRINT));
                pd333.setBorder(Rectangle.NO_BORDER);
                pd333.setPaddingTop(-3f);
                pd333.setPaddingBottom(3f);
                pd333.setPaddingLeft(3f);
                testTable2.addCell(pd333);

                table.addCell(testTable2);

                PdfPTable testTable3 = new PdfPTable(3);

                //commodity
                PdfPCell pd1111 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd1111.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd1111.addElement(new Paragraph(commodity,FONT_BODY_BELLOW_PRINT));
                pd1111.setBorder(Rectangle.NO_BORDER);
                pd1111.setPaddingTop(-3f);
                pd1111.setPaddingBottom(3f);
                pd1111.setPaddingLeft(3f);
                pd1111.setNoWrap(true);
                testTable3.addCell(pd1111);

                //variety
                PdfPCell pd2222 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd2222.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd2222.addElement(new Paragraph(variety,FONT_BODY_BELLOW_PRINT));
                pd2222.setBorder(Rectangle.NO_BORDER);
                pd2222.setPaddingTop(-3f);
                pd2222.setPaddingBottom(3f);
                pd2222.setPaddingLeft(13f);
                pd2222.setNoWrap(true);
                testTable3.addCell(pd2222);

                //empty
                PdfPCell pd3333 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd3333.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd3333.addElement(new Paragraph("",FONT_BODY_BELLOW_PRINT));
                pd3333.setBorder(Rectangle.NO_BORDER);
                pd3333.setPaddingTop(-3f);
                pd3333.setPaddingBottom(3f);
                pd3333.setPaddingLeft(3f);
                pd3333.setNoWrap(true);
                testTable3.addCell(pd3333);

                table.addCell(testTable3);


                PdfPTable testTable4 = new PdfPTable(3);
                //size
                PdfPCell pd11111 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd11111.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd11111.addElement(new Paragraph(size,FONT_BODY_BELLOW_PRINT));
                pd11111.setBorder(Rectangle.NO_BORDER);
                pd11111.setPaddingTop(-3f);
                pd11111.setPaddingBottom(3f);
                pd11111.setPaddingLeft(3f);
                pd11111.setNoWrap(true);
                testTable4.addCell(pd11111);

                //qty
                PdfPCell pd22222 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd22222.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd22222.addElement(new Paragraph(pQty,FONT_BODY_BELLOW_PRINT));
                pd22222.setBorder(Rectangle.NO_BORDER);
                pd22222.setPaddingTop(-3f);
                pd22222.setPaddingBottom(3f);
                pd22222.setPaddingLeft(3f);
                pd22222.setNoWrap(true);
                testTable4.addCell(pd22222);

                //empty
                PdfPCell pd33333 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd33333.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd33333.addElement(new Paragraph("",FONT_BODY_BELLOW_PRINT));
                pd33333.setBorder(Rectangle.NO_BORDER);
                pd33333.setPaddingTop(-3f);
                pd33333.setPaddingBottom(3f);
                pd33333.setPaddingLeft(3f);
                pd33333.setNoWrap(true);
                testTable4.addCell(pd33333);

                table.addCell(testTable4);


                PdfPTable testTable5 = new PdfPTable(3);
                //size
                PdfPCell pd111111 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd111111.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd111111.addElement(new Paragraph(size,FONT_BODY_BELLOW_PRINT));
                pd111111.setBorder(Rectangle.NO_BORDER);
                pd111111.setPaddingTop(-3f);
                pd111111.setPaddingBottom(3f);
                pd111111.setPaddingLeft(3f);
                pd111111.setNoWrap(true);
                testTable5.addCell(pd111111);

                //qty
                PdfPCell pd222222 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd222222.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd222222.addElement(new Paragraph(pQty,FONT_BODY_BELLOW_PRINT));
                pd222222.setBorder(Rectangle.NO_BORDER);
                pd222222.setPaddingTop(-3f);
                pd222222.setPaddingBottom(3f);
                pd222222.setPaddingLeft(3f);
                pd222222.setNoWrap(true);
                testTable5.addCell(pd222222);

                //empty
                PdfPCell pd333333 = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                pd333333.setHorizontalAlignment(Element.ALIGN_LEFT);
                pd333333.addElement(new Paragraph("",FONT_BODY_BELLOW_PRINT));
                pd333333.setBorder(Rectangle.NO_BORDER);
                pd333333.setPaddingTop(-3f);
                pd333333.setPaddingBottom(3f);
                pd333333.setNoWrap(true);
                pd333333.setPaddingLeft(3f);
                testTable5.addCell(pd333333);

                table.addCell(testTable5);



                /*cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph(size+pQty,FONT_BODY_BELLOW_PRINT));
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(3f);
                cell.setPaddingLeft(3f);
                table.addCell(cell);*/


                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLine88(Paragraph reportBody) {
        float[] columnWidths = {5f, 5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                region = printLabel.getCountryid();
                if (region == null) {
                    region = "";
                }
                region = "   " + region;

                outPalletNo = printLabel.getOutpalno();
                if (outPalletNo == null) {
                    outPalletNo = "";
                }
                outPalletNo = "   " + outPalletNo;

                palno = printLabel.getPalletNo();

                Barcode128 barcode128 = new Barcode128();
                barcode128.setCode(palno);
                barcode128.setFont(null);
                barcode128.setCodeType(Barcode.CODE128);
                Image code128Image = barcode128.createImageWithBarcode(cb, null, null);
                code128Image.setAlignment(Image.ALIGN_CENTER);
                code128Image.setWidthPercentage(30);
                code128Image.setIndentationLeft(10);
                code128Image.setIndentationRight(10);

                cell = new PdfPCell(new PdfPCell(code128Image , true));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingTop(10f);
                cell.setPaddingRight(60f);
                cell.setPaddingLeft(40f);
                table.addCell(cell);

                cell = new PdfPCell(new PdfPCell(code128Image , true));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPaddingTop(10f);
                cell.setPaddingRight(60f);
                cell.setPaddingLeft(40f);
                table.addCell(cell);

                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeLine11(Paragraph reportBody) {
        float[] columnWidths = {5f, 5f};
        float[] totalHeight = {1f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                commodity = printLabel.getCommod();
                if (commodity == null) {
                    commodity = "";
                }
                commodity = "   " + commodity;

                method = printLabel.getSizesort();
                if (method == null) {
                    method = "";
                }
                method = "   " + method;

                item = printLabel.getItem();
                if (item == null) {
                    item = "";
                }
                item = "   " + item;

                lotId = printLabel.getLotId();
                if (lotId == null) {
                    lotId = "";
                }
                lotId = "   " + lotId;



                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("COMMODITY",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(commodity,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingBottom(4f);
                cell.setPaddingTop(-3f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("METHOD",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(method,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingLeft(3f);
                cell.setPaddingBottom(4f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("PRODUCT ID",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(item,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("LOT ID",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(lotId,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void writeLine33(Paragraph reportBody) {
        float[] columnWidths = {5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                variety = printLabel.getVariety();
                if (variety == null) {
                    variety = "";
                }
                variety = "   " + variety;


                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("VARIETY",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(variety,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeLine55(Paragraph reportBody) {
        float[] columnWidths = {5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                setId = printLabel.getSetid();
                if (setId == null) {
                    setId = "";
                }
                setId = "   " + setId;


                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("PURCHASE ORDER/GROWER SET ID",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(setId,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(4f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void writeLine77(Paragraph reportBody) {
        float[] columnWidths = {5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                productMat = printLabel.getItemDesc();
                if (productMat == null) {
                    productMat = "";
                }
                productMat = "   " + productMat;

                pQty = printLabel.getQty();
                if (pQty == null) {
                    pQty = "";
                }
                pQty = "   " + pQty;

                date = printLabel.getPltcrea();
                if (date == null) {
                    date = "";
                }
                date = "   " + date;


                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("PRODUCT MATERIALS",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(productMat,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-2f);
                cell.setPaddingBottom(2f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("QUANTITY",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(pQty,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-2f);
                cell.setPaddingBottom(2f);
                cell.setPaddingLeft(3f);
                cell.setFixedHeight(32f);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("DATE",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(date,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-2f);
                cell.setPaddingBottom(2f);
                cell.setFixedHeight(32f);
                cell.setPaddingLeft(3f);
                table.addCell(cell);

                reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeLine22(Paragraph reportBody) {
        float[] columnWidths = {5f, 5f};
        table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        try {
            if (PrintLabelList.size() > 0) {       //SCS CIRK 2022/07/25 CT69649C:
                PrintPalletTag printLabel = PrintLabelList.get(0);

                commodity = printLabel.getItem();
                if (commodity == null) {
                    commodity = "";
                }
                commodity = "   " + commodity;



                cell = new PdfPCell(new Phrase("", FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.addElement(new Paragraph("VARIETY",FONT_TABLE_BODY));
                cell.addElement(new Paragraph(commodity,FONT_TABLE_CONTANT_WITHOUT_BOLD));
                cell.setBorder(Rectangle.BOX);
                cell.setPaddingTop(-3f);
                cell.setPaddingBottom(3f);
                cell.setPaddingLeft(3f);
                table.addCell(cell);


            reportBody.add(table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class GetPrintPalletDetails extends AsyncTask<String, String, String> {
        private ProgressDialog dialog;
        private String uCode;

        public GetPrintPalletDetails(String user) {
            this.uCode = user;
            dialog = new ProgressDialog(PrintPalletActivity.this);
            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Please wait...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String result = "";

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_PALLET_DETAILS);
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
                info.setName("pPalno");
                info.setValue(mPallet);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pLoctid");
                info.setValue(mLoctid);
                info.setType(String.class);
                request.addProperty(info);

                info = new PropertyInfo();
                info.setName("pUserName");
                info.setValue(mUsername);
                info.setType(String.class);
                request.addProperty(info);

                envelope.dotNet = true;// to handle .net services asmx/aspx
                envelope.setOutputSoapObject(request);
                HttpTransportSE ht = new HttpTransportSE(URL_PROTOCOL + URL_SERVER_PATH + "/" + APPLICATION_NAME + "/" + URL_SERVICE_NAME);
                ht.debug = true;
                String soap_action = NAMESPACE + METHOD_GET_PALLET_DETAILS;
                ht.call(soap_action, envelope);
                SoapPrimitive resultString = (SoapPrimitive) envelope.getResponse();
                File mImpOutputFile = Supporter.getImpOutputFilePathByCompany(uCode, "01", "GetPalletTag_Details" + ".xml");
                if (!mImpOutputFile.exists()) {
                    Supporter.createFile(mImpOutputFile);
                } else {
                    mImpOutputFile.delete(); // to refresh the file
                    Supporter.createFile(mImpOutputFile);
                }

                BufferedWriter buf = new BufferedWriter(new FileWriter(
                        mImpOutputFile, true));

                buf.append(resultString.toString());

                if (resultString.toString().contains("false")) {
                    result = "Failed";
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
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
            if (result.equals("success")) {

               new Load_PrintPalletTag().execute();

            } else if (result.equals("Failed")) {

                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PrintPalletActivity.this,
                        "Invalid Pallet");
                edtPallet.setText("");
                edtPallet.requestFocus();

            } else if (result.equals("Assinged another user")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                Getmsg = GetErrorMessage();
                mToastMessage.showToast(PrintPalletActivity.this,
                        Getmsg);
                edtPallet.setText("");
                edtPallet.requestFocus();
            }  else if (result.equalsIgnoreCase("time out error")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                new GetPrintPalletDetails(mUsername).execute();
            } else {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PrintPalletActivity.this, result);
                edtPallet.setText("");
                edtPallet.requestFocus();
            }
        }
    }


    private class Load_PrintPalletTag extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;


        public Load_PrintPalletTag() {
            dialog = new ProgressDialog(PrintPalletActivity.this);

            dialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait...");
            // this.dialog.setMessage("Please wait until the Item data is loaded");
            this.dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            String errMsg = "";

            try {

                String result = "";

                DataLoader fileLoader = new DataLoader(PrintPalletActivity.this, mDbHelper, Globals.gUsercode);

                List<String> importFileList = mSupporter.loadImportFileList();

                int totImpFile = importFileList.size();

                File salPer_folder_path = Supporter.getImportFolderPath(Globals.gUsercode);

                List<String> compList = mSupporter.getFolderNames(salPer_folder_path);

                int compSize = compList.size();

                mDbHelper.openWritableDatabase();
                mDbHelper.deletePrintPalletDetails();
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

                            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "GetPalletTag_Details" + ".xml");
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
                PDFCreate();
               // mToastMessage.showToast(PrintPalletActivity.this, "Loaded successfully.");
               // edtPallet.requestFocus();

            } else if (result.equals("nosd")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PrintPalletActivity.this, "Sd card required");
            } else if (result.equals("parsing error")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PrintPalletActivity.this, "Error during parsing the data");
                edtPallet.requestFocus();
            } else if (result.equals("File not available")) {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PrintPalletActivity.this, "File not available");
                edtPallet.requestFocus();
            } else {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                mToastMessage.showToast(PrintPalletActivity.this, "Error");
                edtPallet.requestFocus();
            }
        }


    }

    private void endDBTransaction() {
        mDbHelper.mSetTransactionSuccess(); // setting the transaction

        Log.i("Transaction success", "Transaction success.");
        mDbHelper.mEndTransaction();
        Log.i("Transaction success", "Transaction end.");
        mDbHelper.closeDatabase();
        Log.i("DB closed", "Database closed successfully.");
    }
    private void startDBTransaction(String action) {
        // transaction is started here..
        mDbHelper.getWritableDatabase();
        Log.i("Writable DB Open", "Writable Database Opened.");
        mDbHelper.mBeginTransaction();
        Log.i("Transaction started", "Transaction successfully started for "
                + action);
    }



    private String GetErrorMessage() {

        String GetErrMsg = "";
        try {
            //creating a constructor of file class and parsing an XML file
            mImpOutputFile = Supporter.getImpOutputFilePathByCompany(Globals.gUsercode, "01", "SubPickTaskList" + ".xml");
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(mImpOutputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Acknowledgement");
            // nodeList is not iterable, so we are using for loop
            for (int itr = 0; itr < nodeList.getLength(); itr++) {
                Node node = nodeList.item(itr);
                System.out.println("\nNode Name :" + node.getNodeName());
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element eElement = (org.w3c.dom.Element) node;
                    GetErrMsg = eElement.getElementsByTagName("ErrorMessage").item(0).getTextContent();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String errorCode = "Err501";
            LogfileCreator.mAppendLog(errorCode + " : " + e.getMessage());
            String result = "Invalid File";
            return result;
        }
        return GetErrMsg;
    }

    @Override
    public void onBackPressed() {
        edtPallet.setText("");
        mSupporter.simpleNavigateTo(DataUtilitiesActivity.class);
    }
}