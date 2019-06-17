package com.courierplus.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by iAbdullahi on 1/16/2018.
 */

public class dataListAdapter extends BaseAdapter {
        Context context;
        ArrayList<dataList> dList;
        private static LayoutInflater inflater = null;

        public dataListAdapter(Context context, ArrayList<dataList> dList) {
            this.context = context;
            this.dList = dList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return dList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null)
                convertView = inflater.inflate(R.layout.layou_grid_item, null);

            TextView awbnoTextView = (TextView) convertView.findViewById(R.id.gdAwbno);
            TextView originTextView = (TextView) convertView.findViewById(R.id.gdOrigin);
            TextView destinationTextView = (TextView) convertView.findViewById(R.id.gdDestination);
            TextView operationTextView = (TextView) convertView.findViewById(R.id.gdOperation);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.gdDate);

            dataList e = new dataList();
            e = dList.get(position);
            /*awbnoTextView.setText("Code: " + String.valueOf(e.getCode()));
            originTextView.setText("Name: " + e.getName());
            destinationTextView.setText("Email: " + e.getEmail());
            operationTextView.setText("Address: " + e.getAddress());
            dateTextView.setText("Address: " + e.getAddress());*/

            awbnoTextView.setText("AWBNO : " + e.getAwbno());
            originTextView.setText("ORIGIN : " + e.getOrigin());
            destinationTextView.setText("DESTINATION : " + e.getDestination());
            operationTextView.setText( Global.globalDataListOpertionCode + " : " + e.getOp());
            dateTextView.setText("DATE : " + e.getDate());
            return convertView;
        }

    }

