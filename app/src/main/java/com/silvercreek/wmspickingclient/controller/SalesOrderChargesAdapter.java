package com.silvercreek.wmspickingclient.controller;

import static com.silvercreek.wmspickingclient.util.Globals.chargelistHash;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.SalesOrderCategoryList;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.HashMap;
import java.util.List;

public class SalesOrderChargesAdapter extends ArrayAdapter<SalesOrderCategoryList> {

    private final List<SalesOrderCategoryList> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;
    private String SOFT_KEYBOARD;


    static class ViewHolder {
        protected TextView Charges;
        protected EditText Qty;

    }

    public SalesOrderChargesAdapter(Activity context, List<SalesOrderCategoryList> list,String SOFT_KEYBOARD) {
        super(context, R.layout.adapter_salesordercharges, list);
        this.context = context;
        this.list = list;
        this.SOFT_KEYBOARD = SOFT_KEYBOARD;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
        this.inflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = inflator.inflate(R.layout.adapter_salesordercharges, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#879DCE"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#879DCE"));
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.Charges = (TextView) view
                    .findViewById(R.id.tv_charges);
            viewHolder.Qty = (EditText) view.findViewById(R.id.edt_qty);

//            viewHolder.Qty.setShowSoftInputOnFocus(false);

            view.setTag(viewHolder);

        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#879DCE"));
            } else {
                view.setBackgroundColor(Color.parseColor("#879DCE"));
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.Charges.setText(String.valueOf(list.get(position).getCategory()));


        if (SOFT_KEYBOARD.equals("CHECKED")) {
            holder.Qty.setShowSoftInputOnFocus(false);
        } else {
            holder.Qty.setShowSoftInputOnFocus(true);
        }

       // holder.Qty.setEnabled(false);

        return view;
    }
}
