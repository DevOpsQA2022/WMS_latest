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

public class RepackFInishedGoodsAdapter extends ArrayAdapter<RepackIngredients> {

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

    public RepackFInishedGoodsAdapter(Activity context, List<RepackIngredients> list) {
        super(context, R.layout.adapter_repack_finishedgoods, list);
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

        }

        ViewHolder holder = (ViewHolder) view.getTag();
        String cQty = list.get(position).getRIT_PALNO();
        getRit_qtyUsed = Double.parseDouble(list.get(position).getRIT_QTYUSED());

        holder.tvQty.setText(String.valueOf(Math.round(getRit_qtyUsed)));
        holder.tvUom.setText(list.get(position).getRIT_UMEASUR());
        holder.tvItem.setText(list.get(position).getRIT_ITEM());
        holder.tvDesc.setText(list.get(position).getRIT_DESCRIP());

        if(list.get(position).getRIT_LOTNO().equalsIgnoreCase("")){
            holder.tvLotrfId.setText(list.get(position).getRIT_LOTNO());
        }else {
            holder.tvLotrfId.setText(list.get(position).getRIT_LOTNO());
        }

        return view;
    }
}
