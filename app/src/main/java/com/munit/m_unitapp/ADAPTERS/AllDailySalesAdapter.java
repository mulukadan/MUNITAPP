package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    ClickListener listener;
    String selectedUserName = "";

    public AllDailySalesAdapter(Context context, List<DailySales> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }
    public AllDailySalesAdapter(Context context, List<DailySales> data, String selectedUserName) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.selectedUserName = selectedUserName;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.layout_dailysale, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        DailySales current = data.get(position);
        String username = "";

        holder.name.setText(current.getUserName());
        holder.csrv.setText("" + current.getComputer_service());
        holder.csls.setText("" + current.getComputer_sales());
        holder.mov.setText("" + current.getMovies());
        holder.gms.setText("" + current.getGames());
        holder.total.setText("" + current.getTotal());
        holder.GroupcardView.setOnClickListener(v -> {
            if(listener!=null)
            listener.showCashBreakDown(current, true);
        });

        if(selectedUserName.equalsIgnoreCase(current.getUserName())){
            holder.bkLL.setBackgroundColor(Color.parseColor("#AD9EBC58"));
            listener.showCashBreakDown(current, false);
        }else {
            holder.bkLL.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView csrv;
        TextView csls;
        TextView mov;
        TextView gms;
        TextView total;
        CardView GroupcardView;
        LinearLayout bkLL;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            csrv = itemView.findViewById(R.id.csrv);
            csls = itemView.findViewById(R.id.csls);
            mov = itemView.findViewById(R.id.mov);
            gms = itemView.findViewById(R.id.gms);
            total = itemView.findViewById(R.id.total);
            GroupcardView =  itemView.findViewById(R.id.GroupcardView);
            bkLL =  itemView.findViewById(R.id.bkLL);
        }


    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener{
        public void showCashBreakDown(DailySales dailySales, boolean refreshRV);
    }

    public void setSelectedUserName(String selectedUserName) {
        this.selectedUserName = selectedUserName;
    }
}
