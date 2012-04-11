package com.urs.hotelsearch;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


// This is the custom Array Adapter, used for Custom Listview.
public class CustomListAdapter extends ArrayAdapter<CustomListView>{

    Context context; 
    int layoutResourceId;    
    CustomListView data[] = null;
    
    public CustomListAdapter(Context context, int layoutResourceId, CustomListView[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        WeatherHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new WeatherHolder();
            holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
            holder.txtTitle = (TextView)row.findViewById(R.id.txtTitle);
            
            row.setTag(holder);
        }
        else
        {
            holder = (WeatherHolder)row.getTag();
        }
        
        CustomListView customList = data[position];
        // Set text and image for the list.
        holder.txtTitle.setText(customList.title);
        holder.imgIcon.setImageBitmap(customList.bmp);
        
        return row;
    }
    
    static class WeatherHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
    }
}
