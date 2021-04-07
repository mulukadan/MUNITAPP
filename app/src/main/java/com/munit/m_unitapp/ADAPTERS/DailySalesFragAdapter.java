package com.munit.m_unitapp.ADAPTERS;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.munit.m_unitapp.DailySalesFragment;
import com.munit.m_unitapp.MODELS.DailySales;

import java.util.ArrayList;
import java.util.List;

public class DailySalesFragAdapter extends FragmentStatePagerAdapter {
    List<DailySales> sales = new ArrayList<>();

    public DailySalesFragAdapter(FragmentManager fm, List<DailySales> sales) {
        super(fm);
        this.sales = sales;
    }

    @Override
    public Fragment getItem(int position) {
        DailySalesFragment salesFragment = new DailySalesFragment();
        Bundle bundle = new Bundle();
        position = position + 1;

        DailySales dailySale = sales.get(position -1);

        bundle.putString("csl", String.valueOf(dailySale.getComputer_sales()));
        bundle.putString("csr", String.valueOf(dailySale.getComputer_service()));
        bundle.putString("mov", String.valueOf(dailySale.getMovies()));
        bundle.putString("game", String.valueOf(dailySale.getGames()));
        bundle.putString("total", String.valueOf(dailySale.getTotal()));
        bundle.putString("date", String.valueOf(dailySale.getDate()));
        salesFragment.setArguments(bundle);
        return salesFragment;
    }

    @Override
    public int getCount() {
        return 7;
    }
}
