package com.happysanz.m3admin.activity.piamodule;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.happysanz.m3admin.R;
import com.happysanz.m3admin.adapter.StartStopListAdapter;
import com.happysanz.m3admin.bean.pia.StartStop;
import com.happysanz.m3admin.bean.pia.StartStopList;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TrackingDistanceActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = TrackingDistanceActivity.class.getName();

    private MapView mapView;
    private GoogleMap mMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    List<LatLng> list = new ArrayList<>();
    String d;
    TextView dis;
    private ListView startStopList;
    StartStopList tradeDataList;
    ArrayList<StartStop> taskDataArrayList = new ArrayList<>();
    ArrayList<StartStop> taskDataArrayList1 = new ArrayList<>();
    StartStopListAdapter startStopListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        dis = findViewById(R.id.distance);
        dis.setVisibility(View.VISIBLE);
        startStopList = findViewById(R.id.start_stop_list);
        list =  getIntent().getParcelableArrayListExtra("latlng");
        dis.setText("Distance Travelled : " + getIntent().getSerializableExtra("dist").toString() + " Km");

        String scamDatas = getIntent().getStringExtra("start");
//        String scamSubCategoryText  = getIntent().getStringExtra("scamSubCategoryText");
        try {
            JsonParser parser = new JsonParser();
            JsonObject scamDataJsonObject = parser.parse(scamDatas).getAsJsonObject();
            Gson gson = new Gson();
            StartStopList tradeDataList = gson.fromJson(scamDataJsonObject.toString(), StartStopList.class);
            if (tradeDataList.getStartStop() != null && tradeDataList.getStartStop().size() > 0) {
//            totalCount = tradeDataList.getCount();
//            isLoadingForFirstTime = false;
                this.taskDataArrayList.addAll(tradeDataList.getStartStop());
                if (startStopListAdapter == null) {
                    startStopListAdapter = new StartStopListAdapter(this, this.taskDataArrayList);
                    startStopList.setAdapter(startStopListAdapter);
                } else {
                    startStopListAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//
//                        new LatLng(-35.016, 143.321),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309)));

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(list.get(0), 12));
        PolylineOptions options =  new PolylineOptions().addAll(list).width(5).color(Color.BLUE).geodesic(true);
        Polyline polyline = googleMap.addPolyline(options);
        googleMap.addMarker(new MarkerOptions()
                .position(list.get(0))
                .title("Start Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        googleMap.addMarker(new MarkerOptions()
                .position(list.get(list.size() - 1))
                .title("End Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

}
