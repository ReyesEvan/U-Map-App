package com.example.charles.u_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class roomAdapter extends BaseAdapter {

    LayoutInflater layoutI;
    String[] classrooms;

    public roomAdapter(Context c, String[] r){
        classrooms = r;
        layoutI = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return classrooms.length;
    }

    @Override
    public Object getItem(int position) {
        return classrooms[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = layoutI.inflate(R.layout.room_nav, null);
        TextView roomTextView = (TextView) v.findViewById(R.id.roomTextView);

        String room = classrooms[position];

        roomTextView.setText(room);

        return v;
    }
}

