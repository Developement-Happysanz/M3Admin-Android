package com.happysanz.m3admin.activity.tnsrlmmodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.activity.piamodule.SchemeDetailActivity;
import com.happysanz.m3admin.adapter.PiaListAdapter;
import com.happysanz.m3admin.adapter.SchemeListAdapter;
import com.happysanz.m3admin.bean.pia.Mobilizer;
import com.happysanz.m3admin.bean.pia.MobilizerList;
import com.happysanz.m3admin.bean.tnsrlm.Scheme;
import com.happysanz.m3admin.bean.tnsrlm.SchemeList;
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

public class PiaSchemeActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = PiaActivity.class.getName();

    ImageView addNewPia;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    SchemeListAdapter tradeDataListAdapter;
    ArrayList<Scheme> tradeDataArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    private TextView tite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pia);
        addNewPia = (ImageView) findViewById(R.id.add_pia);

        addNewPia.setVisibility(View.GONE);
        TextView asdf = findViewById(R.id.skilName);
        asdf.setText("Scheme");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tite = findViewById(R.id.title);
        tite.setText("Skill Development Schemes");

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
                    SchemeList tradeDataList = gson.fromJson(response.toString(), SchemeList.class);
                    if (tradeDataList.getSchemeArrayList() != null && tradeDataList.getSchemeArrayList().size() > 0) {
                        totalCount = tradeDataList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(tradeDataList.getSchemeArrayList());
                    }
                }
            });
        }
    }


    @Override
    public void onError(final String error) {


    }

    protected void updateListAdapter(ArrayList<Scheme> tradeDataArrayList) {
        this.tradeDataArrayList.addAll(tradeDataArrayList);
        if (tradeDataListAdapter == null) {
            tradeDataListAdapter = new SchemeListAdapter(this, this.tradeDataArrayList);
            loadMoreListView.setAdapter(tradeDataListAdapter);
        } else {
            tradeDataListAdapter.notifyDataSetChanged();
        }
    }

    private void loadTrades() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(M3AdminConstants.SCHEME_ID, PreferenceStorage.getUserId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.SCHEME_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        d(TAG, "onEvent list item click" + position);
        Scheme piaData = null;
        if ((tradeDataListAdapter != null) && (tradeDataListAdapter.ismSearching())) {
            d(TAG, "while searching");
            int actualindex = tradeDataListAdapter.getActualEventPos(position);
            d(TAG, "actual index" + actualindex);
            piaData = tradeDataArrayList.get(actualindex);
        } else {
            piaData = tradeDataArrayList.get(position);
        }
        Intent intent = new Intent(this, SchemeDetailActivity.class);
        PreferenceStorage.saveSchemeId(this,piaData.getscheme_id());
        startActivity(intent);
    }

}