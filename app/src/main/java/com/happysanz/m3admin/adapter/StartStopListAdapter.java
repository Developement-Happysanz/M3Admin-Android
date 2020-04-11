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
import com.happysanz.m3admin.bean.pia.AllProspects;
import com.happysanz.m3admin.bean.pia.StartStop;

import java.util.ArrayList;

public class StartStopListAdapter  extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<StartStop> upcomingHoliday;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public StartStopListAdapter(Context context, ArrayList<StartStop> upcomingHoliday) {
        this.context = context;
        this.upcomingHoliday = upcomingHoliday;
//        Collections.reverse(upcomingHoliday);
//        transformation = new RoundedTransformationBuilder()
//                .cornerRadiusDp(0)
//                .oval(false)
//                .build();
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
            return upcomingHoliday.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return upcomingHoliday.get(mValidSearchIndices.get(position));
        } else {
            return upcomingHoliday.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StartStopListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.all_prospects_list_item, parent, false);

            holder = new StartStopListAdapter.ViewHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.prospect_name);
            holder.txtStatus = (TextView) convertView.findViewById(R.id.prospect_status);
            convertView.setTag(holder);
        } else {
            holder = (StartStopListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        holder.txtName.setText(upcomingHoliday.get(position).gettracking_status());
        holder.txtStatus.setText(upcomingHoliday.get(position).getstart_stop_time());

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < upcomingHoliday.size(); i++) {
            String holidayTitle = upcomingHoliday.get(i).getId();
            if ((holidayTitle != null) && !(holidayTitle.isEmpty())) {
                if (holidayTitle.toLowerCase().contains(eventName.toLowerCase())) {
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
        public TextView txtName, txtStatus;
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