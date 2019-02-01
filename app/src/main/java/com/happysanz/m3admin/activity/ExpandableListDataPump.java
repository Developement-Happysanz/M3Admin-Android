package com.happysanz.m3admin.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListDataPump {
    public static HashMap getData() {
        HashMap expandableListDetail = new HashMap();

        List master = new ArrayList();
        master.add("Scheme");
        master.add("Center");
        master.add("Project");
        master.add("Trade");
        master.add("Batch");
        master.add("Trade and Batch");
        master.add("Time");

        List misc = new ArrayList();
        misc.add("Prospect");
        misc.add("User");
        misc.add("Add Plan");
        misc.add("Task");
        misc.add("Tracking");
        misc.add("Control Panel");


        expandableListDetail.put("Master", master);
//        expandableListDetail.put("Misc", misc);
        return expandableListDetail;
    }
}
