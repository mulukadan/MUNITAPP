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

import com.munit.m_unitapp.MODELS.CarwashExpenseModel;
import com.munit.m_unitapp.MODELS.DailySales;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Mulu Kadan 15/05/2022.
 */

public class CarwashExpenseAdapter extends RecyclerView.Adapter<CarwashExpenseAdapter.myViewHolder> {

    private LayoutInflater inflator;
    List<CarwashExpenseModel> data = Collections.emptyList();
    Context mContext;
    ClickListener listener;
    String selectedUserName = "";

    public CarwashExpenseAdapter(Context context, List<CarwashExpenseModel> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
    }
    public CarwashExpenseAdapter(Context context, List<CarwashExpenseModel> data, String selectedUserName) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        this.selectedUserName = selectedUserName;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflator.inflate(R.layout.layout_carwash_expense, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {
        CarwashExpenseModel current = data.get(position);
        String username = current.getName();
        if(username == null){
            username = "";
        }
        holder.name.setText(username);
        holder.countTV.setText(""+current.getCount());
        if(username.equalsIgnoreCase("Total") || current.getCount() == 1){
            holder.name.setText(username);
        }

        holder.total.setText("" + current.getTotal());
        holder.GroupcardView.setOnClickListener(v -> {
//            if(listener!=null)
//            listener.showCashBreakDown(current, true);
        });

//        if(selectedUserName.equalsIgnoreCase(current.getUserName())){
//            holder.bkLL.setBackgroundColor(Color.parseColor("#AD9EBC58"));
////            listener.showCashBreakDown(current, false);
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
        TextView countTV;
        TextView total;
        CardView GroupcardView;
        LinearLayout bkLL;

        public myViewHolder(final View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            countTV = itemView.findViewById(R.id.countTV);
            total = itemView.findViewById(R.id.total);
            GroupcardView =  itemView.findViewById(R.id.GroupcardView);
            bkLL =  itemView.findViewById(R.id.bkLL);
        }

    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    public interface ClickListener{
        void showCashBreakDown(DailySales dailySales, boolean refreshRV);
    }

    public void setSelectedUserName(String selectedUserName) {
        this.selectedUserName = selectedUserName;
    }

}
