package com.example.charles.u_map;

import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    GridView myGridView;
    String[] areas;
    String[] description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources resources = getResources();
        myGridView = (GridView) findViewById(R.id.myGridView);
        areas = resources.getStringArray(R.array.areas);
        description = resources.getStringArray(R.array.areasdescription);

        areaAdapter areaA = new areaAdapter(this, areas, description);
        myGridView.setAdapter(areaA);

        myGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToRoom = new Intent(getApplicationContext(), RoomSelector.class);
                goToRoom.putExtra("com.example.charles.u_map.AREA",areas[position]);
                startActivity(goToRoom);
            }
        });
    }
}
