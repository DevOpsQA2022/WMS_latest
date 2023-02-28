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
import com.silvercreek.wmspickingclient.model.RepackIngredients;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class RepackIngredientsAdapter extends ArrayAdapter<RepackIngredients> {

    private final List<RepackIngredients> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<RepackFG> tpicktaskdetail=null;
    public Double getRit_qtyUsed =0.0;

    static class ViewHolder {

        protected TextView tvQty;
        protected TextView tvUom;
        protected TextView tvItem;
        protected TextView tvDesc;
        protected TextView tvLotrfId;
    }

    public RepackIngredientsAdapter(Activity context, List<RepackIngredients> list) {
        super(context, R.layout.adapter_repack_ingredients, list);
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
            view = inflator.inflate(R.layout.adapter_repack_ingredients, null);

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
            viewHolder.tvDesc = (TextView) view
                    .findViewById(R.id.tvDesc);
            viewHolder.tvLotrfId = (TextView) view
                    .findViewById(R.id.tvLotRfId);
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
        String cQty = list.get(position).getRIT_PALNO();
        //holder.tvQty.setText(String.format("%.2s", list.get(position).getRIT_QTYUSED()));


        /*  String getRit_QtyUsed = list.get(position).getRIT_QTYUSED();
        if (getRit_QtyUsed!=null){
            getRit_qtyUsed = Double.parseDouble(list.get(position).getRIT_QTYUSED());        //for null in the qty uncomend the code
        }
        else {
            getRit_qtyUsed=Double.valueOf(0);
        }*/

        getRit_qtyUsed = Double.parseDouble(list.get(position).getRIT_QTYUSED());
        holder.tvQty.setText(String.valueOf(Math.round(getRit_qtyUsed)));
        // holder.tvQty.setText(list.get(position).getRIT_QTYUSED());
        holder.tvUom.setText(list.get(position).getRIT_UMEASUR());
        holder.tvItem.setText(list.get(position).getRIT_ITEM());
        holder.tvDesc.setText(list.get(position).getRIT_DESCRIP());
        if(list.get(position).getRIT_LOTREFID().equalsIgnoreCase("")){
            holder.tvLotrfId.setText(list.get(position).getRIT_PALNO());
        }else {
            holder.tvLotrfId.setText(list.get(position).getRIT_LOTREFID());
        }

        return view;
    }
}
