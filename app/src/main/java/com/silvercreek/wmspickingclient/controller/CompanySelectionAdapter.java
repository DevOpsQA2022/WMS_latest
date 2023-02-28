package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.model.company;

import java.util.ArrayList;
import java.util.List;

public class CompanySelectionAdapter extends ArrayAdapter<company> implements Filterable {

    private final List<company> list;
    private final Activity context;

    private List<company> allModelItemsArray;
    private List<company> filteredModelItemsArray;
    private ModelFilter filter;
    private LayoutInflater inflator;

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ModelFilter();
        }
        return filter;
    }

    class ViewHolder {
        protected TextView number;
        protected TextView name;
    }

    public CompanySelectionAdapter(Activity context, List<company> list) {
        super(context, R.layout.company_list_columns, list);
        this.context = context;
        this.list = list;
        this.allModelItemsArray = new ArrayList<company>(list);
        this.filteredModelItemsArray = new ArrayList<company>(allModelItemsArray);
        inflator = context.getLayoutInflater();
        getFilter();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.company_list_columns, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.number = (TextView) view.findViewById(R.id.txt_Number);
            viewHolder.name = (TextView) view.findViewById(R.id.txt_Name);

            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.number.setText(list.get(position).getCompanyID());
        holder.name.setText(list.get(position).getCompanyName());
        return view;
    }

    private class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<company> filteredItems = new ArrayList<company>();

                for (int i = 0, l = allModelItemsArray.size(); i < l; i++) {
                    company comp = allModelItemsArray.get(i);
                    String strNum = comp.getCompanyID();
                    String strName = comp.getCompanyName();
                    if (strNum.toLowerCase().contains(constraint)|| strName.toLowerCase().contains(constraint))
                        filteredItems.add(comp);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = allModelItemsArray;
                    result.count = allModelItemsArray.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            filteredModelItemsArray = (ArrayList<company>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filteredModelItemsArray.size(); i < l; i++)
                add(filteredModelItemsArray.get(i));
            notifyDataSetInvalidated();
        }
    }
}
