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

public class TakenMealPlanAdapter extends BaseAdapter {

    private List list;
    private Context context;

    // true if need populate list view with friendship requests
    // false if need populate list view with friends
    // in this case need to use: users.get(number).getParseObject("from").getString("name")
    private boolean isRequests;

    public TakenMealPlanAdapter(List listView, Context context){
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
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_taken_mealplan, parent, false);
        }

        TextView usernameTextView = (TextView) convertView.findViewById(R.id.takenMealPlanID);
        try {
            String mealPlanId = object.getParseObject("owner").fetchIfNeeded().getString("username");
            String mealPlanType = object.getString("type");

            usernameTextView.setText(mealPlanId +" | "+ mealPlanType.toUpperCase());
        }catch(ParseException e) {

        }
        return convertView;
    }


}
