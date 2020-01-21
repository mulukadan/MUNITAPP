package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mulu Kadan 22/11/2019.
 */

public class AllDailySalesAdapter extends RecyclerView.Adapter<AllDailySalesAdapter.myViewHolder> {

    private LayoutInflater inflator;
    List<DailySales> data = Collections.emptyList();
    Context mContext;

    public AllDailySalesAdapter(Context context, List<DailySales> data) {
        inflator= LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.layout_dailysale,parent,false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        DailySales current = data.get(position);

        holder.name.setText(current.getUser());
        holder.csrv.setText(""+current.getComputer_service());
        holder.csls.setText(""+current.getComputer_sales());
        holder.pic.setText(""+current.getPhotos());
        holder.vid.setText(""+current.getVideo());
        holder.mov.setText(""+current.getMovies());
        holder.gms.setText(""+current.getGames());
        holder.total.setText(""+current.getTotal());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        TextView csrv;
        TextView csls;
        TextView pic;
        TextView vid;
        TextView mov;
        TextView gms;
        TextView total;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            csrv = itemView.findViewById(R.id.csrv);
            csls = itemView.findViewById(R.id.csls);
            pic = itemView.findViewById(R.id.pic);
            vid = itemView.findViewById(R.id.vid);
            mov = itemView.findViewById(R.id.mov);
            gms = itemView.findViewById(R.id.gms);
            total = itemView.findViewById(R.id.total);

        }


    }
}
