package com.courierplus.mobile;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import db.DataDB;

public class DataListActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<dataList> dList;
    dataListAdapter adapter;
    TextView txtListDetails;
    DataDB db;
    String op;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        listView = (ListView) findViewById(R.id.gdList);
        txtListDetails = (TextView) findViewById(R.id.txtListDetails);
        txtListDetails.setText(Global.globalDataListOpertionType + " DATA NOT UPLOADED.");

        db=new DataDB();
        dList = new ArrayList<dataList>();
        op =  this.getIntent().getExtras().getString("Operation");

        dList = db.getAllData(DataListActivity.this,op);
        adapter = new dataListAdapter(DataListActivity.this, dList);
        listView.setAdapter(adapter);
    }

}
