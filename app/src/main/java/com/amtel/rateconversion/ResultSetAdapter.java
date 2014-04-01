package com.amtel.rateconversion;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amtel.rateconversion.values.ConversionValue;

/**
 * Created by pradeepbk on 3/31/14.
 */
public class ResultSetAdapter extends BaseAdapter {


    @Override
    public int getCount() {
        return ConversionValue.ITEMS.size();
    }

    @Override
    public ConversionValue.ConversionData getItem(int position) {
        return ConversionValue.ITEMS.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.conversion_history_row, parent, false);
        }

        final ConversionValue.ConversionData dataModel = ConversionValue.ITEMS.get(position);

        TextView textView = (TextView) convertView.findViewById(R.id.conversion_result_history);
        textView.setText(Html.fromHtml(dataModel.content));


        ImageView delete = (ImageView) convertView.findViewById(R.id.remove_item);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConversionValue.ITEMS.remove(position);
                notifyDataSetChanged();
            }
        });


        return convertView;
    }
}
