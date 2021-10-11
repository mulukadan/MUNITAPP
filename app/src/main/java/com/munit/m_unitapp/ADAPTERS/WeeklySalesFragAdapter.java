package com.munit.m_unitapp.ADAPTERS;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.gson.Gson;
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
//        position = position + 1;
        String date = "";
        for (DailySales dailySales : allUsersWeeklySales.get(position)) {
            sales.add(dailySales);
            if (date.equals("")) {
//                date = dailySales.getDate();
                date = dailySales.getDateWithDay();
            }
        }

//        for (List<DailySales> userSales : allUsersWeeklySales) {
//            if (userSales.size() > 0) {
//                DailySales dailySale = userSales.get(position - 1);
//                if (date.equals("")) {
//                    date = dailySale.getDate();
//                }
//                sales.add(dailySale);
//            }
//        }

        Gson gson = new Gson();
        String salesJson = gson.toJson(sales);

        bundle.putString("salesjson", salesJson);
        bundle.putString("date", date);
        salesFragment.setArguments(bundle);
        return salesFragment;
    }

    @Override
    public int getCount() {
        return 7;
    }
}
