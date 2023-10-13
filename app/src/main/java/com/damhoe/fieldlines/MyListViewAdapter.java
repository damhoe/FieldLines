package com.damhoe.fieldlines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fieldlines.R;

import java.util.ArrayList;
/**
 * Created by damian on 09.12.2017.
 */

public class MyListViewAdapter extends ArrayAdapter<IElement> {

    private final LayoutInflater inflater;

    private static class ViewHolder {
        private TextView nameView;
        private TextView valueView;
    }


    public MyListViewAdapter(Context context, ArrayList<IElement> elements) {
        super(context, -1, elements);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // ViewHolder pattern:
        // see http://www.vogella.com/tutorials/AndroidListView/article.html#adapterperformance_problems
        // https://developer.android.com/training/improving-layouts/smooth-scrolling.html
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_load_list_element, parent, false);
            holder = new ViewHolder();
            holder.nameView = (TextView) convertView.findViewById(R.id.list_element_load_textview);
            holder.valueView = (TextView) convertView.findViewById(R.id.listELementValue);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        IElement element = super.getItem(position);
        if (element != null) {
            holder.nameView.setText(element.getName());
            holder.valueView.setText(element.getValue());
        }

        return convertView;
    }
}
