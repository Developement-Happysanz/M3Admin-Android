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
import com.happysanz.m3admin.bean.pia.Mobilizer;
import com.happysanz.m3admin.bean.pia.MobilizerRecord;

import java.util.ArrayList;

public class RecordListAdapter  extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<MobilizerRecord> mobilizers;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();

    public RecordListAdapter(Context context, ArrayList<MobilizerRecord> mobilizers) {
        this.context = context;
        this.mobilizers = mobilizers;
//        Collections.reverse(mobilizers);
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
            return mobilizers.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return mobilizers.get(mValidSearchIndices.get(position));
        } else {
            return mobilizers.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecordListAdapter.ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.record_list_item, parent, false);

            holder = new RecordListAdapter.ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_date);
            holder.txtDistance = (TextView) convertView.findViewById(R.id.txt_distance);
//            holder.txtSNo = (TextView) convertView.findViewById(R.id.txt_distance);

            convertView.setTag(holder);
        } else {
            holder = (RecordListAdapter.ViewHolder) convertView.getTag();

        }


        if (mSearching) {
            position = mValidSearchIndices.get(position);

        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }

        holder.txtDate.setText(mobilizers.get(position).getcreated_at());
        holder.txtDistance.setText(mobilizers.get(position).getkm());
//        holder.txtSNo.setText("" + (position + 1));
        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < mobilizers.size(); i++) {
            String homeWorkTitle = mobilizers.get(i).getId();
            if ((homeWorkTitle != null) && !(homeWorkTitle.isEmpty())) {
                if (homeWorkTitle.toLowerCase().contains(eventName.toLowerCase())) {
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
        public TextView txtDate, txtSNo, txtDistance;
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