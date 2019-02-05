package com.happysanz.m3admin.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.happysanz.m3admin.R;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;

import org.json.JSONObject;

public class SelectedProspectsFragment extends Fragment implements AdapterView.OnItemClickListener, IServiceListener {
    private View rootView;

    public SelectedProspectsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_all_prospects, container, false);


        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


    }

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}