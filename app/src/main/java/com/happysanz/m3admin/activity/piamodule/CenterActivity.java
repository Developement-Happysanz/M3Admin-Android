package com.happysanz.m3admin.activity.piamodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.PiaCenterListAdapter;
import com.happysanz.m3admin.adapter.TradeDataListAdapter;
import com.happysanz.m3admin.bean.pia.Centers;
import com.happysanz.m3admin.bean.pia.CentersList;
import com.happysanz.m3admin.bean.pia.Mobilizer;
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

public class CenterActivity extends AppCompatActivity implements  IServiceListener, DialogClickListener, AdapterView.OnItemClickListener, View.OnClickListener {


    private static final String TAG = "TradeFragment";
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    ListView loadMoreListView;
    Mobilizer pia;
    PiaCenterListAdapter piaCenterListAdapter;
    ArrayList<Centers> centersArrayList;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    int pageNumber = 0, totalCount = 0;
    ImageView add;
    Boolean hasExtra = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        add = findViewById(R.id.add_center_detail);
        add.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        loadMoreListView = findViewById(R.id.center_list);
        loadMoreListView.setOnItemClickListener(this);
        centersArrayList = new ArrayList<>();

        Intent intent = getIntent();
        if (intent.hasExtra("eventObj")) {
            pia = (Mobilizer) intent.getSerializableExtra("eventObj");
            PreferenceStorage.savePIAProfileId(this, pia.getUser_id());
            add.setVisibility(View.GONE);
            loadCentersForPia();
        } else {
            loadCenters();
        }

        if (PreferenceStorage.getTnsrlmCheck(this)){
            add.setVisibility(View.GONE);
        }
    }

    private void loadCentersForPia() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(M3AdminConstants.KEY_USER_ID, pia.getUser_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_CENTER_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void loadCenters() {
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
        String url = M3AdminConstants.BUILD_URL + M3AdminConstants.GET_CENTER_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                    CentersList centersList = gson.fromJson(response.toString(), CentersList.class);
                    if (centersList.getCenters() != null && centersList.getCenters().size() > 0) {
                        totalCount = centersList.getCount();
                        isLoadingForFirstTime = false;
                        updateListAdapter(centersList.getCenters());
                    }
                }
            });
        }
    }


    @Override
    public void onError(final String error) {


    }

    protected void updateListAdapter(ArrayList<Centers> centersArrayList) {
        this.centersArrayList.addAll(centersArrayList);
        if (piaCenterListAdapter == null) {
            piaCenterListAdapter = new PiaCenterListAdapter(this, this.centersArrayList);
            loadMoreListView.setAdapter(piaCenterListAdapter);
        } else {
            piaCenterListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        d(TAG, "onEvent list item click" + position);
        Centers centers = null;
        if ((piaCenterListAdapter != null) && (piaCenterListAdapter.ismSearching())) {
            d(TAG, "while searching");
            int actualindex = piaCenterListAdapter.getActualEventPos(position);
            d(TAG, "actual index" + actualindex);
            centers = centersArrayList.get(actualindex);
        } else {
            centers = centersArrayList.get(position);
        }
        Intent intent = new Intent(this, CenterDetailActivity.class);
        intent.putExtra("cent", centers);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        if(view == add){
            Intent intent = new Intent(this, AddCenterDetailActivity.class);
            intent.putExtra("page", "add");
            startActivity(intent);
//            finish();
        }
    }
}
