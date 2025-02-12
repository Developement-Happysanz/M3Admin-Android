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
import com.happysanz.m3admin.bean.pia.ProspectDoc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProspectDocListAdapter  extends BaseAdapter {

    //    private final Transformation transformation;
    private Context context;
    private ArrayList<ProspectDoc> planData;
    private boolean mSearching = false;
    private boolean mAnimateSearch = false;
    private ArrayList<Integer> mValidSearchIndices = new ArrayList<Integer>();


    public ProspectDocListAdapter(Context context, ArrayList<ProspectDoc> planData) {
        this.context = context;
        this.planData = planData;
//        Collections.reverse(planData);
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
            return planData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (mSearching) {
            return planData.get(mValidSearchIndices.get(position));
        } else {
            return planData.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProspectDocListAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.prospect_doc_list_item, parent, false);

            holder = new ProspectDocListAdapter.ViewHolder();
            holder.txtPlanId = (TextView) convertView.findViewById(R.id.txt_download);
            holder.txtPlanTitle = (TextView) convertView.findViewById(R.id.doc_name);
//            holder.txtPlanDate = (TextView) convertView.findViewById(R.id.plan_date);
            convertView.setTag(holder);
        } else {
            holder = (ProspectDocListAdapter.ViewHolder) convertView.getTag();
        }

        if (mSearching) {
            position = mValidSearchIndices.get(position);
        } else {
            Log.d("Event List Adapter", "getview pos called" + position);
        }
//        holder.txtPlanId.setText(planData.get(position).getid());
        holder.txtPlanTitle.setText(planData.get(position).getdoc_name());
//        String start = planData.get(position).getDocMonthYear();
//        try {
//            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = formatter.parse(start);
//            SimpleDateFormat sent_date = new SimpleDateFormat("dd-MM-yyyy");
//            String sent_date_name = sent_date.format(date.getTime());
//            if (start != null) {
//                holder.txtPlanDate.setText(sent_date_name);
//            } else {
//                holder.txtPlanDate.setText("N/A");
//            }
//        } catch (final ParseException e) {
//            e.printStackTrace();
//        }

        return convertView;
    }

    public void startSearch(String eventName) {
        mSearching = true;
        mAnimateSearch = false;
        Log.d("EventListAdapter", "serach for event" + eventName);
        mValidSearchIndices.clear();
        for (int i = 0; i < planData.size(); i++) {
            String classStudent = planData.get(i).getid();
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
        public TextView txtPlanId, txtPlanTitle;
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