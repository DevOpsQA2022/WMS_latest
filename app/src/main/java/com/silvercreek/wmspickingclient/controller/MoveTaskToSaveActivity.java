package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Supporter.MyPREFERENCES;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.MoveTaskSlotList;
import com.silvercreek.wmspickingclient.model.SlotList;
import com.silvercreek.wmspickingclient.model.movetaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.List;

public class MoveTaskToSaveActivity extends AppCompatActivity {

    private TextView tvPallNo,tvWlotNo,tvFromSlot,tvUom,tvQty,tvItem,tvItemdesc;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private List<movetaskdetail> moveTaskDetail;
    private String palNo ="", wLontNo ="", fromSlot="", uom="", qty="",item="",itemDesc="", toSlot="";
    private EditText edtToSlot;
    private ImageView btnSlot_lookup;
    private String mSessionId ="", mCompany ="",mUsername ="",mDeviceId ="";
    public static String NAMESPACE = "";
    public static String URL_PROTOCOL = "";
    public static String URL_SERVICE_NAME = "";
    public static String APPLICATION_NAME = "";
    public static String URL_SERVER_PATH = "";
    private int mTimeout;
    private SharedPreferences sharedpreferences;
    public static String SOFT_KEYBOARD = "";
    private String mLoctid = "";
    private ListView slotList = null;
    private MoveTaskSlotListAdapter moveTaskSlotListAdapter;
    private List<MoveTaskSlotList> tMoveTaskSlotList;
    private Button btnMTSave,btnMTCancel;
    private Boolean isSlotAvilable=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_task_to_save);


        tvPallNo = findViewById(R.id.tvMTPalNo);
        tvWlotNo = findViewById(R.id.tvMTP_WlotNo);
        tvFromSlot = findViewById(R.id.tvMTFromSlt);
        tvUom = findViewById(R.id.tvMT_UOM);
        tvQty = findViewById(R.id.tvMTQty);
        tvItem = findViewById(R.id.tvItem);
        tvItemdesc = findViewById(R.id.tvItemDesc);
        edtToSlot = findViewById(R.id.edtMT_ToSLot);
        btnSlot_lookup = findViewById(R.id.imgBtn_lookup);
        btnMTSave = findViewById(R.id.btnMTSave);
        btnMTCancel = findViewById(R.id.btnMTCancel);

        edtToSlot.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(10)});

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
        NAMESPACE = NAMESPACE +"/";
        Globals.gNamespace=NAMESPACE;
        Globals.gProtocol=URL_PROTOCOL;
        Globals.gServicename=URL_SERVICE_NAME;
        Globals.gAppName=APPLICATION_NAME;
        Globals.gTimeout=sharedpreferences.getString("Timeout", "");
        mCompany = Globals.gCompanyDatabase;
        mLoctid = Globals.gLoctid;
        mUsername = Globals.gUsercode;
        mDeviceId = Globals.gDeviceId;


        edtToSlot.requestFocus();
        if(edtToSlot.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        if (SOFT_KEYBOARD.equals("CHECKED")){
            edtToSlot.setShowSoftInputOnFocus(false);

        }else {
            edtToSlot.setShowSoftInputOnFocus(true);

        }




        mDbHelper.openReadableDatabase();
        moveTaskDetail = mDbHelper.getMoveTaskDetailForSave(Globals.gMTTaskNo,Globals.gMTPalNO,Globals.gMTItem);
        mDbHelper.closeDatabase();

        item = moveTaskDetail.get(0).getItem();
        itemDesc = moveTaskDetail.get(0).getItmdesc();
        palNo = moveTaskDetail.get(0).getPalno();
        wLontNo = moveTaskDetail.get(0).getWlotno();
        fromSlot = moveTaskDetail.get(0).getFromSlot();
        uom = moveTaskDetail.get(0).getUmeasur();
        qty = String.valueOf(Math.round(Double.parseDouble(moveTaskDetail.get(0).getTqtyrq())));
        toSlot = moveTaskDetail.get(0).getToSlot();

        tvPallNo.setText(": "+palNo);
        tvWlotNo.setText(": "+wLontNo);
        tvFromSlot.setText(": "+fromSlot);
        tvUom.setText(": "+uom);
        tvQty.setText(": "+qty);
        tvItem.setText(": "+item);
        tvItemdesc.setText(": "+itemDesc);
        edtToSlot.setText(toSlot);
        edtToSlot.requestFocus();

        btnMTCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlert();
            }
        });

        btnMTSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ToSlot = edtToSlot.getText().toString();

                mDbHelper.openReadableDatabase();
                isSlotAvilable = mDbHelper.isMoveTaskSlotAvail(Globals.gMTLoctId ,Globals.gMTTaskNo,ToSlot);
                mDbHelper.closeDatabase();

                if (ToSlot.equals("")){
                    mToastMessage.showToast(MoveTaskToSaveActivity.this,"Enter valid To Slot");
                }else if (fromSlot.equals(ToSlot)) {
                    mToastMessage.showToast(MoveTaskToSaveActivity.this,"From Slot and To Slot cannot be the same.");
                    edtToSlot.setText("");
                    edtToSlot.requestFocus();
                }else if (!isSlotAvilable) {
                    mToastMessage.showToast(MoveTaskToSaveActivity.this,"Invalid Slot");
                    edtToSlot.setText("");
                    edtToSlot.requestFocus();
                }else{

                    mDbHelper.openWritableDatabase();
                    mDbHelper.updateMoveTaskDetail(palNo,fromSlot,qty,ToSlot,Globals.gMTTaskNo);
                    mDbHelper.closeDatabase();
                    mSupporter.simpleNavigateTo(MoveTaskSaveActivity.class);

                    // new MoveManuallyActivity.GetSlotNoForFinelSave().execute();
                }
            }
        });


        btnSlot_lookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDbHelper.openReadableDatabase();
                tMoveTaskSlotList = mDbHelper.selectMoveTaskSlotList(Globals.gMTLoctId ,Globals.gMTTaskNo,Globals.gMTFromSlot);
                mDbHelper.closeDatabase();

                LayoutInflater li = LayoutInflater.from(MoveTaskToSaveActivity.this);
                View promptsView = li.inflate(R.layout.receive_slot_list,null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        MoveTaskToSaveActivity.this);
                alertDialogBuilder.setView(promptsView);
                final AlertDialog alertDialog = alertDialogBuilder.create();

                slotList = (ListView) promptsView.findViewById(R.id.SlotList);
                moveTaskSlotListAdapter = new MoveTaskSlotListAdapter(MoveTaskToSaveActivity.this, tMoveTaskSlotList);
                slotList.setAdapter(moveTaskSlotListAdapter);
                slotList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MoveTaskSlotList MoveTaskSlotList = (MoveTaskSlotList) moveTaskSlotListAdapter.getItem(i);
                        Globals.LookUp_Slot = MoveTaskSlotList.getSlot().toString();
                        alertDialog.dismiss();

                        edtToSlot.setText(Globals.LookUp_Slot);
                        edtToSlot.clearFocus();



                    }
                });


                alertDialog.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        mSupporter.simpleNavigateTo(MoveTaskSaveActivity.class);
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
                        mSupporter.simpleNavigateTo(MoveTaskSaveActivity.class);
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