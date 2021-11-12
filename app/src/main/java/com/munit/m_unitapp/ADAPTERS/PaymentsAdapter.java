package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.munit.m_unitapp.MODELS.EmployeePayment;
import com.munit.m_unitapp.MODELS.PoolTableRecord;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

public class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<EmployeePayment> data = Collections.emptyList();
    private Context mContext;

//    FirebaseService db = new FirebaseService();

    public PaymentsAdapter(Context context, List<EmployeePayment> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        Collections.reverse(data);
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_employee_payments, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        EmployeePayment current = data.get(position);


        String payType = "Salary";
        if(current.getType().equals("A")){
            payType = "Advance";
        }
        holder.dateTV.setText(current.getDate() + "(" + payType + ")");
        holder.payAmtTV.setText("Ksh. " + currencyFormatter(current.getAmount()));
        holder.initialAdvTotalTv.setText("Ksh. " + currencyFormatter(current.getInitialAdvTotal()));
        holder.currentAdvTV.setText("Ksh. " + currencyFormatter(current.getCurrent()));
        holder.descTV.setText(current.getDescription());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV;
        TextView payAmtTV;
        TextView initialAdvTotalTv;
        TextView currentAdvTV;
        TextView descTV;

//        ImageButton viewBtn, addBtn;

        public myViewHolder(final View itemView) {
            super(itemView);
            dateTV = itemView.findViewById(R.id.dateTV);
            payAmtTV = itemView.findViewById(R.id.payAmtTV);
            initialAdvTotalTv = itemView.findViewById(R.id.initialAdvTotalTv);
            currentAdvTV = itemView.findViewById(R.id.currentAdvTV);
            descTV = itemView.findViewById(R.id.descTV);

//            addBtn.setOnClickListener((view) ->{
//                int pos = getAdapterPosition();
//                if(pos != RecyclerView.NO_POSITION) {
//                    PoolTableRecord room = data.get(pos);
//                    Gson gson = new Gson();
//                    String RoomJson = gson.toJson(room);
//
//                    Intent addI = new Intent(mContext, PoolRecordsAdapter.class);
//                    addI.putExtra("RoomJson", RoomJson);
//                    mContext.startActivity(addI);
//                }
//
//            });
        }
    }

    public String currencyFormatter( double amount){
        return String.format("%,.2f", amount);
    }

}