package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.munit.m_unitapp.MODELS.CarWashDailySummary;
import com.munit.m_unitapp.MODELS.CarwashRec;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mulu Kadan 22/11/2019.
 */

public class AllCarwashSalesAdapter extends RecyclerView.Adapter<AllCarwashSalesAdapter.myViewHolder> {

    private LayoutInflater inflator;
    List<CarWashDailySummary> data = Collections.emptyList();
    Context mContext;
    ClickListener listener;
    String selectedUserName = "";

    public AllCarwashSalesAdapter(Context context, List<CarWashDailySummary> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }
    public AllCarwashSalesAdapter(Context context, List<CarWashDailySummary> data, String selectedUserName) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.selectedUserName = selectedUserName;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.layout_daily_car_wash_sale, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        CarWashDailySummary current = data.get(position);
        String date = current.getDate();

        holder.name.setText(date);

        holder.countTV.setText(current.getMotorbikes()+"|" + current.getCars()+"|"+current.getTrucks()+"|"+current.getOthers());

        holder.overalTotalTV.setText("" + current.getOverallTotal());
        holder.lbrTotalTv.setText("" + current.getLabourTotal());
        holder.expenseTV.setText("" + current.getExpense());
        holder.balTotalTV.setText("" + current.getBalTotal());
        holder.GroupcardView.setOnClickListener(v -> {
            if(listener!=null)
            listener.showCashBreakDown(current, true);
        });

//        if(selectedUserName.equalsIgnoreCase(current.getUserName())){
//            holder.bkLL.setBackgroundColor(Color.parseColor("#AD9EBC58"));
//            listener.showCashBreakDown(current, false);
//        }else {
//            holder.bkLL.setBackgroundColor(Color.parseColor("#ffffff"));
//        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView overalTotalTV;
        TextView lbrTotalTv;
        TextView expenseTV;
        TextView balTotalTV;
        TextView countTV;
        CardView GroupcardView;
        LinearLayout bkLL;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            overalTotalTV = itemView.findViewById(R.id.overalTotalTV);
            lbrTotalTv = itemView.findViewById(R.id.lbrTotalTv);
            expenseTV = itemView.findViewById(R.id.expenseTV);
            balTotalTV = itemView.findViewById(R.id.balTotalTV);
            countTV = itemView.findViewById(R.id.countTV);
            GroupcardView =  itemView.findViewById(R.id.GroupcardView);
            bkLL =  itemView.findViewById(R.id.bkLL);
        }


    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener{
        void showCashBreakDown(CarWashDailySummary summary, boolean refreshRV);
    }

    public void setSelectedUserName(String selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

}
