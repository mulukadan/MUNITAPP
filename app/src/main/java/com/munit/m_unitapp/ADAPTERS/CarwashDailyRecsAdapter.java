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

import com.munit.m_unitapp.MODELS.CarwashRec;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mulu Kadan 22/11/2019.
 */

public class CarwashDailyRecsAdapter extends RecyclerView.Adapter<CarwashDailyRecsAdapter.myViewHolder> {

    private LayoutInflater inflator;
    List<CarwashRec> data = Collections.emptyList();
    Context mContext;
    ClickListener listener;
    String selectedUserName = "";

    public CarwashDailyRecsAdapter(Context context, List<CarwashRec> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }

    public CarwashDailyRecsAdapter(Context context, List<CarwashRec> data, String selectedUserName) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.selectedUserName = selectedUserName;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.layout_carwash_rec, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        CarwashRec current = data.get(position);

        holder.regnoTV.setText(current.getRegNo());
        holder.byTv.setText("" + current.getAttendant());
        holder.amountTV.setText("Ksh. " + current.getAmount());
        holder.paidStatusTV.setText("" + current.isPaid());


        if (current.isPaid()) {
            holder.regnoTV.setBackgroundColor(Color.parseColor("#AD9EBC58"));

        } else {
            holder.regnoTV.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {

        TextView regnoTV;
        TextView byTv;
        TextView amountTV;
        TextView paidStatusTV;

        public myViewHolder(final View itemView) {
            super(itemView);
            regnoTV = itemView.findViewById(R.id.regnoTV);
            byTv = itemView.findViewById(R.id.byTv);
            amountTV = itemView.findViewById(R.id.amountTV);
            paidStatusTV = itemView.findViewById(R.id.paidStatusTV);
        }


    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener {
        public void showCashBreakDown(DailySales dailySales, boolean refreshRV);
    }

    public void setSelectedUserName(String selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

}
