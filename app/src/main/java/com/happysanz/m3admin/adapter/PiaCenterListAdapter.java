package com.happysanz.m3admin.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.bean.pia.TradeData;

import java.util.ArrayList;

public class PiaCenterListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<Centers> centers;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();


    public PiaCenterListAdapter(Context context, ArrayList<Centers> centers) {
        this.context = context;
        this.centers = centers;
        mSearching = false;
    }

    @Override
    public int getCount() {
        if (mSearching) {
            if (!mAnimateSearch) {
                mAnimateSearch = true;
            }
            return mValidSearchIndices.size();

        } else {
            return centers.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return centers.get(mValidSearchIndices.get(position));
        } else {
            return centers.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PiaCenterListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.centers_list_item, parent, false);

            holder = new PiaCenterListAdapter.ViewHolder();
            holder.txtCenterName = (TextView) convertView.findViewById(R.id.center_name);
            holder.txtCenterLocation = (TextView) convertView.findViewById(R.id.center_location);
            convertView.setTag(holder);
        } else {
            holder = (PiaCenterListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }
        holder.txtCenterLocation.setText(centers.get(position).getcenter_address());
        holder.txtCenterName.setText(centers.get(position).getCenter_name());

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < centers.size(); i++) {
            String classStudent = centers.get(i).getCenter_name();
            if ((classStudent != null) && !(classStudent.isEmpty())) {
                if (classStudent.toLowerCase().contains(eventName.toLowerCase())) {
                    mValidSearchIndices.add(i);
                }
            }
        }
        Log.d("Event List Adapter", "notify" + mValidSearchIndices.size());
    }

    public void exitSearch() {
        mSearching = false;
        mValidSearchIndices.clear();
        mAnimateSearch = false;
    }

    public void clearSearchFlag() {
        mSearching = false;
    }

    public class ViewHolder {
        public TextView txtCenterLocation, txtCenterName;
    }

    public boolean ismSearching() {
        return mSearching;
    }

    public int getActualEventPos(int selectedSearchpos) {
        if (selectedSearchpos < mValidSearchIndices.size()) {
            return mValidSearchIndices.get(selectedSearchpos);
        } else {
            return 0;
        }
    }
}