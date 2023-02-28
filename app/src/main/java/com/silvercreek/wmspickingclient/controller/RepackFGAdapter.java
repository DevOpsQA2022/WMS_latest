package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.RepackFG;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class RepackFGAdapter extends ArrayAdapter<RepackFG> {

    private final List<RepackFG> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<RepackFG> tpicktaskdetail=null;
    public Double grtrepackG_Qty = 0.0;

    static class ViewHolder {

        protected TextView tvQty;
        protected TextView tvUom;
        protected TextView tvItem;
        protected TextView tvDesc;
        protected TextView tvlOT;
    }

    public RepackFGAdapter(Activity context, List<RepackFG> list) {
        super(context, R.layout.adapter_repackfg_detail, list);
        this.context = context;
        this.list = list;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_repackfg_detail, null);

           /* if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
*/
            //Completed TASK background color change code
          /*  mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(strFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
            for(int i=0;i<tpicktaskdetail.size();i++){
                int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                if(position==pos){
                    view.setBackgroundColor(Color.parseColor("#00FF00"));
                }
            }
            }

            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(subFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){
                    int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#FF7F50"));
                    }
                }


            }*/

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvQty = (TextView) view
                    .findViewById(R.id.tvQty);
            viewHolder.tvUom  = (TextView) view
                    .findViewById(R.id.tvUom);
            viewHolder.tvItem = (TextView) view
                    .findViewById(R.id.tvItem);
            viewHolder.tvDesc = (TextView) view.findViewById(R.id.tvDesc);
            viewHolder.tvlOT = (TextView) view.findViewById(R.id.tvlOT);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            /*if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }*/

        }

        ViewHolder holder = (ViewHolder) view.getTag();

       /* String qty = list.get(position).getTQty();
        String decnum = list.get(position).getdecnum();
        if(decnum==null){
            decnum="0";
        }
        String cQty = mDbHelper.DecimalFractionConversion(qty, decnum);*/



       /* String grtrepackG = list.get(position).getREPACKFG_QTYMADE();
        if (grtrepackG!=null){
            grtrepackG_Qty = Double.parseDouble(list.get(position).getREPACKFG_QTYMADE());        //for null in the qty uncomend the code
        }
        else {
            grtrepackG_Qty=Double.valueOf(0);
        }*/

       // grtrepackG_Qty = Double.parseDouble(list.get(position).getREPACKFG_QTYMADE());
        if(list.get(position).getREPACKFG_QTYMADE() != null){
            grtrepackG_Qty = Double.parseDouble(list.get(position).getREPACKFG_QTYMADE());
        }else{
            grtrepackG_Qty = 0.0;
        }
        holder.tvQty.setText(String.valueOf(Math.round(grtrepackG_Qty)));
        holder.tvUom.setText(list.get(position).getREPACKFG_UMEASUR());
        holder.tvItem.setText(list.get(position).getREPACKFG_ITEM());
        holder.tvDesc.setText(list.get(position).getREPACKFG_DESCRIP());
        //holder.tvlOT.setText("99886574986");
        holder.tvlOT.setText(list.get(position).getREPACKFG_LOTNO().trim());

        return view;
    }
}
