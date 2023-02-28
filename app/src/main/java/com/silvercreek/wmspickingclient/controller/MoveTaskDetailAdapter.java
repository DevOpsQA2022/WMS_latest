package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.movetaskdetail;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class MoveTaskDetailAdapter extends ArrayAdapter<movetaskdetail> {

    private final List<movetaskdetail> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<movetaskdetail> tmovetaskdetails = null;
    private String strFlag = "Y";

    public MoveTaskDetailAdapter(Activity context, List<movetaskdetail> list) {
        super(context, R.layout.adapter_movetaskdetails, list);
        this.context = context;
        this.list = list;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String Toslot = "", isCompleted = "N", isPartial = "N";
        double tqtyrec = 0.0, trkqtyrec = 0.0, tqtyinc = 0.0;
        String flag = "N";
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_movetaskdetails, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
            //Completed TASK background color change code
            mDbHelper.openReadableDatabase();
            tmovetaskdetails = mDbHelper.getCompletedMTTrans(strFlag);
            mDbHelper.closeDatabase();

            if (tmovetaskdetails.size() != 0) {
                for (int i = 0; i < tmovetaskdetails.size(); i++) {
                    isCompleted = "N";
                    isPartial = "N";

                    int pos = Integer.valueOf(tmovetaskdetails.get(i).getTranlineno()) - 1;
                    Toslot = tmovetaskdetails.get(i).getToSlot();
                    flag = tmovetaskdetails.get(i).getFlag();

                    if (!Toslot.trim().equals("") && flag.equals("Y")) {
                        isCompleted = "Y";
                    }

                    if (isCompleted.equals("Y")) {
                        if (position == i) {
                            view.setBackgroundColor(Color.parseColor("#00FF00")); //Green color
                        }
                    } else if (isPartial.equals("Y")) {
                        if (position == i) {
                            view.setBackgroundColor(Color.parseColor("#ffff4b")); //Yellow color
                        }
                    }
                }
            }

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvQty = (TextView) view
                    .findViewById(R.id.tvQty);
            viewHolder.tvUom = (TextView) view
                    .findViewById(R.id.tvuom);
            viewHolder.tvItem = (TextView) view
                    .findViewById(R.id.item);
            viewHolder.tvPalno = (TextView) view.findViewById(R.id.tvPalno);
            viewHolder.tvFromSlot = (TextView) view.findViewById(R.id.tvFromSlot);
            viewHolder.tvToSlot = (TextView) view.findViewById(R.id.tvToSlot);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            mDbHelper.openReadableDatabase();
            tmovetaskdetails = mDbHelper.getCompletedMTTrans(strFlag);
            mDbHelper.closeDatabase();

            if (tmovetaskdetails.size() != 0) {
                for (int i = 0; i < tmovetaskdetails.size(); i++) {
                    isCompleted = "N";
                    isPartial = "N";

                    int pos = Integer.valueOf(tmovetaskdetails.get(i).getTranlineno()) - 1;
                    Toslot = tmovetaskdetails.get(i).getToSlot();
                    flag = tmovetaskdetails.get(i).getFlag();

                    if (!Toslot.equals("") && flag.equals("Y")) {
                        isCompleted = "Y";
                    }

                    if (isCompleted.equals("Y")) {
                        if (position == i) {
                            view.setBackgroundColor(Color.parseColor("#00FF00")); //Green color
                        }
                    } else if (isPartial.equals("Y")) {
                        if (position == i) {
                            view.setBackgroundColor(Color.parseColor("#ffff4b")); //Yellow color
                        }
                    }
                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvUom.setText(list.get(position).getUmeasur().trim());
        holder.tvItem.setText(list.get(position).getItem().trim());
        holder.tvPalno.setText(list.get(position).getPalno().trim());
        holder.tvFromSlot.setText(list.get(position).getFromSlot().trim());
        holder.tvToSlot.setText(list.get(position).getToSlot().trim());
        holder.tvQty.setText(String.valueOf(Math.round(Double.valueOf(list.get(position).getTqtyrq()))).trim());

        return view;
    }

    static class ViewHolder {
        protected TextView tvItem;
        protected TextView tvUom;
        protected TextView tvQty;
        protected TextView tvFromSlot;
        protected TextView tvToSlot;
        protected TextView tvPalno;

    }
}
