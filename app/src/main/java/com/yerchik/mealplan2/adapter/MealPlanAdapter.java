package com.yerchik.mealplan2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.yerchik.mealplan2.R;

import java.util.List;


public class MealPlanAdapter extends BaseAdapter {
    private List list;
    private Context context;

    // true if need populate list view with friendship requests
    // false if need populate list view with friends
    // in this case need to use: users.get(number).getParseObject("from").getString("name")
    private boolean isRequests;

    public MealPlanAdapter(List listView, Context context){
        this.list = listView;
        this.context = context;
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParseObject object = (ParseObject)getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_mealplan, parent, false);
        }
        TextView usernameTextView = (TextView) convertView.findViewById(R.id.mealPlanOwnerName);
        String fname,lname,name;

        try {
                fname = object.getParseObject("owner").fetchIfNeeded().getString("name").toLowerCase();
                lname = object.getParseObject("owner").fetchIfNeeded().getString("surname").toLowerCase();
                String mealPlanType = object.getString("type");
                name = fname.substring(0, 1).toUpperCase() + fname.substring(1) + " " + lname.substring(0, 1).toUpperCase() + lname.substring(1);
                usernameTextView.setText(name+" | "+mealPlanType.toUpperCase());
        }catch(ParseException e) {

        }
        return convertView;
    }

}
