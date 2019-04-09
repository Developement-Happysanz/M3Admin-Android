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
import com.happysanz.m3admin.bean.pia.ProjectPeriod;
import com.happysanz.m3admin.bean.pia.TaskData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ProjectPeriodListAdapter extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<ProjectPeriod> taskData;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();


    public ProjectPeriodListAdapter(Context context, ArrayList<ProjectPeriod> taskData) {
        this.context = context;
        this.taskData = taskData;
        Collections.reverse(taskData);
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
            return taskData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return taskData.get(mValidSearchIndices.get(position));
        } else {
            return taskData.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProjectPeriodListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.project_period_list_item, parent, false);

            holder = new ProjectPeriodListAdapter.ViewHolder();
            holder.txtPeriodId = (TextView) convertView.findViewById(R.id.text_period_id);
            holder.txtPeriodStatus = (TextView) convertView.findViewById(R.id.text_period_status);
            holder.txtPeriodStart = (TextView) convertView.findViewById(R.id.period_date_start);
            holder.txtPeriodEnd = (TextView) convertView.findViewById(R.id.period_date_end);
            convertView.setTag(holder);
        } else {
            holder = (ProjectPeriodListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }
        holder.txtPeriodId.setText(taskData.get(position).getId());
        holder.txtPeriodStatus.setText(taskData.get(position).getStatus());
        String start = taskData.get(position).getPeriodFrom();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(start);
            SimpleDateFormat sent_date = new SimpleDateFormat("dd-MM-yyyy");
            String sent_date_name = sent_date.format(date.getTime());
            if (start != null) {
                holder.txtPeriodStart.setText("From : " + sent_date_name);
            } else {
                holder.txtPeriodStart.setText("N/A");
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        String end = taskData.get(position).getPeriodTo();
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = formatter.parse(end);
            SimpleDateFormat sent_date = new SimpleDateFormat("dd-MM-yyyy");
            String sent_date_name = sent_date.format(date.getTime());
            if (end != null) {
                holder.txtPeriodEnd.setText("To : " + sent_date_name);
            } else {
                holder.txtPeriodEnd.setText("N/A");
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < taskData.size(); i++) {
            String classStudent = taskData.get(i).getId();
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
        public TextView txtPeriodId, txtPeriodStatus, txtPeriodStart, txtPeriodEnd;
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