package com.munit.m_unitapp.ADAPTERS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
import com.munit.m_unitapp.DailySalesFragment;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.WeeklySalesFragment;

import java.util.ArrayList;
import java.util.List;

public class WeeklySalesFragAdapter extends FragmentStatePagerAdapter {
    List<DailySales> sales = new ArrayList<>();

    List<List<DailySales>> allUsersWeeklySales = new ArrayList<>();

    public WeeklySalesFragAdapter(FragmentManager fm, List<List<DailySales>> sales) {
        super(fm);
        this.allUsersWeeklySales = sales;
    }

    @Override
    public Fragment getItem(int position) {
        sales = new ArrayList<>();
        WeeklySalesFragment salesFragment = new WeeklySalesFragment();
        Bundle bundle = new Bundle();
        position = position + 1;
        String date = "";

        for (List<DailySales> userSales : allUsersWeeklySales) {
            DailySales dailySale = userSales.get(position - 1);
            if (date.equals("")) {
                date = dailySale.getDate();
            }
            sales.add(dailySale);
        }

        Gson gson = new Gson();
        String salesJson = gson.toJson(sales);


        bundle.putString("salesjson", salesJson);
        bundle.putString("date",date);
        salesFragment.setArguments(bundle);
        return salesFragment;
    }

    @Override
    public int getCount() {
        return 7;
    }
}
