package com.munit.m_unitapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.munit.m_unitapp.ADAPTERS.AllDailySalesAdapter;
import com.munit.m_unitapp.MODELS.DailySales;

import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WeeklySalesFragment extends Fragment {
private TextView Date;
private RecyclerView AllUsersRV;

    public WeeklySalesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_sales_all, container, false);
        Date = view.findViewById(R.id.Date);
        Date.setText(getArguments().getString("date"));

        AllUsersRV = view.findViewById(R.id.AllUsersRV);
        AllUsersRV.setLayoutManager(new LinearLayoutManager(this.getContext()));
        AllUsersRV.smoothScrollToPosition(0);
        String addDailySalesJson = getArguments().getString("salesjson");
        Gson gson = new Gson();

        DailySales[] userDataRecords = gson.fromJson(addDailySalesJson, DailySales[].class);
        List<DailySales> userDataRecords1 = Arrays.asList(userDataRecords);

        AllDailySalesAdapter allDailySalesAdapter = new AllDailySalesAdapter(this.getContext(), userDataRecords1);
        AllUsersRV.setAdapter(allDailySalesAdapter);

        return view;
    }
}
