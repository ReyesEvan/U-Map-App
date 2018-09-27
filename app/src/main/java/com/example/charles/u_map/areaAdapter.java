package com.example.charles.u_map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class areaAdapter extends BaseAdapter {

    LayoutInflater layoutI;
    String[] areas;
    String[] areasDescription;

    public areaAdapter(Context c, String[] a, String[] d){
        areas = a;
        areasDescription = d;
        layoutI = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return areas.length;
    }

    @Override
    public Object getItem(int position) {
        return areas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = layoutI.inflate(R.layout.nav_symbols, null);
        TextView areaTextView = (TextView) v.findViewById(R.id.areaTextView);
        TextView areaDTextView = (TextView) v.findViewById(R.id.areaDTextView);
        ImageView areaImageView = (ImageView) v.findViewById(R.id.areaImageView);
        String area = areas[position];
        String description = areasDescription[position];

        areaTextView.setText(area);
        areaDTextView.setText(description);

        switch (position){
            case 0: areaImageView.setImageResource(R.drawable.ia);
                    break;
            case 1: areaImageView.setImageResource(R.drawable.cn);
                    break;
            case 2: areaImageView.setImageResource(R.drawable.ne);
                    break;
            case 3: areaImageView.setImageResource(R.drawable.cs);
                    break;
            case 4: areaImageView.setImageResource(R.drawable.sl);
                    break;
            case 5: areaImageView.setImageResource(R.drawable.hu);
                    break;
            case 6: areaImageView.setImageResource(R.drawable.ag);
                     break;
            case 7: areaImageView.setImageResource(R.drawable.au);
                    break;
            case 8: areaImageView.setImageResource(R.drawable.ce);
                    break;
            case 9: areaImageView.setImageResource(R.drawable.cal);
                    break;
            case 10: areaImageView.setImageResource(R.drawable.ha);
                    break;
            case 11: areaImageView.setImageResource(R.drawable.cc);
                break;
            case 12: areaImageView.setImageResource(R.drawable.cl);
                break;
            case 13: areaImageView.setImageResource(R.drawable.cb);
                break;
            case 14: areaImageView.setImageResource(R.drawable.cg);
                break;
            case 15: areaImageView.setImageResource(R.drawable.j1);
                break;
            case 16: areaImageView.setImageResource(R.drawable.j2);
                break;
            case 17: areaImageView.setImageResource(R.drawable.j3);
                break;
            case 18: areaImageView.setImageResource(R.drawable.j4);
                break;
            case 19: areaImageView.setImageResource(R.drawable.j5);
                break;
            case 20: areaImageView.setImageResource(R.drawable.j6);
                break;
            case 21: areaImageView.setImageResource(R.drawable.a);
                break;
            case 22: areaImageView.setImageResource(R.drawable.cfr);
                break;
            case 23: areaImageView.setImageResource(R.drawable.ct);
                break;
            case 24: areaImageView.setImageResource(R.drawable.ga);
                break;
            case 25: areaImageView.setImageResource(R.drawable.gb);
                break;
            case 26: areaImageView.setImageResource(R.drawable.gc);
                break;
            case 27: areaImageView.setImageResource(R.drawable.atl);
                break;
            default: areaImageView.setImageResource(R.drawable.cn);
                    break;
        }

        return v;
    }
}
