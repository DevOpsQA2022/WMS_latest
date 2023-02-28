package com.silvercreek.wmspickingclient.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.MoveManuallyTransaction;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.Supporter;
import java.util.List;


public class MPMList extends AppBaseActivity {

    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ToastMessage mToastMessage;
    private String mSessionId;
    private TextView tvManuallyMovedPro,tvManuallyDisplay;
    private List<MoveManuallyTransaction> mmTran;
    private MPMListAdapter adapter;
    private ListView mpList;
    private Button btnScanNewLot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpmlist);

        mDbHelper = new WMSDbHelper(this);
        mSupporter = new Supporter(this, mDbHelper);
        mToastMessage = new ToastMessage();
        mDbHelper.openReadableDatabase();
        mSessionId = mDbHelper.mGetSessionId();
        mDbHelper.closeDatabase();

        tvManuallyMovedPro = findViewById(R.id.tvMPM);
        tvManuallyDisplay = findViewById(R.id.tvMPMDisplay);
        mpList = findViewById(R.id.lst_movedProducts);
        btnScanNewLot = findViewById(R.id.btnScanNewLot);

        mDbHelper.openReadableDatabase();
        mmTran = mDbHelper.getMmTran();
        mDbHelper.closeDatabase();

        adapter = new MPMListAdapter(MPMList.this, mmTran);
        mpList.setAdapter(adapter);

        btnScanNewLot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSupporter.simpleNavigateTo(MoveManuallyActivity.class);
            }
        });

        mpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MoveManuallyTransaction mmTran = (MoveManuallyTransaction) adapter.getItem(position);
                Globals.mmTlot = mmTran.getMmTranWlotno();
                Globals.mmTSlot = mmTran.getMmTranSlot();
                Globals.mmTUOM = mmTran.getMmTranUOM();
                Globals.mmTItem = mmTran.getMmTranItem();
                Globals.mmTLotRefid = mmTran.getMmTranLotrefid();
                /*Globals.mmTLotRefid = mmTran.getMmTranWlotno();*/

                mSupporter.simpleNavigateTo(MMToSlotActivity.class);
            }
        });




    }

    @Override
    public void onBackPressed() {
        if(mmTran.size()>0){
            cancelAlert();
        }else {
            mSupporter.simpleNavigateTo(MainmenuActivity.class);

        }


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
                        mSupporter.simpleNavigateTo(MainmenuActivity.class);
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
