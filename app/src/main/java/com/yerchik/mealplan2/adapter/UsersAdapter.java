package com.yerchik.mealplan2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yerchik.mealplan2.R;
import com.yerchik.mealplan2.model.User;

import java.util.ArrayList;
import java.util.List;


public class UsersAdapter extends BaseAdapter {
    private List list;
    private Context context;

    public UsersAdapter(List listView, Context context){
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
        ParseObject user = (ParseObject)getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_friend, parent, false);
        }
        TextView usernameTextView = (TextView) convertView.findViewById(R.id.usernameListItem);
        String name = user.getString("userName");
        //String surname = user.getString("surname");
        usernameTextView.setText(name);
        return convertView;
    }
}
