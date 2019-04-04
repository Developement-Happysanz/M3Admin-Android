package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.piamodule.CenterActivity;
import com.happysanz.m3admin.activity.piamodule.PiaDashboard;
import com.happysanz.m3admin.adapter.MobilizerListAdapter;
import com.happysanz.m3admin.adapter.PiaListAdapter;
import com.happysanz.m3admin.adapter.TradeDataListAdapter;
import com.happysanz.m3admin.bean.pia.Mobilizer;
import com.happysanz.m3admin.bean.pia.MobilizerList;
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

public class PiaActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = PiaActivity.class.getName();

    ImageView addNewPia;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    PiaListAdapter tradeDataListAdapter;
    ArrayList<Mobilizer> tradeDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pia);
        addNewPia = (ImageView) findViewById(R.id.add_pia);
        addNewPia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddNewPiaActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.pia_list);
        loadMoreListView.setOnItemClickListener(this);
        tradeDataArrayList = new ArrayList<>();
        loadTrades();
    }

    @Override
    public void onClick(View v) {

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
                    MobilizerList tradeDataList = gson.fromJson(response.toString(), MobilizerList.class);
                    if (tradeDataList.getMobilizerArrayList() != null && tradeDataList.getMobilizerArrayList().size() > 0) {
                        totalCount = tradeDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(tradeDataList.getMobilizerArrayList());
                    }
                }
            });
        }
    }


    @Override
    public void onError(final String error) {


    }

    protected void updateListAdapter(ArrayList<Mobilizer> tradeDataArrayList) {
        this.tradeDataArrayList.addAll(tradeDataArrayList);
        if (tradeDataListAdapter == null) {
            tradeDataListAdapter = new PiaListAdapter(this, this.tradeDataArrayList);
            loadMoreListView.setAdapter(tradeDataListAdapter);
        } else {
            tradeDataListAdapter.notifyDataSetChanged();
        }
    }

    private void loadTrades() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.PIA_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        d(TAG, "onEvent list item click" + position);
        Mobilizer piaData = null;
        if ((tradeDataListAdapter != null) && (tradeDataListAdapter.ismSearching())) {
            d(TAG, "while searching");
            int actualindex = tradeDataListAdapter.getActualEventPos(position);
            d(TAG, "actual index" + actualindex);
            piaData = tradeDataArrayList.get(actualindex);
        } else {
            piaData = tradeDataArrayList.get(position);
        }
        Intent intent = new Intent(this, PiaDetailsActivity.class);
        PreferenceStorage.savePIAProfileId(this,piaData.getUser_id());
//        intent.putExtra("eventObj", piaData);
        startActivity(intent);
    }

}
