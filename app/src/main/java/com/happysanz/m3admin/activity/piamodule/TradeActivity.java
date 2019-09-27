package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.TradeDataListAdapter;
import com.happysanz.m3admin.bean.pia.TradeData;
import com.happysanz.m3admin.bean.pia.TradeDataList;
import com.happysanz.m3admin.helper.AlertDialogHelper;
import com.happysanz.m3admin.helper.ProgressDialogHelper;
import com.happysanz.m3admin.interfaces.DialogClickListener;
import com.happysanz.m3admin.servicehelpers.ServiceHelper;
import com.happysanz.m3admin.serviceinterfaces.IServiceListener;
import com.happysanz.m3admin.utils.M3AdminConstants;
import com.happysanz.m3admin.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class TradeActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {


    private static final String TAG = "TradeFragment";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    TradeDataListAdapter tradeDataListAdapter;
    ArrayList<TradeData> tradeDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    ImageView addNewTrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.trade_list);
        loadMoreListView.setOnItemClickListener(this);
        tradeDataArrayList = new ArrayList<>();
        loadTrades();
        addNewTrade = findViewById(R.id.add_trade);
        addNewTrade.setOnClickListener(this);
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("1")) {
            addNewTrade.setVisibility(View.GONE);
        } else {
            addNewTrade.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == addNewTrade) {
            Intent intent = new Intent(getApplicationContext(), AddTradeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateSignInResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(M3AdminConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);

                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();

        if (validateSignInResponse(response)) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
//                    progressDialogHelper.hideProgressDialog();

                    Gson gson = new Gson();
                    TradeDataList tradeDataList = gson.fromJson(response.toString(), TradeDataList.class);
                    if (tradeDataList.getTradeData() != null && tradeDataList.getTradeData().size() > 0) {
                        totalCount = tradeDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(tradeDataList.getTradeData());
                    }
                }
            });
        }
    }


    @Override
    public void onError(final String error) {


    }

    protected void updateListAdapter(ArrayList<TradeData> tradeDataArrayList) {
        this.tradeDataArrayList.addAll(tradeDataArrayList);
        if (tradeDataListAdapter == null) {
            tradeDataListAdapter = new TradeDataListAdapter(this, this.tradeDataArrayList);
            loadMoreListView.setAdapter(tradeDataListAdapter);
        } else {
            tradeDataListAdapter.notifyDataSetChanged();
        }
    }

    private void loadTrades() {
        JSONObject jsonObject = new JSONObject();
        String id = "";
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("1")) {
            id = PreferenceStorage.getPIAProfileId(this);
        } else {
            id = PreferenceStorage.getUserId(this);
        }
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.TRADE_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        d(TAG, "onEvent list item click" + position);
        TradeData tradeData = null;
        if ((tradeDataListAdapter != null) && (tradeDataListAdapter.ismSearching())) {
            d(TAG, "while searching");
            int actualindex = tradeDataListAdapter.getActualEventPos(position);
            d(TAG, "actual index" + actualindex);
            tradeData = tradeDataArrayList.get(actualindex);
        } else {
            tradeData = tradeDataArrayList.get(position);
        }
//        Intent intent = new Intent(this, BatchDetailsActivity.class);
//        intent.putExtra("eventObj", tradeData);
//        startActivity(intent);
    }
    
}
