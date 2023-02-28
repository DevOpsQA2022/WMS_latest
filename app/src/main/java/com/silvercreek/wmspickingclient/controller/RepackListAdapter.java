package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.RepackList;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class RepackListAdapter extends ArrayAdapter<RepackList> {

    private final List<RepackList> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<RepackList> trepackList=null;
    public Double grtrepackG_Qty = 0.0;


    static class ViewHolder {

        protected TextView tvQty;
        protected TextView tvUom;
        protected TextView tvItem;
        protected TextView tvDesc;
    }

    public RepackListAdapter(Activity context, List<RepackList> list) {
        super(context, R.layout.adapter_repack_picklist, list);
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
            view = inflator.inflate(R.layout.adapter_repack_picklist, null);


            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvQty = (TextView) view
                    .findViewById(R.id.tvQty);
            viewHolder.tvUom  = (TextView) view
                    .findViewById(R.id.tvUom);
            viewHolder.tvItem = (TextView) view
                    .findViewById(R.id.tvItem);
            viewHolder.tvDesc = (TextView) view
                    .findViewById(R.id.tvDesc);
            view.setTag(viewHolder);
        } else {
            view = convertView;

        }

        ViewHolder holder = (ViewHolder) view.getTag();

        String aa =list.get(position).getPadate();
        String[] aaa = aa.split("T0");

        String aaaa = aaa[0];

        holder.tvItem.setText(list.get(position).getPano());
        holder.tvQty.setText(list.get(position).getLoctid());
      //  holder.tvUom.setText(list.get(position).getPadate());
        holder.tvUom.setText(aaaa);
        holder.tvDesc.setText(list.get(position).getAddtime());

        return view;
    }
}
