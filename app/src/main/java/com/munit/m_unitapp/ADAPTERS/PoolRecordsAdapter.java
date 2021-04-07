package com.munit.m_unitapp.ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.munit.m_unitapp.MODELS.PoolTableRecord;
import com.munit.m_unitapp.R;

import java.util.Collections;
import java.util.List;

public class PoolRecordsAdapter extends RecyclerView.Adapter<PoolRecordsAdapter.myViewHolder> {
    private LayoutInflater inflator;
    private List<PoolTableRecord> data = Collections.emptyList();
    private Context mContext;

//    FirebaseService db = new FirebaseService();

    public PoolRecordsAdapter(Context context, List<PoolTableRecord> data) {
        inflator = LayoutInflater.from(context);
        mContext = context;
        this.data = data;
        Collections.reverse(data);
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflator.inflate(R.layout.layout_record, parent, false);
        myViewHolder holder = new myViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        PoolTableRecord current = data.get(position);
        double AllTotal = current.getTotal();
        double biz1Total = current.getBiz1Total();
        if(current.getTableThreeTotal()>0){
            holder.biz2Layout.setVisibility(View.VISIBLE);
        }else {
            holder.biz2Layout.setVisibility(View.GONE);
        }

        holder.Date.setText(current.getDate());
        holder.Table1.setText("Ksh.  " + currencyFormatter(current.getTableOneTotal()));
        holder.Table2.setText("Ksh. " + currencyFormatter(current.getTableTwoTotal()));
        holder.Table3.setText("Ksh. " + currencyFormatter(current.getTableThreeTotal()));
        holder.Total.setText("Ksh. " + currencyFormatter(biz1Total));
        holder.GrantTotal.setText("Ksh. " + currencyFormatter(AllTotal));

        if (biz1Total > 9999) {
            holder.Date.setBackgroundColor(Color.parseColor("#5EBA7D"));
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView Date;
        TextView Table1;
        TextView Table2;
        TextView Table3;
        TextView Total;
        TextView GrantTotal;
        LinearLayout biz2Layout;

//        ImageButton viewBtn, addBtn;

        public myViewHolder(final View itemView) {
            super(itemView);
            Date = itemView.findViewById(R.id.date);
            Table1 = itemView.findViewById(R.id.Table1);
            Table2 = itemView.findViewById(R.id.Table2);
            Table3 = itemView.findViewById(R.id.Table3);
            Total = itemView.findViewById(R.id.Total);
            GrantTotal = itemView.findViewById(R.id.GrantTotal);
            biz2Layout = itemView.findViewById(R.id.biz2Layout);

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