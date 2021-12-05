package com.munit.m_unitapp.UI.CYBER;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munit.m_unitapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DailySalesFragment extends Fragment {
private TextView Date;
private TextView computerService;
private TextView computerSales;
private TextView pictures;
private TextView video;
private TextView movies;
private TextView games;
private TextView Total;

    public DailySalesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_sales, container, false);
        Date = view.findViewById(R.id.Date);
        Date.setText(getArguments().getString("date"));

        computerService = view.findViewById(R.id.computerService);
        computerService.setText(getArguments().getString("csr"));

        computerSales = view.findViewById(R.id.computerSales);
        computerSales.setText(getArguments().getString("csl"));

        pictures = view.findViewById(R.id.pictures);
        pictures.setText(getArguments().getString("pics"));

        video = view.findViewById(R.id.video);
        video.setText(getArguments().getString("vid"));

        movies = view.findViewById(R.id.movies);
        movies.setText(getArguments().getString("mov"));

        games = view.findViewById(R.id.games);
        games.setText(getArguments().getString("game"));

        Total = view.findViewById(R.id.Total);
        Total.setText(getArguments().getString("total"));

        return view;
    }
}
